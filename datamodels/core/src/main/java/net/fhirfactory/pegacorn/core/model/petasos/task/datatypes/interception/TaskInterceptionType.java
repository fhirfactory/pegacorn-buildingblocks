/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.interception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;

import java.io.Serializable;
import java.time.Instant;

public class TaskInterceptionType implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant interceptionRedirectInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant interceptionReturnInstant;
    private Boolean redirected;
    private String targetParticipant;
    private ComponentIdType targetComponent;

    //
    // Constructor(s)
    //

    public TaskInterceptionType(){
        this.interceptionRedirectInstant = null;
        this.interceptionReturnInstant = null;
        this.targetComponent = null;
        this.redirected = null;
        this.targetParticipant = null;
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasInterceptionRedirectInstant(){
        boolean hasValue = this.interceptionRedirectInstant != null;
        return(hasValue);
    }

    public Instant getInterceptionRedirectInstant() {
        return interceptionRedirectInstant;
    }

    public void setInterceptionRedirectInstant(Instant interceptionRedirectInstant) {
        this.interceptionRedirectInstant = interceptionRedirectInstant;
    }

    @JsonIgnore
    public boolean hasInterceptionReturnInstant(){
        boolean hasValue = this.interceptionReturnInstant != null;
        return(hasValue);
    }

    public Instant getInterceptionReturnInstant() {
        return interceptionReturnInstant;
    }

    public void setInterceptionReturnInstant(Instant interceptionReturnInstant) {
        this.interceptionReturnInstant = interceptionReturnInstant;
    }

    @JsonIgnore
    public boolean hasRedirect(){
        boolean hasValue = this.redirected != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean isRedirect(){
        return(this.redirected);
    }

    public Boolean getRedirected() {
        return redirected;
    }

    public void setRedirected(Boolean redirected) {
        this.redirected = redirected;
    }

    @JsonIgnore
    public boolean hasTargetParticipant(){
        boolean hasValue = targetParticipant != null;
        return(hasValue);
    }

    public String getTargetParticipant() {
        return targetParticipant;
    }

    public void setTargetParticipant(String targetParticipant) {
        this.targetParticipant = targetParticipant;
    }

    @JsonIgnore
    public boolean hasTargetComponent(){
        boolean hasValue = this.targetComponent != null;
        return(hasValue);
    }

    public ComponentIdType getTargetComponent() {
        return targetComponent;
    }

    public void setTargetComponent(ComponentIdType targetComponent) {
        this.targetComponent = targetComponent;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskInterceptionType{" +
                "interceptionRedirectInstant=" + interceptionRedirectInstant +
                ", interceptionReturnInstant=" + interceptionReturnInstant +
                ", redirected=" + redirected +
                ", targetParticipant='" + targetParticipant + '\'' +
                ", targetComponent=" + targetComponent +
                '}';
    }
}
