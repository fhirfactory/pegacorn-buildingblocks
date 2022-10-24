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
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.SoftwareComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.common.EndpointProviderInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ProcessingPlantSoftwareComponent extends SoftwareComponent implements EndpointProviderInterface {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPlantSoftwareComponent.class);

    private ArrayList<ComponentIdType> workshops;
    private ArrayList<ComponentIdType> endpoints;
    private ArrayList<ComponentIdType> connections;
    private String nameSpace;
    private String petasosIPCStackConfigFile;
    private String petasosTopologyStackConfigFile;
    private String petasosSubscriptionsStackConfigFile;
    private String petasosMetricsStackConfigFile;
    private String petasosInterceptionStackConfigFile;
    private String petasosTaskingStackConfigFile;
    private String petasosAuditStackConfigFile;

    private String multiZoneInfinispanStackConfigFile;

    private String actualHostIP;
    private String actualPodIP;
    private String assignedDNSName;
    private boolean internalTrafficEncrypted;
    private Integer replicationCount;
    private String siteName;
    private String podName;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    //
    // Constructor(s)
    //

    public ProcessingPlantSoftwareComponent(){
        super();
        this.workshops = new ArrayList<>();
        this.endpoints = new ArrayList<>();
        this.connections = new ArrayList<>();

        this.nameSpace = null;
        this.assignedDNSName = null;
        this.actualHostIP = null;
        this.actualPodIP = null;
        this.podName = null;
        this.siteName = null;
        this.internalTrafficEncrypted = false;

        this.petasosIPCStackConfigFile = null;
        this.petasosSubscriptionsStackConfigFile = null;
        this.petasosMetricsStackConfigFile = null;
        this.petasosInterceptionStackConfigFile = null;
        this.petasosTaskingStackConfigFile = null;
        this.petasosTopologyStackConfigFile = null;
        this.petasosAuditStackConfigFile = null;

        this.multiZoneInfinispanStackConfigFile = null;

        setComponentType(SoftwareComponentTypeEnum.PROCESSING_PLANT);
    }

    //
    // Getters and Setters
    //

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getActualHostIP() {
        return actualHostIP;
    }

    public void setActualHostIP(String actualHostIP) {
        this.actualHostIP = actualHostIP;
    }

    public String getActualPodIP() {
        return actualPodIP;
    }

    public void setActualPodIP(String actualPodIP) {
        this.actualPodIP = actualPodIP;
    }

    public String getPetasosMetricsStackConfigFile() {
        return petasosMetricsStackConfigFile;
    }

    public void setPetasosMetricsStackConfigFile(String interZoneMetricsStackConfigFile) {
        this.petasosMetricsStackConfigFile = interZoneMetricsStackConfigFile;
    }

    public String getPetasosInterceptionStackConfigFile() {
        return petasosInterceptionStackConfigFile;
    }

    public void setPetasosInterceptionStackConfigFile(String interZoneInterceptionStackConfigFile) {
        this.petasosInterceptionStackConfigFile = interZoneInterceptionStackConfigFile;
    }

    public String getPetasosTaskingStackConfigFile() {
        return petasosTaskingStackConfigFile;
    }

    public void setPetasosTaskingStackConfigFile(String interZoneTaskingStackConfigFile) {
        this.petasosTaskingStackConfigFile = interZoneTaskingStackConfigFile;
    }

    public String getPetasosAuditStackConfigFile() {
        return petasosAuditStackConfigFile;
    }

    public void setPetasosAuditStackConfigFile(String interZoneAuditStackConfigFile) {
        this.petasosAuditStackConfigFile = interZoneAuditStackConfigFile;
    }

    public String getPetasosIPCStackConfigFile() {
        return petasosIPCStackConfigFile;
    }

    public void setPetasosIPCStackConfigFile(String interZoneIPCStackConfigFile) {
        this.petasosIPCStackConfigFile = interZoneIPCStackConfigFile;
    }

    public String getPetasosTopologyStackConfigFile() {
        return petasosTopologyStackConfigFile;
    }

    public void setPetasosTopologyStackConfigFile(String interZoneTopologyStackConfigFile) {
        this.petasosTopologyStackConfigFile = interZoneTopologyStackConfigFile;
    }

    public boolean isInternalTrafficEncrypted() {
        return internalTrafficEncrypted;
    }

    public void setInternalTrafficEncrypted(boolean internalTrafficEncrypted) {
        this.internalTrafficEncrypted = internalTrafficEncrypted;
    }

    public ArrayList<ComponentIdType> getWorkshops() {
        return workshops;
    }

    public void setWorkshops(ArrayList<ComponentIdType> workshops) {
        this.workshops = workshops;
    }

    public ArrayList<ComponentIdType> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(ArrayList<ComponentIdType> endpoints) {
        this.endpoints = endpoints;
    }

    public ArrayList<ComponentIdType> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<ComponentIdType> connections) {
        this.connections = connections;
    }

    public String getAssignedDNSName() {
        return assignedDNSName;
    }

    public void setAssignedDNSName(String assignedDNSName) {
        this.assignedDNSName = assignedDNSName;
    }

    public Integer getReplicationCount() {
        return replicationCount;
    }

    public void setReplicationCount(Integer replicationCount) {
        this.replicationCount = replicationCount;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    @Override
    public void addEndpoint(ComponentIdType endpointId) {
        endpoints.add(endpointId);
    }

    public String getPetasosSubscriptionsStackConfigFile() {
        return petasosSubscriptionsStackConfigFile;
    }

    public void setPetasosSubscriptionsStackConfigFile(String petasosSubscriptionsStackConfigFile) {
        this.petasosSubscriptionsStackConfigFile = petasosSubscriptionsStackConfigFile;
    }

    public String getMultiZoneInfinispanStackConfigFile() {
        return multiZoneInfinispanStackConfigFile;
    }

    public void setMultiZoneInfinispanStackConfigFile(String multiZoneInfinispanStackConfigFile) {
        this.multiZoneInfinispanStackConfigFile = multiZoneInfinispanStackConfigFile;
    }

    @JsonIgnore
    public String getClusterServiceName(){
        return(getParticipant().getParticipantId().getSubsystemName());
    }

    //
    // To String
    //


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProcessingPlantSoftwareComponent{");
        sb.append("workshops=").append(workshops);
        sb.append(", endpoints=").append(endpoints);
        sb.append(", connections=").append(connections);
        sb.append(", nameSpace='").append(nameSpace).append('\'');
        sb.append(", petasosIPCStackConfigFile='").append(petasosIPCStackConfigFile).append('\'');
        sb.append(", petasosTopologyStackConfigFile='").append(petasosTopologyStackConfigFile).append('\'');
        sb.append(", petasosSubscriptionsStackConfigFile='").append(petasosSubscriptionsStackConfigFile).append('\'');
        sb.append(", petasosMetricsStackConfigFile='").append(petasosMetricsStackConfigFile).append('\'');
        sb.append(", petasosInterceptionStackConfigFile='").append(petasosInterceptionStackConfigFile).append('\'');
        sb.append(", petasosTaskingStackConfigFile='").append(petasosTaskingStackConfigFile).append('\'');
        sb.append(", petasosAuditStackConfigFile='").append(petasosAuditStackConfigFile).append('\'');
        sb.append(", multiZoneInfinispanStackConfigFile='").append(multiZoneInfinispanStackConfigFile).append('\'');
        sb.append(", actualHostIP='").append(actualHostIP).append('\'');
        sb.append(", actualPodIP='").append(actualPodIP).append('\'');
        sb.append(", assignedDNSName='").append(assignedDNSName).append('\'');
        sb.append(", internalTrafficEncrypted=").append(internalTrafficEncrypted);
        sb.append(", replicationCount=").append(replicationCount);
        sb.append(", clusterServiceName='").append(getClusterServiceName()).append('\'');
        sb.append(", siteName=").append(siteName);
        sb.append(", podName=").append(podName);
        sb.append(", ").append(super.toString()).append('}');
        return sb.toString();
    }
}
