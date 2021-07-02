package net.fhirfactory.pegacorn.internals.esr.resources.datatypes;

public enum IdentifierType {
    SHORT_NAME("ShortName"),
    LONG_NAME("LongName"),
    EMAIL_ADDRESS("EmailAddress"),
    USER_ID("user_id"),
    MATRIX_ROOM_SYSTEM_ID("RoomSystemID"),
    MATRIX_ROOM_ID("matrix.room_id"),
    MATRIX_USER_ID("matrix.user_id"),
    CUMULATIVE_SHORT_NAME_IDENTIFIER_TYPE("CumulativeShortName"),
    CUMULATIVE_LONG_NAME_IDENTIFIER_TYPE("CumulativeLongName"),
    COMMUNICATE_SESSION("SessionID"),
    PATIENT_URN("patient_urn"),
    DATE_OF_BIRTH("Date of Birth");
    

    
    private String value;
    
    IdentifierType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    
    public static IdentifierType get(String searchValue) {
        for (IdentifierType type : values()) {
            if (type.getValue().equalsIgnoreCase(searchValue)) {
                return type;
            }
        }
        
        return null;
    }
}

