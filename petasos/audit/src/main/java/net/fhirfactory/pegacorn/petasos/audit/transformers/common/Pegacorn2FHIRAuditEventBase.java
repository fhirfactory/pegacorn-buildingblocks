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
package net.fhirfactory.pegacorn.petasos.audit.transformers.common;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.topology.nodes.DefaultWorkshopSetEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.AuditEventSubTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.AuditEventTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.resilience.parcel.ResilienceParcel;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoW;
import net.fhirfactory.pegacorn.core.model.petasos.wup.datatypes.WUPIdentifier;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Period;

import java.util.Date;

public abstract class Pegacorn2FHIRAuditEventBase {

    protected String stripEscapeCharacters(String incomingString){
        String outgoingString0 = incomingString.replaceAll("\\\\", "");
        String outgoingString1 = outgoingString0.replaceAll("\\\"","\"");
        return(outgoingString1);
    }

    protected Period extractProcessingPeriod(ResilienceParcel parcel){
        if(parcel == null){
            return(null);
        }
        Date startDate = null;
        Date endDate = null;
        if(parcel.hasStartDate()){
            startDate = parcel.getStartDate();
        }
        if(parcel.hasFinishedDate()){
            endDate = parcel.getFinishedDate();
        }
        if(parcel.hasFinalisationDate()){
            endDate = parcel.getFinalisationDate();
        }
        if(parcel.hasCancellationDate()){
            if(endDate == null){
                endDate = parcel.getCancellationDate();
            }
        }
        if(startDate == null){
            startDate = endDate;
        }
        Period period = new Period();
        period.setStart(startDate);
        period.setEnd(endDate);
        return(period);
    }

    protected AuditEvent.AuditEventOutcome extractAuditEventOutcome(ResilienceParcel parcel, UoW uow){
        if(parcel == null){
            return(AuditEvent.AuditEventOutcome._8);
        }
        if(uow == null) {
            switch (parcel.getProcessingStatus()) {
                case PARCEL_STATUS_REGISTERED:
                case PARCEL_STATUS_ACTIVE:
                case PARCEL_STATUS_INITIATED:
                    return (null);
                case PARCEL_STATUS_FINISHED:
                case PARCEL_STATUS_FINALISED:
                    return (AuditEvent.AuditEventOutcome._0);
                case PARCEL_STATUS_CANCELLED:
                    return (AuditEvent.AuditEventOutcome._4);
                case PARCEL_STATUS_FAILED:
                default:
                    return (AuditEvent.AuditEventOutcome._8);
            }
        } else {
            if(uow.hasProcessingOutcome()) {
                switch (uow.getProcessingOutcome()) {
                    case UOW_OUTCOME_FAILED:
                        return (AuditEvent.AuditEventOutcome._8);
                    case UOW_OUTCOME_SUCCESS:
                        return (AuditEvent.AuditEventOutcome._0);
                    case UOW_OUTCOME_INCOMPLETE:
                    case UOW_OUTCOME_NOTSTARTED:
                    case UOW_OUTCOME_NO_PROCESSING_REQUIRED:
                    default:
                        return (null);
                }
            } else {
                return (AuditEvent.AuditEventOutcome._0);
            }
        }
    }

    protected AuditEvent.AuditEventOutcome extractAuditEventOutcome(ResilienceParcel parcel){
        AuditEvent.AuditEventOutcome outcome = extractAuditEventOutcome(parcel, null);
        return(outcome);
    }

    protected AuditEventTypeEnum extractAuditEventType(ResilienceParcel parcel){
        if(parcel == null){
            return(AuditEventTypeEnum.DICOM_APPLICATION_ACTIVITY);
        }
        WUPIdentifier wupIdentifier = parcel.getAssociatedWUPIdentifier();
        TopologyNodeFDN nodeFDN = new TopologyNodeFDN(wupIdentifier);
        TopologyNodeRDN topologyNodeRDN = nodeFDN.extractRDNForNodeType(ComponentTypeTypeEnum.WORKSHOP);
        if(topologyNodeRDN == null){
            return(AuditEventTypeEnum.DICOM_APPLICATION_ACTIVITY);
        }
        if(topologyNodeRDN.getNodeName().contentEquals(DefaultWorkshopSetEnum.TRANSFORM_WORKSHOP.getWorkshop())){
            return(AuditEventTypeEnum.HL7_TERMINOLOGY_TRANSFORM);
        } else {
            return(AuditEventTypeEnum.HL7_TERMINOLOGY_TRANSMIT);
        }
    }

    protected AuditEventSubTypeEnum extractAuditEventSubType(ResilienceParcel parcel){
        AuditEventSubTypeEnum outcomeAuditEventSubType = extractAuditEventSubType(parcel, false);
        return(outcomeAuditEventSubType);
    }

    protected AuditEventSubTypeEnum extractAuditEventSubType(ResilienceParcel parcel, boolean isInteractDone){
        if(parcel == null){
            return(AuditEventSubTypeEnum.DICOM_APPLICATION_LOCAL_SERVICE_OPERATION_STOPPED) ;
        }
        if(isInteractDone){
            return(AuditEventSubTypeEnum.DICOM_APPLICATION_LOCAL_SERVICE_OPERATION_STOPPED);
        }
        switch(parcel.getProcessingStatus()) {
            case PARCEL_STATUS_REGISTERED:
            case PARCEL_STATUS_ACTIVE:
            case PARCEL_STATUS_INITIATED:
                return(AuditEventSubTypeEnum.DICOM_APPLICATION_LOCAL_SERVICE_OPERATION_STARTED);
            case PARCEL_STATUS_FINISHED:
            case PARCEL_STATUS_FINALISED:
            default:
                return(AuditEventSubTypeEnum.DICOM_APPLICATION_LOCAL_SERVICE_OPERATION_STOPPED);
        }
    }

    protected String extractAuditEventEntityNameFromParcel(ResilienceParcel parcel){
        if(parcel == null){
            return(null);
        }
        UoW uow = parcel.getActualUoW();
        if(uow == null){
            return(null);
        }
        DataParcelManifest dataParcelManifest = null;
        switch(parcel.getProcessingStatus()) {
            case PARCEL_STATUS_REGISTERED:
            case PARCEL_STATUS_ACTIVE:
            case PARCEL_STATUS_INITIATED:
                if (uow.hasIngresContent()) {
                    dataParcelManifest = uow.getIngresContent().getPayloadManifest();
                }
                break;
            case PARCEL_STATUS_FINISHED:
            case PARCEL_STATUS_FINALISED:
                if (uow.hasEgressContent()) {
                    if (!uow.getEgressContent().getPayloadElements().isEmpty()) {
                        dataParcelManifest = uow.getEgressContent().getPayloadElements().iterator().next().getPayloadManifest();
                    }
                }
                break;
            default:
        }
        if(dataParcelManifest == null){
            return(null);
        }
        String bestValue = null;
        if(dataParcelManifest.hasContentDescriptor()){
            bestValue = extractBestDescriptorValue(dataParcelManifest.getContentDescriptor());
        }
        if(bestValue == null){
            if(dataParcelManifest.hasContainerDescriptor()){
                bestValue = extractBestDescriptorValue(dataParcelManifest.getContainerDescriptor());
            }
        }
        return(bestValue);
    }

    protected String extractBestDescriptorValue(DataParcelTypeDescriptor descriptor){
        if(descriptor == null){
            return(null);
        }
        String value = new String();
        if(descriptor.hasDataParcelResource()){
            value = descriptor.getDataParcelResource();
        }
        if(descriptor.hasDataParcelSubCategory()){
            value = descriptor.getDataParcelSubCategory() + "." + value;
        }
        if(descriptor.hasDataParcelCategory()){
            value = descriptor.getDataParcelCategory() + "." + value;
        }
        if(descriptor.hasDataParcelDefiner()){
            value = descriptor.getDataParcelDefiner() + "." + value;
        }
        if(descriptor.hasVersion()){
            value = value + "(" + descriptor.getVersion() + ")";
        }
        return(value);
    }

    protected String extractNiceNodeName(ResilienceParcel parcel){
        if(parcel == null){
            return(null);
        }
        WUPIdentifier associatedWUPIdentifier = parcel.getAssociatedWUPIdentifier();
        TopologyNodeFDN wupFDN = new TopologyNodeFDN(associatedWUPIdentifier);
        TopologyNodeRDN processingPlantRDN = wupFDN.extractRDNForNodeType(ComponentTypeTypeEnum.PROCESSING_PLANT);
        TopologyNodeRDN workshopRDN = wupFDN.extractRDNForNodeType(ComponentTypeTypeEnum.WORKSHOP);
        TopologyNodeRDN wupRDN = wupFDN.extractRDNForNodeType(ComponentTypeTypeEnum.WUP);

        String name = new String();
        if(processingPlantRDN != null){
            name = processingPlantRDN.getNodeName() + ".";
        }
        if(workshopRDN != null){
            name = name + workshopRDN.getNodeName() + ".";
        }
        if(wupRDN != null){
            name = name + wupRDN.getNodeName();
        }
        return(name);
    }

}
