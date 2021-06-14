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

import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.CareTeamESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.CareTeamESR;
import net.fhirfactory.pegacorn.internals.esr.resources.RoleESR;
import net.fhirfactory.pegacorn.internals.esr.resources.valuesets.IdentifierESDTTypesEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public abstract class CareTeamESRBroker extends ESRBroker {
    private static final Logger LOG = LoggerFactory.getLogger(CareTeamESRBroker.class);

    @Inject
    private CareTeamESRCache careTeamCache;

    @Inject
    private IdentifierESDTTypesEnum identifierESDTTypesEnum;

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
        resource.assignSimplifiedID(true, IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_SHORT_NAME.getIdentifierType(), IdentifierESDTUseEnum.USUAL);
    }

    //
    // Create
    //

    public ESRMethodOutcome createCareTeam(RoleESR newRole){
        ESRMethodOutcome outcome = this.createDirectoryEntry(newRole);
        return(outcome);
    }

    public ESRMethodOutcome createCareTeam(String careTeamShortName, String careTeamLongName){
        if(careTeamShortName == null || careTeamLongName == null ){
            ESRMethodOutcome outcome = new ESRMethodOutcome();
            outcome.setStatus(ESRMethodOutcomeEnum.CREATE_ENTRY_INVALID);
            outcome.setStatusReason("Role Category or Role Name is null");
            outcome.setCreated(false);
            return(outcome);
        } else {
            CareTeamESR newCareTeam = new CareTeamESR();
            IdentifierESDT newCareTeamIdentifier = new IdentifierESDT();
            newCareTeamIdentifier.setValue(careTeamShortName);
            newCareTeamIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
            newCareTeamIdentifier.setType(IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_SHORT_NAME.getIdentifierType());
            newCareTeamIdentifier.setLeafValue(careTeamShortName);
            newCareTeam.getIdentifiers().add(newCareTeamIdentifier);
            newCareTeam.setDisplayName(careTeamLongName);
            ESRMethodOutcome outcome = this.createDirectoryEntry(newCareTeam);
            return (outcome);
        }
    }


    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) {

    }
}
