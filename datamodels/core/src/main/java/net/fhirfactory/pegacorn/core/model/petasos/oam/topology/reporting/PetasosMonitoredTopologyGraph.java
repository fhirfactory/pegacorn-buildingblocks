
/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.petasos.oam.topology.reporting;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.ProcessingPlantSummary;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PetasosMonitoredTopologyGraph implements Serializable {
    private String deploymentName;
    private Map<String, ProcessingPlantSummary> processingPlants;

    //
    // Constructor(s)
    //

    public PetasosMonitoredTopologyGraph(){
        processingPlants = new ConcurrentHashMap<>();
    }

    //
    // Getters and Setters
    //

    public Map<String, ProcessingPlantSummary> getProcessingPlants() {
        return processingPlants;
    }

    public void setProcessingPlants(Map<String, ProcessingPlantSummary> processingPlants) {
        this.processingPlants = processingPlants;
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public void addProcessingPlant(ProcessingPlantSummary processingPlant){
        removeProcessingPlant(processingPlant.getComponentID());
        processingPlants.put(processingPlant.getComponentID().getId(), processingPlant);
    }

    public void removeProcessingPlant(ComponentIdType componentID){
        if(processingPlants.containsKey(componentID.getId())){
            removeProcessingPlant(componentID.getId());
        }
    }

    public void removeProcessingPlant(String id){
        if(processingPlants.containsKey(id)){
            processingPlants.remove(id);
        }
    }

    //
    // To String
    //


    @Override
    public String toString() {
        return "PetasosMonitoredTopologyGraph{" +
                "deploymentName='" + deploymentName + '\'' +
                ", processingPlants=" + processingPlants +
                '}';
    }
}
