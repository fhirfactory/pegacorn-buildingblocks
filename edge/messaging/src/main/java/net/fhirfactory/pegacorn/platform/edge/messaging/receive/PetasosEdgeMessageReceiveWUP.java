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
package net.fhirfactory.pegacorn.platform.edge.messaging.receive;

import net.fhirfactory.pegacorn.core.constants.petasos.PegacornIPCCommonValues;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentConnectivityContextEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapterDefinition;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpointContainer;
import net.fhirfactory.pegacorn.platform.edge.messaging.codecs.*;
import net.fhirfactory.pegacorn.workshops.EdgeWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.EdgeIngresMessagingGatewayWUP;
import org.apache.camel.ExchangePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PetasosEdgeMessageReceiveWUP extends EdgeIngresMessagingGatewayWUP {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosEdgeMessageReceiveWUP.class);

    @Inject
    private EdgeWorkshop edgeWorkshop;

    @Inject
    private PegacornIPCCommonValues ipcFunctionalityNames;

    //
    // Constructor(s)
    //

    public PetasosEdgeMessageReceiveWUP(){
        super();
    }

    //
    // Getters (and Setters and Specifies)
    //

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (edgeWorkshop);
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyWUPInstanceName() {
        return (PetasosPropertyConstants.TASK_IPC_RECEIVER_NAME);
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return ("1.0.0");
    }

    @Override
    protected SoftwareComponentConnectivityContextEnum specifyConnectivityContext(){
        return(SoftwareComponentConnectivityContextEnum.COMPONENT_ROLE_SUBSYSTEM_EDGE);
    }

    @Override
    protected MessageBasedWUPEndpointContainer specifyIngresEndpoint() {
        MessageBasedWUPEndpointContainer ingresEndpoint = new MessageBasedWUPEndpointContainer();
        assignIngresTopologyEndpoint();
        ingresEndpoint.setEndpointTopologyNode(getAssociatedIngresTopologyEndpoint());
        ingresEndpoint.setEndpointSpecification(getIPCComponentNames().getInterZoneIPCReceiverRouteEndpointName());
        ingresEndpoint.setFrameworkEnabled(false);
        return(ingresEndpoint);
    }

    @Override
    protected String specifyIngresInterfaceName() {
        return (getInterfaceNames().getPetasosIPCMessagingEndpointName());
    }

    @Override
    protected IPCAdapterDefinition specifyIngresInterfaceDefinition() {
        IPCAdapterDefinition interfaceDefinition = new IPCAdapterDefinition();
        interfaceDefinition.setInterfaceFormalName(getIPCComponentNames().getJGroupsInterzoneRepeaterClientInterfaceType());
        interfaceDefinition.setInterfaceFormalVersion("1.0.0");
        return (interfaceDefinition);
    }

    //
    // Application Logic (Route Definition)
    //

    private String getWUPContinuityRoute() {
        return ("seda:" + this.getNameSet().getRouteCoreWUP() + ".InnerWUP.Continuity");
    }

    @Override
    public void configure() throws Exception {
        getLogger().info("EdgeIPCReceiverWUP :: WUPIngresPoint/ingresFeed --> {}", ingresFeed());
        getLogger().info("EdgeIPCReceiverWUP :: WUPEgressPoint/egressFeed --> {}", egressFeed());

        fromIncludingPetasosServices(ingresFeed())
                .routeId(getNameSet().getRouteCoreWUP())
                .bean(InterProcessingPlantHandoverPacketDecoderBean.class, "handoverPacketDecode(*)")
                .bean(InterProcessingPlantHandoverRegistrationBean.class, "ipcReceiverActivityStart(*,  Exchange)")
                .to(ExchangePattern.InOnly, getWUPContinuityRoute())
                .bean(InterProcessingPlantHandoverResponseGenerationBean.class, "generateInterProcessingPlantHandoverResponse(*,  Exchange)")
                .bean(InterProcessingPlantHandoverResponseEncoderBean.class, "responseEncoder(*)");

        fromWithStandardExceptionHandling(getWUPContinuityRoute())
                .bean(InterProcessingPlantHandoverUoWExtractionBean.class, "extractUoW(*, Exchange)")
                .to(egressFeed());
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
}
