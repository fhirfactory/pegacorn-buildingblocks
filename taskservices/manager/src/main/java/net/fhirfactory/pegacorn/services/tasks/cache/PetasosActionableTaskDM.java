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
package net.fhirfactory.pegacorn.services.tasks.cache;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.services.tasks.datatypes.PetasosActionableTaskRegistrationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PetasosActionableTaskDM {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosActionableTaskDM.class);

    private ConcurrentHashMap<TaskIdType, PetasosActionableTaskRegistrationType> taskRegistrationMap;
    private ConcurrentHashMap<ComponentIdType, List<TaskIdType>> componentToTaskMap;

    //
    // Constructor(s)
    //

    public PetasosActionableTaskDM(){
        this.taskRegistrationMap = new ConcurrentHashMap<>();
        this.componentToTaskMap = new ConcurrentHashMap<>();
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    public ConcurrentHashMap<TaskIdType, PetasosActionableTaskRegistrationType> getTaskRegistrationMap() {
        return taskRegistrationMap;
    }

    public ConcurrentHashMap<ComponentIdType, List<TaskIdType>> getComponentToTaskMap() {
        return componentToTaskMap;
    }

    //
    // Business Methods
    //

    public PetasosActionableTaskRegistrationType registerPetasosActionableTask(PetasosActionableTask actionableTask, PetasosEndpointIdentifier endpointIdentifier){


        return(null);
    }

    public PetasosActionableTaskRegistrationType updatePetasosActionableTask(PetasosActionableTask actionableTask, PetasosEndpointIdentifier endpointIdentifier){


        return(null);
    }

    public PetasosActionableTask getPetasosActionableTask(TaskIdType taskId){


        return(null);
    }

    public List<PetasosActionableTask> getPetasosActionableTasksForComponent(ComponentIdType componentId){


        return(null);
    }

    public List<PetasosActionableTask> getWaitingActionableTasksForComponent(ComponentIdType componentId){


        return(null);
    }

    public boolean deletePetasosActionableTask(PetasosActionableTask actionableTask){

        return(false);
    }

    public boolean deletePetasosActionableTask(TaskIdType taskId){

        return(false);
    }
}
