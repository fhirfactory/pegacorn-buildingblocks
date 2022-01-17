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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoW;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWProcessingOutcomeEnum;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.PetasosPathwayExchangePropertyNames;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.metrics.cache.PetasosLocalMetricsDM;
import net.fhirfactory.pegacorn.platform.edge.messaging.codecs.common.IPCPacketBeanCommon;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InterProcessingPlantHandoverResponseProcessingBean extends IPCPacketBeanCommon {
    private static final Logger LOG = LoggerFactory.getLogger(InterProcessingPlantHandoverResponseProcessingBean.class);
    ObjectMapper jsonMapper;

    @Inject
    TopologyIM topologyIM;

    @Inject
    PetasosPathwayExchangePropertyNames exchangePropertyNames;


    @Inject
    PetasosLocalMetricsDM metricsAgent;

    @PostConstruct
    public void initialise() {
        this.jsonMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        jsonMapper.registerModule(module);
    }


    public UoW processResponse(InterProcessingPlantHandoverResponsePacket responsePacket, Exchange camelExchange) throws JsonProcessingException {
        LOG.debug(".ipcSenderNotifyActivityFinished(): Entry, responsePacket->{}", responsePacket);
        LOG.trace(".ipcSenderNotifyActivityFinished(): Get Job Card and Status Element from Exchange for extraction by the WUP Egress Conduit");
        PetasosFulfillmentTask fulfillmentTask = camelExchange.getProperty(PetasosPropertyConstants.WUP_PETASOS_FULFILLMENT_TASK_EXCHANGE_PROPERTY, PetasosFulfillmentTask.class);

        LOG.trace(".contextualiseInterProcessingPlantHandoverResponsePacket(): check the response");
        boolean responseOK = false;
        String responseReason = null;
        if(responsePacket.getActionableTaskId().equals(fulfillmentTask.getActionableTaskId())){
            switch(responsePacket.getStatus()) {
                case PACKET_RECEIVE_TIMED_OUT: {
                    responseOK = false;
                    responseReason = "Message Send Timed Out!";
                    break;
                }
                case PACKET_RECEIVED_AND_DECODED: {
                    responseOK = true;
                }
                case PACKET_RECEIVED_BUT_FAILED_DECODING: {
                    responseOK = false;
                    responseReason = responsePacket.getStatusReason();
                    break;
                }
                case PACKET_SEND_FAILURE:
                default: {
                    responseOK = false;
                    responseReason = "Cannot Send the Message";
                    break;
                }
            }
        } else {
            responseOK = false;
            responseReason = "Mismatch Message Flows (Passed PetasosFullmentTask id differs)";
        }
        metricsAgent.getWorkUnitProcessingMetricsData(fulfillmentTask.getTaskFulfillment().getFulfillerComponent().getComponentID()).setEventProcessingFinishInstant(responsePacket.getMessageSendFinishInstant());

        LOG.trace(".contextualiseInterProcessingPlantHandoverResponsePacket(): formulate the processing outcome");
        UoW uow = SerializationUtils.clone(fulfillmentTask.getTaskWorkItem());
        if(responseOK){
            UoWPayload egressContent = new UoWPayload();
            DataParcelManifest egressContentManifest = uow.getIngresContent().getPayloadManifest();
            if(egressContentManifest.hasContainerDescriptor()){
                egressContentManifest.getContainerDescriptor().setDataParcelDiscriminatorType("MessageSendStatus");
                egressContentManifest.getContainerDescriptor().setDataParcelDiscriminatorValue("Success");
            } else {
                egressContentManifest.getContentDescriptor().setDataParcelDiscriminatorType("MessageSendStatus");
                egressContentManifest.getContentDescriptor().setDataParcelDiscriminatorValue("Success");
            }
            egressContent.setPayload("OK");
            egressContent.setPayloadManifest(egressContentManifest);
            uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
            uow.getEgressContent().addPayloadElement(egressContent);
        } else {
            uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_FAILED);
            uow.setFailureDescription(responseReason);
        }

        //
        // Do some metrics
        WorkUnitProcessorMetricsAgent wupMetricsAgent = camelExchange.getProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorMetricsAgent.class);
        wupMetricsAgent.incrementDistributedMessageEndpointCount(uow.getPayloadTopicID().getTargetProcessingPlantParticipantName());

        return (uow);
    }

    public ObjectMapper getJSONMapper() {
        return jsonMapper;
    }
}
