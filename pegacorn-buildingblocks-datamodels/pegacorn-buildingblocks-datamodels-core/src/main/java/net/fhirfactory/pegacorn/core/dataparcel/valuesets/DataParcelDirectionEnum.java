package net.fhirfactory.pegacorn.core.dataparcel.valuesets;

public enum DataParcelDirectionEnum {
    INBOUND_DATA_PARCEL("dataparcel.flow-direction.inbound"),
    OUTBOUND_DATA_PARCEL("dataparcel.flow-direction.outbound"),
    SUBSYSTEM_IPC_DATA_PARCEL("dataparcel.flow-direction.subsystem_ipc");

    private String dataParcelDirectionValue;

    private DataParcelDirectionEnum(String direction){
        this.dataParcelDirectionValue = direction;
    }

    public String getDataParcelDirectionValue() {
        return dataParcelDirectionValue;
    }

    public static DataParcelDirectionEnum fromTypeValue(String typeValueString){
        for (DataParcelDirectionEnum b : DataParcelDirectionEnum.values()) {
            if (b.getDataParcelDirectionValue().equalsIgnoreCase(typeValueString)) {
                return b;
            }
        }
        return null;
    }
}
