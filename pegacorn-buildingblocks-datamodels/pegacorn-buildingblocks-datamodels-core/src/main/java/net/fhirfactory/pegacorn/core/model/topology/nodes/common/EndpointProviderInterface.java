package net.fhirfactory.pegacorn.core.model.topology.nodes.common;

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;

public interface EndpointProviderInterface {
    public void addEndpoint(TopologyNodeFDN endpointFDN);
    public TopologyNodeRDN getNodeRDN();
    public TopologyNodeFDN getNodeFDN();
    public TopologyNodeFunctionFDN getNodeFunctionFDN();
    public TopologyNodeTypeEnum getComponentType();
    public ResilienceModeEnum getResilienceMode();
    public ConcurrencyModeEnum getConcurrencyMode();
}
