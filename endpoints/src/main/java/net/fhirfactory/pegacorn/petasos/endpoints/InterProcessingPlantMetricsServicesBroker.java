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

import net.fhirfactory.pegacorn.core.interfaces.oam.metrics.PetasosMetricsBrokerInterface;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.PetasosComponentMetric;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.PetasosComponentMetricSet;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.metrics.PetasosInterZoneOAMMetricsEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.metrics.PetasosIntraZoneOAMMetricsEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class InterProcessingPlantMetricsServicesBroker implements PetasosMetricsBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(InterProcessingPlantMetricsServicesBroker.class);

    @Inject
    private PetasosInterZoneOAMMetricsEndpoint interZoneMetricsEndpoint;

    @Inject
    private PetasosIntraZoneOAMMetricsEndpoint intraZoneMetricsEndpoint;

    @Override
    public Instant captureMetric(String serviceProviderName, PetasosComponentMetric metric) {
        LOG.debug(".captureMetric(): Entry, serviceProviderName->{}, metric->{}", serviceProviderName, metric);

        Instant captureInstant = null;
        if(intraZoneMetricsEndpoint.metricsServiceProviderIsInScope(serviceProviderName)){
            LOG.trace(".captureMetric(): Using inter-zone communication framework");
            captureInstant = intraZoneMetricsEndpoint.updateMetric(serviceProviderName, metric);
        }
        if(interZoneMetricsEndpoint.metricsServiceProviderIsInScope(serviceProviderName)){
            LOG.trace(".captureMetric(): Using intra-zone communication framework");
            captureInstant = interZoneMetricsEndpoint.updateMetric(serviceProviderName, metric);
        }
        if(captureInstant == null) {
            LOG.trace(".captureMetric(): Can't find suitable capability provider");
        }
        LOG.debug(".captureMetric(): Exit, captureInstant->{}", captureInstant);
        return(captureInstant);
    }

    @Override
    public Instant captureMetrics(String serviceProviderName, PetasosComponentMetricSet metricSet) {
        LOG.debug(".captureMetrics(): Entry, serviceProviderName->{}, metricSet->{}", serviceProviderName, metricSet);

        Instant captureInstant = null;
        if(intraZoneMetricsEndpoint.metricsServiceProviderIsInScope(serviceProviderName)){
            LOG.trace(".captureMetrics(): Using inter-zone communication framework");
            captureInstant = intraZoneMetricsEndpoint.updateMetrics(serviceProviderName, metricSet);
        }
        if(interZoneMetricsEndpoint.metricsServiceProviderIsInScope(serviceProviderName)){
            LOG.trace(".captureMetrics(): Using intra-zone communication framework");
            captureInstant = interZoneMetricsEndpoint.updateMetrics(serviceProviderName, metricSet);
        }
        if(captureInstant == null) {
            LOG.trace(".captureMetrics(): Can't find suitable capability provider");
        }
        LOG.debug(".captureMetrics(): Exit, registeredTask->{}", captureInstant);
        return(captureInstant);
    }

 }
