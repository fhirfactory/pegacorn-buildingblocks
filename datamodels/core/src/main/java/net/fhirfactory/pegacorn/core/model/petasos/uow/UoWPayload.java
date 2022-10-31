/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.petasos.uow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelQualityStatement;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.schedule.datatypes.TaskWindow;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Mark A. Hunter
 */
public class UoWPayload implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(UoWPayload.class);
    protected Logger getLogger(){
        return(LOG);
    }

    private DataParcelManifest payloadManifest;
    private TaskWindow payloadWindow;
    private String payload;

    public UoWPayload() {
        payload = null;
        payloadManifest = null;
    }

    public UoWPayload(UoWPayload originalUoWPayload) {
        this.payload = (String)SerializationUtils.clone(originalUoWPayload.getPayload());
        this.payloadManifest = (DataParcelManifest) SerializationUtils.clone(originalUoWPayload.getPayloadManifest());
    }

    public UoWPayload(DataParcelManifest payloadType, String payloadContent){
        this.payload = (String)SerializationUtils.clone(payloadContent);
        this.payloadManifest = (DataParcelManifest) SerializationUtils.clone(payloadType);
    }

    public String getPayload() {
        getLogger().debug(".getPayload(): Entry");
        getLogger().debug(".getPayload(): Exit, returning Payload (String) --> {}", this.payload);
        return payload;
    }

    public void setPayload(String payload) {
        getLogger().debug(".setPayload(): Entry, payload (String) --> {}", payload);
        this.payload = (String) SerializationUtils.clone(payload);
    }

    public boolean hasPayloadManifest(){
        boolean hasValue = this.payloadManifest != null;
        return(hasValue);
    }

    public DataParcelManifest getPayloadManifest() {
        getLogger().debug(".getPayloadTopicID(): Entry");
        getLogger().debug(".getPayloadTopicID(): Exit, returning Payload (String) --> {}", this.payloadManifest);
        return payloadManifest;
    }

    public void setPayloadManifest(DataParcelManifest payloadManifest) {
        getLogger().debug(".setPayloadTopicID(): Entry, payloadTopicID (TopicToken) --> {}", payloadManifest);
        this.payloadManifest = (DataParcelManifest) SerializationUtils.clone(payloadManifest);
    }

    public boolean hasDataParcelQualityStatement(){
        if(payloadManifest == null){
            return(false);
        }
        return(payloadManifest.hasDataParcelQualityStatement());
    }

    public TaskWindow getPayloadWindow() {
        return payloadWindow;
    }

    public void setPayloadWindow(TaskWindow payloadWindow) {
        this.payloadWindow = payloadWindow;
    }

    @JsonIgnore
    public DataParcelQualityStatement getPayloadQuality() {
        getLogger().debug(".getPayloadQuality(): Entry");
        if(hasDataParcelQualityStatement()){
            getLogger().debug(".getPayloadQuality(): Exit, returning payloadQuality->{}", this.getPayloadManifest().getPayloadQuality());
            return(this.getPayloadManifest().getPayloadQuality());
        } else {
            getLogger().debug(".getPayloadQuality(): Exit, no payloadQuality statement available");
            return (null);
        }
    }

    //
    // toString
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UoWPayload{");
        sb.append("payloadManifest=").append(payloadManifest);
        sb.append(", payload=").append(payload);
        sb.append(", payloadWindow=").append(payloadWindow);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UoWPayload)) return false;
        UoWPayload that = (UoWPayload) o;
        return Objects.equals(getPayloadManifest(), that.getPayloadManifest()) && Objects.equals(getPayload(), that.getPayload()) && Objects.equals(getPayloadWindow(), that.getPayloadWindow());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPayloadManifest(), getPayload());
    }
}
