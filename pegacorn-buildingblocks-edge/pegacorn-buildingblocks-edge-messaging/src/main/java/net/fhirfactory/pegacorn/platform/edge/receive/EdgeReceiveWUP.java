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
package net.fhirfactory.pegacorn.platform.edge.receive;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.components.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.TopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.GenericMessageBasedWUPEndpoint;
import net.fhirfactory.pegacorn.petasos.ipc.beans.receiver.*;
import net.fhirfactory.pegacorn.workshops.EdgeWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.EdgeIngresMessagingGatewayWUP;
import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class EdgeReceiveWUP extends EdgeIngresMessagingGatewayWUP {
    private static final Logger LOG = LoggerFactory.getLogger(EdgeReceiveWUP.class);

    private boolean initialised;
    private IPCTopologyEndpoint designatedEndpoint;

    @Inject
    private CamelContext camelCTX;

    @Inject
    private EdgeWorkshop edgeWorkshop;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Override
    protected void executePostInitialisationActivities(){
        deriveTopologyEndpoint();
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyWUPInstanceName() {
        return ("EdgeReceiveWUP");
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return ("1.0.0");
    }

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (edgeWorkshop);
    }

    //
    // Application Logic (Route Definition)
    //

    private String getWUPContinuityRoute() {
        return ("seda:" + this.getNameSet().getRouteCoreWUP() + ".InnerWUP.Continuity");
    }

    @Override
    public void configure() throws Exception {
        if (this.getIngresTopologyEndpoint() == null) {
            getLogger().error("EdgeIPCReceiverWUPTemplate::configure(): Guru Software Meditation Error --> No Ingres Point Specified to consider!!!");
        }

        String ingresFeed = this.getIngresTopologyEndpoint().getEndpointSpecification();
        String egressFeed = this.getEgressTopologyEndpoint().getEndpointSpecification();

        getLogger().info("EdgeIPCReceiverWUPTemplate :: WUPIngresPoint/ingresFeed --> {}", ingresFeed);
        getLogger().info("EdgeIPCReceiverWUPTemplate :: WUPEgressPoint/egressFeed --> {}", egressFeed);

        getLogger().debug("EdgeIPCReceiverWUPTemplate::configure(): http provider --> {}", this.getIngresTopologyEndpoint());
        getLogger().debug("EdgeIPCReceiverWUPTemplate::configure(): hostname --> {}", this.getIngresTopologyEndpoint().getEndpointTopologyNode().getInterfaceDNSName());
        getLogger().debug("EdgeIPCReceiverWUPTemplate::configure(): port --> {}", this.getIngresTopologyEndpoint().getEndpointTopologyNode().getPortValue());

        fromWithStandardExceptionHandling(ingresFeed)
                .routeId(getNameSet().getRouteCoreWUP())
                .transform(simple("${bodyAs(String)}"))
                .bean(InterProcessingPlantHandoverPacketDecoderBean.class, "handoverPacketDecode(*)")
                .bean(InterProcessingPlantHandoverRegistrationBean.class, "ipcReceiverActivityStart(*,  Exchange," + this.getWUPTopologyNode().getNodeFDN().toTag() + ")")
                .to(ExchangePattern.InOnly, getWUPContinuityRoute())
                .bean(InterProcessingPlantHandoverResponseGenerationBean.class, "generateInterProcessingPlantHandoverResponse(*,  Exchange," + this.getWUPTopologyNode().getNodeFDN().toTag() + ")")
                .bean(InterProcessingPlantHandoverResponseEncoderBean.class, "responseEncoder(*)");

        fromWithStandardExceptionHandling(getWUPContinuityRoute())
                .bean(InterProcessingPlantHandoverUoWExtractionBean.class, "extractUoW(*, Exchange)")
                .to(egressFeed);
    }

    //
    // Application Logic (Establishing WUP)
    //

    protected TopologyEndpointTypeEnum specifyIPCType() {
        return (TopologyEndpointTypeEnum.PEGACORN_IPC_MESSAGING_SERVICE);
    }

    protected void deriveTopologyEndpoint(){
        getLogger().debug(".deriveIPCTopologyEndpoint(): Entry");
        IPCTopologyEndpoint ipcEndpoint = null;
        for(TopologyNodeFDN currentEndpointFDN: getProcessingPlant().getProcessingPlantNode().getEndpoints()){
            IPCTopologyEndpoint currentEndpoint = (IPCTopologyEndpoint)getTopologyIM().getNode(currentEndpointFDN);
            TopologyEndpointTypeEnum endpointType = currentEndpoint.getEndpointType();
            boolean endpointTypeMatches = endpointType.equals(specifyIPCType());
            if(endpointTypeMatches){
                    ipcEndpoint = currentEndpoint;
                    break;
            }
        }
        if(ipcEndpoint == null){
            getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, no Endpoint found");
            return;
        }
        if(ipcEndpoint.getSupportedInterfaceSet().size() <= 0){
            getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, no Interfaces defined in Endpoint");
            return;
        }
        getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, found IPCTopologyEndpoint, returning it");
        setDesignatedEndpoint(ipcEndpoint);
    }

    @Override
    protected GenericMessageBasedWUPEndpoint specifyIngresTopologyEndpoint() {
        GenericMessageBasedWUPEndpoint ingresEndpoint = new GenericMessageBasedWUPEndpoint();
        ingresEndpoint.setEndpointTopologyNode(getDesignatedEndpoint());
        ingresEndpoint.setEndpointSpecification("direct:EdgeReceiveCommon");
        ingresEndpoint.setFrameworkEnabled(false);
        return(ingresEndpoint);
    }

    //
    // Getters and Setters
    //

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    public IPCTopologyEndpoint getDesignatedEndpoint() {
        return designatedEndpoint;
    }

    public void setDesignatedEndpoint(IPCTopologyEndpoint designatedEndpoint) {
        this.designatedEndpoint = designatedEndpoint;
    }
}
