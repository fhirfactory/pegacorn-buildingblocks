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

import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskWindowBoundaryConditionEnum;

import java.io.Serializable;
import java.time.Instant;

public class TaskWindow implements Serializable {
    private TaskWindowBoundary upperBound;
    private TaskWindowBoundary lowerBound;

    //
    // Constructor(s)
    //

    public TaskWindow(){
        this.upperBound = new TaskWindowBoundary(TaskWindowBoundaryConditionEnum.TASK_WINDOW_BOUNDARY_NONE, Instant.now());
        this.lowerBound = new TaskWindowBoundary(TaskWindowBoundaryConditionEnum.TASK_WINDOW_BOUNDARY_NONE, Instant.now());
    }

    //
    // Getters and Setters
    //

    public TaskWindowBoundary getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(TaskWindowBoundary upperBound) {
        this.upperBound = upperBound;
    }

    public TaskWindowBoundary getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(TaskWindowBoundary lowerBound) {
        this.lowerBound = lowerBound;
    }


    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskWindow{");
        sb.append("upperBound=").append(upperBound);
        sb.append(", lowerBound=").append(lowerBound);
        sb.append('}');
        return sb.toString();
    }
}
