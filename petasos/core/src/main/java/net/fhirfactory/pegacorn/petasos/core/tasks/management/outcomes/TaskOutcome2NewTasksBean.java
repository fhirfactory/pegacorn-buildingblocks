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

package net.fhirfactory.pegacorn.petasos.core.tasks.management.outcomes;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.jobcard.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.completion.datatypes.TaskCompletionSummaryType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.TaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemType;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayloadSet;
import net.fhirfactory.pegacorn.petasos.core.tasks.cache.LocalActionableTaskCache;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosActionableTaskFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.execution.LocalTaskActivityManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.queue.LocalTaskQueueManager;
import net.fhirfactory.pegacorn.petasos.oam.reporting.tasks.agents.WorkUnitProcessorTaskReportAgent;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Dependent
public class TaskOutcome2NewTasksBean {
    private static final Logger LOG = LoggerFactory.getLogger(TaskOutcome2NewTasksBean.class);
    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    private LocalActionableTaskCache actionableTaskCache;

    @Inject
    private PetasosActionableTaskFactory actionableTaskFactory;

    @Inject
    private LocalTaskActivityManager actionableTaskActivityController;

    @Inject
    private TaskPerformerSubscriptionDecisionEngine distributionDecisionEngine;

    @Inject
    private LocalTaskQueueManager localTaskQueueManager;

    //
    // Getters and Setters
    //

    protected LocalTaskQueueManager getLocalTaskQueueManager(){
        return(this.localTaskQueueManager);
    }

    protected PetasosActionableTaskFactory getActionableTaskFactory(){
        return(this.actionableTaskFactory);
    }

    protected LocalTaskActivityManager getTaskActivityManager(){
        return(actionableTaskActivityController);
    }

    //
    // Business Methods
    //

    /**
     * This method performs tree key tasks:
     *
     * 1. It extracts each UoWPayload from the egressPayloadSet within the incomingUoW and creates a
     * new UoW (and, subsequently, a new WorkUnitTransportPacket) based on the content of those egress
     * UoWPayload elements.
     * 2. As part of the WorkUnitTransportPacket creation, it embeds the current ActivityID.
     * 3. It then returns a List<> of these new WorkUnitTransportPackets for distribution.
     *
     * It generates the
     * @param actionableTask
     * @param camelExchange
     * @return A List<> of WorkUnitTransportPackets - one for each egress UoWPayload element within the incoming UoW.
     */

    public void collectOutcomesAndCreateNewTasks(PetasosActionableTask actionableTask, Exchange camelExchange) {
        getLogger().debug(".collectOutcomesAndCreateNewTasks(): Entry, actionableTask->{}", actionableTask);

        List<PetasosActionableTask> newTasks = null;
        getLogger().trace(".collectOutcomesAndCreateNewTasks(): [Create New Tasks] Start");
        if(actionableTask.getTaskOutcomeStatus().getOutcomeStatus().equals(TaskOutcomeStatusEnum.OUTCOME_STATUS_FAILED)){
            getLogger().trace(".collectOutcomesAndCreateNewTasks(): [Create New Tasks] Is a Retry");
            newTasks = createRetryTask(actionableTask);
        } else {
            getLogger().trace(".collectOutcomesAndCreateNewTasks(): [Create New Tasks] Creating Tasks from UoW Content");
            newTasks = createNewTasksFromUoWEgressContent(actionableTask);
        }
        getLogger().trace(".collectOutcomesAndCreateNewTasks(): [Create New Tasks] Finish");

        if(newTasks == null) {
            getLogger().debug(".collectOutcomesAndCreateNewTasks(): Exit, newTasks list is null, exiting");
            return;
        }
        if(newTasks.isEmpty()){
            getLogger().debug(".collectOutcomesAndCreateNewTasks(): Exit, newTasks list is empty, exiting");
            return;
        }

        getLogger().trace(".collectOutcomesAndCreateNewTasks(): [Queue New Tasks] Start");
        for(PetasosActionableTask currentNewTask: newTasks){
            PetasosTaskJobCard jobCard =  getTaskActivityManager().registerLocallyCreatedTask(currentNewTask, null);
            getLocalTaskQueueManager().queueTask(currentNewTask);
        }
        getLogger().trace(".collectOutcomesAndCreateNewTasks(): [Queue New Tasks] Finish");

        getLogger().trace(".collectOutcomesAndCreateNewTasks(): [Trigger Queue Check for Associated Task Performers] Start");
        for(PetasosActionableTask currentNewTask: newTasks){
            if(currentNewTask.hasTaskPerformerTypes()) {
                for(TaskPerformerTypeType currentPerformer: currentNewTask.getTaskPerformerTypes()) {
                    if(currentPerformer.getKnownTaskPerformer() != null) {
                        String participantName = currentPerformer.getKnownTaskPerformer().getName();
                        getLogger().trace(".collectOutcomesAndCreateNewTasks(): [Trigger Queue Check for Associated Task Performers] currentPerformer->{}", participantName);
                        getLocalTaskQueueManager().processNextQueuedTaskForParticipant(participantName);
                    }
                }
            }
        }
        getLogger().trace(".collectOutcomesAndCreateNewTasks(): [Trigger Queue Check for Associated Task Performers] Finish");

        getLogger().trace(".collectOutcomesAndCreateNewTasks(): Updating actionableTask with task completion details");
        actionableTaskActivityController.notifyTaskFinalisation(actionableTask.getTaskId());

        //
        // Get out metricsAgent for the WUP that sent the task & do add some metrics
        WorkUnitProcessorTaskReportAgent taskReportAgent = camelExchange.getProperty(PetasosPropertyConstants.ENDPOINT_TASK_REPORT_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorTaskReportAgent.class);
        if(taskReportAgent != null){
            taskReportAgent.sendITOpsTaskReport(actionableTask, newTasks);
        }

        getLogger().debug(".collectOutcomesAndCreateNewTasks(): Exit");
    }

    public List<PetasosActionableTask> createRetryTask(PetasosActionableTask actionableTask){
        getLogger().warn(".createRetryTask(): Entry, actionableTask->{}", actionableTask);

        List<PetasosActionableTask> newTaskList = new ArrayList<>();

        if(actionableTask != null){
            if(actionableTask.hasTaskWorkItem()) {
                if(actionableTask.getTaskWorkItem().hasIngresContent()) {
                    UoWPayload retryUoWPayload = SerializationUtils.clone(actionableTask.getTaskWorkItem().getIngresContent());
                    TaskWorkItemType taskWork = new TaskWorkItemType(retryUoWPayload);
                    String targetParticipantName = null;
                    try{
                        targetParticipantName = actionableTask.getTaskFulfillment().getFulfiller().getParticipant().getParticipantId().getName();
                    } catch(Exception ex){
                        getLogger().debug(".createRetryTask(): Unable to derive sourceParticipantName from actionableTask, ex->", ex);
                    }
                    PetasosActionableTask newActionableTask = getActionableTaskFactory().newMessageBasedActionableTask(taskWork, targetParticipantName);
                    newActionableTask.getTaskId().setTaskSequenceNumber(SerializationUtils.clone(actionableTask.getTaskId().getTaskSequenceNumber()));
                    newActionableTask.setTaskTraceability(SerializationUtils.clone(actionableTask.getTaskTraceability()));
                    newActionableTask.getTaskTraceability().setaRetry(true);
                    newActionableTask.getTaskTraceability().setRetryOrigin(actionableTask.getTaskId());
                    newActionableTask.setTaskPerformerTypes(new ArrayList<>());
                    if(actionableTask.hasTaskPerformerTypes()) {
                        for(TaskPerformerTypeType currentPerformer: actionableTask.getTaskPerformerTypes()) {
                            newActionableTask.getTaskPerformerTypes().add(SerializationUtils.clone(currentPerformer));
                        }
                    }
                    actionableTaskCache.addToCache(newActionableTask);
                    newTaskList.add(newActionableTask);
                }
            }
        }

        if (!actionableTask.hasTaskCompletionSummary()) {
            actionableTask.setTaskCompletionSummary(new TaskCompletionSummaryType());
            actionableTask.getTaskCompletionSummary().setLastInChain(true);
            actionableTask.getTaskCompletionSummary().setFinalised(true);
        }

        getLogger().debug(".createRetryTask(): Entry, newTaskList->{}", newTaskList);
        return(newTaskList);
    }

    public List<PetasosActionableTask> createNewTasksFromUoWEgressContent(PetasosActionableTask actionableTask) {
        getLogger().debug(".collectOutcomesAndCreateNewTasks(): Entry, actionableTask->{}", actionableTask);
        TaskWorkItemType incomingUoW = actionableTask.getTaskWorkItem();
        UoWPayloadSet egressContent = incomingUoW.getEgressContent();
        Set<UoWPayload> egressPayloadList = egressContent.getPayloadElements();
        if (getLogger().isDebugEnabled()) {
            int counter = 0;
            for(UoWPayload currentPayload: egressPayloadList){
                getLogger().debug(".collectOutcomesAndCreateNewTasks(): payload (UoWPayload).PayloadTopic --> [{}] {}", counter, currentPayload.getPayloadManifest());
                getLogger().debug(".collectOutcomesAndCreateNewTasks(): payload (UoWPayload).Payload --> [{}] {}", counter, currentPayload.getPayload());
                counter++;
            }
        }

        ArrayList<PetasosActionableTask> newActionableTaskList = new ArrayList<>();
        //
        // We need to establish whether this actionableTask has any successor (downstream) tasks (subscribed), cause if
        // it doesn't, then we need to tag the message as being the "last-in-chain" and also "finalised". If it does
        // have
        if (!actionableTask.hasTaskCompletionSummary()) {
            actionableTask.setTaskCompletionSummary(new TaskCompletionSummaryType());
            actionableTask.getTaskCompletionSummary().setLastInChain(false);
            actionableTask.getTaskCompletionSummary().setFinalised(false);
        }

        Boolean hasADownstreamTask = false;
        for (UoWPayload currentPayload : egressPayloadList) {
            DataParcelManifest payloadManifest = currentPayload.getPayloadManifest();
            List<PetasosParticipantRegistration> subscriberList = distributionDecisionEngine.deriveSubscriberList(payloadManifest);
            if(getLogger().isDebugEnabled()){
                getLogger().debug(".collectOutcomesAndCreateNewTasks(): number of subscribers for egressPayload->{} is count->{}", payloadManifest, subscriberList.size());
            }
            if (!subscriberList.isEmpty()) {
                for(PetasosParticipantRegistration currentSubscriber: subscriberList) {
                    TaskWorkItemType newWorkItem = new TaskWorkItemType(currentPayload);
                    getLogger().trace(".collectOutcomesAndCreateNewTasks(): newWorkItem->{}", newWorkItem);
                    PetasosActionableTask newDownstreamTask = newActionableTask(actionableTask, newWorkItem, currentSubscriber.getParticipantId().getName());
                    TaskPerformerTypeType downstreamPerformerType = new TaskPerformerTypeType();
                    downstreamPerformerType.setKnownTaskPerformer(currentSubscriber.getParticipantId());
                    downstreamPerformerType.setCapabilityBased(false);
                    if(!newDownstreamTask.hasTaskPerformerTypes()){
                        newDownstreamTask.setTaskPerformerTypes(new ArrayList<>());
                    }
                    newDownstreamTask.getTaskPerformerTypes().add(downstreamPerformerType);
                    if(getLogger().isTraceEnabled()){
                        getLogger().trace(".collectOutcomesAndCreateNewTasks(): newDownstreamTask->{}", newDownstreamTask);
                    }
                    actionableTaskCache.addToCache(newDownstreamTask);
                    newActionableTaskList.add(newDownstreamTask);
                    actionableTask.getTaskCompletionSummary().addDownstreamTask(newDownstreamTask.getTaskId());
                }
                hasADownstreamTask = true;
            }
        }

        if (!hasADownstreamTask) {
            if (actionableTask.getTaskCompletionSummary().getDownstreamTaskMap().isEmpty()) {
                actionableTask.getTaskCompletionSummary().setLastInChain(true);
                actionableTask.getTaskCompletionSummary().setFinalised(true);
            }
        }

        getLogger().debug(".collectOutcomesAndCreateNewTasks(): Exit, new PetasosActionableTasks created, number --> {} ", newActionableTaskList.size());
        return (newActionableTaskList);
    }

    private PetasosActionableTask newActionableTask(PetasosActionableTask previousActionableTask, TaskWorkItemType work, String targetPerformer){
        getLogger().debug(".newActionableTask(): Entry, previousActionableTask->{}, work->{}", previousActionableTask,  work);
        if(previousActionableTask == null){
            getLogger().debug(".newActionableTask(): Exit, previousTaskFulfillmentDetail is null, returning null");
            return(null);
        }
        if(work == null){
            getLogger().debug(".newActionableTask(): Exit, No new work to be done, returning null");
            return(null);
        }
        PetasosActionableTask petasosActionableTask = actionableTaskFactory.newMessageBasedActionableTask(previousActionableTask, work, targetPerformer);
        return(petasosActionableTask);
    }
}
