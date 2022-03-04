/*
 * Copyright (c) 2021 ACT Health
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
package net.fhirfactory.pegacorn.internals.communicate.entities.message.factories;

import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.fhirfactory.pegacorn.fhir.helpers.ContactPointHelper;
import net.fhirfactory.pegacorn.fhir.helpers.exception.ContactPointRetrieveException;
import net.fhirfactory.pegacorn.internals.communicate.entities.message.CommunicateEmailMessage;
import net.fhirfactory.pegacorn.internals.communicate.entities.message.datatypes.CommunicateEmailAttachment;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Communication.CommunicationPayloadComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CommunicationToPegacornEmailFactory {
    
    public static final String EMAIL_SUBJECT_EXTENSION_URL = "identifier://net.fhirfactory.pegacorn.fhirbreak.corpservices.emailgateway/subject";  // will be moved to a more common petasos class
    public static final String EMAIL_CONTENT_TYPE_EXTENSION_URL = "identifier://net.fhirfactory.pegacorn.fhirbreak.corpservices.emailgateway/contenttype";  // will be moved to a more common petasos class

    private static final Logger LOG = LoggerFactory.getLogger(CommunicationToPegacornEmailFactory.class);
    
    protected static final String FAILURE_MULTIPLE_CONTENT = "Found multiple contentString payload elements";
    protected static final String FAILURE_INVALID_SENDER_REFERENCE = "Could not get resource for sender";
    protected static final String FAILURE_INVALID_RECIPIENT_REFERENCE = "Could not get resource for recipient";
    protected static final String FAILURE_NO_EMAIL_FOR_SENDER = "Could not get email for sender";
    protected static final String FAILURE_NO_EMAIL_FOR_RECIPIENT = "Could not get email for recipient";
    protected static final String FAILURE_CONVERT_EMAIL_TO_JSON = "Could not convert email to JSON";
    
    private ObjectMapper jsonMapper; //TODO make common
    private IParser fhirParser;
    private boolean initialised;
    @Inject
    private FHIRContextUtility fhirContextUtility;
    
    //
    // Constructor
    //
    
    public CommunicationToPegacornEmailFactory() {
    }
    
    //
    // Post Construct
    //
    
    @PostConstruct
    public void initialise() {
        LOG.debug(".initialise(): Entry");
        if(!initialised) {
            LOG.info(".initialise(): initialising....");
            fhirParser = fhirContextUtility.getJsonParser();
            jsonMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT); // sets pretty printing
            this.initialised = true;
            LOG.info(".initialise(): Done.");
        }
        else {
            LOG.debug(".initialise(): Already initialised, nothing to do...");
        }
        LOG.debug(".initialise(): Exit");
    }
    

    public CommunicateEmailMessage transformCommunicationToEmail(Communication incomingCommunication) {
        LOG.debug(".transformCommunicationToEmail(): Entry");
        
        CommunicateEmailMessage email = new CommunicateEmailMessage();
        
        // get the sender
        if (incomingCommunication.hasSender()) {
            Resource sender = incomingCommunication.getSenderTarget();
            if (sender == null && incomingCommunication.getSender() != null) {
                Reference senderRef = incomingCommunication.getSender();
                sender = (Resource) senderRef.getResource();
            }
            if (sender == null) {
                LOG.debug(".transformCommunicationToEmail(): Exit, sender is null");
                return (null);
            }
            try {
                email.setFrom(ContactPointHelper.getTopRankContact(sender, ContactPoint.ContactPointSystem.EMAIL).getValue());
            } catch (ContactPointRetrieveException e) {
                LOG.warn(".transformCommunicationToEmail(): Exit, {}->{}", FAILURE_NO_EMAIL_FOR_SENDER, sender, e);
                return (null);
            }
        }
        
        // get the recipients
        List<String> toEmails = new ArrayList<>();
        if (incomingCommunication.hasRecipient()) {
            List<Reference> recipients = incomingCommunication.getRecipient();
            for (Reference recipientRef: recipients) {
                Resource recipient = (Resource) recipientRef.getResource();
                if (recipient == null) {
                    LOG.warn(".transformCommunicationToEmail(): Exit, {}", FAILURE_INVALID_RECIPIENT_REFERENCE); //TODO add reference
                    return (null);
                }
                try {
                    toEmails.add(ContactPointHelper.getTopRankContact(recipient, ContactPoint.ContactPointSystem.EMAIL).getValue());
                } catch (ContactPointRetrieveException e) {
                    LOG.warn(".transformCommunicationToEmail(): Exit, {}->{}", FAILURE_NO_EMAIL_FOR_RECIPIENT, recipient, e);
                    return (null);
                }
            }
        }
        email.setTo(toEmails);
        
        // get the content, subject and attachments
        if (incomingCommunication.hasPayload()) {
            List<CommunicationPayloadComponent> payload = incomingCommunication.getPayload();       
            boolean hasContent = false;
            int numAttachments = 0;
            
            for (CommunicationPayloadComponent payloadComponent : payload) {
                if (payloadComponent.hasContentAttachment()) {
                    numAttachments++;
                    //TODO do we have any use for language?
                    LOG.debug(".transformCommunicationToEmail(): Processing attachment {}", numAttachments);
                    Attachment communicationAttachment = payloadComponent.getContentAttachment();
                    
                    CommunicateEmailAttachment emailAttachment = new CommunicateEmailAttachment();
                    emailAttachment.setContentType(communicationAttachment.getContentType());
                    emailAttachment.setName(communicationAttachment.getTitle());
                    if (communicationAttachment.hasSize()) {
                        emailAttachment.setSize(Long.valueOf(communicationAttachment.getSize())); // note that the FHIR attachment returns size as an int so not sure what it does if size is larger than max in size (~2MB)
                    }
                    if (communicationAttachment.hasCreation()) {
                        //TODO check this (as not sure if time is local or GMT and what is wanted for end email)
                        emailAttachment.setCreationTime(communicationAttachment.getCreation().toString());
                    }
                    if (communicationAttachment.hasHash()) {
                        emailAttachment.setHash(communicationAttachment.getHashElement().getValueAsString());
                    }
                    if (communicationAttachment.hasData()) {
                        emailAttachment.setData(communicationAttachment.getDataElement().getValueAsString());
                    }
                    if (communicationAttachment.hasUrl()) {
                        emailAttachment.setUrl(communicationAttachment.getUrl());
                    }
                    email.getAttachments().add(emailAttachment);
                    
                } else if (payloadComponent.hasContentReference()) {
                    //TODO support this
                    // Just log a warning and ignore this
                    String referenceDisplay = payloadComponent.getContentReference().getDisplay();
                    if (referenceDisplay == null) {
                        referenceDisplay = "";
                    } else {
                        referenceDisplay = ": display->" + referenceDisplay;
                    }
                    LOG.warn(".transformCommunicationToEmail(): Ignored unsupported reference payload type{}", referenceDisplay);
                    
                } else if (payloadComponent.hasContentStringType()) {
                    if (hasContent) {
                        // multiple content - not allowed as not sure how this should be processed
                        //TODO check this.  This could make sense in some scenarios, particularly for multipart/alternative however
                        //     would need an extension element to flag this

                        LOG.warn(".transformCommunicationToEmail(): Exit, {} for email->{}", FAILURE_MULTIPLE_CONTENT, email);
                        return (null);
                    }
                    hasContent = true;
                    
                    // set content
                    String emailContent = payloadComponent.getContentStringType().primitiveValue();
                    email.setContent(emailContent);
                    
                    // get the subject from the extension
                    LOG.debug(".transformCommunicationToEmail(): Getting email subject from payload extension");
                    Extension subjectExtension = payloadComponent.getExtensionByUrl(EMAIL_SUBJECT_EXTENSION_URL);
                    if (subjectExtension != null) {
                        email.setSubject(subjectExtension.getValue().primitiveValue());
                    }
                    
                    // get the content type from the extension
                    LOG.debug(".transformCommunicationToEmail(): Getting email content type from payload extension");
                    Extension contentTypeExtension = payloadComponent.getExtensionByUrl(EMAIL_CONTENT_TYPE_EXTENSION_URL);
                    if (contentTypeExtension != null) {
                        email.setContentType(contentTypeExtension.getValue().primitiveValue());
                    }
                }
            }
        }
        
        // log at info level
        //TODO find out what limits there should be in what is logged
        //TODO add some sort of correlation id for tracking log messages to this this.
        //     note that the Camel MDC values such as message.id do not seem useful for this
        LOG.info(".transformCommunicationToEmail(): Transformed communication into email->{}", email);
        
        LOG.debug(".transformCommunicationToEmail(): Exit");
        return(email);
    }
}
