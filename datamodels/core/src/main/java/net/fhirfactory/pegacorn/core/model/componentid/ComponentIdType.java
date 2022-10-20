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
package net.fhirfactory.pegacorn.core.model.componentid;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

public class ComponentIdType implements Serializable {
    private String id;
    private String name;
    private String displayName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant idValidityStartInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant idValidityEndInstant;

    private static final Integer ID_VALIDITY_RANGE_IN_YEARS = 100;

    //
    // Constructor(s)
    //

    public ComponentIdType(){
        this.id = null;
        this.idValidityStartInstant = null;
        this.idValidityEndInstant = null;
        this.displayName = null;
        this.name = null;
    }

    public ComponentIdType(ComponentIdType ori){
        this.id = null;
        this.idValidityStartInstant = null;
        this.idValidityEndInstant = null;
        this.displayName = null;
        this.name = null;
        if(ori.hasId()){
            setId(SerializationUtils.clone(ori.getId()));
        }
        if(ori.hasIdValidityStartInstant()){
            setIdValidityStartInstant(SerializationUtils.clone(ori.getIdValidityStartInstant()));
        }
        if(ori.hasIdValidityEndInstant()){
            setIdValidityEndInstant(SerializationUtils.clone(ori.getIdValidityEndInstant()));
        }
        if(ori.hasDisplayName()){
            setDisplayName(SerializationUtils.clone(ori.getDisplayName()));
        }
        if(ori.hasName()){
            setName(SerializationUtils.clone(ori.getName()));
        }
    }

    //
    // Static Constructor
    //

    public static final ComponentIdType fromComponentName(String name){
        ComponentIdType componentId = new ComponentIdType();
        componentId.setName(name);
        componentId.setIdValidityStartInstant(Instant.now());
        componentId.setIdValidityEndInstant(ZonedDateTime.now().plusYears(ID_VALIDITY_RANGE_IN_YEARS).toInstant());
        UUID uniqueId = UUID.randomUUID();
        String msbString = Long.toHexString(uniqueId.getMostSignificantBits());
        String lsbString = Long.toHexString(uniqueId.getLeastSignificantBits());
        componentId.setId(msbString + lsbString);
        componentId.setDisplayName(name + "::" + componentId.getId());
        return(componentId);
    }

    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasName(){
        boolean hasValue = this.name != null;
        return(hasValue);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public boolean hasId(){
        boolean hasValue = this.id != null;
        return(hasValue);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public boolean hasDisplayName(){
        boolean hasValue = this.displayName != null;
        return(hasValue);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore
    public boolean hasIdValidityStartInstant(){
        boolean hasValue = this.idValidityStartInstant != null;
        return(hasValue);
    }

    public Instant getIdValidityStartInstant() {
        return idValidityStartInstant;
    }

    public void setIdValidityStartInstant(Instant idValidityStartInstant) {
        this.idValidityStartInstant = idValidityStartInstant;
    }

    @JsonIgnore
    public boolean hasIdValidityEndInstant(){
        boolean hasValue = this.idValidityEndInstant != null;
        return(hasValue);
    }

    public Instant getIdValidityEndInstant() {
        return idValidityEndInstant;
    }

    public void setIdValidityEndInstant(Instant idValidityEndInstant) {
        this.idValidityEndInstant = idValidityEndInstant;
    }

    //
    // Equals and Hashcodes
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComponentIdType)) return false;
        ComponentIdType that = (ComponentIdType) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    //
    // To String
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ComponentIdType{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append(", idValidityStartInstant=").append(idValidityStartInstant);
        sb.append(", idValidityEndInstant=").append(idValidityEndInstant);
        sb.append('}');
        return sb.toString();
    }
}
