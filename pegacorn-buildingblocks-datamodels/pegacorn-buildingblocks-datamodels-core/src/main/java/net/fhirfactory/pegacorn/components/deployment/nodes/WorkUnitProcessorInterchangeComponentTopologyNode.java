package net.fhirfactory.pegacorn.components.deployment.nodes;

import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkUnitProcessorInterchangeComponentTopologyNode extends TopologyNode {
    private static final Logger LOG = LoggerFactory.getLogger(WorkUnitProcessorInterchangeComponentTopologyNode.class);

    @Override
    protected Logger getLogger() {
        return (LOG);
    }
}
