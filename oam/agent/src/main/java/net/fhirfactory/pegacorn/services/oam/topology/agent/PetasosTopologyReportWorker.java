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
package net.fhirfactory.pegacorn.services.oam.topology.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.core.interfaces.capabilities.CapabilityProviderNameServiceInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.PetasosMonitoredTopologyGraph;
import net.fhirfactory.pegacorn.deployment.names.capabilities.CapabilityProviderNameServiceInterface;
import net.fhirfactory.pegacorn.deployment.names.capabilities.CapabilityProviderTitlesEnum;
import net.fhirfactory.pegacorn.petasos.endpoints.InterProcessingPlantTopologyServicesBroker;
import net.fhirfactory.pegacorn.petasos.oam.topology.PetasosMonitoredTopologyReportingAgent;
import net.fhirfactory.pegacorn.petasos.oam.topology.cache.PetasosLocalTopologyReportingDM;
import net.fhirfactory.pegacorn.services.oam.metrics.agent.PetasosMetricsAgentWorker;
import net.fhirfactory.pegacorn.services.oam.monitoring.metrics.management.common.ITOpsReportForwarderCommon;
import net.fhirfactory.pegacorn.petasos.itops.caches.ITOpsTopologyLocalDM;
import net.fhirfactory.pegacorn.petasos.itops.caches.common.ITOpsLocalDMRefreshBase;
import net.fhirfactory.pegacorn.petasos.itops.collectors.ITOpsTopologyCollectionAgent;
import net.fhirfactory.pegacorn.petasos.itops.valuesets.ITOpsCapabilityNamesEnum;
import net.fhirfactory.pegacorn.petasos.model.itops.topology.ITOpsTopologyGraph;
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
public class PetasosTopologyReportWorker {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosTopologyReportWorker.class);
    private boolean initialised;

    @Inject
    private PetasosLocalTopologyReportingDM itOpsTopologyDM;

    @Inject
    private PetasosMonitoredTopologyReportingAgent topologyReportingAgent;

    @Inject
    private InterProcessingPlantTopologyServicesBroker topologyServicesBroker;

    @Inject
    private ProcessingPlantInterface processingPlant;

    public PetasosTopologyReportWorker(){
        super();
        this.initialised = false;
    }

    @PostConstruct
    public void initialise(){
        if(!initialised){
            if(!getProcessingPlant().isITOpsNode()) {
                scheduleMetricsForwarding();
            }
            this.initialised = true;
        }
    }

    protected Logger getLogger() {
        return (LOG);
    }


    protected ProcessingPlantInterface getProcessingPlant(){
        return(processingPlant);
    }


    protected void forwardTopologyDetails() {
        LOG.debug(".forwardTopologyDetails(): Entry");
        //
        // Building Map
        //
        LOG.trace(".forwardTopologyDetails(): [Build Topology Graph] Start");
        topologyReportingAgent.refreshLocalTopologyGraph();
        LOG.trace(".forwardTopologyDetails(): [Build Topology Graph] Finish");
        //
        // Get Data to be Reported ON
        //
        LOG.trace(".forwardTopologyDetails(): [Grab Current Topology Graph] Start");
        PetasosMonitoredTopologyGraph currentState = itOpsTopologyDM.getCurrentState();
        currentState.setDeploymentName(getProcessingPlant().getSolutionNode().getComponentRDN().getNodeName());
        LOG.trace(".forwardTopologyDetails(): [Grab Current Topology Graph] Finish");
        //
        // Build Query
        //
        LOG.trace(".forwardTopologyDetails(): [Create Task] Start");
        CapabilityUtilisationRequest task = new CapabilityUtilisationRequest();
        task.setRequestID(UUID.randomUUID().toString());
        String topologyGraphAsJSONString = convertToJSONString(currentState);
        if(StringUtils.isEmpty(topologyGraphAsJSONString)){
            LOG.debug(".forwardTopologyDetails(): Exit");
            return;
        }
        task.setRequestContent(topologyGraphAsJSONString);
        task.setRequiredCapabilityName(ITOpsCapabilityNamesEnum.IT_OPS_TOPOLOGY_REPORT_COLLATOR.getCapabilityName());
        task.setRequestDate(Instant.now());
        LOG.trace(".forwardTopologyDetails(): [Create Task] Finish");
        //
        // Do Query
        //
        LOG.trace(".forwardTopologyDetails(): [Execute RPC Call] Start");
        String serviceProvider = capabilityProviderNameResolver.resolveCapabilityServiceProvider(CapabilityProviderTitlesEnum.CAPABILITY_INFORMATION_MANAGEMENT_IT_OPS);
        CapabilityUtilisationResponse response = getCapabilityUtilisationBroker().executeTask(serviceProvider, task);
        LOG.trace(".forwardTopologyDetails(): [Execute RPC Call] Finish");
        //
        // Extract the response
        //
        if(response == null){
            LOG.error(".forwardTopologyDetails(): Problem updating Topology details with the Information Manager");
            return;
        }
        if(response.isSuccessful()){
            LOG.debug(".forwardTopologyDetails(): Exit, Topology details updated with the Information Manager");
            itOpsTopologyDM.stateReported();
            return;
        } else {
            LOG.error(".forwardTopologyDetails(): Exit, Unsuccesful attemt at updating Topology details with the Information Manager");
            return;
        }
    }


    private String convertToJSONString(ITOpsTopologyGraph graph){
        try {
            String reportString = getJsonMapper().writeValueAsString(graph);
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
    public void scheduleMetricsForwarding() {

        getLogger().debug(".scheduleITOpsBackgroundSynchronisationTask(): Entry");
        if(isBackgroundCheckInitiated()){
            // do nothing
        } else {
            TimerTask ITOpsTopologyCacheSynchronisationCheck = new TimerTask() {
                public void run() {
                    getLogger().debug(".ITOpsCacheSynchronisationCheck(): Entry");
                    forwardTopologyDetails();
                    getLogger().debug(".ITOpsCacheSynchronisationCheck(): Exit");
                }
            };
            String timerName = "ITOpsTopologyCacheSynchronisationCheck";
            Timer timer = new Timer(timerName);
            timer.schedule(ITOpsTopologyCacheSynchronisationCheck, getInitialCheckDelayPeriod(), getSynchronizationCheckPeriod());
            setBackgroundCheckInitiated(true);
        }
        getLogger().debug(".scheduleITOpsBackgroundSynchronisationTask(): Exit");
    }
}
