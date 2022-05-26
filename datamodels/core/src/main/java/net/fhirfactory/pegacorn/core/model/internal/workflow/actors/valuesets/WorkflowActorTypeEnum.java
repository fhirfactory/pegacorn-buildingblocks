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
package net.fhirfactory.pegacorn.core.model.internal.workflow.actors.valuesets;

public enum WorkflowActorTypeEnum {
    PRACTITIONER_ACTOR_TYPE("PractitionerAsActor","dricats.workflow.actor-type.practitioner"),
    PRACTITIONER_ROLE_ACTOR_TYPE("PractitionerRoleAsActor", "dricats.workflow.actor-type.practitioner-role"),
    ORGANIZATION_ACTOR_TYPE("OrganizationAsActor", "dricats.workflow.actor-type.organization"),
    SOFTWARE_COMPONENT_ACTOR_TYPE("SoftwareComponentAsActor", "dricats.workflow.actor-type.software-component"),
    UNKNOWN_ACTOR_TYPE("UnknownActor", "dricats.workflow.actor-type.unknown")
    ;

    private String displayName;
    private String token;

    private WorkflowActorTypeEnum(String name, String token){
        this.displayName = name;
        this.token = token;
    }

    public String getToken(){
        return(this.token);
    }

    public String getDisplayName(){
        return(this.displayName);
    }
}
