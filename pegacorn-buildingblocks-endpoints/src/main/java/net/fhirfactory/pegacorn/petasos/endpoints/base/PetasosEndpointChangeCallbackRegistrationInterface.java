package net.fhirfactory.pegacorn.petasos.endpoints.base;

public interface PetasosEndpointChangeCallbackRegistrationInterface {
    public void registerTopologyCallbackChange(PetasosEndpointChangeInterface publisherChangeCallback);
}
