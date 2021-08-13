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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.base;

import net.fhirfactory.pegacorn.deployment.names.functionality.base.PegacornCommonInterfaceNames;
import net.fhirfactory.pegacorn.deployment.properties.codebased.PegacornIPCCommonValues;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.*;
import net.fhirfactory.pegacorn.petasos.endpoints.CoreSubsystemPetasosEndpointsWatchdog;
import net.fhirfactory.pegacorn.petasos.endpoints.map.PetasosEndpointMap;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.common.PetasosAdapterTechnologyInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.helpers.EndpointNameUtilities;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.helpers.JGroupsBasedParticipantInformationService;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public abstract class JGroupsPetasosEndpointBase extends JGroupsPetasosAdapterBase implements PetasosAdapterTechnologyInterface {

    private PetasosEndpoint petasosEndpoint;
    private PubSubParticipant participant;


    @Inject
    private PetasosEndpointMap endpointMap;

    @Inject
    private PegacornCommonInterfaceNames interfaceNames;

    @Inject
    private PegacornIPCCommonValues ipcFunctionalityNames;

    @Inject
    private JGroupsBasedParticipantInformationService jgroupsParticipantInformationService;

    @Inject
    private CoreSubsystemPetasosEndpointsWatchdog coreSubsystemPetasosEndpointsWatchdog;

    @Inject
    private EndpointNameUtilities endpointNameUtilities;

    //
    // Constructor
    //

    public JGroupsPetasosEndpointBase(){
        super();
    }

    //
    // Abstract Methods
    //

    abstract protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType();
    abstract protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType();
    abstract protected PubSubParticipant specifyPubSubParticipant();
    abstract protected void resolveTopologyEndpoint();
    abstract protected void registerWithCoreSubsystemPetasosEndpointsWatchdog();
    abstract protected void executePostConstructActivities();

    //
    // PostConstruct Initialisation
    //

    @PostConstruct
    public void initialise() {
        getLogger().debug(".initialise(): Entry");
        if (isInitialised()) {
            getLogger().debug(".initialise(): Exit, already initialised!");
            return;
        }
        // 1st, Derive my Endpoint (Topology)
        getLogger().info(".initialise(): Step 1: Start ==> Get my IPCEndpoint Detail");
        resolveTopologyEndpoint();
        getLogger().info(".initialise(): Step 1: Complete ==> IPCEndpoint derived ->{}", getTopologyNode());

        // 2nd, the PetasosEndpoint
        getLogger().info(".initialise(): Step 2: Start ==> Creating my PetasosEndpoint");
        this.setEndpointID(specifyEndpointID());
        PetasosEndpoint petasosEndpoint = getEndpointMap().newPetasosEndpoint(
                getEndpointID(), "JGroups", getEndpointServiceName(),
                getPetasosEndpointFunctionType(), getPetasosEndpointPayloadType(), specifyPetasosEndpointScope());
        this.petasosEndpoint = petasosEndpoint;
        this.getPetasosEndpoint().setEndpointStatus(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_STARTED);
        getLogger().info(".initialise(): Step 2: Completed ==> PetasosEndpoint created ->{}", getPetasosEndpoint());

        // 3rd, Register with the CoreSubsystemPetasosEndpointsWatchdog
        getLogger().info(".initialise(): Step 3: Start ==> Registering with CoreSubsystemPetasosEndpointsWatchdog");
        registerWithCoreSubsystemPetasosEndpointsWatchdog();
        getLogger().info(".initialise(): Step 3: Completed ==> Registered with CoreSubsystemPetasosEndpointsWatchdog");

        // 4th, the Participant
        getLogger().info(".initialise(): Step 4: Start ==> Create and bind the PubSubParticipant");
        this.participant = specifyPubSubParticipant();
        getLogger().info(".initialise(): Step 4: Completed ==> Create and bind the PubSubParticipant, this.participant->{}", getParticipant());

        // 5th, Initialise my JChannel
        getLogger().info(".initialise(): Step 5: Start ==> Initialise my JChannel Connection & Join Cluster/Group");
        establishJChannel();
        getLogger().info(".initialise(): Step 5: Completed ==> ipcChannel ->{}", getIPCChannel());

        //
        // 6th, Our Endpoint is Operational, So Assign Status
        //
        getPetasosEndpoint().setEndpointStatus(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_OPERATIONAL);

        //
        // 7th, Call any subclass PostConstruct methods.
        //
        executePostConstructActivities();

        // We're done!
        setInitialised(true);

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

    protected EndpointNameUtilities getEndpointNameUtilities(){
        return(endpointNameUtilities);
    }

    //
    //
    //

    /**
     *
     * @param targetEndpointID
     * @param myEndpoint
     * @return
     */
    public PetasosEndpoint probeEndpoint(PetasosEndpointIdentifier targetEndpointID, PetasosEndpoint myEndpoint){
        getLogger().debug(".probeEndpoint(): Entry, targetEndpointID->{}", targetEndpointID);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = myEndpoint;
            classSet[0] = PetasosEndpoint.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Address endpointAddress = getTargetMemberAddress(targetEndpointID.getEndpointChannelName());
            PetasosEndpoint targetPetasosEndpoint = getRPCDispatcher().callRemoteMethod(endpointAddress, "probeEndpointHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".probeEndpoint(): Exit, response->{}", targetPetasosEndpoint);
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
     * @param sourcePetasosEndpoint
     * @return
     */
    public PetasosEndpoint probeEndpointHandler(PetasosEndpoint sourcePetasosEndpoint){
        getLogger().debug(".probeEndpointHandler(): Entry, sourcePetasosEndpoint->{}", sourcePetasosEndpoint);
        getEndpointMap().addEndpoint(sourcePetasosEndpoint);
        PetasosEndpoint myEndpoint = SerializationUtils.clone(getPetasosEndpoint());
        myEndpoint.setEndpointStatus(getCoreSubsystemPetasosEndpointsWatchdog().getAggregatePetasosEndpointStatus());
        getLogger().info(".probeEndpointHandler(): Exit, myEndpoint->{}", myEndpoint);
        return(myEndpoint);
    }

    protected PetasosEndpoint cloneAndRemoveFunctionNameSuffixFromEndpointName(PetasosEndpoint originalEndpoint){
        if(originalEndpoint == null){
            return(null);
        }
        PetasosEndpoint endpoint = SerializationUtils.clone(originalEndpoint);
        removeFunctionNameSuffixFromEndpointName(endpoint);
        return(endpoint);
    }

    @Deprecated
    protected void removeFunctionNameSuffixFromEndpointName(PetasosEndpoint originalEndpoint) {
        if (originalEndpoint == null) {
            return;
        }
        if (originalEndpoint.getEndpointID() == null) {
            return;
        }
        String endpointKey = originalEndpoint.getEndpointID().getEndpointChannelName();
        if (StringUtils.isEmpty(endpointKey)) {
            return;
        }
        String newName = removeFunctionNameSuffixFromEndpointName(endpointKey);
        originalEndpoint.getEndpointID().setEndpointChannelName(newName);
    }

    @Deprecated
    protected String removeFunctionNameSuffixFromEndpointName(String endpointKey){
        if (StringUtils.isEmpty(endpointKey)) {
            return(null);
        }
        String newName = null;
        if(endpointKey.contains(PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getFunctionName())){
            newName = endpointKey.replace(PetasosEndpointFunctionTypeEnum.PETASOS_IPC_ENDPOINT.getFunctionName(), "");
        }
        if(endpointKey.contains(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionName())){
            newName = endpointKey.replace(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_DISCOVERY_ENDPOINT.getFunctionName(), "");
        }
        if(endpointKey.contains(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT.getFunctionName())){
            newName = endpointKey.replace(PetasosEndpointFunctionTypeEnum.PETASOS_OAM_PUBSUB_ENDPOINT.getFunctionName(), "");
        }
        if(newName == null){
            newName = endpointKey;
        }
        return(newName);
    }

    protected String addFunctionNameSuffixToEndpointName(String endpointName, PetasosEndpointFunctionTypeEnum functionType){
        String functionInclusiveName = endpointName.replace("(", functionType.getFunctionName()+"(");
        return(functionInclusiveName);
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
        PetasosEndpoint remotePetasosEndpoint = probeEndpoint(endpointID, getPetasosEndpoint());
        PetasosEndpointStatusEnum endpointStatus = null;
        if(remotePetasosEndpoint != null){
            endpointStatus = remotePetasosEndpoint.getEndpointStatus();
        } else {
            endpointStatus = PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_UNREACHABLE;
        }
        return(endpointStatus);
    }

    protected boolean isWithinScopeOfEndpoint(InterSubsystemPubSubParticipant testParticipant) {
        boolean inScope = testParticipant.getEndpointScope().equals(getPetasosEndpoint().getEndpointScope());
        return(inScope);
    }


    protected boolean isWithinScopeOfEndpoint(PetasosEndpointIdentifier endpointID) {
        boolean sameZone = endpointID.getEndpointZone().equals(getPetasosEndpoint().getEndpointID().getEndpointZone());
        boolean sameSite = endpointID.getEndpointSite().contentEquals(getPetasosEndpoint().getEndpointID().getEndpointSite());
        boolean doSubscription = false;
        if (sameSite && sameZone) {
            if (specifyPetasosEndpointScope().equals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTRAZONE)) {
                doSubscription = true;
            }
        }
        if (sameSite && !sameZone) {
            if (specifyPetasosEndpointScope().equals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTERZONE)) {
                doSubscription = true;
            }
        }
        if (!sameSite) {
            if (specifyPetasosEndpointScope().equals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTERSITE)) {
                doSubscription = true;
            }
        }
        return(doSubscription);
    }

    protected boolean isWithinScopeBasedOnChannelName(String endpointChannelName) {
        getLogger().info(".isWithinScopeBasedOnChannelName(): Entry, endpointChannelName->{}", endpointChannelName);
        boolean sameSite = getEndpointNameUtilities().getEndpointSiteFromChannelName(endpointChannelName).contentEquals(getPetasosEndpoint().getEndpointID().getEndpointSite());
        getLogger().info(".isWithinScopeBasedOnChannelName(): Checking to see if other endpoint is in same Site.... result->{}", sameSite);
        boolean sameZone = getEndpointNameUtilities().getEndpointZoneFromChannelName(endpointChannelName).contentEquals(getPetasosEndpoint().getEndpointID().getEndpointZone().getNetworkSecurityZoneCamelCase());
        getLogger().info(".isWithinScopeBasedOnChannelName(): Checking to see if other endpoint is in same Zone.... result->{}", sameZone);
        String otherChannelScope = getEndpointNameUtilities().getEndpointScopeFromChannelName(endpointChannelName);
        getLogger().info(".isWithinScopeBasedOnChannelName(): Checking endpoint channel scope->{}", otherChannelScope);
        switch(specifyPetasosEndpointScope()){
            case ENDPOINT_CHANNEL_SCOPE_INTRAZONE:{
                getLogger().info(".isWithinScopeBasedOnChannelName(): My Scope is -> ENDPOINT_CHANNEL_SCOPE_INTRAZONE");
                if (otherChannelScope.contentEquals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTRAZONE.getEndpointScopeName())) {
                    getLogger().info(".isWithinScopeBasedOnChannelName(): Other endpoint Scope is the same");
                    if(sameSite && sameZone){
                        getLogger().info(".isWithinScopeBasedOnChannelName(): Exit, returning True");
                        return(true);
                    } else {
                        getLogger().info(".isWithinScopeBasedOnChannelName(): Exit, returning False");
                        return(false);
                    }
                }
                break;
            }
            case ENDPOINT_CHANNEL_SCOPE_INTERZONE:{
                getLogger().info(".isWithinScopeBasedOnChannelName(): My Scope is -> ENDPOINT_CHANNEL_SCOPE_INTERZONE");
                if (otherChannelScope.contentEquals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTERZONE.getEndpointScopeName())) {
                    getLogger().info(".isWithinScopeBasedOnChannelName(): Other endpoint Scope is the same");
                    if(sameSite && !sameZone){
                        getLogger().info(".isWithinScopeBasedOnChannelName(): Exit, returning True");
                        return(true);
                    } else {
                        getLogger().info(".isWithinScopeBasedOnChannelName(): Exit, returning False");
                        return(false);
                    }
                }
                break;
            }
            case ENDPOINT_CHANNEL_SCOPE_INTERSITE:{
                getLogger().info(".isWithinScopeBasedOnChannelName(): My Scope is -> ENDPOINT_CHANNEL_SCOPE_INTERSITE");
                if (specifyPetasosEndpointScope().equals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTERSITE)) {
                    getLogger().info(".isWithinScopeBasedOnChannelName(): Other endpoint Scope is the same");
                    if(!sameSite){
                        getLogger().info(".isWithinScopeBasedOnChannelName(): Exit, returning True");
                        return(true);
                    } else {
                        getLogger().info(".isWithinScopeBasedOnChannelName(): Exit, returning False");
                        return(false);
                    }
                }
                break;
            }
        }
        getLogger().info(".isWithinScopeBasedOnChannelName(): Exit, default returning False");
        return(false);
    }
}
