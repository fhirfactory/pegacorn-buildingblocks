/*
 * Copyright (c) 2022 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.internals.communicate.entities.message;

import net.fhirfactory.pegacorn.core.model.internal.resources.simple.CommunicateMessageESR;
import net.fhirfactory.pegacorn.internals.communicate.entities.message.valuesets.CommunicateSMSStatusEnum;

import java.time.Instant;

//TODO work out where the split for general SMS in Pegacorn and Modica specific
//     stuff is
public class CommunicateSMSMessage extends CommunicateMessageESR {
    private String messageId;
    private String phoneNumber;
    private String message;
    private Instant sendDate;
    private CommunicateSMSStatusEnum status;
    
    //
    // Constructor(s)
    //

    public CommunicateSMSMessage(){
        super();
        status = CommunicateSMSStatusEnum.CREATED;
        this.message = null;
        this.messageId = null;
        this.phoneNumber = null;
        this.sendDate = null;
    }

    //
    // Getters and Setters
    //
    
    public String getMessageId() {
        return messageId;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getSendDate() {
        return sendDate;
    }
    public void setSendDate(Instant sendDate) {
        this.sendDate = sendDate;
    }

    public CommunicateSMSStatusEnum getStatus() {
        return status;
    }
    public void setStatus(CommunicateSMSStatusEnum status) {
        this.status = status;
    }
    
    //
    // To String
    //

    @Override
    public String toString() {
        return "CommunicateSMSMessage{" +
                "messageId='" + messageId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", message='" + message + '\'' +
                ", sendDate=" + sendDate +
                ", status=" + status +
                ", resourceType=" + getResourceType() +
                ", systemManaged=" + isSystemManaged() +
                ", simplifiedID='" + getSimplifiedID() + '\'' +
                ", otherID='" + getOtherID() + '\'' +
                ", identifiers=" + getIdentifiers() +
                ", displayName='" + getDisplayName() + '\'' +
                ", simplifiedIDMetadata=" + getSimplifiedIDMetadata() +
                ", description='" + getDescription() + '\'' +
                ", resourceESRType=" + getResourceESRType() +
                '}';
    }
}
