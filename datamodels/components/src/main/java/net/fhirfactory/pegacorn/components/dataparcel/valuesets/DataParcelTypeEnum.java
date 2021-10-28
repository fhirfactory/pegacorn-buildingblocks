package net.fhirfactory.pegacorn.components.dataparcel.valuesets;

public enum DataParcelTypeEnum {
    IPC_DATA_PARCEL_TYPE("dataparcel-type.ipc"),
    SEARCH_QUERY_DATA_PARCEL_TYPE("dataparcel-type.search-query"),
    SEARCH_RESULT_DATA_PARCEL_TYPE("dataparcel-type.search-result"),
    GENERAL_DATA_PARCEL_TYPE("dataparcel-type.general");

    private String dataParcelTypeValue;

    private DataParcelTypeEnum(String discriminatorType){
        this.dataParcelTypeValue = discriminatorType;
    }

    public String getDataParcelTypeValue() {
        return dataParcelTypeValue;
    }

    public static DataParcelTypeEnum fromTypeValue(String typeValueString){
        for (DataParcelTypeEnum b : DataParcelTypeEnum.values()) {
            if (b.getDataParcelTypeValue().equalsIgnoreCase(typeValueString)) {
                return b;
            }
        }
        return null;
    }
}
