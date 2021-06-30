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
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifestSet;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.names.functionality.base.PegacornCommonInterfaceNames;
import net.fhirfactory.pegacorn.deployment.properties.codebased.PegacornIPCCommonValues;
import net.fhirfactory.pegacorn.deployment.properties.codebased.jgroups.JGroupsStacks;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCServerTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.TopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.edge.StandardEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.petasos.model.pubsub.*;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacketStatusEnum;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionRequest;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionResponse;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionStatus;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.stack.ProtocolStack;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.sql.Date;
import java.time.Instant;
import java.util.List;

public abstract class JGroupsIPCEndpoint extends JGroupsEndpoint {
    private PubSubPublisher publisherID;
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

    protected abstract Logger getLogger();

    protected abstract String specifyChannelName();
    protected abstract String specifyGroupName();
    protected abstract String specifyFileName();
    protected abstract String specifyIPCInterfaceName();
    protected abstract TopologyEndpointTypeEnum specifyIPCType();
    protected abstract EdgeForwarderService specifyForwarderService();
    protected EdgeForwarderService getForwarderService(){return(specifyForwarderService());}

    public JGroupsIPCEndpoint(){
        super();
        this.publisherID = null;
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
        getLogger().trace(".initialise(): Build my DistributedPubSubPublisher Instance");
        getLogger().trace(".initialise(): First, my memberIdentifier (DistributedPubSubParticipantIdentifier)");
        DistributedPubSubParticipantIdentifier memberIdentifier = new DistributedPubSubParticipantIdentifier();
        memberIdentifier.setSubsystemName(processingPlantInterface.getSimpleFunctionName());
        memberIdentifier.setSubsystemInstanceName(processingPlantInterface.getSimpleInstanceName());
        memberIdentifier.setSecurityZone(getProcessingPlantInterface().getNetworkZone());
        getLogger().trace(".initialise(): memberIdentifier (DistributedPubSubParticipantIdentifier) Created -->{}", memberIdentifier);
        getLogger().trace(".initialise(): Now create my localPublisher (LocalPubSubPublisher)");
//        TopologyNodeFDNToken fdnToken = getForwarderService().getWUPTopologyNode().getNodeFDN().getToken();
        getLogger().trace(".initialise(): localPublisher TopologyNodeFDNToken is ->{}", fdnToken);
        LocalPubSubPublisher localPublisher = new LocalPubSubPublisher(fdnToken);
        getLogger().trace(".initialise(): localPublisher (LocalPubSubPublisher) created -->{}", localPublisher);
        getLogger().trace(".initialise(): Now create my Publisher (PubSubPublisher)");
        PubSubPublisher publisher = new PubSubPublisher();
        getLogger().trace(".initialise(): Add the localPublisher aspect to the publisher");
        publisher.setLocalPublisher(localPublisher);
        getLogger().trace(".initialise(): Create my distributedPublisher (DistributedPubSubPublisher) aspect");
        DistributedPubSubPublisher distributedPublisher = new DistributedPubSubPublisher();
        DistributedPubSubParticipantIdentifier distributedPublisherIdentifier = new DistributedPubSubParticipantIdentifier();
        distributedPublisherIdentifier.setSubsystemName(getProcessingPlantInterface().getSimpleFunctionName());
        distributedPublisherIdentifier.setSubsystemInstanceName(getProcessingPlantInterface().getSimpleInstanceName());
        distributedPublisherIdentifier.setSecurityZone(getProcessingPlantInterface().getNetworkZone());
        distributedPublisher.setIdentifier(distributedPublisherIdentifier);
        distributedPublisher.setMembershipStatus(PubSubSubscriptionStatusEnum.SUBSCRIPTION_ACTIVE);
        getLogger().trace(".initialise(): distributedPublisher (DistributedPubSubPublisher) created ->{}", distributedPublisher);
        getLogger().trace(".initialise(): Add the distributedPublisher aspect to the publisher");
        publisher.setDistributedPublisher(distributedPublisher);
        getLogger().trace(".initialise(): distributedPublisher aspect added to the publisher, now assigning it to class variable");
        this.setPublisherID(publisher);
        getLogger().trace(".initialise(): publisher assigned to class variable, now initialising the associated JChannel!");
        establishJChannel(specifyFileName(), specifyGroupName(), specifyChannelName());
        getLogger().trace(".initialise(): publisher created & JChannel initialised, set initialised state to true");
        this.setInitialised(true);
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Remote Called Procedures for Subscribing
    //
    public RemoteSubscriptionResponse rpcSubscribe(RemoteSubscriptionRequest subscriptionRequest){
        getLogger().debug(".rpcSubscribe(): Entry, subscriptionRequest->{}", subscriptionRequest);

        PubSubSubscriber subscriber = subscriptionRequest.getSubscriber();
        List<DataParcelManifest> subscriptionList = subscriptionRequest.getSubscriptionList();

        RemoteSubscriptionStatus subscriptionStatus = getForwarderService().subscribeToDataParcelSet(subscriptionList, subscriber);

        RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
        response.setPublisher(this.getPublisherID());
        response.setSubscriptionRegistrationDate(Date.from(Instant.now()));
        response.setSubscriptionSuccessful(subscriptionStatus.isSubscriptionSuccessful());
        response.setSubscriptionCommentary(subscriptionStatus.getSubscriptionCommentary());
        getLogger().debug(".rpcSubscribe(): Exit, response->{}", response);
        return(response);
    }

    /*
    public RemoteSubscriptionResponse rpcModifySubscription(RemoteSubscriptionRequest subscriptionRequest){
        getLogger().debug(".rpcModifySubscription(): Entry, subscriptionRequest->{}", subscriptionRequest);
        RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
        Address myAddress = getIPCChannel().getAddress();
        response.setPublisherIdentifier(this.getIdentifier());
        response.setSubscriptionRegistrationDate(Date.from(Instant.now()));
        RemoteSubscriptionRequestLocal subscriptionRequestLocal = new RemoteSubscriptionRequestLocal(subscriptionRequest);
        subscriptionRequestLocal.setAddress(getTargetAddress(subscriptionRequest.getIdentifier()));
        RemoteSubscriptionStatus subscriptionStatus = getDistributionMap().addRemoteSubscription(subscriptionRequestLocal);
        response.setSubscriptionSuccessful(subscriptionStatus.isSubscriptionSuccessful());
        response.setSubscriptionCommentary(subscriptionStatus.getSubscriptionCommentary());
        getLogger().debug(".rpcModifySubscription(): Exit, response->{}", response);
        return(response);
    }
     */

    //
    // Local Procedure Calls for Subscribing
    //

    public RemoteSubscriptionResponse subscribe(List<DataParcelManifest> subscriptionList, PubSubPublisher publisher ) {
        getLogger().debug(".subscribe(): Entry, subscriptionList->{}, publisher->{} ", subscriptionList, publisher);

        boolean nothingToSubscribeTo = false;
        if(subscriptionList == null){
            nothingToSubscribeTo = true;
        }
        if(!nothingToSubscribeTo){
            if(subscriptionList.isEmpty()){
                nothingToSubscribeTo = true;
            }
        }
        if(nothingToSubscribeTo){
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Nothing to subscribe");
            return(response);
        }
        RemoteSubscriptionResponse response = null;
        if (isPublisherAvailable(publisher)) {
            response = subscribeToPublisher(getTargetAddress(publisher), subscriptionList);
        } else {
            getLogger().error(".subscribe(): Publisher ({}) is not available!!!", publisher);
            response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Publisher ("+publisher+") is not available!");
        }

        return (response);
    }

    public boolean isPublisherAvailable(PubSubPublisher publisherName){
        boolean publisherAvailable = getTargetAddress(publisherName.getDistributedPublisher().getIdentifier().getSubsystemName()) != null;
        return(publisherAvailable);
    }

    private RemoteSubscriptionResponse subscribeToPublisher(Address publisherAddress, List<DataParcelManifest> parcelTokenList){
        getLogger().debug(".subscribeToPublisher(): Entry");
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            DataParcelManifestSet tokenSet = new DataParcelManifestSet(parcelTokenList);
            objectSet[0] = tokenSet;
            classSet[0] = DataParcelManifestSet.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, RPC_UNICAST_TIMEOUT);
            RemoteSubscriptionResponse response = getRPCDispatcher().callRemoteMethod(publisherAddress, "rpcSubscribe", objectSet, classSet, requestOptions);
            getLogger().debug(".subscribeToPublisher(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".subscribeToPublisher(): Error (NoSuchMethodException) ->{}", e.getMessage());
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (NoSuchMethodException)" + e.getMessage());
            return(response);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".sendIPCMessage: Error (GeneralException) ->{}", e.getMessage());
            RemoteSubscriptionResponse response = new RemoteSubscriptionResponse();
            response.setSubscriptionSuccessful(false);
            response.setSubscriptionCommentary("Error (GeneralException)" + e.getMessage());
            return(response);
        }
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
        getLogger().debug(".executeRPC(): Entry, targetAddress->{}", targetAddress);
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
//            ProtocolStack protocolStack = specifyProtocolStack();
//            getLogger().trace(".establishJChannel(): Got required protocolStack (ProtocolStack)->{}", protocolStack);
            JChannel newChannel = new JChannel(fileName);
            getLogger().trace(".establishJChannel(): JChannel instantiated, now initialising");
//            specifyProtocolStack().init();
            getLogger().trace(".establishJChannel(): JChannel initialised, now setting JChannel name");
            newChannel.name(channelName);
            getLogger().trace(".establishJChannel(): JChannel Name set, now set ensure we don't get our own messages");
            newChannel.setDiscardOwnMessages(true);
            getLogger().trace(".establishJChannel(): Now setting RPCDispatcher");
            RpcDispatcher newRPCDispatcher = new RpcDispatcher(newChannel, this);
            getLogger().trace(".establishJChannel(): RPCDispatcher assigned, now connect to JGroup");
            newChannel.connect(groupName);
            getLogger().trace(".establishJChannel(): Connected to JGroup complete, now assigning class attributes");
            this.setIPCChannel(newChannel);
            this.setRPCDispatcher(newRPCDispatcher);
            getLogger().debug(".establishJChannel(): Exit, JChannel & RPCDispatcher created");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".establishJChannel(): Cannot establish JGroups Channel, error->{}", e.getMessage());
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

    public PubSubPublisher getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(PubSubPublisher publisherID) {
        this.publisherID = publisherID;
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
