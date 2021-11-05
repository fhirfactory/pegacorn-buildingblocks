
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
package net.fhirfactory.pegacorn.petasos.oam.topology.factories;

import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.PetasosMonitoredEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCServerTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.InteractMLLPClientAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.InteractMLLPServerAdapter;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.oam.topology.factories.common.PetasosMonitoredComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PetasosMonitoredEndpointFactory extends PetasosMonitoredComponentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosMonitoredEndpointFactory.class);

    @Inject
    private TopologyIM topologyIM;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public PetasosMonitoredEndpoint newEndpoint(IPCTopologyEndpoint endpointTopologyNode){
        getLogger().debug(".newEndpoint(): Entry, endpointTopologyNode->{}", endpointTopologyNode);
        if(endpointTopologyNode == null){
            return(null);
        }
        PetasosMonitoredEndpoint endpoint = new PetasosMonitoredEndpoint();
        endpoint = (PetasosMonitoredEndpoint) newPetasosMonitoredComponent(endpoint, endpointTopologyNode);
        endpoint.setEndpointType(endpointTopologyNode.getEndpointType());
        endpoint.setEncrypted(endpointTopologyNode.isEncrypted());
        endpoint.setActualHostIP(endpointTopologyNode.getActualHostIP());
        endpoint.setActualPodIP(endpointTopologyNode.getActualPodIP());
        switch(endpointTopologyNode.getEndpointType()){
            case JGROUPS_INTRAZONE_SERVICE:
            case JGROUPS_INTERZONE_SERVICE:
            case JGROUPS_INTERSITE_SERVICE:{
                IPCServerTopologyEndpoint jgroupsEndpoint = (IPCServerTopologyEndpoint)endpointTopologyNode;
                endpoint.setLocalPort(Integer.toString(jgroupsEndpoint.getPortValue()));
                endpoint.setLocalDNSEntry(jgroupsEndpoint.getHostDNSName());
                break;
            }
            case MLLP_SERVER: {
                InteractMLLPServerAdapter mllpServerEndpoint = (InteractMLLPServerAdapter)endpointTopologyNode;
                endpoint.setLocalPort(Integer.toString(mllpServerEndpoint.getPortValue()));
                endpoint.setLocalDNSEntry(mllpServerEndpoint.getServerMLLPPort().getHostName());
                endpoint.setRemoteSystemName(mllpServerEndpoint.getConnectedSystemName());
                endpoint.setLocalServicePort(mllpServerEndpoint.getServerMLLPPort().getPort());
                endpoint.setLocalServiceDNSEntry(mllpServerEndpoint.getServiceDNSName());
                break;
            }
            case MLLP_CLIENT: {
                InteractMLLPClientAdapter mllpClientEndpoint = (InteractMLLPClientAdapter)endpointTopologyNode;
                endpoint.setRemoteSystemName(mllpClientEndpoint.getConnectedSystemName());
                endpoint.setRemoteDNSEntry(mllpClientEndpoint.getTargetMLLPPorts().get(0).getTargetName());
                endpoint.setRemotePort(mllpClientEndpoint.getTargetMLLPPorts().get(0).getPort());
                break;
            }
            case HTTP_API_SERVER:
                break;
            case HTTP_API_CLIENT:
                break;
            case SQL_SERVER:
                break;
            case SQL_CLIENT:
                break;
            case OTHER_API_SERVER:
                break;
            case OTHER_API_CLIENT:
                break;
            case OTHER_SERVER:
                break;
        }
        getLogger().debug(".newEndpoint(): Exit, endpoint->{}", endpoint);
        return(endpoint);
    }
}
