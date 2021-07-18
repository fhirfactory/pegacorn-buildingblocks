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
package net.fhirfactory.pegacorn.platform.edge.endpoints.jgroups;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.TopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.edge.InitialHostSpecification;
import net.fhirfactory.pegacorn.platform.edge.endpoints.jgroups.common.JGroupsIPCPubSubSubscriberService;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.InterZoneEdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import org.apache.camel.ExchangePattern;
import org.jgroups.stack.ProtocolStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class JGroupsInterZoneIPCService extends JGroupsIPCPubSubSubscriberService {
    private static final Logger LOG = LoggerFactory.getLogger(JGroupsInterZoneIPCService.class);

    private static String GOSSIP_ROUTER_HOSTS = "hosts";

    public JGroupsInterZoneIPCService(){
        super();
    }

    @Inject
    private InterZoneEdgeForwarderService forwarderService;

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyGroupName() {
        return (getIPCComponentNames().getInterZoneIPCGroupName());
    }

    @Override
    protected String specifyIPCInterfaceName() {
        return (getInterfaceNames().getFunctionNameInterZoneJGroupsIPC());
    }

    @Override
    protected String specifyFileName() {
        return ("sitea.xml");
    }

    @Override
    protected TopologyEndpointTypeEnum specifyIPCType() {
        return (TopologyEndpointTypeEnum.JGROUPS_INTERZONE_IPC_MESSAGING_SERVICE);
    }

    @Override
    public EdgeForwarderService getEdgeForwarderService() {
        return (forwarderService);
    }

    @Override
    protected InterProcessingPlantHandoverResponsePacket injectMessageIntoRoute(InterProcessingPlantHandoverPacket handoverPacket) {
        InterProcessingPlantHandoverResponsePacket response = (InterProcessingPlantHandoverResponsePacket)getCamelProducer()
                .sendBody(getIPCComponentNames().getInterZoneIPCReceiverRouteEndpointName(), ExchangePattern.InOut, handoverPacket);
        return(response);
    }

    @Override
    protected ProtocolStack specifyProtocolStack() {
        getLogger().debug(".specifyProtocolStack(): Entry");
        getLogger().trace(".specifyProtocolStack(): Sourcing the bindPort");
        String bindPort = Integer.toString(getIPCTopologyEndpoint().getPortValue());
        getLogger().trace(".specifyProtocolStack(): bindPort sourced, value->{}", bindPort);
        getLogger().trace(".specifyProtocolStack(): Sourcing the bindAddress");
        String bindAddress = getIPCTopologyEndpoint().getHostDNSName();
        getLogger().trace(".specifyProtocolStack(): bindAddress sourced, value->{}", bindAddress);
        getLogger().trace(".specifyProtocolStack(): Sourcing the initialHosts");
        List<InitialHostSpecification> initialHosts = getIPCTopologyEndpoint().getInitialHosts();
        getLogger().trace(".specifyProtocolStack(): initialHosts sourced, value->{}", initialHosts);
        ProtocolStack stack = null;
        try {
            stack = getStacks().getInterZoneProtocolStack(bindAddress ,bindPort, initialHosts );
            getLogger().debug(".specifyProtocolStack(): Exit, stack->{}", stack);
            return(stack);
        } catch (Exception e) {
            getLogger().error(".specifyProtocolStack(): Cannot resolve JGroups Stack, error->{}", e.getMessage());
            return (null);
        }
    }

    @Override
    protected EdgeForwarderService specifyForwarderService() {
        return (forwarderService);
    }
}
