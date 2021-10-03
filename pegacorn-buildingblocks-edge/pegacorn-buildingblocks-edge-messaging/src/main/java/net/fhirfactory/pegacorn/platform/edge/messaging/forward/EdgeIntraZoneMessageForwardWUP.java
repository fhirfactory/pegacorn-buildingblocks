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
package net.fhirfactory.pegacorn.platform.edge.messaging.forward;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterfaceDefinition;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.ipc.PetasosIntraZoneIPCEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.ipc.base.PetasosIPCEndpoint;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpoint;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.platform.edge.messaging.forward.common.EdgeMessageForwardWUP;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.IntraZoneEdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class EdgeIntraZoneMessageForwardWUP extends EdgeMessageForwardWUP implements IntraZoneEdgeForwarderService {
    private static final Logger LOG = LoggerFactory.getLogger(EdgeIntraZoneMessageForwardWUP.class);

    private static String WUP_VERSION = "1.0.0";

    @Inject
    private PetasosIntraZoneIPCEndpoint ipcEndpoint;

    @Override
    protected String specifyWUPInstanceName() {
        return (this.getClass().getSimpleName());
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return (WUP_VERSION);
    }

    @Override
    protected String specifyIPCZoneType() {
        return (getIPCComponentNames().getIntraZoneIPCGroupName());
    }

    @Override
    protected PetasosIPCEndpoint specifyIPCEndpoint() {
        return (ipcEndpoint);
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected List<DataParcelManifest> specifySubscriptionTopics() {
        List<DataParcelManifest> subscriptionList = new ArrayList<>();
        return (subscriptionList);
    }

    @Override
    protected MessageBasedWUPEndpoint specifyEgressEndpoint() {
        MessageBasedWUPEndpoint ingresEndpoint = new MessageBasedWUPEndpoint();
        assignEgressTopologyEndpoint();
        ingresEndpoint.setEndpointTopologyNode(getAssociatedEgressTopologyEndpoint());
        ingresEndpoint.setEndpointSpecification(getIPCComponentNames().getIntraZoneIPCForwarderRouteEndpointName());
        ingresEndpoint.setFrameworkEnabled(false);
        return(ingresEndpoint);
    }

    @Override
    protected String specifyEgressInterfaceName() {
        return (getInterfaceNames().getFunctionNameIntraZoneJGroupsIPC());
    }

    @Override
    protected IPCInterfaceDefinition specifyEgressInterfaceDefinition() {
        IPCInterfaceDefinition interfaceDefinition = new IPCInterfaceDefinition();
        interfaceDefinition.setInterfaceFormalName("JGroups-Kube");
        interfaceDefinition.setInterfaceFormalVersion("1.0.0");
        return (interfaceDefinition);
    }

    @Override
    public WorkUnitProcessorTopologyNode getWUPTopologyNode() {
        return (getAssociatedTopologyNode());
    }

    @Override
    public RemoteSubscriptionStatus subscribeOnBehalfOfRemoteSubscriber(List<DataParcelManifest> contentSubscriptionList, PubSubParticipant subscriber) {
        RemoteSubscriptionStatus status = this.subscribeToDataParcelSet(contentSubscriptionList, subscriber);
        return(status);
    }

    @Override
    public boolean supportsMultiSiteIPC() {
        return (false);
    }

    @Override
    public boolean supportsMultiZoneIPC() {
        return (false);
    }

    @Override
    public boolean supportsIntraZoneIPC() {
        return (true);
    }
}
