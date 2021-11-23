package net.fhirfactory.pegacorn.platform.edge.messaging.receive.common;

import net.fhirfactory.pegacorn.core.constants.petasos.PegacornIPCCommonValues;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.platform.edge.messaging.codecs.*;
import net.fhirfactory.pegacorn.workshops.EdgeWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.EdgeIngresMessagingGatewayWUP;
import org.apache.camel.ExchangePattern;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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

    protected PetasosEndpointTopologyTypeEnum specifyIPCType() {
        return (PetasosEndpointTopologyTypeEnum.EDGE_JGROUPS_INTRAZONE_SERVICE);
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
