package net.fhirfactory.pegacorn.core.model.topology.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;

import java.util.ArrayList;
import java.util.Objects;

public class IPCInterface {
    private TopologyNodeFDN enablingTopologyEndpoint;
    private String targetName;
    private String groupName;
    private ArrayList<ResilienceModeEnum> supportedDeploymentModes;
    private ArrayList<IPCInterfaceDefinition> supportedInterfaceDefinitions;

    public IPCInterface(){
        supportedDeploymentModes = new ArrayList<>();
        supportedInterfaceDefinitions = new ArrayList<>();
    }


    public ArrayList<ResilienceModeEnum> getSupportedDeploymentModes() {
        return supportedDeploymentModes;
    }

    public void setSupportedDeploymentModes(ArrayList<ResilienceModeEnum> supportedDeploymentModes) {
        this.supportedDeploymentModes = supportedDeploymentModes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPCInterface that = (IPCInterface) o;
        return Objects.equals(getEnablingTopologyEndpoint(), that.getEnablingTopologyEndpoint()) && getInstanceName().equals(that.getInstanceName()) && Objects.equals(getTargetName(), that.getTargetName()) && Objects.equals(groupName, that.groupName) && Objects.equals(getSupportedDeploymentModes(), that.getSupportedDeploymentModes()) && Objects.equals(getSupportedInterfaceDefinitions(), that.getSupportedInterfaceDefinitions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEnablingTopologyEndpoint(), getInstanceName(), getTargetName(), groupName, getSupportedDeploymentModes(), getSupportedInterfaceDefinitions());
    }

    @JsonIgnore
    public String getInstanceName(){
        return(getEnablingTopologyEndpoint().getLeafRDN().getNodeName());
    }

    @JsonIgnore
    public String getInstanceVersion(){
        return(getEnablingTopologyEndpoint().getLeafRDN().getNodeVersion());
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public TopologyNodeFDN getEnablingTopologyEndpoint() {
        return enablingTopologyEndpoint;
    }

    public void setEnablingTopologyEndpoint(TopologyNodeFDN enablingTopologyEndpoint) {
        this.enablingTopologyEndpoint = enablingTopologyEndpoint;
    }

    public ArrayList<IPCInterfaceDefinition> getSupportedInterfaceDefinitions() {
        return supportedInterfaceDefinitions;
    }

    public void setSupportedInterfaceDefinitions(ArrayList<IPCInterfaceDefinition> supportedInterfaceDefinitions) {
        this.supportedInterfaceDefinitions = supportedInterfaceDefinitions;
    }

    public ArrayList<String> getSupportInterfaceTags(){
        ArrayList<String> tags = new ArrayList<>();
        for(IPCInterfaceDefinition ipcInterface: getSupportedInterfaceDefinitions()){
            tags.add(ipcInterface.getInterfaceDefinitionTag());
        }
        return(tags);
    }

    public boolean supportsInterface(String interfaceTag ){
        for(IPCInterfaceDefinition ipcInterface: getSupportedInterfaceDefinitions()){
            if(interfaceTag.contentEquals(ipcInterface.getInterfaceDefinitionTag())){
                return(true);
            }
        }
        return(false);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "IPCInterface{" +
                "enablingTopologyEndpoint=" + enablingTopologyEndpoint +
                ", targetName='" + targetName + '\'' +
                ", groupName='" + groupName + '\'' +
                ", supportedDeploymentModes=" + supportedDeploymentModes +
                ", supportedInterfaceDefinitions=" + supportedInterfaceDefinitions +
                '}';
    }
}
