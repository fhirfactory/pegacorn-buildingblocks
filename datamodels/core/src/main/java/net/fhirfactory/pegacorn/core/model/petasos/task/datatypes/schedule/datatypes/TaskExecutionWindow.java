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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.datatypes;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskExecutionWindowTypeEnum;

import java.io.Serializable;
import java.time.Instant;

public class TaskExecutionWindow implements Serializable {
    TaskExecutionWindowTypeEnum executionWindowType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant lowerTimingBound;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant upperTimingBound;

    //
    // Constructor(s)
    //

    public TaskExecutionWindow(){
        this.executionWindowType = TaskExecutionWindowTypeEnum.TASK_EXECUTION_WINDOW_ASAP;
        this.lowerTimingBound = Instant.now();
        this.upperTimingBound = Instant.now();
    }

    //
    // Getters and Setters
    //

    public TaskExecutionWindowTypeEnum getExecutionWindowType() {
        return executionWindowType;
    }

    public void setExecutionWindowType(TaskExecutionWindowTypeEnum executionWindowType) {
        this.executionWindowType = executionWindowType;
    }

    public Instant getLowerTimingBound() {
        return lowerTimingBound;
    }

    public void setLowerTimingBound(Instant lowerTimingBound) {
        this.lowerTimingBound = lowerTimingBound;
    }

    public Instant getUpperTimingBound() {
        return upperTimingBound;
    }

    public void setUpperTimingBound(Instant upperTimingBound) {
        this.upperTimingBound = upperTimingBound;
    }

    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskExecutionWindow{");
        sb.append("executionWindowType=").append(executionWindowType);
        sb.append(", lowerTimingBound=").append(lowerTimingBound);
        sb.append(", upperTimingBound=").append(upperTimingBound);
        sb.append('}');
        return sb.toString();
    }
}
