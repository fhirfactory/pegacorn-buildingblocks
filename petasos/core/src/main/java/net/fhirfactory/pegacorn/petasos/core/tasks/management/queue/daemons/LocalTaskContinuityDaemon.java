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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.queue.daemons;

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskBrokerInterface;
import net.fhirfactory.pegacorn.core.model.petasos.jobcard.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.collections.PetasosActionableTaskSet;
import net.fhirfactory.pegacorn.core.model.petasos.task.collections.PetasosTaskIdSet;
import net.fhirfactory.pegacorn.core.model.petasos.task.queue.ParticipantTaskQueueEntry;
import net.fhirfactory.pegacorn.petasos.core.participants.management.LocalParticipantManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.cache.LocalActionableTaskCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.cache.LocalTaskJobCardCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.execution.LocalParticipantExecutionManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.execution.LocalTaskActivityManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.execution.watchdogs.common.WatchdogBase;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.queue.LocalTaskQueueCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.queue.LocalTaskQueueManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class LocalTaskContinuityDaemon extends WatchdogBase  {
    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskContinuityDaemon.class);

    private Long TASK_CONTINUITY_CHECK_INITIAL_DELAY = 60000L; // milliseconds
    private Long TASK_CONTINUITY_CHECK_PERIOD = 15000L; // milliseconds
    private Long MINIMUM_TASK_AGE_FOR_RETIREMENT = 15L; // Seconds

    private static final Long TASK_QUEUE_CHECK_PERIOD = 10000L;
    private static final Long TASK_QUEUE_CHECK_STARTUP_WAIT = 60000L;
    private static final Long TASK_QUEUE_CHECK_MAX_PERIOD = 120000L;

    private Instant actionableTaskCheckInstant;
    private Instant taskJobCardCheckInstant;
    private boolean initialised;
    private boolean queueDaemonTaskStillExecuting;
    private boolean queueOffloadDaemonTaskStillExecuting;
    private Instant queueDaemonLastRunTime;
    private Instant queueOffloadDaemonLastRunTime;

    @Inject
    private LocalActionableTaskCache taskCache;

    @Inject
    private LocalTaskJobCardCache taskJobCardDM;

    @Inject
    private LocalTaskQueueCache localTaskQueueCache;

    @Inject
    private LocalParticipantExecutionManager participantExecutionManager;

    @Inject
    private LocalParticipantManager participantManager;

    @Inject
    private LocalTaskQueueManager taskQueueManager;

    @Inject
    private PetasosTaskBrokerInterface globalTaskServicesBroker;

    @Inject
    private LocalTaskActivityManager taskActivityManager;

    //
    // Constructor(s)
    //

    public LocalTaskContinuityDaemon(){
        this.actionableTaskCheckInstant = Instant.EPOCH;
        this.taskJobCardCheckInstant = Instant.EPOCH;
        this.initialised = false;
    }

    //
    // Post Constrct
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(this.initialised){
            getLogger().debug(".initialise(): Exit, already initialised, nothing to do!");
            return;
        } else {
            getLogger().info(".initialise(): Initialisation Start");

            getLogger().info(".initialise(): [Start Task Continuity Daemon] Starting ");
            scheduleTaskContinuityWatchdog();
            getLogger().info(".initialise(): [Start Task Continuity Daemon] Finish ");

            getLogger().info(".initialise(): [Start Task Queue Processing Daemon] Starting ");
            scheduleTaskQueueCheckDaemon();
            getLogger().info(".initialise(): [Start Task Queue Processing Daemon] Finish ");

            getLogger().info(".initialise(): [Start Task Queue Offload Daemon] Starting ");
            scheduleTaskOffloadCheckDaemon();
            getLogger().info(".initialise(): [Start Task Queue Offload Daemon] Finish ");

            getLogger().info(".initialise(): Initialisation Finished");
            this.initialised = true;
            getLogger().debug(".initialise(): Exit");
        }
    }

    //
    // Scheduling & Initialisation
    //

    public void scheduleTaskContinuityWatchdog() {
        getLogger().debug(".scheduleTaskContinuityWatchdog(): Entry");
        TimerTask startupWatchdogTask = new TimerTask() {
            public void run() {
                getLogger().debug(".taskContinuityWatchdog(): Entry");
                taskContinuityWatchdog();
                getLogger().debug(".taskContinuityWatchdog(): Exit");
            }
        };
        Timer timer = new Timer("taskContinuityWatchdogTimer");
        timer.schedule(startupWatchdogTask, TASK_CONTINUITY_CHECK_INITIAL_DELAY, TASK_CONTINUITY_CHECK_PERIOD);
        getLogger().debug(".scheduleTaskContinuityWatchdog(): Exit");
    }

    private void scheduleTaskQueueCheckDaemon() {
        getLogger().debug(".scheduleTaskQueueCheckDaemon(): Entry");
        TimerTask taskQueueCheckDaemonTimerTask = new TimerTask() {
            public void run() {
                getLogger().debug(".taskQueueCheckDaemonTimerTask(): Entry");
                if (!isQueueDaemonTaskStillExecuting()) {
                    taskQueueProcessingDaemon();
                    setQueueDaemonLastRunTime(Instant.now());
                } else {
                    Long ageSinceRun = Instant.now().getEpochSecond() - getQueueDaemonLastRunTime().getEpochSecond();
                    if (ageSinceRun > TASK_QUEUE_CHECK_MAX_PERIOD) {
                        taskQueueProcessingDaemon();
                        setQueueDaemonLastRunTime(Instant.now());
                    }
                }
                getLogger().debug(".taskQueueCheckDaemonTimerTask(): Exit");
            }
        };
        Timer timer = new Timer("TaskQueueDaemonTimer");
        timer.schedule(taskQueueCheckDaemonTimerTask, TASK_QUEUE_CHECK_STARTUP_WAIT, TASK_QUEUE_CHECK_PERIOD);
        getLogger().debug(".scheduleTaskQueueCheckDaemon(): Exit");
    }

    private void scheduleTaskOffloadCheckDaemon() {
        getLogger().debug(".scheduleTaskOffloadCheckDaemon(): Entry");
        TimerTask taskQueueOffloadDaemonTimerTask = new TimerTask() {
            public void run() {
                getLogger().debug(".taskQueueCheckDaemonTimerTask(): Entry");
                if (!isQueueOffloadDaemonTaskStillExecuting()) {
                    taskQueueOffloadHandlingDaemon();
                    setQueueOffloadDaemonLastRunTime(Instant.now());
                } else {
                    Long ageSinceRun = Instant.now().getEpochSecond() - getQueueOffloadDaemonLastRunTime().getEpochSecond();
                    if (ageSinceRun > TASK_QUEUE_CHECK_MAX_PERIOD) {
                        taskQueueOffloadHandlingDaemon();
                        setQueueOffloadDaemonLastRunTime(Instant.now());
                    }
                }
                getLogger().debug(".taskQueueCheckDaemonTimerTask(): Exit");
            }
        };
        Timer timer = new Timer("TaskQueueOffloadDaemonTimer");
        timer.schedule(taskQueueOffloadDaemonTimerTask, TASK_QUEUE_CHECK_STARTUP_WAIT, TASK_QUEUE_CHECK_PERIOD);
        getLogger().debug(".scheduleTaskOffloadCheckDaemon(): Exit");
    }

    //
    // Actionable Task Controller / Watchdog
    //

    protected void taskContinuityWatchdog(){
        getLogger().debug(".taskContinuityWatchdog(): Entry");
        Set<String> allTaskIds = taskCache.getAllTaskIds();
        for(String currentTaskId: allTaskIds){
            if(getLogger().isInfoEnabled()){
                getLogger().debug(".taskContinuityWatchdog(): Checking task {}", currentTaskId);
            }
            boolean unregisterTask = false;
            PetasosActionableTask currentActionableTask = taskCache.getTask(currentTaskId);
            if(currentActionableTask.hasTaskCompletionSummary()){
                if (currentActionableTask.getTaskCompletionSummary().isFinalised()) {
                    unregisterTask = true;
                }
            }
            if(!unregisterTask) {
                if (currentActionableTask.hasTaskFulfillment()) {
                    if (currentActionableTask.getTaskFulfillment().hasStatus()) {
                        switch (currentActionableTask.getTaskFulfillment().getStatus()) {
                            case FULFILLMENT_EXECUTION_STATUS_UNREGISTERED:
                            case FULFILLMENT_EXECUTION_STATUS_REGISTERED:
                            case FULFILLMENT_EXECUTION_STATUS_INITIATED:
                            case FULFILLMENT_EXECUTION_STATUS_ACTIVE:
                            case FULFILLMENT_EXECUTION_STATUS_ACTIVE_ELSEWHERE:

                                break;
                            case FULFILLMENT_EXECUTION_STATUS_CANCELLED:
                            case FULFILLMENT_EXECUTION_STATUS_NO_ACTION_REQUIRED:
                            case FULFILLMENT_EXECUTION_STATUS_FINISHED:
                            case FULFILLMENT_EXECUTION_STATUS_FAILED:
                            case FULFILLMENT_EXECUTION_STATUS_FINISHED_ELSEWHERE:
                                Long age = Instant.now().getEpochSecond() - currentActionableTask.getCreationInstant().getEpochSecond();
                                if (age > MINIMUM_TASK_AGE_FOR_RETIREMENT) {
                                    unregisterTask = true;
                                }
                                break;
                            case FULFILLMENT_EXECUTION_STATUS_FINALISED:
                            case FULFILLMENT_EXECUTION_STATUS_FINALISED_ELSEWHERE:
                                unregisterTask = true;
                                break;
                        }
                    }
                }
            }
            if(unregisterTask){
                getLogger().debug(".taskContinuityWatchdog(): Task {} is finalised, removing from shared cache... start", currentTaskId);
                PetasosActionableTask unregisteredActionableTask = taskCache.removeTaskFromDirectory(currentTaskId);
                getLogger().debug(".taskContinuityWatchdog(): Task {} is finalised, removing from shared cache... done...");
            }
            if(getLogger().isDebugEnabled()){
                getLogger().debug(".taskContinuityWatchdog(): Shared ActionableTaskCache size->{}", taskCache.getAllTaskIds().size());
            }
        }
        getLogger().debug(".taskContinuityWatchdog(): Exit");
    }

    //
    // Task Queue Processing Daemon
    //

    private void taskQueueProcessingDaemon(){
        getLogger().debug(".taskQueueProcessingDaemon(): Entry");
        setQueueDaemonTaskStillExecuting(true);
        try {
            getLogger().trace(".taskQueueProcessingDaemon(): [Process Local Queues] Start");
            Set<String> allRegisteredComponent = getParticipantManager().getAllRegisteredComponentIds();
            for (String currentLocalRegisteredComponentIdValue : allRegisteredComponent) {
                getLogger().trace(".taskQueueCheckDaemon(): [Process Local Queues] Processing component->{}", currentLocalRegisteredComponentIdValue);
                PetasosParticipant currentLocalRegistration = getParticipantManager().getLocalParticipantForComponentIdValue(currentLocalRegisteredComponentIdValue);
                if(currentLocalRegistration != null) {
                    String currentParticipantName = currentLocalRegistration.getParticipantId().getName();
                    getLogger().trace(".taskQueueCheckDaemon(): [Process Local Queues] Processing participant->{}", currentParticipantName);
                    Integer participantQueueSize = getLocalTaskQueueCache().getParticipantQueueSize(currentParticipantName);
                    if (participantQueueSize > 0) {
                        getLogger().trace(".taskQueueProcessingDaemon(): [Process Local Queues] queueSize->{}", participantQueueSize);
                        boolean participantIsIdle = getParticipantExecutionManager().isTaskPerformerIdle(currentParticipantName);
                        getLogger().trace(".taskQueueProcessingDaemon():[Process Local Queues] isIdle->{}", participantIsIdle);
                        boolean participantIsEnabled = getParticipantExecutionManager().isTaskPerformerEnabled(currentParticipantName);
                        getLogger().trace(".taskQueueProcessingDaemon(): [Process Local Queues] isEnabled->{}", participantIsEnabled);
                        if (participantIsIdle && participantIsEnabled) {
                            getLogger().trace(".taskQueueProcessingDaemon(): [Process Local Queues] Processing Queue Entry");
                            getTaskQueueManager().processNextQueuedTaskForParticipant(currentParticipantName);
                        }
                    }
                }
            }
            getLogger().trace(".taskQueueProcessingDaemon(): [Process Local Queues] Finish");
        } catch (Exception ex){
            getLogger().warn(".taskQueueProcessingDaemon encountered an error, exception->", ex);
        }
        setQueueDaemonTaskStillExecuting(false);
        getLogger().debug(".taskQueueProcessingDaemon(): Exit");
    }

    //
    // Task Offload / Onload Daemon
    //

    private void taskQueueOffloadHandlingDaemon(){
        getLogger().debug(".taskQueueOffloadHandlingDaemon(): Entry");
        setQueueOffloadDaemonTaskStillExecuting(true);

        getLogger().trace(".taskQueueOffloadHandlingDaemon(): [Get Participant List] Start");
        Set<PetasosParticipant> participantList = new HashSet<>();
        try{
            Set<String> allRegisteredComponent = getParticipantManager().getAllRegisteredComponentIds();
            for (String currentLocalRegisteredComponentIdValue : allRegisteredComponent) {
                PetasosParticipant currentParticipant = getParticipantManager().getLocalParticipantForComponentIdValue(currentLocalRegisteredComponentIdValue);
                if(currentParticipant != null){
                    participantList.add(currentParticipant);
                }
            }
        } catch (Exception ex){
            getLogger().warn(".taskQueueOffloadHandlingDaemon(): [Get Participant List] encountered an error, exception->", ex);
        }
        getLogger().trace(".taskQueueOffloadHandlingDaemon(): [Get Participant List] Finish");


        try {
            getLogger().trace(".taskQueueOffloadHandlingDaemon(): [Process Local Queues - Offload] Start");
            for (PetasosParticipant currentParticipant : participantList) {
                String currentParticipantName = currentParticipant.getParticipantId().getName();
                getLogger().trace(".taskQueueOffloadHandlingDaemon(): [Process Local Queues - Offload] Processing participant->{}", currentParticipantName);
                getLogger().trace(".taskQueueOffloadHandlingDaemon(): [Process Local Queues - Offload][Offloaded Tasks] Start");
                Integer currentQueueSizeForParticipant = getLocalTaskQueueCache().getParticipantQueueSize(currentParticipantName);
                boolean needsOffloadingOfTasks = currentQueueSizeForParticipant >= getTaskQueueManager().getParticipantQueueSizeOnloadThreshold();
                boolean participantIsSuspended = getParticipantManager().isParticipantSuspended(currentParticipantName);
                Integer offloadedTasks = 0;
                if(needsOffloadingOfTasks || participantIsSuspended){
                    offloadedTasks = offloadTasks(currentParticipantName);
                }
                getLogger().trace(".taskQueueOffloadHandlingDaemon(): [Process Local Queues - Offload][Offloaded Tasks] Finish, offloadedTasks->{}", offloadedTasks);
            }
            getLogger().trace(".taskQueueOffloadHandlingDaemon(): [Process Local Queues - Offload] Finish");
        } catch (Exception ex){
            getLogger().warn(".taskQueueOffloadHandlingDaemon(): [Process Local Queues - Offload] encountered an error, exception->", ex);
        }

        try {
            getLogger().trace(".taskQueueOffloadHandlingDaemon(): [Process Local Queues - Reload] Start");
            for (PetasosParticipant currentParticipant : participantList) {
                String currentParticipantName = currentParticipant.getParticipantId().getName();
                getLogger().trace(".taskQueueOffloadHandlingDaemon(): [Process Local Queues - Reload] Processing participant->{}", currentParticipantName);
                getLogger().trace(".taskQueueOffloadHandlingDaemon(): [Process Local Queues - Reload][Check for Offloaded Tasks] Start");
                boolean hasOffloadedTasks = getParticipantManager().participantHasOffloadedTasks(currentParticipantName);
                getLogger().trace(".taskQueueOffloadHandlingDaemon(): [Process Local Queues - Reload][Check for Offloaded Tasks] Finish, hasOffloadedTasks->{}", hasOffloadedTasks);
                getLogger().trace(".taskQueueOffloadHandlingDaemon(): [Process Local Queues - Reload][Reload Tasks] Start");
                Integer currentQueueSizeForParticipant = getLocalTaskQueueCache().getParticipantQueueSize(currentParticipantName);
                boolean canOnloadTasks = currentQueueSizeForParticipant < getTaskQueueManager().getParticipantQueueSizeOnloadThreshold();
                Integer reloadedTasks = 0;
                if(hasOffloadedTasks && canOnloadTasks){
                    reloadedTasks = onloadTasks(currentParticipantName);
                }
                getLogger().trace(".taskQueueOffloadHandlingDaemon(): [Process Local Queues - Offload][Reload Tasks] Finish, reloadedTasks->{}", reloadedTasks);

            }
        } catch (Exception ex){
            getLogger().warn(".taskQueueOffloadHandlingDaemon(): [Process Local Queues - Reload] encountered an error, exception->", ex);
        }

        setQueueOffloadDaemonTaskStillExecuting(false);
        getLogger().debug(".taskQueueOffloadHandlingDaemon(): Exit");
    }

    public Integer offloadTasks(String participantName){
        getLogger().debug(".offloadTasks(): Entry, participantName->{}", participantName);

        if(StringUtils.isEmpty(participantName)){
            getLogger().debug(".offloadTasks(): Exit, participantName is empty");
            return(0);
        }

        Integer resultCode = 0;

        getLogger().trace(".offloadTasks(): [Get Task Offload Batch] Start");
        PetasosParticipantId participantId = getParticipantManager().getParticipantId(participantName);
        Set<ParticipantTaskQueueEntry> offloadTaskBatch = getTaskQueueManager().getOffloadTaskBatch(participantName);
        getLogger().trace(".offloadTasks(): [Get Task Offload Batch] Finish, offloadTaskBatch->{}", offloadTaskBatch);

        getLogger().trace(".offloadTasks(): [Extract TaskId Set] Start");
        PetasosTaskIdSet offloadedTaskSet = new PetasosTaskIdSet();
        for(ParticipantTaskQueueEntry currentEntry: offloadTaskBatch){
            offloadedTaskSet.addTaskId(currentEntry.getTaskId());
        }
        getLogger().trace(".offloadTasks(): [Extract TaskId Set] Finish");

        getLogger().trace(".offloadTasks(): [Task Offload] Start");
        Integer offloadedTasks = getGlobalTaskServicesBroker().offloadPendingTasks(participantId, offloadedTaskSet);
        if(offloadedTasks == offloadedTaskSet.getTaskIdSet().size())
        {
            getLogger().trace(".offloadTasks(): [Task Offload] Offload successful, removing tasks from local cache, start");
            for(ParticipantTaskQueueEntry currentEntry: offloadTaskBatch){
                getTaskCache().removeTaskFromDirectory(currentEntry.getTaskId());
            }
            getLogger().trace(".offloadTasks(): [Task Offload] Offload successful, removing tasks from local cache, finish");
            resultCode = offloadedTasks;
        } else {
            getLogger().trace(".offloadTasks(): [Task Offload] Offload unsuccessful, re-adding tasks to local queue, start");
            for (ParticipantTaskQueueEntry currentEntry : offloadTaskBatch) {
                PetasosActionableTask task = getTaskCache().getTask(currentEntry.getTaskId());
                getTaskQueueManager().queueTask(task);
            }
            getLogger().trace(".offloadTasks(): [Task Offload] Offload unsuccessful, re-adding tasks to local queue, start");
            resultCode = -1;
        }
        getLogger().trace(".offloadTasks(): [Task Offload] Finish");

        getLogger().debug(".offloadTasks(): Exit, resultCode->{}", resultCode);
        return(resultCode);
    }

    public Integer onloadTasks(String participantName){
        getLogger().debug(".onloadTasks(): Entry, participantName->{}", participantName);

        if(StringUtils.isEmpty(participantName)){
            getLogger().debug(".onloadTasks(): Exit, participantName is empty");
            return(0);
        }

        getLogger().trace(".onloadTasks(): [Checking for Offloaded Tasks] Start");
        boolean hasOffloadedTasks = getParticipantManager().participantHasOffloadedTasks(participantName);
        getLogger().trace(".onloadTasks(): [Checking for Offloaded Tasks] Finish, hasOffloadedTasks->{}", hasOffloadedTasks);

        if(!hasOffloadedTasks){
            getLogger().debug(".onloadTasks(): Exit, there are no offloaded tasks");
            return(0);
        }

        getLogger().trace(".onloadTasks(): [Checking Performer Capability] Start");
        boolean performerCannotAddMoreTasks = getTaskQueueManager().isPerformerRequiringTaskOffload(participantName);
        getLogger().trace(".onloadTasks(): [Checking Performer Capability] Finish, performerCannotAddMoreTasks->{}", performerCannotAddMoreTasks);

        if(performerCannotAddMoreTasks){
            getLogger().debug(".onloadTasks(): Exit, performer cannot add more tasks!");
            return(0);
        }

        PetasosParticipantId participantId = getParticipantManager().getParticipantId(participantName);
        PetasosActionableTaskSet offloadedPendingTasks = getGlobalTaskServicesBroker().getOffloadedPendingTasks(participantId, getTaskQueueManager().getQueueBatchSize());
        Integer tasksToBeReloaded = offloadedPendingTasks.getActionableTaskSet().size();
        Integer tasksLocallyReloaded = 0;
        if(offloadedPendingTasks.getActionableTaskSet().size() > 0){
            for(PetasosActionableTask actionableTask: offloadedPendingTasks.getActionableTaskSet().values()){
                PetasosTaskJobCard taskJobCard = getTaskActivityManager().registerCentrallyCreatedActionableTask(actionableTask, null);
                boolean addedToQueue = getTaskQueueManager().queueTask(actionableTask);
                tasksLocallyReloaded += 1;
            }
        }

        Integer outcome = tasksLocallyReloaded;
        if(tasksLocallyReloaded < tasksToBeReloaded){
            getLogger().warn(".onloadTasks(): Could not onload tasks, expected->{}, actual->{}", tasksLocallyReloaded, tasksLocallyReloaded);
            outcome = -1;
        }

        getLogger().debug(".onloadTasks(): Exit, outcome->{}", outcome);
        return(outcome);
    }

    //
    // Task Job Card Controller / Watchdog
    //

    protected void taskJobCardWatchdog(){

    }

    //
    // Getters and Setters
    //

    public Instant getActionableTaskCheckInstant() {
        return actionableTaskCheckInstant;
    }

    public void setActionableTaskCheckInstant(Instant actionableTaskCheckInstant) {
        this.actionableTaskCheckInstant = actionableTaskCheckInstant;
    }

    public Instant getTaskJobCardCheckInstant() {
        return taskJobCardCheckInstant;
    }

    public void setTaskJobCardCheckInstant(Instant taskJobCardCheckInstant) {
        this.taskJobCardCheckInstant = taskJobCardCheckInstant;
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    public LocalActionableTaskCache getTaskCache() {
        return taskCache;
    }

    public LocalTaskJobCardCache getTaskJobCardDM() {
        return taskJobCardDM;
    }

    protected Logger getLogger(){
        return(LOG);
    }

    protected boolean isQueueDaemonTaskStillExecuting() {
        return queueDaemonTaskStillExecuting;
    }

    protected void setQueueDaemonTaskStillExecuting(boolean queueDaemonTaskStillExecuting) {
        this.queueDaemonTaskStillExecuting = queueDaemonTaskStillExecuting;
    }

    protected Instant getQueueDaemonLastRunTime() {
        return queueDaemonLastRunTime;
    }

    protected void setQueueDaemonLastRunTime(Instant queueDaemonLastRunTime) {
        this.queueDaemonLastRunTime = queueDaemonLastRunTime;
    }

    protected LocalTaskQueueCache getLocalTaskQueueCache(){
        return(localTaskQueueCache);
    }

    protected LocalParticipantExecutionManager getParticipantExecutionManager(){
        return(participantExecutionManager);
    }

    protected LocalParticipantManager getParticipantManager(){
        return(participantManager);
    }

    protected LocalTaskQueueManager getTaskQueueManager(){
        return(taskQueueManager);
    }

    protected boolean isQueueOffloadDaemonTaskStillExecuting() {
        return queueOffloadDaemonTaskStillExecuting;
    }

    protected void setQueueOffloadDaemonTaskStillExecuting(boolean queueOffloadDaemonTaskStillExecuting) {
        this.queueOffloadDaemonTaskStillExecuting = queueOffloadDaemonTaskStillExecuting;
    }

    protected Instant getQueueOffloadDaemonLastRunTime() {
        return queueOffloadDaemonLastRunTime;
    }

    protected void setQueueOffloadDaemonLastRunTime(Instant queueOffloadDaemonLastRunTime) {
        this.queueOffloadDaemonLastRunTime = queueOffloadDaemonLastRunTime;
    }

    protected PetasosTaskBrokerInterface getGlobalTaskServicesBroker(){
        return(globalTaskServicesBroker);
    }

    protected LocalTaskActivityManager getTaskActivityManager(){
        return(taskActivityManager);
    }
}
