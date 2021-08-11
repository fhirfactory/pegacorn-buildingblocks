package net.fhirfactory.pegacorn.core.deployment.nodes;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class SolutionTopologyNode extends TopologyNode {
    private static final Logger LOG = LoggerFactory.getLogger(SolutionTopologyNode.class);

    private ArrayList<TopologyNodeFDN> subsystemList;
    private String systemOwner;

    public SolutionTopologyNode(){
        super();
        subsystemList = new ArrayList<>();
    }

    public ArrayList<TopologyNodeFDN> getSubsystemList() {
        return subsystemList;
    }

    public void setSubsystemList(ArrayList<TopologyNodeFDN> subsystemList) {
        this.subsystemList = subsystemList;
    }

    public String getSystemOwner() {
        return systemOwner;
    }

    public void setSystemOwner(String systemOwner) {
        this.systemOwner = systemOwner;

    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }
}
