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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.factories.TaskIdentifierFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.factories.TaskWorkItemFactory;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

public abstract class FHIRTaskFromPetasosTask {

    private ObjectMapper jsonMapper;
    private Boolean initialised;
    private IParser fhirJSONParser;

    @Inject
    private TaskIdentifierFactory taskIdentifierFactory;

    @Inject
    private FHIRContextUtility fhirContextUtility;

    @Inject
    private TaskWorkItemFactory workItemFactory;

    public FHIRTaskFromPetasosTask(){
        jsonMapper = new ObjectMapper();
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        JavaTimeModule module = new JavaTimeModule();
        this.jsonMapper.registerModule(module);
        this.jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.initialised = false;
    }

    @PostConstruct
    public void initialise(){
        if(initialised){
            // do nothing
        } else {
            fhirJSONParser = getFHIRContextUtility().getJsonParser();
            this.initialised = true;
        }
    }

    //
    // Abstract Methods
    //

    abstract protected Logger getLogger();
    abstract protected Reference specifyBasedOn(PetasosTask petasosTask);
    abstract protected Identifier specifyGroupIdentifier(PetasosTask petasosTask);
    abstract protected List<Reference> specifyPartOf(PetasosTask petasosTask);
    abstract protected Task.TaskStatus specifyStatus(PetasosTask petasosTask);
    abstract protected CodeableConcept specifyStatusReason(PetasosTask petasosTask);
    abstract protected CodeableConcept specifyBusinessStatus(PetasosTask petasosTask);
    abstract protected Task.TaskIntent specifyIntent(PetasosTask petasosTask);
    abstract protected Task.TaskPriority specifyPriority(PetasosTask petasosTask);
    abstract protected CodeableConcept specifyCode(PetasosTask petasosTask);
    abstract protected String specifyDescription(PetasosTask petasosTask);
    abstract protected Reference specifyFocus(PetasosTask petasosTask);
    abstract protected Reference specifyFor(PetasosTask petasosTask);
    abstract protected Reference specifyEncounter(PetasosTask petasosTask);
    abstract protected Period specifyExecutionPeriod(PetasosTask petasosTask);
    abstract protected Period specifyExecutionPeriod(PetasosTask petasosTask, Set<PetasosTask> subTaskSet);
    abstract protected Reference specifyRequester(PetasosTask petasosTask);
    abstract protected List<CodeableConcept> specifyPerformerType(PetasosTask petasosTask);
    abstract protected Reference specifyOwner(PetasosTask petasosTask);
    abstract protected CodeableConcept specifyReasonCode(PetasosTask petasosTask);
    abstract protected Reference specifyReasonReference(PetasosTask petasosTask);

    //
    // Business Methods
    //

    public Task newTaskFromPetasosTask(PetasosTask petasosTask){
        getLogger().debug(".newTaskFromPetasosTask(): Entry, actionableTask->{}", petasosTask);
        Task fhirTask = new Task();

        //
        // Create and add the Task::Identifier
        getLogger().trace(".newTaskFromPetasosTask(): [Create and Add Identifier] Start");
        Identifier identifier = getTaskIdentifierFactory().newTaskIdentifier(TaskTypeTypeEnum.PETASOS_ACTIONABLE_TASK_TYPE, petasosTask.getTaskId());
        fhirTask.addIdentifier(identifier);
        getLogger().trace(".newTaskFromPetasosTask(): [Create and Add Identifier] Finish");

        //
        // Set the "BasedOn" Attribute
        getLogger().trace(".newTaskFromPetasosTask(): [Create and Add BasedOn] Start");
        Reference basedOnReference = specifyBasedOn(petasosTask);
        if(basedOnReference != null) {
            fhirTask.addBasedOn(basedOnReference);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Create and Add -basedOn-] Finish");

        //
        // Set the Group Identifier
        getLogger().trace(".newTaskFromPetasosTask(): [Create and Add GroupIdentifier] Start");
        Identifier groupIdentifier = specifyGroupIdentifier(petasosTask);
        if(groupIdentifier != null){
            fhirTask.setGroupIdentifier(groupIdentifier);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Create and Add GroupIdentifier] Finish");

        //
        // Set the "Part Of" Attribute
        getLogger().trace(".newTaskFromPetasosTask(): [Create and Add PartOf] Start");
        List<Reference> partOfReference = specifyPartOf(petasosTask);
        if(partOfReference != null){
            fhirTask.getPartOf().addAll(partOfReference);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Create and Add PartOf] Finish");

        //
        // Set the status
        getLogger().trace(".newTaskFromPetasosTask(): [Set Status] Start");
        fhirTask.setStatus(specifyStatus(petasosTask));
        getLogger().trace(".newTaskFromPetasosTask(): [Set Status] Finish");

        //
        // Set the Status Reason
        getLogger().trace(".newTaskFromPetasosTask(): [Set StatusReason] Start");
        CodeableConcept statusReasonCC = specifyStatusReason(petasosTask);
        if(statusReasonCC != null) {
            fhirTask.setStatusReason(statusReasonCC);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set StatusReason] Finish");

        //
        // Set the Business Status
        getLogger().trace(".newTaskFromPetasosTask(): [Set BusinessStatus] Start");
        CodeableConcept businessStatusCC = specifyBusinessStatus(petasosTask);
        if(businessStatusCC != null){
            fhirTask.setBusinessStatus(businessStatusCC);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set BusinessStatus] Finish");

        //
        // Set the Task "Intent"
        getLogger().trace(".newTaskFromPetasosTask(): [Set Intent] Start");
        Task.TaskIntent taskIntent = specifyIntent(petasosTask);
        if(taskIntent != null){
            fhirTask.setIntent(taskIntent);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set Intent] Finish");

        //
        // Set the Task "Priority"
        getLogger().trace(".newTaskFromPetasosTask(): [Set Priority] Start");
        Task.TaskPriority taskPriority = specifyPriority(petasosTask);
        if(taskPriority != null){
            fhirTask.setPriority(taskPriority);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set Priority] Finish");

        //
        // Set the Task "Code"
        getLogger().trace(".newTaskFromPetasosTask(): [Set Code] Start");
        CodeableConcept taskCodeCC = specifyCode(petasosTask);
        if(taskCodeCC != null){
            fhirTask.setCode(taskCodeCC);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set Code] Finish");

        //
        // Set the Task Description
        getLogger().trace(".newTaskFromPetasosTask(): [Set Description] Start");
        String taskDescription = specifyDescription(petasosTask);
        if(StringUtils.isNotBlank(taskDescription)){
            fhirTask.setDescription(taskDescription);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set Description] Finish");

        //
        // Set the Task "Focus"
        getLogger().trace(".newTaskFromPetasosTask(): [Set Focus] Start");
        Reference taskFocus = specifyFocus(petasosTask);
        if(taskFocus != null){
            fhirTask.setFocus(taskFocus);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set Focus] Finish");

        //
        // Set the Task "For" (Beneficiary)
        getLogger().trace(".newTaskFromPetasosTask(): [Set Beneficiary] Start");
        Reference taskFor = specifyFor(petasosTask);
        if(taskFor != null){
            fhirTask.setFor(taskFor);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set Beneficiary] Finish");

        //
        // Set the Task Encounter
        getLogger().trace(".newTaskFromPetasosTask(): [Set Encounter] Start");
        Reference taskEncounter = specifyEncounter(petasosTask);
        if(taskEncounter != null){
            fhirTask.setEncounter(taskEncounter);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set Encounter] Finish");

        //
        // Set the Execution Period
        getLogger().trace(".newTaskFromPetasosTask(): [Set ExecutionPeriod] Start");
        Period taskExecutionPeriod = specifyExecutionPeriod(petasosTask);
        if(taskExecutionPeriod != null){
            fhirTask.setExecutionPeriod(taskExecutionPeriod);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set ExecutionPeriod] Finish");

        //
        // Set the creation (Authored On) date
        getLogger().trace(".newTaskFromPetasosTask(): [Set AuthoredOn] Start");
        Instant creationInstant = petasosTask.getCreationInstant();
        Date creationDate = Date.from(creationInstant);
        fhirTask.setAuthoredOn(creationDate);
        getLogger().trace(".newTaskFromPetasosTask(): [Set AuthoredOn] Finish");

        //
        // Set the last update (Last Update On) date
        getLogger().trace(".newTaskFromPetasosTask(): [Set LastModified] Start");
        Instant lastModifiedInstant = petasosTask.getUpdateInstant();
        Date updateDate = Date.from(lastModifiedInstant);
        fhirTask.setLastModified(creationDate);
        getLogger().trace(".newTaskFromPetasosTask(): [Set LastModified] Finish");

        //
        // Set the Requester
        getLogger().trace(".newTaskFromPetasosTask(): [Set Requester] Start");
        Reference taskRequester = specifyRequester(petasosTask);
        if(taskRequester != null){
            fhirTask.setRequester(taskRequester);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set Requester] Finish");

        //
        // Set the PerformerType
        getLogger().trace(".newTaskFromPetasosTask(): [Set PerformerType] Start");
        List<CodeableConcept> taskPerformerCC = specifyPerformerType(petasosTask);
        if(taskPerformerCC != null){
            fhirTask.getPerformerType().addAll(taskPerformerCC);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set PerformerType] Finsh");

        //
        // Set the Owner
        getLogger().trace(".newTaskFromPetasosTask(): [Set Owner] Start");
        Reference taskOwner = specifyOwner(petasosTask);
        if(taskOwner != null){
            fhirTask.setOwner(taskOwner);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set Owner] Finish");

        //
        // Set the Task Reason
        getLogger().trace(".newTaskFromPetasosTask(): [Set Reason] Start");
        CodeableConcept taskReasonCodeCC = specifyReasonCode(petasosTask);
        if(taskReasonCodeCC != null){
            fhirTask.setReasonCode(taskReasonCodeCC);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set Reason] Finish");

        //
        // Add the Task Input
        getLogger().trace(".newTaskFromPetasosTask(): [Set Input] Start");
        if(petasosTask.getTaskWorkItem().getIngresContent() != null){
            Task.ParameterComponent taskInput = getWorkItemFactory().newWorkItemPayload(petasosTask.getTaskWorkItem().getIngresContent());
            fhirTask.addInput(taskInput);
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set Input] Finish");

        //
        // Add the Task Output
        getLogger().trace(".newTaskFromPetasosTask(): [Set Output] Start");
        if(petasosTask.getTaskWorkItem().hasEgressContent()){
            if(!petasosTask.getTaskWorkItem().getEgressContent().getPayloadElements().isEmpty()){
                for(UoWPayload currentEgressPayload: petasosTask.getTaskWorkItem().getEgressContent().getPayloadElements()){
                    Task.ParameterComponent taskOutput = getWorkItemFactory().newWorkItemPayload(currentEgressPayload);
                    fhirTask.addInput(taskOutput);
                }
            }
        }
        getLogger().trace(".newTaskFromPetasosTask(): [Set Output] Start");

        if(getLogger().isTraceEnabled()){
            try{
                getFHIRJSONParser().setPrettyPrint(true);
                String taskAsString = getFHIRJSONParser().encodeResourceToString(fhirTask);
                getLogger().trace(".newTaskFromPetasosTask(): Task->{}", taskAsString);
            } catch(Exception ex){
                getLogger().trace(".newTaskFromPetasosTask(): Can't print task...", ex);
            }
        }

        getLogger().debug(".newTaskFromPetasosTask(): Exit, fhirTask->{}", fhirTask);
        return(fhirTask);
    }

    //
    // Helper Method(s)
    //

    protected String mapDataParcelManifestToString(DataParcelManifest parcelManifest){
        try {
            String s = getJSONMapper().writeValueAsString(parcelManifest);
            return(s);
        } catch (JsonProcessingException e) {
            getLogger().error(".mapDataParcelManifestToString(): Error -->{}", ExceptionUtils.getStackTrace(e));
            return(null);
        }
    }

    //
    // Getters (and Setters)
    //

    protected TaskIdentifierFactory getTaskIdentifierFactory(){
        return(this.taskIdentifierFactory);
    }

    protected ObjectMapper getJSONMapper(){
        return(this.jsonMapper);
    }

    protected TaskWorkItemFactory getWorkItemFactory(){
        return(this.workItemFactory);
    }

    protected FHIRContextUtility getFHIRContextUtility(){
        return(fhirContextUtility);
    }

    protected IParser getFHIRJSONParser(){
        return(fhirJSONParser);
    }
}
