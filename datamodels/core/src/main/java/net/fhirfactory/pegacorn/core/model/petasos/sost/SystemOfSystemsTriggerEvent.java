package net.fhirfactory.pegacorn.core.model.petasos.sost;

import net.fhirfactory.pegacorn.core.model.generalid.FDNToken;

public class SystemOfSystemsTriggerEvent extends SystemOfSystemsEvent{


    public SystemOfSystemsTriggerEvent(SystemOfSystemsEventTypeEnum newEventType, FDNToken endpointId) {
        super(newEventType, endpointId);
    }
}
