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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IPCAdapter implements Serializable {
    private ComponentIdType enablingTopologyEndpoint;
    private String targetNameInstant;
    private String groupName;
    private boolean encrypted;
    private boolean active;
    private ArrayList<ResilienceModeEnum> supportedDeploymentModes;
    private ArrayList<IPCAdapterDefinition> supportedInterfaceDefinitions;
    private Map<String, String> additionalParameters;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant lastActivity;

    //
    // Constructor(s)
    //
    public IPCAdapter(){
        this.supportedDeploymentModes = new ArrayList<>();
        this.supportedInterfaceDefinitions = new ArrayList<>();
        this.encrypted = false;
        this.active = false;
        this.targetNameInstant = null;
        this.groupName = null;
        this.enablingTopologyEndpoint = null;
        this.additionalParameters = new HashMap<>();
        lastActivity = Instant.EPOCH;
    }

    //
    // Getters and Setters
    //

    public ArrayList<ResilienceModeEnum> getSupportedDeploymentModes() {
        return supportedDeploymentModes;
    }

    public void setSupportedDeploymentModes(ArrayList<ResilienceModeEnum> supportedDeploymentModes) {
        this.supportedDeploymentModes = supportedDeploymentModes;
    }

    public String getTargetNameInstant() {
        return targetNameInstant;
    }

    public void setTargetNameInstant(String targetNameInstant) {
        this.targetNameInstant = targetNameInstant;
    }

    public ComponentIdType getEnablingTopologyEndpoint() {
        return enablingTopologyEndpoint;
    }

    public void setEnablingTopologyEndpoint(ComponentIdType enablingTopologyEndpoint) {
        this.enablingTopologyEndpoint = enablingTopologyEndpoint;
    }

    public ArrayList<IPCAdapterDefinition> getSupportedInterfaceDefinitions() {
        return supportedInterfaceDefinitions;
    }

    public void setSupportedInterfaceDefinitions(ArrayList<IPCAdapterDefinition> supportedInterfaceDefinitions) {
        this.supportedInterfaceDefinitions = supportedInterfaceDefinitions;
    }

    public ArrayList<String> getSupportInterfaceTags(){
        ArrayList<String> tags = new ArrayList<>();
        for(IPCAdapterDefinition ipcInterface: getSupportedInterfaceDefinitions()){
            tags.add(ipcInterface.getInterfaceDefinitionTag());
        }
        return(tags);
    }

    public boolean supportsInterface(String interfaceTag ){
        for(IPCAdapterDefinition ipcInterface: getSupportedInterfaceDefinitions()){
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Map<String, String> getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(Map<String, String> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    public Instant getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Instant lastActivity) {
        this.lastActivity = lastActivity;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "IPCInterface{" +
                "enablingTopologyEndpoint=" + enablingTopologyEndpoint +
                ", targetNameInstant=" + targetNameInstant +
                ", groupName=" + groupName +
                ", encrypted=" + encrypted +
                ", supportedDeploymentModes=" + supportedDeploymentModes +
                ", supportedInterfaceDefinitions=" + supportedInterfaceDefinitions +
                ", supportInterfaceTags=" + getSupportInterfaceTags() +
                ", active=" + active +
                ", lastActivity=" + lastActivity +
                '}';
    }

    //
    // Hashcode and Comparator
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IPCAdapter)) return false;
        IPCAdapter that = (IPCAdapter) o;
        return isEncrypted() == that.isEncrypted() && isActive() == that.isActive() && Objects.equals(getEnablingTopologyEndpoint(), that.getEnablingTopologyEndpoint()) && Objects.equals(getTargetNameInstant(), that.getTargetNameInstant()) && Objects.equals(getGroupName(), that.getGroupName()) && Objects.equals(getSupportedDeploymentModes(), that.getSupportedDeploymentModes()) && Objects.equals(getSupportedInterfaceDefinitions(), that.getSupportedInterfaceDefinitions()) && Objects.equals(getAdditionalParameters(), that.getAdditionalParameters()) && Objects.equals(getLastActivity(), that.getLastActivity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEnablingTopologyEndpoint(), getTargetNameInstant(), getGroupName(), isEncrypted(), isActive(), getSupportedDeploymentModes(), getSupportedInterfaceDefinitions(), getAdditionalParameters(), getLastActivity());
    }
}
