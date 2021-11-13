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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class TaskMetadataType implements Serializable {
    TaskBeneficiaryType taskBeneficiary;
    TaskEncounterType taskEncounter;
    TaskTriggerSummaryType taskTriggerSummary;

    //
    // Constructor(s)
    //

    public TaskMetadataType(){
        this.taskEncounter = null;
        this.taskBeneficiary = null;
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasTaskBeneficiary(){
        boolean hasValue = this.taskBeneficiary != null;
        return(hasValue);
    }

    public TaskBeneficiaryType getTaskBeneficiary() {
        return taskBeneficiary;
    }

    public void setTaskBeneficiary(TaskBeneficiaryType taskBeneficiary) {
        this.taskBeneficiary = taskBeneficiary;
    }

    @JsonIgnore
    public boolean hasTaskEncounter(){
        boolean hasValue = this.taskEncounter != null;
        return(hasValue);
    }

    public TaskEncounterType getTaskEncounter() {
        return taskEncounter;
    }

    public void setTaskEncounter(TaskEncounterType taskEncounter) {
        this.taskEncounter = taskEncounter;
    }

    @JsonIgnore
    public boolean hasTaskTriggerSummary(){
        boolean hasValue = this.taskTriggerSummary != null;
        return(hasValue);
    }

    public TaskTriggerSummaryType getTaskTriggerSummary() {
        return taskTriggerSummary;
    }

    public void setTaskTriggerSummary(TaskTriggerSummaryType taskTriggerSummary) {
        this.taskTriggerSummary = taskTriggerSummary;
    }
    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskMetadataType{" +
                "taskBeneficiary=" + taskBeneficiary +
                ", taskEncounter=" + taskEncounter +
                ", taskTriggerSummary" + getTaskTriggerSummary() +
                '}';
    }
}
