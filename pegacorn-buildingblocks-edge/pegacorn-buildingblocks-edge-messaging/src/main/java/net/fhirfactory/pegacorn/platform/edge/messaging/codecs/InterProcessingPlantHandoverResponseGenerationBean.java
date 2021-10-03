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

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.PetasosPathwayExchangePropertyNames;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa.ParcelStatusElement;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import net.fhirfactory.pegacorn.platform.edge.messaging.codecs.common.IPCPacketBeanCommon;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacketStatusEnum;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.Date;
import java.time.Instant;

@ApplicationScoped
public class InterProcessingPlantHandoverResponseGenerationBean  extends IPCPacketBeanCommon {
    private static final Logger LOG = LoggerFactory.getLogger(InterProcessingPlantHandoverResponseGenerationBean.class);
    @Inject
    TopologyIM topologyIM;
    @Inject
    PetasosPathwayExchangePropertyNames exchangePropertyNames;

    public InterProcessingPlantHandoverResponseGenerationBean() {
    }

    public InterProcessingPlantHandoverResponsePacket generateInterProcessingPlantHandoverResponse(InterProcessingPlantHandoverPacket incomingPacket, Exchange camelExchange, String wupInstanceKey) {
        LOG.debug(".generateInterProcessingPlantHandoverResponse(): Entry, incomingPacket (InterProcessingPlantHandoverPacket) --> {}, wupInstanceKey (String) --> {}", incomingPacket, wupInstanceKey);
        LOG.trace(".generateInterProcessingPlantHandoverResponse(): reconstituted token, now attempting to retrieve NodeElement");
        WorkUnitProcessorTopologyNode node = getWUPNodeFromExchange(camelExchange);
        LOG.trace(".generateInterProcessingPlantHandoverResponse(): Node Element retrieved --> {}", node);
        LOG.trace(".generateInterProcessingPlantHandoverResponse(): Extracting Job Card and Status Element from Exchange");
        WUPJobCard jobCard = (WUPJobCard)camelExchange.getProperty(PetasosPropertyConstants.WUP_JOB_CARD_EXCHANGE_PROPERTY_NAME, WUPJobCard.class);
        ParcelStatusElement statusElement = (ParcelStatusElement)camelExchange.getProperty(PetasosPropertyConstants.WUP_PETASOS_PARCEL_STATUS_EXCHANGE_PROPERTY_NAME, ParcelStatusElement.class);
        LOG.trace(".generateInterProcessingPlantHandoverResponse(): Creating the Response message");
        InterProcessingPlantHandoverResponsePacket response = new InterProcessingPlantHandoverResponsePacket();
        response.setActivityID(jobCard.getActivityID());
        String processingPlantName = node.getNodeFDN().toTag();
        response.setMessageIdentifier(processingPlantName + "-" + Date.from(Instant.now()).toString());
        response.setSendDate(Date.from(Instant.now()));
        LOG.trace(".generateInterProcessingPlantHandoverResponse(): We are at this point, so it is all good - so assign appropriate status");
        response.setStatus(InterProcessingPlantHandoverPacketStatusEnum.PACKET_RECEIVED_AND_DECODED);
        LOG.debug(".generateInterProcessingPlantHandoverResponse(): Exit, response (InterProcessingPlantHandoverResponsePacket) --> {}", response);
        return response;
    }
}
