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
package net.fhirfactory.pegacorn.internals.directories.transformers.common;

import net.fhirfactory.pegacorn.internals.directories.entries.PractitionerDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.ContactPointDE;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.datatypes.ContactPointFactory;
import org.hl7.fhir.r4.model.ContactPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DataTypeTransformers {
    private static final Logger LOG = LoggerFactory.getLogger(DataTypeTransformers.class);

    @Inject
    private ContactPointFactory contactPointFactory;

    /**
     *
     * @param practitionerDirectoryEntry
     * @return
     */
    public List<ContactPoint> createContactPoints(PractitionerDirectoryEntry practitionerDirectoryEntry) {
        List<ContactPoint> contactPoints = createContactPoints(practitionerDirectoryEntry.getContactPoints());
        return(contactPoints);
    }

    /**
     *
     * @param communicationChannels
     * @return
     */
    public List<ContactPoint> createContactPoints(List<ContactPointDE> communicationChannels){
        ArrayList<ContactPoint> contactPointList = new ArrayList<ContactPoint>();
        int rank = 1;

        for(ContactPointDE currentEntry: communicationChannels) {
            switch(currentEntry.getType()) {
                case LINGO: {
                    ContactPoint emailContactPoint = contactPointFactory.buildContactPoint(
                            currentEntry.getValue(), ContactPoint.ContactPointUse.WORK, ContactPoint.ContactPointSystem.OTHER, rank);
                    contactPointList.add(emailContactPoint);
                    rank += 1;
                    break;
                }
                case LANDLINE:
                case PABX_EXTENSION: {
                    ContactPoint landlineContactPoint = contactPointFactory.buildContactPoint(
                            currentEntry.getValue(), ContactPoint.ContactPointUse.WORK, ContactPoint.ContactPointSystem.PHONE, rank);
                    rank += 1;
                    contactPointList.add(landlineContactPoint);
                    break;
                }
                case MOBILE: {
                    ContactPoint mobilePhone = contactPointFactory.buildContactPoint(
                            currentEntry.getValue(), ContactPoint.ContactPointUse.WORK, ContactPoint.ContactPointSystem.PHONE, rank);
                    rank += 1;
                    contactPointList.add(mobilePhone);
                    ContactPoint smsPhone = contactPointFactory.buildContactPoint(
                            currentEntry.getValue(), ContactPoint.ContactPointUse.WORK, ContactPoint.ContactPointSystem.SMS, rank);
                    rank += 1;
                    contactPointList.add(smsPhone);
                    break;
                }
                case EMAIL: {
                    ContactPoint emailContactPoint = contactPointFactory.buildContactPoint(
                            currentEntry.getValue(), ContactPoint.ContactPointUse.WORK, ContactPoint.ContactPointSystem.EMAIL, rank);
                    contactPointList.add(emailContactPoint);
                    rank += 1;
                    break;
                }
                case PAGER: {
                    ContactPoint emailContactPoint = contactPointFactory.buildContactPoint(
                            currentEntry.getValue(), ContactPoint.ContactPointUse.WORK, ContactPoint.ContactPointSystem.PAGER, rank);
                    contactPointList.add(emailContactPoint);
                    rank += 1;
                    break;
                }
                default:
            }
        }
        return(contactPointList);
    }
}
