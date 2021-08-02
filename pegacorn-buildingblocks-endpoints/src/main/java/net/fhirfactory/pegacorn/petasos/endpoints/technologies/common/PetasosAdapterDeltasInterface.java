package net.fhirfactory.pegacorn.petasos.endpoints.technologies.common;

import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;

public interface PetasosAdapterDeltasInterface {
    public void interfaceAdded(PetasosAdapterAddress addedInterface);
    public void interfaceRemoved(PetasosAdapterAddress removedInterface);
    public void interfaceSuspect(PetasosAdapterAddress suspectInterface);
}
