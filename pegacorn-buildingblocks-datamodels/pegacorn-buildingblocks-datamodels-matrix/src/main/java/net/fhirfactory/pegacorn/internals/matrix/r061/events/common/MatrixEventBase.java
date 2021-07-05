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
package net.fhirfactory.pegacorn.internals.matrix.r061.events.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes.MEventTypeEnum;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes.MUnsignedDataType;

import java.util.Date;

public class MatrixEventBase extends MatrixEventKey{

    private String sender;
    private MEventTypeEnum eventType;
    private Date timeStamp;
    private MUnsignedDataType unsigned;

    public MatrixEventBase(){
        super();
        this.sender = null;
        this.eventType = null;
        this.timeStamp = null;
        this.unsigned = null;
    }

    @JsonProperty("unsigned")
    public MUnsignedDataType getUnsigned() {
        return unsigned;
    }

    @JsonProperty("unsigned")
    public void setUnsigned(MUnsignedDataType unsigned) {
        this.unsigned = unsigned;
    }

    @JsonProperty("sender")
    public String getSender() {
        return sender;
    }

    @JsonProperty("sender")
    public void setSender(String sender) {
        this.sender = sender;
    }

    @JsonProperty("type")
    public MEventTypeEnum getEventType() {
        return eventType;
    }

    @JsonProperty("type")
    public void setEventType(MEventTypeEnum eventType) {
        this.eventType = eventType;
    }

    @JsonProperty("origin_server_ts")
    public Date getTimeStamp() {
        return timeStamp;
    }

    @JsonProperty("origin_server_ts")
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @JsonIgnore
    public boolean hasSender(){
        if(this.sender == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasEventType(){
        if(this.eventType == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasTimeStamp(){
        if(this.timeStamp == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasUnsigned(){
        if(this.unsigned == null){
            return(false);
        } else {
            return(true);
        }
    }
}
