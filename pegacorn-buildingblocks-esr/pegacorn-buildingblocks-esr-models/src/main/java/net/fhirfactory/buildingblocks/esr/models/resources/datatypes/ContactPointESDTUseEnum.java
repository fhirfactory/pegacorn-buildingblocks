package net.fhirfactory.buildingblocks.esr.models.resources.datatypes;

public enum ContactPointESDTUseEnum {
    WORK("Work"),
    OTHER("other");
    
    private String value;
    
    ContactPointESDTUseEnum(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
