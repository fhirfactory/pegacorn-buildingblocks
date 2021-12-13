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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.tasks;

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskHandlerInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.core.model.capabilities.base.rpc.RemoteProcedureCallRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.rpc.RemoteProcedureCallResponse;
import net.fhirfactory.pegacorn.core.model.capabilities.base.rpc.factories.RemoteProcedureCallRequestFactory;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
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

@ApplicationScoped
public class PetasosTaskServicesEndpoint extends JGroupsPetasosEndpointBase implements PetasosTaskBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosTaskServicesEndpoint.class);

    @Produce
    private ProducerTemplate camelProducer;

    @Inject
    private PetasosTaskHandlerInterface taskManagementHandler;

    @Inject
    private RemoteProcedureCallRequestFactory rpcRequestFactory;

    //
    // Constructor(s)
    //

    public PetasosTaskServicesEndpoint(){
        super();
    }

    //
    // PostConstruct Activities
    //

    @Override
    protected void executePostConstructActivities() {

    }

    //
    // Getters (and Setters)
    //

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    public ProducerTemplate getCamelProducer() {
        return camelProducer;
    }

    //
    // Endpoint Specification
    //

    @Override
    protected String specifyIPCInterfaceName() {
        return (getInterfaceNames().getPetasosTaskServicesEndpointName());
    }

    @Override
    protected PetasosEndpointTopologyTypeEnum specifyIPCType() {
        return (PetasosEndpointTopologyTypeEnum.EDGE_JGROUPS_MESSAGING_SERVICE);
    }

    @Override
    protected String specifyJGroupsStackFileName() {
        return (getProcessingPlantInterface().getProcessingPlantNode().getPetasosTaskingStackConfigFile());
    }

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
        endpointID.setEndpointGroup(getJgroupsParticipantInformationService().getPetasosTaskServicesGroupName());
        endpointID.setEndpointComponentID(getTopologyNode().getComponentID());
        endpointID.setProcessingPlantComponentID(getProcessingPlantInterface().getProcessingPlantNode().getComponentID());
        String endpointAddress = "JGroups:" + endpointChannelName + ":" + getJgroupsParticipantInformationService().getPetasosTaskServicesGroupName();
        endpointID.setEndpointDetailedAddressName(endpointAddress);
        return (endpointID);
    }

    @Override
    protected String specifyEndpointServiceName() {
        return (getProcessingPlantInterface().getIPCServiceName());
    }

    @Override
    protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType() {
        return (PetasosEndpointFunctionTypeEnum.PETASOS_TASKING_ENDPOINT);
    }

    @Override
    protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType() {
        return (EndpointPayloadTypeEnum.ENDPOINT_PAYLOAD_INTERNAL_TASKS);
    }

    @Override
    protected void resolveTopologyEndpoint() {
        setTopologyNode(getJgroupsParticipantInformationService().getMyPetasosTopologyEndpoint());
    }

    @Override
    protected PubSubParticipant specifyPubSubParticipant() {
        PubSubParticipant myInterZoneParticipantRole = getJgroupsParticipantInformationService().getMyPetasosParticipantRole();
        if(myInterZoneParticipantRole == null){
            myInterZoneParticipantRole = getJgroupsParticipantInformationService().buildMyPetasosParticipantRole(getPetasosEndpoint());
        }
        return (myInterZoneParticipantRole);
    }

    @Override
    protected void registerWithCoreSubsystemPetasosEndpointsWatchdog() {
        getCoreSubsystemPetasosEndpointsWatchdog().setPetasosTaskServicesEndpoint(this.getPetasosEndpoint());
    }

    //
    // Business Methods
    //

    public List<Address> getTaskTargetAddressSet(String endpointServiceName){
        getLogger().debug(".getTaskTargetAddressSet(): Entry, endpointServiceName->{}", endpointServiceName);
        List<Address> endpointAddressSet = new ArrayList<>();
        if(StringUtils.isEmpty(endpointServiceName)){
            getLogger().debug(".getTaskTargetAddressSet(): Exit, endpointServiceName is empty");
            return(endpointAddressSet);
        }
        List<PetasosAdapterAddress> memberAdapterSetForService = getTargetMemberAdapterSetForService(endpointServiceName);
        for(PetasosAdapterAddress currentMember: memberAdapterSetForService){
            Address currentMemberAddress = currentMember.getJGroupsAddress();
            if(currentMemberAddress != null){
                endpointAddressSet.add(currentMemberAddress);
            }
        }
        getLogger().debug(".getTaskTargetAddressSet(): Exit, endpointAddressSet->{}", endpointAddressSet);
        return(endpointAddressSet);
    }

    public Address getCandidateTaskTargetAddress(String endpointServiceName){
        getLogger().debug(".getCandidateTaskTargetAddress(): Entry, endpointServiceName->{}", endpointServiceName);
        if(StringUtils.isEmpty(endpointServiceName)){
            getLogger().debug(".getCandidateTaskTargetAddress(): Exit, endpointServiceName is empty");
            return(null);
        }
        List<Address> endpointAddressSet = getTaskTargetAddressSet(endpointServiceName);
        if(endpointAddressSet.isEmpty()){
            getLogger().debug(".getCandidateTaskTargetAddress(): Exit, endpointAddressSet is empty");
            return(null);
        }
        Address endpointJGroupsAddress = endpointAddressSet.get(0);
        getLogger().debug(".getCandidateTaskTargetAddress(): Exit, selected address->{}", endpointJGroupsAddress);
        return(endpointJGroupsAddress);
    }

    //
    // ****Tactical****
    // Task Execution / Capability Utilisation Services
    //

    public CapabilityUtilisationResponse executeTask(String capabilityProviderName, CapabilityUtilisationRequest task){
        getLogger().trace(".executeTask(): Entry, capabilityProviderName->{}, task->{}", capabilityProviderName, task);
        Address targetAddress = getCandidateTaskTargetAddress(capabilityProviderName);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = task;
            classSet[0] = CapabilityUtilisationRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            CapabilityUtilisationResponse response = getRPCDispatcher().callRemoteMethod(targetAddress, "executeTaskHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".executeTask(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".executeTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            CapabilityUtilisationResponse response = new CapabilityUtilisationResponse();
            response.setAssociatedRequestID(task.getRequestID());
            response.setSuccessful(false);
            return(response);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".executeTask: Error (GeneralException) ->{}", e.getMessage());
            CapabilityUtilisationResponse response = new CapabilityUtilisationResponse();
            response.setAssociatedRequestID(task.getRequestID());
            response.setSuccessful(false);
            return(response);
        }
    }

    //
    // PetasosActionableTask Activities
    //

    //
    // Register a PetasosActionableTask
    public PetasosActionableTask registerActionableTask(String taskFulfiller, PetasosActionableTask actionableTask){
        getLogger().trace(".registerActionableTask(): Entry, taskFulfiller->{}, task->{}", taskFulfiller, actionableTask);
        PetasosEndpointIdentifier endpointIdentifier = getEndpointID();
        Address targetAddress = getCandidateTaskTargetAddress(taskFulfiller);
        RemoteProcedureCallRequest remoteProcedureCallRequest = rpcRequestFactory.newRemoteProcedureCallRequest(actionableTask, PetasosActionableTask.class, endpointIdentifier);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = remoteProcedureCallRequest;
            classSet[0] = RemoteProcedureCallRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            RemoteProcedureCallResponse response = getRPCDispatcher().callRemoteMethod(targetAddress, "registerActionableTaskHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".registerActionableTask(): Exit, response->{}", response);
            if(response.isSuccessful()){
                PetasosActionableTask registeredTask = (PetasosActionableTask) response.getResponseContent();
                return(registeredTask);
            } else {
                getLogger().error(".registerActionableTask(): Could not register task, response->{}", response);
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getLogger().error(".registerActionableTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".registerActionableTask: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    public RemoteProcedureCallResponse registerActionableTaskHandler(RemoteProcedureCallRequest rpcRequest){
        getLogger().trace(".registerActionableTaskHandler(): Entry, rpcRequest->{}", rpcRequest);
        PetasosActionableTask taskToRegister = null;
        PetasosEndpointIdentifier endpointIdentifier = null;
        if(rpcRequest != null){
            if(rpcRequest.hasRequestContent()){
                if(rpcRequest.hasRequestContentType()){
                    if(rpcRequest.getRequestContentType().equals(PetasosActionableTask.class)){
                        taskToRegister = (PetasosActionableTask) rpcRequest.getRequestContent();
                    }
                }
            }
            if(rpcRequest.hasRequestingEndpoint()){
                endpointIdentifier = rpcRequest.getRequestingEndpoint();
            }
        }
        PetasosActionableTask registeredTask = null;
        if((taskToRegister != null) && (endpointIdentifier != null)) {
            registeredTask = taskManagementHandler.registerActionableTask(taskToRegister, endpointIdentifier);
        }
        RemoteProcedureCallResponse rpcResponse = new RemoteProcedureCallResponse();
        rpcResponse.setAssociatedRequestID(rpcRequest.getRequestID());
        rpcResponse.setInScope(true);
        rpcResponse.setInstantCompleted(Instant.now());
        if(registeredTask != null){
            rpcResponse.setResponseContent(registeredTask);
            rpcResponse.setResponseContentType(PetasosActionableTask.class);
            rpcResponse.setSuccessful(true);
        } else {
            rpcResponse.setSuccessful(false);
        }
        getLogger().debug(".registerActionableTaskHandler(): Exit, rpcResponse->{}", rpcResponse);
        return(rpcResponse);
    }

    //
    // Execute/Fulfill A PetasosActionableTask

    public PetasosActionableTask fulfillActionableTask(String taskFulfiller, PetasosActionableTask actionableTask){
        getLogger().trace(".fulfillActionableTask(): Entry, taskFulfiller->{}, task->{}", taskFulfiller, actionableTask);
        PetasosEndpointIdentifier endpointIdentifier = getEndpointID();
        Address targetAddress = getCandidateTaskTargetAddress(taskFulfiller);
        RemoteProcedureCallRequest remoteProcedureCallRequest = rpcRequestFactory.newRemoteProcedureCallRequest(actionableTask, PetasosActionableTask.class, endpointIdentifier);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = remoteProcedureCallRequest;
            classSet[0] = RemoteProcedureCallRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            RemoteProcedureCallResponse response = getRPCDispatcher().callRemoteMethod(targetAddress, "fulfillActionableTaskHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".fulfillActionableTask(): Exit, response->{}", response);
            if(response.isSuccessful()){
                PetasosActionableTask registeredTask = (PetasosActionableTask) response.getResponseContent();
                return(registeredTask);
            } else {
                getLogger().error(".fulfillActionableTask(): Could not register task, response->{}", response);
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getLogger().error(".fulfillActionableTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".fulfillActionableTask: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    public RemoteProcedureCallResponse fulfillActionableTaskHandler(RemoteProcedureCallRequest rpcRequest){
        getLogger().debug(".fulfillActionableTaskHandler(): Entry, rpcRequest->{}", rpcRequest);
        PetasosActionableTask taskToAction = null;
        PetasosEndpointIdentifier endpointIdentifier = null;
        if(rpcRequest != null){
            if(rpcRequest.hasRequestContent()){
                if(rpcRequest.hasRequestContentType()){
                    if(rpcRequest.getRequestContentType().equals(PetasosActionableTask.class)){
                        taskToAction = (PetasosActionableTask) rpcRequest.getRequestContent();
                    }
                }
            }
            if(rpcRequest.hasRequestingEndpoint()){
                endpointIdentifier = rpcRequest.getRequestingEndpoint();
            }
        }
        PetasosActionableTask updatedTask = null;
        if((taskToAction != null) && (endpointIdentifier != null)) {
            updatedTask = taskManagementHandler.fulfillActionableTask(taskToAction, endpointIdentifier);
        }
        RemoteProcedureCallResponse rpcResponse = new RemoteProcedureCallResponse();
        rpcResponse.setAssociatedRequestID(rpcRequest.getRequestID());
        rpcResponse.setInScope(true);
        rpcResponse.setInstantCompleted(Instant.now());
        if(updatedTask != null){
            rpcResponse.setResponseContent(updatedTask);
            rpcResponse.setResponseContentType(PetasosActionableTask.class);
            rpcResponse.setSuccessful(true);
        } else {
            rpcResponse.setSuccessful(false);
        }
        getLogger().debug(".fulfillActionableTaskHandler(): Exit, rpcResponse->{}", rpcResponse);
        return(rpcResponse);
    }

    //
    // Update a PetasosActionableTask

    public PetasosActionableTask updateActionableTask(String taskFulfiller, PetasosActionableTask actionableTask){
        getLogger().trace(".updateActionableTask(): Entry, taskFulfiller->{}, task->{}", taskFulfiller, actionableTask);
        PetasosEndpointIdentifier endpointIdentifier = getEndpointID();
        Address targetAddress = getCandidateTaskTargetAddress(taskFulfiller);
        RemoteProcedureCallRequest remoteProcedureCallRequest = rpcRequestFactory.newRemoteProcedureCallRequest(actionableTask, PetasosActionableTask.class, endpointIdentifier);

        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = remoteProcedureCallRequest;
            classSet[0] = RemoteProcedureCallRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            RemoteProcedureCallResponse response = getRPCDispatcher().callRemoteMethod(targetAddress, "updateActionableTaskHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".updateActionableTask(): Exit, response->{}", response);
            if(response.isSuccessful()){
                PetasosActionableTask registeredTask = (PetasosActionableTask) response.getResponseContent();
                return(registeredTask);
            } else {
                getLogger().error(".updateActionableTask(): Could not register task, response->{}", response);
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getLogger().error(".updateActionableTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".updateActionableTask: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    public RemoteProcedureCallResponse updateActionableTaskHandler(RemoteProcedureCallRequest rpcRequest ){
        getLogger().debug(".updateActionableTaskHandler(): Entry, rpcRequest->{}", rpcRequest);
        PetasosActionableTask taskToRegister = null;
        PetasosEndpointIdentifier endpointIdentifier = null;
        if(rpcRequest != null){
            if(rpcRequest.hasRequestContent()){
                if(rpcRequest.hasRequestContentType()){
                    if(rpcRequest.getRequestContentType().equals(PetasosActionableTask.class)){
                        taskToRegister = (PetasosActionableTask) rpcRequest.getRequestContent();
                    }
                }
            }
            if(rpcRequest.hasRequestingEndpoint()){
                endpointIdentifier = rpcRequest.getRequestingEndpoint();
            }
        }
        PetasosActionableTask updatedTask = null;
        if((taskToRegister != null) && (endpointIdentifier != null)) {
            updatedTask = taskManagementHandler.updateActionableTask(taskToRegister, endpointIdentifier);
        }
        RemoteProcedureCallResponse rpcResponse = new RemoteProcedureCallResponse();
        rpcResponse.setAssociatedRequestID(rpcRequest.getRequestID());
        rpcResponse.setInScope(true);
        rpcResponse.setInstantCompleted(Instant.now());
        if(updatedTask != null){
            rpcResponse.setResponseContent(updatedTask);
            rpcResponse.setResponseContentType(PetasosActionableTask.class);
            rpcResponse.setSuccessful(true);
        } else {
            rpcResponse.setSuccessful(false);
        }
        getLogger().debug(".updateActionableTaskHandler(): Exit, rpcResponse->{}", rpcResponse);
        return(rpcResponse);
    }

}
