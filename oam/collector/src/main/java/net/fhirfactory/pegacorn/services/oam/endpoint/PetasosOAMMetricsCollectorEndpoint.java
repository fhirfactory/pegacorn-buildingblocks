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
package net.fhirfactory.pegacorn.services.oam.endpoint;

import net.fhirfactory.pegacorn.core.interfaces.oam.metrics.PetasosMetricsHandlerInterface;
import net.fhirfactory.pegacorn.core.interfaces.oam.subscriptions.PetasosSubscriptionReportHandlerInterface;
import net.fhirfactory.pegacorn.core.interfaces.oam.topology.PetasosTopologyReportingHandlerInterface;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.PetasosComponentMetric;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.PetasosComponentMetricSet;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.PetasosSubscriptionSummaryReport;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.PetasosMonitoredTopologyGraph;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.JGroupsIntegrationPointIdentifier;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.petasos.endpoints.services.metrics.PetasosOAMMetricsEndpointBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class PetasosOAMMetricsCollectorEndpoint extends PetasosOAMMetricsEndpointBase {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosOAMMetricsCollectorEndpoint.class);

    @Inject
    private PetasosMetricsHandlerInterface metricsHandler;

    @Inject
    PetasosTopologyReportingHandlerInterface topologyHandler;

    @Inject
    private PetasosSubscriptionReportHandlerInterface subscriptionHandler;

    //
    // Constructor(s)
    //

    public PetasosOAMMetricsCollectorEndpoint(){
        super();
    }

    //
    // PostConstruct Activities
    //

    @Override
    protected void executePostConstructActivities() {

    }

    //
    // Getters (and Setters)
    //

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    //
    // Metrics Services
    //

    public Instant captureMetric(String serviceProviderName, PetasosComponentMetric metric) {
        getLogger().info(".captureMetric(): Entry, serviceProviderName->{}, metric->{}", serviceProviderName, metric);

        Instant captureInstant = metricsHandler.replicateMetricToServerHandler(metric, createSummary(getJGroupsIntegrationPoint()));

        getLogger().debug(".captureMetric(): Exit, captureInstant->{}", captureInstant);
        return(captureInstant);
    }

    public Instant captureMetrics(String serviceProviderName, PetasosComponentMetricSet metricSet) {
        getLogger().debug(".captureMetrics(): Entry, serviceProviderName->{}, metricSet->{}", serviceProviderName, metricSet);

        Instant captureInstant = metricsHandler.replicateMetricSetToServerHandler( metricSet, createSummary(getJGroupsIntegrationPoint()));

        getLogger().debug(".captureMetrics(): Exit, captureInstant->{}", captureInstant);
        return(captureInstant);
    }


    public Instant captureMetric(PetasosComponentMetric metric, JGroupsIntegrationPointSummary integrationPoint) {
        Instant captureInstance = metricsHandler.replicateMetricToServerHandler(metric, integrationPoint);
        return (captureInstance);
    }

    public Instant captureMetrics(PetasosComponentMetricSet metricSet, JGroupsIntegrationPointSummary integrationPoint) {
        Instant captureInstance = metricsHandler.replicateMetricSetToServerHandler(metricSet, integrationPoint);
        return (captureInstance);
    }

    //
    // Capture Subscription Reports
    //

    public Instant shareSubscriptionSummaryReport(String targetName, PetasosSubscriptionSummaryReport summaryReport) {
        getLogger().info(".shareSubscriptionSummaryReport(): Entry, summaryReport->{}", summaryReport);
        Instant instant = subscriptionHandler.replicateSubscriptionSummaryReportHandler(summaryReport, createSummary(getJGroupsIntegrationPoint()));
        return(instant);
    }

    public Instant shareSubscriptionSummaryReport(PetasosSubscriptionSummaryReport summaryReport, JGroupsIntegrationPointSummary integrationPoint) {
        Instant instant = subscriptionHandler.replicateSubscriptionSummaryReportHandler(summaryReport, integrationPoint);
        return(instant);
    }

    public Instant replicateSubscriptionSummaryReportHandler(PetasosSubscriptionSummaryReport summaryReport, JGroupsIntegrationPointSummary integrationPoint){
        Instant instant = subscriptionHandler.replicateSubscriptionSummaryReportHandler(summaryReport, integrationPoint);
        return(instant);
    }

    //
    // Topology Reporting
    //

    public Instant topologyGraphHandler(PetasosMonitoredTopologyGraph topologyGraph, JGroupsIntegrationPointSummary integrationPoint){
        getLogger().trace(".topologyGraphHandler(): Entry, topologyGraph->{}, integrationPoint->{}", topologyGraph, integrationPoint);
        Instant outcomeInstant = null;
        if((topologyGraph != null) && (integrationPoint != null)) {
            outcomeInstant = topologyHandler.mergeTopologyGraph(integrationPoint, topologyGraph);
        }
        getLogger().debug(".topologyGraphHandler(): Exit, outcomeInstant->{}", outcomeInstant);
        return(outcomeInstant);
    }

}
