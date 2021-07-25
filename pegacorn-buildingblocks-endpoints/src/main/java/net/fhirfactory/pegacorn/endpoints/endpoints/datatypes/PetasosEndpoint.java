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
package net.fhirfactory.pegacorn.endpoints.endpoints.datatypes;

import org.hl7.fhir.r4.model.Endpoint;

import java.io.Serializable;
import java.util.Objects;

public class PetasosEndpoint implements Serializable {
    String endpointName;
    String endpointService;
    String endpointZone;
    String endpointGroup;
    String endpointSite;
    Endpoint representativeFHIREndpoint;
    PetasosInterfaceStatusEnum status;
    PetasosInterfaceFunctionTypeEnum interfaceFunction;

    public PetasosEndpoint(){
        this.endpointName = null;
        this.endpointService = null;
        this.representativeFHIREndpoint = null;
        this.status = null;
        this.endpointGroup = null;
        this.endpointZone = null;
        this.endpointSite = null;
        this.interfaceFunction = null;
    }

    public PetasosInterfaceFunctionTypeEnum getInterfaceFunction() {
        return interfaceFunction;
    }

    public void setInterfaceFunction(PetasosInterfaceFunctionTypeEnum interfaceFunction) {
        this.interfaceFunction = interfaceFunction;
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    public String getEndpointService() {
        return endpointService;
    }

    public void setEndpointService(String endpointService) {
        this.endpointService = endpointService;
    }

    public Endpoint getRepresentativeFHIREndpoint() {
        return representativeFHIREndpoint;
    }

    public void setRepresentativeFHIREndpoint(Endpoint representativeFHIREndpoint) {
        this.representativeFHIREndpoint = representativeFHIREndpoint;
    }

    public PetasosInterfaceStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PetasosInterfaceStatusEnum status) {
        this.status = status;
    }

    public String getEndpointZone() {
        return endpointZone;
    }

    public void setEndpointZone(String endpointZone) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetasosEndpoint)) return false;
        PetasosEndpoint that = (PetasosEndpoint) o;
        return Objects.equals(getEndpointName(), that.getEndpointName()) && Objects.equals(getEndpointService(), that.getEndpointService()) && Objects.equals(getEndpointZone(), that.getEndpointZone()) && Objects.equals(getEndpointGroup(), that.getEndpointGroup()) && Objects.equals(getEndpointSite(), that.getEndpointSite()) && Objects.equals(getRepresentativeFHIREndpoint(), that.getRepresentativeFHIREndpoint()) && getStatus() == that.getStatus() && getInterfaceFunction() == that.getInterfaceFunction();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEndpointName(), getEndpointService(), getEndpointZone(), getEndpointGroup(), getEndpointSite(), getRepresentativeFHIREndpoint(), getStatus(), getInterfaceFunction());
    }

    @Override
    public String toString() {
        return "PetasosEndpoint{" +
                "endpointName='" + endpointName + '\'' +
                ", endpointService='" + endpointService + '\'' +
                ", endpointZone='" + endpointZone + '\'' +
                ", endpointGroup='" + endpointGroup + '\'' +
                ", endpointSite='" + endpointSite + '\'' +
                ", representativeFHIREndpoint=" + representativeFHIREndpoint +
                ", status=" + status +
                ", interfaceFunction=" + interfaceFunction +
                '}';
    }
}
