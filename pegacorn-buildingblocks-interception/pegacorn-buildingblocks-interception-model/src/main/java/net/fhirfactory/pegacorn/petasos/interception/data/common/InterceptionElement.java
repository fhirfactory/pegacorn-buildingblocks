/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.interception.data.common;

import java.time.Instant;

public class InterceptionElement {
    String interceptionId;
    Instant creationInstant;
    Instant interceptionInstant;
    String payload;
    String payloadType;

    //
    // Constructor(s)
    //

    public InterceptionElement(){
        this.interceptionId = null;
        this.creationInstant = Instant.now();
        this.interceptionInstant = null;
        this.payload = null;
        this.payloadType = null;
    }

    //
    // Getters and Setters
    //

    public String getInterceptionId() {
        return interceptionId;
    }

    public void setInterceptionId(String interceptionId) {
        this.interceptionId = interceptionId;
    }

    public Instant getCreationInstant() {
        return creationInstant;
    }

    public void setCreationInstant(Instant creationInstant) {
        this.creationInstant = creationInstant;
    }

    public Instant getInterceptionInstant() {
        return interceptionInstant;
    }

    public void setInterceptionInstant(Instant interceptionInstant) {
        this.interceptionInstant = interceptionInstant;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(String payloadType) {
        this.payloadType = payloadType;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "InterceptionElement{" +
                "interceptionId=" + interceptionId +
                ", creationInstant=" + creationInstant +
                ", interceptionInstant=" + interceptionInstant +
                ", payload='" + payload + '\'' +
                ", payloadType='" + payloadType + '\'' +
                '}';
    }
}
