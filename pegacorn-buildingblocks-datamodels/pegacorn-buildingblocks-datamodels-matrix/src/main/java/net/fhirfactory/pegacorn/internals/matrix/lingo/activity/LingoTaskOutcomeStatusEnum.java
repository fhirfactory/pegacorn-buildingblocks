package net.fhirfactory.pegacorn.internals.matrix.lingo.activity;

public enum LingoTaskOutcomeStatusEnum {
    ACTIVITY_OUTCOME_NO_ACTION ("activity-outcome-no-action"),
    ACTIVITY_OUTCOME_NO_RESULT("activity-outcome-no-result"),
    ACTIVITY_OUTCOME_FAILED ("activity-outcome-failed"),
    ACTIVITY_OUTCOME_SUCCESS ("activity-outcome-success");

    private String outcomeStatus;

    private LingoTaskOutcomeStatusEnum(String activityType){
        this.outcomeStatus = activityType;
    }

    public String getOutcomeStatus() {
        return outcomeStatus;
    }
}
