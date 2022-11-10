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
package net.fhirfactory.pegacorn.services.tasks.transforms.fromfhir;

import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosAggregateTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.provenance.transformers.FHIRProvenanceToPetasosTaskJourneyTransformer;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.factories.TaskExtensionSystemFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.factories.TaskStatusReasonFactory;
import org.hl7.fhir.r4.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PetasosAggregateTaskFromFHIRTask extends PetasosTaskFromFHIRTask {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosAggregateTaskFromFHIRTask.class);

    @Inject
    private TaskStatusReasonFactory taskStatusReasonFactory1;

    @Inject
    private TaskExtensionSystemFactory taskExtensionSystems;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private FHIRProvenanceToPetasosTaskJourneyTransformer provenanceToTaskJourneyTransformer;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    protected PetasosTask createEmptyPetasosTaskSubclassObject() {
        PetasosActionableTask task = new PetasosActionableTask();
        return (task);
    }

    @Override
    protected TaskTypeTypeEnum getTaskType() {
        return (TaskTypeTypeEnum.PETASOS_AGGREGATE_TASK_TYPE);
    }

    public PetasosAggregateTask transformFHIRTaskIntoPetasosAggregateTask(Task task){
        getLogger().debug(".transformFHIRTaskIntoPetasosActionableTask(): Entry");
        if(task == null){
            getLogger().debug(".transformFHIRTaskIntoPetasosActionableTask(): Exit, task is null");
            return(null);
        }
        PetasosAggregateTask aggregateTask = (PetasosAggregateTask) newPetasosTaskFromTask(task);

        getLogger().debug(".transformFHIRTaskIntoPetasosActionableTask(): Exit");
        return(aggregateTask);
    }
}

