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
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantRoleSupportInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.EndpointMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.common.CommonComponentMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.notifications.PetasosComponentITOpsNotification;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.common.ComponentMetricsAgentBase;
import net.fhirfactory.pegacorn.petasos.oam.metrics.cache.PetasosLocalMetricsDM;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;


public class EndpointMetricsAgent extends ComponentMetricsAgentBase {
    private static final Logger LOG = LoggerFactory.getLogger(EndpointMetricsAgent.class);

    private EndpointMetricsData metricsData;
    private String connectedSystemName;
    private String endpointDescription;

    //
    // Constructors
    //

    public EndpointMetricsAgent(ProcessingPlantRoleSupportInterface processingPlantFunction, PetasosLocalMetricsDM localMetricsDM, ComponentIdType componentId, String participantName, String connectedSystemName, String endpointDescription){
        super();
        getLogger().debug(".EndpointMetricsAgent(): Initialising Endpoint Metrics Agent");
        this.metricsData = new EndpointMetricsData();
        setLocalMetricsDM(localMetricsDM);
        this.metricsData.setParticipantName(participantName);
        this.metricsData.setComponentID(componentId);
        this.metricsData.setComponentStartupInstant(Instant.now());
        this.connectedSystemName = connectedSystemName;
        this.endpointDescription = endpointDescription;
        setProcessingPlantCapabilityStatement(processingPlantFunction);
        getLogger().debug(".EndpointMetricsAgent(): Initialising Endpoint Metrics Agent ->{}", participantName);
    }

    //
    // Implement Superclass Methods
    //

    @Override
    protected CommonComponentMetricsData getMetricsData(){
        return(metricsData);
    }

    public EndpointMetricsData getEndpointMetricsData(){
        return(metricsData);
    }

    //
    // Getters and Setters
    //

    public String getConnectedSystemName() {
        return connectedSystemName;
    }

    public void setConnectedSystemName(String connectedSystemName) {
        this.connectedSystemName = connectedSystemName;
    }

    public String getEndpointDescription() {
        return endpointDescription;
    }

    public void setEndpointDescription(String endpointDescription) {
        this.endpointDescription = endpointDescription;
    }


    //
    // Notifications
    //

    @Override
    public void sendITOpsNotification(String message) {
        try {
            PetasosComponentITOpsNotification notification = new PetasosComponentITOpsNotification();
            notification.setComponentId(getMetricsData().getComponentID());
            notification.setParticipantName(getEndpointMetricsData().getParticipantName());
            notification.setContent(message);
            notification.setComponentType(PetasosMonitoredComponentTypeEnum.PETASOS_MONITORED_COMPONENT_ENDPOINT);
            getNotificationAgent().sendNotification(notification);
            if (getLogger().isInfoEnabled()) {
                getLogger().info(".sendITOpsNotification(): Send Notification->{}", notification);
            }
        } catch(Exception ex){
            getLogger().warn(".sendITOpsNotification(): Can't send notifications, message->{}, stacktrace->{}", ExceptionUtils.getMessage(ex), ExceptionUtils.getStackTrace(ex));
        }
    }

    //
    // Helpers
    //

    @JsonIgnore
    public void incrementRemoteProcedureCallCount(){
        synchronized (getMetricsDataLock()){
            int count = getEndpointMetricsData().getRemoteProcedureCallCount();
            count += 1;
            getEndpointMetricsData().setRemoteProcedureCallCount(count);
        }
    }

    @JsonIgnore
    public void incrementRemoteProcedureCallFailureCount(){
        synchronized (getMetricsDataLock()){
            int count = getEndpointMetricsData().getRemoteProcedureCallFailureCount();
            count += 1;
            getEndpointMetricsData().setRemoteProcedureCallCount(count);
        }
    }

    @JsonIgnore
    public void incrementRemoteProcedureCallHandledCount(){
        synchronized (getMetricsDataLock()){
            int count = getEndpointMetricsData().getRemoteProcedureCallCount();
            count += 1;
            getEndpointMetricsData().setRemoteProcedureCallCount(count);
        }
    }

    @JsonIgnore
    public void incrementEgressSendAttemptCount(){
        synchronized (getMetricsDataLock()){
            int count = getEndpointMetricsData().getEgressSendAttemptCount();
            count += 1;
            getEndpointMetricsData().setEgressSendAttemptCount(count);
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
        return "EndpointMetricsAgent{" +
                "metricsData=" + metricsData +
                ", endpointMetricsData=" + getEndpointMetricsData() +
                ", metricsDataLock=" + getMetricsDataLock() +
                ", rollingAverageCount=" + getRollingAverageCount() +
                ", initialised=" + isInitialised() +
                '}';
    }
}
