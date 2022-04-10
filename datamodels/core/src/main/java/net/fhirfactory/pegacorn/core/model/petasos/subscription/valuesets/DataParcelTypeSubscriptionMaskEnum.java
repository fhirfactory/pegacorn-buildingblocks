package net.fhirfactory.pegacorn.core.model.petasos.subscription.valuesets;

public enum DataParcelTypeSubscriptionMaskEnum {
    PARCEL_TYPE_ANY("Parcel.AnyType","dataparcel-type.any"),
    IPC_DATA_PARCEL_TYPE("Parcel.IPC","dataparcel-type.ipc"),
    SEARCH_QUERY_DATA_PARCEL_TYPE("Parcel.Search-Query", "dataparcel-type.search-query"),
    SEARCH_RESULT_DATA_PARCEL_TYPE("Parcel.Search-Result", "dataparcel-type.search-result"),
    GENERAL_DATA_PARCEL_TYPE("Parcel.General", "dataparcel-type.general");

    private String token;
    private String displayName;

    private DataParcelTypeSubscriptionMaskEnum(String displayName, String discriminatorType){
        this.token = discriminatorType;
        this.displayName = displayName;
    }

    public String getToken() {
        return token;
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public static DataParcelTypeSubscriptionMaskEnum fromTypeValue(String typeValueString){
        for (DataParcelTypeSubscriptionMaskEnum b : DataParcelTypeSubscriptionMaskEnum.values()) {
            if (b.getToken().equalsIgnoreCase(typeValueString)) {
                return b;
            }
        }
        return null;
    }
}
