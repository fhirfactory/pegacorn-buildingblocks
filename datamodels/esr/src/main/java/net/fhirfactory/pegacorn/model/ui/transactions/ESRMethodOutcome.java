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
package net.fhirfactory.pegacorn.model.ui.transactions;

import net.fhirfactory.pegacorn.model.ui.resources.simple.common.ExtremelySimplifiedResource;

import java.util.ArrayList;

public class ESRMethodOutcome {
    private String id;
    private boolean created;
    private ExtremelySimplifiedResource entry;
    private ESRMethodOutcomeEnum status;
    private String statusReason;

    private boolean search;
    private ArrayList<ExtremelySimplifiedResource> searchResult;
    private boolean searchSuccessful;

    public boolean isSearch() {
        return search;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    public ESRMethodOutcome(){
        this.searchResult = new ArrayList<>();
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public ExtremelySimplifiedResource getEntry() {
        return entry;
    }

    public void setEntry(ExtremelySimplifiedResource entry) {
        this.entry = entry;
    }

    public ESRMethodOutcomeEnum getStatus() {
        return status;
    }

    public void setStatus(ESRMethodOutcomeEnum status) {
        this.status = status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public ArrayList<ExtremelySimplifiedResource> getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(ArrayList<ExtremelySimplifiedResource> searchResult) {
        this.searchResult = searchResult;
    }

    public boolean isSearchSuccessful() {
        return searchSuccessful;
    }

    public void setSearchSuccessful(boolean searchSuccessful) {
        this.searchSuccessful = searchSuccessful;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
