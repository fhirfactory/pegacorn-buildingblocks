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
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.TaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.petasos.core.participants.management.LocalParticipantManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.cache.LocalActionableTaskCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.distribution.LocalTaskDistributionService;
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
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class LocalTaskQueueManager {
    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskQueueManager.class);

    private Integer retryDelay;

    private boolean daemonStillRunning;
    private boolean initialised;
    private Instant daemonLastRunTime;

    private static final Long TASK_QUEUE_CHECK_PERIOD = 10000L;
    private static final Long TASK_QUEUE_CHECK_STARTUP_WAIT = 60000L;
    private static final Long TASK_QUEUE_CHECK_MAX_PERIOD = 120000L;

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

    //
    // Constructor(s)
    //

    public LocalTaskQueueManager(){
        this.initialised = false;
        setRetryDelay(DEFAULT_TASK_RETRY_DELAY);
    }

    //
    // PostConstructor(s)
    //

    @PostConstruct
    public void initialise(){
        if(initialised){
            // do nothing
        } else {
            String taskFailureRetryDelay = processingPlant.getTopologyNode().getOtherConfigurationParameter("TASK_FAILURE_RETRY_DELAY");
            if(StringUtils.isNotEmpty(taskFailureRetryDelay)){
                Integer retryDelayFromConfig = Integer.valueOf(taskFailureRetryDelay);
                if(retryDelayFromConfig != null){
                    setRetryDelay(retryDelayFromConfig);
                }
            }
            scheduleTaskQueueCheckDaemon();
        }
    }

    //
    // Daemons
    //

    private void scheduleTaskQueueCheckDaemon() {
        getLogger().debug(".scheduleTaskQueueCheckDaemon(): Entry");
        TimerTask taskQueueCheckDaemonTimerTask = new TimerTask() {
            public void run() {
                getLogger().debug(".taskQueueCheckDaemonTimerTask(): Entry");
                if (!isDaemonStillRunning()) {
                    taskQueueCheckDaemon();
                    setDaemonLastRunTime(Instant.now());
                } else {
                    Long ageSinceRun = Instant.now().getEpochSecond() - getDaemonLastRunTime().getEpochSecond();
                    if (ageSinceRun > TASK_QUEUE_CHECK_MAX_PERIOD) {
                        taskQueueCheckDaemon();
                        setDaemonLastRunTime(Instant.now());
                    }
                }
                getLogger().debug(".taskQueueCheckDaemonTimerTask(): Exit");
            }
        };
        Timer timer = new Timer("TaskQueueDaemonTimer");
        timer.schedule(taskQueueCheckDaemonTimerTask, TASK_QUEUE_CHECK_STARTUP_WAIT, TASK_QUEUE_CHECK_PERIOD);
        getLogger().debug(".scheduleTaskQueueCheckDaemon(): Exit");
    }

    private void taskQueueCheckDaemon(){
        getLogger().debug(".taskQueueCheckDaemon(): Entry");
        setDaemonStillRunning(true);
        try {
            Set<String> allRegisteredComponent = getLocalParticipantManager().getAllRegisteredComponentIds();
            for (String currentLocalRegisteredComponentIdValue : allRegisteredComponent) {
                getLogger().trace(".taskQueueCheckDaemon(): Processing component->{}", currentLocalRegisteredComponentIdValue);
                PetasosParticipantRegistration currentLocalRegistration = getLocalParticipantManager().getLocalParticipantRegistration(currentLocalRegisteredComponentIdValue);
                if(currentLocalRegistration != null) {
                    String currentParticipantName = currentLocalRegistration.getParticipantId().getName();
                    getLogger().trace(".taskQueueCheckDaemon(): Processing participant->{}", currentParticipantName);
                    Integer participantQueueSize = getLocalTaskQueueCache().getParticipantQueueSize(currentParticipantName);
                    if (participantQueueSize > 0) {
                        getLogger().trace(".taskQueueCheckDaemon(): Processing participant->{}, queueSize->{}", currentParticipantName, participantQueueSize);
                        boolean participantIsIdle = isTaskPerformerIdle(currentParticipantName);
                        getLogger().trace(".taskQueueCheckDaemon(): Processing participant->{} isIdle->{}", currentParticipantName, participantIsIdle);
                        boolean participantIsEnabled = isTaskPerformerEnabled(currentParticipantName);
                        getLogger().trace(".taskQueueCheckDaemon(): Processing participant->{} isEnabled->{}", currentParticipantName, participantIsEnabled);
                        if (participantIsIdle && participantIsEnabled) {
                            getLogger().trace(".taskQueueCheckDaemon(): Processing participant->{} Queue", currentParticipantName);
                            processNextQueuedTaskForParticipant(currentParticipantName);
                        }
                    }
                }
            }
        } catch (Exception ex){
            getLogger().warn(".taskQueueCheckDaemon encountered an error, exception->", ex);
        }
        setDaemonStillRunning(false);
        getLogger().debug(".taskQueueCheckDaemon(): Exit");
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

    public Instant getDaemonLastRunTime() {
        return daemonLastRunTime;
    }

    public void setDaemonLastRunTime(Instant daemonLastRunTime) {
        this.daemonLastRunTime = daemonLastRunTime;
    }

    public boolean isDaemonStillRunning() {
        return daemonStillRunning;
    }

    public void setDaemonStillRunning(boolean daemonStillRunning) {
        this.daemonStillRunning = daemonStillRunning;
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
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

    //
    // Queue Input / Queue Output
    //

    public void queueTask(PetasosActionableTask actionableTask){
        getLogger().debug(".queueTask(): Entry, actionableTask->{}", actionableTask);

        getLogger().debug(".queueTask(): [Checking actionableTask] Start" );
        if(actionableTask == null){
            getLogger().debug(".queueTask(): [Checking actionableTask] is null, exiting");
            return;
        }
        getLogger().debug(".queueTask(): [Checking actionableTask] Finish (not null)" );

        getLogger().trace(".queueTask(): [Check Task for TaskPerformer] Start");
        if(!actionableTask.hasTaskPerformerTypes()){
            getLogger().debug(".queueTask(): [Check Task for TaskPerformer] has no performerType object, exiting");
            return;
        }
        if(actionableTask.getTaskPerformerTypes().isEmpty()){
            getLogger().debug(".queueTask(): [Check Task for TaskPerformer] performerType list is empty, exiting");
            return;
        }
        getLogger().trace(".queueTask(): [Check Task for TaskPerformer] Finish, has at least 1 performer");

        getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] Start");
        for(TaskPerformerTypeType currentPerformer: actionableTask.getTaskPerformerTypes()) {
            getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] processing taskPerformer->{}", currentPerformer);
            boolean performerIsIdle = isTaskPerformerIdle(currentPerformer);
            boolean performerIsSuspended = getLocalParticipantManager().isParticipantSuspended(currentPerformer);
            boolean performerQueueIsEmpty = isTaskPerformerQueueEmpty(currentPerformer);
            getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] performerIsIdle->{}, performerIsSuspended->{}, performerQueueIsEmpty->{}", performerIsIdle, performerIsSuspended, performerQueueIsEmpty);
            boolean taskQueuedOrForwarded = false;
            if (!performerQueueIsEmpty && performerIsIdle && !performerIsSuspended) {

                if(currentPerformer.getKnownTaskPerformer() != null) {
                    getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] Not Queueing Task, Forwarding directly into WUP");
                    sendQueuedTask(currentPerformer.getKnownTaskPerformer(), actionableTask);
                    taskQueuedOrForwarded = true;
                } else {
                    getLogger().warn(".queueTask(): [Queue to ALL TaskPerformers] No known TaskPerformer for task");
                }
            } else {
                if(currentPerformer.getKnownTaskPerformer() != null) {
                    if (StringUtils.isNotEmpty(currentPerformer.getKnownTaskPerformer().getName())) {
                        getLocalTaskQueueCache().queueTask(currentPerformer.getKnownTaskPerformer().getName(), actionableTask);
                        taskQueuedOrForwarded = true;
                        getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] Task Queued");
                    } else {
                        getLogger().warn(".queueTask(): [Queue to ALL TaskPerformers] TaskPerformer has invalid Participant Name");
                    }
                } else {
                    getLogger().warn(".queueTask(): [Queue to ALL TaskPerformers] No known TaskPerformer for task");
                }
            }
            if(taskQueuedOrForwarded){
                getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] Task Queued or Forwarded :)");
            } else {
                getLogger().warn(".queueTask(): [Queue to ALL TaskPerformers] Task NOT Queued or Forwarded :(");
            }
        }
        getLogger().trace(".queueTask(): [Queue to ALL TaskPerformers] Finish");

        getLogger().debug(".queueTask(): Exit");
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
            getLogger().debug(".processNextQueuedTaskForParticipant(): Exit, participant is suspended");
            return;
        }
        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Participant is Suspended] Finish -> Not Suspended");

        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Any Pending Tasks] Start");
        if (getLocalTaskQueueCache().getParticipantQueueSize(participantName) <= 0) {
            getLogger().debug(".processNextQueuedTaskForParticipant(): Exit, participant has no pending tasks");
            return;
        }
        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Any Pending Tasks] Finish -> Has Pending Tasks");

        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Valid Task] Start");
        ParticipantTaskQueueEntry participantTaskQueueEntry = getLocalTaskQueueCache().peekNextTask(participantName);
        PetasosActionableTask task = getLocalTaskCache().getTask(participantTaskQueueEntry.getTaskId());
        if(task == null){
            getLocalTaskQueueCache().pollNextTask(participantName);
            getLogger().debug(".processNextQueuedTaskForParticipant(): Exit, task is invalid");
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
            getLogger().debug(".processNextQueuedTaskForParticipant(): Exit, participant is suspended");
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
            getLogger().debug(".processNextQueuedTaskForParticipant(): Exit, Task is delayed until retry delay passes");
            return;
        }
        getLogger().trace(".processNextQueuedTaskForParticipant(): [Check if Retry & Retry-Delay Passed] Finish, not delayed");

        getLogger().trace(".processNextQueuedTaskForParticipant(): [Initiate Task Fulfillment] Start");
        getLocalTaskQueueCache().pollNextTask(participantName);
        PetasosParticipantId participantId = getLocalParticipantManager().getParticipantId(participantName);
        if(participantId != null) {
            sendQueuedTask(participantId, task);
        }
        getLogger().trace(".processNextQueuedTaskForParticipant(): [Initiate Task Fulfillment] Finish");

        getLogger().debug(".processNextQueuedTaskForParticipant(): Exit");
    }

    //
    // Helper Methods
    //

    protected boolean isTaskPerformerEnabled(TaskPerformerTypeType performerType) {
        getLogger().debug(".isTaskPerformerEnabled(): Entry, performerType->{}", performerType);
        if (performerType == null) {
            getLogger().debug(".isTaskPerformerEnabled(): Exit, performerType is null, returning false");
            return (false);
        }
        if (performerType.getKnownTaskPerformer() == null) {
            getLogger().debug(".isTaskPerformerEnabled(): Exit, performerType.getKnownTaskPerformer() is null, returning false");
            return (false);
        }
        if (StringUtils.isEmpty(performerType.getKnownTaskPerformer().getName())) {
            getLogger().debug(".isTaskPerformerEnabled(): Exit, performerType.getKnownTaskPerformer().getName() is empty, returning false");
            return (false);
        }
        boolean performerEnabled = isTaskPerformerEnabled(performerType.getKnownTaskPerformer().getName());
        getLogger().debug(".isTaskPerformerEnabled(): Exit, performerEnabled->{}", performerEnabled);
        return(performerEnabled);
    }

    protected boolean isTaskPerformerEnabled(String performerName){
        getLogger().debug(".isTaskPerformerEnabled(): Entry, performerName->{}", performerName);
        boolean isSuspended = getLocalParticipantManager().isParticipantSuspended(performerName);
        getLogger().trace(".isTaskPerformerEnabled(): isSuspended->{}", isSuspended);
        boolean isDisabled = getLocalParticipantManager().isParticipantDisabled(performerName);
        getLogger().trace(".isTaskPerformerEnabled(): isDisabled->{}", isDisabled);
        if(isDisabled || isSuspended){
            getLogger().debug(".isTaskPerformerEnabled(): Exit, is Disabled or Suspended, returning false");
            return(false);
        } else {
            getLogger().debug(".isTaskPerformerEnabled(): Exit, neither Disabled nor Suspended, returning true");
            return (true);
        }
    }

    protected boolean isTaskPerformerIdle(TaskPerformerTypeType performerType) {
        getLogger().debug(".isTaskPerformerIdle(): Entry, performerType->{}", performerType);
        if (performerType == null) {
            getLogger().debug(".isTaskPerformerIdle(): Exit, performerType is null, returning false");
            return (false);
        }
        if (performerType.getKnownTaskPerformer() == null) {
            getLogger().debug(".isTaskPerformerIdle(): Exit, performerType.getKnownTaskPerformer() is null, returning false");
            return (false);
        }
        if (StringUtils.isEmpty(performerType.getKnownTaskPerformer().getName())) {
            getLogger().debug(".isTaskPerformerIdle(): Exit, performerType.getKnownTaskPerformer().getName() is empty, returning false");
            return (false);
        }
        boolean isPerformerIdle = isTaskPerformerIdle(performerType.getKnownTaskPerformer().getName());
        getLogger().debug(".isTaskPerformerIdle(): Exit, isPerformerIdle->{}", isPerformerIdle);
        return(isPerformerIdle);
    }

    protected boolean isTaskPerformerIdle(String performerName){
        getLogger().debug(".isTaskPerformerIdle(): Entry, performerName->{}", performerName);
        switch(getLocalParticipantManager().getParticipantStatus(performerName)) {
            case PARTICIPANT_IS_IDLE:
                getLogger().debug(".isTaskPerformerIdle(): Exit, Performer is IDLE, returning true");
                return(true);
            case PARTICIPANT_IS_ACTIVE:
                getLogger().debug(".isTaskPerformerIdle(): Exit, Performer is ACTIVE, returning false");
                return(false);
            case PARTICIPANT_IS_NOT_READY:
                getLogger().debug(".isTaskPerformerIdle(): Exit, Performer is NOT_READY, returning false");
                return(false);
            case PARTICIPANT_IS_STOPPING:
                getLogger().debug(".isTaskPerformerIdle(): Exit, Performer is STOPPING, returning false");
                return(false);
            case PARTICIPANT_HAS_FAILED:
                getLogger().debug(".isTaskPerformerIdle(): Exit, Performer is FAILED, returning false");
                return(false);
            default:
                getLogger().debug(".isTaskPerformerIdle(): Exit, Performer has no status, returning false");
                return(false);
        }
    }

    protected boolean isTaskPerformerQueueEmpty(TaskPerformerTypeType performerType){
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
}
