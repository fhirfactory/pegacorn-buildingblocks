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
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming.RouteElementNames;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.PetasosFulfillmentTaskSharedInstance;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import org.apache.camel.Exchange;
import org.apache.camel.RecipientList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;


@ApplicationScoped
public class WUPContainerInterceptionRedirectPoint {
    private static final Logger LOG = LoggerFactory.getLogger(WUPContainerInterceptionRedirectPoint.class);

    protected Logger getLogger(){
        return(LOG);
    }


    /**
     * This class/method checks the status of the WUPJobCard for the parcel, and ascertains if it is to be
     * discarded (because of some processing error or due to the fact that the processing has occurred already
     * within another WUP). At the moment, it reaches the "discard" decisions purely by checking the
     * WUPJobCard.isToBeDiscarded boolean.
     *
     * @param fulfillmentTask     The WorkUnitTransportPacket that is to be forwarded to the Intersection (if all is OK)
     * @param camelExchange    The Apache Camel Exchange object, used to store a Semaphore as we iterate through Dynamic Route options
     * @return Should either return the ingres point into the associated WUP Ingres Conduit or null (if the packet is to be discarded)
     */
    public List<String> ingresGatekeeper(PetasosFulfillmentTaskSharedInstance fulfillmentTask, Exchange camelExchange) {
        if(getLogger().isInfoEnabled()){
            getLogger().info(".ingresGatekeeper(): Entry, fulfillmentTaskId/ActionableTaskId->{}/{}", fulfillmentTask.getTaskId(), fulfillmentTask.getActionableTaskId());
        }
        getLogger().debug(".ingresGatekeeper(): Enter, fulfillmentTask->{}", fulfillmentTask);
        WorkUnitProcessorSoftwareComponent wupSoftwareComponent = camelExchange.getProperty(PetasosPropertyConstants.WUP_TOPOLOGY_NODE_EXCHANGE_PROPERTY_NAME, WorkUnitProcessorSoftwareComponent.class);


        //
        // Get out metricsAgent & do add some metrics
        WorkUnitProcessorMetricsAgent metricsAgent = camelExchange.getProperty(PetasosPropertyConstants.WUP_METRICS_AGENT_EXCHANGE_PROPERTY, WorkUnitProcessorMetricsAgent.class);
        // Now, continue with business logic
        // Get Route Names
        TopologyNodeFunctionFDNToken wupToken = fulfillmentTask.getTaskFulfillment().getFulfillerWorkUnitProcessor().getNodeFunctionFDN().getFunctionToken();
        getLogger().trace(".ingresGatekeeper(): wupFunctionToken (NodeElementFunctionToken) for this activity --> {}", wupToken);
        RouteElementNames nameSet = new RouteElementNames(wupToken);
        ArrayList<String> targetList = new ArrayList<String>();
        getLogger().trace(".ingresGatekeeper(): So, we will now determine if the Packet should be forwarded or discarded");
        if (fulfillmentTask.getTaskFulfillment().isToBeDiscarded()) {
            getLogger().debug(".ingresGatekeeper(): Returning null, as message is to be discarded (isToBeDiscarded == true)");
            metricsAgent.touchLastActivityFinishInstant();
            targetList.add(PetasosPropertyConstants.TASK_OUTCOME_COLLECTION_QUEUE);
            return (targetList);
        } else {
            getLogger().trace(".ingresGatekeeper(): We return the channel name of the associated WUP Ingres Conduit");
            String targetEndpoint = nameSet.getEndPointWUPIngresConduitIngres();
            targetList.add(targetEndpoint);
            getLogger().debug(".ingresGatekeeper(): Returning route to the WUP Ingres Conduit instance --> {}", targetEndpoint);
            return (targetList);
        }
    }
}
