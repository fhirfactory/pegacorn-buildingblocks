package net.fhirfactory.pegacorn.components.capabilities;

import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationResponse;

public interface CapabilityFulfillmentInterface {
    public CapabilityUtilisationResponse executeTask(CapabilityUtilisationRequest request);
}
