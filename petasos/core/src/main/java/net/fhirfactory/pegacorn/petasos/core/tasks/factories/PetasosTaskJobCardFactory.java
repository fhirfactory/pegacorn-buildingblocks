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
package net.fhirfactory.pegacorn.petasos.core.tasks.factories;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.petasos.jobcard.datatypes.PetasosTaskFulfillmentCard;
import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.jobcard.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskExecutionCommandEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class PetasosTaskJobCardFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosTaskJobCardFactory.class);

    @Inject
    private ProcessingPlantInterface processingPlant;

    public PetasosTaskJobCard newTaskJobCard(PetasosFulfillmentTask fulfillmentTask){
        getLogger().debug(".newPetasosTaskJobCard(): Entry, fulfillmentTask->{}", fulfillmentTask);
        PetasosTaskJobCard jobCard = new PetasosTaskJobCard();
        jobCard.setTaskId(fulfillmentTask.getActionableTaskId());
        jobCard.setClusterMode(processingPlant.getTopologyNode().getConcurrencyMode());
        jobCard.setUpdateInstant(Instant.EPOCH);
        jobCard.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
        jobCard.setTaskFulfillmentCard(new PetasosTaskFulfillmentCard());
        jobCard.getTaskFulfillmentCard().setFulfillmentTaskId(fulfillmentTask.getTaskId());
        jobCard.getTaskFulfillmentCard().setFulfillerParticipantId(fulfillmentTask.getTaskFulfillment().getFulfiller().getParticipant().getParticipantId());
        jobCard.getTaskFulfillmentCard().setFulfillmentExecutionStatus(fulfillmentTask.getTaskFulfillment().getStatus());
        jobCard.setSystemMode(processingPlant.getTopologyNode().getResilienceMode());
        jobCard.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
        jobCard.setUpdateInstant(Instant.now());
        getLogger().debug(".newPetasosTaskJobCard(): Exit, jobCard->{}", jobCard);
        return(jobCard);
    }

    public PetasosTaskJobCard newTaskJobCard(TaskIdType actionableTaskId, PetasosParticipantId participantId){
        PetasosTaskJobCard jobCard = new PetasosTaskJobCard();
        jobCard.setTaskId(actionableTaskId);
        jobCard.setClusterMode(processingPlant.getTopologyNode().getConcurrencyMode());
        jobCard.setSystemMode(processingPlant.getTopologyNode().getResilienceMode());
        jobCard.setAffinityNode(processingPlant.getTopologyNode().getComponentId());
        jobCard.setSystemMode(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_STANDALONE);
        jobCard.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
        jobCard.setUpdateInstant(Instant.EPOCH);
        jobCard.setTaskFulfillmentCard(new PetasosTaskFulfillmentCard());
        jobCard.getTaskFulfillmentCard().setFulfillerParticipantId(participantId);
        jobCard.getTaskFulfillmentCard().setFulfillmentExecutionStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED);
        return(jobCard);
    }

    public PetasosTaskJobCard newTaskJobCard(TaskIdType actionableTaskId, ProcessingPlantInterface processingPlant){
        PetasosTaskJobCard jobCard = new PetasosTaskJobCard();
        jobCard.setTaskId(actionableTaskId);
        jobCard.setClusterMode(processingPlant.getTopologyNode().getConcurrencyMode());
        jobCard.setSystemMode(processingPlant.getTopologyNode().getResilienceMode());
        jobCard.setAffinityNode(processingPlant.getTopologyNode().getComponentId());
        jobCard.setSystemMode(ResilienceModeEnum.RESILIENCE_MODE_KUBERNETES_STANDALONE);
        jobCard.setCurrentStatus(TaskExecutionCommandEnum.TASK_COMMAND_WAIT);
        return(jobCard);
    }


    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
