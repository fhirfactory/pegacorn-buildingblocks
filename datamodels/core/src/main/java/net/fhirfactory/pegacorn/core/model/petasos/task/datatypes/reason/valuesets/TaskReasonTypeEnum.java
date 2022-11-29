package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.reason.valuesets;

import org.apache.commons.lang3.StringUtils;

public enum TaskReasonTypeEnum {
    TASK_REASON_UNKNOWN("ReasonUnknownTask", "petasos.task_reason.unknown"),
    TASK_REASON_MESSAGE_PROCESSING("MessageProcessingTask", "petasos.task_reason.message_processing"),
    TASK_REASON_TASK_ROUTING("RoutingTask", "petasos.task_reason.task_routing"),
    TASK_REASON_API_REQUEST("APIProcessingTask", "petasos.task_reason.api_request");

    private String taskReasonCode;
    private String taskReasonDisplayName;

    private TaskReasonTypeEnum(String name, String reason){
        this.taskReasonDisplayName = name;
        this.taskReasonCode = reason;
    }

    public String getTaskReasonCode(){
        return(taskReasonCode);
    }

    public String getTaskReasonDisplayName(){
        return(taskReasonDisplayName);
    }

    public static TaskReasonTypeEnum fromTaskReasonCode(String reasonCode){
        if(StringUtils.isEmpty(reasonCode)){
            return(TASK_REASON_UNKNOWN);
        }
        for(TaskReasonTypeEnum currentReason: TaskReasonTypeEnum.values()){
            if(currentReason.getTaskReasonCode().contentEquals(reasonCode)){
                return(currentReason);
            }
        }
        return(TASK_REASON_UNKNOWN);
    }
}
