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
    private PetasosEndpointIdentifier endpointID;
    private String endpointService;
    private String endpointDescription;
    private Endpoint representativeFHIREndpoint;
    private PetasosEndpointStatusEnum status;
    private PetasosEndpointFunctionTypeEnum interfaceFunction;

    public PetasosEndpoint(){
        this.endpointID = null;
        this.endpointService = null;
        this.representativeFHIREndpoint = null;
        this.status = null;
        this.interfaceFunction = null;
        this.endpointDescription = null;
    }

    public PetasosEndpointFunctionTypeEnum getInterfaceFunction() {
        return interfaceFunction;
    }

    public void setInterfaceFunction(PetasosEndpointFunctionTypeEnum interfaceFunction) {
        this.interfaceFunction = interfaceFunction;
    }

    public String getEndpointService() {
        return endpointService;
    }

    public void setEndpointServiceName(String endpointService) {
        this.endpointService = endpointService;
    }

    public Endpoint getRepresentativeFHIREndpoint() {
        return representativeFHIREndpoint;
    }

    public void setRepresentativeFHIREndpoint(Endpoint representativeFHIREndpoint) {
        this.representativeFHIREndpoint = representativeFHIREndpoint;
    }

    public PetasosEndpointStatusEnum getStatus() {
        return status;
    }

    public void setStatus(PetasosEndpointStatusEnum status) {
        this.status = status;
    }


    public String getEndpointDescription() {
        return endpointDescription;
    }

    public void setEndpointDescription(String endpointDescription) {
        this.endpointDescription = endpointDescription;
    }

    public PetasosEndpointIdentifier getEndpointID() {
        return endpointID;
    }

    public void setEndpointID(PetasosEndpointIdentifier endpointID) {
        this.endpointID = endpointID;
    }

    public void setEndpointService(String endpointService) {
        this.endpointService = endpointService;
    }

    @Override
    public String toString() {
        return "PetasosEndpoint{" +
                "endpointID=" + endpointID +
                ", endpointService='" + endpointService + '\'' +
                ", endpointDescription='" + endpointDescription + '\'' +
                ", representativeFHIREndpoint=" + representativeFHIREndpoint +
                ", status=" + status +
                ", interfaceFunction=" + interfaceFunction +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetasosEndpoint)) return false;
        PetasosEndpoint that = (PetasosEndpoint) o;
        return Objects.equals(getEndpointID(), that.getEndpointID()) && Objects.equals(getEndpointService(), that.getEndpointService()) && Objects.equals(getEndpointDescription(), that.getEndpointDescription()) && Objects.equals(getRepresentativeFHIREndpoint(), that.getRepresentativeFHIREndpoint()) && getStatus() == that.getStatus() && getInterfaceFunction() == that.getInterfaceFunction();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEndpointID(), getEndpointService(), getEndpointDescription(), getRepresentativeFHIREndpoint(), getStatus(), getInterfaceFunction());
    }
}
