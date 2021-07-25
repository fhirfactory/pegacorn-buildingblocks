/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.endpoints.endpoints.roles;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.common.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosInterfaceAddress;
import net.fhirfactory.pegacorn.endpoints.endpoints.roles.base.PubSubParticipantEndpointServiceInterface;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.activitycache.EndpointCheckSchedule;
import net.fhirfactory.pegacorn.petasos.datasets.manager.PublisherRegistrationMapIM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.*;
import net.fhirfactory.pegacorn.endpoints.endpoints.roles.base.PubSubParticipantRoleBase;
import net.fhirfactory.pegacorn.platform.edge.endpoints.technologies.activitycache.datatypes.IPCEndpointCheckScheduleElement;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionRequest;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionResponse;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionStatus;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PubSubPublisherRole extends PubSubParticipantRoleBase {
    private static final Logger LOG = LoggerFactory.getLogger(PubSubPublisherRole.class);

    private Object publisherCheckLock;
    private boolean publisherCheckScheduled;
    private EndpointCheckSchedule publisherCheckSchedule;
    private static Long MEMBERSHIP_CHECK_DELAY = 5000L;


    public PubSubPublisherRole(
            ProcessingPlantInterface processingPlant,
            PubSubParticipantEndpointServiceInterface endpointServiceInterface,
            PubSubParticipant me,
            PublisherRegistrationMapIM publisherMapIM,
            JChannel channel,
            RpcDispatcher dispatcher,
            EdgeForwarderService forwarderService){
        super(processingPlant, endpointServiceInterface, me, publisherMapIM, channel, dispatcher, forwarderService);
        this.publisherCheckLock = new Object();
        this.publisherCheckScheduled = false;
        this.publisherCheckSchedule = new EndpointCheckSchedule();
    }

    //
    // Overrides of Abstract Methods
    //

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    //
    // Publisher Management
    //

    /**
     *
     * @param publisher
     * @return
     */
    public InterSubsystemPubSubPublisherRegistration rpcRegisterPublisherHandler(InterSubsystemPubSubParticipant publisher){
        getLogger().info(".rpcRegisterPublisherHandler(): Entry, publisher->{}", publisher);
        InterSubsystemPubSubPublisherRegistration registration = null;
        if(getPublisherMapIM().isPublisherRegistered(publisher)){
            getLogger().info(".rpcRegisterPublisherHandler(): Publisher is already Registered");
            registration = getPublisherMapIM().getPublisherInstanceRegistration(publisher);
        } else {
            getLogger().info(".rpcRegisterPublisherHandler(): Publisher is not locally Registered, so add it");
            registration = getPublisherMapIM().registerPublisherInstance(publisher);
            getLogger().info(".rpcRegisterPublisherHandler(): Publisher Registered, registration->{}", registration);
            if(registration.getPublisherStatus().equals(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_REGISTERED)){
                getLogger().info(".rpcRegisterPublisherHandler(): Scheduling complete subscription check");
                getLogger().info(".rpcRegisterPublisherHandler(): Scheduling of complete subscription check completed");
            }
        }
        getLogger().info("rpcRegisterPublisherHandler(): Exit, registration->{}", registration);
        return(registration);
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

        NetworkSecurityZoneEnum subscriberSecurityZone = subscriber.getInterSubsystemParticipant().getSecurityZone();
        String subscriberSite = subscriber.getInterSubsystemParticipant().getSite();

        boolean sameZone = subscriberSecurityZone.equals(getMe().getInterSubsystemParticipant().getSecurityZone());
        boolean sameSite = subscriberSite.contentEquals(getMe().getInterSubsystemParticipant().getSite());
        boolean doSubscription = false;
        if(sameSite && sameZone){
            if(getEdgeForwarderService().supportsIntraZoneIPC()){
                doSubscription = true;
            }
        }
        if(sameSite && !sameZone){
            if(getEdgeForwarderService().supportsMultiZoneIPC()){
                doSubscription = true;
            }
        }
        if(!sameSite){
            if(getEdgeForwarderService().supportsMultiSiteIPC()){
                doSubscription = true;
            }
        }
        getLogger().trace(".rpcRequestSubscriptionHandler(): doSubscription->{}", true);
        RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
        if (doSubscription) {
            RemoteSubscriptionStatus subscriptionStatus = getEdgeForwarderService().subscribeOnBehalfOfRemoteSubscriber(subscriptionList, subscriber);
            response.setPublisher(getMe());
            response.setSubscriptionRegistrationDate(Date.from(Instant.now()));
            response.setSubscriptionSuccessful(subscriptionStatus.isSubscriptionSuccessful());
            response.setSubscriptionCommentary(subscriptionStatus.getSubscriptionCommentary());
            response.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_CONNECTION_ESTABLISHED);
            response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_ACTIVE);
        } else {
            response.setPublisher(getMe());
            response.setSubscriptionRegistrationDate(Date.from(Instant.now()));
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("This IPC Endpoint does not support PubSub from Provided Node");
            response.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_CONNECTION_FAILED);
            response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_FAILED);
        }

        getLogger().info(".rpcRequestSubscriptionHandler(): Exit, response->{}", response);
        return(response);
    }

    /**
     *
     */
    protected boolean removePublisher(){
        getLogger().info(".performPublisherCheck(): Entry");
        boolean doAgain = false;

        getLogger().info(".performPublisherCheck(): Getting a list of all Publishers");
        List<String> allPublishers = getPublisherMapIM().getAllPublishers();
        getLogger().info(".performPublisherCheck(): Publisher List retrieved, now iterating through");
        for(String publisherInstanceName: allPublishers){
            getLogger().info(".performPublisherCheck(): Checking Publisher->{}", publisherInstanceName);
            InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = null;
            if(isParticipantInstanceAvailable(publisherInstanceName)){
                // Do Nothing!
                getLogger().info(".performPublisherCheck(): Publisher->{} is still available, doing nothing!", publisherInstanceName);
            } else {
                getLogger().info(".performPublisherCheck(): Publisher->{} is not available, removing!", publisherInstanceName);
                InterSubsystemPubSubPublisherRegistration publisherInstanceRegistration = getPublisherMapIM().getPublisherInstanceRegistration(publisherInstanceName);
                getPublisherMapIM().unregisterPublisherInstance(publisherInstanceRegistration.getPublisher());
            }
            getLogger().info(".performPublisherCheck(): Now checking if there is an existing subscriptionRegistration");
            if(subscriptionRegistration != null) {
                if (subscriptionRegistration.getPublisherServiceRegistrationStatus().equals(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_PENDING_NO_PROVIDERS)) {
                    getLogger().warn(".performPublisherCheck(): No publisher for service ({}) is available", subscriptionRegistration.getPublisherServiceName() );
                } else {
                    getLogger().warn(".performPublisherCheck(): Publisher for service ({}) is available", subscriptionRegistration.getPublisherServiceName() );
                }
            } else {
                getLogger().info(".performPublisherCheck(): No subscription available");
            }
        }
        getLogger().info(".performPublisherCheck(): Now scheduling a subscription check");
  //      scheduleSubscriptionCheck();
        getLogger().info(".performPublisherCheck(): Exit, returning doAgain->{}", doAgain);
        return(doAgain);
    }

    @Override
    protected void performPublisherEventUpdateCheck(List<PetasosInterfaceAddress> publishersRemoved, List<PetasosInterfaceAddress> publishersAdded) {
        for(PetasosInterfaceAddress currentAddress: publishersRemoved){
            InterSubsystemPubSubPublisherRegistration publisherRegistration = getPublisherMapIM().getPublisherInstanceRegistration(currentAddress.getAddressName());
            if(publisherRegistration != null){
                getPublisherMapIM().unregisterPublisherInstance(currentAddress.getAddressName());
            }
        }
        for(PetasosInterfaceAddress currentAddress: publishersAdded){
            publisherCheckSchedule.scheduleEndpointCheck(currentAddress, false, true);
        }
        schedulePublisherCheck();
    }

    @Override
    protected void performSubscriberEventUpdateCheck(List<PetasosInterfaceAddress> subscribersRemoved, List<PetasosInterfaceAddress> subscribersAdded) {

    }

    /**
     *
     */
    public void schedulePublisherCheck() {
        getLogger().info(".schedulePublisherCheck(): Entry (schedulePublisherCheck->{}", isPublisherCheckScheduled());
        synchronized (getPublisherCheckLock()) {
            if (isSubscriptionCheckScheduled()) {
                // do nothing, it is already scheduled
            } else {
                TimerTask publisherCheckTask = new TimerTask() {
                    public void run() {
                        getLogger().info(".publisherCheckTask(): Entry");
                        boolean doAgain = performPublisherCheckTask();
                        getLogger().info(".publisherCheckTask(): doAgain ->{}", doAgain);
                        if (!doAgain) {
                            cancel();
                            setPublisherCheckScheduled(false);
                        }
                        getLogger().info(".publisherCheckTask(): Exit");
                    }
                };
                Timer timer = new Timer("publisherCheckTask");
                timer.schedule(publisherCheckTask, getParticipantMembershipCheckDelay(), getParticipantMembershipCheckPeriod());
                setSubscriptionCheckScheduled(true);
            }
        }
        getLogger().info(".publisherCheckTask(): Exit");
    }

    public boolean performPublisherCheckTask(){
        List<IPCEndpointCheckScheduleElement> endpointsToCheck = publisherCheckSchedule.getEndpointsToCheck();
        for(IPCEndpointCheckScheduleElement currentScheduleElement: endpointsToCheck) {
            if(currentScheduleElement.isEndpointAdded()) {
                String participantInstanceName = currentScheduleElement.getEndpoint().getAddressName();
                InterSubsystemPubSubPublisherRegistration publisherRegistration = getPublisherMapIM().getPublisherInstanceRegistration(participantInstanceName);
                if (publisherRegistration == null) {
                    String myName = getMe().getInterSubsystemParticipant().getIdentifier().getServiceInstanceName();
                    InterSubsystemPubSubParticipant distributedPublisher = requestParticipantDetail(currentScheduleElement.getEndpoint().getJGroupsAddress(), myName);
                    publisherRegistration = getPublisherMapIM().registerPublisherInstance(distributedPublisher);
                }
            }
        }
        if(publisherCheckSchedule.scheduleIsEmpty()){
            return(false);
        } else {
            return(true);
        }
    }


    /**
     *
     * @param target
     * @param publisher
     * @return
     */
    public InterSubsystemPubSubPublisherRegistration requestPublisherRegistration(PetasosInterfaceAddress target, InterSubsystemPubSubParticipant publisher){
        getLogger().info(".requestPublisherRegistration(): Entry, target->{}, publisher->{}", target, publisher);
        Object objectSet[] = new Object[1];
        Class classSet[] = new Class[1];
        objectSet[0] = publisher;
        classSet[0] = InterSubsystemPubSubParticipant.class;
        RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, RPC_UNICAST_TIMEOUT);
        InterSubsystemPubSubPublisherRegistration response = null;
        Address jgroupsAddress = target.getJGroupsAddress();
        try {
            response = getRPCDispatcher().callRemoteMethod(jgroupsAddress, "rpcRegisterPublisherHandler", objectSet, classSet, requestOptions);
        } catch (Exception e) {
            getLogger().error(".requestPublisherRegistration(): Error registering Publisher, message->{}", e.getMessage());
            return(null);
        }
        getLogger().info(".requestPublisherRegistration(): Exit, response->{}", response);
        return(response);
    }

    public void announceMeAsAPublisher(){
        List<PetasosInterfaceAddress> allEndpoints = getIPCEndpoint().getAllPubSubParticipantAddresses();
        for(PetasosInterfaceAddress endpoint: allEndpoints){
            requestPublisherRegistration(endpoint, getMe().getInterSubsystemParticipant());
        }
    }

    //
    // Getters and Setters
    //


    public Object getPublisherCheckLock() {
        return publisherCheckLock;
    }

    public void setPublisherCheckLock(Object publisherCheckLock) {
        this.publisherCheckLock = publisherCheckLock;
    }

    public boolean isPublisherCheckScheduled() {
        return publisherCheckScheduled;
    }

    public void setPublisherCheckScheduled(boolean publisherCheckScheduled) {
        this.publisherCheckScheduled = publisherCheckScheduled;
    }
}
