package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.context.valuesets;

public enum TaskTriggerSummaryTypeTypeEnum {
    TASK_TRIGGER_HL7_V2X_MESSAGE("HL7 v2.x Message", "petasos.task.trigger_summary_type.hl7_v2x_message"),
    TASK_TRIGGER_HL7_FHIR("HL7 FHIR", "petasos.task.trigger_summary_type.hl7_fhir"),
    TASK_TRIGGER_COMMUNICATE_MATRIX_EVENT("Communicate Matrix Event", "petasos.task.trigger_summary_type.communicate_matrix_event");

    private String displayName;
    private String token;

    private TaskTriggerSummaryTypeTypeEnum(String name, String token){
        this.displayName = name;
        this.token = token;
    }

    public String getToken(){
        return(this.token);
    }

    public String getDisplayName(){
        return(this.displayName);
    }

}
