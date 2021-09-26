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
package net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.datatypes;

import net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.valuesets.CacheActivityActionEnum;
import net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.valuesets.CacheActivityErrorLevelEnum;
import net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.valuesets.CacheActivityOutcomeEnum;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Resource;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

public class CacheActivityStatusElement implements Serializable {
    private IdType resourceId;
    private Identifier identifier;
    private CacheActivityActionEnum action;
    private CacheActivityOutcomeEnum outcome;
    private CacheActivityErrorLevelEnum errorLevel;
    private Instant updateDate;
    private String commentary;
    private String activityLocation;
    private Resource resource;
    private boolean successful;

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getActivityLocation() {
        return activityLocation;
    }

    public void setActivityLocation(String activityLocation) {
        this.activityLocation = activityLocation;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public CacheActivityOutcomeEnum getOutcome() {
        return outcome;
    }

    public void setOutcome(CacheActivityOutcomeEnum outcome) {
        this.outcome = outcome;
    }

    public IdType getResourceId() {
        return resourceId;
    }

    public void setResourceId(IdType resourceId) {
        this.resourceId = resourceId;
    }

    public CacheActivityActionEnum getAction() {
        return action;
    }

    public void setAction(CacheActivityActionEnum action) {
        this.action = action;
    }

    public CacheActivityErrorLevelEnum getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(CacheActivityErrorLevelEnum errorLevel) {
        this.errorLevel = errorLevel;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    @Override
    public String toString() {
        return "CacheActivityStatusElement{" +
                "resourceId=" + resourceId +
                ", identifier=" + identifier +
                ", action=" + action +
                ", outcome=" + outcome +
                ", errorLevel=" + errorLevel +
                ", updateDate=" + updateDate +
                ", commentary='" + commentary + '\'' +
                ", activityLocation='" + activityLocation + '\'' +
                ", resource='" + resource + '\'' +
                ", successful=" + successful +
                '}';
    }
}
