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

import net.fhirfactory.pegacorn.core.model.topology.connector.ActiveIPCConnection;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.StandardInteractClientTopologyEndpointPort;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.datatype.MLLPPort;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class InteractMLLPClientAdapter extends StandardInteractClientTopologyEndpointPort implements ActiveIPCConnection {
    private List<MLLPPort> targetMLLPPorts;

    public InteractMLLPClientAdapter(){
        super();
        targetMLLPPorts = new ArrayList<>();
    }

    public List<MLLPPort> getTargetMLLPPorts() {
        return targetMLLPPorts;
    }

    public void setTargetMLLPPorts(List<MLLPPort> targetMLLPPorts) {
        this.targetMLLPPorts = targetMLLPPorts;
    }

    @Override
    public String getTargetSystemName() {
        return(getConnectedSystemName());
    }

    @Override
    public String getTargetConnectionDescription() {
        if(getTargetMLLPPorts().isEmpty()){
            return(null);
        }
        MLLPPort activePort = null;
        for(MLLPPort currentPort: getTargetMLLPPorts()){
            if(currentPort.isActive()){
                activePort = currentPort;
                break;
            }
        }
        if(activePort == null){
            return(null);
        }
        String portDescription = "mllp://"+activePort.getHostName()+":"+activePort.getPort();
        return(portDescription);
    }

    @Override
    public String getDetailedConnectionDescription() {
        if(getTargetMLLPPorts().isEmpty()){
            return(null);
        }
        MLLPPort activePort = null;
        for(MLLPPort currentPort: getTargetMLLPPorts()){
            if(currentPort.isActive()){
                activePort = currentPort;
                break;
            }
        }
        if(activePort == null){
            return(null);
        }
        StringBuilder portDescription = new StringBuilder();
        portDescription.append("mllp://"+activePort.getHostName()+":"+activePort.getPort());
        if(activePort.getConfigurationParameters().isEmpty()){
            return(portDescription.toString());
        }
        portDescription.append("?");
        Enumeration<String> configurationParameterNames = activePort.getConfigurationParameters().keys();
        while(configurationParameterNames.hasMoreElements()){
            String name = configurationParameterNames.nextElement();
            String value = activePort.getConfigurationParameters().get(name);
            portDescription.append(name + "=" + value);
            if(configurationParameterNames.hasMoreElements()){
                portDescription.append("&");
            }
        }
        return(portDescription.toString());
    }
}
