package net.fhirfactory.pegacorn.internals.fhir.r4.codesystems;

import net.fhirfactory.pegacorn.internals.PegacornReferenceProperties;
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
