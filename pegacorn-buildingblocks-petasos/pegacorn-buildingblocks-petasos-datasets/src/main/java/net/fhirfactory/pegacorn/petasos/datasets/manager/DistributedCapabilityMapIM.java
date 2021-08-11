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
package net.fhirfactory.pegacorn.petasos.datasets.manager;

import net.fhirfactory.pegacorn.core.tasks.PetasosCapabilityDeliveryNode;
import net.fhirfactory.pegacorn.core.tasks.PetasosCapabilityDeliveryNodeRegistration;
import net.fhirfactory.pegacorn.core.tasks.base.PetasosCapabilityCommonName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class DistributedCapabilityMapIM {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedCapabilityMapIM.class);

    @Inject
    private DistributedCapabilityMapIM capabilityMapIM;

    public PetasosCapabilityDeliveryNodeRegistration registerTaskCompletionEngine(PetasosCapabilityDeliveryNode ptce) {
        LOG.info(".registerTaskCompletionEngine(): Entry, ptce->{}", ptce);
        PetasosCapabilityDeliveryNodeRegistration petasosCapabilityDeliveryNodeRegistration = capabilityMapIM.registerTaskCompletionEngine(ptce);
        LOG.debug(".registerTaskCompletionEngine(): Exit, registration->{}", petasosCapabilityDeliveryNodeRegistration);
        return (petasosCapabilityDeliveryNodeRegistration);
    }

    public void unregisterTaskCompletionEngine(String petasosRoutingEndpointName) {
        LOG.info(".unregisterTaskCompletionEngine(): Entry, petasosRoutingEndpointName->{}", petasosRoutingEndpointName);
        capabilityMapIM.unregisterTaskCompletionEngine(petasosRoutingEndpointName);
        LOG.debug(".unregisterTaskCompletionEngine(): Exit");
    }

    public PetasosCapabilityDeliveryNodeRegistration getTaskEngineRegistration(String ptceRoutingEndpointName) {
        LOG.debug(".getTaskEngineRegistration(): Entry, ptceRoutingEndpointName->{}", ptceRoutingEndpointName);
        PetasosCapabilityDeliveryNodeRegistration taskEngineRegistration = capabilityMapIM.getTaskEngineRegistration(ptceRoutingEndpointName);
        LOG.debug(".getTaskEngineRegistration(): Exit, registration->{}", taskEngineRegistration);
        return (taskEngineRegistration);
    }

    public List<PetasosCapabilityDeliveryNodeRegistration> getTaskExecutionEngineServiceRegistrationSet(PetasosCapabilityCommonName capabilityName) {
        LOG.info(".getTaskExecutionEngineServiceSet(): Entry, capabilityName->{}", capabilityName);
        List<PetasosCapabilityDeliveryNodeRegistration> engineRegistrations = capabilityMapIM.getTaskExecutionEngineServiceRegistrationSet(capabilityName);
        LOG.debug(".getTaskExecutionEngineServiceSet(): Exit");
        return (engineRegistrations);
    }

    public boolean isTaskExecutionEngineRegistered(String engineRoutingEndpointName) {
        LOG.debug(".isTaskExecutionEngineRegistered(): Entry, engineRoutingEndpointName->{}", engineRoutingEndpointName);
        boolean taskExecutionEngineRegistered = capabilityMapIM.isTaskExecutionEngineRegistered(engineRoutingEndpointName);
        LOG.debug(".isTaskExecutionEngineRegistered(): Exit, returning->{}", taskExecutionEngineRegistered);
        return (taskExecutionEngineRegistered);
    }

    public boolean isTaskExecutionEngineAvailableToSupportCapability(PetasosCapabilityCommonName commonName) {
        LOG.debug(".isTaskExecutionEngineAvailableToSupportCapability(): Entry, commonName->{}", commonName);
        boolean engineAvailableToSupportCapability = capabilityMapIM.isTaskExecutionEngineAvailableToSupportCapability(commonName);
        LOG.debug(".isTaskExecutionEngineAvailableToSupportCapability(): Exit, returning->{}", engineAvailableToSupportCapability);
        return (engineAvailableToSupportCapability);
    }
}