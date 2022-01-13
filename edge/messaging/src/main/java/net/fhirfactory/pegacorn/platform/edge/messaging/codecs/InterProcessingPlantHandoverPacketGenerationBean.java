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
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelDirectionEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityElementType;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoW;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.shared.SharedActionableTaskDM;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.metrics.cache.PetasosLocalMetricsDM;
import net.fhirfactory.pegacorn.platform.edge.messaging.codecs.common.IPCPacketBeanCommon;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.Date;
import java.time.Instant;

    @ApplicationScoped
    public class InterProcessingPlantHandoverPacketGenerationBean extends IPCPacketBeanCommon {
        private static final Logger LOG = LoggerFactory.getLogger(InterProcessingPlantHandoverPacketGenerationBean.class);

        @Inject
        private PetasosLocalMetricsDM metricsAgent;

        @Inject
        private ProcessingPlantInterface processingPlant;

        @Inject
        private SharedActionableTaskDM actionableTaskDM;

        /**
         * This method constructs the handover packet (forwarding packet) to be used to transport a "Task" from one
         * ProcessingPlant (PetasosParticipant) to another (PetasosParticipant) as part of a publish/subscribe process.
         *
         * The handover packet contains essentially three things:
         * (1) The ActionableTask
         * (2) A TaskTraceabilityElementType for the current FulfillmentTask (which this bean is being executed within), and
         * (3) Some basic hand-shaking details which includes the target ProcessingPlant's ParticipantName.
         *
         * It constructs a TaskTraceabilityElementType for the current fulfillment task, as the present ActionableTask
         * will not have the "fulfiller" details populated at this point in the WUP processing lifecycle (the values
         * are normally populated into the ActionableTask when the FulfillmentTask is complete).
         *
         * The method extracts the FulfillmentTask from the CamelExchange Properties, and uses this to obtain the
         * ActionableTask from the (infinispan) shared ActionableTask cache (SharedActionableTaskDM).
         *
         * It ignores the incoming UoW (theUoW).
         *
         * @param theUoW ignored...
         * @param camelExchange The Apache Camel Exchange which contains the FulfillmentTask being "fulfilled"
         * @return A handover packet for forwarding to one (and only one) "downstream" ProcessingPlant
         */
        public InterProcessingPlantHandoverPacket constructInterProcessingPlantHandoverPacket(UoW theUoW, Exchange camelExchange){
            LOG.debug(".constructInterProcessingPlantHandoverPacket(): Entry, theUoW (UoW) --> {}, wupInstanceKey (String) --> {}", theUoW);

            LOG.trace(".constructInterProcessingPlantHandoverPacket(): Retrieving the PetasosFulfillmentTask from the Exchange object");
            PetasosFulfillmentTask fulfillmentTask = camelExchange.getProperty(PetasosPropertyConstants.WUP_PETASOS_FULFILLMENT_TASK_EXCHANGE_PROPERTY, PetasosFulfillmentTask.class);

            LOG.trace(".constructInterProcessingPlantHandoverPacket(): Retrieving the associated PetasosActionableTask from the Exchange object");
            PetasosActionableTask actionableTask = actionableTaskDM.getActionableTask(fulfillmentTask.getActionableTaskId());

            LOG.trace(".constructInterProcessingPlantHandoverPacket(): Retrieving the associated Metrics Agent");
            WorkUnitProcessorMetricsAgent metricsAgent = camelExchange.getProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorMetricsAgent.class);

            LOG.trace(".constructInterProcessingPlantHandoverPacket(): Create TaskFullment Traceability element");
            TaskTraceabilityElementType traceabilityElement = new TaskTraceabilityElementType();
            traceabilityElement.setFulfillerId(fulfillmentTask.getTaskFulfillment().getFulfillerComponent().getComponentID());
            traceabilityElement.setActionableTaskId(actionableTask.getTaskId());
            traceabilityElement.setFulfillerTaskId(fulfillmentTask.getTaskId());
            traceabilityElement.setStartInstant(fulfillmentTask.getTaskFulfillment().getStartInstant());
            traceabilityElement.setRegistrationInstant(fulfillmentTask.getTaskFulfillment().getRegistrationInstant());

            LOG.trace(".constructInterProcessingPlantHandoverPacket(): Creating the Handover message");
            InterProcessingPlantHandoverPacket forwardingPacket = new InterProcessingPlantHandoverPacket();
            forwardingPacket.setActionableTask(actionableTask);
            forwardingPacket.setUpstreamFulfillmentTaskDetails(traceabilityElement);
            String processingPlantName = fulfillmentTask.getTaskFulfillment().getFulfillerComponent().getComponentID().getDisplayName();
            forwardingPacket.setMessageIdentifier(processingPlantName + "-" + Date.from(Instant.now()).toString());
            forwardingPacket.setMessageSendStartInstant(Instant.now());
            if(metricsAgent != null){
                int messageProcessingCount = metricsAgent.getWUPMetricsData().getIngresMessageCount();
                Instant messageProcessingStartInstant = metricsAgent.getWUPMetricsData().getEventProcessingStartInstant();
                forwardingPacket.setMessageTransferCount(messageProcessingCount);
                if(messageProcessingStartInstant != null){
                    forwardingPacket.setEventProcessingStartTime(messageProcessingStartInstant);
                }
            }
            synchronized (fulfillmentTask.getTaskWorkItemLock()){
                fulfillmentTask.getTaskWorkItem().getIngresContent().getPayloadManifest().setDataParcelFlowDirection(DataParcelDirectionEnum.INFORMATION_FLOW_SUBSYSTEM_IPC_DATA_PARCEL);
            }
            forwardingPacket.setTarget(theUoW.getPayloadTopicID().getTargetProcessingPlantParticipantName());
            forwardingPacket.setSource(processingPlant.getSubsystemParticipantName());
            LOG.debug(".constructInterProcessingPlantHandoverPacket(): Exit, forwardingPacket (InterProcessingPlantHandoverPacket) --> {}", forwardingPacket);
            return(forwardingPacket);
        }
}
