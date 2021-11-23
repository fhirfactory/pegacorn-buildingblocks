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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.global;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.processingplant.LocalPetasosFulfillmentTaskDM;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.shared.SharedActionableTaskDM;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.shared.SharedTaskJobCardDM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class GlobalPetasosTaskExecutionController {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalPetasosTaskExecutionController.class);

    private boolean initialised;

    @Inject
    private SharedActionableTaskDM actionableTaskDM;

    @Inject
    private SharedTaskJobCardDM taskJobCardDM;

    @Inject
    private LocalPetasosFulfillmentTaskDM fulfillmentTaskDM;

    //
    // Constructor(s)
    //

    public GlobalPetasosTaskExecutionController(){
        this.initialised = false;
    }

    //
    // Post Construct
    //

    @PostConstruct
    public void initialise(){

    }

    //
    // Business Methods
    //

    public PetasosTaskJobCard requestTaskExecutionPrivilege(PetasosTaskJobCard jobcard){
        getLogger().debug(".requestTaskExecutionPrivilege(): Entry, jobcard->{}", jobcard);
        if(jobcard == null){
            getLogger().debug(".requestTaskExecutionPrivilege(): Entry, jobcard is empty, returning null");
            return(null);
        }
        synchronized (jobcard.getUpdateLock()){
            PetasosTaskJobCard executingFulfillmentTaskJobCard = taskJobCardDM.getExecutingFulfillmentTaskJobCard(jobcard.getActionableTaskIdentifier());
            if(executingFulfillmentTaskJobCard == null){
                PetasosTaskJobCard globalCacheJobCard = taskJobCardDM.getJobCardForFulfillmentTask(jobcard.getFulfillmentTaskIdentifier());
                PetasosActionableTask actionableTask = actionableTaskDM.getActionableTask(jobcard.getActionableTaskIdentifier());
                synchronized(globalCacheJobCard.getUpdateLock()){
                    if(actionableTask != null){
                        //
                        // If no one else is executing, and this jobcard has node affinity with the ActionableTask, then grant execution privileges
                        if(actionableTask.hasTaskNodeAffinity()){
                            if(actionableTask.getTaskNodeAffinity().equals(jobcard.getProcessingPlant())){
                                globalCacheJobCard.setGrantedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
                                jobcard.setGrantedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
                            }
                        } else {
                            //
                            // If no one else is executing, but this jobcard does not have node affinity with the ActionableTask, then wait
                            // "n" seconds begore granting it... This allows time for the synchronisation of state across all the
                            // WUPs/ProcessingPlants associated within the ActionableTask.
                            Long timeSinceRegistrationOrLastCheck = Instant.now().getEpochSecond() - jobcard.getCoordinatorUpdateInstant().getEpochSecond();
                            if (timeSinceRegistrationOrLastCheck > PetasosPropertyConstants.PETASOS_DISTRIBUTED_CACHE_SYNCHRONISATION_WAIT) {
                                globalCacheJobCard.setGrantedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
                                jobcard.setGrantedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
                            }
                        }
                    }
                }
            }
        }
        return (jobcard);
    }

    public PetasosTaskJobCard reportTaskExecutionStart(PetasosTaskJobCard jobcard){

        return(jobcard);
    }

    public PetasosTaskJobCard reportTaskExecutionFinish(PetasosTaskJobCard jobcard){

        return(jobcard);
    }

    public PetasosTaskJobCard reportTaskExecutionFailure(PetasosTaskJobCard jobcard){

        return(jobcard);
    }

    public PetasosTaskJobCard reportTaskCancellation(PetasosTaskJobCard jobcard){

        return(jobcard);
    }


    //
    // Getters (and Setters)
    //

    public boolean isInitialised() {
        return initialised;
    }

    protected SharedActionableTaskDM getActionableTaskDM() {
        return actionableTaskDM;
    }

    protected SharedTaskJobCardDM getTaskJobCardDM() {
        return taskJobCardDM;
    }

    protected LocalPetasosFulfillmentTaskDM getFulfillmentTaskDM() {
        return fulfillmentTaskDM;
    }

    protected Logger getLogger(){
        return(LOG);
    }
}
