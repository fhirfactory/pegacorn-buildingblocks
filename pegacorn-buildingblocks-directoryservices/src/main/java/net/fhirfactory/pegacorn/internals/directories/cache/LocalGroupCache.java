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
import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.PegId;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocalGroupCache extends PegacornDirectoryEntryCache {
    private static final Logger LOG = LoggerFactory.getLogger(LocalGroupCache.class);

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    public LocalGroupCache(){
        super();
    }

    public DirectoryMethodOutcome addGroup(GroupDirectoryEntry groupDirectoryEntry){
        DirectoryMethodOutcome outcome = addCacheEntry(groupDirectoryEntry);
        return(outcome);
    }

    public GroupDirectoryEntry getGroup(PegId id){
        PegacornDirectoryEntry foundEntry = this.getCacheEntry(id.getValue());
        GroupDirectoryEntry foundGroupEntry = (GroupDirectoryEntry) foundEntry;
        return(foundGroupEntry);
    }

    public GroupDirectoryEntry getGroup(IdentifierDE groupID){
        PegacornDirectoryEntry foundEntry = this.getCacheEntry(groupID);
        GroupDirectoryEntry foundGroupEntry = (GroupDirectoryEntry) foundEntry;
        return(foundGroupEntry);
    }

    public GroupDirectoryEntry searchForGroup(IdentifierDE groupID){
        PegacornDirectoryEntry foundEntry = null;
        DirectoryMethodOutcome outcome = this.searchCacheForEntryUsingIdentifierDE(groupID);
        outcome.setStatus(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
        outcome.getSearchResult().add(foundEntry);
        if(foundEntry == null){
            return(null);
        }
        GroupDirectoryEntry foundGroupDirectoryEntry = (GroupDirectoryEntry) foundEntry;
        return(foundGroupDirectoryEntry);
    }

    public DirectoryMethodOutcome addMember(IdentifierDE groupIdentifier, IdentifierDE memberIdentifier){
        GroupDirectoryEntry foundGroup = getGroup(groupIdentifier);
        if(foundGroup == null || memberIdentifier == null){
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            outcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
            outcome.setStatusReason("Group does not exist");
            return(outcome);
        }
        if(foundGroup.getGroupMembership().contains(memberIdentifier)){
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        } else {
            foundGroup.getGroupMembership().add(memberIdentifier);
        }
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            outcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
            outcome.setId(foundGroup.getId());
            outcome.setEntry(foundGroup);
            return (outcome);
    }

    public DirectoryMethodOutcome removeMember(IdentifierDE groupIdentifier, IdentifierDE memberIdentifier){
        GroupDirectoryEntry foundGroup = getGroup(groupIdentifier);
        if(foundGroup == null || memberIdentifier == null){
            DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
            outcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_INVALID);
            outcome.setStatusReason("Group does not exist");
            return(outcome);
        }
        if(foundGroup.getGroupMembership().contains(memberIdentifier)){
            foundGroup.getGroupMembership().remove(memberIdentifier);
        }
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        outcome.setStatus(DirectoryMethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
        outcome.setId(foundGroup.getId());
        outcome.setEntry(foundGroup);
        return(outcome);
    }
}
