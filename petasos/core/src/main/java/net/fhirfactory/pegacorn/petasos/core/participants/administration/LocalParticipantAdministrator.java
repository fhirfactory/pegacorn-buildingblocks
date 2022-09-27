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

package net.fhirfactory.pegacorn.petasos.core.participants.administration;

import net.fhirfactory.pegacorn.core.interfaces.participant.PetasosParticipantManagementServiceInterface;
import net.fhirfactory.pegacorn.core.interfaces.participant.TaskPathwayManagementServiceInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentExecutionControlEnum;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentStatusEnum;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.participant.ProcessingPlantPetasosParticipantHolder;
import net.fhirfactory.pegacorn.core.model.petasos.participant.ProcessingPlantPetasosParticipantNameHolder;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemManifestType;
import net.fhirfactory.pegacorn.petasos.core.participants.cache.LocalParticipantCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class LocalParticipantAdministrator {
	private static final Logger LOG = LoggerFactory.getLogger(LocalParticipantAdministrator.class);

	@Inject
	private LocalParticipantCache participantCacheDM;

	@Inject
	private TaskPathwayManagementServiceInterface taskPathwayManagementService;

	@Inject
	private PetasosParticipantManagementServiceInterface participantManagementService;

	@Inject
	private ProcessingPlantInterface myProcessingPlant;

	@Inject
	private ProcessingPlantPetasosParticipantHolder participantHolder;

	@Inject
	private ProcessingPlantPetasosParticipantNameHolder participantNameHolder;


	//
	// Constructor(s)
 	//

    public LocalParticipantAdministrator(){
    }

	//
	// Business Methods
	//

	public PetasosParticipantRegistration getLocalParticipantRegistration(ComponentIdType componentId){
    	getLogger().debug(".getLocalParticipantRegistration(): Entry, componentId->{}", componentId);
		PetasosParticipantRegistration registration = participantCacheDM.getPetasosParticipantRegistration(componentId);
		getLogger().debug(".getLocalParticipantRegistration(): Exit, registration->{}", registration);
		return(registration);
	}

	public PetasosParticipantRegistration registerLocalParticipant(PetasosParticipant participant) {
		getLogger().debug(".registerPetasosParticipant(): Entry, participant->{}", participant);
		//
		// 1st, some basic defensive programming and parameter checking
		if (participant.getParticipantId() == null || participant.getSubscriptions() == null || participant.getPublishedWorkItemManifests() == null) {
			getLogger().debug(".registerPetasosParticipant(): Exit, publisherId, publishedTopics or subscribedTopics is null, not registering anything");
			return (null);
		}
		getLogger().trace(".registerPetasosParticipant(): [Register Participant into the DM] Start");
		PetasosParticipantRegistration registration = getParticipantCacheDM().addPetasosParticipant(participant);
		getLogger().trace(".registerPetasosParticipant(): [Register Participant into the DM] Finish");

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
		PetasosParticipant cachedPetasosParticipant = getParticipantCacheDM().getPetasosParticipant(participantComponent.getComponentID());
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
		PetasosParticipantRegistration registration = getParticipantManagementService().updatePetasosParticipant(cachedPetasosParticipant);
		getParticipantCacheDM().updatePetasosParticipant(registration.getParticipant());
		if(registration.getParticipant().getComponentID().equals(myProcessingPlant.getMeAsASoftwareComponent().getComponentID())){
			participantHolder.setMyProcessingPlantPetasosParticipant(registration.getParticipant());
		}
		getLogger().debug(".updateProducedWorkItems(): Exit");
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

	public void updateLocalParticipantFromGlobal(PetasosParticipant participant){

	}

	/**
	 *
	 * @param participantId
	 * @return
	 */
	public PetasosParticipant getPetasosParticipant(ComponentIdType participantId){
		getLogger().debug(".getPetasosParticipant(): Entry, participantId->{}", participantId);
		if(participantId == null){
			getLogger().debug(".getPetasosParticipant(): Exit, participantId is null, not doing anything");
			return(null);
		}
		PetasosParticipant petasosParticipant = getParticipantCacheDM().getPetasosParticipant(participantId);
		getLogger().debug(".getPetasosParticipant(): Exit, petasosParticipant->{}", petasosParticipant);
		return(petasosParticipant);
	}

	/**
	 *
	 * @param publisherId
	 * @return
	 */
	public Set<DataParcelManifest> getPublishedParcels(ComponentIdType publisherId) {
		getLogger().debug(".getPublishedParcels(): Entry, publisherId->{}", publisherId);
		if (publisherId == null) {
			getLogger().debug(".getPublishedParcels(): Exit, publisherId is null, returning empty set");
			return (new HashSet<>());
		}
		PetasosParticipant petasosParticipant = getPetasosParticipant(publisherId);
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
	 * @param publisherId
	 * @return
	 */
	public Set<TaskWorkItemManifestType> getProducedWorkItemManifests(ComponentIdType publisherId){
		getLogger().debug(".getPublishedParcels(): Entry, publisherId->{}", publisherId);
		if(publisherId == null){
			getLogger().debug(".getPublishedParcels(): Exit, publisherId is null, returning empty set");
			return(new HashSet<>());
		}
		PetasosParticipant petasosParticipant = getPetasosParticipant(publisherId);
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
				removedPetasosParticipant.setComponentExecutionStatus(SoftwareComponentExecutionControlEnum.SOFTWARE_COMPONENT_PAUSE_EXECUTION);
				removedPetasosParticipant.setComponentStatus(SoftwareComponentStatusEnum.SOFTWARE_COMPONENT_FAILED);
			} else {
				getParticipantCacheDM().removePetasosParticipant(removedPetasosParticipant);
			}
		}
	}

	public boolean isParticipantSuspended(String participantName){
		boolean isSuspended = getParticipantCacheDM().isParticipantSuspended(participantName);
		return(isSuspended);
	}

	public void suspendParticipant(String participantName){
		getParticipantCacheDM().suspendParticipant(participantName);
	}

	//
	// Getters and Setters
	//

	protected Logger getLogger(){
		return(LOG);
	}

	protected LocalParticipantCache getParticipantCacheDM() {
		return participantCacheDM;
	}

	protected TaskPathwayManagementServiceInterface getTaskPathwayManagementService(){
		return(this.taskPathwayManagementService);
	}

	protected PetasosParticipantManagementServiceInterface getParticipantManagementService(){
		return(this.participantManagementService);
	}
}
