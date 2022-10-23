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

package net.fhirfactory.pegacorn.petasos.core.tasks.management.execution;

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskActivityNotificationInterface;
import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.petasos.jobcard.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.jobcard.datatypes.PetasosTaskFulfillmentCard;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.completion.datatypes.TaskCompletionSummaryType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.FulfillmentTrackingIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.datatypes.TaskOutcomeStatusType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.TaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskStorageType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityElementType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.factories.TaskTraceabilityElementTypeFactory;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.valuesets.TaskStorageStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayloadSet;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWProcessingOutcomeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskExecutionCommandEnum;
import net.fhirfactory.pegacorn.petasos.audit.brokers.PetasosFulfillmentTaskAuditServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.tasks.cache.LocalTaskJobCardCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosTaskJobCardFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.queue.LocalTaskQueueCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.cache.LocalActionableTaskCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.registries.LocalFulfillmentTaskRegistry;
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
    private PetasosTaskBrokerInterface taskRepositoryService;

    @Inject
    private TaskTraceabilityElementTypeFactory traceabilityElementTypeFactory;

    @Inject
    private LocalFulfillmentTaskRegistry localFulfillmentTaskRegistry;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private LocalTaskQueueCache localTaskQueueCache;

    @Inject
    private LocalActionableTaskCache localActionableTaskCache;

    @Inject
    private PetasosTaskJobCardFactory taskJobCardFactory;

    @Inject
    private LocalTaskJobCardCache localTaskJobCardCache;

    @Inject
    private PetasosFulfillmentTaskAuditServicesBroker auditServicesBroker;


    //
    // Constructor(s)
    //


    //
    // Post Construct
    //


    //
    // Task Registration
    //

    /**
     * Tasks are either created Locally (by the "Task Outcome Collection" framework (TaskOutcome2NewTaskBean.java)) in
     * response to a "subscription" to the TaskWorkItem or due to a "Directive OR they are forwarded by Ponos-IM for
     * processing by local WorkUnitProcessors (WUPs).
     *
     * In the former case, the ActionableTask needs to be registered within the "Local Registry" AND the
     * "Central Registry" (i.e. Ponos-IM). Issues of Buffering and downstream WUP suspension are handled via the
     * Queue Management services (LocalTaskQueueManager.java) and are ignored here.
     *
     * @param localActionableTask
     * @param localJobCard A CLONED version of the current state of the local JobCard.
     * @return
     */
    public PetasosTaskJobCard registerLocallyCreatedTask(PetasosActionableTask localActionableTask, PetasosTaskJobCard localJobCard){
        getLogger().debug(".registerLocallyCreatedActionableTask(): Entry, localActionableTask->{}", localActionableTask);
        if(localActionableTask == null){
            getLogger().debug(".registerLocallyCreatedActionableTask(): Exit, localActionableTask is null");
            return(null);
        }
        getLogger().trace(".registerLocallyCreatedActionableTask(): [Create/Update TaskJobCard] Start");
        if(localJobCard == null){
            if(localActionableTask.hasTaskPerformerTypes()){
                if(!localActionableTask.getTaskPerformerTypes().isEmpty()){
                    TaskPerformerTypeType taskPerformerTypeType = localActionableTask.getTaskPerformerTypes().get(0);
                    PetasosParticipantId knownTaskPerformer = taskPerformerTypeType.getKnownTaskPerformer();
                    localJobCard = getTaskJobCardFactory().newTaskJobCard(localActionableTask.getTaskId(), knownTaskPerformer);
                }
            }
            if(localJobCard == null) {
                localJobCard = getTaskJobCardFactory().newTaskJobCard(localActionableTask.getTaskId(), getProcessingPlant());
            }
        }
        localJobCard.setPersistenceStatus(localActionableTask.getTaskTraceability().getPersistenceStatus());
        localJobCard.setUpdateInstant(Instant.now());
        getLogger().trace(".registerLocallyCreatedActionableTask(): [Create/Update TaskJobCard] Start");

        getLogger().trace(".registerLocallyCreatedActionableTask(): [Set ActionableTask to WAIT/Unsaved State] Start");
        localActionableTask.getExecutionControl().setExecutionCommand(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
        localActionableTask.setRegistered(false);
        localActionableTask.getTaskTraceability().getPersistenceStatus().setLocalStorageLocation(processingPlant.getTopologyNode().getComponentId().getDisplayName());
        localActionableTask.getTaskTraceability().getPersistenceStatus().setLocalStorageStatus(TaskStorageStatusEnum.TASK_UNSAVED);
        localActionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED);
        getLogger().trace(".registerLocallyCreatedActionableTask(): [Set ActionableTask to WAIT/Unsaved State] Finish");

        getLogger().trace(".registerLocallyCreatedActionableTask(): [Register Task With Local Registry] Start");
        boolean successfullyLocallyRegistered = getLocalActionableTaskRegistry().addToCache(localActionableTask);
        if(!successfullyLocallyRegistered){
            getLogger().warn(".registerLocallyCreatedActionableTask(): Cannot register ActionableTask into local registry, something is clearly wrong!");
            return(null);
        }
        getLogger().trace(".registerLocallyCreatedActionableTask(): [Register Task With Local Registry] Finish");

        getLogger().trace(".registerLocallyCreatedActionableTask(): [Update ActionableTask Traceability to reflect Stored State] Start");
        localJobCard.getPersistenceStatus().setLocalStorageInstant(localActionableTask.getTaskTraceability().getPersistenceStatus().getLocalStorageInstant());
        localJobCard.getPersistenceStatus().setLocalStorageLocation(localActionableTask.getTaskTraceability().getPersistenceStatus().getLocalStorageLocation());
        localJobCard.getPersistenceStatus().setLocalStorageStatus(localActionableTask.getTaskTraceability().getPersistenceStatus().getLocalStorageStatus());
        getLogger().trace(".registerLocallyCreatedActionableTask(): [Update ActionableTask Traceability to reflect Stored State] Finish");


        getLogger().trace(".registerLocallyCreatedActionableTask(): [Register Task With Ponos] Start");
        PetasosTaskJobCard jobcard = getTaskRepositoryService().registerTask(localActionableTask, localJobCard);
        getLogger().trace(".registerLocallyCreatedActionableTask(): [Register Task With Ponos] Finish, jobcard->{}", jobcard);

        getLogger().trace(".registerLocallyCreatedActionableTask(): [Update ActionableTask Traceability to reflect Stored State] Start");
        if(jobcard == null){
            getLogger().warn(".registerLocallyCreatedActionableTask(): Cannot register ActionableTask, will try again in {} seconds", PONOS_CONNECTIVITY_RETRY_PERIOD/1000L);
            localActionableTask.getExecutionControl().setExecutionCommand(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
        } else {
            localJobCard.getPersistenceStatus().setCentralStorageStatus(jobcard.getPersistenceStatus().getCentralStorageStatus());
            localJobCard.getPersistenceStatus().setCentralStorageLocation(jobcard.getPersistenceStatus().getCentralStorageLocation());
            localJobCard.getPersistenceStatus().setCentralStorageInstant(jobcard.getPersistenceStatus().getCentralStorageInstant());
            localJobCard.setGrantedStatus(jobcard.getGrantedStatus());
            localJobCard.setUpdateInstant(Instant.now());
            localActionableTask.getTaskTraceability().getPersistenceStatus().setCentralStorageInstant(jobcard.getPersistenceStatus().getCentralStorageInstant());
            localActionableTask.getTaskTraceability().getPersistenceStatus().setCentralStorageStatus(jobcard.getPersistenceStatus().getCentralStorageStatus());
            localActionableTask.getTaskTraceability().getPersistenceStatus().setCentralStorageLocation(jobcard.getPersistenceStatus().getCentralStorageLocation());
            localActionableTask.getExecutionControl().setExecutionCommand(jobcard.getGrantedStatus());
        }
        getLogger().trace(".registerLocallyCreatedActionableTask(): [Update ActionableTask Traceability to reflect Stored State] Finish");

        getLogger().trace(".registerLocallyCreatedActionableTask(): [Put JobCard into Cache] Start");
        getLocalTaskJobCardCache().addJobCard(localJobCard);
        getLogger().trace(".registerLocallyCreatedActionableTask(): [Put JobCard into Cache] Finish");

        PetasosTaskJobCard clonedJobCard = SerializationUtils.clone(localJobCard);

        getLogger().debug(".registerLocallyCreatedActionableTask(): Exit");
        return(clonedJobCard);
    }

    public PetasosTaskJobCard registerCentrallyCreatedActionableTask(PetasosActionableTask centralActionableTask, PetasosTaskJobCard jobCard) {
        getLogger().debug(".registerCentrallyCreatedActionableTask(): Entry, centralActionableTask->{}, jobCard->{}", centralActionableTask, jobCard);

        getLogger().trace(".registerCentrallyCreatedActionableTask(): [Create/Update TaskJobCard] Start");
        boolean hasPersistenceStatus = false;
        if(jobCard == null) {
            jobCard = getTaskJobCardFactory().newTaskJobCard(centralActionableTask.getTaskId(), getProcessingPlant());
        }
        if (!jobCard.hasPersistenceStatus()) {
            if (centralActionableTask.hasTaskTraceability()) {
                if (centralActionableTask.getTaskTraceability().hasPersistenceStatus()) {
                    jobCard.setPersistenceStatus(centralActionableTask.getTaskTraceability().getPersistenceStatus());
                    hasPersistenceStatus = true;
                }
            }
            if (!hasPersistenceStatus) {
                jobCard.setPersistenceStatus(new TaskStorageType());
                jobCard.getPersistenceStatus().setCentralStorageLocation("unknown");
                jobCard.getPersistenceStatus().setCentralStorageStatus(TaskStorageStatusEnum.TASK_SAVED);
                jobCard.getPersistenceStatus().setCentralStorageInstant(Instant.now());
            }
        }
        jobCard.setUpdateInstant(Instant.now());
        getLogger().trace(".registerCentrallyCreatedActionableTask(): [Create/Update TaskJobCard] Start");

        getLogger().trace(".registerCentrallyCreatedActionableTask(): [Set ActionableTask to Unsaved State] Start");
        centralActionableTask.getTaskTraceability().getPersistenceStatus().setLocalStorageLocation(processingPlant.getTopologyNode().getComponentId().getDisplayName());
        centralActionableTask.getTaskTraceability().getPersistenceStatus().setLocalStorageStatus(TaskStorageStatusEnum.TASK_UNSAVED);
        centralActionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED);
        getLogger().trace(".registerCentrallyCreatedActionableTask(): [Set ActionableTask to Unsaved State] Finish");

        getLogger().trace(".registerCentrallyCreatedActionableTask(): [Register Task With Local Registry] Start");
        boolean successfullyLocallyRegistered = getLocalActionableTaskRegistry().addToCache(centralActionableTask);
        if(!successfullyLocallyRegistered){
            getLogger().warn(".registerCentrallyCreatedActionableTask(): Cannot register ActionableTask into local registry, something is clearly wrong!");
            return(null);
        }
        getLogger().trace(".registerCentrallyCreatedActionableTask(): [Register Task With Local Registry] Finish");

        getLogger().trace(".registerCentrallyCreatedActionableTask(): [Update ActionableTask Traceability to reflect Stored State] Start");
        jobCard.getPersistenceStatus().setLocalStorageInstant(centralActionableTask.getTaskTraceability().getPersistenceStatus().getLocalStorageInstant());
        jobCard.getPersistenceStatus().setLocalStorageLocation(centralActionableTask.getTaskTraceability().getPersistenceStatus().getLocalStorageLocation());
        jobCard.getPersistenceStatus().setLocalStorageStatus(centralActionableTask.getTaskTraceability().getPersistenceStatus().getLocalStorageStatus());
        getLogger().trace(".registerCentrallyCreatedActionableTask(): [Update ActionableTask Traceability to reflect Stored State] Finish");

        getLogger().trace(".registerCentrallyCreatedActionableTask(): [Put JobCard into Cache] Start");
        getLocalTaskJobCardCache().addJobCard(jobCard);
        getLogger().trace(".registerCentrallyCreatedActionableTask(): [Put JobCard into Cache] Finish");

        PetasosTaskJobCard clonedJobCard = SerializationUtils.clone(jobCard);

        getLogger().debug(".registerCentrallyCreatedActionableTask(): Exit");
        return(clonedJobCard);
    }

    public PetasosActionableTask deregisterActionableTask(TaskIdType taskId){
        getLogger().debug(".deregisterActionableTask(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".deregisterActionableTask(): Exit, taskId is null/empty!");
            return(null);
        }
        PetasosActionableTask petasosActionableTask = getLocalActionableTaskRegistry().removeTaskFromDirectory(taskId);
        getLogger().debug(".deregisterActionableTask(): Exit, petasosActionableTask->{}", petasosActionableTask);
        return(petasosActionableTask);
    }

    public void registerTaskOutcome(PetasosActionableTask actionableTask, PetasosTaskJobCard jobCard){
        getLogger().debug(".registerTaskOutcome(): Entry, localActionableTask->{}", actionableTask);
        if(actionableTask == null){
            getLogger().debug(".registerTaskOutcome(): Exit, localActionableTask is null");
            return;
        }

        if(jobCard == null) {
            jobCard = getTaskJobCardFactory().newTaskJobCard(actionableTask.getTaskId(), getProcessingPlant());
            jobCard.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_FINALISE);
            jobCard.setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_FINALISED);
        }

        getLogger().trace(".registerTaskOutcome(): [Register Task Outcome With Ponos] Start");
        PetasosTaskJobCard jobcard = getTaskRepositoryService().registerTaskOutcome(actionableTask, jobCard);
        getLogger().trace(".registerTaskOutcome(): [Register Task Outcome With Ponos] Finish, jobcard->{}", jobcard);

        getLocalTaskJobCardCache().addJobCard(jobCard);

        getLogger().debug(".registerTaskOutcome(): Exit");
    }

    // FulfilmentTask Registration Services

    public PetasosFulfillmentTask registerFulfillmentTask(PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".registerFulfillmentTask(): Entry, fulfillmentTask->{}", fulfillmentTask);
        if(fulfillmentTask == null){
            getLogger().debug(".registerFulfillmentTask(): Exit, fulfillmentTask is null/empty!");
            return(null);
        }
        PetasosFulfillmentTask petasosFulfillmentTask = registerFulfillmentTask(fulfillmentTask, false);
        getLogger().debug(".registerFulfillmentTask(): Exit, fulfillmentTask->{}", petasosFulfillmentTask);
        return(petasosFulfillmentTask);
    }

    /**
     * A FulfillmentTask is created if (and only if) the local ParticipantManager has detailed that the target WUP is
     * not "Suspended" AND has execution privilege on the task - and can thus undertake task processing. The Task
     * Distribution Service (LocalTaskDistributionBean.java) will create a FulfillmentTask for the "next-in-queue"
     * ActionableTask for forward this into the WUP-Framework for the target WUP and subsequently REGISTER it. So, we
     * need to "register" (i.e. merely stick into the FulfillmentTaskRegistry the FulfillmentTask) and update the
     * status of the ActionableTask and TaskJobCard to reflect the "execution" status.
     *
     * Note that the returned FulfillmentTask is a CLONE of the original (and, thus, a clone of the one "stored" in
     * the FulfillmentTaskRegistry). All "updates" of the ActionableTask, TaskJobCard and FulfillmentTask are handled
     * via this manager class --> to protected against race conditions. This may change in subsequent releases.
     *
     * Lastly, we need to create an AuditEvent for the FulfillmentTask and log it with Hestia-Audit.
     *
     * @param fulfillmentTask
     * @param writeSynchronousAuditEvent
     * @return
     */
    public PetasosFulfillmentTask registerFulfillmentTask(PetasosFulfillmentTask fulfillmentTask, boolean writeSynchronousAuditEvent){
        getLogger().debug(".registerFulfillmentTask(): Entry, fulfillmentTask->{}", fulfillmentTask);
        if(fulfillmentTask == null){
            getLogger().debug(".registerFulfillmentTask(): Exit, fulfillmentTask is null/empty!");
            return(null);
        }
        //
        // Register the Task
        PetasosFulfillmentTask registeredTask = getLocalFulfillmentTaskRegistry().registerTask(fulfillmentTask);

        //
        // Update the ActionableTask & TaskJobCard
        getLogger().trace(".registerFulfillmentTask(): [Get the ActionableTask from Registry and Update] Start");
        PetasosActionableTask actionableTask = getLocalActionableTaskRegistry().getTask(registeredTask.getActionableTaskId());
        if(actionableTask == null){
            getLogger().debug(".registerFulfillmentTask(): There is no actionable task for this fulfillment task, deleting!");
            deregisterFulfillmentTask(fulfillmentTask);
            return(null);
        }
        if(!actionableTask.hasTaskFulfillment()){
            actionableTask.setTaskFulfillment(new TaskFulfillmentType());
        }
        actionableTask.getTaskFulfillment().setStatus(registeredTask.getTaskFulfillment().getStatus());
        actionableTask.getTaskFulfillment().setFulfiller(registeredTask.getTaskFulfillment().getFulfiller());
        actionableTask.getTaskFulfillment().setRegistrationInstant(Instant.now());
        actionableTask.getTaskFulfillment().setTrackingID(registeredTask.getTaskFulfillment().getTrackingID());
        actionableTask.getTaskFulfillment().setResilientActivity(registeredTask.getTaskFulfillment().isResilientActivity());
        getLogger().trace(".registerFulfillmentTask(): [Get the ActionableTask from Registry and Update] Finish");

        getLogger().trace(".registerFulfillmentTask(): [Get the JobCard from Registry and Update] Start");
        PetasosTaskJobCard taskJobCard = getLocalTaskJobCardCache().getJobCard(actionableTask.getTaskId());
        boolean needToRegisterJobCard = false;
        if(taskJobCard == null) {
            needToRegisterJobCard = true;
        }
        if(taskJobCard == null && registeredTask.hasTaskJobCard()){
            taskJobCard = registeredTask.getTaskJobCard();
        }
        if(taskJobCard == null) {
            taskJobCard = getTaskJobCardFactory().newTaskJobCard(registeredTask.getActionableTaskId(), getProcessingPlant());
            taskJobCard.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
            taskJobCard.setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_WAITING);
        }
        if(!taskJobCard.hasTaskFufillmentCard()) {
            taskJobCard.setTaskFulfillmentCard(new PetasosTaskFulfillmentCard());
            taskJobCard.getTaskFulfillmentCard().setFulfillerParticipantId(registeredTask.getTaskFulfillment().getFulfiller().getParticipant().getParticipantId());
            taskJobCard.getTaskFulfillmentCard().setFulfillmentTaskId(registeredTask.getTaskId());
            taskJobCard.getTaskFulfillmentCard().setFulfillmentExecutionStatus(registeredTask.getTaskFulfillment().getStatus());
        }
        taskJobCard.getTaskFulfillmentCard().setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_REGISTERED);
        if(needToRegisterJobCard){
            getLocalTaskJobCardCache().addJobCard(taskJobCard);
        }
        getLogger().trace(".registerFulfillmentTask(): [Get the JobCard from Registry and Update] Finish");

        getLogger().trace(".registerFulfillmentTask(): [Update the JobCard within the FulfillmentTask] Start");
        registeredTask.setTaskJobCard(taskJobCard);
        getLogger().trace(".registerFulfillmentTask(): [Update the JobCard within the FulfillmentTask] Finish");

        getLogger().trace(".registerFulfillmentTask(): [Clone FulfillmentTask] Start");
        PetasosFulfillmentTask updatedRegistryTask = getLocalFulfillmentTaskRegistry().synchroniseTask(registeredTask);
        getLogger().trace(".registerFulfillmentTask(): [Clone FulfillmentTask] Finish");

        //
        // Create an audit event
        auditServicesBroker.logActivity(updatedRegistryTask, writeSynchronousAuditEvent);
        //
        // We're done
        getLogger().debug(".registerFulfillmentTask(): Exit, fulfillmentTask->{}", updatedRegistryTask);
        return(updatedRegistryTask);
    }

    public PetasosFulfillmentTask deregisterFulfillmentTask(PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".deregisterFulfillmentTask(): Entry, fulfillmentTask->{}", fulfillmentTask);
        if(fulfillmentTask == null){
            getLogger().debug(".deregisterFulfillmentTask(): Exit, fulfillmentTask is null/empty!");
            return(null);
        }
        PetasosFulfillmentTask petasosFulfillmentTask = getLocalFulfillmentTaskRegistry().removeTask(fulfillmentTask.getTaskId());
        getLogger().debug(".deregisterFulfillmentTask(): Exit, fulfillmentTask->{}", petasosFulfillmentTask);
        return(petasosFulfillmentTask);
    }

    //
    // Task Activity Notifications
    //

    /**
     *
     * @param taskId
     * @param fulfillmentTask
     * @return
     */
    @Override
    public TaskExecutionCommandEnum notifyTaskStart(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask) {
        getLogger().debug(".notifyTaskStart(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskStart(): Exit, taskId is null");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }
        if(fulfillmentTask == null){
            getLogger().debug(".notifyTaskStart(): Exit, fulfillmentTask is null");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }

        getLogger().trace(".notifyTaskStart(): [Get the ActionableTask from Registry] Start");
        PetasosActionableTask actionableTask = getLocalActionableTaskRegistry().getTask(taskId);
        if(actionableTask == null){
            getLogger().warn(".notifyTaskStart(): Exit, could not find ActionableTask for given TaskId!!!");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }
        getLogger().trace(".notifyTaskStart(): [Get the ActionableTask from Registry] Finish");

        getLogger().trace(".notifyTaskStart(): [Update Local Job Card based on FulfillmentTask] Start");
        PetasosTaskJobCard jobCardInRegistry = getLocalTaskJobCardCache().getJobCard(taskId);
        PetasosTaskJobCard jobCardFromFulfillmentTask = fulfillmentTask.getTaskJobCard();
        jobCardInRegistry.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_EXECUTE);
        jobCardInRegistry.setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_ACTIVE);
        jobCardInRegistry.setTaskFulfillmentCard(SerializationUtils.clone(jobCardFromFulfillmentTask.getTaskFulfillmentCard()));
        jobCardInRegistry.getTaskFulfillmentCard().setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE);
        jobCardFromFulfillmentTask.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_EXECUTE);
        jobCardFromFulfillmentTask.setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_ACTIVE);
        getLogger().trace(".notifyTaskStart(): [Update Local Job Card based on FulfillmentTask] Finish");

        getLogger().trace(".notifyTaskStart(): [Synchronise with Ponos] Start");
        PetasosTaskJobCard centralJobCard = getTaskRepositoryService().registerTaskStart(jobCardInRegistry);
        getLogger().trace(".notifyTaskStart(): [Synchronise with Ponos] Finish");

        getLogger().trace(".notifyTaskStart(): [Update Local Job Card based on Central JobCard] Start");
        if(centralJobCard != null) {
            jobCardInRegistry.setCurrentStatus(centralJobCard.getGrantedStatus());
            jobCardInRegistry.setGrantedStatus(centralJobCard.getGrantedStatus());
            jobCardInRegistry.setUpdateInstant(centralJobCard.getUpdateInstant());
            jobCardFromFulfillmentTask.setCurrentStatus(centralJobCard.getGrantedStatus());
            jobCardFromFulfillmentTask.setGrantedStatus(centralJobCard.getGrantedStatus());
            jobCardFromFulfillmentTask.setUpdateInstant(centralJobCard.getUpdateInstant());
        }
        getLogger().trace(".notifyTaskStart(): [Update Local Job Card based on Central JobCard] Finish");

        switch(centralJobCard.getGrantedStatus()) {
            case TASK_COMMAND_EXECUTE: {
                getLogger().trace(".notifyTaskStart(): [Update ActionableTask] Start");
                if (!actionableTask.hasTaskFulfillment()) {
                    actionableTask.setTaskFulfillment(SerializationUtils.clone(fulfillmentTask.getTaskFulfillment()));
                    actionableTask.getTaskFulfillment().setFulfiller(fulfillmentTask.getTaskFulfillment().getFulfiller());
                    actionableTask.getTaskFulfillment().setTrackingID(new FulfillmentTrackingIdType(fulfillmentTask.getTaskId()));
                }
                actionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE);
                actionableTask.getTaskFulfillment().setStartInstant(Instant.now());
                actionableTask.setUpdateInstant(Instant.now());
                actionableTask.getExecutionControl().setExecutionCommand(TaskExecutionCommandEnum.TASK_COMMAND_EXECUTE);
                getLogger().trace(".notifyTaskStart(): [Update ActionableTask] Finish");
                break;
            }
            default:{
                getLogger().info(".notifyTaskStart(): Task it to be cancelled/failed-out, fulfillmentTask->{}", fulfillmentTask);
            }
        }
        TaskExecutionCommandEnum executionStatus = jobCardInRegistry.getGrantedStatus();
        getLogger().debug(".notifyTaskStart(): Exit, executionStatus->{}", executionStatus);
        return (executionStatus);
    }

    @Override
    public TaskExecutionCommandEnum notifyTaskFinish(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".notifyTaskFinish(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskFinish(): Exit, taskId is null");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }
        if(fulfillmentTask == null){
            getLogger().debug(".notifyTaskFinish(): Exit, fulfillmentTask is null");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }

        getLogger().trace(".notifyTaskFinish(): [Get the ActionableTask from Registry] Start");
        PetasosActionableTask actionableTask = getLocalActionableTaskRegistry().getTask(taskId);
        if(actionableTask == null){
            getLogger().warn(".notifyTaskFinish(): Exit, could not find ActionableTask for given TaskId!!!");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }
        getLogger().trace(".notifyTaskFinish(): [Get the ActionableTask from Registry] Finish");

        getLogger().trace(".notifyTaskFinish(): [Update ActionableTask.taskFulfillment] Start");
        if(!actionableTask.hasTaskFulfillment()){
            actionableTask.setTaskFulfillment(fulfillmentTask.getTaskFulfillment());
        }
        actionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FINISHED);
        actionableTask.getTaskFulfillment().setFinishInstant(Instant.now());
        getLogger().trace(".notifyTaskFinish(): [Update ActionableTask.taskFulfillment] Finish");

        getLogger().trace(".notifyTaskFinish(): [Update ActionableTask.taskWorkItem] Start");
        UoWPayloadSet egressContent = fulfillmentTask.getTaskWorkItem().getEgressContent();
        UoWPayloadSet clonedEgressContent = SerializationUtils.clone(egressContent);
        actionableTask.getTaskWorkItem().setEgressContent(clonedEgressContent);
        actionableTask.getTaskWorkItem().setProcessingOutcome(fulfillmentTask.getTaskWorkItem().getProcessingOutcome());
        getLogger().trace(".notifyTaskFinish(): [Update ActionableTask.taskWorkItem] Finish");

        getLogger().trace(".notifyTaskFinish(): [Update ActionableTask Basic Status] Start");
        actionableTask.setUpdateInstant(Instant.now());
        actionableTask.getExecutionControl().setExecutionCommand(TaskExecutionCommandEnum.TASK_COMMAND_FINISH);
        getLogger().trace(".notifyTaskFinish(): [Update ActionableTask Basic Status] Finish");

        getLogger().trace(".notifyTaskFinish(): [Update ActionableTask.taskOutcomeStatus] Start");
        if(actionableTask.getTaskOutcomeStatus() == null){
            TaskOutcomeStatusType outcomeStatus = new TaskOutcomeStatusType();
            actionableTask.setTaskOutcomeStatus(outcomeStatus);
        }
        actionableTask.getTaskOutcomeStatus().setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_FINISHED);
        actionableTask.getTaskOutcomeStatus().setEntryInstant(Instant.now());
        getLogger().trace(".notifyTaskFinish(): [Update ActionableTask.taskOutcomeStatus] Finish");

        getLogger().trace(".notifyTaskFinish(): [Update ActionableTask.taskTraceability] Start");
        TaskTraceabilityElementType traceabilityElementType = traceabilityElementTypeFactory.newTaskTraceabilityElementFromTask(taskId,fulfillmentTask.getTaskFulfillment());
        actionableTask.getTaskTraceability().addToTaskJourney(traceabilityElementType);
        getLogger().trace(".notifyTaskFinish(): [Update ActionableTask.taskTraceability] Finish");

        getLogger().trace(".notifyTaskFinish(): [Update Local Job Card based on FulfillmentTask] Start");
        PetasosTaskJobCard jobCardFromRegistry = getLocalTaskJobCardCache().getJobCard(taskId);
        PetasosTaskJobCard jobCardFromFulfillmentTask = fulfillmentTask.getTaskJobCard();
        jobCardFromRegistry.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_FINISH);
        jobCardFromRegistry.setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_FINISHED);
        if(!jobCardFromRegistry.hasTaskFufillmentCard()){
            jobCardFromRegistry.setTaskFulfillmentCard(SerializationUtils.clone(jobCardFromFulfillmentTask.getTaskFulfillmentCard()));
        }
        jobCardFromRegistry.setUpdateInstant(Instant.now());
        jobCardFromRegistry.getTaskFulfillmentCard().setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FINISHED);
        getLogger().trace(".notifyTaskFinish(): [Update Local Job Card based on FulfillmentTask] Finish");

        getLogger().trace(".notifyTaskFinish(): [Synchronise with Ponos] Start");
        PetasosTaskJobCard centralJobCard = getTaskRepositoryService().registerTaskStart(jobCardFromRegistry);
        getLogger().trace(".notifyTaskFinish(): [Synchronise with Ponos] Finish");

        getLogger().trace(".notifyTaskFinish(): [Update Local Job Card based on Central JobCard] Start");
        if(centralJobCard != null) {
            jobCardFromRegistry.setCurrentStatus(centralJobCard.getGrantedStatus());
            jobCardFromRegistry.setGrantedStatus(centralJobCard.getGrantedStatus());
            jobCardFromRegistry.setUpdateInstant(centralJobCard.getUpdateInstant());
            jobCardFromFulfillmentTask.setCurrentStatus(centralJobCard.getGrantedStatus());
            jobCardFromFulfillmentTask.setGrantedStatus(centralJobCard.getGrantedStatus());
            jobCardFromFulfillmentTask.setUpdateInstant(centralJobCard.getUpdateInstant());
        }
        getLogger().trace(".notifyTaskFinish(): [Update Local Job Card based on Central JobCard] Finish");

        getLogger().trace(".notifyTaskFinish(): [Update ActionableTask based on Central JobCard] Start");
        actionableTask.getExecutionControl().setExecutionCommand(centralJobCard.getGrantedStatus());
        getLogger().trace(".notifyTaskFinish(): [Update ActionableTask based on Central JobCard] Finish");

        TaskExecutionCommandEnum executionStatus = centralJobCard.getGrantedStatus();
        getLogger().debug(".notifyTaskFinish(): Exit, executionStatus->{}", executionStatus);
        return(executionStatus);
    }

    @Override
    public TaskExecutionCommandEnum notifyTaskFailure(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".notifyTaskFailure(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskFailure(): Exit, taskId is null");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }
        if(fulfillmentTask == null){
            getLogger().debug(".notifyTaskFailure(): Exit, fulfillmentTask is null");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }

        getLogger().trace(".notifyTaskFailure(): [Get the ActionableTask from Registry] Start");
        PetasosActionableTask actionableTask = getLocalActionableTaskRegistry().getTask(taskId);
        if(actionableTask == null){
            getLogger().warn(".notifyTaskFailure(): Exit, could not find ActionableTask for given TaskId!!!");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }
        getLogger().trace(".notifyTaskFailure(): [Get the ActionableTask from Registry] Finish");

        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask.taskFulfillment] Start");
        if(!actionableTask.hasTaskFulfillment()){
            actionableTask.setTaskFulfillment(fulfillmentTask.getTaskFulfillment());
        }
        actionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FAILED);
        actionableTask.getTaskFulfillment().setFinishInstant(Instant.now());
        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask.taskFulfillment] Finish");

        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask.taskWorkItem] Start");
        UoWPayloadSet egressContent = fulfillmentTask.getTaskWorkItem().getEgressContent();
        UoWPayloadSet clonedEgressContent = SerializationUtils.clone(egressContent);
        actionableTask.getTaskWorkItem().setEgressContent(clonedEgressContent);
        actionableTask.getTaskWorkItem().setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_FAILED);
        if(StringUtils.isNotEmpty(fulfillmentTask.getTaskWorkItem().getFailureDescription())){
            actionableTask.getTaskWorkItem().setFailureDescription(fulfillmentTask.getTaskWorkItem().getFailureDescription());
        }
        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask.taskWorkItem] Finish");

        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask Basic Status] Start");
        actionableTask.setUpdateInstant(Instant.now());
        actionableTask.getExecutionControl().setExecutionCommand(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask Basic Status] Finish");

        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask.taskOutcomeStatus] Start");
        if(actionableTask.getTaskOutcomeStatus() == null){
            TaskOutcomeStatusType outcomeStatus = new TaskOutcomeStatusType();
            actionableTask.setTaskOutcomeStatus(outcomeStatus);
        }
        actionableTask.getTaskOutcomeStatus().setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_FAILED);
        actionableTask.getTaskOutcomeStatus().setEntryInstant(Instant.now());
        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask.taskOutcomeStatus] Finish");

        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask.taskCompletionSummary] Start");
        if(actionableTask.getTaskCompletionSummary() == null){
            TaskCompletionSummaryType outcomeStatus = new TaskCompletionSummaryType();
            actionableTask.setTaskCompletionSummary(outcomeStatus);
        }
        actionableTask.getTaskCompletionSummary().setFinalised(true);
        actionableTask.getTaskCompletionSummary().setLastInChain(true);
        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask.taskCompletionSummary] Finish");

        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask.taskTraceability] Start");
        TaskTraceabilityElementType traceabilityElementType = traceabilityElementTypeFactory.newTaskTraceabilityElementFromTask(taskId,fulfillmentTask.getTaskFulfillment());
        actionableTask.getTaskTraceability().addToTaskJourney(traceabilityElementType);
        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask.taskTraceability] Finish");

        getLogger().trace(".notifyTaskFailure(): [Update Local Job Card based on FulfillmentTask] Start");
        PetasosTaskJobCard jobCardFromRegistry = getLocalTaskJobCardCache().getJobCard(taskId);
        PetasosTaskJobCard jobCardFromFulfillmentTask = fulfillmentTask.getTaskJobCard();
        jobCardFromRegistry.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        jobCardFromRegistry.setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_FAILED);
        if(!jobCardFromRegistry.hasTaskFufillmentCard()){
            jobCardFromRegistry.setTaskFulfillmentCard(SerializationUtils.clone(jobCardFromFulfillmentTask.getTaskFulfillmentCard()));
        }
        jobCardFromRegistry.setUpdateInstant(Instant.now());
        jobCardFromRegistry.getTaskFulfillmentCard().setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FAILED);
        getLogger().trace(".notifyTaskFailure(): [Update Local Job Card based on FulfillmentTask] Finish");

        getLogger().trace(".notifyTaskFailure(): [Synchronise with Ponos] Start");
        PetasosTaskJobCard centralJobCard = getTaskRepositoryService().registerTaskFailure(jobCardFromRegistry);
        getLogger().trace(".notifyTaskFailure(): [Synchronise with Ponos] Finish");

        getLogger().trace(".notifyTaskFailure(): [Update Local Job Card based on Central JobCard] Start");
        if(centralJobCard != null) {
            jobCardFromRegistry.setCurrentStatus(centralJobCard.getCurrentStatus());
            jobCardFromRegistry.setGrantedStatus(centralJobCard.getGrantedStatus());
            jobCardFromRegistry.setUpdateInstant(centralJobCard.getUpdateInstant());
            jobCardFromFulfillmentTask.setCurrentStatus(centralJobCard.getGrantedStatus());
            jobCardFromFulfillmentTask.setGrantedStatus(centralJobCard.getGrantedStatus());
            jobCardFromFulfillmentTask.setUpdateInstant(centralJobCard.getUpdateInstant());

        }
        getLogger().trace(".notifyTaskFailure(): [Update Local Job Card based on Central JobCard] Finish");

        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask based on Central JobCard] Start");
        actionableTask.getExecutionControl().setExecutionCommand(centralJobCard.getGrantedStatus());
        getLogger().trace(".notifyTaskFailure(): [Update ActionableTask based on Central JobCard] Finish");

        //
        // Extract Task Status
        TaskExecutionCommandEnum executionStatus = centralJobCard.getGrantedStatus();

        getLogger().debug(".notifyTaskFailure(): Exit, executionStatus->{}", executionStatus);
        return(executionStatus);
    }

    @Override
    public TaskExecutionCommandEnum notifyTaskCancellation(TaskIdType taskId, PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".notifyTaskCancellation(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskCancellation(): Exit, taskId is null");
            return(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
        }
        if(fulfillmentTask == null){
            getLogger().debug(".notifyTaskCancellation(): Exit, fulfillmentTask is null");
            return(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
        }

        getLogger().trace(".notifyTaskCancellation(): [Get the ActionableTask from Registry] Start");
        PetasosActionableTask actionableTask = getLocalActionableTaskRegistry().getTask(taskId);
        if(actionableTask == null){
            getLogger().warn(".notifyTaskCancellation(): Exit, could not find ActionableTask for given TaskId!!!");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }
        getLogger().trace(".notifyTaskCancellation(): [Get the ActionableTask from Registry] Finish");

        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask.taskFulfillment] Start");
        if(!actionableTask.hasTaskFulfillment()){
            actionableTask.setTaskFulfillment(fulfillmentTask.getTaskFulfillment());
        }
        actionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_CANCELLED);
        actionableTask.getTaskFulfillment().setFinishInstant(Instant.now());
        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask.taskFulfillment] Finish");

        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask Basic Status] Start");
        actionableTask.setUpdateInstant(Instant.now());
        actionableTask.getExecutionControl().setExecutionCommand(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask Basic Status] Finish");

        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask.taskCompletionSummary] Start");
        if(actionableTask.getTaskCompletionSummary() == null){
            TaskCompletionSummaryType outcomeStatus = new TaskCompletionSummaryType();
            actionableTask.setTaskCompletionSummary(outcomeStatus);
        }
        actionableTask.getTaskCompletionSummary().setFinalised(true);
        actionableTask.getTaskCompletionSummary().setLastInChain(true);
        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask.taskCompletionSummary] Finish");

        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask.taskOutcomeStatus] Start");
        if(actionableTask.getTaskOutcomeStatus() == null){
            TaskOutcomeStatusType outcomeStatus = new TaskOutcomeStatusType();
            actionableTask.setTaskOutcomeStatus(outcomeStatus);
        }
        actionableTask.getTaskOutcomeStatus().setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_CANCELLED);
        actionableTask.getTaskOutcomeStatus().setEntryInstant(Instant.now());
        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask.taskOutcomeStatus] Start");

        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask.taskTraceability] Start");
        TaskTraceabilityElementType traceabilityElementType = traceabilityElementTypeFactory.newTaskTraceabilityElementFromTask(taskId,fulfillmentTask.getTaskFulfillment());
        actionableTask.getTaskTraceability().addToTaskJourney(traceabilityElementType);
        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask.taskTraceability] Finish");

        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask.taskWorkItem] Start");
        UoWPayloadSet egressContent = fulfillmentTask.getTaskWorkItem().getEgressContent();
        UoWPayloadSet clonedEgressContent = SerializationUtils.clone(egressContent);
        actionableTask.getTaskWorkItem().setEgressContent(clonedEgressContent);
        actionableTask.getTaskWorkItem().setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_FAILED);
        if(StringUtils.isNotEmpty(fulfillmentTask.getTaskWorkItem().getFailureDescription())){
            actionableTask.getTaskWorkItem().setFailureDescription(fulfillmentTask.getTaskWorkItem().getFailureDescription());
        }
        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask.taskWorkItem] Finish");

        getLogger().trace(".notifyTaskCancellation(): [Update Local Job Card based on FulfillmentTask] Start");
        PetasosTaskJobCard jobCardFromRegistry = getLocalTaskJobCardCache().getJobCard(taskId);
        PetasosTaskJobCard jobCardFromFulfillmentTask = fulfillmentTask.getTaskJobCard();
        jobCardFromRegistry.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
        jobCardFromRegistry.setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_CANCELLED);
        if(!jobCardFromRegistry.hasTaskFufillmentCard()){
            jobCardFromRegistry.setTaskFulfillmentCard(SerializationUtils.clone(jobCardFromFulfillmentTask.getTaskFulfillmentCard()));
        }
        jobCardFromRegistry.setUpdateInstant(Instant.now());
        jobCardFromRegistry.getTaskFulfillmentCard().setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_CANCELLED);
        getLogger().trace(".notifyTaskCancellation(): [Update Local Job Card based on FulfillmentTask] Finish");

        getLogger().trace(".notifyTaskCancellation(): [Synchronise with Ponos] Start");
        PetasosTaskJobCard centralJobCard = getTaskRepositoryService().registerTaskCancellation(jobCardFromRegistry);
        getLogger().trace(".notifyTaskCancellation(): [Synchronise with Ponos] Finish");

        getLogger().trace(".notifyTaskCancellation(): [Update Local Job Card based on Central JobCard] Start");
        if(centralJobCard != null) {
            jobCardFromRegistry.setCurrentStatus(centralJobCard.getGrantedStatus());
            jobCardFromRegistry.setGrantedStatus(centralJobCard.getGrantedStatus());
            jobCardFromRegistry.setUpdateInstant(centralJobCard.getUpdateInstant());
            jobCardFromFulfillmentTask.setCurrentStatus(centralJobCard.getGrantedStatus());
            jobCardFromFulfillmentTask.setGrantedStatus(centralJobCard.getGrantedStatus());
            jobCardFromFulfillmentTask.setUpdateInstant(centralJobCard.getUpdateInstant());
        }
        getLogger().trace(".notifyTaskCancellation(): [Update Local Job Card based on Central JobCard] Finish");

        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask based on Central JobCard] Start");
        actionableTask.getExecutionControl().setExecutionCommand(centralJobCard.getGrantedStatus());
        getLogger().trace(".notifyTaskCancellation(): [Update ActionableTask based on Central JobCard] Finish");

        //
        // Extract Task Status
        TaskExecutionCommandEnum executionStatus = centralJobCard.getGrantedStatus();

        getLogger().debug(".notifyTaskCancellation(): Exit, executionStatus->{}", executionStatus);
        return(executionStatus);
    }

    @Override
    public TaskExecutionCommandEnum notifyTaskWaiting(TaskIdType taskId) {
        getLogger().debug(".notifyTaskWaiting(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskWaiting(): Exit, taskId is null");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }

        getLogger().trace(".notifyTaskWaiting(): [Get the ActionableTask from Registry] Start");
        PetasosActionableTask actionableTask = getLocalActionableTaskRegistry().getTask(taskId);
        if(actionableTask == null){
            getLogger().warn(".notifyTaskWaiting(): Exit, could not find ActionableTask for given TaskId!!!");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }
        getLogger().trace(".notifyTaskWaiting(): [Get the ActionableTask from Registry] Finish");

        getLogger().trace(".notifyTaskWaiting(): [Update ActionableTask Basic Status] Start");
        actionableTask.setUpdateInstant(Instant.now());
        actionableTask.getExecutionControl().setExecutionCommand(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
        getLogger().trace(".notifyTaskWaiting(): [Update ActionableTask Basic Status] Finish");

        getLogger().trace(".notifyTaskWaiting(): [Update Local Job Card] Start");
        PetasosTaskJobCard jobCard = getLocalTaskJobCardCache().getJobCard(taskId);
        jobCard.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
        jobCard.setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_WAITING);
        jobCard.setUpdateInstant(Instant.now());
        getLogger().trace(".notifyTaskWaiting(): [Update Local Job Card] Finish");

        getLogger().trace(".notifyTaskWaiting(): [Synchronise with Ponos] Start");
        PetasosTaskJobCard centralJobCard = getTaskRepositoryService().registerTaskWaiting(jobCard);
        getLogger().trace(".notifyTaskWaiting(): [Synchronise with Ponos] Finish");

        getLogger().trace(".notifyTaskWaiting(): [Update Local Job Card based on Central JobCard] Start");
        if(centralJobCard != null) {
            jobCard.setCurrentStatus(centralJobCard.getCurrentStatus());
            jobCard.setGrantedStatus(centralJobCard.getGrantedStatus());
            jobCard.setUpdateInstant(centralJobCard.getUpdateInstant());
        }
        getLogger().trace(".notifyTaskWaiting(): [Update Local Job Card based on Central JobCard] Finish");

        getLogger().trace(".notifyTaskWaiting(): [Update ActionableTask based on Central JobCard] Start");
        actionableTask.getExecutionControl().setExecutionCommand(centralJobCard.getGrantedStatus());
        getLogger().trace(".notifyTaskWaiting(): [Update ActionableTask based on Central JobCard] Finish");

        //
        // Extract Task Status
        TaskExecutionCommandEnum executionStatus = centralJobCard.getGrantedStatus();

        getLogger().debug(".notifyTaskWaiting(): Exit, executionStatus->{}", executionStatus);
        return(executionStatus);
    }

    public TaskExecutionCommandEnum notifyTaskFinalisation(TaskIdType taskId) {
        getLogger().debug(".notifyTaskFinalisation(): Entry, taskId->{}", taskId);
        if(taskId == null){
            getLogger().debug(".notifyTaskFinalisation(): Exit, taskId is null");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }

        getLogger().trace(".notifyTaskFinalisation(): [Get the ActionableTask from Registry] Start");
        PetasosActionableTask actionableTask = getLocalActionableTaskRegistry().getTask(taskId);
        if(actionableTask == null){
            getLogger().warn(".notifyTaskFinalisation(): Exit, could not find ActionableTask for given TaskId!!!");
            return(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
        }
        getLogger().trace(".notifyTaskFinalisation(): [Get the ActionableTask from Registry] Finish");

        getLogger().trace(".notifyTaskFinalisation(): [Update ActionableTask Basic Status] Start");
        actionableTask.setUpdateInstant(Instant.now());
        actionableTask.getExecutionControl().setExecutionCommand(TaskExecutionCommandEnum.TASK_COMMAND_FINALISE);
        getLogger().trace(".notifyTaskFinalisation(): [Update ActionableTask Basic Status] Finish");

        getLogger().trace(".notifyTaskFinalisation(): [Update ActionableTask.taskOutcomeStatus] Start");
        if(actionableTask.getTaskOutcomeStatus() == null){
            TaskOutcomeStatusType outcomeStatus = new TaskOutcomeStatusType();
            actionableTask.setTaskOutcomeStatus(outcomeStatus);
        }
        actionableTask.getTaskOutcomeStatus().setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_FINALISED);
        actionableTask.getTaskOutcomeStatus().setEntryInstant(Instant.now());
        getLogger().trace(".notifyTaskFinalisation(): [Update ActionableTask.taskOutcomeStatus] Start");

        getLogger().trace(".notifyTaskFinalisation(): [Update Local Job Card] Start");
        PetasosTaskJobCard jobCard = getLocalTaskJobCardCache().getJobCard(taskId);
        jobCard.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_FINALISE);
        jobCard.setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_FINALISED);
        jobCard.setUpdateInstant(Instant.now());
        getLogger().trace(".notifyTaskFinalisation(): [Update Local Job Card] Finish");

        getLogger().trace(".notifyTaskFinalisation(): [Synchronise with Ponos] Start");
        PetasosTaskJobCard centralJobCard = getTaskRepositoryService().registerTaskFinalisation(jobCard);
        getLogger().trace(".notifyTaskFinalisation(): [Synchronise with Ponos] Finish");

        getLogger().trace(".notifyTaskFinalisation(): [Update Local Job Card based on Central JobCard] Start");
        if(centralJobCard != null) {
            jobCard.setCurrentStatus(centralJobCard.getCurrentStatus());
            jobCard.setGrantedStatus(centralJobCard.getGrantedStatus());
            jobCard.setUpdateInstant(centralJobCard.getUpdateInstant());
        }
        getLogger().trace(".notifyTaskFinalisation(): [Update Local Job Card based on Central JobCard] Finish");

        getLogger().trace(".notifyTaskFinalisation(): [Update ActionableTask based on Central JobCard] Start");
        actionableTask.getExecutionControl().setExecutionCommand(centralJobCard.getGrantedStatus());
        getLogger().trace(".notifyTaskFinalisation(): [Update ActionableTask based on Central JobCard] Finish");

        //
        // Extract Task Status
        TaskExecutionCommandEnum executionStatus = centralJobCard.getGrantedStatus();

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

    protected LocalTaskQueueCache getLocalTaskQueueManager(){
        return(localTaskQueueCache);
    }

    protected LocalActionableTaskCache getLocalActionableTaskRegistry(){
        return(localActionableTaskCache);
    }
    
    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlant);
    }

    protected LocalTaskJobCardCache getLocalTaskJobCardCache(){
        return(localTaskJobCardCache);
    }

    protected LocalFulfillmentTaskRegistry getLocalFulfillmentTaskRegistry(){
        return(localFulfillmentTaskRegistry);
    }

    protected PetasosFulfillmentTaskAuditServicesBroker getAuditServicesBroker(){
        return(auditServicesBroker);
    }

    protected PetasosTaskJobCardFactory getTaskJobCardFactory(){
        return(taskJobCardFactory);
    }

}
