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
package net.fhirfactory.pegacorn.services.oam.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.CapabilityUtilisationBrokerInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.EndpointMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.ProcessingPlantMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.WorkUnitProcessorMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.common.CommonComponentMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.PetasosComponentMetricSet;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.factories.PetasosComponentMetricSetFactory;
import net.fhirfactory.pegacorn.deployment.names.subsystems.SubsystemNames;
import net.fhirfactory.pegacorn.internals.SerializableObject;
import net.fhirfactory.pegacorn.petasos.oam.metrics.cache.PetasosLocalMetricsDM;
import net.fhirfactory.pegacorn.services.oam.agent.common.AgentWorkerBase;
import net.fhirfactory.pegacorn.services.oam.endpoint.PetasosOAMMetricsAgentEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PetasosMetricsAgentWorker extends AgentWorkerBase {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosMetricsAgentWorker.class);

    private ConcurrentHashMap<ComponentIdType, PetasosComponentMetricSet> metricsQueue;
    private SerializableObject metricQueueLock = new SerializableObject();

    private static long SYNCHRONIZATION_CHECK_PERIOD = 15000;
    private static long INITIAL_CHECK_DELAY_PERIOD=60000;
    private boolean backgroundCheckInitiated;
    private ObjectMapper jsonMapper;
    private boolean initialised;

    @Inject
    private CapabilityUtilisationBrokerInterface capabilityUtilisationBroker;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private PetasosOAMMetricsAgentEndpoint metricsServicesBroker;

    @Inject
    private SubsystemNames subsystemNames;

    @Inject
    private PetasosLocalMetricsDM metricsDM;

    @Inject
    private PetasosComponentMetricSetFactory componentMetricSetFactory;

    //
    // Constructor(s)
    //

    public PetasosMetricsAgentWorker(){
        this.initialised = false;
        this.metricsQueue = new ConcurrentHashMap<>();
        this.backgroundCheckInitiated = false;
        this.jsonMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        this.jsonMapper.registerModule(module);
        this.jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    //
    // Post Construct
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(!initialised){
            getLogger().info(".initialise(): Initialising....");
            if(!getProcessingPlant().isITOpsNode()) {
                getLogger().info(".initialise(): [Scheduling Background Synchronisation] Start");
                scheduleMetricsSynchronisation();
                getLogger().info(".initialise(): [Scheduling Background Synchronisation] Finish");
            }
            this.initialised = true;
            getLogger().info(".initialise(): Done.");
        } else {
            getLogger().debug(".initialise(): Already initialised, nothing to do!");
        }
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Actual Daemon Tasks
    //

    protected void captureLocalMetrics(){
        getLogger().debug(".captureLocalMetrics(): Entry");
        //
        // Get Metrics from the Cache
        List<CommonComponentMetricsData> allLocalMetricsSets = getMetricsDM().getAllMetricsSets();
        getLogger().trace(".captureLocalMetrics(): Iterating through Metrics retrieved from local cache");
        for(CommonComponentMetricsData currentMetrics: allLocalMetricsSets){
            PetasosComponentMetricSet metricSet = null;
            ComponentIdType componentId = currentMetrics.getComponentID();
            getLogger().trace(".captureLocalMetrics(): Iterating through Metrics (Processing->{})", componentId);
            if(currentMetrics instanceof ProcessingPlantMetricsData){
                getLogger().trace(".captureLocalMetrics(): Iterating through Metrics (Is a ProcessingPlant)");
                ProcessingPlantMetricsData plantMetricsData = (ProcessingPlantMetricsData) currentMetrics;
                metricSet = getComponentMetricSetFactory().convertProcessingPlantMetricsData(plantMetricsData);
            }
            if(currentMetrics instanceof  WorkUnitProcessorMetricsData){
                getLogger().trace(".captureLocalMetrics(): Iterating through Metrics (Is a WorkUnitProcessor)");
                WorkUnitProcessorMetricsData wupMetricsData = (WorkUnitProcessorMetricsData) currentMetrics;
                metricSet = getComponentMetricSetFactory().convertWorkUnitProcessorMetricsData(wupMetricsData);
            }
            if(currentMetrics instanceof EndpointMetricsData){
                getLogger().trace(".captureLocalMetrics(): Iterating through Metrics (Is a WorkUnitProcessor)");
                EndpointMetricsData endpointMetricsData = (EndpointMetricsData) currentMetrics;
                metricSet = getComponentMetricSetFactory().convertEndpointMetricsData(endpointMetricsData);
            }
            if(metricSet != null) {
                synchronized (getMetricQueueLock()) {
                    if (getMetricsQueue().containsKey(componentId)) {
                        getMetricsQueue().remove(componentId);
                    }
                    getMetricsQueue().put(componentId, metricSet);
                }
            }
        }
        getLogger().debug(".captureLocalMetrics(): Exit");
    }

    protected void forwardLocalMetricsToServer(){
        LOG.debug(".forwardLocalMetricsToServer(): Entry");
        List<PetasosComponentMetricSet> allMetrics = new ArrayList<>();
        LOG.trace(".forwardLocalMetricsToServer(): Number of MetricSets to processing->{}", metricsQueue.size());
        synchronized (metricQueueLock) {
            allMetrics.addAll(metricsQueue.values());
            metricsQueue.clear();
        }
        LOG.trace(".forwardLocalMetricsToServer(): Loaded metrics form local cache, forwarding");
        boolean metricsUpdateFailed = false;
        for(PetasosComponentMetricSet currentMetric: allMetrics){
            LOG.trace(".forwardLocalMetricsToServer(): Sending metrics for component->{}", currentMetric.getMetricSourceComponentId());
            Instant captureInstant = metricsServicesBroker.replicateMetricSetToServer(subsystemNames.getITOpsIMParticipantName(), currentMetric);
            if(captureInstant == null){
                metricsUpdateFailed = true;
                synchronized (metricQueueLock){
                    if(!metricsQueue.containsKey(currentMetric.getMetricSourceComponentId())) {
                        metricsQueue.put(currentMetric.getMetricSourceComponentId(), currentMetric);
                    }
                }
            }
        }
        if(metricsUpdateFailed){
            LOG.info(".forwardLocalMetricsToServer(): Exit, failed to update");
        } else {
            LOG.debug(".forwardLocalMetricsToServer(): Exit, Update successful");
        }
    }

    //
    // Schedule Period Synchronisation Check/Update Activity
    //

    protected void scheduleMetricsSynchronisation() {

        getLogger().debug(".scheduleMetricsSynchronisation(): Entry");
        if(isBackgroundCheckInitiated()){
            // do nothing
        } else {
            TimerTask MetricsCacheSynchronisation = new TimerTask() {
                public void run() {
                    getLogger().debug(".MetricsCacheSynchronisation(): Entry");
                    metricsSynchronisationDaemon();
                    getLogger().debug(".MetricsCacheSynchronisation(): Exit");
                }
            };
            String timerName = "MetricsCacheSynchronisation";
            Timer timer = new Timer(timerName);
            timer.schedule(MetricsCacheSynchronisation, getInitialCheckDelayPeriod(), getSynchronizationCheckPeriod());
            setBackgroundCheckInitiated(true);
        }
        getLogger().debug(".scheduleMetricsSynchronisation(): Exit");
    }

    protected void metricsSynchronisationDaemon(){
        getLogger().debug(".scheduleMetricsSynchronisation(): Entry");
        captureLocalMetrics();
        forwardLocalMetricsToServer();
        getLogger().debug(".scheduleMetricsSynchronisation(): Exit");
    }

    //
    // Getters (and Setters)
    //

    @Override
    protected String getFriendlyName() {
        return ("PetasosMetricsAgent");
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected boolean isBackgroundCheckInitiated() {
        return backgroundCheckInitiated;
    }

    protected void setBackgroundCheckInitiated(boolean backgroundCheckInitiated) {
        this.backgroundCheckInitiated = backgroundCheckInitiated;
    }

    protected static long getSynchronizationCheckPeriod() {
        return SYNCHRONIZATION_CHECK_PERIOD;
    }

    protected static long getInitialCheckDelayPeriod() {
        return INITIAL_CHECK_DELAY_PERIOD;
    }

    protected CapabilityUtilisationBrokerInterface getCapabilityUtilisationBroker() {
        return capabilityUtilisationBroker;
    }

    protected ObjectMapper getJsonMapper() {
        return jsonMapper;
    }

    protected ProcessingPlantInterface getProcessingPlant() {
        return processingPlant;
    }

    protected PetasosLocalMetricsDM getMetricsDM() {
        return metricsDM;
    }

    protected PetasosComponentMetricSetFactory getComponentMetricSetFactory() {
        return componentMetricSetFactory;
    }

    protected SerializableObject getMetricQueueLock() {
        return metricQueueLock;
    }

    protected ConcurrentHashMap<ComponentIdType, PetasosComponentMetricSet> getMetricsQueue() {
        return metricsQueue;
    }
}
