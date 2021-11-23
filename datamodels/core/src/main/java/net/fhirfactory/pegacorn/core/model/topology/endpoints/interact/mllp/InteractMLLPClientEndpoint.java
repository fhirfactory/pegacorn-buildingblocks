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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentSystemRoleEnum;
import net.fhirfactory.pegacorn.core.model.topology.connector.ActiveIPCConnection;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.StandardInteractClientTopologyEndpointPort;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.adapters.MLLPClientAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class InteractMLLPClientEndpoint extends StandardInteractClientTopologyEndpointPort implements ActiveIPCConnection {

    //
    // Constructor(s)
    //

    public InteractMLLPClientEndpoint(){
        super();
        setEndpointType(PetasosEndpointTopologyTypeEnum.INTERACT_MLLP_CLIENT);
        setComponentSystemRole(SoftwareComponentSystemRoleEnum.COMPONENT_ROLE_INTERACT_EGRESS);
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public List<MLLPClientAdapter> getMLLPClientAdapters() {
        List<MLLPClientAdapter> mllpAdapterList = new ArrayList<>();
        for(IPCAdapter currentInterface: getAdapterList()){
            MLLPClientAdapter currentClientAdapter = (MLLPClientAdapter)currentInterface;
            mllpAdapterList.add(currentClientAdapter);
        }
        return mllpAdapterList;
    }

    @JsonIgnore
    public void setMLLPClientAdapters(List<MLLPClientAdapter> targetMLLPClientAdapters) {
        if(targetMLLPClientAdapters != null) {
            this.getAdapterList().clear();
            this.getAdapterList().addAll(targetMLLPClientAdapters);
        }
    }

    @JsonIgnore
    @Override
    public String getTargetSystemName() {
        return(getConnectedSystemName());
    }

    @JsonIgnore
    @Override
    public String getTargetConnectionDescription() {
        if(getMLLPClientAdapters().isEmpty()){
            return(null);
        }
        MLLPClientAdapter activePort = null;
        for(MLLPClientAdapter currentPort: getMLLPClientAdapters()){
            if(currentPort.isActive()){
                activePort = currentPort;
                break;
            }
        }
        if(activePort == null){
            return(null);
        }
        String portDescription = "mllp://"+activePort.getHostName()+":"+activePort.getPortNumber();
        return(portDescription);
    }

    @JsonIgnore
    @Override
    public String getDetailedConnectionDescription() {
        if(getMLLPClientAdapters().isEmpty()){
            return(null);
        }
        MLLPClientAdapter activePort = null;
        for(MLLPClientAdapter currentPort: getMLLPClientAdapters()){
            if(currentPort.isActive()){
                activePort = currentPort;
                break;
            }
        }
        if(activePort == null){
            return(null);
        }
        StringBuilder portDescription = new StringBuilder();
        portDescription.append("mllp://"+activePort.getHostName()+":"+activePort.getPortNumber());
        if(activePort.getAdditionalParameters().isEmpty()){
            return(portDescription.toString());
        }
        portDescription.append("?");
        Set<String> configurationParameterNames = activePort.getAdditionalParameters().keySet();
        int size = configurationParameterNames.size();
        int count = 0;
        for(String currentParameterName: configurationParameterNames){
            String value = activePort.getAdditionalParameters().get(currentParameterName);
            portDescription.append(currentParameterName + "=" + value);
            if(count < (size-1)){
                portDescription.append("&");
            }
            count += 1;
        }
        return(portDescription.toString());
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "InteractMLLPClientEndpoint{" +
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
                ", server=" + isServer() +
                ", implementingWUP=" + getImplementingWUP() +
                ", connectedSystemName='" + getConnectedSystemName() + '\'' +
                ", endpointType=" + getEndpointType() +
                ", adapterList=" + getAdapterList() +
                ", targetSystem=" + getTargetSystem() +
                ", targetMLLPPorts=" + getMLLPClientAdapters() +
                ", targetSystemName='" + getTargetSystemName() + '\'' +
                ", targetConnectionDescription='" + getTargetConnectionDescription() + '\'' +
                ", detailedConnectionDescription='" + getDetailedConnectionDescription() + '\'' +
                '}';
    }
}
