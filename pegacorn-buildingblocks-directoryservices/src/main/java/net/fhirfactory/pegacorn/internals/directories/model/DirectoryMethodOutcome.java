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
package net.fhirfactory.pegacorn.internals.directories.model;

import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.PegId;

import java.util.ArrayList;

public class DirectoryMethodOutcome {
    private PegId id;
    private boolean created;
    private PegacornDirectoryEntry entry;
    private DirectoryMethodOutcomeEnum status;
    private String statusReason;

    private boolean search;
    private ArrayList<PegacornDirectoryEntry> searchResult;
    private boolean searchSuccessful;

    public boolean isSearch() {
        return search;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    public DirectoryMethodOutcome(){
        this.searchResult = new ArrayList<>();
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public PegacornDirectoryEntry getEntry() {
        return entry;
    }

    public void setEntry(PegacornDirectoryEntry entry) {
        this.entry = entry;
    }

    public DirectoryMethodOutcomeEnum getStatus() {
        return status;
    }

    public void setStatus(DirectoryMethodOutcomeEnum status) {
        this.status = status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public ArrayList<PegacornDirectoryEntry> getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(ArrayList<PegacornDirectoryEntry> searchResult) {
        this.searchResult = searchResult;
    }

    public boolean isSearchSuccessful() {
        return searchSuccessful;
    }

    public void setSearchSuccessful(boolean searchSuccessful) {
        this.searchSuccessful = searchSuccessful;
    }

    public PegId getId() {
        return id;
    }

    public void setId(PegId id) {
        this.id = id;
    }
}
