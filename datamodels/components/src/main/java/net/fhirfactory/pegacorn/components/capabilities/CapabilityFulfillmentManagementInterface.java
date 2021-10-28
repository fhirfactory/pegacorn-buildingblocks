package net.fhirfactory.pegacorn.components.capabilities;

public interface CapabilityFulfillmentManagementInterface {
    public void registerCapabilityFulfillmentService(String capabilityName, CapabilityFulfillmentInterface fulfillmentInterface);
}
