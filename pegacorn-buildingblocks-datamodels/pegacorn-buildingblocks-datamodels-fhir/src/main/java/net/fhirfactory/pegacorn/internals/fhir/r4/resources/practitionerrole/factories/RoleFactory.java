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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.practitionerrole.factories;

import net.fhirfactory.pegacorn.internals.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.deployment.properties.codebased.ContainmentBasedValueSeparators;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RoleFactory {
    private static final Logger LOG = LoggerFactory.getLogger(RoleFactory.class);

    private static final String PEGACORN_ROLE_CATEGORY_CODE_SYSTEM = "/role-category";
    private static final String PEGACORN_ROLE_CODE_SYSTEM = "/roles";

    @Inject
    private PegacornReferenceProperties systemWideProperties;

    @Inject
    private ContainmentBasedValueSeparators containmentBasedValueSeparators;

    public String getPegacornRoleCategoryCodeSystem() {
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_ROLE_CATEGORY_CODE_SYSTEM;
        return (codeSystem);
    }

    public String getPegacornRoleCodeSystem() {
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_ROLE_CODE_SYSTEM;
        return (codeSystem);
    }

    public CodeableConcept buildRoleWithCategory(String categoryName, String roleName){
        CodeableConcept roleCategoryAndCode = new CodeableConcept();
        Coding codeCategoryType = new Coding();
        codeCategoryType.setCode(categoryName);
        codeCategoryType.setSystem(getPegacornRoleCategoryCodeSystem());
        codeCategoryType.setDisplayElement(new StringType(categoryName));
        roleCategoryAndCode.getCoding().add(codeCategoryType);
        Coding codeRole = buildRoleCode(roleName);
        roleCategoryAndCode.getCoding().add(codeRole);
        String text = containmentBasedValueSeparators.wrapEntry(categoryName)
                + containmentBasedValueSeparators.getEntrySeparator()
                + containmentBasedValueSeparators.wrapEntry(roleName);
        roleCategoryAndCode.setText("Role Name:" + text);
        return(roleCategoryAndCode);
    }

    public CodeableConcept buildRole(String roleName){
        CodeableConcept role = new CodeableConcept();
        Coding idTypeCoding = buildRoleCode(roleName);
        role.getCoding().add(idTypeCoding);
        role.setText("Role Name:" + containmentBasedValueSeparators.wrapEntry(roleName));
        return(role);
    }

    private Coding buildRoleCode(String roleName){
        Coding idTypeCoding = new Coding();
        idTypeCoding.setCode(roleName);
        idTypeCoding.setSystem(getPegacornRoleCodeSystem());
        idTypeCoding.setDisplayElement(new StringType(roleName));
        return(idTypeCoding);
    }

}
