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
import net.fhirfactory.pegacorn.core.model.componentid.PegacornSystemComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantRegistrationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes.DataParcelManifestSubscriptionMaskType;
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
public class LocalPetasosParticipantCacheDM {
	private static final Logger LOG = LoggerFactory.getLogger(LocalPetasosParticipantCacheDM.class);

	private ConcurrentHashMap<ComponentIdType, PetasosParticipantRegistration> participantCache;
	private Object participantCacheLock;

	@Inject
	private ProcessingPlantInterface myProcessingPlant;

	//
	// Constructor(s)
 	//

    public LocalPetasosParticipantCacheDM(){
        this.participantCache = new ConcurrentHashMap<ComponentIdType, PetasosParticipantRegistration>();
        this.participantCacheLock = new Object();
    }

	//
	// Business Methods
	//

	public PetasosParticipantRegistration addPetasosParticipant(PetasosParticipant participant){
		getLogger().debug(".addPetasosParticipant(): Entry, participant->{}", participant);
		if(participant == null){
			getLogger().debug(".addPetasosParticipant(): Exit, participant is null");
		}
		PetasosParticipantRegistration registration = null;
		synchronized (getParticipantCacheLock()){
			boolean inCache = false;
			if(getParticipantCache().containsKey(participant.getComponentID())){
				registration = getParticipantCache().get(participant.getComponentID());
				inCache = true;
			} else {
				registration = new PetasosParticipantRegistration();
			}
			PetasosParticipant cacheEntry = SerializationUtils.clone(participant);
			registration.setParticipant(cacheEntry);
			registration.setRegistrationInstant(Instant.now());
			registration.setRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PETASOS_PARTICIPANT_REGISTRATION_LOCAL_ONLY);
			if(!inCache) {
				getParticipantCache().put(cacheEntry.getComponentID(), registration);
			}
		}
		getLogger().debug(".addPetasosParticipant(): Exit, registration->{}", registration);
		return(registration);
	}

	public void removePetasosParticipant(PetasosParticipant participant){
		getLogger().debug(".removePetasosParticipant(): Entry, participant->{}", participant);
		if(participant == null){
			getLogger().debug(".removePetasosParticipant(): Exit, participant is null");
		}
		synchronized (getParticipantCacheLock()){
			if(getParticipantCache().containsKey(participant.getComponentID())){
				getParticipantCache().remove(participant.getComponentID());
			}
		}
		getLogger().debug(".removePetasosParticipant(): Exit");
	}

	public void updatePetasosParticipant(PetasosParticipant participant){
		getLogger().debug(".updatePetasosParticipant(): Entry, participant->{}", participant);
		if(participant == null){
			getLogger().debug(".updatePetasosParticipant(): Exit, participant is null");
			return;
		}
		PetasosParticipantRegistration registration = null;
		synchronized (getParticipantCacheLock()){
			boolean inCache = false;
			if(getParticipantCache().containsKey(participant.getComponentID())){
				registration = getParticipantCache().get(participant.getComponentID());
				inCache = true;
			} else {
				registration = new PetasosParticipantRegistration();
			}
			PetasosParticipant cacheEntry = SerializationUtils.clone(participant);
			registration.setParticipant(cacheEntry);
			registration.setRegistrationInstant(Instant.now());
			registration.setRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PETASOS_PARTICIPANT_REGISTRATION_LOCAL_ONLY);
			if(!inCache) {
				getParticipantCache().put(cacheEntry.getComponentID(), registration);
			}
		}
		getLogger().debug(".updatePetasosParticipant(): Exit, registration->{}", registration);
	}

	public void updatePetasosParticipantRegistration(PetasosParticipantRegistration registration){
		getLogger().debug(".updatePetasosParticipantRegistration(): Entry, registration->{}", registration);
		if(registration == null){
			getLogger().debug(".updatePetasosParticipantRegistration(): Exit, registration is null");
			return;
		}
		if(registration.getParticipant() == null){
			getLogger().warn(".updatePetasosParticipantRegistration(): Exit, registration contains malformed content, registration->{}", registration);
			return;
		}
		PetasosParticipantRegistration localRegistration = null;
		synchronized (getParticipantCacheLock()){
			boolean inCache = false;
			if(getParticipantCache().containsKey(registration.getParticipant().getComponentID())){
				localRegistration = getParticipantCache().get(registration.getParticipant().getComponentID());
				inCache = true;
			} else {
				localRegistration = new PetasosParticipantRegistration();
			}
			PetasosParticipant cacheEntry = SerializationUtils.clone(registration.getParticipant());
			localRegistration.setParticipant(cacheEntry);
			localRegistration.setRegistrationInstant(registration.getRegistrationInstant());
			localRegistration.setRegistrationId(registration.getRegistrationId());
			localRegistration.setRegistrationCommentary(registration.getRegistrationCommentary());
			localRegistration.setRegistrationStatus(registration.getRegistrationStatus());
			if(!inCache) {
				getParticipantCache().put(cacheEntry.getComponentID(), registration);
			}
		}
		getLogger().debug(".updatePetasosParticipantRegistration(): Exit, localRegistration->{}", localRegistration);
	}

	public PetasosParticipant getPetasosParticipant(ComponentIdType participantId){
		getLogger().debug(".getPetasosParticipant(): Entry, participantId->{}", participantId);
		if(participantId == null){
			getLogger().debug(".getPetasosParticipant(): Exit, participantId is null");
			return(null);
		}
		PetasosParticipant participant = null;
		synchronized (getParticipantCacheLock()){
			if(getParticipantCache().containsKey(participantId)){
				PetasosParticipantRegistration cacheEntry = getParticipantCache().get(participantId);
				participant = SerializationUtils.clone(cacheEntry.getParticipant());
			}
		}
		getLogger().debug(".getPetasosParticipant(): Exit, participant->{}", participant);
		return(participant);
	}

	public PetasosParticipantRegistration getPetasosParticipantRegistration(ComponentIdType participantId){
		getLogger().debug(".getPetasosParticipantRegistration(): Entry, participantId->{}", participantId);
		if(participantId == null){
			getLogger().debug(".getPetasosParticipantRegistration(): Exit, participantId is null");
			return(null);
		}
		PetasosParticipantRegistration registration = null;
		synchronized (getParticipantCacheLock()){
			if(getParticipantCache().containsKey(participantId)){
				PetasosParticipantRegistration cacheEntry = getParticipantCache().get(participantId);
				registration = SerializationUtils.clone(cacheEntry);
			}
		}
		getLogger().debug(".getPetasosParticipantRegistration(): Exit, registration->{}", registration);
		return(registration);
	}

	public Set<PetasosParticipant> getDownstreamParticipantSet(){
    	Set<PetasosParticipant> downstreamParticipants = new HashSet<>();
    	synchronized(getParticipantCacheLock()) {
			Enumeration<ComponentIdType> participantKeys = getParticipantCache().keys();
			while (participantKeys.hasMoreElements()) {
				PetasosParticipant currentParticipant = getParticipantCache().get(participantKeys.nextElement()).getParticipant();
				if(!currentParticipant.getSubscriptions().isEmpty()){
					if(!currentParticipant.getSubsystemParticipantName().equals(myProcessingPlant.getSubsystemParticipantName())){
						for(DataParcelManifestSubscriptionMaskType currentParticipantSubscription: currentParticipant.getSubscriptions()){
							if(currentParticipantSubscription.getOriginMask().getBoundaryPointProcessingPlantParticipantNameMask().equals(myProcessingPlant.getSubsystemParticipantName())){
								if(!downstreamParticipants.contains(currentParticipant)){
									downstreamParticipants.add(currentParticipant);
								}
							}
						}
					}
				}
			}
		}
		return(downstreamParticipants);
	}

	public Set<PetasosParticipant> getParticipantSetForSerivce(String serviceName){
    	Set<PetasosParticipant> serviceParticipantSet = new HashSet<>();
    	if(StringUtils.isEmpty(serviceName)){
    		return(serviceParticipantSet);
		}
    	synchronized (getParticipantCacheLock()){
			Enumeration<ComponentIdType> participantKeys = getParticipantCache().keys();
			while (participantKeys.hasMoreElements()) {
				PetasosParticipant currentParticipant = getParticipantCache().get(participantKeys.nextElement()).getParticipant();
				if(currentParticipant.getComponentType().equals(PegacornSystemComponentTypeTypeEnum.PROCESSING_PLANT)){
					if(currentParticipant.getSubsystemParticipantName().equals(serviceName)){
						if(!serviceParticipantSet.contains(currentParticipant)){
							serviceParticipantSet.add(currentParticipant);
						}
					}
				}
			}
		}
    	return(serviceParticipantSet);
	}

	public Set<PetasosParticipant> getAllPetasosParticipants(){
		Set<PetasosParticipant> participants = new HashSet<>();
		synchronized (getParticipantCacheLock()){
			for(PetasosParticipantRegistration currentRegistration: getParticipantCache().values()){
				PetasosParticipant currentParticipant = currentRegistration.getParticipant();
				if(participants.contains(currentParticipant)){
					// do nothing
				} else {
					participants.add(currentParticipant);
				}
			}
		}
		return(participants);
	}

	//
	// Getters and Setters
	//

	protected Logger getLogger(){
		return(LOG);
	}

	protected ConcurrentHashMap<ComponentIdType, PetasosParticipantRegistration> getParticipantCache() {
		return participantCache;
	}

	protected Object getParticipantCacheLock() {
		return participantCacheLock;
	}
}
