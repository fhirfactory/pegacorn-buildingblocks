package net.fhirfactory.pegacorn.components.deployment.nodes;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class DeploymentSiteTopologyNode extends TopologyNode {
    private static final Logger LOG = LoggerFactory.getLogger(DeploymentSiteTopologyNode.class);

    private ArrayList<TopologyNodeFDN> clusterServices;
    private Integer instanceCount;

    public DeploymentSiteTopologyNode(){
        super();
        this.clusterServices = new ArrayList<>();
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ArrayList<TopologyNodeFDN> getClusterServices() {
        return clusterServices;
    }

    public void setClusterServices(ArrayList<TopologyNodeFDN> clusterServices) {
        this.clusterServices = clusterServices;
    }

    public Integer getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(Integer instanceCount) {
        this.instanceCount = instanceCount;
    }
}
