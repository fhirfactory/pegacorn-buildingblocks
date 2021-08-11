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
package net.fhirfactory.pegacorn.core.dataparcel;

import net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelDirectionEnum;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Objects;

public class DataParcelManifest implements Serializable {

    public static String WILDCARD_CHARACTER = "*";

    private DataParcelTypeDescriptor contentDescriptor;
    private DataParcelTypeDescriptor containerDescriptor;
    private net.fhirfactory.pegacorn.core.dataparcel.DataParcelQualityStatement payloadQuality;
    private net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelNormalisationStatusEnum normalisationStatus;
    private net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelValidationStatusEnum validationStatus;
    private net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelTypeEnum dataParcelType;
    private String sourceSystem;
    private String intendedTargetSystem;
    private net.fhirfactory.pegacorn.core.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum enforcementPointApprovalStatus;
    private boolean interSubsystemDistributable;
    private net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelDirectionEnum dataParcelFlowDirection;

    public DataParcelManifest(){
        this.contentDescriptor = null;
        this.containerDescriptor = null;
        this.sourceSystem = null;
        this.intendedTargetSystem = null;
        this.interSubsystemDistributable = false;
        this.dataParcelFlowDirection = null;
        this.enforcementPointApprovalStatus = net.fhirfactory.pegacorn.core.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE;
        this.normalisationStatus = net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY;
        this.validationStatus = net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY;
        this.dataParcelType = net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE;
    }

    public DataParcelManifest(DataParcelTypeDescriptor contentDescriptor){
        this.containerDescriptor = null;
        this.contentDescriptor = (DataParcelTypeDescriptor) SerializationUtils.clone(contentDescriptor);
        this.sourceSystem = null;
        this.intendedTargetSystem = null;
        this.interSubsystemDistributable = false;
        this.dataParcelFlowDirection = null;
        this.enforcementPointApprovalStatus = net.fhirfactory.pegacorn.core.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE;
        this.normalisationStatus = net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY;
        this.validationStatus = net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY;
        this.dataParcelType = net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE;
    }

    public DataParcelManifest(DataParcelManifest ori){
        if(ori.hasContainerDescriptor()) {
            this.containerDescriptor = (DataParcelTypeDescriptor) SerializationUtils.clone(ori.getContainerDescriptor());
        } else {
            this.containerDescriptor = null;
        }
        if(ori.hasContentDescriptor()) {
            this.contentDescriptor = (DataParcelTypeDescriptor) SerializationUtils.clone(ori.getContentDescriptor());
        } else {
            this.contentDescriptor = null;
        }
        if(ori.hasSourceSystem()) {
            this.setSourceSystem(ori.getSourceSystem());
        } else {
            this.setSourceSystem(null);
        }
        if(ori.hasIntendedTargetSystem()) {
            this.setIntendedTargetSystem(ori.getIntendedTargetSystem());
        } else {
            this.setIntendedTargetSystem(null);
        }
        this.setInterSubsystemDistributable(ori.isInterSubsystemDistributable());
        if(ori.hasDataParcelFlowDirection()) {
            this.setDataParcelFlowDirection(ori.getDataParcelFlowDirection());
        } else {
            this.setDataParcelFlowDirection(null);
        }
        if(ori.hasEnforcementPointApprovalStatus()) {
            this.setEnforcementPointApprovalStatus(ori.getEnforcementPointApprovalStatus());
        } else {
            this.enforcementPointApprovalStatus = net.fhirfactory.pegacorn.core.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE;
        }
        if(ori.hasNormalisationStatus()){
            this.setNormalisationStatus(ori.getNormalisationStatus());
        } else {
            this.normalisationStatus = net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY;
        }
        if(ori.hasValidationStatus()){
            this.setNormalisationStatus(ori.getNormalisationStatus());
        } else {
            this.validationStatus = net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY;
        }
        if(ori.hasDataParcelType()){
            this.setDataParcelType(ori.getDataParcelType());
        } else {
            this.dataParcelType = net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE;
        }
    }

    public boolean hasDataParcelType(){
        boolean has = this.dataParcelType != null;
        return(has);
    }

    public boolean hasValidationStatus(){
        boolean has = this.validationStatus != null;
        return(has);
    }

    public boolean hasNormalisationStatus(){
        boolean has = this.normalisationStatus != null;
        return(has);
    }

    public boolean hasEnforcementPointApprovalStatus(){
        boolean has = this.enforcementPointApprovalStatus != null;
        return(has);
    }

    public boolean hasDataParcelFlowDirection(){
        boolean has = this.dataParcelFlowDirection != null;
        return(has);
    }

    public boolean hasContentDescriptor(){
        boolean hasCT = this.contentDescriptor != null;
        return(hasCT);
    }

    public boolean hasContainerDescriptor(){
        boolean hasCT = this.containerDescriptor != null;
        return(hasCT);
    }

    public boolean hasSourceSystem(){
        boolean hasSS = this.sourceSystem != null;
        return(hasSS);
    }

    public boolean hasIntendedTargetSystem(){
        boolean hasITS = this.intendedTargetSystem != null;
        return(hasITS);
    }

    public boolean hasDataParcelQualityStatement(){
        boolean hasDPQS = this.payloadQuality != null;
        return(hasDPQS);
    }

    public DataParcelTypeDescriptor getContentDescriptor() {
        return contentDescriptor;
    }

    public DataParcelTypeDescriptor getContainerDescriptor() {
        return containerDescriptor;
    }

    public void setContainerDescriptor(DataParcelTypeDescriptor containerDescriptor) {
        this.containerDescriptor = (DataParcelTypeDescriptor) SerializationUtils.clone(containerDescriptor);
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = (String)SerializationUtils.clone(sourceSystem);
    }

    public String getIntendedTargetSystem() {
        return intendedTargetSystem;
    }

    public void setIntendedTargetSystem(String intendedTargetSystem) {
        this.intendedTargetSystem = (String)SerializationUtils.clone(intendedTargetSystem);
    }

    public boolean isInterSubsystemDistributable() {
        return interSubsystemDistributable;
    }

    public void setInterSubsystemDistributable(boolean interSubsystemDistributable) {
        this.interSubsystemDistributable = interSubsystemDistributable;
    }

    public void setContentDescriptor(DataParcelTypeDescriptor contentDescriptor) {
        this.contentDescriptor = (DataParcelTypeDescriptor)SerializationUtils.clone(contentDescriptor);
    }

    public net.fhirfactory.pegacorn.core.dataparcel.DataParcelQualityStatement getPayloadQuality() {
        return payloadQuality;
    }

    public void setPayloadQuality(net.fhirfactory.pegacorn.core.dataparcel.DataParcelQualityStatement payloadQuality) {
        this.payloadQuality = (DataParcelQualityStatement)SerializationUtils.clone(payloadQuality);
    }

    public net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelNormalisationStatusEnum getNormalisationStatus() {
        return normalisationStatus;
    }

    public void setNormalisationStatus(net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelNormalisationStatusEnum normalisationStatus) {
        this.normalisationStatus = normalisationStatus;
    }

    public net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelValidationStatusEnum getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelValidationStatusEnum validationStatus) {
        this.validationStatus = validationStatus;
    }

    public net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelTypeEnum getDataParcelType() {
        return dataParcelType;
    }

    public void setDataParcelType(net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelTypeEnum dataParcelType) {
        this.dataParcelType = dataParcelType;
    }

    public net.fhirfactory.pegacorn.core.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum getEnforcementPointApprovalStatus() {
        return enforcementPointApprovalStatus;
    }

    public void setEnforcementPointApprovalStatus(net.fhirfactory.pegacorn.core.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum enforcementPointApprovalStatus) {
        this.enforcementPointApprovalStatus = enforcementPointApprovalStatus;
    }

    public net.fhirfactory.pegacorn.core.dataparcel.valuesets.DataParcelDirectionEnum getDataParcelFlowDirection() {
        return dataParcelFlowDirection;
    }

    public void setDataParcelFlowDirection(DataParcelDirectionEnum dataParcelFlowDirection) {
        this.dataParcelFlowDirection = dataParcelFlowDirection;
    }

    @Override
    public String toString() {
        return "DataParcelManifest{" +
                "contentDescriptor=" + contentDescriptor +
                ", containerDescriptor=" + containerDescriptor +
                ", payloadQuality=" + payloadQuality +
                ", normalisationStatus=" + normalisationStatus +
                ", validationStatus=" + validationStatus +
                ", dataParcelType=" + dataParcelType +
                ", sourceSystem='" + sourceSystem + '\'' +
                ", intendedTargetSystem='" + intendedTargetSystem + '\'' +
                ", interSubsystemDistributable=" + interSubsystemDistributable +
                ", enforcementPointApprovalStatus=" + getEnforcementPointApprovalStatus() +
                ", dataParcelFlowDirection=" + getDataParcelFlowDirection() +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataParcelManifest)) return false;
        DataParcelManifest that = (DataParcelManifest) o;
        return isInterSubsystemDistributable() == that.isInterSubsystemDistributable() && Objects.equals(getContentDescriptor(), that.getContentDescriptor()) && Objects.equals(getContainerDescriptor(), that.getContainerDescriptor()) && Objects.equals(getPayloadQuality(), that.getPayloadQuality()) && getNormalisationStatus() == that.getNormalisationStatus() && getValidationStatus() == that.getValidationStatus() && getDataParcelType() == that.getDataParcelType() && Objects.equals(getSourceSystem(), that.getSourceSystem()) && Objects.equals(getIntendedTargetSystem(), that.getIntendedTargetSystem()) && getEnforcementPointApprovalStatus() == that.getEnforcementPointApprovalStatus() && getDataParcelFlowDirection() == that.getDataParcelFlowDirection();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContentDescriptor(), getContainerDescriptor(), getPayloadQuality(), getNormalisationStatus(), getValidationStatus(), getDataParcelType(), getSourceSystem(), getIntendedTargetSystem(), getEnforcementPointApprovalStatus(), isInterSubsystemDistributable(), getDataParcelFlowDirection());
    }
}
