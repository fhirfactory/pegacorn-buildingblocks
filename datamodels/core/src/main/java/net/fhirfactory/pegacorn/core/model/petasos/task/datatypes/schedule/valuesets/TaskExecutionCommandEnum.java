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
public enum TaskExecutionCommandEnum {
    TASK_COMMAND_WAIT("Wait", "petasos.task.execution_command.wait"),
    TASK_COMMAND_CANCEL( "Cancel", "pegasos.execution_command.cancel"),
    TASK_COMMAND_FAIL("Fail" , "pegasos.task.execution_command.fail"),
    TASK_COMMAND_FINISH("Finish", "pegacorn.pegasos.task.execution_command.finish"),
    TASK_COMMAND_FINALISE("Finalise", "pegacorn.pegasos.task.execution_command.finalise"),
    TASK_COMMAND_EXECUTE("Execute", "pegacorn.petasos.execution_command.execute"),
    TASK_COMMAND_CLEAN_UP("CleanUp", "pegacorn.petasos.execution_command.clean_up");

    private String name;
    private String token;

    private TaskExecutionCommandEnum(String name, String token ){
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
