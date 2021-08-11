package net.fhirfactory.pegacorn.core.model.topology.nodes;

import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.topology.common.TopologyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class BusinessServiceTopologyNode extends TopologyNode {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessServiceTopologyNode.class);

    private ArrayList<TopologyNodeFDN> deploymentSites;
    private ArrayList<TopologyNodeFDN> externalisedServices;
    private boolean preferringEncryption;
    private String defaultDNSName;

    public BusinessServiceTopologyNode(){
        super();
        this.deploymentSites = new ArrayList<>();
        this.externalisedServices = new ArrayList<>();
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ArrayList<TopologyNodeFDN> getDeploymentSites() {
        return deploymentSites;
    }

    public void setDeploymentSites(ArrayList<TopologyNodeFDN> deploymentSites) {
        this.deploymentSites = deploymentSites;
    }

    public ArrayList<TopologyNodeFDN> getExternalisedServices() {
        return externalisedServices;
    }

    public void setExternalisedServices(ArrayList<TopologyNodeFDN> externalisedServices) {
        this.externalisedServices = externalisedServices;
    }

    public boolean isPreferringEncryption() {
        return preferringEncryption;
    }

    public void setPreferringEncryption(boolean preferringEncryption) {
        this.preferringEncryption = preferringEncryption;
    }

    public String getDefaultDNSName() {
        return defaultDNSName;
    }

    public void setDefaultDNSName(String defaultDNSName) {
        this.defaultDNSName = defaultDNSName;
    }
}
