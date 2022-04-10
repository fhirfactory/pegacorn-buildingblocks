package net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets;

public enum DataParcelTypeEnum {
    IPC_DATA_PARCEL_TYPE("Parcel.IPC","dataparcel-type.ipc"),
    SEARCH_QUERY_DATA_PARCEL_TYPE("Parcel.Search-Query", "dataparcel-type.search-query"),
    SEARCH_RESULT_DATA_PARCEL_TYPE("Parcel.Search-Result", "dataparcel-type.search-result"),
    GENERAL_DATA_PARCEL_TYPE("Parcel.General", "dataparcel-type.general");

    private String token;
    private String displayName;

    private DataParcelTypeEnum(String displayName, String discriminatorType){
        this.token = discriminatorType;
        this.displayName = displayName;
    }

    public String getToken() {
        return token;
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public static DataParcelTypeEnum fromTypeValue(String typeValueString){
        for (DataParcelTypeEnum b : DataParcelTypeEnum.values()) {
            if (b.getToken().equalsIgnoreCase(typeValueString)) {
                return b;
            }
        }
        return null;
    }
}
