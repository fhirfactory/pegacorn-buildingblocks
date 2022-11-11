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
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.context.TaskContextType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemType;
import net.fhirfactory.pegacorn.services.tasks.metadata.fromhl7v2x.FHIRTaskMetadataResourceSetFromHL7v2x;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class FHIRResourceSetFromPetasosActionableTask {
    private static final Logger LOG = LoggerFactory.getLogger(FHIRResourceSetFromPetasosActionableTask.class);

    @Inject
    private FHIRProvenanceFromPetasosTaskJourney provenanceFromPetasosTaskJourney;

    @Inject
    private FHIRTaskFromPetasosActionableTask taskFromPetasosActionableTask;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private FHIRTaskMetadataResourceSetFromHL7v2x metadataResourceSetFromHL7v2x;


    public List<Resource> transformTask(PetasosActionableTask actionableTask){
        getLogger().debug(".newTask(): Entry, actionableTask->{}", actionableTask);
        List<Resource> resourceList = new ArrayList<>();

        //
        // Build the FHIR::Task
        Task fhirTask = taskFromPetasosActionableTask.newTaskFromPetasosTask(actionableTask);
        resourceList.add(fhirTask);
        //
        // Now the FHIR::Provenance
        Provenance fhirProvenance = provenanceFromPetasosTaskJourney.newProvenanceFromTaskJourney(processingPlant.getMeAsASoftwareComponent().getComponentID(), fhirTask.getIdentifierFirstRep(), actionableTask.getTaskTraceability());
        resourceList.add(fhirProvenance);
        
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
