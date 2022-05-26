/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.internal.resources.simple.datatypes;

import net.fhirfactory.pegacorn.core.model.internal.resources.simple.valuesets.ExtremelySimplifiedResourceTypeEnum;

import java.util.Objects;

public class ReferenceESDT {
    String simplifiedID;
    IdentifierESDT identifier;
    ExtremelySimplifiedResourceTypeEnum resourceType;

    public ReferenceESDT(){
        this.simplifiedID = null;
        this.identifier = null;
        this.resourceType = null;
    }

    public ReferenceESDT(String value, ExtremelySimplifiedResourceTypeEnum resourceType){
        this.simplifiedID = value;
        this.resourceType = resourceType;
    }

    public ReferenceESDT(IdentifierESDT value, ExtremelySimplifiedResourceTypeEnum resourceType){
        this.identifier = value;
        this.resourceType = resourceType;
    }

    public ReferenceESDT(String simplifiedID, IdentifierESDT identifier, ExtremelySimplifiedResourceTypeEnum resourceType){
        this.simplifiedID = simplifiedID;
        this.identifier = identifier;
        this.resourceType = resourceType;
    }

    public String getSimplifiedID() {
        return simplifiedID;
    }

    public void setSimplifiedID(String simplifiedID) {
        this.simplifiedID = simplifiedID;
    }

    public ExtremelySimplifiedResourceTypeEnum getResourceType() {
        return resourceType;
    }

    public void setResourceType(ExtremelySimplifiedResourceTypeEnum resourceType) {
        this.resourceType = resourceType;
    }

    public IdentifierESDT getIdentifier() {
        return identifier;
    }

    public void setIdentifier(IdentifierESDT identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "ReferenceESDT{" +
                "simplifiedID=" + simplifiedID +
                ", identifier=" + identifier +
                ", resourceType=" + resourceType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReferenceESDT)) return false;
        ReferenceESDT that = (ReferenceESDT) o;
        return Objects.equals(getSimplifiedID(), that.getSimplifiedID()) && Objects.equals(getIdentifier(), that.getIdentifier()) && getResourceType() == that.getResourceType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSimplifiedID(), getIdentifier(), getResourceType());
    }
}
