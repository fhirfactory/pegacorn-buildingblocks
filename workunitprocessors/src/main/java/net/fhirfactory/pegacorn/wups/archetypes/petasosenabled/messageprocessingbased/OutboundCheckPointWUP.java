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
package net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased;

import net.fhirfactory.pegacorn.core.constants.systemwide.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.core.interfaces.pathway.PetasosInterSubsystemSubscriptionInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelDirectionEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.HL7V2XTopicFactory;
import net.fhirfactory.pegacorn.workshops.PolicyEnforcementWorkshop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class OutboundCheckPointWUP extends MOAStandardWUP implements PetasosInterSubsystemSubscriptionInterface {

    @Inject
    private PolicyEnforcementWorkshop policyEnforcementWorkshop;

    @Inject
    private PegacornReferenceProperties referenceProperties;

    @Override
    protected List<DataParcelManifest> specifySubscriptionTopics() {
        getLogger().debug(".specifySubscriptionTopics(): Entry");
        List<DataParcelManifest> subscriptionList = new ArrayList<>();
        /*
        DataParcelManifest subscriptionManifest = new DataParcelManifest();
        DataParcelTypeDescriptor messageDescriptor = new DataParcelTypeDescriptor();
        messageDescriptor.setDataParcelDefiner(topicFactory.getHl7MessageDefiner());
        messageDescriptor.setDataParcelCategory(topicFactory.getHl7MessageCategory());
        messageDescriptor.setDataParcelSubCategory(DataParcelManifest.WILDCARD_CHARACTER);
        messageDescriptor.setDataParcelResource(DataParcelManifest.WILDCARD_CHARACTER);
        messageDescriptor.setDataParcelDiscriminatorType(DataParcelManifest.WILDCARD_CHARACTER);
        messageDescriptor.setDataParcelDiscriminatorValue(DataParcelManifest.WILDCARD_CHARACTER);
        messageDescriptor.setVersion(DataParcelManifest.WILDCARD_CHARACTER);
        subscriptionManifest.setContentDescriptor(messageDescriptor);
        subscriptionManifest.setDataParcelFlowDirection(DataParcelDirectionEnum.INFORMATION_FLOW_CORE_DISTRIBUTION);
        subscriptionManifest.setSourceSystem("*");
        subscriptionManifest.setIntendedTargetSystem("*");
        subscriptionManifest.setEnforcementPointApprovalStatus(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE);
        subscriptionManifest.setDataParcelType(DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE);
        subscriptionManifest.setValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE);
        subscriptionManifest.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_TRUE);
        subscriptionManifest.setInterSubsystemDistributable(false);
        subscriptionList.add(subscriptionManifest);

         */
        return (subscriptionList);
    }

    @Override
    public void subscribeToRemoteSubsystems(Set<DataParcelManifest> subscriptionSet) {
        List<DataParcelManifest> subscriptionList = new ArrayList<>();
        for(DataParcelManifest currentSubscription: subscriptionSet){
            currentSubscription.setDataParcelFlowDirection(DataParcelDirectionEnum.INFORMATION_FLOW_CORE_DISTRIBUTION);
            subscriptionList.add(currentSubscription);
        }

    }


    @Override
    protected String specifyWUPInstanceName() {
        return (getClass().getSimpleName());
    }

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (policyEnforcementWorkshop);
    }

    @Override
    protected String specifyParticipantDisplayName(){
        return("OutboundMessageCheckpoint");
    }

    @Override
    protected List<DataParcelManifest> declarePublishedTopics() {
        return (new ArrayList<>());
    }
}
