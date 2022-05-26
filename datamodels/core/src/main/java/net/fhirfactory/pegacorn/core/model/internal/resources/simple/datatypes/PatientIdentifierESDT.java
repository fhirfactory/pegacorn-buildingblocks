package net.fhirfactory.pegacorn.core.model.internal.resources.simple.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientIdentifierESDT {
    private String givenName;
    private String lastName;
    private List<String> otherGiven;
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Date dateOfBirth;
    private String medicalRecordNumber;

    //
    // Constructor(s)
    //

    public PatientIdentifierESDT(){
        this.givenName = null;
        this.lastName = null;
        this.otherGiven = new ArrayList<>();
        this.dateOfBirth = null;
        this.medicalRecordNumber = null;
    }

    //
    // Getters and Setters
    //

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getOtherGiven() {
        return otherGiven;
    }

    public void setOtherGiven(List<String> otherGiven) {
        this.otherGiven = otherGiven;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PatientIdentifierESDT{" +
                "givenName=" + givenName +
                ", lastName=" + lastName +
                ", otherGiven=" + otherGiven +
                ", dateOfBirth=" + dateOfBirth +
                ", medicalRecordNumber=" + medicalRecordNumber +
                '}';
    }
}
