package net.fhirfactory.pegacorn.endpoints.endpoints.base;

public interface PetasosDiscoveryService {
    public void registerHealthCheckCallback(PetasosPubSubEndpointChangeInterface newPublisherCallback);
}
