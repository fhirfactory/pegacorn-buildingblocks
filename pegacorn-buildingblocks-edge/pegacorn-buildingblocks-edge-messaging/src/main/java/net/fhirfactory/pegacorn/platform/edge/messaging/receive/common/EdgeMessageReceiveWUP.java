package net.fhirfactory.pegacorn.platform.edge.messaging.receive.common;

import net.fhirfactory.pegacorn.components.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.deployment.properties.codebased.PegacornIPCCommonValues;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosTopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.platform.edge.messaging.codecs.*;
import net.fhirfactory.pegacorn.workshops.EdgeWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.EdgeIngresMessagingGatewayWUP;
import org.apache.camel.ExchangePattern;

import javax.inject.Inject;

public abstract class EdgeMessageReceiveWUP extends EdgeIngresMessagingGatewayWUP {

    @Inject
    private EdgeWorkshop edgeWorkshop;

    @Inject
    private PegacornIPCCommonValues ipcFunctionalityNames;

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (edgeWorkshop);
    }

    protected abstract String specifyIPCZoneType();

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
//                .transform(simple("${bodyAs(String)}"))
                .bean(InterProcessingPlantHandoverPacketDecoderBean.class, "handoverPacketDecode(*)")
                .bean(InterProcessingPlantHandoverRegistrationBean.class, "ipcReceiverActivityStart(*,  Exchange)")
                .to(ExchangePattern.InOnly, getWUPContinuityRoute())
                .bean(InterProcessingPlantHandoverResponseGenerationBean.class, "generateInterProcessingPlantHandoverResponse(*,  Exchange)")
                .bean(InterProcessingPlantHandoverResponseEncoderBean.class, "responseEncoder(*)");

        fromWithStandardExceptionHandling(getWUPContinuityRoute())
                .bean(InterProcessingPlantHandoverUoWExtractionBean.class, "extractUoW(*, Exchange)")
                .to(egressFeed());
    }

    //
    // Application Logic (Establishing WUP)
    //

    protected PetasosTopologyEndpointTypeEnum specifyIPCType() {
        return (PetasosTopologyEndpointTypeEnum.JGROUPS_INTRAZONE_SERVICE);
    }

    //
    // Getters and Setters
    //

    public PegacornIPCCommonValues getIPCComponentNames() {
        return ipcFunctionalityNames;
    }
}
