/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets;

/**
 *
 * @author ACT Health (Mark A. Hunter)
 */
public enum TaskOutcomeStatusEnum {
    OUTCOME_STATUS_UNKNOWN("Outcome Unknown","petasos.task.outcome_status.unknown"),
    OUTCOME_STATUS_WAITING("Waiting","petasos.task.outcome_status.waiting"),
    OUTCOME_STATUS_CANCELLED("Cancelled", "petasos.task.outcome_status.cancelled"),
    OUTCOME_STATUS_ACTIVE("Active", "petasos.task.outcome_status.active"),
    OUTCOME_STATUS_FINISHED("Finished", "petasos.task.outcome_status.finished"),
    OUTCOME_STATUS_FINALISED("Finalised", "petasos.task.outcome_status.finalised"),
    OUTCOME_STATUS_FAILED("Failed", "petasos.task.outcome_status.failed");

    private String token;
    private String displayName;
    
    private TaskOutcomeStatusEnum(String name, String executionStatus){
        this.token = executionStatus;
        this.displayName = name;
    }
    
    public String getToken(){
        return(this.token);
    }

    public String getDisplayName(){
        return(this.displayName);
    }
}
