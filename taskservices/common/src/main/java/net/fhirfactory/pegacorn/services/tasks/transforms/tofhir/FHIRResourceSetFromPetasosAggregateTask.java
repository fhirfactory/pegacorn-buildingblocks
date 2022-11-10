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
package net.fhirfactory.pegacorn.services.tasks.transforms.tofhir;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosAggregateTask;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FHIRResourceSetFromPetasosAggregateTask {
    private static final Logger LOG = LoggerFactory.getLogger(FHIRResourceSetFromPetasosAggregateTask.class);

    @Inject
    private FHIRProvenanceFromPetasosTaskJourney provenanceFromPetasosTaskJourney;

    @Inject
    private FHIRTaskFromPetasosAggregateTask taskFromPetasosAggregateTask;

    @Inject
    private ProcessingPlantInterface processingPlant;


    public List<Resource> transformTask(PetasosAggregateTask actionableTask){
        getLogger().debug(".newTask(): Entry, actionableTask->{}", actionableTask);
        List<Resource> resourceList = new ArrayList<>();

        //
        // Build the FHIR::Task
        Task fhirTask = taskFromPetasosAggregateTask.newTaskFromPetasosTask(actionableTask);
        if(fhirTask != null){
            resourceList.add(fhirTask);
        }
        //
        // Now the FHIR::Provenance

        Provenance fhirProvenance = provenanceFromPetasosTaskJourney.newProvenanceFromTaskJourney(processingPlant.getMeAsASoftwareComponent().getComponentID(), fhirTask.getIdentifierFirstRep(), actionableTask.getTaskTraceability());
        if(fhirProvenance != null){
            resourceList.add(fhirProvenance);
        }
        //
        // And done...
        getLogger().debug(".newTask(): Exit, resourceList->{}", resourceList);
        return(resourceList);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
