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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.context.valuesets.TaskTriggerSummaryTypeTypeEnum;

import java.io.Serializable;

public class TaskTriggerSummaryType implements Serializable {
    private TaskTriggerSummaryTypeTypeEnum triggerSummaryType;
    private TaskIdType triggerTaskId;
    private String triggerName;
    private String triggerDescription;
    private String triggerLocation;

    //
    // Constructor(s)
    //

    public TaskTriggerSummaryType(){
        this.triggerTaskId = null;
        this.triggerDescription = null;
        this.triggerName = null;
        this.triggerSummaryType = null;
        this.triggerLocation = null;
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasTriggerTaskId(){
        boolean hasValue = this.triggerTaskId != null;
        return(hasValue);
    }

    public TaskIdType getTriggerTaskId() {
        return triggerTaskId;
    }

    public void setTriggerTaskId(TaskIdType triggerTask) {
        this.triggerTaskId = triggerTask;
    }

    @JsonIgnore
    public boolean hasTriggerSummaryType(){
        boolean hasValue = this.triggerSummaryType != null;
        return(hasValue);
    }

    public TaskTriggerSummaryTypeTypeEnum getTriggerSummaryType() {
        return triggerSummaryType;
    }

    public void setTriggerSummaryType(TaskTriggerSummaryTypeTypeEnum triggerSummaryType) {
        this.triggerSummaryType = triggerSummaryType;
    }

    @JsonIgnore
    public boolean hasTriggerName(){
        boolean hasValue = this.triggerName != null;
        return(hasValue);
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    @JsonIgnore
    public boolean hasTriggerDescription(){
        boolean hasValue = this.triggerDescription != null;
        return(hasValue);
    }

    public String getTriggerDescription() {
        return triggerDescription;
    }

    public void setTriggerDescription(String triggerDescription) {
        this.triggerDescription = triggerDescription;
    }

    @JsonIgnore
    public boolean hasTriggerLocation(){
        boolean hasValue = this.triggerLocation != null;
        return(hasValue);
    }

    public String getTriggerLocation() {
        return triggerLocation;
    }

    public void setTriggerLocation(String triggerLocation) {
        this.triggerLocation = triggerLocation;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskTriggerSummaryType{" +
                "triggerSummaryType=" + triggerSummaryType +
                ", triggerName=" + triggerName +
                ", triggerDescription=" + triggerDescription +
                ", triggerLocation=" + triggerLocation +
                ", triggerTaskId=" + getTriggerTaskId() +
                '}';
    }
}
