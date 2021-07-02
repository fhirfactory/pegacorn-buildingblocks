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

import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.OrganizationESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.internals.esr.resources.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.OrganizationESR;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;

@ApplicationScoped
public class OrganizationESRBroker extends ESRBroker {
    private static final Logger LOG = LoggerFactory.getLogger(OrganizationESRBroker.class);

    @Inject
    OrganizationESRCache organizationCache;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected PegacornESRCache specifyCache() {
        return (organizationCache);
    }

    //
    // SimplifiedID Setting
    //
    @Override
    protected void assignSimplifiedID(ExtremelySimplifiedResource resource) {
        getLogger().debug(".assignPrimaryKey(): Entry, resource --> {}", resource);
        if(resource == null){
            getLogger().debug(".assignPrimaryKey(): Entry, resource is null, exiting");
            return;
        }
        resource.assignSimplifiedID(true, IdentifierType.SHORT_NAME, IdentifierESDTUseEnum.OFFICIAL);
    }

    //
    // Create
    //

    public ESRMethodOutcome createOrganizationDE(OrganizationESR newOrg){
        ESRMethodOutcome outcome = this.createDirectoryEntry(newOrg);
        return(outcome);
    }

    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) {

    }
    
    
    
    public ESRMethodOutcome searchForDirectoryEntryUsingLeafValue(String recordID) {
        getLogger().info(".searchForDirectoryEntryUsingLeafValue(): Entry, recordID --> {}", recordID);
        
        ESRMethodOutcome outcome = new ESRMethodOutcome();
        ExtremelySimplifiedResource entry = ((OrganizationESRCache)getCache()).getDirectoryEntryForLeafValue(recordID);
        if(entry == null){
            outcome.setStatus(ESRMethodOutcomeEnum.REVIEW_ENTRY_NOT_FOUND);
            outcome.setId(recordID);
        } else {       
            outcome.setEntry(entry);
            outcome.setStatus(ESRMethodOutcomeEnum.REVIEW_ENTRY_FOUND);
            outcome.setId(entry.getSimplifiedID());
        }
        
        getLogger().info(".getResource(): Exit");
        return(outcome);        
    }
}
