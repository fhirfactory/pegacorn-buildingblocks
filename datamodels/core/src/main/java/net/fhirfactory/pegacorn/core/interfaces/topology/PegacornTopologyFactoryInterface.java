package net.fhirfactory.pegacorn.core.interfaces.topology;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.*;

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
