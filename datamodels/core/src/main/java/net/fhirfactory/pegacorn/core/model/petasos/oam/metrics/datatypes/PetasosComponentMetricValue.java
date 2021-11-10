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
package net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.Date;

public class PetasosComponentMetricValue {
    private Object object;
    private Class objectType;

    //
    // Constructor(s)
    //

    public PetasosComponentMetricValue(){
        this.object = null;
        this.objectType = null;
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasObject(){
        boolean hasValue = this.object != null;
        return(hasValue);
    }

    @JsonIgnore
    public boolean hasObjectType(){
        boolean hasValue = this.objectType != null;
        return(hasValue);
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Class getObjectType() {
        return objectType;
    }

    public void setObjectType(Class objectType) {
        this.objectType = objectType;
    }

    //
    // Type Based Accessors
    //

    @JsonIgnore
    public String getStringValue(){
        if(hasObject()) {
            if (objectType.equals(String.class)) {
                String stringValue = (String) (this.object);
                return (stringValue);
            }
        }
        return(null);
    }

    @JsonIgnore
    public void setStringValue(String stringValue){
        this.object = stringValue;
        this.objectType = String.class;
    }

    @JsonIgnore
    public Integer getIntegerValue(){
        if(hasObject()) {
            if (objectType.equals(Integer.class)) {
                Integer integerValue = (Integer) (this.object);
                return (integerValue);
            }
        }
        return(null);
    }

    @JsonIgnore
    public void setIntegerValue(Integer integerValue){
        this.object = integerValue;
        this.objectType = Integer.class;
    }

    @JsonIgnore
    public Long getLongValue(){
        if(hasObject()) {
            if (objectType.equals(Long.class)) {
                Long longValue = (Long) (this.object);
                return (longValue);
            }
        }
        return(null);
    }

    @JsonIgnore
    public void setLongValue(Long longValue){
        this.object = longValue;
        this.objectType = Long.class;
    }

    @JsonIgnore
    public Instant getInstantValue(){
        if(hasObject()) {
            if (objectType.equals(Instant.class)) {
                Instant instantValue = (Instant) (this.object);
                return (instantValue);
            }
        }
        return(null);
    }

    @JsonIgnore
    public void setInstantValue(Instant instantValue){
        this.object = instantValue;
        this.objectType = Instant.class;
    }

    @JsonIgnore
    public Date getDateValue(){
        if(hasObject()) {
            if (objectType.equals(Date.class)) {
                Date dateValue = (Date)(this.object);
                return(dateValue);
            }
        }
        return(null);
    }

    @JsonIgnore
    public Double getDoubleValue(){
        if(hasObject()){
            if(objectType.equals(Double.class)){
                Double doubleValue = (Double)(this.object);
                return(doubleValue);
            }
        }
        return(null);
    }

    @JsonIgnore
    public void setDoubleValue(Double doubleValue){
        this.object = doubleValue;
        this.objectType = Double.class;
    }

    @JsonIgnore
    public Boolean getBooleanValue(){
        if(hasObject()){
            if(objectType.equals(Boolean.class)){
                Boolean booleanValue = (Boolean)(this.object);
                return(booleanValue);
            }
        }
        return(null);
    }

    @JsonIgnore
    public void setBooleanValue(Boolean booleanValue){
        this.object = booleanValue;
        this.objectType = Boolean.class;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosComponentMetricValue{" +
                "object=" + object +
                ", objectType=" + objectType +
                '}';
    }
}
