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
import net.fhirfactory.pegacorn.core.model.topology.connector.ActiveIPCConnection;
import net.fhirfactory.pegacorn.core.model.topology.nodes.external.ConnectedExternalSystemTopologyNode;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.ClusteredInteractServerTopologyEndpointPort;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.datatype.MLLPPort;

import java.util.Enumeration;

public class InteractMLLPServerAdapter extends ClusteredInteractServerTopologyEndpointPort implements ActiveIPCConnection {
    private ConnectedExternalSystemTopologyNode targetSystem;
    private MLLPPort serverMLLPPort;

    public InteractMLLPServerAdapter(){
        super();
        targetSystem = new ConnectedExternalSystemTopologyNode();
        serverMLLPPort = null;
    }

    public ConnectedExternalSystemTopologyNode getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(ConnectedExternalSystemTopologyNode targetSystem) {
        this.targetSystem = targetSystem;
    }

    @JsonIgnore
    public boolean hasMllpPort(){
        boolean hasValue = this.serverMLLPPort != null;
        return(hasValue);
    }

    public MLLPPort getServerMLLPPort() {
        return serverMLLPPort;
    }

    public void setServerMLLPPort(MLLPPort serverMLLPPort) {
        this.serverMLLPPort = serverMLLPPort;
    }

    @Override
    public String getTargetSystemName() {
        return(getConnectedSystemName());
    }

    @Override
    public String getTargetConnectionDescription() {
        if(hasMllpPort()) {
            String portDescription = "mllp://" + getServerMLLPPort().getHostName() + ":" + getServerMLLPPort().getPort();
            return(portDescription);
        }
        return(null);
    }

    @Override
    public String getDetailedConnectionDescription() {
        if(hasMllpPort()) {
            StringBuilder portDescription = new StringBuilder();
            portDescription.append("mllp://" + getServerMLLPPort().getHostName() + ":" + getServerMLLPPort().getPort());
            if (getServerMLLPPort().getConfigurationParameters().isEmpty()) {
                return (portDescription.toString());
            }
            portDescription.append("?");
            Enumeration<String> configurationParameterNames = getServerMLLPPort().getConfigurationParameters().keys();
            while (configurationParameterNames.hasMoreElements()) {
                String name = configurationParameterNames.nextElement();
                String value = getServerMLLPPort().getConfigurationParameters().get(name);
                portDescription.append(name + "=" + value);
                if (configurationParameterNames.hasMoreElements()) {
                    portDescription.append("&");
                }
            }
            return (portDescription.toString());
        }
        return(null);
    }
}
