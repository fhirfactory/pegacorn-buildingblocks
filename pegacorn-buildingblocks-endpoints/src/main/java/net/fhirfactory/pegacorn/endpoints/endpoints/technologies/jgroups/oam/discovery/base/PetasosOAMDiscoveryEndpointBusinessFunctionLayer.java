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
package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.oam.discovery.base;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpoint;
import net.fhirfactory.pegacorn.endpoints.endpoints.map.datatypes.PetasosEndpointCheckScheduleElement;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DistributedPubSubSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherRegistration;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

public abstract class PetasosOAMDiscoveryEndpointBusinessFunctionLayer extends PetasosOAMDiscoveryEndpointCoreFunctionLayer implements PetasosAdapterDeltasInterface {


    @Inject
    DistributedPubSubSubscriptionMapIM distributedPubSubSubscriptionMapIM;

    //
    // Constructor
    //

    public PetasosOAMDiscoveryEndpointBusinessFunctionLayer(){
        super();

    }

    //
    // PostConstruct Activities
    //

    @Override
    protected void executePostConstructActivities() {

        executePostConstructSupervisoryActivities();
    }

    //
    // Abstract Methods
    //

    abstract protected void executePostConstructSupervisoryActivities();

    //
    // Getters (and Setters)
    //

    protected DistributedPubSubSubscriptionMapIM getDistributedPubSubSubscriptionMapIM() {
        return (distributedPubSubSubscriptionMapIM);
    }

    //
    // Endpoint (Discovery) Functions
    //

    protected void processEndpoint(String endpointName, PetasosEndpoint returnedEndpointFromTarget){
        getLogger().info(".processEndpoint(): Entry");
        PetasosEndpoint localCachedEndpoint = getEndpointMap().getEndpoint(endpointName);
        getLogger().trace(".checkEndpointAddition(): CachedEndpoint->{}", localCachedEndpoint);
        if (localCachedEndpoint == null) {
            PetasosEndpoint addedPetasosEndpoint = getEndpointMap().addEndpoint(returnedEndpointFromTarget);
            getLogger().trace(".checkEndpointAddition(): addedPetasosEndpoint->{}", addedPetasosEndpoint);
            addPublisherToRegistry(addedPetasosEndpoint);
            if (!StringUtils.isEmpty(returnedEndpointFromTarget.getEndpointServiceName())) {
                getEndpointMap().updateServiceNameMembership(returnedEndpointFromTarget.getEndpointServiceName(),endpointName);
            }
        } else {
            synchronized (getEndpointMap().getEndpointLock(endpointName)) {
                localCachedEndpoint.encrichPetasosEndpoint(returnedEndpointFromTarget);
            }
            addPublisherToRegistry(localCachedEndpoint);
        }
    }

    protected boolean checkEndpointAddition(PetasosEndpointCheckScheduleElement currentScheduleElement){
        getLogger().debug(".checkEndpointAddition(): Entry, currentScheduleElement->{}", currentScheduleElement);
        if(isRightChannel(currentScheduleElement.getPetasosEndpointID().getEndpointName())) {
            boolean sameGroup = currentScheduleElement.getPetasosEndpointID().getEndpointGroup().equals(specifyJGroupsClusterName());
            boolean sameEndpointType = currentScheduleElement.getPetasosEndpointID().getEndpointChannelName().contains(getPetasosEndpointFunctionType().getFunctionSuffix());
            if ( sameGroup && sameEndpointType){
                if (isTargetAddressActive(currentScheduleElement.getPetasosEndpointID().getEndpointChannelName())) {
                    PetasosEndpoint returnedEndpointFromTarget = probeEndpoint(currentScheduleElement.getPetasosEndpointID(), getPetasosEndpoint());
                    getLogger().trace(".checkEndpointAddition(): returnedEndpointFromTarget->{}", returnedEndpointFromTarget);
                    if (returnedEndpointFromTarget != null) {
                        boolean otherEndpointAtSameSite = returnedEndpointFromTarget.getEndpointID().getEndpointSite().contentEquals(getEndpointID().getEndpointSite());
                        boolean otherEndpointInSameZone = returnedEndpointFromTarget.getEndpointID().getEndpointZone().equals(getEndpointID().getEndpointZone());
                        switch(getPetasosEndpoint().getEndpointChannelScope()){
                            case ENDPOINT_CHANNEL_SCOPE_INTRAZONE: {
                                if (otherEndpointAtSameSite && otherEndpointInSameZone) {
                                    processEndpoint(currentScheduleElement.getPetasosEndpointID().getEndpointName(), returnedEndpointFromTarget);
                                }
                                break;
                            }
                            case ENDPOINT_CHANNEL_SCOPE_INTERZONE:{
                                if(otherEndpointAtSameSite && !otherEndpointInSameZone){
                                    processEndpoint(currentScheduleElement.getPetasosEndpointID().getEndpointName(), returnedEndpointFromTarget);
                                }
                                break;
                            }
                            case ENDPOINT_CHANNEL_SCOPE_INTERSITE:{
                                if(!otherEndpointAtSameSite){
                                    processEndpoint(currentScheduleElement.getPetasosEndpointID().getEndpointName(), returnedEndpointFromTarget);
                                    break;
                                }
                            }
                        }
                    } else {
                        int retryCountSoFar = currentScheduleElement.getRetryCount();
                        if(retryCountSoFar > MAX_PROBE_RETRIES){
                            getLogger().debug(".checkEndpointAddition(): we've tried to probe endpoint MAX_PROBE_RETRIES ({}) times and failed, so delete it", MAX_PROBE_RETRIES);
                            getEndpointMap().scheduleEndpointCheck(currentScheduleElement.getPetasosEndpointID(), true, false);
                        } else {
                            getLogger().debug(".checkEndpointAddition(): probe has failed ({}) times, but we will try again", retryCountSoFar);
                            retryCountSoFar += 1;
                            getEndpointMap().scheduleEndpointCheck(currentScheduleElement.getPetasosEndpointID(), false, true, retryCountSoFar);
                        }
                    }
                }
                getLogger().debug(".performEndpointValidationCheck(): Exit, was checked->true");
                return(true);
            }
        }
        getLogger().debug(".performEndpointValidationCheck(): Exit, was checked->false");
        return (false);
    }

    protected void checkEndpointRemoval(PetasosEndpointCheckScheduleElement currentScheduleElement){
        getLogger().debug(".checkEndpointRemoval(): Entry, currentScheduleElement->{}", currentScheduleElement);
        boolean sameGroup = currentScheduleElement.getPetasosEndpointID().getEndpointGroup().equals(specifyJGroupsClusterName());
        boolean sameEndpointType = currentScheduleElement.getPetasosEndpointID().getEndpointChannelName().contains(getPetasosEndpointFunctionType().getFunctionSuffix());
        if( sameGroup && sameEndpointType ){
            boolean wasRemoved = removePublisher(currentScheduleElement.getPetasosEndpointID().getEndpointName());
            getEndpointMap().deleteEndpoint(currentScheduleElement.getPetasosEndpointID().getEndpointName());
        }
        getLogger().debug(".checkEndpointRemoval(): Exit");
    }

    //
    // Publisher Management
    //

    protected void addPublisherToRegistry(PetasosEndpoint addedPetasosEndpoint) {
        getLogger().debug(".addPublisherToRegistry(): Entry, addedPetasosEndpoint->{}", addedPetasosEndpoint);
        InterSubsystemPubSubPublisherRegistration publisherRegistration = getDistributedPubSubSubscriptionMapIM().getPublisherInstanceRegistration(addedPetasosEndpoint.getEndpointID().getEndpointName());
        if(publisherRegistration == null) {
            getLogger().info(".addPublisherToRegistry(): Publisher doesn't exist in cache, so adding");
            InterSubsystemPubSubParticipant publisher = new InterSubsystemPubSubParticipant(addedPetasosEndpoint);
            publisherRegistration = getDistributedPubSubSubscriptionMapIM().registerPublisherInstance(publisher);
            getCoreSubsystemPetasosEndpointsWatchdog().notifyNewPublisher(publisher);

        }
        getLogger().info(".addPublisherToRegistry(): Exit, publisherRegistration->{}",publisherRegistration);
    }

    protected boolean removePublisher(String publisherInstanceName) {
        getLogger().debug(".performPublisherCheck(): Entry, publisherInstanceName->{}", publisherInstanceName);
        if (StringUtils.isEmpty(publisherInstanceName)) {
            getLogger().debug(".performPublisherCheck(): Exit, publisherInstanceName is empty");
            return (false);
        }
        getLogger().trace(".performPublisherCheck(): Getting a list of all Publishers");
        InterSubsystemPubSubPublisherRegistration publisherInstanceRegistration = getDistributedPubSubSubscriptionMapIM().getPublisherInstanceRegistration(publisherInstanceName);
        if (publisherInstanceRegistration != null) {
            getDistributedPubSubSubscriptionMapIM().unregisterPublisherInstance(publisherInstanceRegistration.getPublisher());
            return (true);
        }
        return (false);
    }
}
