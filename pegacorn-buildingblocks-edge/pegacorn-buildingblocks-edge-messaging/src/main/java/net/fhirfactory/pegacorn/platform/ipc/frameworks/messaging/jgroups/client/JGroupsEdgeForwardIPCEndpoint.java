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
package net.fhirfactory.pegacorn.platform.ipc.frameworks.messaging.jgroups.client;

import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.TopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.petasos.ipc.model.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.petasos.ipc.model.InterProcessingPlantHandoverPacketStatusEnum;
import net.fhirfactory.pegacorn.petasos.ipc.model.InterProcessingPlantHandoverResponsePacket;
import net.fhirfactory.pegacorn.platform.ipc.frameworks.messaging.jgroups.common.JGroupsEdgeIPCEndpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

@ApplicationScoped
public class JGroupsEdgeForwardIPCEndpoint extends JGroupsEdgeIPCEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(JGroupsEdgeForwardIPCEndpoint.class);

    private int JGROUPS_REQUEST_TIMEOUT = 1000;

    protected Logger specifyLogger(){
        return(LOG);
    }

    private HashMap<String, JGroupsIPCSender> senders;

    public JGroupsEdgeForwardIPCEndpoint(){
        super();
    }



    @Override
    public void configure() throws Exception {
        from("direct:EdgeForwardJGroups")
            .process(new Processor(){
                public void process(Exchange camelExchange) throws Exception {
                    InterProcessingPlantHandoverPacket handoverPacket = camelExchange.getIn().getBody(InterProcessingPlantHandoverPacket.class);
                    String receivingEndpointName = handoverPacket.getTarget() + getGroupServerEndpointName();
                    InterProcessingPlantHandoverResponsePacket handoverResponsePacket = sendIPCMessage(receivingEndpointName, handoverPacket);
                }
            });
    }

    @PostConstruct
    public void initialse(){
        if(!isInitialised()) {
            setInitialised(true);
            deriveIPCTopologyEndpoint();
            establishAllConnections();
        }
    }

    //
    // Build Connections
    //

    protected void initialiseClients(){
        getLogger().debug(".initialiseClients(): Entry");
        deriveIPCTopologyEndpoint();
    }

    protected JGroupsIPCSender establishConnection(IPCInterface currentInterface){
        getLogger().debug(".establishConnection(): Entry, currentInterface (IPCInterface)->{}", currentInterface);
        String interfaceGroupName = currentInterface.getGroupName();
        String interfaceChannelName = buildChannelName(currentInterface.getInstanceName(), currentInterface.getInstanceVersion() ,false);
        JGroupsIPCSender newIPCConnection = new JGroupsIPCSender();
        JChannel targetChannel = establishJChannel(interfaceGroupName, interfaceChannelName);
        RpcDispatcher rpcDispatcher = new RpcDispatcher(targetChannel, null);
        if(targetChannel != null){
            newIPCConnection.setChannel(targetChannel);
            newIPCConnection.setRpcDispatcher(rpcDispatcher);
            newIPCConnection.setChannelInitialised(true);
        } else {
            newIPCConnection.setChannelInitialised(false);
        }
        if(newIPCConnection.isChannelInitialised()){
            Address targetAddress = deriveTargetAddress(targetChannel, currentInterface.getTargetName()+getGroupServerEndpointName());
            if(targetAddress != null){
                newIPCConnection.setTargetAddress(targetAddress);
                newIPCConnection.setTargetAddressInitialised(true);
            } else {
                newIPCConnection.setTargetAddressInitialised(false);
            }
        }
        getLogger().debug(".establishConnection(): Exit, newIPCConnection (JGroupsIPCConnection)->{}", newIPCConnection);
        return(newIPCConnection);
    }

    protected void establishAllConnections(){
        getLogger().debug(".establishAllConnections(): Entry");
        if(getDesignatedEndpoint() == null){
            getLogger().error(".establishAllConnections(): No endpoint available!");
            return;
        }
        if(getDesignatedEndpoint().getSupportedInterfaceSet().isEmpty()){
            getLogger().error(".establishAllConnections(): No endpoint interfaces defined!");
            return;
        }
        for(IPCInterface currentInterface: getDesignatedEndpoint().getSupportedInterfaceSet()) {
            JGroupsIPCSender newIPCConnection = establishConnection(currentInterface);
            if(isSameNetworkZone(currentInterface.getTargetName())) {
                if (newIPCConnection.isTargetAddressInitialised()) {
                    String setTargetEndpoint = currentInterface.getTargetName() + getGroupServerEndpointName();
                    senders.put(setTargetEndpoint, newIPCConnection);
                }
            }
        }
    }

    private InterProcessingPlantHandoverResponsePacket sendIPCMessage(String target, InterProcessingPlantHandoverPacket payloadPacket){
        getLogger().debug(".establishAllConnections(): Entry, payloadPacket->{}", payloadPacket);
        RequestOptions requestOptions = new RequestOptions(ResponseMode.GET_FIRST, getJGroupsRequestTimeout());
        Class classes[] = new Class[1];
        classes[0] = InterProcessingPlantHandoverPacket.class;
        java.lang.Object objectSet[] = new java.lang.Object[1];
        objectSet[0] = payloadPacket;
        JGroupsIPCSender actualConnection = senders.get(target);
        InterProcessingPlantHandoverResponsePacket responsePacket = null;
        try {
            LOG.trace(".establishAllConnections(): Sending Request");
            responsePacket = actualConnection.getRpcDispatcher().callRemoteMethod(actualConnection.getTargetAddress(), "receiveIPCMessage", objectSet, classes, requestOptions);
            LOG.trace(".establishAllConnections(): Response Received->{}", responsePacket);
        } catch(Exception ex){
            responsePacket = new InterProcessingPlantHandoverResponsePacket();
            responsePacket.setActivityID(payloadPacket.getActivityID());
            responsePacket.setMessageIdentifier(payloadPacket.getMessageIdentifier());
            responsePacket.setStatus(InterProcessingPlantHandoverPacketStatusEnum.PACKET_SEND_FAILURE);
            responsePacket.setSendDate(Date.from(Instant.now()));
            responsePacket.setStatusReason(ex.getMessage());
        }
        return(responsePacket);
    }

    public int getJGroupsRequestTimeout() {
        return JGROUPS_REQUEST_TIMEOUT;
    }
}
