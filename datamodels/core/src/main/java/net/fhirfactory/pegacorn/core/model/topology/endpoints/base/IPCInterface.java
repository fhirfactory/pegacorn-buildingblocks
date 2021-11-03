/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.core.model.topology.endpoints.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class IPCInterface implements Serializable {
    private TopologyNodeFDN enablingTopologyEndpoint;
    private String targetName;
    private String groupName;
    private boolean encrypted;
    private ArrayList<ResilienceModeEnum> supportedDeploymentModes;
    private ArrayList<IPCInterfaceDefinition> supportedInterfaceDefinitions;

    public IPCInterface(){
        supportedDeploymentModes = new ArrayList<>();
        supportedInterfaceDefinitions = new ArrayList<>();
        encrypted = false;
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

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    //
    // To String
    //


    @Override
    public String toString() {
        return "IPCInterface{" +
                "enablingTopologyEndpoint=" + enablingTopologyEndpoint +
                ", targetName='" + targetName + '\'' +
                ", groupName='" + groupName + '\'' +
                ", encrypted=" + encrypted +
                ", supportedDeploymentModes=" + supportedDeploymentModes +
                ", supportedInterfaceDefinitions=" + supportedInterfaceDefinitions +
                ", instanceName='" + getInstanceName() + '\'' +
                ", instanceVersion='" + getInstanceVersion() + '\'' +
                ", supportInterfaceTags=" + getSupportInterfaceTags() +
                '}';
    }
}
