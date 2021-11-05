package net.fhirfactory.pegacorn.core.interfaces.topology;

import net.fhirfactory.pegacorn.core.interfaces.pubsub.PetasosPubSubEndpointChangeInterface;

public interface PetasosTopologyDiscoveryService {
    public void registerHealthCheckCallback(PetasosPubSubEndpointChangeInterface newPublisherCallback);
}
