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

package net.fhirfactory.pegacorn.petasos.core.tasks.management.local;

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskRepositoryServiceProviderNameInterface;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.FulfillmentTrackingIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.datatypes.TaskOutcomeStatusType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.ActionableTaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityElementType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.factories.TaskTraceabilityElementTypeFactory;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayloadSet;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.shared.SharedActionableTaskDM;
import org.apache.camel.CamelContext;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class LocalPetasosActionableTaskActivityController {
    private static final Logger LOG = LoggerFactory.getLogger(LocalPetasosActionableTaskActivityController.class);

    @Inject
    CamelContext camelctx;

    @Inject
    private PetasosTaskBrokerInterface taskRepositoryService;

    @Inject
    private SharedActionableTaskDM sharedActionableTaskDM;

    @Inject
    private PetasosTaskRepositoryServiceProviderNameInterface taskRepositoryServiceProviderNameInterface;

    @Inject
    private TaskTraceabilityElementTypeFactory traceabilityElementTypeFactory;


    //
    // Constructor(s)
    //


    //
    // Post Construct
    //


    //
    // Task Registration/Deregistration
    //

    public PetasosActionableTask registerActionableTask(PetasosActionableTask actionableTask){
        getLogger().debug(".registerActionableTask(): Entry, actionableTask->{}",actionableTask);
        getSharedActionableTaskDM().registerActionableTask(actionableTask);
        getTaskRepositoryService().registerActionableTask(taskRepositoryServiceProviderNameInterface.getPetasosTaskRepositoryServiceProviderName(), actionableTask);
        getLogger().debug(".registerActionableTask(): Exit, actionableTask->{}", actionableTask);
        return(actionableTask);
    }

    public PetasosActionableTask deregisterActionableTask(PetasosActionableTask actionableTask){
        getLogger().debug(".deregisterActionableTask(): Entry, actionableTask->{}",actionableTask);
        getSharedActionableTaskDM().removeActionableTask(actionableTask);
        getLogger().debug(".deregisterActionableTask(): Exit, actionableTask->{}",actionableTask);
        return(actionableTask);
    }

    //
    // Notifications
    //

    public Instant notifyActionTaskExecutionStart(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".notifyActionableTaskExecutionFinish(): Entry, taskId->{}, fulfillmentTask->{}", taskId, fulfillmentTask);
        if(fulfillmentTask == null){
            getLogger().debug(".notifyActionableTaskExecutionFinish(): Exit, fulfillmentTask is null");
            return(null);
        }
        if(fulfillmentTask.getTaskJobCard().getGrantedStatus().equals(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING)){
            PetasosActionableTask actionableTask = getSharedActionableTaskDM().getActionableTask(taskId);
            actionableTask.getTaskFulfillment().setStartInstant(Instant.now());
            actionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE);
            actionableTask.getTaskFulfillment().setTrackingID(new FulfillmentTrackingIdType(fulfillmentTask.getTaskId()));
        }
        Instant updateInstant = Instant.now();
        return(updateInstant);
    }

    public PetasosActionableTask notifyActionableTaskExecutionFinish(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".notifyActionableTaskExecutionFinish(): Entry, taskId->{}, fulfillmentTask->{}", taskId, fulfillmentTask);
        if(fulfillmentTask == null){
            getLogger().debug(".notifyActionableTaskExecutionFinish(): Exit, fulfillmentTask is null");
            return(null);
        }
        PetasosActionableTask actionableTask = getSharedActionableTaskDM().getActionableTask(taskId);
        //
        // Update Fulfillment Status
        if(actionableTask.getTaskFulfillment() == null){
            TaskFulfillmentType taskFulfillment = SerializationUtils.clone(fulfillmentTask.getTaskFulfillment());
            actionableTask.setTaskFulfillment(taskFulfillment);
        }
        actionableTask.getTaskFulfillment().setStartInstant(fulfillmentTask.getTaskFulfillment().getStartInstant());
        actionableTask.getTaskFulfillment().setFinishInstant(Instant.now());
        actionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FINISHED);
        actionableTask.getTaskFulfillment().setTrackingID(new FulfillmentTrackingIdType(fulfillmentTask.getTaskId()));
        actionableTask.getTaskFulfillment().setFulfillerComponent(fulfillmentTask.getTaskFulfillment().getFulfillerComponent());
        //
        // Update Outcome Status
        if(actionableTask.getTaskOutcomeStatus() == null){
            TaskOutcomeStatusType outcomeStatus = new TaskOutcomeStatusType();
            actionableTask.setTaskOutcomeStatus(outcomeStatus);
        }
        actionableTask.getTaskOutcomeStatus().setOutcomeStatus(ActionableTaskOutcomeStatusEnum.ACTIONABLE_TASK_OUTCOME_STATUS_FINISHED);
        actionableTask.getTaskOutcomeStatus().setEntryInstant(Instant.now());

        UoWPayloadSet taskWorkItemEgressContent = SerializationUtils.clone(fulfillmentTask.getTaskWorkItem().getEgressContent());
        actionableTask.getTaskWorkItem().setEgressContent(taskWorkItemEgressContent);
        TaskTraceabilityElementType traceabilityElementType = traceabilityElementTypeFactory.newTaskTraceabilityElementFromTask(taskId,fulfillmentTask.getTaskFulfillment());
        actionableTask.getTaskTraceability().addToTaskJourney(traceabilityElementType);
        PetasosActionableTask petasosActionableTask = getTaskRepositoryService().updateActionableTask(taskRepositoryServiceProviderNameInterface.getPetasosTaskRepositoryServiceProviderName(), actionableTask);
        PetasosActionableTask clonedTask = SerializationUtils.clone(petasosActionableTask);
        getLogger().debug(".notifyActionableTaskExecutionFinish(): Exit, clonedTask->{}", clonedTask);
        return(clonedTask);
    }

    public PetasosActionableTask notifyActionableTaskExecutionFailure(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".notifyActionableTaskExecutionFailure(): Entry, taskId->{}, fulfillmentTask->{}", taskId, fulfillmentTask);
        if(fulfillmentTask == null){
            getLogger().debug(".notifyActionableTaskExecutionFailure(): Exit, fulfillmentTask is null");
            return(null);
        }
        PetasosActionableTask actionableTask = getSharedActionableTaskDM().getActionableTask(taskId);
        //
        // Update Fulfillment Status
        if(actionableTask.getTaskFulfillment() == null){
            TaskFulfillmentType taskFulfillment = SerializationUtils.clone(fulfillmentTask.getTaskFulfillment());
            actionableTask.setTaskFulfillment(taskFulfillment);
        }
        actionableTask.getTaskFulfillment().setFinishInstant(Instant.now());
        actionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FAILED);
        actionableTask.getTaskFulfillment().setTrackingID(new FulfillmentTrackingIdType(fulfillmentTask.getTaskId()));
        //
        // Update Outcome Status
        if(actionableTask.getTaskOutcomeStatus() == null){
            TaskOutcomeStatusType outcomeStatus = new TaskOutcomeStatusType();
            actionableTask.setTaskOutcomeStatus(outcomeStatus);
        }
        actionableTask.getTaskOutcomeStatus().setOutcomeStatus(ActionableTaskOutcomeStatusEnum.ACTIONABLE_TASK_OUTCOME_STATUS_FAILED);
        actionableTask.getTaskOutcomeStatus().setEntryInstant(Instant.now());
        TaskTraceabilityElementType traceabilityElementType = traceabilityElementTypeFactory.newTaskTraceabilityElementFromTask(taskId,fulfillmentTask.getTaskFulfillment());
        actionableTask.getTaskTraceability().addToTaskJourney(traceabilityElementType);
        PetasosActionableTask petasosActionableTask = getTaskRepositoryService().updateActionableTask(taskRepositoryServiceProviderNameInterface.getPetasosTaskRepositoryServiceProviderName(), actionableTask);
        PetasosActionableTask clonedTask = SerializationUtils.clone(petasosActionableTask);
        getLogger().debug(".notifyActionableTaskExecutionFailure(): Exit");
        return(clonedTask);
    }

    public PetasosActionableTask notifyActionableTaskExecutionCancellation(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".notifyActionableTaskExecutionCancellation(): Entry, taskId->{}, fulfillmentTask->{}", taskId, fulfillmentTask);
        if(fulfillmentTask == null){
            getLogger().debug(".notifyActionableTaskExecutionCancellation(): Exit, fulfillmentTask is null");
            return(null);
        }
        PetasosActionableTask actionableTask = getSharedActionableTaskDM().getActionableTask(taskId);
        //
        // Update Fulfillment Status
        if(actionableTask.getTaskFulfillment() == null){
            TaskFulfillmentType taskFulfillment = SerializationUtils.clone(fulfillmentTask.getTaskFulfillment());
            actionableTask.setTaskFulfillment(taskFulfillment);
        }
        actionableTask.getTaskFulfillment().setFinishInstant(Instant.now());
        actionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_CANCELLED);
        actionableTask.getTaskFulfillment().setTrackingID(new FulfillmentTrackingIdType(fulfillmentTask.getTaskId()));
        //
        // Update Outcome Status
        if(actionableTask.getTaskOutcomeStatus() == null){
            TaskOutcomeStatusType outcomeStatus = new TaskOutcomeStatusType();
            actionableTask.setTaskOutcomeStatus(outcomeStatus);
        }
        actionableTask.getTaskOutcomeStatus().setOutcomeStatus(ActionableTaskOutcomeStatusEnum.ACTIONABLE_TASK_OUTCOME_STATUS_CANCELLED);
        actionableTask.getTaskOutcomeStatus().setEntryInstant(Instant.now());
        TaskTraceabilityElementType traceabilityElementType = traceabilityElementTypeFactory.newTaskTraceabilityElementFromTask(taskId,fulfillmentTask.getTaskFulfillment());
        actionableTask.getTaskTraceability().addToTaskJourney(traceabilityElementType);
        PetasosActionableTask petasosActionableTask = getTaskRepositoryService().updateActionableTask(taskRepositoryServiceProviderNameInterface.getPetasosTaskRepositoryServiceProviderName(), actionableTask);
        PetasosActionableTask clonedTask = SerializationUtils.clone(petasosActionableTask);
        getLogger().debug(".notifyActionableTaskExecutionCancellation(): Exit");
        return(clonedTask);
    }

    public PetasosActionableTask updateActionableTask(PetasosActionableTask actionableTask){
        getLogger().debug(".updateActionableTaskFinalisationStatus(): Entry, actionableTask->{}", actionableTask);
        if(actionableTask == null){
            getLogger().debug(".updateActionableTaskFinalisationStatus(): Exit, fulfillmentTask is null");
            return(null);
        }
        PetasosActionableTask cachedActionableTask = getSharedActionableTaskDM().getActionableTask(actionableTask.getTaskId());
        if(cachedActionableTask != null){
            cachedActionableTask.update(actionableTask);
        }
        PetasosActionableTask syncrhonisedActionableTask = getTaskRepositoryService().updateActionableTask(taskRepositoryServiceProviderNameInterface.getPetasosTaskRepositoryServiceProviderName(), cachedActionableTask);
        cachedActionableTask.update(syncrhonisedActionableTask);
        return(syncrhonisedActionableTask);
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

    protected SharedActionableTaskDM getSharedActionableTaskDM(){
        return(sharedActionableTaskDM);
    }
}
