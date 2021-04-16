package net.fhirfactory.pegacorn.platform.edge.receive;


import net.fhirfactory.pegacorn.deployment.topology.model.nodes.DefaultWorkshopSetEnum;
import net.fhirfactory.pegacorn.petasos.wup.archetypes.EdgeIngresMessagingGatewayWUP;
import org.apache.camel.CamelContext;
import org.apache.camel.ExchangePattern;
import org.apache.camel.component.netty.ServerInitializerFactory;
import org.apache.camel.spi.Registry;

import javax.inject.Inject;

public abstract class DefaultFHIRResourceFromLadonReceiver extends EdgeIngresMessagingGatewayWUP {

    @Inject
    CamelContext camelCTX;

    @Override
    protected void executePostInitialisationActivities(){
        executePostInitialisationActivities(camelCTX, specifyServerInitializerFactoryName());

    }

    public String getSourceSubsystem(){return("Ladon");}

    public static void executePostInitialisationActivities(CamelContext camelCTX, String serverInitializerFactoryName){
        Registry registry = camelCTX.getRegistry();
        ServerInitializerFactory serverInitializerFactory = new IPCPacketDecoderInitializerFactory();
        registry.bind(serverInitializerFactoryName, serverInitializerFactory);
    }
    
    private String getWUPContinuityRoute() {
        return ("seda:" + this.getNameSet().getRouteCoreWUP() + ".InnerWUP.Continuity");
    }

    @Override
    public void configure() throws Exception {
        getLogger().info("EdgeIPCReceiverWUPTemplate :: WUPIngresPoint/ingresFeed --> {}", this.ingresFeed());
        getLogger().info("EdgeIPCReceiverWUPTemplate :: WUPEgressPoint/egressFeed --> {}", this.egressFeed());

        if (this.getIngresTopologyEndpointElement() == null) {
            getLogger().error("EdgeIPCReceiverWUPTemplate::configure(): Guru Software Meditation Error --> No Ingres Point Specified to consider!!!");
        }

        getLogger().debug("EdgeIPCReceiverWUPTemplate::configure(): http provider --> {}", this.specifyEndpointComponentDefinition());
        getLogger().debug("EdgeIPCReceiverWUPTemplate::configure(): hostname --> {}", this.getIngresTopologyEndpointElement().getInternalHostname());
        getLogger().debug("EdgeIPCReceiverWUPTemplate::configure(): port --> {}", this.getIngresTopologyEndpointElement().getExposedPort());

        fromWithStandardExceptionHandling(this.ingresFeed())
                .routeId(getNameSet().getRouteCoreWUP())
                .transform(simple("${bodyAs(String)}"))
                .bean(InterProcessingPlantHandoverPacketDecoderBean.class, "handoverPacketDecode(*)")
                .bean(InterProcessingPlantHandoverRegistrationBean.class, "ipcReceiverActivityStart(*,  Exchange," + this.getWUPTopologyNode().extractNodeKey() + ")")
                .to(ExchangePattern.InOnly, getWUPContinuityRoute())
                .bean(InterProcessingPlantHandoverResponseGenerationBean.class, "generateInterProcessingPlantHandoverResponse(*,  Exchange," + this.getWUPTopologyNode().extractNodeKey() + ")")
                .bean(InterProcessingPlantHandoverResponseEncoderBean.class, "responseEncoder(*)");

        fromWithStandardExceptionHandling(getWUPContinuityRoute())
                .bean(InterProcessingPlantHandoverUoWExtractionBean.class, "extractUoW(*, Exchange," + getSourceSubsystem() + ")")
                .to(egressFeed());
    }

    @Override
    protected String specifyServerInitializerFactoryName() {
        return "ipcFromLandReceiverFactory";
    }

    @Override
    protected String specifyWUPWorkshop() {
        return (DefaultWorkshopSetEnum.EDGE_WORKSHOP.getWorkshop());
    }
}
