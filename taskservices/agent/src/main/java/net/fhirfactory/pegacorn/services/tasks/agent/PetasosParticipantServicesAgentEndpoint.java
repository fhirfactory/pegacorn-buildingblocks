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
package net.fhirfactory.pegacorn.services.tasks.agent;

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskRepositoryServiceProviderNameInterface;
import net.fhirfactory.pegacorn.core.interfaces.ui.resources.ParticipantUIServicesAPI;
import net.fhirfactory.pegacorn.core.interfaces.ui.resources.TaskUIServicesAPI;
import net.fhirfactory.pegacorn.core.model.petasos.oam.notifications.ITOpsNotificationContent;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantControlStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistration;
import net.fhirfactory.pegacorn.core.model.petasos.participant.registration.PetasosParticipantRegistrationStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.TaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.PetasosParticipantESR;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.PetasosParticipantSummary;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.TaskSummary;
import net.fhirfactory.pegacorn.petasos.core.participants.management.LocalParticipantManager;
import net.fhirfactory.pegacorn.petasos.endpoints.services.participants.PetasosParticipantServicesEndpointBase;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class PetasosParticipantServicesAgentEndpoint extends PetasosParticipantServicesEndpointBase implements ParticipantUIServicesAPI, TaskUIServicesAPI {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosParticipantServicesAgentEndpoint.class);

    @Inject
    private PetasosTaskRepositoryServiceProviderNameInterface taskServiceProviderName;

    @Inject
    private LocalParticipantManager participantManager;

    //
    // Constructor(s)
    //

    public PetasosParticipantServicesAgentEndpoint(){
        super();
    }

    //
    // Post Construct
    //

    @Override
    protected void initialiseCacheSynchronisationDaemon() {
        getLogger().debug(".initialiseCacheSynchronisationDaemon(): Entry");
        // nothing to do
        getLogger().debug(".initialiseCacheSynchronisationDaemon(): Exit");
    }

    //
    // Endpoint Changes
    //

    //
    // Business Methods
    //

    @Override
    public PetasosParticipantRegistration synchroniseRegistration(PetasosParticipantRegistration localRegistration) {
        getLogger().debug(".synchroniseRegistration(): Entry, localRegistration->{}", localRegistration);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        if(localRegistration == null ){
            getLogger().trace(".synchroniseRegistration(): localRegistration is null");
            getLogger().debug(".synchroniseRegistration(): Exit");
            return(null);
        }
        if(StringUtils.isEmpty(localRegistration.getParticipantId().getSubsystemName())){
            localRegistration.getParticipantId().setSubsystemName(getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getSubsystemName());
        }
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".synchroniseRegistration(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null){
            getLogger().warn(".synchroniseRegistration(): Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            getLogger().debug(".synchroniseRegistration(): Exit, (Wrong endpoint technology (should be JGroups))");
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, localRegistration};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantRegistration participantStatus = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "synchroniseRegistrationHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCResponseCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".synchroniseRegistration(): Exit, participantStatus->{}", participantStatus);
            return(participantStatus);
        } catch (Exception e) {
            getLogger().warn(".synchroniseRegistration(): Error (GeneralException) ->",e);
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCFailureCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            PetasosParticipantRegistration clonedRegistration = new PetasosParticipantRegistration(localRegistration);
            clonedRegistration.setCentralRegistrationStatus(PetasosParticipantRegistrationStatusEnum.PARTICIPANT_REGISTRATION_FAILED);
            clonedRegistration.setCentralRegistrationInstant(Instant.now());
            return(clonedRegistration);
        }
    }

    @Override
    public Set<PetasosParticipantRegistration> getDownstreamSubscribers(String producerServiceName) {
        getLogger().debug(".getDownstreamSubscribers(): Entry, producerServiceName->{}", producerServiceName);
        if(StringUtils.isEmpty(producerServiceName)){
            return(new HashSet<>());
        }
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".getDownstreamSubscribers(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".getDownstreamSubscribers: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (new HashSet<>());
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, producerServiceName};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Set<PetasosParticipantRegistration> participantSet = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "getDownstreamSubscribersHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCResponseCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getProcessingPlantMetricsAgent().touchParticipantSynchronisationIndicator(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".getDownstreamSubscribers(): Exit, participantSet->{}", participantSet);
            return(participantSet);
        } catch (Exception e) {
            getLogger().debug(".getDownstreamSubscribers(): Error ->",e);
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCFailureCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            return(new HashSet<>());
        }
    }

    //
    // Command API
    //

    @Override
    public List<PetasosParticipantSummary> listSubsystems() {
        getLogger().debug(".listSubsystems(): Entry");
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".listSubsystems(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".listSubsystems: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            List<PetasosParticipantSummary> answer = new ArrayList<>();
            return (answer);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            List<PetasosParticipantSummary> answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "listSubsystemsHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCResponseCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".listSubsystems(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCFailureCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".listSubsystems(): Error (GeneralException) ->",e);
            List<PetasosParticipantSummary> answer = new ArrayList<>();
            return(answer);
        }
    }

    @Override
    public List<PetasosParticipantSummary> listParticipants(String subsystemName) {
        getLogger().debug(".listParticipants(): Entry, subsystemName->{}", subsystemName);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".listParticipants(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".listParticipants: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            List<PetasosParticipantSummary> answer =new ArrayList<>();
            return (answer);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, subsystemName};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            List<PetasosParticipantSummary> answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "listParticipantsHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCResponseCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".listParticipants(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCFailureCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".listParticipants(): Error (GeneralException) ->",e);
            List<PetasosParticipantSummary> answer = new ArrayList<>();
            return(answer);
        }
    }

    @Override
    public PetasosParticipantSummary setControlStatus(String participantName, PetasosParticipantControlStatusEnum controlStatus) {
        getLogger().debug(".setControlStatus(): Entry, participantName->{}", participantName);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".setControlStatus(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".setControlStatus: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, participantName, controlStatus};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantSummary answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "setControlStatusHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCResponseCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".setControlStatus(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCFailureCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".setControlStatus(): Error (GeneralException) ->",e);
            return(null);
        }
    }

    @Override
    public List<TaskSummary> listTasks(String participantName, TaskOutcomeStatusEnum status, boolean order, Instant startTime, Instant endTime) {
        getLogger().debug(".listTasks(): Entry, participantName->{}, status->{}, order->{}, startTime->{}, endTime->{}", participantName, status, order, startTime, endTime);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".listTasks(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".listTasks: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (new ArrayList<>());
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, participantName, status, order, startTime, endTime};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            List<TaskSummary> answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "listTasksHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCResponseCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".listTasks(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCFailureCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".listTasks(): Error (GeneralException) ->",e);
            return(new ArrayList<>());
        }
    }

    @Override
    public TaskSummary initiateTaskRedo(String taskId) {
        getLogger().debug(".initiateTaskRedo(): Entry, taskId->{}}", taskId);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".initiateTaskRedo(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".initiateTaskRedo: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, taskId};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskSummary answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "redoTaskHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCResponseCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".initiateTaskRedo(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCFailureCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".redoTask(): Error (GeneralException) ->",e);
            return(null);
        }
    }

    @Override
    public TaskSummary initiateTaskCancellation(String taskId) {
        getLogger().debug(".initiateTaskCancellation(): Entry, taskId->{}}", taskId);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".initiateTaskCancellation(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".initiateTaskCancellation: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, taskId};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            TaskSummary answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "cancelTaskHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCResponseCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".initiateTaskCancellation(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCFailureCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".initiateTaskCancellation(): Error (GeneralException) ->",e);
            return(null);
        }
    }

    @Override
    public PetasosParticipantSummary getParticipantSummary(String participantName) {
        getLogger().debug(".getParticipantSummary(): Entry, participantName->{}", participantName);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".getParticipantSummary(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".getParticipantSummary: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, participantName};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantSummary answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "getParticipantSummaryHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCResponseCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".getParticipantSummary(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCFailureCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".getParticipantSummary(): Error (GeneralException) ->",e);
            return(null);
        }
    }

    @Override
    public PetasosParticipantESR getParticipantESR(String participantName) {
        getLogger().debug(".getParticipantESR(): Entry, participantName->{}", participantName);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address taskServicesAddress = getCandidateTargetServiceAddress(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
        getLogger().trace(".getParticipantESR(): Extract JGroups Address->{}", taskServicesAddress);
        if(taskServicesAddress == null) {
            getLogger().warn(".getParticipantESR: Cannot resolve Task Services Provider endpoint (JGroups Channel Name)");
            return (null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, participantName};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosParticipantESR answer = getRPCDispatcher().callRemoteMethod(taskServicesAddress, "getParticipantESRHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCResponseCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".getParticipantESR(): Exit, answer->{}", answer);
            return(answer);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getMetricsAgent().incrementRPCInvocationCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getMetricsAgent().incrementRPCFailureCount(taskServiceProviderName.getPetasosTaskRepositoryServiceProviderName());
            getLogger().debug(".getParticipantESR(): Error (GeneralException) ->",e);
            return(null);
        }
    }

    protected ITOpsNotificationContent makeErrorResponse(String content){
        ITOpsNotificationContent response = new ITOpsNotificationContent();
        if(StringUtils.isEmpty(content)){
            content = "Unknown Error";
        }
        response.setContent(content);
        response.setContentHeading(content);
        response.setFormattedContent("<br>"+content+"</br>");
        return(response);
    }


    //
    // Getters (and Setters)
    //

    @Override
    protected Logger specifyLogger() {
        return LOG;
    }

 }
