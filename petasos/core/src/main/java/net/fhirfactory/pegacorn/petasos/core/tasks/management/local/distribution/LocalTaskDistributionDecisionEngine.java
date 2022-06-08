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
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets.DataParcelTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes.DataParcelManifestSubscriptionMaskType;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes.DataParcelDescriptorSubscriptionMaskType;
import net.fhirfactory.pegacorn.petasos.core.participants.cache.LocalPetasosParticipantCacheDM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class LocalTaskDistributionDecisionEngine {
    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskDistributionDecisionEngine.class);

    @Inject
    private LocalPetasosParticipantCacheDM localPetasosParticipantCacheDM;

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

    protected LocalPetasosParticipantCacheDM getLocalPetasosParticipantCacheDM(){
        return(localPetasosParticipantCacheDM);
    }

    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlant);
    }

    //
    // More sophisticated SubscriberList derivation
    //

    public boolean hasRemoteServiceName(PetasosParticipant subscriber){
        getLogger().debug(".hasRemoteServiceName(): Entry, subscriber->{}", subscriber.getSubsystemParticipantName());
        if(subscriber == null){
            return(false);
        }
        if(StringUtils.isEmpty(subscriber.getSubsystemParticipantName())){
            return(false);
        }
        getLogger().debug(".hasRemoteServiceName(): Entry, processingPlant.getSubsystemParticipantName()->{}", processingPlant.getSubsystemParticipantName());
        if(subscriber.getSubsystemParticipantName().contentEquals(processingPlant.getSubsystemParticipantName())){
            return(false);
        }
        return(true);
    }

    public boolean hasIntendedTarget(DataParcelManifest parcelManifest){
        if(parcelManifest == null){
            return(false);
        }
        if(parcelManifest.hasDestination()) {
        	if(parcelManifest.getDestination().hasBoundaryPointProcessingPlantParticipantName()) {
		        if(StringUtils.isNotEmpty(parcelManifest.getDestination().getBoundaryPointProcessingPlantParticipantName().getName())){
		            return(true);
		        }
        	}
        }
        return(false);
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

    public List<PetasosParticipant> deriveSubscriberList(DataParcelManifest parcelManifest){
        getLogger().debug(".deriveSubscriberList(): Entry, parcelManifest->{}", parcelManifest);
        if(getLogger().isDebugEnabled()){
            if(parcelManifest.hasContentDescriptor()){
                String messageToken = parcelManifest.getContentDescriptor().toFDN().getToken().toTag();
                getLogger().debug(".deriveSubscriberList(): parcel.ContentDescriptor->{}", messageToken);
            }
        }
        List<PetasosParticipant> subscriberList = new ArrayList<>();

        Set<PetasosParticipant> participants = getLocalPetasosParticipantCacheDM().getAllPetasosParticipants();

        List<String> alreadySubscribedSubsystemParticipants = new ArrayList<>();

        for(PetasosParticipant currentParticipant: participants) {
            boolean passFirstPhaseTest = false;
            if(isRemoteParticipant(currentParticipant)){
                if(alreadySubscribedSubsystemParticipants.contains(currentParticipant.getSubsystemParticipantName())){
                    passFirstPhaseTest = false;
                } else {
                    passFirstPhaseTest = true;
                }
            } else {
                passFirstPhaseTest = true;
            }
            if (passFirstPhaseTest) {
                getLogger().debug(".deriveSubscriberList(): Processing participant->{}/{}", currentParticipant.getParticipantName(), currentParticipant.getSubsystemParticipantName());
                 for (DataParcelManifestSubscriptionMaskType currentSubscription : currentParticipant.getSubscriptions()) {
                    if (applySubscriptionFilter(currentSubscription, parcelManifest)) {
                        if (!subscriberList.contains(currentParticipant)) {
                            subscriberList.add(currentParticipant);
                            getLogger().debug(".deriveSubscriberList(): Adding.... ");
                        }
                        if(StringUtils.isNotEmpty(currentParticipant.getSubsystemParticipantName())) {
                            alreadySubscribedSubsystemParticipants.add(currentParticipant.getSubsystemParticipantName());
                        }
                        break;
                    }
                }
            }
        }

        getLogger().debug(".getSubscriberList(): Exit!");
        return(subscriberList);
    }

    protected boolean isRemoteParticipant(PetasosParticipant participant){
        if(participant == null){
            return(false);
        }
        if(StringUtils.isEmpty(participant.getSubsystemParticipantName())){
            return(false);
        }
        if(processingPlant.getSubsystemParticipantName().contentEquals(participant.getSubsystemParticipantName())){
            return(false);
        }
        return(true);
    }

    //
    // Filter Implementation
    //

    public boolean applySubscriptionFilter(DataParcelManifestSubscriptionMaskType subscription, DataParcelManifest testManifest){
        getLogger().info(".applySubscriptionFilter(): Entry, testManifest->{}", testManifest);

        boolean containerIsEqual = containerDescriptorIsEqual(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: containerIsEqual->{}",containerIsEqual);

        boolean contentIsEqual = contentDescriptorIsEqual(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: contentIsEqual->{}",contentIsEqual);

        boolean containerOnlyIsEqual = containerDescriptorOnlyEqual(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: containerOnlyIsEqual->{}",containerOnlyIsEqual);

        boolean dataQualityMaskPasses = false;
        if(subscription.hasContentQualityMask()) {
            dataQualityMaskPasses = subscription.getContentQualityMask().applyMask(testManifest.getContentQuality());
        } else {
            if(testManifest.hasContentQuality()){
                dataQualityMaskPasses = false;
            }
        }
        getLogger().info(".applySubscriptionFilter(): Checking for equivalence/match: matchedDataQuality->{}",dataQualityMaskPasses);


        boolean matchedManifestType = manifestTypeMatches(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: matchedManifestType->{}",matchedManifestType);

        boolean originMatches = originMatches(testManifest, subscription);
        getLogger().trace(".applySubscriptionFilter(): Checking for equivalence/match: originMatches->{}",originMatches);

        boolean destinationMatches = destinationMatches(testManifest, subscription);
        getLogger().info(".applySubscriptionFilter(): Checking for equivalence/match: destinationMatches->{}",destinationMatches);

        boolean goodEnoughMatch = containerIsEqual
                && contentIsEqual
                && dataQualityMaskPasses
                && matchedManifestType
                && originMatches
                && destinationMatches;
        getLogger().info(".filter(): Checking for equivalence/match: goodEnoughMatch->{}",goodEnoughMatch);

        boolean containerBasedOKMatch = containerOnlyIsEqual
                && dataQualityMaskPasses
                && matchedManifestType
                && originMatches
                && destinationMatches;
        getLogger().info(".filter(): Checking for equivalence/match: containerBasedOKMatch->{}",containerBasedOKMatch);

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
        boolean subscribedManifestHasContainerDescriptor = subscribedManifest.hasContainerDescriptorMask();
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

        boolean containersAreEqual = subscribedManifest.getContainerDescriptorMask().applyMask(publisherManifest.getContainerDescriptor());

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
        boolean subscribedManifestHasContentDescriptor = subscribedManifest.hasContentDescriptorMask();
        if(!testManifestHasContentDescriptor ) {
            getLogger().debug(".contentIsEqual(): Exit, testManifest has not contentDescriptor, returning -false-");
            return(false);
        }
        if(!subscribedManifestHasContentDescriptor){
            getLogger().debug(".contentIsEqual(): Exit, subscribedManifest has not contentDescriptor, returning -false-");
            return(false);
        }

        DataParcelTypeDescriptor testDescriptor = testManifest.getContentDescriptor();
        DataParcelDescriptorSubscriptionMaskType subscribedDescriptor = subscribedManifest.getContentDescriptorMask();

        boolean equalWithWildcardsInOther = subscribedDescriptor.applyMask(testDescriptor);

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
        if(subscribedManifest.hasContainerDescriptorMask() && subscribedManifest.hasContentDescriptorMask()){
            getLogger().trace(".containerOnlyEqual(): subscribedManifest has both contentDescriptor && containerDescriptor, checking to see if they are the same");
            if(!subscribedManifest.getContainerDescriptorMask().applyMask(publisherManifest.getContentDescriptor())){
                getLogger().debug(".containerOnlyEqual(): contentDescriptor && containerDescriptor are different, so subscription subscriberManifest is after specific content, returning -false-");
                return(false);
            }
        }
        getLogger().trace(".containerOnlyEqual(): subscribedManifest does not have a ContentDescriber!, checking comparisons of the container only");
        if(publisherManifest.hasContainerDescriptor() && subscribedManifest.hasContainerDescriptorMask()){
            getLogger().trace(".containerOnlyEqual(): publisherManifest cotnains a ContainerDescriptor, so comparing");
            boolean containerIsEqual = containerDescriptorIsEqual(publisherManifest, subscribedManifest);
            getLogger().debug(".containerOnlyEqual(): Comparison of ContainerContent is ->{}, returning it!", containerIsEqual);
            return(containerIsEqual);
        }
        getLogger().debug(".containerOnlyEqual(): Publisher does not have a ContainerDescriptor, returning -false-");
        return(false);
    }



    private boolean manifestTypeMatches(DataParcelManifest testManifest, DataParcelManifestSubscriptionMaskType subscribedManifest) {
        if (testManifest == null || subscribedManifest == null) {
            return (false);
        }
        switch(subscribedManifest.getDataParcelTypeMask()) {
	        case PARCEL_TYPE_ANY:
	        	return(true);
	        case IPC_DATA_PARCEL_TYPE:
	        	if(testManifest.getDataParcelType().equals( DataParcelTypeEnum.IPC_DATA_PARCEL_TYPE)){
	        		return(true);
	        	}
	        	break;
	        case SEARCH_QUERY_DATA_PARCEL_TYPE:
	        	if(testManifest.getDataParcelType().equals( DataParcelTypeEnum.SEARCH_QUERY_DATA_PARCEL_TYPE)){
	        		return(true);
	        	}
	        	break;
	        case SEARCH_RESULT_DATA_PARCEL_TYPE:
	        	if(testManifest.getDataParcelType().equals( DataParcelTypeEnum.SEARCH_RESULT_DATA_PARCEL_TYPE)){
	        		return(true);
	        	}
	        	break;
	        case GENERAL_DATA_PARCEL_TYPE:
	        	if(testManifest.getDataParcelType().equals( DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE)){
	        		return(true);
	        	}
	        	break;
        }
        return(false);
    }

    private boolean destinationMatches(DataParcelManifest testManifest, DataParcelManifestSubscriptionMaskType subscribedManifest) {
        if (testManifest == null && subscribedManifest == null) {
            return (false);
        }
        if (testManifest == null || subscribedManifest == null) {
            return (false);
        }
        if(subscribedManifest.hasDestinationMask()) {
        	boolean passes = subscribedManifest.getDestinationMask().applyMask(testManifest.getDestination());
        	return(passes);
        } 
    	if(testManifest.hasDestination()) {
    		return(false);
    	} else {
    		return(true);
    	}
    }

    private boolean originMatches(DataParcelManifest testManifest, DataParcelManifestSubscriptionMaskType subscribedManifest) {
        if (testManifest == null && subscribedManifest == null) {
            return (false);
        }
        if (testManifest == null || subscribedManifest == null) {
            return (false);
        }
        if(subscribedManifest.hasOriginMask()) {
        	boolean passes = subscribedManifest.getOriginMask().applyMask(testManifest.getOrigin());
        	return(passes);
        } 
    	if(testManifest.hasOrigin()) {
    		return(false);
    	} else {
    		return(true);
    	}
    }
}
