/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.topology.nodes;

public enum DefaultWorkshopSetEnum {
    DATA_GRID_WORKSHOP("DataGrid"),
    INTER_SUBSYSTEM_INTEGRATION_WORKSHOP("ISSI"),
    BEHAVIOUR_WORKSHOP("Behaviour"),
    INTERACT_WORKSHOP("ExternalIPC"),
    TRANSFORM_WORKSHOP("Transform"),
    WORKFLOW_WORKSHOP("Workflow"),
    POLICY_ENFORCEMENT_WORKSHOP("PolicyEnforcement"),
    AUDIT_SERVICES_WORKSHOP("Audit"),
    EDGE_WORKSHOP("InternalIPC");

    private String workshop;

    private DefaultWorkshopSetEnum(String newWorkshop){
        this.workshop = newWorkshop;
    }

    public String getWorkshop(){
        return(this.workshop);
    }
}
