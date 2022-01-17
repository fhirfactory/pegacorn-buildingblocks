/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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

package net.fhirfactory.pegacorn.petasos.core.tasks.management.local;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.FulfillmentTrackingIdType;
import net.fhirfactory.pegacorn.petasos.audit.brokers.PetasosFulfillmentTaskAuditServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.processingplant.LocalPetasosFulfillmentTaskDM;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.shared.SharedTaskJobCardDM;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosTaskJobCardFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.global.GlobalPetasosTaskExecutionController;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

/**
 * @author Mark A. Hunter
 */
@ApplicationScoped
public class LocalPetasosFulfilmentTaskActivityController {
    private static final Logger LOG = LoggerFactory.getLogger(LocalPetasosFulfilmentTaskActivityController.class);

    @Inject
    private LocalPetasosFulfillmentTaskDM fulfillmentTaskDM;

    @Inject
    private SharedTaskJobCardDM sharedTaskJobCardDM;

    @Inject
    private PetasosFulfillmentTaskAuditServicesBroker auditServicesBroker;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private GlobalPetasosTaskExecutionController taskExecutionController;

    @Inject
    private PetasosTaskJobCardFactory jobCardFactory;

    //
    // PetasosFulfillmentTask Registration
    //

    public PetasosFulfillmentTask registerFulfillmentTask(PetasosFulfillmentTask task, boolean synchronousWriteToAudit){
        getLogger().debug(".registerFulfillmentTask(): Entry, fulfillmentTask->{}", task);
        PetasosTaskJobCard petasosTaskJobCard = null;
        if(task.hasTaskJobCard()) {
            petasosTaskJobCard = task.getTaskJobCard();
        } else {
            petasosTaskJobCard = jobCardFactory.newPetasosTaskJobCard(task);
            petasosTaskJobCard.setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_WAITING);
            petasosTaskJobCard.setGrantedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_WAITING);
            petasosTaskJobCard.setLocalFulfillmentStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_UNREGISTERED);
            petasosTaskJobCard.setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_WAITING);
            petasosTaskJobCard.setToBeDiscarded(false);
            petasosTaskJobCard.setLocalUpdateInstant(Instant.now());
            synchronized (task.getTaskJobCardLock()) {
                task.setTaskJobCard(petasosTaskJobCard);
            }
        }
        //
        // Register the Fulfillment Task
        fulfillmentTaskDM.registerFulfillmentTask(task);
        //
        // Register the Task Job Card
        PetasosTaskJobCard sharedJobCard = SerializationUtils.clone(petasosTaskJobCard);
        sharedTaskJobCardDM.registerJobCard(sharedJobCard);
        //
        // Request Execution Privileges
        taskExecutionController.requestTaskExecutionPrivilege(task.getTaskJobCard());
        getLogger().debug(".registerFulfillmentTask(): Exit, fulfillmentTask->{}", task);
        return(task);
    }

    public void deregisterFulfillmentTask(PetasosFulfillmentTask task){
        getLogger().debug(".deregisterFulfillmentTask(): Entry, task->{}", task);
        fulfillmentTaskDM.removeFulfillmentTask(task.getTaskId());
        getLogger().debug(".deregisterFulfillmentTask(): Exit");
    }

    public void deregisterFulfillmentTask(TaskIdType taskId){
        fulfillmentTaskDM.removeFulfillmentTask(taskId);
    }

    //
    // Requests
    //

    public Instant requestFulfillmentTaskExecutionPrivilege(PetasosTaskJobCard localJobCard){
        getLogger().debug(".requestFulfillmentTaskExecutionPrivilege(): Entry, localJobCard->{}", localJobCard);
        if(getLogger().isInfoEnabled()){
            getLogger().info(".requestFulfillmentTaskExecutionPrivilege(): Entry, WUP Component Id->{}", localJobCard.getWorkUnitProcessor().getDisplayName());
        }
        PetasosTaskJobCard petasosTaskJobCard = taskExecutionController.requestTaskExecutionPrivilege(localJobCard);
        Instant updateInstant = Instant.now();
        return(updateInstant);
    }

    //
    // Notifications
    //

    public Instant notifyFulfillmentTaskExecutionStart(PetasosTaskJobCard localJobCard){
        getLogger().debug(".notifyFulfillmentTaskExecutionStart(): Entry, localJobCard->{}", localJobCard);
        PetasosTaskJobCard petasosTaskJobCard = taskExecutionController.reportTaskExecutionStart(localJobCard);
        Instant updateInstant = Instant.now();
        getLogger().debug(".notifyFulfillmentTaskExecutionStart(): Exit, localJobCard->{}", localJobCard);
        return(updateInstant);
    }

    public Instant notifyFulfillmentTaskExecutionFinish(PetasosTaskJobCard localJobCard){
        getLogger().debug(".notifyFulfillmentTaskExecutionFinish(): Entry, localJobCard->{}", localJobCard);
        PetasosTaskJobCard petasosTaskJobCard = taskExecutionController.reportTaskExecutionFinish(localJobCard);
        Instant updateInstant = Instant.now();
        getLogger().debug(".notifyFulfillmentTaskExecutionFinish(): Exit, localJobCard->{}", localJobCard);
        return(updateInstant);
    }

    public Instant notifyFulfillmentTaskExecutionFailure(PetasosTaskJobCard localJobCard){
        getLogger().debug(".notifyFulfillmentTaskExecutionFailure(): Entry, localJobCard->{}", localJobCard);
        PetasosTaskJobCard petasosTaskJobCard = taskExecutionController.reportTaskExecutionFailure(localJobCard);
        Instant updateInstant = Instant.now();
        getLogger().debug(".notifyFulfillmentTaskExecutionFailure(): Exit, localJobCard->{}", localJobCard);
        return(updateInstant);
    }

    public Instant notifyFulfillmentTaskExecutionCancellation(PetasosTaskJobCard localJobCard){
        getLogger().debug(".notifyFulfillmentTaskExecutionCancellation(): Entry, localJobCard->{}", localJobCard);
        PetasosTaskJobCard petasosTaskJobCard = taskExecutionController.reportTaskCancellation(localJobCard);
        Instant updateInstant = Instant.now();
        getLogger().debug(".notifyFulfillmentTaskExecutionCancellation(): Exit, localJobCard->{}", localJobCard);
        return(updateInstant);
    }

    public Instant notifyFulfillmentTaskExecutionNoActionRequired(PetasosTaskJobCard localJobCard){

        Instant updateInstant = Instant.now();
        return(updateInstant);
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
