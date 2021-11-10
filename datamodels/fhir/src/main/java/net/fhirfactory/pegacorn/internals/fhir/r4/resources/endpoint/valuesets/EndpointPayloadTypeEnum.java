package net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets;

public enum EndpointPayloadTypeEnum {
    ENPOINT_PAYLOAD_PEGACORN_IPC("Pegacorn.FHIR.R4.Endpoint.IPC"),
    ENPOINT_PAYLOAD_PEGACORN_OAM("Pegacorn.FHIR.R4.Endpoint.OAM");

    private String payloadType;

    private EndpointPayloadTypeEnum(String payloadType ){
        this.payloadType = payloadType;
    }

    public String getPayloadType(){
        return(this.payloadType);
    }
}
