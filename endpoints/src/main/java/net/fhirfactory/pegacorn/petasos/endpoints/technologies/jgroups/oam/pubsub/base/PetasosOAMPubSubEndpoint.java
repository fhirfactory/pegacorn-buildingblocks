package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.pubsub.base;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.*;
import net.fhirfactory.pegacorn.petasos.endpoints.base.PetasosPubSubEndpointChangeInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.roles.common.MultiPublisherResponseSet;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddressTypeEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
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

public abstract class PetasosOAMPubSubEndpoint extends JGroupsPetasosEndpointBase implements PetasosAdapterDeltasInterface, PetasosPubSubEndpointChangeInterface {

    private boolean subscriptionCheckScheduled;
    private Object subscriptionCheckLock;

    private static Long SUBSCRIPTION_CHECK_INITIAL_DELAY=5000L;
    private static Long SUBSCRIPTION_CHECK_PERIOD = 5000L;

    private int subscriptionCheckCount;
    private static int CHANGE_DETECTION_SUBSCRIPTION_CHECK_COUNT = 10;

    @Inject
    DistributedPubSubSubscriptionMapIM distributedPubSubSubscriptionMapIM;

    //
    // Constructor
    //

    public PetasosOAMPubSubEndpoint(){
        super();
        subscriptionCheckScheduled = false;
        subscriptionCheckLock = new Object();
        subscriptionCheckCount = 0;
    }

    @Override
    protected void executePostConstructActivities(){
        getCoreSubsystemPetasosEndpointsWatchdog().registerPubSubCallbackChange(this);
        scheduleASubscriptionCheck();
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
    // Endpoint Discovery
    //

    @Override
    public void registerInterfaceEventCallbacks(PetasosAdapterDeltasInterface interfaceEventCallbacks) {
        this.getMembershipEventListeners().add(interfaceEventCallbacks);
    }

    @Override
    public void interfaceAdded(PetasosAdapterAddress addedInterface){
        scheduleASubscriptionCheck();
    }

    @Override
    public void interfaceRemoved(PetasosAdapterAddress removedInterface){
        scheduleASubscriptionCheck();
    }

    @Override
    public void interfaceSuspect(PetasosAdapterAddress suspectInterface){

    }

    //
    // Callback Procedures for Subscribing
    //

    /**
     *
     * @param subscriptionRequest
     * @return
     */
    public RemoteSubscriptionResponse rpcRequestSubscriptionHandler(RemoteSubscriptionRequest subscriptionRequest){
        getLogger().debug(".rpcRequestSubscriptionHandler(): Entry, subscriptionRequest.getSubscriber()->{}", subscriptionRequest);

        PubSubParticipant subscriber = subscriptionRequest.getSubscriber();
        List<DataParcelManifest> subscriptionList = subscriptionRequest.getSubscriptionList();

        boolean withinScope = isWithinScopeOfEndpoint(subscriber.getInterSubsystemParticipant());
        getLogger().trace(".rpcRequestSubscriptionHandler(): withinScope->{}", withinScope);
        boolean doSubscription;
        PetasosEndpointStatusEnum aggregatePetasosEndpointStatus = getCoreSubsystemPetasosEndpointsWatchdog().getAggregatePetasosEndpointStatus();
        getLogger().trace(".rpcRequestSubscriptionHandler(): aggregateOperationalEndpointStatus->{}", aggregatePetasosEndpointStatus);
        boolean operationalStatusIsGood = aggregatePetasosEndpointStatus.equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL);
        if(operationalStatusIsGood){
            doSubscription = withinScope;
        } else {
            doSubscription = false;
        }

        getLogger().trace(".rpcRequestSubscriptionHandler(): doSubscription->{}", doSubscription);
        RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
        if (doSubscription) {
            getLogger().trace(".rpcRequestSubscriptionHandler(): Attempting subscription");
            RemoteSubscriptionStatus subscriptionStatus = getEdgeForwarderService().subscribeOnBehalfOfRemoteSubscriber(subscriptionList, subscriber);
            response.setPublisher(getParticipant());
            response.setSubscriptionRegistrationDate(Date.from(Instant.now()));
            response.setSubscriptionSuccessful(subscriptionStatus.isSubscriptionSuccessful());
            response.setSubscriptionCommentary(subscriptionStatus.getSubscriptionCommentary());
            response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_ACTIVE);
        } else {
            getLogger().trace(".rpcRequestSubscriptionHandler(): Not Attempting subscription");
            response.setPublisher(getParticipant());
            response.setSubscriptionRegistrationDate(Date.from(Instant.now()));
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("This IPC Endpoint can not presently support subscription");
            response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_FAILED);
        }
        getLogger().debug(".rpcRequestSubscriptionHandler(): Exit, response->{}", response);
        return(response);
    }

    //
    // Calls for Subscribing
    //

    /**
     *
     * @param subscriptionList
     * @param publisherServiceName
     * @return
     */
    @Deprecated
    public InterSubsystemPubSubPublisherSubscriptionRegistration subscribeToRemotePublishers(List<DataParcelManifest> subscriptionList, String publisherServiceName) {
        getLogger().debug(".subscribeToRemotePublishers(): Entry, publisher->{}", publisherServiceName);
        if (StringUtils.isEmpty(publisherServiceName)) {
            getLogger().debug(".subscribeToRemotePublishers(): Cannot resolve service name, exiting");
            return (null);
        }
        getLogger().trace(".subscribeToRemotePublishers(): DistributedPublisher ServiceName exists, now looking for a publisher to match");
        List<InterSubsystemPubSubPublisherRegistration> publisherRegistrations = getDistributedPubSubSubscriptionMapIM().getPublisherServiceProviderInstanceRegistrations(publisherServiceName);
        List<InterSubsystemPubSubPublisherRegistration> scopedPublisherRegistrations = new ArrayList<>();
        for(InterSubsystemPubSubPublisherRegistration currentRegistration: publisherRegistrations){
            if(currentRegistration.getPublisher().getEndpointScope().equals(getPetasosEndpoint().getEndpointScope())){
                scopedPublisherRegistrations.add(currentRegistration);
            }
        }
        if (scopedPublisherRegistrations.isEmpty()) {
            getLogger().debug(".subscribeToRemotePublishers(): There are no potential publishers at the moment, so register request");
            InterSubsystemPubSubPublisherSubscriptionRegistration newPubSubRegistration = getDistributedPubSubSubscriptionMapIM().addSubscriptionToPublisher(subscriptionList, publisherServiceName);
            newPubSubRegistration.setPublisherServiceRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_PENDING_NO_PROVIDERS);
            newPubSubRegistration.setRegistrationCommentary("Cannot locate suitable publisher");
            return (newPubSubRegistration);
        }
        getLogger().trace(".subscribeToRemotePublishers(): A publisher is available");
        // Now we are going to Subscribe to each Publisher, and we will let the Petasos framework handle
        // the fact we don't want duplicate messages
        getLogger().trace(".subscribeToRemotePublishers(): Now subscribing to each of the potential publishers");
        List<MultiPublisherResponseSet> responseSetList = new ArrayList<>();
        boolean aSubscriptionWasSuccessful = false;
        RemoteSubscriptionResponse aSuccessfulResponse = null;
        getLogger().trace(".subscribeToRemotePublishers(): Now subscribing to each of the potential publishers");
        for (InterSubsystemPubSubPublisherRegistration currentPublisherRegistration : scopedPublisherRegistrations) {
            InterSubsystemPubSubParticipant currentPublisher = currentPublisherRegistration.getPublisher();
            if (currentPublisher.getEndpointStatus().equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL)) {
                String publisherInstanceName = currentPublisher.getEndpointID().getEndpointChannelName();
                getLogger().trace(".subscribeToRemotePublishers(): subscribing to: {}", publisherInstanceName);
                RemoteSubscriptionResponse remoteSubscriptionResponse = subscribeToPublisherInstance(subscriptionList,publisherInstanceName);
                updatePublisherRegistration(remoteSubscriptionResponse);
                if (remoteSubscriptionResponse.isSubscriptionSuccessful()) {
                    aSubscriptionWasSuccessful = aSubscriptionWasSuccessful || true;
                    aSuccessfulResponse = remoteSubscriptionResponse;
                }
            }
        }
        if(aSubscriptionWasSuccessful){
            getLogger().trace(".subscribeToRemotePublishers(): aSubscriptionWasSuccessful->{}", aSubscriptionWasSuccessful);
            InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = updateSubscriptionRegistration(subscriptionList, publisherServiceName, true);
            getLogger().debug(".subscribeToRemotePublishers(): Exit, subscriptionRegistration->{} ", subscriptionRegistration);
            return(subscriptionRegistration);
        } else {
            InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = updateSubscriptionRegistration(subscriptionList, null, false);
            getLogger().debug(".subscribeToRemotePublishers(): Exit, subscriptionRegistration->{} ", subscriptionRegistration);
            return(subscriptionRegistration);
        }
    }

    /**
     * This methods updates the InterSubsystemPubSubPublisherSubscriptionRegistration for the (successful or
     * otherwise) subscription registration activity.
     * @param subscriptionList
     * @param publisherServiceName
     * @param isSuccessfullySubscribed
     * @return
     */
    protected InterSubsystemPubSubPublisherSubscriptionRegistration updateSubscriptionRegistration( List<DataParcelManifest> subscriptionList, String publisherServiceName, boolean isSuccessfullySubscribed){
        if(StringUtils.isEmpty(publisherServiceName)){
            return(null);
        }
        InterSubsystemPubSubPublisherSubscriptionRegistration serviceSubscription = getDistributedPubSubSubscriptionMapIM().getPublisherServiceSubscription(publisherServiceName);
        if(serviceSubscription == null){
            serviceSubscription = getDistributedPubSubSubscriptionMapIM().addSubscriptionToPublisher(subscriptionList, publisherServiceName);
        }
        if(isSuccessfullySubscribed) {
            serviceSubscription.setRegistrationDate(Date.from(Instant.now()));
            serviceSubscription.setPublisherServiceRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_ACTIVE);
        } else {
            serviceSubscription.setPublisherServiceRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_PENDING_NO_PROVIDERS);
        }
        return(serviceSubscription);
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
        getLogger().debug(".updatePublisherRegistration(): Entry, subscriptionResponse->{}", subscriptionResponse);
        boolean aSubscriptionWasSuccessful = subscriptionResponse.isSubscriptionSuccessful();
        PubSubParticipant publisher = subscriptionResponse.getPublisher();
        if (publisher == null) {
            getLogger().debug(".updatePublisherRegistration(): Exit, publisher is not within subscription response");
            return;
        }
        InterSubsystemPubSubParticipant interSubsystemParticipant = publisher.getInterSubsystemParticipant();
        if (interSubsystemParticipant == null) {
            getLogger().debug(".updatePublisherRegistration(): Exit, interSubsystemParticipant is not within subscription response");
            return;
        }
        PetasosEndpointIdentifier endpointID = interSubsystemParticipant.getEndpointID();
        if (endpointID == null) {
            getLogger().debug(".updatePublisherRegistration(): Exit, endpointID is not within subscription response");
            return;
        }
        String publisherEndpointName = endpointID.getEndpointName();
        if (StringUtils.isEmpty(publisherEndpointName)) {
            getLogger().debug(".updatePublisherRegistration(): Exit, publisherEndpointNmae is not within subscription response");
            return;
        }
        getLogger().trace(".updatePublisherRegistration(): processing response for->{}", publisherEndpointName);
        InterSubsystemPubSubPublisherRegistration cachedPublisherRegistration = getDistributedPubSubSubscriptionMapIM().getPublisherInstanceRegistration(publisherEndpointName);
        getLogger().trace(".updatePublisherRegistration(): cachedPublisherRegistration->{}", cachedPublisherRegistration);
        if (cachedPublisherRegistration == null) {
            getLogger().trace(".updatePublisherRegistration(): Adding publisher instance to cache");
            getEndpointMap().addEndpoint(publisher.getInterSubsystemParticipant());
            cachedPublisherRegistration = getDistributedPubSubSubscriptionMapIM().registerPublisherInstance(publisher.getInterSubsystemParticipant());
        } else {
            getLogger().trace(".updatePublisherRegistration(): Updating content in cached entry");
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


    /**
     *
     * @param subscriptionList
     * @param petasosEndpointChannelName
     * @return
     */
    private RemoteSubscriptionResponse subscribeToPublisherInstance(List<DataParcelManifest> subscriptionList, String petasosEndpointChannelName ) {
        getLogger().debug(".subscribeToPublisherInstance(): Entry, subscriptionList->{}, petasosEndpointChannelName->{} ", subscriptionList, petasosEndpointChannelName);
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
        getLogger().trace(".subscribeToPublisherInstance(): Build subscriptionRequest (RemoteSubscriptionRequest)");
        RemoteSubscriptionRequest subscriptionRequest = new RemoteSubscriptionRequest();
        subscriptionRequest.setSubscriber(getParticipant());
        subscriptionRequest.setSubscriptionList(subscriptionList);
        getLogger().trace(".subscribeToPublisherInstance(): subscriptionRequest built, value->{}", subscriptionRequest);
        getLogger().trace(".subscribeToPublisherInstance(): Now ascertain if the publisher is actually available");
        if (isPetasosEndpointChannelAvailable(petasosEndpointChannelName)) {
            getLogger().trace(".subscribeToPublisherInstance(): Publisher is available, so register");
            String petasosEndpointName = getEndpointNameUtilities().getOAMPubSubEndpointChannelNameFromOtherChannelName(petasosEndpointChannelName);
            PetasosAdapterAddress publisherAddress = getTargetMemberAdapterAddress(petasosEndpointName);
            if (publisherAddress != null) {
                getLogger().trace(".subscribeToPublisherInstance(): Subscribing to PublisherInstance->{}", petasosEndpointName);
                response = rpcRequestSubscription(publisherAddress, subscriptionRequest);
            }
        }
        if(response == null){
            getLogger().trace(".subscribeToPublisherInstance(): Publisher ({}) is not available!!!", petasosEndpointChannelName);
            response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_PENDING_NO_PROVIDERS);
            response.setSubscriptionCommentary("Publisher ("+petasosEndpointChannelName+") is not available!");
        }
        getLogger().debug(".subscribeToPublisherInstance(): Exit, response->{}", response);
        return (response);
    }


    /**
     *
     * @param publisherAddress
     * @param subscriptionRequest
     * @return
     */

    public RemoteSubscriptionResponse rpcRequestSubscription(PetasosAdapterAddress publisherAddress, RemoteSubscriptionRequest subscriptionRequest){
        getLogger().debug(".rpcRequestSubscription(): Entry, publisher->{}, subscriptionRequest->{}", publisherAddress, subscriptionRequest);
        if(publisherAddress == null || subscriptionRequest == null){
            getLogger().debug(".rpcRequestSubscription: publisherAddress or subscriptionRequest are null");
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (publisherAddress or subscriptionRequest are null)");
            return(response);
        }
        if(!publisherAddress.getAddressType().equals(PetasosAdapterAddressTypeEnum.ADDRESS_TYPE_JGROUPS)){
            getLogger().debug(".rpcRequestSubscription: publisherAddress or subscriptionRequest are null");
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
            getLogger().debug(".rpcRequestSubscription(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().debug(".rpcRequestSubscription(): Error (NoSuchMethodException)->", e);
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (NoSuchMethodException)" + e.getMessage());
            return(response);
        } catch (Exception e) {
            getLogger().debug(".rpcRequestSubscription: Error (GeneralException) ->",e);
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (GeneralException)" + e.getMessage());
            return(response);
        }
    }


    //
    // Schedule Subscription Checks
    //

    /**
     *
     */
    protected boolean performFullSubscriptionCheck(){
        getLogger().debug(".performSubscriptionCheck(): Entry");
        List<InterSubsystemPubSubPublisherSubscriptionRegistration> subscriptionRegistrationList = getDistributedPubSubSubscriptionMapIM().getAllPublisherServiceSubscriptions();
        getLogger().trace(".performSubscriptionCheck(): Iterate through Subscription Registrations");
        for(InterSubsystemPubSubPublisherSubscriptionRegistration currentServiceRegistration: subscriptionRegistrationList){
            getLogger().trace(".performSubscriptionCheck(): Looking for publisher->{}", currentServiceRegistration.getPublisherServiceName());
            List<InterSubsystemPubSubPublisherRegistration> instanceRegistrations = getDistributedPubSubSubscriptionMapIM().getPublisherServiceProviderInstanceRegistrations(currentServiceRegistration.getPublisherServiceName());
            getLogger().trace(".performSubscriptionCheck(): Iterate through Publisher Registrations");
            for(InterSubsystemPubSubPublisherRegistration currentInstanceRegistration: instanceRegistrations) {
                getLogger().trace(".performSubscriptionCheck(): Iterating, looking at publisher->{}", currentInstanceRegistration.getPublisher().getEndpointID().getEndpointName());
                boolean weNeedToSubscribeToPublisher = currentInstanceRegistration.getPublisherStatus().equals(PUBLISHER_NOT_UTILISED)
                        || currentInstanceRegistration.getPublisherStatus().equals(PUBLISHER_REGISTERED);
                getLogger().trace(".performSubscriptionCheck(): weNeedToSubscribeToPublisher->{}", weNeedToSubscribeToPublisher);
                boolean publisherIsOperational = currentInstanceRegistration.getPublisher().getEndpointStatus().equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL);
                getLogger().trace(".performSubscriptionCheck(): publisherIsOperational->{}", publisherIsOperational);
                boolean isWithinScope = currentInstanceRegistration.getPublisher().getEndpointScope().equals(getPetasosEndpoint().getEndpointScope());
                getLogger().trace(".performSubscriptionCheck(): isWithinScope->{}", isWithinScope);
                if (isWithinScope && weNeedToSubscribeToPublisher && publisherIsOperational) {
                    String publisherInstanceName = currentInstanceRegistration.getPublisher().getEndpointID().getEndpointChannelName();
                    getLogger().trace(".performSubscriptionCheck(): Subscribing.... to isntance ->{}", publisherInstanceName);
                    RemoteSubscriptionResponse remoteSubscriptionResponse = subscribeToPublisherInstance(currentServiceRegistration.getSubscriptionList(), publisherInstanceName);
                    if(remoteSubscriptionResponse.isSubscriptionSuccessful()){
                        currentServiceRegistration.setPublisherServiceRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_ACTIVE);
                    }
                    updatePublisherRegistration(remoteSubscriptionResponse);
                }
            }
        }
        boolean requiresAnotherSubscriptionCheck = false;
        for(InterSubsystemPubSubPublisherSubscriptionRegistration currentServiceRegistration: subscriptionRegistrationList){
            if(currentServiceRegistration.getPublisherServiceRegistrationStatus().equals(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_PENDING_NO_PROVIDERS)){
                requiresAnotherSubscriptionCheck = true;
                break;
            }
            getLogger().trace(".performSubscriptionCheck(): Looking for publisher->{}", currentServiceRegistration.getPublisherServiceName());
            List<InterSubsystemPubSubPublisherRegistration> instanceRegistrations = getDistributedPubSubSubscriptionMapIM().getPublisherServiceProviderInstanceRegistrations(currentServiceRegistration.getPublisherServiceName());
            getLogger().trace(".performSubscriptionCheck(): Iterate through Publisher Registrations");
            for(InterSubsystemPubSubPublisherRegistration currentInstanceRegistration: instanceRegistrations) {
                if(currentInstanceRegistration.getPublisherStatus().equals(PUBLISHER_NOT_UTILISED)){
                    requiresAnotherSubscriptionCheck = true;
                    break;
                }
            }
        }
        getLogger().debug(".performSubscriptionCheck(): Exit, requiresAnotherSubscriptionCheck->{}",requiresAnotherSubscriptionCheck);
        return(requiresAnotherSubscriptionCheck);
    }

    /**
     *
     */
    public void scheduleASubscriptionCheck() {
        getLogger().debug(".scheduleASubscriptionCheck(): Entry (subscriptionCheckScheduled->{}", subscriptionCheckScheduled);
        synchronized (subscriptionCheckLock) {
            if (subscriptionCheckScheduled) {
                // do nothing, it is already scheduled
            } else {
                TimerTask subscriptionCheckTask = new TimerTask() {
                    public void run() {
                        getLogger().debug(".subscriptionCheckTask(): Entry");
                        boolean doAgain = performFullSubscriptionCheck();
                        getLogger().debug(".subscriptionCheckTask(): doAgain ->{}", doAgain);
                        if (!doAgain) {
                            if(subscriptionCheckCount > CHANGE_DETECTION_SUBSCRIPTION_CHECK_COUNT) {
                                cancel();
                                subscriptionCheckScheduled = false;
                                subscriptionCheckCount = 0;
                            } else {
                                subscriptionCheckCount += 1;
                            }
                        }
                        getLogger().debug(".subscriptionCheckTask(): Exit");
                    }
                };
                Timer timer = new Timer("SubscriptionScheduleTimer");
                timer.schedule(subscriptionCheckTask, SUBSCRIPTION_CHECK_INITIAL_DELAY, SUBSCRIPTION_CHECK_PERIOD);
                subscriptionCheckScheduled = true;
            }
        }
        getLogger().debug(".scheduleASubscriptionCheck(): Exit");
    }

    //
    // Endpoint/Participant tests
    //

    protected boolean hasParticipantServiceName(PubSubParticipant participant){
        if(participant != null){
            if(participant.getInterSubsystemParticipant() != null){
                if(participant.getInterSubsystemParticipant().getEndpointID() != null){
                    if(participant.getInterSubsystemParticipant().getEndpointServiceName() != null){
                        return(true);
                    }
                }
            }
        }
        return(false);
    }

    protected boolean isParticipantServiceAvailable(String participantServiceName){
        getLogger().debug(".isParticipantAvailable(): Entry, publisherServiceName->{}", participantServiceName);
        boolean participantIsAvailable = getAvailableParticipantInstanceName(participantServiceName) != null;
        getLogger().debug(".isParticipantAvailable(): Exit, returning->{}", participantIsAvailable);
        return(participantIsAvailable);
    }

    protected String getAvailableParticipantInstanceName(PubSubParticipant participant){
        getLogger().debug(".getAvailableParticipantInstanceName(): Entry, participant->{}", participant);
        String serviceInstanceName = null;
        if(hasParticipantServiceName(participant)) {
            String publisherServiceName = getAvailableParticipantInstanceName(participant.getInterSubsystemParticipant().getEndpointServiceName());
            serviceInstanceName = getAvailableParticipantInstanceName(publisherServiceName);
        }
        getLogger().debug(".getAvailableParticipantInstanceName(): Exit, serviceInstanceName->{}", serviceInstanceName);
        return(serviceInstanceName);
    }

    public String getAvailableParticipantInstanceName(String participantServiceName){
        getLogger().debug(".getAvailableParticipantInstanceName(): Entry, participantServiceName->{}", participantServiceName);
        PetasosAdapterAddress targetAddress = getTargetMemberAdapterInstanceForService(participantServiceName);
        String participantInstanceName = targetAddress.toString();
        getLogger().debug(".getAvailableParticipantInstanceName(): Exit, participantInstanceName->{}", participantInstanceName);
        return(participantInstanceName);
    }

    public boolean isPetasosEndpointChannelAvailable(String petasosEndpointChannelName){
        getLogger().debug(".isParticipantInstanceAvailable(): Entry, participantInstanceName->{}", petasosEndpointChannelName);
        boolean participantInstanceNameStillActive = getTargetMemberAddress(petasosEndpointChannelName) != null;
        getLogger().debug(".isParticipantInstanceAvailable(): Exit, participantInstanceNameStillActive->{}", participantInstanceNameStillActive);
        return(participantInstanceNameStillActive);
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

    @Override
    public void notifyNewPublisher(InterSubsystemPubSubParticipant newPublisher) {
        getLogger().debug(".notifyNewPublisher(): Entry, newPublisher->{}", newPublisher);
        boolean inScope = newPublisher.getEndpointScope().equals(getPetasosEndpoint().getEndpointScope());
        if(inScope) {
            InterSubsystemPubSubPublisherRegistration publisherInstanceRegistration = getDistributedPubSubSubscriptionMapIM().getPublisherInstanceRegistration(newPublisher);
            if(publisherInstanceRegistration == null){
                publisherInstanceRegistration = getDistributedPubSubSubscriptionMapIM().registerPublisherInstance(newPublisher);
            }
            if(publisherInstanceRegistration == null){
                getLogger().debug(".notifyNewPublisher(): Cannot register publisher->{}",newPublisher);
                return;
            }
            String endpointServiceName = newPublisher.getEndpointServiceName();
            InterSubsystemPubSubPublisherSubscriptionRegistration publisherServiceSubscription = getDistributedPubSubSubscriptionMapIM().getPublisherServiceSubscription(endpointServiceName);
            if(publisherServiceSubscription == null){
                getLogger().debug(".notifyNewPublisher(): Nothing is interested in this publisher!");
                return;
            }
            String publisherInstanceName = newPublisher.getEndpointID().getEndpointChannelName();
            getLogger().trace(".notifyNewPublisher(): Subscribing.... to instance ->{}", publisherInstanceName);
            RemoteSubscriptionResponse remoteSubscriptionResponse = subscribeToPublisherInstance(publisherServiceSubscription.getSubscriptionList(), publisherInstanceName);
            if(remoteSubscriptionResponse.isSubscriptionSuccessful()){
                getLogger().trace(".notifyNewPublisher(): Subscription to {} Successful", publisherInstanceName);
                publisherServiceSubscription.setPublisherServiceRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_ACTIVE);
            } else {
                getLogger().trace(".notifyNewPublisher(): Subscription to {} Failed, reason->{}", publisherInstanceName, remoteSubscriptionResponse.getSubscriptionCommentary());
            }
            getLogger().trace(".notifyNewPublisher(): Updating publisher");
            updatePublisherRegistration(remoteSubscriptionResponse);
            getLogger().trace(".notifyNewPublisher(): Updating publisher.... finished, exiting");
        }
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
            if(currentMember.contains(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT.getFunctionName())){
                candidateSet.add(currentMember);
            }
        }
        return(candidateSet);
    }

}

