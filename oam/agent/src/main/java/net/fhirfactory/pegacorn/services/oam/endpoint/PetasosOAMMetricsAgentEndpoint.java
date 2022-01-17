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

import net.fhirfactory.pegacorn.core.interfaces.oam.metrics.PetasosMetricsBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.oam.notifications.PetasosITOpsNotificationBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.oam.subscriptions.PetasosSubscriptionReportBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.oam.topology.PetasosTopologyReportingBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.oam.topology.PetasosTopologyReportingServiceProviderNameInterface;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.PetasosComponentMetric;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.PetasosComponentMetricSet;
import net.fhirfactory.pegacorn.core.model.petasos.oam.notifications.PetasosComponentITOpsNotification;
import net.fhirfactory.pegacorn.core.model.petasos.oam.subscriptions.reporting.PetasosSubscriptionSummaryReport;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.reporting.PetasosMonitoredTopologyGraph;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.services.metrics.PetasosOAMMetricsEndpointBase;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

@ApplicationScoped
public class PetasosOAMMetricsAgentEndpoint extends PetasosOAMMetricsEndpointBase
    implements PetasosMetricsBrokerInterface,
        PetasosSubscriptionReportBrokerInterface,
        PetasosTopologyReportingBrokerInterface,
        PetasosITOpsNotificationBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosOAMMetricsAgentEndpoint.class);

    @Inject
    private PetasosTopologyReportingServiceProviderNameInterface topologyReportingProvider;

    //
    // Constructor(s)
    //

    public PetasosOAMMetricsAgentEndpoint(){
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
    // Metrics (Client) RPC Method Support
    //

    @Override
    public Instant replicateMetricSetToServer(String serviceProviderName, PetasosComponentMetricSet metricSet){
        getLogger().debug(".replicateMetricSetToServer(): Entry, serviceProviderName->{}, metricSet->{}", serviceProviderName, metricSet);
        JGroupsIntegrationPointSummary myIntegrationPoint = createSummary(getJGroupsIntegrationPoint());
        PetasosAdapterAddress targetPetasosAddress = getTargetMemberAdapterInstanceForSubsystem(serviceProviderName);
        if(targetPetasosAddress == null){
            getLogger().warn(".replicateMetricSetToServer(): No Metrics Server available");
            return(Instant.now());
        }
        Address targetAddress = targetPetasosAddress.getJGroupsAddress();
        if(targetAddress == null){
            getLogger().warn(".replicateMetricSetToServer(): No Metrics Server available");
            return(Instant.now());
        }
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = metricSet;
            classSet[0] = PetasosComponentMetricSet.class;
            objectSet[1] = myIntegrationPoint;
            classSet[1] = JGroupsIntegrationPointSummary.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Instant response = getRPCDispatcher().callRemoteMethod(targetAddress, "captureMetrics", objectSet, classSet, requestOptions);
            getLogger().info(".replicateMetricSetToServer(): Exit, response->{}", response);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            return(response);
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".replicateMetricSetToServer(): Error (NoSuchMethodException) Message->{}, StackTrace->{} ", ExceptionUtils.getMessage(e), ExceptionUtils.getStackTrace(e));
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".replicateMetricSetToServer(): Error (GeneralException) Message->{}, StackTrace->{} ", ExceptionUtils.getMessage(e), ExceptionUtils.getStackTrace(e));
            return(null);
        }
    }

    @Override
    public Instant replicateMetricToServer(String serviceProviderName, PetasosComponentMetric metric){
        getLogger().trace(".replicateMetricToServer(): Entry, serviceProviderName->{}, metric->{}", serviceProviderName, metric);
        JGroupsIntegrationPointSummary myIntegrationPoint = createSummary(getJGroupsIntegrationPoint());
        PetasosAdapterAddress targetPetasosAddress = getTargetMemberAdapterInstanceForSubsystem(serviceProviderName);
        if(targetPetasosAddress == null){
            getLogger().warn(".replicateMetricSetToServer(): No Metrics Server available");
            return(Instant.now());
        }
        Address targetAddress = targetPetasosAddress.getJGroupsAddress();
        if(targetAddress == null){
            getLogger().warn(".replicateMetricToServer(): No Metrics Server available");
            return(Instant.now());
        }
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = metric;
            classSet[0] = PetasosComponentMetric.class;
            objectSet[1] = myIntegrationPoint;
            classSet[1] = JGroupsIntegrationPointSummary.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Instant responseInstant = getRPCDispatcher().callRemoteMethod(targetAddress, "captureMetric", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".replicateMetricToServer(): Exit, responseInstant->{}", responseInstant);
            return(responseInstant);
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".replicateMetricToServer(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".replicateMetricToServer: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    //
    // Publish/Performer Reporting Services
    //

    @Override
    public Instant replicationSubscriptionSummaryReportToServer(String serviceProviderName, PetasosSubscriptionSummaryReport summaryReport) {
        getLogger().trace(".shareLocalSubscriptionSummaries(): Entry, serviceProviderName->{}, subscriptionSummaries->{}", serviceProviderName, summaryReport);
        JGroupsIntegrationPointSummary myIntegrationPoint = createSummary(getJGroupsIntegrationPoint());
        Address targetAddress = getCandidateTargetServiceAddress(serviceProviderName);
        if(targetAddress == null){
            getLogger().warn(".shareLocalTopologyGraph(): No Metrics Server available");
            return(Instant.now());
        }
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = summaryReport;
            classSet[0] = PetasosSubscriptionSummaryReport.class;
            objectSet[1] = myIntegrationPoint;
            classSet[1] = JGroupsIntegrationPointSummary.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Instant responseInstant = getRPCDispatcher().callRemoteMethod(targetAddress, "replicateSubscriptionSummaryReportHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".shareLocalSubscriptionSummaries(): Exit, responseInstant->{}", responseInstant);
            return(responseInstant);
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".shareLocalSubscriptionSummaries(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            e.printStackTrace();
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".shareLocalSubscriptionSummaries: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    //
    // Topology OAM Services
    //

    @Override
    public void reportTopologyMap(PetasosMonitoredTopologyGraph topologyGraph) {
        shareLocalTopologyGraph(topologyReportingProvider.getPetasosTopologyReportingServiceProviderName(), topologyGraph);
    }

    public Instant shareLocalTopologyGraph(String serviceProviderName, PetasosMonitoredTopologyGraph topologyGraph){
        getLogger().trace(".shareLocalTopologyGraph(): Entry, serviceProviderName->{}, topologyGraph->{}", serviceProviderName, topologyGraph);
        JGroupsIntegrationPointSummary myIntegrationPoint = createSummary(getJGroupsIntegrationPoint());
        Address targetAddress = getCandidateTargetServiceAddress(serviceProviderName);
        if(targetAddress == null){
            getLogger().warn(".shareLocalTopologyGraph(): No Metrics Server available");
            return(Instant.now());
        }
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = topologyGraph;
            classSet[0] = PetasosMonitoredTopologyGraph.class;
            objectSet[1] = myIntegrationPoint;
            classSet[1] = JGroupsIntegrationPointSummary.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Instant responseInstant = getRPCDispatcher().callRemoteMethod(targetAddress, "topologyGraphHandler", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".shareLocalTopologyGraph(): Exit, responseInstant->{}", responseInstant);
            return(responseInstant);
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".shareLocalTopologyGraph(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return(null);
        } catch (Exception e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            e.printStackTrace();
            getLogger().error(".shareLocalTopologyGraph: Error (GeneralException) ->{}", e.getMessage());
            return(null);
        }
    }

    //
    // Notifications Service
    //


    @Override
    public void sendNotification(PetasosComponentITOpsNotification notification) {
        getLogger().debug(".sendNotification(): Entry, notification->{}", notification);
        JGroupsIntegrationPointSummary myIntegrationPoint = createSummary(getJGroupsIntegrationPoint());
        Address targetAddress = getCandidateTargetServiceAddress(topologyReportingProvider.getPetasosTopologyReportingServiceProviderName());
        if(targetAddress == null){
            getLogger().warn(".sendNotification(): No Metrics Server available");
            return;
        }
        try {
            Object objectSet[] = new Object[2];
            Class classSet[] = new Class[2];
            objectSet[0] = notification;
            classSet[0] = PetasosComponentITOpsNotification.class;
            objectSet[1] = myIntegrationPoint;
            classSet[1] = JGroupsIntegrationPointSummary.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            getRPCDispatcher().callRemoteMethod(targetAddress, "receiveNotification", objectSet, classSet, requestOptions);
            getMetricsAgent().incrementRemoteProcedureCallCount();
            getLogger().debug(".sendNotification(): Exit, responseInstant");
            return;
        } catch (NoSuchMethodException e) {
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".sendNotification(): Error (NoSuchMethodException) ->{}", e.getMessage());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            getMetricsAgent().incrementRemoteProcedureCallFailureCount();
            getLogger().error(".sendNotification: Error (GeneralException) ->{}", e.getMessage());
            return;
        }
    }
}
