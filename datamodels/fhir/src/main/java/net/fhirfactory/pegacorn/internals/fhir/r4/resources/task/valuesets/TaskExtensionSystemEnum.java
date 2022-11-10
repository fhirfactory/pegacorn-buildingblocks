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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.task.valuesets;

import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;

public enum TaskExtensionSystemEnum {
    SYSTEM_WIDE_FOCUS_EXTENSION_URL("SystemWideFocus", "/statusReason/system_wide_focus"),
    CLUSTER_WIDE_FOCUS_EXTENSION_URL("ClusterWideFocus","/statusReason/cluster_wide_focus"),
    TASK_RETRY_REQUIRED_EXTENSION_URL("TaskRetryRequired", "/statusReason/retry_required"),
    TASK_RETRY_COUNT_EXTENSION_URL("TaskRetryCount", "/statusReason/task_retry_count"),
    TASK_REGISTRATON_INSTANT_EXTENSION_URL("TaskRegistrationInstant", "/executionPeriod/registration_instant"),
    TASK_FINALISATION_INSTANT_EXTENSION_URL("TaskFinalisationInstant", "/executionPeriod/finalisation_instant"),
    TASK_OUTCOME_ENTRY_INSTANT_EXTENSION_URL("TaskOutcomeEntryInstant", "/businessStatus/outcome_instant"),
    TASK_SEQUENCE_NUMBER("TaskSequenceNumber", "/identifier/sequence_number");

    private String token;
    private String display;

    private TaskExtensionSystemEnum(String display, String token){
        this.display = display;
        this.token = token;
    }

    //
    // Getters and Setters
    //

    public String getToken() {
        return token;
    }

    public String getDisplay() {
        return display;
    }

    public static TaskExtensionSystemEnum fromToken(String token){
        if(StringUtils.isNotEmpty(token)){
            for(TaskExtensionSystemEnum currentValue: values()){
                if(token.equalsIgnoreCase(currentValue.getToken())){
                    return(currentValue);
                }
            }
        }
        return(null);
    }
}

