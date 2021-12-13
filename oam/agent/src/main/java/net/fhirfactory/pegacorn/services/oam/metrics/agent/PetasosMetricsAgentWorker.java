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
package net.fhirfactory.pegacorn.services.oam.metrics.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.pegacorn.core.interfaces.oam.metrics.PetasosMetricsHandlerInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.CapabilityUtilisationBrokerInterface;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.PetasosComponentMetric;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.PetasosComponentMetricSet;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.deployment.names.subsystems.SubsystemNames;
import net.fhirfactory.pegacorn.internals.SerializableObject;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.PetasosOAMMetricsEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.*;

@ApplicationScoped
public class PetasosMetricsAgentWorker implements PetasosMetricsHandlerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosMetricsAgentWorker.class);

    private ArrayList<PetasosComponentMetric> metricsQueue;
    private SerializableObject metricQueueLock = new SerializableObject();

    private static long SYNCHRONIZATION_CHECK_PERIOD = 30000;
    private static long INITIAL_CHECK_DELAY_PERIOD=60000;
    private boolean backgroundCheckInitiated;
    private ObjectMapper jsonMapper;
    private boolean initialised;

    @Inject
    private CapabilityUtilisationBrokerInterface capabilityUtilisationBroker;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private PetasosOAMMetricsEndpoint metricsServicesBroker;

    @Inject
    private SubsystemNames subsystemNames;

    //
    // Constructor(s)
    //

    public PetasosMetricsAgentWorker(){
        this.initialised = false;
        this.metricsQueue = new ArrayList<>();
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

    protected void forwardMetrics(){
        LOG.debug(".forwardMetricsReports(): Entry");
        List<PetasosComponentMetric> allMetrics = new ArrayList<>();
        synchronized (metricQueueLock) {
            allMetrics.addAll(metricsQueue);
            metricsQueue.clear();
        }
        boolean metricsUpdateFailed = false;
        for(PetasosComponentMetric currentMetric: allMetrics){
            Instant captureInstant = metricsServicesBroker.captureMetric(subsystemNames.getITOpsIMSystemName(), currentMetric);
            if(captureInstant == null){
                metricsUpdateFailed = true;
                break;
            }
        }
        if(metricsUpdateFailed){
            LOG.debug(".forwardMetricsReports(): Exit, failed to update");
        } else {
            LOG.debug(".forwardMetricsReports(): Exit, Update successful");
        }
    }

    //
    // Schedule Period Synchronisation Check/Update Activity
    //

    public void scheduleMetricsSynchronisation() {

        getLogger().debug(".scheduleMetricsSynchronisation(): Entry");
        if(isBackgroundCheckInitiated()){
            // do nothing
        } else {
            TimerTask MetricsCacheSynchronisation = new TimerTask() {
                public void run() {
                    getLogger().debug(".MetricsCacheSynchronisation(): Entry");
                    forwardMetrics();
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

    @Override
    public Instant captureMetric(PetasosComponentMetric metric, PetasosEndpointIdentifier endpointIdentifier) {
        return null;
    }

    @Override
    public Instant captureMetrics(PetasosComponentMetricSet metricSet, PetasosEndpointIdentifier endpointIdentifier) {
        return null;
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger(){
        return(LOG);
    }

    public boolean isBackgroundCheckInitiated() {
        return backgroundCheckInitiated;
    }

    public void setBackgroundCheckInitiated(boolean backgroundCheckInitiated) {
        this.backgroundCheckInitiated = backgroundCheckInitiated;
    }

    public static long getSynchronizationCheckPeriod() {
        return SYNCHRONIZATION_CHECK_PERIOD;
    }

    public static long getInitialCheckDelayPeriod() {
        return INITIAL_CHECK_DELAY_PERIOD;
    }

    public CapabilityUtilisationBrokerInterface getCapabilityUtilisationBroker() {
        return capabilityUtilisationBroker;
    }

    public ObjectMapper getJsonMapper() {
        return jsonMapper;
    }

    public ProcessingPlantInterface getProcessingPlant() {
        return processingPlant;
    }

}
