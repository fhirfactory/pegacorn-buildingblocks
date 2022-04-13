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
package net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelFragmentQualityStatement;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelQualityStatement;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.valuesets.*;

import java.io.Serializable;
import java.util.List;

public class DataParcelQualitySubscriptionMaskType implements Serializable {
    private List<DataParcelFragmentQualityStatement> fragmentQualityStatementList;
    private DataParcelNormalisationStatusSubscriptionMaskEnum contentNormalisationStatusMask;
    private DataParcelValidationStatusSubscriptionMaskEnum contentValidationStatusMask;
    private PolicyEnforcementPointApprovalStatusSubscriptionMaskEnum contentPolicyEnforcementStatusMask;
    private DataParcelInternallyDistributableStatusSubscriptionMaskEnum interSubsystemDistributableMask;
    private DataParcelExternallyDistributableStatusSubscriptionMaskEnum contentExternalDistributionStatusMask;

    //
    // Constructor(s)
    //

    public DataParcelQualitySubscriptionMaskType(){
        this.contentPolicyEnforcementStatusMask = PolicyEnforcementPointApprovalStatusSubscriptionMaskEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_ANY;
        this.contentNormalisationStatusMask = DataParcelNormalisationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY;
        this.contentValidationStatusMask = DataParcelValidationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY;
        this.contentExternalDistributionStatusMask = DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE;
        this.interSubsystemDistributableMask = DataParcelInternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_ANY;
    }

    public DataParcelQualitySubscriptionMaskType(DataParcelQualitySubscriptionMaskType ori){
        this.contentPolicyEnforcementStatusMask = PolicyEnforcementPointApprovalStatusSubscriptionMaskEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_ANY;
        this.contentNormalisationStatusMask = DataParcelNormalisationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY;
        this.contentValidationStatusMask = DataParcelValidationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY;
        this.contentExternalDistributionStatusMask = DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE;
        this.interSubsystemDistributableMask = DataParcelInternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_ANY;

        if(ori.hasContentValidationStatusMask()){
            setContentValidationStatusMask(ori.contentValidationStatusMask);
        }

        if(ori.hasInterSubsystemDistributableMask()){
            setInterSubsystemDistributableMask(ori.getInterSubsystemDistributableMask());
        }

        if(ori.hasContentNormalisationStatusMask()){
            ori.setContentNormalisationStatusMask(ori.getContentNormalisationStatusMask());
        }

        if(ori.hasContentPolicyEnforcementStatusMask()){
            ori.setContentPolicyEnforcementStatusMask(ori.getContentPolicyEnforcementStatusMask());
        }

        if(ori.hasContentExternalDistributionStatusMask()){
            setContentExternalDistributionStatusMask(ori.getContentExternalDistributionStatusMask());
        }
    }


    //
    // ifExists (has)
    //

    @JsonIgnore
    public boolean hasInterSubsystemDistributableMask(){
        boolean hasValue = this.interSubsystemDistributableMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasContentExternalDistributionStatusMask(){
        boolean hasValue = this.contentExternalDistributionStatusMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasContentValidationStatusMask(){
        boolean hasValue = this.contentValidationStatusMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasContentNormalisationStatusMask(){
        boolean hasValue = contentNormalisationStatusMask != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasContentPolicyEnforcementStatusMask(){
        boolean hasValue = this.contentPolicyEnforcementStatusMask != null;
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

    public DataParcelNormalisationStatusSubscriptionMaskEnum getContentNormalisationStatusMask() {
        return contentNormalisationStatusMask;
    }

    public void setContentNormalisationStatusMask(DataParcelNormalisationStatusSubscriptionMaskEnum contentNormalisationStatusMask) {
        this.contentNormalisationStatusMask = contentNormalisationStatusMask;
    }

    public DataParcelValidationStatusSubscriptionMaskEnum getContentValidationStatusMask() {
        return contentValidationStatusMask;
    }

    public void setContentValidationStatusMask(DataParcelValidationStatusSubscriptionMaskEnum contentValidationStatusMask) {
        this.contentValidationStatusMask = contentValidationStatusMask;
    }

    public PolicyEnforcementPointApprovalStatusSubscriptionMaskEnum getContentPolicyEnforcementStatusMask() {
        return contentPolicyEnforcementStatusMask;
    }

    public void setContentPolicyEnforcementStatusMask(PolicyEnforcementPointApprovalStatusSubscriptionMaskEnum contentPolicyEnforcementStatusMask) {
        this.contentPolicyEnforcementStatusMask = contentPolicyEnforcementStatusMask;
    }

    public DataParcelInternallyDistributableStatusSubscriptionMaskEnum getInterSubsystemDistributableMask() {
        return interSubsystemDistributableMask;
    }

    public void setInterSubsystemDistributableMask(DataParcelInternallyDistributableStatusSubscriptionMaskEnum interSubsystemDistributableMask) {
        this.interSubsystemDistributableMask = interSubsystemDistributableMask;
    }

    public DataParcelExternallyDistributableStatusSubscriptionMaskEnum getContentExternalDistributionStatusMask() {
        return contentExternalDistributionStatusMask;
    }

    public void setContentExternalDistributionStatusMask(DataParcelExternallyDistributableStatusSubscriptionMaskEnum contentExternalDistributionStatusMask) {
        this.contentExternalDistributionStatusMask = contentExternalDistributionStatusMask;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return "DataParcelQualitySubscriptionMaskType{" +
                "fragmentQualityStatementList=" + fragmentQualityStatementList +
                ", contentNormalisationStatus=" + contentNormalisationStatusMask +
                ", contentValidationStatus=" + contentValidationStatusMask +
                ", contentPolicyEnforcementStatus=" + contentPolicyEnforcementStatusMask +
                ", interSubsystemDistributable=" + interSubsystemDistributableMask +
                ", contentExternalDistributionStatus=" + contentExternalDistributionStatusMask +
                '}';
    }

    //
    // Business Logic
    //

    public boolean applyMask(DataParcelQualityStatement qualityStatement){
        boolean passesMask = false;
        if(qualityStatement == null){
            return(false);
        }
        boolean passesValidationMask = false;
        if(!hasContentValidationStatusMask() && !(qualityStatement.hasContentValidationStatus())){
            passesValidationMask = true;
        }
        if(hasContentValidationStatusMask()){
            if(getContentValidationStatusMask().equals(DataParcelValidationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY)){
                passesValidationMask = true;
            } else {
                if(qualityStatement.hasContentValidationStatus()) {
                    boolean validationMaskIsTrue = getContentExternalDistributionStatusMask().equals(DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_TRUE);
                    boolean validationMaskIsFalse = getContentExternalDistributionStatusMask().equals(DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE);
                    boolean validationIsTrue = qualityStatement.getContentValidationStatus().equals(DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_TRUE);
                    boolean validationIsFalse = getContentExternalDistributionStatusMask().equals(DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE);
                    if (validationIsFalse && validationMaskIsFalse) {
                        passesValidationMask = true;
                    }
                    if (validationIsTrue && validationMaskIsTrue) {
                        passesValidationMask = true;
                    }
                }
            }
        } else {
            if(!qualityStatement.hasContentValidationStatus()){
                passesValidationMask = true;
            }
        }
    }
}
