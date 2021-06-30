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
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.HealthcareServiceESR;
import net.fhirfactory.buildingblocks.esr.models.resources.OrganizationESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierType;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.OrganisationStructure;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.HealthcareServiceESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;

@ApplicationScoped
public class HealthcareServiceESRBroker extends ESRBroker {
    private static final Logger LOG = LoggerFactory.getLogger(HealthcareServiceESRBroker.class);

    @Inject
    private HealthcareServiceESRCache healthCareServiceCache;
    
    @Inject
    private OrganizationESRBroker organisationBroker;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected PegacornESRCache specifyCache() {
        return (healthCareServiceCache);
    }

    //
    // Primary Key Setting
    //
    @Override
    protected void assignSimplifiedID(ExtremelySimplifiedResource resource) {
        getLogger().debug(".assignSimplifiedID(): Entry, resource --> {}", resource);
        if (resource == null) {
            getLogger().debug(".assignSimplifiedID(): Entry, resource is null, exiting");
            return;
        }
        resource.assignSimplifiedID(true, IdentifierType.SHORT_NAME, IdentifierESDTUseEnum.USUAL);
    }

    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) {

    }
    
    public ESRMethodOutcome createHealthCareService(HealthcareServiceESR newHealthCareService) throws ResourceInvalidSearchException{
        addOrganisationStructure(newHealthCareService);
        
        ESRMethodOutcome outcome = this.createDirectoryEntry(newHealthCareService);
        
        return outcome;
    }
    
    
    
    /**
     * Add the organisation structure.
     * 
     * @param directoryEntry
     * @throws ResourceInvalidSearchException
     */
    private void addOrganisationStructure(HealthcareServiceESR directoryEntry) throws ResourceInvalidSearchException {      
        if (directoryEntry.getPrimaryOrganizationID() != null) {
            ESRMethodOutcome outcome = organisationBroker.searchForDirectoryEntryUsingLeafValue(directoryEntry.getPrimaryOrganizationID().toLowerCase());
            
            if (outcome.getStatus().equals(ESRMethodOutcomeEnum.REVIEW_ENTRY_FOUND)) {
                OrganizationESR organisation = (OrganizationESR)outcome.getEntry();
                
                IdentifierESDT shortNameIdentifier = organisation.getIdentifierWithType(IdentifierType.SHORT_NAME);
                
                OrganisationStructure structure = new OrganisationStructure();
                
                structure.setIndex(1);
                structure.setValue(shortNameIdentifier.getLeafValue());
                structure.setType(organisation.getOrganizationType().getTypeDisplayValue());
                directoryEntry.getOrganisationStructure().add(structure);
            }
        }
    }

}
