package net.fhirfactory.pegacorn.core.interfaces.topology;

import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkshopSoftwareComponent;

public interface WorkshopInterface {
    public void initialiseWorkshop();
    public WorkshopSoftwareComponent getWorkshopNode();
//    public PetasosEnabledSubsystemTopologyFactory getTopologyFactory();
    public WorkUnitProcessorSoftwareComponent getWUP(String workshopName, String version);
    public WorkUnitProcessorSoftwareComponent getWUP(String workshopName);
}
