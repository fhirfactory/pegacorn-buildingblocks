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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.component.valuesets.AdditionalParametersListEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class IPCTopologyEndpoint extends SoftwareComponent {
    private static final Logger LOG = LoggerFactory.getLogger(IPCTopologyEndpoint.class);

    private String name;
    private PetasosEndpointTopologyTypeEnum endpointType;
    private Map<AdditionalParametersListEnum, String> additionalParameters;
    private String connectedSystemName;
    private Boolean encrypted;
    private TopologyNodeFDN implementingWUP;

    public IPCTopologyEndpoint(){
        super();
        encrypted = false;
        this.additionalParameters = new HashMap<>();
    }

    public TopologyNodeFDN getImplementingWUP() {
        return implementingWUP;
    }

    public void setImplementingWUP(TopologyNodeFDN implementingWUP) {
        this.implementingWUP = implementingWUP;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
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

    public PetasosEndpointTopologyTypeEnum getEndpointType() {
        return endpointType;
    }

    public void setEndpointType(PetasosEndpointTopologyTypeEnum endpointType) {
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
                ", otherConfigurationParameters=" + getOtherConfigurationParameters() +
                ", name=" + name +
                ", endpointType=" + endpointType +
                ", additionalParameters=" + additionalParameters +
                ", connectedSystemName=" + connectedSystemName +
                ", encrypted=" + encrypted +
                ", implementingWUP=" + implementingWUP +
                ", actualHostIP=" + getActualHostIP() +
                '}';
    }
}
