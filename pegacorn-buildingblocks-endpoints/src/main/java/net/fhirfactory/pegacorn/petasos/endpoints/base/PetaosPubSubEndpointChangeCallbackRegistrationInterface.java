package net.fhirfactory.pegacorn.petasos.endpoints.base;

public interface PetaosPubSubEndpointChangeCallbackRegistrationInterface {
    public void registerTopologyCallbackChange(PetasosTopologyEndpointChangeInterface publisherChangeCallback);
}
