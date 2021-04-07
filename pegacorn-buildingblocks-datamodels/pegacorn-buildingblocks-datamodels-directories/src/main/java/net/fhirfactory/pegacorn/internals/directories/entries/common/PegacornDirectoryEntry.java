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

package net.fhirfactory.pegacorn.internals.directories.entries.common;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDEUseEnum;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.PegId;
import org.hl7.fhir.r4.model.IdType;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

public abstract class PegacornDirectoryEntry {
    private PegId id;
    private ArrayList<IdentifierDE> identifiers;
    private String displayName;
    private boolean systemManaged;

    public PegacornDirectoryEntry(){
        this.identifiers = new ArrayList<>();
        this.displayName = null;
    }

    abstract protected Logger getLogger();

    public boolean isSystemManaged() {
        return systemManaged;
    }

    public void setSystemManaged(boolean systemManaged) {
        this.systemManaged = systemManaged;
    }

    public PegId getId() {
        return id;
    }

    public void setId(PegId id) {
        this.id = id;
    }

    public void generateId(){
        this.id = new PegId(UUID.randomUUID().toString());
    }

    public ArrayList<IdentifierDE> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(ArrayList<IdentifierDE> identifier) {
        this.identifiers = identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public IdentifierDE getIdentifierWithUse(IdentifierDEUseEnum identifierUse){
        for(IdentifierDE identifier: this.identifiers){
            if(identifier.getUse().equals(identifierUse)){
                return(identifier);
            }
        }
        return(null);
    }

    public IdentifierDE getIdentifierWithType(String identifierType){
        for(IdentifierDE identifier: this.identifiers){
            if(identifier.getType().contentEquals(identifierType)){
                return(identifier);
            }
        }
        return(null);
    }

    public void addIdentifier(IdentifierDE newIdentifier){
        getLogger().info(".addIdentifier(): Entry, newIdentifier --> {}", newIdentifier);
        if(this.identifiers.contains(newIdentifier)){
            return;
        }
        this.identifiers.add(newIdentifier);
    }

    @Override
    public String toString() {
        return "PegacornDirectoryEntry{" +
                "id=" + id +
                ", identifiers=" + identifiers +
                ", displayName='" + displayName + '\'' +
                ", systemManaged=" + systemManaged +
                '}';
    }

    public static Comparator<PegacornDirectoryEntry> identifierComparator = new Comparator<PegacornDirectoryEntry>() {
        @Override
        public int compare(PegacornDirectoryEntry o1, PegacornDirectoryEntry o2) {
            if(o1 == null && o2 == null) {
                return (0);
            }
            if(o1 == null && o2 != null) {
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            if(o1.getIdentifiers() == null && o2.getIdentifiers() == null){
                return(0);
            }
            if(o1.getIdentifiers().isEmpty() && o2.getIdentifiers().isEmpty()){
                return(0);
            }
            if(o1.getIdentifiers().isEmpty() && !o2.getIdentifiers().isEmpty()){
                return(1);
            }
            if(!o1.getIdentifiers().isEmpty() && o2.getIdentifiers().isEmpty()){
                return(-1);
            }
            String o1IdValue = o1.getIdentifiers().get(0).getValue();
            String o2IdValue = o2.getIdentifiers().get(0).getValue();
            int comparison = o1IdValue.compareTo(o2IdValue);
            return(comparison);
        }
    };

    public static Comparator<PegacornDirectoryEntry> displayNameComparator = new Comparator<PegacornDirectoryEntry>() {
        @Override
        public int compare(PegacornDirectoryEntry o1, PegacornDirectoryEntry o2) {
            if(o1 == null && o2 == null) {
                return (0);
            }
            if(o1 == null && o2 != null) {
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            if(o1.getDisplayName() == null && o2.getDisplayName() == null){
                return(0);
            }
            if(o1.getDisplayName() == null && !(o2.getDisplayName() == null)){
                return(1);
            }
            if(!(o1.getDisplayName() == null) && o2.getDisplayName() ==  null){
                return(-1);
            }
            String displayName1 = o1.getDisplayName();
            String displayName2 = o2.getDisplayName();
            int comparison = displayName1.compareTo(displayName2);
            return(comparison);
        }
    };

    public static Comparator<PegacornDirectoryEntry> pegIdComparator = new Comparator<PegacornDirectoryEntry>() {
        @Override
        public int compare(PegacornDirectoryEntry o1, PegacornDirectoryEntry o2) {
            if(o1 == null && o2 == null) {
                return (0);
            }
            if(o1 == null && o2 != null) {
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            if(o1.getId() == null && o2.getId() == null){
                return(0);
            }
            if(o1.getId() == null && o2.getId() != null){
                return(1);
            }
            if(o1.getId() != null && o2.getId() ==  null){
                return(-1);
            }
            String value1 = o1.getId().getValue();
            String value2 = o2.getId().getValue();
            int comparison = value1.compareTo(value2);
            return(comparison);
        }
    };
}
