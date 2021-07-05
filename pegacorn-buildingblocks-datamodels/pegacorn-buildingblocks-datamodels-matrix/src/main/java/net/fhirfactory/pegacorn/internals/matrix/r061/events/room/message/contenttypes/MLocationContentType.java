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
import net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes.MLocationInfoType;

public class MLocationContentType {
    private String body;
    private String geographicURI;
    private MRoomMessageTypeEnum messageType;
    private MLocationInfoType locationInfo;

    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
    }

    @JsonProperty("geo_uri")
    public String getGeographicURI() {
        return geographicURI;
    }

    @JsonProperty("geo_uri")
    public void setGeographicURI(String geographicURI) {
        this.geographicURI = geographicURI;
    }

    @JsonProperty("msgtype")
    public MRoomMessageTypeEnum getMessageType() {
        return messageType;
    }

    @JsonProperty("msgtype")
    public void setMessageType(MRoomMessageTypeEnum messageType) {
        this.messageType = messageType;
    }

    @JsonProperty("info")
    public MLocationInfoType getLocationInfo() {
        return locationInfo;
    }

    @JsonProperty("info")
    public void setLocationInfo(MLocationInfoType locationInfo) {
        this.locationInfo = locationInfo;
    }
}
