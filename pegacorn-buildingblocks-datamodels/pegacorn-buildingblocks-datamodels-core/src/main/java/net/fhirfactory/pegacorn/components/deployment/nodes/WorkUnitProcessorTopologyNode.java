package net.fhirfactory.pegacorn.components.deployment.nodes;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class WorkUnitProcessorTopologyNode extends TopologyNode {
    private static final Logger LOG = LoggerFactory.getLogger(WorkUnitProcessorTopologyNode.class);

    private ArrayList<TopologyNodeFDN> wupComponents;
    private ArrayList<TopologyNodeFDN> wupInterchangeComponents;

    public WorkUnitProcessorTopologyNode(){
        this.wupComponents = new ArrayList<>();
        this.wupInterchangeComponents = new ArrayList<>();
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ArrayList<TopologyNodeFDN> getWupComponents() {
        return wupComponents;
    }

    public void setWupComponents(ArrayList<TopologyNodeFDN> wupComponents) {
        this.wupComponents = wupComponents;
    }

    public ArrayList<TopologyNodeFDN> getWupInterchangeComponents() {
        return wupInterchangeComponents;
    }

    public void setWupInterchangeComponents(ArrayList<TopologyNodeFDN> wupInterchangeComponents) {
        this.wupInterchangeComponents = wupInterchangeComponents;
    }

}
