package net.fhirfactory.pegacorn.endpoints.endpoints.datatypes;

public enum PetasosInterfaceStatusEnum {
    INTERFACE_STATUS_DETECTED("petasos_interface.detected"),
    INTERFACE_STATUS_REACHABLE("petasos_interface.reachable"),
    INTERFACE_STATUS_ACTIVE("petasos_interface.active"),
    INTERFACE_STATUS_SUSPECT("petasos_interface.suspect"),
    INTERFACE_STATUS_FAILED("petasos_interface.failed");

    private String interfaceStatus;

    private PetasosInterfaceStatusEnum(String status){
        this.interfaceStatus = status;
    }

    public String getInterfaceStatus() {
        return interfaceStatus;
    }
}
