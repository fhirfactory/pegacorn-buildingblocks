package net.fhirfactory.pegacorn.core.model.topology.nodes.common;

import net.fhirfactory.pegacorn.core.model.componentid.*;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;

public interface EndpointProviderInterface {
    public void addEndpoint(TopologyNodeFDN endpointFDN);
    public TopologyNodeRDN getComponentRDN();
    public TopologyNodeFDN getComponentFDN();
    public ComponentIdType getComponentID();
    public TopologyNodeFunctionFDN getNodeFunctionFDN();
    public ComponentTypeTypeEnum getComponentType();
    public ResilienceModeEnum getResilienceMode();
    public ConcurrencyModeEnum getConcurrencyMode();
}
