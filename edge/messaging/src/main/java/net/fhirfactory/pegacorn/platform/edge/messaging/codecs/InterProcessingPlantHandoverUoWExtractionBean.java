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

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelDirectionEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelTypeEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWProcessingOutcomeEnum;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InterProcessingPlantHandoverUoWExtractionBean {
    private static final Logger LOG = LoggerFactory.getLogger(InterProcessingPlantHandoverResponseGenerationBean.class);

    @Inject
    private ProcessingPlantInterface processingPlant;

    public InterProcessingPlantHandoverUoWExtractionBean() {
    }

    /**
     *
     * @param thePacket
     * @param camelExchange
     * @return
     */
    public UoW extractUoW(InterProcessingPlantHandoverPacket thePacket, Exchange camelExchange) {
        LOG.debug(".extractUoW(): Entry, thePacket --> {}", thePacket);
        UoW theUoW = thePacket.getPayloadPacket();
        UoWPayload outputPayload = new UoWPayload();
        String clonedPayload = SerializationUtils.clone(theUoW.getIngresContent().getPayload());
        outputPayload.setPayload(clonedPayload);
        DataParcelManifest parcelManifest = SerializationUtils.clone(theUoW.getPayloadTopicID());
        parcelManifest.setDataParcelFlowDirection(DataParcelDirectionEnum.OUTBOUND_DATA_PARCEL);
        parcelManifest.setDataParcelType(DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE);
        parcelManifest.setEnforcementPointApprovalStatus(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE);
        parcelManifest.setInterSubsystemDistributable(false);
        if(parcelManifest.hasIntendedTargetSystem()){
            if(parcelManifest.getIntendedTargetSystem().contentEquals(processingPlant.getIPCServiceName())){
                parcelManifest.setIntendedTargetSystem(null);
            }
        }
        outputPayload.setPayloadManifest(parcelManifest);
        theUoW.getEgressContent().addPayloadElement(outputPayload);
        theUoW.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
        LOG.debug(".ipcReceiverActivityStart(): exit, extracted UoW --> {}", theUoW);
        return theUoW;
    }
}
