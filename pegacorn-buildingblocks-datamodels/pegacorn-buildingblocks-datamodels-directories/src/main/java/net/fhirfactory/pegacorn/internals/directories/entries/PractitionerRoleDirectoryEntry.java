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

import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.ContactPointDE;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;

public class PractitionerRoleDirectoryEntry extends PegacornDirectoryEntry {
    private static final Logger LOG = LoggerFactory.getLogger(PractitionerRoleDirectoryEntry.class);
    @Override
    protected Logger getLogger(){return(LOG);}

    private IdentifierDE primaryOrganizationID;
    private IdentifierDE primaryLocationID;
    private String primaryRoleCategory;
    private String primaryRole;
    private String practitionerRoleADGroup;
    private ArrayList<ContactPointDE> contactPoints;
    private ArrayList<IdentifierDE> activePractitionerSet;

    public PractitionerRoleDirectoryEntry(){
        this.contactPoints = new ArrayList<>();
        this.activePractitionerSet = new ArrayList<>();
    }

    public IdentifierDE getPrimaryOrganizationID() {
        return primaryOrganizationID;
    }

    public void setPrimaryOrganizationID(IdentifierDE primaryOrganizationID) {
        this.primaryOrganizationID = primaryOrganizationID;
    }

    public IdentifierDE getPrimaryLocationID() {
        return primaryLocationID;
    }

    public void setPrimaryLocationID(IdentifierDE primaryLocationID) {
        this.primaryLocationID = primaryLocationID;
    }

    public String getPrimaryRoleCategory() {
        return primaryRoleCategory;
    }

    public void setPrimaryRoleCategory(String primaryRoleCategory) {
        this.primaryRoleCategory = primaryRoleCategory;
    }

    public String getPrimaryRole() {
        return primaryRole;
    }

    public void setPrimaryRole(String primaryRole) {
        this.primaryRole = primaryRole;
    }

    public String getPractitionerRoleADGroup() {
        return practitionerRoleADGroup;
    }

    public void setPractitionerRoleADGroup(String practitionerRoleADGroup) {
        this.practitionerRoleADGroup = practitionerRoleADGroup;
    }

    public ArrayList<IdentifierDE> getActivePractitionerSet() {
        return activePractitionerSet;
    }

    public void setActivePractitionerSet(ArrayList<IdentifierDE> activePractitionerSet) {
        this.activePractitionerSet = activePractitionerSet;
    }

    public ArrayList<ContactPointDE> getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(ArrayList<ContactPointDE> contactPoints) {
        this.contactPoints = contactPoints;
    }

    public static Comparator<PractitionerRoleDirectoryEntry> roleCategoryComparator = new Comparator<PractitionerRoleDirectoryEntry>() {
        @Override
        public int compare(PractitionerRoleDirectoryEntry o1, PractitionerRoleDirectoryEntry o2) {
            if(o1.getPrimaryRoleCategory() == null && o2.getPrimaryRoleCategory() == null){
                return(0);
            }
            if(o1.getPrimaryRoleCategory() == null && !(o2.getPrimaryRoleCategory() == null)){
                return(1);
            }
            if(!(o1.getPrimaryRoleCategory() == null) && o2.getPrimaryRoleCategory() ==  null){
                return(-1);
            }
            String value1 = o1.getPrimaryRoleCategory();
            String value2 = o2.getPrimaryRoleCategory();
            int comparison = value1.compareTo(value2);
            return(comparison);
        }
    };

    public static Comparator<PegacornDirectoryEntry> primaryLocationComparator = new Comparator<PegacornDirectoryEntry>() {
        @Override
        public int compare(PegacornDirectoryEntry o1, PegacornDirectoryEntry o2) {
            if(o1 == null && o2 == null){
                return(0);
            }
            if(o1 == null && o2 != null){
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            PractitionerRoleDirectoryEntry practitionerRole1 = (PractitionerRoleDirectoryEntry) o1;
            PractitionerRoleDirectoryEntry practitionerRole2 = (PractitionerRoleDirectoryEntry) o2;
            if(practitionerRole1.getPrimaryLocationID() == null && practitionerRole2.getPrimaryLocationID() == null){
                return(0);
            }
            if(practitionerRole1.getPrimaryLocationID() == null && !(practitionerRole2.getPrimaryLocationID() == null)){
                return(1);
            }
            if(!(practitionerRole1.getPrimaryLocationID() == null) && practitionerRole2.getPrimaryLocationID() ==  null){
                return(-1);
            }
            String o1Value = practitionerRole1.getPrimaryLocationID().getValue();
            String o2Value = practitionerRole2.getPrimaryLocationID().getValue();
            int comparison = o1Value.compareTo(o2Value);
            return(comparison);
        }
    };

    public static Comparator<PegacornDirectoryEntry> primaryOrganizationComparator = new Comparator<PegacornDirectoryEntry>() {
        @Override
        public int compare(PegacornDirectoryEntry o1, PegacornDirectoryEntry o2) {
            if(o1 == null && o2 == null){
                return(0);
            }
            if(o1 == null && o2 != null){
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            PractitionerRoleDirectoryEntry practitionerRole1 = (PractitionerRoleDirectoryEntry) o1;
            PractitionerRoleDirectoryEntry practitionerRole2 = (PractitionerRoleDirectoryEntry) o2;
            if(practitionerRole1.getPrimaryOrganizationID() == null && practitionerRole2.getPrimaryOrganizationID() == null){
                return(0);
            }
            if(practitionerRole1.getPrimaryOrganizationID() == null && !(practitionerRole2.getPrimaryOrganizationID() == null)){
                return(1);
            }
            if(!(practitionerRole1.getPrimaryOrganizationID() == null) && practitionerRole2.getPrimaryOrganizationID() ==  null){
                return(-1);
            }
            String o1Value = practitionerRole1.getPrimaryOrganizationID().getValue();
            String o2Value = practitionerRole2.getPrimaryOrganizationID().getValue();
            int comparison = o1Value.compareTo(o2Value);
            return(comparison);
        }
    };

    public static Comparator<PegacornDirectoryEntry> primaryRoleCategoryComparator = new Comparator<PegacornDirectoryEntry>() {
        @Override
        public int compare(PegacornDirectoryEntry o1, PegacornDirectoryEntry o2) {
            if(o1 == null && o2 == null){
                return(0);
            }
            if(o1 == null && o2 != null){
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            PractitionerRoleDirectoryEntry practitionerRole1 = (PractitionerRoleDirectoryEntry) o1;
            PractitionerRoleDirectoryEntry practitionerRole2 = (PractitionerRoleDirectoryEntry) o2;
            if(practitionerRole1.getPrimaryRoleCategory() == null && practitionerRole2.getPrimaryRoleCategory() == null){
                return(0);
            }
            if(practitionerRole1.getPrimaryRoleCategory() == null && !(practitionerRole2.getPrimaryRoleCategory() == null)){
                return(1);
            }
            if(!(practitionerRole1.getPrimaryRoleCategory() == null) && practitionerRole1.getPrimaryRoleCategory() ==  null){
                return(-1);
            }
            String primaryRoleCategory1 = practitionerRole1.getPrimaryRoleCategory();
            String primaryRoleCategory2 = practitionerRole2.getPrimaryRoleCategory();
            int comparison = primaryRoleCategory1.compareTo(primaryRoleCategory2);
            return(comparison);
        }
    };

    public static Comparator<PegacornDirectoryEntry> primaryRoleIDComparator = new Comparator<PegacornDirectoryEntry>() {
        @Override
        public int compare(PegacornDirectoryEntry o1, PegacornDirectoryEntry o2) {
            if(o1 == null && o2 == null){
                return(0);
            }
            if(o1 == null && o2 != null){
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            PractitionerRoleDirectoryEntry practitionerRole1 = (PractitionerRoleDirectoryEntry) o1;
            PractitionerRoleDirectoryEntry practitionerRole2 = (PractitionerRoleDirectoryEntry) o2;
            if(practitionerRole1.getPrimaryRole() == null && practitionerRole2.getPrimaryRole() == null){
                return(0);
            }
            if(practitionerRole1.getPrimaryRole() == null && !(practitionerRole2.getPrimaryRole() == null)){
                return(1);
            }
            if(!(practitionerRole1.getPrimaryRole() == null) && practitionerRole1.getPrimaryRole() ==  null){
                return(-1);
            }
            String primaryRole1 = practitionerRole1.getPrimaryRole();
            String primaryRole2 = practitionerRole2.getPrimaryRole();
            int comparison = primaryRole1.compareTo(primaryRole2);
            return(comparison);
        }
    };
}
