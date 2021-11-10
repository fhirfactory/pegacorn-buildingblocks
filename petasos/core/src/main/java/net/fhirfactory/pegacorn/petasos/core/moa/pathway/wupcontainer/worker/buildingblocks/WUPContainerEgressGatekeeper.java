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

package net.fhirfactory.pegacorn.petasos.core.moa.pathway.wupcontainer.worker.buildingblocks;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.generalid.FDN;
import net.fhirfactory.pegacorn.core.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import org.apache.camel.Exchange;
import org.apache.camel.RecipientList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mark A. Hunter
 * @since 2020-07-01
 */
@ApplicationScoped
public class WUPContainerEgressGatekeeper {
    private static final Logger LOG = LoggerFactory.getLogger(WUPContainerEgressGatekeeper.class);
    private static final String EGRESS_GATEKEEPER_PROCESSED_PROPERTY = "EgressGatekeeperSemaphore";
    protected Logger getLogger(){
        return(LOG);
    }

    @Inject
    TopologyIM topologyProxy;

    private String getGatekeeperProperty(FDNToken wupIdentifier) {
        FDN workingFDN = new FDN(wupIdentifier);
        String workingInstanceID = workingFDN.getUnqualifiedRDN().getValue();
        String stringToUse = EGRESS_GATEKEEPER_PROCESSED_PROPERTY + "-" + workingInstanceID;
        return (stringToUse);
    }

    /**
     * This class/method checks the status of the WUPJobCard for the parcel, and ascertains if it is to be
     * discarded (because of some processing error or due to the fact that the processing has occurred already
     * within another WUP). At the moment, it reaches the "discard" decisions purely by checking the
     * WUPJobCard.isToBeDiscarded boolean.
     *
     * @param fulfillmentTask The WorkUnitTransportPacket that is to be forwarded to the Intersection (if all is OK)
     * @param camelExchange   The Apache Camel Exchange object, used to store a Semaphore as we iterate through Dynamic Route options
     * @return Should either return the ingres point into the associated Interchange Payload Transformer or null (if the packet is to be discarded)
     */
    @RecipientList
    public List<String> egressGatekeeper(PetasosFulfillmentTask fulfillmentTask, Exchange camelExchange) {
        getLogger().debug(".egressGatekeeper(): Enter, fulfillmentTask ->{}", fulfillmentTask );
        ArrayList<String> targetList = new ArrayList<String>();
        if (fulfillmentTask.getTaskJobCard().isToBeDiscarded()) {
            getLogger().trace(".egressGatekeeper(): The isToBeDiscarded attribute is true, so we return null (and discard the packet");
            getLogger().debug(".egressGatekeeper(): Returning null, as message is to be discarded (isToBeDiscarded == true)");
            return (targetList);
        } else {
            getLogger().trace(".egressGatekeeper(): the isToBeDiscarded attribute is false, so routing the message to the outcomes collector");
            String targetEndpoint = PetasosPropertyConstants.TASK_OUTCOME_COLLECTION_QUEUE;
            targetList.add(targetEndpoint);
            getLogger().debug(".egressGatekeeper(): Returning route to the Interchange Payload Transformer instance --> {}", targetEndpoint);
            return (targetList);
        }
    }
}
