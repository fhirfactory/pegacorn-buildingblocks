package net.fhirfactory.pegacorn.components.interfaces.topology;

import net.fhirfactory.pegacorn.common.model.componentid.PetasosNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.*;

public interface PegacornTopologyFactoryInterface {
    public PetasosNodeRDN createNodeRDN(String nodeName, String nodeVersion, TopologyNodeTypeEnum nodeType);

    public WorkshopTopologyNode createWorkshop(String name, String version, ProcessingPlantTopologyNode processingPlant, TopologyNodeTypeEnum nodeType);

    public WorkUnitProcessorTopologyNode createWorkUnitProcessor(String name, String version, WorkshopTopologyNode workshop, TopologyNodeTypeEnum nodeType);

    public WorkUnitProcessorComponentTopologyNode createWorkUnitProcessorComponent(String name, TopologyNodeTypeEnum topologyType, WorkUnitProcessorTopologyNode wup);

    public WorkUnitProcessorInterchangeComponentTopologyNode createWorkUnitProcessingInterchangeComponent(String name, TopologyNodeTypeEnum topologyNodeType, WorkUnitProcessorTopologyNode wup);

    public Boolean getSubsystemInternalTrafficEncrypt();

    public ResilienceModeEnum getResilienceMode();
    public ConcurrencyModeEnum getConcurrenceMode();

    public void initialise();
}
