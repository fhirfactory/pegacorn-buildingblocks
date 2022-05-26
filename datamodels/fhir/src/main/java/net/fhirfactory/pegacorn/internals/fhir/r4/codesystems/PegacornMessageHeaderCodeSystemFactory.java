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
package net.fhirfactory.pegacorn.internals.fhir.r4.codesystems;

import net.fhirfactory.pegacorn.core.constants.systemwide.PegacornReferenceProperties;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PegacornMessageHeaderCodeSystemFactory {

    @Inject
    private PegacornReferenceProperties systemWideProperties;

    private static final String PEGACORN_MESSAGE_HEADER_TYPE_CODE_SYSTEM = "/bundle-message-type";
    private static final String PEGACORN_MESSAGE_HEADER_REASON_CODE_SYSTEM = "/bundle-message-type";

    public Coding buildMessageTypeCoding(String messageType, String messageTypeDisplay){
        Coding newCoding = new Coding();
        String messageHeaderCodingSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_MESSAGE_HEADER_TYPE_CODE_SYSTEM;
        newCoding.setSystem(messageHeaderCodingSystem);
        newCoding.setCode(messageType);
        newCoding.setDisplay(messageTypeDisplay);
        return(newCoding);
    }

    public Coding buildMessageTypeCoding(String messageType){
        Coding newCoding = buildMessageTypeCoding(messageType, messageType);
        return(newCoding);
    }

    public CodeableConcept buildMessageReasonCodeableConcept(String messageReason, String MessageReasonDisplay){
        CodeableConcept messageReasonConcept = new CodeableConcept();
        Coding newCoding = new Coding();
        String messageReasonCodingSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_MESSAGE_HEADER_REASON_CODE_SYSTEM;
        newCoding.setSystem(messageReasonCodingSystem);
        newCoding.setCode(messageReason);
        newCoding.setDisplay(MessageReasonDisplay);
        messageReasonConcept.addCoding(newCoding);
        messageReasonConcept.setText(messageReason);
        return(messageReasonConcept);
    }

    public CodeableConcept buildMessageReasonCodeableConcept(String messageReason){
        CodeableConcept reasonConcept = buildMessageReasonCodeableConcept(messageReason, messageReason);
        return(reasonConcept);
    }
}
