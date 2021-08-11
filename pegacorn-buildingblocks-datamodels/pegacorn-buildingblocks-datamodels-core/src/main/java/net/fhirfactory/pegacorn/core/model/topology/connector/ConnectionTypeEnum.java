package net.fhirfactory.pegacorn.core.model.topology.connector;

public enum ConnectionTypeEnum {
    PEGACORN_INTERNAL_IPC_STREAM_MESSAGING_CONNECTION("endpoint.ipc_ingres_stream_messaging"),
    PEGACORN_INTERNAL_IPC_HTTP_MESSAGING_CONNECTION("endpoint.ipc_egress_stream_messaging"),
    PEGACORN_EXTERNAL_IPC_HTTP_CLIENT_CONNECTION("endpoint.ipc_ingres_http_server"),
    PEGACORN_EXTERNAL_IPC_HTTP_SERVER_CONNECTION("endpoint.ipc_egress_http_client"),
    PEGACORN_EXTERNAL_IPC_FILE_SHARE_WRITER("endpoint.http_api_server"),
    PEGACORN_EXTERNAL_IPC_FILE_SHARE_READER("endpoint.http_api_server"),
    PEGACORN_EXTERNAL_IPC_API_CLIENT("endpoint.http_api_client"),
    PEGACORN_EXTERNAL_IPC_API_SERVER("endpoint.http_api_client"),
    PEGACORN_EXTERNAL_IPC_OTHER_SERVER("endpoint.other_type_of_server"),
    PEGACORN_EXTERNAL_IPC_OTHER_CLIENT("endpoint.other_type_of_client");

    private String connectionType;

    private ConnectionTypeEnum(String connectionType){
        this.connectionType = connectionType;
    }

    public String getConnectionType(){
        return(this.connectionType);
    }
}
