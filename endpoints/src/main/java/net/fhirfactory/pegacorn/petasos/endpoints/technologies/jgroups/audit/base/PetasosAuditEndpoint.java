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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.audit.base;

import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventServiceHandlerInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.core.model.transaction.factories.PegacornTransactionMethodOutcomeFactory;
import net.fhirfactory.pegacorn.core.model.transaction.model.PegacornTransactionMethodOutcome;
import net.fhirfactory.pegacorn.core.model.transaction.valuesets.PegacornTransactionStatusEnum;
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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class PetasosAuditEndpoint extends JGroupsPetasosEndpointBase {

    @Produce
    private ProducerTemplate camelProducer;

    @Inject
    private PetasosAuditEventServiceHandlerInterface auditEventServiceHandler;

    @Inject
    private PegacornTransactionMethodOutcomeFactory outcomeFactory;

    //
    // Constructor
    //

    public PetasosAuditEndpoint(){
        super();
    }

    //
    // PostConstruct Activities
    //

    @Override
    protected void executePostConstructActivities() {

    }

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

    public boolean auditServiceProviderIsInScope(String capabilityProviderServiceName){
        List<String> memberSetBasedOnService = getClusterMemberSetBasedOnService(capabilityProviderServiceName);
        if(memberSetBasedOnService.isEmpty()){
            return(false);
        }
        for(String currentName: memberSetBasedOnService){
            if(isWithinScopeBasedOnChannelName(currentName)){
                return(true);
            }
        }
        return(false);
    }

    //
    // AuditEvent RPC Method Support
    //

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

    //
    // Getters (and Setters)
    //


    public ProducerTemplate getCamelProducer() {
        return camelProducer;
    }
}
