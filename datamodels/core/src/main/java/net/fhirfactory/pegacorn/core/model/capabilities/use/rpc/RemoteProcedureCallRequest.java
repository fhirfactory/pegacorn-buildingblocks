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
package net.fhirfactory.pegacorn.core.model.capabilities.use.rpc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPointSummary;

import java.io.Serializable;
import java.time.Instant;

public class RemoteProcedureCallRequest implements Serializable {
    private String requestID;
    private Instant requestInstant;
    private Object requestContent;
    private Class requestContentType;
    private JGroupsIntegrationPointSummary requestingEndpoint;
    private String method;

    //
    // Constructor(s)
    //

    public RemoteProcedureCallRequest(){
        setRequestID(null);
        setRequestInstant(null);
        setRequestContentType(null);
        setRequestingEndpoint(null);
        setRequestContent(null);
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasMethod(){
        boolean hasValue = this.method != null;
        return(hasValue);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @JsonIgnore
    public boolean hasRequestContentType(){
        boolean hasValue = this.requestContentType != null;
        return(hasValue);
    }

    public Class getRequestContentType() {
        return requestContentType;
    }

    public void setRequestContentType(Class requestContentType) {
        this.requestContentType = requestContentType;
    }

    @JsonIgnore
    public boolean hasRequestContent(){
        boolean hasValue = this.requestContent != null;
        return(hasValue);
    }

    public Object getRequestContent() {
        return requestContent;
    }

    public void setRequestContent(Object requestContent) {
        this.requestContent = requestContent;
    }

    @JsonIgnore
    public boolean hasRequestingEndpoint(){
        boolean hasValue = this.requestingEndpoint != null;
        return(hasValue);
    }

    public JGroupsIntegrationPointSummary getRequestingEndpoint() {
        return requestingEndpoint;
    }

    public void setRequestingEndpoint(JGroupsIntegrationPointSummary requestingEndpoint) {
        this.requestingEndpoint = requestingEndpoint;
    }

    @JsonIgnore
    public boolean hasRequestID(){
        boolean hasValue = this.requestID != null;
        return(hasValue);
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    @JsonIgnore
    public boolean hasRequestDate(){
        boolean hasValue = this.requestInstant != null;
        return(hasValue);
    }

    public Instant getRequestInstant() {
        return requestInstant;
    }

    public void setRequestInstant(Instant requestInstant) {
        this.requestInstant = requestInstant;
    }

    //
    // Type Based Getters/Setters
    //

    @JsonIgnore
    public String getRequestStringContent(){
        if(hasRequestContent()){
            if(getRequestContentType().equals(String.class)){
                String stringValue = (String)(getRequestContent());
                return(stringValue);
            }
        }
        return(null);
    }

    @JsonIgnore
    public void setRequestStringContent(String stringContent){
        setRequestContent(stringContent);
        setRequestContentType(String.class);
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "RemoteProcedureCallRequest{" +
                "requestID='" + requestID + '\'' +
                ", requestInstant=" + requestInstant +
                ", requestContent=" + requestContent +
                ", requestContentType=" + requestContentType +
                ", requestingEndpoint=" + requestingEndpoint +
                ", method="+method+
                '}';
    }
}
