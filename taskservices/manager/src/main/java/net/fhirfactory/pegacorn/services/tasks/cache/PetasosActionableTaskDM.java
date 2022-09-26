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
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTaskSet;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.services.tasks.datatypes.PetasosActionableTaskRegistrationType;
import org.slf4j.Logger;

import java.util.List;

public abstract class PetasosActionableTaskDM {

    //
    // Constructor(s)
    //

    public PetasosActionableTaskDM() {
    }

    //
    // Abstract Methods
    //

    abstract protected Logger specifyLogger();

    abstract public PetasosActionableTaskRegistrationType registerPetasosActionableTask(PetasosActionableTask actionableTask);

    abstract public PetasosActionableTaskRegistrationType updatePetasosActionableTask(PetasosActionableTask actionableTask);

    abstract public PetasosActionableTask getPetasosActionableTask(TaskIdType taskId);

    abstract public List<PetasosActionableTask> getPetasosActionableTasksForComponent(ComponentIdType componentId);

    abstract public PetasosActionableTaskSet getPendingActionableTasks(PetasosParticipantId participantId);

    abstract public boolean archivePetasosActionableTask(PetasosActionableTask actionableTask);

    abstract public boolean archivePetasosActionableTask(TaskIdType taskId);

    //
    // Getters (and Setters)
    //

    protected Logger getLogger() {
        return (specifyLogger());
    }

    //
    // Business Methods
    //

}


