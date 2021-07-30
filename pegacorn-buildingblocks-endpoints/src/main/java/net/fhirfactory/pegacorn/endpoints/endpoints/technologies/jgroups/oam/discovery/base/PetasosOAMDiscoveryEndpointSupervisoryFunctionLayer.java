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

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.endpoints.endpoints.map.datatypes.PetasosEndpointCheckScheduleElement;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class PetasosOAMDiscoveryEndpointSupervisoryFunctionLayer extends PetasosOAMDiscoveryEndpointBusinessFunctionLayer{

    private boolean endpointCheckScheduled;

    private int MAX_PROBE_RETRIES = 5;

    //
    // Constructor
    //

    public PetasosOAMDiscoveryEndpointSupervisoryFunctionLayer(){
        super();
        endpointCheckScheduled = false;
    }

    //
    // PostContruct Activities
    //

    @Override
    protected void executePostConstructSupervisoryActivities(){
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
    // Watchdog Activity (Endpoint Scan) Request / Scheduling
    //

    public void scheduleEndpointScan(){
        getLogger().info(".scheduleEndpointScan(): Entry");
        List<PetasosAdapterAddress> groupMembers = getAllGroupMembers();
        for(PetasosAdapterAddress currentGroupMember: groupMembers){
            boolean itIsAnotherInstanceOfMe = currentGroupMember.getAddressName().contains(getEndpointServiceName());
            boolean itIsSameFunction = currentGroupMember.getAddressName().contains(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionSuffix());
            boolean rightChannel = isRightChannel(currentGroupMember.getAddressName());
            if(rightChannel && !itIsAnotherInstanceOfMe && itIsSameFunction) {
                getLogger().info(".scheduleEndpointScan(): Endpoint is isWithinScope && Not(itIsAnotherInstanceOfMe) && itIsSameType, so scheduling a check");
                PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
                endpointID.setEndpointName(removeFunctionNameSuffixFromEndpointName(currentGroupMember.getAddressName()));
                endpointID.setEndpointChannelName(currentGroupMember.getAddressName());
                endpointID.setEndpointGroup(specifyJGroupsClusterName());
                getEndpointMap().scheduleEndpointCheck(endpointID, false, true);
                getLogger().info(".scheduleEndpointScan(): Added ->{} to scan", endpointID);
            } else {
                getLogger().info(".interfaceAdded(): Endpoint removed is NOT(isWithinScope && NOT(itIsAnotherInstanceOfMe) && itIsSameType), so ignoring");
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
        getLogger().info(".scheduleEndpointValidation(): Entry (isEndpointCheckScheduled->{})", endpointCheckScheduled);
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
            String timerName = "watchdog:endpoint-validation-" + specifyIPCType().getEndpointType();
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
        List<PetasosEndpointCheckScheduleElement> unCheckedList = new ArrayList<>();
        getLogger().trace(".performEndpointValidationCheck(): Iterate through...");
        for(PetasosEndpointCheckScheduleElement currentScheduleElement: endpointsToCheck) {
            getLogger().trace(".performEndpointValidationCheck(): currentScheduleElement->{}", currentScheduleElement);
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
            getLogger().trace(".performEndpointValidationCheck(): Re-Adding to schedule the uncheckedElement->{}", uncheckedElement);
            getEndpointMap().scheduleEndpointCheck(uncheckedElement.getPetasosEndpointID(), false, true);
        }
        if(getEndpointMap().getEndpointsToCheck().isEmpty()){
            getLogger().debug(".performEndpointValidationCheck(): Exit, perform again->false");
            return(false);
        } else {
            getLogger().debug(".performEndpointValidationCheck(): Exit, perform again->true");
            return(true);
        }
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
        getLogger().info(".interfaceAdded(): Entry, addedInterface->{}", addedInterface);
        boolean itIsAnotherInstanceOfMe = addedInterface.contains(getEndpointServiceName());
        boolean itIsSameType = addedInterface.contains(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionSuffix());
        boolean isWithinScope = isRightChannel(addedInterface);
        if(isWithinScope && !itIsAnotherInstanceOfMe && itIsSameType) {
            getLogger().info(".interfaceAdded(): New Endpoint is (isWithinScope && Not(itIsAnotherInstanceOfMe) && itIsSameType), so scheduling a check");
            PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
            endpointID.setEndpointName(removeFunctionNameSuffixFromEndpointName(addedInterface));
            endpointID.setEndpointChannelName(addedInterface);
            endpointID.setEndpointGroup(specifyJGroupsClusterName());
            getEndpointMap().scheduleEndpointCheck(endpointID, false, true);
            scheduleEndpointValidation();
        } else {
            getLogger().info(".interfaceAdded(): New Endpoint is NOT(isWithinScope && NOT(itIsAnotherInstanceOfMe) && itIsSameType), so ignoring");
        }
    }

    @Override
    public void interfaceRemoved(String removedInterface){
        getLogger().info(".interfaceRemoved(): Entry, addedInterface->{}", removedInterface);
        boolean itIsAnotherInstanceOfMe = removedInterface.contains(getEndpointServiceName());
        boolean itIsSameType = removedInterface.contains(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionSuffix());
        boolean isWithinScope = isRightChannel(removedInterface);
        if(isWithinScope && !itIsAnotherInstanceOfMe && itIsSameType) {
            getLogger().info(".interfaceRemoved(): Endpoint removed is isWithinScope && Not(itIsAnotherInstanceOfMe) && itIsSameType, so scheduling a check");
            PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
            endpointID.setEndpointName(removeFunctionNameSuffixFromEndpointName(removedInterface));
            endpointID.setEndpointChannelName(removedInterface);
            endpointID.setEndpointGroup(specifyJGroupsClusterName());
            getEndpointMap().scheduleEndpointCheck(endpointID, true, false);
            scheduleEndpointValidation();
        } else {
            getLogger().info(".interfaceAdded(): Endpoint removed is NOT(isWithinScope && NOT(itIsAnotherInstanceOfMe) && itIsSameType), so ignoring");
        }
    }

    public void interfaceSuspect(String suspectInterface){

    }
}
