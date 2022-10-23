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

import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.provenance.transformers.FHIRProvenanceToPetasosTaskJourneyTransformer;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.factories.TaskExtensionSystemFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.factories.TaskStatusReasonFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.valuesets.TaskExtensionSystemEnum;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class PetasosActionableTaskFromFHIRTask extends PetasosTaskFromFHIRTask {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosActionableTaskFromFHIRTask.class);

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
        return (TaskTypeTypeEnum.PETASOS_ACTIONABLE_TASK_TYPE);
    }

    public PetasosActionableTask transformFHIRTaskIntoPetasosActionableTask(Task task){
        getLogger().debug(".transformFHIRTaskIntoPetasosActionableTask(): Entry");
        if(task == null){
            getLogger().debug(".transformFHIRTaskIntoPetasosActionableTask(): Exit, task is null");
            return(null);
        }
        PetasosActionableTask actionableTask = (PetasosActionableTask) newPetasosTaskFromTask(task);

        //
        // Add FulfillmentStatus details
        TaskFulfillmentType taskFulfillmentStatus = transformFHIRTaskToFulfillmentType(task);
        if(taskFulfillmentStatus != null){
            actionableTask.setTaskFulfillment(taskFulfillmentStatus);
        }

        getLogger().debug(".transformFHIRTaskIntoPetasosActionableTask(): Exit");
        return(actionableTask);
    }



    protected TaskFulfillmentType transformFHIRTaskToFulfillmentType(Task fhirTask){
        getLogger().debug(".transformFHIRTaskToFulfillmentExecutionStatus(): Entry");
        if(fhirTask == null){
            getLogger().debug(".transformFHIRTaskToFulfillmentExecutionStatus(): Exit, fhirTask is null");
            return(null);
        }
        TaskFulfillmentType taskFulfillmentReport = new TaskFulfillmentType();
        taskFulfillmentReport.setLastCheckedInstant(Instant.now());
        if(fhirTask.hasBusinessStatus()) {
            String code = fhirTask.getBusinessStatus().getCodingFirstRep().getCode();
            FulfillmentExecutionStatusEnum fulfillmentExecutionStatus = FulfillmentExecutionStatusEnum.fromToken(code);
            taskFulfillmentReport.setStatus(fulfillmentExecutionStatus);
        }
        if(fhirTask.hasOwner()){
            ComponentIdType componentId = new ComponentIdType();
            String identifierValue = fhirTask.getOwner().getIdentifier().getValue();
            componentId.setId(identifierValue);
            if(fhirTask.getOwner().getIdentifier().hasPeriod()){
                Period identifierPeriod = fhirTask.getOwner().getIdentifier().getPeriod();
                if(identifierPeriod.hasStart()){
                    componentId.setIdValidityStartInstant(identifierPeriod.getStart().toInstant());
                }
                if(identifierPeriod.hasEnd()){
                    componentId.setIdValidityEndInstant(identifierPeriod.getEnd().toInstant());
                }
            }
            SoftwareComponent node = topologyIM.getNode(componentId);
            taskFulfillmentReport.setFulfiller(node);
        }
        if(fhirTask.hasExecutionPeriod()){
            if(fhirTask.getExecutionPeriod().hasStart()){
                taskFulfillmentReport.setStartDate(fhirTask.getExecutionPeriod().getStart());
            }
            if(fhirTask.getExecutionPeriod().hasEnd()){
                taskFulfillmentReport.setFinishedDate(fhirTask.getExecutionPeriod().getEnd());
            }
            if(fhirTask.getExecutionPeriod().hasExtension(taskExtensionSystems.getDricatsTaskExtensionSystemURL(TaskExtensionSystemEnum.TASK_REGISTRATON_INSTANT_EXTENSION_URL))){
                Extension registrationExtension = fhirTask.getExecutionPeriod().getExtensionByUrl(taskExtensionSystems.getDricatsTaskExtensionSystemURL(TaskExtensionSystemEnum.TASK_REGISTRATON_INSTANT_EXTENSION_URL));
                InstantType registrationInstantType = (InstantType) registrationExtension.getValue();
                Date registrationDate = registrationInstantType.getValue();
                taskFulfillmentReport.setRegistrationDate(registrationDate);
            }
            if(fhirTask.getExecutionPeriod().hasExtension(taskExtensionSystems.getDricatsTaskExtensionSystemURL(TaskExtensionSystemEnum.TASK_FINALISATION_INSTANT_EXTENSION_URL))){
                Extension finalisationExtension = fhirTask.getExecutionPeriod().getExtensionByUrl(taskExtensionSystems.getDricatsTaskExtensionSystemURL(TaskExtensionSystemEnum.TASK_FINALISATION_INSTANT_EXTENSION_URL));
                InstantType finalisationInstantType = (InstantType) finalisationExtension.getValue();
                Date finalisationDate = finalisationInstantType.getValue();
                taskFulfillmentReport.setFinalisationDate(finalisationDate);
            }
        }
        getLogger().debug(".transformFHIRTaskToFulfillmentExecutionStatus(): Exit, taskFulfillmentReport->{}", taskFulfillmentReport);
        return(taskFulfillmentReport);
    }
}
