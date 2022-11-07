/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.processingplant;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.constants.systemwide.DRICaTSReferenceProperties;
import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventGranularityLevelInterface;
import net.fhirfactory.pegacorn.core.interfaces.capabilities.CapabilityFulfillmentInterface;
import net.fhirfactory.pegacorn.core.interfaces.participant.TaskPathwayManagementServiceInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantRoleSupportInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.use.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.use.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.SoftwareComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelDirectionEnum;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.audit.valuesets.PetasosAuditEventGranularityLevelEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.JGroupsIntegrationPointNamingUtilities;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.ProcessingPlantPetasosParticipantHolder;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.ProcessingPlantSoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkshopSoftwareComponent;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.ClusterServiceDeliverySubsystemPropertyFile;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.datatypes.ParameterNameValuePairType;
import net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.interfaces.SolutionNodeFactoryInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.FHIRElementTopicFactory;
import net.fhirfactory.pegacorn.petasos.core.participants.management.LocalParticipantManager;
import net.fhirfactory.pegacorn.petasos.oam.metrics.PetasosMetricAgentFactory;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.ProcessingPlantMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.ProcessingPlantMetricsAgentAccessor;
import net.fhirfactory.pegacorn.util.PegacornEnvironmentProperties;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ProcessingPlant extends RouteBuilder implements ProcessingPlantRoleSupportInterface, ProcessingPlantInterface, PetasosAuditEventGranularityLevelInterface, CapabilityFulfillmentInterface {

    private ProcessingPlantSoftwareComponent meAsASoftwareComponent;
    private String instanceQualifier;
    private boolean isInitialised;
    private ProcessingPlantMetricsAgent metricsAgent;

    private PetasosAuditEventGranularityLevelEnum processingPlantAuditLevel;

    ConcurrentHashMap<String, CapabilityFulfillmentInterface> capabilityDeliveryServices;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private PegacornEnvironmentProperties environmentProperties;

    @Inject
    private JGroupsIntegrationPointNamingUtilities componentNameUtilities;

    @Inject
    private ProcessingPlantPetasosParticipantHolder participantHolder;

    @Inject
    private FHIRElementTopicFactory fhirElementTopicFactory;

    @Inject
    private TaskPathwayManagementServiceInterface taskPathwayManagementService;

    @Inject
    private LocalParticipantManager participantRegistrationAdministrator;

    @Inject
    private DRICaTSReferenceProperties pegacornReferenceProperties;

    @Inject
    private ProcessingPlantMetricsAgentAccessor metricsAgentAccessor;

    @Inject
    private PetasosMetricAgentFactory metricAgentFactory;

    //
    // Constructor(s)
    //

    public ProcessingPlant() {
        super();
        this.capabilityDeliveryServices = new ConcurrentHashMap<>();
        this.isInitialised = false;
        this.instanceQualifier = UUID.randomUUID().toString();
        this.processingPlantAuditLevel = PetasosAuditEventGranularityLevelEnum.AUDIT_LEVEL_COARSE;
    }

    //
    // Abstract Methods
    //

    abstract protected ClusterServiceDeliverySubsystemPropertyFile specifyPropertyFile();
    abstract protected Logger specifyLogger();
    abstract protected PegacornTopologyFactoryInterface specifyTopologyFactory();
    abstract protected SolutionNodeFactoryInterface specifySolutionNodeFactory();
    abstract protected void executePostConstructActivities();

    //
    // Getters (and Setters)
    //

    protected DRICaTSReferenceProperties getPegacornReferenceProperties(){
        return(pegacornReferenceProperties);
    }

    protected Logger getLogger(){
        return(specifyLogger());
    }

    protected ClusterServiceDeliverySubsystemPropertyFile getPropertyFile(){
        return(specifyPropertyFile());
    }

    protected TaskPathwayManagementServiceInterface getTaskPathwayManagementService(){
        return(taskPathwayManagementService);
    }

    protected LocalParticipantManager getParticipantRegistrationAdministrator(){
        return(participantRegistrationAdministrator);
    }

    protected FHIRElementTopicFactory getFHIRElementTopicFactory(){
        return(fhirElementTopicFactory);
    }

    public ProcessingPlantPetasosParticipantHolder getParticipantHolder() {
        return (participantHolder);
    }

    @Override
    public PegacornTopologyFactoryInterface getTopologyFactory(){
        return(specifyTopologyFactory());
    }

    @Override
    public String getHostName() {
        return (getTopologyNode().getAssignedDNSName());
    }

    public void setHostName(String hostName) {
        getTopologyNode().setAssignedDNSName(hostName);
    }

    public TopologyIM getTopologyIM() {
        return (topologyIM);
    }

    public PetasosAuditEventGranularityLevelEnum getProcessingPlantAuditLevel() {
        return processingPlantAuditLevel;
    }

    protected ProcessingPlantMetricsAgent getMetricsAgent(){
        return(metricsAgent);
    }

    //
    // Post Construct
    //

    @PostConstruct
    public void initialise() {
        getLogger().debug(".initalise(): Entry");
        if (!isInitialised) {
            getLogger().info("ProcessingPlant::initialise(): Initialising....");
            getLogger().info("ProcessingPlant::initialise(): [TopologyIM Initialisation] Start");
            getTopologyIM().initialise();
            getLogger().info("ProcessingPlant::initialise(): [TopologyIM Initialisation] Finish");
            getLogger().info("ProcessingPlant::initialise(): [Topology Factory Initialisation] Start");
            getTopologyFactory().initialise();
            getLogger().info("ProcessingPlant::initialise(): [Topology Factory Initialisation] Finish");
            getLogger().info("ProcessingPlant::initialise(): [Solution Node Factory Initialisation] Start");
            specifySolutionNodeFactory().initialise();
            getLogger().info("ProcessingPlant::initialise(): [Solution Node Factory Initialisation] Finish");
            getLogger().info("ProcessingPlant::initialise(): [ProcessingPlant Resolution] Start");
            resolveProcessingPlant();
            isInitialised = true;
            getLogger().info("ProcessingPlant::initialise(): [ProcessingPlant Resolution] softwareComponet->{}", meAsASoftwareComponent);
            getLogger().info("ProcessingPlant::initialise(): [ProcessingPlant Resolution] Finish");
            getLogger().info("ProcessingPlant::initialise(): [Capability Delivery Services Map Initialisation] Start");
            getLogger().info("ProcessingPlant::initialise(): [POD Name Resolution and Assignment] Start");
            String myPodName = environmentProperties.getMandatoryProperty("MY_POD_NAME");
            setHostName(myPodName);
            getLogger().info("ProcessingPlant::initialise(): [POD Name Resolution and Assignment] Finish");
            getLogger().info("ProcessingPlant::initialise(): [Executing other PostConstruct Activities] Start");
            executePostConstructActivities();
            getLogger().info("ProcessingPlant::initialise(): [Executing other PostConstruct Activities] Finish");
            getLogger().info("ProcessingPlant::initialise(): [Register My PetasosPartcipant] Start");
            registerParticipant();
            getLogger().info("ProcessingPlant::initialise(): [Register My PetasosPartcipant] Finish");
            getLogger().info("ProcessingPlant::initialise(): [Audit Event Level Derivation] Start");
            this.processingPlantAuditLevel = deriveAuditEventGranularityLevel();
            getLogger().info("ProcessingPlant::initialise(): [Audit Event Level Derivation] Finish");
            getLogger().info("ProcessingPlant::initialise(): [Initialise Metrics Agent] Start");
            this.metricsAgent = metricAgentFactory.newProcessingPlantMetricsAgent(this, getTopologyNode().getComponentId(), getTopologyNode().getParticipant().getParticipantId().getSubsystemName());
            metricsAgentAccessor.setMetricsAgent(this.metricsAgent);
            getLogger().info("ProcessingPlant::initialise(): [Initialise Metrics Agent] Finish");

            getLogger().info("StandardProcessingPlatform::initialise(): Done...");
        } else {
            getLogger().debug(".initialise(): Already initialised, nothing to do!");
        }
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Overridden Business Methods
    //

    @Override
    public void initialisePlant() {
       //
    }

    //
    // Topology Detail
    //

    private void resolveProcessingPlant() {
        getLogger().debug(".resolveProcessingPlant(): Entry");
        String processingPlantName = getPropertyFile().getSubsystemInstant().getParticipantName();
        String processingPlantVersion = getPropertyFile().getSubsystemInstant().getProcessingPlantVersion();
        getLogger().debug(".resolveProcessingPlant(): Getting ProcessingPlant->{}, version->{}", processingPlantName, processingPlantVersion);
        getLogger().trace(".resolveProcessingPlant(): Resolving list of available ProcessingPlants");
        List<SoftwareComponent> topologyNodes = topologyIM.nodeSearch(SoftwareComponentTypeEnum.PROCESSING_PLANT, processingPlantName, processingPlantVersion);
        if(topologyNodes == null){
            throw new RuntimeException(".resolveProcessingPlant(): nodeSearch return a null list");
        }
        if(topologyNodes.isEmpty()){
            throw new RuntimeException(".resolveProcessingPlant(): nodeSearch return an empty list");
        }
        if(topologyNodes.size() > 1){
            throw new RuntimeException(".resolveProcessingPlant(): nodeSearch return a list containing more than 1 entry!");
        }
        this.meAsASoftwareComponent = (ProcessingPlantSoftwareComponent) topologyNodes.get(0);
        getLogger().debug(".resolveProcessingPlant(): Exit, Resolved ProcessingPlant, processingPlant->{}", meAsASoftwareComponent);
    }

    //
    // Dummy Route to ensure Startup
    //

    @Override
    public void configure() throws Exception {
        String processingPlantName = getFriendlyName();

        from("timer://"+processingPlantName+"?delay=1000&repeatCount=1")
            .routeId("ProcessingPlant::"+processingPlantName)
            .log(LoggingLevel.DEBUG, "Starting....");
    }

    private String getFriendlyName(){
        getLogger().debug(".getFriendlyName(): Entry");
        String nodeName = this.getTopologyNode().getComponentId().getDisplayName();
        return(nodeName);
    }

    @Override
    public ProcessingPlantSoftwareComponent getTopologyNode() {
        return (this.meAsASoftwareComponent);
    }

    @Override
    public WorkshopSoftwareComponent getWorkshop(String workshopName, String version) {
        getLogger().debug(".getWorkshop(): Entry, workshopName --> {}, version --> {}", workshopName, version);
        WorkshopSoftwareComponent foundWorkshop = null;
        for (ComponentIdType containedWorkshopId : this.meAsASoftwareComponent.getWorkshops()) {
            boolean sameName = false;
            boolean sameDisplayName = false;
            boolean sameVersion = true;
            if(containedWorkshopId.hasName()){
                sameName = containedWorkshopId.getName().contentEquals(workshopName);
            }
            if(containedWorkshopId.hasDisplayName()){
                sameDisplayName = containedWorkshopId.getDisplayName().contentEquals(workshopName);
            }
            if (sameName || sameDisplayName) {
                WorkshopSoftwareComponent containedWorkshop = (WorkshopSoftwareComponent)topologyIM.getNode(containedWorkshopId);
                if(StringUtils.isNotEmpty(version)) {
                    if(containedWorkshop.getVersion().contentEquals(version)) {
                        foundWorkshop = containedWorkshop;
                        break;
                    }
                }
                break;
            }
        }
        getLogger().debug(".getWorkshop(): Exit, workshop->{}", foundWorkshop);
        return (foundWorkshop);
    }

    @Override
	public WorkshopSoftwareComponent getWorkshop(String workshopName){
        getLogger().debug(".getWorkshop(): Entry, workshopName --> {}", workshopName);
        String version = this.meAsASoftwareComponent.getVersion();
        WorkshopSoftwareComponent workshop = getWorkshop(workshopName, version);
        getLogger().debug(".getWorkshop(): Exit");
        return(workshop);
    }

    @Override
    public NetworkSecurityZoneEnum getNetworkZone(){
        NetworkSecurityZoneEnum securityZone = this.getTopologyNode().getSecurityZone();
        return(securityZone);
    }

    @Override
    public String getDeploymentSite() {
        String siteName = getTopologyNode().getDeploymentSite();
        return (siteName);
    }


    @Override
    public void registerCapabilityFulfillmentService(String capabilityName, CapabilityFulfillmentInterface fulfillmentInterface) {
        getLogger().debug(".registerCapabilityFulfillmentService(): Entry, capabilityName->{}", capabilityName);
        if(fulfillmentInterface == null){
            getLogger().debug(".registerCapabilityFulfillmentService(): Exit, Capability Fulfillment Interface is NULL");
            return;
        }
        this.capabilityDeliveryServices.put(capabilityName, fulfillmentInterface);
        getLogger().debug(".registerCapabilityFulfillmentService(): Exit, Capability Fulillment Interface registered");
    }

    @Override
    public CapabilityUtilisationResponse executeTask(CapabilityUtilisationRequest request) {
        getLogger().debug(".executeTask(): Entry, request->{}", request);
        if(request == null){
            CapabilityUtilisationResponse response = new CapabilityUtilisationResponse();
            response.setInstantCompleted(Instant.now());
            response.setSuccessful(false);
            getLogger().debug(".executeTask(): Exit, request is null, response->{}", response);
            return(response);
        }
        String capabilityName = request.getRequiredCapabilityName();
        CapabilityFulfillmentInterface interfaceToUse = this.capabilityDeliveryServices.get(capabilityName);
        if(interfaceToUse == null){
            CapabilityUtilisationResponse response = new CapabilityUtilisationResponse();
            response.setInstantCompleted(Instant.now());
            response.setSuccessful(false);
            response.setInScope(false);
            getLogger().debug(".executeTask(): Exit, not registered capability, response->{}", response);
            return(response);
        }
        CapabilityUtilisationResponse capabilityUtilisationResponse = interfaceToUse.executeTask(request);
        getLogger().debug(".executeTask(): Exit, capabilityUtilisationResponse->{}", capabilityUtilisationResponse);
        return(capabilityUtilisationResponse);
    }

    @Override
    public boolean isITOpsNode() {
        return (false);
    }

    @Override
    public PetasosAuditEventGranularityLevelEnum getAuditEventGranularityLevel() {
        return(this.processingPlantAuditLevel);
    }

    //
    // Get ProcessingPlant Audit Level
    //

    protected PetasosAuditEventGranularityLevelEnum deriveAuditEventGranularityLevel(){
        List<ParameterNameValuePairType> otherDeploymentParameters = getPropertyFile().getDeploymentMode().getOtherDeploymentParameters();
        if(otherDeploymentParameters != null){
            for(ParameterNameValuePairType currentNameValuePair: otherDeploymentParameters){
                if(currentNameValuePair.getParameterName().equalsIgnoreCase(PetasosPropertyConstants.AUDIT_LEVEL_PARAMETER_NAME)){
                    String parameterValue = currentNameValuePair.getParameterValue();
                    PetasosAuditEventGranularityLevelEnum petasosAuditEventGranularityLevelEnum = PetasosAuditEventGranularityLevelEnum.fromDisplayName(parameterValue);
                    if(petasosAuditEventGranularityLevelEnum == null){
                        getLogger().warn(".deriveAuditEventGranularityLevel(): Unable to derive PetasosAuditEventGranularityLevelEnum, setting to AUDIT_LEVEL_COARSE");
                        return(PetasosAuditEventGranularityLevelEnum.AUDIT_LEVEL_COARSE);
                    } else {
                        return (petasosAuditEventGranularityLevelEnum);
                    }
                }
            }
        }
        getLogger().warn(".deriveAuditEventGranularityLevel(): Unable to derive PetasosAuditEventGranularityLevelEnum, setting to AUDIT_LEVEL_COARSE");
        return (PetasosAuditEventGranularityLevelEnum.AUDIT_LEVEL_COARSE);
    }

    //
    // Remote Subscription Functions
    //

    protected void subscribeToRemoteDataParcels(List<DataParcelTypeDescriptor> triggerEventList, String sourceSystem){
        getLogger().info(".subscribeToRemoteDataParcels(): Entry, sourceSystem->{}", sourceSystem);
        if(triggerEventList.isEmpty()){
            return;
        }
        getLogger().trace(".subscribeToRemoteDataParcels(): We have entries in the subscription list, processing");
        for(DataParcelTypeDescriptor currentTriggerEvent: triggerEventList){
            getLogger().info(".subscribeToRemoteDataParcels(): currentTriggerEvent->{}", currentTriggerEvent);
            DataParcelTypeDescriptor container = fhirElementTopicFactory.newTopicToken(ResourceType.Communication.name(), pegacornReferenceProperties.getPegacornDefaultFHIRVersion());
            getLogger().info(".subscribeToRemoteDataParcels(): container->{}", container);
            TaskWorkItemSubscriptionType manifest = new TaskWorkItemSubscriptionType();
            manifest.setContentDescriptor(currentTriggerEvent);
            manifest.setContainerDescriptor(container);
            manifest.setEnforcementPointApprovalStatus(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_POSITIVE);
            manifest.setDataParcelFlowDirection(DataParcelDirectionEnum.INFORMATION_FLOW_INBOUND_DATA_PARCEL);
            manifest.setInterSubsystemDistributable(true);
            manifest.setExternalSourceSystem(sourceSystem);
            manifest.setSourceProcessingPlantParticipantName(sourceSystem);
            getTopologyNode().getParticipant().getSubscriptions().add(manifest);
        }
        getLogger().info(".subscribeToRemoteDataParcels(): Exit");
    }

    protected PetasosParticipant registerParticipant(){
        getLogger().info(".registerParticipant(): Entry");
        PetasosParticipant participantRegistration = getParticipantRegistrationAdministrator().registerParticipant(getTopologyNode().getParticipant());
        getLogger().info(".registerParticipant(): Exit, partipantRegistration->{}", participantRegistration);
        return(participantRegistration);
    }
}
