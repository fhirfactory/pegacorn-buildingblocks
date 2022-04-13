/*
 * Copyright (c) 2022 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.petasos.dataparcel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.*;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataParcelQualityStatement implements Serializable {
    private List<DataParcelFragmentQualityStatement> fragmentQualityStatementList;
    private DataParcelNormalisationStatusEnum contentNormalisationStatus;
    private DataParcelValidationStatusEnum contentValidationStatus;
    private PolicyEnforcementPointApprovalStatusEnum contentPolicyEnforcementStatus;
    private DataParcelInternallyDistributableStatusEnum contentInterSubsystemDistributable;
    private DataParcelExternallyDistributableStatusEnum contentExternalDistributionStatus;

    //
    // Constructor(s)
    //

    public DataParcelQualityStatement(){
        this.fragmentQualityStatementList = new ArrayList<>();
        this.contentPolicyEnforcementStatus = PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE;
        this.contentNormalisationStatus = DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE;
        this.contentValidationStatus = DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE;
        this.contentExternalDistributionStatus = DataParcelExternallyDistributableStatusEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE;
        this.contentInterSubsystemDistributable = DataParcelInternallyDistributableStatusEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE;
    }

    public DataParcelQualityStatement(DataParcelQualityStatement ori){
        this.fragmentQualityStatementList = new ArrayList<>();
        this.contentPolicyEnforcementStatus = PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE;
        this.contentNormalisationStatus = DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE;
        this.contentValidationStatus = DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE;
        this.contentExternalDistributionStatus = DataParcelExternallyDistributableStatusEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE;
        this.contentInterSubsystemDistributable = DataParcelInternallyDistributableStatusEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE;

        if(ori.hasContentPolicyEnforcementStatus()){
            setContentPolicyEnforcementStatus(ori.getContentPolicyEnforcementStatus());
        }

        if(ori.hasContentInterSubsystemDistributable()){
            setContentInterSubsystemDistributable(ori.getContentInterSubsystemDistributable());
        }

        if(ori.hasContentValidationStatus()){
            setContentValidationStatus(ori.getContentValidationStatus());
        }

        if(ori.hasContentNormalisationStatus()){
            setContentNormalisationStatus(ori.getContentNormalisationStatus());
        }

        if(ori.hasContentExternalDistributionStatus()){
            setContentExternalDistributionStatus(ori.getContentExternalDistributionStatus());
        }

        if(ori.hasFragmentQualityStatementList()){
            getFragmentQualityStatementList().clear();
            for(DataParcelFragmentQualityStatement currentStatement: ori.getFragmentQualityStatementList()){
                getFragmentQualityStatementList().add(SerializationUtils.clone(currentStatement));
            }
        }
    }

    //
    // ifExists (has)
    //

    @JsonIgnore
    public boolean hasFragmentQualityStatementList(){
        boolean hasValue = this.fragmentQualityStatementList != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasContentInterSubsystemDistributable(){
        boolean hasValue = this.contentInterSubsystemDistributable != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasContentExternalDistributionStatus(){
        boolean hasValue = this.contentExternalDistributionStatus != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasContentValidationStatus(){
        boolean hasValue = this.contentValidationStatus != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasContentNormalisationStatus(){
        boolean hasValue = this.contentNormalisationStatus != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasContentPolicyEnforcementStatus(){
        boolean hasValue = this.contentPolicyEnforcementStatus != null;
        return(hasValue);
    }

    //
    // Getters and Setters
    //

    public List<DataParcelFragmentQualityStatement> getFragmentQualityStatementList() {
        return fragmentQualityStatementList;
    }

    public void setFragmentQualityStatementList(List<DataParcelFragmentQualityStatement> fragmentQualityStatementList) {
        this.fragmentQualityStatementList = fragmentQualityStatementList;
    }

    public DataParcelNormalisationStatusEnum getContentNormalisationStatus() {
        return contentNormalisationStatus;
    }

    public void setContentNormalisationStatus(DataParcelNormalisationStatusEnum contentNormalisationStatus) {
        this.contentNormalisationStatus = contentNormalisationStatus;
    }

    public DataParcelValidationStatusEnum getContentValidationStatus() {
        return contentValidationStatus;
    }

    public void setContentValidationStatus(DataParcelValidationStatusEnum contentValidationStatus) {
        this.contentValidationStatus = contentValidationStatus;
    }

    public PolicyEnforcementPointApprovalStatusEnum getContentPolicyEnforcementStatus() {
        return contentPolicyEnforcementStatus;
    }

    public void setContentPolicyEnforcementStatus(PolicyEnforcementPointApprovalStatusEnum contentPolicyEnforcementStatus) {
        this.contentPolicyEnforcementStatus = contentPolicyEnforcementStatus;
    }

    public DataParcelInternallyDistributableStatusEnum getContentInterSubsystemDistributable() {
        return contentInterSubsystemDistributable;
    }

    public void setContentInterSubsystemDistributable(DataParcelInternallyDistributableStatusEnum contentInterSubsystemDistributable) {
        this.contentInterSubsystemDistributable = contentInterSubsystemDistributable;
    }

    public DataParcelExternallyDistributableStatusEnum getContentExternalDistributionStatus() {
        return contentExternalDistributionStatus;
    }

    public void setContentExternalDistributionStatus(DataParcelExternallyDistributableStatusEnum contentExternalDistributionStatus) {
        this.contentExternalDistributionStatus = contentExternalDistributionStatus;
    }

    //
    // toString
    //

    @Override
    public String toString() {
        return "DataParcelQualityStatement{" +
                "fragmentQualityStatementList=" + fragmentQualityStatementList +
                ", contentNormalisationStatus=" + contentNormalisationStatus +
                ", contentValidationStatus=" + contentValidationStatus +
                ", contentPolicyEnforcementStatus=" + contentPolicyEnforcementStatus +
                ", interSubsystemDistributable=" + contentInterSubsystemDistributable +
                ", contentExternalDistributionStatus=" + contentExternalDistributionStatus +
                '}';
    }
}
