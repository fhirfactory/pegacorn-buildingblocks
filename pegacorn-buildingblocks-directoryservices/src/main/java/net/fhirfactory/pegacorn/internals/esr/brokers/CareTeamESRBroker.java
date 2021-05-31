/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.internals.esr.brokers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.CareTeamESR;
import net.fhirfactory.buildingblocks.esr.models.resources.CommonIdentifierESDTTypes;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.GroupESR;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerRoleESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.ParticipantESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.PractitionerRoleCareTeam;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.CareTeamESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;

@ApplicationScoped
public class CareTeamESRBroker extends ESRBroker {
    private static final Logger LOG = LoggerFactory.getLogger(CareTeamESRBroker.class);

    @Inject
    private CareTeamESRCache careTeamCache;
    
    @Inject
    private PractitionerRoleESRBroker practitionerRoleBroker;

    @Inject
    private CommonIdentifierESDTTypes commonIdentifierESDTTypes;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected PegacornESRCache specifyCache() {
        return (careTeamCache);
    }

    //
    // Primary Key Setting
    //
    @Override
    protected void assignSimplifiedID(ExtremelySimplifiedResource resource) {
        getLogger().debug(".assignSimplifiedID(): Entry, resource --> {}", resource);
        if(resource == null){
            getLogger().debug(".assignSimplifiedID(): Entry, resource is null, exiting");
            return;
        }
        resource.assignSimplifiedID(true, getCommonIdentifierTypes().getShortName(), IdentifierESDTUseEnum.USUAL);
    }

    //
    // Create
    //

    public ESRMethodOutcome createCareTeam(CareTeamESR newCareTeam) throws ResourceInvalidSearchException {
        ESRMethodOutcome outcome = this.createDirectoryEntry(newCareTeam);
        
        for (ParticipantESDT participant : newCareTeam.getParticipants()) {
        	PractitionerRoleESR practitionerRole =  (PractitionerRoleESR)practitionerRoleBroker.getResource(participant.getSimplifiedId().toLowerCase()).getEntry();
        	
        	if (practitionerRole != null) {
                // Need to update the practitioner role now with the care team details
        		practitionerRole.addCareTeam(newCareTeam.getSimplifiedID(), participant.getParticipantType());
        		practitionerRoleBroker.updatePractitionerRole(practitionerRole);
        	} else {
        		getLogger().warn("Practitioner role not found. Name: {}", participant.getSimplifiedId());
        	}
        }
        
        return(outcome);
    }

    
 
    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry)  {

    }

	/**
	 * Update/create a care team
	 * 
	 * @param entry
	 * @return
	 * @throws ResourceInvalidSearchException
	 */
	public ESRMethodOutcome updateCareTeam(CareTeamESR entry) throws ResourceInvalidSearchException {
		LOG.info(".updateCareTeam(): Entry");
		
		CareTeamESR existing = (CareTeamESR)careTeamCache.getCacheEntry(entry.getSimplifiedID().toLowerCase());
		
	    if(existing != null) {
	    	ESRMethodOutcome outcome = updateDirectoryEntry(entry);
	    	
	    	// If the care team was updated/created then update the practitioners records within the care team.
	    	if(outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL) || outcome.getStatus().equals(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL_CREATE)){
	        
	    		for (ParticipantESDT participant : existing.getParticipants()) {
	    			PractitionerRoleESR practitionerRole = (PractitionerRoleESR) practitionerRoleBroker.getResource(participant.getSimplifiedId().toLowerCase()).getEntry();
	    			practitionerRole.removeCareTeam(entry.getSimplifiedID());	
	    			practitionerRoleBroker.updatePractitionerRole(practitionerRole);
	    		}
		        
	    		for (ParticipantESDT participant : entry.getParticipants()) {
	    			PractitionerRoleESR practitionerRole = (PractitionerRoleESR) practitionerRoleBroker.getResource(participant.getSimplifiedId().toLowerCase()).getEntry();
	    			practitionerRole.addCareTeam(new PractitionerRoleCareTeam(entry.getSimplifiedID(), participant.getParticipantType()));
		        	practitionerRoleBroker.updatePractitionerRole(practitionerRole);
	    		}
	    	}
	        
	        return outcome;
	    } else {
	    	return createCareTeam(entry);
	    }
	}
}
