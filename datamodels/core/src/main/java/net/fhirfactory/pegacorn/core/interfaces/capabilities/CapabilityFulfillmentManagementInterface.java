package net.fhirfactory.pegacorn.core.interfaces.capabilities;

public interface CapabilityFulfillmentManagementInterface {
    public void registerCapabilityFulfillmentService(String capabilityName, CapabilityFulfillmentInterface fulfillmentInterface);
}
