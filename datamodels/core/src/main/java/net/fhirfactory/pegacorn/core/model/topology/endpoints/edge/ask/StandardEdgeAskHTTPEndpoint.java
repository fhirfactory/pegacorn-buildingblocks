/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.ask;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentSystemRoleEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.HTTPClientAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.StandardInteractClientTopologyEndpointPort;

import java.util.ArrayList;
import java.util.List;

public class StandardEdgeAskHTTPEndpoint extends StandardInteractClientTopologyEndpointPort {

    //
    // Constructor(s)
    //

    public StandardEdgeAskHTTPEndpoint(){
        super();
        setEndpointType(PetasosEndpointTopologyTypeEnum.HTTP_API_CLIENT);
        setComponentSystemRole(SoftwareComponentSystemRoleEnum.COMPONENT_ROLE_SUBSYSTEM_EDGE);
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public List<HTTPClientAdapter> getHTTPClientAdapters() {
        List<HTTPClientAdapter> httpAdapterList = new ArrayList<>();
        for(IPCAdapter currentInterface: getAdapterList()){
            HTTPClientAdapter currentClientAdapter = (HTTPClientAdapter)currentInterface;
            httpAdapterList.add(currentClientAdapter);
        }
        return httpAdapterList;
    }

    @JsonIgnore
    public void setHTTPClientAdapters(List<HTTPClientAdapter> targetHTTPClientAdapters) {
        if(targetHTTPClientAdapters != null) {
            this.getAdapterList().clear();
            this.getAdapterList().addAll(targetHTTPClientAdapters);
        }
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "StandardEdgeAskHTTPEndpoint{" +
                "componentFDN=" + getComponentFDN() +
                ", kubernetesDeployed=" + isKubernetesDeployed() +
                ", otherConfigurationParameters=" + getOtherConfigurationParameters() +
                ", concurrencyMode=" + getConcurrencyMode() +
                ", resilienceMode=" + getResilienceMode() +
                ", securityZone=" + getSecurityZone() +
                ", componentID=" + getComponentID() +
                ", nodeFunctionFDN=" + getNodeFunctionFDN() +
                ", componentType=" + getComponentType() +
                ", containingNodeFDN=" + getContainingNodeFDN() +
                ", actualHostIP='" + getActualHostIP() + '\'' +
                ", actualPodIP='" + getActualPodIP() + '\'' +
                ", componentRDN=" + getComponentRDN() +
                ", metrics=" + getMetrics() +
                ", componentSystemRole=" + getComponentSystemRole() +
                ", endpointConfigurationName='" + getEndpointConfigurationName() + '\'' +
                ", server=" + isServer() +
                ", implementingWUP=" + getImplementingWUP() +
                ", connectedSystemName='" + getConnectedSystemName() + '\'' +
                ", endpointType=" + getEndpointType() +
                ", adapterList=" + getAdapterList() +
                ", HTTPClientAdapters=" + getHTTPClientAdapters() +
                ", targetSystem=" + getTargetSystem() +
                '}';
    }
}
