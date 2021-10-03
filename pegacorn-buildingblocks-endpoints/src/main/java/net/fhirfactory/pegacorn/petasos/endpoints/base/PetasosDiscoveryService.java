package net.fhirfactory.pegacorn.petasos.endpoints.base;

public interface PetasosDiscoveryService {
    public void registerHealthCheckCallback(PetasosPubSubEndpointChangeInterface newPublisherCallback);
}
