package net.fhirfactory.pegacorn.core.model.topology.nodes.common;

import net.fhirfactory.pegacorn.core.model.capabilities.definition.Capability;
import net.fhirfactory.pegacorn.core.model.componentid.*;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantId;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;

public interface EndpointProviderInterface {
    public void addEndpoint(ComponentIdType componentId);
    public ComponentIdType getComponentID();
    public SoftwareComponentTypeEnum getComponentType();
    public ResilienceModeEnum getResilienceMode();
    public ConcurrencyModeEnum getConcurrencyMode();
    public PetasosParticipantId getParticipantId();
    public String getVersion();
}
