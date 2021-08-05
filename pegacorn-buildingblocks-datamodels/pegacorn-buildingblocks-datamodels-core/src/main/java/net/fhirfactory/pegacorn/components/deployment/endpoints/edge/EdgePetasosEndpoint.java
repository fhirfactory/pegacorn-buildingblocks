package net.fhirfactory.pegacorn.components.deployment.endpoints.edge;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointScopeGroup;

public class EdgePetasosEndpoint {
    String petasosEndpointName;
    PetasosEndpointScopeGroup interSiteEndpointGroup;
    PetasosEndpointScopeGroup interZoneEndpointGroup;
    PetasosEndpointScopeGroup intraZoneEndpointGroup;



}
