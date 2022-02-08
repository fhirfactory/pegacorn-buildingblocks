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
package net.fhirfactory.pegacorn.core.model.datagrid.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.datagrid.valuesets.DatagridPersistenceResourceStatusEnum;
import net.fhirfactory.pegacorn.core.model.keyring.PegacornResourceKeyring;

import java.io.Serializable;
import java.time.Instant;

public class DataGridResourceIdType extends PegacornResourceKeyring implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant gridLoadInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant checkInstant;
    private DatagridPersistenceResourceStatusEnum gridResourcePersistenceStatus;

    //
    // Constructor(s)
    //

    public DataGridResourceIdType(){
        this.gridLoadInstant = null;
        this.checkInstant = null;
        this.gridResourcePersistenceStatus = null;
    }

    //
    // Getters and Setters
    //


    public DatagridPersistenceResourceStatusEnum getGridResourcePersistenceStatus() {
        return gridResourcePersistenceStatus;
    }

    public void setGridResourcePersistenceStatus(DatagridPersistenceResourceStatusEnum gridResourcePersistenceStatus) {
        this.gridResourcePersistenceStatus = gridResourcePersistenceStatus;
    }

    public Instant getGridLoadInstant() {
        return gridLoadInstant;
    }

    public void setGridLoadInstant(Instant gridLoadInstant) {
        this.gridLoadInstant = gridLoadInstant;
    }

    public Instant getCheckInstant() {
        return checkInstant;
    }

    public void setCheckInstant(Instant checkInstant) {
        this.checkInstant = checkInstant;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "DataGridResourceIdType{" +
                "gridLoadInstant=" + gridLoadInstant +
                ", checkInstant=" + checkInstant +
                ", gridResourcePersistenceStatus=" + gridResourcePersistenceStatus +
                ", primaryBusinessIdentifier=" + getPrimaryBusinessIdentifier() +
                ", localId='" + getLocalId() + '\'' +
                ", sourceSystemKeyMap=" + getSourceSystemKeyMap() +
                ", resourceId=" + getResourceId() +
                ", businessIdentifiersMap=" + getBusinessIdentifiersMap() +
                ", defaultSourceSystemMapEntry='" + getDefaultSourceSystemMapEntry() + '\'' +
                ", resourceType='" + getResourceType() + '\'' +
                '}';
    }
}
