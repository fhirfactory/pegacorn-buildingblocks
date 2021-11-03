package net.fhirfactory.pegacorn.petasos.endpoints;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.ipc.PetasosInterZoneIPCEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.ipc.PetasosIntraZoneIPCEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.discovery.PetasosInterZoneOAMDiscoveryEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.discovery.PetasosIntraZoneOAMDiscoveryEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.pubsub.PetasosInterZoneOAMPubSubEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.pubsub.PetasosIntraZoneOAMPubSubEndpoint;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CoreSubsystemPetasosITOpsService extends RouteBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(CoreSubsystemPetasosITOpsService.class);
    private boolean initialised;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private PetasosInterZoneIPCEndpoint interZoneIPCEndpoint;

    @Inject
    private PetasosIntraZoneIPCEndpoint intraZoneIPCEndpoint;

    @Inject
    private PetasosIntraZoneOAMPubSubEndpoint intraZoneOAMPubSubEndpoint;

    @Inject
    private PetasosIntraZoneOAMDiscoveryEndpoint intraZoneOAMDiscoveryEndpoint;

    @Inject
    private PetasosInterZoneOAMDiscoveryEndpoint interZoneOAMDiscoveryEndpoint;

    @Inject
    private PetasosInterZoneOAMPubSubEndpoint interZoneOAMPubSubEndpoint;


    public CoreSubsystemPetasosITOpsService(){
        super();
        this.initialised = false;
    }

    @PostConstruct
    public void initialise(){
        LOG.debug(".initailse(): Entry");
        if(!this.initialised) {
            LOG.info(".initialise(): Initialising...");
            LOG.info(".initialise(): [ITOpsAgent Initialisation] Start");

            LOG.info(".initialise(): [ITOpsAgent Initialisation] Finish");
            LOG.info(".initialise(): interZoneIPCEndpoint ==> {}", interZoneIPCEndpoint.getEndpointID());
            LOG.info(".initialise(): intraZoneIPCEndpoint ==> {}", intraZoneIPCEndpoint.getEndpointID());
            LOG.info(".initialise(): intraZoneOAMPubSubEndpoint ==> {}", intraZoneOAMPubSubEndpoint.getEndpointID());
            LOG.info(".initialise(): interZoneOAMPubSubEndpoint ==> {}", interZoneOAMPubSubEndpoint.getEndpointID());
            LOG.info(".initialise(): intraZoneOAMDiscoveryEndpoint ==> {}", intraZoneOAMDiscoveryEndpoint.getEndpointID());
            LOG.info(".initialise(): interZoneOAMDiscoveryEndpoint ==> {}", interZoneOAMDiscoveryEndpoint.getEndpointID());
            LOG.info(".initialise(): Done.");
        } else {
            LOG.debug(".initialise(): Already initialised, nothing to do!");
        }
        LOG.debug(".initialise(): Exit");
    }

    @Override
    public void configure() throws Exception {
        String nodeName = processingPlant.getProcessingPlantNode().getComponentRDN().getNodeName();
        String nodeVersion = processingPlant.getProcessingPlantNode().getComponentRDN().getNodeVersion();
        String friendlyName = nodeName + "(" + nodeVersion + ").CoreSubsystemEndpoints";

        LOG.info(".configure(): Entry, friendlyName->{}", friendlyName);

        from("timer://"+friendlyName+"?delay=1000&repeatCount=1")
                .routeId("ProcessingPlant::"+friendlyName)
                .log(LoggingLevel.DEBUG, "Starting....");
    }
}