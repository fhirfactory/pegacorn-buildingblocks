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
package net.fhirfactory.pegacorn.oam.monitoring.metrics;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.deployment.names.capabilities.CapabilityProviderNameServiceInterface;
import net.fhirfactory.pegacorn.deployment.names.capabilities.CapabilityProviderTitlesEnum;
import net.fhirfactory.pegacorn.oam.monitoring.metrics.common.ITOpsReportForwarderCommon;
import net.fhirfactory.pegacorn.petasos.itops.caches.ITOpsPubSubMapLocalDM;
import net.fhirfactory.pegacorn.petasos.itops.caches.common.ITOpsLocalDMRefreshBase;
import net.fhirfactory.pegacorn.petasos.itops.collectors.ITOpsPubSubCollectionAgent;
import net.fhirfactory.pegacorn.petasos.itops.valuesets.ITOpsCapabilityNamesEnum;
import net.fhirfactory.pegacorn.petasos.model.itops.subscriptions.ITOpsPubSubReport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@ApplicationScoped
public class ITOpsPubSubReportForwarder extends ITOpsReportForwarderCommon {
    private static final Logger LOG = LoggerFactory.getLogger(ITOpsPubSubReportForwarder.class);
    private boolean initialised;

    @Inject
    private ITOpsPubSubMapLocalDM pubsubMapDM;

    @Inject
    private ITOpsPubSubCollectionAgent pubSubCollectionAgent;

    @Inject
    private CapabilityProviderNameServiceInterface capabilityProviderNameResolver;

    public ITOpsPubSubReportForwarder() {
        super();
        this.initialised = false;
    }

    @PostConstruct
    public void initialise(){
        if(!initialised){
            if(!getProcessingPlant().isITOpsNode()) {
                scheduleITOpsBackgroundSynchronisationTask();
            }
            this.initialised = true;
        }
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected ITOpsLocalDMRefreshBase specifyLocalDM() {
        return (pubsubMapDM);
    }

    protected void forwardPubSubReports() {
        LOG.debug(".forwardPubSubReports(): Entry");
        //
        // Build Map
        //
        LOG.trace(".forwardPubSubReports(): [Build ProcessingPlant PubSub Summary] Start");
        pubSubCollectionAgent.refreshLocalProcessingPlantPubSubMap();
        LOG.trace(".forwardPubSubReports(): [Build ProcessingPlant PubSub Summary] Finish");
        LOG.trace(".forwardPubSubReports(): [Build WorkUnitProcessor PubSub Summaries] Start");
        pubSubCollectionAgent.refreshWorkUnitProcessorPubSubMap();
        LOG.trace(".forwardPubSubReports(): [Build WorkUnitProcessor PubSub Summaries] Finish");
        //
        // Get Data to be Reported ON
        //
        LOG.trace(".forwardPubSubReports(): [Build WorkUnitProcessor PubSub Report] Start");
        ITOpsPubSubReport pubSubReport = pubsubMapDM.getPubSubReport();
        pubSubReport.setProcessingPlantComponentID(getProcessingPlant().getProcessingPlantNode().getComponentID());
        LOG.trace(".forwardPubSubReports(): [Build WorkUnitProcessor PubSub Report] Start");
        //
        // Build Task
        //
        LOG.trace(".forwardPubSubReports(): [Build Report Submission Task] Start");
        CapabilityUtilisationRequest task = new CapabilityUtilisationRequest();
        task.setRequestID(UUID.randomUUID().toString());
        String pubsubReportString = convertToJSONString(pubSubReport);
        if(StringUtils.isEmpty(pubsubReportString)){
            LOG.trace(".forwardPubSubReports(): [Build Report Submission Task] Terminate Activity, Nothing To Report");
            return;
        }
        task.setRequestContent(pubsubReportString);
        task.setRequiredCapabilityName(ITOpsCapabilityNamesEnum.IT_OPS_PUBSUB_REPORT_COLLATOR.getCapabilityName());
        task.setRequestDate(Instant.now());
        LOG.trace(".forwardPubSubReports(): [Build Report Submission Task] Finish");
        //
        // Execute Task
        //
        LOG.trace(".forwardPubSubReports(): [Execute Report Submission Task] Start");
        String serviceProvider = capabilityProviderNameResolver.resolveCapabilityServiceProvider(CapabilityProviderTitlesEnum.CAPABILITY_INFORMATION_MANAGEMENT_IT_OPS);
        CapabilityUtilisationResponse response = getCapabilityUtilisationBroker().executeTask(serviceProvider, task);
        LOG.trace(".forwardPubSubReports(): [Execute Report Submission Task] Finish");
        //
        // Extract the response
        //
        if(response == null){
            LOG.error(".forwardPubSubReports(): Problem updating PubSub details with the Information Manager");
            return;
        }
        if(response.isSuccessful()){
            LOG.debug(".forwardPubSubReports(): Exit, PubSub details updated with the Information Manager");
            pubsubMapDM.refreshReportedStateUpdateInstant();
            return;
        } else {
            LOG.error(".forwardPubSubReports(): Exit, Unsuccesful attemt at updating PubSub details with the Information Manager");
            return;
        }
    }

    private String convertToJSONString(ITOpsPubSubReport pubSubReport){
        try {
            String reportString = getJsonMapper().writeValueAsString(pubSubReport);
            return(reportString);
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
            TimerTask ITOpsPubSubCacheSynchronisationCheck = new TimerTask() {
                public void run() {
                    getLogger().debug(".ITOpsCacheSynchronisationCheck(): Entry");
                    forwardPubSubReports();
                    getLogger().debug(".ITOpsCacheSynchronisationCheck(): Exit");
                }
            };
            String timerName = "ITOpsPubSubCacheSynchronisationCheck";
            Timer timer = new Timer(timerName);
            timer.schedule(ITOpsPubSubCacheSynchronisationCheck, getInitialCheckDelayPeriod(), getSynchronizationCheckPeriod());
            setBackgroundCheckInitiated(true);
        }
        getLogger().debug(".scheduleITOpsBackgroundSynchronisationTask(): Exit");
    }
}
