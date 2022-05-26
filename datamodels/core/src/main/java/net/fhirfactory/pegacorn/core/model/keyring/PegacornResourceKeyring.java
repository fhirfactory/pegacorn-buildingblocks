/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.keyring;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public class PegacornResourceKeyring implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(PegacornResourceKeyring.class);
    protected Logger getLogger(){
        return(LOG);
    }

    private Identifier primaryBusinessIdentifier;
    private String localId;
    private Map<String, Set<Identifier>> businessIdentifiersMap;
    private Map<String, IdType> sourceSystemKeyMap;
    private String resourceType;

    private static String DEFAULT_SOURCE_SYSTEM_MAP_ENTRY = "SourceSystem.Default";

    //
    // Constructor(s)
    //

    public PegacornResourceKeyring(){
        this.primaryBusinessIdentifier = null;
        this.sourceSystemKeyMap = new HashMap<>();
        this.businessIdentifiersMap = new HashMap<>();
        this.localId = null;
        this.resourceType = null;
    }

    public PegacornResourceKeyring(IdType id, String keyContext){
        this.primaryBusinessIdentifier = null;
        this.localId = null;
        this.businessIdentifiersMap = new HashMap<>();
        this.sourceSystemKeyMap = new HashMap<>();
        this.sourceSystemKeyMap.put(keyContext, id);
        this.resourceType = null;
    }

    public PegacornResourceKeyring(Identifier identifier){
        this.primaryBusinessIdentifier = identifier;
        this.sourceSystemKeyMap = new HashMap<>();
        this.businessIdentifiersMap = new HashMap<>();
        this.localId = null;
        this.resourceType = null;
    }

    public PegacornResourceKeyring(PegacornResourceKeyring ori){
        this.sourceSystemKeyMap = new HashMap<>();
        this.sourceSystemKeyMap.putAll(ori.getSourceSystemKeyMap());
        if(ori.getPrimaryBusinessIdentifier() != null) {
            setPrimaryBusinessIdentifier(ori.getPrimaryBusinessIdentifier());
        }
        if(ori.getLocalId() != null) {
            setLocalId(ori.getLocalId());
        }
        if(ori.getResourceType() != null){
            setResourceType(ori.getResourceType());
        }
    }

    //
    // Getters and Setters
    //

    public boolean hasIdentifierTypeKey(){
        return(this.primaryBusinessIdentifier != null);
    }

    public Identifier getPrimaryBusinessIdentifier() {
        return primaryBusinessIdentifier;
    }

    public void setPrimaryBusinessIdentifier(Identifier primaryBusinessIdentifier) {
        this.primaryBusinessIdentifier = primaryBusinessIdentifier;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public Map<String, IdType> getSourceSystemKeyMap() {
        return sourceSystemKeyMap;
    }

    public void setSourceSystemKeyMap(Map<String, IdType> sourceSystemKeyMap) {
        this.sourceSystemKeyMap = new HashMap<>();
        this.sourceSystemKeyMap.putAll(sourceSystemKeyMap);
    }

    @JsonIgnore
    public void setResourceId(IdType id){
        String mapEntryKey = getDefaultSourceSystemMapEntry();
        getSourceSystemKeyMap().replace(mapEntryKey, id );
    }

    @JsonIgnore
    public IdType getResourceId(){
        if(getSourceSystemKeyMap().isEmpty()){
            return(null);
        }
        if(getSourceSystemKeyMap().containsKey(getDefaultSourceSystemMapEntry())) {
            return (getSourceSystemKeyMap().get(getDefaultSourceSystemMapEntry()));
        }
        for(String mapEntryKey: getSourceSystemKeyMap().keySet()){
            return(getSourceSystemKeyMap().get(mapEntryKey));
        }
        return(null);
    }

    @JsonIgnore
    public void addId(IdType id, String sourceSystem){
        if(getSourceSystemKeyMap().containsKey(sourceSystem)){
            getSourceSystemKeyMap().replace(sourceSystem, id);
        } else {
            getSourceSystemKeyMap().put(sourceSystem, id);
        }
    }

    public Map<String, Set<Identifier>> getBusinessIdentifiersMap() {
        return businessIdentifiersMap;
    }

    public void setBusinessIdentifiersMap(Map<String, Set<Identifier>> businessIdentifiersMap) {
        this.businessIdentifiersMap = businessIdentifiersMap;
    }

    public String getDefaultSourceSystemMapEntry() {
        return DEFAULT_SOURCE_SYSTEM_MAP_ENTRY;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    //
    // Hash and Equals
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PegacornResourceKeyring that = (PegacornResourceKeyring) o;
        return Objects.equals(getPrimaryBusinessIdentifier(), that.getPrimaryBusinessIdentifier()) && Objects.equals(getLocalId(), that.getLocalId()) && Objects.equals(getSourceSystemKeyMap(), that.getSourceSystemKeyMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPrimaryBusinessIdentifier(), getLocalId(), getSourceSystemKeyMap());
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PegacornResourceKeyring{" +
                "primaryBusinessIdentifier=" + primaryBusinessIdentifier +
                ", localId='" + localId + '\'' +
                ", businessIdentifiersMap=" + businessIdentifiersMap +
                ", sourceSystemKeyMap=" + sourceSystemKeyMap +
                ", resourceType='" + resourceType + '\'' +
                '}';
    }
}
