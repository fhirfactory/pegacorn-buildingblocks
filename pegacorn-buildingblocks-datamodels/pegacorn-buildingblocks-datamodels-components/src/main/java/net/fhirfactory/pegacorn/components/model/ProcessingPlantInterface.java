package net.fhirfactory.pegacorn.components.model;

import net.fhirfactory.pegacorn.deployment.topology.model.nodes.ProcessingPlantTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkshopTopologyNode;

public interface ProcessingPlantInterface {
    public void initialisePlant();
//    public PetasosEnabledSubsystemTopologyFactory getTopologyFactory();
    public ProcessingPlantTopologyNode getProcessingPlantNode();
    public WorkshopTopologyNode getWorkshop(String workshopName, String version);
    public WorkshopTopologyNode getWorkshop(String workshopName);
}
