package net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common;

import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.pubsub.PubSubParticipant;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionStatus;

import java.util.List;

public interface EdgeForwarderService {
    public RemoteSubscriptionStatus subscribeOnBehalfOfRemoteSubscriber(List<DataParcelManifest> contentSubscriptionList, PubSubParticipant subscriber);
    public WorkUnitProcessorTopologyNode getWUPTopologyNode();

    public boolean supportsMultiSiteIPC();
    public boolean supportsMultiZoneIPC();
    public boolean supportsIntraZoneIPC();
}
