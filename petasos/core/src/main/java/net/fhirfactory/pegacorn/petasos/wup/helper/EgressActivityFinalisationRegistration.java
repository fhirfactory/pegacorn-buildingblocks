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
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoW;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosFulfilmentTaskActivityController;
import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * This class (bean) is to be injected into the flow of an Egress Only WUP Implementation
 * (i.e. Egress Messaging, RESTful.POST, RESTful.PUT, RESTful.DELETE client). It provides the
 * Petasos pseudo finalisation Sequence of the Transaction/Messaging flow - including logging
 * the initial Audit-Trail entry.
 *
 * The method registerActivityEnd is invoked AFTER the call to the forwarding framework
 * system with a +ve/-ve response.
 *
 */

@ApplicationScoped
public class EgressActivityFinalisationRegistration {
    private static final Logger LOG = LoggerFactory.getLogger(EgressActivityFinalisationRegistration.class);

    @Inject
    TopologyIM topologyProxy;

    @Inject
    private LocalPetasosFulfilmentTaskActivityController fulfillmentTaskActivityController;

    public UoW registerActivityFinishAndFinalisation(UoW theUoW, Exchange camelExchange, String wupInstanceKey){
        LOG.debug(".registerActivityFinishAndFinalisation(): Entry, payload --> {}, wupInstanceKey --> {}", theUoW, wupInstanceKey);
        LOG.trace(".registerActivityFinishAndFinalisation(): Retrieve the Fulfillment Task from the Camel Exchange object");
        PetasosFulfillmentTask fulfillmentTask = camelExchange.getProperty(PetasosPropertyConstants.WUP_PETASOS_FULFILLMENT_TASK_EXCHANGE_PROPERTY, PetasosFulfillmentTask.class);
        LOG.trace(".registerActivityFinishAndFinalisation(): Merge the UoW Egress Content into the PetasosFulfillmentTask object & process");
        if(theUoW.hasEgressContent()){
            synchronized (fulfillmentTask.getTaskWorkItemLock()) {
                fulfillmentTask.getTaskWorkItem().getEgressContent().getPayloadElements().addAll(theUoW.getEgressContent().getPayloadElements());
            }
        }
        synchronized (fulfillmentTask.getTaskWorkItemLock()){
            fulfillmentTask.getTaskWorkItem().setProcessingOutcome(theUoW.getProcessingOutcome());
            if(StringUtils.isNotEmpty(theUoW.getFailureDescription())){
                fulfillmentTask.getTaskWorkItem().setFailureDescription(theUoW.getFailureDescription());
            }
        }
        LOG.trace(".registerActivityFinishAndFinalisation(): Process the status");
        switch(theUoW.getProcessingOutcome()){
            case UOW_OUTCOME_SUCCESS:{
                synchronized (fulfillmentTask.getTaskJobCardLock()) {
                    fulfillmentTask.getTaskJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FINISHED);
                }
                fulfillmentTaskActivityController.notifyFulfillmentTaskExecutionFinish(fulfillmentTask.getTaskId());
                break;
            }
            case UOW_OUTCOME_INCOMPLETE:
            case UOW_OUTCOME_NOTSTARTED:
            case UOW_OUTCOME_FAILED:{
                synchronized (fulfillmentTask.getTaskJobCardLock()){
                    fulfillmentTask.getTaskJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_FAILED);
                }
                fulfillmentTaskActivityController.notifyFulfillmentTaskExecutionFailure(fulfillmentTask.getTaskId());
            }
        }
        LOG.debug(".registerActivityFinishAndFinalisation(): exit, my work is done!");
        return(theUoW);
    }
}
