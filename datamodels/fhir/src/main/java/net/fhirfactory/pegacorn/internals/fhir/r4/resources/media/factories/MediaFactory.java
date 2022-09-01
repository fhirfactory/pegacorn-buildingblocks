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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.media.factories;

import java.util.Date;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Media;
import org.hl7.fhir.r4.model.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierFactory;

@ApplicationScoped
public class MediaFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MediaFactory.class);

    @Inject
    private PegacornIdentifierFactory identifierFactory;

	
    public Media newMediaResource(String idValue, Date date) {
        LOG.debug(".newMediaResource(): Entry, idValue->{}, date->{}", idValue, date);
        Media mediaResource = new Media();
        Period newPeriod = new Period();
        newPeriod.setStart(date);
        Identifier baseIdentifier = identifierFactory.newIdentifier(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_HL7V2_COMMUNICATION_CONTAINER,idValue,newPeriod);
        mediaResource.addIdentifier(baseIdentifier);
        UUID uuid = UUID.randomUUID();
        String id = Long.toHexString(uuid.getMostSignificantBits()) + Long.toHexString(uuid.getLeastSignificantBits());
        mediaResource.setId(id);
        LOG.debug(".newMediaResource(): Exit, mediaResource->{}", mediaResource);
        return(mediaResource);
    }
    
    @VisibleForTesting
    public void setIdentifierFactory(PegacornIdentifierFactory identifierFactory) {
    	this.identifierFactory = identifierFactory;
    }
}
