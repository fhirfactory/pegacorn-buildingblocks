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
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.brokers.PetasosMOAServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.PetasosPathwayExchangePropertyNames;
import net.fhirfactory.pegacorn.petasos.model.configuration.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.moa.ParcelStatusElement;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWProcessingOutcomeEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import net.fhirfactory.pegacorn.platform.edge.messaging.codecs.common.IPCPacketBeanCommon;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverContextualResponse;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InterProcessingPlantHandoverFinisherBean extends IPCPacketBeanCommon {
    private static final Logger LOG = LoggerFactory.getLogger(InterProcessingPlantHandoverFinisherBean.class);
    ObjectMapper jsonMapper;

    @Inject
    TopologyIM topologyIM;

    @Inject
    PetasosMOAServicesBroker servicesBroker;

    @Inject
    PetasosPathwayExchangePropertyNames exchangePropertyNames;

    @PostConstruct
    public void initialise() {
        this.jsonMapper = new ObjectMapper();
    }


    public UoW ipcSenderNotifyActivityFinished(InterProcessingPlantHandoverContextualResponse handoverPacket, Exchange camelExchange) throws JsonProcessingException {
        LOG.debug(".ipcSenderNotifyActivityFinished(): Entry, handoverPacket (InterProcessingPlantHandoverContextualResponse) --> {}", handoverPacket);
        LOG.trace(".ipcSenderNotifyActivityFinished(): Get Job Card and Status Element from Exchange for extraction by the WUP Egress Conduit");
        WorkUnitProcessorTopologyNode node = getWUPNodeFromExchange(camelExchange);
        WUPJobCard activityJobCard = camelExchange.getProperty(PetasosPropertyConstants.WUP_JOB_CARD_EXCHANGE_PROPERTY_NAME, WUPJobCard.class); // <-- Note the "WUPJobCard" property name, make sure this is aligned with the code in the WUPEgressConduit.java file
        ParcelStatusElement statusElement = camelExchange.getProperty(PetasosPropertyConstants.WUP_PETASOS_PARCEL_STATUS_EXCHANGE_PROPERTY_NAME, ParcelStatusElement.class); // <-- Note the "ParcelStatusElement" property name, make sure this is aligned with the code in the WUPEgressConduit.java file
        LOG.trace(".ipcSenderNotifyActivityFinished(): Extract the UoW");
        UoW theUoW = handoverPacket.getTheUoW();
        LOG.trace(".ipcSenderNotifyActivityFinished(): Extracted UoW --> {}", theUoW);
        InterProcessingPlantHandoverResponsePacket responsePacket = handoverPacket.getResponsePacket();
        switch (responsePacket.getStatus()) {
            case PACKET_RECEIVED_AND_DECODED:
                LOG.trace(".ipcSenderNotifyActivityFinished(): PACKET_RECEIVED_AND_DECODED");
                theUoW.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
                break;
            case PACKET_RECEIVED_BUT_FAILED_DECODING:
                LOG.trace(".ipcSenderNotifyActivityFinished(): PACKET_RECEIVED_BUT_FAILED_DECODING");
                theUoW.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_FAILED);
                theUoW.setFailureDescription("Message encoding/decoding failure!");
                break;
            case PACKET_RECEIVE_TIMED_OUT:
                LOG.trace(".ipcSenderNotifyActivityFinished(): PACKET_RECEIVE_TIMED_OUT");
            default:
                theUoW.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_FAILED);
                theUoW.setFailureDescription("Message delivery failure!");
        }

        String egressPayloadData = getJSONMapper().writeValueAsString(responsePacket);
        UoWPayload egressPayload = new UoWPayload();
        egressPayload.setPayload(egressPayloadData);
        DataParcelTypeDescriptor token = new DataParcelTypeDescriptor();
        token.setDataParcelDefiner("Pegacorn");
        token.setDataParcelCategory("IPC");
        token.setDataParcelSubCategory("Model");
        token.setDataParcelResource("InterProcessingPlantHandoverResponsePacket");
        token.setVersion("1.0.0");
        DataParcelManifest dataParcelManifest = new DataParcelManifest();
        dataParcelManifest.setContentDescriptor(token);
        dataParcelManifest.setDataParcelType(DataParcelTypeEnum.IPC_DATA_PARCEL_TYPE);
        egressPayload.setPayloadManifest(dataParcelManifest);
        theUoW.getEgressContent().addPayloadElement(egressPayload);
        switch (theUoW.getProcessingOutcome()) {
            case UOW_OUTCOME_SUCCESS:
                servicesBroker.notifyFinishOfWorkUnitActivity(activityJobCard, theUoW);
                break;
            case UOW_OUTCOME_NOTSTARTED:
            case UOW_OUTCOME_INCOMPLETE:
            case UOW_OUTCOME_FAILED:
                servicesBroker.notifyFailureOfWorkUnitActivity(activityJobCard, theUoW);
        }
        LOG.debug(".ipcSenderNotifyActivityFinished(): exit, theUoW (UoW) --> {}", theUoW);
        return (theUoW);
    }

    public ObjectMapper getJSONMapper() {
        return jsonMapper;
    }
}
