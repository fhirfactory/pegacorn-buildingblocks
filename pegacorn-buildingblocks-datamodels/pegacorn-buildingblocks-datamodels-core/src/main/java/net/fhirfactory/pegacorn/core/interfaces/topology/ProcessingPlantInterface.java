package net.fhirfactory.pegacorn.core.interfaces.topology;

import net.fhirfactory.pegacorn.deployment.topology.model.common.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.ProcessingPlantTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.SolutionTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkshopTopologyNode;

public interface ProcessingPlantInterface {
    public void initialisePlant();
    public SolutionTopologyNode getSolutionNode();
    public String getSimpleFunctionName();
    public String getSimpleInstanceName();
    public NetworkSecurityZoneEnum getNetworkZone();
    public String getDeploymentSite();
    public PegacornTopologyFactoryInterface getTopologyFactory();
    public ProcessingPlantTopologyNode getProcessingPlantNode();
    public WorkshopTopologyNode getWorkshop(String workshopName, String version);
    public WorkshopTopologyNode getWorkshop(String workshopName);
    public String getIPCServiceName();
}
