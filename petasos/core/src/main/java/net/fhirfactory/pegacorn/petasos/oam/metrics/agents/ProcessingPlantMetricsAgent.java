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
package net.fhirfactory.pegacorn.petasos.oam.metrics.agents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.ProcessingPlantMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.common.CommonComponentMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.notifications.PetasosComponentITOpsNotification;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.common.ComponentMetricsAgentBase;
import net.fhirfactory.pegacorn.petasos.oam.metrics.cache.PetasosLocalMetricsDM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;



public class ProcessingPlantMetricsAgent extends ComponentMetricsAgentBase {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPlantMetricsAgent.class);

    public static final String PROCESSING_PLANT_METRICS_TYPE = "ProcessingPlantBasedMetrics";

    private ProcessingPlantMetricsData metricsData;


    //
    // Constructors
    //

    public ProcessingPlantMetricsAgent(PetasosLocalMetricsDM localMetricsDM, ComponentIdType componentId, String participantName){
        super();
        this.metricsData = new ProcessingPlantMetricsData();
        setLocalMetricsDM(localMetricsDM);
        this.metricsData.setParticipantName(participantName);
        this.metricsData.setComponentID(componentId);
        this.metricsData.setComponentStartupInstant(Instant.now());
        getLogger().info(".WorkUnitProcessorMetricsAgent(): Initialising Working Unit Processor Metrics Agent ->{}", participantName);
    }

    //
    // Implement Superclass Methods
    //

    @Override
    protected CommonComponentMetricsData getMetricsData(){
        return(metricsData);
    }

    public ProcessingPlantMetricsData getProcessingPlantMetricsData(){
        return(metricsData);
    }

    //
    // Notifications
    //

    @Override
    public void sendITOpsNotification(String message) {
        PetasosComponentITOpsNotification notification = new PetasosComponentITOpsNotification();
        notification.setComponentId(getMetricsData().getComponentID());
        notification.setProcessingPlantParticipantName(getProcessingPlantMetricsData().getParticipantName());
        notification.setContent(message);
        notification.setComponentType(PetasosMonitoredComponentTypeEnum.PETASOS_MONITORED_COMPONENT_SUBSYSTEM);
        getNotificationAgent().sendNotification(notification);
    }

    //
    // Helpers
    //

    public void updateLocalCacheStatus(String cacheName, String cacheStatus){
        if(StringUtils.isEmpty(cacheName) || StringUtils.isEmpty(cacheStatus)){
            return;
        }
        synchronized (getMetricsDataLock()) {
            if (getProcessingPlantMetricsData().getLocalCacheStatusMap().containsKey(cacheName)) {
                getProcessingPlantMetricsData().getLocalCacheStatusMap().remove(cacheName);
            }
            getProcessingPlantMetricsData().getLocalCacheStatusMap().put(cacheName, cacheStatus);
        }
    }

    public void incrementSynchronousAuditEventWritten(){
        synchronized (getMetricsDataLock()) {
            int count = getProcessingPlantMetricsData().getSynchronousAuditEventsWritten();
            count += 1;
            getProcessingPlantMetricsData().setSynchronousAuditEventsWritten(count);
        }
    }

    public void incrementAsynchronousAuditEventWritten(){
        synchronized (getMetricsDataLock()) {
            int count = getProcessingPlantMetricsData().getAsynchronousAuditEventsWritten();
            count += 1;
            getProcessingPlantMetricsData().setAsynchronousAuditEventsWritten(count);
        }
    }

    public void touchAsynchronousAuditEventWrite(){
        synchronized (getMetricsDataLock()) {
            getProcessingPlantMetricsData().setLastAsynchronousAuditEventWrite(Instant.now());
        }
    }

    @JsonIgnore
    public void incrementIngresMessageCount(){
        synchronized (getMetricsDataLock()) {
            int count = getProcessingPlantMetricsData().getIngresMessageCount();
            count += 1;
            getProcessingPlantMetricsData().setIngresMessageCount(count);
        }
    }

    @JsonIgnore
    public void incrementEgressMessageCount(){
        synchronized (getMetricsDataLock()) {
            int count = getProcessingPlantMetricsData().getEgressMessageCount();
            count += 1;
            getProcessingPlantMetricsData().setEgressMessageCount(count);
        }
    }

    @JsonIgnore
    public void incrementDistributedMessageCount(){
        synchronized (getMetricsDataLock()) {
            int count = getProcessingPlantMetricsData().getDistributedMessageCount();
            count += 1;
            getProcessingPlantMetricsData().setDistributedMessageCount(count);
        }
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger(){
        return(LOG);
    }


    //
    // To String
    //

    @Override
    public String toString() {
        return "ProcessingPlantMetricsAgent{" +
                "metricsData=" + metricsData +
                ", metricsDataLock=" + getMetricsDataLock() +
                ", rollingAverageCount=" + getRollingAverageCount() +
                '}';
    }
}
