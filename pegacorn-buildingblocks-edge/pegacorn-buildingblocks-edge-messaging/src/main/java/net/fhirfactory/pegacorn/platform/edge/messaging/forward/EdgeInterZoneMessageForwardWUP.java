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
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelDirectionEnum;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.PolicyEnforcementPointApprovalStatusEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterfaceDefinition;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpoint;
import net.fhirfactory.pegacorn.petasos.model.pubsub.LocalPubSubParticipantIdentifier;
import net.fhirfactory.pegacorn.petasos.model.pubsub.LocalPubSubSubscriber;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubSubscriber;
import net.fhirfactory.pegacorn.platform.edge.endpoints.jgroups.JGroupsInterZoneIPCEndpoint;
import net.fhirfactory.pegacorn.platform.edge.endpoints.jgroups.common.JGroupsIPCEndpoint;
import net.fhirfactory.pegacorn.platform.edge.messaging.forward.common.EdgeMessageForwardWUP;
import net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.InterZoneEdgeForwarderService;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class EdgeInterZoneMessageForwardWUP extends EdgeMessageForwardWUP implements InterZoneEdgeForwarderService {
    private static final Logger LOG = LoggerFactory.getLogger(EdgeInterZoneMessageForwardWUP.class);

    private static String WUP_VERSION = "1.0.0";

    @Inject
    private JGroupsInterZoneIPCEndpoint ipcEndpoint;

    @Override
    protected MessageBasedWUPEndpoint specifyEgressEndpoint() {
        MessageBasedWUPEndpoint egressEndpoint = new MessageBasedWUPEndpoint();
        assignEgressTopologyEndpoint();
        egressEndpoint.setEndpointTopologyNode(getAssociatedEgressTopologyEndpoint());
        egressEndpoint.setEndpointSpecification(getIPCComponentNames().getInterZoneIPCForwarderRouteEndpointName());
        egressEndpoint.setFrameworkEnabled(false);
        return(egressEndpoint);
    }

    @Override
    protected String specifyIPCZoneType() {
        return (getIPCComponentNames().getInterZoneIPCGroupName());
    }

    @Override
    protected JGroupsIPCEndpoint specifyIPCEndpoint() {
        return (ipcEndpoint);
    }

    @Override
    public RemoteSubscriptionStatus subscribeToDataParcelSet(List<DataParcelManifest> contentSubscriptionList, PubSubSubscriber subscriber) {
        LOG.debug(".subscribeToDataParcelSet(): Entry, contentSubscriptionList->{}, subscriber->{}", contentSubscriptionList, subscriber);
        if(contentSubscriptionList == null || subscriber == null){
            LOG.debug(".contentSubscriptionList(): Exit, either contentSubscriptionList or subscriber is null");
            RemoteSubscriptionStatus badStatus = new RemoteSubscriptionStatus();
            badStatus.setSubscriptionSuccessful(false);
            badStatus.setSubscriptionCommentary("Either contentSubscriptionList or subscriber is null!");
            return(badStatus);
        }
        if(!subscriber.hasLocalSubscriber()){
            LocalPubSubSubscriber localSubscriber = new LocalPubSubSubscriber();
            LocalPubSubParticipantIdentifier identifier = new LocalPubSubParticipantIdentifier(getWUPTopologyNode().getNodeFDN().getToken());
            localSubscriber.setIdentifier(identifier);
            subscriber.setLocalSubscriber(localSubscriber);
        }
        for(DataParcelManifest currentDataParcel: contentSubscriptionList) {
            currentDataParcel.setDataParcelFlowDirection(DataParcelDirectionEnum.INBOUND_DATA_PARCEL);
            currentDataParcel.setEnforcementPointApprovalStatus(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_POSITIVE);
            currentDataParcel.setInterSubsystemDistributable(true);
            getTopicServer().addTopicSubscriber(currentDataParcel, subscriber);
        }
        RemoteSubscriptionStatus okStatus = new RemoteSubscriptionStatus();
        okStatus.setSubscriptionSuccessful(true);
        LOG.debug(".contentSubscriptionList(): Exit, okStatus->{}", okStatus);
        return (okStatus);
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
    protected String specifyWUPInstanceName() {
        return (this.getClass().getSimpleName());
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return (WUP_VERSION);
    }

    @Override
    protected String specifyEgressInterfaceName() {
        return (getInterfaceNames().getFunctionNameInterZoneJGroupsIPC());
    }

    @Override
    protected IPCInterfaceDefinition specifyEgressInterfaceDefinition() {
        IPCInterfaceDefinition interfaceDefinition = new IPCInterfaceDefinition();
        interfaceDefinition.setInterfaceFormalName("JGroups-Gossip");
        interfaceDefinition.setInterfaceFormalVersion("1.0.0");
        return (interfaceDefinition);
    }

    @Override
    public WorkUnitProcessorTopologyNode getWUPTopologyNode() {
        return (getAssociatedTopologyNode());
    }
}
