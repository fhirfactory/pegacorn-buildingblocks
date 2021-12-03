
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

import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCServerTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.datatypes.JGroupsAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.InteractMLLPClientEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.InteractMLLPServerEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.adapters.MLLPClientAdapter;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.EndpointSummary;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.PortSoftwareComponentSummary;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.SoftwareComponentSummary;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.oam.topology.factories.common.PetasosMonitoredComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PetasosEndpointSummaryFactory extends PetasosMonitoredComponentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosEndpointSummaryFactory.class);

    @Inject
    private TopologyIM topologyIM;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public SoftwareComponentSummary newEndpoint(IPCTopologyEndpoint endpointTopologyNode){
        getLogger().debug(".newEndpoint(): Entry, endpointTopologyNode->{}", endpointTopologyNode);
        if(endpointTopologyNode == null){
            return(null);
        }
        EndpointSummary endpoint = new EndpointSummary();
        endpoint = (EndpointSummary) newPetasosMonitoredComponent(endpoint, endpointTopologyNode);
        endpoint.setEndpointType(endpointTopologyNode.getEndpointType());
        boolean isEncrypted = false;
        for(IPCAdapter currentAdapter: endpointTopologyNode.getAdapterList()){
            if(currentAdapter.isEncrypted()){
                isEncrypted = true;
                break;
            }
        }
        switch(endpointTopologyNode.getEndpointType()){
            case EDGE_JGROUPS_INTRAZONE_SERVICE:
            case EDGE_JGROUPS_INTERZONE_SERVICE:
            case EDGE_JGROUPS_INTERSITE_SERVICE:{
                IPCServerTopologyEndpoint jgroupsEndpoint = (IPCServerTopologyEndpoint)endpointTopologyNode;
                JGroupsAdapter currentAdapter = (JGroupsAdapter) jgroupsEndpoint.getAdapterList().get(0);
                PortSoftwareComponentSummary portSummary = new PortSoftwareComponentSummary();
                portSummary.setEncrypted(isEncrypted);
                portSummary.setHostPort(Integer.toString(currentAdapter.getPortNumber()));
                portSummary.setHostDNSName(currentAdapter.getHostName());
                endpoint.getClientPorts().add(portSummary);
                break;
            }
            case INTERACT_MLLP_SERVER: {
                InteractMLLPServerEndpoint mllpServerEndpoint = (InteractMLLPServerEndpoint)endpointTopologyNode;
                if(mllpServerEndpoint.getMLLPServerAdapter() != null) {
                    PortSoftwareComponentSummary portSummary = new PortSoftwareComponentSummary();
                    portSummary.setHostPort(Integer.toString(mllpServerEndpoint.getMLLPServerAdapter().getPortNumber()));
                    portSummary.setHostDNSName(mllpServerEndpoint.getMLLPServerAdapter().getHostName());
                    endpoint.setConnectedSystemName(mllpServerEndpoint.getConnectedSystemName());
                    portSummary.setServicePort(Integer.toString(mllpServerEndpoint.getMLLPServerAdapter().getServicePortValue()));
                    portSummary.setServiceDNSName(mllpServerEndpoint.getMLLPServerAdapter().getServiceDNSName());
                    endpoint.getServerPorts().add(portSummary);
                }
                break;
            }
            case INTERACT_MLLP_CLIENT: {
                InteractMLLPClientEndpoint mllpClientEndpoint = (InteractMLLPClientEndpoint)endpointTopologyNode;
                if(mllpClientEndpoint.getMLLPClientAdapters().get(0) != null) {
                    MLLPClientAdapter clientAdapter = mllpClientEndpoint.getMLLPClientAdapters().get(0);
                    PortSoftwareComponentSummary portSummary = new PortSoftwareComponentSummary();
                    endpoint.setConnectedSystemName(mllpClientEndpoint.getConnectedSystemName());
                    portSummary.setHostDNSName(clientAdapter.getHostName());
                    portSummary.setHostPort(Integer.toString(clientAdapter.getPortNumber()));
                    endpoint.getServerPorts().add(portSummary);
                }
                break;
            }
            case INTERACT_HTTP_API_SERVER:
                break;
            case INTERACT_HTTP_API_CLIENT:
                break;
            case INTERACT_SQL_SERVER:
                break;
            case INTERACT_SQL_CLIENT:
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
