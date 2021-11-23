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
package net.fhirfactory.pegacorn.platform.edge.messaging.forward.common;

import net.fhirfactory.pegacorn.core.constants.petasos.PegacornIPCCommonValues;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.IntraSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.IntraSubsystemPubSubParticipantIdentifier;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.RemoteSubscriptionStatus;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.petasos.core.subscriptions.manager.DataParcelSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.ipc.base.PetasosIPCEndpoint;
import net.fhirfactory.pegacorn.platform.edge.messaging.codecs.InterProcessingPlantHandoverResponseProcessingBean;
import net.fhirfactory.pegacorn.platform.edge.messaging.codecs.InterProcessingPlantHandoverPacketGenerationBean;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import net.fhirfactory.pegacorn.workshops.EdgeWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.EdgeEgressMessagingGatewayWUP;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public abstract class EdgeMessageForwardWUP extends EdgeEgressMessagingGatewayWUP {

    @Inject
    private EdgeWorkshop edgeWorkshop;

    @Inject
    private PegacornIPCCommonValues ipcFunctionalityNames;

    @Inject
    DataParcelSubscriptionMapIM topicServer;

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (edgeWorkshop);
    }

    protected abstract String specifyIPCZoneType();
    protected abstract PetasosIPCEndpoint specifyIPCEndpoint();

    //
    // Application Logic (Route Definition)
    //

    private String getWUPContinuityRoute() {
        return ("seda:" + this.getNameSet().getRouteCoreWUP() + ".InnerWUP.Continuity");
    }

    @Override
    public void configure() throws Exception {

        getLogger().info("EdgeIPCForwarderWUP :: WUPIngresPoint/ingresFeed --> {}", ingresFeed());
        getLogger().info("EdgeIPCForwarderWUP :: WUPEgressPoint/egressFeed --> {}", egressFeed());

        fromIncludingPetasosServices(ingresFeed())
                .routeId(getNameSet().getRouteCoreWUP())

                .process(new NodeDetailInjector())
                .bean(InterProcessingPlantHandoverPacketGenerationBean.class, "constructInterProcessingPlantHandoverPacket(*,  Exchange)")
                .to(egressFeed())
                .bean(InterProcessingPlantHandoverResponseProcessingBean.class, "processResponse(*, Exchange)");

        from(egressFeed())
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        InterProcessingPlantHandoverPacket packet = exchange.getIn().getBody(InterProcessingPlantHandoverPacket.class);
                        InterProcessingPlantHandoverResponsePacket response = getIPCEndpoint().sendIPCMessage(packet.getTarget(), packet);
                        exchange.getIn().setBody(response);
                    }
                });
    }

    //
    // Application Logic (Establishing WUP)
    //

    protected PetasosEndpointTopologyTypeEnum specifyIPCType() {
        return (PetasosEndpointTopologyTypeEnum.EDGE_JGROUPS_INTRAZONE_SERVICE);
    }

    @Override
    public void executePostInitialisationActivities(){
        this.getIPCEndpoint().initialise();
    }

    @Override
    protected List<DataParcelManifest> declarePublishedTopics() {
        return (new ArrayList<>());
    }

    //
    // Getters and Setters
    //

    public PegacornIPCCommonValues getIPCComponentNames() {
        return ipcFunctionalityNames;
    }

    public DataParcelSubscriptionMapIM getTopicServer(){
        return(this.topicServer);
    }

    protected PetasosIPCEndpoint getIPCEndpoint(){
        return(specifyIPCEndpoint());
    }

    //
    // Remote Subscription Service
    //

    protected RemoteSubscriptionStatus subscribeToDataParcelSet(List<DataParcelManifest> contentSubscriptionList, PubSubParticipant subscriber) {
        getLogger().info(".subscribeToDataParcelSet(): Entry, contentSubscriptionList->{}, subscriber->{}", contentSubscriptionList, subscriber);
        if(contentSubscriptionList == null || subscriber == null){
            getLogger().debug(".contentSubscriptionList(): Exit, either contentSubscriptionList or subscriber is null");
            RemoteSubscriptionStatus badStatus = new RemoteSubscriptionStatus();
            badStatus.setSubscriptionSuccessful(false);
            badStatus.setSubscriptionCommentary("Either contentSubscriptionList or subscriber is null!");
            return(badStatus);
        }
        // Add LocalSubscriber Details (i.e. the local WUP)
        IntraSubsystemPubSubParticipant localSubscriber = new IntraSubsystemPubSubParticipant();
        IntraSubsystemPubSubParticipantIdentifier localSubscriberIdentifier = new IntraSubsystemPubSubParticipantIdentifier(getAssociatedTopologyNode().getComponentID());
        localSubscriber.setIdentifier(localSubscriberIdentifier);
        subscriber.setIntraSubsystemParticipant(localSubscriber);
        // Now Register within the (Internal) PubSub Service
        for(DataParcelManifest currentDataParcel: contentSubscriptionList) {
            getTopicServer().addTopicSubscriber(currentDataParcel, subscriber);
        }
        RemoteSubscriptionStatus okStatus = new RemoteSubscriptionStatus();
        okStatus.setSubscriptionSuccessful(true);
        getLogger().debug(".contentSubscriptionList(): Exit, okStatus->{}", okStatus);
        return (okStatus);
    }

}
