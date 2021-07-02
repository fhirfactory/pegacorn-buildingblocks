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
import net.fhirfactory.pegacorn.internals.esr.cache.LocationESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.internals.esr.resources.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.LocationESR;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;

@ApplicationScoped
public class LocationESRBroker extends ESRBroker {
    private static final Logger LOG = LoggerFactory.getLogger(LocationESRBroker.class);

    @Inject
    LocationESRCache locationCache;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected PegacornESRCache specifyCache() {
        return (locationCache);
    }

    //
    // Primary Key Setting
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

    public ESRMethodOutcome createLocationDE(LocationESR newLocation){
        ESRMethodOutcome outcome = this.createDirectoryEntry(newLocation);
        return(outcome);
    }

    public ESRMethodOutcome createLocationDE(String parentLocationRID, String locationShortName, String locationLongName) throws ResourceInvalidSearchException {
        LocationESR locationDE = new LocationESR();
        // 1st, let's build the cumulative name identifier
        LocationESR parentLocationESR = null;
        if(parentLocationRID != null) {
            ESRMethodOutcome outcome = this.getResource(parentLocationRID);
            ExtremelySimplifiedResource entry = outcome.getEntry();
            if (entry != null) {
                parentLocationESR = (LocationESR) entry;
            }
        }
        String longCumulativeName = locationDE.populateCumulativeNameIdentifiers(parentLocationESR.getIdentifiers(), locationShortName, locationLongName);
        locationDE.setDisplayName(longCumulativeName);
        locationDE.setSystemManaged(true);
        ESRMethodOutcome outcome = getCache().addCacheEntry(locationDE);
        return(outcome);
    }


    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) {

    }
}
