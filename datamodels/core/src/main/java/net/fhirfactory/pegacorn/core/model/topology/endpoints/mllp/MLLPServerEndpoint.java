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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.mllp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentConnectivityContextEnum;
import net.fhirfactory.pegacorn.core.model.topology.connector.ActiveIPCConnection;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.ClusteredInteractServerTopologyEndpointPort;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.mllp.adapters.MLLPServerAdapter;

import java.util.Set;

public class MLLPServerEndpoint extends ClusteredInteractServerTopologyEndpointPort implements ActiveIPCConnection {

    //
    // Constructor(s)
    //
    public MLLPServerEndpoint(){
        super();
        setEndpointType(PetasosEndpointTopologyTypeEnum.MLLP_SERVER);
        setComponentSystemRole(SoftwareComponentConnectivityContextEnum.COMPONENT_ROLE_INTERACT_INGRES);
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public MLLPServerAdapter getMLLPServerAdapter(){
        if(getAdapterList().isEmpty()){
            return(null);
        }
        MLLPServerAdapter mllpServer = (MLLPServerAdapter) getAdapterList().get(0);
        return(mllpServer);
    }

    @JsonIgnore
    public void setMLLPServerAdapter(MLLPServerAdapter mllpServer){
        if(mllpServer != null){
            getAdapterList().add(mllpServer);
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
        if(!getAdapterList().isEmpty()) {
            MLLPServerAdapter mllpServer = (MLLPServerAdapter) getAdapterList().get(0);
            String portDescription = "mllp://" + mllpServer.getHostName() + ":" + mllpServer.getPortNumber();
            return(portDescription);
        }
        return(null);
    }

    @JsonIgnore
    @Override
    public String getDetailedConnectionDescription() {
        if(!getAdapterList().isEmpty()) {
            MLLPServerAdapter mllpServer = (MLLPServerAdapter) getAdapterList().get(0);
            StringBuilder portDescription = new StringBuilder();
            portDescription.append("mllp://" + mllpServer.getHostName() + ":" + mllpServer.getPortNumber());
            if (mllpServer.getAdditionalParameters().isEmpty()) {
                return (portDescription.toString());
            }
            portDescription.append("?");
            Set<String> configurationParameterNames = mllpServer.getAdditionalParameters().keySet();
            int size = configurationParameterNames.size();
            int count = 0;
            for(String currentConfigParameterName: configurationParameterNames){
                String value = mllpServer.getAdditionalParameters().get(currentConfigParameterName);
                portDescription.append(currentConfigParameterName + "=" + value);
                if (count < (size-1)) {
                    portDescription.append("&");
                }
                count += 1;
            }
            return (portDescription.toString());
        }
        return(null);
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "InteractMLLPServerEndpoint{" +
                "componentFDN=" + getComponentFDN() +
                ", kubernetesDeployed=" + isKubernetesDeployed() +
                ", otherConfigurationParameters=" + getOtherConfigurationParameters() +
                ", concurrencyMode=" + getConcurrencyMode() +
                ", resilienceMode=" + getResilienceMode() +
                ", securityZone=" + getSecurityZone() +
                ", hasComponentID=" + hasComponentID() +
                ", componentID=" + getComponentID() +
                ", hasNodeFunctionFDN=" + hasNodeFunctionFDN() +
                ", nodeFunctionFDN=" + getNodeFunctionFDN() +
                ", componentType=" + getComponentType() +
                ", hasContainingNodeFDN=" + hasContainingNodeFDN() +
                ", containingNodeFDN=" + getContainingNodeFDN() +
                ", actualHostIP='" + getActualHostIP() + '\'' +
                ", hasComponentRDN=" + hasComponentRDN() +
                ", componentRDN=" + getComponentRDN() +
                ", metrics=" + getMetrics() +
                ", componentSystemRole=" + getComponentSystemRole() +
                ", servicePortValue=" + getServicePortValue() +
                ", servicePortName='" + getServicePortName() + '\'' +
                ", servicePortOffset=" + getServicePortOffset() +
                ", serviceDNSName='" + getServiceDNSName() + '\'' +
                ", server=" + isServer() +
                ", implementingWUP=" + getImplementingWUP() +
                ", connectedSystemName='" + getConnectedSystemName() + '\'' +
                ", endpointType=" + getEndpointType() +
                ", adapterList=" + getAdapterList() +
                '}';
    }
}
