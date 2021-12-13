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

import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.services.tasks.cache.PetasosActionableTaskDM;
import net.fhirfactory.pegacorn.services.tasks.datatypes.PetasosActionableTaskRegistrationType;
import net.fhirfactory.pegacorn.services.tasks.distribution.PetasosTasksDistributionHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class PetasosTaskServicesManagerHandler extends PetasosTasksDistributionHandler {

    //
    // Constructor(s)
    //


    //
    // Abstract Methods
    //

    abstract protected PetasosActionableTaskDM specifyActionableTaskDM();

    //
    // Getters and Setters
    //

   protected PetasosActionableTaskDM getActionableTaskDM(){
       return(specifyActionableTaskDM());
   }

    //
    // Business Methods
    //

    @Override
    public PetasosActionableTask registerActionableTask(PetasosActionableTask actionableTask, PetasosEndpointIdentifier requesterEndpointIdentifier) {
        getLogger().debug(".registerActionableTask(): Entry, actionableTask->{}, requesterEndpointIdentifier->{}", actionableTask, requesterEndpointIdentifier);
        PetasosActionableTaskRegistrationType petasosActionableTaskRegistration = getActionableTaskDM().registerPetasosActionableTask(actionableTask, requesterEndpointIdentifier);
        actionableTask.setRegistered(petasosActionableTaskRegistration.getRegistrationInstant()!= null);
        getLogger().debug(".registerActionableTask(): Exit, actionableTask->{}", actionableTask);
        return(actionableTask);
    }

    @Override
    public PetasosActionableTask updateActionableTask(PetasosActionableTask actionableTask, PetasosEndpointIdentifier requesterEndpointIdentifier) {
        getLogger().debug(".updateActionableTask(): Entry, actionableTask->{}, requesterEndpointIdentifier->{}", actionableTask, requesterEndpointIdentifier);
        PetasosActionableTaskRegistrationType petasosActionableTaskRegistration = getActionableTaskDM().updatePetasosActionableTask(actionableTask, requesterEndpointIdentifier);
        PetasosActionableTask updatedActionableTask = getActionableTaskDM().getPetasosActionableTask(actionableTask.getTaskId());
        getLogger().debug(".updateActionableTask(): Exit, updatedActionableTask->{}", updatedActionableTask);
        return(updatedActionableTask);
    }

    @Override
    public List<PetasosActionableTask> retrievePendingActionableTasks(PetasosEndpointIdentifier requestorEndpointIdentifier) {
        getLogger().debug(".retrievePendingActionableTasks(): Entry, requestorEndpointIdentifier->{}", requestorEndpointIdentifier);
        List<PetasosActionableTask> taskList = new ArrayList<>();
        if (requestorEndpointIdentifier == null) {
            getLogger().debug(".retrievePendingActionableTasks(): Exit, requestorEndpointIdentifier is null, returning empty list");
            return (taskList);
        }
        List<PetasosActionableTask> waitingActionableTasksForComponent = getActionableTaskDM().getWaitingActionableTasksForComponent(requestorEndpointIdentifier.getProcessingPlantComponentID());
        taskList.addAll(waitingActionableTasksForComponent);
        getLogger().debug(".retrievePendingActionableTasks(): Exit");
        return(taskList);
    }
}
