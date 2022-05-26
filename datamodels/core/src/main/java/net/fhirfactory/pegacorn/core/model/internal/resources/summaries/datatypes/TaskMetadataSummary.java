package net.fhirfactory.pegacorn.core.model.internal.resources.summaries.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public class TaskMetadataSummary implements Serializable {
    private String taskBeneficiary;
    private String taskFocus;
    private String taskEncounter;
    private String taskInputType;
    private String taskInputDescription;
    private String taskOutputType;
    private String taskOutputDescription;

    //
    // Constructor(s)
    //

    public TaskMetadataSummary(){
        this.taskBeneficiary = null;
        this.taskFocus = null;
        this.taskInputType = null;
        this.taskInputDescription = null;
        this.taskOutputDescription = null;
        this.taskOutputType = null;
        this.taskEncounter = null;
    }

    public TaskMetadataSummary(TaskMetadataSummary ori){
        this.taskBeneficiary = null;
        this.taskFocus = null;
        this.taskInputType = null;
        this.taskInputDescription = null;
        this.taskOutputDescription = null;
        this.taskOutputType = null;
        this.taskEncounter = null;
        if(ori.hasTaskEncounter()){
            this.setTaskEncounter(SerializationUtils.clone(ori.getTaskEncounter()));
        }
        if(ori.hasTaskBeneficiary()){
            this.setTaskBeneficiary(SerializationUtils.clone(ori.getTaskBeneficiary()));
        }
        if(ori.hasTaskFocus()){
            this.setTaskFocus(SerializationUtils.clone(ori.getTaskFocus()));
        }
        if(ori.hasTaskInputType()){
            this.setTaskInputType(SerializationUtils.clone(ori.getTaskInputType()));
        }
        if(ori.hasTaskInputDescription()){
            this.setTaskInputDescription(SerializationUtils.clone(ori.getTaskInputDescription()));
        }
        if(ori.hasTaskOutputType()){
            this.setTaskOutputType(SerializationUtils.clone(ori.getTaskOutputType()));
        }
        if(ori.hasTaskOutputDescription()){
            this.setTaskOutputDescription(SerializationUtils.clone(ori.getTaskOutputDescription()));
        }
    }


    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasTaskEncounter(){
        boolean hasValue = this.taskEncounter != null;
        return(hasValue);
    }

    public String getTaskEncounter() {
        return taskEncounter;
    }

    public void setTaskEncounter(String taskEncounter) {
        this.taskEncounter = taskEncounter;
    }

    @JsonIgnore
    public boolean hasTaskBeneficiary(){
        boolean hasValue = this.taskBeneficiary != null;
        return(hasValue);
    }

    public String getTaskBeneficiary() {
        return taskBeneficiary;
    }

    public void setTaskBeneficiary(String taskBeneficiary) {
        this.taskBeneficiary = taskBeneficiary;
    }

    @JsonIgnore
    public boolean hasTaskFocus(){
        boolean hasValue = this.taskFocus != null;
        return(hasValue);
    }

    public String getTaskFocus() {
        return taskFocus;
    }

    public void setTaskFocus(String taskFocus) {
        this.taskFocus = taskFocus;
    }

    @JsonIgnore
    public boolean hasTaskInputType(){
        boolean hasValue = this.taskInputType != null;
        return(hasValue);
    }

    public String getTaskInputType() {
        return taskInputType;
    }

    public void setTaskInputType(String taskInputType) {
        this.taskInputType = taskInputType;
    }

    @JsonIgnore
    public boolean hasTaskInputDescription(){
        boolean hasValue = this.taskInputDescription != null;
        return(hasValue);
    }

    public String getTaskInputDescription() {
        return taskInputDescription;
    }

    public void setTaskInputDescription(String taskInputDescription) {
        this.taskInputDescription = taskInputDescription;
    }

    @JsonIgnore
    public boolean hasTaskOutputType(){
        boolean hasValue = this.taskOutputType != null;
        return(hasValue);
    }

    public String getTaskOutputType() {
        return taskOutputType;
    }

    public void setTaskOutputType(String taskOutputType) {
        this.taskOutputType = taskOutputType;
    }

    @JsonIgnore
    public boolean hasTaskOutputDescription(){
        boolean hasValue = this.taskOutputDescription != null;
        return(hasValue);
    }

    public String getTaskOutputDescription() {
        return taskOutputDescription;
    }

    public void setTaskOutputDescription(String taskOutputDescription) {
        this.taskOutputDescription = taskOutputDescription;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskMetadataSummary{" +
                "taskBeneficiary='" + taskBeneficiary + '\'' +
                ", taskFocus='" + taskFocus + '\'' +
                ", taskInputType='" + taskInputType + '\'' +
                ", taskInputDescription='" + taskInputDescription + '\'' +
                ", taskOutputType='" + taskOutputType + '\'' +
                ", taskOutputDescription='" + taskOutputDescription + '\'' +
                '}';
    }
}
