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
package net.fhirfactory.pegacorn.components.endpoints;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Endpoint;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public class PetasosEndpoint implements Serializable {
    private PetasosEndpointIdentifier endpointID;
    private String endpointServiceName;
    private String endpointDescription;
    private Endpoint representativeFHIREndpoint;
    private PetasosEndpointStatusEnum endpointStatus;
    private PetasosEndpointFunctionTypeEnum interfaceFunction;
    private PetasosEndpointChannelScopeEnum endpointScope;

    public PetasosEndpoint(){
        this.endpointID = null;
        this.endpointServiceName = null;
        this.representativeFHIREndpoint = null;
        this.endpointStatus = null;
        this.interfaceFunction = null;
        this.endpointDescription = null;
        this.endpointScope = null;
    }

    public PetasosEndpoint(PetasosEndpoint ori){
        this.setEndpointID(ori.getEndpointID());
        this.setEndpointDescription(ori.getEndpointDescription());
        this.setEndpointStatus(ori.getEndpointStatus());
        this.setRepresentativeFHIREndpoint(ori.getRepresentativeFHIREndpoint());
        this.setInterfaceFunction(ori.getInterfaceFunction());
        this.setEndpointServiceName(ori.getEndpointServiceName());
        this.setEndpointScope(ori.getEndpointScope());
    }

    public PetasosEndpointChannelScopeEnum getEndpointScope() {
        return endpointScope;
    }

    public void setEndpointScope(PetasosEndpointChannelScopeEnum endpointScope) {
        this.endpointScope = endpointScope;
    }

    public PetasosEndpointFunctionTypeEnum getInterfaceFunction() {
        return interfaceFunction;
    }

    public void setInterfaceFunction(PetasosEndpointFunctionTypeEnum interfaceFunction) {
        this.interfaceFunction = interfaceFunction;
    }

    public String getEndpointServiceName() {
        return endpointServiceName;
    }

    public Endpoint getRepresentativeFHIREndpoint() {
        return representativeFHIREndpoint;
    }

    public void setRepresentativeFHIREndpoint(Endpoint representativeFHIREndpoint) {
        this.representativeFHIREndpoint = representativeFHIREndpoint;
    }

    public PetasosEndpointStatusEnum getEndpointStatus() {
        return endpointStatus;
    }

    public void setEndpointStatus(PetasosEndpointStatusEnum endpointStatus) {
        this.endpointStatus = endpointStatus;
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

    public void setEndpointServiceName(String endpointServiceName) {
        this.endpointServiceName = endpointServiceName;
    }

    @Override
    public String toString() {
        return "PetasosEndpoint{" +
                "endpointID=" + endpointID +
                ", endpointServiceName='" + endpointServiceName + '\'' +
                ", endpointDescription='" + endpointDescription + '\'' +
                ", representativeFHIREndpoint=" + representativeFHIREndpoint +
                ", endpointStatus=" + endpointStatus +
                ", interfaceFunction=" + interfaceFunction +
                ", endpointScope=" + endpointScope +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PetasosEndpoint)) return false;
        PetasosEndpoint that = (PetasosEndpoint) o;
        return Objects.equals(getEndpointID(), that.getEndpointID()) && Objects.equals(getEndpointServiceName(), that.getEndpointServiceName()) && Objects.equals(getEndpointDescription(), that.getEndpointDescription()) && Objects.equals(getRepresentativeFHIREndpoint(), that.getRepresentativeFHIREndpoint()) && getEndpointStatus() == that.getEndpointStatus() && getInterfaceFunction() == that.getInterfaceFunction() && getEndpointScope() == that.getEndpointScope();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEndpointID(), getEndpointServiceName(), getEndpointDescription(), getRepresentativeFHIREndpoint(), getEndpointStatus(), getInterfaceFunction(), getEndpointScope());
    }

    public PetasosEndpoint encrichPetasosEndpoint(PetasosEndpoint ori){
        if(ori == null){
            return(this);
        }
        if(!StringUtils.isEmpty(ori.getEndpointID().getEndpointGroup())  && StringUtils.isEmpty(this.getEndpointID().getEndpointGroup())){
            this.getEndpointID().setEndpointGroup(ori.getEndpointID().getEndpointGroup());
        }
        if(!StringUtils.isEmpty(ori.getEndpointID().getEndpointDetailedAddressName()) && StringUtils.isEmpty(this.getEndpointID().getEndpointDetailedAddressName())){
            this.getEndpointID().setEndpointDetailedAddressName(ori.getEndpointID().getEndpointDetailedAddressName());
        }
        if(!StringUtils.isEmpty(ori.getEndpointID().getEndpointSite()) && StringUtils.isEmpty(this.getEndpointID().getEndpointSite())){
            this.getEndpointID().setEndpointSite(ori.getEndpointID().getEndpointSite());
        }
        if(ori.getEndpointID().getEndpointZone() != null && this.getEndpointID().getEndpointZone() == null){
            this.getEndpointID().setEndpointZone(ori.getEndpointID().getEndpointZone());
        }
        if(!StringUtils.isEmpty(ori.getEndpointServiceName()) && StringUtils.isEmpty(this.getEndpointServiceName())){
            this.setEndpointServiceName(ori.getEndpointServiceName());
        }
        if(!StringUtils.isEmpty(ori.getEndpointDescription()) && StringUtils.isEmpty(this.getEndpointDescription())){
            this.setEndpointDescription(ori.getEndpointDescription());
        }
        if((ori.getEndpointID() != null) && (this.getEndpointScope() == null)){
            this.setEndpointScope(ori.getEndpointScope());
        }
        if((ori.getRepresentativeFHIREndpoint() != null) && (this.getRepresentativeFHIREndpoint() == null)){
            this.setRepresentativeFHIREndpoint(ori.getRepresentativeFHIREndpoint());
        } else {
            if(ori.getRepresentativeFHIREndpoint() != null){
                this.getRepresentativeFHIREndpoint().getMeta().setLastUpdated(Date.from(Instant.now()));
                this.getRepresentativeFHIREndpoint().setStatus(ori.getRepresentativeFHIREndpoint().getStatus());
            }
        }
        if((ori.getEndpointStatus() != null)){
            this.setEndpointStatus(ori.getEndpointStatus());
        }
        if(ori.getInterfaceFunction() != null){
            this.setInterfaceFunction(ori.getInterfaceFunction());
        }
        return(this);
    }
}
