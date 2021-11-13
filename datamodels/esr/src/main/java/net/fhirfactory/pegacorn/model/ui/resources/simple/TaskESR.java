package net.fhirfactory.pegacorn.model.ui.resources.simple;

import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.ActionableTaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.model.ui.resources.simple.datatypes.PatientIdentifierESDT;
import net.fhirfactory.pegacorn.model.ui.resources.simple.datatypes.PeriodESDT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskESR {
    private String taskId;
    private String taskFocus;
    private PatientIdentifierESDT taskBeneficiary;
    private String taskEncounter;
    private Map<Integer, String> taskJourney;
    private Map<Integer, TaskESR> subTasks;
    private String taskInput;
    private List<String> taskOutput;
    private PeriodESDT taskPeriod;
    private ActionableTaskOutcomeStatusEnum taskStatus;
    private String taskStatusReason;
    private String taskTriggerType;
    private String taskTriggerId;
    private String taskFulfillmentLocation;

    //
    // Constructor(s)
    //

    public TaskESR(){
        this.taskId = null;
        this.taskFocus = null;
        this.taskBeneficiary = null;
        this.taskEncounter = null;
        this.taskJourney = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.taskInput = null;
        this.taskPeriod = new PeriodESDT();
        this.taskStatus = null;
        this.taskStatusReason = null;
        this.taskTriggerId = null;
        this.taskTriggerType = null;
        this.taskFulfillmentLocation = null;
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

    public PatientIdentifierESDT getTaskBeneficiary() {
        return taskBeneficiary;
    }

    public void setTaskBeneficiary(PatientIdentifierESDT taskBeneficiary) {
        this.taskBeneficiary = taskBeneficiary;
    }

    public String getTaskEncounter() {
        return taskEncounter;
    }

    public void setTaskEncounter(String taskEncounter) {
        this.taskEncounter = taskEncounter;
    }

    public Map<Integer, String> getTaskJourney() {
        return taskJourney;
    }

    public void setTaskJourney(Map<Integer, String> taskJourney) {
        this.taskJourney = taskJourney;
    }

    public Map<Integer, TaskESR> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(Map<Integer, TaskESR> subTasks) {
        this.subTasks = subTasks;
    }

    public String getTaskInput() {
        return taskInput;
    }

    public void setTaskInput(String taskInput) {
        this.taskInput = taskInput;
    }

    public List<String> getTaskOutput() {
        return taskOutput;
    }

    public void setTaskOutput(List<String> taskOutput) {
        this.taskOutput = taskOutput;
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

    public String getTaskStatusReason() {
        return taskStatusReason;
    }

    public void setTaskStatusReason(String taskStatusReason) {
        this.taskStatusReason = taskStatusReason;
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

    public String getTaskFulfillmentLocation() {
        return taskFulfillmentLocation;
    }

    public void setTaskFulfillmentLocation(String taskFulfillmentLocation) {
        this.taskFulfillmentLocation = taskFulfillmentLocation;
    }

    //
    // ToString
    //

    @Override
    public String toString() {
        return "TaskESR{" +
                "taskId=" + taskId +
                ", taskFocus=" + taskFocus +
                ", taskBeneficiary=" + taskBeneficiary +
                ", taskEncounter=" + taskEncounter +
                ", taskJourney=" + taskJourney +
                ", subTasks=" + subTasks +
                ", taskInput=" + taskInput +
                ", taskOutput=" + taskOutput +
                ", taskPeriod=" + taskPeriod +
                ", taskStatus=" + taskStatus +
                ", taskStatusReason='" + taskStatusReason +
                ", taskTriggerType=" + taskTriggerType +
                ", taskTriggerId=" + taskTriggerId +
                ", taskFulfillmentLocation=" + taskFulfillmentLocation +
                '}';
    }
}
