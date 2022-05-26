package net.fhirfactory.pegacorn.internals.communicate.activity;

public enum CommunicateActivityOutcomeStatusEnum {
    ACTIVITY_OUTCOME_NO_ACTION ("activity-outcome-no-action"),
    ACTIVITY_OUTCOME_NO_RESULT("activity-outcome-no-result"),
    ACTIVITY_OUTCOME_FAILED ("activity-outcome-failed"),
    ACTIVITY_OUTCOME_SUCCESS ("activity-outcome-success");

    private String outcomeStatus;

    private CommunicateActivityOutcomeStatusEnum(String activityType){
        this.outcomeStatus = activityType;
    }

    public String getOutcomeStatus() {
        return outcomeStatus;
    }
}
