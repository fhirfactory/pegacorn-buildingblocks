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

import net.fhirfactory.pegacorn.core.interfaces.pubsub.PetasosSubscriptionBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.PetasosTopologyBrokerInterface;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.PetasosSubscriptionSummaryReport;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.PetasosMonitoredTopologyGraph;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.discovery.PetasosInterZoneOAMDiscoveryEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.discovery.PetasosIntraZoneOAMDiscoveryEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class InterProcessingPlantSubscriptionServicesBroker implements PetasosSubscriptionBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(InterProcessingPlantSubscriptionServicesBroker.class);

    @Inject
    private PetasosInterZoneOAMDiscoveryEndpoint interZoneDiscoveryEndpoint;

    @Inject
    private PetasosIntraZoneOAMDiscoveryEndpoint intraZoneDiscoveryEndpoint;

    @Override
    public Instant shareSubscriptionSummaryReport(String serviceProviderName, PetasosSubscriptionSummaryReport summaryReport) {
        LOG.debug(".shareSubscriptionSummaryReport(): Entry, serviceProviderName->{}, summaryReport->{}", serviceProviderName, summaryReport);

        Instant captureInstant = null;
        if(interZoneDiscoveryEndpoint.discoveryServiceProviderIsInScope(serviceProviderName)){
            LOG.trace(".shareSubscriptionSummaryReport(): Using inter-zone communication framework");
            captureInstant = interZoneDiscoveryEndpoint.shareLocalTopologyGraph(serviceProviderName, summaryReport);
        }
        if(intraZoneDiscoveryEndpoint.discoveryServiceProviderIsInScope(serviceProviderName)){
            LOG.trace(".shareSubscriptionSummaryReport(): Using intra-zone communication framework");
            captureInstant = intraZoneDiscoveryEndpoint.shareLocalTopologyGraph(serviceProviderName, summaryReport);
        }
        if(captureInstant == null) {
            LOG.trace(".shareSubscriptionSummaryReport(): Can't find suitable capability provider");
        }
        LOG.debug(".shareSubscriptionSummaryReport(): Exit, captureInstant->{}", captureInstant);
        return(captureInstant);
    }

}
