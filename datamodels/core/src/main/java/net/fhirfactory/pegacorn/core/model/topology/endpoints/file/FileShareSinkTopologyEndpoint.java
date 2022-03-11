package net.fhirfactory.pegacorn.core.model.topology.endpoints.file;

import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentConnectivityContextEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.StandardInteractClientTopologyEndpointPort;


public class FileShareSinkTopologyEndpoint extends StandardInteractClientTopologyEndpointPort {

    public FileShareSinkTopologyEndpoint(){
        super();
        setEndpointType(PetasosEndpointTopologyTypeEnum.FILE_SHARE_SINK);
        setComponentSystemRole(SoftwareComponentConnectivityContextEnum.COMPONENT_ROLE_INTERACT_EGRESS);
    }
}
