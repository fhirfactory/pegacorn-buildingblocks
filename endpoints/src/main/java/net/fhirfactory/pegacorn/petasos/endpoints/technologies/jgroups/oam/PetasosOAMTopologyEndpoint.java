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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam;

import net.fhirfactory.pegacorn.core.interfaces.topology.PetasosTopologyBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.PetasosTopologyHandlerInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.PetasosMonitoredTopologyGraph;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.InterSubsystemPubSubPublisherRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.*;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.core.subscriptions.manager.DistributedPubSubSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.endpoints.map.datatypes.PetasosEndpointCheckScheduleElement;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class PetasosOAMTopologyEndpoint extends JGroupsPetasosEndpointBase implements PetasosAdapterDeltasInterface, PetasosTopologyBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosOAMTopologyEndpoint.class);

    private boolean endpointCheckScheduled;

    private int MAX_PROBE_RETRIES = 5;

    @Inject
    private DistributedPubSubSubscriptionMapIM distributedPubSubSubscriptionMapIM;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private PetasosTopologyHandlerInterface topologyHandler;



    //
    // Constructor
    //

    public PetasosOAMTopologyEndpoint(){
        super();
        endpointCheckScheduled = false;
    }

    //
    // PostConstruct Activities
    //

    @Override
    protected void executePostConstructActivities() {
        //
        // 8th, Do an initial endpoint scan
        //
        scheduleEndpointScan();

        //
        // 9th, Now kickstart the ongoing Endpoint Validation Process
        //
        scheduleEndpointValidation();

        //
        // 10th, Register Callbacks
        //
        registerInterfaceEventCallbacks(this);
    }

    //
    // Getters (and Setters)
    //

    protected DistributedPubSubSubscriptionMapIM getDistributedPubSubSubscriptionMapIM() {
        return (distributedPubSubSubscriptionMapIM);
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    //
    // Endpoint Definition
    //

    @Override
    protected PetasosEndpointIdentifier specifyEndpointID() {
        PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
        // Get Core Values
        String endpointServiceName = specifyEndpointServiceName();
        String endpointFunctionName = specifyPetasosEndpointFunctionType().getDisplayName();
        String endpointUUID = getEndpointNameUtilities().getCurrentUUID();
        String endpointSite = getProcessingPlantInterface().getDeploymentSite();
        String endpointZone = getProcessingPlantInterface().getNetworkZone().getDisplayName();
        // Build EndpointName
        String endpointName = getEndpointNameUtilities().buildEndpointName(endpointServiceName, endpointUUID);
        // Build EndpointChannelName
        String endpointChannelName = getEndpointNameUtilities().buildChannelName(endpointSite, endpointZone, endpointServiceName, endpointFunctionName, endpointUUID);
        // Build EndpointID
        endpointID.setEndpointChannelName(endpointChannelName);
        endpointID.setEndpointName(endpointName);
        endpointID.setEndpointZone(getProcessingPlantInterface().getNetworkZone());
        endpointID.setEndpointSite(getProcessingPlantInterface().getDeploymentSite());
        endpointID.setEndpointGroup(getJgroupsParticipantInformationService().getPetasosTopologyServicesGroupName());
        endpointID.setEndpointComponentID(getTopologyNode().getComponentID());
        endpointID.setProcessingPlantComponentID(getProcessingPlantInterface().getProcessingPlantNode().getComponentID());
        String endpointAddress = "JGroups:" + endpointChannelName + ":" + getJgroupsParticipantInformationService().getPetasosTopologyServicesGroupName();
        endpointID.setEndpointDetailedAddressName(endpointAddress);
        return(endpointID);
    }

    @Override
    protected String specifyEndpointServiceName() {
        return (getProcessingPlantInterface().getIPCServiceName());
    }

    @Override
    protected String specifyIPCInterfaceName() {
        return (getInterfaceNames().getPetasosTopologyServicesEndpointName());
    }

    @Override
    protected PetasosEndpointTopologyTypeEnum specifyIPCType() {
        return (PetasosEndpointTopologyTypeEnum.EDGE_JGROUPS_MESSAGING_SERVICE);
    }

    @Override
    protected String specifyJGroupsStackFileName() {
        return (getProcessingPlantInterface().getProcessingPlantNode().getPetasosTopologyStackConfigFile());
    }

    @Override
    protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType() {
        return (PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT);
    }

    @Override
    protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType() {
        return (EndpointPayloadTypeEnum.ENDPOINT_PAYLOAD_INTERNAL_TOPOLOGY);
    }

    @Override
    protected void resolveTopologyEndpoint() {
        setTopologyNode(getJgroupsParticipantInformationService().getMyPetasosTopologyEndpoint());
    }

    @Override
    public PetasosEndpointStatusEnum checkInterfaceStatus(PetasosEndpointIdentifier endpointID) {
        if(endpointID == null){
            return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_UNREACHABLE);
        }
        if(StringUtils.isEmpty(endpointID.getEndpointName())){
            return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_UNREACHABLE);
        }
        String targetService = getEndpointNameUtilities().getEndpointServiceNameFromEndpointName(endpointID.getEndpointName());
        String myServiceName = getEndpointServiceName();
        if(targetService.contentEquals(myServiceName)){
            return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_SAME);
        }
        PetasosEndpoint petasosEndpoint = probeEndpoint(endpointID, getPetasosEndpoint());
        if(petasosEndpoint == null){
            return(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_UNREACHABLE);
        } else {
            return (petasosEndpoint.getEndpointStatus());
        }
    }

    @Override
    protected PubSubParticipant specifyPubSubParticipant() {
        return (getJgroupsParticipantInformationService().getMyPetasosParticipantRole());
    }

    @Override
    protected void registerWithCoreSubsystemPetasosEndpointsWatchdog() {
        getCoreSubsystemPetasosEndpointsWatchdog().setPetasosOAMDiscoveryEndpoint(this.getPetasosEndpoint());
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
        getLogger().info(".interfaceAdded(): Entry, addedInterface->{}", addedInterface);
        boolean itIsAnotherInstanceOfMe = getEndpointNameUtilities().getEndpointServiceNameFromEndpointName(addedInterface.getAddressName()).contentEquals(getEndpointServiceName());
        boolean itIsSameType = getEndpointNameUtilities().getEndpointFunctionFromChannelName(addedInterface.getAddressName()).contentEquals(PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName());
        if(!itIsAnotherInstanceOfMe && itIsSameType) {
            getLogger().debug(".interfaceAdded(): !itIsAnotherInstanceOfMe && itIsSameType");
            PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
            String endpointChannelName = addedInterface.getAddressName();
            endpointID.setEndpointName(getEndpointNameUtilities().buildEndpointNameFromChannelName(endpointChannelName));
            endpointID.setEndpointChannelName(endpointChannelName);
            endpointID.setEndpointGroup(specifyJGroupsClusterName());
            endpointID.setEndpointSite(getEndpointNameUtilities().getEndpointSiteFromChannelName(endpointChannelName));
            String endpointZoneName = getEndpointNameUtilities().getEndpointZoneFromChannelName(endpointChannelName);
            NetworkSecurityZoneEnum networkSecurityZoneEnum = NetworkSecurityZoneEnum.fromSecurityZoneCamelCaseString(endpointZoneName);
            endpointID.setEndpointZone(networkSecurityZoneEnum);
            getEndpointMap().scheduleEndpointCheck(endpointID, false, true);
            scheduleEndpointValidation();
        }
        getLogger().debug(".interfaceAdded(): Exit");
    }

    @Override
    public void interfaceRemoved(PetasosAdapterAddress removedInterface){
        getLogger().info(".interfaceRemoved(): Entry, removedInterface->{}", removedInterface);
        boolean itIsAnotherInstanceOfMe = getEndpointNameUtilities().getEndpointServiceNameFromEndpointName(removedInterface.getAddressName()).contentEquals(getEndpointServiceName());
        boolean itIsSameType = getEndpointNameUtilities().getEndpointFunctionFromChannelName(removedInterface.getAddressName()).contentEquals(PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName());
        if(!itIsAnotherInstanceOfMe && itIsSameType) {
            getLogger().trace(".interfaceRemoved(): !itIsAnotherInstanceOfMe && itIsSameType");
            PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
            String endpointChannelName = removedInterface.getAddressName();
            endpointID.setEndpointName(getEndpointNameUtilities().buildEndpointNameFromChannelName(endpointChannelName));
            endpointID.setEndpointChannelName(endpointChannelName);
            endpointID.setEndpointGroup(specifyJGroupsClusterName());
            endpointID.setEndpointSite(getEndpointNameUtilities().getEndpointSiteFromChannelName(endpointChannelName));
            String endpointZoneName = getEndpointNameUtilities().getEndpointZoneFromChannelName(endpointChannelName);
            NetworkSecurityZoneEnum networkSecurityZoneEnum = NetworkSecurityZoneEnum.fromSecurityZoneCamelCaseString(endpointZoneName);
            endpointID.setEndpointZone(networkSecurityZoneEnum);
            getEndpointMap().scheduleEndpointCheck(endpointID, true, false);
            scheduleEndpointValidation();
        }
        getLogger().debug(".interfaceRemoved(): Exit");
    }

    public void interfaceSuspect(PetasosAdapterAddress suspectInterface){

    }

    public void scheduleEndpointScan(){
        getLogger().debug(".scheduleEndpointScan(): Entry");
        List<PetasosAdapterAddress> groupMembers = getAllClusterMemberAdapterAddresses();
        for(PetasosAdapterAddress currentGroupMember: groupMembers){
            if(currentGroupMember.getAddressName().contains(PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName())) {
                PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
                String endpointChannelName = currentGroupMember.getAddressName();
                endpointID.setEndpointName(getEndpointNameUtilities().buildEndpointNameFromChannelName(endpointChannelName));
                endpointID.setEndpointChannelName(endpointChannelName);
                endpointID.setEndpointGroup(specifyJGroupsClusterName());
                endpointID.setEndpointSite(getEndpointNameUtilities().getEndpointSiteFromChannelName(endpointChannelName));
                String endpointZoneName = getEndpointNameUtilities().getEndpointZoneFromChannelName(endpointChannelName);
                NetworkSecurityZoneEnum networkSecurityZoneEnum = NetworkSecurityZoneEnum.fromSecurityZoneCamelCaseString(endpointZoneName);
                endpointID.setEndpointZone(networkSecurityZoneEnum);
                getEndpointMap().scheduleEndpointCheck(endpointID, false, true);
                getLogger().trace(".scheduleEndpointScan(): Added ->{} to scan", endpointID);
            }
        }
        getLogger().debug(".scheduleEndpointScan(): Exit");
    }

    //
    // Basic Endpoint Validation Test
    //

    /**
     *
     */
    public void scheduleEndpointValidation() {
        getLogger().debug(".scheduleEndpointValidation(): Entry (isEndpointCheckScheduled->{})", endpointCheckScheduled);
        if (endpointCheckScheduled) {
            // do nothing, it is already scheduled
        } else {
            TimerTask endpointValidationTask = new TimerTask() {
                public void run() {
                    getLogger().debug(".endpointValidationTask(): Entry");
                    boolean doAgain = performEndpointValidationCheck();
                    getLogger().debug(".endpointValidationTask(): doAgain ->{}", doAgain);
                    if (!doAgain) {
                        cancel();
                        endpointCheckScheduled = false;
                    }
                    getLogger().debug(".endpointValidationTask(): Exit");
                }
            };
            String timerName = "EndpointValidationWatchdogTask";
            Timer timer = new Timer(timerName);
            timer.schedule(endpointValidationTask, getJgroupsParticipantInformationService().getEndpointValidationStartDelay(), getJgroupsParticipantInformationService().getEndpointValidationPeriod());
            endpointCheckScheduled = true;
        }
        getLogger().debug(".scheduleEndpointValidation(): Exit");
    }

    /**
     * This method retrieves the list of "Endpoints" to be "Probed" from the EndpointMap.EndpointsToCheck
     * (ConcurrentHashMap) and, if they are in the same Group (JGroups Cluster), attempts to retrieve their
     * PetasosEndpoint instance.
     *
     * It then uses this PetasosEndpoint instance (returnedEndpointFromTarget) to update the EndpointMap with
     * the current details (from the source, so to speak).
     *
     * It keeps a list of endpoints that it couldn't check and re-schedules their validation check.
     *
     * It also checks the Service-to-EndpointName map and ensures this aligns with the information provided.
     *
     * It then checks to see if there is a need to do another check/validation iteration and returns the result.
     *
     * @return True if another validation is required, false otherwise.
     */
    public boolean performEndpointValidationCheck(){
        getLogger().debug(".performEndpointValidationCheck(): Entry");
        List<PetasosEndpointCheckScheduleElement> endpointsToCheck = getEndpointMap().getEndpointsToCheck();
        List<PetasosEndpointCheckScheduleElement> redoList = new ArrayList<>();
        getLogger().trace(".performEndpointValidationCheck(): Iterate through...");
        for(PetasosEndpointCheckScheduleElement currentScheduleElement: endpointsToCheck) {
            getLogger().trace(".performEndpointValidationCheck(): currentScheduleElement->{}", currentScheduleElement);
            if(currentScheduleElement.isEndpointAdded()) {
                boolean wasProcessed = checkEndpointAddition(currentScheduleElement);
                if(wasProcessed) {
                    getLogger().trace(".performEndpointValidationCheck(): item was processed!");
                } else {
                    getLogger().trace(".performEndpointValidationCheck(): item was NOT processed, adding to redo list");
                    redoList.add(currentScheduleElement);
                }
            }
            if(currentScheduleElement.isEndpointRemoved()){
                checkEndpointRemoval(currentScheduleElement);
            }
        }
        for(PetasosEndpointCheckScheduleElement redoItem: redoList){
            getLogger().trace(".performEndpointValidationCheck(): Re-Adding to schedule the redoItem->{}", redoItem);
            getEndpointMap().scheduleEndpointCheck(redoItem.getPetasosEndpointID(), false, true);
        }
        if(getEndpointMap().isCheckScheduleIsEmpty()){
            getLogger().debug(".performEndpointValidationCheck(): Exit, perform again->false");
            return(false);
        } else {
            getLogger().debug(".performEndpointValidationCheck(): Exit, perform again->true");
            return(true);
        }
    }

    private boolean isOAMDiscoveryEndpoint(String endpointChannelName){
        if(StringUtils.isEmpty(endpointChannelName)){
            return(false);
        }
        String endpointFunctionType = getEndpointNameUtilities().getEndpointFunctionFromChannelName(endpointChannelName);
        boolean isOAMDiscoveryEndpoint = endpointFunctionType.contentEquals(PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName());
        return(isOAMDiscoveryEndpoint);
    }

    private PetasosEndpoint synchroniseEndpointCache(PetasosEndpointCheckScheduleElement currentScheduleElement){
        getLogger().debug(".synchroniseEndpointCache: Entry, currentScheduleElement->{}", currentScheduleElement);
        if(currentScheduleElement == null){
            getLogger().debug(".synchroniseEndpointCache: Exit, currentScheduleElement is null");
            return(null);
        }
        String endpointName = currentScheduleElement.getPetasosEndpointID().getEndpointName();
        String endpointChannelName = currentScheduleElement.getPetasosEndpointID().getEndpointChannelName();
        getLogger().trace(".synchroniseEndpointCache: Checking to see if endpoint is already in EndpointMap");
        PetasosEndpoint cachedEndpoint = getEndpointMap().getEndpoint(endpointName);
        getLogger().trace(".synchroniseEndpointCache: Retrieved PetasosEndpoint->{}", cachedEndpoint);
        boolean doProbe = true;
        boolean isToBeRemoved = false;
        if(cachedEndpoint != null){
            switch(cachedEndpoint.getEndpointStatus()) {
                case PETASOS_ENDPOINT_STATUS_DETECTED:
                case PETASOS_ENDPOINT_STATUS_STARTED:
                case PETASOS_ENDPOINT_STATUS_REACHABLE:{
                    getLogger().trace(".synchroniseEndpointCache: Endpoint is ok, but not operational, going to have to Probe it!!");
                    doProbe = true;
                    break;
                }
                case PETASOS_ENDPOINT_STATUS_OPERATIONAL:
                {
                    getLogger().debug(".synchroniseEndpointCache(): Endpoint is operational, do nothing! ");
                    return(cachedEndpoint);
                }
                case PETASOS_ENDPOINT_STATUS_SAME: {
                    getLogger().debug(".synchroniseEndpointCache(): Endpoint is 'me', do nothing! ");
                    return (cachedEndpoint);
                }
                case PETASOS_ENDPOINT_STATUS_SUSPECT:{
                    getLogger().debug(".synchroniseEndpointCache(): Endpoint is suspect, do nothing and wait and see! ");
                    return (cachedEndpoint);
                }
                case PETASOS_ENDPOINT_STATUS_FAILED:
                case PETASOS_ENDPOINT_STATUS_UNREACHABLE:
                default:{
                    getLogger().trace(".synchroniseEndpointCache(): Endpoint is in a poor state, remove it from our cache! ");
                    doProbe = false;
                    isToBeRemoved = true;
                }
            }
        }
        if(doProbe) {
            if (isTargetAddressActive(currentScheduleElement.getPetasosEndpointID().getEndpointChannelName())) {
                getLogger().trace(".checkEndpointAddition(): Probing (or attempting to Probe) the Endpoint");
                PetasosEndpoint returnedEndpointFromTarget = probeEndpoint(currentScheduleElement.getPetasosEndpointID(), getPetasosEndpoint());
                getLogger().trace(".checkEndpointAddition(): returnedEndpointFromTarget->{}", returnedEndpointFromTarget);
                if (returnedEndpointFromTarget != null) {
                    getLogger().trace(".checkEndpointAddition(): Probe succeded, so let's synchronise/update local cache");
                    if (cachedEndpoint == null) {
                        cachedEndpoint = getEndpointMap().addEndpoint(returnedEndpointFromTarget);
                        getLogger().trace(".checkEndpointAddition(): addedPetasosEndpoint->{}", cachedEndpoint);
                        if (!StringUtils.isEmpty(returnedEndpointFromTarget.getEndpointServiceName())) {
                            getEndpointMap().updateServiceNameMembership(returnedEndpointFromTarget.getEndpointServiceName(), currentScheduleElement.getPetasosEndpointID().getEndpointName());
                        }
                    } else {
                        synchronized (getEndpointMap().getEndpointLock(currentScheduleElement.getPetasosEndpointID().getEndpointName())) {
                            cachedEndpoint.encrichPetasosEndpoint(returnedEndpointFromTarget);
                        }
                    }
                    return(cachedEndpoint);
                } else {
                    getLogger().trace(".checkEndpointAddition(): Probe failed, we should consider removing it!");
                    isToBeRemoved = true;
                }
            } else {
                getLogger().trace(".checkEndpointAddition(): Couldn't even find the endpoint, we should consider removing it!");
                isToBeRemoved = true;
            }
        }
        if(isToBeRemoved){
            getLogger().trace(".checkEndpointAddition(): We should remove the Endpoint from our Cache and ToDo schedule!");
            int retryCountSoFar = currentScheduleElement.getRetryCount();
            if(retryCountSoFar > MAX_PROBE_RETRIES){
                getLogger().trace(".checkEndpointAddition(): we've tried to probe endpoint MAX_PROBE_RETRIES ({}) times and failed, so delete it", MAX_PROBE_RETRIES);
                getEndpointMap().scheduleEndpointCheck(currentScheduleElement.getPetasosEndpointID(), true, false);
            } else {
                getLogger().trace(".checkEndpointAddition(): probe has failed ({}) times, but we will try again", retryCountSoFar);
                retryCountSoFar += 1;
                getEndpointMap().scheduleEndpointCheck(currentScheduleElement.getPetasosEndpointID(), false, true, retryCountSoFar);
            }
            return(null);
        }
        return(cachedEndpoint);
    }

    protected boolean checkEndpointAddition(PetasosEndpointCheckScheduleElement currentScheduleElement){
        getLogger().debug(".checkEndpointAddition(): Entry, currentScheduleElement->{}", currentScheduleElement);
        String endpointChannelName = currentScheduleElement.getPetasosEndpointID().getEndpointChannelName();
        if(!isOAMDiscoveryEndpoint(endpointChannelName)){
            getLogger().debug(".checkEndpointAddition(): We are not going to waste time checking non-Discovery ports, returning -true- (was processed)");
            return(true);
        }
        PetasosEndpoint synchronisedEndpoint = synchroniseEndpointCache(currentScheduleElement);
        if(synchronisedEndpoint != null){
            switch(synchronisedEndpoint.getEndpointStatus()){
                case PETASOS_ENDPOINT_STATUS_OPERATIONAL:{
                    addPublisherToRegistry(synchronisedEndpoint);
                    getEndpointMap().updateServiceNameMembership(synchronisedEndpoint.getEndpointServiceName(), currentScheduleElement.getPetasosEndpointID().getEndpointName());
                    getLogger().debug(".checkEndpointAddition(): Does not need re-checking, returning -true- (was processed)");
                    return(true);
                }
                case PETASOS_ENDPOINT_STATUS_SUSPECT:
                case PETASOS_ENDPOINT_STATUS_REACHABLE:
                case PETASOS_ENDPOINT_STATUS_STARTED:
                case PETASOS_ENDPOINT_STATUS_DETECTED:{
                    getLogger().debug(".checkEndpointAddition(): Needs re-checking, returning -false- (wasn't completely processed)");
                    return (false);
                }
                case PETASOS_ENDPOINT_STATUS_SAME:
                case PETASOS_ENDPOINT_STATUS_UNREACHABLE:
                case PETASOS_ENDPOINT_STATUS_FAILED:
                default:{
                    getLogger().debug(".checkEndpointAddition(): We've rescheduled the removal of this endpoint returning -true- (was processed)");
                    return (true);
                }
            }
        }
        getLogger().debug(".checkEndpointAddition(): there is nothing to check, so returning->true (was processed)");
        return (true);
    }

    protected void checkEndpointRemoval(PetasosEndpointCheckScheduleElement currentScheduleElement){
        getLogger().debug(".checkEndpointRemoval(): Entry, currentScheduleElement->{}", currentScheduleElement);
        boolean sameGroup = currentScheduleElement.getPetasosEndpointID().getEndpointGroup().equals(specifyJGroupsClusterName());
        boolean sameEndpointType = currentScheduleElement.getPetasosEndpointID().getEndpointChannelName().contains(getPetasosEndpointFunctionType().getDisplayName());
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
            getLogger().trace(".addPublisherToRegistry(): Add Publsher ===> Start");
            getLogger().trace(".addPublisherToRegistry(): Creating new publisher instance");
            InterSubsystemPubSubParticipant publisher = new InterSubsystemPubSubParticipant(addedPetasosEndpoint);
            getLogger().trace(".addPublisherToRegistry(): Registering the publisher into the PupSub Map");
            publisherRegistration = getDistributedPubSubSubscriptionMapIM().registerPublisherInstance(publisher);
            getLogger().trace(".addPublisherToRegistry(): Notifying other Modules that a new Publisher is available");
            getCoreSubsystemPetasosEndpointsWatchdog().notifyNewPublisher(publisher);
            getLogger().trace(".addPublisherToRegistry(): Add Publisher ===> Finish");
        }
        getLogger().debug(".addPublisherToRegistry(): Exit, publisherRegistration->{}",publisherRegistration);
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

    //
    // OAM Services
    //

    public Instant shareLocalTopologyGraph(String serviceProviderName, PetasosMonitoredTopologyGraph topologyGraph){
        getLogger().trace(".updateMetric(): Entry, serviceProviderName->{}, topologyGraph->{}", serviceProviderName, topologyGraph);
        PetasosEndpointIdentifier endpointIdentifier = getEndpointID();
        Address targetAddress = getCandidateTargetServiceAddress(serviceProviderName);
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = topologyGraph;
            classSet[0] = PetasosMonitoredTopologyGraph.class;
            objectSet[1] = endpointIdentifier;
            classSet[1] = PetasosEndpointIdentifier.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Instant responseInstant = getRPCDispatcher().callRemoteMethod(targetAddress, "topologyGraphHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".updateMetric(): Exit, responseInstant->{}", responseInstant);
            return(responseInstant);
        } catch (NoSuchMethodException e) {
            getLogger().error(".updateMetric(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".updateMetric: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    public Instant topologyGraphHandler(PetasosMonitoredTopologyGraph topologyGraph, PetasosEndpointIdentifier endpointIdentifier){
        getLogger().trace(".logAuditEventHandler(): Entry, topologyGraph->{}, endpointIdentifier->{}", topologyGraph, endpointIdentifier);
        Instant outcomeInstant = null;
        if((topologyGraph != null) && (endpointIdentifier != null)) {
            outcomeInstant = topologyHandler.mergeRemoteTopologyGraph(topologyGraph, endpointIdentifier);
        }
        getLogger().debug(".logAuditEventHandler(): Exit, outcomeInstant->{}", outcomeInstant);
        return(outcomeInstant);
    }

}
