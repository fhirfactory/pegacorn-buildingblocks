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
package net.fhirfactory.pegacorn.petasos.endpoints.services.tasking;

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskDataGridInterface;
import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskHandlerInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.core.model.capabilities.base.rpc.RemoteProcedureCallRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.rpc.RemoteProcedureCallResponse;
import net.fhirfactory.pegacorn.core.model.capabilities.base.rpc.factories.RemoteProcedureCallRequestFactory;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.deployment.names.subsystems.SubsystemNames;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.JGroupsIntegrationPointBase;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Map;

@ApplicationScoped
public class PetasosTaskServicesEndpoint extends JGroupsIntegrationPointBase implements PetasosTaskDataGridInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosTaskServicesEndpoint.class);

    @Produce
    private ProducerTemplate camelProducer;

    @Inject
    private PetasosTaskHandlerInterface taskManagementHandler;

    @Inject
    private RemoteProcedureCallRequestFactory rpcRequestFactory;

    @Inject
    private SubsystemNames subsystemNames;


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
        return (PetasosEndpointTopologyTypeEnum.JGROUPS_INTEGRATION_POINT);
    }

    @Override
    protected String specifyJGroupsStackFileName() {
        return (getProcessingPlant().getMeAsASoftwareComponent().getPetasosTaskingStackConfigFile());
    }

    @Override
    protected String specifySubsystemParticipantName() {
        return (getProcessingPlant().getSubsystemParticipantName());
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
    protected String specifyJGroupsClusterName() {
        return (getComponentNameUtilities().getPetasosTaskServicesGroupName());
    }

    @Override
    protected void addIntegrationPointToJGroupsIntegrationPointSet() {
        getJgroupsIPSet().setPetasosTaskServicesEndpoint(getJGroupsIntegrationPoint());
    }

    //
    // Processing Plant check triggered by JGroups Cluster membership change
    //

    @Override
    protected void doIntegrationPointBusinessFunctionCheck(JGroupsIntegrationPointSummary integrationPointSummary, boolean isRemoved, boolean isAdded) {

    }

    //
    // ****Tactical****
    // Task Execution / Capability Utilisation Services
    //

    public CapabilityUtilisationResponse executeTask(String capabilityProviderName, CapabilityUtilisationRequest task){
        getLogger().trace(".executeTask(): Entry, capabilityProviderName->{}, task->{}", capabilityProviderName, task);
        Address targetAddress = getCandidateTargetServiceAddress(capabilityProviderName);
        if(targetAddress == null){
            getLogger().error(".executeTask(): Cannot find candidate service address: capabilityProviderName->{}, task->{}", capabilityProviderName, task);
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate " + capabilityProviderName);
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate " + capabilityProviderName);
            return(null);
        }
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = task;
            classSet[0] = CapabilityUtilisationRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            CapabilityUtilisationResponse response = null;
            synchronized (getIPCChannelLock()) {
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "executeTaskHandler", objectSet, classSet, requestOptions);
            }
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".executeTask(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".executeTask: Error (GeneralException) Message->{}, targetAddress->{}, StackTrace->{}", ExceptionUtils.getMessage(e), targetAddress, ExceptionUtils.getStackTrace(e));
            CapabilityUtilisationResponse response = new CapabilityUtilisationResponse();
            response.setAssociatedRequestID(task.getRequestID());
            response.setSuccessful(false);
            return(response);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".executeTask: Error (GeneralException) Message->{}, targetAddress->{}, StackTrace->{}", ExceptionUtils.getMessage(e), targetAddress, ExceptionUtils.getStackTrace(e));
            CapabilityUtilisationResponse response = new CapabilityUtilisationResponse();
            response.setAssociatedRequestID(task.getRequestID());
            response.setSuccessful(false);
            return(response);
        }
    }
    
    public CapabilityUtilisationResponse executeTaskHandler(CapabilityUtilisationRequest task){
        getLogger().debug(".executeTaskHandler(): Entry, task->{}", task);
        CapabilityUtilisationResponse response = getProcessingPlant().executeTask(task);
        getLogger().debug(".executeTaskHandler(): Exit, response->{}", response);
        return(response);
    }

    //
    // PetasosActionableTask Activities
    //

    //
    // Register a PetasosActionableTask
    @Override
    public PetasosActionableTask queueTask(PetasosActionableTask actionableTask){
        getLogger().debug(".registerActionableTask(): Entry, task->{}", actionableTask);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".registerActionableTask(): Cannot Access {} to update task",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".registerActionableTask(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            return(null);
        }
        RemoteProcedureCallRequest remoteProcedureCallRequest = rpcRequestFactory.newRemoteProcedureCallRequest(actionableTask, PetasosActionableTask.class, jgroupsIPSummary);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = remoteProcedureCallRequest;
            classSet[0] = RemoteProcedureCallRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            RemoteProcedureCallResponse response = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "registerActionableTaskHandler", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".registerActionableTask(): Exit, response->{}", response);
            if(response.isSuccessful()){
                PetasosActionableTask registeredTask = (PetasosActionableTask) response.getResponseContent();
                return(registeredTask);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".registerActionableTask(): Could not register task, response->{}", response);
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".registerActionableTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".registerActionableTask: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    @Override
    public PetasosActionableTask retrieveNextPendingActionableTask(String performerName) {
        return null;
    }

    public RemoteProcedureCallResponse registerActionableTaskHandler(RemoteProcedureCallRequest rpcRequest){
        getLogger().debug(".registerActionableTaskHandler(): Entry, rpcRequest->{}", rpcRequest);
        PetasosActionableTask taskToRegister = null;
        getMetricsAgent().incrementRemoteProcedureCallHandledCount();
        JGroupsIntegrationPointSummary endpointIdentifier = null;
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

    @Override
    public PetasosActionableTask fulfillActionableTask(PetasosActionableTask actionableTask){
        getLogger().trace(".fulfillActionableTask(): Entry, task->{}", actionableTask);
        JGroupsIntegrationPointSummary endpointIdentifier = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".fulfillActionableTask(): Cannot Access {} to update task",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".fulfillActionableTask(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.fulfillActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.fulfillActionableTask())!!!");
            return(null);
        }
        RemoteProcedureCallRequest remoteProcedureCallRequest = rpcRequestFactory.newRemoteProcedureCallRequest(actionableTask, PetasosActionableTask.class, endpointIdentifier);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = remoteProcedureCallRequest;
            classSet[0] = RemoteProcedureCallRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            RemoteProcedureCallResponse response = null;
            synchronized(getIPCChannelLock()) {
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "fulfillActionableTaskHandler", objectSet, classSet, requestOptions);
            }
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
        JGroupsIntegrationPointSummary endpointIdentifier = null;
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

    @Override
    public PetasosActionableTask updateActionableTask( PetasosActionableTask actionableTask){
        getLogger().debug(".updateActionableTask(): Entry, task->{}",  actionableTask);
        JGroupsIntegrationPointSummary endpointIdentifier = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".updateActionableTask(): Cannot Access {} to update task",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".updateActionableTask(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance!!!");
            return(null);
        }
        RemoteProcedureCallRequest remoteProcedureCallRequest = null;
        try {
            remoteProcedureCallRequest = rpcRequestFactory.newRemoteProcedureCallRequest(actionableTask, PetasosActionableTask.class, endpointIdentifier);
        } catch(Exception ex){
            getLogger().warn(".updateActionableTask(): Warning: Cannot formulate Ponos-IM RPC Request (for updateActionableTask)!!!",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getMetricsAgent().sendITOpsNotification("Warning: Cannot formulate Ponos-IM RPC Request (for updateActionableTask)!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Warning: Cannot formulate Ponos-IM RPC Request (for updateActionableTask)!!!");
            return(null);
        }

        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = remoteProcedureCallRequest;
            classSet[0] = RemoteProcedureCallRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            RemoteProcedureCallResponse response = null;
            synchronized (getIPCChannelLock()) {
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "updateActionableTaskHandler", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".updateActionableTask(): Exit, response->{}", response);
            if(response.isSuccessful()){
                PetasosActionableTask registeredTask = (PetasosActionableTask) response.getResponseContent();
                return(registeredTask);
            } else {
                getLogger().error(".updateActionableTask(): Could not update task, response->{}", response);
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
        JGroupsIntegrationPointSummary endpointIdentifier = null;
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

    @Override
    public Map<Integer, PetasosActionableTask> retrievePendingActionableTasks(String participantName) {
        return null;
    }
}
