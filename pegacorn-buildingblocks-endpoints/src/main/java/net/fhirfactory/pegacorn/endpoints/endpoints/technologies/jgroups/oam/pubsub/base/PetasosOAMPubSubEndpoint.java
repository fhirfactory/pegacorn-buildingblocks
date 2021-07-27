package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.oam.pubsub.base;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.deployment.topology.model.common.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.endpoints.endpoints.roles.common.MultiPublisherResponseSet;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddressTypeEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DistributedPubSubSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.*;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionRequest;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionResponse;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionStatus;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import javax.inject.Inject;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherStatusEnum.*;

public abstract class PetasosOAMPubSubEndpoint extends JGroupsPetasosEndpointBase {

    @Inject
    DistributedPubSubSubscriptionMapIM distributedPubSubSubscriptionMapIM;

    //
    // Constructor
    //

    public PetasosOAMPubSubEndpoint(){
        super();
    }

    //
    // Abstract Methods
    //

    abstract protected EdgeForwarderService specifyEdgeForwarderService();

    //
    // Getters (and Setters)
    //

    protected EdgeForwarderService getEdgeForwarderService(){
        return(specifyEdgeForwarderService());
    }

    protected DistributedPubSubSubscriptionMapIM getDistributedPubSubSubscriptionMapIM(){
        return(distributedPubSubSubscriptionMapIM);
    }

    //
    // Remote Called Procedures for Subscribing
    //

    /**
     *
     * @param subscriptionRequest
     * @return
     */
    public RemoteSubscriptionResponse rpcRequestSubscriptionHandler(RemoteSubscriptionRequest subscriptionRequest){
        getLogger().info(".rpcRequestSubscriptionHandler(): Entry, subscriptionRequest.getSubscriber()->{}", subscriptionRequest.getSubscriber());

        PubSubParticipant subscriber = subscriptionRequest.getSubscriber();
        List<DataParcelManifest> subscriptionList = subscriptionRequest.getSubscriptionList();

        NetworkSecurityZoneEnum subscriberSecurityZone = subscriber.getInterSubsystemParticipant().getEndpointID().getEndpointZone();
        String subscriberSite = subscriber.getInterSubsystemParticipant().getEndpointID().getEndpointSite();

        boolean sameZone = subscriberSecurityZone.equals(getPetasosEndpoint().getEndpointID().getEndpointZone());
        boolean sameSite = subscriberSite.contentEquals(getPetasosEndpoint().getEndpointID().getEndpointSite());
        boolean doSubscription = false;
        if(sameSite && sameZone){
            if(supportsIntraZoneCommunication()){
                doSubscription = true;
            }
        }
        if(sameSite && !sameZone){
            if(supportsInterZoneCommunication()){
                doSubscription = true;
            }
        }
        if(!sameSite){
            if(supportsInterSiteCommunication()){
                doSubscription = true;
            }
        }
        getLogger().trace(".rpcRequestSubscriptionHandler(): doSubscription->{}", true);
        RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
        if (doSubscription) {
            RemoteSubscriptionStatus subscriptionStatus = getEdgeForwarderService().subscribeOnBehalfOfRemoteSubscriber(subscriptionList, subscriber);
            response.setPublisher(getParticipant());
            response.setSubscriptionRegistrationDate(Date.from(Instant.now()));
            response.setSubscriptionSuccessful(subscriptionStatus.isSubscriptionSuccessful());
            response.setSubscriptionCommentary(subscriptionStatus.getSubscriptionCommentary());
            response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_ACTIVE);
        } else {
            response.setPublisher(getParticipant());
            response.setSubscriptionRegistrationDate(Date.from(Instant.now()));
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("This IPC Endpoint does not support PubSub from Provided Node");
            response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_FAILED);
        }

        getLogger().info(".rpcRequestSubscriptionHandler(): Exit, response->{}", response);
        return(response);
    }

    //
    // Calls for Subscribing
    //

    /**
     *
     * @param subscriptionList
     * @param publisher
     * @return
     */
    public InterSubsystemPubSubPublisherSubscriptionRegistration subscribeToRemotePublishers(List<DataParcelManifest> subscriptionList, String publisherServiceName) {
        getLogger().info(".subscribeToRemotePublishers(): Entry, publisher->{}", publisherServiceName);
        if (StringUtils.isEmpty(publisherServiceName)) {
            getLogger().debug(".subscribeToRemotePublishers(): Cannot resolve service name, exiting");
            return (null);
        }
        getLogger().trace(".subscribeToRemotePublishers(): DistributedPublisher ServiceName exists, now looking for a publisher to match");
        List<String> publisherServicePubSubCandidateSet = getPublisherServicePubSubCandidateSet(publisherServiceName);
        if (publisherServicePubSubCandidateSet.isEmpty()) {
            getLogger().trace(".subscribeToRemotePublishers(): There are no potential publishers at the moment, so register request");
            InterSubsystemPubSubPublisherSubscriptionRegistration newPubSubRegistration = getDistributedPubSubSubscriptionMapIM().addSubscriptionToPublisher(subscriptionList, publisherServiceName);
            ;
            newPubSubRegistration.setPublisherServiceRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_PENDING_NO_PROVIDERS);
            newPubSubRegistration.setRegistrationCommentary("Cannot locate suitable publisher");
            return (newPubSubRegistration);
        }
        getLogger().trace(".subscribeToRemotePublishers(): A publisher is available");
        // There exists a publisher capable of servicing our needs, so we are going to register that
        // publisher (or set of publishers) and push our subscription requirements to it (them).
        getLogger().trace(".subscribeToRemotePublishers(): Registering each of the potential publishers");
        for (String currentPublisherCandidate : publisherServicePubSubCandidateSet) {
            PetasosEndpoint publisherPetasosEndpoint = getEndpointMap().getEndpoint(currentPublisherCandidate);
            if (publisherPetasosEndpoint != null) {
                InterSubsystemPubSubParticipant newPublisher = new InterSubsystemPubSubParticipant(publisherPetasosEndpoint);
                InterSubsystemPubSubPublisherRegistration publisherRegistration = getDistributedPubSubSubscriptionMapIM().registerPublisherInstance(newPublisher);
            }
        }
        // Now we are going to Subscribe to each Publisher, and we will let the Petasos framework handle
        // the fact we don't want duplicate messages
        getLogger().trace(".subscribeToRemotePublishers(): Now subscribing to each of the potential publishers");
        List<MultiPublisherResponseSet> responseSetList = new ArrayList<>();
        boolean aSubscriptionWasSuccessful = false;
        RemoteSubscriptionResponse aSuccessfulResponse = null;
        for (InterSubsystemPubSubPublisherRegistration currentPublisherRegistration : getDistributedPubSubSubscriptionMapIM().getPublisherServiceProviderInstanceRegistrations(publisherServiceName)) {
            InterSubsystemPubSubParticipant currentPublisher = currentPublisherRegistration.getPublisher();
            if (currentPublisher.getEndpointStatus().equals(PUBLISHER_UTILISED)) {
                RemoteSubscriptionResponse remoteSubscriptionResponse = requestSubscriptionToPublisherInstance(subscriptionList, currentPublisher.getEndpointID().getEndpointName());
                updatePublisherRegistration(remoteSubscriptionResponse);
                if (remoteSubscriptionResponse.isSubscriptionSuccessful()) {
                    aSubscriptionWasSuccessful = aSubscriptionWasSuccessful || true;
                    aSuccessfulResponse = remoteSubscriptionResponse;
                }
            }
        }
        if(aSubscriptionWasSuccessful){
            getLogger().info(".subscribeToRemotePublishers(): aSubscriptionWasSuccessful->{}", aSubscriptionWasSuccessful);
            InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = updatePublisherSubscriptionRegistration(subscriptionList, aSuccessfulResponse.getPublisher(), true);
            getLogger().info(".subscribeToRemotePublishers(): Exit, subscriptionRegistration->{} ", subscriptionRegistration);
            return(subscriptionRegistration);
        } else {
            InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = updatePublisherSubscriptionRegistration(subscriptionList, null, false);
            getLogger().info(".subscribeToRemotePublishers(): Exit, subscriptionRegistration->{} ", subscriptionRegistration);
            return(subscriptionRegistration);
        }
    }

    /**
     * This function takes the response to a SubscriptionRequest and updates the associated publishers details within
     * the RegistrationMap and the EndpointMap.
     *
     * It effectively over-writes content within the EndpointMap (given that the response has come back from the
     * actual subsystem to which the EndpointMap entry pertains, we can consider it a source of truth).
     * @param subscriptionResponse
     */
    protected void updatePublisherRegistration(RemoteSubscriptionResponse subscriptionResponse) {
        boolean aSubscriptionWasSuccessful = subscriptionResponse.isSubscriptionSuccessful();
        PubSubParticipant publisher = subscriptionResponse.getPublisher();
        if (publisher == null) {
            return;
        }
        InterSubsystemPubSubParticipant interSubsystemParticipant = publisher.getInterSubsystemParticipant();
        if (interSubsystemParticipant == null) {
            return;
        }
        PetasosEndpointIdentifier endpointID = interSubsystemParticipant.getEndpointID();
        if (endpointID == null) {
            return;
        }
        String publisherEndpointName = endpointID.getEndpointName();
        if (StringUtils.isEmpty(publisherEndpointName)) {
            return;
        }
        InterSubsystemPubSubPublisherRegistration cachedPublisherRegistration = getDistributedPubSubSubscriptionMapIM().getPublisherInstanceRegistration(publisherEndpointName);
        if (cachedPublisherRegistration == null) {
            getEndpointMap().addEndpoint(publisher.getInterSubsystemParticipant());
            cachedPublisherRegistration = getDistributedPubSubSubscriptionMapIM().registerPublisherInstance(publisher.getInterSubsystemParticipant());
        } else {
            synchronized (getEndpointMap().getEndpointLock(publisherEndpointName)) {
                cachedPublisherRegistration.getPublisher().encrichPetasosEndpoint(subscriptionResponse.getPublisher().getInterSubsystemParticipant());
            }
        }
        if (aSubscriptionWasSuccessful) {
            cachedPublisherRegistration.setPublisherStatus(PUBLISHER_UTILISED);
        } else {
            cachedPublisherRegistration.setPublisherStatus(PUBLISHER_NOT_UTILISED);
        }
    }

    protected InterSubsystemPubSubPublisherSubscriptionRegistration updatePublisherSubscriptionRegistration(List<DataParcelManifest> subscriptionList, PubSubParticipant publisher, boolean subscriptionSuccessful){
        getLogger().info(".updatePublisherSubscriptionRegistration(): Entry, success->{}, publisher->{}", subscriptionSuccessful, publisher);
        InterSubsystemPubSubPublisherSubscriptionRegistration publisherServiceSubscription = getPublisherMapIM().getPublisherServiceSubscription(publisher.getInterSubsystemParticipant());
        if(publisherServiceSubscription == null) {
            publisherServiceSubscription = getPublisherMapIM().addSubscriptionToPublisher(subscriptionList, publisher.getInterSubsystemParticipant());
        }
        if(subscriptionSuccessful) {
            getLogger().info(".updatePublisherSubscriptionRegistration(): updating status");
            publisherServiceSubscription.setPublisherServiceRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_ACTIVE);
        }
        getLogger().info(".updatePublisherSubscriptionRegistration(): publisherServiceSubscription->{}", publisherServiceSubscription);
        return(publisherServiceSubscription);
    }

    /**
     *
     * @param subscriptionList
     * @param publisher
     * @return
     */
    private RemoteSubscriptionResponse requestSubscriptionToPublisherInstance(List<DataParcelManifest> subscriptionList, String petasosEndpointName ) {
        getLogger().info(".requestSubscription(): Entry, subscriptionList->{}, publisher->{} ", subscriptionList, publisher);
        boolean nothingToSubscribeTo = false;
        if(subscriptionList == null){
            nothingToSubscribeTo = true;
        }
        if(!nothingToSubscribeTo){
            if(subscriptionList.isEmpty()){
                nothingToSubscribeTo = true;
            }
        }
        if(nothingToSubscribeTo){
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Nothing to subscribe");
            return(response);
        }
        RemoteSubscriptionResponse response = null;
        getLogger().info(".requestSubscription(): Build subscriptionRequest (RemoteSubscriptionRequest)");
        RemoteSubscriptionRequest subscriptionRequest = new RemoteSubscriptionRequest();
        subscriptionRequest.setSubscriber(getMe());
        subscriptionRequest.setSubscriptionList(subscriptionList);
        getLogger().info(".requestSubscription(): subscriptionRequest built, value->{}", subscriptionRequest);
        getLogger().info(".requestSubscription(): Now ascertain if the publisher is actually available");
        String publisherInstanceName = publisher.getInterSubsystemParticipant().getIdentifier().getPetasosEndpointName();
        if (isParticipantInstanceAvailable(publisherInstanceName)) {
            getLogger().info(".requestSubscription(): Publisher is available, so register");
            PetasosAdapterAddress publisherAddress = getAddressForParticipantInstance(publisherInstanceName);
            if (publisherAddress != null) {
                getLogger().info(".requestSubscription(): Subscribing to PublisherInstance->{}", publisherInstanceName);
                response = rpcRequestSubscription(publisherAddress, subscriptionRequest);
                this.subscriptionCheckSchedule.scheduleEndpointCheck(publisherAddress, false, true);
            }
        }
        if(response == null){
            getLogger().error(".requestSubscription(): Publisher ({}) is not available!!!", publisher);
            response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setNetworkConnectionStatus(PubSubParticipantUtilisationStatusEnum.PUB_SUB_NETWORK_CONNECTION_FAILED);
            response.setSubscriptionCommentary("Publisher ("+publisher+") is not available!");
        }
        getLogger().info(".requestSubscription(): Exit, response->{}", response);
        return (response);
    }


    /**
     *
     * @param publisherAddress
     * @param subscriptionRequest
     * @return
     */

    public RemoteSubscriptionResponse rpcRequestSubscription(PetasosAdapterAddress publisherAddress, RemoteSubscriptionRequest subscriptionRequest){
        getLogger().info(".rpcRequestSubscription(): Entry, publisher->{}, subscriptionRequest.getSubscriber{}", publisherAddress, subscriptionRequest.getSubscriber());
        if(publisherAddress == null || subscriptionRequest == null){
            getLogger().error(".rpcRequestSubscription: publisherAddress or subscriptionRequest are null");
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (publisherAddress or subscriptionRequest are null)");
            return(response);
        }
        if(!publisherAddress.getAddressType().equals(PetasosAdapterAddressTypeEnum.ADDRESS_TYPE_JGROUPS)){
            getLogger().error(".rpcRequestSubscription: publisherAddress or subscriptionRequest are null");
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (Wrong endpoint technology (should be JGroups))");
            return(response);
        }
        Address jgroupsAddress = publisherAddress.getJGroupsAddress();
        getLogger().trace(".rpcRequestSubscription(): Extract JGroups Address->{}", jgroupsAddress);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = subscriptionRequest;
            classSet[0] = RemoteSubscriptionRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            RemoteSubscriptionResponse response = getRPCDispatcher().callRemoteMethod(jgroupsAddress, "rpcRequestSubscriptionHandler", objectSet, classSet, requestOptions);
            getLogger().info(".rpcRequestSubscription(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".rpcRequestSubscription(): Error (NoSuchMethodException)->", e);
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (NoSuchMethodException)" + e.getMessage());
            return(response);
        } catch (Exception e) {
            getLogger().error(".rpcRequestSubscription: Error (GeneralException) ->",e);
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (GeneralException)" + e.getMessage());
            return(response);
        }
    }

    /**
     *
     */
    protected void performSubscriptionCheck(){
        getLogger().debug(".performSubscriptionCheck(): Entry");
        List<InterSubsystemPubSubPublisherSubscriptionRegistration> subscriptionRegistrationList = getPublisherMapIM().getAllPublisherServiceSubscriptions();
        getLogger().trace(".performSubscriptionCheck(): Iterate through Subscription Registrations");
        for(InterSubsystemPubSubPublisherSubscriptionRegistration currentServiceRegistration: subscriptionRegistrationList){
            getLogger().trace(".performSubscriptionCheck(): Looking for publisher->{}", currentServiceRegistration.getPublisherServiceName());
            List<InterSubsystemPubSubPublisherRegistration> instanceRegistrations = getPublisherMapIM().getPublisherServiceProviderInstanceRegistrations(currentServiceRegistration.getPublisherServiceName());
            getLogger().trace(".performSubscriptionCheck(): Iterate through Publisher Registrations");
            for(InterSubsystemPubSubPublisherRegistration currentInstanceRegistration: instanceRegistrations){
                getLogger().trace(".performSubscriptionCheck(): Iterating, looking at publisher->{}", currentInstanceRegistration.getPublisher().getIdentifier().getPetasosEndpointName());
                if(currentInstanceRegistration.getPublisherStatus().equals(PUBLISHER_REGISTERED) || currentInstanceRegistration.getPublisherStatus().equals(PUBLISHER_NOT_UTILISED)){
                    getLogger().trace(".performSubscriptionCheck(): Checking....");
                    PubSubParticipant newPublisher = new PubSubParticipant();
                    newPublisher.setInterSubsystemParticipant(currentInstanceRegistration.getPublisher());
                    InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = subscribeToRemotePublishers(currentServiceRegistration.getSubscriptionList(), newPublisher);
                }
            }
            getLogger().trace(".performSubscriptionCheck(): Looking now into JGroups itself for Publisher Service->{}", currentServiceRegistration.getPublisherServiceName());
            List<PetasosAdapterAddress> serviceInstanceSet = getParticipantServiceInstanceSet(currentServiceRegistration.getPublisherServiceName());
            if(serviceInstanceSet != null){
                getLogger().trace(".performSubscriptionCheck(): there are publishers!, number->{}", serviceInstanceSet.size());
                for(PetasosAdapterAddress serviceProvider: serviceInstanceSet){
                    getLogger().trace(".performSubscriptionCheck(): checking participant->{}", serviceProvider);
                    String publisherInstanceName = serviceProvider.getAddressName();
                    String publisherServiceName = getServiceNameFromParticipantInstanceName(publisherInstanceName);
                    getLogger().trace(".performSubscriptionCheck(): Checking Participant->(participantServiceName->{}, participantInstanceName->{} ", publisherServiceName, publisherInstanceName);
                    InterSubsystemPubSubPublisherRegistration publisherRegistration = getPublisherMapIM().getPublisherInstanceRegistration(publisherInstanceName);
                    getLogger().trace(".performSubscriptionCheck(): Existing Publisher Registration->{}", publisherRegistration);
                    if(publisherRegistration == null) {
                        getLogger().trace(".performSubscriptionCheck(): checking participant->{}", serviceProvider);
                        String myName = getMe().getInterSubsystemParticipant().getIdentifier().getPetasosEndpointName();
                        InterSubsystemPubSubParticipant newParticipant = requestParticipantDetail(serviceProvider.getJGroupsAddress(), myName);
                        publisherRegistration = getPublisherMapIM().registerPublisherInstance(newParticipant);
                        if (publisherRegistration != null) {
                            PubSubParticipant newPublisher = new PubSubParticipant();
                            newPublisher.setInterSubsystemParticipant(newParticipant);
                            InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = subscribeToRemotePublishers(currentServiceRegistration.getSubscriptionList(), newPublisher);
                        }
                    } else{
                        // Do nothing
                    }
                }
            }
        }
    }

    /**
     *
     */
    public void scheduleASubscriptionCheck() {
        getLogger().info(".scheduleASubscriptionCheck(): Entry (subscriptionCheckScheduled->{}", isSubscriptionCheckScheduled());
        synchronized (getSubscriptionCheckScheduledLock()) {
            if (isSubscriptionCheckScheduled()) {
                // do nothing, it is already scheduled
            } else {
                TimerTask subscriptionCheckTask = new TimerTask() {
                    public void run() {
                        getLogger().info(".subscriptionCheckTask(): Entry");
                        boolean doAgain = doSubscriptionCheck();
                        getLogger().info(".subscriptionCheckTask(): doAgain ->{}", doAgain);
                        if (!doAgain) {
                            cancel();
                            setSubscriptionCheckScheduled(false);
                        }
                        getLogger().info(".subscriptionCheckTask(): Exit");
                    }
                };
                Timer timer = new Timer("SubscriptionScheduleTimer");
                timer.schedule(subscriptionCheckTask, getParticipantMembershipCheckDelay(), getParticipantMembershipCheckPeriod());
                setSubscriptionCheckScheduled(true);
            }
        }
        getLogger().info(".scheduleASubscriptionCheck(): Exit");
    }

    protected boolean doSubscriptionCheck(){
        getLogger().info(".doSubscriptionCheck(): Entry");
        List<IPCEndpointCheckScheduleElement> endpointsToCheck = subscriptionCheckSchedule.getEndpointsToCheck();
        for(IPCEndpointCheckScheduleElement currentScheduleElement: endpointsToCheck) {
            PetasosAdapterAddress participantAddress = currentScheduleElement.getEndpoint();
            getLogger().info(".doSubscriptionCheck(): Entry, participantAddress->{}", participantAddress);
            String participantInstanceName = participantAddress.getAddressName();
            String participantServiceName = extractPublisherServiceName(participantInstanceName);
            if (StringUtils.isEmpty(participantInstanceName) || StringUtils.isEmpty(participantServiceName)) {
                getLogger().info(".doSubscriptionCheck(): Exit, publisher name is bad...");
            } else {
                InterSubsystemPubSubPublisherSubscriptionRegistration serviceSubscription = null;
                InterSubsystemPubSubPublisherRegistration instanceRegistration = getPublisherMapIM().getPublisherInstanceRegistration(participantInstanceName);
                if (instanceRegistration == null) {
                    getLogger().info(".doSubscriptionCheck(): There is no existing PublisherRegistration for this PubSubParticipant, creating");
                    serviceSubscription = getPublisherMapIM().getPublisherServiceSubscription(participantServiceName);
                    if (serviceSubscription != null) {
                        List<DataParcelManifest> subscriptionList = serviceSubscription.getSubscriptionList();
                        if (subscriptionList != null) {
                            getLogger().info(".doSubscriptionCheck(): Exit, We have no topics to register with this publisher");
                            getLogger().trace(".doSubscriptionCheck(): Create new PubSubPublisher");
                            String myName = getMe().getInterSubsystemParticipant().getIdentifier().getPetasosEndpointName();
                            InterSubsystemPubSubParticipant newParticipant = requestParticipantDetail(participantAddress.getJGroupsAddress(), myName);
                            instanceRegistration = getPublisherMapIM().registerPublisherInstance(newParticipant);
                        }
                    }
                }
                if(instanceRegistration != null) {
                    if (instanceRegistration.getPublisherStatus().equals(PUBLISHER_REGISTERED) || instanceRegistration.getPublisherStatus().equals(PUBLISHER_NOT_UTILISED)) {
                        PubSubParticipant newParticipant = new PubSubParticipant();
                        newParticipant.setInterSubsystemParticipant(instanceRegistration.getPublisher());
                        RemoteSubscriptionResponse subscriptionResponse = requestSubscriptionToPublisherInstance(serviceSubscription.getSubscriptionList(), newParticipant);
                        if (subscriptionResponse.isSubscriptionSuccessful()) {
                            instanceRegistration.setPublisherStatus(PUBLISHER_UTILISED);
                            instanceRegistration.setLastActivityDate(Date.from(Instant.now()));
                            serviceSubscription.setPublisherServiceRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_ACTIVE);
                        }
                    }
                }
            }
        }
        if(subscriptionCheckSchedule.scheduleIsEmpty()){
            return(false);
        } else {
            return(true);
        }
    }

    //
    // Endpoint/Participant tests
    //

    protected boolean isParticipantServiceAvailable(PubSubParticipant participant){
        getLogger().debug(".isParticipantAvailable(): Entry, participant->{}", participant);
        boolean participantIsAvailable = false;
        if(hasParticipantServiceName(participant)) {
            String serviceName = participant.getInterSubsystemParticipant().getIdentifier().getServiceName();
            participantIsAvailable = getIPCEndpoint().getPubSubParticipantServiceCandidateAddress(serviceName) != null;
        } else {
            participantIsAvailable = false;
        }
        getLogger().debug(".isParticipantAvailable(): Exit, returning->{}", participantIsAvailable);
        return(participantIsAvailable);
    }

    protected boolean hasParticipantServiceName(PubSubParticipant participant){
        if(participant != null){
            if(participant.getInterSubsystemParticipant() != null){
                if(participant.getInterSubsystemParticipant().getIdentifier() != null){
                    if(participant.getInterSubsystemParticipant().getIdentifier().getServiceName() != null){
                        return(true);
                    }
                }
            }
        }
        return(false);
    }

    protected boolean isParticipantServiceAvailable(String participantServiceName){
        getLogger().debug(".isParticipantAvailable(): Entry, publisherServiceName->{}", participantServiceName);
        boolean participantIsAvailable = getIPCEndpoint().getPubSubParticipantServiceCandidateAddress(participantServiceName) != null;
        getLogger().debug(".isParticipantAvailable(): Exit, returning->{}", participantIsAvailable);
        return(participantIsAvailable);
    }

    protected String getAvailableParticipantInstanceName(PubSubParticipant participant){
        getLogger().debug(".getAvailableParticipantInstanceName(): Entry, participant->{}", participant);
        String serviceInstanceName = null;
        if(hasParticipantServiceName(participant)) {
            String publisherServiceName = participant.getInterSubsystemParticipant().getIdentifier().getServiceName();
            serviceInstanceName = getAvailableParticipantInstanceName(publisherServiceName);
        }
        getLogger().debug(".getAvailableParticipantInstanceName(): Exit, serviceInstanceName->{}", serviceInstanceName);
        return(serviceInstanceName);
    }

    public String getAvailableParticipantInstanceName(String participantServiceName){
        getLogger().debug(".getAvailableParticipantInstanceName(): Entry, participantServiceName->{}", participantServiceName);
        PetasosAdapterAddress targetAddress = getIPCEndpoint().getPubSubParticipantServiceCandidateAddress(participantServiceName);
        String participantInstanceName = targetAddress.toString();
        getLogger().debug(".getAvailableParticipantInstanceName(): Exit, participantInstanceName->{}", participantInstanceName);
        return(participantInstanceName);
    }

    public boolean isParticipantInstanceAvailable(String participantInstanceName){
        getLogger().debug(".isParticipantInstanceAvailable(): Entry, participantInstanceName->{}", participantInstanceName);
        boolean participantInstanceNameStillActive = getIPCEndpoint().isPubSubParticipantInstanceActive(participantInstanceName);
        getLogger().debug(".isParticipantInstanceAvailable(): Exit, participantInstanceNameStillActive->{}", participantInstanceNameStillActive);
        return(participantInstanceNameStillActive);
    }

    public boolean isParticipantInstanceAvailable(PetasosAdapterAddress participantAddress){
        getLogger().debug(".isParticipantInstanceAvailable(): Entry, participantAddress->{}", participantAddress);
        boolean participantInstanceNameStillActive = getIPCEndpoint().isPubSubParticipantInstanceActive(participantAddress);
        getLogger().debug(".isParticipantInstanceAvailable(): Exit, participantInstanceNameStillActive->{}", participantInstanceNameStillActive);
        return(participantInstanceNameStillActive);
    }

    public PetasosAdapterAddress getAddressForParticipantInstance(PubSubParticipant publisher){
        if(publisher == null){
            return(null);
        }
        PetasosAdapterAddress instanceAddress = getAddressForParticipantInstance(publisher.getInterSubsystemParticipant());
        return(instanceAddress);
    }

    public PetasosAdapterAddress getAddressForParticipantInstance(InterSubsystemPubSubParticipant publisherInterParticipant){
        if(publisherInterParticipant == null){
            return(null);
        }
        PetasosAdapterAddress instanceAddress = getAddressForParticipantInstance(publisherInterParticipant.getIdentifier());
        return(instanceAddress);
    }

    public PetasosAdapterAddress getAddressForParticipantInstance(InterSubsystemPubSubParticipantIdentifier publisherInterID){
        if(publisherInterID == null){
            return(null);
        }
        PetasosAdapterAddress instanceAddress = getAddressForParticipantInstance(publisherInterID.getPetasosEndpointName());
        return(instanceAddress);
    }

    public PetasosAdapterAddress getAddressForParticipantInstance(String publisherInstanceName){
        if(StringUtils.isEmpty(publisherInstanceName)){
            return(null);
        }
        PetasosAdapterAddress instanceAddress = getIPCEndpoint().getPubSubParticipantInstanceAddress(publisherInstanceName);
        return(instanceAddress);
    }

    public PetasosAdapterAddress getAddressForParticipantService(PubSubParticipant publisher){
        if(publisher == null){
            return(null);
        }
        PetasosAdapterAddress address = getAddressForParticipantService(publisher.getInterSubsystemParticipant());
        return(address);
    }

    public PetasosAdapterAddress getAddressForParticipantService(InterSubsystemPubSubParticipant publisher){
        if(publisher == null){
            return(null);
        }
        PetasosAdapterAddress address = getAddressForParticipantService(publisher.getIdentifier());
        return(address);
    }

    public PetasosAdapterAddress getAddressForParticipantService(InterSubsystemPubSubParticipantIdentifier identifier){
        if(getEndpointProxy() == null){
            return(null);
        }
        PetasosAdapterAddress address = getIPCEndpoint().getPubSubParticipantServiceCandidateAddress(identifier.getServiceName());
        return(address);
    }

    public List<PetasosAdapterAddress> getParticipantServiceInstanceSet(String participantServiceName){
        List<PetasosAdapterAddress> addressSet = new ArrayList<>();
        if(StringUtils.isEmpty(participantServiceName)){
            return(addressSet);
        }
        List<PetasosAdapterAddress> targetServiceInstanceAddresses = getIPCEndpoint().getTargetServiceInstanceAddresses(participantServiceName);
        addressSet.addAll(targetServiceInstanceAddresses);
        return(addressSet);
    }

    public String getServiceNameFromParticipantInstanceName(String participantInstanceName){
        if(StringUtils.isEmpty(participantInstanceName)){
            return(null);
        }
        String[] nameParts = StringUtils.split(participantInstanceName, "(");
        return(nameParts[0]);
    }

    protected String extractPublisherServiceName(String participantInstanceName){
        return(getServiceNameFromParticipantInstanceName(participantInstanceName));
    }

    //
    // Helpers
    //

    /**
     * This method returns a set of possible endpoints supporting the PUBSUB function for the given publisherServiceName.
     *
     * It first pulls ALL the petasosEndpointNames that are part of the generic publisherServiceName list (i.e. OAM.PubSub,
     * OAM.Discovery & IPC based endpoints) and then filters them down to only include the OAM.PubSub entries.
     *
     * @param publisherServiceName The "Publisher Service Name" to which candidate endpoints are to be found
     * @return The list of .OAM.PubSub endpoints supporting that service.
     */
    List<String> getPublisherServicePubSubCandidateSet(String publisherServiceName){
        List<String> candidateSet = new ArrayList<>();
        if(StringUtils.isEmpty(publisherServiceName)){
            return(candidateSet);
        }
        List<String> serviceNameMembership = getEndpointMap().getServiceNameMembership(publisherServiceName);
        if(serviceNameMembership.isEmpty()){
            return(candidateSet);
        }
        for(String currentMember: serviceNameMembership){
            if(currentMember.contains(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT.getFunctionSuffix())){
                candidateSet.add(currentMember);
            }
        }
        return(candidateSet);
    }
}

