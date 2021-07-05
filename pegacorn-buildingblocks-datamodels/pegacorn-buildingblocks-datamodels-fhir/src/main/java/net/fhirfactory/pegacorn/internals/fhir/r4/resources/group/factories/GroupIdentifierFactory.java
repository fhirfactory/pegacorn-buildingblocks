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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.factories;

import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeSystemFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.systems.DeploymentInstanceDetailInterface;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class GroupIdentifierFactory {
    private static final Logger LOG = LoggerFactory.getLogger(GroupIdentifierFactory.class);

    @Inject
    private DeploymentInstanceDetailInterface deploymentInstanceDetailInterface;

    @Inject
    private PegacornIdentifierCodeSystemFactory pegacornIdentifierCodeSystemFactory;

    public Identifier newCommunicateRoomBasedGroupIdentifier(String room_id, Period period){
        LOG.debug(".buildIdentifierFromShortName(): Entry");
        Identifier identifier = new Identifier();
        identifier.setUse(Identifier.IdentifierUse.SECONDARY);
        CodeableConcept idType = pegacornIdentifierCodeSystemFactory.buildIdentifierType(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_COMMUNICATE_ROOM_ID);
        identifier.setType(idType);
        identifier.setSystem(deploymentInstanceDetailInterface.getDeploymentInstanceSystemEndpointSystem());
        identifier.setValue(room_id);
        if(period == null) {
            period = new Period();
            period.setStart(Date.from(Instant.now()));
        }
        identifier.setPeriod(period);
        identifier.setAssigner(deploymentInstanceDetailInterface.getDeploymentInstanceSystemOwnerOrganization());
        LOG.debug(".buildIdentifierFromShortName(): Exit, created Identifier --> {}", identifier);
        return(identifier);
    }

    public Reference newGroupReference(Identifier identifier){
        LOG.debug(".newGroupReference(): Entry, identifier->{}", identifier);
        Reference reference = new Reference();
        reference.setType(ResourceType.Group.name());
        reference.setIdentifier(identifier);
        reference.setDisplay(identifier.getValue());
        LOG.debug(".newGroupReference(): Exit, reference->{}", reference);
        return(reference);
    }
}
