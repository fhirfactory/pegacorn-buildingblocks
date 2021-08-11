package net.fhirfactory.pegacorn.core.model.topology.endpoints.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCServerTopologyEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPCClusteredServerTopologyEndpoint extends IPCServerTopologyEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(IPCClusteredServerTopologyEndpoint.class);

    private Integer servicePortValue;
    private String servicePortName;
    private Integer servicePortOffset;
    private String serviceDNSName;

    @JsonIgnore
    private String toStringString;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public IPCClusteredServerTopologyEndpoint(){
        super();
    }

    public Integer getServicePortValue() {
        return servicePortValue;
    }

    public void setServicePortValue(Integer servicePortValue) {
        this.servicePortValue = servicePortValue;
    }

    public String getServicePortName() {
        return servicePortName;
    }

    public void setServicePortName(String servicePortName) {
        this.servicePortName = servicePortName;
    }

    public Integer getServicePortOffset() {
        return servicePortOffset;
    }

    public void setServicePortOffset(Integer servicePortOffset) {
        this.servicePortOffset = servicePortOffset;
    }

    public String getServiceDNSName() {
        return serviceDNSName;
    }

    public void setServiceDNSName(String serviceDNSName) {
        this.serviceDNSName = serviceDNSName;
    }

    @Override
    public String toString() {
        return "IPCClusteredServerTopologyEndpoint{" +
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
                ", servicePortValue=" + servicePortValue +
                ", servicePortName='" + servicePortName + '\'' +
                ", servicePortOffset=" + servicePortOffset +
                ", serviceDNSName='" + serviceDNSName + '\'' +
                ", toStringString='" + toStringString + '\'' +
                ", endpointType=" + getEndpointType() +
                ", supportedInterfaceSet=" + getSupportedInterfaceSet() +
                ", encrypted=" + isEncrypted() +
                ", portValue=" + getPortValue() +
                ", portType='" + getPortType() + '\'' +
                ", aServer=" + getaServer() +
                ", encrypted=" + getEncrypted() +
                ", interfaceDNSName='" + getHostDNSName() + '\'' +
                ", name='" + getName() + '\'' +
                ", additionalParameters=" + getAdditionalParameters() +
                '}';
    }
}
