package net.fhirfactory.pegacorn.core.interfaces.pathway;

import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;

import java.util.Set;

public interface PetasosInterSubsystemSubscriptionInterface {
    public void subscribeToRemoteSubsystems(Set<DataParcelManifest> subscriptionSet);
}
