/*
 * Copyright (c) 2020 Mark A. Hunter
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
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantRoleSupportInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentStatusEnum;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.PegacornSystemComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemManifestType;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapterDefinition;
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
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.ProcessingPlantMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.ProcessingPlantMetricsAgentAccessor;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.reporting.tasks.PetasosTaskReportAgentFactory;
import net.fhirfactory.pegacorn.petasos.oam.reporting.tasks.agents.WorkUnitProcessorTaskReportAgent;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generic Message Orientated Architecture (MOA) Work Unit Processor (WUP) Template
 * 
 * @author Mark A. Hunter
 * @since 2020-07-01
 */

public abstract class  GenericMessageBasedWUPTemplate extends BaseRouteBuilder {

    public static final Integer IPC_PACKET_MAXIMUM_FRAME_SIZE = 25 * 1024 * 1024; // 25 MB

    private boolean initialised;

    private WorkUnitProcessorSoftwareComponent meAsATopologyComponent;
    private PetasosParticipant meAsAPetasosParticipant;
    private RouteElementNames nameSet;
    private WUPArchetypeEnum wupArchetype;
    private List<DataParcelManifest> topicSubscriptionSet;
    private MessageBasedWUPEndpointContainer egressEndpoint;
    private MessageBasedWUPEndpointContainer ingresEndpoint;
    private WorkUnitProcessorMetricsAgent metricsAgent;
    private WorkUnitProcessorTaskReportAgent taskReportAgent;

    @Inject
    private WorkUnitProcessorFrameworkManager wupFrameworkManager;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private FHIRElementTopicFactory fhirTopicIDBuilder;

    @Inject
    private ProcessingPlantInterface processingPlantServices;

    @Inject
    private CamelContext camelContext;

    @Inject
    private FHIRContextUtility fhirContextUtility;

    @Inject
    private LocalPetasosParticipantCacheIM participantCacheIM;

    @Inject
    private PetasosMetricAgentFactory metricAgentFactory;

    @Inject
    private ProcessingPlantMetricsAgentAccessor processingPlantMetricsAgentAccessor;

    @Inject
    private PetasosTaskReportAgentFactory taskReportAgentFactory;

    @Inject
    private ProcessingPlantRoleSupportInterface processingPlantCapabilityStatement;

    //
    // Constructor(s)
    //

    public GenericMessageBasedWUPTemplate() {
        super();
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
        if(!isInitialised()) {
            getLogger().info(".initialise(): Initialising...");

            getLogger().info(".initialise(): Metadata: WUP Instance Name --> {}", specifyWUPInstanceName());
            getLogger().info(".initialise(): Metadata: WUP Instance Version --> {}", specifyWUPInstanceVersion());

            getLogger().info(".initialise(): [Initialise Containing Processing Plant (if required)] Start");
            this.getProcessingPlant().initialisePlant();
            getLogger().info(".initialise(): [Initialise Containing Processing Plant (if required)] Finish");

            getLogger().info(".initialise(): [Initialise My WUP Topology Node] Start");
            this.meAsATopologyComponent = buildWUPNodeElement();
            getLogger().info(".initialise(): [Initialise My WUP Topology Node] Finish");

            getLogger().info(".initialise(): [Initialise Route NameSet for this WUP] Start");
            this.nameSet = new RouteElementNames(getMeAsATopologyComponent().getNodeFunctionFDN().getFunctionToken());
            getLogger().info(".initialise(): [Initialise Route NameSet for this WUP] Finish");

            getLogger().info(".initialise(): [Setting the WUP EgressEndpoint] Start");
            this.egressEndpoint = specifyEgressEndpoint();
            this.getMeAsATopologyComponent().setEgressEndpoint(this.egressEndpoint.getEndpointTopologyNode());
            getLogger().info(".initialise(): [Setting the WUP EgressEndpoint] Finish");

            getLogger().info(".initialise(): [Setting the WUP IngresEndpoint] Start");
            this.ingresEndpoint = specifyIngresEndpoint();
            this.getMeAsATopologyComponent().setIngresEndpoint(this.ingresEndpoint.getEndpointTopologyNode());
            getLogger().info(".initialise(): [Setting the WUP IngresEndpoint] Finish");

            getLogger().info(".initialise(): [Setting the WUP Archetype] Start");
            this.wupArchetype = specifyWUPArchetype();
            getLogger().info(".initialise(): [Setting the WUP Archetype] Finish");

            getLogger().info(".initialise(): [Invoking subclass initialising function(s)] Start");
            executePostInitialisationActivities();
            getLogger().info(".initialise(): [Invoking subclass initialising function(s)] Finish");

            getLogger().info(".initialise(): [Building my PetasosParticipant] Start");
            this.meAsAPetasosParticipant = buildPetasosParticipant();
            getLogger().info(".initialise(): [Building my PetasosParticipant] Finish");

            getLogger().info(".initialise(): [Setting the Topic Subscription Set] Start");
            this.topicSubscriptionSet = specifySubscriptionTopics();
            getLogger().info(".initialise(): [Setting the Topic Subscription Set] Finish");

            getLogger().info(".initialise(): [Establish the WorkUnitProcessor Metrics Agent] Start");
            ComponentIdType componentId = getMeAsATopologyComponent().getComponentID();
            String participantName = getMeAsAPetasosParticipant().getParticipantName();
            this.metricsAgent = metricAgentFactory.newWorkUnitProcessingMetricsAgent(processingPlantCapabilityStatement, componentId, participantName);
            getLogger().info(".initialise(): [Establish the WorkUnitProcessor Metrics Agent] Finish");

            getLogger().info(".initialise(): [Establish the WorkUnitProcessor Metrics Agent] Start");
            this.taskReportAgent = taskReportAgentFactory.newWorkUnitProcessorTaskReportingAgent(processingPlantCapabilityStatement, componentId, participantName);
            getLogger().info(".initialise(): [Establish the WorkUnitProcessor Metrics Agent] Start");

            getLogger().info(".initialise(): [Establish (if any) Endpoint Metric Agents] Start");
            establishEndpointMetricAgents();
            getLogger().info(".initialise(): [Establish (if any) Endpoint Metric Agents] Finish");

            getLogger().info(".initialise(): [Build Surrounding WUP Framework] Start");
            buildWUPFramework(this.getContext());
            getLogger().info(".initialise(): [Build Surrounding WUP Framework] Finish");

            getLogger().info(".initialise(): [Register any Capabilities this Work Unit Processor supports] Start");
            registerCapabilities();
            getLogger().info(".initialise(): [Register any Capabilities this Work Unit Processor supports] Finish");

            getLogger().info(".initialise(): [Set my component status!] Start");
            this.getMeAsATopologyComponent().setComponentStatus(SoftwareComponentStatusEnum.SOFTWARE_COMPONENT_OPERATIONAL);
            getLogger().info(".initialise(): [Set my component status!] Finish");

            this.initialised = true;

            getLogger().info(".initialise(): Initialising... Done...");
        } else {
            getLogger().debug(".initialise(): Already initialised");
        }
        getLogger().debug(".initialise(): Exit");
    }

    //
    // To be implemented methods (in Specialisations)
    //

    protected abstract List<DataParcelManifest> specifySubscriptionTopics();
    protected abstract List<DataParcelManifest> declarePublishedTopics();
    protected abstract WUPArchetypeEnum specifyWUPArchetype();
    protected abstract String specifyWUPInstanceName();
    protected abstract String specifyWUPInstanceVersion();

    protected void establishEndpointMetricAgents(){
        // no nothing
    }

    protected void registerCapabilities(){
        // do nothing
    }

    protected abstract WorkshopInterface specifyWorkshop();
    protected abstract MessageBasedWUPEndpointContainer specifyIngresEndpoint();
    protected abstract MessageBasedWUPEndpointContainer specifyEgressEndpoint();

    abstract protected Logger specifyLogger();

    //
    // Getters (and Setters)
    //

    private boolean isInitialised(){
        return(this.initialised);
    }

    protected WorkshopInterface getWorkshop(){
        return(specifyWorkshop());
    }

    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlantServices);
    }

    protected PegacornTopologyFactoryInterface getTopologyFactory(){
        return(processingPlantServices.getTopologyFactory());
    }

    protected Logger getLogger(){
        return(specifyLogger());
    }

    public MessageBasedWUPEndpointContainer getEgressEndpoint() {
        return egressEndpoint;
    }

    public MessageBasedWUPEndpointContainer getIngresEndpoint() {
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

    protected PetasosMetricAgentFactory getMetricAgentFactory(){
        return(this.metricAgentFactory);
    }

    protected String getEndpointHostName(){
        String dnsName = getProcessingPlant().getMeAsASoftwareComponent().getAssignedDNSName();
        return(dnsName);
    }
    
    protected TopologyIM getTopologyIM(){
        return(topologyIM);
    }

    protected void setMeAsATopologyComponent(WorkUnitProcessorSoftwareComponent meAsATopologyComponent) {
        this.meAsATopologyComponent = meAsATopologyComponent;
    }

    protected RouteElementNames getNameSet() {
        return nameSet;
    }

    protected String getWupInstanceName() {
        return getMeAsATopologyComponent().getComponentRDN().getTag();
    }

    protected WUPArchetypeEnum getWupArchetype() {
        return wupArchetype;
    }

    protected List<DataParcelManifest> getTopicSubscriptionSet() {
        return topicSubscriptionSet;
    }

    protected void setTopicSubscriptionSet(List<DataParcelManifest> topicSubscriptionSet) {
        this.topicSubscriptionSet = topicSubscriptionSet;
    }

    protected String getVersion() {
        return meAsATopologyComponent.getComponentRDN().getNodeVersion();
    }

    protected FHIRElementTopicFactory getFHIRTopicIDBuilder(){
        return(this.fhirTopicIDBuilder);
    }

    protected CamelContext getCamelContext() {
        return camelContext;
    }

    protected FHIRContextUtility getFHIRContextUtility(){
        return(this.fhirContextUtility);
    }

    protected PetasosParticipant getMeAsAPetasosParticipant() {
        return meAsAPetasosParticipant;
    }

    protected WorkUnitProcessorMetricsAgent getMetricsAgent(){
        return(this.metricsAgent);
    }

    protected ProcessingPlantMetricsAgent getProcessingPlantMetricsAgent(){
        return(this.processingPlantMetricsAgentAccessor.getMetricsAgent());
    }

    protected WorkUnitProcessorTaskReportAgent getTaskReportAgent(){
        return(this.taskReportAgent);
    }

    protected String specifyParticipantDisplayName(){
        return(specifyWUPInstanceName());
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
                exchange.setProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, getMeAsATopologyComponent());
            }
        }
    }

    public class AuditAgentInjector implements Processor{
        @Override
        public void process(Exchange camelExchange) throws Exception{
            getLogger().debug("AuditAgentInjector.process(): Entry");
            camelExchange.setProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, getMetricsAgent());
        }
    }

    public class TaskReportAgentInjector implements Processor{
        @Override
        public void process(Exchange camelExchange) throws Exception{
            getLogger().debug("TaskReportAgentInjector.process(): Entry");
            camelExchange.setProperty(PetasosPropertyConstants.ENDPOINT_TASK_REPORT_AGENT_EXCHANGE_PROPERTY, getTaskReportAgent());
        }
    }

    public WorkUnitProcessorSoftwareComponent getMeAsATopologyComponent() {
        return meAsATopologyComponent;
    }

    /**
     * @param uri
     * @return the RouteBuilder.from(uri) with all exceptions logged but not handled
     */
    protected RouteDefinition fromIncludingPetasosServices(String uri) {
        NodeDetailInjector nodeDetailInjector = new NodeDetailInjector();
        AuditAgentInjector auditAgentInjector = new AuditAgentInjector();
        TaskReportAgentInjector taskReportAgentInjector = new TaskReportAgentInjector();
        RouteDefinition route = fromWithStandardExceptionHandling(uri);
        route
                .process(nodeDetailInjector)
                .process(auditAgentInjector)
                .process(taskReportAgentInjector)
        ;
        return route;
    }

    //
    // PetasosParticipant Functions
    //

    public void buildWUPFramework(CamelContext routeContext) {
        getLogger().debug(".buildWUPFramework(): Entry");
        wupFrameworkManager.buildWUPFramework(this.meAsATopologyComponent, this.getTopicSubscriptionSet(), this.getWupArchetype(), getMetricsAgent());
        getLogger().debug(".buildWUPFramework(): Exit");
    }

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
        String participantName;
        if(StringUtils.isEmpty(getMeAsATopologyComponent().getParticipantName())){
            if(StringUtils.isEmpty(getMeAsATopologyComponent().getComponentID().getDisplayName())){
                participantName = specifyWUPInstanceName();
            } else {
                participantName = getMeAsATopologyComponent().getComponentID().getDisplayName();
            }
        } else {
            participantName = getMeAsATopologyComponent().getParticipantName();
        }
        PetasosParticipantRegistration participantRegistration = participantCacheIM.registerPetasosParticipant(participantName, getMeAsATopologyComponent(),  publishedTopicSet, subscribedTopicSet);
        PetasosParticipant participant = null;
        if(participantRegistration != null){
            participant = participantRegistration.getParticipant();
        }
        return(participant);
    }


    //
    // Topology Functions
    //

    private WorkUnitProcessorSoftwareComponent buildWUPNodeElement(){
        getLogger().debug(".buildWUPNodeElement(): Entry");
        String participantName = getWorkshop().getWorkshopNode().getParticipantName() + "." + specifyWUPInstanceName();
        WorkUnitProcessorSoftwareComponent wupNode = getTopologyFactory().createWorkUnitProcessor(
                specifyWUPInstanceName(),
                specifyWUPInstanceVersion(),
                participantName,
                getWorkshop().getWorkshopNode(),
                PegacornSystemComponentTypeTypeEnum.WUP);
        if(StringUtils.isNotEmpty(specifyParticipantDisplayName())){
            wupNode.setParticipantDisplayName(specifyParticipantDisplayName());
        }
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
    protected IPCTopologyEndpoint deriveAssociatedTopologyEndpoint(String interfaceName, IPCAdapterDefinition interfaceDefinition){
        getLogger().debug(".deriveServerTopologyEndpoint(): Entry, interfaceName->{}, interfaceDefinition->{}", interfaceName, interfaceDefinition);
        ProcessingPlantSoftwareComponent processingPlantSoftwareComponent = processingPlantServices.getMeAsASoftwareComponent();
        getLogger().trace(".deriveServerTopologyEndpoint(): Parse through all endpoints and their IPC Definitions");
        for(TopologyNodeFDN endpointFDN: processingPlantSoftwareComponent.getEndpoints()){
            IPCTopologyEndpoint endpoint = (IPCTopologyEndpoint)topologyIM.getNode(endpointFDN);
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
            getLogger().trace(".getTopologyEndpoint(): Iterating Through Endpoints, current->{}", endpointTopologyNode);
            if(endpointTopologyNode.getEndpointConfigurationName() != null) {
                if (endpointTopologyNode.getEndpointConfigurationName().contentEquals(topologyEndpointName)) {
                    getLogger().debug(".getTopologyEndpoint(): Exit, node found->{}", endpointTopologyNode);
                    return (endpointTopologyNode);
                }
            }
        }
        getLogger().debug(".getTopologyEndpoint(): Exit, Could not find node!");
        return(null);
    }
}
