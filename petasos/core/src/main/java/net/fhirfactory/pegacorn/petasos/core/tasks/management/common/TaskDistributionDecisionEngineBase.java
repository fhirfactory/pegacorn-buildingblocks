/*
 * Copyright (c) 2020 MAHun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of subscription software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and subscription permission notice shall be included in all
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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.common;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelValidationStatusEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;
import net.fhirfactory.pegacorn.petasos.participants.cache.LocalPetasosParticipantCacheDM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class TaskDistributionDecisionEngineBase {

    @Inject
    protected LocalPetasosParticipantCacheDM localPetasosParticipantCacheDM;

    @Inject
    protected ProcessingPlantInterface processingPlant;

    //
    // Constructor(s)
    //

    //
    // Getters (and Setters)
    //

    abstract protected Logger getLogger();

    protected LocalPetasosParticipantCacheDM getLocalPetasosParticipantCacheDM(){
        return(localPetasosParticipantCacheDM);
    }

    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlant);
    }

    //
    // More sophisticated SubscriberList derivation
    //

    //
    // Helper Methods To Filter Implementation
    //

    protected boolean containerDescriptorIsEqual(DataParcelManifest publisherManifest, TaskWorkItemSubscriptionType subscribedManifest){
        getLogger().debug(".containerIsEqual(): Entry");
        if(publisherManifest == null || subscribedManifest == null){
            getLogger().debug(".containerIsEqual(): publisherManifest or subscribedManifest is null, return -false-");
            return(false);
        }
        getLogger().trace(".containerIsEqual(): publisherManifest & subscribedManifest are bot NOT null");
        getLogger().trace(".containerIsEqual(): checking to see if publisherManifest has a containerDescriptor");
        boolean testManifestHasContainerDescriptor = publisherManifest.hasContainerDescriptor();
        getLogger().trace(".containerIsEqual(): checking to see if subscribedManifest has a containerDescriptor");
        boolean subscribedManifestHasContainerDescriptor = subscribedManifest.hasContainerDescriptor();
        if(!testManifestHasContainerDescriptor && !subscribedManifestHasContainerDescriptor) {
            getLogger().debug(".contentIsEqual(): Exit, neither publisherManifest or subscribedManifest has a containerDescriptor, returning -true-");
            return(true);
        }
        if(!subscribedManifestHasContainerDescriptor){
            getLogger().debug(".containerIsEqual(): Exit, subscribedManifest has no containerDescriptor, but publisherManifest does, returning -false-");
            return(false);
        }
        DataParcelTypeDescriptor subscriberContainer = subscribedManifest.getContainerDescriptor();
        DataParcelTypeDescriptor publishedContainer = publisherManifest.getContainerDescriptor();
        if(publishedContainer == null) {
            getLogger().debug(".containerIsEqual(): Exit, publisherManifest has no containerDescriptor, but subscribedManifest does, returning -false-");
            publishedContainer = new DataParcelTypeDescriptor();
        }
        getLogger().trace(".containerIsEqual(): publisherManifest and subscribedManifest both have containerDescriptors, now testing for equality");

        boolean containersAreEqual = publishedContainer.isEqualWithWildcardsInOther(subscriberContainer);

        getLogger().debug(".containerIsEqual(): Exit, publisherManifest and subscribedManifest containerDescriptor comparison yielded->{}", containersAreEqual);
        return(containersAreEqual);
    }

    protected boolean contentDescriptorIsEqual(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest){
        getLogger().debug(".contentIsEqual(): Entry");
        if(testManifest == null || subscribedManifest == null){
            getLogger().debug(".contentIsEqual(): testManifest or subscribedManifest is null, return -false-");
            return(false);
        }
        getLogger().trace(".contentIsEqual(): testManifest & subscribedManifest are bot NOT null");
        getLogger().trace(".contentIsEqual(): checking to see if testManifest has a contentDescriptor");
        boolean testManifestHasContentDescriptor = testManifest.hasContentDescriptor();
        getLogger().trace(".contentIsEqual(): checking to see if subscribedManifest has a contentDescriptor");
        boolean subscribedManifestHasContentDescriptor = subscribedManifest.hasContentDescriptor();
        if(!testManifestHasContentDescriptor ) {
            getLogger().debug(".contentIsEqual(): Exit, testManifest has not contentDescriptor, returning -false-");
            return(false);
        }
        if(!subscribedManifestHasContentDescriptor){
            getLogger().debug(".contentIsEqual(): Exit, subscribedManifest has not contentDescriptor, returning -false-");
            return(false);
        }

        DataParcelTypeDescriptor testDescriptor = testManifest.getContentDescriptor();
        DataParcelTypeDescriptor subscribedDescriptor = subscribedManifest.getContentDescriptor();

        boolean equalWithWildcardsInOther = testDescriptor.isEqualWithWildcardsInOther(subscribedDescriptor);

        getLogger().debug(".contentIsEqual(): Exit, equalWithWildcardsInOther->{}", equalWithWildcardsInOther);
        return (equalWithWildcardsInOther);
    }

    protected boolean containerDescriptorOnlyEqual(DataParcelManifest publisherManifest, TaskWorkItemSubscriptionType subscribedManifest){
        getLogger().debug(".containerOnlyEqual(): Entry");
        if(publisherManifest == null || subscribedManifest == null){
            getLogger().debug(".containerOnlyEqual(): testManifest or subscribedManifest is null, return -false-");
            return(false);
        }
        getLogger().trace(".containerOnlyEqual(): testManifest & subscribedManifest are bot NOT null");
        getLogger().trace(".containerOnlyEqual(): checking to see if subscribedManifest has a contentDescriptor && containerDescriptor");
        if(subscribedManifest.hasContainerDescriptor() && subscribedManifest.hasContentDescriptor()){
            getLogger().trace(".containerOnlyEqual(): subscribedManifest has both contentDescriptor && containerDescriptor, checking to see if they are the same");
            if(!subscribedManifest.getContainerDescriptor().equals(subscribedManifest.getContentDescriptor())){
                getLogger().debug(".containerOnlyEqual(): contentDescriptor && containerDescriptor are different, so subscription subscriberManifest is after specific content, returning -false-");
                return(false);
            }
        }
        getLogger().trace(".containerOnlyEqual(): subscribedManifest does not have a ContentDescriber!, checking comparisons of the container only");
        if(publisherManifest.hasContainerDescriptor() && subscribedManifest.hasContainerDescriptor()){
            getLogger().trace(".containerOnlyEqual(): publisherManifest cotnains a ContainerDescriptor, so comparing");
            boolean containerIsEqual = containerDescriptorIsEqual(publisherManifest, subscribedManifest);
            getLogger().debug(".containerOnlyEqual(): Comparison of ContainerContent is ->{}, returning it!", containerIsEqual);
            return(containerIsEqual);
        }
        getLogger().debug(".containerOnlyEqual(): Publisher does not have a ContainerDescriptor, returning -false-");
        return(false);
    }

    protected boolean normalisationMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest){
        getLogger().debug(".normalisationMatches(): Entry");
        if(testManifest == null || subscribedManifest == null){
            getLogger().debug(".normalisationMatches(): Exit, either testManifest or subscribedManifest are null, returning -false-");
            return(false);
        }
        getLogger().trace(".normalisationMatches(): subscribedManifest.getNormalisationStatus()->{}", subscribedManifest.getNormalisationStatus());
        if(subscribedManifest.getNormalisationStatus().equals(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_ANY)){
            getLogger().debug(".normalisationMatches(): Exit, subscribedManifest has requested 'ANY', returning -true-");
            return(true);
        }
        boolean normalisationStatusIsEqual = subscribedManifest.getNormalisationStatus().equals(testManifest.getNormalisationStatus());
        getLogger().debug(".normalisationMatches(): Exit, returning comparison result->{}", normalisationStatusIsEqual);
        return(normalisationStatusIsEqual);
    }

    protected boolean validationMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest) {
        if (testManifest == null || subscribedManifest == null) {
            return (false);
        }
        if(!subscribedManifest.hasValidationStatus() && !testManifest.hasValidationStatus()){
            return(true);
        }
        if(!subscribedManifest.hasValidationStatus()){
            return(false);
        }
        if(subscribedManifest.hasValidationStatus()) {
            if (subscribedManifest.getValidationStatus().equals(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATION_ANY)) {
                return (true);
            }
        }
        if(!testManifest.hasValidationStatus()){
            return(false);
        }
        boolean validationStatusIsEqual = subscribedManifest.getValidationStatus().equals(testManifest.getValidationStatus());
        return(validationStatusIsEqual);
    }

    protected boolean manifestTypeMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest) {
        if (testManifest == null || subscribedManifest == null) {
            return (false);
        }
        boolean manifestTypeMatches = subscribedManifest.getDataParcelType().equals(testManifest.getDataParcelType());
        return(manifestTypeMatches);
    }

    protected boolean sourceSystemMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest) {
        if (testManifest == null && subscribedManifest == null) {
            return (false);
        }
        if (testManifest == null || subscribedManifest == null) {
            return (false);
        }
        if(subscribedManifest.hasExternalSourceSystem()){
            if(subscribedManifest.getExternalSourceSystem().contentEquals("*")){
                return(true);
            }
        }
        if(!testManifest.hasSourceSystem() && !subscribedManifest.hasExternalSourceSystem()){
            return(true);
        }
        if (testManifest.hasSourceSystem() && subscribedManifest.hasExternalSourceSystem()) {
            boolean sourceIsSame = testManifest.getSourceSystem().contentEquals(subscribedManifest.getExternalSourceSystem());
            return (sourceIsSame);
        }
        return(false);
    }

    protected boolean targetSystemMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest) {
        if (testManifest == null && subscribedManifest == null) {
            return (false);
        }
        if (testManifest == null || subscribedManifest == null) {
            return (false);
        }
        if(subscribedManifest.hasExternalTargetSystem()){
            if(subscribedManifest.getExternalTargetSystem().contentEquals("*")){
                return(true);
            }
        }
        if(!testManifest.hasIntendedTargetSystem() && !subscribedManifest.hasExternalTargetSystem()){
            return(true);
        }
        if (testManifest.hasIntendedTargetSystem() && subscribedManifest.hasExternalTargetSystem()) {
            boolean targetIsSame = testManifest.getIntendedTargetSystem().contentEquals(subscribedManifest.getExternalTargetSystem());
            return (targetIsSame);
        }
        if(!subscribedManifest.hasExternalTargetSystem()){
            return(true);
        }
        return(false);
    }

    protected boolean enforcementPointApprovalStatusMatches(DataParcelManifest publishedManifest, TaskWorkItemSubscriptionType subscribedManifest) {
        getLogger().debug(".enforcementPointApprovalStatusMatches(): Entry");
        if (publishedManifest == null || subscribedManifest == null) {
            getLogger().debug(".enforcementPointApprovalStatusMatches(): Exit, either publishedManifest or subscribedManifest are null, returning -false-");
            return (false);
        }
        if (subscribedManifest.getEnforcementPointApprovalStatus().equals(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_ANY)) {
            getLogger().debug(".enforcementPointApprovalStatusMatches(): Exit, subscribedManifest is set to 'ANY', returning -true-");
            return (true);
        }
        getLogger().trace(".enforcementPointApprovalStatusMatches(): publishedManifest PEP Status->{}", publishedManifest.getEnforcementPointApprovalStatus());
        getLogger().trace(".enforcementPointApprovalStatusMatches(): subscribedManifest PEP Status->{}", subscribedManifest.getEnforcementPointApprovalStatus());
        boolean approvalStatusMatch = subscribedManifest.getEnforcementPointApprovalStatus().equals(publishedManifest.getEnforcementPointApprovalStatus());
        return (approvalStatusMatch);
    }

    protected boolean isDistributableMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest) {
        if (testManifest == null || subscribedManifest == null) {
            return (false);
        }
        return (testManifest.isInterSubsystemDistributable() == subscribedManifest.isInterSubsystemDistributable());
    }

    protected boolean parcelFlowDirectionMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest){
        if(testManifest == null || subscribedManifest == null){
            return(false);
        }
        boolean directionMatches = testManifest.getDataParcelFlowDirection() == subscribedManifest.getDataParcelFlowDirection();
        return(directionMatches);
    }

    protected boolean originParticipantFilter(DataParcelManifest publishedManifest, TaskWorkItemSubscriptionType subscribedManifest) {
        getLogger().debug(".originParticipantFilter(): Entry");
        if (publishedManifest == null || subscribedManifest == null) {
            getLogger().debug(".originParticipantFilter(): Exit, either publishedManifest or subscribedManifest are null, returning -false-");
            return (false);
        }
        if (!subscribedManifest.hasOriginParticipant() && !publishedManifest.hasOriginParticipant()) {
            getLogger().debug(".originParticipantFilter(): Neither subscribedManifest or publishedManifest have originParticipants, returning true");
            return (true);
        }
        boolean passesFilter = filterParticipantId(subscribedManifest.getOriginParticipant(), publishedManifest.getOriginParticipant());
        getLogger().debug(".originParticipantFilter(): Exit, passesFilter->{}", passesFilter);
        return(passesFilter);
    }

    protected boolean previousParticipantFiler(DataParcelManifest publishedManifest, TaskWorkItemSubscriptionType subscribedManifest) {
        getLogger().debug(".previousParticipantFiler(): Entry");
        if (publishedManifest == null || subscribedManifest == null) {
            getLogger().debug(".previousParticipantFiler(): Exit, either publishedManifest or subscribedManifest are null, returning -false-");
            return (false);
        }
        if (!subscribedManifest.hasPreviousParticipant() && !publishedManifest.hasPreviousParticipant()) {
            getLogger().debug(".previousParticipantFiler(): Neither subscribedManifest or publishedManifest have previousParticipants, returning true");
            return (true);
        }
        boolean passesFilter = filterParticipantId(subscribedManifest.getPreviousParticipant(), publishedManifest.getPreviousParticipant());
        getLogger().debug(".previousParticipantFiler(): Exit, passesFilter->{}", passesFilter);
        return(passesFilter);
    }

    protected boolean filterParticipantId(PetasosParticipantId filterCriteria, PetasosParticipantId testParticipantId){
        getLogger().debug(".filterParticipantId(): Entry, filterCriteria->{}, testParticipantId->{}", filterCriteria, testParticipantId);
        if(filterCriteria == null && testParticipantId == null){
            getLogger().debug(".filterParticipantId(): Exit, filterCriteria and testParticipantId are empty, returning -true-");
            return(false);
        }
        if(filterCriteria == null){
            getLogger().debug(".filterParticipantId(): Exit, filterCriteria is empty, returning -false-");
            return(false);
        }
        if(testParticipantId == null){
            getLogger().trace(".filterParticipantId(): testParticipantId is empty, checking filter");
            boolean namePasses = filterString(filterCriteria.getName(), null);
            boolean subsystemPasses = filterString(filterCriteria.getSubsystemName(), null);
            boolean versionPasses = filterString(filterCriteria.getVersion(), null);
            if(namePasses && subsystemPasses && versionPasses){
                getLogger().debug(".filterParticipantId(): Exit, testParticipantId is empty, filter has wildcards, returning -true-");
                return(true);
            } else {
                getLogger().debug(".filterParticipantId(): Exit, testParticipantId is empty, filter does not contain wildcards, returning -false-");
                return(false);
            }
        } else {
            getLogger().trace(".filterParticipantId(): testParticipantId is NOT empty, applying filter");
            boolean namePasses = filterString(filterCriteria.getName(), testParticipantId.getName());
            boolean subsystemPasses = filterString(filterCriteria.getSubsystemName(), testParticipantId.getSubsystemName());
            boolean versionPasses = filterString(filterCriteria.getVersion(), testParticipantId.getVersion());
            if(namePasses && subsystemPasses && versionPasses){
                getLogger().debug(".filterParticipantId(): Exit, filter->{}", true);
                return(true);
            } else {
                getLogger().debug(".filterParticipantId(): Exit, filter->{}", false);
                return(false);
            }
        }
    }

    protected boolean filterString(String filterCriteria, String testString){
        getLogger().debug(".filterString(): Entry, filterCriteria->{}, testString->{}", filterCriteria, testString);
        if(StringUtils.isEmpty(filterCriteria) && StringUtils.isEmpty(testString)){
            getLogger().debug(".filterString(): Exit, both filterCriteria and testString are empty, returning -true-");
            return(true);
        }
        if(StringUtils.isEmpty(filterCriteria)){
            getLogger().debug(".filterString(): Exit, filterCriteria only is empty, returning -false-");
            return(false);
        }
        if(filterCriteria.contentEquals(DataParcelManifest.WILDCARD_CHARACTER)){
            getLogger().debug(".filterString(): Exit, filterCriteria contains wildcard, returning -true-");
            return(true);
        }
        if(StringUtils.isEmpty(testString)){
            getLogger().debug(".filterString(): Exit, filterCriteria requires content, testString is empty, returning -false-");
            return(false);
        }
        if(filterCriteria.contentEquals(testString)){
            getLogger().debug(".filterString(): Exit, filterCriteria and testString contain same content, returning -true-");
            return(true);
        }
        getLogger().debug(".filterString(): Exit, default, returning -false-");
        return(false);
    }
}
