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
package net.fhirfactory.pegacorn.petasos.oam.subscriptions.cache;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.PetasosProcessingPlantSubscriptionSummary;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.PetasosSubscriptionSummaryReport;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.PetasosWorkUnitProcessorSubscriptionSummary;
import net.fhirfactory.pegacorn.petasos.oam.common.LocalOAMCacheBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class PetasosLocalSubscriptionReportingDM extends LocalOAMCacheBase {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosLocalSubscriptionReportingDM.class);

    // ConcurrentHashMap<componentID, PetasosProcessingPlantSubscriptionSummary>
    private ConcurrentHashMap<ComponentIdType, PetasosProcessingPlantSubscriptionSummary> processingPlantSubscriptionSummarySet;
    private Object processingPlantMapLock;
    // ConcurrentHashMap<componentID, PetasosWorkUnitProcessorSubscriptionSummary>
    private ConcurrentHashMap<ComponentIdType, PetasosWorkUnitProcessorSubscriptionSummary> workUnitProcessorSubscriptionSummarySet;
    private Object wupMapLock;

    public PetasosLocalSubscriptionReportingDM(){
        this.processingPlantSubscriptionSummarySet = new ConcurrentHashMap<>();
        this.workUnitProcessorSubscriptionSummarySet = new ConcurrentHashMap<>();
        this.processingPlantMapLock = new Object();
        this.wupMapLock = new Object();
    }

    //
    // Publisher Subscription Traceability
    //

    public void addProcessingPlantSubscriptionSummary(PetasosProcessingPlantSubscriptionSummary summary){
        LOG.debug(".addProcessingPlantSubscriptionSummary(): Entry");
        synchronized (processingPlantMapLock) {
            if (processingPlantSubscriptionSummarySet.containsKey(summary.getComponentID())) {
                processingPlantSubscriptionSummarySet.remove(summary.getComponentID());
            }
            processingPlantSubscriptionSummarySet.put(summary.getComponentID(), summary);
        }
        refreshCurrentStateUpdateInstant();
        LOG.debug(".addProcessingPlantSubscriptionSummary(): Exit");
    }

    public void addWorkUnitProcessorSubscriptionSummary(PetasosWorkUnitProcessorSubscriptionSummary summary){
        LOG.debug(".addWorkUnitProcessorSubscriptionSummary(): Entry");
        synchronized (wupMapLock) {
            if (workUnitProcessorSubscriptionSummarySet.containsKey(summary.getSubscriber())) {
                workUnitProcessorSubscriptionSummarySet.remove(summary.getSubscriber());
            }
            workUnitProcessorSubscriptionSummarySet.put(summary.getSubscriber(), summary);
        }
        refreshCurrentStateUpdateInstant();
        LOG.debug(".addWorkUnitProcessorSubscriptionSummary(): Exit");
    }

    public PetasosSubscriptionSummaryReport getPubSubReport(){
        LOG.debug(".getPubSubReport(): Entry");
        PetasosSubscriptionSummaryReport report = new PetasosSubscriptionSummaryReport();
        synchronized (wupMapLock) {
            for (PetasosWorkUnitProcessorSubscriptionSummary currentSummary : this.workUnitProcessorSubscriptionSummarySet.values()) {
                LOG.trace(".getPubSubReport(): Adding summary->{}", currentSummary);
                report.addWorkUnitProcessorSubscriptionSummary(currentSummary);
            }
        }
        synchronized(processingPlantMapLock){
            for(PetasosProcessingPlantSubscriptionSummary currentSummary: this.processingPlantSubscriptionSummarySet.values()){
                report.addProcessingPlantSubscriptionSummary(currentSummary);
            }
        }
        report.setTimestamp(getCurrentStateUpdateInstant());
        LOG.debug(".getPubSubReport(): Eixt");
        return(report);
    }
}
