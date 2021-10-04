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
package net.fhirfactory.pegacorn.petasos.endpoints.itops.forwarders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.deployment.names.capabilities.CapabilityProviderNameServiceInterface;
import net.fhirfactory.pegacorn.deployment.names.capabilities.CapabilityProviderTitlesEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.itops.forwarders.common.ITOpsReportForwarderCommon;
import net.fhirfactory.pegacorn.petasos.itops.caches.ITOpsMetricsLocalDM;
import net.fhirfactory.pegacorn.petasos.itops.caches.common.ITOpsLocalDMRefreshBase;
import net.fhirfactory.pegacorn.petasos.itops.valuesets.ITOpsCapabilityNamesEnum;
import net.fhirfactory.pegacorn.petasos.model.itops.metrics.ITOpsMetricsSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@ApplicationScoped
public class ITOpsMetricsReportForwarder extends ITOpsReportForwarderCommon {
    private static final Logger LOG = LoggerFactory.getLogger(ITOpsMetricsReportForwarder.class);

    private boolean initialised;

    @Inject
    private ITOpsMetricsLocalDM metricsDM;

    @Inject
    private CapabilityProviderNameServiceInterface capabilityProviderNameResolver;

    public ITOpsMetricsReportForwarder() {
        super();
        this.initialised = false;
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected ITOpsLocalDMRefreshBase specifyLocalDM() {
        return (metricsDM);
    }

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(!initialised){
            getLogger().info(".initialise(): Initialising....");
            if(!getProcessingPlant().isITOpsNode()) {
                getLogger().info(".initialise(): [Scheduling Background Synchronisation] Start");
                scheduleITOpsBackgroundSynchronisationTask();
                getLogger().info(".initialise(): [Scheduling Background Synchronisation] Finish");
            }
            this.initialised = true;
            getLogger().info(".initialise(): Done.");
        } else {
            getLogger().debug(".initialise(): Already initialised, nothing to do!");
        }
        getLogger().debug(".initialise(): Exit");
    }
    
    protected void forwardMetricsReports(){
        LOG.debug(".forwardMetricsReports(): Entry");
        List<ITOpsMetricsSet> allMetricsSets = metricsDM.getAllMetricsSets();
        boolean metricsUpdateFailed = false;
        for(ITOpsMetricsSet currentMetricsSet: allMetricsSets){
            boolean metricsUpdateSent = sendMetricsUpdate(currentMetricsSet);
            if(!metricsUpdateSent){
                metricsUpdateFailed = true;
                break;
            }
        }
        if(allMetricsSets.isEmpty()){
            metricsUpdateFailed = false;
        }
        if(metricsUpdateFailed){
            LOG.debug(".forwardMetricsReports(): Exit, failed to update");
        } else {
            metricsDM.refreshReportedStateUpdateInstant();
            LOG.debug(".forwardMetricsReports(): Exit, Update successful");
        }
    }

    protected boolean sendMetricsUpdate(ITOpsMetricsSet metricsSet) {
        LOG.debug(".sendMetricsUpdate(): Entry");
        //
        // Build Query
        //
        CapabilityUtilisationRequest task = new CapabilityUtilisationRequest();
        task.setRequestID(UUID.randomUUID().toString());
        String metricsSetString = convertToJSONString(metricsSet);
        if(StringUtils.isEmpty(metricsSetString)){
            return(false);
        }
        task.setRequestContent(metricsSetString);
        task.setRequiredCapabilityName(ITOpsCapabilityNamesEnum.IT_OPS_METRICS_REPORT_COLLATOR.getCapabilityName());
        task.setRequestDate(Instant.now());
        //
        // Do Query
        //
        String serviceProvider = capabilityProviderNameResolver.resolveCapabilityServiceProvider(CapabilityProviderTitlesEnum.CAPABILITY_INFORMATION_MANAGEMENT_IT_OPS);
        CapabilityUtilisationResponse response = getCapabilityUtilisationBroker().executeTask(serviceProvider, task);
        //
        // Extract the response
        //
        if(response == null){
            LOG.error(".sendMetricsUpdate(): Problem updating Metrics details with the Information Manager");
            return(false);
        }
        if(response.isSuccessful()){
            LOG.debug(".sendMetricsUpdate(): Exit, Metrics details updated with the Information Manager");
            return(true);
        } else {
            LOG.error(".sendMetricsUpdate(): Exit, Unsuccesful attemt at updating Metrics details with the Information Manager");
            return(false);
        }
    }

    private String convertToJSONString(ITOpsMetricsSet metricsSet){
        try {
            String metricsSetString = getJsonMapper().writeValueAsString(metricsSet);
            return(metricsSetString);
        } catch (JsonProcessingException e) {
            LOG.error(".convertToJSONString(): Unable to convert ->{}",e);
            return(null);
        }
    }

    //
    // Schedule Period Synchronisation Check/Update Activity
    //

    @Override
    public void scheduleITOpsBackgroundSynchronisationTask() {

        getLogger().debug(".scheduleITOpsBackgroundSynchronisationTask(): Entry");
        if(isBackgroundCheckInitiated()){
            // do nothing
        } else {
            TimerTask ITOpsMetricsCacheSynchronisationCheck = new TimerTask() {
                public void run() {
                    getLogger().debug(".ITOpsCacheSynchronisationCheck(): Entry");
                    forwardMetricsReports();
                    getLogger().debug(".ITOpsCacheSynchronisationCheck(): Exit");
                }
            };
            String timerName = "ITOpsMetricsCacheSynchronisationCheck";
            Timer timer = new Timer(timerName);
            timer.schedule(ITOpsMetricsCacheSynchronisationCheck, getInitialCheckDelayPeriod(), getSynchronizationCheckPeriod());
            setBackgroundCheckInitiated(true);
        }
        getLogger().debug(".scheduleITOpsBackgroundSynchronisationTask(): Exit");
    }
}
