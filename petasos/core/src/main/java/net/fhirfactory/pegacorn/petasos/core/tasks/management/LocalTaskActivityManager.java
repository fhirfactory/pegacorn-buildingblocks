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

package net.fhirfactory.pegacorn.petasos.core.tasks.management;

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskActivityNotificationInterface;
import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.completion.datatypes.TaskCompletionSummaryType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.FulfillmentTrackingIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.datatypes.TaskOutcomeStatusType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.ActionableTaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityElementType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.factories.TaskTraceabilityElementTypeFactory;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayloadSet;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosTaskExecutionStatusEnum;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosActionableTaskSharedInstance;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosFulfillmentTaskSharedInstance;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosFulfillmentTaskSharedInstanceAccessorFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.cache.LocalActionableTaskCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.queue.LocalTaskQueueManager;
import org.apache.camel.CamelContext;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class LocalTaskActivityManager implements PetasosTaskActivityNotificationInterface {
    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskActivityManager.class);

    private static final Long PONOS_CONNECTIVITY_RETRY_PERIOD = 15000L;

    @Inject
    CamelContext camelctx;

    @Inject
    private PetasosTaskBrokerInterface taskRepositoryService;

    @Inject
    private TaskTraceabilityElementTypeFactory traceabilityElementTypeFactory;

    @Inject
    private PetasosFulfillmentTaskSharedInstanceAccessorFactory fulfillmentTaskFactory;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private LocalTaskQueueManager localTaskQueueManager;

    @Inject
    private LocalActionableTaskCache localActionableTaskCache;


    //
    // Constructor(s)
    //


    //
    // Post Construct
    //


    //
    // Notifications
    //

    public void registerActionableTask(PetasosActionableTask localActionableTask){
        getLogger().debug(".registerActionableTask(): Entry, localActionableTask->{}", localActionableTask);
        if(localActionableTask == null){
            getLogger().debug(".registerActionableTask(): Exit, localActionableTask is null");
            return;
        }
        localActionableTask.setExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_WAITING);


        //
        // Synchronise with Ponos
        PetasosActionableTask centralActionableTask = getTaskRepositoryService().registerActionableTask(localActionableTask);

        //
        // Register Locally
        if(centralActionableTask == null){
            getLogger().warn(".registerActionableTask(): Cannot register ActionableTask, will try again in {} seconds", PONOS_CONNECTIVITY_RETRY_PERIOD/1000L);
            // TODO support retry
        } else {
            localActionableTask.update(centralActionableTask);
            boolean successfullyLocallyRegistered = getLocalActionableTaskCache().registerTask(localActionableTask);
            if(successfullyLocallyRegistered) {
                localActionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_REGISTERED);
                localActionableTask.getTaskFulfillment().setRegistrationInstant(Instant.now());
                if(get)
                getLocalTaskQueueManager().queueTask(localActionableTask);
                getLogger().debug(".registerActionableTask(): Exit, petasosActionableTaskSharedInstance->{}", petasosActionableTaskSharedInstance);
                return (petasosActionableTaskSharedInstance);
            }
        }
    }

    @Override
    public PetasosTaskExecutionStatusEnum notifyTaskStart(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask) {
        getLogger().debug(".notifyTaskStart(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskStart(): Exit, taskId is null");
            return(null);
        }
        PetasosActionableTaskSharedInstance petasosActionableTaskSharedInstance = taskInstanceFactory.getActionableTaskSharedInstance(taskId);

        petasosActionableTaskSharedInstance.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE);
        petasosActionableTaskSharedInstance.getTaskFulfillment().setStartInstant(Instant.now());
        petasosActionableTaskSharedInstance.getTaskFulfillment().setFulfiller(fulfillmentTask.getTaskFulfillment().getFulfiller());
        petasosActionableTaskSharedInstance.getTaskFulfillment().setTrackingID(new FulfillmentTrackingIdType(fulfillmentTask.getTaskId()));
        petasosActionableTaskSharedInstance.setUpdateInstant(Instant.now());
        petasosActionableTaskSharedInstance.setExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_EXECUTING);

        petasosActionableTaskSharedInstance.update();
        //
        // Synchronise with Ponos
        PetasosActionableTask petasosActionableTask = getTaskRepositoryService().updateActionableTask(petasosActionableTaskSharedInstance.getInstance());

        PetasosTaskExecutionStatusEnum executionStatus = petasosActionableTaskSharedInstance.getExecutionStatus();
        getLogger().debug(".notifyTaskStart(): Exit, executionStatus->{}", executionStatus);
        return (executionStatus);
    }

    @Override
    public PetasosTaskExecutionStatusEnum notifyTaskFinish(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".notifyTaskFinish(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskFinish(): Exit, taskId is null");
            return(null);
        }
        PetasosActionableTaskSharedInstance actionableTask = taskInstanceFactory.getActionableTaskSharedInstance(taskId);
        getLogger().trace(".notifyTaskFinish(): actionableTask.getTaskFulfillment()->{}", actionableTask.getTaskFulfillment());
        TaskFulfillmentType taskFulfillment = SerializationUtils.clone(fulfillmentTask.getTaskFulfillment());
        taskFulfillment.setTrackingID(new FulfillmentTrackingIdType(fulfillmentTask.getTaskId()));
        actionableTask.setTaskFulfillment(taskFulfillment);
        actionableTask.setUpdateInstant(Instant.now());
        actionableTask.setExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FINISHED);

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
        // Synchronise with Ponos
        PetasosActionableTask petasosActionableTask = getTaskRepositoryService().updateActionableTask(actionableTask.getInstance());

        PetasosTaskExecutionStatusEnum executionStatus = actionableTask.getExecutionStatus();
        getLogger().debug(".notifyTaskFinish(): Exit, executionStatus->{}", executionStatus);
        return(executionStatus);
    }

    @Override
    public PetasosTaskExecutionStatusEnum notifyTaskFailure(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".notifyTaskFailure(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskFailure(): Exit, taskId is null");
            return(null);
        }
        PetasosActionableTaskSharedInstance actionableTask = taskInstanceFactory.newActionableTaskSharedInstance(taskId);

        //
        // Update Fulfillment Status
        TaskFulfillmentType taskFulfillment = SerializationUtils.clone(fulfillmentTask.getTaskFulfillment());
        actionableTask.getTaskFulfillment().setTrackingID(new FulfillmentTrackingIdType(fulfillmentTask.getTaskId()));
        actionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FAILED);
        actionableTask.getTaskFulfillment().setFinishInstant(Instant.now());
        actionableTask.setTaskFulfillment(taskFulfillment);
        actionableTask.setUpdateInstant(Instant.now());
        actionableTask.setExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_FAILED);

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
        }

        //
        // Synchronise the Participant Cache
        actionableTask.update();

        //
        // Synchronise with Ponos
        PetasosActionableTask petasosActionableTask = getTaskRepositoryService().updateActionableTask(actionableTask.getInstance());

        //
        // Extract Task Status
        PetasosTaskExecutionStatusEnum executionStatus = petasosActionableTask.getExecutionStatus();

        getLogger().debug(".notifyTaskFailure(): Exit, executionStatus->{}", executionStatus);
        return(executionStatus);
    }

    @Override
    public PetasosTaskExecutionStatusEnum notifyTaskCancellation(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".notifyTaskCancellation(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskCancellation(): Exit, taskId is null");
            return(null);
        }
        PetasosActionableTaskSharedInstance actionableTask = taskInstanceFactory.newActionableTaskSharedInstance(taskId);

        //
        // Update Fulfillment Status
        TaskFulfillmentType taskFulfillment = SerializationUtils.clone(fulfillmentTask.getTaskFulfillment());
        actionableTask.setTaskFulfillment(taskFulfillment);
        actionableTask.setUpdateInstant(Instant.now());
        actionableTask.setExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_CANCELLED);

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
        // Synchronise with Ponos
        PetasosActionableTask petasosActionableTask = getTaskRepositoryService().updateActionableTask(actionableTask.getInstance());

        //
        // Extract Task Status
        PetasosTaskExecutionStatusEnum executionStatus = petasosActionableTask.getExecutionStatus();

        getLogger().debug(".notifyTaskCancellation(): Exit, executionStatus->{}", executionStatus);
        return(executionStatus);
    }

    @Override
    public PetasosTaskExecutionStatusEnum notifyTaskWaiting(TaskIdType taskId) {
        getLogger().debug(".notifyTaskWaiting(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskWaiting(): Exit, taskId is null");
            return(null);
        }
        PetasosActionableTaskSharedInstance actionableTask = taskInstanceFactory.newActionableTaskSharedInstance(taskId);

        //
        // Update Fulfillment Status
        actionableTask.setUpdateInstant(Instant.now());
        actionableTask.setExecutionStatus(PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_WAITING);

        //
        // Synchronise the Participant Cache
        actionableTask.update();

        //
        // Synchronise with Ponos
        PetasosActionableTask petasosActionableTask = getTaskRepositoryService().updateActionableTask(actionableTask.getInstance());

        //
        // Extract Task Status
        PetasosTaskExecutionStatusEnum executionStatus = petasosActionableTask.getExecutionStatus();

        getLogger().debug(".notifyTaskWaiting(): Exit, executionStatus->{}", executionStatus);
        return(executionStatus);
    }

    public PetasosTaskExecutionStatusEnum notifyTaskFinalisation(TaskIdType taskId) {
        getLogger().debug(".notifyTaskFinalisation(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskFinalisation(): Exit, taskId is null");
            return(null);
        }
        PetasosActionableTaskSharedInstance actionableTask = taskInstanceFactory.newActionableTaskSharedInstance(taskId);
        PetasosFulfillmentTaskSharedInstance fulfillmentTask = fulfillmentTaskFactory.newFulfillmentTaskSharedAccessor(actionableTask.getTaskFulfillment().getTrackingID());

        //
        // Synchronise the Participant Cache
        actionableTask.update();

        //
        // Synchronise with Ponos
        PetasosActionableTask petasosActionableTask = getTaskRepositoryService().updateActionableTask(actionableTask.getInstance());

        //
        // Extract Task Status
        PetasosTaskExecutionStatusEnum executionStatus = petasosActionableTask.getExecutionStatus();

        getLogger().debug(".notifyTaskWaiting(): Exit, executionStatus->{}", executionStatus);
        return(executionStatus);
    }

    //
    // Getters (and Setters)
    //

    protected PetasosTaskBrokerInterface getTaskRepositoryService(){
        return(taskRepositoryService);
    }

    protected Logger getLogger(){
        return(LOG);
    }

    protected LocalTaskQueueManager getLocalTaskQueueManager(){
        return(localTaskQueueManager);
    }

    protected LocalActionableTaskCache getLocalActionableTaskCache(){
        return(localActionableTaskCache);
    }

}
