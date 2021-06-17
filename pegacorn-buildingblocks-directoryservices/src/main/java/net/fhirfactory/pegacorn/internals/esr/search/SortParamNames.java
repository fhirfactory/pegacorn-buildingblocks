package net.fhirfactory.pegacorn.internals.esr.search;

/**
 * Sort param names.
 * 
 * @author Brendan Douglas
 *
 */
public enum SortParamNames {
    SHORT_NAME("shortName"),
    LONG_NAME("longName"),
    SIMPLIFIED_ID("simplifiedID"),
    DISPLAY_NAME("displayName"),
    PRIMARY_ROLE_CATEGORY_ID("primaryRoleCategegoryID"),
    PRIMARY_ROLE_ID("primaryRoleID"),
    PRIMARY_ORGANISATION_ID("primaryOrganisationID"),
    PRIMARY_ORGANIZATION_ID("primaryOrganizationID"),
    PRIMARY_LOCATION_ID("primaryLocationID"),
    GROUP_MANAGER("groupManager"),
    GROUP_TYPE("groupType"),
    LEAF_VALUE("leafValue"),
    LAST_ROLE_SELECTED("lastRoleSelected");
    
    private String value;
    
    SortParamNames(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    
    public static SortParamNames get(String searchValue) {
        for (SortParamNames type : values()) {
            if (type.getValue().equalsIgnoreCase(searchValue)) {
                return type;
            }
        }
        
        return null;
    }
}
