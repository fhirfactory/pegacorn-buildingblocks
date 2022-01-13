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
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.WorkUnitProcessorMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.common.CommonComponentMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.notifications.PetasosComponentITOpsNotification;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.common.ComponentMetricsAgentBase;
import net.fhirfactory.pegacorn.petasos.oam.metrics.cache.PetasosLocalMetricsDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;


public class WorkUnitProcessorMetricsAgent extends ComponentMetricsAgentBase {
    private static final Logger LOG = LoggerFactory.getLogger(WorkUnitProcessorMetricsAgent.class);

    public static final String WORK_UNIT_PROCESSOR_METRICS_TYPE = "WorkUnitProcessorBasedMetrics";

    private WorkUnitProcessorMetricsData metricsData;


    //
    // Constructor(s)
    //

    public WorkUnitProcessorMetricsAgent(PetasosLocalMetricsDM localMetricsDM, ComponentIdType componentId, String participantName){
        super();
        this.metricsData = new WorkUnitProcessorMetricsData();
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

    public WorkUnitProcessorMetricsData getWUPMetricsData(){
        return(metricsData);
    }

    //
    // Notifications
    //

    @Override
    public void sendITOpsNotification(String message) {
        PetasosComponentITOpsNotification notification = new PetasosComponentITOpsNotification();
        notification.setComponentId(getMetricsData().getComponentID());
        notification.setProcessingPlantParticipantName(getWUPMetricsData().getProcessingPlantParticipantName());
        notification.setWorkshopParticipantName(getWUPMetricsData().getWorkshopParticipantName());
        notification.setWorkUnitProcessorParticipantName(getWUPMetricsData().getParticipantName());
        notification.setComponentType(PetasosMonitoredComponentTypeEnum.PETASOS_MONITORED_COMPONENT_WORK_UNIT_PROCESSOR);
        notification.setContent(message);
        getNotificationAgent().sendNotification(notification);
    }

    //
    // Some Helper Methods
    //

    @JsonIgnore
    public void incrementDistributedMessageEndpointCount(String targetName){
        synchronized (getMetricsDataLock()) {
            if (!getWUPMetricsData().getDistributionCountMap().containsKey(targetName)) {
                getWUPMetricsData().getDistributionCountMap().put(targetName, 1);
            } else {
                Integer currentValue = getWUPMetricsData().getDistributionCountMap().get(targetName);
                currentValue += 1;
                getWUPMetricsData().getDistributionCountMap().remove(targetName);
                getWUPMetricsData().getDistributionCountMap().put(targetName, currentValue);
            }
        }
    }

    @JsonIgnore
    public void incrementIngresMessageCount(){
        synchronized (getMetricsDataLock()) {
            int count = getWUPMetricsData().getIngresMessageCount();
            count += 1;
            getWUPMetricsData().setIngresMessageCount(count);
        }
    }

    @JsonIgnore
    public void incrementEgressMessageCount(){
        synchronized (getMetricsDataLock()) {
            int count = getWUPMetricsData().getEgressMessageCount();
            count += 1;
            getWUPMetricsData().setEgressMessageCount(count);
        }
    }

    @JsonIgnore
    public void incrementDistributedMessageCount(){
        synchronized (getMetricsDataLock()) {
            int count = getWUPMetricsData().getDistributedMessageCount();
            count += 1;
            getWUPMetricsData().setDistributedMessageCount(count);
        }
    }

    @JsonIgnore
    public void incrementFinalisedTasks(){
        synchronized (getMetricsDataLock()) {
            int count = getWUPMetricsData().getFinalisedTasks();
            count += 1;
            getWUPMetricsData().setFinalisedTasks(count);
        }
    }

    @JsonIgnore
    public void incrementFinishedTasks(){
        synchronized (getMetricsDataLock()) {
            int count = getWUPMetricsData().getFinishedTasks();
            count += 1;
            getWUPMetricsData().setFinishedTasks(count);
        }
    }

    @JsonIgnore
    public void incrementFailedTasks(){
        synchronized (getMetricsDataLock()){
            int count = getWUPMetricsData().getFailedTasks();
            count += 1;
            getWUPMetricsData().setFailedTasks(count);
        }
    }

    @JsonIgnore
    public void incrementStartedTasks(){
        synchronized (getMetricsDataLock()){
            int count = getWUPMetricsData().getStartedTasks();
            count += 1;
            getWUPMetricsData().setStartedTasks(count);
        }
    }

    @JsonIgnore
    public void incrementRegisteredTasks(){
        synchronized (getMetricsDataLock()){
            int count = getWUPMetricsData().getRegisteredTasks();
            count += 1;
            getWUPMetricsData().setRegisteredTasks(count);
        }
    }

    @JsonIgnore
    public void incrementCancelledTasks(){
        synchronized (getMetricsDataLock()){
            int count = getWUPMetricsData().getCancelledTasks();
            count += 1;
            getWUPMetricsData().setCancelledTasks(count);
        }
    }

    @JsonIgnore
    public void updateAverageEventProcessingDuration(double newDuration){
        synchronized (getMetricsDataLock()) {
            double currentAverage = getWUPMetricsData().getAverageEventProcessingDuration();
            if (currentAverage <= 0) {
                getWUPMetricsData().setAverageEventProcessingDuration(newDuration);
            } else {
                double aggregateSum = (getWUPMetricsData().getIngresMessageCount() * currentAverage) + newDuration;
                double newAverage = aggregateSum / (getWUPMetricsData().getIngresMessageCount() + 1.0);
                getWUPMetricsData().setAverageEventProcessingDuration(newAverage);
            }
        }
    }

    @JsonIgnore
    public void updateRollingEventProcessingDuration(double newDuration){
        synchronized (getMetricsDataLock()) {
            double currentAverage = getWUPMetricsData().getRollingEventProcessingDuration();
            if (currentAverage <= 0) {
                getWUPMetricsData().setRollingEventProcessingDuration(newDuration);
            } else {
                double aggregateSum = (currentAverage * (getRollingAverageCount() - 1.0)) + newDuration;
                double newAverage = aggregateSum / (getRollingAverageCount());
                getWUPMetricsData().setRollingAverageEventDistributionDuration(newAverage);
            }
        }
    }

    @JsonIgnore
    public void touchLastActivityStartInstant(){
        synchronized (getMetricsDataLock()) {
            getWUPMetricsData().setEventProcessingStartInstant(Instant.now());
        }
    }

    @JsonIgnore
    public void touchEventDistributionStartInstant(){
        synchronized (getMetricsDataLock()) {
            getWUPMetricsData().setEventDistributionStartInstant(Instant.now());
        }
    }

    @JsonIgnore
    public void touchEventDistributionFinishInstant(){
        double duration = 0.0;
        synchronized (getMetricsDataLock()) {
            getWUPMetricsData().setEventDistributionFinishInstant(Instant.now());
            double finishTime = (double) getWUPMetricsData().getEventDistributionFinishInstant().getEpochSecond() + (double) getWUPMetricsData().getEventDistributionFinishInstant().getNano() / 1000000000.0;
            double startTime = (double) getWUPMetricsData().getEventDistributionStartInstant().getEpochSecond() + (double) getWUPMetricsData().getEventDistributionStartInstant().getNano() / 1000000000.0;
            duration = finishTime - startTime;
        }
        updateRollingAverageEventEventDistributionDuration(duration);
        updateAverageEventDistributionDuration(duration);
    }

    @JsonIgnore
    public void updateRollingAverageEventEventDistributionDuration(double newDuration){
        synchronized (getMetricsDataLock()) {
            double currentAverage = getWUPMetricsData().getRollingAverageEventDistributionDuration();
            if (currentAverage <= 0) {
                getWUPMetricsData().setRollingAverageEventDistributionDuration(newDuration);
            } else {
                double aggregateSum = (currentAverage * ((double) getRollingAverageCount() - 1.0)) + newDuration;
                double newAverage = aggregateSum / (getRollingAverageCount());
                getWUPMetricsData().setRollingAverageEventDistributionDuration(newAverage);
            }
        }
    }

    @JsonIgnore
    public void updateAverageEventDistributionDuration(double newDuration){
        synchronized (getMetricsDataLock()) {
            double currentAverage = getWUPMetricsData().getAverageEventDistributionDuration();
            double currentDistributionCount = (double) getWUPMetricsData().getDistributedMessageCount();
            if ((currentDistributionCount <= 0) || (currentAverage == 0)) {
                getWUPMetricsData().setAverageEventDistributionDuration(newDuration);
            } else {
                double aggregateSum = (currentAverage * (currentDistributionCount - 1.0)) + newDuration;
                double newAverage = aggregateSum / (currentDistributionCount);
                getWUPMetricsData().setAverageEventDistributionDuration(newAverage);
            }
        }
    }

    @JsonIgnore
    public void touchLastActivityFinishInstant(){
        double duration = 0.0;
        synchronized (getMetricsDataLock()) {
            getWUPMetricsData().setEventProcessingFinishInstant(Instant.now());
            double finishTime = (double) getWUPMetricsData().getEventProcessingFinishInstant().getEpochSecond() + (double) getWUPMetricsData().getEventProcessingFinishInstant().getNano() / 1000000000.0;
            double startTime = (double) getWUPMetricsData().getEventProcessingStartInstant().getEpochSecond() + (double) getWUPMetricsData().getEventProcessingStartInstant().getNano() / 1000000000.0;
            duration = finishTime - startTime;
            getWUPMetricsData().setLastEventProcessingDuration(duration);
        }
        updateAverageEventProcessingDuration(duration);
        updateRollingEventProcessingDuration(duration);
    }

    //
    // Getters (and Setters)
    //

    public void setMetricsData(WorkUnitProcessorMetricsData metricsData) {
        this.metricsData = metricsData;
    }

    protected Logger getLogger(){
        return(LOG);
    }

    //
    // To String Method(s)
    //

    @Override
    public String toString() {
        return "WorkUnitProcessorMetricsAgent{" +
                "metricsData=" + metricsData +
                ", metricsDataLock=" + getMetricsDataLock() +
                ", rollingAverageCount=" + getRollingAverageCount() +
                '}';
    }
}
