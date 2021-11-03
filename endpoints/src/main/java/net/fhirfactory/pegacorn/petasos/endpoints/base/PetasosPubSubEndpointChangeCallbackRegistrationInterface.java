package net.fhirfactory.pegacorn.petasos.endpoints.base;

public interface PetasosPubSubEndpointChangeCallbackRegistrationInterface {
    public void registerPubSubCallbackChange(PetasosPubSubEndpointChangeInterface publisherChangeCallback);
}
