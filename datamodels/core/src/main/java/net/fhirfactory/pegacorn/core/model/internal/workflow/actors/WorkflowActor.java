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
package net.fhirfactory.pegacorn.core.model.internal.workflow.actors;

import net.fhirfactory.pegacorn.core.model.internal.resources.simple.OrganizationESR;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.PractitionerESR;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.PractitionerRoleESR;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.SoftwareComponentESR;
import net.fhirfactory.pegacorn.core.model.internal.workflow.actors.valuesets.WorkflowActorTypeEnum;

import java.io.Serializable;

public class WorkflowActor implements Serializable {
    private PractitionerESR practitionerAsActor;
    private OrganizationESR organizationAsActor;
    private PractitionerRoleESR practitionerRoleAsActor;
    private SoftwareComponentESR softwareComponentAsActor;
    private WorkflowActorTypeEnum actorType;

    //
    // Constructor(s)
    //

    public WorkflowActor(){
        this.practitionerAsActor = null;
        this.organizationAsActor = null;
        this.practitionerRoleAsActor = null;
        this.softwareComponentAsActor = null;
        this.actorType = WorkflowActorTypeEnum.UNKNOWN_ACTOR_TYPE;
    }

    //
    // Getters and Setters
    //

    public PractitionerESR getPractitionerAsActor() {
        return practitionerAsActor;
    }

    public void setPractitionerAsActor(PractitionerESR practitionerAsActor) {
        this.practitionerAsActor = practitionerAsActor;
    }

    public OrganizationESR getOrganizationAsActor() {
        return organizationAsActor;
    }

    public void setOrganizationAsActor(OrganizationESR organizationAsActor) {
        this.organizationAsActor = organizationAsActor;
    }

    public PractitionerRoleESR getPractitionerRoleAsActor() {
        return practitionerRoleAsActor;
    }

    public void setPractitionerRoleAsActor(PractitionerRoleESR practitionerRoleAsActor) {
        this.practitionerRoleAsActor = practitionerRoleAsActor;
    }

    public SoftwareComponentESR getSoftwareComponentAsActor() {
        return softwareComponentAsActor;
    }

    public void setSoftwareComponentAsActor(SoftwareComponentESR softwareComponentAsActor) {
        this.softwareComponentAsActor = softwareComponentAsActor;
    }

    public WorkflowActorTypeEnum getActorType() {
        return actorType;
    }

    public void setActorType(WorkflowActorTypeEnum actorType) {
        this.actorType = actorType;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return "WorkflowActor{" +
                "practitionerAsActor=" + practitionerAsActor +
                ", organizationAsActor=" + organizationAsActor +
                ", practitionerRoleAsActor=" + practitionerRoleAsActor +
                ", softwareComponentAsActor=" + softwareComponentAsActor +
                ", actorType=" + actorType +
                '}';
    }
}
