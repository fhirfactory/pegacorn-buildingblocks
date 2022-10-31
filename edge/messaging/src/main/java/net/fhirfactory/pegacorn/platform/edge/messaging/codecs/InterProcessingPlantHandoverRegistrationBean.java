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
package net.fhirfactory.pegacorn.platform.edge.messaging.codecs;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.petasos.jobcard.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.jobcard.datatypes.PetasosTaskFulfillmentCard;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskExecutionCommandEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityElementType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemType;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.audit.brokers.PetasosFulfillmentTaskAuditServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosActionableTaskFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosFulfillmentTaskFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.execution.LocalTaskActivityManager;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.ProcessingPlantMetricsAgentAccessor;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.metrics.cache.PetasosLocalMetricsDM;
import net.fhirfactory.pegacorn.platform.edge.messaging.codecs.common.IPCPacketBeanCommon;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class InterProcessingPlantHandoverRegistrationBean extends IPCPacketBeanCommon {
    private static final Logger LOG = LoggerFactory.getLogger(InterProcessingPlantHandoverRegistrationBean.class);

    @Inject
    TopologyIM topologyIM;

    @Inject
    PetasosLocalMetricsDM metricsAgent;

    @Inject
    private PetasosActionableTaskFactory actionableTaskFactory;

    @Inject
    private PetasosFulfillmentTaskFactory fulfillmentTaskFactory;

    @Inject
    private LocalTaskActivityManager taskActivityController;

    @Inject
    private ProcessingPlantMetricsAgentAccessor processingPlantMetricAgentAccessor;

    @Inject
    private PetasosFulfillmentTaskAuditServicesBroker fulfillmentTaskAuditServicesBroker;

    public InterProcessingPlantHandoverRegistrationBean() {
    }

    public InterProcessingPlantHandoverPacket ipcReceiverActivityStart(InterProcessingPlantHandoverPacket handoverPacket, Exchange camelExchange, String wupInstanceKey) {
        getLogger().debug(".ipcReceiverActivityStart(): Entry, handoverPacket->{}, wupInstanceKey->{}", handoverPacket, wupInstanceKey);
        getLogger().trace(".ipcReceiverActivityStart(): reconstituted token, now attempting to retrieve NodeElement");
        WorkUnitProcessorSoftwareComponent node = getWUPNodeFromExchange(camelExchange);
        getLogger().trace(".ipcReceiverActivityStart(): Node Element retrieved --> {}", node);

        PetasosParticipantId wupParticipantId = node.getParticipant().getParticipantId();
        getLogger().trace(".registerActivityStart(): wupParticipantId (PetasosParticipantId) for this activity --> {}", wupParticipantId);


        getLogger().trace(".ipcReceiverActivityStart(): get Metrics Agent from Exchange");
        WorkUnitProcessorMetricsAgent wupMetricsAgent = camelExchange.getProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorMetricsAgent.class);

        getLogger().trace(".ipcReceiverActivityStart(): Capture some metrics: Start");
        wupMetricsAgent.incrementInternalReceivedMessageCount();
        wupMetricsAgent.getWUPMetricsData().setEventProcessingStartInstant(handoverPacket.getEventProcessingStartTime());
        processingPlantMetricAgentAccessor.getMetricsAgent().incrementInternalReceivedMessageCount();
        processingPlantMetricAgentAccessor.getMetricsAgent().touchLastActivityInstant();
        getLogger().trace(".ipcReceiverActivityStart(): Capture some metrics: Finish");

        getLogger().trace(".ipcReceiverActivityStart(): Create new ActionableTask: Start");
        TaskWorkItemType taskWorkItem = SerializationUtils.clone(handoverPacket.getActionableTask().getTaskWorkItem());
        PetasosActionableTask incomingActionableTask = handoverPacket.getActionableTask();
        TaskTraceabilityElementType upstreamTaskTraceability = handoverPacket.getUpstreamFulfillmentTaskDetails();
        PetasosActionableTask newActionableTask = actionableTaskFactory.newMessageBasedActionableTask(incomingActionableTask, upstreamTaskTraceability, taskWorkItem);
        getLogger().trace(".ipcReceiverActivityStart(): Create new ActionableTask: Finish");

        getLogger().trace(".ipcReceiverActivityStart(): Register new ActionableTask: Start");
        PetasosTaskJobCard jobCard = getTaskActivityController().registerLocallyCreatedTask(newActionableTask, null);
        // Add some more metrics
        wupMetricsAgent.incrementRegisteredTasks();
        getLogger().trace(".ipcReceiverActivityStart(): Register new ActionableTask: Finish");

        getLogger().trace(".ipcReceiverActivityStart(): Create a new FulfillmentTask: Start");
        PetasosFulfillmentTask fulfillmentTask = fulfillmentTaskFactory.newFulfillmentTask(newActionableTask, node);
        fulfillmentTask.getExecutionControl().setExecutionCommand(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
        fulfillmentTask.setUpdateInstant(Instant.now());
        fulfillmentTask.getTaskFulfillment().setStartInstant(Instant.now());
        PetasosFulfillmentTask registeredFulfillmentTask = getTaskActivityController().registerFulfillmentTask(fulfillmentTask, true);
        registeredFulfillmentTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_REGISTERED);
        getLogger().trace(".ipcReceiverActivityStart(): Create a new FulfillmentTask: Finish");

        getLogger().trace(".registerActivityStart(): Update TaskJobCard: Start");
        PetasosTaskFulfillmentCard fulfillmentCard = new PetasosTaskFulfillmentCard();
        fulfillmentCard.setFulfillmentExecutionStatus(registeredFulfillmentTask.getTaskFulfillment().getStatus());
        fulfillmentCard.setFulfillmentTaskId(registeredFulfillmentTask.getTaskId());
        fulfillmentCard.setFulfillmentStartInstant(registeredFulfillmentTask.getTaskFulfillment().getStartInstant());
        fulfillmentCard.setFulfillerParticipantId(registeredFulfillmentTask.getTaskFulfillment().getFulfiller().getParticipant().getParticipantId());
        jobCard.setTaskFulfillmentCard(fulfillmentCard);
        getLogger().trace(".registerActivityStart(): Update TaskJobCard: Finish");

        getLogger().trace(".ipcReceiverActivityStart(): Register PetasosFulfillmentTask for the (local) processing implementation activity: Start");
        registeredFulfillmentTask.getExecutionControl().setExecutionCommand(TaskExecutionCommandEnum.TASK_COMMAND_EXECUTE);
        registeredFulfillmentTask.getTaskFulfillment().setStartInstant(Instant.now());
        jobCard.getTaskFulfillmentCard().setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE);
        jobCard.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_EXECUTE);
        jobCard.getTaskFulfillmentCard().setFulfillmentStartInstant(Instant.now());
        TaskExecutionCommandEnum taskExecutionCommand = getTaskActivityController().notifyTaskStart(registeredFulfillmentTask.getActionableTaskId(), registeredFulfillmentTask);
        // Add some more metrics
        wupMetricsAgent.incrementStartedTasks();
        getLogger().trace(".ipcReceiverActivityStart(): Register PetasosFulfillmentTask for the (local) processing implementation activity: Finish");

        getLogger().trace(".ipcReceiverActivityStart(): Set processing to the grantedExecutionStatus: Start");
        if(!jobCard.getGrantedStatus().equals(TaskExecutionCommandEnum.TASK_COMMAND_EXECUTE)){
            registeredFulfillmentTask.getExecutionControl().setExecutionCommand(TaskExecutionCommandEnum.TASK_COMMAND_FAIL);
            registeredFulfillmentTask.getTaskFulfillment().setStartInstant(Instant.now());
            registeredFulfillmentTask.getTaskFulfillment().setFinishInstant(Instant.now());
            registeredFulfillmentTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_FAILED);
            getTaskActivityController().notifyTaskFailure(registeredFulfillmentTask.getActionableTaskId(), registeredFulfillmentTask);
        }
        getLogger().trace(".ipcReceiverActivityStart(): Update status to reflect local processing is proceeding: Finish");

        getLogger().trace(".ipcReceiverActivityStart(): Injecting Fulfillment Task into Exchange for extraction by the WUP Egress Conduit");
        camelExchange.setProperty(PetasosPropertyConstants.WUP_PETASOS_FULFILLMENT_TASK_EXCHANGE_PROPERTY, registeredFulfillmentTask);
        getLogger().debug(".ipcReceiverActivityStart(): exit, my work is done!");
        return handoverPacket;
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }


    protected LocalTaskActivityManager getTaskActivityController(){
        return(this.taskActivityController);
    }
}
