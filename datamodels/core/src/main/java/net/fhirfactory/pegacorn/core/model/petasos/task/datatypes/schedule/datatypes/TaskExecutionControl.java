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
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets.TaskExecutionCommandEnum;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosTaskExecutionStatusEnum;

import java.io.Serializable;
import java.time.Instant;

public class TaskExecutionControl implements Serializable {
    TaskExecutionCommandEnum executionCommand;
    private PetasosTaskExecutionStatusEnum currentExecutionStatus;
    private String taskExecutionStatusReason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant updateInstant;
    TaskWindow executionWindow;

    //
    // Constructor(s)
    //

    public TaskExecutionControl(){
        this.executionCommand = TaskExecutionCommandEnum.TASK_COMMAND_WAIT;
        this.currentExecutionStatus = PetasosTaskExecutionStatusEnum.PETASOS_TASK_ACTIVITY_STATUS_ASSIGNED;
        this.executionWindow = new TaskWindow();
        this.updateInstant = Instant.now();
        this.taskExecutionStatusReason = null;
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasTaskExecutionStatusReason(){
        boolean hasValue = this.taskExecutionStatusReason != null;
        return(hasValue);
    }

    public String getTaskExecutionStatusReason() {
        return taskExecutionStatusReason;
    }

    public void setTaskExecutionStatusReason(String taskExecutionStatusReason) {
        this.taskExecutionStatusReason = taskExecutionStatusReason;
    }

    public PetasosTaskExecutionStatusEnum getCurrentExecutionStatus() {
        return currentExecutionStatus;
    }

    public void setCurrentExecutionStatus(PetasosTaskExecutionStatusEnum currentExecutionStatus) {
        this.currentExecutionStatus = currentExecutionStatus;
    }

    public Instant getUpdateInstant() {
        return updateInstant;
    }

    public void setUpdateInstant(Instant updateInstant) {
        this.updateInstant = updateInstant;
    }

    public TaskExecutionCommandEnum getExecutionCommand() {
        return executionCommand;
    }

    public void setExecutionCommand(TaskExecutionCommandEnum executionCommand) {
        this.executionCommand = executionCommand;
    }

    public TaskWindow getExecutionWindow() {
        return executionWindow;
    }

    public void setExecutionWindow(TaskWindow executionWindow) {
        this.executionWindow = executionWindow;
    }

    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskExecutionControl{");
        sb.append("executionCommand=").append(executionCommand);
        sb.append(", currentExecutionStatus=").append(currentExecutionStatus);
        sb.append(", updateInstant=").append(updateInstant);
        sb.append(", executionWindow=").append(executionWindow);
        sb.append(", taskExeuctionStatusReason=").append(taskExecutionStatusReason);
        sb.append('}');
        return sb.toString();
    }
}
