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
import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryInvalidSearchException;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryInvalidSortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class LocalMatrixRoomCache extends PegacornDirectoryEntryCache {
    private static final Logger LOG = LoggerFactory.getLogger(LocalMatrixRoomCache.class);

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    public LocalMatrixRoomCache(){
        super();
    }

    @Override
    public DirectoryMethodOutcome directoryEntrySpecificSearch(List<PegacornDirectoryEntry> sortedEntryList, Map<String, String> searchParameters, Integer paginationSize, Integer paginationNumber)
            throws DirectoryEntryInvalidSortException, DirectoryEntryInvalidSearchException {
        throw (new DirectoryEntryInvalidSearchException("Unsupported search function"));
    }

    @Override
    public DirectoryMethodOutcome getSortedDirectoryEntrySet(String sortParameter, Boolean sortAscendingOrder) throws DirectoryEntryInvalidSortException {
        throw (new DirectoryEntryInvalidSortException("Unsupported sort function"));
    }

    @Override
    protected Boolean isSupportiveOfSearchType(String attributeName) {
        return(false);
    }
}
