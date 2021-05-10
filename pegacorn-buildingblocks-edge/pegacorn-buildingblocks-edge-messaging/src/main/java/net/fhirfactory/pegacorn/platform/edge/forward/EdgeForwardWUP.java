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
package net.fhirfactory.pegacorn.platform.edge.forward;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.topicid.TopicToken;
import net.fhirfactory.pegacorn.components.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.TopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.GenericMessageBasedWUPEndpoint;
import net.fhirfactory.pegacorn.workshops.EdgeWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.EdgeEgressMessagingGatewayWUP;
import org.apache.camel.LoggingLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EdgeForwardWUP extends EdgeEgressMessagingGatewayWUP {
    private static final Logger LOG = LoggerFactory.getLogger(EdgeForwardWUP.class);

    private boolean initialised;
    private IPCTopologyEndpoint designatedEndpoint;

    @Inject
    private EdgeWorkshop edgeWorkshop;

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected Set<TopicToken> specifySubscriptionTopics() {
        getLogger().debug(".specifySubscriptionTopics(): Entry");
        HashSet<TopicToken> myTopicSet = new HashSet<TopicToken>();
        TopicToken topicId = getFHIRTopicIDBuilder().createTopicToken("Bundle", "4.0.1");
        topicId.addDescriminator("Destination", "*");
        myTopicSet.add(topicId);
        getLogger().debug(".specifySubscriptionTopics(): Exit, added TopicToken --> {}", topicId);
        return (myTopicSet);
    }

    @Override
    protected void executePostInitialisationActivities(){
        deriveTopologyEndpoint();
    }

    @Override
    protected String specifyWUPInstanceName() {
        return ("EdgeForwardWUP");
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return ("1.0.0");
    }

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (edgeWorkshop);
    }

    //
    // Application Logic (Route Definition)
    //

    @Override
    public void configure() throws Exception {
        fromWithStandardExceptionHandling(this.getIngresTopologyEndpoint().getEndpointSpecification())
                .routeId(getNameSet().getRouteCoreWUP())
                .log(LoggingLevel.DEBUG, "Raw Content to be Forwarded --> ${body}");
    }

    //
    // Getters and Setters
    //

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    public IPCTopologyEndpoint getDesignatedEndpoint() {
        return designatedEndpoint;
    }

    public void setDesignatedEndpoint(IPCTopologyEndpoint designatedEndpoint) {
        this.designatedEndpoint = designatedEndpoint;
    }

    protected TopologyEndpointTypeEnum specifyIPCType() {
        return (TopologyEndpointTypeEnum.PEGACORN_IPC_MESSAGING_SERVICE);
    }

    //
    // Application Logic (Establishing WUP)
    //

    @Override
    protected GenericMessageBasedWUPEndpoint specifyEgressTopologyEndpoint() {
        GenericMessageBasedWUPEndpoint egressEndoint = new GenericMessageBasedWUPEndpoint();
        egressEndoint.setEndpointTopologyNode(getDesignatedEndpoint());
        egressEndoint.setEndpointSpecification("direct:EdgeReceiveCommon");
        egressEndoint.setFrameworkEnabled(false);
        return(egressEndoint);
    }

    protected void deriveTopologyEndpoint(){
        getLogger().debug(".deriveIPCTopologyEndpoint(): Entry");
        IPCTopologyEndpoint ipcEndpoint = null;
        for(TopologyNodeFDN currentEndpointFDN: getProcessingPlant().getProcessingPlantNode().getEndpoints()){
            IPCTopologyEndpoint currentEndpoint = (IPCTopologyEndpoint)getTopologyIM().getNode(currentEndpointFDN);
            TopologyEndpointTypeEnum endpointType = currentEndpoint.getEndpointType();
            boolean endpointTypeMatches = endpointType.equals(specifyIPCType());
            if(endpointTypeMatches){
                ipcEndpoint = currentEndpoint;
                break;
            }
        }
        if(ipcEndpoint == null){
            getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, no Endpoint found");
            return;
        }
        if(ipcEndpoint.getSupportedInterfaceSet().size() <= 0){
            getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, no Interfaces defined in Endpoint");
            return;
        }
        getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, found IPCTopologyEndpoint, returning it");
        setDesignatedEndpoint(ipcEndpoint);
    }
}
