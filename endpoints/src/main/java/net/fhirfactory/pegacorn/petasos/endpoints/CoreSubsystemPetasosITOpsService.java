package net.fhirfactory.pegacorn.petasos.endpoints;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.audit.PetasosAuditServicesEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.ipc.PetasosIPCMessagingEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.PetasosOAMInterceptionEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.PetasosOAMMetricsEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.PetasosOAMPubSubEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.PetasosOAMTopologyEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.tasks.PetasosTaskServicesEndpoint;
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
    private PetasosIPCMessagingEndpoint petasosIPCMessagingEndpoint;

    @Inject
    private PetasosOAMTopologyEndpoint petasosOAMTopologyEndpoint;

    @Inject
    private PetasosOAMPubSubEndpoint petasosOAMPubSubEndpoint;

    @Inject
    private PetasosAuditServicesEndpoint petasosAuditServicesEndpoint;

    @Inject
    private PetasosOAMInterceptionEndpoint petasosOAMInterceptionEndpoint;

    @Inject
    private PetasosOAMInterceptionEndpoint petasosOAMInterceptionEndpoint1;

    @Inject
    private PetasosOAMMetricsEndpoint petasosOAMMetricsEndpoint;

    @Inject
    private PetasosTaskServicesEndpoint petasosTaskServicesEndpoint;

    //
    // Constructor(s)
    //

    public CoreSubsystemPetasosITOpsService(){
        super();
        this.initialised = false;
    }

    //
    // Post Constructor
    //

    @PostConstruct
    public void initialise(){
        LOG.debug(".initailse(): Entry");
        if(!this.initialised) {
            LOG.info(".initialise(): Initialising...");
            LOG.info(".initialise(): [ITOpsAgent Initialisation] Start");

            LOG.info(".initialise(): [ITOpsAgent Initialisation] Finish");
            LOG.info(".initialise(): petasosIPCMessagingEndpoint ==> {}", petasosIPCMessagingEndpoint.getEndpointID());
            LOG.info(".initialise(): petasosOAMPubSubEndpoint ==> {}", petasosOAMPubSubEndpoint.getEndpointID());
            LOG.info(".initialise(): petasosOAMTopologyEndpoint ==> {}", petasosOAMTopologyEndpoint.getEndpointID());
            LOG.info(".initialise(): petasosAuditServicesEndpoint ==>{}", petasosAuditServicesEndpoint.getEndpointID());
            LOG.info(".initialise(): petasosOAMMetricsEndpoint ==>{}", petasosOAMMetricsEndpoint.getEndpointID());
            LOG.info(".initialise(): petasosOAMInterceptionEndpoint ==>{}", petasosOAMInterceptionEndpoint.getEndpointID());
            LOG.info(".initialise(): petasosOAMInterceptionEndpoint1 ==>{}", petasosOAMInterceptionEndpoint1.getEndpointID());
            LOG.info(".initialise(): petasosTaskServicesEndpoint ==>{}", petasosTaskServicesEndpoint.getEndpointID());
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
