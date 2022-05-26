/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.datagrid.datatypes;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DatagridPersistenceServiceType implements Serializable {
    private Set<DatagridPersistenceResourceCapabilityType> supportedResourceTypes;
    private ComponentIdType persistenceServiceInstance;
    private String site;
    private boolean active;


    //
    // Constructor(s)
    //

    public DatagridPersistenceServiceType(){
        this.persistenceServiceInstance = null;
        this.supportedResourceTypes = new HashSet<>();
        this.site = null;
        this.active = false;
    }

    //
    // Getters and Setters
    //

    public Set<DatagridPersistenceResourceCapabilityType> getSupportedResourceTypes() {
        return supportedResourceTypes;
    }

    public void setSupportedResourceTypes(Set<DatagridPersistenceResourceCapabilityType> supportedResourceTypes) {
        this.supportedResourceTypes = supportedResourceTypes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ComponentIdType getPersistenceServiceInstance() {
        return persistenceServiceInstance;
    }

    public void setPersistenceServiceInstance(ComponentIdType persistenceServiceInstanceName) {
        this.persistenceServiceInstance = persistenceServiceInstanceName;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
    //
    // Hashcode and Equals
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatagridPersistenceServiceType that = (DatagridPersistenceServiceType) o;
        return isActive() == that.isActive() && Objects.equals(getSupportedResourceTypes(), that.getSupportedResourceTypes()) && Objects.equals(getPersistenceServiceInstance(), that.getPersistenceServiceInstance()) && Objects.equals(getSite(), that.getSite());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSupportedResourceTypes(), getPersistenceServiceInstance(), getSite(), isActive());
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "DatagridPersistenceServiceType{" +
                "supportedResourceTypes=" + supportedResourceTypes +
                ", persistenceServiceInstance=" + persistenceServiceInstance +
                ", site=" + site +
                ", active=" + active +
                '}';
    }
}
