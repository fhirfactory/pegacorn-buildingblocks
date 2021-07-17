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
package net.fhirfactory.pegacorn.platform.edge.endpoints.jgroups.common;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.names.functionality.base.PegacornCommonInterfaceNames;
import net.fhirfactory.pegacorn.deployment.properties.codebased.PegacornIPCCommonValues;
import net.fhirfactory.pegacorn.deployment.properties.codebased.jgroups.JGroupsStacks;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.TopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.edge.StandardEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.petasos.datasets.manager.PublisherRegistrationMapIM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.*;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacketStatusEnum;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.stack.ProtocolStack;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

public abstract class JGroupsIPCEndpoint extends JGroupsEndpoint {
    private PubSubParticipant pubsubParticipant;
    private RpcDispatcher rpcDispatcher;
    private StandardEdgeIPCEndpoint ipcTopologyEndpoint;

    protected static final long RPC_UNICAST_TIMEOUT = 5000;
    protected static final long RPC_MULTICAST_TIMEOUT = 20000;

    @Inject
    private ProcessingPlantInterface processingPlantInterface;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private PegacornIPCCommonValues ipcFunctionalityNames;

    @Inject
    private JGroupsStacks stacks;

    @Inject
    private PegacornCommonInterfaceNames interfaceNames;

    @Produce
    private ProducerTemplate camelProducer;

    @Inject
    private PublisherRegistrationMapIM publisherRegistrationMapIM;

    protected abstract String specifyGroupName();
    protected abstract String specifyFileName();
    protected abstract String specifyIPCInterfaceName();
    protected abstract TopologyEndpointTypeEnum specifyIPCType();
    protected abstract EdgeForwarderService specifyForwarderService();
    protected EdgeForwarderService getForwarderService(){return(specifyForwarderService());}
    protected abstract void additionalInitialisation();

    public JGroupsIPCEndpoint(){
        super();
        this.pubsubParticipant = null;
        this.rpcDispatcher = null;
        this.ipcTopologyEndpoint = null;
    }

    @PostConstruct
    public void initialise(TopologyNodeFDNToken fdnToken){
        getLogger().debug(".initialise(): Entry");
        if(isInitialised()){
            getLogger().debug(".initialise(): Exit, already initialised!");
            return;
        }
        getLogger().trace(".initialise(): Get my IPCEndpoint Detail");
        deriveTopologyEndpoint();
        getLogger().trace(".initialise(): IPCEndpoint derived ->{}", getIPCTopologyEndpoint());

        // 1st, the IntraSubsystem Pub/Sub Participant} component
        getLogger().trace(".initialise(): Now create my intraSubsystemParticipant (LocalPubSubPublisher)");
//        TopologyNodeFDNToken fdnToken = getForwarderService().getWUPTopologyNode().getNodeFDN().getToken();
        getLogger().trace(".initialise(): localPublisher TopologyNodeFDNToken is ->{}", fdnToken);
        IntraSubsystemPubSubParticipant intraSubsystemParticipant = new IntraSubsystemPubSubParticipant(fdnToken);
        getLogger().trace(".initialise(): intraSubsystemParticipant created -->{}", intraSubsystemParticipant);
        getLogger().trace(".initialise(): Now create my PubSubParticipant");
        PubSubParticipant partipant = new PubSubParticipant();
        getLogger().trace(".initialise(): Add the intraSubsystemParticipant aspect to the partipant");
        partipant.setIntraSubsystemParticipant(intraSubsystemParticipant);

        // Now the InterSubsystem Pub/Sub Participant component
        getLogger().trace(".initialise(): Create my interSubsystemParticipant aspect");
        getLogger().trace(".initialise(): First, my distributedPublisherIdentifier");
        InterSubsystemPubSubParticipantIdentifier interParticipantIdentifier = new InterSubsystemPubSubParticipantIdentifier();
        interParticipantIdentifier.setServiceName(getProcessingPlantInterface().getIPCServiceName());
        String serviceInstanceName = getProcessingPlantInterface().getIPCServiceName() + "(" + UUID.randomUUID().toString() + ")";
        interParticipantIdentifier.setServiceInstanceName(serviceInstanceName);
        getLogger().trace(".initialise(): interParticipantIdentifier Created -->{}", interParticipantIdentifier);
        InterSubsystemPubSubParticipant distributedPublisher = new InterSubsystemPubSubParticipant();
        distributedPublisher.setSecurityZone(getProcessingPlantInterface().getNetworkZone());
        distributedPublisher.setSite(getProcessingPlantInterface().getDeploymentSite());
        distributedPublisher.setIdentifier(interParticipantIdentifier);
        distributedPublisher.setConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_CONNECTION_NOT_ESTABLISHED);
        distributedPublisher.setConnectionEstablishmentDate(Date.from(Instant.now()));
        getLogger().trace(".initialise(): distributedPublisher (DistributedPubSubPublisher) created ->{}", distributedPublisher);

        // Now assemble the "Participant"
        getLogger().trace(".initialise(): Add the distributedPublisher aspect to the partipant");
        partipant.setInterSubsystemParticipant(distributedPublisher);
        getLogger().trace(".initialise(): distributedPublisher aspect added to the partipant, now assigning it to class variable");
        this.setPubsubParticipant(partipant);
        getLogger().trace(".initialise(): participant assigned to class variable, now initialising the associated JChannel!");
        establishJChannel(specifyFileName(), specifyGroupName(), serviceInstanceName);
        getLogger().trace(".initialise(): calling subclass post-construct methods");
        additionalInitialisation();
        getLogger().trace(".initialise(): partipant created & JChannel initialised, set initialised state to true");
        this.setInitialised(true);
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Receive Messages (via RPC invocations)
    //

    protected abstract InterProcessingPlantHandoverResponsePacket injectMessageIntoRoute(InterProcessingPlantHandoverPacket handoverPacket);

    public InterProcessingPlantHandoverResponsePacket receiveIPCMessage(InterProcessingPlantHandoverPacket handoverPacket){
        getLogger().debug(".receiveIPCMessage(): Entry, handoverPacket->{}",handoverPacket);
        InterProcessingPlantHandoverResponsePacket response = injectMessageIntoRoute(handoverPacket);
        getLogger().debug(".receiveIPCMessage(): Exit, response->{}",response);
        return(response);
    }

    //
    // Send Messages (via RPC invocations)
    //

    private InterProcessingPlantHandoverResponsePacket sendIPCMessage(Address targetAddress, InterProcessingPlantHandoverPacket handoverPacket){
        getLogger().debug(".sendIPCMessage(): Entry, targetAddress->{}", targetAddress);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = handoverPacket;
            classSet[0] = InterProcessingPlantHandoverPacket.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, RPC_UNICAST_TIMEOUT);
            InterProcessingPlantHandoverResponsePacket response = getRPCDispatcher().callRemoteMethod(targetAddress, "receiveIPCMessage", objectSet, classSet, requestOptions);
            getLogger().debug(".sendIPCMessage(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".sendIPCMessage(): Error (NoSuchMethodException) ->{}", e.getMessage());
            InterProcessingPlantHandoverResponsePacket response = new InterProcessingPlantHandoverResponsePacket();
            response.setActivityID(handoverPacket.getActivityID());
            response.setStatus(InterProcessingPlantHandoverPacketStatusEnum.PACKET_SEND_FAILURE);
            response.setStatusReason("Error (NoSuchMethodException)" + e.getMessage());
            return(response);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".sendIPCMessage: Error (GeneralException) ->{}", e.getMessage());
            InterProcessingPlantHandoverResponsePacket response = new InterProcessingPlantHandoverResponsePacket();
            response.setActivityID(handoverPacket.getActivityID());
            response.setStatus(InterProcessingPlantHandoverPacketStatusEnum.PACKET_SEND_FAILURE);
            response.setStatusReason("Error (GeneralException)" + e.getMessage());
            return(response);
        }
    }

    public InterProcessingPlantHandoverResponsePacket sendIPCMessage(String target, InterProcessingPlantHandoverPacket handoverPacket){
        getLogger().debug(".sendIPCMessage(): Entry, target->{}, handoverPacker->{}", target, handoverPacket);
        Address targetAddress = getTargetAddress(target);
        InterProcessingPlantHandoverResponsePacket response = sendIPCMessage(targetAddress, handoverPacket);
        return(response);
    }

    //
    // Setup The Channel
    //

    protected abstract ProtocolStack specifyProtocolStack();

    protected void establishJChannel(String fileName, String groupName, String channelName){
        getLogger().debug(".establishJChannel(): Entry, groupName->{}, channelName->{}", groupName, channelName);
        try {
            getLogger().trace(".establishJChannel(): Creating JChannel");
            getLogger().trace(".establishJChannel(): Getting the required ProtocolStack");
            JChannel newChannel = new JChannel(fileName);
            getLogger().trace(".establishJChannel(): JChannel initialised, now setting JChannel name");
            newChannel.name(channelName);
            getLogger().trace(".establishJChannel(): JChannel Name set, now set ensure we don't get our own messages");
            newChannel.setDiscardOwnMessages(true);
            getLogger().trace(".establishJChannel(): Now setting RPCDispatcher");
            RpcDispatcher newRPCDispatcher = new RpcDispatcher(newChannel, this);
            newChannel.setReceiver(this);
            getLogger().trace(".establishJChannel(): RPCDispatcher assigned, now connect to JGroup");
            newChannel.connect(groupName);
            getLogger().trace(".establishJChannel(): Connected to JGroup complete, now assigning class attributes");
            this.setIPCChannel(newChannel);
            this.setRPCDispatcher(newRPCDispatcher);
            this.getPubsubParticipant().getInterSubsystemParticipant().setConnectionEstablishmentDate(Date.from(Instant.now()));
            this.getPubsubParticipant().getInterSubsystemParticipant().setConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_CONNECTION_ESTABLISHED);
            getLogger().trace(".establishJChannel(): Exit, JChannel & RPCDispatcher created");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".establishJChannel(): Cannot establish JGroups Channel, error->{}", e.getMessage());
            this.getPubsubParticipant().getInterSubsystemParticipant().setConnectionEstablishmentDate(Date.from(Instant.now()));
            this.getPubsubParticipant().getInterSubsystemParticipant().setConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_CONNECTION_FAILED);
            return;
        }
    }



    //
    // Getters (and Setters)
    //

    public ProcessingPlantInterface getProcessingPlantInterface() {
        return processingPlantInterface;
    }


    public PegacornIPCCommonValues getIPCComponentNames() {
        return ipcFunctionalityNames;
    }

    public PubSubParticipant getPubsubParticipant() {
        return pubsubParticipant;
    }

    public void setPubsubParticipant(PubSubParticipant pubsubParticipant) {
        this.pubsubParticipant = pubsubParticipant;
    }

    public RpcDispatcher getRPCDispatcher() {
        return rpcDispatcher;
    }

    protected void setRPCDispatcher(RpcDispatcher rpcDispatcher) {
        this.rpcDispatcher = rpcDispatcher;
    }

    protected TopologyIM getTopologyIM(){
        return(this.topologyIM);
    }

    public StandardEdgeIPCEndpoint getIPCTopologyEndpoint() {
        return ipcTopologyEndpoint;
    }

    public void setIPCTopologyEndpoint(StandardEdgeIPCEndpoint ipcEndpoint) {
        this.ipcTopologyEndpoint = ipcEndpoint;
    }

    protected JGroupsStacks getStacks() {
        return stacks;
    }

    protected ProducerTemplate getCamelProducer() {
        return camelProducer;
    }

    public PegacornCommonInterfaceNames getInterfaceNames() {
        return (interfaceNames);
    }

    public PublisherRegistrationMapIM getPublisherRegistrationMapIM() {
        return publisherRegistrationMapIM;
    }

    //
    // Resolve my Endpoint Details
    //

    protected void deriveTopologyEndpoint(){
        getLogger().debug(".deriveIPCTopologyEndpoint(): Entry");
        for(TopologyNodeFDN currentEndpointFDN: getProcessingPlantInterface().getProcessingPlantNode().getEndpoints()){
            IPCTopologyEndpoint currentEndpoint = (IPCTopologyEndpoint)getTopologyIM().getNode(currentEndpointFDN);
            TopologyEndpointTypeEnum endpointType = currentEndpoint.getEndpointType();
            boolean endpointTypeMatches = endpointType.equals(specifyIPCType());
            if(endpointTypeMatches){
                if(currentEndpoint.getName().contentEquals(specifyIPCInterfaceName())) {
                    setIPCTopologyEndpoint((StandardEdgeIPCEndpoint)currentEndpoint);
                    getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, found IPCTopologyEndpoint and assigned it");
                    break;
                }
            }
        }
        getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, Could not find appropriate Endpoint");
    }
}
