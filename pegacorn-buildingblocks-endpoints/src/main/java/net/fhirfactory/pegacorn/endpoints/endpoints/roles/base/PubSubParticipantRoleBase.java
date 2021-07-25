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
package net.fhirfactory.pegacorn.endpoints.endpoints.roles.base;

import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosInterfaceAddress;
import net.fhirfactory.pegacorn.endpoints.endpoints.roles.common.IPCEndpointProxy;
import net.fhirfactory.pegacorn.petasos.datasets.manager.PublisherRegistrationMapIM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipantIdentifier;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacketStatusEnum;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class PubSubParticipantRoleBase  implements EndpointChangeNotificationActionInterface {

    private ProcessingPlantInterface processingPlant;
    private PubSubParticipantEndpointServiceInterface ipcEndpoint;
    private PubSubParticipant me;
    private PublisherRegistrationMapIM publisherMapIM;
    private IPCEndpointProxy endpointProxy;
    private EdgeForwarderService forwarderService;
    private JChannel channel;
    private RpcDispatcher dispatcher;


    private boolean subscriptionCheckScheduled;
    private Object subscriptionCheckScheduledLock;

    private Long PARTICIPANT_MEMBERSHIP_CHECK_DELAY = 5000L;
    private Long PARTICIPANT_MEMBERSHIP_CHECK_PERIOD = 10000L;

    protected static final long RPC_UNICAST_TIMEOUT = 5000;
    protected static final long RPC_MULTICAST_TIMEOUT = 20000;

    //
    // Constructor(s)
    //
    public PubSubParticipantRoleBase(
            ProcessingPlantInterface processingPlant,
            PubSubParticipantEndpointServiceInterface endpointServiceInterface,
            PubSubParticipant me,
            PublisherRegistrationMapIM publisherMapIM,
            JChannel channel,
            RpcDispatcher dispatcher,
            EdgeForwarderService forwarderService){
        this.processingPlant = processingPlant;
        this.ipcEndpoint = endpointServiceInterface;
        this.me = me;
        this.publisherMapIM = publisherMapIM;
        IPCEndpointProxy proxy = new IPCEndpointProxy(channel);
        this.endpointProxy = proxy;
        this.forwarderService = forwarderService;
        this.subscriptionCheckScheduled = false;
        this.subscriptionCheckScheduledLock = new Object();
        this.channel = channel;
        this.dispatcher = dispatcher;
    }

    //
    // Abstract Methods
    //
    abstract protected Logger specifyLogger();
    abstract protected void performPublisherEventUpdateCheck(List<PetasosInterfaceAddress> publishersRemoved, List<PetasosInterfaceAddress> publishersAdded);
    abstract protected void performSubscriberEventUpdateCheck(List<PetasosInterfaceAddress> subscribersRemoved, List<PetasosInterfaceAddress> subscribersAdded);


    //
    // Endpoint/Participant tests
    //

    protected boolean isParticipantServiceAvailable(PubSubParticipant participant){
        getLogger().debug(".isParticipantAvailable(): Entry, participant->{}", participant);
        boolean participantIsAvailable = false;
        if(hasParticipantServiceName(participant)) {
            String serviceName = participant.getInterSubsystemParticipant().getIdentifier().getServiceName();
            participantIsAvailable = getIPCEndpoint().getPubSubParticipantServiceCandidateAddress(serviceName) != null;
        } else {
            participantIsAvailable = false;
        }
        getLogger().debug(".isParticipantAvailable(): Exit, returning->{}", participantIsAvailable);
        return(participantIsAvailable);
    }

    protected boolean hasParticipantServiceName(PubSubParticipant participant){
        if(participant != null){
            if(participant.getInterSubsystemParticipant() != null){
                if(participant.getInterSubsystemParticipant().getIdentifier() != null){
                    if(participant.getInterSubsystemParticipant().getIdentifier().getServiceName() != null){
                        return(true);
                    }
                }
            }
        }
        return(false);
    }

    protected boolean isParticipantServiceAvailable(String participantServiceName){
        getLogger().debug(".isParticipantAvailable(): Entry, publisherServiceName->{}", participantServiceName);
        boolean participantIsAvailable = getIPCEndpoint().getPubSubParticipantServiceCandidateAddress(participantServiceName) != null;
        getLogger().debug(".isParticipantAvailable(): Exit, returning->{}", participantIsAvailable);
        return(participantIsAvailable);
    }

    protected String getAvailableParticipantInstanceName(PubSubParticipant participant){
        getLogger().debug(".getAvailableParticipantInstanceName(): Entry, participant->{}", participant);
        String serviceInstanceName = null;
        if(hasParticipantServiceName(participant)) {
            String publisherServiceName = participant.getInterSubsystemParticipant().getIdentifier().getServiceName();
            serviceInstanceName = getAvailableParticipantInstanceName(publisherServiceName);
        }
        getLogger().debug(".getAvailableParticipantInstanceName(): Exit, serviceInstanceName->{}", serviceInstanceName);
        return(serviceInstanceName);
    }

    public String getAvailableParticipantInstanceName(String participantServiceName){
        getLogger().debug(".getAvailableParticipantInstanceName(): Entry, participantServiceName->{}", participantServiceName);
        PetasosInterfaceAddress targetAddress = getIPCEndpoint().getPubSubParticipantServiceCandidateAddress(participantServiceName);
        String participantInstanceName = targetAddress.toString();
        getLogger().debug(".getAvailableParticipantInstanceName(): Exit, participantInstanceName->{}", participantInstanceName);
        return(participantInstanceName);
    }

    public boolean isParticipantInstanceAvailable(String participantInstanceName){
        getLogger().debug(".isParticipantInstanceAvailable(): Entry, participantInstanceName->{}", participantInstanceName);
        boolean participantInstanceNameStillActive = getIPCEndpoint().isPubSubParticipantInstanceActive(participantInstanceName);
        getLogger().debug(".isParticipantInstanceAvailable(): Exit, participantInstanceNameStillActive->{}", participantInstanceNameStillActive);
        return(participantInstanceNameStillActive);
    }

    public boolean isParticipantInstanceAvailable(PetasosInterfaceAddress participantAddress){
        getLogger().debug(".isParticipantInstanceAvailable(): Entry, participantAddress->{}", participantAddress);
        boolean participantInstanceNameStillActive = getIPCEndpoint().isPubSubParticipantInstanceActive(participantAddress);
        getLogger().debug(".isParticipantInstanceAvailable(): Exit, participantInstanceNameStillActive->{}", participantInstanceNameStillActive);
        return(participantInstanceNameStillActive);
    }

    public PetasosInterfaceAddress getAddressForParticipantInstance(PubSubParticipant publisher){
        if(publisher == null){
            return(null);
        }
        PetasosInterfaceAddress instanceAddress = getAddressForParticipantInstance(publisher.getInterSubsystemParticipant());
        return(instanceAddress);
    }

    public PetasosInterfaceAddress getAddressForParticipantInstance(InterSubsystemPubSubParticipant publisherInterParticipant){
        if(publisherInterParticipant == null){
            return(null);
        }
        PetasosInterfaceAddress instanceAddress = getAddressForParticipantInstance(publisherInterParticipant.getIdentifier());
        return(instanceAddress);
    }

    public PetasosInterfaceAddress getAddressForParticipantInstance(InterSubsystemPubSubParticipantIdentifier publisherInterID){
        if(publisherInterID == null){
            return(null);
        }
        PetasosInterfaceAddress instanceAddress = getAddressForParticipantInstance(publisherInterID.getServiceInstanceName());
        return(instanceAddress);
    }

    public PetasosInterfaceAddress getAddressForParticipantInstance(String publisherInstanceName){
        if(StringUtils.isEmpty(publisherInstanceName)){
            return(null);
        }
        PetasosInterfaceAddress instanceAddress = getIPCEndpoint().getPubSubParticipantInstanceAddress(publisherInstanceName);
        return(instanceAddress);
    }

    public PetasosInterfaceAddress getAddressForParticipantService(PubSubParticipant publisher){
        if(publisher == null){
            return(null);
        }
        PetasosInterfaceAddress address = getAddressForParticipantService(publisher.getInterSubsystemParticipant());
        return(address);
    }

    public PetasosInterfaceAddress getAddressForParticipantService(InterSubsystemPubSubParticipant publisher){
        if(publisher == null){
            return(null);
        }
        PetasosInterfaceAddress address = getAddressForParticipantService(publisher.getIdentifier());
        return(address);
    }

    public PetasosInterfaceAddress getAddressForParticipantService(InterSubsystemPubSubParticipantIdentifier identifier){
        if(getEndpointProxy() == null){
            return(null);
        }
        PetasosInterfaceAddress address = getIPCEndpoint().getPubSubParticipantServiceCandidateAddress(identifier.getServiceName());
        return(address);
    }

    public List<PetasosInterfaceAddress> getParticipantServiceInstanceSet(String participantServiceName){
        List<PetasosInterfaceAddress> addressSet = new ArrayList<>();
        if(StringUtils.isEmpty(participantServiceName)){
            return(addressSet);
        }
        List<PetasosInterfaceAddress> targetServiceInstanceAddresses = getIPCEndpoint().getTargetServiceInstanceAddresses(participantServiceName);
        addressSet.addAll(targetServiceInstanceAddresses);
        return(addressSet);
    }

    public String getServiceNameFromParticipantInstanceName(String participantInstanceName){
        if(StringUtils.isEmpty(participantInstanceName)){
            return(null);
        }
        String[] nameParts = StringUtils.split(participantInstanceName, "(");
        return(nameParts[0]);
    }

    protected String extractPublisherServiceName(String participantInstanceName){
        return(getServiceNameFromParticipantInstanceName(participantInstanceName));
    }

    //
    // Getter and Setters
    //



    public boolean isSubscriptionCheckScheduled() {
        return subscriptionCheckScheduled;
    }

    public void setSubscriptionCheckScheduled(boolean subscriptionCheckScheduled) {
        this.subscriptionCheckScheduled = subscriptionCheckScheduled;
    }

    public Object getSubscriptionCheckScheduledLock() {
        return subscriptionCheckScheduledLock;
    }

    public void setSubscriptionCheckScheduledLock(Object subscriptionCheckScheduledLock) {
        this.subscriptionCheckScheduledLock = subscriptionCheckScheduledLock;
    }

    protected PubSubParticipant getMe(){
        return(this.me);
    }

    protected PublisherRegistrationMapIM getPublisherMapIM(){
        return(this.publisherMapIM);
    }

    protected IPCEndpointProxy getEndpointProxy(){
        return(this.endpointProxy);
    }

    protected EdgeForwarderService getEdgeForwarderService() {
        return (forwarderService);
    }

    protected Logger getLogger(){
        return(specifyLogger());
    }

    protected PubSubParticipantEndpointServiceInterface getIPCEndpoint(){
        return(ipcEndpoint);
    }

    protected Long getParticipantMembershipCheckDelay(){
        return(this.PARTICIPANT_MEMBERSHIP_CHECK_DELAY);
    }

    protected Long getParticipantMembershipCheckPeriod(){
        return(this.PARTICIPANT_MEMBERSHIP_CHECK_PERIOD);
    }

    protected RpcDispatcher getRPCDispatcher(){
        return(dispatcher);
    }

    protected JChannel getIPCChannel(){
        return(channel);
    }

    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlant);
    }

    //
    // Endpoint Change Notification Handlers
    //


    @Override
    public void notifyMembershipChange(List<PetasosInterfaceAddress> endpointsAdded, List<PetasosInterfaceAddress> endpointsRemoved) {
        performPublisherEventUpdateCheck(endpointsRemoved, endpointsAdded );
        performSubscriberEventUpdateCheck(endpointsRemoved, endpointsAdded);
    }

    @Override
    public void notifyMembershipChange(PetasosInterfaceAddress changedEndpoint) {

    }

    public InterSubsystemPubSubParticipant requestParticipantDetail(Address participantAddress, String myName){
        getLogger().info(".requestParticipantDetail(): participantAddress->{}", participantAddress);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = myName;
            classSet[0] = String.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, RPC_UNICAST_TIMEOUT);
            InterSubsystemPubSubParticipant response = getRPCDispatcher().callRemoteMethod(participantAddress, "requestParticipantDetailsHandler", objectSet, classSet, requestOptions);
            getLogger().info(".requestParticipantDetail(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".requestParticipantDetail(): Error (NoSuchMethodException)->", e);
            return(null);
        } catch (Exception e) {
            getLogger().error(".requestParticipantDetail: Error (GeneralException) ->",e);
            return(null);
        }
    }

    public InterSubsystemPubSubParticipant requestParticipantDetailsHandler(String sourceName){
        getLogger().info(".requestParticipantDetailsHandler(): Entry sourceName->{}", sourceName);
        InterSubsystemPubSubParticipant response = new InterSubsystemPubSubParticipant();
        response.setIdentifier(getMe().getInterSubsystemParticipant().getIdentifier());
        response.setSite(getMe().getInterSubsystemParticipant().getSite());
        response.setSecurityZone(getMe().getInterSubsystemParticipant().getSecurityZone());
        getLogger().info(".requestParticipantDetailsHandler(): Exit response->{}", response);
        return(response);
    }



    //
    // Message Senders
    //

    public InterProcessingPlantHandoverResponsePacket sendIPCMessage(Address targetAddress, InterProcessingPlantHandoverPacket handoverPacket){
        getLogger().info(".sendIPCMessage(): Entry, targetAddress->{}", targetAddress);
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


}
