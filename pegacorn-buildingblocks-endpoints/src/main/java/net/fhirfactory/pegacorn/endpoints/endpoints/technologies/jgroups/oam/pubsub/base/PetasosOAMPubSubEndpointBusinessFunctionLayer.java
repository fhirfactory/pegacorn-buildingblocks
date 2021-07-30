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
package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.oam.pubsub.base;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointStatusEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.base.PetasosPubSubEndpointChangeInterface;
import net.fhirfactory.pegacorn.endpoints.endpoints.roles.common.MultiPublisherResponseSet;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DistributedPubSubSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.*;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionRequest;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionResponse;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_UTILISED;
import static net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_UTILISED;

public abstract class PetasosOAMPubSubEndpointBusinessFunctionLayer extends PetasosOAMPubSubEndpointCoreFunctionLayer implements PetasosAdapterDeltasInterface, PetasosPubSubEndpointChangeInterface {

    @Inject
    DistributedPubSubSubscriptionMapIM distributedPubSubSubscriptionMapIM;

    //
    // Constructor
    //

    public PetasosOAMPubSubEndpointBusinessFunctionLayer(){
        super();

    }

    @Override
    protected void executePostConstructActivities(){
        getCoreSubsystemPetasosEndpointsWatchdog().registerPubSubCallbackChange(this);
        executePostConstructSupervisoryActivities();
    }

    //
    // Abstract Methods
    //

    abstract protected void executePostConstructSupervisoryActivities();

    //
    // Getters (and Setters)
    //

    protected DistributedPubSubSubscriptionMapIM getDistributedPubSubSubscriptionMapIM(){
        return(distributedPubSubSubscriptionMapIM);
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
    public InterSubsystemPubSubPublisherSubscriptionRegistration subscribeToAllRemotePublishersForService(List<DataParcelManifest> subscriptionList, String publisherServiceName) {
        getLogger().debug(".subscribeToRemotePublishers(): Entry, publisher->{}", publisherServiceName);
        if (StringUtils.isEmpty(publisherServiceName)) {
            getLogger().debug(".subscribeToRemotePublishers(): Cannot resolve service name, exiting");
            return (null);
        }
        getLogger().trace(".subscribeToRemotePublishers(): DistributedPublisher ServiceName exists, now looking for a publisher to match");
        List<InterSubsystemPubSubPublisherRegistration> publisherRegistrations = getDistributedPubSubSubscriptionMapIM().getPublisherServiceProviderInstanceRegistrations(publisherServiceName);
        List<InterSubsystemPubSubPublisherRegistration> scopedPublisherRegistrations = new ArrayList<>();
        for(InterSubsystemPubSubPublisherRegistration currentRegistration: publisherRegistrations){
            if(currentRegistration.getPublisher().getEndpointChannelScope().equals(getPetasosEndpoint().getEndpointChannelScope())){
                scopedPublisherRegistrations.add(currentRegistration);
            }
        }
        if (scopedPublisherRegistrations.isEmpty()) {
            getLogger().trace(".subscribeToRemotePublishers(): There are no potential publishers at the moment, so register request");
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
                getLogger().info(".subscribeToRemotePublishers(): subscribing to: {}", publisherInstanceName);
                RemoteSubscriptionResponse remoteSubscriptionResponse = subscribeToPublisherInstance(subscriptionList,publisherInstanceName);
                getLogger().info(".subscribeToRemotePublishers(): subscription request response->{}", remoteSubscriptionResponse);
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
     *
     * @param subscriptionList
     * @param petasosEndpointKey
     * @return
     */
    protected RemoteSubscriptionResponse subscribeToPublisherInstance(List<DataParcelManifest> subscriptionList, String petasosEndpointKey ) {
        getLogger().debug(".subscribeToPublisherInstance(): Entry, subscriptionList->{}, petasosEndpointKey->{} ", subscriptionList, petasosEndpointKey);
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
        if (isEndpointInstanceReachable(petasosEndpointKey)) {
            getLogger().trace(".subscribeToPublisherInstance(): Publisher is available, so register");
            String petasosEndpointName = removeFunctionNameSuffixFromEndpointName(petasosEndpointKey);
            String petasosEndpointPubSubKey = addFunctionNameSuffixToEndpointName(petasosEndpointName, PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT);
            PetasosAdapterAddress publisherAddress = getTargetMemberAdapterAddress(petasosEndpointPubSubKey);
            if (publisherAddress != null) {
                getLogger().info(".subscribeToPublisherInstance(): Subscribing to PublisherInstance->{}", petasosEndpointName);
                response = rpcRequestSubscription(publisherAddress, subscriptionRequest);
                getLogger().info(".subscribeToPublisherInstance(): Subscription Response->{}", petasosEndpointName);
            }
        }
        if(response == null){
            getLogger().error(".subscribeToPublisherInstance(): Publisher ({}) is not available!!!", petasosEndpointKey);
            response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_PENDING_NO_PROVIDERS);
            response.setSubscriptionCommentary("Publisher ("+petasosEndpointKey+") is not available!");
        }
        getLogger().debug(".subscribeToPublisherInstance(): Exit, response->{}", response);
        return (response);
    }



    //
    // Publisher Synchronisation Activities
    //

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
            getLogger().info(".updatePublisherRegistration(): Adding publisher instance to cache");
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
}

