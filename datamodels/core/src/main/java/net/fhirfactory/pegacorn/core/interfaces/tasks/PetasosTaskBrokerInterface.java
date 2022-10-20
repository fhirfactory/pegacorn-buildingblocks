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
package net.fhirfactory.pegacorn.core.interfaces.tasks;

import net.fhirfactory.pegacorn.core.model.petasos.jobcard.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.collections.PetasosActionableTaskSet;
import net.fhirfactory.pegacorn.core.model.petasos.task.collections.PetasosTaskIdSet;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;

public interface PetasosTaskBrokerInterface {

    public PetasosTaskJobCard registerTask(PetasosActionableTask actionableTask, PetasosTaskJobCard jobCard);
    public PetasosTaskJobCard registerTaskOutcome(PetasosActionableTask actionableTask, PetasosTaskJobCard jobCard);

    public PetasosTaskJobCard registerTaskWaiting(PetasosTaskJobCard jobCard);
    public PetasosTaskJobCard registerTaskStart(PetasosTaskJobCard jobCard);
    public PetasosTaskJobCard registerTaskFailure(PetasosTaskJobCard jobCard);
    public PetasosTaskJobCard registerTaskFinish(PetasosTaskJobCard jobCard);
    public PetasosTaskJobCard registerTaskCancellation(PetasosTaskJobCard jobCard);
    public PetasosTaskJobCard registerTaskFinalisation(PetasosTaskJobCard jobCard);

    public PetasosActionableTaskSet getOffloadedPendingTasks(PetasosParticipantId participantId, Integer maxNumber);
    public PetasosActionableTask getOffloadedPendingTask(PetasosParticipantId participantId, TaskIdType additionalPendingTaskId);
    public Boolean hasOffloadedPendingTasks(PetasosParticipantId participantId);
    public Integer offloadPendingTasks(PetasosParticipantId participantId, PetasosTaskIdSet tasksToBeOffloaded);
    public PetasosTaskIdSet synchronisePendingTasks(PetasosParticipantId participantId, PetasosTaskIdSet localPendingTaskSet);

}
