/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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

package net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks;

import java.util.ArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelActivityLocation;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelQualityStatement;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.DataParcelExternallyDistributableStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.DataParcelValidationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantName;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.audit.brokers.PetasosFulfillmentTaskAuditServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosFulfillmentTaskSharedInstance;

/**
 * @author Mark A. Hunter
 * @since 2020-07-01
 */
@ApplicationScoped
public class WUPContainerEgressMetadataProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(WUPContainerEgressMetadataProcessor.class);

    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    private PetasosFulfillmentTaskAuditServicesBroker auditServicesBroker;
    

    /**
     * The WUP-Egress-Metadata-Processor updates the details of the TaskWorkItem within the FulfillmentTask to better
     * reflect the operational outcome of the task.
     *
     * It is also the point at which an AuditEvent for the PetasosFulfillmentTask is created/reported.
     *
     * @param fulfillmentTask The PetasosFulfillmentTaskSharedInstance that is to be forwarded to the Intersection (if all is OK)
     * @param camelExchange   The Apache Camel Exchange object, used to store a Semaphore as we iterate through Dynamic Route options
     * @return Returns a PetasosFulfillmentTaskSharedInstance with the egress payload containing the DiscardedTask value set
     */

    public PetasosFulfillmentTaskSharedInstance alterTaskWorkItemMetadata(PetasosFulfillmentTaskSharedInstance fulfillmentTask, Exchange camelExchange) {
        if(getLogger().isInfoEnabled()){
            getLogger().info(".alterTaskWorkItemMetadata(): Entry, fulfillmentTaskId/ActionableTaskId->{}/{}", fulfillmentTask.getTaskId(), fulfillmentTask.getActionableTaskId());
        }
        getLogger().debug(".alterTaskWorkItemMetadata(): Enter, fulfillmentTask ->{}", fulfillmentTask );
        ArrayList<String> targetList = new ArrayList<String>();
        if (fulfillmentTask.getTaskFulfillment().isToBeDiscarded()) {
            DataParcelManifest discardedParcelManifest = new DataParcelManifest();
            DataParcelTypeDescriptor discardedParcelDescriptor = new DataParcelTypeDescriptor();
            discardedParcelDescriptor.setDataParcelDefiner("FHIRFactory");
            discardedParcelDescriptor.setDataParcelCategory("Petasos");
            discardedParcelDescriptor.setDataParcelSubCategory("Tasking");
            discardedParcelManifest.setContentDescriptor(discardedParcelDescriptor);
            discardedParcelManifest.setDataParcelFlowDirection(fulfillmentTask.getTaskWorkItem().getIngresContent().getPayloadManifest().getDataParcelFlowDirection());
            DataParcelQualityStatement parcelQuality = new DataParcelQualityStatement();
            parcelQuality.setContentPolicyEnforcementStatus(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_POSITIVE);
            parcelQuality.setContentExternalDistributionStatus(DataParcelExternallyDistributableStatusEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE);
            parcelQuality.setContentNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_TRUE);
            parcelQuality.setContentValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_TRUE);
            discardedParcelManifest.setContentQuality(parcelQuality);
            if(fulfillmentTask.getTaskWorkItem().getIngresContent().getPayloadManifest().hasOrigin()) {
                discardedParcelManifest.setOrigin(fulfillmentTask.getTaskWorkItem().getIngresContent().getPayloadManifest().getOrigin());
            }
            DataParcelActivityLocation activityLocation = new DataParcelActivityLocation();
            activityLocation.setProcessingPlantParticipantName(new PetasosParticipantName(fulfillmentTask.getTaskFulfillment().getFulfillerWorkUnitProcessor().getSubsystemParticipantName()));
            activityLocation.setWorkUnitProcessorParticipantName(new PetasosParticipantName(fulfillmentTask.getTaskFulfillment().getFulfillerWorkUnitProcessor().getParticipantName()));
            discardedParcelManifest.setLastActivityLocation(activityLocation);
            UoWPayload discardedItemPayload = new UoWPayload();

            switch(fulfillmentTask.getTaskWorkItem().getProcessingOutcome()){
                case UOW_OUTCOME_NO_PROCESSING_REQUIRED:
                    discardedParcelDescriptor.setDataParcelResource("OAM-NoProcessingRequired");
                    discardedItemPayload.setPayload("Task was a No-Operation");
                    break;
                case UOW_OUTCOME_FILTERED:
                    discardedParcelDescriptor.setDataParcelResource("OAM-Filtered");
                    discardedItemPayload.setPayload("Task was filtered");
                    break;
                case UOW_OUTCOME_DISCARD:
                    discardedParcelDescriptor.setDataParcelResource("OAM-Discard");
                    discardedItemPayload.setPayload("Task is to be discarded");
                    break;
                default:
                    discardedParcelDescriptor.setDataParcelResource("OAM-Failure");
                    discardedItemPayload.setPayload("Failed");
            }
            discardedItemPayload.setPayloadManifest(discardedParcelManifest);
            fulfillmentTask.getTaskWorkItem().getEgressContent().getPayloadElements().clear();
            fulfillmentTask.getTaskWorkItem().getEgressContent().addPayloadElement(discardedItemPayload);
            fulfillmentTask.update();
        }
        auditServicesBroker.logActivity(fulfillmentTask.getInstance());

        if(getLogger().isInfoEnabled()){
            getLogger().info(".alterTaskWorkItemMetadata(): Exit, fulfillmentTaskId/ActionableTaskId->{}/{}: Status->{}", fulfillmentTask.getTaskId(), fulfillmentTask.getActionableTaskId(),fulfillmentTask.getTaskFulfillment().getStatus() );
        }

        getLogger().debug(".alterTaskWorkItemMetadata(): Exit, fulfillmentTask ->{}", fulfillmentTask );
        return(fulfillmentTask);
    }
}
