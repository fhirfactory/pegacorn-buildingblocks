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

package net.fhirfactory.pegacorn.core.model.internal.resources.simple.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.datatypes.CacheIDMetadata;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.valuesets.ExtremelySimplifiedResourceTypeEnum;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.valuesets.IdentifierESDTTypesEnum;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.valuesets.IdentifierESDTUseEnum;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public abstract class  ExtremelySimplifiedResource implements Serializable {
    private String simplifiedID;
    private CacheIDMetadata simplifiedIDMetadata;
    private ArrayList<IdentifierESDT> identifiers;
    private String displayName;
    private String description;
    private boolean systemManaged;
    private String otherID;
    private ExtremelySimplifiedResourceTypeEnum resourceESRType;

    public ExtremelySimplifiedResource(){
        this.identifiers = new ArrayList<>();
        this.displayName = null;
        this.simplifiedID = null;
        this.otherID = null;
        this.description = null;
        this.resourceESRType = null;
        this.simplifiedIDMetadata = null;
    }

    public ExtremelySimplifiedResource(ExtremelySimplifiedResource ori){
        this.identifiers = new ArrayList<>();
        this.identifiers.addAll(ori.getIdentifiers());
        this.displayName = ori.getDisplayName();
        this.simplifiedID = ori.getSimplifiedID();
        this.otherID = ori.getOtherID();
        this.description = ori.getDescription();
        this.resourceESRType = ori.getResourceESRType();
        this.simplifiedIDMetadata = ori.getSimplifiedIDMetadata();
    }

    //
    // Abstract Methods
    //
    abstract protected Logger getLogger();
    abstract protected ResourceType specifyResourceType();

    //
    // Has Methods
    //

    @JsonIgnore
    public boolean hasSimplifiedID(){
        if(this.simplifiedID == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasSimplifiedIDMetadata(){
        if(this.simplifiedIDMetadata == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasDisplayName(){
        if(this.displayName == null) {
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasDescription(){
        if(this.description == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasOtherID(){
        if(this.otherID == null){
            return(false);
        } else {
            return(true);
        }
    }

    //
    // Bean Methods
    //

    @JsonIgnore
    public ResourceType getResourceType(){
        return(specifyResourceType());
    }

    public boolean isSystemManaged() {
        return systemManaged;
    }

    public void setSystemManaged(boolean systemManaged) {
        this.systemManaged = systemManaged;
    }

    public String getSimplifiedID() {
        return simplifiedID;
    }

    public void setSimplifiedID(String simplifiedID) {
        this.simplifiedID = simplifiedID;
    }

    @JsonIgnore
    public void assignSimplifiedID(boolean useIdentifier, String identifierType, IdentifierESDTUseEnum identifierUse){
        IdentifierESDT shortNameIdentifier = this.getIdentifierWithType(identifierType);
        CacheIDMetadata meta = new CacheIDMetadata();
        setSimplifiedID(shortNameIdentifier.getValue());
        meta.setIdentifierBased(true);
        meta.setIdentifierType(identifierType);
        meta.setIdentifierUse(identifierUse.getUseCode());
        setSimplifiedIDMetadata(meta);
    }

    @JsonIgnore
    public void assignSimplifiedID(String key, String keySource){
        this.simplifiedID =key;
        CacheIDMetadata meta = new CacheIDMetadata();
        meta.setDerivationSource(keySource);
        meta.setIdentifierBased(false);
        setSimplifiedIDMetadata(meta);
    }

    public String getOtherID() {
        return otherID;
    }

    public void setOtherID(String otherID) {
        this.otherID = otherID;
    }

    public ArrayList<IdentifierESDT> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(ArrayList<IdentifierESDT> identifier) {
        this.identifiers = identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public CacheIDMetadata getSimplifiedIDMetadata() {
        return simplifiedIDMetadata;
    }

    public void setSimplifiedIDMetadata(CacheIDMetadata simplifiedIDMetadata) {
        this.simplifiedIDMetadata = simplifiedIDMetadata;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IdentifierESDT getIdentifierWithUse(IdentifierESDTUseEnum identifierUse){
        for(IdentifierESDT identifier: this.identifiers){
            if(identifier.getUse().equals(identifierUse)){
                return(identifier);
            }
        }
        return(null);
    }

    public void setResourceESRType(ExtremelySimplifiedResourceTypeEnum resourceESRType) {
        this.resourceESRType = resourceESRType;
    }

    public IdentifierESDT getIdentifierWithType(String identifierType){
        for(IdentifierESDT identifier: this.identifiers){
            if(identifier.getType().contentEquals(identifierType)){
                return(identifier);
            }
        }
        return(null);
    }

    public IdentifierESDT getIdentifierWithType(IdentifierESDTTypesEnum identifierType){
        IdentifierESDT identifier = getIdentifierWithType(identifierType.getIdentifierType());
        return(identifier);
    }

    public void addIdentifier(IdentifierESDT newIdentifier){
        getLogger().info(".addIdentifier(): Entry, newIdentifier --> {}", newIdentifier);
        if(this.identifiers.contains(newIdentifier)){
            return;
        }
        this.identifiers.add(newIdentifier);
    }

    public ExtremelySimplifiedResourceTypeEnum getResourceESRType() {
        return resourceESRType;
    }

    @Override
    public String toString() {
        return "ExtremelySimplifiedResource{" +
                "simplifiedID=" + simplifiedID +
                ", simplifiedIDMetadata=" + simplifiedIDMetadata +
                ", identifiers=" + identifiers +
                ", displayName=" + displayName +
                ", description=" + description +
                ", systemManaged=" + systemManaged +
                ", otherID=" + otherID +
                '}';
    }

//
   // Sorting
   //

    public static Comparator<ExtremelySimplifiedResource> displayNameComparator = new Comparator<ExtremelySimplifiedResource>() {
        @Override
        public int compare(ExtremelySimplifiedResource o1, ExtremelySimplifiedResource o2) {
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

    public static Comparator<ExtremelySimplifiedResource> simplifiedIDComparator = new Comparator<ExtremelySimplifiedResource>() {
        @Override
        public int compare(ExtremelySimplifiedResource o1, ExtremelySimplifiedResource o2) {
            if(o1 == null && o2 == null) {
                return (0);
            }
            if(o1 == null && o2 != null) {
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            if(o1.getSimplifiedID() == null && o2.getSimplifiedID() == null){
                return(0);
            }
            if(o1.getSimplifiedID() == null && o2.getSimplifiedID() != null){
                return(1);
            }
            if(o1.getSimplifiedID() != null && o2.getSimplifiedID() ==  null){
                return(-1);
            }
            String value1 = o1.getSimplifiedID();
            String value2 = o2.getSimplifiedID();
            int comparison = value1.compareTo(value2);
            return(comparison);
        }
    };

    //
    // Identifier Type based Comparator
    //

    public static Comparator<ExtremelySimplifiedResource> identifierShortNameBasedComparator = new Comparator<ExtremelySimplifiedResource>() {
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
            IdentifierESDT practitionerRole1Identifier = o1.getIdentifierWithType("ShortName");
            IdentifierESDT practitionerRole2Identifier = o2.getIdentifierWithType("ShortName");
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

    public static Comparator<ExtremelySimplifiedResource> identifierLongNameTypeComparator = new Comparator<ExtremelySimplifiedResource>() {
        @Override
        public int compare(ExtremelySimplifiedResource o1, ExtremelySimplifiedResource o2) {
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
            IdentifierESDT practitionerRole1Identifier = o1.getIdentifierWithType("LongName");
            IdentifierESDT practitionerRole2Identifier = o2.getIdentifierWithType("LongName");
            if(practitionerRole1Identifier == null && practitionerRole2Identifier == null){
                return(0);
            }
            if(practitionerRole1Identifier == null && practitionerRole2Identifier != null){
                return(1);
            }
            if(practitionerRole1Identifier != null && practitionerRole2Identifier == null){
                return(-1);
            }
            String testValue1 = practitionerRole1Identifier.getValue();
            String testValue2 = practitionerRole2Identifier.getValue();
            int comparison = testValue1.compareTo(testValue2);
            return(comparison);
        }
    };
}
