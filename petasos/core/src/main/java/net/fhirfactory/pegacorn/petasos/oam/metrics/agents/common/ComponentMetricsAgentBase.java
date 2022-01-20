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
package net.fhirfactory.pegacorn.petasos.oam.metrics.agents.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.interfaces.oam.notifications.PetasosITOpsNotificationAgentInterface;
import net.fhirfactory.pegacorn.core.interfaces.oam.tasks.PetasosITOpsTaskReportingAgentInterface;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.common.CommonComponentMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.notifications.PetasosComponentITOpsNotification;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import net.fhirfactory.pegacorn.petasos.oam.metrics.cache.PetasosLocalMetricsDM;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;

public abstract class ComponentMetricsAgentBase  {

    private Object metricsDataLock;
    private boolean initialised;

    private static Long ROLLING_AVERAGE_COUNT = 10L;
    private PetasosLocalMetricsDM localMetricsDM;

    private PetasosITOpsNotificationAgentInterface notificationAgent;

    //
    // Constructor(s)
    //

    public ComponentMetricsAgentBase() {
        this.metricsDataLock = new Object();
        initialised = false;
    }

    //
    // Abstract Methods
    //

    abstract protected CommonComponentMetricsData getMetricsData();
    abstract public void sendITOpsNotification(String message);

    //
    // Some Helper Methods
    //

    @JsonIgnore
    public void touchLastActivityInstant(){
        getMetricsData().setLastActivityInstant(Instant.now());
    }

    //
    // Cache Interaction Methods
    //

    public void registerWithCache(){
        getLocalMetricsDM().addNodeMetrics(getMetricsData(), getMetricsDataLock());
    }

    //
    // Getters and Setters
    //

    public Object getMetricsDataLock() {
        return metricsDataLock;
    }

    public Long getRollingAverageCount(){
        return(ROLLING_AVERAGE_COUNT);
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    public void setLocalMetricsDM(PetasosLocalMetricsDM localMetricsDM) {
        this.localMetricsDM = localMetricsDM;
    }

    protected PetasosLocalMetricsDM getLocalMetricsDM(){
        return(this.localMetricsDM);
    }

    protected PetasosITOpsNotificationAgentInterface getNotificationAgent(){
        return(notificationAgent);
    }

    public void setNotificationAgent(PetasosITOpsNotificationAgentInterface notificationAgent) {
        this.notificationAgent = notificationAgent;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "ComponentMetricsAgentBase{" +
                "metricsDataLock=" + metricsDataLock +
                ", initialised=" + initialised +
                ", localMetricsDM=" + localMetricsDM +
                ", rollingAverageCount=" + getRollingAverageCount() +
                '}';
    }
}
