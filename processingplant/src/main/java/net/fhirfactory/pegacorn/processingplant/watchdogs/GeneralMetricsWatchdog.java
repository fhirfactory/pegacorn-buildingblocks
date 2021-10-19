/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.processingplant.watchdogs;

import net.fhirfactory.pegacorn.components.topology.interfaces.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.components.metrics.ProcessingPlantLocalCacheMetricsReportingInterface;
import net.fhirfactory.pegacorn.petasos.datasets.cache.DataParcelSubscriptionMapDM;
import net.fhirfactory.pegacorn.petasos.datasets.cache.DistributedPubSubSubscriptionMapDM;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class GeneralMetricsWatchdog extends RouteBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(GeneralMetricsWatchdog.class);

    private boolean initialised;
    private boolean taskScheduled;

    private static Long WATCHDOG_INITIAL_DELAY = 60000L;
    private static Long WATCHDOG_CHECK_PERIOD = 10000L;

    @Inject
    private ProcessingPlantLocalCacheMetricsReportingInterface metricsAgent;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    DataParcelSubscriptionMapDM subscriptionMapDM;

    @Inject
    private DistributedPubSubSubscriptionMapDM registrationMapDM;

    //
    // Constructor
    //

    public GeneralMetricsWatchdog(){
        this.initialised = false;
        this.taskScheduled = false;
    }

    //
    // Post Construct Activities
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(isInitialised()){
            getLogger().debug(".initialise(): Nothing to do, already initialised");
        } else {
            getLogger().info(".initialise(): Initialising...");
            getLogger().info(".initialise(): [Scheduling ProcessingPlant General Metrics Watchdog] Start");
            scheduleMetricsWatchdog();
            getLogger().info(".initialise(): [Scheduling ProcessingPlant General Metrics Watchdog] Finish");
            this.initialised = true;
            getLogger().info(".initialise(): Done...");
        }
        getLogger().debug(".initialise(): Exit");
    }


    //
    // Getters (and Setters)
    //

    public Logger getLogger(){
        return(LOG);
    }

    public boolean isInitialised() {
        return initialised;
    }

    public boolean isTaskScheduled() {
        return taskScheduled;
    }

    public static Long getWatchdogInitialDelay() {
        return WATCHDOG_INITIAL_DELAY;
    }

    public static Long getWatchdogCheckPeriod() {
        return WATCHDOG_CHECK_PERIOD;
    }

    //
    // Watchdog Scheduler
    //

    protected void scheduleMetricsWatchdog(){
        getLogger().debug(".scheduleMetricsWatchdog(): Entry");
        if(isTaskScheduled()){
            // do nothing
        } else {
            TimerTask processingPlantGeneralMetricsWatchDogTask = new TimerTask() {
                public void run() {
                    getLogger().debug(".processingPlantGeneralMetricsWatchDogTask(): Entry");
                    metricsWatchdog();
                    getLogger().debug(".processingPlantGeneralMetricsWatchDogTask(): Exit");
                }
            };
            String timerName = "ProcessingPlantGeneralMetricsWatchDogTask";
            Timer timer = new Timer(timerName);
            timer.schedule(processingPlantGeneralMetricsWatchDogTask, getWatchdogInitialDelay(), getWatchdogCheckPeriod());
            this.taskScheduled = true;
        }
        getLogger().debug(".scheduleMetricsWatchdog(): Exit");
    }


    protected void metricsWatchdog(){
        metricsAgent.updatedLocalCacheStatus(processingPlant.getProcessingPlantNode().getComponentType(), subscriptionMapDM.getCacheName(), subscriptionMapDM.getMetrics());
        metricsAgent.updatedLocalCacheStatus(processingPlant.getProcessingPlantNode().getComponentType(), registrationMapDM.getCacheName(), registrationMapDM.getMetrics());
    }


    //
    // Needed to initiate Component
    //

    @Override
    public void configure() throws Exception {
        String className = getClass().getSimpleName();

        from("timer://"+className+"?delay=1000&repeatCount=1")
                .routeId(className)
                .log(LoggingLevel.DEBUG, "Starting....");
    }
}
