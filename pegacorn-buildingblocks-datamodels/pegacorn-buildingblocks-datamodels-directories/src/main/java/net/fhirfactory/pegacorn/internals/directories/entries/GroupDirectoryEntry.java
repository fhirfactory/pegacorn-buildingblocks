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
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;

public class GroupDirectoryEntry extends PegacornDirectoryEntry {
    private static final Logger LOG = LoggerFactory.getLogger(GroupDirectoryEntry.class);
    @Override
    protected Logger getLogger(){return(LOG);}

    private IdentifierDE groupManager;
    private ArrayList<IdentifierDE> groupMembership;
    private String groupType;

    public GroupDirectoryEntry(){
        super();
        this.groupMembership = new ArrayList<>();
    }

    public IdentifierDE getGroupManager() {
        return groupManager;
    }

    public void setGroupManager(IdentifierDE groupManager) {
        this.groupManager = groupManager;
    }

    public ArrayList<IdentifierDE> getGroupMembership() {
        return groupMembership;
    }

    public void setGroupMembership(ArrayList<IdentifierDE> groupMembership) {
        this.groupMembership = groupMembership;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    @Override
    public String toString() {
        return "GroupDirectoryEntry{" +
                "id=" + this.getId() +
                ", identifiers=" + this.getIdentifiers() +
                ", displayName='" + this.getDisplayName() + '\'' +
                ", systemManaged=" + this.isSystemManaged() +
                ", groupManager=" + this.getGroupManager() +
                ", groupMembership=" + this.getGroupMembership() +
                ", groupType='" + this.getGroupType() + '\'' +
                '}';
    }

    public static Comparator<PegacornDirectoryEntry> groupTypeComparator = new Comparator<PegacornDirectoryEntry>() {
        @Override
        public int compare(PegacornDirectoryEntry o1, PegacornDirectoryEntry o2) {
            if(o1 == null && o2 == null){
                return(0);
            }
            GroupDirectoryEntry groupDE1 = (GroupDirectoryEntry) o1;
            GroupDirectoryEntry groupDE2 = (GroupDirectoryEntry) o2;
            if(groupDE1.getGroupType() == null && groupDE2.getGroupType() == null){
                return(0);
            }
            if(groupDE1.getGroupType() == null && !(groupDE2.getGroupType() == null)){
                return(1);
            }
            if(!(groupDE1.getGroupType() == null) && groupDE1.getGroupType() ==  null){
                return(-1);
            }
            String groupType1 = groupDE1.getGroupType();
            String groupType2 = groupDE2.getGroupType();
            int comparison = groupType1.compareTo(groupType2);
            return(comparison);
        }
    };

    public static Comparator<PegacornDirectoryEntry> groupManagerComparator = new Comparator<PegacornDirectoryEntry>() {
        @Override
        public int compare(PegacornDirectoryEntry o1, PegacornDirectoryEntry o2) {
            if(o1 == null || o2 == null){
                return(0);
            }
            GroupDirectoryEntry groupDE1 = (GroupDirectoryEntry) o1;
            GroupDirectoryEntry groupDE2 = (GroupDirectoryEntry) o2;
            if(groupDE1.getGroupManager() == null || groupDE2.getGroupManager() == null){
                return(0);
            }
            if(groupDE1.getGroupManager() == null && !(groupDE2.getGroupManager() == null)){
                return(1);
            }
            if(!(groupDE1.getGroupManager() == null) && (groupDE2.getGroupManager() == null)){
                return(-1);
            }
            String o1GroupManagerValue = groupDE1.getGroupManager().getValue();
            String o2GroupManagerValue = groupDE2.getGroupManager().getValue();
            int comparison = o1GroupManagerValue.compareTo(o2GroupManagerValue);
            return(comparison);
        }
    };
}
