package net.fhirfactory.pegacorn.endpoints.endpoints.roles.base;

import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddress;

import java.util.List;

public interface EndpointChangeNotificationActionInterface {

    public void notifyMembershipChange(List<PetasosAdapterAddress> endpointsAdded, List<PetasosAdapterAddress> endpointsRemoved);
    public void notifyMembershipChange(PetasosAdapterAddress changedEndpoint);
}
