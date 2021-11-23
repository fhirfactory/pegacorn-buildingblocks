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

import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.tasks.PetasosTaskRepositoryServiceProviderNameInterface;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.petasos.core.tasks.caches.shared.SharedActionableTaskDM;
import org.apache.camel.CamelContext;
import org.hl7.fhir.r4.model.SearchParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class LocalPetasosActionableTaskActivityController {
    private static final Logger LOG = LoggerFactory.getLogger(LocalPetasosActionableTaskActivityController.class);

    @Inject
    CamelContext camelctx;

    @Inject
    private PetasosTaskBrokerInterface taskRepositoryService;

    @Inject
    private SharedActionableTaskDM sharedActionableTaskDM;

    @Inject
    private PetasosTaskRepositoryServiceProviderNameInterface taskRepositoryServiceProviderNameInterface;


    //
    // Constructor(s)
    //


    //
    // Post Construct
    //


    //
    // Task Registration/Deregistration
    //

    public PetasosActionableTask registerActionableTask(PetasosActionableTask actionableTask){
        getSharedActionableTaskDM().registerActionableTask(actionableTask);
        getTaskRepositoryService().registerActionableTask(taskRepositoryServiceProviderNameInterface.getPetasosTaskRepositoryServiceProviderName(), actionableTask);
        return(actionableTask);
    }

    public PetasosActionableTask deregisterActionableTask(PetasosActionableTask actionableTask){

        return(actionableTask);
    }

    //
    // Notifications
    //

    public Instant notifyActionTaskExecutionStart(TaskIdType taskId){

        Instant updateInstant = Instant.now();
        return(updateInstant);
    }

    public Instant notifyActionableTaskExecutionFinish(TaskIdType taskId){

        Instant updateInstant = Instant.now();
        return(updateInstant);
    }

    public Instant notifyActionableTaskExecutionFailure(TaskIdType taskId){

        Instant updateInstant = Instant.now();
        return(updateInstant);
    }

    public Instant notifyActionableTaskExecutionCancellation(TaskIdType taskId){

        Instant updateInstant = Instant.now();
        return(updateInstant);
    }

    //
    // Getters (and Setters)
    //

    protected PetasosTaskBrokerInterface getTaskRepositoryService(){
        return(taskRepositoryService);
    }

    protected Logger getLogger(){
        return(LOG);
    }

    protected SharedActionableTaskDM getSharedActionableTaskDM(){
        return(sharedActionableTaskDM);
    }
}
