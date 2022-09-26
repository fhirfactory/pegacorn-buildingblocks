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
package net.fhirfactory.pegacorn.services.tasks.manager;

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskBrokerInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.use.rpc.RemoteProcedureCallRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.use.rpc.RemoteProcedureCallResponse;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTaskSet;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.petasos.endpoints.services.tasking.PetasosTaskServicesEndpoint;
import net.fhirfactory.pegacorn.services.tasks.cache.PetasosActionableTaskDM;
import net.fhirfactory.pegacorn.services.tasks.datatypes.PetasosActionableTaskRegistrationType;

import java.time.Instant;

public abstract class PetasosDistributedTaskServicesManager extends PetasosTaskServicesEndpoint implements PetasosTaskBrokerInterface {

    //
    // Constructor(s)
    //


    //
    // Abstract Methods
    //

    abstract protected PetasosActionableTaskDM specifyActionableTaskCache();

    //
    // Getters and Setters
    //

   protected PetasosActionableTaskDM getActionableTaskCache(){
       return(specifyActionableTaskCache());
   }

    //
    // PetasosActionableTask Activities
    //

    public RemoteProcedureCallResponse registerActionableTaskHandler(RemoteProcedureCallRequest rpcRequest){
        getLogger().debug(".registerActionableTaskHandler(): Entry, rpcRequest->{}", rpcRequest);
        PetasosActionableTask taskToRegister = null;
        getMetricsAgent().incrementRemoteProcedureCallHandledCount();
        JGroupsIntegrationPointSummary endpointIdentifier = null;
        if(rpcRequest != null){
            if(rpcRequest.hasRequestContent()){
                if(rpcRequest.hasRequestContentType()){
                    if(rpcRequest.getRequestContentType().equals(PetasosActionableTask.class)){
                        taskToRegister = (PetasosActionableTask) rpcRequest.getRequestContent();
                    }
                }
            }
            if(rpcRequest.hasRequestingEndpoint()){
                endpointIdentifier = rpcRequest.getRequestingEndpoint();
            }
        }
        PetasosActionableTask registeredTask = null;
        if((taskToRegister != null) && (endpointIdentifier != null)) {
            registeredTask = registerActionableTask(taskToRegister);
        }
        RemoteProcedureCallResponse rpcResponse = new RemoteProcedureCallResponse();
        rpcResponse.setAssociatedRequestID(rpcRequest.getRequestID());
        rpcResponse.setInScope(true);
        rpcResponse.setInstantCompleted(Instant.now());
        if(registeredTask != null){
            rpcResponse.setResponseContent(registeredTask);
            rpcResponse.setResponseContentType(PetasosActionableTask.class);
            rpcResponse.setSuccessful(true);
        } else {
            rpcResponse.setSuccessful(false);
        }
        getLogger().debug(".registerActionableTaskHandler(): Exit, rpcResponse->{}", rpcResponse);
        return(rpcResponse);
    }

    //
    // Execute/Fulfill A PetasosActionableTask



    public RemoteProcedureCallResponse fulfillActionableTaskHandler(RemoteProcedureCallRequest rpcRequest){
        getLogger().debug(".fulfillActionableTaskHandler(): Entry, rpcRequest->{}", rpcRequest);
        PetasosActionableTask taskToAction = null;
        JGroupsIntegrationPointSummary endpointIdentifier = null;
        if(rpcRequest != null){
            if(rpcRequest.hasRequestContent()){
                if(rpcRequest.hasRequestContentType()){
                    if(rpcRequest.getRequestContentType().equals(PetasosActionableTask.class)){
                        taskToAction = (PetasosActionableTask) rpcRequest.getRequestContent();
                    }
                }
            }
            if(rpcRequest.hasRequestingEndpoint()){
                endpointIdentifier = rpcRequest.getRequestingEndpoint();
            }
        }
        PetasosActionableTask updatedTask = null;
        if((taskToAction != null) && (endpointIdentifier != null)) {
            updatedTask = fulfillActionableTask(taskToAction);
        }
        RemoteProcedureCallResponse rpcResponse = new RemoteProcedureCallResponse();
        rpcResponse.setAssociatedRequestID(rpcRequest.getRequestID());
        rpcResponse.setInScope(true);
        rpcResponse.setInstantCompleted(Instant.now());
        if(updatedTask != null){
            rpcResponse.setResponseContent(updatedTask);
            rpcResponse.setResponseContentType(PetasosActionableTask.class);
            rpcResponse.setSuccessful(true);
        } else {
            rpcResponse.setSuccessful(false);
        }
        getLogger().debug(".fulfillActionableTaskHandler(): Exit, rpcResponse->{}", rpcResponse);
        return(rpcResponse);
    }

    public RemoteProcedureCallResponse updateActionableTaskHandler(RemoteProcedureCallRequest rpcRequest ){
        getLogger().debug(".updateActionableTaskHandler(): Entry, rpcRequest->{}", rpcRequest);
        PetasosActionableTask taskToRegister = null;
        JGroupsIntegrationPointSummary endpointIdentifier = null;
        if(rpcRequest != null){
            if(rpcRequest.hasRequestContent()){
                if(rpcRequest.hasRequestContentType()){
                    if(rpcRequest.getRequestContentType().equals(PetasosActionableTask.class)){
                        taskToRegister = (PetasosActionableTask) rpcRequest.getRequestContent();
                    }
                }
            }
            if(rpcRequest.hasRequestingEndpoint()){
                endpointIdentifier = rpcRequest.getRequestingEndpoint();
            }
        }
        PetasosActionableTask updatedTask = null;
        if((taskToRegister != null) && (endpointIdentifier != null)) {
            updatedTask = updateActionableTask(taskToRegister);
        }
        RemoteProcedureCallResponse rpcResponse = new RemoteProcedureCallResponse();
        rpcResponse.setAssociatedRequestID(rpcRequest.getRequestID());
        rpcResponse.setInScope(true);
        rpcResponse.setInstantCompleted(Instant.now());
        if(updatedTask != null){
            rpcResponse.setResponseContent(updatedTask);
            rpcResponse.setResponseContentType(PetasosActionableTask.class);
            rpcResponse.setSuccessful(true);
        } else {
            rpcResponse.setSuccessful(false);
        }
        getLogger().debug(".updateActionableTaskHandler(): Exit, rpcResponse->{}", rpcResponse);
        return(rpcResponse);
    }

    public RemoteProcedureCallResponse retrievePendingActionableTasksHandler(RemoteProcedureCallRequest rpcRequest ){
        getLogger().debug(".retrievePendingActionableTasksHandler(): Entry, rpcRequest->{}", rpcRequest);
        PetasosParticipantId participantId = null;
        JGroupsIntegrationPointSummary endpointIdentifier = null;
        if(rpcRequest != null){
            if(rpcRequest.hasRequestContent()){
                if(rpcRequest.hasRequestContentType()){
                    if(rpcRequest.getRequestContentType().equals(PetasosActionableTask.class)){
                        participantId = (PetasosParticipantId) rpcRequest.getRequestContent();
                    }
                }
            }
            if(rpcRequest.hasRequestingEndpoint()){
                endpointIdentifier = rpcRequest.getRequestingEndpoint();
            }
        }
        // Get List
        PetasosActionableTaskSet petasosActionableTaskSet = retrievePendingActionableTasks(participantId);
        // Do Response
        RemoteProcedureCallResponse rpcResponse = new RemoteProcedureCallResponse();
        rpcResponse.setAssociatedRequestID(rpcRequest.getRequestID());
        rpcResponse.setInScope(true);
        rpcResponse.setInstantCompleted(Instant.now());
        if(petasosActionableTaskSet != null){
            rpcResponse.setResponseContent(petasosActionableTaskSet);
            rpcResponse.setResponseContentType(PetasosActionableTaskSet.class);
            rpcResponse.setSuccessful(true);
        } else {
            rpcResponse.setSuccessful(false);
        }
        getLogger().debug(".retrievePendingActionableTasksHandler(): Exit, rpcResponse->{}", rpcResponse);
        return(rpcResponse);
    }

    @Override
    public PetasosActionableTask fulfillActionableTask(PetasosActionableTask actionableTask) {
        return null;
    }

    //
    // Local (Server) Business Methods
    //

    @Override
    public PetasosActionableTask registerActionableTask(PetasosActionableTask actionableTask) {
        getLogger().debug(".registerActionableTask(): Entry, actionableTask->{}, integrationPoint->{}", actionableTask);
        PetasosActionableTaskRegistrationType petasosActionableTaskRegistration = getActionableTaskCache().registerPetasosActionableTask(actionableTask);
        actionableTask.setRegistered(petasosActionableTaskRegistration.getRegistrationInstant()!= null);
        getLogger().debug(".registerActionableTask(): Exit, actionableTask->{}", actionableTask);
        return(actionableTask);
    }

    @Override
    public PetasosActionableTask updateActionableTask(PetasosActionableTask actionableTask) {
        getLogger().debug(".updateActionableTask(): Entry, actionableTask->{}, integrationPoint->{}", actionableTask);
        PetasosActionableTaskRegistrationType petasosActionableTaskRegistration = getActionableTaskCache().updatePetasosActionableTask(actionableTask);
        PetasosActionableTask updatedActionableTask = getActionableTaskCache().getPetasosActionableTask(actionableTask.getTaskId());
        getLogger().debug(".updateActionableTask(): Exit, updatedActionableTask->{}", updatedActionableTask);
        return(updatedActionableTask);
    }

    @Override
    public PetasosActionableTaskSet retrievePendingActionableTasks(PetasosParticipantId participantId) {
        getLogger().debug(".retrievePendingActionableTasks(): Entry, participantId->{}", participantId);
        if (participantId == null) {
            getLogger().debug(".retrievePendingActionableTasks(): Exit, participantId is null, returning empty list");
            return (new PetasosActionableTaskSet());
        }
        PetasosActionableTaskSet waitingActionableTasksForComponent = getActionableTaskCache().getPendingActionableTasks(participantId);
        getLogger().info(".retrievePendingActionableTasks(): Exit");
        return(waitingActionableTasksForComponent);
    }


}
