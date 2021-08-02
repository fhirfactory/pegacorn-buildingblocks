package net.fhirfactory.pegacorn.petasos.endpoints.roles.base;

import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;

import java.util.List;

public interface EndpointChangeNotificationActionInterface {

    public void notifyMembershipChange(List<PetasosAdapterAddress> endpointsAdded, List<PetasosAdapterAddress> endpointsRemoved);
    public void notifyMembershipChange(PetasosAdapterAddress changedEndpoint);
}
