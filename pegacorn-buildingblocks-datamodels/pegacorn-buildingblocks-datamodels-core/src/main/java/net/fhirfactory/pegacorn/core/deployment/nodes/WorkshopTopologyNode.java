package net.fhirfactory.pegacorn.core.deployment.nodes;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class WorkshopTopologyNode extends TopologyNode {
    private static final Logger LOG = LoggerFactory.getLogger(WorkshopTopologyNode.class);

    private ArrayList<TopologyNodeFDN> wupSet;

    public WorkshopTopologyNode(){
        this.wupSet = new ArrayList<>();
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ArrayList<TopologyNodeFDN> getWupSet() {
        return wupSet;
    }

    public void setWupSet(ArrayList<TopologyNodeFDN> wupSet) {
        this.wupSet = wupSet;
    }
}
