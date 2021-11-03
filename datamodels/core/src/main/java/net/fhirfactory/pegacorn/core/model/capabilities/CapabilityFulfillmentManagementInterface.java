package net.fhirfactory.pegacorn.core.model.capabilities;

public interface CapabilityFulfillmentManagementInterface {
    public void registerCapabilityFulfillmentService(String capabilityName, CapabilityFulfillmentInterface fulfillmentInterface);
}
