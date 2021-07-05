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

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes.MEncryptedFileType;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes.MVideoInfoType;

public class MVideoContentType {
    private String body;
    private MVideoInfoType videoInfo;
    private MRoomMessageTypeEnum messageType;
    private String videoURL;
    private MEncryptedFileType videoFile;

    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
    }

    @JsonProperty("info")
    public MVideoInfoType getVideoInfo() {
        return videoInfo;
    }

    @JsonProperty("info")
    public void setVideoInfo(MVideoInfoType videoInfo) {
        this.videoInfo = videoInfo;
    }

    @JsonProperty("msgtype")
    public MRoomMessageTypeEnum getMessageType() {
        return messageType;
    }

    @JsonProperty("msgtype")
    public void setMessageType(MRoomMessageTypeEnum messageType) {
        this.messageType = messageType;
    }

    @JsonProperty("url")
    public String getVideoURL() {
        return videoURL;
    }

    @JsonProperty("url")
    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    @JsonProperty("file")
    public MEncryptedFileType getVideoFile() {
        return videoFile;
    }

    @JsonProperty("file")
    public void setVideoFile(MEncryptedFileType videoFile) {
        this.videoFile = videoFile;
    }
}
