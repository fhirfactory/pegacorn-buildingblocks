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
package net.fhirfactory.pegacorn.core.model.ui.resources.summaries.factories;

import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.ui.resources.summaries.PetasosParticipantSummary;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
public class ParticipantSummaryFactory extends SoftwareComponentSummaryFactory{
    private static final Logger LOG = LoggerFactory.getLogger(ParticipantSummaryFactory.class);


    //
    // Business Methods
    //

    public PetasosParticipantSummary newParticipantSummary(PetasosParticipant participant){
        getLogger().debug(".newParticipantSummary(): Entry, participant->{}", participant);

        PetasosParticipantSummary participantSummary = new PetasosParticipantSummary();
        participantSummary.setParticipantId(SerializationUtils.clone(participant.getParticipantId()));
        participantSummary.setControlStatus(participant.getControlStatus());
        participantSummary.setStatus(participant.getParticipantStatus());
        participantSummary.setFulfillmentState(SerializationUtils.clone(participant.getFulfillmentState()));
        participantSummary.setLastActivityInstant(participant.getUtilisationUpdateInstant());
        participantSummary.setNodeType(PetasosMonitoredComponentTypeEnum.nodeTypeFromTopologyNodeType(participant.getComponentType()));
        participantSummary.setResourceId(participant.getComponentId().getId());
        participantSummary.setLastSynchronisationInstant(Instant.now());
        getLogger().debug(".newParticipantSummary(): Entry, participantSummary->{}", participantSummary);
        return(participantSummary);
    }

    //
    // Getters (and Setters)
    //

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

 }
