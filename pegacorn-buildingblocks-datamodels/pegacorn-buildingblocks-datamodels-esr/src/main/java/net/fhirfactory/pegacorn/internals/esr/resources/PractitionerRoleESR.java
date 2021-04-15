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

import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.ContactPointESDT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;

public class PractitionerRoleESR extends ExtremelySimplifiedResource {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleESR.class);
    @Override
    protected Logger getLogger(){return(LOG);}

    private String primaryOrganizationID;
    private String primaryLocationID;
    private String primaryRoleCategoryID;
    private String primaryRoleID;
    private String practitionerRoleADGroup;
    private ArrayList<ContactPointESDT> contactPoints;
    private ArrayList<String> activePractitionerSet;

    public PractitionerRoleESR(){
        this.contactPoints = new ArrayList<>();
        this.activePractitionerSet = new ArrayList<>();
    }

    public String getPrimaryOrganizationID() {
        return primaryOrganizationID;
    }

    public void setPrimaryOrganizationID(String primaryOrganizationID) {
        this.primaryOrganizationID = primaryOrganizationID;
    }

    public String getPrimaryLocationID() {
        return primaryLocationID;
    }

    public void setPrimaryLocationID(String primaryLocationID) {
        this.primaryLocationID = primaryLocationID;
    }

    public ArrayList<String> getActivePractitionerSet() {
        return activePractitionerSet;
    }

    public void setActivePractitionerSet(ArrayList<String> activePractitionerSet) {
        this.activePractitionerSet = activePractitionerSet;
    }

    public String getPrimaryRoleCategoryID() {
        return primaryRoleCategoryID;
    }

    public void setPrimaryRoleCategoryID(String primaryRoleCategoryID) {
        this.primaryRoleCategoryID = primaryRoleCategoryID;
    }

    public String getPrimaryRoleID() {
        return primaryRoleID;
    }

    public void setPrimaryRoleID(String primaryRoleID) {
        this.primaryRoleID = primaryRoleID;
    }

    public String getPractitionerRoleADGroup() {
        return practitionerRoleADGroup;
    }

    public void setPractitionerRoleADGroup(String practitionerRoleADGroup) {
        this.practitionerRoleADGroup = practitionerRoleADGroup;
    }

    public ArrayList<ContactPointESDT> getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(ArrayList<ContactPointESDT> contactPoints) {
        this.contactPoints = contactPoints;
    }

    //
    // Sorting (Comparators)
    //

    public static Comparator<ExtremelySimplifiedResource> primaryLocationIDComparator = new Comparator<ExtremelySimplifiedResource>() {
        @Override
        public int compare(ExtremelySimplifiedResource o1, ExtremelySimplifiedResource o2) {
            if(o1 == null && o2 == null){
                return(0);
            }
            if(o1 == null && o2 != null){
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            PractitionerRoleESR practitionerRole1 = (PractitionerRoleESR) o1;
            PractitionerRoleESR practitionerRole2 = (PractitionerRoleESR) o2;
            if(practitionerRole1.getPrimaryLocationID() == null && practitionerRole2.getPrimaryLocationID() == null){
                return(0);
            }
            if(practitionerRole1.getPrimaryLocationID() == null && !(practitionerRole2.getPrimaryLocationID() == null)){
                return(1);
            }
            if(!(practitionerRole1.getPrimaryLocationID() == null) && practitionerRole2.getPrimaryLocationID() ==  null){
                return(-1);
            }
            String o1Value = practitionerRole1.getPrimaryLocationID();
            String o2Value = practitionerRole2.getPrimaryLocationID();
            int comparison = o1Value.compareTo(o2Value);
            return(comparison);
        }
    };

    public static Comparator<ExtremelySimplifiedResource> primaryOrganizationIDComparator = new Comparator<ExtremelySimplifiedResource>() {
        @Override
        public int compare(ExtremelySimplifiedResource o1, ExtremelySimplifiedResource o2) {
            if(o1 == null && o2 == null){
                return(0);
            }
            if(o1 == null && o2 != null){
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            PractitionerRoleESR practitionerRole1 = (PractitionerRoleESR) o1;
            PractitionerRoleESR practitionerRole2 = (PractitionerRoleESR) o2;
            if(practitionerRole1.getPrimaryOrganizationID() == null && practitionerRole2.getPrimaryOrganizationID() == null){
                return(0);
            }
            if(practitionerRole1.getPrimaryOrganizationID() == null && !(practitionerRole2.getPrimaryOrganizationID() == null)){
                return(1);
            }
            if(!(practitionerRole1.getPrimaryOrganizationID() == null) && practitionerRole2.getPrimaryOrganizationID() ==  null){
                return(-1);
            }
            String o1Value = practitionerRole1.getPrimaryOrganizationID();
            String o2Value = practitionerRole2.getPrimaryOrganizationID();
            int comparison = o1Value.compareTo(o2Value);
            return(comparison);
        }
    };

    public static Comparator<ExtremelySimplifiedResource> primaryRoleCategoryIDComparator = new Comparator<ExtremelySimplifiedResource>() {
        @Override
        public int compare(ExtremelySimplifiedResource o1, ExtremelySimplifiedResource o2) {
            if(o1 == null && o2 == null){
                return(0);
            }
            if(o1 == null && o2 != null){
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            PractitionerRoleESR practitionerRole1 = (PractitionerRoleESR) o1;
            PractitionerRoleESR practitionerRole2 = (PractitionerRoleESR) o2;
            if(practitionerRole1.getPrimaryRoleCategoryID() == null && practitionerRole2.getPrimaryRoleCategoryID() == null){
                return(0);
            }
            if(practitionerRole1.getPrimaryRoleCategoryID() == null && !(practitionerRole2.getPrimaryRoleCategoryID() == null)){
                return(1);
            }
            if(!(practitionerRole1.getPrimaryRoleCategoryID() == null) && practitionerRole1.getPrimaryRoleCategoryID() ==  null){
                return(-1);
            }
            String primaryRoleCategory1 = practitionerRole1.getPrimaryRoleCategoryID();
            String primaryRoleCategory2 = practitionerRole2.getPrimaryRoleCategoryID();
            int comparison = primaryRoleCategory1.compareTo(primaryRoleCategory2);
            return(comparison);
        }
    };

    public static Comparator<ExtremelySimplifiedResource> primaryRoleIDComparator = new Comparator<ExtremelySimplifiedResource>() {
        @Override
        public int compare(ExtremelySimplifiedResource o1, ExtremelySimplifiedResource o2) {
            if(o1 == null && o2 == null){
                return(0);
            }
            if(o1 == null && o2 != null){
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            PractitionerRoleESR practitionerRole1 = (PractitionerRoleESR) o1;
            PractitionerRoleESR practitionerRole2 = (PractitionerRoleESR) o2;
            if(practitionerRole1.getPrimaryRoleID() == null && practitionerRole2.getPrimaryRoleID() == null){
                return(0);
            }
            if(practitionerRole1.getPrimaryRoleID() == null && !(practitionerRole2.getPrimaryRoleID() == null)){
                return(1);
            }
            if(!(practitionerRole1.getPrimaryRoleID() == null) && practitionerRole1.getPrimaryRoleID() ==  null){
                return(-1);
            }
            String primaryRole1 = practitionerRole1.getPrimaryRoleID();
            String primaryRole2 = practitionerRole2.getPrimaryRoleID();
            int comparison = primaryRole1.compareTo(primaryRole2);
            return(comparison);
        }
    };
}
