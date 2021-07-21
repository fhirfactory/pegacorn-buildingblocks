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
package net.fhirfactory.pegacorn.platform.edge.endpoints.roles;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.petasos.datasets.manager.PublisherRegistrationMapIM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.*;
import net.fhirfactory.pegacorn.platform.edge.endpoints.roles.base.PubSubParticipantEndpointServiceInterface;
import net.fhirfactory.pegacorn.platform.edge.endpoints.roles.base.PubSubParticipantRoleBase;
import net.fhirfactory.pegacorn.platform.edge.endpoints.roles.common.IPCEndpointAddress;
import net.fhirfactory.pegacorn.platform.edge.endpoints.roles.common.IPCEndpointAddressTypeEnum;
import net.fhirfactory.pegacorn.platform.edge.endpoints.roles.common.MultiPublisherResponseSet;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionRequest;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionResponse;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.text.spi.DateFormatProvider;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherStatusEnum.*;

public class PubSubParticipantClientRole extends PubSubParticipantRoleBase {
    private static final Logger LOG = LoggerFactory.getLogger(PubSubParticipantClientRole.class);


    public PubSubParticipantClientRole(
            ProcessingPlantInterface processingPlant,
            PubSubParticipantEndpointServiceInterface endpointServiceInterface,
            PubSubParticipant me,
            PublisherRegistrationMapIM publisherMapIM,
            JChannel channel,
            RpcDispatcher rpcDispatcher,
            EdgeForwarderService forwarderService){
        super(processingPlant, endpointServiceInterface, me, publisherMapIM, channel, rpcDispatcher, forwarderService);

    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
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

    @Override
    protected void performPublisherEventUpdateCheck(List<IPCEndpointAddress> publishersRemoved, List<IPCEndpointAddress> publishersAdded) {
        for(IPCEndpointAddress currentAddress: publishersAdded){
            doSubscriptionCheck(currentAddress);
        }
    }

    @Override
    protected void performSubscriberEventUpdateCheck(List<IPCEndpointAddress> subscribersRemoved, List<IPCEndpointAddress> subscribersAdded) {

    }

    /**
     *
     */
    public void scheduleSubscriptionCheck(){
        getLogger().info(".scheduleSubscriptionCheck(): Entry (subscriptionCheckScheduled->{}", isSubscriptionCheckScheduled());
        synchronized(getSubscriptionCheckScheduledLock()) {
            if (isSubscriptionCheckScheduled()) {
                // do nothing, it is already scheduled
            } else {
                TimerTask subscriptionCheckTask = new TimerTask() {
                    public void run() {
                        getLogger().info(".subscriptionCheckTask(): Entry");
                        boolean doAgain = performSubscriptionCheck();
                        getLogger().info(".subscriptionCheckTask(): doAgain ->{}", doAgain);
                        if(!doAgain) {
                            cancel();
                            setSubscriptionCheckScheduled(false);
                        }
                        getLogger().info(".subscriptionCheckTask(): Exit");
                    }
                };
                Timer timer = new Timer("SubscriptionCheck");
                timer.schedule(subscriptionCheckTask, getParticipantMembershipCheckDelay(), getParticipantMembershipCheckPeriod());
                setSubscriptionCheckScheduled(true);
            }
        }
        getLogger().info(".scheduleSubscriptionCheck(): Exit");
    }

    /**
     *
     */
    protected boolean performSubscriptionCheck(){
        getLogger().info(".performSubscriptionCheck(): Entry");
        List<InterSubsystemPubSubPublisherSubscriptionRegistration> subscriptionRegistrationList = getPublisherMapIM().getAllPublisherServiceSubscriptions();
        getLogger().info(".performSubscriptionCheck(): Iterate through Subscription Registrations");
        for(InterSubsystemPubSubPublisherSubscriptionRegistration currentServiceRegistration: subscriptionRegistrationList){
            getLogger().info(".performSubscriptionCheck(): Looking for publisher->{}", currentServiceRegistration.getPublisherServiceName());
            List<InterSubsystemPubSubPublisherRegistration> instanceRegistrations = getPublisherMapIM().getPublisherServiceProviderInstanceRegistrations(currentServiceRegistration.getPublisherServiceName());
            getLogger().info(".performSubscriptionCheck(): Iterate through Publisher Registrations");
            for(InterSubsystemPubSubPublisherRegistration currentInstanceRegistration: instanceRegistrations){
                getLogger().info(".performSubscriptionCheck(): Iterating, looking at publisher->{}", currentInstanceRegistration.getPublisher().getIdentifier().getServiceInstanceName());
                if(currentInstanceRegistration.getPublisherStatus().equals(PUBLISHER_REGISTERED) || currentInstanceRegistration.getPublisherStatus().equals(PUBLISHER_UNREACHABLE)){
                    getLogger().info(".performSubscriptionCheck(): Checking....");
                    PubSubParticipant newPublisher = new PubSubParticipant();
                    newPublisher.setInterSubsystemParticipant(currentInstanceRegistration.getPublisher());
                    InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = subscribeToRemotePublishers(currentServiceRegistration.getSubscriptionList(), newPublisher);
                }
            }
            getLogger().info(".performSubscriptionCheck(): Looking now into JGroups itself for Publisher Service->{}", currentServiceRegistration.getPublisherServiceName());
            List<IPCEndpointAddress> serviceInstanceSet = getParticipantServiceInstanceSet(currentServiceRegistration.getPublisherServiceName());
            if(serviceInstanceSet != null){
                getLogger().info(".performSubscriptionCheck(): there are publishers!, number->{}", serviceInstanceSet.size());
                for(IPCEndpointAddress serviceProvider: serviceInstanceSet){
                    getLogger().info(".performSubscriptionCheck(): checking participant->{}", serviceProvider);
                    String publisherInstanceName = serviceProvider.getAddressName();
                    String publisherServiceName = getServiceNameFromParticipantInstanceName(publisherInstanceName);
                    getLogger().info(".performSubscriptionCheck(): Checking Participant->(participantServiceName->{}, participantInstanceName->{} ", publisherServiceName, publisherInstanceName);
                    InterSubsystemPubSubPublisherRegistration publisherRegistration = getPublisherMapIM().getPublisherInstanceRegistration(publisherInstanceName);
                    getLogger().info(".performSubscriptionCheck(): Existing Publisher Registration->{}", publisherRegistration);
                    if(publisherRegistration == null) {
                        getLogger().info(".performSubscriptionCheck(): checking participant->{}", serviceProvider);
                        String myName = getMe().getInterSubsystemParticipant().getIdentifier().getServiceInstanceName();
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
        getLogger().trace(".performSubscriptionCheck(): iterated through, now seeing if i need to reschedule SubscriptionCheck again");
        for(InterSubsystemPubSubPublisherSubscriptionRegistration currentServiceRegistration: subscriptionRegistrationList) {
            if(currentServiceRegistration.getPublisherServiceRegistrationStatus().equals(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_PENDING_NO_PROVIDERS)) {
                getLogger().info(".performSubscriptionCheck(): iterated through, another SubscriptionCheck is needed");
                return(true);
            }
        }
        getLogger().info(".performSubscriptionCheck(): iterated through, another SubscriptionCheck is NOT needed");
        return(false);
    }

    protected void doSubscriptionCheck(IPCEndpointAddress participantAddress){
        getLogger().info(".doSubscriptionCheck(): Entry, participantAddress->{}", participantAddress);
        String participantInstanceName = participantAddress.getAddressName();
        String participantServiceName = extractPublisherServiceName(participantInstanceName);
        if(StringUtils.isEmpty(participantInstanceName) || StringUtils.isEmpty(participantServiceName)) {
            getLogger().info(".doSubscriptionCheck(): Exit, publisher name is bad...");
            return;
        }
        InterSubsystemPubSubPublisherSubscriptionRegistration serviceSubscription = null;
        InterSubsystemPubSubPublisherRegistration instanceRegistration = getPublisherMapIM().getPublisherInstanceRegistration(participantInstanceName);
        if(instanceRegistration == null) {
            getLogger().info(".doSubscriptionCheck(): There is no existing PublisherRegistration for this PubSubParticipant, creating");
            serviceSubscription = getPublisherMapIM().getPublisherServiceSubscription(participantServiceName);
            if (serviceSubscription == null) {
                getLogger().info(".doSubscriptionCheck(): Exit, We have no interest in this publisher");
                return;
            }
            List<DataParcelManifest> subscriptionList = serviceSubscription.getSubscriptionList();
            if (subscriptionList == null) {
                getLogger().info(".doSubscriptionCheck(): Exit, We have no topics to register with this publisher");
                return;
            }
            getLogger().trace(".doSubscriptionCheck(): Create new PubSubPublisher");
            String myName = getMe().getInterSubsystemParticipant().getIdentifier().getServiceInstanceName();
            InterSubsystemPubSubParticipant newParticipant = requestParticipantDetail(participantAddress.getJGroupsAddress(), myName);
            instanceRegistration = getPublisherMapIM().registerPublisherInstance(newParticipant);
        }
        if(instanceRegistration.getPublisherStatus().equals(PUBLISHER_REGISTERED) || instanceRegistration.getPublisherStatus().equals(PUBLISHER_UNREACHABLE)) {
            PubSubParticipant newParticipant = new PubSubParticipant();
            newParticipant.setInterSubsystemParticipant(instanceRegistration.getPublisher());
            RemoteSubscriptionResponse subscriptionResponse = requestSubscriptionToPublisherInstance(serviceSubscription.getSubscriptionList(), newParticipant);
            if(subscriptionResponse.isSubscriptionSuccessful()){
                instanceRegistration.setPublisherStatus(PUBLISHER_ACTIVE);
                instanceRegistration.setLastActivityDate(Date.from(Instant.now()));
                serviceSubscription.setPublisherServiceRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_ACTIVE);
            }
        }
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
    private RemoteSubscriptionResponse requestSubscriptionToPublisherInstance(List<DataParcelManifest> subscriptionList, PubSubParticipant publisher ) {
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
        String publisherInstanceName = publisher.getInterSubsystemParticipant().getIdentifier().getServiceInstanceName();
        if (isParticipantInstanceAvailable(publisherInstanceName)) {
            getLogger().info(".requestSubscription(): Publisher is available, so register");
            IPCEndpointAddress publisherAddress = getAddressForParticipantInstance(publisherInstanceName);
            if (publisherAddress != null) {
                getLogger().info(".requestSubscription(): Subscribing to PublisherInstance->{}", publisherInstanceName);
                response = rpcRequestSubscription(publisherAddress, subscriptionRequest);
            }
        }
        if(response == null){
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
     * @param subscriptionList
     * @param publisher
     * @return
     */
    public InterSubsystemPubSubPublisherSubscriptionRegistration subscribeToRemotePublishers(List<DataParcelManifest> subscriptionList, PubSubParticipant publisher) {
        getLogger().info(".subscribeToRemotePublishers(): Entry, publisher->{}", publisher);
        if (!hasParticipantServiceName(publisher)) {
            getLogger().debug(".subscribeToRemotePublishers(): Cannot resolve service name, exiting");
            return (null);
        }
        getLogger().trace(".subscribeToRemotePublishers(): DistributedPublisher ServiceName exists, now looking for a publisher to match");
        if (isParticipantServiceAvailable(publisher)) {
            getLogger().trace(".subscribeToRemotePublishers(): A publisher is available");
            // There exists a publisher capable of servicing our needs, so we are going to register that
            // publisher and push our subscription requirements to it
            String publisherServiceName = publisher.getInterSubsystemParticipant().getIdentifier().getServiceName();
            boolean isPublisherRegistered = getPublisherMapIM().isPublisherRegistered(publisher.getInterSubsystemParticipant());
            if (!isPublisherRegistered) {
                getLogger().trace(".subscribeToRemotePublishers(): Publisher is not registered, so registering it");
                InterSubsystemPubSubPublisherRegistration publisherRegistration = getPublisherMapIM().registerPublisherInstance(publisher.getInterSubsystemParticipant());
                publisherRegistration.setPublisherStatus(PUBLISHER_REGISTERED);
            }
            List<MultiPublisherResponseSet> responseSetList = new ArrayList<>();
            for (InterSubsystemPubSubPublisherRegistration currentPublisherRegistration : getPublisherMapIM().getPublisherServiceProviderInstanceRegistrations(publisherServiceName)) {
                if (currentPublisherRegistration.getPublisherStatus().equals(PUBLISHER_REGISTERED) || currentPublisherRegistration.getPublisherStatus().equals(PUBLISHER_UNREACHABLE)) {
                    RemoteSubscriptionResponse remoteSubscriptionResponse = requestSubscriptionToPublisherInstance(subscriptionList, publisher);
                    MultiPublisherResponseSet currentResponseSet = new MultiPublisherResponseSet();
                    currentResponseSet.setPublisher(publisher);
                    currentResponseSet.setSubscriptionRequestResponse(remoteSubscriptionResponse);
                    currentResponseSet.getSubscriptionList().addAll(subscriptionList);
                    currentResponseSet.setPublisherRegistration(currentPublisherRegistration);
                    responseSetList.add(currentResponseSet);
                    if (remoteSubscriptionResponse.isSubscriptionSuccessful()) {
                        currentPublisherRegistration.setPublisherStatus(PUBLISHER_ACTIVE);
                    } else {
                        currentPublisherRegistration.setPublisherStatus(PUBLISHER_UNREACHABLE);
                        scheduleSubscriptionCheck();
                    }
                }
            }
            boolean aSubscriptionWasSuccessful = false;
            boolean aPublisherWasAvailable = false;
            for(MultiPublisherResponseSet currentResponseSetInstance: responseSetList){
                if(aSubscriptionWasSuccessful || currentResponseSetInstance.getSubscriptionRequestResponse().isSubscriptionSuccessful()){
                    aSubscriptionWasSuccessful = true;
                }
                if(aPublisherWasAvailable || currentResponseSetInstance.getPublisherRegistration().getPublisherStatus().equals(PUBLISHER_ACTIVE)){
                    aPublisherWasAvailable = true;
                }
            }
            getLogger().info(".subscribeToRemotePublishers(): aSubscriptionWasSuccessful->{}, aPublisherWasAvailable->{} ", aSubscriptionWasSuccessful, aPublisherWasAvailable);
            InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = updatePublisherSubscriptionRegistration(subscriptionList, publisher, (aSubscriptionWasSuccessful && aPublisherWasAvailable));
            getLogger().info(".subscribeToRemotePublishers(): Exit, subscriptionRegistration->{} ", subscriptionRegistration);
            return(subscriptionRegistration);
        } else {
            InterSubsystemPubSubPublisherSubscriptionRegistration subscriptionRegistration = updatePublisherSubscriptionRegistration(subscriptionList, publisher, false);
            getLogger().info(".subscribeToRemotePublishers(): Exit, subscriptionRegistration->{} ", subscriptionRegistration);
            return(subscriptionRegistration);
        }
    }
    /**
     *
     * @param publisherAddress
     * @param subscriptionRequest
     * @return
     */

    public RemoteSubscriptionResponse rpcRequestSubscription(IPCEndpointAddress publisherAddress, RemoteSubscriptionRequest subscriptionRequest){
        getLogger().info(".rpcRequestSubscription(): Entry, publisher->{}, subscriptionRequest.getSubscriber{}", publisherAddress, subscriptionRequest.getSubscriber());
        if(publisherAddress == null || subscriptionRequest == null){
            getLogger().error(".rpcRequestSubscription: publisherAddress or subscriptionRequest are null");
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (publisherAddress or subscriptionRequest are null)");
            return(response);
        }
        if(!publisherAddress.getAddressType().equals(IPCEndpointAddressTypeEnum.ADDRESS_TYPE_JGROUPS)){
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
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, RPC_UNICAST_TIMEOUT);
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



}
