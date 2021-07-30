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

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointStatusEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherRegistration;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherSubscriptionRegistration;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionResponse;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_NOT_UTILISED;
import static net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherStatusEnum.PUBLISHER_REGISTERED;

public abstract class PetasosOAMPubSubEndpointSupervisoryFunctionLayer extends PetasosOAMPubSubEndpointBusinessFunctionLayer{

    private boolean subscriptionCheckScheduled;
    private Object subscriptionCheckLock;

    private static Long SUBSCRIPTION_CHECK_INITIAL_DELAY=10000L;
    private static Long SUBSCRIPTION_CHECK_PERIOD = 5000L;

    private int subscriptionCheckCount;
    private static int CHANGE_DETECTION_SUBSCRIPTION_CHECK_COUNT = 10;

    //
    // Constructor
    //

    public PetasosOAMPubSubEndpointSupervisoryFunctionLayer(){
        super();
        subscriptionCheckScheduled = false;
        subscriptionCheckLock = new Object();
        subscriptionCheckCount = 0;
    }

    //
    // Post Construct Activities
    //

    @Override
    protected void executePostConstructSupervisoryActivities(){
        scheduleASubscriptionCheck();
    }

    //
    // Endpoint Discovery
    //

    @Override
    public void registerInterfaceEventCallbacks(PetasosAdapterDeltasInterface interfaceEventCallbacks) {
        this.getMembershipEventListeners().add(interfaceEventCallbacks);
    }

    @Override
    public void interfaceAdded(String addedInterface){
        scheduleASubscriptionCheck();
    }

    @Override
    public void interfaceRemoved(String removedInterface){
        scheduleASubscriptionCheck();
    }

    @Override
    public void interfaceSuspect(String suspectInterface){

    }


    //
    // Schedule Subscription Checks
    //

    /**
     *
     */
    protected boolean performFullSubscriptionCheck(){
        getLogger().debug(".performSubscriptionCheck(): Entry");
        if(getParticipant() == null){
            getLogger().warn(".performFullSubscriptionCheck(): Still initialising subsystem, pausing subscription process for {} seconds", SUBSCRIPTION_CHECK_PERIOD/1000);
            return(true);
        }
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
                boolean publisherIsOperational = currentInstanceRegistration.getPublisher().getEndpointStatus().equals(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL);
                boolean isWithinScope = currentInstanceRegistration.getPublisher().getEndpointChannelScope().equals(getPetasosEndpoint().getEndpointChannelScope());
                boolean isSameSite = currentInstanceRegistration.getPublisher().getEndpointID().getEndpointSite().equals(getPetasosEndpoint().getEndpointID().getEndpointSite());
                boolean isSameZone = currentInstanceRegistration.getPublisher().getEndpointID().getEndpointZone().equals(getPetasosEndpoint().getEndpointID().getEndpointZone());
                boolean weAreProperChannel = false;
                switch(getPetasosEndpoint().getEndpointChannelScope()){
                    case ENDPOINT_CHANNEL_SCOPE_INTRAZONE:{
                        if(isSameSite && isSameZone){
                            weAreProperChannel = true;
                        }
                        break;
                    }
                    case ENDPOINT_CHANNEL_SCOPE_INTERZONE:{
                        if(isSameSite && !isSameZone){
                            weAreProperChannel = true;
                        }
                        break;
                    }
                    case ENDPOINT_CHANNEL_SCOPE_INTERSITE:{
                        if(!isSameSite){
                            weAreProperChannel = true;
                        }
                    }
                }
                if (weAreProperChannel && isWithinScope && weNeedToSubscribeToPublisher && publisherIsOperational) {
                    String publisherInstanceName = currentInstanceRegistration.getPublisher().getEndpointID().getEndpointChannelName();
                    getLogger().info(".performSubscriptionCheck(): Subscribing.... to instance ->{}", publisherInstanceName);
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
                if(currentInstanceRegistration.getPublisherStatus().equals(PUBLISHER_NOT_UTILISED) || currentInstanceRegistration.getPublisherStatus().equals(PUBLISHER_REGISTERED)){
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
                Timer timer = new Timer("watchdog:subscription-check-" + specifyIPCType().getEndpointType());
                timer.schedule(subscriptionCheckTask, SUBSCRIPTION_CHECK_INITIAL_DELAY, SUBSCRIPTION_CHECK_PERIOD);
                subscriptionCheckScheduled = true;
            }
        }
        getLogger().debug(".scheduleASubscriptionCheck(): Exit");
    }

    @Override
    public void notifyNewPublisher(InterSubsystemPubSubParticipant newPublisher) {
        getLogger().debug(".notifyNewPublisher(): Entry, newPublisher->{}", newPublisher);
        boolean inScope = newPublisher.getEndpointChannelScope().equals(getPetasosEndpoint().getEndpointChannelScope());
        if(inScope) {
            InterSubsystemPubSubPublisherRegistration publisherInstanceRegistration = getDistributedPubSubSubscriptionMapIM().getPublisherInstanceRegistration(newPublisher);
            if(publisherInstanceRegistration == null){
                publisherInstanceRegistration = getDistributedPubSubSubscriptionMapIM().registerPublisherInstance(newPublisher);
            }
            if(publisherInstanceRegistration == null){
                getLogger().error(".notifyNewPublisher(): Cannot register publisher->{}",newPublisher);
                return;
            }
            String endpointServiceName = newPublisher.getEndpointServiceName();
            InterSubsystemPubSubPublisherSubscriptionRegistration publisherServiceSubscription = getDistributedPubSubSubscriptionMapIM().getPublisherServiceSubscription(endpointServiceName);
            if(publisherServiceSubscription == null){
                getLogger().debug(".notifyNewPublisher(): Nothing is interested in this publisher!");
                return;
            }
            String publisherInstanceName = newPublisher.getEndpointID().getEndpointChannelName();
            getLogger().debug(".notifyNewPublisher(): Subscribing.... to instance ->{}", publisherInstanceName);
            RemoteSubscriptionResponse remoteSubscriptionResponse = subscribeToPublisherInstance(publisherServiceSubscription.getSubscriptionList(), publisherInstanceName);
            if(remoteSubscriptionResponse.isSubscriptionSuccessful()){
                getLogger().trace(".notifyNewPublisher(): Subscription to {} Successful", publisherInstanceName);
                publisherServiceSubscription.setPublisherServiceRegistrationStatus(InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum.PUBLISHER_SERVICE_REGISTRATION_ACTIVE);
            } else {
                getLogger().trace(".notifyNewPublisher(): Subscription to {} Failed, reason->{}", publisherInstanceName, remoteSubscriptionResponse.getSubscriptionCommentary());
            }
            getLogger().trace(".notifyNewPublisher(): Updating publisher");
            updatePublisherRegistration(remoteSubscriptionResponse);
            getLogger().debug(".notifyNewPublisher(): Updating publisher.... finished, exiting");
        }
    }
}
