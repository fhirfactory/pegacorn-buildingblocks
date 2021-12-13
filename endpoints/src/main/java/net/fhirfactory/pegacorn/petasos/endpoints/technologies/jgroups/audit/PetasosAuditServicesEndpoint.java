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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.audit;

import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventServiceBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventServiceHandlerInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.transaction.factories.PegacornTransactionMethodOutcomeFactory;
import net.fhirfactory.pegacorn.core.model.transaction.model.PegacornTransactionMethodOutcome;
import net.fhirfactory.pegacorn.core.model.transaction.valuesets.PegacornTransactionStatusEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.IdType;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PetasosAuditServicesEndpoint extends JGroupsPetasosEndpointBase implements PetasosAuditEventServiceBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosAuditServicesEndpoint.class);

    @Produce
    private ProducerTemplate camelProducer;

    @Inject
    private PetasosAuditEventServiceHandlerInterface auditEventServiceHandler;

    @Inject
    private PegacornTransactionMethodOutcomeFactory outcomeFactory;

    //
    // Constructor(s)
    //

    public PetasosAuditServicesEndpoint(){
        super();
    }

    //
    // PostConstruct Activities
    //

    @Override
    protected void executePostConstructActivities() {

    }

    //
    // Getters (and Setters)
    //

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    public ProducerTemplate getCamelProducer() {
        return camelProducer;
    }

    //
    // Endpoint Specification
    //

    @Override
    protected String specifyIPCInterfaceName() {
        return (getInterfaceNames().getPetasosAuditServicesEndpointName());
    }

    @Override
    protected PetasosEndpointTopologyTypeEnum specifyIPCType() {
        return (PetasosEndpointTopologyTypeEnum.EDGE_JGROUPS_MESSAGING_SERVICE);
    }

    @Override
    protected String specifyJGroupsStackFileName() {
        return (getProcessingPlantInterface().getProcessingPlantNode().getPetasosAuditStackConfigFile());
    }

    @Override
    protected PetasosEndpointIdentifier specifyEndpointID() {
        PetasosEndpointIdentifier endpointID = new PetasosEndpointIdentifier();
        // Get Core Values
        String endpointServiceName = specifyEndpointServiceName();
        String endpointFunctionName = specifyPetasosEndpointFunctionType().getDisplayName();
        String endpointUUID = getEndpointNameUtilities().getCurrentUUID();
        String endpointSite = getProcessingPlantInterface().getDeploymentSite();
        String endpointZone = getProcessingPlantInterface().getNetworkZone().getDisplayName();
        // Build EndpointName
        String endpointName = getEndpointNameUtilities().buildEndpointName(endpointServiceName, endpointUUID);
        // Build EndpointChannelName
        String endpointChannelName = getEndpointNameUtilities().buildChannelName(endpointSite, endpointZone, endpointServiceName, endpointFunctionName, endpointUUID);
        // Build EndpointID
        endpointID.setEndpointChannelName(endpointChannelName);
        endpointID.setEndpointName(endpointName);
        endpointID.setEndpointZone(getProcessingPlantInterface().getNetworkZone());
        endpointID.setEndpointSite(getProcessingPlantInterface().getDeploymentSite());
        endpointID.setEndpointGroup(getJgroupsParticipantInformationService().getPetasosAuditGroupName());
        endpointID.setEndpointComponentID(getTopologyNode().getComponentID());
        endpointID.setProcessingPlantComponentID(getProcessingPlantInterface().getProcessingPlantNode().getComponentID());
        String endpointAddress = "JGroups:" + endpointChannelName + ":" + getJgroupsParticipantInformationService().getPetasosAuditGroupName();
        endpointID.setEndpointDetailedAddressName(endpointAddress);
        return (endpointID);
    }

    @Override
    protected String specifyEndpointServiceName() {
        return (getProcessingPlantInterface().getIPCServiceName());
    }

    @Override
    protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType() {
        return (PetasosEndpointFunctionTypeEnum.PETASOS_INTERCEPTION_ENDPOINT);
    }

    @Override
    protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType() {
        return (EndpointPayloadTypeEnum.ENDPOINT_PAYLOAD_INTERNAL_AUDITEVENTS);
    }

    @Override
    protected void resolveTopologyEndpoint() {
        setTopologyNode(getJgroupsParticipantInformationService().getMyPetasosTopologyEndpoint());
    }

    @Override
    protected PubSubParticipant specifyPubSubParticipant() {
        PubSubParticipant myInterZoneParticipantRole = getJgroupsParticipantInformationService().getMyPetasosParticipantRole();
        if(myInterZoneParticipantRole == null){
            myInterZoneParticipantRole = getJgroupsParticipantInformationService().buildMyPetasosParticipantRole(getPetasosEndpoint());
        }
        return (myInterZoneParticipantRole);
    }

    @Override
    protected void registerWithCoreSubsystemPetasosEndpointsWatchdog() {
        getCoreSubsystemPetasosEndpointsWatchdog().setPetasosAuditServicesEndpoint(this.getPetasosEndpoint());
    }

    //
    // Business Methods
    //

    public List<Address> getAuditServerTargetAddressSet(String endpointServiceName){
        getLogger().debug(".getAuditServerTargetAddressSet(): Entry, endpointServiceName->{}", endpointServiceName);
        List<Address> endpointAddressSet = new ArrayList<>();
        if(StringUtils.isEmpty(endpointServiceName)){
            getLogger().debug(".getAuditServerTargetAddressSet(): Exit, endpointServiceName is empty");
            return(endpointAddressSet);
        }
        List<PetasosAdapterAddress> memberAdapterSetForService = getTargetMemberAdapterSetForService(endpointServiceName);
        for(PetasosAdapterAddress currentMember: memberAdapterSetForService){
            Address currentMemberAddress = currentMember.getJGroupsAddress();
            if(currentMemberAddress != null){
                endpointAddressSet.add(currentMemberAddress);
            }
        }
        getLogger().debug(".getAuditServerTargetAddressSet(): Exit, endpointAddressSet->{}", endpointAddressSet);
        return(endpointAddressSet);
    }

    public Address getCandidateAuditServerTargetAddress(String endpointServiceName){
        getLogger().debug(".getCandidateAuditServerTargetAddress(): Entry, endpointServiceName->{}", endpointServiceName);
        if(StringUtils.isEmpty(endpointServiceName)){
            getLogger().debug(".getCandidateAuditServerTargetAddress(): Exit, endpointServiceName is empty");
            return(null);
        }
        List<Address> endpointAddressSet = getAuditServerTargetAddressSet(endpointServiceName);
        if(endpointAddressSet.isEmpty()){
            getLogger().debug(".getCandidateAuditServerTargetAddress(): Exit, endpointAddressSet is empty");
            return(null);
        }
        Address endpointJGroupsAddress = endpointAddressSet.get(0);
        getLogger().debug(".getCandidateAuditServerTargetAddress(): Exit, selected address->{}", endpointJGroupsAddress);
        return(endpointJGroupsAddress);
    }

    //
    // ****Tactical****
    // Task Execution / Capability Utilisation Services
    //

    public CapabilityUtilisationResponse executeTask(String capabilityProviderName, CapabilityUtilisationRequest task){
        getLogger().trace(".executeTask(): Entry, capabilityProviderName->{}, task->{}", capabilityProviderName, task);
        Address targetAddress = getCandidateAuditServerTargetAddress(capabilityProviderName);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = task;
            classSet[0] = CapabilityUtilisationRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            CapabilityUtilisationResponse response = getRPCDispatcher().callRemoteMethod(targetAddress, "executeTaskHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".executeTask(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".executeTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            CapabilityUtilisationResponse response = new CapabilityUtilisationResponse();
            response.setAssociatedRequestID(task.getRequestID());
            response.setSuccessful(false);
            return(response);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".executeTask: Error (GeneralException) ->{}", e.getMessage());
            CapabilityUtilisationResponse response = new CapabilityUtilisationResponse();
            response.setAssociatedRequestID(task.getRequestID());
            response.setSuccessful(false);
            return(response);
        }
    }

    public CapabilityUtilisationResponse executeTaskHandler(CapabilityUtilisationRequest task){
        getLogger().debug(".executeTaskHandler(): Entry, task->{}", task);
        CapabilityUtilisationResponse response = getProcessingPlantInterface().executeTask(task);
        getLogger().debug(".executeTaskHandler(): Exit, response->{}", response);
        return(response);
    }

    //
    // AuditEvent RPC Method Support
    //

    @Override
    public PegacornTransactionMethodOutcome logAuditEvent(String serviceProviderName, AuditEvent event){
        getLogger().trace(".logAuditEvent(): Entry, serviceProviderName->{}, event->{}", serviceProviderName, event);
        PetasosEndpointIdentifier endpointIdentifier = getEndpointID();
        Address targetAddress = getCandidateAuditServerTargetAddress(serviceProviderName);
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = event;
            classSet[0] = AuditEvent.class;
            objectSet[1] = endpointIdentifier;
            classSet[1] = PetasosEndpointIdentifier.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PegacornTransactionMethodOutcome response = getRPCDispatcher().callRemoteMethod(targetAddress, "logAuditEventHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".logAuditEvent(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".logAuditEvent(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".logAuditEvent: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }
    public PegacornTransactionMethodOutcome logAuditEventHandler(AuditEvent event, PetasosEndpointIdentifier endpointIdentifier){
        getLogger().trace(".logAuditEventHandler(): Entry, event->{}, endpointIdentifier->{}", event, endpointIdentifier);
        PegacornTransactionMethodOutcome outcome = null;
        if((event != null) && (endpointIdentifier != null)) {
            outcome = auditEventServiceHandler.logAuditEvent(event, endpointIdentifier);
        }
        if(outcome == null) {
            IdType id = null;
            if(event.getId() == null){
                id = new IdType();
                id.setValue(UUID.randomUUID().toString());
            } else {
                id = event.getIdElement();
            }
            outcome = outcomeFactory.createResourceActivityOutcome(id, PegacornTransactionStatusEnum.CREATION_FAILURE, getProcessingPlantInterface().getSimpleInstanceName());
        }
        getLogger().debug(".logAuditEventHandler(): Exit, outcome->{}", outcome);
        return(outcome);
    }

    @Override
    public PegacornTransactionMethodOutcome logAuditEvent(String serviceProviderName, List<AuditEvent> eventList){
        getLogger().trace(".logAuditEvent(): Entry, serviceProviderName->{}, eventList->{}", serviceProviderName, eventList);
        PetasosEndpointIdentifier endpointIdentifier = getEndpointID();
        Address targetAddress = getCandidateAuditServerTargetAddress(serviceProviderName);
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = eventList;
            classSet[0] = List.class;
            objectSet[1] = endpointIdentifier;
            classSet[1] = PetasosEndpointIdentifier.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PegacornTransactionMethodOutcome response = getRPCDispatcher().callRemoteMethod(targetAddress, "logMultipleAuditEventHandler", objectSet, classSet, requestOptions);
            getLogger().debug(".logAuditEvent(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().error(".logAuditEvent(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".logAuditEvent: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    public PegacornTransactionMethodOutcome logMultipleAuditEventHandler(List<AuditEvent> eventList, PetasosEndpointIdentifier endpointIdentifier){
        getLogger().trace(".logAuditEventHandler(): Entry, eventList->{}, endpointIdentifier->{}", eventList, endpointIdentifier);
        PegacornTransactionMethodOutcome outcome = null;
        if((eventList != null) && (endpointIdentifier != null)) {
            outcome = auditEventServiceHandler.logAuditEvent(eventList, endpointIdentifier);
        }
        if(outcome == null) {
            outcome = outcomeFactory.createResourceActivityOutcome(null, PegacornTransactionStatusEnum.CREATION_FAILURE, getProcessingPlantInterface().getSimpleInstanceName());
        }
        getLogger().debug(".logAuditEventHandler(): Exit, outcome->{}", outcome);
        return(outcome);
    }
}
