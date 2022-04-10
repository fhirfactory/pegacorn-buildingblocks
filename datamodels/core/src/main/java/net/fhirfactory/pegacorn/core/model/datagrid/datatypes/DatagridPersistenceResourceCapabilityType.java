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

import net.fhirfactory.pegacorn.core.model.datagrid.valuesets.DatagridPersistenceServiceDeploymentScopeEnum;
import net.fhirfactory.pegacorn.core.model.datagrid.valuesets.DatagridPersistenceServiceResourceScopeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.dataparcel.DataParcelTypeDescriptor;

import java.io.Serializable;
import java.util.Objects;

public class DatagridPersistenceResourceCapabilityType implements Serializable {
    private DatagridPersistenceServiceDeploymentScopeEnum persistenceServiceDeploymentScope;
    private DatagridPersistenceServiceResourceScopeEnum persistenceServiceResourceScope;
    private DataParcelTypeDescriptor supportedResourceDescriptor;

    //
    // Constructor(s)
    //

    public DatagridPersistenceResourceCapabilityType(){
        this.supportedResourceDescriptor = null;
        this.persistenceServiceDeploymentScope = null;
        this.persistenceServiceResourceScope = null;
    }

    //
    // Getters and Setters
    //

    public DatagridPersistenceServiceDeploymentScopeEnum getPersistenceServiceDeploymentScope() {
        return persistenceServiceDeploymentScope;
    }

    public void setPersistenceServiceDeploymentScope(DatagridPersistenceServiceDeploymentScopeEnum persistenceServiceDeploymentScope) {
        this.persistenceServiceDeploymentScope = persistenceServiceDeploymentScope;
    }

    public DatagridPersistenceServiceResourceScopeEnum getPersistenceServiceResourceScope() {
        return persistenceServiceResourceScope;
    }

    public void setPersistenceServiceResourceScope(DatagridPersistenceServiceResourceScopeEnum persistenceServiceResourceScope) {
        this.persistenceServiceResourceScope = persistenceServiceResourceScope;
    }

    public DataParcelTypeDescriptor getSupportedResourceDescriptor() {
        return supportedResourceDescriptor;
    }

    public void setSupportedResourceDescriptor(DataParcelTypeDescriptor persistenceServiceScopeValue) {
        this.supportedResourceDescriptor = persistenceServiceScopeValue;
    }

    //
    // Hashcode and Equals
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatagridPersistenceResourceCapabilityType that = (DatagridPersistenceResourceCapabilityType) o;
        return getPersistenceServiceDeploymentScope() == that.getPersistenceServiceDeploymentScope() && getPersistenceServiceResourceScope() == that.getPersistenceServiceResourceScope() && Objects.equals(getSupportedResourceDescriptor(), that.getSupportedResourceDescriptor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPersistenceServiceDeploymentScope(), getPersistenceServiceResourceScope(), getSupportedResourceDescriptor());
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "DatagridPersistenceResourceCapabilityType{" +
                "persistenceServiceDeploymentScope=" + persistenceServiceDeploymentScope +
                ", persistenceServiceResourceScope=" + persistenceServiceResourceScope +
                ", supportedResourceDescriptor=" + getSupportedResourceDescriptor() +
                '}';
    }
}
