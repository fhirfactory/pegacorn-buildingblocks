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

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskBrokerInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.use.rpc.RemoteProcedureCallRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.use.rpc.RemoteProcedureCallResponse;
import net.fhirfactory.pegacorn.core.model.capabilities.use.rpc.factories.RemoteProcedureCallRequestFactory;
import net.fhirfactory.pegacorn.core.model.petasos.jobcard.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.collections.PetasosActionableTaskSet;
import net.fhirfactory.pegacorn.core.model.petasos.task.collections.PetasosTaskIdSet;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskExecutionCommandEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.deployment.names.subsystems.SubsystemNames;
import net.fhirfactory.pegacorn.petasos.endpoints.services.tasking.PetasosTaskServicesEndpoint;
import org.apache.commons.lang3.SerializationUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@ApplicationScoped
public class PetasosDistributedTaskServicesAgent extends PetasosTaskServicesEndpoint implements PetasosTaskBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosDistributedTaskServicesAgent.class);

    @Inject
    private RemoteProcedureCallRequestFactory rpcRequestFactory;

    @Inject
    private SubsystemNames subsystemNames;


    //
    // Business Methods
    //


    // Register a PetasosActionableTask
    @Override
    public PetasosTaskJobCard registerTask(PetasosActionableTask actionableTask, PetasosTaskJobCard jobCard){
        getLogger().debug(".registerTask(): Entry, task->{}", actionableTask);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            printParticipantResolutionError("[Method->registerTask()] Cannot Access Participant within JGroups Cluster, participant->" + subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, actionableTask, jobCard};
            Class classSet[] = createClassSet(objectSet);

            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosTaskJobCard response = null;
            synchronized(getIPCChannelLock()) {
                response = (PetasosTaskJobCard) getRPCDispatcher().callRemoteMethod(targetAddress, "registerTaskHandler", objectSet, classSet, requestOptions);
            }
            if(response != null){
                getMetricsAgent().incrementRPCInvocationCount(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
                getMetricsAgent().incrementRemoteProcedureCallCount();
                getLogger().debug(".registerTask(): Exit, response->{}", response);
                return(response);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".registerTask(): Could not register task, response->{}", response);
                PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
                badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
                badOutcomeJobCard.setUpdateInstant(Instant.now());
                getLogger().debug(".registerTask(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
                return(badOutcomeJobCard);
            }
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".registerTask: Error (GeneralException) ->", e);
            PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
            badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
            badOutcomeJobCard.setUpdateInstant(Instant.now());
            getLogger().debug(".registerTask(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
            return(badOutcomeJobCard);
        }
    }

    @Override
    public PetasosTaskJobCard registerTaskOutcome(PetasosActionableTask actionableTask, PetasosTaskJobCard jobCard){
        getLogger().debug(".registerTaskOutcome(): Entry, task->{}", actionableTask);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            printParticipantResolutionError("[Method->registerTaskOutcome()] Cannot Access Participant within JGroups Cluster, participant->" + subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, actionableTask, jobCard};
            Class classSet[] = createClassSet(objectSet);

            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosTaskJobCard response = null;
            synchronized(getIPCChannelLock()) {
                response = (PetasosTaskJobCard) getRPCDispatcher().callRemoteMethod(targetAddress, "registerTaskOutcomeHandler", objectSet, classSet, requestOptions);
            }
            if(response != null){
                getMetricsAgent().incrementRPCInvocationCount(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
                getMetricsAgent().incrementRemoteProcedureCallCount();
                getLogger().debug(".registerTaskOutcome(): Exit, response->{}", response);
                return(response);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".registerTaskOutcome(): Could not register task, response->{}", response);
                PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
                badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
                badOutcomeJobCard.setUpdateInstant(Instant.now());
                getLogger().debug(".registerTaskOutcome(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
                return(badOutcomeJobCard);
            }
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".registerTaskOutcome: Error (GeneralException) ->", e);
            PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
            badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
            badOutcomeJobCard.setUpdateInstant(Instant.now());
            getLogger().debug(".registerTaskOutcome(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
            return(badOutcomeJobCard);
        }
    }

    @Override
    public PetasosTaskJobCard registerTaskWaiting(PetasosTaskJobCard jobCard){
        getLogger().debug(".registerTaskWaiting(): Entry, jobCard->{}", jobCard);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            printParticipantResolutionError("[Method->registerTaskWaiting()] Cannot Access Participant within JGroups Cluster, participant->" + subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, jobCard};
            Class classSet[] = createClassSet(objectSet);

            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosTaskJobCard response = null;
            synchronized(getIPCChannelLock()) {
                response = (PetasosTaskJobCard) getRPCDispatcher().callRemoteMethod(targetAddress, "registerTaskWaitingHandler", objectSet, classSet, requestOptions);
            }
            if(response != null){
                getMetricsAgent().incrementRPCInvocationCount(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
                getMetricsAgent().incrementRemoteProcedureCallCount();
                getLogger().debug(".registerTaskWaiting(): Exit, response->{}", response);
                return(response);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".registerTaskWaiting(): Could not register task, response->{}", response);
                PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
                badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
                badOutcomeJobCard.setUpdateInstant(Instant.now());
                getLogger().debug(".registerTaskWaiting(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
                return(badOutcomeJobCard);
            }
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".registerTaskWaiting: Error (GeneralException) ->", e);
            PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
            badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
            badOutcomeJobCard.setUpdateInstant(Instant.now());
            getLogger().debug(".registerTaskWaiting(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
            return(badOutcomeJobCard);
        }
    }

    @Override
    public PetasosTaskJobCard registerTaskStart(PetasosTaskJobCard jobCard){
        getLogger().debug(".registerTaskStart(): Entry, jobCard->{}", jobCard);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            printParticipantResolutionError("[Method->registerTaskStart()] Cannot Access Participant within JGroups Cluster, participant->" + subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, jobCard};
            Class classSet[] = createClassSet(objectSet);

            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosTaskJobCard response = null;
            synchronized(getIPCChannelLock()) {
                response = (PetasosTaskJobCard) getRPCDispatcher().callRemoteMethod(targetAddress, "registerTaskStartHandler", objectSet, classSet, requestOptions);
            }
            if(response != null){
                getMetricsAgent().incrementRPCInvocationCount(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
                getMetricsAgent().incrementRemoteProcedureCallCount();
                getLogger().debug(".registerTaskStart(): Exit, response->{}", response);
                return(response);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".registerTaskStart(): Could not register task, response->{}", response);
                PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
                badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
                badOutcomeJobCard.setUpdateInstant(Instant.now());
                getLogger().debug(".registerTaskFailure(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
                return(badOutcomeJobCard);
            }
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".registerTaskStart: Error (GeneralException) ->", e);
            PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
            badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
            badOutcomeJobCard.setUpdateInstant(Instant.now());
            getLogger().debug(".registerTaskFailure(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
            return(badOutcomeJobCard);
        }
    }

    @Override
    public PetasosTaskJobCard registerTaskFailure(PetasosTaskJobCard jobCard){
        getLogger().debug(".registerTaskFailure(): Entry, jobCard->{}", jobCard);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            printParticipantResolutionError("[Method->registerTaskFailure()] Cannot Access Participant within JGroups Cluster, participant->" + subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, jobCard};
            Class classSet[] = createClassSet(objectSet);

            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosTaskJobCard response = null;
            synchronized(getIPCChannelLock()) {
                response = (PetasosTaskJobCard) getRPCDispatcher().callRemoteMethod(targetAddress, "registerTaskFailureHandler", objectSet, classSet, requestOptions);
            }
            if(response != null){
                getMetricsAgent().incrementRPCInvocationCount(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
                getMetricsAgent().incrementRemoteProcedureCallCount();
                getLogger().debug(".registerTaskFailure(): Exit, response->{}", response);
                return(response);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".registerTaskFailure(): Could not register task, response->{}", response);
                PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
                badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
                badOutcomeJobCard.setUpdateInstant(Instant.now());
                getLogger().debug(".registerTaskFailure(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
                return(badOutcomeJobCard);
            }
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".registerTaskFailure: Error (GeneralException) ->", e);
            PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
            badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
            badOutcomeJobCard.setUpdateInstant(Instant.now());
            getLogger().debug(".registerTaskFailure(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
            return(badOutcomeJobCard);
        }
    }

    @Override
    public PetasosTaskJobCard registerTaskFinish(PetasosTaskJobCard jobCard){
        getLogger().debug(".registerTaskCompletion(): Entry, jobCard->{}", jobCard);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            printParticipantResolutionError("[Method->registerTaskFinish()] Cannot Access Participant within JGroups Cluster, participant->" + subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, jobCard};
            Class classSet[] = createClassSet(objectSet);

            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosTaskJobCard response = null;
            synchronized(getIPCChannelLock()) {
                response = (PetasosTaskJobCard) getRPCDispatcher().callRemoteMethod(targetAddress, "registerTaskFinishHandler", objectSet, classSet, requestOptions);
            }
            if(response != null){
                getMetricsAgent().incrementRPCInvocationCount(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
                getMetricsAgent().incrementRemoteProcedureCallCount();
                getLogger().debug(".registerTaskFinish(): Exit, response->{}", response);
                return(response);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".registerTaskFinish(): Could not register task, response->{}", response);
                PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
                badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
                badOutcomeJobCard.setUpdateInstant(Instant.now());
                getLogger().debug(".registerTaskFinish(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
                return(badOutcomeJobCard);
            }
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".registerTaskFinish: Error (GeneralException) ->", e);
            PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
            badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
            badOutcomeJobCard.setUpdateInstant(Instant.now());
            getLogger().debug(".registerTaskFinish(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
            return(badOutcomeJobCard);
        }
    }

    @Override
    public PetasosTaskJobCard registerTaskFinalisation(PetasosTaskJobCard jobCard){
        getLogger().debug(".registerTaskFinalisation(): Entry, jobCard->{}", jobCard);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            printParticipantResolutionError("[Method->registerTaskFinalisation()] Cannot Access Participant within JGroups Cluster, participant->" + subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, jobCard};
            Class classSet[] = createClassSet(objectSet);

            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosTaskJobCard response = null;
            synchronized(getIPCChannelLock()) {
                response = (PetasosTaskJobCard) getRPCDispatcher().callRemoteMethod(targetAddress, "registerTaskFinalisationHandler", objectSet, classSet, requestOptions);
            }
            if(response != null){
                getMetricsAgent().incrementRPCInvocationCount(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
                getMetricsAgent().incrementRemoteProcedureCallCount();
                getLogger().debug(".registerTaskFinalisation(): Exit, response->{}", response);
                return(response);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".registerTaskFinalisation(): Could not register task, response->{}", response);
                PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
                badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
                badOutcomeJobCard.setUpdateInstant(Instant.now());
                getLogger().debug(".registerTaskFinalisation(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
                return(badOutcomeJobCard);
            }
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".registerTaskFinalisation: Error (GeneralException) ->", e);
            PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
            badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
            badOutcomeJobCard.setUpdateInstant(Instant.now());
            getLogger().debug(".registerTaskFinalisation(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
            return(badOutcomeJobCard);
        }
    }

    @Override
    public PetasosTaskJobCard registerTaskCancellation(PetasosTaskJobCard jobCard){
        getLogger().debug(".registerTaskCancellation(): Entry, jobCard->{}", jobCard);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            printParticipantResolutionError("[Method->registerTaskCancellation()] Cannot Access Participant within JGroups Cluster, participant->" + subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            return(null);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, jobCard};
            Class classSet[] = createClassSet(objectSet);

            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosTaskJobCard response = null;
            synchronized(getIPCChannelLock()) {
                response = (PetasosTaskJobCard) getRPCDispatcher().callRemoteMethod(targetAddress, "registerTaskCancellationHandler", objectSet, classSet, requestOptions);
            }
            if(response != null){
                getMetricsAgent().incrementRPCInvocationCount(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
                getMetricsAgent().incrementRemoteProcedureCallCount();
                getLogger().debug(".registerTaskCancellation(): Exit, response->{}", response);
                return(response);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".registerTaskCancellation(): Could not register task, response->{}", response);
                PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
                badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
                badOutcomeJobCard.setUpdateInstant(Instant.now());
                getLogger().debug(".registerTaskCancellation(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
                return(badOutcomeJobCard);
            }
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".registerTaskCancellation: Error (GeneralException) ->", e);
            PetasosTaskJobCard badOutcomeJobCard = SerializationUtils.clone(jobCard);
            badOutcomeJobCard.setGrantedStatus(TaskExecutionCommandEnum.TASK_COMMAND_CANCEL);
            badOutcomeJobCard.setUpdateInstant(Instant.now());
            getLogger().debug(".registerTaskCancellation(): Exit, badOutcomeJobCard->{}", badOutcomeJobCard);
            return(badOutcomeJobCard);
        }
    }

    @Override
    public PetasosActionableTask getOffloadedPendingTask(PetasosParticipantId participantId, TaskIdType taskId){
        getLogger().trace(".fulfillActionableTask(): Entry, participantId->{}, taskId->{}", participantId, taskId);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            printParticipantResolutionError(" Cannot Access Participant within JGroups Cluster, participant->" + subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            return(null);
        }

        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, participantId, taskId};
            Class classSet[] = createClassSet(objectSet);

            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosActionableTask registeredTask = null;
            synchronized(getIPCChannelLock()) {
                registeredTask = getRPCDispatcher().callRemoteMethod(targetAddress, "getAdditionalPendingTaskHandler", objectSet, classSet, requestOptions);
            }
            if(registeredTask != null){
                getMetricsAgent().incrementRPCInvocationCount(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
                getMetricsAgent().incrementRemoteProcedureCallCount();
                getLogger().debug(".fulfillActionableTask(): Exit, registeredTask->{}", registeredTask);
                return(registeredTask);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".fulfillActionableTask(): Could not register task, registeredTask->{}", registeredTask);
                return(null);
            }
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".fulfillActionableTask: Error (GeneralException) ->", e);
            return(null);
        }
    }

    @Override
    public Boolean hasOffloadedPendingTasks(PetasosParticipantId participantId) {
        getLogger().debug(".hasAdditionalPendingTasks(): Entry, participantId->{}",  participantId);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            printParticipantResolutionError(".hasAdditionalPendingTasks(): Cannot Access Participant within JGroups Cluster, participant->" + subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            return(false);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, participantId};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Boolean hasAdditionalPendingTasks = null;
            getLogger().trace(".hasAdditionalPendingTasks(): [Invoke RMI Request] Start");
            synchronized (getIPCChannelLock()) {
                hasAdditionalPendingTasks  = getRPCDispatcher().callRemoteMethod(targetAddress, "hasAdditionalPendingTasksHandler", objectSet, classSet, requestOptions);
            }
            getLogger().trace(".hasAdditionalPendingTasks(): [Invoke RMI Request] Finish, response(hasAdditionalPendingTasks)->{}", hasAdditionalPendingTasks);

            if(hasAdditionalPendingTasks != null){
                getMetricsAgent().incrementRPCInvocationCount(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
                getMetricsAgent().incrementRemoteProcedureCallCount();
                getLogger().error(".hasAdditionalPendingTasks(): Exit");
                return(hasAdditionalPendingTasks);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".hasAdditionalPendingTasks(): could not invoke RMI method");
                return(false);
            }
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".hasAdditionalPendingTasks: Error (GeneralException) ->", e);
            return(false);
        }
    }

    @Override
    public Integer offloadPendingTasks(PetasosParticipantId participantId, PetasosTaskIdSet tasksToBeOffloaded) {
        getLogger().debug(".offloadPendingTasks(): Entry, participantId->{}, tasksToBeOffloaded->{}",  participantId, tasksToBeOffloaded);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            printParticipantResolutionError(".offloadPendingTasks(): Cannot Access Participant within JGroups Cluster, participant->" + subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            return(-1);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, participantId, tasksToBeOffloaded};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Integer offloadCount = null;
            getLogger().trace(".offloadPendingTasks(): [Invoke RMI Request] Start");
            synchronized (getIPCChannelLock()) {
                offloadCount  = getRPCDispatcher().callRemoteMethod(targetAddress, "offloadPendingTasksHandler", objectSet, classSet, requestOptions);
            }
            getLogger().trace(".offloadPendingTasks(): [Invoke RMI Request] Finish, response(offloadCount)->{}", offloadCount);

            if(offloadCount != null){
                getMetricsAgent().incrementRPCInvocationCount(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
                getMetricsAgent().incrementRemoteProcedureCallCount();
                getLogger().error(".offloadPendingTasks(): Exit");
                return(offloadCount);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".offloadPendingTasks(): could not invoke RMI method");
                return(-1);
            }
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".offloadPendingTasks: Error (GeneralException) ->", e);
            return(-1);
        }
    }

    @Override
    public PetasosTaskIdSet synchronisePendingTasks(PetasosParticipantId participantId, PetasosTaskIdSet localPendingTaskSet) {
        getLogger().debug(".synchronisePendingTasks(): Entry, participantId->{}, tasksToBeOffloaded->{}",  participantId, localPendingTaskSet);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            printParticipantResolutionError(".synchronisePendingTasks(): Cannot Access Participant within JGroups Cluster, participant->" + subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            PetasosTaskIdSet outcome = SerializationUtils.clone(localPendingTaskSet);
            return(outcome);
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, participantId, localPendingTaskSet};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosTaskIdSet pendingTaskSet = null;
            getLogger().trace(".synchronisePendingTasks(): [Invoke RMI Request] Start");
            synchronized (getIPCChannelLock()) {
                pendingTaskSet  = getRPCDispatcher().callRemoteMethod(targetAddress, "synchronisePendingTasksHandler", objectSet, classSet, requestOptions);
            }
            getLogger().trace(".synchronisePendingTasks(): [Invoke RMI Request] Finish, response(pendingTaskSet)->{}", pendingTaskSet);

            if(pendingTaskSet != null){
                getMetricsAgent().incrementRPCInvocationCount(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
                getMetricsAgent().incrementRemoteProcedureCallCount();
                getLogger().error(".synchronisePendingTasks(): Exit");
                return(pendingTaskSet);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                PetasosTaskIdSet outcome = SerializationUtils.clone(localPendingTaskSet);
                getLogger().error(".synchronisePendingTasks(): could not invoke RMI method");
                return(outcome);
            }
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            PetasosTaskIdSet outcome = SerializationUtils.clone(localPendingTaskSet);
            getLogger().error(".synchronisePendingTasks: Error (GeneralException) ->", e);
            return(outcome);
        }
    }

    @Override
    public PetasosActionableTaskSet getOffloadedPendingTasks(PetasosParticipantId participantId, Integer count) {
        getLogger().debug(".retrievePendingActionableTasks(): Entry, participantId->{}",  participantId);
        String myName = getProcessingPlant().getTopologyNode().getComponentId().getName();
        String myParticipantName = getProcessingPlant().getTopologyNode().getParticipant().getParticipantId().getName();
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            printParticipantResolutionError(".retrievePendingActionableTasks(): Cannot Access Participant within JGroups Cluster, participant->" + subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            return(new PetasosActionableTaskSet());
        }
        try {
            Object objectSet[] = new Object[]{myName, myParticipantName, participantId, count};
            Class classSet[] = createClassSet(objectSet);
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosActionableTaskSet taskSet = null;

            getLogger().trace(".retrieveBufferedTasks(): [Invoke RMI Request] Start");
            synchronized (getIPCChannelLock()) {
                taskSet  = getRPCDispatcher().callRemoteMethod(targetAddress, "retrieveBufferedTasks", objectSet, classSet, requestOptions);
            }
            getLogger().trace(".retrieveBufferedTasks(): [Invoke RMI Request] Finish, response(taskSet)->{}", taskSet);

            if(taskSet != null){
                getLogger().error(".updateActionableTask(): Exit");
                return(taskSet);
            } else {
                getLogger().error(".updateActionableTask(): could not invoke RMI method");
                return(null);
            }
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".updateActionableTask: Error (GeneralException) ->", e);
            return(new PetasosActionableTaskSet());
        }
    }

    //
    // Helper Methods
    //

    protected void printParticipantResolutionError(String error){
        getLogger().error(".fulfillActionableTask(): {}", error);
        getMetricsAgent().sendITOpsNotification("Error: " + error);
        getProcessingPlantMetricsAgent().sendITOpsNotification("Error: " + error);
    }

    //
    // Getters (and Setters)
    //

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    protected SubsystemNames getSubsystemNames(){
        return(subsystemNames);
    }

    protected RemoteProcedureCallRequestFactory getRpcRequestFactory(){
        return(rpcRequestFactory);
    }

}
