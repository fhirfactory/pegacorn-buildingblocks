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
package net.fhirfactory.pegacorn.platform.ipc.frameworks.messaging.jgroups.server;

import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.TopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.petasos.ipc.model.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.petasos.ipc.model.InterProcessingPlantHandoverResponsePacket;
import net.fhirfactory.pegacorn.platform.ipc.frameworks.messaging.jgroups.common.JGroupsEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.platform.ipc.frameworks.messaging.jgroups.client.JGroupsIPCSender;
import org.apache.camel.*;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class JGroupsEdgeReceiveIPCEndpoint extends JGroupsEdgeIPCEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(JGroupsEdgeReceiveIPCEndpoint.class);

    private ConcurrentHashMap<String, JGroupsIPCReceiver> receivers;

    public JGroupsEdgeReceiveIPCEndpoint(){
        super();
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    public ConcurrentHashMap<String, JGroupsIPCReceiver> getReceivers() {
        return receivers;
    }

    public void setReceivers(ConcurrentHashMap<String, JGroupsIPCReceiver> receivers) {
        this.receivers = receivers;
    }

    @Override
    public void configure() throws Exception {
        from("direct:EdgeReceiveJGroups")
                .log(LoggingLevel.DEBUG, "Message Received --> {body}")
                .to("direct:EdgeReceiveCommon");

    }

    @PostConstruct
    public void initialise() {
        if(!isInitialised()) {
            deriveIPCTopologyEndpoint();
            establishAllServers();
            setInitialised(true);
        }
    }

    protected JGroupsIPCReceiver establishServer(IPCInterface currentInterface){
        getLogger().debug(".establishServer(): Entry, currentInterface->{}", currentInterface);
        String interfaceGroupName = currentInterface.getGroupName();
        String interfaceChannelName = buildChannelName(currentInterface.getInstanceName(), currentInterface.getInstanceVersion() ,true);
        JGroupsIPCReceiver newIPCReceiver = new JGroupsIPCReceiver();
        JChannel receiverChannel = establishJChannel(interfaceGroupName, interfaceChannelName);
        RpcDispatcher rpcDispatcher = new RpcDispatcher(receiverChannel, this);
        if(receiverChannel != null){
            newIPCReceiver.setChannel(receiverChannel);
            newIPCReceiver.setRpcDispatcher(rpcDispatcher);
            newIPCReceiver.setChannelInitialised(true);
        } else {
            newIPCReceiver.setChannelInitialised(false);
        }
        getLogger().debug(".establishServer(): Exit, newIPCReceiver->{}", newIPCReceiver);
        return(newIPCReceiver);
    }

    protected void establishAllServers(){
        getLogger().debug(".establishAllServers(): Entry");
        if(getDesignatedEndpoint() == null){
            getLogger().error(".establishAllServers(): No endpoint available!");
            return;
        }
        if(getDesignatedEndpoint().getSupportedInterfaceSet().isEmpty()){
            getLogger().error(".establishAllServers(): No endpoint interfaces defined!");
            return;
        }
        for(IPCInterface currentInterface: getDesignatedEndpoint().getSupportedInterfaceSet()) {
            JGroupsIPCReceiver newIPCReceiver = establishServer(currentInterface);
            receivers.put(newIPCReceiver.getChannelName(), newIPCReceiver);
        }
    }


    public InterProcessingPlantHandoverResponsePacket receiveIPCMessage(InterProcessingPlantHandoverPacket payloadPacket){
        ProducerTemplate template = getCamelContext().createProducerTemplate();
        InterProcessingPlantHandoverResponsePacket response = template.requestBody("direct:EdgeReceiveJGroups", payloadPacket, InterProcessingPlantHandoverResponsePacket.class);
        return(response);
    }

}
