/*
 * Copyright (c) 2022 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.traceability.valuesets.TaskStorageStatusEnum;

import java.io.Serializable;
import java.time.Instant;

public class TaskStorageType implements Serializable {
    private TaskStorageStatusEnum localCacheStatus;
    private String localCacheLocation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant localCacheInstant;
    private TaskStorageStatusEnum centralCacheStatus;
    private String centralCacheLocation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant centralCacheInstant;
    private TaskStorageStatusEnum persistenceStatus;
    private String persistenceLocation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant persistenceInstant;

    //
    // Constructor(s)
    //

    public TaskStorageType(){
        this.localCacheLocation = null;
        this.localCacheStatus = TaskStorageStatusEnum.TASK_UNSAVED;
        this.centralCacheLocation = null;
        this.centralCacheStatus = TaskStorageStatusEnum.TASK_UNSAVED;
        this.centralCacheInstant = null;
        this.localCacheInstant = null;
        this.persistenceInstant = null;
        this.persistenceLocation = null;
        this.persistenceStatus = TaskStorageStatusEnum.TASK_UNSAVED;
    }

    //
    // Getters and Setters
    //


    public TaskStorageStatusEnum getPersistenceStatus() {
        return persistenceStatus;
    }

    public void setPersistenceStatus(TaskStorageStatusEnum persistenceStatus) {
        this.persistenceStatus = persistenceStatus;
    }

    public String getPersistenceLocation() {
        return persistenceLocation;
    }

    public void setPersistenceLocation(String persistenceLocation) {
        this.persistenceLocation = persistenceLocation;
    }

    public Instant getPersistenceInstant() {
        return persistenceInstant;
    }

    public void setPersistenceInstant(Instant persistenceInstant) {
        this.persistenceInstant = persistenceInstant;
    }

    public Instant getLocalCacheInstant() {
        return localCacheInstant;
    }

    public void setLocalCacheInstant(Instant localCacheInstant) {
        this.localCacheInstant = localCacheInstant;
    }

    public Instant getCentralCacheInstant() {
        return centralCacheInstant;
    }

    public void setCentralCacheInstant(Instant centralCacheInstant) {
        this.centralCacheInstant = centralCacheInstant;
    }

    public TaskStorageStatusEnum getLocalCacheStatus() {
        return localCacheStatus;
    }

    public void setLocalCacheStatus(TaskStorageStatusEnum localCacheStatus) {
        this.localCacheStatus = localCacheStatus;
    }

    public String getLocalCacheLocation() {
        return localCacheLocation;
    }

    public void setLocalCacheLocation(String localCacheLocation) {
        this.localCacheLocation = localCacheLocation;
    }

    public TaskStorageStatusEnum getCentralCacheStatus() {
        return centralCacheStatus;
    }

    public void setCentralCacheStatus(TaskStorageStatusEnum centralCacheStatus) {
        this.centralCacheStatus = centralCacheStatus;
    }

    public String getCentralCacheLocation() {
        return centralCacheLocation;
    }

    public void setCentralCacheLocation(String centralCacheLocation) {
        this.centralCacheLocation = centralCacheLocation;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskStorageType{");
        sb.append("localCacheStatus=").append(localCacheStatus);
        sb.append(", localCacheLocation=").append(localCacheLocation);
        sb.append(", localCacheInstant=").append(localCacheInstant);
        sb.append(", centralCacheStatus=").append(centralCacheStatus);
        sb.append(", centralCacheLocation=").append(centralCacheLocation);
        sb.append(", centralCacheInstant=").append(centralCacheInstant);
        sb.append(", persistenceStatus=").append(persistenceStatus);
        sb.append(", persistenceInstant=").append(persistenceInstant);
        sb.append(", persistenceLocation=").append(persistenceLocation);
        sb.append('}');
        return sb.toString();
    }
}
