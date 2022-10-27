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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.provenance.factories;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.codesystems.ProvenanceAgentRole;
import org.hl7.fhir.r4.model.codesystems.ProvenanceAgentType;
import org.hl7.fhir.r4.model.codesystems.W3cProvenanceActivityType;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProvenanceComponentFactory {

    public CodeableConcept newAgentRole(ProvenanceAgentRole role){
        CodeableConcept agentRoleCC = new CodeableConcept();
        Coding agentRoleCoding = new Coding();
        agentRoleCoding.setCode(role.toCode());
        agentRoleCoding.setDisplay(role.getDisplay());
        agentRoleCoding.setSystem(role.getSystem());
        agentRoleCC.addCoding(agentRoleCoding);
        agentRoleCC.setText(role.getDefinition());
        return(agentRoleCC);
    }

    public CodeableConcept newAgentType(ProvenanceAgentType agentType){
        CodeableConcept agentRoleCC = new CodeableConcept();
        Coding agentRoleCoding = new Coding();
        agentRoleCoding.setCode(agentType.toCode());
        agentRoleCoding.setDisplay(agentType.getDisplay());
        agentRoleCoding.setSystem(agentType.getSystem());
        agentRoleCC.addCoding(agentRoleCoding);
        agentRoleCC.setText(agentType.getDefinition());
        return(agentRoleCC);
    }

    public CodeableConcept newProvenanceActivity(W3cProvenanceActivityType activityType){
        CodeableConcept activityCC = new CodeableConcept();
        Coding activityCoding = new Coding();
        activityCoding.setCode(activityType.toCode());
        activityCoding.setDisplay(activityType.getDisplay());
        activityCoding.setSystem(activityType.getSystem());
        activityCC.addCoding(activityCoding);
        activityCC.setText(activityType.getDefinition());
        return(activityCC);
    }
}
