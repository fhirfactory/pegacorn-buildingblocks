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
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.audit.brokers.PetasosFulfillmentTaskAuditServicesBroker;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.LocalPetasosFulfilmentTaskActivityController;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class WUPContainerIngresProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(WUPContainerIngresProcessor.class);
    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    private LocalPetasosFulfilmentTaskActivityController fulfillmentActivityController;

    @Inject
    private PetasosFulfillmentTaskAuditServicesBroker auditServicesBroker;
    
    /**
     * This class/method is used as the injection point into the WUP Processing Framework for the specific WUP Type/Instance in question.
     * It registers the following:
     *      - A ResilienceParcel for the UoW (registered with the SystemModule Parcel Cache: via the PetasosServiceBroker)
     *      - A WUPJobCard for the associated Work Unit Activity (registered into the SystemModule Activity Matrix: via the PetasosServiceBroker)
     *      - A ParcelStatusElement for the ResilienceParcel (again, register into the SystemModule Activity Matrix: via the PetasosServiceBroker)
     *
     * The function handles both new UoW or UoW instances that are being re-tried.
     *
     * It performs checks on the Status (WUPJobCard.currentStatus & ParcelStatusElement.hasClusterFocus) to determine if this WUP-Thread should
     * actually perform the Processing of the UoW via the WUP.
     *
     * It also checks on / assigns values to the Status (ParcelStatusElement.parcelStatus) if there are issues with the parcel. If there are, it may also
     * assign a "failed" status to both the WUPJobCard and ParcelStatusElement, and trigger a discard of this Parcel (for a retry) via setting the
     * WUPJobCard.isToBeDiscarded attribute to true.
     *
     * Finally, if all is going OK, but this WUP-Thread does not have the Cluster Focus (or SystemWide Focus), it waits in a sleep/loop until a condition
     * changes.
     *
     * @param fulfillmentTask The WorkUnitTransportPacket that is to be forwarded to the Intersection (if all is OK)
     * @param camelExchange The Apache Camel Exchange object, used to store a Semaphors and Attributes
     * @return Should return a WorkUnitTransportPacket that is forwarding onto the WUP Ingres Gatekeeper.
     */
    public PetasosFulfillmentTask ingresContentProcessor(PetasosFulfillmentTask fulfillmentTask, Exchange camelExchange) {
        getLogger().debug(".ingresContentProcessor(): Enter, fulfillmentTask->{}", fulfillmentTask );
        //
        // Now, set out wait-loop sleep period
        long waitTime = PetasosPropertyConstants.WUP_SLEEP_INTERVAL_MILLISECONDS;
        //
        // Set our wait-loop check state
        boolean waitState = true;
        //
        // Write an AuditEvent
        auditServicesBroker.logActivity(fulfillmentTask);
        //
        // Now check status
        while (waitState) {
            switch (fulfillmentTask.getTaskJobCard().getCurrentStatus()) {
                case WUP_ACTIVITY_STATUS_WAITING:
                    getLogger().info(".ingresContentProcessor(): jobCard.getCurrentStatus --> {}", PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_WAITING );
                    synchronized (fulfillmentTask.getTaskJobCardLock()) {
                        fulfillmentTask.getTaskJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
                    }
                    fulfillmentActivityController.requestFulfillmentTaskExecutionPrivilege(fulfillmentTask.getTaskJobCard());
                    if (fulfillmentTask.getTaskJobCard().getGrantedStatus() == PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING) {
                        synchronized (fulfillmentTask.getTaskJobCardLock()) {
                            fulfillmentTask.getTaskJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING);
                            fulfillmentTask.getTaskJobCard().setLocalFulfillmentStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE);
                            fulfillmentTask.getTaskJobCard().setLocalUpdateInstant(Instant.now());
                        }
                        synchronized (fulfillmentTask.getTaskFulfillmentLock()){
                            fulfillmentTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_ACTIVE);
                            fulfillmentTask.getTaskFulfillment().setStartInstant(Instant.now());
                        }
                        fulfillmentActivityController.notifyFulfillmentTaskExecutionStart(fulfillmentTask.getTaskJobCard());
                        if(getLogger().isInfoEnabled()) {
                            getLogger().info(".ingresContentProcessor(): jobcard->{}", fulfillmentTask.getTaskJobCard());
                        }
                        waitState = false;
                        break;
                    }
                    if(getLogger().isInfoEnabled()) {
                        getLogger().info(".ingresContentProcessor(): jobcard->{}", fulfillmentTask.getTaskJobCard());
                    }
                    break;
                case WUP_ACTIVITY_STATUS_EXECUTING:
                case WUP_ACTIVITY_STATUS_FINISHED:
                case WUP_ACTIVITY_STATUS_FAILED:
                case WUP_ACTIVITY_STATUS_CANCELED:
                default:
                    getLogger().info(".ingresContentProcessor(): jobCard.getCurrentStatus --> Default");
                    synchronized(fulfillmentTask.getTaskJobCardLock()) {
                        fulfillmentTask.getTaskJobCard().setToBeDiscarded(true);
                        fulfillmentTask.getTaskJobCard().setCurrentStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_CANCELED);
                        fulfillmentTask.getTaskJobCard().setRequestedStatus(PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_CANCELED);
                        fulfillmentTask.getTaskJobCard().setLocalFulfillmentStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_CANCELLED);
                    }
                    synchronized (fulfillmentTask.getTaskFulfillmentLock()){
                        fulfillmentTask.getTaskFulfillment().setStatus(FulfillmentExecutionStatusEnum.FULFILLMENT_EXECUTION_STATUS_CANCELLED);
                        fulfillmentTask.getTaskFulfillment().setCancellationDate(Date.from(Instant.now()));
                    }
                    fulfillmentActivityController.notifyFulfillmentTaskExecutionCancellation(fulfillmentTask.getTaskJobCard());
                    if(getLogger().isInfoEnabled()) {
                        getLogger().info(".ingresContentProcessor(): jobcard->{}", fulfillmentTask.getTaskJobCard());
                    }
                    waitState = false;
            }
            if (waitState) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    getLogger().trace(".ingresContentProcessor(): Something interrupted my nap! reason --> {}", e.getMessage());
                }
            }
        }
        getLogger().debug(".ingresContentProcessor(): Exit, newTransportPacket --> {}", fulfillmentTask);
        return (fulfillmentTask);
    }
}
