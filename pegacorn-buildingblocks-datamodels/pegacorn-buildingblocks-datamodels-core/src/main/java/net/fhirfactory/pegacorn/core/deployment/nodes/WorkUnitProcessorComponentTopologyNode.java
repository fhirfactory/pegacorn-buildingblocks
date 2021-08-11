package net.fhirfactory.pegacorn.core.deployment.nodes;

import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkUnitProcessorComponentTopologyNode extends TopologyNode {
    private static final Logger LOG = LoggerFactory.getLogger(WorkUnitProcessorComponentTopologyNode.class);

    @Override
    protected Logger getLogger() {
        return (LOG);
    }
}
