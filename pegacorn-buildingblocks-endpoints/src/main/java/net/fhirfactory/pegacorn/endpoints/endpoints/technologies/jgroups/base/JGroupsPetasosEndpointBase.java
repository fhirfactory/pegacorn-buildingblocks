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
package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.base;

import net.fhirfactory.pegacorn.deployment.names.functionality.base.PegacornCommonInterfaceNames;
import net.fhirfactory.pegacorn.deployment.properties.codebased.PegacornIPCCommonValues;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.endpoints.endpoints.CoreSubsystemPetasosEndpointsWatchdog;
import net.fhirfactory.pegacorn.endpoints.endpoints.map.PetasosEndpointMap;
import net.fhirfactory.pegacorn.endpoints.endpoints.map.datatypes.PetasosEndpointCheckScheduleElement;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.common.PetasosAdapterTechnologyInterface;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.helpers.JGroupsBasedParticipantInformationService;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class JGroupsPetasosEndpointBase extends JGroupsPetasosAdapterBase implements PetasosAdapterTechnologyInterface {

    private PetasosEndpoint petasosEndpoint;
    private PubSubParticipant participant;
    private boolean endpointCheckScheduled;

    @Inject
    private PetasosEndpointMap endpointMap;

    @Inject
    private PegacornCommonInterfaceNames interfaceNames;

    @Inject
    private PegacornIPCCommonValues ipcFunctionalityNames;

    @Inject
    private JGroupsBasedParticipantInformationService jgroupsParticipantInformationService;

    @Inject
    CoreSubsystemPetasosEndpointsWatchdog coreSubsystemPetasosEndpointsWatchdog;

    //
    // Constructor
    //

    public JGroupsPetasosEndpointBase(){
        super();
        endpointCheckScheduled = false;
    }

    //
    // Abstract Methods
    //

    abstract protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType();
    abstract protected boolean supportsInterZoneCommunication();
    abstract protected boolean supportsInterSiteCommunication();
    abstract protected boolean supportsIntraZoneCommunication();
    abstract protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType();
    abstract protected PubSubParticipant specifyPubSubParticipant();
    abstract protected void resolveTopologyEndpoint();
    abstract protected void registerWithCoreSubsystemPetasosEndpointsWatchdog();

    //
    // PostConstruct Initialisation
    //

    @PostConstruct
    public void initialise() {
        getLogger().info(".initialise(): Entry");
        if (isInitialised()) {
            getLogger().info(".initialise(): Exit, already initialised!");
            return;
        }
        // 1st, Derive my Endpoint (Topology)
        getLogger().info(".initialise(): Step 1: Start ==> Get my IPCEndpoint Detail");
        resolveTopologyEndpoint();
        getLogger().info(".initialise(): Step 1: Complete ==> IPCEndpoint derived ->{}", getTopologyNode());

        // 2nd, the PetasosEndpoint
        getLogger().info(".initialise(): Step 2: Start ==> Creating my PetasosEndpoint");
        PetasosEndpoint petasosEndpoint = getEndpointMap().addEndpoint(getEndpointID(), "JGroups", getEndpointServiceName(), getPetasosEndpointFunctionType(), getPetasosEndpointPayloadType());
        this.petasosEndpoint = petasosEndpoint;
        getLogger().info(".initialise(): Step 2: Completed ==> PetasosEndpoint created ->{}", getPetasosEndpoint());

        // 3rd, Register with the CoreSubsystemPetasosEndpointsWatchdog
        getLogger().info(".initialise(): Step 3: Start ==> Registering with CoreSubsystemPetasosEndpointsWatchdog");
        registerWithCoreSubsystemPetasosEndpointsWatchdog();
        getLogger().info(".initialise(): Step 3: Completed ==> Registered with CoreSubsystemPetasosEndpointsWatchdog");

        // 4th, the Participant
        getLogger().info(".initialise(): [Create and bind the PubSubParticipant] Start");
        this.participant = specifyPubSubParticipant();
        getLogger().info(".initialise(): [Create and bind the PubSubParticipant] Complete, this.participant->{}", getParticipant());

        // 5th, Initialise my JChannel
        getLogger().info(".initialise(): Step 3: Start ==> Initialise my JChannel Connection & Join Cluster/Group");
        establishJChannel();
        getLogger().info(".initialise(): Step 3: Completed ==> ipcChannel ->{}", getIPCChannel());

        //
        // 6th, Schedule Basic Endpoint Validation
        //

    }

    //
    // Getters (and Setters)
    //

    protected PetasosEndpointMap getEndpointMap(){
        return(endpointMap);
    }

    protected PetasosEndpoint getPetasosEndpoint(){
        return(petasosEndpoint);
    }

    protected PetasosEndpointFunctionTypeEnum getPetasosEndpointFunctionType(){
        return(specifyPetasosEndpointFunctionType());
    }

    protected EndpointPayloadTypeEnum getPetasosEndpointPayloadType(){
        return(specifyPetasosEndpointPayloadType());
    }

    protected PegacornCommonInterfaceNames getInterfaceNames(){
        return(interfaceNames);
    }

    protected PegacornIPCCommonValues getIPCComponentNames() {
        return ipcFunctionalityNames;
    }

    protected JGroupsBasedParticipantInformationService getJgroupsParticipantInformationService() {
        return jgroupsParticipantInformationService;
    }

    public PubSubParticipant getParticipant(){
        return(participant);
    }

    protected CoreSubsystemPetasosEndpointsWatchdog getCoreSubsystemPetasosEndpointsWatchdog(){
        return(coreSubsystemPetasosEndpointsWatchdog);
    }

    //
    //
    //

    public void scheduleEndpointScan(){
        List<PetasosAdapterAddress> groupMembers = getAllGroupMembers();
        for(PetasosAdapterAddress currentGroupMember: groupMembers){
            PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
            endpointID.setEndpointName(currentGroupMember.getAddressName());
            endpointID.setEndpointGroup(specifyJGroupsClusterName());
            getEndpointMap().scheduleEndpointCheck(endpointID, false, true);
        }
    }

    /**
     *
     * @param endpointID
     * @param myEndpoint
     * @return
     */
    public PetasosEndpoint probeEndpoint(PetasosEndpointIdentifier endpointID, PetasosEndpoint myEndpoint){
        getLogger().info(".probeEndpoint(): Entry, endpointID->{}", endpointID);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = myEndpoint;
            classSet[0] = PetasosEndpoint.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Address endpointAddress = getTargetMemberAddress(endpointID.getEndpointName());
            PetasosEndpoint targetPetasosEndpoint = getRPCDispatcher().callRemoteMethod(endpointAddress, "probeEndpointHandler", objectSet, classSet, requestOptions);
            getLogger().info(".probeEndpoint(): Exit, response->{}", targetPetasosEndpoint);
            return(targetPetasosEndpoint);
        } catch (NoSuchMethodException e) {
            getLogger().error(".probeEndpoint(): Error (NoSuchMethodException)->", e);
            return(null);
        } catch (Exception e) {
            getLogger().error(".probeEndpoint: Error (GeneralException) ->",e);
            return(null);
        }
    }

    /**
     *
     * @param endpointID
     * @return
     */
    public PetasosEndpoint probeEndpointHandler(PetasosEndpoint endpointID){
        getLogger().info(".probeEndpointHandler(): Entry, endpointID->{}", endpointID);
        getEndpointMap().addEndpoint(endpointID);
        getLogger().info(".probeEndpointHandler(): Exit, returning->{}", getPetasosEndpoint());
        return(getPetasosEndpoint());
    }

    @Override
    public void registerInterfaceEventCallbacks(PetasosAdapterDeltasInterface interfaceEventCallbacks) {
        this.getMembershipEventListeners().add(interfaceEventCallbacks);
    }

    @Override
    protected String deriveEndpointServiceName(String endpointName) {
        getLogger().info(".deriveEndpointServiceName(): Entry, endpointName->{}", endpointName);
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        String[] split = StringUtils.split(endpointName, ".");
        String serviceName = split[0];
        getLogger().info(".deriveEndpointServiceName(): Exit, serviceName->{}", serviceName);
        return(serviceName);
    }

    //
    // Basic Endpoint Validation Test
    //

    /**
     *
     */
    public void scheduleEndpointValidation() {
        getLogger().info(".schedulePublisherCheck(): Entry (isEndpointCheckScheduled->{})", endpointCheckScheduled);
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
                    getLogger().info(".publisherCheckTask(): Exit");
                }
            };
            Timer timer = new Timer("EndpointValidationTask");
            timer.schedule(endpointValidationTask, getJgroupsParticipantInformationService().getEndpointValidationStartDelay(), getJgroupsParticipantInformationService().getEndpointValidationPeriod());
            endpointCheckScheduled = true;
        }

        getLogger().info(".publisherCheckTask(): Exit");
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
        List<PetasosEndpointCheckScheduleElement> endpointsToCheck = getEndpointMap().getEndpointsToCheck();
        List<PetasosEndpointCheckScheduleElement> unCheckedList = new ArrayList<>();
        for(PetasosEndpointCheckScheduleElement currentScheduleElement: endpointsToCheck) {
            if(currentScheduleElement.isEndpointAdded()) {
                if( currentScheduleElement.getPetasosEndpointID().getEndpointGroup().equals(specifyJGroupsClusterName()) &&
                    currentScheduleElement.getPetasosEndpointID().getEndpointName().contains(getPetasosEndpointFunctionType().getFunctionSuffix()))
                {
                    if(isTargetAddressActive(currentScheduleElement.getPetasosEndpointID().getEndpointName())){
                        PetasosEndpoint returnedEndpointFromTarget = probeEndpoint(currentScheduleElement.getPetasosEndpointID(), getPetasosEndpoint());
                        if(returnedEndpointFromTarget != null){
                            PetasosEndpoint localCachedEndpoint = getEndpointMap().getEndpoint(currentScheduleElement.getPetasosEndpointID().getEndpointName());
                            if (localCachedEndpoint == null) {
                                getEndpointMap().addEndpoint(returnedEndpointFromTarget);
                            } else {
                                synchronized (getEndpointMap().getEndpointLock(currentScheduleElement.getPetasosEndpointID().getEndpointName())){
                                    localCachedEndpoint.encrichPetasosEndpoint(returnedEndpointFromTarget);
                                }
                                if(!StringUtils.isEmpty(returnedEndpointFromTarget.getEndpointServiceName())){
                                    getEndpointMap().updateServiceNameMembership(returnedEndpointFromTarget.getEndpointServiceName(), currentScheduleElement.getPetasosEndpointID().getEndpointName());
                                }
                            }
                        }
                    } else {
                        unCheckedList.add(currentScheduleElement);
                    }
                }
            }
        }
        for(PetasosEndpointCheckScheduleElement uncheckedElement: unCheckedList){
            getEndpointMap().scheduleEndpointCheck(uncheckedElement.getPetasosEndpointID(), false, true);
        }
        if(getEndpointMap().getEndpointsToCheck().isEmpty()){
            return(false);
        } else {
            return(true);
        }
    }

    public List<String> getIPCTargetSet(String endpointServiceName){
        List<String> endpointNameSet = new ArrayList<>();
        if(StringUtils.isEmpty(endpointServiceName)){
            return(endpointNameSet);
        }
        endpointNameSet.addAll(getEndpointMap().getServiceNameMembership(endpointServiceName));
        return(endpointNameSet);
    }

    public List<Address> getIPCTargetAddressSet(String endpointServiceName){
        List<Address> endpointAddressSet = new ArrayList<>();
        if(StringUtils.isEmpty(endpointServiceName)){
            return(endpointAddressSet);
        }
        List<String> serviceNameMembership = getEndpointMap().getServiceNameMembership(endpointServiceName);
        for(String currentMember: serviceNameMembership){
            Address currentMemberAddress = getTargetMemberAddress(currentMember);
            if(currentMemberAddress != null){
                endpointAddressSet.add(currentMemberAddress);
            }
        }
        return(endpointAddressSet);
    }

    public Address getCandidateIPCTargetAddress(String endpointServiceName){
        if(StringUtils.isEmpty(endpointServiceName)){
            return(null);
        }
        List<Address> endpointAddressSet = getIPCTargetAddressSet(endpointServiceName);
        if(endpointAddressSet.isEmpty()){
            return(null);
        }
        return(endpointAddressSet.get(0));
    }
}
