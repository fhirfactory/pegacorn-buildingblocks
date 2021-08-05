package net.fhirfactory.pegacorn.components.deployment.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.common.EndpointProviderInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ProcessingPlantTopologyNode extends TopologyNode implements EndpointProviderInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPlantTopologyNode.class);

    private ArrayList<TopologyNodeFDN> workshops;
    private ArrayList<TopologyNodeFDN> endpoints;
    private ArrayList<TopologyNodeFDN> connections;
    private String nameSpace;

    private String interSiteIPCStackConfigFile;
    private String interSiteOAMStackConfigFile;
    private String interSiteTaskingStackConfigFile;
    private String interZoneIPCStackConfigFile;
    private String interZoneOAMStackConfigFile;
    private String interZoneTaskingStackConfigFile;
    private String intraZoneIPCStackConfigFile;
    private String intraZoneOAMStackConfigFile;
    private String intraZoneTaskingStackConfigFile;

    private String defaultDNSName;
    private boolean internalTrafficEncrypted;
    private Integer instanceCount;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ProcessingPlantTopologyNode(){
        this.workshops = new ArrayList<>();
        this.endpoints = new ArrayList<>();
        this.connections = new ArrayList<>();

        this.nameSpace = null;
        this.defaultDNSName = null;
        this.internalTrafficEncrypted = false;

        this.interSiteIPCStackConfigFile = null;
        this.interSiteOAMStackConfigFile = null;
        this.interSiteTaskingStackConfigFile = null;
        this.interZoneIPCStackConfigFile = null;
        this.interZoneOAMStackConfigFile = null;
        this.interZoneTaskingStackConfigFile = null;
        this.intraZoneIPCStackConfigFile = null;
        this.intraZoneOAMStackConfigFile = null;
        this.intraZoneTaskingStackConfigFile = null;
    }

    public String getInterSiteIPCStackConfigFile() {
        return interSiteIPCStackConfigFile;
    }

    public void setInterSiteIPCStackConfigFile(String interSiteIPCStackConfigFile) {
        this.interSiteIPCStackConfigFile = interSiteIPCStackConfigFile;
    }

    public String getInterSiteOAMStackConfigFile() {
        return interSiteOAMStackConfigFile;
    }

    public void setInterSiteOAMStackConfigFile(String interSiteOAMStackConfigFile) {
        this.interSiteOAMStackConfigFile = interSiteOAMStackConfigFile;
    }

    public String getInterSiteTaskingStackConfigFile() {
        return interSiteTaskingStackConfigFile;
    }

    public void setInterSiteTaskingStackConfigFile(String interSiteTaskingStackConfigFile) {
        this.interSiteTaskingStackConfigFile = interSiteTaskingStackConfigFile;
    }

    public String getInterZoneTaskingStackConfigFile() {
        return interZoneTaskingStackConfigFile;
    }

    public void setInterZoneTaskingStackConfigFile(String interZoneTaskingStackConfigFile) {
        this.interZoneTaskingStackConfigFile = interZoneTaskingStackConfigFile;
    }

    public String getIntraZoneTaskingStackConfigFile() {
        return intraZoneTaskingStackConfigFile;
    }

    public void setIntraZoneTaskingStackConfigFile(String intraZoneTaskingStackConfigFile) {
        this.intraZoneTaskingStackConfigFile = intraZoneTaskingStackConfigFile;
    }

    public String getInterZoneIPCStackConfigFile() {
        return interZoneIPCStackConfigFile;
    }

    public void setInterZoneIPCStackConfigFile(String interZoneIPCStackConfigFile) {
        this.interZoneIPCStackConfigFile = interZoneIPCStackConfigFile;
    }

    public String getInterZoneOAMStackConfigFile() {
        return interZoneOAMStackConfigFile;
    }

    public void setInterZoneOAMStackConfigFile(String interZoneOAMStackConfigFile) {
        this.interZoneOAMStackConfigFile = interZoneOAMStackConfigFile;
    }

    public boolean isInternalTrafficEncrypted() {
        return internalTrafficEncrypted;
    }

    public void setInternalTrafficEncrypted(boolean internalTrafficEncrypted) {
        this.internalTrafficEncrypted = internalTrafficEncrypted;
    }

    public ArrayList<TopologyNodeFDN> getWorkshops() {
        return workshops;
    }

    public void setWorkshops(ArrayList<TopologyNodeFDN> workshops) {
        this.workshops = workshops;
    }

    public ArrayList<TopologyNodeFDN> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(ArrayList<TopologyNodeFDN> endpoints) {
        this.endpoints = endpoints;
    }

    public ArrayList<TopologyNodeFDN> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<TopologyNodeFDN> connections) {
        this.connections = connections;
    }

    public String getDefaultDNSName() {
        return defaultDNSName;
    }

    public void setDefaultDNSName(String defaultDNSName) {
        this.defaultDNSName = defaultDNSName;
    }

    public Integer getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(Integer instanceCount) {
        this.instanceCount = instanceCount;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    @Override
    public void addEndpoint(TopologyNodeFDN endpointFDN) {
        endpoints.add(endpointFDN);
    }

    public String getIntraZoneIPCStackConfigFile() {
        return intraZoneIPCStackConfigFile;
    }

    public void setIntraZoneIPCStackConfigFile(String intraZoneIPCStackConfigFile) {
        this.intraZoneIPCStackConfigFile = intraZoneIPCStackConfigFile;
    }

    public String getIntraZoneOAMStackConfigFile() {
        return intraZoneOAMStackConfigFile;
    }

    public void setIntraZoneOAMStackConfigFile(String intraZoneOAMStackConfigFile) {
        this.intraZoneOAMStackConfigFile = intraZoneOAMStackConfigFile;
    }

    @JsonIgnore
    public String getSubsystemName(){
        TopologyNodeFDN nodeFDN = getNodeFDN();
        TopologyNodeRDN subsystemRDN = nodeFDN.extractRDNForNodeType(TopologyNodeTypeEnum.SUBSYSTEM);
        String subsystemName = subsystemRDN.getNodeName();
        return(subsystemName);
    }

    @JsonIgnore
    public String getClusterServiceName(){
        TopologyNodeFDN nodeFDN = getNodeFDN();
        TopologyNodeRDN subsystemRDN = nodeFDN.extractRDNForNodeType(TopologyNodeTypeEnum.CLUSTER_SERVICE);
        String subsystemName = subsystemRDN.getNodeName();
        return(subsystemName);
    }

    @Override
    public String toString() {
        return "ProcessingPlantTopologyNode{" +
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
                ", workshops=" + workshops +
                ", endpoints=" + endpoints +
                ", connections=" + connections +
                ", nameSpace='" + nameSpace + '\'' +
                ", interSiteIPCStackConfigFile='" + interSiteIPCStackConfigFile + '\'' +
                ", interSiteOAMStackConfigFile='" + interSiteOAMStackConfigFile + '\'' +
                ", interSiteTaskingStackConfigFile='" + interSiteTaskingStackConfigFile + '\'' +
                ", interZoneIPCStackConfigFile='" + interZoneIPCStackConfigFile + '\'' +
                ", interZoneOAMStackConfigFile='" + interZoneOAMStackConfigFile + '\'' +
                ", interZoneTaskingStackConfigFile='" + interZoneTaskingStackConfigFile + '\'' +
                ", intraZoneIPCStackConfigFile='" + intraZoneIPCStackConfigFile + '\'' +
                ", intraZoneOAMStackConfigFile='" + intraZoneOAMStackConfigFile + '\'' +
                ", intreZoneTaskingStackConfigFile'" + intraZoneTaskingStackConfigFile + '\'' +
                ", defaultDNSName='" + defaultDNSName + '\'' +
                ", internalTrafficEncrypted=" + internalTrafficEncrypted +
                ", instanceCount=" + instanceCount +
                ", subsystemName='" + getSubsystemName() + '\'' +
                ", clusterServiceName='" + getClusterServiceName() + '\'' +
                '}';
    }
}
