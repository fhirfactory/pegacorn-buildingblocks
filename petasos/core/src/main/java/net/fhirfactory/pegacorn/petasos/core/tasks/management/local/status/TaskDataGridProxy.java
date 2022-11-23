/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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

package net.fhirfactory.pegacorn.petasos.core.tasks.management.local.status;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskGridClientInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.completion.datatypes.TaskCompletionSummaryType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.FulfillmentTrackingIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.datatypes.TaskExecutionControl;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskExecutionCommandEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.datatypes.TaskOutcomeStatusType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.ActionableTaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityElementType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.factories.TaskTraceabilityElementTypeFactory;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayloadSet;
import net.fhirfactory.pegacorn.core.model.petasos.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosTaskExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.*;
import org.apache.camel.CamelContext;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class TaskDataGridProxy {
    private static final Logger LOG = LoggerFactory.getLogger(TaskDataGridProxy.class);

    private Long taskQueueRetryWaitTime;
    private int taskQueueRetryMaxAttempt;

    @Inject
    CamelContext camelctx;

    @Inject
    private PetasosTaskGridClientInterface taskGridClient;

    @Inject
    private TaskTraceabilityElementTypeFactory traceabilityElementTypeFactory;

    @Inject
    private PetasosActionableTaskSharedInstanceAccessorFactory taskInstanceFactory;

    @Inject
    private PetasosFulfillmentTaskSharedInstanceAccessorFactory fulfillmentTaskFactory;

    @Inject
    private PetasosTaskJobCardSharedInstanceAccessorFactory jobCardSharedInstanceFactory;

    @Inject
    private ProcessingPlantInterface processingPlant;

    //
    // Constructor(s)
    //

    public TaskDataGridProxy(){
        this.taskQueueRetryWaitTime = PetasosPropertyConstants.TASK_DATA_GRID_TASK_QUEUEING_RETRY_DELAY;
        this.taskQueueRetryMaxAttempt = PetasosPropertyConstants.TASK_DATA_GRID_TASK_QUEUEING_MAX_RETRY;
    }
    //
    // Post Construct
    //


    //
    // Task DataGrid Integration Services
    //

    public PetasosActionableTaskSharedInstance registerExternallyTriggeredTask(String participantName, PetasosActionableTask actionableTask){
        getLogger().debug(".registerExternallyTriggeredTask(): Entry, actionableTask->{}", actionableTask);
        if (actionableTask == null) {
            getLogger().debug(".registerExternallyTriggeredTask(): Exit, actionableTask is null");
            return (null);
        }
        //
        // Register with TaskDataGrid
        PetasosActionableTask registeredTask = null;
        int taskQueueRetryCount = 0;
        while (registeredTask == null && taskQueueRetryCount < getTaskQueueRetryMaxAttempt()){
            registeredTask = getTaskRepositoryService().registerExternallyTriggeredTask(participantName, actionableTask);
            if (registeredTask == null) {
                taskQueueRetryCount++;
                getLogger().error(".registerExternallyTriggeredTask(): Could not queue task, will try again in {} milliseconds", getTaskQueueRetryWaitTime());
                try {
                    Thread.sleep(getTaskQueueRetryWaitTime());
                } catch (InterruptedException e) {
                    getLogger().trace(".registerExternallyTriggeredTask(): Something interrupted my nap! reason->", e);
                }
            }
        }
        PetasosActionableTaskSharedInstance task = taskInstanceFactory.newActionableTaskSharedInstance(actionableTask);
        task.getTaskExecutionDetail().setExecutionCommand(TaskExecutionCommandEnum.TASK_COMMAND_EXECUTE);
        getLogger().debug(".registerExternallyTriggeredTask(): Exit, registeredTask->{}", registeredTask);
        return (task);
    }

    public TaskIdType queueTask(PetasosActionableTask actionableTask) {
        getLogger().debug(".queueTask(): Entry, actionableTask->{}", actionableTask);
        if (actionableTask == null) {
            getLogger().debug(".queueTask(): Exit, actionableTask is null");
            return (null);
        }
        //
        // Register with TaskDataGrid
        TaskIdType taskId = null;
        int taskQueueRetryCount = 0;
        while (taskId == null && taskQueueRetryCount < getTaskQueueRetryMaxAttempt()){
            taskId = getTaskRepositoryService().queueTask(actionableTask);
            if (taskId == null) {
                taskQueueRetryCount++;
                getLogger().error(".queueTask(): Could not queue task, will try again in {} milliseconds", getTaskQueueRetryWaitTime());
                try {
                    Thread.sleep(getTaskQueueRetryWaitTime());
                } catch (InterruptedException e) {
                    getLogger().trace(".queueTask(): Something interrupted my nap! reason->", e);
                }
            }
        }

        getLogger().debug(".queueTask(): Exit, taskId->{}", taskId);
        return (taskId);
    }

    public PetasosActionableTaskSharedInstance loadNextTask(String performerName){
        getLogger().debug(".loadNextTask(): Entry, participantName->{}", performerName);
        if(performerName == null){
            getLogger().debug(".loadNextTask(): Exit, performerName is null");
            return(null);
        }

        //
        // Retrieve next task from TaskDataGrid
        PetasosActionableTask nextPendingActionableTask = getTaskRepositoryService().getNextPendingTask(performerName);
        if(nextPendingActionableTask == null){
            // nothing to do
            return(null);
        }
        PetasosActionableTaskSharedInstance petasosActionableTaskSharedInstance = taskInstanceFactory.newActionableTaskSharedInstance(nextPendingActionableTask);
        //
        // Build a TaskJobCard
        PetasosTaskJobCard newJobCard = new PetasosTaskJobCard();
        newJobCard.setActionableTaskId(petasosActionableTaskSharedInstance.getTaskId());
        newJobCard.setProcessingPlantParticipantName(processingPlant.getSubsystemParticipantName());
        newJobCard.setCurrentStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_ASSIGNED);
        newJobCard.setGrantedStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_ASSIGNED);
        newJobCard.setSystemMode(processingPlant.getMeAsASoftwareComponent().getResilienceMode());
        newJobCard.setClusterMode(processingPlant.getMeAsASoftwareComponent().getConcurrencyMode());
        newJobCard.setLastActivityCheckInstant(Instant.now());
        jobCardSharedInstanceFactory.newTaskJobCardSharedInstanceAccessor(newJobCard);
        getLogger().debug(".queueTask(): Exit, petasosActionableTaskSharedInstance->{}", petasosActionableTaskSharedInstance);
        return (petasosActionableTaskSharedInstance);
    }


    public PetasosTaskExecutionStatusEnum notifyTaskFulfillerChange(TaskIdType actionableTaskId, TaskIdType fulfillmentTaskId){
        getLogger().warn(".notifyTaskStart(): Entry, actionableTaskId->{}, fulfillmentTaskId->{}", actionableTaskId, fulfillmentTaskId);
        if(fulfillmentTaskId == null || actionableTaskId == null){
            getLogger().debug(".notifyTaskStart(): Exit, actionableTaskId or fulfillmentTaskId is null");
            return(null);
        }
        PetasosActionableTaskSharedInstance petasosActionableTaskSharedInstance = taskInstanceFactory.getActionableTaskSharedInstance(actionableTaskId);
        PetasosFulfillmentTaskSharedInstance petasosFulfillmentTaskSharedInstance = fulfillmentTaskFactory.getFulfillmentTaskSharedInstance(fulfillmentTaskId);
        if(petasosFulfillmentTaskSharedInstance != null) {

            TaskFulfillmentType taskFulfillment = petasosFulfillmentTaskSharedInstance.getTaskFulfillment();
            petasosActionableTaskSharedInstance.setTaskFulfillment(SerializationUtils.clone(taskFulfillment));
            petasosActionableTaskSharedInstance.getTaskFulfillment().setTrackingID(new FulfillmentTrackingIdType(petasosFulfillmentTaskSharedInstance.getTaskId()));
            petasosActionableTaskSharedInstance.setTaskExecutionDetail(petasosFulfillmentTaskSharedInstance.getTaskExecutionDetail());
            petasosActionableTaskSharedInstance.setUpdateInstant(Instant.now());

            petasosActionableTaskSharedInstance.update();
        }

        PetasosTaskExecutionStatusEnum executionStatus = petasosActionableTaskSharedInstance.getTaskExecutionDetail().getCurrentExecutionStatus();
        getLogger().debug(".notifyTaskStart(): Exit, executionStatus->{}", executionStatus);
        return (executionStatus);
    }

    public TaskExecutionCommandEnum notifyTaskStart(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask) {
        getLogger().warn(".notifyTaskStart(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskStart(): Exit, taskId is null");
            return(null);
        }
        PetasosActionableTaskSharedInstance petasosActionableTaskSharedInstance = taskInstanceFactory.getActionableTaskSharedInstance(taskId);
        getLogger().warn(".notifyTaskStart(): Entry, taskInstanceFactory->{}", taskInstanceFactory);
        petasosActionableTaskSharedInstance.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE);
        petasosActionableTaskSharedInstance.getTaskFulfillment().setStartInstant(Instant.now());
        petasosActionableTaskSharedInstance.getTaskFulfillment().setFulfillerWorkUnitProcessor(fulfillmentTask.getTaskFulfillment().getFulfillerWorkUnitProcessor());
        petasosActionableTaskSharedInstance.getTaskFulfillment().setTrackingID(new FulfillmentTrackingIdType(fulfillmentTask.getTaskId()));
        petasosActionableTaskSharedInstance.setUpdateInstant(Instant.now());
        petasosActionableTaskSharedInstance.getTaskExecutionDetail().setCurrentExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_EXECUTING);

        petasosActionableTaskSharedInstance.update();
        //
        // Synchronise with TaskDataGrid
        TaskFulfillmentType taskFulfillment = petasosActionableTaskSharedInstance.getTaskFulfillment();
        String participantName = taskFulfillment.getFulfillerWorkUnitProcessor().getParticipantName();

        TaskExecutionControl taskExecutionControl = getTaskRepositoryService().notifyTaskStart(participantName, petasosActionableTaskSharedInstance.getInstance().getTaskId(), taskFulfillment);

        getLogger().debug(".notifyTaskStart(): Exit, taskExecutionControl->{}", taskExecutionControl);
        return (taskExecutionControl.getExecutionCommand());
    }

    public TaskExecutionCommandEnum notifyTaskFinish(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask){
        getLogger().warn(".notifyTaskFinish(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskFinish(): Exit, taskId is null");
            return(null);
        }
        PetasosActionableTaskSharedInstance actionableTask = taskInstanceFactory.getActionableTaskSharedInstance(taskId);
        getLogger().trace(".notifyTaskFinish(): actionableTask.getTaskFulfillment()->{}", actionableTask.getTaskFulfillment());
        TaskFulfillmentType taskFulfillment = SerializationUtils.clone(fulfillmentTask.getTaskFulfillment());
        taskFulfillment.setTrackingID(new FulfillmentTrackingIdType(fulfillmentTask.getTaskId()));
        if(taskFulfillment.hasFulfillerWorkUnitProcessor()) {
            actionableTask.getTaskFulfillment().setFulfillerWorkUnitProcessor(fulfillmentTask.getTaskFulfillment().getFulfillerWorkUnitProcessor());
        }
        actionableTask.setTaskFulfillment(taskFulfillment);
        actionableTask.setUpdateInstant(Instant.now());
        actionableTask.getTaskExecutionDetail().setCurrentExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FINISHED);

        //
        // Update Outcome Status
        if(actionableTask.getTaskOutcomeStatus() == null){
            TaskOutcomeStatusType outcomeStatus = new TaskOutcomeStatusType();
            actionableTask.setTaskOutcomeStatus(outcomeStatus);
        }
        actionableTask.getTaskOutcomeStatus().setOutcomeStatus(ActionableTaskOutcomeStatusEnum.ACTIONABLE_TASK_OUTCOME_STATUS_FINISHED);
        actionableTask.getTaskOutcomeStatus().setEntryInstant(Instant.now());

        //
        // Update the Task Traceability
        TaskTraceabilityElementType traceabilityElementType = traceabilityElementTypeFactory.newTaskTraceabilityElementFromTask(taskId,fulfillmentTask.getTaskFulfillment());
        actionableTask.getTaskTraceability().addToTaskJourney(traceabilityElementType);

        //
        // Update the Task Work Item
        UoWPayloadSet egressContent = fulfillmentTask.getTaskWorkItem().getEgressContent();
        UoWPayloadSet clonedEgressContent = SerializationUtils.clone(egressContent);
        actionableTask.getTaskWorkItem().setEgressContent(clonedEgressContent);
        actionableTask.getTaskWorkItem().setProcessingOutcome(fulfillmentTask.getTaskWorkItem().getProcessingOutcome());

        actionableTask.update();

        //
        // Synchronise with TaskDataGrid
        String participantName = actionableTask.getTaskFulfillment().getFulfillerWorkUnitProcessor().getParticipantName();
        TaskOutcomeStatusType taskOutcomeStatus = actionableTask.getTaskOutcomeStatus();
        TaskExecutionControl executionControl = getTaskRepositoryService().notifyTaskFinish(participantName, actionableTask.getInstance().getTaskId(), taskFulfillment, clonedEgressContent, taskOutcomeStatus, "Ordinary Processing");

        getLogger().debug(".notifyTaskFinish(): Exit, executionStatus->{}", executionControl);
        return(executionControl.getExecutionCommand());
    }

    public TaskExecutionCommandEnum notifyTaskFailure(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask){
        getLogger().warn(".notifyTaskFailure(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskFailure(): Exit, taskId is null");
            return(null);
        }
        PetasosActionableTaskSharedInstance actionableTask = taskInstanceFactory.getActionableTaskSharedInstance(taskId);

        //
        // Update Fulfillment Status
        TaskFulfillmentType taskFulfillment = SerializationUtils.clone(fulfillmentTask.getTaskFulfillment());
        actionableTask.getTaskFulfillment().setTrackingID(new FulfillmentTrackingIdType(fulfillmentTask.getTaskId()));
        actionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FAILED);
        if(taskFulfillment.hasFulfillerWorkUnitProcessor()) {
            actionableTask.getTaskFulfillment().setFulfillerWorkUnitProcessor(fulfillmentTask.getTaskFulfillment().getFulfillerWorkUnitProcessor());
        }
        actionableTask.getTaskFulfillment().setFinishInstant(Instant.now());
        actionableTask.setTaskFulfillment(taskFulfillment);
        actionableTask.setUpdateInstant(Instant.now());
        actionableTask.getTaskExecutionDetail().setCurrentExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FAILED);

        //
        // Update Outcome Status
        if(actionableTask.getTaskOutcomeStatus() == null){
            TaskOutcomeStatusType outcomeStatus = new TaskOutcomeStatusType();
            actionableTask.setTaskOutcomeStatus(outcomeStatus);
        }
        actionableTask.getTaskOutcomeStatus().setOutcomeStatus(ActionableTaskOutcomeStatusEnum.ACTIONABLE_TASK_OUTCOME_STATUS_FAILED);
        actionableTask.getTaskOutcomeStatus().setEntryInstant(Instant.now());

        //
        // Update Completion Status
        if(actionableTask.getTaskCompletionSummary() == null){
            TaskCompletionSummaryType outcomeStatus = new TaskCompletionSummaryType();
            actionableTask.setTaskCompletionSummary(outcomeStatus);
        }
        actionableTask.getTaskCompletionSummary().setFinalised(true);
        actionableTask.getTaskCompletionSummary().setLastInChain(true);

        //
        // Update the Task Traceability
        TaskTraceabilityElementType traceabilityElementType = traceabilityElementTypeFactory.newTaskTraceabilityElementFromTask(taskId,fulfillmentTask.getTaskFulfillment());
        actionableTask.getTaskTraceability().addToTaskJourney(traceabilityElementType);

        //
        // Update the Task Work Item
        UoWPayloadSet egressContent = fulfillmentTask.getTaskWorkItem().getEgressContent();
        UoWPayloadSet clonedEgressContent = SerializationUtils.clone(egressContent);
        actionableTask.getTaskWorkItem().setEgressContent(clonedEgressContent);
        actionableTask.getTaskWorkItem().setProcessingOutcome(fulfillmentTask.getTaskWorkItem().getProcessingOutcome());
        if(StringUtils.isNotEmpty(fulfillmentTask.getTaskWorkItem().getFailureDescription())){
            actionableTask.getTaskWorkItem().setFailureDescription(fulfillmentTask.getTaskWorkItem().getFailureDescription());
        } else {
            actionableTask.getTaskWorkItem().setFailureDescription("Failure Reason Unknown");
        }

        //
        // Synchronise the Participant Cache
        actionableTask.update();

        //
        // Synchronise with TaskDataGrid
        String participantName = actionableTask.getTaskFulfillment().getFulfillerWorkUnitProcessor().getParticipantName();
        TaskOutcomeStatusType taskOutcomeStatus = actionableTask.getTaskOutcomeStatus();
        String taskFailureReason = actionableTask.getTaskWorkItem().getFailureDescription();
        TaskExecutionControl executionControl = getTaskRepositoryService().notifyTaskFailure(participantName, actionableTask.getInstance().getTaskId(), actionableTask.getTaskFulfillment(),clonedEgressContent,taskOutcomeStatus, taskFailureReason);

        getLogger().debug(".notifyTaskFailure(): Exit, executionControl->{}", executionControl);
        return(executionControl.getExecutionCommand());
    }

    public TaskExecutionCommandEnum notifyTaskCancellation(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask){
        getLogger().warn(".notifyTaskCancellation(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskCancellation(): Exit, taskId is null");
            return(null);
        }
        PetasosActionableTaskSharedInstance actionableTask = taskInstanceFactory.getActionableTaskSharedInstance(taskId);

        //
        // Update Fulfillment Status
        TaskFulfillmentType taskFulfillment = SerializationUtils.clone(fulfillmentTask.getTaskFulfillment());
        actionableTask.setTaskFulfillment(taskFulfillment);
        if(taskFulfillment.hasFulfillerWorkUnitProcessor()) {
            actionableTask.getTaskFulfillment().setFulfillerWorkUnitProcessor(fulfillmentTask.getTaskFulfillment().getFulfillerWorkUnitProcessor());
        }
        actionableTask.setUpdateInstant(Instant.now());
        actionableTask.getTaskExecutionDetail().setCurrentExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_CANCELLED);

        //
        // Update Completion Status
        if(actionableTask.getTaskCompletionSummary() == null){
            TaskCompletionSummaryType outcomeStatus = new TaskCompletionSummaryType();
            actionableTask.setTaskCompletionSummary(outcomeStatus);
        }
        actionableTask.getTaskCompletionSummary().setFinalised(true);
        actionableTask.getTaskCompletionSummary().setLastInChain(true);

        //
        // Update Outcome Status
        if(actionableTask.getTaskOutcomeStatus() == null){
            TaskOutcomeStatusType outcomeStatus = new TaskOutcomeStatusType();
            actionableTask.setTaskOutcomeStatus(outcomeStatus);
        }
        actionableTask.getTaskOutcomeStatus().setOutcomeStatus(ActionableTaskOutcomeStatusEnum.ACTIONABLE_TASK_OUTCOME_STATUS_CANCELLED);
        actionableTask.getTaskOutcomeStatus().setEntryInstant(Instant.now());

        //
        // Update the Task Traceability
        TaskTraceabilityElementType traceabilityElementType = traceabilityElementTypeFactory.newTaskTraceabilityElementFromTask(taskId,fulfillmentTask.getTaskFulfillment());
        actionableTask.getTaskTraceability().addToTaskJourney(traceabilityElementType);

        //
        // Update the Task Work Item
        UoWPayloadSet egressContent = fulfillmentTask.getTaskWorkItem().getEgressContent();
        UoWPayloadSet clonedEgressContent = SerializationUtils.clone(egressContent);
        actionableTask.getTaskWorkItem().setEgressContent(clonedEgressContent);
        actionableTask.getTaskWorkItem().setProcessingOutcome(fulfillmentTask.getTaskWorkItem().getProcessingOutcome());
        if(StringUtils.isNotEmpty(fulfillmentTask.getTaskWorkItem().getFailureDescription())){
            actionableTask.getTaskWorkItem().setFailureDescription(fulfillmentTask.getTaskWorkItem().getFailureDescription());
        }

        //
        // Synchronise the Participant Cache
        actionableTask.update();

        //
        // Synchronise with TaskDataGrid
        String participantName = actionableTask.getTaskFulfillment().getFulfillerWorkUnitProcessor().getParticipantName();
        TaskOutcomeStatusType taskOutcomeStatus = actionableTask.getTaskOutcomeStatus();
        String taskFailureReason = actionableTask.getTaskWorkItem().getFailureDescription();
        TaskExecutionControl executionControl = getTaskRepositoryService().notifyTaskCancellation(participantName, actionableTask.getInstance().getTaskId(),taskFulfillment,egressContent,taskOutcomeStatus, "Locally Cancelled" );

        getLogger().debug(".notifyTaskCancellation(): Exit, executionControl->{}", executionControl);
        return(executionControl.getExecutionCommand());
    }

    public TaskExecutionCommandEnum notifyTaskWaiting(TaskIdType taskId) {
        getLogger().warn(".notifyTaskWaiting(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskWaiting(): Exit, taskId is null");
            return(null);
        }
        PetasosActionableTaskSharedInstance actionableTask = taskInstanceFactory.getActionableTaskSharedInstance(taskId);

        //
        // Update Fulfillment Status
        actionableTask.setUpdateInstant(Instant.now());
        actionableTask.getTaskExecutionDetail().setCurrentExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_ASSIGNED);

        //
        // Synchronise the Participant Cache
        actionableTask.update();

        getLogger().debug(".notifyTaskWaiting(): Exit, executionStatus->{}", TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
        return(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
    }

    public TaskExecutionCommandEnum notifyTaskFinalisation(TaskIdType taskId) {
        getLogger().warn(".notifyTaskFinalisation(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskFinalisation(): Exit, taskId is null");
            return(null);
        }
        PetasosActionableTaskSharedInstance actionableTask = taskInstanceFactory.getActionableTaskSharedInstance(taskId);

        //
        // Synchronise the Participant Cache
        actionableTask.update();

        //
        // Synchronise with TaskDataGrid
        String participantName = actionableTask.getTaskFulfillment().getFulfillerWorkUnitProcessor().getParticipantName();
        TaskCompletionSummaryType taskCompletionSummary = actionableTask.getTaskCompletionSummary();
        TaskExecutionControl executionControl = getTaskRepositoryService().notifyTaskFinalisation(participantName, actionableTask.getInstance().getTaskId(),taskCompletionSummary);

        getLogger().debug(".notifyTaskWaiting(): Exit, executionControl->{}", executionControl);
        return(executionControl.getExecutionCommand());
    }

    //
    // Getters (and Setters)
    //

    protected PetasosTaskGridClientInterface getTaskRepositoryService(){
        return(taskGridClient);
    }

    protected Logger getLogger(){
        return(LOG);
    }

    public Long getTaskQueueRetryWaitTime() {
        return taskQueueRetryWaitTime;
    }

    public int getTaskQueueRetryMaxAttempt() {
        return taskQueueRetryMaxAttempt;
    }
}
