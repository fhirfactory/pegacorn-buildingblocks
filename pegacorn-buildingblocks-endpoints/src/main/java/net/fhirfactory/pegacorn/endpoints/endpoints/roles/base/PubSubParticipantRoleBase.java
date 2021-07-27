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
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.endpoints.endpoints.roles.common.IPCEndpointProxy;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DistributedPubSubSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common.EdgeForwarderService;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;

import java.util.List;

public abstract class PubSubParticipantRoleBase  implements EndpointChangeNotificationActionInterface {

    private ProcessingPlantInterface processingPlant;
    private PubSubParticipantEndpointServiceInterface ipcEndpoint;
    private PubSubParticipant me;
    private DistributedPubSubSubscriptionMapIM publisherMapIM;
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
            DistributedPubSubSubscriptionMapIM publisherMapIM,
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
    abstract protected void performPublisherEventUpdateCheck(List<PetasosAdapterAddress> publishersRemoved, List<PetasosAdapterAddress> publishersAdded);
    abstract protected void performSubscriberEventUpdateCheck(List<PetasosAdapterAddress> subscribersRemoved, List<PetasosAdapterAddress> subscribersAdded);




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

    protected DistributedPubSubSubscriptionMapIM getPublisherMapIM(){
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
    public void notifyMembershipChange(List<PetasosAdapterAddress> endpointsAdded, List<PetasosAdapterAddress> endpointsRemoved) {
        performPublisherEventUpdateCheck(endpointsRemoved, endpointsAdded );
        performSubscriberEventUpdateCheck(endpointsRemoved, endpointsAdded);
    }

    @Override
    public void notifyMembershipChange(PetasosAdapterAddress changedEndpoint) {

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






}
