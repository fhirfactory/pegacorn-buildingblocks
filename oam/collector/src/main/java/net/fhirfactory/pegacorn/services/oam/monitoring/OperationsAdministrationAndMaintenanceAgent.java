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
package net.fhirfactory.pegacorn.services.oam.monitoring;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.services.oam.monitoring.metrics.management.ITOpsMetricsReportForwarder;
import net.fhirfactory.pegacorn.services.oam.monitoring.metrics.management.ITOpsPubSubReportForwarder;
import net.fhirfactory.pegacorn.services.oam.monitoring.metrics.management.ITOpsTopologyReportForwarder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OperationsAdministrationAndMaintenanceAgent {
    private static final Logger LOG = LoggerFactory.getLogger(OperationsAdministrationAndMaintenanceAgent.class);
    private boolean initialised;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private ITOpsMetricsReportForwarder metricsReportForwarder;

    @Inject
    private ITOpsPubSubReportForwarder pubsubReportForwarder;

    @Inject
    private ITOpsTopologyReportForwarder topologyReportForwarder;

    public OperationsAdministrationAndMaintenanceAgent(){
        this.initialised = false;
    }

    @PostConstruct
    public void initialise(){
        LOG.debug(".initialise(): Entry");
        if(!this.initialised){
            LOG.info(".initialise(): Initalising.....");
            if(processingPlant.isITOpsNode()){
                // Do nothing!
            } else {
                LOG.info(".initialise(): [ITOpsTopologyReportForwarder Initialisation] Start");
                topologyReportForwarder.initialise();
                LOG.info(".initialise(): [ITOpsTopologyReportForwarder Initialisation] Finish");
                LOG.info(".initialise(): [ITOpsPubSubReportForwarder Initialisation] Start");
                pubsubReportForwarder.initialise();
                LOG.info(".initialise(): [ITOpsPubSubReportForwarder Initialisation] Finish");
                LOG.info(".initialise(): [ITOpsMetricsReportForwarder Initialisation] Start");
                metricsReportForwarder.initialise();
                LOG.info(".initialise(): [ITOpsMetricsReportForwarder Initialisation] Finish");
            }
            this.initialised = true;
            LOG.info(".initialise(): Done.");
        } else {
            LOG.debug(".initialise(): Already initialised, nothing to do!");
        }
        LOG.debug(".initialise(): Exit");
    }
}
