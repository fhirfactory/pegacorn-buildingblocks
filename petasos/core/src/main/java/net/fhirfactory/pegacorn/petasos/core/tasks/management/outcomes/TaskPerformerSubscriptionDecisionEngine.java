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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.outcomes;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelValidationStatusEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;
import net.fhirfactory.pegacorn.petasos.core.participants.cache.LocalParticipantRegistrationCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class TaskPerformerSubscriptionDecisionEngine {
    private static final Logger LOG = LoggerFactory.getLogger(TaskPerformerSubscriptionDecisionEngine.class);

    @Inject
    private LocalParticipantRegistrationCache localParticipantRegistrationCache;

    @Inject
    private ProcessingPlantInterface processingPlant;

    //
    // Constructor(s)
    //

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected LocalParticipantRegistrationCache getLocalParticipantRegistrationCache(){
        return(localParticipantRegistrationCache);
    }

    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlant);
    }

    //
    // More sophisticated SubscriberList derivation
    //

    public boolean hasRemoteServiceName(PetasosParticipantRegistration subscriber){
        getLogger().debug(".hasRemoteServiceName(): Entry, subscriber->{}", subscriber.getParticipantId().getSubsystemName());
        if(subscriber == null){
            return(false);
        }
        if(StringUtils.isEmpty(subscriber.getParticipantId().getSubsystemName())){
            return(false);
        }
        getLogger().debug(".hasRemoteServiceName(): Entry, processingPlant.getSubsystemParticipantName()->{}", processingPlant.getTopologyNode().getParticipant().getParticipantId().getSubsystemName());
        if(subscriber.getParticipantId().getSubsystemName().contentEquals(processingPlant.getTopologyNode().getParticipant().getParticipantId().getSubsystemName())){
            return(false);
        }
        return(true);
    }

    public boolean hasIntendedTarget(DataParcelManifest parcelManifest){
        if(parcelManifest == null){
            return(false);
        }
        if(StringUtils.isEmpty(parcelManifest.getIntendedTargetSystem())){
            return(false);
        }
        return(true);
    }

    public boolean hasAtLeastOneSubscriber(DataParcelManifest parcelManifest){
        if(parcelManifest == null){
            return(false);
        }
        if(deriveSubscriberList(parcelManifest).isEmpty()){
            return(false);
        }
        return(true);
    }

    public List<PetasosParticipantRegistration> deriveSubscriberList(DataParcelManifest parcelManifest){
        getLogger().debug(".deriveSubscriberList(): Entry, parcelManifest->{}", parcelManifest);
        if(getLogger().isDebugEnabled()){
            if(parcelManifest.hasContentDescriptor()){
                String messageToken = parcelManifest.getContentDescriptor().toFDN().getToken().toTag();
                getLogger().debug(".deriveSubscriberList(): parcel.ContentDescriptor->{}", messageToken);
            }
        }
        List<PetasosParticipantRegistration> subscriberList = new ArrayList<>();

        getLogger().trace(".deriveSubscriberList(): [Retrieve PetasosParticipant list] Start");
        Set<PetasosParticipantRegistration> participants = getLocalParticipantRegistrationCache().getAllParticipantRegistrations();
        getLogger().trace(".deriveSubscriberList(): [Retrieve PetasosParticipant list] Finish, list.size()->{}", participants.size());

        List<String> alreadySubscribedSubsystemParticipants = new ArrayList<>();

        getLogger().trace(".deriveSubscriberList(): [Process Participant List] Start");
        for(PetasosParticipantRegistration currentParticipant: participants) {
            if(getLogger().isTraceEnabled()) {
                getLogger().trace(".deriveSubscriberList(): [Process Participant List] processing participant->{}", currentParticipant.getParticipantId().getName());
            }
            boolean passFirstPhaseTest = false;
            if(isRemoteParticipant(currentParticipant)){
                if(alreadySubscribedSubsystemParticipants.contains(currentParticipant.getParticipantId().getSubsystemName())){
                    passFirstPhaseTest = false;
                } else {
                    passFirstPhaseTest = true;
                }
            } else {
                passFirstPhaseTest = true;
            }
            if (passFirstPhaseTest) {
                if(getLogger().isTraceEnabled()) {
                    getLogger().trace(".deriveSubscriberList(): [Process Participant List] Into 2ndPhase Test ->{}", currentParticipant.getParticipantId().getName());
                }
                for (TaskWorkItemSubscriptionType currentSubscription : currentParticipant.getSubscriptions()) {
                    if (applySubscriptionFilter(currentSubscription, parcelManifest)) {
                        if (!subscriberList.contains(currentParticipant)) {
                            subscriberList.add(currentParticipant);
                            getLogger().trace(".deriveSubscriberList(): [Process Participant List] Adding.... ");
                        }
                        if(StringUtils.isNotEmpty(currentParticipant.getParticipantId().getSubsystemName())) {
                            alreadySubscribedSubsystemParticipants.add(currentParticipant.getParticipantId().getSubsystemName());
                        }
                        break;
                    }
                }
            }
        }
        getLogger().trace(".deriveSubscriberList(): [Process Participant List] Finish");

        getLogger().debug(".deriveSubscriberList(): Exit!");
        return(subscriberList);
    }

    protected boolean isRemoteParticipant(PetasosParticipantRegistration participant){
        if(participant == null){
            return(false);
        }
        if(StringUtils.isEmpty(participant.getParticipantId().getSubsystemName())){
            return(false);
        }
        if(processingPlant.getTopologyNode().getParticipant().getParticipantId().getSubsystemName().contentEquals(participant.getParticipantId().getSubsystemName())){
            return(false);
        }
        return(true);
    }

    //
    // Filter Implementation
    //

    public boolean applySubscriptionFilter(TaskWorkItemSubscriptionType subscription, DataParcelManifest testManifest){
        getLogger().debug(".applySubscriptionFilter(): Entry, testManifest->{}", testManifest);

        boolean containerIsEqual = containerDescriptorIsEqual(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: containerIsEqual->{}",containerIsEqual);

        boolean contentIsEqual = contentDescriptorIsEqual(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: contentIsEqual->{}",contentIsEqual);

        boolean containerOnlyIsEqual = containerDescriptorOnlyEqual(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: containerOnlyIsEqual->{}",containerOnlyIsEqual);

        boolean matchedNormalisation = normalisationMatches(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: matchedNormalisation->{}",matchedNormalisation);

        boolean matchedValidation = validationMatches(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: matchedValidation->{}",matchedValidation);

        boolean matchedManifestType = manifestTypeMatches(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: matchedManifestType->{}",matchedManifestType);

        boolean matchedSource = sourceSystemMatches(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: matchedSource->{}",matchedSource);

        boolean matchedTarget = targetSystemMatches(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: matchedTarget->{}",matchedTarget);

        boolean matchedPEPStatus = enforcementPointApprovalStatusMatches(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: matchedPEPStatus->{}",matchedPEPStatus);

        boolean matchedDistributionStatus = isDistributableMatches(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: matchedDistributionStatus->{}",matchedDistributionStatus);

        boolean matchedDirection = parcelFlowDirectionMatches(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: matchedDirection->{}",matchedDirection);

        boolean goodEnoughMatch = containerIsEqual
                && contentIsEqual
                && matchedNormalisation
                && matchedValidation
                && matchedManifestType
                && matchedSource
                && matchedTarget
                && matchedPEPStatus
                && matchedDirection
                && matchedDistributionStatus;
        getLogger().trace(".filter(): Checking for equivalence/match: goodEnoughMatch->{}",goodEnoughMatch);

        boolean containerBasedOKMatch = containerOnlyIsEqual
                && matchedNormalisation
                && matchedValidation
                && matchedManifestType
                && matchedSource
                && matchedTarget
                && matchedPEPStatus
                && matchedDirection
                && matchedDistributionStatus;
        getLogger().trace(".filter(): Checking for equivalence/match: containerBasedOKMatch->{}",containerBasedOKMatch);

        boolean passesFilter = goodEnoughMatch || containerBasedOKMatch;
        getLogger().debug(".filter(): Exit, passesFilter->{}", passesFilter);
        return(passesFilter);
    }

    //
    // Helper Methods To Filter Implementation
    //

    private boolean containerDescriptorIsEqual(DataParcelManifest publisherManifest, TaskWorkItemSubscriptionType subscribedManifest){
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
        if(!testManifestHasContainerDescriptor ) {
            getLogger().debug(".containerIsEqual(): Exit, publisherManifest has no containerDescriptor, but subscribedManifest does, returning -false-");
            return(false);
        }
        getLogger().trace(".containerIsEqual(): publisherManifest and subscribedManifest both have containerDescriptors, now testing for equality");

        boolean containersAreEqual = publisherManifest.getContainerDescriptor().isEqualWithWildcardsInOther(subscribedManifest.getContainerDescriptor());

        getLogger().debug(".containerIsEqual(): Exit, publisherManifest and subscribedManifest containerDescriptor comparison yielded->{}", containersAreEqual);
        return(containersAreEqual);
    }

    private boolean contentDescriptorIsEqual(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest){
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

    private boolean containerDescriptorOnlyEqual(DataParcelManifest publisherManifest, TaskWorkItemSubscriptionType subscribedManifest){
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

    private boolean normalisationMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest){
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

    private boolean validationMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest) {
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

    private boolean manifestTypeMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest) {
        if (testManifest == null || subscribedManifest == null) {
            return (false);
        }
        boolean manifestTypeMatches = subscribedManifest.getDataParcelType().equals(testManifest.getDataParcelType());
        return(manifestTypeMatches);
    }

    private boolean sourceSystemMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest) {
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

    private boolean targetSystemMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest) {
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

    private boolean enforcementPointApprovalStatusMatches(DataParcelManifest publishedManifest, TaskWorkItemSubscriptionType subscribedManifest) {
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

    private boolean isDistributableMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest) {
        if (testManifest == null || subscribedManifest == null) {
            return (false);
        }
        return (testManifest.isInterSubsystemDistributable() == subscribedManifest.isInterSubsystemDistributable());
    }

    private boolean parcelFlowDirectionMatches(DataParcelManifest testManifest, TaskWorkItemSubscriptionType subscribedManifest){
        if(testManifest == null || subscribedManifest == null){
            return(false);
        }
        boolean directionMatches = testManifest.getDataParcelFlowDirection() == subscribedManifest.getDataParcelFlowDirection();
        return(directionMatches);
    }
}
