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

import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskExecutionCommandEnum;

import java.io.Serializable;

public class TaskExecutionControl implements Serializable {
    TaskExecutionCommandEnum executionCommand;
    TaskExecutionWindow executionWindow;

    //
    // Constructor(s)
    //

    public TaskExecutionControl(){
        this.executionCommand = TaskExecutionCommandEnum.TASK_COMMAND_WAIT;
        this.executionWindow = new TaskExecutionWindow();
    }

    //
    // Getters and Setters
    //

    public TaskExecutionCommandEnum getExecutionCommand() {
        return executionCommand;
    }

    public void setExecutionCommand(TaskExecutionCommandEnum executionCommand) {
        this.executionCommand = executionCommand;
    }

    public TaskExecutionWindow getExecutionWindow() {
        return executionWindow;
    }

    public void setExecutionWindow(TaskExecutionWindow executionWindow) {
        this.executionWindow = executionWindow;
    }

    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskExecutionSchedule{");
        sb.append("executionCommand=").append(executionCommand);
        sb.append(", executionWindow=").append(executionWindow);
        sb.append('}');
        return sb.toString();
    }
}
