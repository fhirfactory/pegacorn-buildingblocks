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


import net.fhirfactory.pegacorn.core.model.petasos.participant.id.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes.TaskTraceabilityType;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.DRICaTSIdentifierFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.provenance.factories.ProvenanceComponentFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.provenance.factories.ProvenanceSequenceNumberExtensionFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.provenance.transformers.ProvenanceFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.factories.TaskIdentifierFactory;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Provenance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FHIRProvenanceFromPetasosTaskJourney {
    private static final Logger LOG = LoggerFactory.getLogger(FHIRProvenanceFromPetasosTaskJourney.class);

    @Inject
    private TaskIdentifierFactory taskIdentifierFactory;

    @Inject
    private ProvenanceSequenceNumberExtensionFactory sequenceNumberExtensionFactory;

    @Inject
    private DRICaTSIdentifierFactory identifierFactory;

    @Inject
    private ProvenanceComponentFactory provenanceComponentFactory;

    @Inject
    private ProvenanceFactory provenanceFactory;


    //
    // Business Methods
    //

    public Provenance newProvenanceFromTaskJourney(PetasosParticipantId agentId, Identifier taskIdentifier, TaskTraceabilityType taskJourney){
        getLogger().debug(".newProvenanceFromTaskJourney(): Entry, agentId->{}, taslIdentifier->{}, taskJourney->{}", agentId, taskIdentifier, taskJourney);
        if(taskIdentifier == null){
            getLogger().debug(".newProvenanceFromTaskJourney(): Exit, taskIdentifier is null, returning null");
            return(null);
        }
        if(taskJourney == null){
            getLogger().debug(".newProvenanceFromTaskJourney(): Exit, taskJourney is null, returning null");
            return(null);
        }
        if(taskJourney.getTaskJourney().isEmpty()){
            getLogger().debug(".newProvenanceFromTaskJourney(): Exit, taskJourney is empty, returning null");
            return(null);
        }

        Provenance taskJourneyProvenance = getProvenanceFactory().newProvenanceFromTaskJourney(agentId, taskIdentifier, taskJourney);

        getLogger().debug(".newProvenanceFromTaskJourney(): Exit, taskJourneyProvenance->{}", taskJourneyProvenance);
        return(taskJourneyProvenance);
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

    protected ProvenanceComponentFactory getProvenanceComponentFactory(){
        return(this.provenanceComponentFactory);
    }

    protected ProvenanceFactory getProvenanceFactory(){
        return(this.provenanceFactory);
    }
}
