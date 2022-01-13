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
package net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.common.CommonComponentMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;

import java.time.Instant;

public class WorkUnitProcessorMetricsData extends CommonComponentMetricsData {

    private String processingPlantParticipantName;
    private String workshopParticipantName;

    private double averageEventProcessingDuration;
    private double rollingEventProcessingDuration;
    private double lastEventProcessingDuration;
    private double averageEventDistributionDuration;
    private double rollingAverageEventDistributionDuration;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant eventDistributionStartInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant eventDistributionFinishInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant eventProcessingStartInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant eventProcessingFinishInstant;
    private int finalisedTasks;
    private int finishedTasks;
    private int failedTasks;
    private int startedTasks;
    private int registeredTasks;
    private int cancelledTasks;
    private TaskIdType currentActionableTask;
    private TaskIdType lastActionableTask;
    private int nodeIngresQueueSize;

    //
    // Constructor(s)
    //

    public WorkUnitProcessorMetricsData(){
        super();
        this.averageEventDistributionDuration = -1;
        this.rollingEventProcessingDuration = -1;
        this.lastEventProcessingDuration = -1;
        this.averageEventDistributionDuration = -1;
        this.rollingAverageEventDistributionDuration = -1;
        this.eventDistributionStartInstant = Instant.EPOCH;
        this.eventDistributionFinishInstant = Instant.EPOCH;
        this.eventProcessingStartInstant = Instant.EPOCH;
        this.eventProcessingFinishInstant = Instant.EPOCH;
        this.finalisedTasks = 0;
        this.finishedTasks = 0;
        this.failedTasks = 0;
        this.startedTasks = 0;
        this.registeredTasks = 0;
        this.cancelledTasks = 0;
        this.currentActionableTask = null;
        this.lastActionableTask = null;
        this.nodeIngresQueueSize = 0;
    }

    public WorkUnitProcessorMetricsData(ComponentIdType componentId){
        super(componentId);
        this.averageEventDistributionDuration = -1;
        this.rollingEventProcessingDuration = -1;
        this.lastEventProcessingDuration = -1;
        this.averageEventDistributionDuration = -1;
        this.rollingAverageEventDistributionDuration = -1;
        this.eventDistributionStartInstant = Instant.EPOCH;
        this.eventDistributionFinishInstant = Instant.EPOCH;
        this.eventProcessingStartInstant = Instant.EPOCH;
        this.eventProcessingFinishInstant = Instant.EPOCH;
        this.finalisedTasks = 0;
        this.finishedTasks = 0;
        this.failedTasks = 0;
        this.startedTasks = 0;
        this.registeredTasks = 0;
        this.cancelledTasks = 0;
        this.currentActionableTask = null;
        this.lastActionableTask = null;
        this.nodeIngresQueueSize = 0;
    }

    //
    // Getters and Setters
    //


    public String getProcessingPlantParticipantName() {
        return processingPlantParticipantName;
    }

    public void setProcessingPlantParticipantName(String processingPlantParticipantName) {
        this.processingPlantParticipantName = processingPlantParticipantName;
    }

    public String getWorkshopParticipantName() {
        return workshopParticipantName;
    }

    public void setWorkshopParticipantName(String workshopParticipantName) {
        this.workshopParticipantName = workshopParticipantName;
    }

    public double getAverageEventProcessingDuration() {
        return averageEventProcessingDuration;
    }

    public void setAverageEventProcessingDuration(double averageEventProcessingDuration) {
        this.averageEventProcessingDuration = averageEventProcessingDuration;
    }

    public double getRollingEventProcessingDuration() {
        return rollingEventProcessingDuration;
    }

    public void setRollingEventProcessingDuration(double rollingEventProcessingDuration) {
        this.rollingEventProcessingDuration = rollingEventProcessingDuration;
    }

    public double getLastEventProcessingDuration() {
        return lastEventProcessingDuration;
    }

    public void setLastEventProcessingDuration(double lastEventProcessingDuration) {
        this.lastEventProcessingDuration = lastEventProcessingDuration;
    }

    public double getAverageEventDistributionDuration() {
        return averageEventDistributionDuration;
    }

    public void setAverageEventDistributionDuration(double averageEventDistributionDuration) {
        this.averageEventDistributionDuration = averageEventDistributionDuration;
    }

    public double getRollingAverageEventDistributionDuration() {
        return rollingAverageEventDistributionDuration;
    }

    public void setRollingAverageEventDistributionDuration(double rollingAverageEventDistributionDuration) {
        this.rollingAverageEventDistributionDuration = rollingAverageEventDistributionDuration;
    }

    public Instant getEventDistributionStartInstant() {
        return eventDistributionStartInstant;
    }

    public void setEventDistributionStartInstant(Instant eventDistributionStartInstant) {
        this.eventDistributionStartInstant = eventDistributionStartInstant;
    }

    public Instant getEventDistributionFinishInstant() {
        return eventDistributionFinishInstant;
    }

    public void setEventDistributionFinishInstant(Instant eventDistributionFinishInstant) {
        this.eventDistributionFinishInstant = eventDistributionFinishInstant;
    }

    public Instant getEventProcessingStartInstant() {
        return eventProcessingStartInstant;
    }

    public void setEventProcessingStartInstant(Instant eventProcessingStartInstant) {
        this.eventProcessingStartInstant = eventProcessingStartInstant;
    }

    public Instant getEventProcessingFinishInstant() {
        return eventProcessingFinishInstant;
    }

    public void setEventProcessingFinishInstant(Instant eventProcessingFinishInstant) {
        this.eventProcessingFinishInstant = eventProcessingFinishInstant;
    }

    public int getFinalisedTasks() {
        return finalisedTasks;
    }

    public void setFinalisedTasks(int finalisedTasks) {
        this.finalisedTasks = finalisedTasks;
    }

    public int getFinishedTasks() {
        return finishedTasks;
    }

    public void setFinishedTasks(int finishedTasks) {
        this.finishedTasks = finishedTasks;
    }

    public int getFailedTasks() {
        return failedTasks;
    }

    public void setFailedTasks(int failedTasks) {
        this.failedTasks = failedTasks;
    }

    public int getStartedTasks() {
        return startedTasks;
    }

    public void setStartedTasks(int startedTasks) {
        this.startedTasks = startedTasks;
    }

    public int getRegisteredTasks() {
        return registeredTasks;
    }

    public void setRegisteredTasks(int registeredTasks) {
        this.registeredTasks = registeredTasks;
    }

    public int getCancelledTasks() {
        return cancelledTasks;
    }

    public void setCancelledTasks(int cancelledTasks) {
        this.cancelledTasks = cancelledTasks;
    }

    public TaskIdType getCurrentActionableTask() {
        return currentActionableTask;
    }

    public void setCurrentActionableTask(TaskIdType currentActionableTask) {
        this.currentActionableTask = currentActionableTask;
    }

    public TaskIdType getLastActionableTask() {
        return lastActionableTask;
    }

    public void setLastActionableTask(TaskIdType lastActionableTask) {
        this.lastActionableTask = lastActionableTask;
    }

    public int getNodeIngresQueueSize() {
        return nodeIngresQueueSize;
    }

    public void setNodeIngresQueueSize(int nodeIngresQueueSize) {
        this.nodeIngresQueueSize = nodeIngresQueueSize;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "WorkUnitProcessorMetricsData{" +
                "ingresMessageCount=" + getIngresMessageCount() +
                ", egressMessageCount=" + getEgressMessageCount() +
                ", distributedMessageCount=" + getDistributedMessageCount() +
                ", distributionCountMap=" + getDistributionCountMap() +
                ", averageEventProcessingDuration=" + averageEventProcessingDuration +
                ", rollingEventProcessingDuration=" + rollingEventProcessingDuration +
                ", lastEventProcessingDuration=" + lastEventProcessingDuration +
                ", averageEventDistributionDuration=" + averageEventDistributionDuration +
                ", rollingAverageEventDistributionDuration=" + rollingAverageEventDistributionDuration +
                ", eventDistributionStartInstant=" + eventDistributionStartInstant +
                ", eventDistributionFinishInstant=" + eventDistributionFinishInstant +
                ", eventProcessingStartInstant=" + eventProcessingStartInstant +
                ", eventProcessingFinishInstant=" + eventProcessingFinishInstant +
                ", finalisedTasks=" + finalisedTasks +
                ", finishedTasks=" + finishedTasks +
                ", failedTasks=" + failedTasks +
                ", startedTasks=" + startedTasks +
                ", registeredTasks=" + registeredTasks +
                ", cancelledTasks=" + cancelledTasks +
                ", currentActionableTask=" + currentActionableTask +
                ", lastActionableTask=" + lastActionableTask +
                ", nodeIngresQueueSize=" + nodeIngresQueueSize +
                ", componentID=" + getComponentID() +
                ", participantName=" + getParticipantName() +
                ", metricsType='" + getComponentType() + '\'' +
                ", lastActivityInstant=" + getLastActivityInstant() +
                ", componentStartupInstant=" + getComponentStartupInstant() +
                ", componentStatus='" + getComponentStatus() + '\'' +
                ", processingPlantParticipantName=" + getProcessingPlantParticipantName() +
                ", workshopParticipantName=" + getWorkshopParticipantName() +
                '}';
    }
}
