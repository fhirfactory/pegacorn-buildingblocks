/*
 * Copyright (c) 2022 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.queue;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.TaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.queue.ParticipantTaskQueueEntry;
import net.fhirfactory.pegacorn.petasos.core.participants.management.LocalParticipantManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.cache.LocalActionableTaskCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.distribution.LocalTaskDistributionService;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.execution.LocalParticipantExecutionManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.execution.LocalTaskActivityManager;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class LocalTaskQueueManager {
    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskQueueManager.class);

    private Integer retryDelay;

    private boolean initialised;

    private Integer participantQueueSizeMaximum;
    private Integer participantQueueSizeOffloadThreshold;
    private Integer participantQueueSizeOnloadThreshold;
    private Integer queueBatchSize;



    private static final Integer DEFAULT_TASK_RETRY_DELAY = 30;

    @Inject
    private LocalTaskQueueCache localTaskQueueCache;

    @Inject
    private LocalActionableTaskCache localTaskCache;

    @Produce
    private ProducerTemplate camelDistributor;

    @Inject
    private LocalParticipantManager localParticipantManager;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private LocalTaskDistributionService localTaskDistributionService;

    @Inject
    private LocalTaskActivityManager taskActivityManager;

    @Inject
    private LocalParticipantExecutionManager participantExecutionManager;

    //
    // Constructor(s)
    //

    public LocalTaskQueueManager(){
        this.initialised = false;
        setRetryDelay(DEFAULT_TASK_RETRY_DELAY);
        setQueueBatchSize(PetasosPropertyConstants.PARTICIPANT_QUEUE_TASK_BATCH_SIZE);
        setParticipantQueueSizeMaximum(PetasosPropertyConstants.PARTICIPANT_QUEUE_SIZE);
        setParticipantQueueSizeOffloadThreshold(PetasosPropertyConstants.PARTICIPANT_QUEUE_OFFLOAD_THRESHOLD);
        setParticipantQueueSizeOnloadThreshold(PetasosPropertyConstants.PARTICIPANT_QUEUE_ONLOAD_THRESHOLD);
    }

    //
    // PostConstructor(s)
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(initialised){
            getLogger().debug(".initialise(): Exit, already initialised, nothing to do!");
            return;
        } else {
            getLogger().info(".initialise(): Initialisation Start!");
            getLogger().info(".initialise(): [Set Task Retry Delay Period] Start");
            String taskFailureRetryDelay = processingPlant.getTopologyNode().getOtherConfigurationParameter("TASK_FAILURE_RETRY_DELAY");
            if(StringUtils.isNotEmpty(taskFailureRetryDelay)){
                Integer retryDelayFromConfig = Integer.valueOf(taskFailureRetryDelay);
                if(retryDelayFromConfig != null){
                    setRetryDelay(retryDelayFromConfig);
                }
            }
            getLogger().info(".initialise(): [Set Task Retry Delay Period] Finish");
            getLogger().info(".initialise(): [Set Participant Queue Size] Start");
            String taskQueueSize = processingPlant.getTopologyNode().getOtherConfigurationParameter("PARTICIPANT_QUEUE_SIZE");
            if(StringUtils.isNotEmpty(taskQueueSize)){
                try {
                    Integer taskQueueSizeFromConfig = Integer.valueOf(taskQueueSize);
                    if (taskQueueSizeFromConfig > 0) {
                        setParticipantQueueSizeMaximum(taskQueueSizeFromConfig);
                    }
                } catch(Exception ex){
                    getLogger().debug(".initialise(): Unable to resolve participant queue size, using default. Error->", ex);
                }
            }
            getLogger().info(".initialise(): [Set Participant Queue Size] Finish");
            getLogger().info(".initialise(): [Set Participant Queue Offload Threshold Size] Start");
            String taskQueueThresholdSize = processingPlant.getTopologyNode().getOtherConfigurationParameter("PARTICIPANT_QUEUE_OFFLOAD_THRESHOLD");
            if(StringUtils.isNotEmpty(taskQueueThresholdSize)){
                try {
                    Integer taskQueueThresholdSizeFromConfig = Integer.valueOf(taskQueueThresholdSize);
                    if (taskQueueThresholdSizeFromConfig > 0) {
                        setParticipantQueueSizeOffloadThreshold(taskQueueThresholdSizeFromConfig);
                    }
                } catch(Exception ex){
                    getLogger().debug(".initialise(): Unable to resolve participant queue threshold size, using default. Error->", ex);
                }
            }
            getLogger().info(".initialise(): [Set Participant Queue Offload Threshold Size] Finish");
            getLogger().info(".initialise(): [Set Participant Queue Onload Threshold Size] Start");
            String taskQueueOnloadThresholdSize = processingPlant.getTopologyNode().getOtherConfigurationParameter("PARTICIPANT_QUEUE_ONLOAD_THRESHOLD");
            if(StringUtils.isNotEmpty(taskQueueOnloadThresholdSize)){
                try {
                    Integer taskQueueOnloadThresholdSizeFromConfig = Integer.valueOf(taskQueueOnloadThresholdSize);
                    if (taskQueueOnloadThresholdSizeFromConfig > 0) {
                        setParticipantQueueSizeOffloadThreshold(taskQueueOnloadThresholdSizeFromConfig);
                    }
                } catch(Exception ex){
                    getLogger().debug(".initialise(): Unable to resolve participant queue onload threshold size, using default. Error->", ex);
                }
            }
            getLogger().info(".initialise(): [Set Participant Queue Onload Threshold Size] Finish");
            getLogger().info(".initialise(): [Set Participant Queue Task Batch Size] Start");
            String taskBatchSize = processingPlant.getTopologyNode().getOtherConfigurationParameter("PARTICIPANT_QUEUE_TASK_BATCH_SIZE");
            if(StringUtils.isNotEmpty(taskBatchSize)){
                try {
                    Integer taskBatchSizeFromConfig = Integer.valueOf(taskBatchSize);
                    if (taskBatchSizeFromConfig > 0) {
                        setQueueBatchSize(taskBatchSizeFromConfig);
                    }
                } catch(Exception ex){
                    getLogger().debug(".initialise(): Unable to resolve task batch size, using default. Error->", ex);
                }
            }
            getLogger().info(".initialise(): [Set Participant Queue Task Batch Size] Finish");

            getLogger().info(".initialise(): Initialisation Finish!");
        }
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Getters (and Setters)
    //

    public Integer getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(Integer retryDelay) {
        this.retryDelay = retryDelay;
    }

    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlant);
    }

    protected boolean isInitialised() {
        return initialised;
    }

    protected void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    protected Logger getLogger(){
        return(LOG);
    }

    protected LocalTaskQueueCache getLocalTaskQueueCache(){
        return(localTaskQueueCache);
    }

    protected ProducerTemplate getCamelDistributor(){
        return(camelDistributor);
    }

    protected LocalParticipantManager getLocalParticipantManager(){
        return(localParticipantManager);
    }

    protected LocalActionableTaskCache getLocalTaskCache(){
        return(localTaskCache);
    }

    protected LocalTaskDistributionService getLocalTaskDistributionService(){
        return(localTaskDistributionService);
    }
    protected LocalTaskActivityManager getTaskActivityManager(){
        return(taskActivityManager);
    }

    protected Integer getParticipantQueueSizeMaximum() {
        return participantQueueSizeMaximum;
    }

    protected void setParticipantQueueSizeMaximum(Integer participantQueueSizeMaximum) {
        this.participantQueueSizeMaximum = participantQueueSizeMaximum;
    }

    protected Integer getParticipantQueueSizeOffloadThreshold() {
        return participantQueueSizeOffloadThreshold;
    }

    protected void setParticipantQueueSizeOffloadThreshold(Integer participantQueueSizeOffloadThreshold) {
        this.participantQueueSizeOffloadThreshold = participantQueueSizeOffloadThreshold;
    }

    public Integer getQueueBatchSize() {
        return queueBatchSize;
    }

    protected void setQueueBatchSize(Integer queueBatchSize) {
        this.queueBatchSize = queueBatchSize;
    }

    protected LocalParticipantExecutionManager getParticipantExecutionManager(){
        return(participantExecutionManager);
    }

    public Integer getParticipantQueueSizeOnloadThreshold() {
        return participantQueueSizeOnloadThreshold;
    }

    protected void setParticipantQueueSizeOnloadThreshold(Integer participantQueueSizeOnloadThreshold) {
        this.participantQueueSizeOnloadThreshold = participantQueueSizeOnloadThreshold;
    }



    //
    // Queue Input / Queue Output
    //

    public boolean queueTask(PetasosActionableTask actionableTask){
        getLogger().debug(".queueTask(): Entry, actionableTask->{}", actionableTask);

        getLogger().debug(".queueTask(): [Checking actionableTask] Start" );
        if(actionableTask == null){
            getLogger().debug(".queueTask(): [Checking actionableTask] is null, exiting");
            return(false);
        }
        getLogger().debug(".queueTask(): [Checking actionableTask] Finish (not null)" );

        getLogger().trace(".queueTask(): [Check Task for TaskPerformer] Start");
        if(!actionableTask.hasTaskPerformerTypes()){
            getLogger().warn(".queueTask(): [Check Task for TaskPerformer] has no performerType object, exiting");
            return(false);
        }
        if(actionableTask.getTaskPerformerTypes().isEmpty()){
            getLogger().warn(".queueTask(): [Check Task for TaskPerformer] performerType list is empty, exiting");
            return(false);
        }
        getLogger().trace(".queueTask(): [Check Task for TaskPerformer] Finish, has at least 1 performer");

        getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] Start");
        boolean taskOffloaded = false;
        boolean taskQueued = false;
        boolean taskForwarded = false;
        for(TaskPerformerTypeType currentPerformer: actionableTask.getTaskPerformerTypes()) {
            getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] processing taskPerformer->{}", currentPerformer);
            boolean performerRequiringTaskOffload = isPerformerRequiringTaskOffload(currentPerformer);
            getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] performerRequiringTaskOffload->{}", performerRequiringTaskOffload);
            if(performerRequiringTaskOffload) {
                // If there are offloaded tasks, or the performer is suspended, we don't want to add to the local queue
                // OR the local cache and since we've already registered the task centrally, lets just get rid of it locally.
                getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] Task Cleared Locally, performer has Offloaded Tasks or is Suspended :)");
                getLocalTaskCache().removeTaskFromDirectory(actionableTask.getTaskId());
                taskOffloaded = true;
            } else {
                boolean performerIsIdle = getParticipantExecutionManager().isTaskPerformerIdle(currentPerformer);
                boolean performerQueueIsEmpty = isPerformerQueueEmpty(currentPerformer);
                getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] performerIsIdle->{}, performerIsSuspended->{}, performerQueueIsEmpty->{}", performerIsIdle, performerQueueIsEmpty);
                if (performerQueueIsEmpty && performerIsIdle) {
                    // The task queue is empty AND the taskPerformer is idle, so forward this task directly to it!
                    if (currentPerformer.getKnownTaskPerformer() != null) {
                        getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] Not Queueing Task, Forwarding directly into WUP");
                        sendQueuedTask(currentPerformer.getKnownTaskPerformer(), actionableTask);
                        taskForwarded = true;
                    } else {
                        getLogger().warn(".queueTask(): [Queue to ALL TaskPerformers] No known TaskPerformer for task");
                    }
                } else {
                    // Let's just add the task to the performer's Queue!
                    if (currentPerformer.getKnownTaskPerformer() != null) {
                        if (StringUtils.isNotEmpty(currentPerformer.getKnownTaskPerformer().getName())) {
                            getLocalTaskQueueCache().queueTask(currentPerformer.getKnownTaskPerformer().getName(), actionableTask);
                            taskQueued = true;
                            getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] Task Queued");
                        } else {
                            getLogger().warn(".queueTask(): [Queue to ALL TaskPerformers] TaskPerformer has invalid Participant Name");
                        }
                    } else {
                        getLogger().warn(".queueTask(): [Queue to ALL TaskPerformers] No known TaskPerformer for task");
                    }
                }
            }
        }
        getLogger().debug(".queueTask(): [Queue to ALL TaskPerformers] Finish, taskOffloaded->{}, taskQueued->{}, taskForwarded->{}", taskOffloaded, taskQueued, taskForwarded);

        boolean processedSuccessfully = taskQueued || taskForwarded;

        getLogger().debug(".queueTask(): Exit, processedSuccessfully->{}", processedSuccessfully);
        return(processedSuccessfully);
    }


    /**
     * This method "forwards" the given PetasosActionableTask (actionableTask) into the task distribution service.
     * @param actionableTask
     */
    public void sendQueuedTask(PetasosParticipantId participantId, PetasosActionableTask actionableTask){
        getLogger().debug(".sendQueuedTask(): Entry, participantId->{}, actionableTask->{}", participantId, actionableTask);
        if(actionableTask != null) {
            getLocalTaskDistributionService().distributeTask(participantId, actionableTask);
            getCamelDistributor().sendBody(PetasosPropertyConstants.TASK_DISTRIBUTION_QUEUE, ExchangePattern.InOnly, actionableTask);
        }
        getLogger().debug(".sendQueuedTask(): Exit");
    }

    /**
     * This method checks the TaskQueue for the given Participant (Name) and ascertains if a task can be "commenced" (or
     * forwarded into the task distribution service).
     *
     * @param participantName The name of the participant (ParticipantId.getName()) whose TaskQueue is to be checked/processed.
     */
    public void processNextQueuedTaskForParticipant(String participantName) {
        getLogger().debug(".processNextQueuedTaskForParticipant(): Entry, participantName->{}", participantName);

        if (StringUtils.isEmpty(participantName)) {
            getLogger().debug(".processNextQueuedTaskForParticipant(): Exit, participantName is null");
            return;
        }

        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Participant is Suspended] Start");
        if (getLocalParticipantManager().isParticipantSuspended(participantName)){
            getLogger().warn(".processNextQueuedTaskForParticipant(): Exit, participant is suspended");
            return;
        }
        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Participant is Suspended] Finish -> Not Suspended");

        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Any Pending Tasks] Start");
        if (getLocalTaskQueueCache().getParticipantQueueSize(participantName) <= 0) {
            getLogger().warn(".processNextQueuedTaskForParticipant(): Exit, participant has no pending tasks");
            return;
        }
        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Any Pending Tasks] Finish -> Has Pending Tasks");

        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Participant is Busy] Start");
        boolean isParticipantIdle = getParticipantExecutionManager().isTaskPerformerIdle(participantName);
        if (!isParticipantIdle){
            getLogger().warn(".processNextQueuedTaskForParticipant(): Exit, participant is busy");
            return;
        }
        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Participant is Busy] Finish, participant is IDLE");

        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Valid Task] Start");
        ParticipantTaskQueueEntry participantTaskQueueEntry = getLocalTaskQueueCache().peekNextTask(participantName);
        PetasosActionableTask task = getLocalTaskCache().getTask(participantTaskQueueEntry.getTaskId());
        if(task == null){
            getLocalTaskQueueCache().pollNextTask(participantName);
            getLogger().warn(".processNextQueuedTaskForParticipant(): Exit, task is invalid");
            return;
        }
        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Valid Task] Finish -> Task is Valid");

        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Participant is Disabled] Start");
        if (getLocalParticipantManager().isParticipantDisabled(participantName)){
            ParticipantTaskQueueEntry actualQueueEntry = getLocalTaskQueueCache().pollNextTask(participantName);
            PetasosActionableTask actionableTask = getLocalTaskCache().getTask(actualQueueEntry.getTaskId());
            if(actionableTask != null){
                actionableTask.getTaskOutcomeStatus().setOutcomeStatus(TaskOutcomeStatusEnum.OUTCOME_STATUS_CANCELLED);
                actionableTask.getTaskOutcomeStatus().setEntryInstant(Instant.now());
                if(!actionableTask.hasTaskFulfillment()) {
                    actionableTask.setTaskFulfillment(new TaskFulfillmentType());
                }
                actionableTask.getTaskFulfillment().setFinishInstant(Instant.now());
                actionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_CANCELLED);
                actionableTask.getTaskFulfillment().setCurrentStateReason("Participant is Disabled");
                getTaskActivityManager().registerTaskOutcome(actionableTask, null);
            }
            getLogger().warn(".processNextQueuedTaskForParticipant(): Exit, participant is Disabled");
            return;
        }
        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Participant is Suspended] Finish -> Not Suspended");

        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Retry & Retry-Delay Passed] Start");
        boolean delayExecution = false;
        if (task.hasTaskTraceability()) {
            if (task.getTaskTraceability().isaRetry()) {
                Instant lastUtilisationInstant = getLocalParticipantManager().getParticipantLastUtilisationInstant(participantName);
                if (Instant.now().isBefore(lastUtilisationInstant.plusSeconds(getRetryDelay()))) {
                    delayExecution = true;
                }
            }
        }
        if(delayExecution) {
            getLogger().warn(".processNextQueuedTaskForParticipant(): Exit, Task is delayed until retry delay passes");
            return;
        }
        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Retry & Retry-Delay Passed] Finish, not delayed");

        getLogger().trace(".processNextQueuedTaskForParticipant(): [Initiate Task Fulfillment] Start");
        getLocalTaskQueueCache().pollNextTask(participantName);
        PetasosParticipantId participantId = getLocalParticipantManager().getParticipantId(participantName);
        if(participantId != null) {
            getLogger().warn(".processNextQueuedTaskForParticipant(): [Initiate Task Fulfillment] sending task to be processed!");
            sendQueuedTask(participantId, task);
        }
        getLogger().trace(".processNextQueuedTaskForParticipant(): [Initiate Task Fulfillment] Finish");

        getLogger().debug(".processNextQueuedTaskForParticipant(): Exit");
    }

    //
    // Queue Operating Mode
    //

    /**
     * This method returns -true- if the participant is either NOT enabled (i.e. the participant is Suspended or Disabled)
     * or if the participant's existing queue size is bigger then the Queue Threshold Size.
     *
     * Note that if a task is not be queued locally, it should also not be cached locally either.
     *
     * @param performerType The performerType for the task (typically a known Participant Id)
     * @return True if tasks should be offloaded (not queued locally), false if should be queued locally
     */
    protected boolean isPerformerRequiringTaskOffload(TaskPerformerTypeType performerType){
        getLogger().debug(".isPerformerRequiringTaskOffload(): Entry, performerType->{}", performerType);
        boolean isRequiringOffload = false;
        if(performerType != null){
            if(performerType.getKnownTaskPerformer() != null){
                isRequiringOffload = isPerformerRequiringTaskOffload(performerType.getKnownTaskPerformer().getName());
            }
        }
        getLogger().debug(".isPerformerRequiringTaskOffload(): Exit, isRequiringOffload->{}", isRequiringOffload);
        return(isRequiringOffload);
    }

    /**
     * This method returns -true- if the participant is either NOT enabled (i.e. the participant is Suspended or Disabled)
     * or if the participant's existing queue size is bigger then the Queue Threshold Size.
     *
     * Note that if a task is not be queued locally, it should also not be cached locally either.
     *
     * @param participantName The participant's name
     * @return True if tasks should be offloaded (not queued locally), false if should be queued locally
     */
    public boolean isPerformerRequiringTaskOffload(String participantName){
        getLogger().debug(".isPerformerRequiringTaskOffload(): Entry, participantName->{}", participantName);

        if (StringUtils.isEmpty(participantName)) {
            getLogger().debug(".isPerformerRequiringTaskOffload(): Exit, participantName is empty, returning false");
            return (false);
        }
        boolean isPerformerEnabled = getParticipantExecutionManager().isTaskPerformerEnabled(participantName);
        boolean isExistingQueueSizePastThreshold = getLocalTaskQueueCache().getParticipantQueueSize(participantName) > getParticipantQueueSizeOffloadThreshold();
        boolean isRequiringOffload = !isPerformerEnabled || isExistingQueueSizePastThreshold;
        getLogger().debug(".isPerformerRequiringTaskOffload(): Exit, isRequiringOffload->{}", isRequiringOffload);
        return(isRequiringOffload);
    }

    protected boolean performerHasOffloadedTasks(TaskPerformerTypeType performerType){
        getLogger().debug(".performerHasOffloadedTasks(): Entry, performerType->{}", performerType);
        boolean hasOffloadedTasks = false;
        if(performerType != null){
            if(performerType.getKnownTaskPerformer() != null){
                hasOffloadedTasks = performerHasOffloadedTasks(performerType.getKnownTaskPerformer().getName());
            }
        }
        getLogger().debug(".performerHasOffloadedTasks(): Exit, hasOffloadedTasks->{}", hasOffloadedTasks);
        return(hasOffloadedTasks);
    }

    protected boolean performerHasOffloadedTasks(String performerName){
        if (StringUtils.isEmpty(performerName)) {
            getLogger().debug(".performerHasOffloadedTasks(): Exit, performerName is empty, returning false");
            return (false);
        }
        boolean performerHasOffloadedTasks = getLocalParticipantManager().participantHasOffloadedTasks(performerName);
        getLogger().debug(".performerHasOffloadedTasks(): Exit, performerHasOffloadedTasks->{}", performerHasOffloadedTasks);
        return(performerHasOffloadedTasks);
    }

    protected boolean isPerformerQueueEmpty(TaskPerformerTypeType performerType){
        if(performerType == null){
            return(false);
        }
        if(performerType.getKnownTaskPerformer() == null){
            return(false);
        }
        if(StringUtils.isEmpty(performerType.getKnownTaskPerformer().getName())){
            return(false);
        }
        if( getLocalTaskQueueCache().getParticipantQueueSize(performerType.getKnownTaskPerformer().getName()) <= 0){
            return(true);
        } else {
            return (false);
        }
    }

    public Set<ParticipantTaskQueueEntry> getOffloadTaskBatch(String participantName){
        if(StringUtils.isEmpty(participantName)){
            return(new HashSet<>());
        }
        Set<ParticipantTaskQueueEntry> offloadTaskBatch = getLocalTaskQueueCache().getLastNTasks(participantName, getParticipantQueueSizeOnloadThreshold(), getQueueBatchSize());
        return(offloadTaskBatch);
    }
}
