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
package net.fhirfactory.pegacorn.internals.communicate.entities.message;

import net.fhirfactory.pegacorn.internals.communicate.entities.message.datatypes.CommunicateMessageContentBase;
import net.fhirfactory.pegacorn.internals.communicate.entities.message.datatypes.CommunicateMessageIdentifier;
import net.fhirfactory.pegacorn.internals.communicate.entities.message.valuesets.CommunicateMessageTypeEnum;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes.CommunicateRoomReference;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateUserReference;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.CommunicateMessageESR;

public class CommunicateMessage extends CommunicateMessageESR {
    private CommunicateMessageIdentifier messageIdentifier;
    private CommunicateRoomReference sourceRoom;
    private CommunicateUserReference sourceUser;
    private CommunicateMessageTypeEnum messageType;
    private CommunicateMessageContentBase messageContent;
    private CommunicateMessageIdentifier inResponseTo;

    public CommunicateMessageIdentifier getMessageIdentifier() {
        return messageIdentifier;
    }

    public void setMessageIdentifier(CommunicateMessageIdentifier messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
    }

    public CommunicateRoomReference getSourceRoom() {
        return sourceRoom;
    }

    public void setSourceRoom(CommunicateRoomReference sourceRoom) {
        this.sourceRoom = sourceRoom;
    }

    public CommunicateUserReference getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(CommunicateUserReference sourceUser) {
        this.sourceUser = sourceUser;
    }

    public CommunicateMessageTypeEnum getMessageType() {
        return messageType;
    }

    public void setMessageType(CommunicateMessageTypeEnum messageType) {
        this.messageType = messageType;
    }

    public CommunicateMessageContentBase getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(CommunicateMessageContentBase messageContent) {
        this.messageContent = messageContent;
    }

    public CommunicateMessageIdentifier getInResponseTo() {
        return inResponseTo;
    }

    public void setInResponseTo(CommunicateMessageIdentifier inResponseTo) {
        this.inResponseTo = inResponseTo;
    }

    @Override
    public String toString() {
        return "CommunicateMessage{" +
                "messageIdentifier=" + messageIdentifier +
                ", sourceRoom=" + sourceRoom +
                ", sourceUser=" + sourceUser +
                ", messageType=" + messageType +
                ", messageContent=" + messageContent +
                ", inResponseTo=" + inResponseTo +
                '}';
    }
}
