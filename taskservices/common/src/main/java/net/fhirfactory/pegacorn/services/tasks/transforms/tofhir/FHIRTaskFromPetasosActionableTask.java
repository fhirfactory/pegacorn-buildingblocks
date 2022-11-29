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

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.performer.datatypes.TaskPerformerTypeType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.datatypes.TaskFulfillmenExecutionStatusType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.ActionableTaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.factories.*;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.valuesets.TaskTransformConstants;
import org.apache.commons.lang3.SerializationUtils;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.codesystems.TaskCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class FHIRTaskFromPetasosActionableTask extends FHIRTaskFromPetasosTask {
    private static final Logger LOG = LoggerFactory.getLogger(FHIRTaskFromPetasosActionableTask.class);

    @Inject
    private TaskStatusReasonFactory statusReasonFactory;

    @Inject
    TaskPerformerTypeFactory performerTypeFactory;

    @Inject
    private PegacornIdentifierFactory identifierFactory;

    @Inject
    private TaskBusinessStatusFactory businessStatusFactory;

    @Inject
    private TaskTransformConstants taskTransformConstants;

    @Inject
    private TaskExtensionSystemFactory taskExtensionSystems;

    @Inject
    private TaskReasonFactory taskReasonFactory;

    @Inject
    private TaskPeriodFactory taskPeriodFactory;

    //
    // Constructor(s)
    //

    public FHIRTaskFromPetasosActionableTask(){
        super();
    }

    //
    // Implemented Abstract (Business) Methods
    //

    @Override
    protected Reference specifyBasedOn(PetasosTask petasosTask) {
        getLogger().debug(".specifyBasedOn(): Entry");
        PetasosActionableTask actionableTask = (PetasosActionableTask) petasosTask;
        //
        // Get the Trigger Event/Task ID
        TaskIdType triggerTaskId = null;
        if(actionableTask.hasTaskContext()){
            if(actionableTask.getTaskContext().hasTaskTriggerSummary()){
                if(actionableTask.getTaskContext().getTaskTriggerSummary().hasTriggerTaskId()){
                    triggerTaskId = actionableTask.getTaskContext().getTaskTriggerSummary().getTriggerTaskId();
                }
            }
        }
        if(triggerTaskId == null){
            getLogger().debug(".specifyBasedOn(): Exit, returning null (no trigger task id found");
            return(null);
        }
        Identifier triggerTaskIdentifier = getTaskIdentifierFactory().newTaskIdentifier(TaskTypeTypeEnum.PETASOS_ACTIONABLE_TASK_TYPE, triggerTaskId);
        Reference triggerTaskReference = new Reference();
        triggerTaskReference.setIdentifier(triggerTaskIdentifier);
        triggerTaskReference.setDisplay(triggerTaskId.getLocalId());
        triggerTaskReference.setType(ResourceType.Task.toString());
        getLogger().debug(".specifyBasedOn(): Exit, triggerTaskReference->{}", triggerTaskReference);
        return (triggerTaskReference);
    }

    @Override
    protected Identifier specifyGroupIdentifier(PetasosTask petasosTask) {
        // TODO Implement Group Identifier functionality for PetasosActionableTasks
        return null;
    }

    @Override
    protected List<Reference> specifyPartOf(PetasosTask petasosTask) {
        getLogger().debug(".specifyPartOf(): Entry");
        PetasosActionableTask actionableTask = (PetasosActionableTask) petasosTask;
        //
        // Create List<Reference> for Part Of (Aggregate Task) Membership
        List<Reference> membershipList = new ArrayList<>();
        //
        // Get the Aggregate Task Id if it exists
        TaskIdType aggregateTaskId = null;
        if(actionableTask.hasAggregateTaskMembership()){
            if(!actionableTask.getAggregateTaskMembership().isEmpty()){
                for(TaskIdType currentAggregateTaskId: actionableTask.getAggregateTaskMembership()){
                    Identifier aggregateTaskIdentifer = getTaskIdentifierFactory().newTaskIdentifier(TaskTypeTypeEnum.PETASOS_AGGREGATE_TASK_TYPE, currentAggregateTaskId);
                    Reference aggregateTaskReference = new Reference();
                    aggregateTaskReference.setIdentifier(aggregateTaskIdentifer);
                    aggregateTaskReference.setDisplay(aggregateTaskId.getLocalId());
                    aggregateTaskReference.setType(ResourceType.Task.toString());
                    membershipList.add(aggregateTaskReference);
                }
            }
        }
        getLogger().debug(".specifyPartOf(): Exit");
        return (membershipList);
    }

    @Override
    protected Task.TaskStatus specifyStatus(PetasosTask petasosTask) {
        getLogger().debug(".specifyStatus(): Entry");
        PetasosActionableTask actionableTask = (PetasosActionableTask) petasosTask;

        if(actionableTask.hasTaskOutcomeStatus()) {
            Task.TaskStatus outcome = null;
            ActionableTaskOutcomeStatusEnum outcomeStatus = actionableTask.getTaskOutcomeStatus().getOutcomeStatus();
            switch(outcomeStatus){
                case ACTIONABLE_TASK_OUTCOME_STATUS_UNKNOWN:
                    outcome = Task.TaskStatus.DRAFT;
                    break;
                case ACTIONABLE_TASK_OUTCOME_STATUS_CANCELLED:
                    outcome = Task.TaskStatus.CANCELLED;
                    break;
                case ACTIONABLE_TASK_OUTCOME_STATUS_ACTIVE:
                    outcome = Task.TaskStatus.INPROGRESS;
                    break;
                case ACTIONABLE_TASK_OUTCOME_STATUS_FINISHED:
                    outcome = Task.TaskStatus.COMPLETED;
                    break;
                case ACTIONABLE_TASK_OUTCOME_STATUS_FINALISED:
                    outcome = Task.TaskStatus.COMPLETED;
                    break;
                case ACTIONABLE_TASK_OUTCOME_STATUS_FAILED:
                    outcome = Task.TaskStatus.FAILED;
                    break;
                case ACTIONABLE_TASK_OUTCOME_STATUS_WAITING:
                    outcome = Task.TaskStatus.ONHOLD;
                    break;
            }
            getLogger().debug(".specifyStatus(): Exit, outcome->{}", outcome);
            return(outcome);
        }
        getLogger().debug(".specifyStatus(): Exit, outcome->{}", Task.TaskStatus.DRAFT);
        return(Task.TaskStatus.DRAFT);
    }

    @Override
    protected CodeableConcept specifyStatusReason(PetasosTask petasosTask) {
        getLogger().debug(".specifyStatusReason(): Entry");
        PetasosActionableTask actionableTask = (PetasosActionableTask) petasosTask;
        CodeableConcept statusReasonCC = null;
        if(actionableTask.hasTaskCompletionSummary()){
            statusReasonCC = getStatusReasonFactory().newTaskStatusReason(actionableTask.getTaskCompletionSummary().isFinalised());
        } else {
            statusReasonCC = getStatusReasonFactory().newTaskStatusReason(false);
        }
        getLogger().debug(".specifyStatusReason(): Exit, statusReasonCC->{}", statusReasonCC);
        return(statusReasonCC);
    }

    @Override
    protected CodeableConcept specifyBusinessStatus(PetasosTask petasosTask) {
        getLogger().debug(".specifyBusinessStatus(): Entry");
        if(petasosTask == null){
            return(null);
        }
        PetasosActionableTask actionableTask = (PetasosActionableTask) petasosTask;
        CodeableConcept businessStatusCC = null;
        if(actionableTask.hasTaskFulfillment()){
            businessStatusCC = businessStatusFactory.newTaskStatusReason(actionableTask.getTaskFulfillment().getStatus());
        }
        return (businessStatusCC);
    }

    @Override
    protected Task.TaskIntent specifyIntent(PetasosTask petasosTask) {
        getLogger().debug(".specifyIntent(): Entry");
        Task.TaskIntent intent = Task.TaskIntent.INSTANCEORDER;
        getLogger().debug(".specifyIntent(): Exit, intent->{}", intent);
        return (intent);
    }

    @Override
    protected Task.TaskPriority specifyPriority(PetasosTask petasosTask) {
        getLogger().debug(".specifyPriority(): Entry");
        Task.TaskPriority priority = Task.TaskPriority.ROUTINE;
        getLogger().debug(".specifyPriority(): Exit, priority->{}", priority);
        return (priority);
    }

    @Override
    protected CodeableConcept specifyCode(PetasosTask petasosTask) {
        TaskCode taskCode = TaskCode.FULFILL;
        Coding coding = new Coding();
        coding.setCode(taskCode.toCode());
        coding.setSystem(taskCode.getSystem());
        coding.setDisplay(taskCode.getDisplay());
        CodeableConcept taskCodeCC = new CodeableConcept();
        taskCodeCC.addCoding(coding);
        taskCodeCC.setText(taskCode.getDisplay());
        return taskCodeCC;
    }

    @Override
    protected String specifyDescription(PetasosTask petasosTask) {
        String description = petasosTask.getTaskWorkItem().getIngresContent().getPayloadManifest().toString();
        return(description);
    }

    @Override
    protected Reference specifyFocus(PetasosTask petasosTask) {
        return null;
    }

    @Override
    protected Reference specifyFor(PetasosTask petasosTask) {
        getLogger().debug(".specifyFor(): Entry");
        PetasosActionableTask actionableTask = (PetasosActionableTask) petasosTask;

        Reference benificiaryResource = null;
        if(actionableTask.hasTaskContext()){
            if(actionableTask.getTaskContext().hasTaskBeneficiary()){
                benificiaryResource = actionableTask.getTaskContext().getTaskBeneficiary();
            }
        }

        getLogger().debug(".specifyFor(): Exit, benficiary->{}", benificiaryResource);
        return (benificiaryResource);
    }

    @Override
    protected Reference specifyEncounter(PetasosTask petasosTask) {
        getLogger().debug(".specifyEncounter(): Entry");

        PetasosActionableTask actionableTask = (PetasosActionableTask) petasosTask;

        Reference encounter = null;
        if(actionableTask.hasTaskContext()){
            if(actionableTask.getTaskContext().hasTaskEncounter()){
                encounter = actionableTask.getTaskContext().getTaskEncounter();
            }
        }

        getLogger().debug(".specifyEncounter(): Exit, encounter->{}", encounter);
        return (encounter);
    }

    protected Period specifyExecutionPeriod(PetasosTask petasosTask) {
        Period period = taskPeriodFactory.buildExecutionPeriod(petasosTask);
        return(period);
    }

    @Override
    protected Period specifyExecutionPeriod(PetasosTask petasosTask, Set<PetasosTask> subTaskSet) {
        return null;
    }

    @Override
    protected Reference specifyRequester(PetasosTask petasosTask) {
        getLogger().debug(".specifyRequester(): Entry");
        PetasosActionableTask actionableTask = (PetasosActionableTask) petasosTask;

        Reference requestor = null;
        if(actionableTask.hasTaskNodeAffinity()){
            ComponentIdType taskNodeAffinity = actionableTask.getTaskNodeAffinity();
            Period period = new Period();
            if(taskNodeAffinity.hasIdValidityStartInstant()){
                Date startDate = Date.from(taskNodeAffinity.getIdValidityStartInstant());
                period.setStart(startDate);
            }
            if(taskNodeAffinity.hasIdValidityEndInstant()){
                Date endDate = Date.from(taskNodeAffinity.getIdValidityEndInstant());
                period.setEnd(endDate);
            }
            Identifier identifier = getIdentifierFactory().newIdentifier(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_SOFTWARE_COMPONENT, taskNodeAffinity.getId(), period);
            requestor = new Reference();
            requestor.setIdentifier(identifier);
            requestor.setType(ResourceType.Task.name());
        }
        getLogger().debug(".specifyRequester(): Exit, requestor->{}", requestor);
        return(requestor);
    }

    @Override
    protected List<CodeableConcept> specifyPerformerType(PetasosTask petasosTask) {
        getLogger().debug(".specifyPerformerType(): Entry");

        PetasosActionableTask actionableTask = (PetasosActionableTask) petasosTask;

        List<CodeableConcept> performerTypes = new ArrayList<>();
        if(actionableTask.hasTaskPerformerTypes()){

            getLogger().trace(".specifyPerformerType(): has performerTypes");
            List<TaskPerformerTypeType> taskPerformerTypes = actionableTask.getTaskPerformerTypes();
            getLogger().trace(".specifyPerformerType(): taskPerformerTypes->{}", taskPerformerTypes);
            if(taskPerformerTypes != null) {
                for (TaskPerformerTypeType currentPerformerType : taskPerformerTypes) {
                    getLogger().trace(".specifyPerformerType(): processing ->{}", currentPerformerType);
                    String performerName = null;
                    if(currentPerformerType.getRequiredParticipantName() != null){
                        performerName = currentPerformerType.getRequiredParticipantName();
                    }
                    if(StringUtils.isEmpty(performerName) && currentPerformerType.getRequiredPerformerType() != null) {
                        performerName = currentPerformerType.getRequiredPerformerType().getFunctionToken().getToken();
                    }
                    if(StringUtils.isEmpty(performerName)){
                        performerName = "unknown";
                    }
                    String functionDescription = currentPerformerType.getRequiredPerformerTypeDescription();
                    CodeableConcept performerTypeCC = getPerformerTypeFactory().newTaskPerformerType(performerName, functionDescription);
                    performerTypes.add(performerTypeCC);
                }
            }
        }
        getLogger().debug(".specifyPerformerType(): Exit, performerTypes->{}", performerTypes);
        return (performerTypes);
    }

    @Override
    protected Reference specifyOwner(PetasosTask petasosTask) {
        getLogger().debug(".specifyOwner(): Entry");

        PetasosActionableTask actionableTask = (PetasosActionableTask) petasosTask;

        Reference owner = null;
        if(actionableTask.hasTaskFulfillment()) {
            if(actionableTask.getTaskFulfillment().hasFulfillerWorkUnitProcessor()) {
                //
                // Create the Identifier
                String participantName = null;
                if(actionableTask.getTaskFulfillment().hasFulfillerWorkUnitProcessor()) {
                    participantName = actionableTask.getTaskFulfillment().getFulfillerWorkUnitProcessor().getParticipantName();
                }
                if(StringUtils.isEmpty(participantName)){
                    participantName = "Unknown";
                }
                Identifier identifier = getIdentifierFactory().newIdentifier(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_SOFTWARE_COMPONENT, participantName, null);
                owner = new Reference();
                owner.setIdentifier(identifier);
                owner.setType(ResourceType.Device.name());
            }
        }
        getLogger().debug(".specifyOwner(): Exit, owner->{}", owner);
        return(owner);
    }

    @Override
    protected CodeableConcept specifyReasonCode(PetasosTask petasosTask) {
        getLogger().debug(".specifyReasonCode(): Entry");
        if(petasosTask.hasTaskReason()) {
            if(petasosTask.getTaskReason().getReasonType() != null){
                CodeableConcept reasonCC = taskReasonFactory.newTaskReason(petasosTask.getTaskReason().getReasonType());
                return(reasonCC);
            }
        }
        getLogger().debug(".specifyReasonCode(): Exit");
        return null;
    }

    @Override
    protected Reference specifyReasonReference(PetasosTask petasosTask) {
        if(petasosTask.hasTaskReason()) {
            if(petasosTask.getTaskReason().hasRetryTaskDetail()) {
                Reference reference = taskReasonFactory.newTaskReasonReference(petasosTask.getTaskReason().getRetryTaskDetail());
                return(reference);
            }
        }
        return(null);
    }


    protected CodeableConcept injectAdditionalBusinessStatusInformation(TaskFulfillmenExecutionStatusType executionStatus, CodeableConcept oriBusinessStatus){

        CodeableConcept updateBusinessStatusCC = SerializationUtils.clone(oriBusinessStatus);

        Extension systemWideFocusExtension = new Extension();
        systemWideFocusExtension.setUrl(taskTransformConstants.getSystemWideFocusExtensionUrl());
        systemWideFocusExtension.setValue(new BooleanType(executionStatus.isSystemWideFulfillmentTask()));
        updateBusinessStatusCC.addExtension(systemWideFocusExtension);

        Extension clusterWideFocusExtension = new Extension();
        clusterWideFocusExtension.setUrl(taskTransformConstants.getClusterWideFocusExtensionUrl());
        clusterWideFocusExtension.setValue(new BooleanType(executionStatus.isClusterWideFulfillmentTask()));
        updateBusinessStatusCC.addExtension(clusterWideFocusExtension);

        Extension retryRequiredExtension = new Extension();
        retryRequiredExtension.setUrl(taskTransformConstants.getTaskRetryRequiredExtensionUrl());
        retryRequiredExtension.setValue(new BooleanType(executionStatus.isRetryRequired()));
        updateBusinessStatusCC.addExtension(retryRequiredExtension);

        Extension retryCountExtension = new Extension();
        retryCountExtension.setUrl(taskTransformConstants.getTaskRetryCountExtensionUrl());
        retryCountExtension.setValue(new IntegerType(executionStatus.getRetryCount()));
        updateBusinessStatusCC.addExtension(retryCountExtension);

        return(updateBusinessStatusCC);
    }

    //
    // Getters (and Setters)
    //

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    protected TaskStatusReasonFactory getStatusReasonFactory(){
        return(this.statusReasonFactory);
    }

    protected TaskPerformerTypeFactory getPerformerTypeFactory(){
        return(this.performerTypeFactory);
    }

    protected PegacornIdentifierFactory getIdentifierFactory(){
        return(this.identifierFactory);
    }
}
