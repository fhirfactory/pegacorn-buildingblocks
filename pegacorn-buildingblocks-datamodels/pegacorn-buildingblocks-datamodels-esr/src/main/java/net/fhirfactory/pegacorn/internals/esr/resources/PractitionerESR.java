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
package net.fhirfactory.pegacorn.internals.esr.resources;

import java.util.Comparator;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.EmailAddress;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.FavouriteListESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.PractitionerStatusESDT;

public class PractitionerESR extends PersonESR {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerESR.class);
    @Override
    protected Logger getLogger(){return(LOG);}

    private RoleHistory practitionerRoleHistories;
    private HashMap<String, IdentifierESDT> organizationMembership;
    private FavouriteListESDT practitionerRoleFavourites;
    private FavouriteListESDT healthcareServiceFavourites;
    private FavouriteListESDT practitionerFavourites;
    private PractitionerStatusESDT practitionerStatus;

    public PractitionerESR(){
        super();
        this.organizationMembership = new HashMap<>();
        this.practitionerRoleHistories = new RoleHistory();
        this.practitionerFavourites = new FavouriteListESDT();
        this.healthcareServiceFavourites = new FavouriteListESDT();
        this.practitionerRoleFavourites = new FavouriteListESDT();
    }

    public RoleHistory getPractitionerRoleHistories() {
        return practitionerRoleHistories;
    }
    
    public void setPractitionerRoleHistories(RoleHistory practitionerRoleHistories) {
        this.practitionerRoleHistories = practitionerRoleHistories;
    }

    public HashMap<String, IdentifierESDT> getOrganizationMembership() {
        return organizationMembership;
    }

    public void setOrganizationMembership(HashMap<String, IdentifierESDT> organizationMembership) {
        this.organizationMembership = organizationMembership;
    }

    public FavouriteListESDT getPractitionerRoleFavourites() {
        return practitionerRoleFavourites;
    }

    public void setPractitionerRoleFavourites(FavouriteListESDT practitionerRoleFavourites) {
        this.practitionerRoleFavourites = practitionerRoleFavourites;
    }

    public FavouriteListESDT getHealthcareServiceFavourites() {
        return healthcareServiceFavourites;
    }

    public void setHealthcareServiceFavourites(FavouriteListESDT healthcareServiceFavourites) {
        this.healthcareServiceFavourites = healthcareServiceFavourites;
    }

    public FavouriteListESDT getPractitionerFavourites() {
        return practitionerFavourites;
    }

    public void setPractitionerFavourites(FavouriteListESDT practitionerFavourites) {
        this.practitionerFavourites = practitionerFavourites;
    }

    public PractitionerStatusESDT getPractitionerStatus() {
        return practitionerStatus;
    }

    public void setPractitionerStatus(PractitionerStatusESDT practitionerStatus) {
        this.practitionerStatus = practitionerStatus;
    }

    @JsonIgnore
    public EmailAddress getEmailAddress(){
        IdentifierESDT foundIdentifier = getIdentifierWithType("EmailAddress");
        if(foundIdentifier == null){
            return(null);
        }
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setValue(foundIdentifier.getValue());
        return(emailAddress);
    }

    @JsonIgnore
    public void setEmailAddress(String email){
        IdentifierESDT identifier = new IdentifierESDT();
        identifier.setValue(email);
        identifier.setType("EmailAddress");
        identifier.setUse(IdentifierESDTUseEnum.USUAL);
        addIdentifier(identifier);
    }

    //
    // Identifier Type based Comparator
    //

    public static Comparator<ExtremelySimplifiedResource> identifierEmailAddressBasedComparator = new Comparator<ExtremelySimplifiedResource>() {
        @Override
        public int compare(ExtremelySimplifiedResource o1, ExtremelySimplifiedResource o2) {
            if (o1 == null && o2 == null) {
                return (0);
            }
            if (o1 == null && o2 != null) {
                return (1);
            }
            if (o1 != null && o2 == null) {
                return (-1);
            }
            if (o1.getIdentifiers() == null && o2.getIdentifiers() == null) {
                return (0);
            }
            if (o1.getIdentifiers().isEmpty() && o2.getIdentifiers().isEmpty()) {
                return (0);
            }
            if (o1.getIdentifiers().isEmpty() && !o2.getIdentifiers().isEmpty()) {
                return (1);
            }
            if (!o1.getIdentifiers().isEmpty() && o2.getIdentifiers().isEmpty()) {
                return (-1);
            }
            IdentifierESDT practitionerRole1Identifier = o1.getIdentifierWithType("EmailAddress");
            IdentifierESDT practitionerRole2Identifier = o2.getIdentifierWithType("EmailAddress");
            if (practitionerRole1Identifier == null && practitionerRole2Identifier == null) {
                return (0);
            }
            if (practitionerRole1Identifier == null && practitionerRole2Identifier != null) {
                return (1);
            }
            if (practitionerRole1Identifier != null && practitionerRole2Identifier == null) {
                return (-1);
            }
            String testValue1 = practitionerRole1Identifier.getValue();
            String testValue2 = practitionerRole2Identifier.getValue();
            int comparison = testValue1.compareTo(testValue2);
            return (comparison);
        }
    };

    //
    // Identifier Type based Comparator
    //

    public static Comparator<ExtremelySimplifiedResource> identifierMatrixUserIDBasedComparator = new Comparator<ExtremelySimplifiedResource>() {
        @Override
        public int compare(ExtremelySimplifiedResource o1, ExtremelySimplifiedResource o2) {
            if (o1 == null && o2 == null) {
                return (0);
            }
            if (o1 == null && o2 != null) {
                return (1);
            }
            if (o1 != null && o2 == null) {
                return (-1);
            }
            if (o1.getIdentifiers() == null && o2.getIdentifiers() == null) {
                return (0);
            }
            if (o1.getIdentifiers().isEmpty() && o2.getIdentifiers().isEmpty()) {
                return (0);
            }
            if (o1.getIdentifiers().isEmpty() && !o2.getIdentifiers().isEmpty()) {
                return (1);
            }
            if (!o1.getIdentifiers().isEmpty() && o2.getIdentifiers().isEmpty()) {
                return (-1);
            }
            IdentifierESDT practitionerRole1Identifier = o1.getIdentifierWithType("user_id");
            IdentifierESDT practitionerRole2Identifier = o2.getIdentifierWithType("user_id");
            if (practitionerRole1Identifier == null && practitionerRole2Identifier == null) {
                return (0);
            }
            if (practitionerRole1Identifier == null && practitionerRole2Identifier != null) {
                return (1);
            }
            if (practitionerRole1Identifier != null && practitionerRole2Identifier == null) {
                return (-1);
            }
            String testValue1 = practitionerRole1Identifier.getValue();
            String testValue2 = practitionerRole2Identifier.getValue();
            int comparison = testValue1.compareTo(testValue2);
            return (comparison);
        }
    };
}
