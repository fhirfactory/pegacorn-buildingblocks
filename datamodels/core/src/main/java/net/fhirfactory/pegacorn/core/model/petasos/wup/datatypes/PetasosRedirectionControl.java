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
package net.fhirfactory.pegacorn.core.model.petasos.wup.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;

import java.io.Serializable;
import java.time.Instant;

public class PetasosRedirectionControl implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant redirectionStart;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant redirectionFinish;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant redirectionUpdate;
    private Boolean redirectionActive;
    private String redirectionTargetParticipantName;
    private ComponentIdType redirectionTargetComponentId;

    //
    // Constructor(s)
    //

    public PetasosRedirectionControl(){
        this.redirectionActive = false;
        this.redirectionFinish = null;
        this.redirectionStart = null;
        this.redirectionUpdate = null;
        this.redirectionTargetComponentId = null;
        this.redirectionTargetParticipantName = null;
    }

    //
    // Getters and Setters
    //

    public boolean hasRedirectionStart(){
        boolean hasValue = this.redirectionStart != null;
        return(hasValue);
    }

    public Instant getRedirectionStart() {
        return redirectionStart;
    }

    public void setRedirectionStart(Instant redirectionStart) {
        this.redirectionStart = redirectionStart;
    }

    public boolean hasRedirectionFinish(){
        boolean hasValue = this.redirectionFinish != null;
        return(hasValue);
    }

    public Instant getRedirectionFinish() {
        return redirectionFinish;
    }

    public void setRedirectionFinish(Instant redirectionFinish) {
        this.redirectionFinish = redirectionFinish;
    }

    public boolean hasRedirectionUpdate(){
        boolean hasValue = this.redirectionUpdate != null;
        return(hasValue);
    }

    public Instant getRedirectionUpdate() {
        return redirectionUpdate;
    }

    public void setRedirectionUpdate(Instant redirectionUpdate) {
        this.redirectionUpdate = redirectionUpdate;
    }

    public Boolean getRedirectionActive() {
        return redirectionActive;
    }

    public void setRedirectionActive(Boolean redirectionActive) {
        this.redirectionActive = redirectionActive;
    }

    public boolean hasRedirectionTargetParticipantName(){
        boolean hasValue = this.redirectionTargetParticipantName != null;
        return(hasValue);
    }

    public String getRedirectionTargetParticipantName() {
        return redirectionTargetParticipantName;
    }

    public void setRedirectionTargetParticipantName(String redirectionTargetParticipantName) {
        this.redirectionTargetParticipantName = redirectionTargetParticipantName;
    }

    public boolean hasRedirectionTargetComponentId(){
        boolean hasValue = this.redirectionTargetComponentId != null;
        return(hasValue);
    }

    public ComponentIdType getRedirectionTargetComponentId() {
        return redirectionTargetComponentId;
    }

    public void setRedirectionTargetComponentId(ComponentIdType redirectionTargetComponentId) {
        this.redirectionTargetComponentId = redirectionTargetComponentId;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosRedirectionControl{" +
                "redirectionStart=" + redirectionStart +
                ", redirectionFinish=" + redirectionFinish +
                ", redirectionUpdate=" + redirectionUpdate +
                ", redirectionActive=" + redirectionActive +
                ", redirectionTargetParticipantName='" + redirectionTargetParticipantName + '\'' +
                ", redirectionTargetComponentId=" + redirectionTargetComponentId +
                '}';
    }
}
