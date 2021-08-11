package net.fhirfactory.pegacorn.core.model.topology.endpoints.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.topology.common.TopologyNode;
import net.fhirfactory.pegacorn.core.model.topology.common.valuesets.AdditionalParametersListEnum;
import net.fhirfactory.pegacorn.core.model.endpoints.PetasosTopologyEndpointTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class IPCTopologyEndpoint extends TopologyNode {
    private static final Logger LOG = LoggerFactory.getLogger(IPCTopologyEndpoint.class);

    private String name;
    private PetasosTopologyEndpointTypeEnum endpointType;
    private Map<AdditionalParametersListEnum, String> additionalParameters;
    private String connectedSystemName;

    public IPCTopologyEndpoint(){
        super();
        this.additionalParameters = new HashMap<>();
    }

    public Map<AdditionalParametersListEnum, String> getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(Map<AdditionalParametersListEnum, String> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    public String getConnectedSystemName() {
        return connectedSystemName;
    }

    public void setConnectedSystemName(String connectedSystemName) {
        this.connectedSystemName = connectedSystemName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PetasosTopologyEndpointTypeEnum getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(PetasosTopologyEndpointTypeEnum endpointType) {
        this.endpointType = endpointType;
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @JsonIgnore
    public String getAdditionalParameter(AdditionalParametersListEnum parameterName){
        if(this.additionalParameters.isEmpty()){
            return(null);
        }
        String value = additionalParameters.get(parameterName);
        return(value);
    }

    @Override
    public String toString() {
        return "IPCTopologyEndpoint{" +
                "nodeRDN=" + getNodeRDN() +
                ", nodeFDN=" + getNodeFDN() +
                ", componentType=" + getComponentType() +
                ", containingNodeFDN=" + getContainingNodeFDN() +
                ", nodeKey='" + getNodeKey() + '\'' +
                ", nodeFunctionFDN=" + getNodeFunctionFDN() +
                ", concurrencyMode=" + getConcurrencyMode() +
                ", resilienceMode=" + getResilienceMode() +
                ", securityZone=" + getSecurityZone() +
                ", kubernetesDeployed=" + isKubernetesDeployed() +
                ", name='" + name + '\'' +
                ", endpointType=" + endpointType +
                ", additionalParameters=" + additionalParameters +
                ", connectedSystemName='" + connectedSystemName + '\'' +
                '}';
    }
}
