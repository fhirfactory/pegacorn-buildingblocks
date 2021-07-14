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
package net.fhirfactory.pegacorn.platform.edge.endpoints.jgroups.common;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.deployment.topology.model.common.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.petasos.model.pubsub.*;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionRequest;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionResponse;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionStatus;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class JGroupsIPCPubSubEndpoint extends JGroupsIPCEndpoint{

    private boolean completeSubscriptionCheckScheduled;
    private Object completeSubscriptionCheckScheduledLock;
    private boolean subscriptionCheckScheduled;
    private Object subscriptionCheckScheduledLock;
    private Long SUBSCRIPTION_CHECK_DELAY = 5000L;

    public JGroupsIPCPubSubEndpoint(){
        super();
        this.subscriptionCheckScheduled = false;
        this.subscriptionCheckScheduledLock = new Object();
        this.completeSubscriptionCheckScheduledLock = new Object();
        this.completeSubscriptionCheckScheduled = false;
    }

    @Override
    protected void additionalInitialisation(){
        registerPublisherIntoSolution(getPubsubParticipant().getInterSubsystemParticipant());
    }

    public abstract EdgeForwarderService getEdgeForwarderService();

    //
    // Remote Called Procedures for Subscribing
    //

    /**
     *
     * @param subscriptionRequest
     * @return
     */
    public RemoteSubscriptionResponse rpcRequestSubscriptionHandler(RemoteSubscriptionRequest subscriptionRequest){
        getLogger().debug(".rpcRequestSubscriptionHandler(): Entry, subscriptionRequest->{}", subscriptionRequest);

        PubSubParticipant subscriber = subscriptionRequest.getSubscriber();
        List<DataParcelManifest> subscriptionList = subscriptionRequest.getSubscriptionList();

        NetworkSecurityZoneEnum subscriberSecurityZone = subscriber.getInterSubsystemParticipant().getSecurityZone();
        String subscriberSite = subscriber.getInterSubsystemParticipant().getSite();

        boolean sameZone = subscriberSecurityZone.equals(getPubsubParticipant().getInterSubsystemParticipant().getSecurityZone());
        boolean sameSite = subscriberSite.contentEquals(getPubsubParticipant().getInterSubsystemParticipant().getSite());
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
        RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
        if (doSubscription) {
            RemoteSubscriptionStatus subscriptionStatus = getForwarderService().subscribeOnBehalfOfRemoteSubscriber(subscriptionList, subscriber);
            response.setPublisher(this.getPubsubParticipant());
            response.setSubscriptionRegistrationDate(Date.from(Instant.now()));
            response.setSubscriptionSuccessful(subscriptionStatus.isSubscriptionSuccessful());
            response.setSubscriptionCommentary(subscriptionStatus.getSubscriptionCommentary());
            response.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_CONNECTION_ESTABLISHED);
            response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_SUCCESSFUL);
        } else {
            response.setPublisher(this.getPubsubParticipant());
            response.setSubscriptionRegistrationDate(Date.from(Instant.now()));
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("This IPC Endpoint does not support PubSub from Provided Node");
            response.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_PUBLISHER_NOT_REACHABLE);
            response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_FAILED);
        }

        getLogger().debug(".rpcRequestSubscriptionHandler(): Exit, response->{}", response);
        return(response);
    }

    /*
    public RemoteSubscriptionResponse rpcModifySubscription(RemoteSubscriptionRequest subscriptionRequest){
        getLogger().debug(".rpcModifySubscription(): Entry, subscriptionRequest->{}", subscriptionRequest);
        RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
        Address myAddress = getIPCChannel().getAddress();
        response.setPublisherIdentifier(this.getIdentifier());
        response.setSubscriptionRegistrationDate(Date.from(Instant.now()));
        RemoteSubscriptionRequestLocal subscriptionRequestLocal = new RemoteSubscriptionRequestLocal(subscriptionRequest);
        subscriptionRequestLocal.setAddress(getTargetAddress(subscriptionRequest.getIdentifier()));
        RemoteSubscriptionStatus subscriptionStatus = getDistributionMap().addRemoteSubscription(subscriptionRequestLocal);
        response.setSubscriptionSuccessful(subscriptionStatus.isSubscriptionSuccessful());
        response.setSubscriptionCommentary(subscriptionStatus.getSubscriptionCommentary());
        getLogger().debug(".rpcModifySubscription(): Exit, response->{}", response);
        return(response);
    }
     */

    //
    // Local Procedure Calls for Subscribing
    //

    /**
     *
     * @param subscriptionList
     * @param publisher
     * @return
     */
    public RemoteSubscriptionResponse requestSubscription(List<DataParcelManifest> subscriptionList, PubSubParticipant publisher ) {
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
        subscriptionRequest.setSubscriber(getPubsubParticipant());
        subscriptionRequest.setSubscriptionList(subscriptionList);
        getLogger().info(".requestSubscription(): subscriptionRequest built, value->{}", subscriptionRequest);
        getLogger().info(".requestSubscription(): Now ascertain if the publisher is actually available");
        if (isPublisherAvailable(publisher)) {
            getLogger().info(".requestSubscription(): Publisher is available, so register");
            response = rpcRequestSubscription(getTargetAddress(publisher), subscriptionRequest);
        } else {
            getLogger().error(".requestSubscription(): Publisher ({}) is not available!!!", publisher);
            response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_CONNECTION_FAILED);
            response.setSubscriptionCommentary("Publisher ("+publisher+") is not available!");
        }
        getLogger().info(".requestSubscription(): Exit, response->{}", response);
        return (response);
    }

    /**
     *
     * @param publisher
     * @return
     */
    public boolean isPublisherAvailable(PubSubParticipant publisher){
        String publisherServiceName = publisher.getInterSubsystemParticipant().getIdentifier().getServiceName();
        boolean publisherAvailable = isPublisherAvailable(publisherServiceName);
        return(publisherAvailable);
    }

    /**
     *
     * @param publisherServiceName
     * @return
     */
    public boolean isPublisherAvailable(String publisherServiceName){
        getLogger().info(".isPublisherAvailable(): Entry, publisherServiceName->{}", publisherServiceName);
        boolean publisherAvailable = getTargetAddress(publisherServiceName) != null;
        getLogger().info(".isPublisherAvailable(): Exit, returning->{}", publisherAvailable);
        return(publisherAvailable);
    }

    public String getAvailablePublisherInstanceName(PubSubParticipant publisher){
        String publisherServiceName = publisher.getInterSubsystemParticipant().getIdentifier().getServiceName();
        String serviceName = getAvailablePublisherInstanceName(publisherServiceName);
        return(serviceName);
    }

    public String getAvailablePublisherInstanceName(String publisherServiceName){
        Address targetAddress = getTargetAddress(publisherServiceName);
        String instanceName = targetAddress.toString();
        return(instanceName);
    }

    /**
     *
     * @param publisherAddress
     * @param subscriptionRequest
     * @return
     */
    private RemoteSubscriptionResponse rpcRequestSubscription(Address publisherAddress, RemoteSubscriptionRequest subscriptionRequest){
        getLogger().debug(".rpcRequestSubscription(): Entry");
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = subscriptionRequest;
            classSet[0] = RemoteSubscriptionRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, RPC_UNICAST_TIMEOUT);
            RemoteSubscriptionResponse response = getRPCDispatcher().callRemoteMethod(publisherAddress, "rpcRequestSubscriptionHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".rpcRequestSubscription(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".rpcRequestSubscription(): Error (NoSuchMethodException) ->{}", e.getMessage());
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (NoSuchMethodException)" + e.getMessage());
            return(response);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".rpcRequestSubscription: Error (GeneralException) ->{}", e.getMessage());
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (GeneralException)" + e.getMessage());
            return(response);
        }
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
        if(getPublisherRegistrationMapIM().isPublisherRegistered(publisher)){
            getLogger().info(".rpcRegisterPublisherHandler(): Publisher is already Registered");
            registration = getPublisherRegistrationMapIM().getPublisherInstanceRegistration(publisher);
        } else {
            getLogger().info(".rpcRegisterPublisherHandler(): Publisher is not locally Registered, so add it");
            registration = getPublisherRegistrationMapIM().registerPublisherInstance(publisher);
            getLogger().info(".rpcRegisterPublisherHandler(): Publisher Registered, registration->{}", registration);
            if(registration.getPublisherStatus().equals(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_REGISTERED)){
                getLogger().info(".rpcRegisterPublisherHandler(): Scheduling complete subscription check");
                scheduleCompleteSubscriptionCheck();
                getLogger().info(".rpcRegisterPublisherHandler(): Scheduling of complete subscription check completed");
            }
        }
        getLogger().info("rpcRegisterPublisherHandler(): Exit, registration->{}", registration);
        return(registration);
    }

    /**
     *
     * @param target
     * @param publisher
     * @return
     */
    public InterSubsystemPubSubPublisherRegistration requestPublisherRegistration(Address target, InterSubsystemPubSubParticipant publisher){
        getLogger().info(".requestPublisherRegistration(): Entry, target->{}, publisher->{}", target, publisher);
        Object objectSet[] = new Object[1];
        Class classSet[] = new Class[1];
        objectSet[0] = publisher;
        classSet[0] = InterSubsystemPubSubParticipant.class;
        RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, RPC_UNICAST_TIMEOUT);
        InterSubsystemPubSubPublisherRegistration response = null;
        try {
            response = getRPCDispatcher().callRemoteMethod(target, "rpcRegisterPublisherHandler", objectSet, classSet, requestOptions);
        } catch (Exception e) {
            getLogger().error(".requestPublisherRegistration(): Error registering Publisher, message->{}", e.getMessage());
            return(null);
        }
        getLogger().info(".requestPublisherRegistration(): Exit, response->{}", response);
        return(response);
    }

    /**
     *
     * @param publisher
     */
    public void registerPublisherIntoSolution(InterSubsystemPubSubParticipant publisher){
        getLogger().debug(".publisherRegister(): Entry, publisher->{}", publisher);
        List<Address> addressList = getIPCChannel().getView().getMembers();
        for(Address currentAddress: addressList){
            if(currentAddress.toString().startsWith(this.getPubsubParticipant().getInterSubsystemParticipant().getIdentifier().getServiceName())){
                // don't do anything, as this address either refers to me or another instance of myself
            } else {
                getLogger().trace(".publisherRegister(): sending registration request to->{}", currentAddress);
                InterSubsystemPubSubPublisherRegistration subscriptionResponse = requestPublisherRegistration(currentAddress, publisher);
                getLogger().trace(".publisherRegister(): Registration request to->{}, subscriptionResponse->{}", currentAddress, subscriptionResponse);
            }
        }
        getLogger().debug(".publisherRegister(): Exit");
    }

    //
    // Remote Subscription Management
    //

    /**
     *
     */
    protected void scheduleCompleteSubscriptionCheck(){
        synchronized(this.completeSubscriptionCheckScheduledLock) {
            if (this.subscriptionCheckScheduled) {
                // do nothing, it is already scheduled
            } else {
                TimerTask completeSubscriptionCheckTask = new TimerTask() {
                    public void run() {
                        performCompleteSubscriptionCheck();
                        cancel();
                    }
                };
                Timer timer = new Timer("CompleteSubscriptionCheck");
                timer.schedule(completeSubscriptionCheckTask, this.SUBSCRIPTION_CHECK_DELAY);
                this.completeSubscriptionCheckScheduled = true;
            }
        }
    }

    /**
     *
     */
    protected void performCompleteSubscriptionCheck(){
        getLogger().info(".performCompleteSubscriptionCheck(): Entry");
        List<InterSubsystemPubSubPublisherSubscriptionRegistration> subscriptionRegistrationList = getPublisherRegistrationMapIM().getAllPublisherServiceSubscriptions();
        for(InterSubsystemPubSubPublisherSubscriptionRegistration currentServiceRegistration: subscriptionRegistrationList){
            String currentProviderServiceName = currentServiceRegistration.getPublisherServiceName();
            List<InterSubsystemPubSubPublisherRegistration> publisherServiceProviders = getPublisherRegistrationMapIM().getPublisherServiceProviderInstanceRegistrations(currentProviderServiceName);
            if(publisherServiceProviders.isEmpty()){
                // do nothing
            } else {
                for(InterSubsystemPubSubPublisherRegistration currentPublisher: publisherServiceProviders){
                    if(currentPublisher.getPublisherStatus().equals(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_REGISTERED)){
                        PubSubParticipant publisher = new PubSubParticipant();
                        publisher.setInterSubsystemParticipant(currentPublisher.getPublisher());
                        List<DataParcelManifest> subscriptionList = currentServiceRegistration.getSubscriptionList();
                        RemoteSubscriptionResponse remoteSubscriptionResponse = requestSubscription(subscriptionList, publisher);
                        if(remoteSubscriptionResponse.isSubscriptionSuccessful()){
                            currentPublisher.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_ACTIVE);
                            currentServiceRegistration.setRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_SUCCESSFUL);
                        } else {
                            getLogger().error(".performSubscriptionCheck(): Cannot subscribe to Publisher->{}", publisher);
                        }
                    }
                }
            }
        }
        synchronized(this.completeSubscriptionCheckScheduledLock) {
            this.completeSubscriptionCheckScheduled = false;
        }
    }

    /**
     *
     */
    protected void scheduleSubscriptionCheck(){
        synchronized(this.subscriptionCheckScheduledLock) {
            if (this.subscriptionCheckScheduled) {
                // do nothing, it is already scheduled
            } else {
                TimerTask subscriptionCheckTask = new TimerTask() {
                    public void run() {
                        boolean doAgain = performSubscriptionCheck();
                        if(!doAgain) {
                            cancel();
                        }
                    }
                };
                Timer timer = new Timer("SubscriptionCheck");
                timer.schedule(subscriptionCheckTask, this.SUBSCRIPTION_CHECK_DELAY);
                this.subscriptionCheckScheduled = true;
            }
        }
    }

    /**
     *
     */
    protected boolean performSubscriptionCheck(){
        getLogger().info(".performSubscriptionCheck(): Entry");
        List<InterSubsystemPubSubPublisherSubscriptionRegistration> subscriptionRegistrationList = getPublisherRegistrationMapIM().getAllPublisherServiceSubscriptions();
        for(InterSubsystemPubSubPublisherSubscriptionRegistration currentServiceRegistration: subscriptionRegistrationList){
            if(currentServiceRegistration.getRegistrationStatus().equals(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_PENDING_NO_PROVIDERS)) {
                getLogger().info(".performSubscriptionCheck(): PUBLISHER_REGISTRATION_PENDING_NO_PROVIDERS found");
                String currentProviderServiceName = currentServiceRegistration.getPublisherServiceName();
                List<InterSubsystemPubSubPublisherRegistration> publisherServiceProviders = getPublisherRegistrationMapIM().getPublisherServiceProviderInstanceRegistrations(currentProviderServiceName);
                if (publisherServiceProviders.isEmpty()) {
                    getLogger().info(".performSubscriptionCheck(): No suitable publishers in our registry, so let's scan for one");
                    if(isPublisherAvailable(currentProviderServiceName)){
                        getLogger().info(".performSubscriptionCheck(): Suitable Publisher is available on JGroups, so using");
                        PubSubParticipant publisher = new PubSubParticipant();
                        getLogger().info(".performSubscriptionCheck(): Created new PubSubPublisher");
                        publisher.setInterSubsystemParticipant(currentServiceRegistration.getPublisher());
                        getLogger().info(".performSubscriptionCheck(): Added the DistributedPublisher aspect to the PubSubPublisher");
                        RemoteSubscriptionResponse subscriptionResponse = requestSubscription(currentServiceRegistration.getSubscriptionList(), publisher);
                        getLogger().info(".performSubscriptionCheck(): Subscription attempted, subscriptionResponse->{}", subscriptionResponse);
                        if(subscriptionResponse.isSubscriptionSuccessful()){
                            getLogger().info(".performSubscriptionCheck(): Subscription to Publisher was successful, setting subscription registration date");
                            currentServiceRegistration.setRegistrationDate(Date.from(Instant.now()));
                            getLogger().info(".performSubscriptionCheck(): Setting the registration status");
                            currentServiceRegistration.setRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_SUCCESSFUL);
                            getLogger().info(".performSubscriptionCheck(): Subscription to Publisher was successful!");
                            break;
                        }
                    }
                } else {
                    getLogger().info(".performSubscriptionCheck(): At least one publisher found");
                    for (InterSubsystemPubSubPublisherRegistration currentPublisher : publisherServiceProviders) {
                        getLogger().info(".performSubscriptionCheck(): iterating.... through publishers");
                        if (currentPublisher.getPublisherStatus().equals(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_REGISTERED)) {
                            getLogger().info(".performSubscriptionCheck(): this publisher is in the PUBLISHER_REGISTERED state");
                            PubSubParticipant publisher = new PubSubParticipant();
                            publisher.setInterSubsystemParticipant(currentPublisher.getPublisher());
                            List<DataParcelManifest> subscriptionList = currentServiceRegistration.getSubscriptionList();
                            getLogger().info(".performSubscriptionCheck(): Subscribing to publisher");
                            RemoteSubscriptionResponse remoteSubscriptionResponse = requestSubscription(subscriptionList, publisher);
                            getLogger().info(".performSubscriptionCheck(): subscription finished, response->{}", remoteSubscriptionResponse);
                            if (remoteSubscriptionResponse.isSubscriptionSuccessful()) {
                                currentPublisher.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_ACTIVE);
                                currentServiceRegistration.setRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_SUCCESSFUL);
                            } else {
                                getLogger().error(".performSubscriptionCheck(): Cannot subscribe to Publisher->{}", publisher);
                            }
                        }
                    }
                }
            }
        }
        getLogger().info(".performSubscriptionCheck(): iterated through, now seeing if i need to reschedule SubscriptionCheck again");
        for(InterSubsystemPubSubPublisherSubscriptionRegistration currentServiceRegistration: subscriptionRegistrationList) {
            if(currentServiceRegistration.getRegistrationStatus().equals(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_PENDING_NO_PROVIDERS)) {
                synchronized (this.subscriptionCheckScheduledLock) {
                    this.subscriptionCheckScheduled = false;
                    getLogger().info(".performSubscriptionCheck(): iterated through, another SubscriptionCheck is not needed");
                    return(false);
                }
            }
        }
        getLogger().info(".performSubscriptionCheck(): iterated through, another SubscriptionCheck is needed");
        return(true);
    }

    /**
     *
     * @param subscriptionList
     * @param publisher
     * @return
     */
    public RemoteSubscriptionResponse subscribeToRemotePublishers(List<DataParcelManifest> subscriptionList, PubSubParticipant publisher){
        getLogger().debug(".subscribeToRemotePublishers(): Entry, publisher->{}", publisher);
        if(publisher == null){
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_PUBLISHER_NOT_REACHABLE);
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Publisher is null/empty");
            getLogger().debug(".subscribeToRemotePublishers(): Exit, response->{}", response);
            return(response);
        }
        getLogger().trace(".subscribeToRemotePublishers(): Publisher is not null, now checking if the Publisher has a DistributedPublisher element");
        if(publisher.getInterSubsystemParticipant() == null){
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_PUBLISHER_NOT_REACHABLE);
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Publisher (DistributedPublisher) is null/empty");
            getLogger().debug(".subscribeToRemotePublishers(): Exit, response->{}", response);
            return(response);
        }
        getLogger().trace(".subscribeToRemotePublishers(): DistributedPublisher is not null, now checking if the Publisher has a ServiceName");
        if(StringUtils.isEmpty(publisher.getInterSubsystemParticipant().getIdentifier().getServiceName())){
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_PUBLISHER_NOT_REACHABLE);
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Publisher (ServiceName) is null/empty");
            getLogger().debug(".subscribeToRemotePublishers(): Exit, response->{}", response);
            return(response);
        }
        getLogger().trace(".subscribeToRemotePublishers(): DistributedPublisher ServiceName exists, now looking for a publisher to match");
        if(isPublisherAvailable(publisher)){
            getLogger().trace(".subscribeToRemotePublishers(): A publisher is available");
            // There exists a publisher capable of servicing our needs, so we are going to register that
            // publisher and push our subscription requirements to it
            String publisherServiceName = publisher.getInterSubsystemParticipant().getIdentifier().getServiceName();
            publisher.getInterSubsystemParticipant().getIdentifier().setServiceInstanceName(getAvailablePublisherInstanceName(publisherServiceName));
            boolean isPublisherRegistered = getPublisherRegistrationMapIM().isPublisherRegistered(publisher.getInterSubsystemParticipant());
            if(!isPublisherRegistered){
                getLogger().trace(".subscribeToRemotePublishers(): Publisher is not registered, so registering it");
                getPublisherRegistrationMapIM().registerPublisherInstance(publisher.getInterSubsystemParticipant());
            }
            InterSubsystemPubSubPublisherRegistration publisherInstanceRegistration = getPublisherRegistrationMapIM().getPublisherInstanceRegistration(publisher.getInterSubsystemParticipant());
            switch(publisherInstanceRegistration.getPublisherStatus()) {
                case PUBLISHER_REGISTERED: {
                    getLogger().trace(".subscribeToRemotePublishers(): Publisher Registered, adding subscription");
                    RemoteSubscriptionResponse remoteSubscriptionResponse = requestSubscription(subscriptionList, publisher);
                    if (remoteSubscriptionResponse.isSubscriptionSuccessful()) {
                        publisherInstanceRegistration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_ACTIVE);
                        InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = getPublisherRegistrationMapIM().getPublisherServiceSubscription(publisher.getInterSubsystemParticipant());
                        if (subscriptionRegistration == null) {
                            subscriptionRegistration = getPublisherRegistrationMapIM().addSubscriptionToPublisher(subscriptionList, publisher.getInterSubsystemParticipant());
                        }
                        subscriptionRegistration.setRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_SUCCESSFUL);
                        remoteSubscriptionResponse.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_CONNECTION_ESTABLISHED);
                        remoteSubscriptionResponse.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_SUCCESSFUL);
                    } else {
                        getLogger().error(".performSubscriptionCheck(): Cannot subscribe to Publisher->{}", publisher);
                        remoteSubscriptionResponse.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_PUBLISHER_NOT_REACHABLE);
                        publisherInstanceRegistration.setPublisherStatus(InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_REGISTERED);
                        remoteSubscriptionResponse.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_FAILED);

                    }
                    getLogger().debug(".subscribeToRemotePublishers(): Exit, response->{}", remoteSubscriptionResponse);
                    return (remoteSubscriptionResponse);
                }
                case PUBLISHER_ACTIVE: {
                    getLogger().trace(".subscribeToRemotePublishers(): Publisher Active, doing nothing");
                    // Do nothing, as we've already done what we needed to do with this one.
                    RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
                    response.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_CONNECTION_ESTABLISHED);
                    response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_SUCCESSFUL);
                    response.setSubscriptionSuccessful(true);
                    response.setSubscriptionCommentary("Publisher already servicing us!");
                    getLogger().debug(".subscribeToRemotePublishers(): Exit, response->{}", response);
                    return(response);
                }
                case PUBLISHER_FAILED: {
                    getLogger().trace(".subscribeToRemotePublishers(): Publisher Failed, doing nothing");
                    // If we get to this point, there is something wrong with the Publisher, so we should
                    // remove it.
                    getPublisherRegistrationMapIM().unregisterPublisherInstance(publisher.getInterSubsystemParticipant());
                    RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
                    response.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_PUBLISHER_NOT_REACHABLE);
                    response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_FAILED);
                    response.setSubscriptionSuccessful(false);
                    response.setSubscriptionCommentary("Publisher has failed");
                    getLogger().debug(".subscribeToRemotePublishers(): Exit, response->{}", response);
                    return (response);
                }
                case PUBLISHER_NOT_REGISTERED:
                default: {
                    getLogger().trace(".subscribeToRemotePublishers(): No Publisher Registered, registering Subscriptions for later implementation");
                    // We just shouldn't be able to get to this point....
                    RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
                    response.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_PUBLISHER_NOT_REACHABLE);
                    response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_PENDING_NO_PROVIDERS);
                    response.setSubscriptionSuccessful(false);
                    response.setSubscriptionCommentary(".subscribeToRemotePublishers(): Warning Will Robinson!!!! Danger Approaches");
                    getLogger().debug(".subscribeToRemotePublishers(): Exit, response->{}", response);
                    return (response);
                }
            }
        } else {
            // There is no publisher (accessible on the JGroups grids) capable of servicing our needs, so we are going
            // to log our Subscription request and keep it for when one does come online... But first, we'll log an
            // error to say there is no publisher... :)
            getLogger().error(".subscribeToRemotePublishers(): No publisher available for PublisherServer->{}",publisher.getInterSubsystemParticipant().getIdentifier().getServiceName());
            // 1st, we check to see if this subscription was already registered.
            InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = getPublisherRegistrationMapIM().getPublisherServiceSubscription(publisher.getInterSubsystemParticipant());
            // Now check to see the status of the registration and, if it is PUBLISHER_REGISTRATION_NOT_PRESENT,
            // add it to the registration cache (because there wasn't one registered prior)
            if(subscriptionRegistration.getRegistrationStatus().equals(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_NOT_PRESENT)) {
                getLogger().info(".subscribeToRemotePublishers(): No existing subscription, so creating an entry in the registration map");
                subscriptionRegistration = getPublisherRegistrationMapIM().addSubscriptionToPublisher(subscriptionList, publisher.getInterSubsystemParticipant());
                getLogger().info(".subscribeToRemotePublishers(): Subscription added, subscriptionRegistration->{}", subscriptionRegistration);
            }
            // Now, we update the status of the Subscription to reflect the fact that there are not Providers able to
            // fulfill the request.
            getLogger().info(".subscribeToRemotePublishers(): update the status of the Subscription to PUBLISHER_REGISTRATION_PENDING_NO_PROVIDERS");
            subscriptionRegistration.setRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_PENDING_NO_PROVIDERS);
            // Now, if there are no Providers available, but we've got some in our registry, then we should remove
            // them from the registry.
            getLogger().info(".subscribeToRemotePublishers(): now check if there are any Providers in the Registry, because we think there shouldn't be");
            List<InterSubsystemPubSubPublisherRegistration> instanceRegistrations = getPublisherRegistrationMapIM().getPublisherServiceProviderInstanceRegistrations(publisher.getInterSubsystemParticipant().getIdentifier().getServiceName());
            if(instanceRegistrations.isEmpty()){
                getLogger().info(".subscribeToRemotePublishers(): Provider Registry is empty for this publisherServiceName");
                // Do nothing, as there are none registered anyhow
            } else {
                // There are some, but we can't see them on JGroups, so get rid of them...
                getLogger().info(".subscribeToRemotePublishers(): Provider Registry is not empty for this publisherServiceName, so removing them");
                for(InterSubsystemPubSubPublisherRegistration currentRegistration: instanceRegistrations){
                    getPublisherRegistrationMapIM().unregisterPublisherInstance(currentRegistration.getPublisher());
                }
            }
            getLogger().info(".subscribeToRemotePublishers(): Scheduling a SubscriptionCheck");
            scheduleSubscriptionCheck();
            getLogger().info(".subscribeToRemotePublishers(): Generating response");
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setNetworkConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_PUBLISHER_NOT_REACHABLE);
            response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_REGISTRATION_PENDING_NO_PROVIDERS);
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("No publishers");
            getLogger().debug(".subscribeToRemotePublishers(): Exit, response->{}", response);
            return(response);
        }
    }
}
