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
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.PetasosEndpoint;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.wup.datatypes.PetasosRedirectionControl;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WorkUnitProcessorSoftwareComponent extends SoftwareComponent {
    private static final Logger LOG = LoggerFactory.getLogger(WorkUnitProcessorSoftwareComponent.class);

    private ArrayList<TopologyNodeFDN> wupComponents;
    private ArrayList<TopologyNodeFDN> wupInterchangeComponents;
    private IPCTopologyEndpoint ingresEndpoint;
    private IPCTopologyEndpoint egressEndpoint;
    private Map<PetasosEndpointFunctionTypeEnum, PetasosEndpoint> serviceEndpoints;
    private Integer replicationCount;
    private PetasosRedirectionControl redirectionControl;

    //
    // Constructor(s)
    //

    public WorkUnitProcessorSoftwareComponent(){
        this.wupComponents = new ArrayList<>();
        this.wupInterchangeComponents = new ArrayList<>();
        this.ingresEndpoint = null;
        this.egressEndpoint = null;
        this.replicationCount = null;
        this.serviceEndpoints = new HashMap<>();
        this.redirectionControl = null;
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasRedirectionControl(){
        boolean hasValue = this.redirectionControl != null;
        return(hasValue);
    }

    public PetasosRedirectionControl getRedirectionControl() {
        return redirectionControl;
    }

    public void setRedirectionControl(PetasosRedirectionControl redirectionControl) {
        this.redirectionControl = redirectionControl;
    }

    @JsonIgnore
    public boolean hasServiceEndpoints(){
        boolean hasValue = this.serviceEndpoints != null;
        if(hasValue){
            boolean hasValues = this.serviceEndpoints.isEmpty() != true;
            return(hasValues);
        }
        return(hasValue);
    }

    public Map<PetasosEndpointFunctionTypeEnum, PetasosEndpoint> getServiceEndpoints() {
        return serviceEndpoints;
    }

    public void setServiceEndpoints(Map<PetasosEndpointFunctionTypeEnum, PetasosEndpoint> serviceEndpoints) {
        this.serviceEndpoints = serviceEndpoints;
    }

    @JsonIgnore
    public boolean hasReplicationCount(){
        boolean hasValue = this.replicationCount != null;
        return(hasValue);
    }


    public Integer getReplicationCount() {
        return replicationCount;
    }

    public void setReplicationCount(Integer replicationCount) {
        this.replicationCount = replicationCount;
    }

    @JsonIgnore
    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ArrayList<TopologyNodeFDN> getWupComponents() {
        return wupComponents;
    }

    public void setWupComponents(ArrayList<TopologyNodeFDN> wupComponents) {
        this.wupComponents = wupComponents;
    }

    public ArrayList<TopologyNodeFDN> getWupInterchangeComponents() {
        return wupInterchangeComponents;
    }

    public void setWupInterchangeComponents(ArrayList<TopologyNodeFDN> wupInterchangeComponents) {
        this.wupInterchangeComponents = wupInterchangeComponents;
    }

    public IPCTopologyEndpoint getIngresEndpoint() {
        return ingresEndpoint;
    }

    public void setIngresEndpoint(IPCTopologyEndpoint ingresEndpoint) {
        this.ingresEndpoint = ingresEndpoint;
    }

    public IPCTopologyEndpoint getEgressEndpoint() {
        return egressEndpoint;
    }

    public void setEgressEndpoint(IPCTopologyEndpoint egressEndpoint) {
        this.egressEndpoint = egressEndpoint;
    }

    //
    // To String
    //


    @Override
    public String toString() {
        return "WorkUnitProcessorSoftwareComponent{" +
                "deploymentSite='" + getDeploymentSite() + '\'' +
                ", lastActivityInstant=" + getLastActivityInstant() +
                ", lastReportingInstant=" + getLastReportingInstant() +
                ", subsystemParticipantName='" + getSubsystemParticipantName() + '\'' +
                ", componentFDN=" + getComponentFDN() +
                ", kubernetesDeployed=" + isKubernetesDeployed() +
                ", otherConfigurationParameters=" + getOtherConfigurationParameters() +
                ", concurrencyMode=" + getConcurrencyMode() +
                ", resilienceMode=" + getResilienceMode() +
                ", securityZone=" + getSecurityZone() +
                ", componentID=" + getComponentID() +
                ", nodeFunctionFDN=" + getNodeFunctionFDN() +
                ", componentType=" + getComponentType() +
                ", containingNodeFDN=" + getContainingNodeFDN() +
                ", componentRDN=" + getComponentRDN() +
                ", metrics=" + getMetrics() +
                ", componentSystemRole=" + getComponentSystemRole() +
                ", componentStatus=" + getComponentStatus() +
                ", componentExecutionControl=" + getComponentExecutionControl() +
                ", wupComponents=" + wupComponents +
                ", participantName='" + getParticipantName() + '\'' +
                ", wupInterchangeComponents=" + wupInterchangeComponents +
                ", ingresEndpoint=" + ingresEndpoint +
                ", egressEndpoint=" + egressEndpoint +
                ", replicationCount=" + replicationCount +
                ", redirectionStatus=" + getRedirectionControl() +
                '}';
    }
}
