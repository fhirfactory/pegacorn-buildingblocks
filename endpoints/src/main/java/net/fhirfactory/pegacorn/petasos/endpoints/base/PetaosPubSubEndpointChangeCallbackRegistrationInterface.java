package net.fhirfactory.pegacorn.petasos.endpoints.base;

public interface PetaosPubSubEndpointChangeCallbackRegistrationInterface {
    public void registerPubSubCallbackChange(PetasosPubSubEndpointChangeInterface publisherChangeCallback);
}
