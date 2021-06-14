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
import net.fhirfactory.pegacorn.internals.esr.cache.RoleESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.RoleESR;
import net.fhirfactory.pegacorn.internals.esr.resources.valuesets.IdentifierESDTTypesEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;

import javax.inject.Inject;

public abstract class RoleESRBroker extends ESRBroker {

    @Inject
    private RoleESRCache roleCache;

    @Override
    protected PegacornESRCache specifyCache() {
        return (roleCache);
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

    public ESRMethodOutcome createRole(RoleESR newRole){
        ESRMethodOutcome outcome = this.createDirectoryEntry(newRole);
        return(outcome);
    }

    public ESRMethodOutcome createRole(String roleCategoryID, String roleName){
        if(roleCategoryID == null || roleName == null ){
            ESRMethodOutcome outcome = new ESRMethodOutcome();
            outcome.setStatus(ESRMethodOutcomeEnum.CREATE_ENTRY_INVALID);
            outcome.setStatusReason("Role Category or Role Name is null");
            outcome.setCreated(false);
            return(outcome);
        } else {
            RoleESR newRole = new RoleESR();
            IdentifierESDT newRoleIdentifier = new IdentifierESDT();
            newRoleIdentifier.setValue(roleName);
            newRoleIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
            newRoleIdentifier.setType(IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_SHORT_NAME.getIdentifierType());
            newRoleIdentifier.setLeafValue(roleName);
            newRole.getIdentifiers().add(newRoleIdentifier);
            newRole.setDisplayName(roleName);
            newRole.setRoleCategory(roleCategoryID);
            ESRMethodOutcome outcome = this.createDirectoryEntry(newRole);
            return (outcome);
        }
    }


    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) {

    }
}
