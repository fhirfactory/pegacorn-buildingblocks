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
package net.fhirfactory.pegacorn.internals.directories.cache;

import net.fhirfactory.pegacorn.internals.directories.cache.common.PegacornDirectoryEntryCache;
import net.fhirfactory.pegacorn.internals.directories.entries.GroupDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.PractitionerRoleDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryInvalidSearchException;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryInvalidSortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class LocalPractitionerRoleCache extends PegacornDirectoryEntryCache {
    private static final Logger LOG = LoggerFactory.getLogger(LocalPractitionerRoleCache.class);

    public LocalPractitionerRoleCache(){
        super();
    }

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    public DirectoryMethodOutcome addPractitionerRole(PractitionerRoleDirectoryEntry practitionerDirectoryEntry){
        DirectoryMethodOutcome outcome = addCacheEntry(practitionerDirectoryEntry);
        return(outcome);
    }

    public PractitionerRoleDirectoryEntry getPractitionerRole(IdentifierDE practitionerID){
        PegacornDirectoryEntry foundEntry = this.getCacheEntry(practitionerID);
        PractitionerRoleDirectoryEntry foundPractitionerDirectoryEntry = (PractitionerRoleDirectoryEntry) foundEntry;
        return(foundPractitionerDirectoryEntry);
    }

    @Override
    public DirectoryMethodOutcome directoryEntrySpecificSearch(List<PegacornDirectoryEntry> sortedEntryList, Map<String, String> searchParameters, Integer paginationSize, Integer paginationNumber) throws DirectoryEntryInvalidSortException, DirectoryEntryInvalidSearchException {
        return null;
    }

    @Override
    protected Boolean isSupportiveOfSearchType(String attributeName) {
        if(attributeName == null){
            return(false);
        }
        if(attributeName.isEmpty()){
            return(false);
        }
        if(attributeName.startsWith("Identifier") || attributeName.startsWith("identifier")){
            return(true);
        }
        return false;
    }

    protected ArrayList<PegacornDirectoryEntry> getSortedByPrimaryRoleIdEntrySet(){
        if(this.getCacheEntries().isEmpty()){
            return(new ArrayList<>());
        }
        Collection<PegacornDirectoryEntry> unsortedEntriesCollection = this.getCacheEntries().values();
        ArrayList<PegacornDirectoryEntry> entryList = new ArrayList<>();
        entryList.addAll(unsortedEntriesCollection);
        Collections.sort(entryList, PractitionerRoleDirectoryEntry.primaryRoleIDComparator);
        return(entryList);
    }

    protected ArrayList<PegacornDirectoryEntry> getSortedByPrimaryRoleCategoryEntrySet(){
        getLogger().info(".getSortedByPrimaryRoleCategoryEntrySet(): Entry");
        if(this.getCacheEntries().isEmpty()){
            getLogger().info(".getSortedByPrimaryRoleCategoryEntrySet(): cache is empty, so returning empty list");
            return(new ArrayList<>());
        }
        Collection<PegacornDirectoryEntry> unsortedEntriesCollection = this.getCacheEntries().values();
        ArrayList<PegacornDirectoryEntry> entryList = new ArrayList<>();
        entryList.addAll(unsortedEntriesCollection);
        Collections.sort(entryList, PractitionerRoleDirectoryEntry.primaryRoleCategoryComparator);
        getLogger().info(".getSortedByPrimaryRoleCategoryEntrySet(): Exit, list size --> {}", entryList.size());
        return(entryList);
    }

    protected ArrayList<PegacornDirectoryEntry> getSortedByPrimaryLocationEntrySet(){
        if(this.getCacheEntries().isEmpty()){
            return(new ArrayList<>());
        }
        Collection<PegacornDirectoryEntry> unsortedEntriesCollection = this.getCacheEntries().values();
        ArrayList<PegacornDirectoryEntry> entryList = new ArrayList<>();
        entryList.addAll(unsortedEntriesCollection);
        Collections.sort(entryList, PractitionerRoleDirectoryEntry.primaryLocationComparator);
        return(entryList);
    }

    protected ArrayList<PegacornDirectoryEntry> getSortedByPrimaryOrganizationEntrySet(){
        if(this.getCacheEntries().isEmpty()){
            return(new ArrayList<>());
        }
        Collection<PegacornDirectoryEntry> unsortedEntriesCollection = this.getCacheEntries().values();
        ArrayList<PegacornDirectoryEntry> entryList = new ArrayList<>();
        entryList.addAll(unsortedEntriesCollection);
        Collections.sort(entryList, PractitionerRoleDirectoryEntry.primaryOrganizationComparator);
        return(entryList);
    }

    @Override
    public DirectoryMethodOutcome getSortedDirectoryEntrySet(String sortAttribute, Boolean sortOrderAscending)
            throws DirectoryEntryInvalidSortException {
        getLogger().info(".getSortedDirectoryEntrySet(): Entry, sortAttribute --> {}, sortOrderAscending --> {}", sortAttribute, sortOrderAscending);
        if(sortAttribute == null){
            DirectoryMethodOutcome failureOutcome = new DirectoryMethodOutcome();
            failureOutcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_DID_NOT_COMPLETE);
            failureOutcome.setSearch(true);
            failureOutcome.setStatusReason("Sort Attribute is null");
            getLogger().info(".getSortedDirectoryEntrySet(): Exit, sortAttribute is null");
            return(failureOutcome);
        }

        if(sortAttribute.startsWith("Identifier") || sortAttribute.startsWith("identifier")){
            getLogger().info(".getSortedDirectoryEntrySet(): It is an Identifier based sort!");
            if(!sortAttribute.contains("\\|")){
                throw(new DirectoryEntryInvalidSortException("Does not contain a valid Identifier Type parameter = \"Identifier|Type\""));
            }
            String[] identifierQualifier = sortAttribute.split("\\|");
            if(identifierQualifier.length != 2){
                throw(new DirectoryEntryInvalidSortException("Format of the sort request must be = \"Identifier|Type\""));
            }
            String identifierType = identifierQualifier[1];
            DirectoryMethodOutcome output = this.getSortedDirectoryEntrySet(identifierType, sortOrderAscending);
            getLogger().info(".getSortedDirectoryEntrySet(): Exit");
            return(output);
        }

        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();

        outcome.setSearch(true);
        outcome.setSearchSuccessful(false);
        ArrayList<PegacornDirectoryEntry> sortedList = null;
        Boolean sortAttributeSupported = false;
        if(sortAttribute.equalsIgnoreCase("primaryOrganization")) {
            getLogger().info(".getSortedDirectoryEntrySet(): It is an Organization based sort!");
            sortAttributeSupported = true;
            sortedList = getSortedByPrimaryOrganizationEntrySet();
            if(!sortedList.isEmpty()) {
                getLogger().info(".getSortedDirectoryEntrySet(): extracted list is not empty...");
                outcome.setSearchSuccessful(true);
            }
        }
        if(sortAttribute.equalsIgnoreCase("primaryLocation")) {
            getLogger().info(".getSortedDirectoryEntrySet(): It is an Location based sort!");
            sortAttributeSupported = true;
            sortedList = getSortedByPrimaryLocationEntrySet();
            if(!sortedList.isEmpty()) {
                getLogger().info(".getSortedDirectoryEntrySet(): extracted list is not empty...");
                outcome.setSearchSuccessful(true);
            }
        }
        if(sortAttribute.equalsIgnoreCase("primaryRoleCategory")) {
            getLogger().info(".getSortedDirectoryEntrySet(): It is an RoleCategory based sort!");
            sortAttributeSupported = true;
            sortedList = getSortedByPrimaryRoleCategoryEntrySet();
            if(!sortedList.isEmpty()) {
                getLogger().info(".getSortedDirectoryEntrySet(): extracted list is not empty...");
                outcome.setSearchSuccessful(true);
            }
        }
        if(sortAttribute.equalsIgnoreCase("primaryRole")) {
            getLogger().info(".getSortedDirectoryEntrySet(): It is an Role based sort!");
            sortAttributeSupported = true;
            sortedList = getSortedByPrimaryRoleIdEntrySet();
            if(!sortedList.isEmpty()) {
                getLogger().info(".getSortedDirectoryEntrySet(): extracted list is not empty...");
                outcome.setSearchSuccessful(true);
            }
        }
        if(!sortAttributeSupported){
            throw(new DirectoryEntryInvalidSortException("Sort strategy not supported"));
        }
        if(outcome.isSearchSuccessful()) {
            if (sortOrderAscending) {
                for (Integer counter = 0; counter < sortedList.size(); counter += 1){
                    PegacornDirectoryEntry currentEntry = sortedList.get(counter);
                    outcome.getSearchResult().add(counter, currentEntry);
                    PractitionerRoleDirectoryEntry currentPractitionerRole = (PractitionerRoleDirectoryEntry)outcome.getSearchResult().get(counter);
                    getLogger().info("Info: Entry --> {} :: {}", currentPractitionerRole.getPrimaryRoleCategory(), currentPractitionerRole.getDisplayName() );
                }
                outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
            } else {
                ArrayList<PegacornDirectoryEntry> reverseList = reverseSortOrder(sortedList);
                for (Integer counter = 0; counter < reverseList.size(); counter += 1){
                    PegacornDirectoryEntry currentEntry = reverseList.get(counter);
                    outcome.getSearchResult().add(counter, currentEntry);
                }
            }
        }
        return(outcome);
    }
}
