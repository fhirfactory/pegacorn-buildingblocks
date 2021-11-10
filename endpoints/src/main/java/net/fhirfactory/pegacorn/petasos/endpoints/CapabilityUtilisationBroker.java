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
package net.fhirfactory.pegacorn.petasos.endpoints;

import net.fhirfactory.pegacorn.core.model.capabilities.CapabilityUtilisationBrokerInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.tasks.PetasosInterZoneTaskEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.tasks.PetasosIntraZoneTaskEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class CapabilityUtilisationBroker implements CapabilityUtilisationBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(CapabilityUtilisationBroker.class);

    @Inject
    private PetasosIntraZoneTaskEndpoint intraZoneTaskingEndpoint;

    @Inject
    private PetasosInterZoneTaskEndpoint interZoneTaskingEndpoint;

    @Override
    public CapabilityUtilisationResponse executeTask(String preferredCapabilityProvider, CapabilityUtilisationRequest task) {
        LOG.debug(".executeTask(): Entry, preferredCapabilityProvider->{}, task->{}", preferredCapabilityProvider, task);

        if(interZoneTaskingEndpoint.taskFulfillerIsInScope(preferredCapabilityProvider)){
            LOG.trace(".executeTask(): Using inter-zone communication framework");
            CapabilityUtilisationResponse taskOutcome = interZoneTaskingEndpoint.executeTask(preferredCapabilityProvider, task);
            LOG.debug(".executeTask(): Exit, outcome->{}", taskOutcome);
            return(taskOutcome);
        }
        if(intraZoneTaskingEndpoint.taskFulfillerIsInScope(preferredCapabilityProvider)){
            LOG.trace(".executeTask(): Using intra-zone communication framework");
            CapabilityUtilisationResponse taskOutcome = intraZoneTaskingEndpoint.executeTask(preferredCapabilityProvider, task);
            LOG.debug(".executeTask(): Exit, outcome->{}", taskOutcome);
            return(taskOutcome);
        }

        LOG.trace(".executeTask(): Can't find suitable capability provider");
        CapabilityUtilisationResponse outcome = new CapabilityUtilisationResponse();
        outcome.setSuccessful(false);
        outcome.setInScope(false);
        outcome.setInstantCompleted(Instant.now());
        outcome.setAssociatedRequestID(task.getRequestID());
        LOG.debug(".executeTask(): Exit, failed to find capability provider, outcome->{}", outcome);
        return(outcome);
    }
}
