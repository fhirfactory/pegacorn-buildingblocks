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

public enum SoftwareComponentSystemRoleEnum {
    COMPONENT_ROLE_INTERACT_INGRES("InteractIngresRole", "pegacorn.fhir.device.specialisation.component_role.interact_ingres", "Interact Ingres Role (Inbound Content Handling)"),
    COMPONENT_ROLE_INTERACT_EGRESS("InteractEgressRole", "pegacorn.fhir.device.specialisation.component_role.interact_egress", "Interact Egress Role (Output Content Handling)"),
    COMPONENT_ROLE_SUBSYSTEM_EDGE("SubsystemEdgeRole", "pegacorn.fhir.device.specialisation.component_role.subsystem_edge", "Subsystem Edge (Inter-Process Communication Services for Pegacorn subsystems)"),
    COMPONENT_ROLE_SUBSYSTEM_INTERNAL("SubsystemInternal", "pegacorn.fhir.device.specialisation.component_role.subsystem_internal", "Subsystem Internal Functional Block"),
    COMPONENT_ROLE_SUBSYSTEM_TASK_DISTRIBUTION("SubsystemTaskDistribution", "pegacorn.fhir.device.specialisation.component_role.subsystem_task_distribution", "Inter-Process/Intra-Process Task Management and Distribution Service");

    private String displayName;
    private String token;
    private String displayText;

    private SoftwareComponentSystemRoleEnum(String displayName, String token, String displayText){
        this.displayName = displayName;
        this.token = token;
        this.displayText = displayText;
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public String getToken() {
        return (this.token);
    }

    public String getDisplayText(){
        return(this.displayText);
    }
}
