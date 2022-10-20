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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.valuesets;

/**
 *
 * @author ACT Health (Mark A. Hunter)
 */
public enum TaskExecutionWindowTypeEnum {
    TASK_EXECUTION_WINDOW_ASAP("ASAP", "petasos.task.execution_window.asap"),
    TASK_EXECUTION_WINDOW_NOT_BEFORE( "NotBeforeSpecifiedTime", "pegasos.execution_window.not_before_specified_time"),
    TASK_EXECUTION_WINDOW_BETWEEN("BetweenSpecifiedTimes" , "pegasos.task.execution_window.between_specified_times"),
    TASK_EXECUTION_WINDOW_NOT_AFTER("NotAfterSpecifiedTime" , "pegasos.task.execution_window.not_after_specified_time");

    private String name;
    private String token;

    private TaskExecutionWindowTypeEnum(String name, String token ){
        this.name = name;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }
}
