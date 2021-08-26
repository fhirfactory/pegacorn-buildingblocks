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
package net.fhirfactory.pegacorn.components.capabilities.hl7v2tasks;

import net.fhirfactory.pegacorn.components.capabilities.base.CapabilityUtilisationResponse;

public class A19QueryTaskOutcome extends CapabilityUtilisationResponse {
    String a19QueryResponse;

    public String getA19QueryResponse() {
        return a19QueryResponse;
    }

    public void setA19QueryResponse(String a19QueryResponse) {
        this.a19QueryResponse = a19QueryResponse;
    }

    @Override
    public String toString() {
        return "A19QueryTaskOutcome{" +
                "a19QueryResponse='" + a19QueryResponse + '\'' +
                ", associatedRequestID='" + getAssociatedRequestID() + '\'' +
                ", inScope=" + isInScope() +
                ", successful=" + isSuccessful() +
                ", dateCompleted=" + getDateCompleted() +
                '}';
    }
}