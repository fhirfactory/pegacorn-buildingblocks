/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.component.valuesets;

public enum SoftwareComponentExecutionControlEnum {
    SOFTWARE_COMPONENT_ALLOW_EXECUTION("ExecutionAllowed", "pegacorn.fhir.device.execution_status.allowed", "Software Component is Allowed to Execute Tasks"),
    SOFTWARE_COMPONENT_PAUSE_EXECUTION("ExecutionPaused", "pegacorn.fhir.device.execution_status.paused", "Software Component is Paused");

    private String token;
    private String displayName;
    private String displayText;

    private SoftwareComponentExecutionControlEnum(String name, String token, String displayText){
        this.token = token;
        this.displayName = name;
        this.displayText = displayText;
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public String getToken(){
        return(this.token);
    }

    public String getDisplayText(){
        return(this.displayText);
    }
}
