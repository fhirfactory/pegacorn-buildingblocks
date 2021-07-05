package net.fhirfactory.pegacorn.platform.edge.model.ipc.interfaces.common;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.petasos.model.pubsub.PubSubSubscriber;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionStatus;

import java.util.List;

public interface EdgeForwarderService {
    public RemoteSubscriptionStatus subscribeToDataParcelSet(List<DataParcelManifest> contentSubscriptionList, PubSubSubscriber subscriber);
    public WorkUnitProcessorTopologyNode getWUPTopologyNode();
}
