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
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.PetasosPathwayExchangePropertyNames;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa.ParcelStatusElement;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
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
        private ProcessingPlantInterface processingPlant;

        public InterProcessingPlantHandoverPacket constructInterProcessingPlantHandoverPacket(UoW theUoW, Exchange camelExchange){
            LOG.debug(".constructInterProcessingPlantHandoverPacket(): Entry, theUoW (UoW) --> {}, wupInstanceKey (String) --> {}", theUoW);
            LOG.trace(".constructInterProcessingPlantHandoverPacket(): Attempting to retrieve NodeElement");
            WorkUnitProcessorTopologyNode node = getWUPNodeFromExchange(camelExchange);
            LOG.trace(".constructInterProcessingPlantHandoverPacket(): Node Element retrieved --> {}", node);
            LOG.trace(".constructInterProcessingPlantHandoverPacket(): Extracting Job Card and Status Element from Exchange");
            WUPJobCard jobCard = camelExchange.getProperty(PetasosPropertyConstants.WUP_JOB_CARD_EXCHANGE_PROPERTY_NAME, WUPJobCard.class); // <-- Note the "WUPJobCard" property name, make sure this is aligned with the code in the WUPEgressConduit.java file
            ParcelStatusElement statusElement = camelExchange.getProperty(PetasosPropertyConstants.WUP_PETASOS_PARCEL_STATUS_EXCHANGE_PROPERTY_NAME, ParcelStatusElement.class); // <-- Note the "ParcelStatusElement" property name, make sure this is aligned with the code in the WUPEgressConduit.java file
            LOG.trace(".constructInterProcessingPlantHandoverPacket(): Creating the Response message");
            InterProcessingPlantHandoverPacket forwardingPacket = new InterProcessingPlantHandoverPacket();
            forwardingPacket.setActivityID(jobCard.getActivityID());
            String processingPlantName = node.getNodeFDN().toTag();
            forwardingPacket.setMessageIdentifier(processingPlantName + "-" + Date.from(Instant.now()).toString());
            forwardingPacket.setSendDate(Date.from(Instant.now()));
            theUoW.getIngresContent().getPayloadManifest().setDataParcelFlowDirection(DataParcelDirectionEnum.SUBSYSTEM_IPC_DATA_PARCEL);
            forwardingPacket.setPayloadPacket(theUoW);
            forwardingPacket.setTarget(theUoW.getPayloadTopicID().getIntendedTargetSystem());
            forwardingPacket.setSource(processingPlant.getIPCServiceName());
            LOG.trace(".constructInterProcessingPlantHandoverPacket(): not push the UoW into Exchange as a property for extraction after IPC activity");
            if(camelExchange.getProperty(PetasosPropertyConstants.WUP_CURRENT_UOW_EXCHANGE_PROPERTY_NAME) == null) {
                camelExchange.setProperty(PetasosPropertyConstants.WUP_CURRENT_UOW_EXCHANGE_PROPERTY_NAME, theUoW);
            }
            LOG.debug(".constructInterProcessingPlantHandoverPacket(): Exit, forwardingPacket (InterProcessingPlantHandoverPacket) --> {}", forwardingPacket);
            return(forwardingPacket);
        }
}
