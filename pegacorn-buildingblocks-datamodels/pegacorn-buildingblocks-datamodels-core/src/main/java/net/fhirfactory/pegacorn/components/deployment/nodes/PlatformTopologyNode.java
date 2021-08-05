package net.fhirfactory.pegacorn.components.deployment.nodes;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class PlatformTopologyNode extends TopologyNode {
    private static final Logger LOG = LoggerFactory.getLogger(PlatformTopologyNode.class);

    private ArrayList<TopologyNodeFDN> processingPlants;
    private Integer instanceCount;

    public PlatformTopologyNode(){
        this.processingPlants = new ArrayList<>();
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ArrayList<TopologyNodeFDN> getProcessingPlants() {
        return processingPlants;
    }

    public void setProcessingPlants(ArrayList<TopologyNodeFDN> processingPlants) {
        this.processingPlants = processingPlants;
    }

    public Integer getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(Integer instanceCount) {
        this.instanceCount = instanceCount;
    }
}
