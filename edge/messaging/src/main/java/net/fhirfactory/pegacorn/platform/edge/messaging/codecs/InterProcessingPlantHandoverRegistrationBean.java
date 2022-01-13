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
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityElementType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemType;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosActionableTaskFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosFulfillmentTaskFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosActionableTaskActivityController;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosFulfilmentTaskActivityController;
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
    private LocalPetasosActionableTaskActivityController actionableTaskActivityController;

    @Inject
    private LocalPetasosFulfilmentTaskActivityController fulfilmentTaskActivityController;

    public InterProcessingPlantHandoverRegistrationBean() {
    }

    public InterProcessingPlantHandoverPacket ipcReceiverActivityStart(InterProcessingPlantHandoverPacket handoverPacket, Exchange camelExchange, String wupInstanceKey) {
        LOG.debug(".ipcReceiverActivityStart(): Entry, handoverPacket->{}, wupInstanceKey->{}", handoverPacket, wupInstanceKey);
        LOG.trace(".ipcReceiverActivityStart(): reconstituted token, now attempting to retrieve NodeElement");
        WorkUnitProcessorSoftwareComponent node = getWUPNodeFromExchange(camelExchange);
        LOG.trace(".ipcReceiverActivityStart(): Node Element retrieved --> {}", node);
        TopologyNodeFunctionFDNToken wupFunctionToken = node.getNodeFunctionFDN().getFunctionToken();
        LOG.trace(".ipcReceiverActivityStart(): wupFunctionToken (NodeElementFunctionToken) for this activity --> {}", wupFunctionToken);

        LOG.trace(".ipcReceiverActivityStart(): get Metrics Agent from Exchange");
        WorkUnitProcessorMetricsAgent metricsAgent = camelExchange.getProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorMetricsAgent.class);

        LOG.trace(".ipcReceiverActivityStart(): Create and register a new ActionableTask: Start");
        TaskWorkItemType taskWorkItem = SerializationUtils.clone(handoverPacket.getActionableTask().getTaskWorkItem());
        PetasosActionableTask incomingActionableTask = handoverPacket.getActionableTask();
        TaskTraceabilityElementType upstreamTaskTraceability = handoverPacket.getUpstreamFulfillmentTaskDetails();
        PetasosActionableTask newActionableTask = actionableTaskFactory.newMessageBasedActionableTask(incomingActionableTask, upstreamTaskTraceability, taskWorkItem);
        actionableTaskActivityController.registerActionableTask(newActionableTask);
        LOG.trace(".ipcReceiverActivityStart(): Create and register a new ActionableTask: Finish");

        LOG.trace(".ipcReceiverActivityStart(): Create and register a new FulfillmentTask: Start");
        PetasosFulfillmentTask newFulfillmentTask = fulfillmentTaskFactory.newFulfillmentTask(newActionableTask, node);
        fulfilmentTaskActivityController.registerFulfillmentTask(newFulfillmentTask, false);
        LOG.trace(".ipcReceiverActivityStart(): Create and register a new FulfillmentTask: Finish");

        //
        // Given that this WUP/Node is the "only" one receiving the handover packet, we can assume it should also
        // have execution priveleges.
        LOG.trace(".ipcReceiverActivityStart(): Update Fulfillment Task Status: Start");
        synchronized (newFulfillmentTask.getTaskFulfillmentLock()){
            newFulfillmentTask.getTaskFulfillment().setStartInstant(Instant.now());
        }
        synchronized (newFulfillmentTask.getTaskJobCardLock()){
            newFulfillmentTask.getTaskJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
            newFulfillmentTask.getTaskJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
            newFulfillmentTask.getTaskJobCard().setGrantedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
            newFulfillmentTask.getTaskJobCard().setLocalFulfillmentStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE);
            newFulfillmentTask.getTaskJobCard().setLocalUpdateInstant(Instant.now());
        }
        fulfilmentTaskActivityController.notifyFulfillmentTaskExecutionStart(newFulfillmentTask.getTaskJobCard());
        LOG.trace(".ipcReceiverActivityStart(): Update Fulfillment Task Status: Finish");


        LOG.trace(".ipcReceiverActivityStart(): Capture some metrics: Start");
        metricsAgent.incrementIngresMessageCount();
        metricsAgent.incrementRegisteredTasks();
        metricsAgent.incrementStartedTasks();
        metricsAgent.getWUPMetricsData().setEventProcessingStartInstant(handoverPacket.getEventProcessingStartTime());
        LOG.trace(".ipcReceiverActivityStart(): Capture some metrics: Finish");

        LOG.trace(".ipcReceiverActivityStart(): Injecting Fulfillment Task into Exchange for extraction by the WUP Egress Conduit");
        camelExchange.setProperty(PetasosPropertyConstants.WUP_PETASOS_FULFILLMENT_TASK_EXCHANGE_PROPERTY, newFulfillmentTask);
        LOG.debug(".ipcReceiverActivityStart(): exit, my work is done!");
        return handoverPacket;
    }
}
