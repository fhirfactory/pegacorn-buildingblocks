package net.fhirfactory.pegacorn.internals.communicate.entities.message.valuesets;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CommunicateSMSStatusEnum {
    CREATED,          // initial state.
    GATEWAY_FAILED,   // failed sending from ModicaClient.sendSMS.
                      // This state includes failures that did not result in an actual connection to the Modica SMS Gateway.
    GATEWAY_ACCEPTED, // The Modica SMS Gateway has accepted the request to send an SMS message

    // Callback statuses.  Documented for Modica Gateway.  Names cannot be changed.
    SUBMITTED,        // Message successfully submitted to the carrier for delivery
    SENT,             // Message has been sent by the carrier transport
    RECEIVED,         // Message has been received
    FROZEN,           // A transient error has frozen this message
    REJECTED,         // The carrier rejected the message
    FAILED,           // Message delivery has failed due to carrier connectivity issue
    DEAD,             // Message killed by administrator
    EXPIRED;          // The carrier was unable to deliver the message in a specified amount of time. For instance when the phone was turned off.
    
    @JsonCreator
    public static CommunicateSMSStatusEnum fromString(String smsStatusName) {
        return CommunicateSMSStatusEnum.valueOf(smsStatusName.toUpperCase());
    }
    
    public boolean isDLRStatus() {
        return !(CREATED.equals(this) || GATEWAY_FAILED.equals(this) || GATEWAY_ACCEPTED.equals(this));
    }
    
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
