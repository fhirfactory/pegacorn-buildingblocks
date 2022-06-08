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
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.DataParcelExternallyDistributableStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.DataParcelValidationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes.common.SubscriptionMaskBase;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.valuesets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataParcelQualitySubscriptionMaskType extends SubscriptionMaskBase {
    private static final Logger LOG = LoggerFactory.getLogger(DataParcelQualitySubscriptionMaskType.class);

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
        super();
        this.contentPolicyEnforcementStatusMask = PolicyEnforcementPointApprovalStatusSubscriptionMaskEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_ANY;
        this.contentNormalisationStatusMask = DataParcelNormalisationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY;
        this.contentValidationStatusMask = DataParcelValidationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY;
        this.contentExternalDistributionStatusMask = DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE;
        this.interSubsystemDistributableMask = DataParcelInternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_INTERNALLY_DISTRIBUTABLE_ANY;
        this.fragmentQualityStatementList = new ArrayList<>();
    }

    public DataParcelQualitySubscriptionMaskType(DataParcelQualitySubscriptionMaskType ori){
        super(ori);
        this.contentPolicyEnforcementStatusMask = PolicyEnforcementPointApprovalStatusSubscriptionMaskEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_ANY;
        this.contentNormalisationStatusMask = DataParcelNormalisationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY;
        this.contentValidationStatusMask = DataParcelValidationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY;
        this.contentExternalDistributionStatusMask = DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE;
        this.interSubsystemDistributableMask = DataParcelInternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_INTERNALLY_DISTRIBUTABLE_ANY;
        this.fragmentQualityStatementList = new ArrayList<>();

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

    public DataParcelQualitySubscriptionMaskType(DataParcelQualityStatement ori){
        super();
        this.contentPolicyEnforcementStatusMask = PolicyEnforcementPointApprovalStatusSubscriptionMaskEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_ANY;
        this.contentNormalisationStatusMask = DataParcelNormalisationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY;
        this.contentValidationStatusMask = DataParcelValidationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY;
        this.contentExternalDistributionStatusMask = DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE;
        this.interSubsystemDistributableMask = DataParcelInternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_INTERNALLY_DISTRIBUTABLE_ANY;
        this.fragmentQualityStatementList = new ArrayList<>();

        if(ori.hasContentValidationStatus()){
            switch(ori.getContentValidationStatus()){
                case DATA_PARCEL_CONTENT_VALIDATED_TRUE:
                    setContentValidationStatusMask(DataParcelValidationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_VALIDATED_TRUE);
                    break;
                case DATA_PARCEL_CONTENT_VALIDATED_FALSE:
                    setContentValidationStatusMask(DataParcelValidationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE);
                    break;
            }
        }
        if(ori.hasContentExternalDistributionStatus()){
            switch(ori.getContentExternalDistributionStatus()){
                case DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_TRUE:
                    setContentExternalDistributionStatusMask(DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_TRUE);
                    break;
                case DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE:
                    setContentExternalDistributionStatusMask(DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE);
                    break;
            }
        }
        if(ori.hasContentInterSubsystemDistributable()){
            switch(ori.getContentInterSubsystemDistributable()){
                case DATA_PARCEL_INTERNALLY_DISTRIBUTABLE_TRUE:
                    setInterSubsystemDistributableMask(DataParcelInternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_INTERNALLY_DISTRIBUTABLE_TRUE);
                    break;
                case DATA_PARCEL_INTERNALLY_DISTRIBUTABLE_FALSE:
                    setInterSubsystemDistributableMask(DataParcelInternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_INTERNALLY_DISTRIBUTABLE_FALSE);
                    break;
            }
        }
        if(ori.hasContentNormalisationStatus()){
            switch(ori.getContentNormalisationStatus()){
                case DATA_PARCEL_CONTENT_NORMALISATION_FALSE:
                    setContentNormalisationStatusMask(DataParcelNormalisationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE);
                    break;
                case DATA_PARCEL_CONTENT_NORMALISATION_TRUE:
                    setContentNormalisationStatusMask(DataParcelNormalisationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_NORMALISATION_TRUE);
                    break;
            }
        }
        if(ori.hasContentPolicyEnforcementStatus()){
            switch(ori.getContentPolicyEnforcementStatus()){
                case POLICY_ENFORCEMENT_POINT_APPROVAL_POSITIVE:
                    setContentPolicyEnforcementStatusMask(PolicyEnforcementPointApprovalStatusSubscriptionMaskEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_POSITIVE);
                    break;
                case POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE:
                    setContentPolicyEnforcementStatusMask(PolicyEnforcementPointApprovalStatusSubscriptionMaskEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE);
                    break;
            }
        }
        if(ori.hasFragmentQualityStatementList()){
            getFragmentQualityStatementList().clear();
            getFragmentQualityStatementList().addAll(ori.getFragmentQualityStatementList());
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

    // Logger

    @JsonIgnore
    @Override
    protected Logger getLogger(){
        return(LOG);
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
                ", allowAll=" + getAllowAll() +
                '}';
    }

    //
    // Business Logic (Mask Pass)
    //

    public boolean applyMask(DataParcelQualityStatement qualityStatement){
        getLogger().debug(".applyMask(): Entry, qualityStatement->{}", qualityStatement);

        if(hasAllowAll()){
            if(getAllowAll()){
                getLogger().debug(".applyMask(): Exit, allowAll is true, returning -true-");
                return(true);
            }
        }

        boolean passesExternallyDistributableMask = applyExternallyDistributableMask(qualityStatement);
        boolean passesNormalisationMask = applyNormalisationMask(qualityStatement);
        boolean passesValidationMask = applyValidationMask(qualityStatement);
        boolean passesInternallyDistributableMask = applyInterSubsystemDistributableMask(qualityStatement);
        boolean passesPolicyEnforcementMask = applyPoliceEnforcementMask(qualityStatement);

        boolean passesMask  = passesExternallyDistributableMask && passesNormalisationMask && passesValidationMask && passesInternallyDistributableMask && passesPolicyEnforcementMask;

        getLogger().debug(".applyMask(): Exit, passesMask->{}", passesMask);
        return(passesMask);
    }

    protected boolean applyExternallyDistributableMask(DataParcelQualityStatement testQualityStatement){
        getLogger().debug(".externallyDistributableMatches(): Entry");
        if(hasContentExternalDistributionStatusMask()) {
            if(getContentExternalDistributionStatusMask().equals(DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_ANY)){
                getLogger().debug(".externallyDistributableMatches(): Exit, subscribedManifest has requested 'ANY', returning -true-");
                return(true);
            }
        }
        if(testQualityStatement == null && !hasContentValidationStatusMask()){
            getLogger().debug(".externallyDistributableMatches(): Exit, testQualityStatement is null AND subscriptionMask null, returning -true-");
            return(true);
        }
        if(!testQualityStatement.hasContentExternalDistributionStatus() && hasContentExternalDistributionStatusMask()){
            getLogger().debug(".externallyDistributableMatches(): Exit, testQualityStatement is null AND subscriptionMask is not null, returning -false-");
            return(false);
        }
        if(testQualityStatement.hasContentExternalDistributionStatus() && !hasContentExternalDistributionStatusMask()){
            getLogger().debug(".externallyDistributableMatches(): Exit, testQualityStatement is not null AND subscriptionMask is null, returning -false-");
            return(false);
        }
        boolean externallyDistributableMaskIsTrue = getContentExternalDistributionStatusMask().equals(DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_TRUE);
        boolean externallyDistributableMaskIsFalse = getContentExternalDistributionStatusMask().equals(DataParcelExternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE);
        boolean externallyDistributableIsTrue = testQualityStatement.getContentExternalDistributionStatus().equals(DataParcelExternallyDistributableStatusEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_TRUE);
        boolean externallyDistributableIsFalse = testQualityStatement.getContentExternalDistributionStatus().equals(DataParcelExternallyDistributableStatusEnum.DATA_PARCEL_EXTERNALLY_DISTRIBUTABLE_FALSE);
        if (externallyDistributableMaskIsFalse && externallyDistributableIsFalse) {
            getLogger().debug(".externallyDistributableMatches(): Exit, (matching false) returning -true-");
            return(true);
        }
        if (externallyDistributableMaskIsTrue && externallyDistributableIsTrue) {
            getLogger().debug(".externallyDistributableMatches(): Exit, (matching true) returning -true-");
            return(true);
        }
        getLogger().debug(".externallyDistributableMatches(): Exit, returning -false-");
        return(false);
    }

    protected boolean applyNormalisationMask(DataParcelQualityStatement testQualityStatement){
        getLogger().debug(".applyNormalisationMask(): Entry");

        if(hasContentNormalisationStatusMask()) {
            if (getContentNormalisationStatusMask().equals(DataParcelNormalisationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY)) {
                getLogger().debug(".applyNormalisationMask(): Exit, testQualityStatement has requested 'ANY', returning -true-");
                return (true);
            }
        }
        if(testQualityStatement == null && !hasContentNormalisationStatusMask()){
            getLogger().debug(".applyNormalisationMask(): Exit, testQualityStatement is null AND subscriptionMask null, returning -true-");
            return(true);
        }
        if(!testQualityStatement.hasContentNormalisationStatus() && hasContentNormalisationStatusMask()){
            getLogger().debug(".applyNormalisationMask(): Exit, testQualityStatement is null AND subscriptionMask is not null, returning -false-");
            return(false);
        }
        if(testQualityStatement.hasContentNormalisationStatus() && !hasContentNormalisationStatusMask()){
            getLogger().debug(".applyNormalisationMask(): Exit, testQualityStatement is not null AND subscriptionMask is null, returning -false-");
            return(false);
        }
        boolean normalisationMaskIsTrue = getContentNormalisationStatusMask().equals(DataParcelNormalisationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_NORMALISATION_TRUE);
        boolean normalisationMaskIsFalse = getContentNormalisationStatusMask().equals(DataParcelNormalisationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE);
        boolean normalisationIsTrue = testQualityStatement.getContentNormalisationStatus().equals(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_TRUE);
        boolean normalisationIsFalse = testQualityStatement.getContentNormalisationStatus().equals(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE);
        if (normalisationMaskIsFalse && normalisationIsFalse) {
            getLogger().debug(".applyNormalisationMask(): Exit, (matching false) returning -true-");
            return(true);
        }
        if (normalisationMaskIsTrue && normalisationIsTrue) {
            getLogger().debug(".applyNormalisationMask(): Exit, (matching true) returning -true-");
            return(true);
        }
        getLogger().debug(".applyNormalisationMask(): Exit, returning -false-");
        return(false);
    }

    protected boolean applyValidationMask(DataParcelQualityStatement testQualityStatement) {
        getLogger().debug(".applyValidationMask(): Entry");
        if(hasContentValidationStatusMask()) {
            if (getContentValidationStatusMask().equals(DataParcelValidationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY)) {
                getLogger().debug(".applyValidationMask(): Exit, subscribedManifest has requested 'ANY', returning -true-");
                return (true);
            }
        }
        if(testQualityStatement == null && !hasContentValidationStatusMask()){
            getLogger().debug(".applyValidationMask(): Exit, testQualityStatement is null AND subscriptionMask null, returning -true-");
            return(true);
        }
        if(!testQualityStatement.hasContentValidationStatus() && hasContentValidationStatusMask()){
            getLogger().debug(".applyValidationMask(): Exit, testQualityStatement is null AND subscriptionMask is not null, returning -false-");
            return(false);
        }
        if(testQualityStatement.hasContentValidationStatus() && !hasContentValidationStatusMask()){
            getLogger().debug(".applyValidationMask(): Exit, testQualityStatement is not null AND subscriptionMask is null, returning -false-");
            return(false);
        }
        boolean validationMaskIsTrue = getContentValidationStatusMask().equals(DataParcelValidationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_VALIDATED_TRUE);
        boolean validationMaskIsFalse = getContentValidationStatusMask().equals(DataParcelNormalisationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE);
        boolean validationIsTrue = testQualityStatement.getContentValidationStatus().equals(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_TRUE);
        boolean validationIsFalse = testQualityStatement.getContentValidationStatus().equals(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE);
        if (validationMaskIsFalse && validationIsFalse) {
            getLogger().debug(".applyValidationMask(): Exit, (matching false) returning -true-");
            return(true);
        }
        if (validationMaskIsTrue && validationIsTrue) {
            getLogger().debug(".applyValidationMask(): Exit, (matching true) returning -true-");
            return(true);
        }
        getLogger().debug(".applyValidationMask(): Exit, returning -false-");
        return(false);
    }

    protected boolean applyInterSubsystemDistributableMask(DataParcelQualityStatement testQualityStatement) {
        getLogger().debug(".applyInternalDistributableMask(): Entry");
        if(hasInterSubsystemDistributableMask()) {
            if (getInterSubsystemDistributableMask().equals(DataParcelInternallyDistributableStatusSubscriptionMaskEnum.DATA_PARCEL_INTERNALLY_DISTRIBUTABLE_ANY)) {
                getLogger().debug(".applyInternalDistributableMask(): Exit, subscribedManifest has requested 'ANY', returning -true-");
                return (true);
            }
        }
        if(testQualityStatement == null && !hasInterSubsystemDistributableMask()){
            getLogger().debug(".applyInternalDistributableMask(): Exit, testQualityStatement is null AND subscriptionMask null, returning -true-");
            return(true);
        }
        if(!testQualityStatement.hasContentInterSubsystemDistributable() && hasInterSubsystemDistributableMask()){
            getLogger().debug(".applyInternalDistributableMask(): Exit, testQualityStatement is null AND subscriptionMask is not null, returning -false-");
            return(false);
        }
        if(testQualityStatement.hasContentInterSubsystemDistributable() && !hasInterSubsystemDistributableMask()){
            getLogger().debug(".applyInternalDistributableMask(): Exit, testQualityStatement is not null AND subscriptionMask is null, returning -false-");
            return(false);
        }
        boolean internallyDistributableMaskIsTrue = getContentValidationStatusMask().equals(DataParcelValidationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_VALIDATED_TRUE);
        boolean internallyDistributableMaskIsFalse = getContentValidationStatusMask().equals(DataParcelNormalisationStatusSubscriptionMaskEnum.DATA_PARCEL_CONTENT_NORMALISATION_FALSE);
        boolean internallyDistributableIsTrue = testQualityStatement.getContentValidationStatus().equals(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_TRUE);
        boolean internallyDistributableIsFalse = testQualityStatement.getContentValidationStatus().equals(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE);
        if (internallyDistributableMaskIsFalse && internallyDistributableIsFalse) {
            getLogger().debug(".applyInternalDistributableMask(): Exit, (matching false) returning -true-");
            return(true);
        }
        if (internallyDistributableMaskIsTrue && internallyDistributableIsTrue) {
            getLogger().debug(".applyInternalDistributableMask(): Exit, (matching true) returning -true-");
            return(true);
        }
        getLogger().debug(".applyInternalDistributableMask(): Exit, returning -false-");
        return(false);
    }

    protected boolean applyPoliceEnforcementMask(DataParcelQualityStatement testQualityStatement) {
        getLogger().debug(".applyPoliceEnforcementMask(): Entry");
        if(hasInterSubsystemDistributableMask()) {
            if (getContentPolicyEnforcementStatusMask().equals(PolicyEnforcementPointApprovalStatusSubscriptionMaskEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_ANY)) {
                getLogger().debug(".applyPoliceEnforcementMask(): Exit, subscribedManifest has requested 'ANY', returning -true-");
                return (true);
            }
        }
        if(testQualityStatement == null && !hasContentPolicyEnforcementStatusMask()){
            getLogger().debug(".applyPoliceEnforcementMask(): Exit, testQualityStatement is null AND subscriptionMask null, returning -true-");
            return(true);
        }
        if(!testQualityStatement.hasContentPolicyEnforcementStatus() && hasContentPolicyEnforcementStatusMask()){
            getLogger().debug(".applyPoliceEnforcementMask(): Exit, testQualityStatement.hasContentPolicyEnforcementStatus() is false AND subscriptionMask is not null, returning -false-");
            return(false);
        }
        if(testQualityStatement.hasContentPolicyEnforcementStatus() && !hasContentPolicyEnforcementStatusMask()){
            getLogger().debug(".applyPoliceEnforcementMask(): Exit, testQualityStatement.hasContentPolicyEnforcementStatus() is true AND subscriptionMask is null, returning -false-");
            return(false);
        }
        boolean policyEnforcementMaskIsPositive = getContentPolicyEnforcementStatusMask().equals(PolicyEnforcementPointApprovalStatusSubscriptionMaskEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_POSITIVE);
        boolean policyEnforcementMaskIsNegative = getContentPolicyEnforcementStatusMask().equals(PolicyEnforcementPointApprovalStatusSubscriptionMaskEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE);
        boolean policyEnforcementIsPositive = testQualityStatement.getContentPolicyEnforcementStatus().equals(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_POSITIVE);
        boolean policyEnforcementIsNegative = testQualityStatement.getContentPolicyEnforcementStatus().equals(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE);
        if (policyEnforcementMaskIsNegative && policyEnforcementIsNegative) {
            getLogger().debug(".applyPoliceEnforcementMask(): Exit, (matching false) returning -true-");
            return(true);
        }
        if (policyEnforcementMaskIsPositive && policyEnforcementIsPositive) {
            getLogger().debug(".applyPoliceEnforcementMask(): Exit, (matching true) returning -true-");
            return(true);
        }
        getLogger().debug(".applyPoliceEnforcementMask(): Exit, returning -false-");
        return(false);
    }

}
