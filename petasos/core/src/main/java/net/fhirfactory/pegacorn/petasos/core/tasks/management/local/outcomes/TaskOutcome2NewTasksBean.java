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

package net.fhirfactory.pegacorn.petasos.core.tasks.management.local.outcomes;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.completion.datatypes.TaskCompletionSummaryType;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.shared.SharedActionableTaskDM;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosActionableTaskFactory;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemType;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayloadSet;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosActionableTaskActivityController;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.distribution.LocalTaskDistributionDecisionEngine;
import net.fhirfactory.pegacorn.petasos.oam.reporting.tasks.agents.WorkUnitProcessorTaskReportAgent;
import org.apache.camel.Exchange;
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
    TopologyIM topologyProxy;

    @Inject
    private SharedActionableTaskDM actionableTaskCache;

    @Inject
    private PetasosActionableTaskFactory actionableTaskFactory;

    @Inject
    private LocalPetasosActionableTaskActivityController actionableTaskActivityController;

    @Inject
    private LocalTaskDistributionDecisionEngine distributionDecisionEngine;
    
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

    public List<PetasosActionableTask> collectOutcomesAndCreateNewTasks(PetasosActionableTask actionableTask, Exchange camelExchange) {
        getLogger().debug(".extractUoWPayloadAndCreateNewUoWSet(): Entry, actionableTask (WorkUnitTransportPacket)->{}", actionableTask);
        TaskWorkItemType incomingUoW = actionableTask.getTaskWorkItem();
        UoWPayloadSet egressContent = incomingUoW.getEgressContent();
        Set<UoWPayload> egressPayloadList = egressContent.getPayloadElements();
        if (getLogger().isDebugEnabled()) {
            int counter = 0;
            for(UoWPayload currentPayload: egressPayloadList){
                getLogger().debug(".extractUoWPayloadAndCreateNewUoWSet(): payload (UoWPayload).PayloadTopic --> [{}] {}", counter, currentPayload.getPayloadManifest());
                getLogger().debug(".extractUoWPayloadAndCreateNewUoWSet(): payload (UoWPayload).Payload --> [{}] {}", counter, currentPayload.getPayload());
                counter++;
            }
        }

        //
        // We need to establish whether this actionableTask has any successor (downstream) tasks (subscribed), cause if
        // it doesn't, then we need to tag the message as being the "last-in-chain" and also "finalised". If it does
        // have
        synchronized (actionableTask.getTaskCompletionLock()) {
            if (!actionableTask.hasTaskCompletionSummary()) {
                actionableTask.setTaskCompletionSummary(new TaskCompletionSummaryType());
                actionableTask.getTaskCompletionSummary().setLastInChain(false);
                actionableTask.getTaskCompletionSummary().setFinalised(false);
            }
        }

        ArrayList<PetasosActionableTask> newActionableTaskList = new ArrayList<>();
        for(UoWPayload currentPayload: egressPayloadList) {
            DataParcelManifest payloadManifest = currentPayload.getPayloadManifest();
            if(distributionDecisionEngine.hasAtLeastOneSubscriber(payloadManifest)) {
                TaskWorkItemType newWorkItem = new TaskWorkItemType(currentPayload);
                getLogger().trace(".extractUoWPayloadAndCreateNewUoWSet(): newWorkItem->{}", newWorkItem);

                PetasosActionableTask newDownstreamTask = newActionableTask(actionableTask, newWorkItem);
                newActionableTaskList.add(newDownstreamTask);

                synchronized (actionableTask.getTaskCompletionLock()) {
                    actionableTask.getTaskCompletionSummary().addDownstreamTask(newDownstreamTask.getTaskId());
                }
            } else {
                synchronized (actionableTask.getTaskCompletionLock()) {
                    if (actionableTask.getTaskCompletionSummary().getDownstreamTaskMap().isEmpty()){
                        actionableTask.getTaskCompletionSummary().setLastInChain(true);
                        actionableTask.getTaskCompletionSummary().setFinalised(true);
                    }
                }
            }
        }

        getLogger().trace(".extractUoWPayloadAndCreateNewUoWSet(): Updating actionableTask with task completion details");
        actionableTaskActivityController.updateActionableTask(actionableTask);

        //
        // Get out metricsAgent for the WUP that sent the task & do add some metrics
        WorkUnitProcessorTaskReportAgent taskReportAgent = camelExchange.getProperty(PetasosPropertyConstants.ENDPOINT_TASK_REPORT_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorTaskReportAgent.class);
        if(taskReportAgent != null){
            taskReportAgent.sendITOpsTaskReport(actionableTask, newActionableTaskList);
        }

        getLogger().debug(".extractUoWPayloadAndCreateNewUoWSet(): Exit, new WorkUnitTransportPackets created, number --> {} ", newActionableTaskList.size());
        return (newActionableTaskList);
    }

    private PetasosActionableTask newActionableTask(PetasosActionableTask previousActionableTask, TaskWorkItemType work){
        getLogger().debug(".newActionableTask(): Entry, previousActionableTask->{}, work->{}", previousActionableTask,  work);
        if(previousActionableTask == null){
            getLogger().debug(".newActionableTask(): Exit, previousTaskFulfillmentDetail is null, returning null");
            return(null);
        }
        if(work == null){
            getLogger().debug(".newActionableTask(): Exit, No new work to be done, returning null");
            return(null);
        }
        PetasosActionableTask petasosActionableTask = actionableTaskFactory.newMessageBasedActionableTask(previousActionableTask, work);
        return(petasosActionableTask);
    }
}
