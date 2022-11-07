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

package net.fhirfactory.pegacorn.petasos.core.participants.cache;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantControlStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistrationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@ApplicationScoped
public class LocalParticipantCache {
	private static final Logger LOG = LoggerFactory.getLogger(LocalParticipantCache.class);

	// ConcurrentHashMap<ComponentIdType.getId(), PetasosParticipant> componentIdParticipantMap
	private ConcurrentHashMap<String, PetasosParticipant> componentIdParticipantMap;


	// private ConcurrentHashMap<ParticipantId.getName(), PetasosParticipant> participantCache;
	private ConcurrentHashMap<String, PetasosParticipant> participantCache;
	private Object participantCacheLock;

	@Inject
	private TopologyIM topologyIM;

	@Inject
	private ProcessingPlantInterface myProcessingPlant;

	//
	// Constructor(s)
 	//

    public LocalParticipantCache(){
		this.componentIdParticipantMap = new ConcurrentHashMap<>();
		this.participantCache = new ConcurrentHashMap<>();
        this.participantCacheLock = new Object();
    }

	//
	// Business Methods
	//

	public PetasosParticipant addLocalParticipant(PetasosParticipant localParticipant){
		getLogger().debug(".addLocalParticipant(): Entry, localParticipant->{}", localParticipant);
		if(localParticipant == null){
			getLogger().debug(".addLocalParticipant(): Exit, localParticipant is null");
			return(null);
		}
		if(localParticipant.getParticipantId() == null){
			throw(new IllegalArgumentException("localParticipant.getParticipantId() cannot be null, localParticipant->" + localParticipant));
		}
		if(StringUtils.isEmpty(localParticipant.getParticipantId().getName())){
			throw(new IllegalArgumentException("localParticipant.getParticipantId() cannot be null, localParticipant->" + localParticipant));
		}
		getLogger().trace(".addLocalParticipant(): [Check if Participant is Already in Cache] Start");
		PetasosParticipant localCachedParticipant = getParticipantCache().get(localParticipant.getParticipantId().getName());
		getLogger().trace(".addLocalParticipant(): [Check if Participant is Already in Cache] Finish, localCachedParticipant->{}", localCachedParticipant);

		getLogger().trace(".addLocalParticipant(): [Check if the Object in Cache (if Exists) is SAME Object] Start");
		if(localCachedParticipant != null){
			if(localCachedParticipant == localParticipant){
				getLogger().trace(".addLocalParticipant(): [Check if the Object in Cache (if Exists) is SAME Object] In cache object and passed in object are same instance, nothing to do!");
				getLogger().debug(".addLocalParticipant(): Participant already in cache, exiting");
				PetasosParticipant clone = SerializationUtils.clone(localCachedParticipant);
				return(clone);
			}
		}
		getLogger().trace(".addLocalParticipant(): [Check if the Object in Cache (if Exists) is SAME Object] Finished, not same!");

		PetasosParticipant newClonedParticipant = null;
		if(localCachedParticipant == null) {
			getLogger().trace(".addLocalParticipant(): [Adding Object to Cache] Start");
			localParticipant.setLocalRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PARTICIPANT_REGISTERED);
			localParticipant.setLocalRegistrationInstant(Instant.now());
			getParticipantCache().put(localParticipant.getParticipantId().getName(), localParticipant);
			getComponentIdRegistrationMap().put(localParticipant.getComponentId().getId(), localParticipant);
			newClonedParticipant = SerializationUtils.clone(localParticipant);
			getLogger().debug(".addLocalParticipant(): [Adding Object to Cache] Finish");
		} else {
			getLogger().trace(".addLocalParticipant(): [Updating Existing Cached Object] Start");
			localParticipant.setLocalRegistrationInstant(Instant.now());
			localParticipant.setLocalRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PARTICIPANT_REGISTERED);
			newClonedParticipant = localCachedParticipant.updateFromLocalParticipant(localParticipant);
			getLogger().debug(".addLocalParticipant(): [Updating Existing Cached Object] Finish");
		}

		getLogger().debug(".addLocalParticipant(): Exit, newClonedParticipant->{}", newClonedParticipant);
		return(newClonedParticipant);
	}

	public PetasosParticipant addCentrallyStoredParticipant(PetasosParticipant centrallyStoredParticipant){
		getLogger().debug(".addCentrallyStoredParticipant(): Entry, centrallyStoredParticipant->{}", centrallyStoredParticipant);
		if(centrallyStoredParticipant == null){
			getLogger().debug(".addCentrallyStoredParticipant(): Exit, participant is null");
			return(null);
		}
		if(centrallyStoredParticipant.getParticipantId() == null){
			throw(new IllegalArgumentException("participant.getParticipantId() cannot be null, centrallyStoredParticipant->" + centrallyStoredParticipant));
		}
		if(StringUtils.isEmpty(centrallyStoredParticipant.getParticipantId().getName())){
			throw(new IllegalArgumentException("participant.getParticipantId() cannot be null, centrallyStoredParticipant->" + centrallyStoredParticipant));
		}
		PetasosParticipant locallyStoredParticipant = null;
   	    synchronized (getParticipantCacheLock()){
			getLogger().trace(".addCentrallyStoredParticipant(): [Check if Participant is Already in Cache] Start");
			PetasosParticipant localParticipant = getParticipantCache().get(centrallyStoredParticipant.getParticipantId().getName());
			getLogger().trace(".addCentrallyStoredParticipant(): [Check if Participant is Already in Cache] Finish, localParticipant->{}", localParticipant);

			getLogger().trace(".addCentrallyStoredParticipant(): [Add/Update Participant Cache] Start");

			if(localParticipant != null){
				localParticipant.updateFromCentralParticipant(centrallyStoredParticipant);
				localParticipant.setLocalRegistrationInstant(Instant.now());
				localParticipant.setLocalRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PARTICIPANT_REGISTERED);
				getLogger().trace(".addCentrallyStoredParticipant(): [Add/Update Participant Cache] updated participant->{}", localParticipant);
			} else {
				getParticipantCache().put(centrallyStoredParticipant.getParticipantId().getName(), centrallyStoredParticipant);
				if(centrallyStoredParticipant.hasComponentId()) {
					if(StringUtils.isNotEmpty(centrallyStoredParticipant.getComponentId().getId())) {
						getComponentIdRegistrationMap().put(centrallyStoredParticipant.getComponentId().getId(), centrallyStoredParticipant);
					}
				}
				localParticipant = centrallyStoredParticipant;
				localParticipant.setLocalRegistrationInstant(Instant.now());
				localParticipant.setLocalRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PARTICIPANT_REGISTERED);
				getLogger().trace(".addCentrallyStoredParticipant(): [Add/Update Participant Cache] added participant->{}", localParticipant);
			}
			getLogger().trace(".addCentrallyStoredParticipant(): [Add/Update Participant Cache] Finish");
			locallyStoredParticipant = SerializationUtils.clone(localParticipant);
		}
		getLogger().debug(".addCentrallyStoredParticipant(): Exit, locallyStoredParticipant->{}", locallyStoredParticipant);
		return(locallyStoredParticipant);
	}

	public void removeParticipant(PetasosParticipant participant){
		getLogger().debug(".removeParticipant(): Entry, participant->{}", participant);
		if(participant == null){
			getLogger().debug(".removeParticipant(): Exit, participant is null");
			return;
		}
		synchronized (getParticipantCacheLock()){
			if(getParticipantCache().containsKey(participant.getParticipantId().getName())){
				getParticipantCache().remove(participant.getParticipantId().getName());
			}
			if(participant.getComponentId() != null) {
				if (getComponentIdRegistrationMap().containsKey(participant.getComponentId().getId())) {
					getComponentIdRegistrationMap().remove(participant.getComponentId().getId());
				}
			}
			if(getParticipantCache().containsKey(participant.getParticipantId().getName())){
				getParticipantCache().remove(participant.getParticipantId().getName());
			}
		}
		getLogger().debug(".removeParticipant(): Exit");
	}

	public PetasosParticipant updateParticipant(PetasosParticipant updatedParticipant){
		getLogger().debug(".updateParticipant(): Entry, updatedParticipant->{}", updatedParticipant);
		if(updatedParticipant == null){
			getLogger().debug(".updateParticipant(): Exit, updatedParticipant is null");
			return(null);
		}
		PetasosParticipant participant = getParticipantClone(updatedParticipant.getParticipantId().getName());
		if(participant == null) {
			if (!updatedParticipant.getParticipantId().getSubsystemName().equals(myProcessingPlant.getTopologyNode().getParticipantId().getSubsystemName())) {
				participant = new PetasosParticipant(updatedParticipant);
			}
		} else {
			participant.updateFromLocalParticipant(updatedParticipant);
		}
		getLogger().debug(".updateParticipant(): Exit, resultantRegistration->{}", participant);
		return(participant);
	}


	public PetasosParticipant updateParticipantFromCentral(PetasosParticipant centrallyStoredParticipant){
		getLogger().debug(".updateParticipantFromCentral(): Entry, centrallyStoredParticipant->{}", centrallyStoredParticipant);

		if(centrallyStoredParticipant == null){
			getLogger().error(".updateParticipantFromCentral(): Exit, participant is null");
			return(null);
		}
		if(centrallyStoredParticipant.getParticipantId() == null){
			throw(new IllegalArgumentException("participant.GetComponentId() cannot be null, participant->" + centrallyStoredParticipant));
		}
		if(StringUtils.isEmpty(centrallyStoredParticipant.getParticipantId().getName())){
			throw(new IllegalArgumentException("participant.getParticipantId() cannot be null, participant->" + centrallyStoredParticipant));
		}

		PetasosParticipant updatedParticipant = null;
		synchronized (getParticipantCacheLock()) {
			boolean inCache = false;
			PetasosParticipant localParticipant = getParticipantCache().get(centrallyStoredParticipant.getParticipantId().getName());
			if(localParticipant != null) {
				getLogger().trace(".updateParticipantFromCentral(): [Add/Update Participant] Start");
				localParticipant.updateFromCentralParticipant(centrallyStoredParticipant);
				updatedParticipant = SerializationUtils.clone(localParticipant);
				getLogger().trace(".updateParticipantFromCentral(): [Add/Update Participant] Finish");
			}
		}
		getLogger().debug(".updateParticipantFromCentral(): Exit, updatedParticipant->{}", updatedParticipant);
		return(updatedParticipant);
	}

	public PetasosParticipant getParticipantCloneForComponentId(ComponentIdType componentId){
		getLogger().debug(".getParticipantForComponentId(): Entry, componentId->{}", componentId);
		if(componentId == null){
			getLogger().debug(".getParticipantForComponentId(): Exit, componentId is null");
			return(null);
		}
		String componentIdValue = componentId.getId();
		getLogger().trace(".getParticipantForComponentId(): componentIdValue->{}", componentIdValue);
		if(StringUtils.isEmpty(componentIdValue)){
			getLogger().debug(".getParticipantForComponentId(): Exit, componentIdValue is null");
			return(null);
		}
		PetasosParticipant participantRegistration = null;
		synchronized (getParticipantCacheLock()){
			if(getComponentIdRegistrationMap().containsKey(componentIdValue)){
				participantRegistration = SerializationUtils.clone(getComponentIdRegistrationMap().get(componentIdValue));
			}
		}
		getLogger().debug(".getParticipantForComponentId(): Exit, participantRegistration->{}", participantRegistration);
		return(participantRegistration);
	}

	public PetasosParticipant getParticipantCloneForComponentIdValue(String componentIdValue){
		getLogger().debug(".getParticipantForComponentIdValue(): Entry, componentIdValue->{}", componentIdValue);
		if(StringUtils.isEmpty(componentIdValue)){
			getLogger().debug(".getParticipantForComponentIdValue(): Exit, componentIdValue is null");
			return(null);
		}
		PetasosParticipant localParticipant = null;
		synchronized (getParticipantCacheLock()){
			if(getComponentIdRegistrationMap().containsKey(componentIdValue)){
				localParticipant = SerializationUtils.clone(getComponentIdRegistrationMap().get(componentIdValue));
			}
		}
		getLogger().debug(".getParticipantRgetParticipantForComponentIdValueegistration(): Exit, localParticipant->{}", localParticipant);
		return(localParticipant);
	}

	public PetasosParticipant getParticipantClone(PetasosParticipantId participantId){
		getLogger().debug(".getParticipant(): Entry, participantId->{}", participantId);
		if(participantId == null){
			getLogger().debug(".getParticipant(): Exit, participantId is null");
			return(null);
		}
		PetasosParticipant participant = getParticipantClone(participantId.getName());
		getLogger().debug(".getParticipant(): Exit, participant->{}", participant);
		return(participant);
	}

	public PetasosParticipant getParticipantClone(String participantName){
		getLogger().debug(".getParticipant(): Entry, participantName->{}", participantName);
		PetasosParticipant participant = null;
		synchronized (getParticipantCacheLock()) {
			if (StringUtils.isNotEmpty(participantName)){
				if (getParticipantCache().containsKey(participantName)) {
					participant = SerializationUtils.clone(getParticipantCache().get(participantName));
				}
			}
		}
		getLogger().debug(".getParticipant(): Exit, participant->{}", participant);
		return(participant);
	}

	public void touchParticipantUtilisationInstant(String participantName){
		getLogger().debug(".touchParticipantUtilisationInstant(): Entry, participantName->{}", participantName);
		if(StringUtils.isEmpty(participantName)){
			getLogger().debug(".touchParticipantUtilisationInstant(): Exit, participantName is empty");
			return;
		}
		synchronized (getParticipantCacheLock()){
			if(getParticipantCache().containsKey(participantName)){
				getParticipantCache().get(participantName).setUtilisationUpdateInstant(Instant.now());
			}
		}
	}

	public Instant getParticipantLastUtilisationInstant(String participantName){
		getLogger().debug(".getParticipantLastUtilisationInstant(): Entry, participantName->{}", participantName);
		if(StringUtils.isEmpty(participantName)){
			getLogger().debug(".getParticipantLastUtilisationInstant(): Exit, participantName is empty");
			return(Instant.now()) ;
		}
		Instant utilisationUpdateInstant = null;
		synchronized (getParticipantCacheLock()){
			if(getParticipantCache().containsKey(participantName)){
				utilisationUpdateInstant = getParticipantCache().get(participantName).getUtilisationUpdateInstant();
			}
		}
		if(utilisationUpdateInstant == null){
			utilisationUpdateInstant = Instant.now();
		}
		getLogger().debug(".getParticipantLastUtilisationInstant(): Exit, utilisationUpdateInstant->{}", utilisationUpdateInstant);
		return(utilisationUpdateInstant);
	}

	public void updateParticipantStatus(String participantName, PetasosParticipantStatusEnum status){
		getLogger().debug(".updateParticipantStatus(): Entry, participantName->{}, status->{}", participantName, status);
		if(StringUtils.isEmpty(participantName) || status == null){
			getLogger().debug(".updateParticipantStatus(): Exit, participantName or status are null");
			return;
		}
		synchronized (getParticipantCacheLock()) {
			if (getParticipantCache().containsKey(participantName)) {
				getParticipantCache().get(participantName).setParticipantStatus(status);
			}
		}
		getLogger().debug(".updateParticipantStatus(): Exit");
	}

	public void updateParticipantControlStatus(String participantName, PetasosParticipantControlStatusEnum controlStatus){
		getLogger().debug(".updateParticipantControlStatus(): Entry, participantName->{}, controlStatus->{}", participantName, controlStatus);
		if(StringUtils.isEmpty(participantName) || controlStatus == null){
			getLogger().debug(".updateParticipantControlStatus(): Exit, participantName or controlStatus are null");
			return;
		}
		synchronized (getParticipantCacheLock()) {
			if (getParticipantCache().containsKey(participantName)) {
				getParticipantCache().get(participantName).setControlStatus(controlStatus);
			}
		}
		getLogger().debug(".updateParticipantControlStatus(): Exit");
	}

	public void updateParticipantStatusOverride(String participantName, PetasosParticipantStatusEnum status){
		getLogger().debug(".updateParticipantStatusOverride(): Entry, participantName->{}, status->{}", participantName, status);
		if(StringUtils.isEmpty(participantName) || status == null){
			getLogger().debug(".updateParticipantStatusOverride(): Exit, participantName or status are null");
			return;
		}
		synchronized (getParticipantCacheLock()) {
			if (getParticipantCache().containsKey(participantName)) {
				getParticipantCache().get(participantName).setParticipantStatus(status);
			}
		}
		getLogger().debug(".updateParticipantStatusOverride(): Exit");
	}
	
	public PetasosParticipantStatusEnum getParticipantStatus(String participantName){
		getLogger().debug(".getParticipantStatus(): Entry, participantName->{}", participantName);
		if(StringUtils.isEmpty(participantName)){
			getLogger().debug(".updateParticipantStatusOverride(): Exit, participantName or status are null");
			return(PetasosParticipantStatusEnum.PARTICIPANT_HAS_FAILED);
		}
		PetasosParticipantStatusEnum participantStatus = PetasosParticipantStatusEnum.PARTICIPANT_HAS_FAILED;
		synchronized (getParticipantCacheLock()) {
			if (getParticipantCache().containsKey(participantName)) {
				participantStatus = getParticipantCache().get(participantName).getParticipantStatus();
			}
		}
		getLogger().debug(".getParticipantStatus(): Exit, status->{}", participantStatus);
		return(participantStatus);
	}

	public Set<PetasosParticipant> getDownstreamParticipantSet(){
    	Set<PetasosParticipant> downstreamParticipantRegistrations = new HashSet<>();
    	synchronized(getParticipantCacheLock()) {
			Enumeration<String> participantKeys = getParticipantCache().keys();
			while (participantKeys.hasMoreElements()) {
				PetasosParticipant currentParticipantRegistration = getParticipantCache().get(participantKeys.nextElement());
				if(!currentParticipantRegistration.getSubscriptions().isEmpty()){
					if(!currentParticipantRegistration.getParticipantId().getSubsystemName().equals(myProcessingPlant.getTopologyNode().getParticipant().getParticipantId().getSubsystemName())){
						for(TaskWorkItemSubscriptionType currentParticipantSubscription: currentParticipantRegistration.getSubscriptions()){
							if(currentParticipantSubscription.getSourceProcessingPlantParticipantName().equals(myProcessingPlant.getTopologyNode().getParticipant().getParticipantId().getSubsystemName())){
								if(!downstreamParticipantRegistrations.contains(currentParticipantRegistration)){
									downstreamParticipantRegistrations.add(currentParticipantRegistration);
								}
							}
						}
					}
				}
			}
		}
		return(downstreamParticipantRegistrations);
	}

	public Set<PetasosParticipant> getAllParticipants(){
		Set<PetasosParticipant> participants = new HashSet<>();
		synchronized (getParticipantCacheLock()){
			for(PetasosParticipant currentParticipant: getParticipantCache().values()){
				participants.add(SerializationUtils.clone(currentParticipant));
			}
		}
		return(participants);
	}

	public Set<String> getAllRegisteredComponentIds() {
		Set<String> registeredComponentSet = new HashSet<>();
		synchronized (getParticipantCacheLock()){
			Enumeration<String> componentKeyEnumerator = getComponentIdRegistrationMap().keys();
			while(componentKeyEnumerator.hasMoreElements()) {
				registeredComponentSet.add(componentKeyEnumerator.nextElement());
			}
		}
		return(registeredComponentSet);
	}

	public boolean isParticipantSuspended(String participantName){
		boolean suspensionStatus = false;
		if(StringUtils.isNotEmpty(participantName)) {
			synchronized (getParticipantCacheLock()) {
				if (getParticipantCache().containsKey(participantName)) {
					PetasosParticipant participantRegistration = getParticipantCache().get(participantName);
					if (participantRegistration.getControlStatus().equals(PetasosParticipantControlStatusEnum.PARTICIPANT_IS_SUSPENDED)) {
						suspensionStatus = true;
					}
				}
			}
		}
		return(suspensionStatus);
	}

	public boolean isParticipantDisabled(String participantName){
		boolean disabledStatus = false;
		if(StringUtils.isNotEmpty(participantName)) {
			synchronized (getParticipantCacheLock()) {
				if (getParticipantCache().containsKey(participantName)) {
					PetasosParticipant participantRegistration = getParticipantCache().get(participantName);
					if (participantRegistration.getControlStatus().equals(PetasosParticipantControlStatusEnum.PARTICIPANT_IS_DISABLED)) {
						disabledStatus = true;
					}
				}
			}
		}
		return(disabledStatus);
	}

	public boolean participantHasOffloadedTasks(String participantName){
		boolean offloadedTasksStatus = false;
		if(StringUtils.isNotEmpty(participantName)) {
			synchronized (getParticipantCacheLock()) {
				if (getParticipantCache().containsKey(participantName)) {
					PetasosParticipant participantRegistration = getParticipantCache().get(participantName);
					if(participantRegistration.getTaskQueueStatus() != null) {
						if (participantRegistration.getTaskQueueStatus().isPendingTasksOffloaded()) {
							offloadedTasksStatus = true;
						}
					}
				}
			}
		}
		return(offloadedTasksStatus);
	}

	public PetasosParticipantId getParticipantId(String participantName){
		if(StringUtils.isEmpty(participantName)){
			return(null);
		}
		PetasosParticipantId participantId = null;
		synchronized (getParticipantCacheLock()){
			if(getParticipantCache().containsKey(participantName)){
				participantId = getParticipantCache().get(participantName).getParticipantId();
			}
		}
		return(participantId);
	}


	//
	// Getters and Setters
	//

	protected Logger getLogger(){
		return(LOG);
	}

	protected ConcurrentHashMap<String, PetasosParticipant> getComponentIdRegistrationMap(){
		return(this.componentIdParticipantMap);
	}

	protected Object getParticipantCacheLock() {
		return participantCacheLock;
	}

	protected ConcurrentHashMap<String, PetasosParticipant> getParticipantCache(){
		return(participantCache);
	}
}
