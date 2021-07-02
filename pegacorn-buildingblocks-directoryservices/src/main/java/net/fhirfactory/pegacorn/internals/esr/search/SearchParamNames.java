package net.fhirfactory.pegacorn.internals.esr.search;

public enum SearchParamNames {
    SHORT_NAME("shortName"),
    LONG_NAME("longName"),
    SIMPLIFIED_ID("simplifiedID"),
    ALL_NAME("allName"),
    DISPLAY_NAME("displayName"),
    PRIMARY_ROLE_CATEGORY_ID("primaryRoleCategegoryID"),
    PRIMARY_ROLE_ID("primaryRoleID"),
    PRIMARY_ORGANISATION_ID("primaryOrganisationID"),
    PRIMARY_LOCATION_ID("primaryLocationID"),
    GROUP_MANAGER("groupManager"),
    GROUP_TYPE("groupType"),
    LEAF_VALUE("leafValue"),
    CANONICAL_ALIAS("canonicalalias"),
    DATE_OF_BIRTH("dateofbirth"),
    EMAIL_ADDRESS("emailaddress");
    
    private String value;
    
    SearchParamNames(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    
    public static SearchParamNames get(String searchValue) {
        for (SearchParamNames type : values()) {
            if (type.getValue().equalsIgnoreCase(searchValue)) {
                return type;
            }
        }
        
        return null;
    }
}
