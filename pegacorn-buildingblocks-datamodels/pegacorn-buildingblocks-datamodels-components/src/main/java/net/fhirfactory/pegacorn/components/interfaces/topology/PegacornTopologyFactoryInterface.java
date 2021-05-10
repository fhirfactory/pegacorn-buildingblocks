package net.fhirfactory.pegacorn.components.interfaces.topology;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.BaseSubsystemPropertyFile;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.*;

public interface PegacornTopologyFactoryInterface {
    public TopologyNodeRDN createNodeRDN(String nodeName, String nodeVersion, TopologyNodeTypeEnum nodeType);

    public WorkshopTopologyNode addWorkshop(String name, String version, ProcessingPlantTopologyNode processingPlant, TopologyNodeTypeEnum nodeType);

    public WorkUnitProcessorTopologyNode addWorkUnitProcessor(String name, String version, WorkshopTopologyNode workshop, TopologyNodeTypeEnum nodeType);

    public WorkUnitProcessorComponentTopologyNode addWorkUnitProcessorComponent(String name, TopologyNodeTypeEnum topologyType, WorkUnitProcessorTopologyNode wup);

    public WorkUnitProcessorInterchangeComponentTopologyNode addWorkUnitProcessingInterchangeComponent(String name, TopologyNodeTypeEnum topologyNodeType, WorkUnitProcessorTopologyNode wup);

    public Boolean getSubsystemInternalTrafficEncrypt();

    public ResilienceModeEnum getResilienceMode();
    public ConcurrencyModeEnum getConcurrenceMode();
}
