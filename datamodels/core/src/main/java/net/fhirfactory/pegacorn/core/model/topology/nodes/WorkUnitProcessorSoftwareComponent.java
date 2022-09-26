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
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.PetasosEndpoint;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkUnitProcessorSoftwareComponent extends SoftwareComponent {
    private static final Logger LOG = LoggerFactory.getLogger(WorkUnitProcessorSoftwareComponent.class);

    private ArrayList<ComponentIdType> wupComponents;
    private ArrayList<ComponentIdType> wupInterchangeComponents;
    private IPCTopologyEndpoint ingresEndpoint;
    private IPCTopologyEndpoint egressEndpoint;
    private Map<PetasosEndpointFunctionTypeEnum, PetasosEndpoint> serviceEndpoints;
    private Integer replicationCount;

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
    }

    //
    // Getters and Setters
    //

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

    public ArrayList<ComponentIdType> getWupComponents() {
        return wupComponents;
    }

    public void setWupComponents(ArrayList<ComponentIdType> wupComponents) {
        this.wupComponents = wupComponents;
    }

    public ArrayList<ComponentIdType> getWupInterchangeComponents() {
        return wupInterchangeComponents;
    }

    public void setWupInterchangeComponents(ArrayList<ComponentIdType> wupInterchangeComponents) {
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
        final StringBuilder sb = new StringBuilder("WorkUnitProcessorSoftwareComponent{");
        sb.append("wupComponents=").append(wupComponents);
        sb.append(", wupInterchangeComponents=").append(wupInterchangeComponents);
        sb.append(", ingresEndpoint=").append(ingresEndpoint);
        sb.append(", egressEndpoint=").append(egressEndpoint);
        sb.append(", serviceEndpoints=").append(serviceEndpoints);
        sb.append(", replicationCount=").append(replicationCount);
        sb.append(", ").append(super.toString()).append('}');
        return sb.toString();
    }
}
