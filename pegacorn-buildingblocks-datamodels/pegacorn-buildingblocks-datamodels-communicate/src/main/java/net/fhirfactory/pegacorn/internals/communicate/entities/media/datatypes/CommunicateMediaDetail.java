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
package net.fhirfactory.pegacorn.internals.communicate.entities.media.datatypes;

import net.fhirfactory.pegacorn.internals.communicate.entities.media.valuesets.CommunicateMediaTypeEnum;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes.CommunicateRoomReference;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateUserReference;

import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CommunicateMediaDetail {
    private URL mediaURL;
    private String friendlyName;
    private HashMap<String, String> mediaMetadata;
    private CommunicateRoomReference sourceRoom;
    private CommunicateUserReference sourceUser;
    private CommunicateMediaTypeEnum mediaType;

    public CommunicateMediaDetail(){
        mediaMetadata = new HashMap<String, String>();
    }

    public URL getMediaURL() {
        return mediaURL;
    }

    public void setMediaURL(URL mediaURL) {
        this.mediaURL = mediaURL;
    }

    public CommunicateMediaTypeEnum getMediaType() {
        return mediaType;
    }

    public void setMediaType(CommunicateMediaTypeEnum mediaType) {
        this.mediaType = mediaType;
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

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public HashMap<String, String> getMediaMetadata() {
        return mediaMetadata;
    }

    public void setMediaMetadata(HashMap<String, String> mediaMetadata) {
        this.mediaMetadata = mediaMetadata;
    }
}
