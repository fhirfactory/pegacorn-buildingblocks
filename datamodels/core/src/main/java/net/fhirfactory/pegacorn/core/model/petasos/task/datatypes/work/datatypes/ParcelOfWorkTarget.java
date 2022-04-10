package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes;

import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantName;

public class ParcelOfWorkTarget {
    private String sourceSystem;
    private String sourceInterface;
    private PetasosParticipantName sourceProcessingPlantParticipantName;
    private PetasosParticipantName sourceWorkUnitProcessorParticipantName;
    private PetasosParticipantName sourceEndpointParticipantName;
    private String sourceEndpointInterfaceName;
    private String sourceType;
}
