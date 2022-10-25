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

import ca.uhn.fhir.util.StringUtil;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantControlStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistrationStatus;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinispan.commons.hash.Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistrationStatusEnum.PARTICIPANT_REGISTERED;

@ApplicationScoped
public class LocalParticipantRegistrationCache {
	private static final Logger LOG = LoggerFactory.getLogger(LocalParticipantRegistrationCache.class);

	// ConcurrentHashMap<ComponentIdType.getId(), PetasosParticipantRegistration> componentIdRegistrationMap
	private ConcurrentHashMap<String, PetasosParticipantRegistration> componentIdRegistrationMap;

	// private ConcurrentHashMap<ParticipantId.getName(), PetasosParticipantRegistration> registrationMap;
	private ConcurrentHashMap<String, PetasosParticipantRegistration> registrationMap;

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

    public LocalParticipantRegistrationCache(){
		this.componentIdRegistrationMap = new ConcurrentHashMap<>();
		this.registrationMap = new ConcurrentHashMap<>();
		this.participantCache = new ConcurrentHashMap<>();
        this.participantCacheLock = new Object();
    }

	//
	// Business Methods
	//

	public PetasosParticipantRegistration addParticipant(PetasosParticipant participant){
		getLogger().debug(".addParticipant(): Entry, participant->{}", participant);
		PetasosParticipantRegistration resultantRegistration = addParticipant(participant, null);
		getLogger().debug(".addParticipant(): Exit, resultantRegistration->{}", resultantRegistration);
		return(resultantRegistration);
	}

	public PetasosParticipantRegistration addParticipant(PetasosParticipant participant, PetasosParticipantRegistration centralRegistration){
		getLogger().debug(".addParticipant(): Entry, participant->{}, centralRegistration->{}", participant, centralRegistration);
		if(participant == null){
			getLogger().debug(".addParticipant(): Exit, participant is null");
			return(null);
		}
		if(participant.getComponentId() == null){
			throw(new IllegalArgumentException("participant.GetComponentId() cannot be null, participant->" + participant));
		}
		if(participant.getParticipantId() == null){
			throw(new IllegalArgumentException("participant.getParticipantId() cannot be null, participant->" + participant));
		}
		if(StringUtils.isEmpty(participant.getParticipantId().getName())){
			throw(new IllegalArgumentException("participant.getParticipantId() cannot be null, participant->" + participant));
		}
		PetasosParticipantRegistration registration = null;
   	    synchronized (getParticipantCacheLock()){
			boolean inCache = false;

			getLogger().trace(".addParticipant(): [Add/Update Participant Cache] Start");
			if(centralRegistration != null){
				participant.updateFromRegistration(centralRegistration);
				getLogger().trace(".addParticipant(): [Add/Update Participant Cache] updated participant->{}", participant);
			}
			getParticipantCache().put(participant.getParticipantId().getName(), participant);
			getLogger().trace(".addParticipant(): [Add/Update Participant Cache] Finish");

			getLogger().trace(".addParticipant(): [Add/Update Registration] Start");
			PetasosParticipantRegistration localRegistration = participant.toRegistration();
			if(localRegistration == null){
				throw new RuntimeException("Something is wrong, could not build a Registration instant from the participant, participant->" + participant);
			}
			localRegistration.setLocalRegistrationInstant(Instant.now());
			localRegistration.setLocalRegistrationStatus(PARTICIPANT_REGISTERED);
			getRegistrationMap().put(participant.getParticipantId().getName(), localRegistration);
			getLogger().trace(".addParticipant(): [Add/Update Registration] Finish");

			getLogger().trace(".addParticipant(): [Add/Update Component Map] Start");
			getComponentIdRegistrationMap().put(participant.getComponentId().getId(), localRegistration);
			getLogger().trace(".addParticipant(): [Add/Update Component Map] Finish");

			registration = localRegistration;
		}
		getLogger().debug(".addParticipant(): Exit, registration->{}", registration);
		return(registration);
	}

	public void removeParticipant(PetasosParticipant participant){
		getLogger().debug(".removeParticipant(): Entry, participant->{}", participant);
		if(participant == null){
			getLogger().debug(".removeParticipant(): Exit, participant is null");
			return;
		}
		synchronized (getParticipantCacheLock()){
			if(getRegistrationMap().containsKey(participant.getParticipantId().getName())){
				getRegistrationMap().remove(participant.getParticipantId().getName());
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

	public PetasosParticipantRegistration updateParticipant(PetasosParticipantRegistration centralRegistration){
		getLogger().debug(".updateParticipant(): Entry, centralParticipant->{}", centralRegistration);
		if(centralRegistration == null){
			getLogger().debug(".updateParticipant(): Exit, centralParticipant is null");
			return(null);
		}
		PetasosParticipant participant = getParticipant(centralRegistration.getParticipantId().getName());
		getLogger().trace(".updateParticipant(): cached participant->{}", participant);
		PetasosParticipantRegistration resultantRegistration = updateParticipant(participant, centralRegistration);
		getLogger().debug(".updateParticipant(): Exit, resultantRegistration->{}", resultantRegistration);
		return(resultantRegistration);
	}

	public PetasosParticipantRegistrationStatus updateParticipant(PetasosParticipant participant){
		getLogger().debug(".updateParticipant(): Entry, participant->{}", participant);
		PetasosParticipantRegistrationStatus resultantRegistration = updateParticipant(participant, null);
		getLogger().debug(".updateParticipant(): Exit, resultantRegistration->{}", resultantRegistration);
		return(resultantRegistration);
	}

	public PetasosParticipantRegistration updateParticipant(PetasosParticipant participant, PetasosParticipantRegistration centralRegistration){
		getLogger().debug(".updateParticipant(): Entry, participant->{}, centralRegistration->{}", participant, centralRegistration);
		if(participant == null){
			getLogger().error(".updateParticipant(): Exit, participant is null");
			return(null);
		}
		if(participant.getComponentId() == null){
			throw(new IllegalArgumentException("participant.GetComponentId() cannot be null, participant->" + participant));
		}
		if(participant.getParticipantId() == null){
			throw(new IllegalArgumentException("participant.getParticipantId() cannot be null, participant->" + participant));
		}
		if(StringUtils.isEmpty(participant.getParticipantId().getName())){
			throw(new IllegalArgumentException("participant.getParticipantId() cannot be null, participant->" + participant));
		}
		PetasosParticipantRegistration registration = null;
		synchronized (getParticipantCacheLock()){
			boolean inCache = false;

			getLogger().trace(".updateParticipant(): [Add/Update Participant] Start");
			if(centralRegistration != null){
				getLogger().trace(".updateParticipant(): [Add/Update Registration] centralRegistration is not null, updating participant using it");
				participant.updateFromRegistration(centralRegistration);
			}
			getParticipantCache().put(participant.getParticipantId().getName(), participant);
			getLogger().trace(".updateParticipant(): [Add/Update Participant] Finish");

			getLogger().trace(".updateParticipant(): [Add/Update Registration] Finish");
			PetasosParticipantRegistration localRegistration = participant.toRegistration();
			localRegistration.setLocalRegistrationInstant(Instant.now());
			localRegistration.setLocalRegistrationStatus(PARTICIPANT_REGISTERED);
			getRegistrationMap().put(participant.getParticipantId().getName(), localRegistration);
			getLogger().trace(".updateParticipant(): [Add/Update Registration] Finish");

			getLogger().trace(".updateParticipant(): [Add/Update Component Map] Start");
			getComponentIdRegistrationMap().put(participant.getComponentId().getId(), localRegistration);
			getLogger().trace(".updateParticipant(): [Add/Update Component Map] Finish");
			registration = localRegistration;
		}
		getLogger().debug(".updateParticipant(): Exit, registration->{}", registration);
		return(registration);
	}

	public PetasosParticipantRegistration getParticipantRegistration(ComponentIdType componentId){
		getLogger().debug(".getParticipantRegistration(): Entry, componentId->{}", componentId);
		if(componentId == null){
			getLogger().debug(".getParticipantRegistration(): Exit, componentId is null");
			return(null);
		}
		PetasosParticipantRegistration participantRegistration = getParticipantRegistration(componentId.getId());
		getLogger().debug(".getParticipantRegistration(): Exit, participantRegistration->{}", participantRegistration);
		return(participantRegistration);
	}

	public PetasosParticipantRegistration getParticipantRegistration(String componentIdValue){
		getLogger().debug(".getParticipantRegistration(): Entry, componentIdValue->{}", componentIdValue);
		if(StringUtils.isEmpty(componentIdValue)){
			getLogger().debug(".getParticipantRegistration(): Exit, componentIdValue is null");
			return(null);
		}
		PetasosParticipantRegistration participantRegistration = null;
		synchronized (getParticipantCacheLock()){
			if(getComponentIdRegistrationMap().containsKey(componentIdValue)){
				participantRegistration = SerializationUtils.clone(getComponentIdRegistrationMap().get(componentIdValue));
			}
		}
		getLogger().debug(".getParticipantRegistration(): Exit, participantRegistration->{}", participantRegistration);
		return(participantRegistration);
	}

	public PetasosParticipantRegistration getParticipantRegistration(PetasosParticipantId participantId){
		getLogger().debug(".getParticipantRegistration(): Entry, participantId->{}", participantId);
		if(participantId == null){
			getLogger().debug(".getParticipantRegistration(): Exit, participantId is null");
			return(null);
		}
		PetasosParticipantRegistration participantRegistration = null;
		synchronized (getParticipantCacheLock()) {
			if (StringUtils.isNotEmpty(participantId.getName())){
				if (getRegistrationMap().containsKey(participantId.getName())) {
					participantRegistration = SerializationUtils.clone(getRegistrationMap().get(participantId.getName()));
				}
			}
		}
		getLogger().debug(".getParticipantRegistration(): Exit, participantRegistration->{}", participantRegistration);
		return(participantRegistration);
	}

	public PetasosParticipant getParticipant(PetasosParticipantId participantId){
		getLogger().debug(".getParticipant(): Entry, participantId->{}", participantId);
		if(participantId == null){
			getLogger().debug(".getParticipant(): Exit, participantId is null");
			return(null);
		}
		PetasosParticipant participant = getParticipant(participantId.getName());
		getLogger().debug(".getParticipant(): Exit, participant->{}", participant);
		return(participant);
	}

	public PetasosParticipant getParticipant(String participantName){
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
			if (getRegistrationMap().containsKey(participantName)) {
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
			if (getRegistrationMap().containsKey(participantName)) {
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
			if (getRegistrationMap().containsKey(participantName)) {
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
			if (getRegistrationMap().containsKey(participantName)) {
				participantStatus = getParticipantCache().get(participantName).getParticipantStatus();
			}
		}
		getLogger().debug(".getParticipantStatus(): Exit, status->{}", participantStatus);
		return(participantStatus);
	}

	public Set<PetasosParticipantRegistration> getDownstreamParticipantSet(){
    	Set<PetasosParticipantRegistration> downstreamParticipantRegistrations = new HashSet<>();
    	synchronized(getParticipantCacheLock()) {
			Enumeration<String> participantKeys = getRegistrationMap().keys();
			while (participantKeys.hasMoreElements()) {
				PetasosParticipantRegistration currentParticipantRegistration = getRegistrationMap().get(participantKeys.nextElement());
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

	public Set<PetasosParticipantRegistration> getAllParticipantRegistrations(){
		Set<PetasosParticipantRegistration> participants = new HashSet<>();
		synchronized (getParticipantCacheLock()){
			for(PetasosParticipantRegistration currentParticipant: getRegistrationMap().values()){
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
				if (getRegistrationMap().containsKey(participantName)) {
					PetasosParticipantRegistration participantRegistration = getRegistrationMap().get(participantName);
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
				if (getRegistrationMap().containsKey(participantName)) {
					PetasosParticipantRegistration participantRegistration = getRegistrationMap().get(participantName);
					if (participantRegistration.getControlStatus().equals(PetasosParticipantControlStatusEnum.PARTICIPANT_IS_DISABLED)) {
						disabledStatus = true;
					}
				}
			}
		}
		return(disabledStatus);
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

	protected ConcurrentHashMap<String, PetasosParticipantRegistration> getComponentIdRegistrationMap(){
		return(this.componentIdRegistrationMap);
	}

	protected Object getParticipantCacheLock() {
		return participantCacheLock;
	}

	protected ConcurrentHashMap<String, PetasosParticipantRegistration> getRegistrationMap(){
		return(registrationMap);
	}

	protected ConcurrentHashMap<String, PetasosParticipant> getParticipantCache(){
		return(participantCache);
	}
}
