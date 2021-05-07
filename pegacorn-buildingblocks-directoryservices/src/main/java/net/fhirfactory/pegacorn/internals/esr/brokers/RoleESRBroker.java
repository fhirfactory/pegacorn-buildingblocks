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

import net.fhirfactory.buildingblocks.esr.models.resources.CommonIdentifierESDTTypes;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.RoleESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcome;
import net.fhirfactory.buildingblocks.esr.models.transaction.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.RoleESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;

@ApplicationScoped
public class RoleESRBroker extends ESRBroker {
    private static final Logger LOG = LoggerFactory.getLogger(RoleESRBroker.class);

    @Inject
    private RoleESRCache roleCache;

    @Inject
    private CommonIdentifierESDTTypes commonIdentifierESDTTypes;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

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
        resource.assignSimplifiedID(true, getCommonIdentifierTypes().getShortName(), IdentifierESDTUseEnum.USUAL);
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
            newRoleIdentifier.setType(commonIdentifierESDTTypes.getShortName());
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
