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
import net.fhirfactory.pegacorn.core.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.PetasosEndpointContainerInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.PegacornSystemComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemManifestType;
import net.fhirfactory.pegacorn.core.model.petasos.wup.PetasosTaskJobCard;
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
import net.fhirfactory.pegacorn.petasos.core.participants.manager.LocalPetasosParticipantCacheIM;
import net.fhirfactory.pegacorn.petasos.oam.metrics.PetasosMetricAgentFactory;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
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

    private WorkUnitProcessorSoftwareComponent meAsASoftwareComponent;
    private PetasosParticipant meAsAPetasosParticipant;
    private PetasosTaskJobCard wupJobCard;
    private RouteElementNames nameSet;
    private WUPArchetypeEnum wupArchetype;
    private List<DataParcelManifest> topicSubscriptionSet;
    private PetasosEndpointContainerInterface egressEndpoint;
    private PetasosEndpointContainerInterface ingresEndpoint;
    private String wupInstanceName;
    private WorkUnitProcessorMetricsAgent metricsAgent;

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
    private LocalPetasosParticipantCacheIM participantCacheIM;

    public GenericTriggerBasedWUPTemplate() {
        super();
        this.wupInstanceName = getClass().getSimpleName();
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
        getLogger().debug(".initialise(): Entry, Default Post Constructor function to setup the WUP");
        getLogger().trace(".initialise(): WUP Instance Name --> {}", getWUPInstanceName());
        getLogger().trace(".initialise(): WUP Instance Version --> {}", specifyWUPInstanceVersion());
        this.getProcessingPlant().initialisePlant();
        getLogger().trace(".initialise(): Setting up the wupTopologyElement (NodeElement) instance, which is the Topology Server's representation of this WUP ");
        this.meAsASoftwareComponent = buildWUPNodeElement();
        getLogger().trace(".initialise(): Setting the WUP nameSet, which is the set of Route EndPoints that the WUP Framework will use to link various enablers");
        this.nameSet = new RouteElementNames(getMeAsASoftwareComponent().getNodeFunctionFDN().getFunctionToken());
        getLogger().trace(".initialise(): Setting the WUP EgressEndpoint");
        this.egressEndpoint = specifyEgressEndpoint();
        getLogger().trace(".initialise(): Setting the WUP IngresEndpoint");
        this.ingresEndpoint = specifyIngresEndpoint();
        getLogger().trace(".initialise(): Setting the WUP Archetype - which is used by the WUP Framework to ascertain what wrapping this WUP needs");
        this.wupArchetype =  specifyWUPArchetype();
        getLogger().trace(".initialise(): Now invoking subclass initialising function(s)");
        executePostInitialisationActivities();
        getLogger().trace(".initialise(): Setting the Topic Subscription Set (i.e. the list of Data Sets we will process)");
        this.topicSubscriptionSet = specifySubscriptionTopics();
        getLogger().trace(".initialise(): Building my PetasosParticipant");
        this.meAsAPetasosParticipant = buildPetasosParticipant();
        getLogger().trace(".initialise(): Establish the metrics agent");
        ComponentIdType componentId = getMeAsASoftwareComponent().getComponentID();
        String participantName = getMeAsAPetasosParticipant().getParticipantName();
        this.metricsAgent = metricAgentFactory.newWorkUnitProcessingMetricsAgent(componentId, participantName);
        getLogger().trace(".initialise(): Now call the WUP Framework constructure - which builds the Petasos framework around this WUP");
        buildWUPFramework(this.getContext());
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
        frameworkManager.buildWUPFramework(this.meAsASoftwareComponent, this.getTopicSubscriptionSet(), this.getWupArchetype(), getMetricsAgent());
        getLogger().debug(".buildWUPFramework(): Exit");
    }
    
    public String getEndpointHostName(){
        String dnsName = getProcessingPlant().getMeAsASoftwareComponent().getAssignedDNSName();
        return(dnsName);
    }
    
    public WorkUnitProcessorFrameworkManager getFrameworkManager(){
        return(this.frameworkManager);
    }
    
    public TopologyIM getTopologyIM(){
        return(topologyIM);
    }

    public void setMeAsASoftwareComponent(WorkUnitProcessorSoftwareComponent meAsASoftwareComponent) {
        this.meAsASoftwareComponent = meAsASoftwareComponent;
    }

    public RouteElementNames getNameSet() {
        return nameSet;
    }

    public String getWupInstanceName() {
        return getMeAsASoftwareComponent().getComponentRDN().getTag();
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
        return meAsASoftwareComponent.getComponentRDN().getNodeVersion();
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

    public PetasosParticipant getMeAsAPetasosParticipant() {
        return meAsAPetasosParticipant;
    }

    protected WorkUnitProcessorMetricsAgent getMetricsAgent(){
        return(this.metricsAgent);
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
                exchange.setProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, getMeAsASoftwareComponent());
            }
        }
    }

    public WorkUnitProcessorSoftwareComponent getMeAsASoftwareComponent() {
        return meAsASoftwareComponent;
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
        String participantName = getWorkshop().getWorkshopNode().getParticipantName() + "." + getWUPInstanceName();
        WorkUnitProcessorSoftwareComponent wupNode = getTopologyFactory().createWorkUnitProcessor(
                getWUPInstanceName(),
                specifyWUPInstanceVersion(),
                participantName,
                getWorkshop().getWorkshopNode(),
                PegacornSystemComponentTypeTypeEnum.WUP);
        getTopologyIM().addTopologyNode(getWorkshop().getWorkshopNode().getComponentFDN(), wupNode);
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
        ProcessingPlantSoftwareComponent processingPlantSoftwareComponent = processingPlantServices.getMeAsASoftwareComponent();
        getLogger().trace(".deriveServerTopologyEndpoint(): Parse through all endpoints and their IPC Definitions");
        for(TopologyNodeFDN endpointFDN: processingPlantSoftwareComponent.getEndpoints()){
            IPCServerTopologyEndpoint endpoint = (IPCServerTopologyEndpoint)topologyIM.getNode(endpointFDN);
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
        ArrayList<TopologyNodeFDN> endpointFDNs = getProcessingPlant().getMeAsASoftwareComponent().getEndpoints();
        for(TopologyNodeFDN currentEndpointFDN: endpointFDNs){
            IPCTopologyEndpoint endpointTopologyNode = (IPCTopologyEndpoint)getTopologyIM().getNode(currentEndpointFDN);
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

    private PetasosParticipant buildPetasosParticipant(){
        getLogger().debug(".buildPetasosParticipant(): Entry");
        Set<TaskWorkItemManifestType> subscribedTopicSet = new HashSet<>();
        if (!specifySubscriptionTopics().isEmpty()) {
            for (DataParcelManifest currentTopicID : specifySubscriptionTopics()) {
                TaskWorkItemManifestType taskWorkItem = new TaskWorkItemManifestType(currentTopicID);
                if (subscribedTopicSet.contains(taskWorkItem)) {
                    // Do nothing
                } else {
                    subscribedTopicSet.add(taskWorkItem);
                }
            }
        }

        Set<TaskWorkItemManifestType> publishedTopicSet = new HashSet<>();
        if (!declarePublishedTopics().isEmpty()) {
            for (DataParcelManifest currentTopicID : declarePublishedTopics()) {
                TaskWorkItemManifestType taskWorkItem = new TaskWorkItemManifestType(currentTopicID);
                if (publishedTopicSet.contains(taskWorkItem)) {
                    // Do nothing
                } else {
                    publishedTopicSet.add(taskWorkItem);
                }
            }
        }
        String participantName = getMeAsASoftwareComponent().getParticipantName();
        PetasosParticipantRegistration participantRegistration = participantCacheIM.registerPetasosParticipant(participantName, getMeAsASoftwareComponent(),  publishedTopicSet, subscribedTopicSet);
        PetasosParticipant participant = null;
        if(participantRegistration != null){
            participant = participantRegistration.getParticipant();
        }
        return(participant);
    }
}
