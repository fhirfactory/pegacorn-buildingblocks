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
 */ package net.fhirfactory.pegacorn.petasos.oam.reporting.tasks.agents;


import net.fhirfactory.pegacorn.core.interfaces.oam.tasks.PetasosITOpsTaskReportingAgentInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.notifications.PetasosComponentITOpsNotification;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.petasos.oam.reporting.tasks.PetasosTaskReportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class TaskReportAgentBase {
    private static final Logger LOG = LoggerFactory.getLogger(TaskReportAgentBase.class);

    private boolean initialised;

    private String participantName;
    private ComponentIdType componentId;
    private PetasosTaskReportFactory reportFactory;
    private PetasosITOpsTaskReportingAgentInterface taskReportingAgent;

    //
    // Constructor(s)
    //

    public TaskReportAgentBase(String participantName, ComponentIdType componentId, PetasosTaskReportFactory reportFactory, PetasosITOpsTaskReportingAgentInterface taskReportingAgent) {
        this.participantName = participantName;
        this.componentId = componentId;
        this.reportFactory = reportFactory;
        this.initialised = false;
        this.taskReportingAgent = taskReportingAgent;
    }

    //
    // Getters and Setters
    //

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public ComponentIdType getComponentId() {
        return componentId;
    }

    public void setComponentId(ComponentIdType componentId) {
        this.componentId = componentId;
    }

    protected PetasosTaskReportFactory getReportFactory(){
        return(this.reportFactory);
    }

    protected Logger getLogger(){
        return(LOG);
    }

    //
    // Abstract Methods
    //

    protected abstract PetasosMonitoredComponentTypeEnum specifyComponentType();

    //
    // Core Method
    //

    public void sendITOpsTaskReport(PetasosActionableTask task){
        getLogger().info(".sendITOpsTaskReport(): Entry");
        PetasosComponentITOpsNotification notification = getReportFactory().newTaskSummaryReport(task);
        notification.setComponentType(specifyComponentType());
        notification.setComponentId(getComponentId());
        notification.setParticipantName(getParticipantName());

        taskReportingAgent.sendTaskReport(notification);
        getLogger().info(".sendITOpsTaskReport(): Exit");
    }

    public void sendITOpsTaskReport(PetasosActionableTask task, List<PetasosActionableTask> newTasks){
        getLogger().info(".sendITOpsTaskReport(): Entry");
        PetasosComponentITOpsNotification notification = getReportFactory().newTaskSummaryReport(task, newTasks);
        notification.setComponentType(specifyComponentType());
        notification.setComponentId(getComponentId());
        notification.setParticipantName(getParticipantName());

        taskReportingAgent.sendTaskReport(notification);
        getLogger().info(".sendITOpsTaskReport(): Exit");
    }
}

