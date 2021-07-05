/*
 * Copyright (c) 2021 Mark Hunter
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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier;

import net.fhirfactory.pegacorn.deployment.properties.codebased.DeploymentSystemIdentificationInterface;
import net.fhirfactory.pegacorn.deployment.properties.codebased.DeploymentSystemSiteIdentificationInterface;
import net.fhirfactory.pegacorn.internals.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeSystemFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.systems.DeploymentInstanceDetailInterface;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class PegacornIdentifierFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PegacornIdentifierFactory.class);

    @Inject
    private PegacornReferenceProperties pegacornReferenceProperties;

    @Inject
    private DeploymentInstanceDetailInterface deploymentInstanceDetailInterface;

    @Inject
    private PegacornIdentifierCodeSystemFactory pegacornIdentifierCodeSystemFactory;

    @Inject
    private DeploymentSystemIdentificationInterface systemIdentificationInterface;

    @Inject
    private DeploymentSystemSiteIdentificationInterface siteIdentification;

    private String getLocalIdentifierSystem(){
        String systemPath = siteIdentification.getDeploymentSite()
                + pegacornReferenceProperties.getLocalCodeSystemPath()
                + "/identifiers";
        return(systemPath);
    }

    public Identifier constructIdentifierFromSystemRecordIDValue(String systemName, String ridValue){
        LOG.debug(".constructIdentifierForRecordIDValue(): Entry");
        Identifier builtIdentifier = new Identifier();
        builtIdentifier.setUse(Identifier.IdentifierUse.SECONDARY);
        CodeableConcept idType = new CodeableConcept();
        Coding idTypeCoding = new Coding();
        idTypeCoding.setCode("RI");
        idTypeCoding.setSystem("http://terminology.hl7.org/ValueSet/v2-0203");
        idType.getCoding().add(idTypeCoding);
        idType.setText("Generalized Resource Identifier");
        builtIdentifier.setType(idType);
        builtIdentifier.setSystem(systemName);
        builtIdentifier.setValue(ridValue);
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        builtIdentifier.setPeriod(validPeriod);
        builtIdentifier.setAssigner(deploymentInstanceDetailInterface.getDeploymentInstanceOrganization());
        LOG.debug(".constructEndpointIdentifier(): Exit, created Identifier --> {}", builtIdentifier);
        return builtIdentifier;
    }

    public Identifier newIdentifier(PegacornIdentifierCodeEnum codeEnum, String value, Period period){
        LOG.debug(".newIdentifier(): Entry, codeEnum->{}, vale->{}, period->{}", codeEnum, value, period);
        Identifier identifier = new Identifier();
        CodeableConcept identifierType = pegacornIdentifierCodeSystemFactory.buildIdentifierType(codeEnum);
        identifier.setType(identifierType);
        identifier.setSystem(getLocalIdentifierSystem());
        identifier.setValue(value);
        identifier.setPeriod(period);
        identifier.setAssigner(deploymentInstanceDetailInterface.getDeploymentInstanceOrganization());
        LOG.debug(".newIdentifier(): Exit, identifier->{}", identifier);
        return(identifier);
    }

}
