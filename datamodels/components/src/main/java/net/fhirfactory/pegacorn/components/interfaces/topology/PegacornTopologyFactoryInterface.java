package net.fhirfactory.pegacorn.components.interfaces.topology;

import net.fhirfactory.pegacorn.common.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.*;

public interface PegacornTopologyFactoryInterface {
    public TopologyNodeRDN createNodeRDN(String nodeName, String nodeVersion, ComponentTypeTypeEnum nodeType);

    public WorkshopTopologyNode createWorkshop(String name, String version, ProcessingPlantTopologyNode processingPlant, ComponentTypeTypeEnum nodeType);

    public WorkUnitProcessorTopologyNode createWorkUnitProcessor(String name, String version, WorkshopTopologyNode workshop, ComponentTypeTypeEnum nodeType);

    public WorkUnitProcessorComponentTopologyNode createWorkUnitProcessorComponent(String name, ComponentTypeTypeEnum topologyType, WorkUnitProcessorTopologyNode wup);

    public WorkUnitProcessorInterchangeComponentTopologyNode createWorkUnitProcessingInterchangeComponent(String name, ComponentTypeTypeEnum topologyNodeType, WorkUnitProcessorTopologyNode wup);

    public Boolean getSubsystemInternalTrafficEncrypt();

    public ResilienceModeEnum getResilienceMode();
    public ConcurrencyModeEnum getConcurrenceMode();

    public void initialise();
}
