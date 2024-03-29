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
package net.fhirfactory.pegacorn.core.model.ui.brokers;

import net.fhirfactory.pegacorn.core.model.ui.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.core.model.ui.cache.RoleCategoryESRCache;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.RoleCategoryESR;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTTypesEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.core.model.ui.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.core.model.ui.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.core.model.ui.cache.common.PegacornESRCache;

import javax.inject.Inject;
import java.util.List;

public abstract class RoleCategoryESRBroker extends ESRBroker {

    @Inject
    private RoleCategoryESRCache roleCategoryCache;

    @Override
    protected PegacornESRCache specifyCache() {
        return (roleCategoryCache);
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

    public ESRMethodOutcome createRoleCategory(RoleCategoryESR newRole){
        ESRMethodOutcome outcome = this.createDirectoryEntry(newRole);
        return(outcome);
    }

    public ESRMethodOutcome createRoleCategory(String roleCategoryName, List<String> roleIDs){
        if(roleCategoryName == null ){
            ESRMethodOutcome outcome = new ESRMethodOutcome();
            outcome.setStatus(ESRMethodOutcomeEnum.CREATE_ENTRY_INVALID);
            outcome.setStatusReason("Role Category is null");
            outcome.setCreated(false);
            return(outcome);
        } else {
            RoleCategoryESR newRole = new RoleCategoryESR();
            IdentifierESDT newRoleCategoryIdentifier = new IdentifierESDT();
            newRoleCategoryIdentifier.setValue(roleCategoryName);
            newRoleCategoryIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
            newRoleCategoryIdentifier.setType(IdentifierESDTTypesEnum.ESR_IDENTIFIER_TYPE_SHORT_NAME.getIdentifierType());
            newRoleCategoryIdentifier.setLeafValue(roleCategoryName);
            newRole.getIdentifiers().add(newRoleCategoryIdentifier);
            newRole.setDisplayName(roleCategoryName);
            newRole.getRoles().addAll(roleIDs);
            ESRMethodOutcome outcome = this.createDirectoryEntry(newRole);
            return (outcome);
        }
    }


    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) {

    }
}
