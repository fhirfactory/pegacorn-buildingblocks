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

import net.fhirfactory.pegacorn.core.interfaces.interception.PetasosInterceptionBrokerInterface;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.interception.PetasosInterZoneInterceptionEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.interception.PetasosIntraZoneInterceptionEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class InterProcessingPlantInterceptionServicesBroker implements PetasosInterceptionBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(InterProcessingPlantInterceptionServicesBroker.class);

    @Inject
    private PetasosIntraZoneInterceptionEndpoint intraZoneInterceptionEndpoint;

    @Inject
    private PetasosInterZoneInterceptionEndpoint interZoneInterceptionEndpoint;

    @Override
    public PetasosFulfillmentTask redirectFulfillmentTask(String collectorServiceName, PetasosFulfillmentTask task) {
        LOG.debug(".redirectFulfillmentTask(): Entry, collectorServiceName->{}, task->{}", collectorServiceName, task);

        PetasosFulfillmentTask redirectedTask = null;
        if(intraZoneInterceptionEndpoint.interceptionCollectorIsInScope(collectorServiceName)){
            LOG.trace(".redirectFulfillmentTask(): Using inter-zone communication framework");
            redirectedTask = intraZoneInterceptionEndpoint.redirectFulfillmentTask(collectorServiceName, task);
        }
        if(interZoneInterceptionEndpoint.interceptionCollectorIsInScope(collectorServiceName)){
            LOG.trace(".redirectFulfillmentTask(): Using intra-zone communication framework");
            redirectedTask = interZoneInterceptionEndpoint.redirectFulfillmentTask(collectorServiceName, task);
        }
        if(redirectedTask == null) {
            LOG.trace(".redirectFulfillmentTask(): Can't find suitable capability provider");
        }
        LOG.debug(".redirectFulfillmentTask(): Exit, redirectedTask->{}", redirectedTask);
        return(redirectedTask);
    }
}
