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
package net.fhirfactory.pegacorn.platform.edge.endpoints.technologies.jgroups.base;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDNToken;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.names.functionality.base.PegacornCommonInterfaceNames;
import net.fhirfactory.pegacorn.deployment.properties.codebased.PegacornIPCCommonValues;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.TopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.edge.StandardEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.DefaultWorkshopSetEnum;
import net.fhirfactory.pegacorn.petasos.datasets.manager.PublisherRegistrationMapIM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.*;
import net.fhirfactory.pegacorn.platform.edge.endpoints.roles.PubSubParticipantClientRole;
import net.fhirfactory.pegacorn.platform.edge.endpoints.roles.PubSubParticipantServerRole;
import net.fhirfactory.pegacorn.platform.edge.endpoints.roles.base.PubSubParticipantEndpointServiceInterface;
import net.fhirfactory.pegacorn.platform.edge.endpoints.roles.common.IPCEndpointAddress;
import net.fhirfactory.pegacorn.platform.edge.endpoints.roles.common.IPCEndpointAddressTypeEnum;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionRequest;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionResponse;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.SerializationUtils;
import org.jgroups.Address;
import org.jgroups.MembershipListener;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public abstract class JGroupsIPCPubSubParticipant extends JGroupsEndpointBase implements PubSubParticipantEndpointServiceInterface, MembershipListener {

    private StandardEdgeIPCEndpoint ipcTopologyEndpoint;

    private PubSubParticipantClientRole meAsPubSubClient;
    private PubSubParticipantServerRole meAsPubSubServer;

    @Inject
    private ProcessingPlantInterface processingPlantInterface;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private PegacornIPCCommonValues ipcFunctionalityNames;

    @Inject
    private PegacornCommonInterfaceNames interfaceNames;

    @Produce
    private ProducerTemplate camelProducer;

    @Inject
    private PublisherRegistrationMapIM publisherRegistrationMapIM;

    //
    // Constructor
    //
    public JGroupsIPCPubSubParticipant(){
        this.setIPCChannel(null);
        this.setInitialised(false);

        this.ipcTopologyEndpoint = null;
        this.meAsPubSubClient = null;
        this.meAsPubSubServer = null;
    }

    //
    // Abstract Methods
    //

    protected abstract String specifyGroupName();
    protected abstract String specifyFileName();
    protected abstract String specifyIPCInterfaceName();
    protected abstract TopologyEndpointTypeEnum specifyIPCType();
    protected abstract EdgeForwarderService specifyForwarderService();
    protected abstract String specifyForwarderWUPName();
    protected abstract String specifyForwarderWUPVersion();
    protected EdgeForwarderService getForwarderService(){return(specifyForwarderService());}

    //
    // Post Constructor Initialisation
    //

    @PostConstruct
    public void initialise(){
        getLogger().info(".initialise(): Entry");
        if(isInitialised()){
            getLogger().info(".initialise(): Exit, already initialised!");
            return;
        }
        getLogger().info(".initialise(): Get my IPCEndpoint Detail");
        deriveTopologyEndpoint();
        getLogger().info(".initialise(): IPCEndpoint derived ->{}", getIPCTopologyEndpoint());

        // 1st, the IntraSubsystem Pub/Sub Participant} component
        getLogger().info(".initialise(): Now create my intraSubsystemParticipant (LocalPubSubPublisher)");
        TopologyNodeFDNToken topologyNodeFDNToken = deriveAssociatedForwarderFDNToken();
        getLogger().info(".initialise(): localPublisher TopologyNodeFDNToken is ->{}", topologyNodeFDNToken);
        IntraSubsystemPubSubParticipant intraSubsystemParticipant = new IntraSubsystemPubSubParticipant(topologyNodeFDNToken);
        getLogger().info(".initialise(): intraSubsystemParticipant created -->{}", intraSubsystemParticipant);
        getLogger().info(".initialise(): Now create my PubSubParticipant");
        PubSubParticipant partipant = new PubSubParticipant();
        getLogger().info(".initialise(): Add the intraSubsystemParticipant aspect to the partipant");
        partipant.setIntraSubsystemParticipant(intraSubsystemParticipant);

        // Now the InterSubsystem Pub/Sub Participant component
        getLogger().info(".initialise(): Create my interSubsystemParticipant aspect");
        getLogger().info(".initialise(): First, my distributedPublisherIdentifier");
        InterSubsystemPubSubParticipantIdentifier interParticipantIdentifier = new InterSubsystemPubSubParticipantIdentifier();
        interParticipantIdentifier.setServiceName(getProcessingPlantInterface().getIPCServiceName());
        String serviceInstanceName = getProcessingPlantInterface().getIPCServiceName() + "(" + UUID.randomUUID().toString() + ")";
        interParticipantIdentifier.setServiceInstanceName(serviceInstanceName);
        getLogger().info(".initialise(): interParticipantIdentifier Created -->{}", interParticipantIdentifier);
        InterSubsystemPubSubParticipant distributedPublisher = new InterSubsystemPubSubParticipant();
        distributedPublisher.setSecurityZone(getProcessingPlantInterface().getNetworkZone());
        distributedPublisher.setSite(getProcessingPlantInterface().getDeploymentSite());
        distributedPublisher.setIdentifier(interParticipantIdentifier);
        distributedPublisher.setConnectionStatus(PubSubNetworkConnectionStatusEnum.PUB_SUB_NETWORK_CONNECTION_NOT_ESTABLISHED);
        distributedPublisher.setConnectionEstablishmentDate(Date.from(Instant.now()));
        getLogger().info(".initialise(): distributedPublisher (DistributedPubSubPublisher) created ->{}", distributedPublisher);

        // Now assemble the "Participant"
        getLogger().info(".initialise(): Add the distributedPublisher aspect to the partipant");
        partipant.setInterSubsystemParticipant(distributedPublisher);
        getLogger().info(".initialise(): distributedPublisher aspect added to the partipant, now assigning it to class variable");
        this.setPubsubParticipant(partipant);
        getLogger().info(".initialise(): participant assigned to class variable, now initialising the associated JChannel!");
        establishJChannel(specifyFileName(), specifyGroupName(), serviceInstanceName);
        getLogger().info(".initialise(): calling subclass post-construct methods");
        //additionalInitialisation();
        getLogger().info(".initialise(): partipant created & JChannel initialised, set initialised state to true");

        // Now build my "Roles"
        getLogger().info(".initialise(): building meAsPubSubServer!");
        this.meAsPubSubServer = new PubSubParticipantServerRole(getProcessingPlantInterface(),this, getPubsubParticipant(),getPublisherRegistrationMapIM(),getIPCChannel(), getRPCDispatcher(), getForwarderService());
        getLogger().info(".initialise(): building meAsPubSubServer, done...");
        getLogger().info(".initialise(): building meAsPubSubClient!");
        this.meAsPubSubClient = new PubSubParticipantClientRole(getProcessingPlantInterface(),this, getPubsubParticipant(),getPublisherRegistrationMapIM(),getIPCChannel(), getRPCDispatcher(), getForwarderService());
        getLogger().info(".initialise(): building meAsPubSubClient, done...");
        // Add some callbacks

        getLogger().info(".initialise(): Adding JGroups Listeners");
        this.getMembershipEventListeners().add(this.meAsPubSubClient);
        this.getMembershipEventListeners().add(this.meAsPubSubServer);
        getLogger().info(".initialise(): Adding JGroups Listeners, done...");

        getLogger().info(".initialise(): Adding Subscription Check");
        this.meAsPubSubClient.scheduleSubscriptionCheck();

        getLogger().info(".initialise(): Announce my Publishing Capability");
        this.meAsPubSubServer.announceMeAsAPublisher();

        this.setInitialised(true);
        getLogger().debug(".initialise(): Exit");
    }

    @Override
    public IPCEndpointAddress getPubSubParticipantServiceCandidateAddress(String serviceName) {
        List<IPCEndpointAddress> instanceAddresses = getTargetServiceInstanceAddresses(serviceName);
        return(instanceAddresses.get(0));
    }

    @Override
    public IPCEndpointAddress getPubSubParticipantInstanceAddress(String serviceProviderInstanceName) {
        Address address = getTargetInstanceAddress(serviceProviderInstanceName);
        IPCEndpointAddress ipcAddress = new IPCEndpointAddress();
        ipcAddress.setAddressName(address.toString());
        ipcAddress.setJGroupsAddress(address);
        ipcAddress.setAddressType(IPCEndpointAddressTypeEnum.ADDRESS_TYPE_JGROUPS);
        return(ipcAddress);
    }

    @Override
    public boolean isPubSubParticipantInstanceActive(String serviceProviderInstanceName) {
        return (isTargetAddressActive(serviceProviderInstanceName));
    }

    @Override
    public boolean isPubSubParticipantInstanceActive(IPCEndpointAddress ipcEndpointID) {
        return (isTargetAddressActive(ipcEndpointID.getAddressName()));
    }

    @Override
    public List<IPCEndpointAddress> getAllPubSubParticipantAddresses() {
        return (getAllTargets());
    }

    //
    // PubSub Business Methods
    //

    public InterSubsystemPubSubPublisherSubscriptionRegistration subscribeToRemotePublishers(List<DataParcelManifest> subscriptionList, PubSubParticipant publisher){
        getLogger().info(".subscribeToRemotePublishers(): Entry, publisher->{}", publisher);
        InterSubsystemPubSubPublisherSubscriptionRegistration registration = meAsPubSubClient.subscribeToRemotePublishers(subscriptionList, publisher);
        getLogger().info(".subscribeToRemotePublishers(): Exit, registration->{}", registration);
        return(registration);
    }

    public RemoteSubscriptionResponse rpcRequestSubscriptionHandler(RemoteSubscriptionRequest subscriptionRequest){
        getLogger().info(".rpcRequestSubscriptionHandler(): Entry, subscriptionRequest->{}", subscriptionRequest);
        RemoteSubscriptionResponse remoteSubscriptionResponse = meAsPubSubServer.rpcRequestSubscriptionHandler(subscriptionRequest);
        getLogger().info(".rpcRequestSubscriptionHandler(): Exit, remoteSubscriptionResponse->", remoteSubscriptionResponse);
        return(remoteSubscriptionResponse);
    }

    public InterSubsystemPubSubPublisherRegistration rpcRegisterPublisherHandler(InterSubsystemPubSubParticipant publisher){
        getLogger().info(".rpcRegisterPublisherHandler(): Entry, publisher->{}", publisher);
        InterSubsystemPubSubPublisherRegistration registration = this.meAsPubSubServer.rpcRegisterPublisherHandler(publisher);
        getLogger().info(".rpcRegisterPublisherHandler(): Exit, registration->{}", registration);
        return(registration);
    }

    public InterSubsystemPubSubParticipant requestParticipantDetailsHandler(String name){
        getLogger().info(".requestParticipantDetailsHandler(): Entry, name->{}", name);
        if(this.meAsPubSubServer == null){
            getLogger().warn(".requestParticipantDetailsHandler(): Warning, this.meAsPubSubServer is null");
        }
//        InterSubsystemPubSubParticipant participant = this.getMeAsPubSubServer().requestParticipantDetailsHandler(name);
        InterSubsystemPubSubParticipant participant = getPubsubParticipant().getInterSubsystemParticipant();
        getLogger().info(".requestParticipantDetailsHandler(): Exit, participant->{}", participant);
        return(participant);
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
    public InterProcessingPlantHandoverResponsePacket sendIPCMessage(String targetParticipantServiceName, InterProcessingPlantHandoverPacket handoverPacket){
        getLogger().info(".sendIPCMessage(): Entry, targetParticipantServiceName->{}, handoverPacket->{}", targetParticipantServiceName, handoverPacket);
        Address targetServiceAddress = getTargetServiceAddress(targetParticipantServiceName);
        getLogger().info(".sendIPCMessage(): Got an address, targetServiceAddress->{}", targetServiceAddress);
        InterProcessingPlantHandoverResponsePacket interProcessingPlantHandoverResponsePacket = sendIPCMessage(targetServiceAddress, handoverPacket);
        return(interProcessingPlantHandoverResponsePacket);
    }


    public InterProcessingPlantHandoverResponsePacket sendIPCMessage(Address targetAddress, InterProcessingPlantHandoverPacket handoverPacket){
        getLogger().info(".sendIPCMessage(): Entry, targetAddress->{}, handoverPacket->{}", targetAddress, handoverPacket);
        InterProcessingPlantHandoverResponsePacket interProcessingPlantHandoverResponsePacket = this.meAsPubSubClient.sendIPCMessage(targetAddress, handoverPacket);
        return(interProcessingPlantHandoverResponsePacket);
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

    protected TopologyNodeFDNToken deriveAssociatedForwarderFDNToken(){
        if(this.getIPCTopologyEndpoint() == null){
            getLogger().error(".deriveAssociatedForwarderFDNToken(): Unresolvable endpoint");
            return(null);
        }
        TopologyNodeFDN workshopNodeFDN = null;
        for(TopologyNodeFDN currentWorkshopFDN: getProcessingPlantInterface().getProcessingPlantNode().getWorkshops()){
            if(currentWorkshopFDN.extractRDNForNodeType(TopologyNodeTypeEnum.WORKSHOP).getNodeName().equals(DefaultWorkshopSetEnum.EDGE_WORKSHOP.getWorkshop())){
                workshopNodeFDN = currentWorkshopFDN;
                break;
            }
        }
        if(workshopNodeFDN == null ){
            getLogger().error(".deriveAssociatedForwarderFDNToken(): Unresolvable workshop");
            return(null);
        }
        TopologyNodeFDN wupNodeFDN = SerializationUtils.clone(workshopNodeFDN);
        wupNodeFDN.appendTopologyNodeRDN(new TopologyNodeRDN(TopologyNodeTypeEnum.WUP, specifyForwarderWUPName(), specifyForwarderWUPVersion()));
        TopologyNodeFDNToken associatedForwarderWUPToken = wupNodeFDN.getToken();
        return(associatedForwarderWUPToken);
    }

    //
    // Getters and Setters
    //

    public ProcessingPlantInterface getProcessingPlantInterface() {
        return processingPlantInterface;
    }

    public PegacornIPCCommonValues getIPCComponentNames() {
        return ipcFunctionalityNames;
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

    protected ProducerTemplate getCamelProducer() {
        return camelProducer;
    }

    public PegacornCommonInterfaceNames getInterfaceNames() {
        return (interfaceNames);
    }

    public PublisherRegistrationMapIM getPublisherRegistrationMapIM() {
        return publisherRegistrationMapIM;
    }

    public PubSubParticipantClientRole getMeAsPubSubClient() {
        return meAsPubSubClient;
    }

    public PubSubParticipantServerRole getMeAsPubSubServer() {
        return meAsPubSubServer;
    }
}
