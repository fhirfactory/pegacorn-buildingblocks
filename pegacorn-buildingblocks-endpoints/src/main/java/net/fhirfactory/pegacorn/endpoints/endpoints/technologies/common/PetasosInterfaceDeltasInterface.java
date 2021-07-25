package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.common;

import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosInterfaceAddress;

public interface PetasosInterfaceDeltasInterface {
    public void interfaceAdded(PetasosInterfaceAddress addedInterface);
    public void interfaceRemoved(PetasosInterfaceAddress removedInterface);
    public void interfaceSuspect(PetasosInterfaceAddress suspectInterface);
}
