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
package net.fhirfactory.pegacorn.core.model.petasos.oam.metrics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.datatypes.PetasosComponentMetricValue;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.datatypes.PetasosComponentMetricTimestamp;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.valuesets.PetasosComponentMetricTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.valuesets.PetasosComponentMetricUnitEnum;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class PetasosComponentMetric implements Serializable {
    private String metricName;
    private PetasosComponentMetricTypeEnum metricType;
    private PetasosComponentMetricUnitEnum metricUnit;
    private PetasosComponentMetricTimestamp metricTimestamp;
    private PetasosComponentMetricValue metricValue;
    private ComponentIdType metricSource;
    private ComponentIdType metricAgent;

    //
    // Constructor(s)
    //

    public PetasosComponentMetric(){
        setMetricName(null);
        setMetricAgent(null);
        setMetricUnit(null);
        setMetricTimestamp(null);
        setMetricType(null);
        setMetricSource(null);
        setMetricAgent(null);
    }

    public PetasosComponentMetric(PetasosComponentMetric ori){
        setMetricName(null);
        setMetricAgent(null);
        setMetricUnit(null);
        setMetricTimestamp(null);
        setMetricType(null);
        setMetricSource(null);
        setMetricAgent(null);
        //
        // populate (clone first) from ori
        if(ori.hasMetricAgent()){
            setMetricAgent(SerializationUtils.clone(ori.getMetricAgent()));
        }
        if(ori.hasMetricName()){
            setMetricName(SerializationUtils.clone(ori.getMetricName()));
        }
        if(ori.hasMetricTimestamp()){
            setMetricTimestamp(SerializationUtils.clone(ori.getMetricTimestamp()));
        }
        if(ori.hasMetricUnit()){
            setMetricUnit(SerializationUtils.clone(ori.getMetricUnit()));
        }
        if(ori.hasMetricType()){
            setMetricType(SerializationUtils.clone(ori.getMetricType()));
        }
        if(ori.hasMetricSource()){
            setMetricSource(SerializationUtils.clone(ori.getMetricSource()));
        }
        if(ori.hasMetricAgent()){
            setMetricAgent(SerializationUtils.clone(ori.getMetricAgent()));
        }
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasMetricName(){
        boolean hasValue = this.metricName != null;
        return(hasValue);
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    @JsonIgnore
    public boolean hasMetricType(){
        boolean hasValue = this.metricType != null;
        return(hasValue);
    }

    public PetasosComponentMetricTypeEnum getMetricType() {
        return metricType;
    }

    public void setMetricType(PetasosComponentMetricTypeEnum metricType) {
        this.metricType = metricType;
    }

    @JsonIgnore
    public boolean hasMetricUnit(){
        boolean hasValue = this.metricUnit != null;
        return(hasValue);
    }

    public PetasosComponentMetricUnitEnum getMetricUnit() {
        return metricUnit;
    }

    public void setMetricUnit(PetasosComponentMetricUnitEnum metricUnit) {
        this.metricUnit = metricUnit;
    }

    @JsonIgnore
    public boolean hasMetricTimestamp(){
        boolean hasValue = this.metricTimestamp != null;
        return(hasValue);
    }

    public PetasosComponentMetricTimestamp getMetricTimestamp() {
        return metricTimestamp;
    }

    public void setMetricTimestamp(PetasosComponentMetricTimestamp metricTimestamp) {
        this.metricTimestamp = metricTimestamp;
    }

    @JsonIgnore
    public boolean hasMetricValue(){
        boolean hasValue = this.metricValue != null;
        return(hasValue);
    }

    public PetasosComponentMetricValue getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(PetasosComponentMetricValue metricValue) {
        this.metricValue = metricValue;
    }

    @JsonIgnore
    public boolean hasMetricSource(){
        boolean hasValue = this.metricSource != null;
        return(hasValue);
    }

    public ComponentIdType getMetricSource() {
        return metricSource;
    }

    public void setMetricSource(ComponentIdType metricSource) {
        this.metricSource = metricSource;
    }

    @JsonIgnore
    public boolean hasMetricAgent(){
        boolean hasValue = this.metricAgent != null;
        return(hasValue);
    }

    public ComponentIdType getMetricAgent() {
        return metricAgent;
    }

    public void setMetricAgent(ComponentIdType metricAgent) {
        this.metricAgent = metricAgent;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosComponentMetric{" +
                "metricName='" + metricName + '\'' +
                ", metricType=" + metricType +
                ", metricUnit=" + metricUnit +
                ", metricTimestamp=" + metricTimestamp +
                ", metricValue=" + metricValue +
                ", metricSource=" + metricSource +
                ", metricAgent=" + metricAgent +
                '}';
    }
}
