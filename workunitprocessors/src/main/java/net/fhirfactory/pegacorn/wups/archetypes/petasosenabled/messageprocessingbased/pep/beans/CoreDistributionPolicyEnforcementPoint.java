/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.pep.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.*;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoW;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWProcessingOutcomeEnum;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreDistributionPolicyEnforcementPoint {
    private static final Logger LOG = LoggerFactory.getLogger(CoreDistributionPolicyEnforcementPoint.class);

    protected Logger getLogger(){
        return(LOG);
    }

    ObjectMapper jsonMapper;

    public CoreDistributionPolicyEnforcementPoint(){
        jsonMapper = new ObjectMapper();
    }

    public UoW enforceInboundPolicy(UoW uow, Exchange camelExchange){
        getLogger().debug(".enforceInboundPolicy(): Entry, uow->{}", uow);
        if(uow == null){
            return(null);
        }
        if(!uow.hasIngresContent()){
            return(uow);
        }
        UoWPayload egressPayload = SerializationUtils.clone(uow.getIngresContent());
        egressPayload.getPayloadManifest().setEnforcementPointApprovalStatus(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_POSITIVE);
        egressPayload.getPayloadManifest().setInterSubsystemDistributable(true);
        egressPayload.getPayloadManifest().setDataParcelFlowDirection(DataParcelDirectionEnum.INFORMATION_FLOW_CORE_DISTRIBUTION);
        egressPayload.getPayloadManifest().setDataParcelType(DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE);
        uow.getEgressContent().addPayloadElement(egressPayload);
        uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
        return(uow);
    }

    public UoW enforceOutboundPolicy(UoW uow, Exchange camelExchange){
        getLogger().debug(".enforceOutboundPolicy(): Entry, uow->{}", uow);
        if(uow == null){
            return(null);
        }
        if(!uow.hasIngresContent()){
            return(uow);
        }
        UoWPayload egressPayload = SerializationUtils.clone(uow.getIngresContent());
        egressPayload.getPayloadManifest().setEnforcementPointApprovalStatus(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_POSITIVE);
        egressPayload.getPayloadManifest().setDataParcelFlowDirection(DataParcelDirectionEnum.INFORMATION_FLOW_OUTBOUND_DATA_PARCEL);
        egressPayload.getPayloadManifest().setValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE);
        egressPayload.getPayloadManifest().setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_TRUE);
        egressPayload.getPayloadManifest().setDataParcelType(DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE);
        egressPayload.getPayloadManifest().setInterSubsystemDistributable(false);
        uow.getEgressContent().addPayloadElement(egressPayload);
        uow.setProcessingOutcome(UoWProcessingOutcomeEnum.UOW_OUTCOME_SUCCESS);
        return(uow);
    }
}
