package net.fhirfactory.pegacorn.core.model.topology.endpoints.file;

import net.fhirfactory.pegacorn.core.model.topology.endpoints.http.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentConnectivityContextEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.HTTPClientAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapter;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.StandardInteractClientTopologyEndpointPort;

import java.util.ArrayList;
import java.util.List;

public class FileShareSinkTopologyEndpoint extends StandardInteractClientTopologyEndpointPort {

    public FileShareSinkTopologyEndpoint(){
        super();
        setEndpointType(PetasosEndpointTopologyTypeEnum.FILE_SHARE_SINK);
        setComponentSystemRole(SoftwareComponentConnectivityContextEnum.COMPONENT_ROLE_INTERACT_EGRESS);
    }

    @JsonIgnore
    public List<HTTPClientAdapter> getHTTPClientAdapters() {
        List<HTTPClientAdapter> httpAdapterList = new ArrayList<>();
        for(IPCAdapter currentInterface: getAdapterList()){
            HTTPClientAdapter currentClientAdapter = (HTTPClientAdapter)currentInterface;
            httpAdapterList.add(currentClientAdapter);
        }
        return httpAdapterList;
    }

    @JsonIgnore
    public void setHTTPClientAdapters(List<HTTPClientAdapter> targetHTTPClientAdapters) {
        if(targetHTTPClientAdapters != null) {
            this.getAdapterList().clear();
            this.getAdapterList().addAll(targetHTTPClientAdapters);
        }
    }
}
