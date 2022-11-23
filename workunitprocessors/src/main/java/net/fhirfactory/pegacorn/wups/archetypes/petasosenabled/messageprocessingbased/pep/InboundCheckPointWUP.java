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
package net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.pep;

import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.*;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.workshops.PolicyEnforcementWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.MOAStandardWUP;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.pep.beans.CoreDistributionPolicyEnforcementPoint;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public abstract class InboundCheckPointWUP extends MOAStandardWUP {

    @Inject
    private PolicyEnforcementWorkshop policyEnforcementWorkshop;


    @Override
    protected List<DataParcelManifest> specifySubscriptionTopics() {
        getLogger().debug(".specifySubscriptionTopics(): Entry");
        List<DataParcelManifest> subscriptionList = new ArrayList<>();
        DataParcelManifest subscriptionManifest = new DataParcelManifest();
        DataParcelTypeDescriptor messageDescriptor = new DataParcelTypeDescriptor();
        messageDescriptor.setDataParcelDefiner(DataParcelManifest.WILDCARD_CHARACTER);
        messageDescriptor.setDataParcelCategory(DataParcelManifest.WILDCARD_CHARACTER);
        messageDescriptor.setDataParcelSubCategory(DataParcelManifest.WILDCARD_CHARACTER);
        messageDescriptor.setDataParcelResource(DataParcelManifest.WILDCARD_CHARACTER);
        messageDescriptor.setDataParcelDiscriminatorType(DataParcelManifest.WILDCARD_CHARACTER);
        messageDescriptor.setDataParcelDiscriminatorValue(DataParcelManifest.WILDCARD_CHARACTER);
        messageDescriptor.setVersion(DataParcelManifest.WILDCARD_CHARACTER);
        subscriptionManifest.setContentDescriptor(messageDescriptor);
        subscriptionManifest.setDataParcelFlowDirection(DataParcelDirectionEnum.INFORMATION_FLOW_INBOUND_DATA_PARCEL);
        subscriptionManifest.setEnforcementPointApprovalStatus(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE);
        subscriptionManifest.setDataParcelType(DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE);
        subscriptionManifest.setValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_TRUE);
        subscriptionManifest.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_TRUE);
        subscriptionManifest.setInterSubsystemDistributable(false);
        subscriptionManifest.setSourceSystem("*");
        subscriptionManifest.setIntendedTargetSystem("*");
        PetasosParticipantId previousParticipantId = new PetasosParticipantId();
        previousParticipantId.setSubsystemName(getProcessingPlant().getSubsystemParticipantName());
        previousParticipantId.setVersion(DataParcelManifest.WILDCARD_CHARACTER);
        previousParticipantId.setName(DataParcelManifest.WILDCARD_CHARACTER);
        subscriptionManifest.setPreviousParticipant(previousParticipantId);
        PetasosParticipantId originParticipantId = new PetasosParticipantId();
        originParticipantId.setSubsystemName(DataParcelManifest.WILDCARD_CHARACTER);
        originParticipantId.setVersion(DataParcelManifest.WILDCARD_CHARACTER);
        originParticipantId.setName(DataParcelManifest.WILDCARD_CHARACTER);
        subscriptionManifest.setOriginParticipant(originParticipantId);
        subscriptionList.add(subscriptionManifest);
        return (subscriptionList);
    }


    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (policyEnforcementWorkshop);
    }

    @Override
    protected String specifyParticipantDisplayName(){
        return("InboundMessageCheckpoint");
    }

    @Override
    public void configure() throws Exception {
        getLogger().info("{}:: ingresFeed() --> {}", getClass().getName(), ingresFeed());
        getLogger().info("{}:: egressFeed() --> {}", getClass().getName(), egressFeed());

        fromIncludingPetasosServices(ingresFeed())
                .routeId(getNameSet().getRouteCoreWUP())
                .bean(CoreDistributionPolicyEnforcementPoint.class, "enforceInboundPolicy")
                .to(egressFeed());
    }

    @Override
    protected List<DataParcelManifest> declarePublishedTopics() {
        return (new ArrayList<>());
    }
}
