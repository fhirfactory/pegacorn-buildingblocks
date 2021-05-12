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
package net.fhirfactory.pegacorn.internals.matrix.r061.events.room.message.contenttypes;

import net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes.MEventTypeEnum;

public enum MRoomMessageTypeEnum {
    VIDEO("m.video"),
    LOCATION("m.location"),
    AUDIO("m.audio"),
    FILE("m.file"),
    IMAGE("m.image"),
    NOTICE("m.notice"),
    EMOTE("m.emote"),
    TEXT("m.text"),
    SERVER_NOTICE("m.server_notice");

    private String msgtype;

    private MRoomMessageTypeEnum(String msgtype){
        this.msgtype = msgtype;
    }

    public String getMsgtype(){
        return(msgtype);
    }

    public static MRoomMessageTypeEnum fromString(String text) {
        for (MRoomMessageTypeEnum evType : MRoomMessageTypeEnum.values()) {
            if (evType.msgtype.equalsIgnoreCase(text)) {
                return evType;
            }
        }
        return null;
    }
}
