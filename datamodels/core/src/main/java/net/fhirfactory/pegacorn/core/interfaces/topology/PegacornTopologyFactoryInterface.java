package net.fhirfactory.pegacorn.core.interfaces.topology;

import net.fhirfactory.pegacorn.core.model.componentid.PegacornSystemComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.*;

public interface PegacornTopologyFactoryInterface {
    public TopologyNodeRDN createNodeRDN(String nodeName, String nodeVersion, PegacornSystemComponentTypeTypeEnum nodeType);

    public WorkshopSoftwareComponent createWorkshop(String name, String version, ProcessingPlantSoftwareComponent processingPlant, PegacornSystemComponentTypeTypeEnum nodeType);

    public WorkUnitProcessorSoftwareComponent createWorkUnitProcessor(String name, String version, WorkshopSoftwareComponent workshop, PegacornSystemComponentTypeTypeEnum nodeType);

    public WorkUnitProcessorSubComponentSoftwareComponent createWorkUnitProcessorComponent(String name, PegacornSystemComponentTypeTypeEnum topologyType, WorkUnitProcessorSoftwareComponent wup);

    public WorkUnitProcessorInterchangeSoftwareComponent createWorkUnitProcessingInterchangeComponent(String name, PegacornSystemComponentTypeTypeEnum topologyNodeType, WorkUnitProcessorSoftwareComponent wup);

    public Boolean getSubsystemInternalTrafficEncrypt();

    public ResilienceModeEnum getResilienceMode();
    public ConcurrencyModeEnum getConcurrenceMode();

    public void initialise();
}
