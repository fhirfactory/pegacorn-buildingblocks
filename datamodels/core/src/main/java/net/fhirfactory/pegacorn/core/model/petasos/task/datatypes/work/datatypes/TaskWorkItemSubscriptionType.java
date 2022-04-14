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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelQualityStatement;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.*;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class TaskWorkItemSubscriptionType implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(TaskWorkItemSubscriptionType.class);

    private DataParcelTypeDescriptor contentDescriptor;
    private DataParcelTypeDescriptor containerDescriptor;
    private DataParcelQualityStatement payloadQuality;
    private DataParcelNormalisationStatusEnum normalisationStatus;
    private DataParcelValidationStatusEnum validationStatus;
    private DataParcelTypeEnum dataParcelType;
    private String externalSourceSystem;
    private String externalTargetSystem;
    private PolicyEnforcementPointApprovalStatusEnum enforcementPointApprovalStatus;
    private boolean interSubsystemDistributable;
    private DataParcelDirectionEnum dataParcelFlowDirection;
    private String sourceProcessingPlantParticipantName;
    private String targetProcessingPlantParticipantName;

    //
    // Constructor(s)
    //

    public TaskWorkItemSubscriptionType(){
        this.contentDescriptor = null;
        this.containerDescriptor = null;
        this.externalSourceSystem = null;
        this.externalTargetSystem = null;
        this.interSubsystemDistributable = false;
        this.dataParcelFlowDirection = null;
        this.sourceProcessingPlantParticipantName = null;
        this.targetProcessingPlantParticipantName = null;
        this.enforcementPointApprovalStatus = PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE;
        this.normalisationStatus = DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY;
        this.validationStatus = DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY;
        this.dataParcelType = DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE;
    }

    public TaskWorkItemSubscriptionType(DataParcelTypeDescriptor contentDescriptor){
        this.containerDescriptor = null;
        this.contentDescriptor = (DataParcelTypeDescriptor) SerializationUtils.clone(contentDescriptor);
        this.externalSourceSystem = null;
        this.externalTargetSystem = null;
        this.interSubsystemDistributable = false;
        this.dataParcelFlowDirection = null;
        this.sourceProcessingPlantParticipantName = null;
        this.targetProcessingPlantParticipantName = null;
        this.enforcementPointApprovalStatus = PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE;
        this.normalisationStatus = DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY;
        this.validationStatus = DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY;
        this.dataParcelType = DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE;
    }

    public TaskWorkItemSubscriptionType(DataParcelManifest ori){
        if(ori.hasTargetProcessingPlantParticipantName()){
            this.setTargetProcessingPlantParticipantName(SerializationUtils.clone(ori.getTargetProcessingPlantParticipantName()));
        } else {
            this.setTargetProcessingPlantParticipantName(null);
        }
        if(ori.hasSourceProcessingPlantParticipantName()){
            this.setSourceProcessingPlantParticipantName(SerializationUtils.clone(ori.getSourceProcessingPlantParticipantName()));
        } else {
            this.setSourceProcessingPlantParticipantName(null);
        }
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
            this.setExternalSourceSystem(SerializationUtils.clone(ori.getSourceSystem()));
        } else {
            this.setExternalSourceSystem(null);
        }
        if(ori.hasIntendedTargetSystem()) {
            this.setExternalTargetSystem(SerializationUtils.clone(ori.getIntendedTargetSystem()));
        } else {
            this.setExternalTargetSystem(null);
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
            this.enforcementPointApprovalStatus = PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE;
        }
        if(ori.hasNormalisationStatus()){
            this.setNormalisationStatus(ori.getNormalisationStatus());
        } else {
            this.normalisationStatus = DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY;
        }
        if(ori.hasValidationStatus()){
            this.setValidationStatus(ori.getValidationStatus());
        } else {
            this.validationStatus = DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY;
        }
        if(ori.hasDataParcelType()){
            this.setDataParcelType(ori.getDataParcelType());
        } else {
            this.dataParcelType = DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE;
        }
    }

    public TaskWorkItemSubscriptionType(TaskWorkItemSubscriptionType ori){
        if(ori.hasTargetProcessingPlantParticipantName()){
            this.setTargetProcessingPlantParticipantName(SerializationUtils.clone(ori.getTargetProcessingPlantParticipantName()));
        } else {
            this.setTargetProcessingPlantParticipantName(null);
        }
        if(ori.hasSourceProcessingPlantParticipantName()){
            this.setSourceProcessingPlantParticipantName(SerializationUtils.clone(ori.getSourceProcessingPlantParticipantName()));
        } else {
            this.setSourceProcessingPlantParticipantName(null);
        }
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
        if(ori.hasExternalSourceSystem()) {
            this.setExternalSourceSystem(SerializationUtils.clone(ori.getExternalSourceSystem()));
        } else {
            this.setExternalSourceSystem(null);
        }
        if(ori.hasExternalTargetSystem()) {
            this.setExternalTargetSystem(SerializationUtils.clone(ori.getExternalTargetSystem()));
        } else {
            this.setExternalTargetSystem(null);
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
            this.enforcementPointApprovalStatus = PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE;
        }
        if(ori.hasNormalisationStatus()){
            this.setNormalisationStatus(ori.getNormalisationStatus());
        } else {
            this.normalisationStatus = DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY;
        }
        if(ori.hasValidationStatus()){
            this.setValidationStatus(ori.getValidationStatus());
        } else {
            this.validationStatus = DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY;
        }
        if(ori.hasDataParcelType()){
            this.setDataParcelType(ori.getDataParcelType());
        } else {
            this.dataParcelType = DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE;
        }
    }

    //
    // If Exists
    //

    @JsonIgnore
    public boolean hasTargetProcessingPlantParticipantName(){
        boolean hasValue = this.targetProcessingPlantParticipantName != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasSourceProcessingPlantParticipantName(){
        boolean hasValue = this.sourceProcessingPlantParticipantName != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasDataParcelType(){
        boolean has = this.dataParcelType != null;
        return(has);
    }

    @JsonIgnore
    public boolean hasValidationStatus(){
        boolean has = this.validationStatus != null;
        return(has);
    }

    @JsonIgnore
    public boolean hasNormalisationStatus(){
        boolean has = this.normalisationStatus != null;
        return(has);
    }

    @JsonIgnore
    public boolean hasEnforcementPointApprovalStatus(){
        boolean has = this.enforcementPointApprovalStatus != null;
        return(has);
    }

    @JsonIgnore
    public boolean hasDataParcelFlowDirection(){
        boolean has = this.dataParcelFlowDirection != null;
        return(has);
    }

    @JsonIgnore
    public boolean hasContentDescriptor(){
        boolean hasCT = this.contentDescriptor != null;
        return(hasCT);
    }

    @JsonIgnore
    public boolean hasContainerDescriptor(){
        boolean hasCT = this.containerDescriptor != null;
        return(hasCT);
    }

    @JsonIgnore
    public boolean hasExternalSourceSystem(){
        boolean hasSS = this.externalSourceSystem != null;
        return(hasSS);
    }

    @JsonIgnore
    public boolean hasExternalTargetSystem(){
        boolean hasITS = this.externalTargetSystem != null;
        return(hasITS);
    }

    @JsonIgnore
    public boolean hasDataParcelQualityStatement(){
        boolean hasDPQS = this.payloadQuality != null;
        return(hasDPQS);
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    protected Logger getLogger(){
        return(LOG);
    }

    public DataParcelTypeDescriptor getContentDescriptor() {
        return contentDescriptor;
    }

    public void setContentDescriptor(DataParcelTypeDescriptor contentDescriptor) {
        this.contentDescriptor = contentDescriptor;
    }

    public DataParcelTypeDescriptor getContainerDescriptor() {
        return containerDescriptor;
    }

    public void setContainerDescriptor(DataParcelTypeDescriptor containerDescriptor) {
        this.containerDescriptor = containerDescriptor;
    }

    public DataParcelQualityStatement getPayloadQuality() {
        return payloadQuality;
    }

    public void setPayloadQuality(DataParcelQualityStatement payloadQuality) {
        this.payloadQuality = payloadQuality;
    }

    public DataParcelNormalisationStatusEnum getNormalisationStatus() {
        return normalisationStatus;
    }

    public void setNormalisationStatus(DataParcelNormalisationStatusEnum normalisationStatus) {
        this.normalisationStatus = normalisationStatus;
    }

    public DataParcelValidationStatusEnum getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(DataParcelValidationStatusEnum validationStatus) {
        this.validationStatus = validationStatus;
    }

    public DataParcelTypeEnum getDataParcelType() {
        return dataParcelType;
    }

    public void setDataParcelType(DataParcelTypeEnum dataParcelType) {
        this.dataParcelType = dataParcelType;
    }

    public String getExternalSourceSystem() {
        return externalSourceSystem;
    }

    public void setExternalSourceSystem(String externalSourceSystem) {
        this.externalSourceSystem = externalSourceSystem;
    }

    public String getExternalTargetSystem() {
        return externalTargetSystem;
    }

    public void setExternalTargetSystem(String externalTargetSystem) {
        this.externalTargetSystem = externalTargetSystem;
    }

    public PolicyEnforcementPointApprovalStatusEnum getEnforcementPointApprovalStatus() {
        return enforcementPointApprovalStatus;
    }

    public void setEnforcementPointApprovalStatus(PolicyEnforcementPointApprovalStatusEnum enforcementPointApprovalStatus) {
        this.enforcementPointApprovalStatus = enforcementPointApprovalStatus;
    }

    public boolean isInterSubsystemDistributable() {
        return interSubsystemDistributable;
    }

    public void setInterSubsystemDistributable(boolean interSubsystemDistributable) {
        this.interSubsystemDistributable = interSubsystemDistributable;
    }

    public DataParcelDirectionEnum getDataParcelFlowDirection() {
        return dataParcelFlowDirection;
    }

    public void setDataParcelFlowDirection(DataParcelDirectionEnum dataParcelFlowDirection) {
        this.dataParcelFlowDirection = dataParcelFlowDirection;
    }

    public String getSourceProcessingPlantParticipantName() {
        return sourceProcessingPlantParticipantName;
    }

    public void setSourceProcessingPlantParticipantName(String sourceProcessingPlantParticipantName) {
        this.sourceProcessingPlantParticipantName = sourceProcessingPlantParticipantName;
    }

    public String getTargetProcessingPlantParticipantName() {
        return targetProcessingPlantParticipantName;
    }

    public void setTargetProcessingPlantParticipantName(String targetProcessingPlantParticipantName) {
        this.targetProcessingPlantParticipantName = targetProcessingPlantParticipantName;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskWorkItemSubscription{" +
                "contentDescriptor=" + contentDescriptor +
                ", containerDescriptor=" + containerDescriptor +
                ", payloadQuality=" + payloadQuality +
                ", normalisationStatus=" + normalisationStatus +
                ", validationStatus=" + validationStatus +
                ", dataParcelType=" + dataParcelType +
                ", externalSourceSystem='" + externalSourceSystem + '\'' +
                ", externalTargetSystem='" + externalTargetSystem + '\'' +
                ", enforcementPointApprovalStatus=" + enforcementPointApprovalStatus +
                ", interSubsystemDistributable=" + interSubsystemDistributable +
                ", dataParcelFlowDirection=" + dataParcelFlowDirection +
                ", sourceProcessingPlantParticipantName='" + sourceProcessingPlantParticipantName + '\'' +
                ", targetProcessingPlantParticipantName='" + targetProcessingPlantParticipantName + '\'' +
                '}';
    }

    //
    // To Simple String
    //

    public String toDotString(){
        StringBuilder descriptionBuilder = new StringBuilder();

        if(hasContentDescriptor()){
            descriptionBuilder.append("Content->"+ getContentDescriptor().toDotString() + "\n");
        }
        if(hasContainerDescriptor()){
            descriptionBuilder.append("Container->"+ getContainerDescriptor().toDotString() + "\n");
        }
        if(hasNormalisationStatus()){
            descriptionBuilder.append("<"+getNormalisationStatus().getToken()+">");
        }
        if(hasValidationStatus()){
            descriptionBuilder.append("<"+getValidationStatus().getToken()+">");
        }
        if(hasExternalSourceSystem()){
            descriptionBuilder.append("<From:"+getExternalSourceSystem()+">");
        }
        if(hasExternalTargetSystem()){
            descriptionBuilder.append("<To:"+getExternalTargetSystem()+">");
        }
        if(hasSourceProcessingPlantParticipantName()){
            descriptionBuilder.append("<InternalFrom:"+getSourceProcessingPlantParticipantName()+">");
        }
        if(hasTargetProcessingPlantParticipantName()){
            descriptionBuilder.append("<InternalTo:"+getTargetProcessingPlantParticipantName()+">");
        }
        if(hasEnforcementPointApprovalStatus()){
            descriptionBuilder.append("<"+getEnforcementPointApprovalStatus().getToken()+">");
        }
        descriptionBuilder.append("<Distributable:"+isInterSubsystemDistributable()+">");

        String description = descriptionBuilder.toString();
        return(description);
    }

}
