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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.reason.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.identity.datatypes.TaskIdType;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class RetryTaskReasonType implements Serializable {
    private TaskIdType originalTaskId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant originalTaskExecutionInstant;
    private TaskIdType previousRetryTaskId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant previousTaskExecutionInstant;

    //
    // Constructor(s)
    //

    public RetryTaskReasonType(){
        this.originalTaskId = null;
        this.originalTaskExecutionInstant = null;
        this.previousRetryTaskId = null;
        this.previousTaskExecutionInstant = null;
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasOriginalTaskId(){
        boolean hasValue = this.originalTaskId != null;
        return(hasValue);
    }

    public TaskIdType getOriginalTaskId() {
        return originalTaskId;
    }

    public void setOriginalTaskId(TaskIdType originalTaskId) {
        this.originalTaskId = originalTaskId;
    }

    @JsonIgnore
    public boolean hasOriginalTaskExecutionInstant(){
        boolean hasValue = this.originalTaskExecutionInstant != null;
        return(hasValue);
    }

    public Instant getOriginalTaskExecutionInstant() {
        return originalTaskExecutionInstant;
    }

    public void setOriginalTaskExecutionInstant(Instant originalTaskExecutionInstant) {
        this.originalTaskExecutionInstant = originalTaskExecutionInstant;
    }

    @JsonIgnore
    public boolean hasPreviousRetryTaskId(){
        boolean hasValue = this.previousRetryTaskId != null;
        return(hasValue);
    }

    public TaskIdType getPreviousRetryTaskId() {
        return previousRetryTaskId;
    }

    public void setPreviousRetryTaskId(TaskIdType previousRetryTaskId) {
        this.previousRetryTaskId = previousRetryTaskId;
    }

    @JsonIgnore
    public boolean hasPreviousTaskExecutionInstant(){
        boolean hasValue = this.previousTaskExecutionInstant != null;
        return(hasValue);
    }

    public Instant getPreviousTaskExecutionInstant() {
        return previousTaskExecutionInstant;
    }

    public void setPreviousTaskExecutionInstant(Instant previousTaskExecutionInstant) {
        this.previousTaskExecutionInstant = previousTaskExecutionInstant;
    }

    //
    // Getters and Setters
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RetryTaskReasonType{");
        sb.append("originalTaskId=").append(originalTaskId);
        sb.append(", originalTaskExecutionInstant=").append(originalTaskExecutionInstant);
        sb.append(", previousRetryTaskId=").append(previousRetryTaskId);
        sb.append(", previousTaskExecutionInstant=").append(previousTaskExecutionInstant);
        sb.append('}');
        return sb.toString();
    }

    //
    // hash and equals
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RetryTaskReasonType)) return false;
        RetryTaskReasonType that = (RetryTaskReasonType) o;
        return Objects.equals(getOriginalTaskId(), that.getOriginalTaskId()) && Objects.equals(getPreviousRetryTaskId(), that.getPreviousRetryTaskId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOriginalTaskId(), getPreviousRetryTaskId());
    }
}
