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
package net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;

import java.io.Serializable;
import java.util.HashMap;

public class PetasosComponentMetricSet implements Serializable {
    private HashMap<String, PetasosComponentMetric> metrics;
    private ComponentIdType metricSourceComponentId;

    private PetasosMonitoredComponentTypeEnum componentType;
    private String sourceProcessingPlantParticipantName;
    private String sourceWorkshopParticipantName;
    private String sourceWorkUnitProcessorParticipantName;

    //
    // Contructor(s)
    //

    public PetasosComponentMetricSet(){
        this.metrics = new HashMap<>();
        this.metricSourceComponentId = null;
        this.sourceWorkUnitProcessorParticipantName = null;
    }

    public PetasosComponentMetricSet(ComponentIdType sourceId, PetasosMonitoredComponentTypeEnum componentType){
        metrics = new HashMap<>();
        this.metricSourceComponentId = sourceId;
        this.sourceWorkUnitProcessorParticipantName = null;
        this.componentType = componentType;
    }

    //
    // Getters and Setters
    //

    public String getSourceProcessingPlantParticipantName() {
        return sourceProcessingPlantParticipantName;
    }

    public void setSourceProcessingPlantParticipantName(String sourceProcessingPlantParticipantName) {
        this.sourceProcessingPlantParticipantName = sourceProcessingPlantParticipantName;
    }

    public String getSourceWorkshopParticipantName() {
        return sourceWorkshopParticipantName;
    }

    public void setSourceWorkshopParticipantName(String sourceWorkshopParticipantName) {
        this.sourceWorkshopParticipantName = sourceWorkshopParticipantName;
    }

    public HashMap<String, PetasosComponentMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(HashMap<String, PetasosComponentMetric> metrics) {
        this.metrics = metrics;
    }

    public ComponentIdType getMetricSourceComponentId() {
        return metricSourceComponentId;
    }

    public void setMetricSourceComponentId(ComponentIdType metricSourceComponentId) {
        this.metricSourceComponentId = metricSourceComponentId;
    }

    public String getSourceWorkUnitProcessorParticipantName() {
        return sourceWorkUnitProcessorParticipantName;
    }

    public void setSourceWorkUnitProcessorParticipantName(String sourceWorkUnitProcessorParticipantName) {
        this.sourceWorkUnitProcessorParticipantName = sourceWorkUnitProcessorParticipantName;
    }

    public PetasosMonitoredComponentTypeEnum getComponentType() {
        return componentType;
    }

    public void setComponentType(PetasosMonitoredComponentTypeEnum componentType) {
        this.componentType = componentType;
    }

    //
    // Other Helper Methods
    //

    @JsonIgnore
    public PetasosComponentMetric getMetric(String name){
        if(getMetrics().containsKey(name)){
            PetasosComponentMetric foundMetric = getMetrics().get(name);
            return(foundMetric);
        }
        return(null);
    }

    @JsonIgnore
    public void addMetric(PetasosComponentMetric metric){
        if(metric == null){
            return;
        }
        if(getMetrics().containsKey(metric.getMetricName())){
            getMetrics().remove(metric.getMetricName());
        }
        getMetrics().put(metric.getMetricName(), metric);
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosComponentMetricSet{" +
                "metrics=" + metrics +
                ", metricSourceComponentId=" + metricSourceComponentId +
                ", componentType=" + componentType +
                ", sourceProcessingPlantParticipantName='" + sourceProcessingPlantParticipantName + '\'' +
                ", sourceWorkshopParticipantName='" + sourceWorkshopParticipantName + '\'' +
                ", sourceWorkUnitProcessorParticipantName='" + sourceWorkUnitProcessorParticipantName + '\'' +
                '}';
    }
}
