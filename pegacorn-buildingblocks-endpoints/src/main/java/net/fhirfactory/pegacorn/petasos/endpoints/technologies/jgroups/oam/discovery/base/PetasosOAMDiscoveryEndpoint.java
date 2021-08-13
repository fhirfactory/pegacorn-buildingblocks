package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.discovery.base;

import net.fhirfactory.pegacorn.deployment.topology.model.common.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.*;
import net.fhirfactory.pegacorn.petasos.endpoints.map.datatypes.PetasosEndpointCheckScheduleElement;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DistributedPubSubSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherRegistration;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class PetasosOAMDiscoveryEndpoint extends JGroupsPetasosEndpointBase implements PetasosAdapterDeltasInterface {

    private boolean endpointCheckScheduled;

    private int MAX_PROBE_RETRIES = 5;

    @Inject
    DistributedPubSubSubscriptionMapIM distributedPubSubSubscriptionMapIM;

    //
    // Constructor
    //

    public PetasosOAMDiscoveryEndpoint(){
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

    //
    // Endpoint Discovery
    //

    @Override
    public void registerInterfaceEventCallbacks(PetasosAdapterDeltasInterface interfaceEventCallbacks) {
        this.getMembershipEventListeners().add(interfaceEventCallbacks);
    }

    @Override
    public void interfaceAdded(PetasosAdapterAddress addedInterface){
        boolean itIsAnotherInstanceOfMe = getEndpointNameUtilities().getEndpointServiceNameFromEndpointName(addedInterface.getAddressName()).contentEquals(getEndpointServiceName());
        boolean itIsSameType = getEndpointNameUtilities().getEndpointFunctionFromChannelName(addedInterface.getAddressName()).contentEquals(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionName());
        boolean isWithinScope = isWithinScopeBasedOnChannelName(addedInterface.getAddressName());
        if(isWithinScope && !itIsAnotherInstanceOfMe && itIsSameType) {
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
    }

    @Override
    public void interfaceRemoved(PetasosAdapterAddress removedInterface){
        boolean itIsAnotherInstanceOfMe = getEndpointNameUtilities().getEndpointServiceNameFromEndpointName(removedInterface.getAddressName()).contentEquals(getEndpointServiceName());
        boolean itIsSameType = getEndpointNameUtilities().getEndpointFunctionFromChannelName(removedInterface.getAddressName()).contentEquals(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionName());
        boolean isWithinScope = isWithinScopeBasedOnChannelName(removedInterface.getAddressName());
        if(isWithinScope && !itIsAnotherInstanceOfMe && itIsSameType) {
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
    }

    public void interfaceSuspect(PetasosAdapterAddress suspectInterface){

    }

    public void scheduleEndpointScan(){
        getLogger().debug(".scheduleEndpointScan(): Entry");
        List<PetasosAdapterAddress> groupMembers = getAllGroupMembers();
        for(PetasosAdapterAddress currentGroupMember: groupMembers){
            if(currentGroupMember.getAddressName().contains(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionName())) {
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
            String timerName = "EndpointValidationWatchdogTask-" + specifyPetasosEndpointScope().getEndpointScopeName();
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
        List<PetasosEndpointCheckScheduleElement> endpointsToCheck = getEndpointMap().getEndpointsToCheck(specifyPetasosEndpointScope());
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
        boolean isOAMDiscoveryEndpoint = endpointFunctionType.contentEquals(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionName());
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
        String endpointName = currentScheduleElement.getPetasosEndpointID().getEndpointName();
        String endpointChannelName = currentScheduleElement.getPetasosEndpointID().getEndpointChannelName();
        if(!isOAMDiscoveryEndpoint(endpointChannelName)){
            getLogger().debug(".checkEndpointAddition(): We are not going to waste time checking non-Discovery ports, returning -true- (was processed)");
            return(true);
        }
        boolean isWithinScope = isWithinScopeBasedOnChannelName(endpointChannelName);
        if(!isWithinScope){
            getLogger().debug(".checkEndpointAddition(): Not within my scope, returning -false- (was not processed)");
            return(false);
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
        boolean sameEndpointType = currentScheduleElement.getPetasosEndpointID().getEndpointChannelName().contains(getPetasosEndpointFunctionType().getFunctionName());
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
}
