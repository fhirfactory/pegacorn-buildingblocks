package net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.datatypes;

import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCSocketBasedAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;

public class JGroupsAdapter extends IPCSocketBasedAdapter {

    //
    // Constructor(s)
    //

    public JGroupsAdapter(){
        super();
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "JGroupsAdapter{" +
                "supportedDeploymentModes=" + getSupportedDeploymentModes() +
                ", targetNameInstant='" + getTargetNameInstant() + '\'' +
                ", enablingTopologyEndpoint=" + getEnablingTopologyEndpoint() +
                ", supportedInterfaceDefinitions=" + getSupportedInterfaceDefinitions() +
                ", supportInterfaceTags=" + getSupportInterfaceTags() +
                ", encrypted=" + isEncrypted() +
                ", groupName='" + getGroupName() + '\'' +
                ", active=" + isActive() +
                ", additionalParameters=" + getAdditionalParameters() +
                ", lastActivity=" + getLastActivity() +
                ", portNumber=" + getPortNumber() +
                ", hostName='" + getHostName() + '\'' +
                '}';
    }
}
