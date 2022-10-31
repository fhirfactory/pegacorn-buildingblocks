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
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskWindowBoundaryConditionEnum;

import java.io.Serializable;
import java.time.Instant;

public class TaskWindowBoundary implements Serializable {
    TaskWindowBoundaryConditionEnum windowBoundaryCondition;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant windowsBoundaryInstant;

    //
    // Constructor(s)
    //

    public TaskWindowBoundary() {
        this.windowBoundaryCondition = TaskWindowBoundaryConditionEnum.TASK_WINDOW_BOUNDARY_NONE;
        this.windowsBoundaryInstant = Instant.EPOCH;
    }

    public TaskWindowBoundary(TaskWindowBoundaryConditionEnum condition, Instant boundaryInstant) {
        this.windowBoundaryCondition = condition;
        this.windowsBoundaryInstant = boundaryInstant;
    }

    //
    // Getters and Setters
    //

    public TaskWindowBoundaryConditionEnum getWindowBoundaryCondition() {
        return windowBoundaryCondition;
    }

    public void setWindowBoundaryCondition(TaskWindowBoundaryConditionEnum windowBoundaryCondition) {
        this.windowBoundaryCondition = windowBoundaryCondition;
    }

    public Instant getWindowsBoundaryInstant() {
        return windowsBoundaryInstant;
    }

    public void setWindowsBoundaryInstant(Instant windowsBoundaryInstant) {
        this.windowsBoundaryInstant = windowsBoundaryInstant;
    }

    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskWindowBoundary{");
        sb.append("windowBoundaryCondition=").append(windowBoundaryCondition);
        sb.append(", windowsBoundaryInstant=").append(windowsBoundaryInstant);
        sb.append('}');
        return sb.toString();
    }

}
