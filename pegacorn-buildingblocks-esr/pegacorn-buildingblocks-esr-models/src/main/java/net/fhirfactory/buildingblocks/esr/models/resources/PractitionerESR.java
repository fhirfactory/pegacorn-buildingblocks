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
package net.fhirfactory.buildingblocks.esr.models.resources;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.fhirfactory.buildingblocks.esr.models.helpers.DateUtils;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.EmailAddress;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.FavouriteListESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.PractitionerStatusESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.RoleHistory;

public class PractitionerESR extends PersonESR {	
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerESR.class);
    
    protected static final DateTimeFormatter LAST_ROLE_SELECTION_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_T_HH_MM_SS_INPUT);
    
    @Override
    protected Logger getLogger(){return(LOG);}
    
    @JsonIgnore
    private RoleHistory roleHistory;
    
   
    private HashMap<String, IdentifierESDT> organizationMembership;
    private FavouriteListESDT practitionerRoleFavourites;
    private FavouriteListESDT healthcareServiceFavourites;
    private FavouriteListESDT practitionerFavourites;
    private PractitionerStatusESDT practitionerStatus;
    private String dateTimeLastRoleSelected; // leave this here even though not used.  If removed a practitioner search result will not be returned.  It is required because of the getter.
    private String matrixId;
    private String mainJobTitle;
    private List<PractitionerRoleESR> currentPractitionerRoles;
    
    public PractitionerESR(){
        super();
        this.organizationMembership = new HashMap<>();
        this.currentPractitionerRoles = new ArrayList<>();
        this.practitionerFavourites = new FavouriteListESDT();
        this.healthcareServiceFavourites = new FavouriteListESDT();
        this.practitionerRoleFavourites = new FavouriteListESDT();
        this.practitionerStatus = new PractitionerStatusESDT();
        
        roleHistory = new RoleHistory();
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

    
    public String getMainJobTitle() {
		return mainJobTitle;
	}


	public void setMainJobTitle(String mainJobTitle) {
		this.mainJobTitle = mainJobTitle;
	}

	
	public String getDateTimeLastRoleSelected() {
    	if (!roleHistory.getRoleHistories().isEmpty()) {
    		return DateUtils.format(roleHistory.getMostRecentSelection().getStartDate(), LAST_ROLE_SELECTION_DATE_TIME_FORMATTER);
    	}
    	
    	return null;
	}

	
	public void setDateTimeLastRoleSelected(String dateTimeLastRoleSelected) {
		this.dateTimeLastRoleSelected = dateTimeLastRoleSelected;
	}

	
	public List<PractitionerRoleESR> getCurrentPractitionerRoles() {
		return currentPractitionerRoles;
	}

	
	public void setCurrentPractitionerRoles(List<PractitionerRoleESR> currentPractitionerRoles) {
		this.currentPractitionerRoles = currentPractitionerRoles;
	}
	
	public void addCurrentPractitionerRole(PractitionerRoleESR practitonerRole) {
		this.currentPractitionerRoles.add(practitonerRole);
	}

	
	public String getMatrixId() {
    	//TODO this is just for mimic until the microservices is connected to the Synapse server.
    	
    	String emailAddress = this.getSimplifiedID();
    	
    	emailAddress = "@" + emailAddress;
    	emailAddress = emailAddress.replace("@test.act.gov.au", ":chs.test.gov.au");
    	
    	return emailAddress;
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

    public RoleHistory getRoleHistory() {
		return roleHistory;
	}

    
    public void setRoleHistory(RoleHistory roleHistory) {
		this.roleHistory = roleHistory;
	}

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
    
    // Comparator for last time a practitioner selected a role.
    public static Comparator<ExtremelySimplifiedResource> lastRoleSelectionDateComparator = new Comparator<ExtremelySimplifiedResource>() {

    	@Override
    	public int compare(ExtremelySimplifiedResource first, ExtremelySimplifiedResource second) {
    		if (first == null && second == null) {
    			return 0;
    		}

    		if (first == null) {
    			return -1;
    		}

    		if (second == null) {
    			return 1;
    		}

    		PractitionerESR firstPractitioner = (PractitionerESR)first;
    		PractitionerESR secondPractitioner = (PractitionerESR)second;
    		
    		if (firstPractitioner.getRoleHistory().isEmpty() && secondPractitioner.getRoleHistory().isEmpty()) {
    			return 0;
    		}
    		
    		if (firstPractitioner.getRoleHistory().isEmpty()) {
    			return 1;
    		}
    		
       		if (secondPractitioner.getRoleHistory().isEmpty()) {
    			return -1;
    		}
       		
    		return secondPractitioner.getRoleHistory().getMostRecentSelection().getStartDate().compareTo(firstPractitioner.getRoleHistory().getMostRecentSelection().getStartDate());
    	}
   };
}
