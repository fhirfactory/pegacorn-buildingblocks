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
package net.fhirfactory.pegacorn.core.model.ui.resources.summaries;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.TaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.PeriodESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.common.ResourceSummaryBase;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.datatypes.TaskMetadataSummary;
import org.apache.commons.lang3.SerializationUtils;

public class TaskSummary extends ResourceSummaryBase {
    private String taskId;
    private TaskMetadataSummary taskMetadata;
    private PeriodESDT taskPeriod;
    private TaskOutcomeStatusEnum taskStatus;

    //
    // Constructor(s)
    //

    public TaskSummary(){
        this.taskId = null;
        this.taskPeriod = null;
        this.taskStatus = null;
        this.taskMetadata = null;
    }

    public TaskSummary(TaskSummary ori){
        this.taskId = null;
        this.taskPeriod = null;
        this.taskStatus = null;
        this.taskMetadata = null;
        if(ori.hasTaskId()){
            this.setTaskId(SerializationUtils.clone(ori.getTaskId()));
        }
        if(ori.hasTaskPeriod()){
            this.setTaskPeriod(SerializationUtils.clone(ori.getTaskPeriod()));
        }
        if (ori.hasTaskStatus()) {
            this.setTaskStatus(ori.getTaskStatus());
        }
        if(ori.hasTaskMetadata()){
            this.setTaskMetadata(SerializationUtils.clone(ori.getTaskMetadata()));
        }
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasTaskId(){
        boolean hasValue = this.taskId != null;
        return(hasValue);
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @JsonIgnore
    public boolean hasTaskPeriod(){
        boolean hasValue = this.taskPeriod != null;
        return(hasValue);
    }

    public PeriodESDT getTaskPeriod() {
        return taskPeriod;
    }

    public void setTaskPeriod(PeriodESDT taskPeriod) {
        this.taskPeriod = taskPeriod;
    }

    @JsonIgnore
    public boolean hasTaskStatus(){
        boolean hasValue = this.taskStatus != null;
        return(hasValue);
    }

    public TaskOutcomeStatusEnum getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskOutcomeStatusEnum taskStatus) {
        this.taskStatus = taskStatus;
    }

    @JsonIgnore
    public boolean hasTaskMetadata(){
        boolean hasValue = this.taskMetadata != null;
        return(hasValue);
    }

    public TaskMetadataSummary getTaskMetadata() {
        return taskMetadata;
    }

    public void setTaskMetadata(TaskMetadataSummary taskMetadata) {
        this.taskMetadata = taskMetadata;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskSummary{" +
                "taskId='" + taskId + '\'' +
                ", taskMetadata=" + taskMetadata +
                ", taskPeriod=" + taskPeriod +
                ", taskStatus=" + taskStatus +
                ", lastSynchronisationInstant=" + getLastSynchronisationInstant() +
                ", lastActivityInstant=" + getLastActivityInstant() +
                ", resourceId='" + getResourceId() + '\'' +
                '}';
    }
}
