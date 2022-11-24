/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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

package net.fhirfactory.pegacorn.petasos.participants.manager;

import net.fhirfactory.pegacorn.core.interfaces.pathway.TaskPathwayManagementServiceInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentExecutionControlEnum;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentStatusEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.participant.*;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemManifestType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;
import net.fhirfactory.pegacorn.petasos.participants.cache.LocalPetasosParticipantCacheDM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class LocalParticipantManager {
	private static final Logger LOG = LoggerFactory.getLogger(LocalParticipantManager.class);

	@Inject
	private LocalPetasosParticipantCacheDM participantCacheDM;

	@Inject
	private TaskPathwayManagementServiceInterface taskPathwayManagementService;

	@Inject
	private ProcessingPlantInterface myProcessingPlant;

	@Inject
	private ProcessingPlantPetasosParticipantHolder participantHolder;

	@Inject
	private ProcessingPlantPetasosParticipantNameHolder participantNameHolder;


	//
	// Constructor(s)
 	//

    public LocalParticipantManager(){
    }

	//
	// Business Methods
	//

	public PetasosParticipantRegistration getLocalParticipantRegistration(String participantName){
    	getLogger().debug(".getLocalParticipantRegistration(): Entry, participantName->{}", participantName);
		PetasosParticipantRegistration registration = participantCacheDM.getPetasosParticipantRegistration(participantName);
		getLogger().debug(".getLocalParticipantRegistration(): Exit, registration->{}", registration);
		return(registration);
	}

	@Deprecated
	public PetasosParticipantRegistration registerPetasosParticipant(String preferredParticipantName, SoftwareComponent participantSoftwareComponent, Set<TaskWorkItemManifestType> publishedTopics, Set<TaskWorkItemManifestType> subscribedTopics) {
		getLogger().debug(".registerPetasosParticipant(): Entry, preferredParticipantName->{}, participantSoftwareComponent->{}", preferredParticipantName, participantSoftwareComponent);
		//
		// 1st, some basic defensive programming and parameter checking
		if (participantSoftwareComponent == null || publishedTopics == null || subscribedTopics == null) {
			getLogger().debug(".registerPetasosParticipant(): Exit, publisherId, publishedTopics or subscribedTopics is null, not registering anything");
			return (null);
		}
		if (StringUtils.isNotEmpty(preferredParticipantName)) {
			participantSoftwareComponent.setParticipantName(preferredParticipantName);
		}
		PetasosParticipantRegistration petasosParticipantRegistration = registerPetasosParticipant(participantSoftwareComponent, publishedTopics, subscribedTopics);
		getLogger().debug(".registerPetasosParticipant(): Exit, registeredParticipant->{}", petasosParticipantRegistration);
		return(petasosParticipantRegistration);
	}


	public PetasosParticipantRegistration registerPetasosParticipant(SoftwareComponent participantSoftwareComponent, Set<TaskWorkItemManifestType> publishedTopics, Set<TaskWorkItemManifestType> subscribedTopics) {
		getLogger().debug(".registerPetasosParticipant(): Entry, participantSoftwareComponent->{}", participantSoftwareComponent);
		if (participantSoftwareComponent == null || publishedTopics == null) {
			getLogger().debug(".registerPetasosParticipant(): Exit, publisherId or publishedTopics is null, not registering anything");
			return (null);
		}
		PetasosParticipant participant = new PetasosParticipant(participantSoftwareComponent);
		if (!publishedTopics.isEmpty()) {
			getLogger().trace(".registerPetasosParticipant(): Has published topics");
			for (TaskWorkItemManifestType currentParcelManifest : publishedTopics) {
				participant.getPublishedWorkItemManifests().add(currentParcelManifest);
			}
		}
		if(!subscribedTopics.isEmpty()) {
			getLogger().trace(".registerPetasosParticipant(): Has topics to subscribe to!");
			for (TaskWorkItemManifestType currentParcelManifest : subscribedTopics) {
				TaskWorkItemSubscriptionType currentFilter = new TaskWorkItemSubscriptionType(currentParcelManifest);
				participant.getSubscriptions().add(currentFilter);
			}
		}
		PetasosParticipantRegistration registeredParticipant = registerPetasosParticipant(participant);
		getLogger().debug(".registerPetasosParticipant(): Exit, registeredParticipant->{}", registeredParticipant);
		return(registeredParticipant);
	}


	public PetasosParticipantRegistration registerPetasosParticipant(PetasosParticipant participant){
		getLogger().debug(".registerPetasosParticipant(): Entry, participant->{}", participant);
		if(participant == null ){
			getLogger().debug(".registerPetasosParticipant(): Exit, publisherId or publishedTopics is null, not registering anything");
			return(null);
		}

		//
		// Register with local DM
		getLogger().trace(".registerPetasosParticipant(): [Register Participant into the DM] Start");
		PetasosParticipantRegistration registration = getParticipantCacheDM().addPetasosParticipant(participant);
		getLogger().trace(".registerPetasosParticipant(): [Register Participant into the DM] Finish");

		//
		// Now update local subscription map
		getLogger().trace(".registerPetasosParticipant(): [Update local Subscription Map] Start");
		PetasosParticipant registeredParticipant = registration.getParticipant();
		if(!registeredParticipant.getSubscriptions().isEmpty()){
			getLogger().trace(".registerPetasosParticipant(): [Update local Subscription Map] Has subscription requirements");
			getLogger().trace(".registerPetasosParticipant(): [Update local Subscription Map] Subscribing to Topics");
			for (TaskWorkItemSubscriptionType currentSubscribedManifest : registeredParticipant.getSubscriptions()) {
				getLogger().trace(".registerPetasosParticipant(): [Update local Subscription Map] adding topic->{}", currentSubscribedManifest);
				boolean doSubscribe = true;
				if(currentSubscribedManifest.hasOriginParticipant()) {
					boolean sourceTaskProducerIsMe = currentSubscribedManifest.getOriginParticipant().equals(participantNameHolder.getSubsystemParticipantName());
					boolean sourceSystemIsWildcard = currentSubscribedManifest.getExternalSourceSystem().equals(DataParcelManifest.WILDCARD_CHARACTER);
					if(sourceTaskProducerIsMe || sourceSystemIsWildcard){
						doSubscribe = true;
					} else {
						doSubscribe = false;
					}
				} else {
					doSubscribe = true;
				}
			}
		}
		getLogger().trace(".registerPetasosParticipant(): [Update local Subscription Map] Finish");
		//
		// Our work is done
		getLogger().debug(".registerPetasosParticipant(): Exit, registration->{}", registration);
		return(registration);
	}

	/**
	 *
	 * @return
	 */
	public Set<PetasosParticipant> getDownstreamParticipants(){
		Set<PetasosParticipant> downstreamParticipantSet = getParticipantCacheDM().getDownstreamParticipantSet();
		return(downstreamParticipantSet);
	}

	/**
	 *
	 * @param participantComponent
	 * @param workItemSet
	 */
	public void updateProducedWorkItems(SoftwareComponent participantComponent, Set<TaskWorkItemManifestType> workItemSet){
		getLogger().debug(".updateProducedWorkItems(): Entry, participantComponent->{}", participantComponent);
		if(participantComponent == null || workItemSet == null){
			getLogger().debug(".updateProducedWorkItems(): Exit, participantComponent or publishedTopics is null, not registering anything");
			return;
		}
		if(workItemSet.isEmpty()){
			getLogger().debug(".updateProducedWorkItems(): Exit, publishedTopics is empty, not registering anything");
			return;
		}
		PetasosParticipant cachedPetasosParticipant = getParticipantCacheDM().getPetasosParticipant(participantComponent.getParticipantName());
		for(TaskWorkItemManifestType additionalWorkItem: workItemSet) {
			boolean alreadyInSet = false;
			for (TaskWorkItemManifestType currentWorkItem : cachedPetasosParticipant.getPublishedWorkItemManifests()){
				if(currentWorkItem.equals(additionalWorkItem)){
					alreadyInSet = true;
					break;
				}
			}
			if(!alreadyInSet){
				cachedPetasosParticipant.getPublishedWorkItemManifests().add(additionalWorkItem);
			}
		}
		PetasosParticipantRegistration registration = getTaskPathwayManagementService().updatePetasosParticipant(cachedPetasosParticipant);
		getParticipantCacheDM().updatePetasosParticipant(registration.getParticipant());
		if(registration.getParticipant().getComponentID().equals(myProcessingPlant.getMeAsASoftwareComponent().getComponentID())){
			participantHolder.setMyProcessingPlantPetasosParticipant(registration.getParticipant());
		}
		getLogger().debug(".updateProducedWorkItems(): Exit");
	}

	/**
	 *
	 * @param participant
	 */
	public PetasosParticipantRegistration updatePetasosParticipant(PetasosParticipant participant){
		getLogger().debug(".updatePetasosParticipant(): Entry, participant->{}", participant);
		if(participant == null ){
			getLogger().debug(".updatePetasosParticipant(): Exit, participant is null, not updating anything");
			return(null);
		}
		//
		// Update with Local Participant Cache
		getParticipantCacheDM().updatePetasosParticipant(participant);
		PetasosParticipantRegistration registration = getParticipantCacheDM().getPetasosParticipantRegistration(participant.getParticipantName());
		getLogger().debug(".updatePetasosParticipant(): Exit, registration->{}", registration);
		return(registration);
	}

	/**
	 *
	 * @param participantName
	 */
	public void synchroniseLocalWithCentralCache(String participantName){
		getLogger().debug(".synchroniseLocalWithCentralCache(): Entry, participantName->{}", participantName);
		if(StringUtils.isEmpty(participantName)){
			getLogger().debug(".synchroniseLocalWithCentralCache(): Exit, participantId is null, not doing anything");
			return;
		}
		//
		// Get Registration from Global Pathway Service Manager
		PetasosParticipantRegistration registration = getTaskPathwayManagementService().getPetasosParticipantRegistration(participantName);
		if(registration != null) {
			synchroniseLocalWithCentralCacheDetail(registration.getParticipant());
		}
		if(registration.getParticipant().getComponentID().equals(myProcessingPlant.getMeAsASoftwareComponent().getComponentID())){
			participantHolder.setMyProcessingPlantPetasosParticipant(registration.getParticipant());
		}
		getLogger().debug(".synchroniseLocalWithCentralCache(): Exit");
	}

	public void synchroniseLocalWithCentralCacheDetail(PetasosParticipant participant){
		getLogger().debug(".synchroniseLocalWithCentralCacheDetail(): Entry, participant->{}", participant);
		if(participant != null) {
			//
			// Update local DM with Gobal details
			getLogger().trace(".synchroniseLocalWithCentralCacheDetail(): [Synchronise Local Cache] Start");
			getParticipantCacheDM().updatePetasosParticipant(participant);
			getLogger().trace(".synchroniseLocalWithCentralCacheDetail(): [Synchronise Local Cache] Finish");
		}
		getLogger().debug(".synchroniseLocalWithCentralCacheDetail(): Exit");
	}

	public void synchroniseLocalWithCentralCacheDetail(PetasosParticipantRegistration registration){
		getLogger().debug(".synchroniseLocalWithCentralCacheDetail(): Entry, registration->{}", registration);
		if(registration != null) {
			//
			// Update local DM with Gobal details
			getParticipantCacheDM().updatePetasosParticipantRegistration(registration);
			//
			// Now update local subscription map
			PetasosParticipant participant = registration.getParticipant();
			synchroniseLocalWithCentralCacheDetail(participant);
		}
		getLogger().debug(".synchroniseLocalWithCentralCacheDetail(): Exit");
	}

	public Set<PetasosParticipant> getLocalParticipantSet(){
		getLogger().debug(".getLocalParticipantSet(): Entry");

		Set<PetasosParticipant> localParticipants = new HashSet<>();
		Set<PetasosParticipant> participants = getParticipantCacheDM().getAllPetasosParticipants();

		for(PetasosParticipant currentParticipant: participants){
			if(StringUtils.isEmpty(currentParticipant.getSubsystemParticipantName()) || currentParticipant.getSubsystemParticipantName().contentEquals(myProcessingPlant.getSubsystemParticipantName())){
				localParticipants.add(currentParticipant);
			}
		}

		getLogger().debug(".getLocalParticipantSet(): Exit");
		return(localParticipants);
	}

	public PetasosParticipantRegistrationSet getParticipantRegistrationSet(){
		PetasosParticipantRegistrationSet participantRegistrationSet = getParticipantCacheDM().getParticipantRegistrationSet();
		return(participantRegistrationSet);
	}

	/**
	 *
	 * @param participantName
	 * @return
	 */
	public PetasosParticipant getPetasosParticipant(String participantName){
		getLogger().debug(".getPetasosParticipant(): Entry, participantName->{}", participantName);
		if(participantName == null){
			getLogger().debug(".getPetasosParticipant(): Exit, participantName is null, not doing anything");
			return(null);
		}
		PetasosParticipant petasosParticipant = getParticipantCacheDM().getPetasosParticipant(participantName);
		getLogger().debug(".getPetasosParticipant(): Exit, petasosParticipant->{}", petasosParticipant);
		return(petasosParticipant);
	}

	/**
	 *
	 * @param participantName
	 * @return
	 */
	public Set<DataParcelManifest> getPublishedParcels(String participantName) {
		getLogger().debug(".getPublishedParcels(): Entry, participantName->{}", participantName);
		if (participantName == null) {
			getLogger().debug(".getPublishedParcels(): Exit, participantName is null, returning empty set");
			return (new HashSet<>());
		}
		PetasosParticipant petasosParticipant = getPetasosParticipant(participantName);
		Set<DataParcelManifest> publishedWorkItems = new HashSet<>();
		if (petasosParticipant != null) {
			for (TaskWorkItemManifestType currentItemManifest : petasosParticipant.getPublishedWorkItemManifests()) {
				publishedWorkItems.add(currentItemManifest);
			}
		}
		getLogger().debug(".getPublishedParcels(): Exit");
		return(publishedWorkItems);
	}

	/**
	 *
	 * @param participantName
	 * @return
	 */
	public Set<TaskWorkItemManifestType> getProducedWorkItemManifests(String participantName){
		getLogger().debug(".getPublishedParcels(): Entry, participantName->{}", participantName);
		if(participantName == null){
			getLogger().debug(".getPublishedParcels(): Exit, participantName is null, returning empty set");
			return(new HashSet<>());
		}
		PetasosParticipant petasosParticipant = getPetasosParticipant(participantName);
		Set<TaskWorkItemManifestType> publishedWorkItems = new HashSet<>();
		if(petasosParticipant != null) {
			publishedWorkItems.addAll(petasosParticipant.getPublishedWorkItemManifests());
		}
		getLogger().debug(".getPublishedParcels(): Exit");
		return(publishedWorkItems);
	}

	public Set<PetasosParticipant> getParticipantSetForSubsystem(String serviceName) {
		getLogger().debug(".getParticipantSetForSerivce(): Entry, serviceName->{}", serviceName);
		if(StringUtils.isEmpty(serviceName)) {
			getLogger().debug(".getParticipantSetForSerivce(): Exit, serviceName is null, returning an empty set");
			return (new HashSet<>());
		}
		Set<PetasosParticipant> participantSet = getParticipantCacheDM().getParticipantSetForSerivce(serviceName);
		if(getLogger().isDebugEnabled()) {
			getLogger().debug(".getParticipantSetForSerivce(): Exit, participantSet size->{}", participantSet.size());
		}
		return(participantSet);
	}


	public void removePetasosParticipantFromLocalCache(PetasosParticipant removedPetasosParticipant){
		if(removedPetasosParticipant != null){
			boolean noCandidateTargetEndpointForService = false;
			Set<PetasosParticipant> myDownstreamPerformerSets = getDownstreamParticipants();
			if(!myDownstreamPerformerSets.isEmpty()){
				for(PetasosParticipant currentParticipant: myDownstreamPerformerSets){
					if(currentParticipant.getComponentID().equals(removedPetasosParticipant.getComponentID())){
						Set<PetasosParticipant> serviceParticipantSet = getParticipantSetForSubsystem(currentParticipant.getSubsystemParticipantName());
						if(serviceParticipantSet.isEmpty()) {
							noCandidateTargetEndpointForService = true;
						} else {
							noCandidateTargetEndpointForService = true;
							for(PetasosParticipant currentServiceParticipant: serviceParticipantSet){
								if(!currentParticipant.getComponentID().equals(removedPetasosParticipant.getComponentID())){
									noCandidateTargetEndpointForService = false;
									break;
								}
							}
						}
					}
					break;
				}
			}
			if(noCandidateTargetEndpointForService) {
				// TODO DO SOMETHING
				getLogger().error(".removePetasosParticipantFromLocalCache(): There is no longer a subscriber instance for->{}", removedPetasosParticipant.getSubsystemParticipantName());
				removedPetasosParticipant.setComponentExecutionControl(SoftwareComponentExecutionControlEnum.SOFTWARE_COMPONENT_PAUSE_EXECUTION);
				removedPetasosParticipant.setComponentStatus(SoftwareComponentStatusEnum.SOFTWARE_COMPONENT_FAILED);
			} else {
				getParticipantCacheDM().removePetasosParticipant(removedPetasosParticipant);
			}
		}
	}

	//
	// Getters and Setters
	//

	protected Logger getLogger(){
		return(LOG);
	}

	protected LocalPetasosParticipantCacheDM getParticipantCacheDM() {
		return participantCacheDM;
	}

	protected TaskPathwayManagementServiceInterface getTaskPathwayManagementService(){
		return(this.taskPathwayManagementService);
	}
}
