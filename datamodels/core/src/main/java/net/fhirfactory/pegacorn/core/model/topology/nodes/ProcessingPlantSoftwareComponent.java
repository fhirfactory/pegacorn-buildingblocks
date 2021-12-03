/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.core.model.topology.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.componentid.PegacornSystemComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.common.EndpointProviderInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ProcessingPlantSoftwareComponent extends SoftwareComponent implements EndpointProviderInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPlantSoftwareComponent.class);

    private ArrayList<TopologyNodeFDN> workshops;
    private ArrayList<TopologyNodeFDN> endpoints;
    private ArrayList<TopologyNodeFDN> connections;
    private String nameSpace;
    private String interZoneIPCStackConfigFile;
    private String interZoneTopologyStackConfigFile;
    private String interZoneSubscriptionsStackConfigFile;
    private String interZoneMetricsStackConfigFile;
    private String interZoneInterceptionStackConfigFile;
    private String interZoneTaskingStackConfigFile;
    private String interZoneAuditStackConfigFile;

    private String multiZoneInfinispanStackConfigFile;

    private String intraZoneIPCStackConfigFile;
    private String intraZoneTopologyStackConfigFile;
    private String intraZoneSubscriptionsStackConfigFile;
    private String intraZoneMetricsStackConfigFile;
    private String intraZoneInterceptionStackConfigFile;
    private String intraZoneTaskingStackConfigFile;
    private String intraZoneAuditStackConfigFile;

    private String defaultDNSName;
    private boolean internalTrafficEncrypted;
    private Integer instanceCount;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    //
    // Constructor(s)
    //

    public ProcessingPlantSoftwareComponent(){
        this.workshops = new ArrayList<>();
        this.endpoints = new ArrayList<>();
        this.connections = new ArrayList<>();

        this.nameSpace = null;
        this.defaultDNSName = null;
        this.internalTrafficEncrypted = false;

        this.interZoneIPCStackConfigFile = null;
        this.interZoneSubscriptionsStackConfigFile = null;
        this.interZoneMetricsStackConfigFile = null;
        this.interZoneInterceptionStackConfigFile = null;
        this.interZoneTaskingStackConfigFile = null;
        this.interZoneTopologyStackConfigFile = null;
        this.interZoneAuditStackConfigFile = null;

        this.multiZoneInfinispanStackConfigFile = null;

        this.intraZoneIPCStackConfigFile = null;
        this.intraZoneTopologyStackConfigFile = null;
        this.intraZoneSubscriptionsStackConfigFile = null;
        this.intraZoneMetricsStackConfigFile = null;
        this.intraZoneInterceptionStackConfigFile = null;
        this.intraZoneTaskingStackConfigFile = null;
        this.intraZoneAuditStackConfigFile = null;
    }

    //
    // Getters and Setters
    //

    public String getInterZoneMetricsStackConfigFile() {
        return interZoneMetricsStackConfigFile;
    }

    public void setInterZoneMetricsStackConfigFile(String interZoneMetricsStackConfigFile) {
        this.interZoneMetricsStackConfigFile = interZoneMetricsStackConfigFile;
    }

    public String getInterZoneInterceptionStackConfigFile() {
        return interZoneInterceptionStackConfigFile;
    }

    public void setInterZoneInterceptionStackConfigFile(String interZoneInterceptionStackConfigFile) {
        this.interZoneInterceptionStackConfigFile = interZoneInterceptionStackConfigFile;
    }

    public String getInterZoneTaskingStackConfigFile() {
        return interZoneTaskingStackConfigFile;
    }

    public void setInterZoneTaskingStackConfigFile(String interZoneTaskingStackConfigFile) {
        this.interZoneTaskingStackConfigFile = interZoneTaskingStackConfigFile;
    }

    public String getInterZoneAuditStackConfigFile() {
        return interZoneAuditStackConfigFile;
    }

    public void setInterZoneAuditStackConfigFile(String interZoneAuditStackConfigFile) {
        this.interZoneAuditStackConfigFile = interZoneAuditStackConfigFile;
    }

    public String getIntraZoneMetricsStackConfigFile() {
        return intraZoneMetricsStackConfigFile;
    }

    public void setIntraZoneMetricsStackConfigFile(String intraZoneMetricsStackConfigFile) {
        this.intraZoneMetricsStackConfigFile = intraZoneMetricsStackConfigFile;
    }

    public String getIntraZoneInterceptionStackConfigFile() {
        return intraZoneInterceptionStackConfigFile;
    }

    public void setIntraZoneInterceptionStackConfigFile(String intraZoneInterceptionStackConfigFile) {
        this.intraZoneInterceptionStackConfigFile = intraZoneInterceptionStackConfigFile;
    }

    public String getIntraZoneTaskingStackConfigFile() {
        return intraZoneTaskingStackConfigFile;
    }

    public void setIntraZoneTaskingStackConfigFile(String intraZoneTaskingStackConfigFile) {
        this.intraZoneTaskingStackConfigFile = intraZoneTaskingStackConfigFile;
    }

    public String getIntraZoneAuditStackConfigFile() {
        return intraZoneAuditStackConfigFile;
    }

    public void setIntraZoneAuditStackConfigFile(String intraZoneAuditStackConfigFile) {
        this.intraZoneAuditStackConfigFile = intraZoneAuditStackConfigFile;
    }

    public String getInterZoneIPCStackConfigFile() {
        return interZoneIPCStackConfigFile;
    }

    public void setInterZoneIPCStackConfigFile(String interZoneIPCStackConfigFile) {
        this.interZoneIPCStackConfigFile = interZoneIPCStackConfigFile;
    }

    public String getInterZoneTopologyStackConfigFile() {
        return interZoneSubscriptionsStackConfigFile;
    }

    public void setInterZoneTopologyStackConfigFile(String interZoneTopologyStackConfigFile) {
        this.interZoneSubscriptionsStackConfigFile = interZoneTopologyStackConfigFile;
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

    public String getIntraZoneTopologyStackConfigFile() {
        return intraZoneTopologyStackConfigFile;
    }

    public void setIntraZoneTopologyStackConfigFile(String intraZoneTopologyStackConfigFile) {
        this.intraZoneTopologyStackConfigFile = intraZoneTopologyStackConfigFile;
    }

    public String getInterZoneSubscriptionsStackConfigFile() {
        return interZoneSubscriptionsStackConfigFile;
    }

    public void setInterZoneSubscriptionsStackConfigFile(String interZoneSubscriptionsStackConfigFile) {
        this.interZoneSubscriptionsStackConfigFile = interZoneSubscriptionsStackConfigFile;
    }

    public String getMultiZoneInfinispanStackConfigFile() {
        return multiZoneInfinispanStackConfigFile;
    }

    public void setMultiZoneInfinispanStackConfigFile(String multiZoneInfinispanStackConfigFile) {
        this.multiZoneInfinispanStackConfigFile = multiZoneInfinispanStackConfigFile;
    }

    public String getIntraZoneSubscriptionsStackConfigFile() {
        return intraZoneSubscriptionsStackConfigFile;
    }

    public void setIntraZoneSubscriptionsStackConfigFile(String intraZoneSubscriptionsStackConfigFile) {
        this.intraZoneSubscriptionsStackConfigFile = intraZoneSubscriptionsStackConfigFile;
    }

    @JsonIgnore
    public String getSubsystemName(){
        TopologyNodeFDN nodeFDN = getComponentFDN();
        TopologyNodeRDN subsystemRDN = nodeFDN.extractRDNForNodeType(PegacornSystemComponentTypeTypeEnum.SUBSYSTEM);
        String subsystemName = subsystemRDN.getNodeName();
        return(subsystemName);
    }

    @JsonIgnore
    public String getClusterServiceName(){
        TopologyNodeFDN nodeFDN = getComponentFDN();
        TopologyNodeRDN subsystemRDN = nodeFDN.extractRDNForNodeType(PegacornSystemComponentTypeTypeEnum.CLUSTER_SERVICE);
        String subsystemName = subsystemRDN.getNodeName();
        return(subsystemName);
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "ProcessingPlantTopologyNode{" +
                "nodeRDN=" + getComponentRDN() +
                ", nodeFDN=" + getComponentFDN() +
                ", componentType=" + getComponentType() +
                ", containingNodeFDN=" + getContainingNodeFDN() +
                ", nodeKey=" + getComponentID() +
                ", nodeFunctionFDN=" + getNodeFunctionFDN() +
                ", concurrencyMode=" + getConcurrencyMode() +
                ", resilienceMode=" + getResilienceMode() +
                ", securityZone=" + getSecurityZone() +
                ", kubernetesDeployed=" + isKubernetesDeployed() +
                ", workshops=" + workshops +
                ", endpoints=" + endpoints +
                ", connections=" + connections +
                ", nameSpace=" + nameSpace +
                ", interZoneIPCStackConfigFile=" + interZoneIPCStackConfigFile +
                ", intraZoneIPCStackConfigFile=" + intraZoneIPCStackConfigFile +
                ", interZoneSubscriptionsStackConfigFile=" + interZoneSubscriptionsStackConfigFile +
                ", intraZoneSubscriptionsStackConfigFile=" + intraZoneSubscriptionsStackConfigFile +
                ", interZoneTopologyStackConfigFile=" + interZoneTopologyStackConfigFile +
                ", intraZoneTopologyStackConfigFile=" + intraZoneTopologyStackConfigFile +
                ", interZoneMetricsStackConfigFile=" + interZoneMetricsStackConfigFile +
                ", intraZoneMetricsStackConfigFile=" + intraZoneMetricsStackConfigFile +
                ", interZoneInterceptionStackConfigFile=" + interZoneInterceptionStackConfigFile +
                ", intraZoneInterceptionStackConfigFile=" + intraZoneInterceptionStackConfigFile +
                ", interZoneTaskStackConfigFile=" + interZoneTaskingStackConfigFile +
                ", intraZoneTaskStackConfigFile=" + intraZoneTaskingStackConfigFile +
                ", interZoneAuditStackConfigFile=" + interZoneAuditStackConfigFile +
                ", intraZoneAuditStackConfigFile=" + intraZoneAuditStackConfigFile +
                ", multiZoneInfinispanStackConfigFile=" + multiZoneInfinispanStackConfigFile +
                ", defaultDNSName=" + defaultDNSName +
                ", internalTrafficEncrypted=" + internalTrafficEncrypted +
                ", instanceCount=" + instanceCount +
                ", subsystemName=" + getSubsystemName() +
                ", clusterServiceName=" + getClusterServiceName() +
                '}';
    }
}
