package net.fhirfactory.pegacorn.components.model;

import net.fhirfactory.pegacorn.deployment.topology.map.common.archetypes.common.PetasosEnabledSubsystemTopologyFactory;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkshopTopologyNode;

public interface WorkshopInterface {
    public void initialiseWorkshop();
    public WorkshopTopologyNode getWorkshopNode();
    public PetasosEnabledSubsystemTopologyFactory getTopologyFactory();
    public WorkUnitProcessorTopologyNode getWUP(String workshopName, String version);
    public WorkUnitProcessorTopologyNode getWUP(String workshopName);
}
