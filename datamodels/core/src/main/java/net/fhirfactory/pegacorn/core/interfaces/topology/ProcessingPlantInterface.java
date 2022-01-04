package net.fhirfactory.pegacorn.core.interfaces.topology;

import net.fhirfactory.pegacorn.core.model.capabilities.CapabilityFulfillmentInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.CapabilityFulfillmentManagementInterface;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.ProcessingPlantSoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.SolutionTopologyNode;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkshopSoftwareComponent;

public interface ProcessingPlantInterface extends CapabilityFulfillmentManagementInterface, CapabilityFulfillmentInterface {
    public void initialisePlant();
    public SolutionTopologyNode getSolutionNode();
    public String getSimpleFunctionName();
    public String getSimpleInstanceName();
    public NetworkSecurityZoneEnum getNetworkZone();
    public String getHostName();
    public String getDeploymentSite();
    public PegacornTopologyFactoryInterface getTopologyFactory();
    public ProcessingPlantSoftwareComponent getMeAsASoftwareComponent();
    public String getSubsystemParticipantName();
    public String getSubsystemName();
    public WorkshopSoftwareComponent getWorkshop(String workshopName, String version);
    public WorkshopSoftwareComponent getWorkshop(String workshopName);
    public boolean isITOpsNode();
}
