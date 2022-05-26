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
package net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.reporting;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PetasosSubscriptionSummaryReport implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant timestamp;
    private ComponentIdType processingPlantComponentID;
    private Map<ComponentIdType, PetasosProcessingPlantSubscriptionSummary> processingPlantSubscriptionSummarySet;
    private Map<ComponentIdType, PetasosWorkUnitProcessorSubscriptionSummary> wupSubscriptionSummarySet;

    //
    // Constructor(s)
    //

    public PetasosSubscriptionSummaryReport(){
        this.timestamp = Instant.now();
        wupSubscriptionSummarySet = new HashMap<>();
        processingPlantSubscriptionSummarySet = new HashMap<>();
    }

    //
    // Getters and Setters
    //

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public ComponentIdType getProcessingPlantComponentID() {
        return processingPlantComponentID;
    }

    public void setProcessingPlantComponentID(ComponentIdType processingPlantComponentID) {
        this.processingPlantComponentID = processingPlantComponentID;
    }

    public Map<ComponentIdType, PetasosProcessingPlantSubscriptionSummary> getProcessingPlantSubscriptionSummarySet() {
        return processingPlantSubscriptionSummarySet;
    }

    public void setProcessingPlantSubscriptionSummarySet(Map<ComponentIdType, PetasosProcessingPlantSubscriptionSummary> processingPlantSubscriptionSummarySet) {
        this.processingPlantSubscriptionSummarySet = processingPlantSubscriptionSummarySet;
    }

    public Map<ComponentIdType, PetasosWorkUnitProcessorSubscriptionSummary> getWupSubscriptionSummarySet() {
        return wupSubscriptionSummarySet;
    }

    public void setWupSubscriptionSummarySet(Map<ComponentIdType, PetasosWorkUnitProcessorSubscriptionSummary> wupSubscriptionSummarySet) {
        this.wupSubscriptionSummarySet = wupSubscriptionSummarySet;
    }

    public void addWorkUnitProcessorSubscriptionSummary(PetasosWorkUnitProcessorSubscriptionSummary wupSummary){
        if(wupSubscriptionSummarySet.containsKey(wupSummary.getComponentID())){
            wupSubscriptionSummarySet.remove(wupSummary.getComponentID());
        }
        wupSubscriptionSummarySet.put(wupSummary.getComponentID(), wupSummary);
    }

    public void addProcessingPlantSubscriptionSummary(PetasosProcessingPlantSubscriptionSummary summary){
        if(processingPlantSubscriptionSummarySet.containsKey(summary.getComponentID())){
            processingPlantSubscriptionSummarySet.remove(summary.getComponentID());
        }
        processingPlantSubscriptionSummarySet.put(summary.getComponentID(), summary);
    }

    //
    // To String
    //


    @Override
    public String toString() {
        return "PetasosSubscriptionSummaryReport{" +
                "timestamp=" + timestamp +
                ", processingPlantComponentID=" + processingPlantComponentID +
                ", processingPlantSubscriptionSummarySet=" + processingPlantSubscriptionSummarySet +
                ", wupSubscriptionSummarySet=" + wupSubscriptionSummarySet +
                '}';
    }
}
