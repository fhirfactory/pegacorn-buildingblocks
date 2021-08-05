package net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets;

public enum EndpointPayloadTypeEnum {
    ENDPOINT_PAYLOAD_PEGACORN_IPC("Pegacorn.FHIR.R4.Endpoint.IPC"),
    ENDPOINT_PAYLOAD_PEGACORN_OAM("Pegacorn.FHIR.R4.Endpoint.OAM"),
    ENDPOINT_PAYLOAD_PEGACORN_TASKS("Pegacorn.FHIR.R4.Endpoint.TASKS");

    private String payloadType;

    private EndpointPayloadTypeEnum(String payloadType ){
        this.payloadType = payloadType;
    }

    public String getPayloadType(){
        return(this.payloadType);
    }
}
