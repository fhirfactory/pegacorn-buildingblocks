/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.bundle;

import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.factories.EndpointIdentifierHelper;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeSystemFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornMessageHeaderCodeSystemFactory;
import net.fhirfactory.pegacorn.internals.PegacornReferenceProperties;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.MessageHeader;
import org.hl7.fhir.r4.model.UrlType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MessageHeaderHelper {

    @Inject
    private EndpointIdentifierHelper endpointIdentifierHelper;

    @Inject
    private PegacornIdentifierCodeSystemFactory pegacornIdentifierCodeSystemFactory;

    @Inject
    private PegacornReferenceProperties systemWideProperties;

    @Inject
    private PegacornMessageHeaderCodeSystemFactory pegacornMessageHeaderCodeSystemFactory;

    /**
     *
     * This function generates a (default) FHIR::MessageHeader (default for use
     * with our FHIR::Communication elements).
     *
     * @return MessageHeader - A FHIR::MessageHeader element suitable for
     * inclusion in a FHIR::Bundle for encapsulating and transporting a
     * transformed Matrix::m.room.message (in a FHIR::Communication element).
     *
     */
    public MessageHeader buildMessageHeader(UrlType sourceComponent, String sourceComponentName, UrlType targetComponent, String targetComponentName)
    {
        MessageHeader messageHeaderElement = new MessageHeader();
        Coding messageHeaderCode = pegacornMessageHeaderCodeSystemFactory.buildMessageTypeCoding("standard-ipc");
        messageHeaderElement.setEvent(messageHeaderCode);
        MessageHeader.MessageSourceComponent messageSource = new MessageHeader.MessageSourceComponent();
        messageSource.setName(sourceComponentName);
        if(sourceComponent != null){
            messageSource.setEndpoint(sourceComponent.getValue());
        }
        messageHeaderElement.setSource(messageSource);
        MessageHeader.MessageDestinationComponent messageDestination = new MessageHeader.MessageDestinationComponent();
        messageDestination.setName(targetComponentName);
        if(targetComponent != null){
            messageDestination.setEndpoint(targetComponent.getValue());
        }
        messageHeaderElement.setReason(pegacornMessageHeaderCodeSystemFactory.buildMessageReasonCodeableConcept("standard-ipc"));
        return (messageHeaderElement);
    }

    public MessageHeader buildMessageHeader(String sourceComponentName, String targetComponentName){
        MessageHeader newMessageHeader = buildMessageHeader(null, sourceComponentName, null, targetComponentName);
        return(newMessageHeader);
    }
}
