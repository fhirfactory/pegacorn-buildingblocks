/*
 * Copyright (c) 2020 MAHun
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

package net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.interfaces.oam.notifications.PetasosITOpsNotificationAgentInterface;
import net.fhirfactory.pegacorn.core.model.petasos.oam.notifications.PetasosComponentITOpsNotification;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosFulfilmentTaskActivityController;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.notifications.PetasosITOpsNotificationContentFactory;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class WUPContainerEgressProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(WUPContainerEgressProcessor.class);
    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    LocalPetasosFulfilmentTaskActivityController fulfilmentTaskActivityController;

    @Inject
    private PetasosITOpsNotificationAgentInterface notificationAgent;

    @Inject
    private PetasosITOpsNotificationContentFactory notificationContentFactory;


    public PetasosFulfillmentTask egressContentProcessor(PetasosFulfillmentTask fulfillmentTask, Exchange camelExchange) {
      	getLogger().debug(".egressContentProcessor(): Entry, fulfillmentTask->{}", fulfillmentTask);

        //
        // Get out metricsAgent & do add some metrics
        WorkUnitProcessorMetricsAgent metricsAgent = camelExchange.getProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorMetricsAgent.class);

        switch (fulfillmentTask.getTaskFulfillment().getStatus()) {
            case FULFILLMENT_EXECUTION_STATUS_FINISHED:
                fulfilmentTaskActivityController.notifyFulfillmentTaskExecutionFinish(fulfillmentTask.getTaskJobCard());
                metricsAgent.touchLastActivityFinishInstant();
                metricsAgent.incrementFinishedTasks();
                break;
            case FULFILLMENT_EXECUTION_STATUS_CANCELLED:
                fulfilmentTaskActivityController.notifyFulfillmentTaskExecutionCancellation(fulfillmentTask.getTaskJobCard());
                metricsAgent.incrementCancelledTasks();
                metricsAgent.touchLastActivityFinishInstant();
                break;
            case FULFILLMENT_EXECUTION_STATUS_NO_ACTION_REQUIRED:
                fulfilmentTaskActivityController.notifyFulfillmentTaskExecutionNoActionRequired(fulfillmentTask.getTaskJobCard());
                metricsAgent.incrementFinishedTasks();
                metricsAgent.touchLastActivityFinishInstant();
                break;
            default:
                fulfilmentTaskActivityController.notifyFulfillmentTaskExecutionFailure(fulfillmentTask.getTaskJobCard());
                metricsAgent.incrementFailedTasks();
                metricsAgent.touchLastActivityFinishInstant();
        }
        metricsAgent.touchLastActivityInstant();
        //
        // Add some notifications
        String notificationContent = null;
        if(fulfillmentTask.hasTaskWorkItem()) {
            if(fulfillmentTask.getTaskWorkItem().hasEgressContent()) {
                for(UoWPayload payload: fulfillmentTask.getTaskWorkItem().getEgressContent().getPayloadElements()){
                    String notification = notificationContentFactory.newNotificationContentFromUoWPayload(fulfillmentTask.getTaskFulfillment().getStatus(), payload);
                    metricsAgent.sendITOpsNotification(notification);
                }
            }
        }
        //
        // And we're done!
        return (fulfillmentTask);
    }
}
