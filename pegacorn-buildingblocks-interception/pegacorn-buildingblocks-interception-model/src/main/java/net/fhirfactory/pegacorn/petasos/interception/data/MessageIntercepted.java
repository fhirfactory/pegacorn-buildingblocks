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
package net.fhirfactory.pegacorn.petasos.interception.data;

import net.fhirfactory.pegacorn.petasos.interception.data.common.InterceptionElement;

public class MessageIntercepted extends InterceptionElement {
    private String sourceComponentId;
    private String sourceSubsystemId;

    //
    // Constructor(s)
    //

    public MessageIntercepted(){
        super();
        this.sourceComponentId = null;
        this.sourceSubsystemId = null;
    }

    //
    // Getters and Setters
    //

    public String getSourceComponentId() {
        return sourceComponentId;
    }

    public void setSourceComponentId(String sourceComponentId) {
        this.sourceComponentId = sourceComponentId;
    }

    public String getSourceSubsystemId() {
        return sourceSubsystemId;
    }

    public void setSourceSubsystemId(String sourceSubsystemId) {
        this.sourceSubsystemId = sourceSubsystemId;
    }
}
