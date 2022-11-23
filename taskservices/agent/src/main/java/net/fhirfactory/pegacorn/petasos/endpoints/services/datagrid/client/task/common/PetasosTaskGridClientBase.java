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
package net.fhirfactory.pegacorn.petasos.endpoints.services.datagrid.client.task.common;

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
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

public abstract class PetasosTaskGridClientBase extends JGroupsIntegrationPointBase {

    private boolean activityActive;
    private Instant activityStartTime;

    @Inject
    private SubsystemNames subsystemNames;


    //
    // Constructor(s)
    //

    public PetasosTaskGridClientBase() {
        super();
    }

    //
    // PostConstruct Activities
    //

    @Override
    protected void executePostConstructActivities() {

    }

    //
    // Server Address
    //

    protected String deriveServerName(){
        if(specifyPetasosEndpointFunctionType().equals(PetasosEndpointFunctionTypeEnum.PETASOS_TASK_DISTRIBUTION_GRID_CLIENT_ENDPOINT_UNO)){
            return(PetasosEndpointFunctionTypeEnum.PETASOS_TASK_DISTRIBUTION_GRID_ENDPOINT_ONE.getEndpointParticipantName());
        } else {
            return(PetasosEndpointFunctionTypeEnum.PETASOS_TASK_DISTRIBUTION_GRID_ENDPOINT_TWO.getEndpointParticipantName());
        }
    }

    protected Address getPonosTaskGridServerAddress() {
        getLogger().debug(".getPonosTaskGridServerAddress(): Entry");
        String targetServiceName = subsystemNames.getPonosManagerParticipantName();
        if(getIPCChannel() == null){
            getLogger().debug(".getPonosTaskGridServerAddress(): IPCChannel is null, exit returning (null)");
            return(null);
        }
        getLogger().trace(".getPonosTaskGridServerAddress(): IPCChannel is NOT null, get updated Address set via view");
        List<Address> addressList = getAllViewMembers();
        Address foundAddress = null;
        synchronized (getCurrentScannedMembershipLock()) {
            getLogger().debug(".getPonosTaskGridServerAddress(): Got the Address set via view, now iterate through and see if one is suitable");
            for (Address currentAddress : addressList) {
                getLogger().debug(".getPonosTaskGridServerAddress(): Iterating through Address list, current element->{}", currentAddress);
                String addressNameAsString = currentAddress.toString();
                String currentParticipantEndpointName = deriveIntegrationPointSubsystemName(addressNameAsString);
                if (currentParticipantEndpointName.contentEquals(targetServiceName)) {
                    getLogger().debug(".getPonosTaskGridServerAddress(): Exit, A match!");
                    if(addressNameAsString.contains(PetasosEndpointFunctionTypeEnum.PETASOS_TASK_DISTRIBUTION_GRID_ENDPOINT_ONE.getDisplayName()) || currentParticipantEndpointName.contains(PetasosEndpointFunctionTypeEnum.PETASOS_TASK_DISTRIBUTION_GRID_ENDPOINT_ONE.getDisplayName())) {
                        foundAddress = currentAddress;
                        break;
                    }
                }
            }
        }
        getLogger().debug(".getPonosTaskGridServerAddress(): Exit, foundAddress->{}",foundAddress );
        return(foundAddress);
    }

    //
    // Getters (and Setters)
    //


    public boolean isActivityActive() {
        return activityActive;
    }

    public void setActivityActive(boolean activityActive) {
        this.activityActive = activityActive;
    }

    public Instant getActivityStartTime() {
        return activityStartTime;
    }

    public void setActivityStartTime(Instant activityStartTime) {
        this.activityStartTime = activityStartTime;
    }

    protected void amBusy() {
        setActivityActive(true);
        setActivityStartTime(Instant.now());
    }

    protected void amNotBusy() {
        setActivityActive(false);
    }

    //
    // Endpoint Specification
    //

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

    public TaskIdType queueTask(PetasosActionableTask actionableTask) {
        amBusy();
        getLogger().debug(".queueTask(): Entry, task->{}", actionableTask);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getPonosTaskGridServerAddress();
        if (targetAddress == null) {
            getLogger().warn(".queueTask(): Cannot Access {} to update task", subsystemNames.getPetasosTaskRepositoryServiceProviderName());
            getLogger().error(".queueTask(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            amNotBusy();
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{actionableTask, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions(ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskIdType taskId = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                taskId = getRPCDispatcher().callRemoteMethod(targetAddress, "queueTask", objectSet, classSet, requestOptions);
            }
            if (taskId != null) {
                getLogger().debug(".queueTask(): Exit, taskId->{}", taskId);
                amNotBusy();
                return (taskId);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".queueTask(): Could not register task, taskId->{}", taskId);
                amNotBusy();
                return (null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".queueTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".queueTask: Error (GeneralException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        }
    }

    public PetasosActionableTask registerExternallyTriggeredTask(String participantName, PetasosActionableTask actionableTask) {
        amBusy();
        getLogger().debug(".registerExternallyTriggeredTask(): Entry, actionableTask->{}", actionableTask);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getPonosTaskGridServerAddress();
        if (targetAddress == null) {
            getLogger().warn(".registerExternallyTriggeredTask(): Cannot Access {} to update task", subsystemNames.getPetasosTaskRepositoryServiceProviderName());
            getLogger().error(".registerExternallyTriggeredTask(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            amNotBusy();
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, actionableTask, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions(ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosActionableTask registeredActionableTask = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                registeredActionableTask = getRPCDispatcher().callRemoteMethod(targetAddress, "registerExternallyTriggeredTask", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".registerExternallyTriggeredTask(): Exit, response->{}", actionableTask);
            if (registeredActionableTask != null) {
                return (registeredActionableTask);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".registerExternallyTriggeredTask(): Could not register task, response->{}", actionableTask);
                amNotBusy();
                return (null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".registerExternallyTriggeredTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".registerExternallyTriggeredTask: Error (GeneralException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        }
    }

    //
    // Update a PetasosActionableTask


    public PetasosActionableTask getNextPendingTask(String participantName) {
        amBusy();
        getLogger().debug(".getNextPendingTask(): Entry, participantName->{}", participantName);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getPonosTaskGridServerAddress();
        if (targetAddress == null) {
            getLogger().warn(".getNextPendingTask(): Cannot Access {} to update task", subsystemNames.getPetasosTaskRepositoryServiceProviderName());
            getLogger().error(".getNextPendingTask(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            amNotBusy();
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions(ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosActionableTask actionableTask = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                actionableTask = getRPCDispatcher().callRemoteMethod(targetAddress, "getNextPendingTask", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".getNextPendingTask(): Exit, response->{}", actionableTask);
            if (actionableTask != null) {
                return (actionableTask);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().info(".getNextPendingTask(): no task available");
                amNotBusy();
                return (null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".getNextPendingTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".getNextPendingTask: Error (GeneralException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        }
    }

    public TaskExecutionControl notifyTaskStart(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail) {
        amBusy();
        getLogger().debug(".notifyTaskStart(): Entry, taskId->{}", taskId);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getPonosTaskGridServerAddress();
        if (targetAddress == null) {
            getLogger().warn(".notifyTaskStart(): Cannot Access {} to update task", subsystemNames.getPetasosTaskRepositoryServiceProviderName());
            getLogger().error(".notifyTaskStart(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            amNotBusy();
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, taskId, taskFulfillmentDetail, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions(ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskExecutionControl taskExecutionControl = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                taskExecutionControl = getRPCDispatcher().callRemoteMethod(targetAddress, "notifyTaskStart", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".notifyTaskStart(): Exit, response->{}", taskExecutionControl);
            if (taskExecutionControl != null) {
                amNotBusy();
                return (taskExecutionControl);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".notifyTaskStart(): Could not update task, response->{}", taskId);
                amNotBusy();
                return (null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".notifyTaskStart(): Error (NoSuchMethodException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".notifyTaskStart: Error (GeneralException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        }
    }

    public TaskExecutionControl notifyTaskFinish(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail, UoWPayloadSet egressPayload, TaskOutcomeStatusType taskOutcome, String taskStatusReason) {
        amBusy();
        getLogger().debug(".notifyTaskFinish(): Entry, taskId->{}", taskId);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getPonosTaskGridServerAddress();
        if (targetAddress == null) {
            getLogger().warn(".notifyTaskFinish(): Cannot Access {} to update task", subsystemNames.getPetasosTaskRepositoryServiceProviderName());
            getLogger().error(".notifyTaskFinish(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            amNotBusy();
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, taskId, taskFulfillmentDetail, egressPayload, taskOutcome, taskStatusReason, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions(ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskExecutionControl taskExecutionControl = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                taskExecutionControl = getRPCDispatcher().callRemoteMethod(targetAddress, "notifyTaskFinish", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".notifyTaskFinish(): Exit, taskExecutionControl->{}", taskExecutionControl);
            if (taskExecutionControl != null) {
                amNotBusy();
                return (taskExecutionControl);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".notifyTaskFinish(): Could not update task, response->{}", taskId);
                amNotBusy();
                return (null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".notifyTaskFinish(): Error (NoSuchMethodException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".notifyTaskFinish: Error (GeneralException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        }
    }

    public TaskExecutionControl notifyTaskCancellation(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail, UoWPayloadSet egressPayload, TaskOutcomeStatusType taskOutcome, String taskStatusReason) {
        amBusy();
        getLogger().debug(".notifyTaskCancellation(): Entry, taskId->{}", taskId);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getPonosTaskGridServerAddress();
        if (targetAddress == null) {
            getLogger().warn(".notifyTaskCancellation(): Cannot Access {} to update task", subsystemNames.getPetasosTaskRepositoryServiceProviderName());
            getLogger().error(".notifyTaskCancellation(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            amNotBusy();
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, taskId, taskFulfillmentDetail, egressPayload, taskOutcome, taskStatusReason, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions(ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskExecutionControl taskExecutionControl = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                taskExecutionControl = getRPCDispatcher().callRemoteMethod(targetAddress, "notifyTaskCancellation", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".notifyTaskCancellation(): Exit, response->{}", taskExecutionControl);
            if (taskExecutionControl != null) {
                amNotBusy();
                return (taskExecutionControl);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".notifyTaskCancellation(): Could not update task, response->{}", taskId);
                amNotBusy();
                return (null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".notifyTaskCancellation(): Error (NoSuchMethodException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".notifyTaskCancellation: Error (GeneralException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        }
    }

    public TaskExecutionControl notifyTaskFailure(String participantName, TaskIdType taskId, TaskFulfillmentType taskFulfillmentDetail, UoWPayloadSet egressPayload, TaskOutcomeStatusType taskOutcome, String taskStatusReason) {
        amBusy();
        getLogger().debug(".notifyTaskFailure(): Entry, taskId->{}", taskId);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getPonosTaskGridServerAddress();
        if (targetAddress == null) {
            getLogger().warn(".notifyTaskFailure(): Cannot Access {} to update task", subsystemNames.getPetasosTaskRepositoryServiceProviderName());
            getLogger().error(".notifyTaskFailure(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            amNotBusy();
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, taskId, taskFulfillmentDetail, egressPayload, taskOutcome, taskStatusReason, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions(ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskExecutionControl taskExecutionControl = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                taskExecutionControl = getRPCDispatcher().callRemoteMethod(targetAddress, "notifyTaskFailure", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".notifyTaskFailure(): Exit, response->{}", taskExecutionControl);
            if (taskExecutionControl != null) {
                amNotBusy();
                return (taskExecutionControl);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".notifyTaskFailure(): Could not update task, response->{}", taskId);
                amNotBusy();
                return (null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".notifyTaskFailure(): Error (NoSuchMethodException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".notifyTaskFailure: Error (GeneralException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        }
    }

    public TaskExecutionControl notifyTaskFinalisation(String participantName, TaskIdType taskId, TaskCompletionSummaryType completionSummary) {
        amBusy();
        getLogger().debug(".notifyTaskFinalisation(): Entry, taskId->{}", taskId);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getPonosTaskGridServerAddress();
        if (targetAddress == null) {
            getLogger().warn(".notifyTaskFinalisation(): Cannot Access {} to update task", subsystemNames.getPetasosTaskRepositoryServiceProviderName());
            getLogger().error(".notifyTaskFinalisation(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            amNotBusy();
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{participantName, taskId, completionSummary, jgroupsIPSummary};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions(ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskExecutionControl taskExecutionControl = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                taskExecutionControl = getRPCDispatcher().callRemoteMethod(targetAddress, "notifyTaskFinalisation", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".notifyTaskFinalisation(): Exit, response->{}", taskExecutionControl);
            if (taskExecutionControl != null) {
                amNotBusy();
                return (taskExecutionControl);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".notifyTaskFinalisation(): Could not update task, response->{}", taskId);
                amNotBusy();
                return (null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".notifyTaskFinalisation(): Error (NoSuchMethodException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".notifyTaskFinalisation: Error (GeneralException) ->{}", e.getMessage());
            amNotBusy();
            return (null);
        }
    }
}
