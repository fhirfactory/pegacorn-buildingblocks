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
    private TaskStorageStatusEnum localStorageStatus;
    private String localStorageLocation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant localStorageInstant;
    private TaskStorageStatusEnum centralStorageStatus;
    private String centralStorageLocation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant centralStorageInstant;

    //
    // Constructor(s)
    //

    public TaskStorageType(){
        this.localStorageLocation = null;
        this.localStorageStatus = TaskStorageStatusEnum.TASK_UNSAVED;
        this.centralStorageLocation = null;
        this.centralStorageStatus = TaskStorageStatusEnum.TASK_UNSAVED;
        this.centralStorageInstant = null;
        this.localStorageInstant = null;
    }

    //
    // Getters and Setters
    //


    public Instant getLocalStorageInstant() {
        return localStorageInstant;
    }

    public void setLocalStorageInstant(Instant localStorageInstant) {
        this.localStorageInstant = localStorageInstant;
    }

    public Instant getCentralStorageInstant() {
        return centralStorageInstant;
    }

    public void setCentralStorageInstant(Instant centralStorageInstant) {
        this.centralStorageInstant = centralStorageInstant;
    }

    public TaskStorageStatusEnum getLocalStorageStatus() {
        return localStorageStatus;
    }

    public void setLocalStorageStatus(TaskStorageStatusEnum localStorageStatus) {
        this.localStorageStatus = localStorageStatus;
    }

    public String getLocalStorageLocation() {
        return localStorageLocation;
    }

    public void setLocalStorageLocation(String localStorageLocation) {
        this.localStorageLocation = localStorageLocation;
    }

    public TaskStorageStatusEnum getCentralStorageStatus() {
        return centralStorageStatus;
    }

    public void setCentralStorageStatus(TaskStorageStatusEnum centralStorageStatus) {
        this.centralStorageStatus = centralStorageStatus;
    }

    public String getCentralStorageLocation() {
        return centralStorageLocation;
    }

    public void setCentralStorageLocation(String centralStorageLocation) {
        this.centralStorageLocation = centralStorageLocation;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskStorageType{");
        sb.append("localStorageStatus=").append(localStorageStatus);
        sb.append(", localStorageLocation=").append(localStorageLocation);
        sb.append(", localStorageInstant=").append(localStorageInstant);
        sb.append(", centralStorageStatus=").append(centralStorageStatus);
        sb.append(", centralStorageLocation=").append(centralStorageLocation);
        sb.append(", centralStorageLocation=").append(centralStorageInstant);
        sb.append('}');
        return sb.toString();
    }
}
