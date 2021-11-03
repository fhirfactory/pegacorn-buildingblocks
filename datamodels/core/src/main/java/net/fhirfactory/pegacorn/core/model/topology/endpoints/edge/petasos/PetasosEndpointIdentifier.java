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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;

import java.io.Serializable;
import java.util.Objects;

public class PetasosEndpointIdentifier implements Serializable {
    private String endpointName;
    private String endpointChannelName;
    private NetworkSecurityZoneEnum endpointZone;
    private String endpointGroup;
    private String endpointSite;
    private String endpointDetailedAddressName;
    private ComponentIdType endpointComponentID;
    private ComponentIdType processingPlantComponentID;

    public PetasosEndpointIdentifier(){
        this.endpointName = null;
        this.endpointGroup = null;
        this.endpointChannelName = null;
        this.endpointSite = null;
        this.endpointZone = null;
        this.endpointDetailedAddressName = null;
        this.endpointComponentID = null;
        this.processingPlantComponentID = null;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public String getEndpointChannelName() {
        return endpointChannelName;
    }

    public void setEndpointChannelName(String endpointChannelName) {
        this.endpointChannelName = endpointChannelName;
    }

    public NetworkSecurityZoneEnum getEndpointZone() {
        return endpointZone;
    }

    public void setEndpointZone(NetworkSecurityZoneEnum endpointZone) {
        this.endpointZone = endpointZone;
    }

    public String getEndpointGroup() {
        return endpointGroup;
    }

    public void setEndpointGroup(String endpointGroup) {
        this.endpointGroup = endpointGroup;
    }

    public String getEndpointSite() {
        return endpointSite;
    }

    public void setEndpointSite(String endpointSite) {
        this.endpointSite = endpointSite;
    }

    public String getEndpointDetailedAddressName() {
        return endpointDetailedAddressName;
    }

    public void setEndpointDetailedAddressName(String endpointDetailedAddressName) {
        this.endpointDetailedAddressName = endpointDetailedAddressName;
    }

    public ComponentIdType getEndpointComponentID() {
        return endpointComponentID;
    }

    public void setEndpointComponentID(ComponentIdType endpointComponentID) {
        this.endpointComponentID = endpointComponentID;
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
        if (!(o instanceof PetasosEndpointIdentifier)) return false;
        PetasosEndpointIdentifier that = (PetasosEndpointIdentifier) o;
        return Objects.equals(getEndpointName(), that.getEndpointName()) && Objects.equals(getEndpointChannelName(), that.getEndpointChannelName()) && getEndpointZone() == that.getEndpointZone() && Objects.equals(getEndpointGroup(), that.getEndpointGroup()) && Objects.equals(getEndpointSite(), that.getEndpointSite()) && Objects.equals(getEndpointDetailedAddressName(), that.getEndpointDetailedAddressName()) && Objects.equals(getEndpointComponentID(), that.getEndpointComponentID()) && Objects.equals(getProcessingPlantComponentID(), that.getProcessingPlantComponentID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEndpointName(), getEndpointChannelName(), getEndpointZone(), getEndpointGroup(), getEndpointSite(), getEndpointDetailedAddressName(), getEndpointComponentID(), getProcessingPlantComponentID());
    }

    @Override
    public String toString() {
        return "PetasosEndpointIdentifier{" +
                "endpointName=" + endpointName +
                ", endpointChannelName=" + endpointChannelName +
                ", endpointZone=" + endpointZone +
                ", endpointGroup=" + endpointGroup +
                ", endpointSite=" + endpointSite +
                ", endpointDetailedAddressName=" + endpointDetailedAddressName +
                ", endpointComponentID=" + endpointComponentID +
                ", processingPlantComponentID=" + processingPlantComponentID +
                '}';
    }
}
