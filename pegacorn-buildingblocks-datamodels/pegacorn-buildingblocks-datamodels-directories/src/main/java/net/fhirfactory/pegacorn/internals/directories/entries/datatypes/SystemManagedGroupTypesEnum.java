package net.fhirfactory.pegacorn.internals.directories.entries.datatypes;

public enum SystemManagedGroupTypesEnum {
    PRACTITIONEROLE_MAP_PRACTITIONER_GROUP("SystemManaged-PractitionerRoleMap-PractitionerGroup"),
    PRACTITONERROLE_MAP_PRACTITIONERROLE_GROUP("SystemManaged-PractitionerRoleMap-PractitionerRoleGroup"),
    GENERAL("SystemManaged-GeneralGroup");

    private String typeCode;

    private SystemManagedGroupTypesEnum(String typeValue){
        this.typeCode = typeValue;
    }

    public String getTypeCode(){
        return(typeCode);
    }

    public static SystemManagedGroupTypesEnum fromTypeCode(String code){
        if(code.contentEquals("SystemManaged-PractitionerRoleMap-PractitionerGroup")){
            return(SystemManagedGroupTypesEnum.PRACTITIONEROLE_MAP_PRACTITIONER_GROUP);
        }
        if(code.contentEquals("SystemManaged-PractitionerRoleMap-PractitionerRoleGroup")){
            return(SystemManagedGroupTypesEnum.PRACTITONERROLE_MAP_PRACTITIONERROLE_GROUP);
        }
        return(GENERAL);
    }
}
