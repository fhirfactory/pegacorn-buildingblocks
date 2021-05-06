/*
 * Copyright (c) 2021 Mark Hunter
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
package net.fhirfactory.pegacorn.internals.esr.cache;

import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.internals.esr.resources.GroupESR;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.search.GroupSearchResult;
import net.fhirfactory.pegacorn.internals.esr.resources.search.common.ESRSearchResult;
import net.fhirfactory.pegacorn.internals.esr.resources.search.common.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GroupESRCache extends PegacornESRCache {
    private static final Logger LOG = LoggerFactory.getLogger(GroupESRCache.class);

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    public GroupESRCache(){
        super();
    }

    public ESRMethodOutcome addGroup(GroupESR groupESR){
        ESRMethodOutcome outcome = addCacheEntry(groupESR);
        return(outcome);
    }

    public GroupESR getGroup(String groupPrimaryKey){
        ExtremelySimplifiedResource foundEntry = this.getCacheEntry(groupPrimaryKey);
        GroupESR foundGroupEntry = (GroupESR) foundEntry;
        return(foundGroupEntry);
    }

    public ESRMethodOutcome addMember(String groupPrimaryKey, String memberPrimaryKey){
        GroupESR foundGroup = getGroup(groupPrimaryKey);
        if(foundGroup == null || memberPrimaryKey == null){
            ESRMethodOutcome outcome = new ESRMethodOutcome();
            outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
            outcome.setStatusReason("Group does not exist");
            return(outcome);
        }
        if(foundGroup.getRoleHistory().getAllCurrentRolesAsString().contains(memberPrimaryKey)){
            ESRMethodOutcome outcome = new ESRMethodOutcome();
            outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
            return(outcome);

        } else {
            foundGroup.getGroupMembership().add(memberPrimaryKey);
        }
            ESRMethodOutcome outcome = new ESRMethodOutcome();
            outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
            outcome.setId(foundGroup.getSimplifiedID());
            outcome.setEntry(foundGroup);
            return (outcome);
    }

    public ESRMethodOutcome removeMember(String groupPrimaryKey, String memberPrimaryKey){
        GroupESR foundGroup = getGroup(groupPrimaryKey);
        if(foundGroup == null || memberPrimaryKey == null){
            ESRMethodOutcome outcome = new ESRMethodOutcome();
            outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
            outcome.setStatusReason("Group does not exist");
            return(outcome);
        }
        if(foundGroup.getRoleHistory().getAllCurrentRolesAsString().contains(memberPrimaryKey)){
            foundGroup.getRoleHistory().getAllCurrentRolesAsString().remove(memberPrimaryKey);
        }
        ESRMethodOutcome outcome = new ESRMethodOutcome();
        outcome.setStatus(ESRMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
        outcome.setId(foundGroup.getSimplifiedID());
        outcome.setEntry(foundGroup);
        return(outcome);
    }

    //
    // Search Services
    //

    @Override
    protected ESRSearchResult instatiateNewESRSearchResult(){
        GroupSearchResult result = new GroupSearchResult();
        return(result);
    }

    @Override
    public Boolean supportsSearchType(String attributeName) {
        String searchAttributeNameLowerCase = attributeName.toLowerCase();
        switch(searchAttributeNameLowerCase){
            case "simplifiedid":
            case "shortnmae":
            case "longname":
            case "displayname":
            case "grouptype":
            case "groupmanager":
                return(true);
            default:
                return(false);
        }
    }

    @Override
    public ESRSearchResult search(SearchCriteria searchCriteria)
            throws ResourceInvalidSearchException {
    	
        getLogger().debug(".search(): Entry, searchAttributeName->{}, searchAttributeValue->{}", searchCriteria.getParamName(), searchCriteria.getValue());
        
        if(searchCriteria.isValueNull()){
            throw(new ResourceInvalidSearchException("Search Value is null"));
        }
        if(searchCriteria.isParamNameNull()){
            throw(new ResourceInvalidSearchException("Search Parameter Name is empty"));
        }
        
        ESRSearchResult result = instatiateNewESRSearchResult();
        
        if(searchCriteria.isValueEmpty()){
            return(result);
        }
        
        switch(searchCriteria.getParamName().toLowerCase()){
            case "simplifiedid": {
                result = this.searchCacheUsingSimplifiedID(searchCriteria);
                return (result);
            }
            case "shortname": {
                result = this.searchCacheForESRUsingIdentifierParameters(searchCriteria, "ShortName", IdentifierESDTUseEnum.USUAL);
                return(result);
            }
            case "longname": {
                result = this.searchCacheForESRUsingIdentifierParameters(searchCriteria, "LongName", IdentifierESDTUseEnum.USUAL);
                return(result);
            }
            case "displayname": {
                result = this.searchCacheUsingDisplayName(searchCriteria);
                return(result);
            }
            case "grouptype":{
                result = this.searchCacheUsingGroupType(searchCriteria);
                return(result);
            }
            case "groupmanager":{
                result = this.searchCacheUsingGroupManager(searchCriteria);
                return(result);
            }
            default: {
                return (result);
            }
        }
    }

    protected ESRSearchResult searchCacheUsingGroupType(SearchCriteria searchCriteria) {
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(this.getSimplifiedID2ESRMap().isEmpty()){
            return(result);
        }
        for(ExtremelySimplifiedResource currentResource: this.getSimplifiedID2ESRMap().values()){
            GroupESR currentGroup = (GroupESR) currentResource;
            if(currentGroup.getGroupType().toLowerCase().contains(searchCriteria.getValue().toLowerCase())){
                result.getSearchResultList().add(currentGroup);
            }
        }
        return(result);
    }

    protected ESRSearchResult searchCacheUsingGroupManager(SearchCriteria searchCriteria){
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(this.getSimplifiedID2ESRMap().isEmpty()){
            return(result);
        }
        for(ExtremelySimplifiedResource currentResource: this.getSimplifiedID2ESRMap().values()){
            GroupESR currentGroup = (GroupESR) currentResource;
            if(currentGroup.getGroupManager().toLowerCase().contains(searchCriteria.getValue().toLowerCase())){
                result.getSearchResultList().add(currentGroup);
            }
        }
        return(result);
    }
}

