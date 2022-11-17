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

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskDataGridClientInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.core.model.capabilities.base.rpc.RemoteProcedureCallRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.rpc.RemoteProcedureCallResponse;
import net.fhirfactory.pegacorn.core.model.capabilities.base.rpc.factories.RemoteProcedureCallRequestFactory;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.completion.datatypes.TaskCompletionSummaryType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.datatypes.TaskExecutionControl;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.datatypes.TaskOutcomeStatusType;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayloadSet;
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

@ApplicationScoped
public class PetasosTaskGridClient extends JGroupsIntegrationPointBase implements PetasosTaskDataGridClientInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosTaskGridClient.class);

    @Produce
    private ProducerTemplate camelProducer;

    @Inject
    private RemoteProcedureCallRequestFactory rpcRequestFactory;

    @Inject
    private SubsystemNames subsystemNames;


    //
    // Constructor(s)
    //

    public PetasosTaskGridClient(){
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
        return (PetasosEndpointFunctionTypeEnum.PETASOS_TASK_DISTRIBUTION_GRID_CLIENT_ENDPOINT);
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
    // Task DataGrid Services
    //

    @Override
    public TaskIdType queueTask(PetasosActionableTask actionableTask){
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
        try {
            Object objectSet[] = new Object[]{actionableTask, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            RemoteProcedureCallResponse response = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "queueTask", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".registerActionableTask(): Exit, response->{}", response);
            if(response.isSuccessful()){
                PetasosActionableTask registeredTask = (PetasosActionableTask) response.getResponseContent();
                return(registeredTask.getTaskId());
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
    public PetasosActionableTask registerExternallyTriggeredTask(String participantName, PetasosActionableTask actionableTask) {
        getLogger().debug(".registerExternallyTriggeredTask(): Entry, actionableTask->{}", actionableTask);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".registerExternallyTriggeredTask(): Cannot Access {} to update task",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".registerExternallyTriggeredTask(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, actionableTask, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosActionableTask registeredActionableTask = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                registeredActionableTask = getRPCDispatcher().callRemoteMethod(targetAddress, "registerExternallyTriggeredTask", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".registerExternallyTriggeredTask(): Exit, response->{}", actionableTask);
            if(registeredActionableTask != null){
                return(registeredActionableTask);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".registerExternallyTriggeredTask(): Could not register task, response->{}", actionableTask);
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".registerExternallyTriggeredTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".registerExternallyTriggeredTask: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    //
    // Update a PetasosActionableTask


    @Override
    public PetasosActionableTask getNextPendingTask(String participantName) {
        getLogger().debug(".getNextPendingTask(): Entry, participantName->{}", participantName);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".getNextPendingTask(): Cannot Access {} to update task",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".getNextPendingTask(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosActionableTask actionableTask = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                actionableTask = getRPCDispatcher().callRemoteMethod(targetAddress, "getNextPendingTask", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".notifyTaskStart(): Exit, response->{}", actionableTask);
            if(actionableTask != null){
                return(actionableTask);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".notifyTaskStart(): Could not update task, response is null");
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".notifyTaskStart(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".notifyTaskStart: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    @Override
    public TaskExecutionControl notifyTaskStart(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail) {
        getLogger().debug(".notifyTaskStart(): Entry, taskId->{}", taskId);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".notifyTaskStart(): Cannot Access {} to update task",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".notifyTaskStart(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, taskId, taskFulfillmentDetail, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskExecutionControl taskExecutionControl = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                taskExecutionControl = getRPCDispatcher().callRemoteMethod(targetAddress, "notifyTaskStart", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".notifyTaskStart(): Exit, response->{}", taskExecutionControl);
            if(taskExecutionControl != null){
                return(taskExecutionControl);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".notifyTaskStart(): Could not update task, response->{}", taskId);
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".notifyTaskStart(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".notifyTaskStart: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    @Override
    public TaskExecutionControl notifyTaskFinish(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail, UoWPayloadSet egressPayload, TaskOutcomeStatusType taskOutcome, String taskStatusReason) {
        getLogger().debug(".notifyTaskFinish(): Entry, taskId->{}", taskId);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".notifyTaskFinish(): Cannot Access {} to update task",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".notifyTaskFinish(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, taskId, taskFulfillmentDetail, egressPayload, taskOutcome, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskExecutionControl taskExecutionControl = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                taskExecutionControl = getRPCDispatcher().callRemoteMethod(targetAddress, "notifyTaskFinish", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".notifyTaskFinish(): Exit, response->{}", taskExecutionControl);
            if(taskExecutionControl != null){
                return(taskExecutionControl);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".notifyTaskFinish(): Could not update task, response->{}", taskId);
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".notifyTaskFinish(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".notifyTaskFinish: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    @Override
    public TaskExecutionControl notifyTaskCancellation(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail, UoWPayloadSet egressPayload, TaskOutcomeStatusType taskOutcome, String taskStatusReason) {
        getLogger().debug(".notifyTaskCancellation(): Entry, taskId->{}", taskId);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".notifyTaskCancellation(): Cannot Access {} to update task",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".notifyTaskCancellation(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, taskId, taskFulfillmentDetail, egressPayload, taskOutcome, taskStatusReason, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskExecutionControl taskExecutionControl = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                taskExecutionControl = getRPCDispatcher().callRemoteMethod(targetAddress, "notifyTaskCancellation", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".notifyTaskCancellation(): Exit, response->{}", taskExecutionControl);
            if(taskExecutionControl != null){
                return(taskExecutionControl);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".notifyTaskCancellation(): Could not update task, response->{}", taskId);
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".notifyTaskCancellation(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".notifyTaskCancellation: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    @Override
    public TaskExecutionControl notifyTaskFailure(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail, UoWPayloadSet egressPayload, TaskOutcomeStatusType taskOutcome, String taskStatusReason) {
        getLogger().debug(".notifyTaskFailure(): Entry, taskId->{}", taskId);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".notifyTaskFailure(): Cannot Access {} to update task",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".notifyTaskFailure(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, taskId, taskFulfillmentDetail, egressPayload, taskOutcome, taskStatusReason, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskExecutionControl taskExecutionControl = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                taskExecutionControl = getRPCDispatcher().callRemoteMethod(targetAddress, "notifyTaskFailure", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".notifyTaskFailure(): Exit, response->{}", taskExecutionControl);
            if(taskExecutionControl != null){
                return(taskExecutionControl);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".notifyTaskFailure(): Could not update task, response->{}", taskId);
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".notifyTaskFailure(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".notifyTaskFailure: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    @Override
    public TaskExecutionControl notifyTaskFinalisation(String participantName, TaskIdType taskId, TaskCompletionSummaryType completionSummary) {
        getLogger().debug(".notifyTaskFinalisation(): Entry, taskId->{}", taskId);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".notifyTaskFinalisation(): Cannot Access {} to update task",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".notifyTaskFinalisation(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, taskId, completionSummary, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskExecutionControl taskExecutionControl = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                taskExecutionControl = getRPCDispatcher().callRemoteMethod(targetAddress, "notifyTaskFinalisation", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".notifyTaskFinalisation(): Exit, response->{}", taskExecutionControl);
            if(taskExecutionControl != null){
                return(taskExecutionControl);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".notifyTaskFinalisation(): Could not update task, response->{}", taskId);
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".notifyTaskFinalisation(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".notifyTaskFinalisation: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
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
            Object objectSet[] = new Object[]{task};
            Class classSet[] = createClassSet(objectSet);
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

}
