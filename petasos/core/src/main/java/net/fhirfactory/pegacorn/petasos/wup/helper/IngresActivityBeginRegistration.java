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

package net.fhirfactory.pegacorn.petasos.wup.helper;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.context.TaskContextType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.context.TaskTriggerSummaryType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.FulfillmentTrackingIdType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.datatypes.TaskFulfillmentType;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemType;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoW;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosActionableTaskFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.factories.PetasosFulfillmentTaskFactory;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosActionableTaskActivityController;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosFulfilmentTaskActivityController;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;

/**
 * This class (bean) is to be injected into the flow of an Ingres Only WUP Implementation
 * (i.e. Ingres Messaging, RESTful.POST, RESTful.PUT, RESTful.DELETE). It provides the
 * Petasos Initialisation Sequence of the Transaction/Messaging flow - including logging
 * the initial Audit-Trail entry.
 *
 * The method registerActivityStart must be invoked PRIOR to responding to the source (external)
 * system with a +ve/-ve response.
 *
 */

@ApplicationScoped
public class IngresActivityBeginRegistration {
    private static final Logger LOG = LoggerFactory.getLogger(IngresActivityBeginRegistration.class);

    @Inject
    LocalPetasosActionableTaskActivityController actionableTaskActivityController;

    @Inject
    LocalPetasosFulfilmentTaskActivityController fulfilmentTaskActivityController;

    @Inject
    private PetasosFulfillmentTaskFactory fulfillmentTaskFactory;

    @Inject
    private PetasosActionableTaskFactory actionableTaskFactory;

    //
    // Business Methods
    //

    public UoW registerActivityStart(UoW theUoW, Exchange camelExchange){
        getLogger().debug(".registerActivityStart(): Entry, payload --> {}", theUoW);

        getLogger().trace(".registerActivityStart(): Retrieve NodeElement");
        WorkUnitProcessorSoftwareComponent wup = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorSoftwareComponent.class);

        getLogger().trace(".registerActivityStart(): Node Element retrieved --> {}", wup);
        TopologyNodeFunctionFDNToken wupFunctionToken = wup.getNodeFunctionFDN().getFunctionToken();
        getLogger().trace(".registerActivityStart(): wupFunctionToken (NodeElementFunctionToken) for this activity --> {}", wupFunctionToken);

        //
        // add to WUP Metrics
        WorkUnitProcessorMetricsAgent metricsAgent = camelExchange.getProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorMetricsAgent.class);
        if(metricsAgent == null){
            getLogger().warn(".registerActivityStart(): Could not get metricsAgent");
        }
        metricsAgent.incrementIngresMessageCount();
        metricsAgent.touchLastActivityInstant();

        getLogger().trace(".registerActivityStart(): Create PetasosActionableTask for the incoming message (processing activity): Start");
        TaskWorkItemType workItem = new TaskWorkItemType(theUoW.getIngresContent());
        PetasosActionableTask petasosActionableTask = getActionableTaskFactory().newMessageBasedActionableTask(workItem);
        TaskContextType taskContext = new TaskContextType();
        TaskTriggerSummaryType taskTriggerSummary = new TaskTriggerSummaryType();
        taskTriggerSummary.setTriggerTaskId(petasosActionableTask.getTaskId());
        taskTriggerSummary.setTriggerLocation(wup.getComponentFDN().getToken().getTokenValue());
        taskContext.setTaskTriggerSummary(taskTriggerSummary);
        petasosActionableTask.setTaskContext(taskContext);
        // set task fulfillment status details
        petasosActionableTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE);
        petasosActionableTask.getTaskFulfillment().setStartInstant(Instant.now());
        petasosActionableTask.getTaskFulfillment().setFulfillerComponent(wup);
        petasosActionableTask.getTaskFulfillment().setReadyInstant(Instant.now());
        getLogger().trace(".registerActivityStart(): Create PetasosActionableTask for the incoming message (processing activity): Finish");

        getLogger().trace(".registerActivityStart(): Create a PetasosFulfillmentTask for the (local) processing implementation activity: Start");
        PetasosFulfillmentTask fulfillmentTask = getFulfillmentTaskFactory().newFulfillmentTask(petasosActionableTask, wup);
        getLogger().trace(".registerActivityStart(): Create a PetasosFulfillmentTask for the (local) processing implementation activity: Finish");

        getLogger().trace(".registerActivityStart(): Register PetasosActionableTask for the incoming message (processing activity): Start");
        petasosActionableTask.getTaskFulfillment().setTrackingID(new FulfillmentTrackingIdType(fulfillmentTask.getTaskId()));
        getActionableTaskActivityController().registerActionableTask(petasosActionableTask);
        getLogger().trace(".registerActivityStart(): Register PetasosActionableTask for the incoming message (processing activity): Finish");

        getLogger().trace(".registerActivityStart(): Register PetasosFulfillmentTask for the (local) processing implementation activity: Start");
        getFulfilmentTaskActivityController().registerFulfillmentTask(fulfillmentTask, true); // by default, use synchronous audit writing
        getLogger().trace(".registerActivityStart(): Register PetasosFulfillmentTask for the (local) processing implementation activity: Finish");

        //
        // Now, because this registration activity was the actual source of both the Actionable Task and the Fullfilment Task,
        // it is safe to say that it should continue the fulfillment process unhindered...
        getLogger().trace(".registerActivityStart(): Update status to reflect local processing is proceeding: Start");
        synchronized(fulfillmentTask.getTaskJobCardLock()){
            fulfillmentTask.getTaskJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
            fulfillmentTask.getTaskJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
            fulfillmentTask.getTaskJobCard().setGrantedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
        }
        getFulfilmentTaskActivityController().notifyFulfillmentTaskExecutionStart(fulfillmentTask.getTaskJobCard());
        getLogger().trace(".registerActivityStart(): Update status to reflect local processing is proceeding: Finish");
        //
        // Now we have to Inject some details into the Exchange so that the WUPEgressConduit can extract them as per standard practice
        getLogger().trace(".registerActivityStart(): Injecting Job Card and Status Element into Exchange for extraction by the WUP Egress Conduit");
        camelExchange.setProperty(PetasosPropertyConstants.WUP_PETASOS_FULFILLMENT_TASK_EXCHANGE_PROPERTY, fulfillmentTask);
        //
        // And now we are done!
        getLogger().debug(".registerActivityStart(): exit, my work is done!");
        return(theUoW);
    }

    //
    // Getters (and Setters)
    //

    protected LocalPetasosActionableTaskActivityController getActionableTaskActivityController() {
        return actionableTaskActivityController;
    }

    protected LocalPetasosFulfilmentTaskActivityController getFulfilmentTaskActivityController() {
        return fulfilmentTaskActivityController;
    }

    protected PetasosFulfillmentTaskFactory getFulfillmentTaskFactory() {
        return fulfillmentTaskFactory;
    }

    protected PetasosActionableTaskFactory getActionableTaskFactory() {
        return actionableTaskFactory;
    }

    protected Logger getLogger(){
        return(LOG);
    }
}
