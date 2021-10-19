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

import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelDirectionEnum;
import net.fhirfactory.pegacorn.components.topology.interfaces.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.PetasosPathwayExchangePropertyNames;
import net.fhirfactory.pegacorn.petasos.itops.collectors.metrics.WorkUnitProcessorMetricsCollectionAgent;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.itops.metrics.WorkUnitProcessorNodeMetrics;
import net.fhirfactory.pegacorn.petasos.model.task.PetasosTaskOld;
import net.fhirfactory.pegacorn.petasos.model.task.datatypes.status.datatypes.TaskStatusType;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.PetasosTaskJobCard;
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
        TopologyIM topologyIM;

        @Inject
        PetasosPathwayExchangePropertyNames exchangePropertyNames;

        @Inject
        private WorkUnitProcessorMetricsCollectionAgent metricsAgent;

        @Inject
        private ProcessingPlantInterface processingPlant;

        public InterProcessingPlantHandoverPacket constructInterProcessingPlantHandoverPacket(UoW theUoW, Exchange camelExchange){
            LOG.debug(".constructInterProcessingPlantHandoverPacket(): Entry, theUoW (UoW) --> {}, wupInstanceKey (String) --> {}", theUoW);
            LOG.trace(".constructInterProcessingPlantHandoverPacket(): Attempting to retrieve NodeElement");
            WorkUnitProcessorTopologyNode node = getWUPNodeFromExchange(camelExchange);
            LOG.trace(".constructInterProcessingPlantHandoverPacket(): Node Element retrieved --> {}", node);
            LOG.trace(".constructInterProcessingPlantHandoverPacket(): Extracting Job Card and Status Element from Exchange");
            PetasosTaskOld wupTransportPacket = camelExchange.getProperty(PetasosPropertyConstants.WUP_FULFILLMENT_TASK_PROPERTY_NAME, PetasosTaskOld.class);
            PetasosTaskJobCard jobCard = wupTransportPacket.getCurrentJobCard();
            TaskStatusType statusElement = wupTransportPacket.getCurrentParcelStatus();
            LOG.trace(".constructInterProcessingPlantHandoverPacket(): Creating the Response message");
            InterProcessingPlantHandoverPacket forwardingPacket = new InterProcessingPlantHandoverPacket();
            forwardingPacket.setActivityID(jobCard.getActivityID());
            String processingPlantName = node.getNodeFDN().toTag();
            forwardingPacket.setMessageIdentifier(processingPlantName + "-" + Date.from(Instant.now()).toString());
            forwardingPacket.setMessageSendStartInstant(Instant.now());
            WorkUnitProcessorNodeMetrics nodeMetrics = metricsAgent.getNodeMetrics(node.getComponentType());
            if(nodeMetrics != null){
                int messageProcessingCount = nodeMetrics.getIngresMessageCount();
                Instant messageProcessingStartInstant = nodeMetrics.getEventProcessingStartInstant();
                forwardingPacket.setMessageTransferCount(messageProcessingCount);
                if(messageProcessingStartInstant != null){
                    forwardingPacket.setEventProcessingStartTime(messageProcessingStartInstant);
                }
            }
            theUoW.getIngresContent().getPayloadManifest().setDataParcelFlowDirection(DataParcelDirectionEnum.SUBSYSTEM_IPC_DATA_PARCEL);
            forwardingPacket.setPayloadPacket(theUoW);
            forwardingPacket.setTarget(theUoW.getPayloadTopicID().getIntendedTargetSystem());
            forwardingPacket.setSource(processingPlant.getIPCServiceRoutingName());
            LOG.trace(".constructInterProcessingPlantHandoverPacket(): not push the UoW into Exchange as a property for extraction after IPC activity");
            wupTransportPacket.setPayload(theUoW);
            LOG.debug(".constructInterProcessingPlantHandoverPacket(): Exit, forwardingPacket (InterProcessingPlantHandoverPacket) --> {}", forwardingPacket);
            return(forwardingPacket);
        }
}
