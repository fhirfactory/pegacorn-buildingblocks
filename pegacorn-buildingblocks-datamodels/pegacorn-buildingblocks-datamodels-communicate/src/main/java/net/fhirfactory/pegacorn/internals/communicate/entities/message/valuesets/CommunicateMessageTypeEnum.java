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
package net.fhirfactory.pegacorn.internals.communicate.entities.message.valuesets;

public enum CommunicateMessageTypeEnum {
    COMMUNICATE_TEXT_MESSAGE("comunicate.message-type.text"),
    COMMUNICATE_FORMATTED_TEXT_MESSAGE("comminicate.message-type.formatted-text"),
    COMMUNICATE_VIDEO_MESSAGE("communicate.message-type.video"),
    COMMUNICATE_IMAGE_MESSAGE("communicate.message-type.image"),
    COMMUNICATE_AUDIO_MESSAGE("communicate.message-type.audit"),
    COMMUNICATE_FILE_MESSAGE("communicate.message-type.file"),
    COMMUNICATE_EMOTE_MESSAGE("communicate.message-type.emote");

    private String messageType;

    private CommunicateMessageTypeEnum(String messageType){
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }
}
