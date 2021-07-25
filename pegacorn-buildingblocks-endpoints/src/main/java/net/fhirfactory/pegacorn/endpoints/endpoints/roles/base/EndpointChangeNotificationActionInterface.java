package net.fhirfactory.pegacorn.endpoints.endpoints.roles.base;

import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosInterfaceAddress;

import java.util.List;

public interface EndpointChangeNotificationActionInterface {

    public void notifyMembershipChange(List<PetasosInterfaceAddress> endpointsAdded, List<PetasosInterfaceAddress> endpointsRemoved);
    public void notifyMembershipChange(PetasosInterfaceAddress changedEndpoint);
}
