package net.fhirfactory.pegacorn.core.interfaces.topology;

import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkshopTopologyNode;

public interface WorkshopInterface {
    public void initialiseWorkshop();
    public WorkshopTopologyNode getWorkshopNode();
//    public PetasosEnabledSubsystemTopologyFactory getTopologyFactory();
    public WorkUnitProcessorTopologyNode getWUP(String workshopName, String version);
    public WorkUnitProcessorTopologyNode getWUP(String workshopName);
}
