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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;

public class TaskEncounterType extends Reference {
    private String encounterId;
    private Period encounterPeriod;

    //
    // Constructor(s)
    //

    public TaskEncounterType(){
        super();
        this.encounterId = null;
        this.encounterPeriod = null;
    }

    public TaskEncounterType(Reference reference){
        setIdentifier(reference.getIdentifier());
        setType(reference.getType());
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasEncounterId(){
        boolean hasValue = this.encounterId != null;
        return(hasValue);
    }

    public String getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    @JsonIgnore
    public boolean hasEncounterPeriod(){
        boolean hasValue = this.encounterPeriod != null;
        return(hasValue);
    }

    public Period getEncounterPeriod() {
        return encounterPeriod;
    }

    public void setEncounterPeriod(Period encounterPeriod) {
        this.encounterPeriod = encounterPeriod;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskEncounterType{" +
                "encounterId=" + encounterId +
                ", encounterPeriod=" + encounterPeriod +
                ", reference=" + getReference() +
                ", type=" + getType() +
                ", identifier=" + getIdentifier() +
                ", display=" + getDisplay() +
                '}';
    }
}
