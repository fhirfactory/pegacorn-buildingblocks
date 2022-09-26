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
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTaskSet;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.deployment.names.subsystems.SubsystemNames;
import net.fhirfactory.pegacorn.petasos.endpoints.services.tasking.PetasosTaskServicesEndpoint;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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

    //
    // Register a PetasosActionableTask
    @Override
    public PetasosActionableTask registerActionableTask(PetasosActionableTask actionableTask){
        getLogger().debug(".registerActionableTask(): Entry, task->{}", actionableTask);
        JGroupsIntegrationPointSummary jgroupsIPSummary = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".registerActionableTask(): Cannot Access {} to update task",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".registerActionableTask(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.registerActionableTask())!!!");
            return(null);
        }
        RemoteProcedureCallRequest remoteProcedureCallRequest = rpcRequestFactory.newRemoteProcedureCallRequest(actionableTask, PetasosActionableTask.class, jgroupsIPSummary);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = remoteProcedureCallRequest;
            classSet[0] = RemoteProcedureCallRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            RemoteProcedureCallResponse response = null;
            getMetricsAgent().incrementRemoteProcedureCallCount();
            synchronized (getIPCChannelLock()) {
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "registerActionableTaskHandler", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".registerActionableTask(): Exit, response->{}", response);
            if(response.isSuccessful()){
                PetasosActionableTask registeredTask = (PetasosActionableTask) response.getResponseContent();
                return(registeredTask);
            } else {
                getMetricsAgent().incrementRemoteProcedureCallFailureCount();
                getLogger().error(".registerActionableTask(): Could not register task, response->{}", response);
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".registerActionableTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".registerActionableTask: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    @Override
    public PetasosActionableTask fulfillActionableTask(PetasosActionableTask actionableTask){
        getLogger().trace(".fulfillActionableTask(): Entry, task->{}", actionableTask);
        JGroupsIntegrationPointSummary endpointIdentifier = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".fulfillActionableTask(): Cannot Access {} to update task",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".fulfillActionableTask(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.fulfillActionableTask())!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance (.fulfillActionableTask())!!!");
            return(null);
        }
        RemoteProcedureCallRequest remoteProcedureCallRequest = rpcRequestFactory.newRemoteProcedureCallRequest(actionableTask, PetasosActionableTask.class, endpointIdentifier);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = remoteProcedureCallRequest;
            classSet[0] = RemoteProcedureCallRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            RemoteProcedureCallResponse response = null;
            synchronized(getIPCChannelLock()) {
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "fulfillActionableTaskHandler", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".fulfillActionableTask(): Exit, response->{}", response);
            if(response.isSuccessful()){
                PetasosActionableTask registeredTask = (PetasosActionableTask) response.getResponseContent();
                return(registeredTask);
            } else {
                getLogger().error(".fulfillActionableTask(): Could not register task, response->{}", response);
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getLogger().error(".fulfillActionableTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".fulfillActionableTask: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    //
    // Update a PetasosActionableTask

    @Override
    public PetasosActionableTask updateActionableTask( PetasosActionableTask actionableTask){
        getLogger().debug(".updateActionableTask(): Entry, task->{}",  actionableTask);
        JGroupsIntegrationPointSummary endpointIdentifier = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".updateActionableTask(): Cannot Access {} to update task",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".updateActionableTask(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance!!!");
            return(null);
        }
        RemoteProcedureCallRequest remoteProcedureCallRequest = null;
        try {
            remoteProcedureCallRequest = rpcRequestFactory.newRemoteProcedureCallRequest(actionableTask, PetasosActionableTask.class, endpointIdentifier);
        } catch(Exception ex){
            getLogger().warn(".updateActionableTask(): Warning: Cannot formulate Ponos-IM RPC Request (for updateActionableTask)!!!",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getMetricsAgent().sendITOpsNotification("Warning: Cannot formulate Ponos-IM RPC Request (for updateActionableTask)!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Warning: Cannot formulate Ponos-IM RPC Request (for updateActionableTask)!!!");
            return(null);
        }

        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = remoteProcedureCallRequest;
            classSet[0] = RemoteProcedureCallRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            RemoteProcedureCallResponse response = null;
            synchronized (getIPCChannelLock()) {
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "updateActionableTaskHandler", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".updateActionableTask(): Exit, response->{}", response);
            if(response.isSuccessful()){
                PetasosActionableTask registeredTask = (PetasosActionableTask) response.getResponseContent();
                return(registeredTask);
            } else {
                getLogger().error(".updateActionableTask(): Could not update task, response->{}", response);
                return(null);
            }
        } catch (NoSuchMethodException e) {
            getLogger().error(".updateActionableTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".updateActionableTask: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    @Override
    public PetasosActionableTaskSet retrievePendingActionableTasks(PetasosParticipantId participantId) {
        getLogger().debug(".retrievePendingActionableTasks(): Entry, participantId->{}",  participantId);
        JGroupsIntegrationPointSummary jgroupsIP = createSummary(getJgroupsIPSet().getPetasosTaskServicesEndpoint());
        Address targetAddress = getCandidateTargetServiceAddress(subsystemNames.getPetasosTaskRepositoryServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".retrievePendingActionableTasks(): Cannot Access {} to update task", subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getLogger().error(".retrievePendingActionableTasks(): Cannot find candidate Ponos-IM Instance!!!");
            getMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Error: Cannot find candidate Ponos-IM Instance!!!");
            return(new PetasosActionableTaskSet());
        }
        RemoteProcedureCallRequest remoteProcedureCallRequest = null;
        try {
            remoteProcedureCallRequest = rpcRequestFactory.newRemoteProcedureCallRequest(participantId, PetasosParticipantId.class, jgroupsIP);
        } catch(Exception ex){
            getLogger().warn(".retrievePendingActionableTasks(): Warning: Cannot formulate Ponos-IM RPC Request (for updateActionableTask)!!!",subsystemNames.getPetasosTaskRepositoryServiceProviderName() );
            getMetricsAgent().sendITOpsNotification("Warning: Cannot formulate Ponos-IM RPC Request (for updateActionableTask)!!!");
            getProcessingPlantMetricsAgent().sendITOpsNotification("Warning: Cannot formulate Ponos-IM RPC Request (for updateActionableTask)!!!");
            return(new PetasosActionableTaskSet());
        }

        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = remoteProcedureCallRequest;
            classSet[0] = RemoteProcedureCallRequest.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            RemoteProcedureCallResponse response = null;
            synchronized (getIPCChannelLock()) {
                response = getRPCDispatcher().callRemoteMethod(targetAddress, "retrievePendingActionableTasksHandler", objectSet, classSet, requestOptions);
            }
            getLogger().debug(".updateActionableTask(): Exit, response->{}", response);
            if(response.isSuccessful()){
                PetasosActionableTaskSet registeredTasks = (PetasosActionableTaskSet) response.getResponseContent();
                return(registeredTasks);
            } else {
                getLogger().error(".updateActionableTask(): Could not update task, response->{}", response);
                return(new PetasosActionableTaskSet());
            }
        } catch (NoSuchMethodException e) {
            getLogger().error(".updateActionableTask(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(new PetasosActionableTaskSet());
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error(".updateActionableTask: Error (GeneralException) ->{}", e.getMessage());
            return(new PetasosActionableTaskSet());
        }
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
