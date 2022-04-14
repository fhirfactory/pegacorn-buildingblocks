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
package net.fhirfactory.pegacorn.platform.edge.messaging.forward;

import net.fhirfactory.pegacorn.core.constants.petasos.PegacornIPCCommonValues;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.interfaces.edge.PetasosEdgeMessageForwarderService;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapterDefinition;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpointContainer;
import net.fhirfactory.pegacorn.petasos.core.participants.manager.LocalPetasosParticipantCacheIM;
import net.fhirfactory.pegacorn.petasos.core.participants.manager.LocalPetasosParticipantSubscriptionMapIM;
import net.fhirfactory.pegacorn.petasos.endpoints.services.messaging.PetasosIPCMessagingEndpoint;
import net.fhirfactory.pegacorn.petasos.wup.helper.EgressActivityFinalisationRegistration;
import net.fhirfactory.pegacorn.platform.edge.messaging.codecs.InterProcessingPlantHandoverPacketGenerationBean;
import net.fhirfactory.pegacorn.platform.edge.messaging.codecs.InterProcessingPlantHandoverResponseProcessingBean;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverPacket;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.packets.InterProcessingPlantHandoverResponsePacket;
import net.fhirfactory.pegacorn.workshops.EdgeWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.EdgeEgressMessagingGatewayWUP;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PetasosEdgeMessageForwarderWUP extends EdgeEgressMessagingGatewayWUP implements PetasosEdgeMessageForwarderService {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosEdgeMessageForwarderWUP.class);

    private static String WUP_VERSION = "1.0.0";

    @Inject
    private PetasosIPCMessagingEndpoint petasosMessagingEndpoint;

    @Inject
    private EdgeWorkshop edgeWorkshop;

    @Inject
    private PegacornIPCCommonValues ipcFunctionalityNames;

    @Inject
    LocalPetasosParticipantSubscriptionMapIM topicServer;

    @Inject
    LocalPetasosParticipantCacheIM localPetasosParticipantCacheIM;

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (edgeWorkshop);
    }


    protected PetasosIPCMessagingEndpoint getPetasosMessagingEndpoint() {
        return (petasosMessagingEndpoint);
    }

    //
    // Getters and Setters
    //

    public PegacornIPCCommonValues getIPCComponentNames() {
        return ipcFunctionalityNames;
    }

    public LocalPetasosParticipantSubscriptionMapIM getTopicServer(){
        return(this.topicServer);
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    //
    // WUP Specification
    //

    @Override
    protected MessageBasedWUPEndpointContainer specifyEgressEndpoint() {
        MessageBasedWUPEndpointContainer egressEndpoint = new MessageBasedWUPEndpointContainer();
        assignEgressTopologyEndpoint();
        egressEndpoint.setEndpointTopologyNode(getAssociatedEgressTopologyEndpoint());
        egressEndpoint.setEndpointSpecification(getIPCComponentNames().getInterZoneIPCForwarderRouteEndpointName());
        egressEndpoint.setFrameworkEnabled(false);
        return(egressEndpoint);
    }

    @Override
    protected List<DataParcelManifest> specifySubscriptionTopics() {
        List<DataParcelManifest> subscriptionList = new ArrayList<>();
        return (subscriptionList);
    }

    @Override
    protected List<DataParcelManifest> declarePublishedTopics() {
        return (new ArrayList<>());
    }

    @Override
    protected String specifyWUPInstanceName() {
        return (PetasosPropertyConstants.TASK_IPC_FORWARDER_NAME);
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return (WUP_VERSION);
    }

    @Override
    protected String specifyEgressInterfaceName() {
        return (getInterfaceNames().getPetasosIPCMessagingEndpointName());
    }

    @Override
    protected IPCAdapterDefinition specifyEgressInterfaceDefinition() {
        IPCAdapterDefinition interfaceDefinition = new IPCAdapterDefinition();
        interfaceDefinition.setInterfaceFormalName(getIPCComponentNames().getJGroupsInterzoneRepeaterClientInterfaceType());
        interfaceDefinition.setInterfaceFormalVersion("1.0.0");
        return (interfaceDefinition);
    }

    //
    // Route
    //

    @Override
    public void configure() throws Exception {

        getLogger().info("EdgeIPCForwarderWUP :: WUPIngresPoint/ingresFeed --> {}", ingresFeed());
        getLogger().info("EdgeIPCForwarderWUP :: WUPEgressPoint/egressFeed --> {}", egressFeed());

        fromIncludingPetasosServices(ingresFeed())
                .routeId(getNameSet().getRouteCoreWUP())
                .process(new NodeDetailInjector())
                .bean(InterProcessingPlantHandoverPacketGenerationBean.class, "constructInterProcessingPlantHandoverPacket(*,  Exchange)")
                .to(egressFeed())
                .bean(InterProcessingPlantHandoverResponseProcessingBean.class, "processResponse(*, Exchange)")
                .bean(EgressActivityFinalisationRegistration.class,"registerActivityFinishAndFinalisation(*,  Exchange)");

        from(egressFeed())
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        InterProcessingPlantHandoverPacket packet = exchange.getIn().getBody(InterProcessingPlantHandoverPacket.class);
                        if(getLogger().isInfoEnabled()) {
                            getLogger().info("Calling JGroups IPC Forwarder: Target->{}", packet.getTarget());
                        }
                        InterProcessingPlantHandoverResponsePacket response = getPetasosMessagingEndpoint().sendIPCMessage(packet.getTarget(), packet);
                        exchange.getIn().setBody(response);
                    }
                });
    }

    //
    // Component Id
    //

    @Override
    public ComponentIdType getComponentId() {
        return (getMeAsATopologyComponent().getComponentID());
    }
}
