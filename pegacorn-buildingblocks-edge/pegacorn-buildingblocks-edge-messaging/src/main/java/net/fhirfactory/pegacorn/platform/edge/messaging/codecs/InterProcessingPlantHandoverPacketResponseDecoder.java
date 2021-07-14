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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.PetasosPathwayExchangePropertyNames;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.platform.edge.messaging.codecs.common.IPCPacketBeanCommon;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverContextualResponse;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 *
 * @author mhunter
 */
@ApplicationScoped
public class InterProcessingPlantHandoverPacketResponseDecoder extends IPCPacketBeanCommon {

    private static final Logger LOG = LoggerFactory.getLogger(InterProcessingPlantHandoverPacketGenerationBean.class);
    private ObjectMapper jsonMapper;

    @Inject
    TopologyIM topologyIM;

    @Inject
    PetasosPathwayExchangePropertyNames exchangePropertyNames;

    @PostConstruct
    public void initiate(){
        jsonMapper = new ObjectMapper();
    }

    public ObjectMapper getJSONMapper() {
        return jsonMapper;
    }

    public InterProcessingPlantHandoverContextualResponse contextualiseInterProcessingPlantHandoverResponsePacket(InterProcessingPlantHandoverResponsePacket responsePacket, Exchange camelExchange) {
        LOG.debug(".contextualiseInterProcessingPlantHandoverResponsePacket(): Entry, responsePacket->{}", responsePacket);
        LOG.trace(".contextualiseInterProcessingPlantHandoverResponsePacket(): Convert incoming message string to an InterProcessingPlantHandoverResponsePacket");
        WorkUnitProcessorTopologyNode node = getWUPNodeFromExchange(camelExchange);
        LOG.trace(".contextualiseInterProcessingPlantHandoverResponsePacket(): Attempting to retrieve UoW from the Exchange");
        String uowPropertyKey = exchangePropertyNames.getExchangeUoWPropertyName(node.getNodeKey());
        UoW theUoW = camelExchange.getProperty(PetasosPropertyConstants.WUP_CURRENT_UOW_EXCHANGE_PROPERTY_NAME, UoW.class);
        LOG.trace(".contextualiseInterProcessingPlantHandoverResponsePacket(): Retrieved UoW --> {}", theUoW);
        LOG.trace(".contextualiseInterProcessingPlantHandoverResponsePacket(): Creating the Response message");
        InterProcessingPlantHandoverContextualResponse contextualisedResponsePacket = new InterProcessingPlantHandoverContextualResponse();
        contextualisedResponsePacket.setResponsePacket(responsePacket);
        contextualisedResponsePacket.setTheUoW(theUoW);
        LOG.debug(".contextualiseInterProcessingPlantHandoverResponsePacket(): Exit, contextualisedResponsePacket (InterProcessingPlantHandoverContextualResponse) --> {}", contextualisedResponsePacket);
        return (contextualisedResponsePacket);
    }
}
