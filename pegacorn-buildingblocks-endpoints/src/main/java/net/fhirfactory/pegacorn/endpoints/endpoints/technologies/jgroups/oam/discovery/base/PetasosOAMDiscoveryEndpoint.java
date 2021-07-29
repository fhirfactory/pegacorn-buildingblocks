package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.oam.discovery.base;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.endpoints.endpoints.map.datatypes.PetasosEndpointCheckScheduleElement;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddressTypeEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DistributedPubSubSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherRegistration;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.lang.reflect.ParameterizedType;
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
        boolean itIsAnotherInstanceOfMe = addedInterface.getAddressName().contains(getEndpointServiceName());
        boolean itIsSameType = addedInterface.getAddressName().contains(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionSuffix());
        boolean isWithinScope = isWithinScopeBasedOnKey(addedInterface.getAddressName());
        if(isWithinScope && !itIsAnotherInstanceOfMe && itIsSameType) {
            PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
            endpointID.setEndpointName(removeFunctionNameSuffixFromEndpointName(addedInterface.getAddressName()));
            endpointID.setEndpointAddressName(addedInterface.getAddressName());
            endpointID.setEndpointGroup(specifyJGroupsClusterName());
            getEndpointMap().scheduleEndpointCheck(endpointID, false, true);
            scheduleEndpointValidation();
        }
    }

    @Override
    public void interfaceRemoved(PetasosAdapterAddress removedInterface){
        boolean itIsAnotherInstanceOfMe = removedInterface.getAddressName().contains(getEndpointServiceName());
        boolean itIsSameType = removedInterface.getAddressName().contains(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionSuffix());
        boolean isWithinScope = isWithinScopeBasedOnKey(removedInterface.getAddressName());
        if(isWithinScope && !itIsAnotherInstanceOfMe && itIsSameType) {
            PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
            endpointID.setEndpointName(removeFunctionNameSuffixFromEndpointName(removedInterface.getAddressName()));
            endpointID.setEndpointAddressName(removedInterface.getAddressName());
            endpointID.setEndpointGroup(specifyJGroupsClusterName());
            getEndpointMap().scheduleEndpointCheck(endpointID, true, false);
            scheduleEndpointValidation();
        }
    }

    public void interfaceSuspect(PetasosAdapterAddress suspectInterface){

    }

    public void scheduleEndpointScan(){
        getLogger().info(".scheduleEndpointScan(): Entry");
        List<PetasosAdapterAddress> groupMembers = getAllGroupMembers();
        for(PetasosAdapterAddress currentGroupMember: groupMembers){
            if(currentGroupMember.getAddressName().contains(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionSuffix())) {
                PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
                endpointID.setEndpointName(removeFunctionNameSuffixFromEndpointName(currentGroupMember.getAddressName()));
                endpointID.setEndpointAddressName(currentGroupMember.getAddressName());
                endpointID.setEndpointGroup(specifyJGroupsClusterName());
                getEndpointMap().scheduleEndpointCheck(endpointID, false, true);
                getLogger().info(".scheduleEndpointScan(): Added ->{} to scan", endpointID);
            }
        }
        getLogger().info(".scheduleEndpointScan(): Exit");
    }

    //
    // Basic Endpoint Validation Test
    //

    /**
     *
     */
    public void scheduleEndpointValidation() {
        getLogger().info(".scheduleEndpointValidation(): Entry (isEndpointCheckScheduled->{})", endpointCheckScheduled);
        if (endpointCheckScheduled) {
            // do nothing, it is already scheduled
        } else {
            TimerTask endpointValidationTask = new TimerTask() {
                public void run() {
                    getLogger().info(".endpointValidationTask(): Entry");
                    boolean doAgain = performEndpointValidationCheck();
                    getLogger().info(".endpointValidationTask(): doAgain ->{}", doAgain);
                    if (!doAgain) {
                        cancel();
                        endpointCheckScheduled = false;
                    }
                    getLogger().info(".endpointValidationTask(): Exit");
                }
            };
            Timer timer = new Timer("ScheduleEndpointValidation");
            timer.schedule(endpointValidationTask, getJgroupsParticipantInformationService().getEndpointValidationStartDelay(), getJgroupsParticipantInformationService().getEndpointValidationPeriod());
            endpointCheckScheduled = true;
        }
        getLogger().info(".scheduleEndpointValidation(): Exit");
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
        getLogger().info(".performEndpointValidationCheck(): Entry");
        List<PetasosEndpointCheckScheduleElement> endpointsToCheck = getEndpointMap().getEndpointsToCheck();
        List<PetasosEndpointCheckScheduleElement> unCheckedList = new ArrayList<>();
        getLogger().info(".performEndpointValidationCheck(): Iterate through...");
        for(PetasosEndpointCheckScheduleElement currentScheduleElement: endpointsToCheck) {
            getLogger().info(".performEndpointValidationCheck(): currentScheduleElement->{}", currentScheduleElement);
            if(currentScheduleElement.isEndpointAdded()) {
                boolean wasChecked = checkEndpointAddition(currentScheduleElement);
                if(!wasChecked){
                    unCheckedList.add(currentScheduleElement);
                }
            }
            if(currentScheduleElement.isEndpointRemoved()){
                checkEndpointRemoval(currentScheduleElement);
            }
        }
        for(PetasosEndpointCheckScheduleElement uncheckedElement: unCheckedList){
            getLogger().info(".performEndpointValidationCheck(): Re-Adding to schedule the uncheckedElement->{}", uncheckedElement);
            getEndpointMap().scheduleEndpointCheck(uncheckedElement.getPetasosEndpointID(), false, true);
        }
        if(getEndpointMap().getEndpointsToCheck().isEmpty()){
            getLogger().info(".performEndpointValidationCheck(): Exit, perform again->false");
            return(false);
        } else {
            getLogger().info(".performEndpointValidationCheck(): Exit, perform again->true");
            return(true);
        }
    }

    protected boolean checkEndpointAddition(PetasosEndpointCheckScheduleElement currentScheduleElement){
        getLogger().info(".checkEndpointAddition(): Entry, currentScheduleElement->{}", currentScheduleElement);
        if(isWithinScopeBasedOnKey(currentScheduleElement.getPetasosEndpointID().getEndpointName())) {
            boolean sameGroup = currentScheduleElement.getPetasosEndpointID().getEndpointGroup().equals(specifyJGroupsClusterName());
            boolean sameEndpointType = currentScheduleElement.getPetasosEndpointID().getEndpointAddressName().contains(getPetasosEndpointFunctionType().getFunctionSuffix());
            if ( sameGroup && sameEndpointType){
                if (isTargetAddressActive(currentScheduleElement.getPetasosEndpointID().getEndpointAddressName())) {
                    PetasosEndpoint returnedEndpointFromTarget = probeEndpoint(currentScheduleElement.getPetasosEndpointID(), getPetasosEndpoint());
                    getLogger().info(".checkEndpointAddition(): returnedEndpointFromTarget->{}", returnedEndpointFromTarget);
                    if (returnedEndpointFromTarget != null) {
                        PetasosEndpoint localCachedEndpoint = getEndpointMap().getEndpoint(currentScheduleElement.getPetasosEndpointID().getEndpointName());
                        getLogger().info(".checkEndpointAddition(): CachedEndpoint->{}", localCachedEndpoint);
                        if (localCachedEndpoint == null) {
                            PetasosEndpoint addedPetasosEndpoint = getEndpointMap().addEndpoint(returnedEndpointFromTarget);
                            getLogger().info(".checkEndpointAddition(): addedPetasosEndpoint->{}", addedPetasosEndpoint);
                            addPublisherToRegistry(addedPetasosEndpoint);
                            if (!StringUtils.isEmpty(returnedEndpointFromTarget.getEndpointServiceName())) {
                                getEndpointMap().updateServiceNameMembership(returnedEndpointFromTarget.getEndpointServiceName(), currentScheduleElement.getPetasosEndpointID().getEndpointName());
                            }
                        } else {
                            synchronized (getEndpointMap().getEndpointLock(currentScheduleElement.getPetasosEndpointID().getEndpointName())) {
                                localCachedEndpoint.encrichPetasosEndpoint(returnedEndpointFromTarget);
                            }
                            addPublisherToRegistry(localCachedEndpoint);
                        }
                    } else {
                        int retryCountSoFar = currentScheduleElement.getRetryCount();
                        if(retryCountSoFar > MAX_PROBE_RETRIES){
                            getLogger().info(".checkEndpointAddition(): we've tried to probe endpoint MAX_PROBE_RETRIES ({}) times and failed, so delete it", MAX_PROBE_RETRIES);
                            getEndpointMap().scheduleEndpointCheck(currentScheduleElement.getPetasosEndpointID(), true, false);
                        } else {
                            getLogger().info(".checkEndpointAddition(): probe has failed ({}) times, but we will try again", retryCountSoFar);
                            retryCountSoFar += 1;
                            getEndpointMap().scheduleEndpointCheck(currentScheduleElement.getPetasosEndpointID(), false, true, retryCountSoFar);
                        }
                    }
                }
                getLogger().info(".performEndpointValidationCheck(): Exit, was checked->true");
                return(true);
            }
        }
        getLogger().info(".performEndpointValidationCheck(): Exit, was checked->false");
        return (false);
    }

    protected void checkEndpointRemoval(PetasosEndpointCheckScheduleElement currentScheduleElement){
        getLogger().info(".checkEndpointRemoval(): Entry, currentScheduleElement->{}", currentScheduleElement);
        boolean sameGroup = currentScheduleElement.getPetasosEndpointID().getEndpointGroup().equals(specifyJGroupsClusterName());
        boolean sameEndpointType = currentScheduleElement.getPetasosEndpointID().getEndpointAddressName().contains(getPetasosEndpointFunctionType().getFunctionSuffix());
        if( sameGroup && sameEndpointType ){
            boolean wasRemoved = removePublisher(currentScheduleElement.getPetasosEndpointID().getEndpointName());
            getEndpointMap().deleteEndpoint(currentScheduleElement.getPetasosEndpointID().getEndpointName());
        }
        getLogger().info(".checkEndpointRemoval(): Exit");
    }

    //
    // Publisher Management
    //

    protected void addPublisherToRegistry(PetasosEndpoint addedPetasosEndpoint) {
        getLogger().info(".addPublisherToRegistry(): Entry, addedPetasosEndpoint->{}", addedPetasosEndpoint);
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
        getLogger().info(".performPublisherCheck(): Entry, publisherInstanceName->{}", publisherInstanceName);
        if (StringUtils.isEmpty(publisherInstanceName)) {
            getLogger().info(".performPublisherCheck(): Exit, publisherInstanceName is empty");
            return (false);
        }
        getLogger().info(".performPublisherCheck(): Getting a list of all Publishers");
        InterSubsystemPubSubPublisherRegistration publisherInstanceRegistration = getDistributedPubSubSubscriptionMapIM().getPublisherInstanceRegistration(publisherInstanceName);
        if (publisherInstanceRegistration != null) {
            getDistributedPubSubSubscriptionMapIM().unregisterPublisherInstance(publisherInstanceRegistration.getPublisher());
            return (true);
        }
        return (false);
    }
}
