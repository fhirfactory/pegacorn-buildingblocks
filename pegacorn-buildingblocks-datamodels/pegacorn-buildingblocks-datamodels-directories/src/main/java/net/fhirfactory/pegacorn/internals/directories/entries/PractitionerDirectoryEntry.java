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
package net.fhirfactory.pegacorn.internals.directories.entries;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.EmailAddress;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDEUseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class PractitionerDirectoryEntry extends PersonDirectoryEntry {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerDirectoryEntry.class);
    @Override
    protected Logger getLogger(){return(LOG);}

    private ArrayList<IdentifierDE> currentPractitionerRoles;
    private HashMap<String, IdentifierDE> organizationMembership;
    private ArrayList<IdentifierDE> practitionerRoleFavourites;
    private ArrayList<IdentifierDE> healthcareServiceFavourites;
    private ArrayList<IdentifierDE> practitionerFavourites;

    public PractitionerDirectoryEntry(){
        super();
        organizationMembership = new HashMap<>();
        currentPractitionerRoles = new ArrayList<>();
    }

    public ArrayList<IdentifierDE> getCurrentPractitionerRoles() {
        return currentPractitionerRoles;
    }

    public void setCurrentPractitionerRoles(ArrayList<IdentifierDE> currentPractitionerRoles) {
        this.currentPractitionerRoles = currentPractitionerRoles;
    }

    public HashMap<String, IdentifierDE> getOrganizationMembership() {
        return organizationMembership;
    }

    public void setOrganizationMembership(HashMap<String, IdentifierDE> organizationMembership) {
        this.organizationMembership = organizationMembership;
    }

    public ArrayList<IdentifierDE> getPractitionerRoleFavourites() {
        return practitionerRoleFavourites;
    }

    public void setPractitionerRoleFavourites(ArrayList<IdentifierDE> practitionerRoleFavourites) {
        this.practitionerRoleFavourites = practitionerRoleFavourites;
    }

    public ArrayList<IdentifierDE> getHealthcareServiceFavourites() {
        return healthcareServiceFavourites;
    }

    public void setHealthcareServiceFavourites(ArrayList<IdentifierDE> healthcareServiceFavourites) {
        this.healthcareServiceFavourites = healthcareServiceFavourites;
    }

    public ArrayList<IdentifierDE> getPractitionerFavourites() {
        return practitionerFavourites;
    }

    public void setPractitionerFavourites(ArrayList<IdentifierDE> practitionerFavourites) {
        this.practitionerFavourites = practitionerFavourites;
    }

    @JsonIgnore
    public EmailAddress getEmailAddress(){
        IdentifierDE foundIdentifier = getIdentifierWithType("EmailAddress");
        if(foundIdentifier == null){
            return(null);
        }
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setValue(foundIdentifier.getValue());
        return(emailAddress);
    }

    @JsonIgnore
    public void setEmailAddress(String email){
        IdentifierDE identifier = new IdentifierDE();
        identifier.setValue(email);
        identifier.setType("EmailAddress");
        identifier.setUse(IdentifierDEUseEnum.OFFICIAL);
        addIdentifier(identifier);
    }
}
