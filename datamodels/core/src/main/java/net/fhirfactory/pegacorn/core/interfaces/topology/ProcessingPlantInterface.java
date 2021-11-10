package net.fhirfactory.pegacorn.core.interfaces.topology;

import net.fhirfactory.pegacorn.core.model.capabilities.CapabilityFulfillmentInterface;
import net.fhirfactory.pegacorn.core.model.capabilities.CapabilityFulfillmentManagementInterface;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.ProcessingPlantTopologyNode;
import net.fhirfactory.pegacorn.core.model.topology.nodes.SolutionTopologyNode;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkshopTopologyNode;

public interface ProcessingPlantInterface extends CapabilityFulfillmentManagementInterface, CapabilityFulfillmentInterface {
    public void initialisePlant();
    public SolutionTopologyNode getSolutionNode();
    public String getSimpleFunctionName();
    public String getSimpleInstanceName();
    public NetworkSecurityZoneEnum getNetworkZone();
    public String getHostName();
    public String getDeploymentSite();
    public PegacornTopologyFactoryInterface getTopologyFactory();
    public ProcessingPlantTopologyNode getProcessingPlantNode();
    public WorkshopTopologyNode getWorkshop(String workshopName, String version);
    public WorkshopTopologyNode getWorkshop(String workshopName);
    public String getIPCServiceName();
    public boolean isITOpsNode();
}
