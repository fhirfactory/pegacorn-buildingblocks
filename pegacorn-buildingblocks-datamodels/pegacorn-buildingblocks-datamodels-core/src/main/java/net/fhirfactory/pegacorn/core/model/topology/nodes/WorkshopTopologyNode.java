package net.fhirfactory.pegacorn.core.model.topology.nodes;

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.topology.common.TopologyNode;
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
