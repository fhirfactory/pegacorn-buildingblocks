/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.petasos.core.moa.wup;

import net.fhirfactory.pegacorn.camel.BaseRouteBuilder;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.interfaces.topology.*;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentStatusEnum;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.SoftwareComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistrationStatus;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemManifestType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapterDefinition;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCServerTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.nodes.ProcessingPlantSoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.SolutionTopologyNode;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.FHIRElementTopicFactory;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.manager.WorkUnitProcessorFrameworkManager;
import net.fhirfactory.pegacorn.petasos.core.participants.management.LocalParticipantManager;
import net.fhirfactory.pegacorn.petasos.oam.metrics.PetasosMetricAgentFactory;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.ProcessingPlantMetricsAgentAccessor;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.reporting.tasks.PetasosTaskReportAgentFactory;
import net.fhirfactory.pegacorn.petasos.oam.reporting.tasks.agents.WorkUnitProcessorTaskReportAgent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generic Trigger Initiated Message Architecture Work Unit Processor (WUP) Template
 *
 */

public abstract class GenericTriggerBasedWUPTemplate extends BaseRouteBuilder {

    abstract protected Logger specifyLogger();

    protected Logger getLogger(){
        return(specifyLogger());
    }

    private WorkUnitProcessorSoftwareComponent topologyNode;
    private RouteElementNames nameSet;
    private WUPArchetypeEnum wupArchetype;
    private List<DataParcelManifest> topicSubscriptionSet;
    private PetasosEndpointContainerInterface egressEndpoint;
    private PetasosEndpointContainerInterface ingresEndpoint;
    private String wupInstanceName;
    private WorkUnitProcessorMetricsAgent metricsAgent;
    private WorkUnitProcessorTaskReportAgent taskReportAgent;
    private boolean initialised;

    @Inject
    private WorkUnitProcessorFrameworkManager frameworkManager;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private FHIRElementTopicFactory fhirTopicIDBuilder;

    @Inject
    private ProcessingPlantInterface processingPlantServices;

    @Inject
    private CamelContext camelContext;

    @Inject
    private PetasosMetricAgentFactory metricAgentFactory;

    @Inject
    private LocalParticipantManager participantManager;

    @Inject
    private ProcessingPlantRoleSupportInterface processingPlantCapabilityStatement;
    
    @Inject
    private ProcessingPlantMetricsAgentAccessor processingPlantMetricsAgentAccessor;

    @Inject
    private PetasosTaskReportAgentFactory taskReportAgentFactory;

    //
    // Constructor(s)
    //

    public GenericTriggerBasedWUPTemplate() {
        super();
        this.wupInstanceName = getClass().getSimpleName();
        this.initialised = false;
    }

    /**
     * This function essentially establishes the WUP itself, by first calling all the (abstract classes realised within subclasses)
     * and setting the core attributes of the WUP. Then, it executes the buildWUPFramework() function, which invokes the Petasos
     * framework around this WUP.
     *
     * It is automatically called by the CDI framework following Constructor invocation (see @PostConstruct tag).
     */
    @PostConstruct
    protected void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(initialised){
            getLogger().debug(".initialise(): Already initialised, nothing to do");
            return;
        }
        getLogger().info(".initialise(): [WUP Base] Initialising.... ");
        getLogger().info(".initialise(): WUP Instance Name --> {}", getWUPInstanceName());
        getLogger().info(".initialise(): WUP Instance Version --> {}", specifyWUPInstanceVersion());

        getLogger().info(".initialise(): [Initialise the Processing Plant] Start");
        this.getProcessingPlant().initialisePlant();
        getLogger().info(".initialise(): [Initialise the Processing Plant] Finish");

        getLogger().info(".initialise(): [Build WUP Topology Node Element] Start");
        this.topologyNode = buildWUPNodeElement();
        getLogger().info(".initialise(): [Build WUP Topology Node Element] Finish");

        getLogger().info(".initialise(): [Build WUP Component Name-set] Start");
        this.nameSet = new RouteElementNames(getTopologyNode().getParticipant().getParticipantId());
        getLogger().info(".initialise(): [Build WUP Component Name-set] Finish");

        getLogger().info(".initialise(): Setting the WUP EgressEndpoint");
        this.egressEndpoint = specifyEgressEndpoint();
        this.getTopologyNode().setEgressEndpoint(this.egressEndpoint.getEndpointTopologyNode());
       
        getLogger().info(".initialise(): Setting the WUP IngresEndpoint");
        this.ingresEndpoint = specifyIngresEndpoint();
        this.getTopologyNode().setIngresEndpoint(this.ingresEndpoint.getEndpointTopologyNode());
        
        getLogger().info(".initialise(): Setting the WUP Archetype - which is used by the WUP Framework to ascertain what wrapping this WUP needs");
        this.wupArchetype =  specifyWUPArchetype();
        
        getLogger().info(".initialise(): Now invoking subclass initialising function(s)");
        executePostInitialisationActivities();
        
        getLogger().info(".initialise(): Setting the Topic Subscription Set (i.e. the list of Data Sets we will process)");
        this.topicSubscriptionSet = specifySubscriptionTopics();
        
        getLogger().info(".initialise(): Building my PetasosParticipant");
        registerParticipant();
        getLogger().info(".initialise(): Establish the metrics agent");
        ComponentIdType componentId = getTopologyNode().getComponentId();
        String participantName = getTopologyNode().getParticipant().getParticipantId().getName();
        this.metricsAgent = metricAgentFactory.newWorkUnitProcessingMetricsAgent(processingPlantCapabilityStatement, componentId, participantName);
        
        getLogger().info(".initialise(): Now call the WUP Framework constructure - which builds the Petasos framework around this WUP");
        buildWUPFramework(this.getContext());

        this.getTopologyNode().setComponentStatus(SoftwareComponentStatusEnum.SOFTWARE_COMPONENT_OPERATIONAL);
        participantManager.updateParticipantStatus(getTopologyNode().getParticipant().getParticipantId().getName(), PetasosParticipantStatusEnum.PARTICIPANT_IS_IDLE);

        this.initialised = true;

        getLogger().info(".initialise(): [WUP Base] Initialising.... Done...");
        getLogger().debug(".initialise(): Exit");
    }
    
    // To be implemented methods (in Specialisations)
    
    protected abstract List<DataParcelManifest> specifySubscriptionTopics();
    protected abstract List<DataParcelManifest> declarePublishedTopics();
    protected abstract WUPArchetypeEnum specifyWUPArchetype();
    protected abstract String specifyWUPInstanceVersion();

    protected abstract WorkshopInterface specifyWorkshop();
    protected abstract PetasosEndpointContainerInterface specifyIngresEndpoint();
    protected abstract PetasosEndpointContainerInterface specifyEgressEndpoint();

    protected WorkshopInterface getWorkshop(){
        return(specifyWorkshop());
    }
    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlantServices);
    }
    protected PegacornTopologyFactoryInterface getTopologyFactory(){
        return(processingPlantServices.getTopologyFactory());
    }

    public PetasosEndpointContainerInterface getEgressEndpoint() {
        return egressEndpoint;
    }

    public PetasosEndpointContainerInterface getIngresEndpoint() {
        return ingresEndpoint;
    }

    protected boolean getUsesWUPFrameworkGeneratedIngresEndpoint(){
        return(getIngresEndpoint().isFrameworkEnabled());
    }
    protected boolean getUsesWUPFrameworkGeneratedEgressEndpoint(){
        return(getEgressEndpoint().isFrameworkEnabled());
    }

    protected void executePostInitialisationActivities(){
        // Subclasses can optionally override
    }

    protected SolutionTopologyNode getSolutionTopology(){return(processingPlantServices.getSolutionNode());}

    public void buildWUPFramework(CamelContext routeContext) {
        getLogger().debug(".buildWUPFramework(): Entry");
        frameworkManager.buildWUPFramework(this.topologyNode, this.getTopicSubscriptionSet(), this.getWupArchetype(), getMetricsAgent());
        getLogger().debug(".buildWUPFramework(): Exit");
    }
    
    public String getEndpointHostName(){
        String dnsName = getProcessingPlant().getTopologyNode().getAssignedDNSName();
        return(dnsName);
    }
    
    public WorkUnitProcessorFrameworkManager getFrameworkManager(){
        return(this.frameworkManager);
    }
    
    public TopologyIM getTopologyIM(){
        return(topologyIM);
    }

    public void setTopologyNode(WorkUnitProcessorSoftwareComponent topologyNode) {
        this.topologyNode = topologyNode;
    }

    public RouteElementNames getNameSet() {
        return nameSet;
    }

    public String getWupInstanceName() {
        return getTopologyNode().getComponentId().getName();
    }

    public WUPArchetypeEnum getWupArchetype() {
        return wupArchetype;
    }

    public List<DataParcelManifest> getTopicSubscriptionSet() {
        return topicSubscriptionSet;
    }

    public void setTopicSubscriptionSet(List<DataParcelManifest> topicSubscriptionSet) {
        this.topicSubscriptionSet = topicSubscriptionSet;
    }

    public String getVersion() {
        return topologyNode.getVersion();
    }

    public FHIRElementTopicFactory getFHIRTopicIDBuilder(){
        return(this.fhirTopicIDBuilder);
    }

    public CamelContext getCamelContext() {
        return camelContext;
    }

    public String getWUPInstanceName(){
        return(this.wupInstanceName);
    }

    protected WorkUnitProcessorMetricsAgent getMetricsAgent(){
        return(this.metricsAgent);
    }

    public ProcessingPlantRoleSupportInterface getProcessingPlantServiceProviderFunction() {
        return (processingPlantCapabilityStatement);
    }


    //
    // Routing Support Functions
    //

    protected String ingresFeed(){
        return(getIngresEndpoint().getEndpointSpecification());
    }

    protected String egressFeed(){
        return(getEgressEndpoint().getEndpointSpecification());
    }

    public class NodeDetailInjector implements Processor{
        @Override
        public void process(Exchange exchange) throws Exception {
            getLogger().debug("NodeDetailInjector.process(): Entry");
            boolean alreadyInPlace = false;
            if(exchange.hasProperties()) {
                WorkUnitProcessorSoftwareComponent wupTN = exchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorSoftwareComponent.class);
                if (wupTN != null) {
                    alreadyInPlace = true;
                }
            }
            if(!alreadyInPlace) {
                exchange.setProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, getTopologyNode());
            }
        }
    }

    public WorkUnitProcessorSoftwareComponent getTopologyNode() {
        return topologyNode;
    }

    /**
     * @param uri
     * @return the RouteBuilder.from(uri) with all exceptions logged but not handled
     */
    protected RouteDefinition fromIncludingPetasosServices(String uri) {
        NodeDetailInjector nodeDetailInjector = new NodeDetailInjector();
        RouteDefinition route = fromWithStandardExceptionHandling(uri);
        route
                .process(nodeDetailInjector)
        ;
        return route;
    }

    //
    // Topology Functions
    //

    private WorkUnitProcessorSoftwareComponent buildWUPNodeElement(){
        getLogger().debug(".buildWUPNodeElement(): Entry");
        String participantName = getWorkshop().getWorkshopNode().getParticipant() + "." + getWUPInstanceName();
        WorkUnitProcessorSoftwareComponent wupNode = getTopologyFactory().buildWUP(
                getWUPInstanceName(),
                specifyWUPInstanceVersion(),
                participantName,
                getWorkshop().getWorkshopNode(),
                SoftwareComponentTypeEnum.WUP);
        getTopologyIM().addTopologyNode(getWorkshop().getWorkshopNode().getComponentId(), wupNode);
        wupNode.setResilienceMode(getWorkshop().getWorkshopNode().getResilienceMode());
        wupNode.setConcurrencyMode(getWorkshop().getWorkshopNode().getConcurrencyMode());
        return(wupNode);
    }

    /**
     *
     * @param interfaceDefinition
     * @return
     */
    protected IPCServerTopologyEndpoint deriveAssociatedTopologyEndpoint(String interfaceName, IPCAdapterDefinition interfaceDefinition){
        getLogger().debug(".deriveServerTopologyEndpoint(): Entry, interfaceName->{}, interfaceDefinition->{}", interfaceName, interfaceDefinition);
        ProcessingPlantSoftwareComponent processingPlantSoftwareComponent = processingPlantServices.getTopologyNode();
        getLogger().trace(".deriveServerTopologyEndpoint(): Parse through all endpoints and their IPC Definitions");
        for(ComponentIdType endpointId: processingPlantSoftwareComponent.getEndpoints()){
            IPCServerTopologyEndpoint endpoint = (IPCServerTopologyEndpoint)topologyIM.getNode(endpointId);
            getLogger().trace(".deriveServerTopologyEndpoint(): endpoint->{}", endpoint);
            if(endpoint.getEndpointConfigurationName().equalsIgnoreCase(interfaceName)) {
                getLogger().trace(".deriveServerTopologyEndpoint(): names ({}) match, now confirming supported InterfaceDefinition", interfaceName);
                for (IPCAdapter currentInterface : endpoint.getAdapterList()) {
                    getLogger().trace(".deriveServerTopologyEndpoint(): currentInterface->{}", currentInterface);
                    for (IPCAdapterDefinition currentInterfaceDef : currentInterface.getSupportedInterfaceDefinitions()) {
                        getLogger().trace(".deriveServerTopologyEndpoint(): currentInterfaceDef->{}", currentInterfaceDef);
                        if (currentInterfaceDef.equals(interfaceDefinition)) {
                            getLogger().debug(".deriveServerTopologyEndpoint(): Exit, match found, currentInterfaceDef->{}, endpoint->{}", currentInterfaceDef, endpoint);
                            return (endpoint);
                        }
                    }
                }
            }
        }
        getLogger().debug(".deriveServerTopologyEndpoint(): Exit, nothing found!");
        return(null);
    }

    protected IPCTopologyEndpoint getTopologyEndpoint(String topologyEndpointName){
        getLogger().debug(".getTopologyEndpoint(): Entry, topologyEndpointName->{}", topologyEndpointName);
        ArrayList<ComponentIdType> endpointIds = getProcessingPlant().getTopologyNode().getEndpoints();
        for(ComponentIdType currentEndpointId: endpointIds){
            IPCTopologyEndpoint endpointTopologyNode = (IPCTopologyEndpoint)getTopologyIM().getNode(currentEndpointId);
            if(endpointTopologyNode.getEndpointConfigurationName().contentEquals(topologyEndpointName)){
                getLogger().debug(".getTopologyEndpoint(): Exit, node found -->{}", endpointTopologyNode);
                return(endpointTopologyNode);
            }
        }
        getLogger().debug(".getTopologyEndpoint(): Exit, Could not find node!");
        return(null);
    }

    //
    // PetasosParticipant Functions
    //

    private PetasosParticipant registerParticipant(){
        getLogger().debug(".registerParticipant(): Entry");
        PetasosParticipant participant = getTopologyNode().getParticipant();
        Set<TaskWorkItemManifestType> subscribedTopicSet = new HashSet<>();
        if (!specifySubscriptionTopics().isEmpty()) {
            for (DataParcelManifest currentTopicID : specifySubscriptionTopics()) {
                TaskWorkItemSubscriptionType taskWorkItem = new TaskWorkItemSubscriptionType(currentTopicID);
                if (participant.getSubscriptions().contains(taskWorkItem)) {
                    // Do nothing
                } else {
                    participant.getSubscriptions().add(taskWorkItem);
                }
            }
        }
        if (!declarePublishedTopics().isEmpty()) {
            for (DataParcelManifest currentTopicID : declarePublishedTopics()) {
                TaskWorkItemManifestType taskWorkItem = new TaskWorkItemManifestType(currentTopicID);
                if (participant.getOutputs().contains(taskWorkItem)) {
                    // Do nothing
                } else {
                    participant.getOutputs().add(taskWorkItem);
                }
            }
        }
        PetasosParticipantRegistrationStatus participantRegistration = participantManager.registerParticipant(participant);
        getLogger().debug(".registerParticipant(): Exit, participantRegistration->{}", participantRegistration);
        return(participant);
    }
}
