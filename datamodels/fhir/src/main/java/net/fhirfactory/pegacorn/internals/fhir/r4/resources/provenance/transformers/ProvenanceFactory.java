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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.provenance.transformers;

import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.tasktype.valuesets.TaskTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityElementType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityType;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.DRICaTSIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.DRICaTSIdentifierFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.provenance.factories.ProvenanceComponentFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.provenance.factories.ProvenanceSequenceNumberExtensionFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.factories.TaskIdentifierFactory;
import org.apache.commons.lang3.SerializationUtils;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.codesystems.ProvenanceAgentRole;
import org.hl7.fhir.r4.model.codesystems.ProvenanceAgentType;
import org.hl7.fhir.r4.model.codesystems.W3cProvenanceActivityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.Enumeration;

@ApplicationScoped
public class ProvenanceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ProvenanceFactory.class);

    @Inject
    private TaskIdentifierFactory taskIdentifierFactory;

    @Inject
    private ProvenanceSequenceNumberExtensionFactory sequenceNumberExtensionFactory;

    @Inject
    private DRICaTSIdentifierFactory identifierFactory;

    @Inject
    private ProvenanceComponentFactory provenanceComponentFactory;



    //
    // Business Methods
    //

    public Provenance newProvenanceFromTaskJourney(PetasosParticipantId agentId, Identifier taskIdentifier, TaskTraceabilityType taskJourney){
        getLogger().debug(".newProvenanceFromTaskJourney(): Entry, agentId->{}, taskIdentifier->{}, taskJourney->{}", agentId, taskIdentifier, taskJourney);
        if(taskIdentifier == null){
            getLogger().debug(".newProvenanceFromTaskJourney(): Exit, taskIdentifier is null");
            return(null);
        }
        if(taskJourney == null){
            getLogger().debug(".newProvenanceFromTaskJourney(): Exit, taskJourney is null");
            return(null);
        }
        if(taskJourney.getTaskJourney().isEmpty()){
            getLogger().debug(".newProvenanceFromTaskJourney(): Exit, taskJourney is empty");
            return(null);
        }

        //
        // Create Empty Provenance Resource/Object
        Provenance taskJourneyProvenance = new Provenance();

        //
        // Set Provenance Target (in this case, a Task)
        Reference targetReference = new Reference();
        targetReference.setIdentifier(taskIdentifier);
        targetReference.setType(ResourceType.Task.name());
        taskJourneyProvenance.addTarget(targetReference);

        //
        // Set the Provenance Agent
        Provenance.ProvenanceAgentComponent provenanceAgent = newProvenanceAgentComponent(agentId, ProvenanceAgentRole.PERFORMER, ProvenanceAgentType.PERFORMER);
        taskJourneyProvenance.addAgent(provenanceAgent);

        //
        // Set the Provenance Recorded Instant
        taskJourneyProvenance.setRecorded(Date.from(Instant.now()));

        //
        // Set the Provenance Reason
        // TODO Add Provenance Reason

        //
        // Set the Provenance Activity
        CodeableConcept provenanceActivity = getProvenanceFactory().newProvenanceActivity(W3cProvenanceActivityType.GENERATION);
        taskJourneyProvenance.setActivity(provenanceActivity);

        //
        // Set the Provenance Entities
        Enumeration<Integer> sequenceNumberEnumerator = taskJourney.getTaskJourney().keys();
        getLogger().trace(".newProvenanceFromTaskJourney(): [Iterate Through TaskJourney] Start");
        getLogger().trace(".newProvenanceFromTaskJourney(): [Iterate Through TaskJourney] TaskJourney Length->{}", taskJourney.getTaskJourney().size());
        while(sequenceNumberEnumerator.hasMoreElements()){
            Integer currentSequenceNumber = sequenceNumberEnumerator.nextElement();
            TaskTraceabilityElementType currentJourneyElement = SerializationUtils.clone(taskJourney.getTaskJourney().get(currentSequenceNumber));
            getLogger().trace(".newProvenanceFromTaskJourney(): [Iterate Through TaskJourney] Processing TaskId->{}, JourneyElement({}):{}", taskIdentifier.getValue(), currentSequenceNumber, currentJourneyElement.getActionableTaskId().getId() );

            Provenance.ProvenanceEntityComponent newProvenanceEntity = newProvenanceEntityComponent(currentJourneyElement, currentSequenceNumber);

            // Create the Provenance Agent (a Device --> SoftwareComponent) Identifier
            PetasosParticipantId currentTaskFulfillerId = currentJourneyElement.getFulfillerId();
            Provenance.ProvenanceAgentComponent newProvenanceEntityAgent = newProvenanceAgentComponent(currentTaskFulfillerId, ProvenanceAgentRole.PERFORMER, ProvenanceAgentType.PERFORMER);

            newProvenanceEntity.addAgent(newProvenanceEntityAgent);
            taskJourneyProvenance.addEntity(newProvenanceEntity);

        }
        getLogger().trace(".newProvenanceFromTaskJourney(): [Iterate Through TaskJourney] Exit");
        getLogger().debug(".newProvenanceFromTaskJourney(): Exit, taskJourneyProvenance->{}", taskJourneyProvenance);
        return(taskJourneyProvenance);
    }

    protected Provenance.ProvenanceEntityComponent newProvenanceEntityComponent(TaskTraceabilityElementType traceabilityElement, Integer sequenceNumber){
        getLogger().debug(".newProvenanceEntityComponent(): Entry, traceabilityElement->{}, sequenceNumber->{}", traceabilityElement, sequenceNumber);
        Identifier journeyElementIdentifier = getTaskIdentifierFactory().newTaskIdentifier(TaskTypeTypeEnum.PETASOS_ACTIONABLE_TASK_TYPE, traceabilityElement.getActionableTaskId());
        Reference journeyElementReference = new Reference();
        journeyElementReference.setIdentifier(journeyElementIdentifier);
        journeyElementReference.setType(ResourceType.Task.name());
        getSequenceNumberExtensionFactory().injectTargetSequenceNumber(journeyElementReference, sequenceNumber);
        Provenance.ProvenanceEntityComponent provenanceEntity = new Provenance.ProvenanceEntityComponent();
        provenanceEntity.setWhat(journeyElementReference);
        provenanceEntity.setRole(Provenance.ProvenanceEntityRole.DERIVATION);
        getLogger().debug(".newProvenanceEntityComponent(): Exit, provenanceEntity->{}", provenanceEntity);
        return(provenanceEntity);
    }

    protected Provenance.ProvenanceAgentComponent newProvenanceAgentComponent(PetasosParticipantId id, ProvenanceAgentRole agentRole, ProvenanceAgentType agentType){
        getLogger().warn(".newProvenanceAgentComponent(): Entry, id->{}, agentRole->{}, agentType->{}", id, agentRole, agentType);
        Provenance.ProvenanceAgentComponent createdProvenanceAgent = new Provenance.ProvenanceAgentComponent();
        Period period = new Period();
        Identifier deviceIdentifier = getIdentifierFactory().newIdentifier(DRICaTSIdentifierCodeEnum.IDENTIFIER_CODE_SOFTWARE_COMPONENT, id.getName(), period);
        Reference deviceReference = new Reference();
        deviceReference.setIdentifier(deviceIdentifier);
        deviceReference.setType(ResourceType.Device.name());
        createdProvenanceAgent.setWho(deviceReference);
        CodeableConcept currentAgentRole = getProvenanceFactory().newAgentRole(agentRole);
        CodeableConcept currentAgentType = getProvenanceFactory().newAgentType(agentType);
        createdProvenanceAgent.addRole(currentAgentRole);
        createdProvenanceAgent.setType(currentAgentType);
        getLogger().warn(".newProvenanceAgentComponent(): Exit, createdProvenanceAgent->{}", createdProvenanceAgent);
        return(createdProvenanceAgent);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected TaskIdentifierFactory getTaskIdentifierFactory(){
        return(this.taskIdentifierFactory);
    }

    protected ProvenanceSequenceNumberExtensionFactory getSequenceNumberExtensionFactory(){
        return(this.sequenceNumberExtensionFactory);
    }

    protected DRICaTSIdentifierFactory getIdentifierFactory(){
        return(this.identifierFactory);
    }

    protected ProvenanceComponentFactory getProvenanceFactory(){
        return(this.provenanceComponentFactory);
    }
}
