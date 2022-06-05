package net.fhirfactory.pegacorn.core.model.internal.transactions;

public enum ESRMethodOutcomeEnum {
    REVIEW_ENTRY_FOUND,
    REVIEW_ENTRY_NOT_FOUND,
    CREATE_ENTRY_SUCCESSFUL,
    CREATE_ENTRY_INVALID,
    CREATE_ENTRY_DUPLICATE,
    UPDATE_ENTRY_SUCCESSFUL,
    UPDATE_ENTRY_SUCCESSFUL_CREATE,
    UPDATE_ENTRY_INVALID,
    DELETE_ENTRY_SUCCESSFUL,
    DELETE_ENTRY_NOT_FOUND,
    DELETE_ENTRY_INVALID,
    SEARCH_COMPLETED_SUCCESSFULLY,
    SEARCH_DID_NOT_COMPLETE
}