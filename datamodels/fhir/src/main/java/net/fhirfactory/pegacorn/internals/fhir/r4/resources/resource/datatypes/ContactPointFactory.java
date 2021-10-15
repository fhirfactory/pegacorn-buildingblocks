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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.datatypes;

import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class ContactPointFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ContactPointFactory.class);

    public ContactPoint buildContactPoint(String value, ContactPoint.ContactPointUse use, ContactPoint.ContactPointSystem system, int rank){
        LOG.debug(".buildContactPoint(): Entry, value --> {}, use --> {}, system --> {}, rank --> {}", value, use, system, rank);
        ContactPoint contact = new ContactPoint();
        contact.setSystem(system);
        contact.setValue(value);
        contact.setUse(use);
        contact.setRank(rank);
        Period contactPeriod = new Period();
        contactPeriod.setStart(Date.from(Instant.now()));
        contact.setPeriod(contactPeriod);
        LOG.debug(".buildContactPoint(): Exit, ContactPoint --> {}", contact);
        return(contact);
    }
}
