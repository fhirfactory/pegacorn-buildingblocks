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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.local.distribution;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.DataParcelNormalisationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.DataParcelValidationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.TaskWorkItemSubscriptionRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes.DataParcelManifestSubscriptionMaskType;
import net.fhirfactory.pegacorn.petasos.core.participants.cache.LocalPetasosParticipantSubscriptionMapDM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class LocalTaskDistributionDecisionEngineOld {
    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskDistributionDecisionEngineOld.class);

    @Inject
    private LocalPetasosParticipantSubscriptionMapDM localSubscriptionMapDM;

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

    protected LocalPetasosParticipantSubscriptionMapDM getLocalSubscriptionMapDM(){
        return(localSubscriptionMapDM);
    }

    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlant);
    }

    //
    // More sophisticated SubscriberList derivation
    //

    public List<PetasosParticipant> getSubscriberSet(DataParcelManifest parcelManifest){
        getLogger().debug(".getSubscriberSet(): Entry, parcelManifest->{}", parcelManifest);

        List<PetasosParticipant> subscriberSet = deriveSubscriberList(parcelManifest);
        getLogger().trace(".getSubscriberSet(): Before we do a general routing attempt, let's see if the message is directed somewhere specific");
        //
        // Because auditing is not running yet
        // Remove once Auditing is in place
        //
        if(getLogger().isInfoEnabled()) {
            int subscriberSetSize = 0;
            if (subscriberSet != null) {
                subscriberSetSize = subscriberSet.size();
            }
            if(getLogger().isInfoEnabled()) {
                StringBuilder builder = new StringBuilder();
                builder.append(".getSubscriberSet(): Number of Subscribers->"+subscriberSetSize);
                for(PetasosParticipant currentParticipant: subscriberSet) {
                    builder.append(", participant->"+currentParticipant.getParticipantName());
                }
                getLogger().warn(builder.toString());
            }
        }
        if(subscriberSet == null){
            getLogger().debug(".subscriberSet(): Exit, number of subscribers is 0");
            return(subscriberSet);
        }
        if(subscriberSet.isEmpty()){
            getLogger().debug(".subscriberSet(): Exit, number of subscribers is 0");
            return(subscriberSet);
        }
        //
        // Check to see if the message has a Target defined in the ParcelManifest: This ONLY works if the target
        // system has actually registered interest in getting the message anyhow, so a bit redundant from that
        // sense. It is mainly used a mechanism for restricting OTHER subscribers from getting the message.
        //
        if(hasIntendedTarget(parcelManifest)){
            getLogger().trace(".getSubscriberSet(): It's not empty, so let's see if the appropriate downstream system is registered");
            for(PetasosParticipant currentSubscriber: subscriberSet){
                if(hasRemoteServiceName(currentSubscriber)) {
                    String subscriberName = currentSubscriber.getSubsystemParticipantName();
                    if (subscriberName.contentEquals(parcelManifest.getIntendedTargetSystem())) {
                        subscriberSet.add(currentSubscriber);
                    }
                }
            }
        }
        //
        // TODO Need to add ability to look up remote participant for parcels that explicitly define an intended target
        // that isn't in the system
        //

        getLogger().trace(".getSubscriberSet(): Iterate through the subscribers");
        if(getLogger().isDebugEnabled()){
            getLogger().debug(".getSubscriberSet(): number of subscribers to this UoW->{}", subscriberSet.size());
        }

        if (subscriberSet != null) {
            getLogger().trace(".getSubscriberSet(): Iterating through....");
            for (PetasosParticipant currentSubscriber : subscriberSet) {
                getLogger().trace(".distributeNewFulfillmentTasks(): Iterating, currentSubscriber->{}", currentSubscriber);
                if (!subscriberSet.contains(currentSubscriber)) {
                    subscriberSet.add(currentSubscriber);
                }
            }
        }
        getLogger().debug(".getSubscriberSet(): Exit, number of subscribers data parcel->{}", subscriberSet.size());
        return(subscriberSet);
    }

    public boolean hasRemoteServiceName(PetasosParticipant subscriber){
        if(subscriber == null){
            return(false);
        }
        if(subscriber.getSubsystemParticipantName() == null){
            return(false);
        }
        if(subscriber.getSubsystemParticipantName().equals(processingPlant.getSubsystemParticipantName())){
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
        if(getSubscriberSet(parcelManifest).isEmpty()){
            return(false);
        }
        return(true);
    }

    public List<PetasosParticipant> deriveSubscriberList(DataParcelManifest parcelManifest){
        getLogger().debug(".deriveSubscriberList(): Entry, parcelManifest->{}", parcelManifest);
        if(getLogger().isDebugEnabled()){
            if(parcelManifest.hasContentDescriptor()){
                String messageToken = parcelManifest.getContentDescriptor().toFDN().getToken().toTag();
                getLogger().debug(".deriveSubscriberList(): parcel.ContentDescriptor->{}", messageToken);
            }
        }
        DataParcelTypeDescriptor parcelContentDescriptor = parcelManifest.getContentDescriptor();
        DataParcelTypeDescriptor parcelContainerDescriptor = parcelManifest.getContainerDescriptor();
        List<TaskWorkItemSubscriptionRegistration> contentBasedSubscriberList = new ArrayList<>();
        List<TaskWorkItemSubscriptionRegistration> containerBasedSubscriptionList = new ArrayList<>();
        if(parcelContentDescriptor != null ){
            getLogger().trace(".deriveSubscriberList(): parcelContentDescriptor is not null");
            contentBasedSubscriberList.addAll(getLocalSubscriptionMapDM().getPossibleSubscriptionMatches(parcelContentDescriptor));
            getLogger().trace(".deriveSubscriberList(): contentBasedSubscriberList->{}", contentBasedSubscriberList);
        }
        if(parcelContainerDescriptor != null){
            getLogger().trace(".deriveSubscriberList(): parcelContainerDescriptor is not null");
            containerBasedSubscriptionList.addAll(getLocalSubscriptionMapDM().getPossibleSubscriptionMatches(parcelContainerDescriptor));
            getLogger().trace(".deriveSubscriberList(): containerBasedSubscriptionList->{}", containerBasedSubscriptionList);
        }

        boolean contentListIsEmpty = contentBasedSubscriberList.isEmpty();
        boolean containerListEmpty = containerBasedSubscriptionList.isEmpty();

        if(contentListIsEmpty && containerListEmpty) {
            getLogger().debug(".deriveSubscriberList(): Couldn't find any associated PubSubSubscriber elements [empty lists or nulls] (i.e. couldn't find any interested WUPs), returning an empty set");
            return (new ArrayList<>());
        }
        List<TaskWorkItemSubscriptionRegistration> retrievedSubscriberList = new ArrayList<>();
        if(!contentListIsEmpty){
            getLogger().trace(".deriveSubscriberList(): contentBasedSubscriberList contains something");
            retrievedSubscriberList.addAll(contentBasedSubscriberList);
        }
        if(!containerListEmpty){
            getLogger().trace(".deriveSubscriberList(): containerBasedSubscriptionList contains something");
            retrievedSubscriberList.addAll(containerBasedSubscriptionList);
        }
        if(retrievedSubscriberList.isEmpty()){
            getLogger().debug(".deriveSubscriberList(): Couldn't find any associated PubSubSubscriber elements [empty aggregate list] (i.e. couldn't find any interested WUPs), returning an empty set");
            return (new ArrayList<>());
        }
        List<PetasosParticipant> derivedSubscriberList = new ArrayList<>();
        for(TaskWorkItemSubscriptionRegistration currentRegisteredSubscription: retrievedSubscriberList){
            boolean passesFilter = applySubscriptionFilter(currentRegisteredSubscription.getWorkItemSubscription(), parcelManifest);
            if(passesFilter){
                if(getLogger().isDebugEnabled()) {
                    ComponentIdType subscriber = currentRegisteredSubscription.getParticipant().getComponentID();
                    getLogger().debug(".deriveSubscriberList(): Adding Subscriber->{}", subscriber);
                }
                derivedSubscriberList.add(currentRegisteredSubscription.getParticipant());
            }
        }
        getLogger().debug(".getSubscriberList(): Exit!");
        return(derivedSubscriberList);
    }

    //
    // Filter Implementation
    //

    public boolean applySubscriptionFilter(DataParcelManifestSubscriptionMaskType subscription, DataParcelManifest testManifest){
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

    private boolean containerDescriptorIsEqual(DataParcelManifest publisherManifest, DataParcelManifestSubscriptionMaskType subscribedManifest){
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

    private boolean contentDescriptorIsEqual(DataParcelManifest testManifest, DataParcelManifestSubscriptionMaskType subscribedManifest){
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

    private boolean containerDescriptorOnlyEqual(DataParcelManifest publisherManifest, DataParcelManifestSubscriptionMaskType subscribedManifest){
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

    private boolean normalisationMatches(DataParcelManifest testManifest, DataParcelManifestSubscriptionMaskType subscribedManifest){
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

    private boolean validationMatches(DataParcelManifest testManifest, DataParcelManifestSubscriptionMaskType subscribedManifest) {
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

    private boolean manifestTypeMatches(DataParcelManifest testManifest, DataParcelManifestSubscriptionMaskType subscribedManifest) {
        if (testManifest == null || subscribedManifest == null) {
            return (false);
        }
        boolean manifestTypeMatches = subscribedManifest.getDataParcelType().equals(testManifest.getDataParcelType());
        return(manifestTypeMatches);
    }

    private boolean sourceSystemMatches(DataParcelManifest testManifest, DataParcelManifestSubscriptionMaskType subscribedManifest) {
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

    private boolean targetSystemMatches(DataParcelManifest testManifest, DataParcelManifestSubscriptionMaskType subscribedManifest) {
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

    private boolean enforcementPointApprovalStatusMatches(DataParcelManifest publishedManifest, DataParcelManifestSubscriptionMaskType subscribedManifest) {
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

    private boolean isDistributableMatches(DataParcelManifest testManifest, DataParcelManifestSubscriptionMaskType subscribedManifest) {
        if (testManifest == null || subscribedManifest == null) {
            return (false);
        }
        return (testManifest.isInterSubsystemDistributable() == subscribedManifest.isInterSubsystemDistributable());
    }

    private boolean parcelFlowDirectionMatches(DataParcelManifest testManifest, DataParcelManifestSubscriptionMaskType subscribedManifest){
        if(testManifest == null || subscribedManifest == null){
            return(false);
        }
        boolean directionMatches = testManifest.getDataParcelFlowDirection() == subscribedManifest.getDataParcelFlowDirection();
        return(directionMatches);
    }
}
