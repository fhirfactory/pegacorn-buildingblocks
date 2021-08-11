package net.fhirfactory.pegacorn.core.model.topology.nodes;

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.topology.common.TopologyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class SubsystemTopologyNode extends TopologyNode {
    private static final Logger LOG = LoggerFactory.getLogger(SubsystemTopologyNode.class);

    private ArrayList<TopologyNodeFDN> businessServices;
    private Integer siteCount;

    public SubsystemTopologyNode(){
        businessServices = new ArrayList<>();
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ArrayList<TopologyNodeFDN> getBusinessServices() {
        return businessServices;
    }

    public void setBusinessServices(ArrayList<TopologyNodeFDN> businessServices) {
        this.businessServices = businessServices;
    }

    public Integer getSiteCount() {
        return siteCount;
    }

    public void setSiteCount(Integer siteCount) {
        this.siteCount = siteCount;
    }
}
