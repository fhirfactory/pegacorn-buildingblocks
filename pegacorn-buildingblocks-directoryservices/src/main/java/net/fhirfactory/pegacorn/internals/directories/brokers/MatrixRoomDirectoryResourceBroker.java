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
package net.fhirfactory.pegacorn.internals.directories.brokers;

import net.fhirfactory.pegacorn.internals.directories.brokers.common.ResourceDirectoryBroker;
import net.fhirfactory.pegacorn.internals.directories.cache.LocalMatrixRoomCache;
import net.fhirfactory.pegacorn.internals.directories.cache.common.PegacornDirectoryEntryCache;
import net.fhirfactory.pegacorn.internals.directories.entries.GroupDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.MatrixRoomDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.PractitionerDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDEUseEnum;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.SystemManagedGroupTypesEnum;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MatrixRoomDirectoryResourceBroker extends ResourceDirectoryBroker {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixRoomDirectoryResourceBroker.class);

    @Inject
    private LocalMatrixRoomCache matrixRoomCache;

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    protected PegacornDirectoryEntryCache specifyCache(){
        return(matrixRoomCache);
    }

    //
    // Create
    //

    public DirectoryMethodOutcome createMatrixRoom(MatrixRoomDirectoryEntry newRoom){
        DirectoryMethodOutcome outcome = matrixRoomCache.addCacheEntry(newRoom);
        return(outcome);
    }

    //
    // Review
    //

    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(PegacornDirectoryEntry entry) {
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Entry");
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Exit");
    }

    //
    // Update
    //

    @Override
    public DirectoryMethodOutcome updateDirectoryEntry(PegacornDirectoryEntry entry){
        getLogger().info(".updateDirectoryEntry(): Entry");
        DirectoryMethodOutcome outcome = new DirectoryMethodOutcome();
        PegacornDirectoryEntry foundEntry = null;
        getLogger().info(".updateDirectoryEntry(): Attempting to retrieve existing Resource");
        if(entry.getId() != null){
            getLogger().info(".updateDirectoryEntry(): The PegId is not-Null, so we should be able to retrieve Resource with it");
            if(getLogger().isInfoEnabled()){
                getLogger().info(".updateDirectoryEntry(): Attempting to retrieve PegacornDirectoryEntry for Id --> {}", entry.getId());
            }
            foundEntry = getCache().getCacheEntry(entry.getId().getValue());
        } else {
            getLogger().info(".PegacornDirectoryEntry(): The PegId is Null, so seeing if a suitable Identifier is available");
            IdentifierDE entryIdentifier = entry.getIdentifierWithType("EmailAddress");
            if(entryIdentifier != null){
                getLogger().info(".PegacornDirectoryEntry(): Have a suitable Identifier, now retrieving");
                if(entryIdentifier.getUse().equals(IdentifierDEUseEnum.OFFICIAL)){
                    DirectoryMethodOutcome retrievalOutcome = getCache().searchCacheForEntryUsingIdentifierDE(entryIdentifier);
                    boolean searchCompleted = retrievalOutcome.getStatus().equals(DirectoryMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
                    boolean searchFoundSomething = retrievalOutcome.getSearchResult().size() == 1;
                    if(searchCompleted && searchFoundSomething) {
                        foundEntry = retrievalOutcome.getSearchResult().get(0);
                    }
                }
            }
        }
        getLogger().info(".updatePractitionerEntry(): Check to see if we were able to retrieve existing Resource");
        DirectoryMethodOutcome entryUpdate = updateDirectoryEntry(entry);

        getLogger().info(".updatePractitioner(): Exit");
        return(entryUpdate);
    }

    //
    // Delete
    //

    //
    // Search (by Identifier)
    //

}
