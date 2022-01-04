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
package net.fhirfactory.pegacorn.core.model.petasos.endpoint;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;

import java.io.Serializable;
import java.util.Objects;

public class JGroupsIntegrationPointIdentifier implements Serializable {
    private String subsystemName;
    private String channelName;
    private NetworkSecurityZoneEnum subsystemDeploymentZone;
    private String subsystemDeploymentGroup;
    private String subsystemDeploymentSite;
    private String integrationPointDetailedAddressName;
    private ComponentIdType integrationPointComponentID;
    private ComponentIdType processingPlantComponentID;

    public JGroupsIntegrationPointIdentifier(){
        this.subsystemName = null;
        this.subsystemDeploymentGroup = null;
        this.channelName = null;
        this.subsystemDeploymentSite = null;
        this.subsystemDeploymentZone = null;
        this.integrationPointDetailedAddressName = null;
        this.integrationPointComponentID = null;
        this.processingPlantComponentID = null;
    }

    public String getSubsystemName() {
        return subsystemName;
    }

    public void setSubsystemName(String subsystemName) {
        this.subsystemName = subsystemName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public NetworkSecurityZoneEnum getSubsystemDeploymentZone() {
        return subsystemDeploymentZone;
    }

    public void setSubsystemDeploymentZone(NetworkSecurityZoneEnum subsystemDeploymentZone) {
        this.subsystemDeploymentZone = subsystemDeploymentZone;
    }

    public String getSubsystemDeploymentGroup() {
        return subsystemDeploymentGroup;
    }

    public void setSubsystemDeploymentGroup(String subsystemDeploymentGroup) {
        this.subsystemDeploymentGroup = subsystemDeploymentGroup;
    }

    public String getSubsystemDeploymentSite() {
        return subsystemDeploymentSite;
    }

    public void setSubsystemDeploymentSite(String subsystemDeploymentSite) {
        this.subsystemDeploymentSite = subsystemDeploymentSite;
    }

    public String getIntegrationPointDetailedAddressName() {
        return integrationPointDetailedAddressName;
    }

    public void setIntegrationPointDetailedAddressName(String integrationPointDetailedAddressName) {
        this.integrationPointDetailedAddressName = integrationPointDetailedAddressName;
    }

    public ComponentIdType getIntegrationPointComponentID() {
        return integrationPointComponentID;
    }

    public void setIntegrationPointComponentID(ComponentIdType integrationPointComponentID) {
        this.integrationPointComponentID = integrationPointComponentID;
    }

    public ComponentIdType getProcessingPlantComponentID() {
        return processingPlantComponentID;
    }

    public void setProcessingPlantComponentID(ComponentIdType processingPlantComponentID) {
        this.processingPlantComponentID = processingPlantComponentID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JGroupsIntegrationPointIdentifier)) return false;
        JGroupsIntegrationPointIdentifier that = (JGroupsIntegrationPointIdentifier) o;
        return Objects.equals(getSubsystemName(), that.getSubsystemName()) && Objects.equals(getChannelName(), that.getChannelName()) && getSubsystemDeploymentZone() == that.getSubsystemDeploymentZone() && Objects.equals(getSubsystemDeploymentGroup(), that.getSubsystemDeploymentGroup()) && Objects.equals(getSubsystemDeploymentSite(), that.getSubsystemDeploymentSite()) && Objects.equals(getIntegrationPointDetailedAddressName(), that.getIntegrationPointDetailedAddressName()) && Objects.equals(getIntegrationPointComponentID(), that.getIntegrationPointComponentID()) && Objects.equals(getProcessingPlantComponentID(), that.getProcessingPlantComponentID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubsystemName(), getChannelName(), getSubsystemDeploymentZone(), getSubsystemDeploymentGroup(), getSubsystemDeploymentSite(), getIntegrationPointDetailedAddressName(), getIntegrationPointComponentID(), getProcessingPlantComponentID());
    }

    @Override
    public String toString() {
        return "PetasosEndpointIdentifier{" +
                "endpointName=" + subsystemName +
                ", endpointChannelName=" + channelName +
                ", endpointZone=" + subsystemDeploymentZone +
                ", endpointGroup=" + subsystemDeploymentGroup +
                ", endpointSite=" + subsystemDeploymentSite +
                ", endpointDetailedAddressName=" + integrationPointDetailedAddressName +
                ", endpointComponentID=" + integrationPointComponentID +
                ", processingPlantComponentID=" + processingPlantComponentID +
                '}';
    }
}
