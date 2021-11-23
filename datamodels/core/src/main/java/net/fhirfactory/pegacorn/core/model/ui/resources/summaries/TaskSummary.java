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

import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.ActionableTaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.PeriodESDT;

import java.util.HashMap;
import java.util.Map;

public class TaskSummary {
    private String taskId;
    private String taskFocus;
    private String taskBeneficiary;
    private String taskEncounter;
    private String taskStartPoint;
    private Map<Integer, String> subTasks;
    private String taskEndPoint;
    private PeriodESDT taskPeriod;
    private ActionableTaskOutcomeStatusEnum taskStatus;
    private String taskTriggerType;
    private String taskTriggerId;

    //
    // Constructor(s)
    //

    public TaskSummary(){
        this.taskId = null;
        this.taskFocus = null;
        this.taskBeneficiary = null;
        this.taskEncounter = null;
        this.taskStartPoint = null;
        this.subTasks = new HashMap<>();
        this.taskEndPoint = null;
        this.taskPeriod = null;
        this.taskStatus = null;
        this.taskTriggerId = null;
        this.taskTriggerType = null;
    }

    //
    // Getters and Setters
    //

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskFocus() {
        return taskFocus;
    }

    public void setTaskFocus(String taskFocus) {
        this.taskFocus = taskFocus;
    }

    public String getTaskBeneficiary() {
        return taskBeneficiary;
    }

    public void setTaskBeneficiary(String taskBeneficiary) {
        this.taskBeneficiary = taskBeneficiary;
    }

    public String getTaskEncounter() {
        return taskEncounter;
    }

    public void setTaskEncounter(String taskEncounter) {
        this.taskEncounter = taskEncounter;
    }

    public String getTaskStartPoint() {
        return taskStartPoint;
    }

    public void setTaskStartPoint(String taskStartPoint) {
        this.taskStartPoint = taskStartPoint;
    }

    public Map<Integer, String> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(Map<Integer, String> subTasks) {
        this.subTasks = subTasks;
    }

    public String getTaskEndPoint() {
        return taskEndPoint;
    }

    public void setTaskEndPoint(String taskEndPoint) {
        this.taskEndPoint = taskEndPoint;
    }

    public PeriodESDT getTaskPeriod() {
        return taskPeriod;
    }

    public void setTaskPeriod(PeriodESDT taskPeriod) {
        this.taskPeriod = taskPeriod;
    }

    public ActionableTaskOutcomeStatusEnum getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(ActionableTaskOutcomeStatusEnum taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskTriggerType() {
        return taskTriggerType;
    }

    public void setTaskTriggerType(String taskTriggerType) {
        this.taskTriggerType = taskTriggerType;
    }

    public String getTaskTriggerId() {
        return taskTriggerId;
    }

    public void setTaskTriggerId(String taskTriggerId) {
        this.taskTriggerId = taskTriggerId;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskSummary{" +
                "taskId=" + taskId +
                ", taskFocus=" + taskFocus +
                ", taskBeneficiary=" + taskBeneficiary +
                ", taskEncounter=" + taskEncounter +
                ", taskStartPoint=" + taskStartPoint +
                ", subTasks=" + subTasks +
                ", taskEndPoint=" + taskEndPoint +
                ", taskPeriod=" + taskPeriod +
                ", taskStatus=" + taskStatus +
                ", taskTriggerType=" + taskTriggerType +
                ", taskTriggerId=" + taskTriggerId +
                '}';
    }
}
