package net.fhirfactory.pegacorn.petasos.endpoints.oam;

import net.fhirfactory.pegacorn.petasos.endpoints.base.PetasosEndpointInterface;
import org.hl7.fhir.r4.model.Endpoint;

import java.util.List;

public interface PetasosOAMEndpointInterface extends PetasosEndpointInterface {
    public List<Endpoint> probeNetwork();

}
