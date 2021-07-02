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

import java.util.UUID;

import javax.inject.Inject;

import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.CommunicateRoomESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.internals.esr.resources.CommunicateRoomESR;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;

public abstract class CommunicateRoomESRBroker extends ESRBroker {

    @Inject
    private CommunicateRoomESRCache communicateRoomCache;

    @Override
    protected PegacornESRCache specifyCache(){
        return(communicateRoomCache);
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
        if(resource.getIdentifierWithType(IdentifierType.MATRIX_ROOM_SYSTEM_ID) != null){
            resource.assignSimplifiedID(true, IdentifierType.MATRIX_ROOM_SYSTEM_ID, IdentifierESDTUseEnum.OFFICIAL);
            getLogger().debug(".assignPrimaryKey(): Exit, Assigned MatrixRoomSystemID identifier");
            return;
        }
        if(resource.getIdentifierWithType(IdentifierType.MATRIX_ROOM_ID) != null){
            resource.assignSimplifiedID(true, IdentifierType.MATRIX_ROOM_ID, IdentifierESDTUseEnum.OFFICIAL);
            getLogger().debug(".assignPrimaryKey(): Exit, Assigned Matrix Room Id (room_id) identifier");
            return;
        }
        resource.assignSimplifiedID(UUID.randomUUID().toString(), "UUID.randomUUID()");
        getLogger().debug(".assignPrimaryKey(): Exit, Assigned UUID.randomUUID()");
    }

    //
    // Create
    //

    public ESRMethodOutcome createMatrixRoom(CommunicateRoomESR newRoom){
        ESRMethodOutcome outcome = this.createDirectoryEntry(newRoom);
        return(outcome);
    }

    //
    // Review
    //

    @Override
    protected void enrichWithDirectoryEntryTypeSpecificInformation(ExtremelySimplifiedResource entry) {
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Entry");
        getLogger().info(".enrichWithDirectoryEntryTypeSpecificInformation(): Exit");
    }

    //
    // Update
    //

    @Override
    public ESRMethodOutcome updateDirectoryEntry(ExtremelySimplifiedResource entry) throws ResourceInvalidSearchException {
        getLogger().info(".updateDirectoryEntry(): Entry");
        ESRMethodOutcome outcome = new ESRMethodOutcome();
        ExtremelySimplifiedResource foundEntry = null;
        getLogger().info(".updateDirectoryEntry(): Attempting to retrieve existing Resource");
        if(entry.getSimplifiedID() != null){
            getLogger().info(".updateDirectoryEntry(): The PegId is not-Null, so we should be able to retrieve Resource with it");
            if(getLogger().isInfoEnabled()){
                getLogger().info(".updateDirectoryEntry(): Attempting to retrieve PegacornDirectoryEntry for Id --> {}", entry.getSimplifiedID());
            }
            foundEntry = getCache().getCacheEntry(entry.getSimplifiedID());
        } else {
            getLogger().info(".PegacornDirectoryEntry(): The PegId is Null, so seeing if a suitable Identifier is available");
            IdentifierESDT entryIdentifier = entry.getIdentifierWithType(IdentifierType.EMAIL_ADDRESS);
            if(entryIdentifier != null){
                getLogger().info(".PegacornDirectoryEntry(): Have a suitable Identifier, now retrieving");
                if(entryIdentifier.getUse().equals(IdentifierESDTUseEnum.OFFICIAL)){
                    ESRMethodOutcome retrievalOutcome = getCache().searchCacheForESRUsingIdentifier(entryIdentifier);
                    boolean searchCompleted = retrievalOutcome.getStatus().equals(ESRMethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
                    boolean searchFoundSomething = retrievalOutcome.getSearchResult().size() == 1;
                    if(searchCompleted && searchFoundSomething) {
                        foundEntry = retrievalOutcome.getSearchResult().get(0);
                    }
                }
            }
        }
        getLogger().info(".updatePractitionerEntry(): Check to see if we were able to retrieve existing Resource");
        ESRMethodOutcome entryUpdate = updateDirectoryEntry(entry);

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
