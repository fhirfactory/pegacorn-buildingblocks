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
package net.fhirfactory.pegacorn.core.model.dataparcel.valuesets;

public enum DataParcelDirectionEnum {
    INFORMATION_FLOW_INBOUND_DATA_PARCEL("Message.Inbound", "petasos.information_flow.message.direction.inbound"),
    INFORMATION_FLOW_OUTBOUND_DATA_PARCEL("Message.Outbound", "petasos.information_flow.message.direction.outbound"),
    INFORMATION_FLOW_API_ACTIVITY_REQUEST("API.Request", "petasos.information_flow.api.direction.request"),
    INFORMATION_FLOW_API_ACTIVITY_RESPONSE("API.Request", "petasos.information_flow.api.direction.request"),
    INFORMATION_FLOW_WORKFLOW_OUTPUT("Workflow.Output", "petasos.information_flow.workflow.direction.output"),
    INFORMATION_FLOW_WORKFLOW_INPUT("Workflow.Input", "petasos.information_flow.workflow.direction.input"),
    INFORMATION_FLOW_WORKFLOW_TRANSIENT("Workflow.Transient", "petasos.information_flow.workflow.direction.transient"),
    INFORMATION_FLOW_CORE_DISTRIBUTION("Task.InterSubsystemDistribution", "petasos.information_flow.task.direction.core_distribution");

    private String token;
    private String displayName;

    private DataParcelDirectionEnum(String displayName, String direction){
        this.token = direction;
        this.displayName = displayName;
    }

    public String getToken() {
        return token;
    }

    public String getDisplayName(){
        return(displayName);
    }

    public static DataParcelDirectionEnum fromTypeValue(String typeValueString){
        for (DataParcelDirectionEnum b : DataParcelDirectionEnum.values()) {
            if (b.getToken().equalsIgnoreCase(typeValueString)) {
                return b;
            }
        }
        return null;
    }
}
