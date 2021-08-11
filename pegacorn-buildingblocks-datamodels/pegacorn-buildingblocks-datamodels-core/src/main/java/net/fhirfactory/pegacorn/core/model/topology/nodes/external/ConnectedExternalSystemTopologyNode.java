package net.fhirfactory.pegacorn.core.model.topology.nodes.external;

import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.ExternalSystemIPCEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ConnectedExternalSystemTopologyNode {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectedExternalSystemTopologyNode.class);

    private String owningOrganizationName;
    private String solutionName;
    private String subsystemName;
    private String subsystemVersion;
    private String externalisedServiceName;
    private String externalisedServiceEndpointName;
    private List<ExternalSystemIPCEndpoint> targetPorts;

    public ConnectedExternalSystemTopologyNode(){
        this.targetPorts = new ArrayList<>();
    }

    public String getOwningOrganizationName() {
        return owningOrganizationName;
    }

    public void setOwningOrganizationName(String owningOrganizationName) {
        this.owningOrganizationName = owningOrganizationName;
    }

    public String getSolutionName() {
        return solutionName;
    }

    public void setSolutionName(String solutionName) {
        this.solutionName = solutionName;
    }

    public String getSubsystemName() {
        return subsystemName;
    }

    public void setSubsystemName(String subsystemName) {
        this.subsystemName = subsystemName;
    }

    public String getSubsystemVersion() {
        return subsystemVersion;
    }

    public void setSubsystemVersion(String subsystemVersion) {
        this.subsystemVersion = subsystemVersion;
    }

    public String getExternalisedServiceName() {
        return externalisedServiceName;
    }

    public void setExternalisedServiceName(String externalisedServiceName) {
        this.externalisedServiceName = externalisedServiceName;
    }

    public String getExternalisedServiceEndpointName() {
        return externalisedServiceEndpointName;
    }

    public void setExternalisedServiceEndpointName(String externalisedServiceEndpointName) {
        this.externalisedServiceEndpointName = externalisedServiceEndpointName;
    }

    public List<ExternalSystemIPCEndpoint> getTargetPorts() {
        return targetPorts;
    }

    public void setTargetPorts(List<ExternalSystemIPCEndpoint> targetPorts) {
        this.targetPorts.clear();
        this.targetPorts.addAll(targetPorts);
    }

    @Override
    public String toString() {
        return "ConnectedExternalSystemTopologyNode{" +
                "owningOrganizationName='" + owningOrganizationName + '\'' +
                ", solutionName='" + solutionName + '\'' +
                ", subsystemName='" + subsystemName + '\'' +
                ", subsystemVersion='" + subsystemVersion + '\'' +
                ", externalisedServiceName='" + externalisedServiceName + '\'' +
                ", externalisedServiceEndpointName='" + externalisedServiceEndpointName + '\'' +
                ", targetPorts=" + targetPorts +
                '}';
    }
}
