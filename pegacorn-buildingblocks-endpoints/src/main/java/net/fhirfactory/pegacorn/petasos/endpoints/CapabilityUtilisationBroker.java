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

import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.components.capabilities.hl7v2tasks.A19QueryTask;
import net.fhirfactory.pegacorn.components.capabilities.hl7v2tasks.A19QueryTaskOutcome;
import net.fhirfactory.pegacorn.components.capabilities.CapabilityUtilisationBrokerInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.ipc.PetasosInterZoneIPCEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.ipc.PetasosIntraZoneIPCEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class CapabilityUtilisationBroker implements CapabilityUtilisationBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(CapabilityUtilisationBroker.class);

    @Inject
    private PetasosInterZoneIPCEndpoint interZoneIPCEndpoint;

    @Inject
    private PetasosIntraZoneIPCEndpoint intraZoneIPCEndpoint;

    @Override
    public CapabilityUtilisationResponse executeTask(String preferredCapabilityProvider, CapabilityUtilisationRequest a19QueryTask) {
        LOG.debug(".useA19QueryCapability(): Entry, preferredCapabilityProvider->{}, a19QueryTask->{}", preferredCapabilityProvider, a19QueryTask);

        if(intraZoneIPCEndpoint.capabilityProviderIsInScope(preferredCapabilityProvider)){
            LOG.trace(".useA19QueryCapability(): Using intra-zone communication framework");
            CapabilityUtilisationResponse a19QueryTaskOutcome = intraZoneIPCEndpoint.executeTask(preferredCapabilityProvider, a19QueryTask);
            LOG.debug(".useA19QueryCapability(): Exit, outcome->{}", a19QueryTaskOutcome);
            return(a19QueryTaskOutcome);
        }
        if(interZoneIPCEndpoint.capabilityProviderIsInScope(preferredCapabilityProvider)){
            LOG.trace(".useA19QueryCapability(): Using inter-zone communication framework");
            CapabilityUtilisationResponse a19QueryTaskOutcome = intraZoneIPCEndpoint.executeTask(preferredCapabilityProvider, a19QueryTask);
            LOG.debug(".useA19QueryCapability(): Exit, outcome->{}", a19QueryTaskOutcome);
            return(a19QueryTaskOutcome);
        }

        LOG.trace(".useA19QueryCapability(): Can't find suitable capability provider");
        CapabilityUtilisationResponse outcome = new CapabilityUtilisationResponse();
        outcome.setSuccessful(false);
        outcome.setInScope(false);
        outcome.setDateCompleted(Instant.now());
        outcome.setAssociatedRequestID(a19QueryTask.getRequestID());
        LOG.debug(".useA19QueryCapability(): Exit, failed to find capability provider, outcome->{}", outcome);
        return(outcome);
    }
}
