package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.oam.discovery.base;

import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PetasosOAMDiscoveryEndpoint extends JGroupsPetasosEndpointBase {

    // ConcurrentHashMap<petasosEndpointName, TimeInFutureToScan>
    private ConcurrentHashMap<String, Instant> endpointCheckSchedule;


    //
    //
    //


}
