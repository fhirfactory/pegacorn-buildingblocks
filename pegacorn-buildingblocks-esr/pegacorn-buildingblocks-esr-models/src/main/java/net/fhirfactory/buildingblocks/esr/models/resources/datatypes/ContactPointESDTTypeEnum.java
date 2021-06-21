package net.fhirfactory.buildingblocks.esr.models.resources.datatypes;

public enum ContactPointESDTTypeEnum {
    MOBILE("Mobile"),
    PABX_EXTENSION("PAX Extension"),
    LANDLINE("Landline"),
    EMAIL("Email"),
    LINGO("Lingo"),
    PAGER("Pager"),
    SOCIAL_MEDIA_ID("Social Media ID"),
    USERNAME("Username"),
    FACSIMILE("Facsimile");
    
    private String value;
    
    ContactPointESDTTypeEnum(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
