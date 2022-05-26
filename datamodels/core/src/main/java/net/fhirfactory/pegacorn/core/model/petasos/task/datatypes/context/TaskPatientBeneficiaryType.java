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
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;

public class TaskPatientBeneficiaryType extends TaskBeneficiaryType{
    private HumanName patientName;
    private Identifier patientMedicalRecordNumber;
    private DateType patientDateOfBirth;

    //
    // Constructor(s)
    //

    public TaskPatientBeneficiaryType(){
        super();
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasPatientName(){
        boolean hasValue = this.patientName != null;
        return(hasValue);
    }

    public HumanName getPatientName() {
        return patientName;
    }

    public void setPatientName(HumanName patientName) {
        this.patientName = patientName;
    }

    @JsonIgnore
    public boolean hasPatientMedicalRecordNumber(){
        boolean hasValue = this.patientMedicalRecordNumber != null;
        return(hasValue);
    }

    public Identifier getPatientMedicalRecordNumber() {
        return patientMedicalRecordNumber;
    }

    public void setPatientMedicalRecordNumber(Identifier patientMedicalRecordNumber) {
        this.patientMedicalRecordNumber = patientMedicalRecordNumber;
    }

    @JsonIgnore
    public boolean hasPatientDateOfBirth(){
        boolean hasValue = this.patientDateOfBirth != null;
        return(hasValue);
    }

    public DateType getPatientDateOfBirth() {
        return patientDateOfBirth;
    }

    public void setPatientDateOfBirth(DateType patientDateOfBirth) {
        this.patientDateOfBirth = patientDateOfBirth;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskPatientBeneficiaryType{" +
                "patientName=" + patientName +
                ", patientMedicalRecordNumber=" + patientMedicalRecordNumber +
                ", patientDateOfBirth=" + patientDateOfBirth +
                ", reference=" + getReference() +
                ", type=" + getType() +
                ", identifier=" + getIdentifier() +
                ", display=" + getDisplay() +
                '}';
    }
}
