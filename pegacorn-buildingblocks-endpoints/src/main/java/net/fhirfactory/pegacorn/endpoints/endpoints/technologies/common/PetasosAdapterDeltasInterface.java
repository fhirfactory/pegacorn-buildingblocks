package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.common;

import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddress;

public interface PetasosAdapterDeltasInterface {
    public void interfaceAdded(String addedInterface);
    public void interfaceRemoved(String removedInterface);
    public void interfaceSuspect(String suspectInterface);
}
