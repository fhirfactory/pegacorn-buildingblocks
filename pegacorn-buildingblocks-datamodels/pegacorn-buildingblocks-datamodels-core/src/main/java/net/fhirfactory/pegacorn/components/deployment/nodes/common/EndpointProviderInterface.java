package net.fhirfactory.pegacorn.components.deployment.nodes.common;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ResilienceModeEnum;

public interface EndpointProviderInterface {
    public void addEndpoint(TopologyNodeFDN endpointFDN);
    public TopologyNodeRDN getNodeRDN();
    public TopologyNodeFDN getNodeFDN();
    public TopologyNodeFunctionFDN getNodeFunctionFDN();
    public TopologyNodeTypeEnum getComponentType();
    public ResilienceModeEnum getResilienceMode();
    public ConcurrencyModeEnum getConcurrencyMode();
}
