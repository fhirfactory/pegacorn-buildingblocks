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

import java.io.Serializable;
import java.time.Instant;

public class RemoteProcedureCallResponse implements Serializable {
    private String associatedRequestID;
    private boolean inScope;
    private boolean successful;
    private Instant instantCompleted;
    private Object responseContent;
    private Class responseContentType;

    //
    // Constructor(s)
    //

    public RemoteProcedureCallResponse(){
        setAssociatedRequestID(null);
        setInScope(false);
        setSuccessful(false);
        setInstantCompleted(null);
        setResponseContent(null);
        setResponseContentType(null);
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasReponseContentType(){
        boolean hasValue = this.responseContentType != null;
        return(hasValue);
    }

    public Class getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(Class responseContentType) {
        this.responseContentType = responseContentType;
    }

    @JsonIgnore
    public boolean hasResponseContent(){
        boolean hasValue = this.responseContent != null;
        return(hasValue);
    }

    public Object getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(Object responseContent) {
        this.responseContent = responseContent;
    }

    @JsonIgnore
    public boolean hasAssociatedRequestID(){
        boolean hasValue = this.associatedRequestID != null;
        return(hasValue);
    }

    public String getAssociatedRequestID() {
        return associatedRequestID;
    }

    public void setAssociatedRequestID(String associatedRequestID) {
        this.associatedRequestID = associatedRequestID;
    }

    public boolean isInScope() {
        return inScope;
    }

    public void setInScope(boolean inScope) {
        this.inScope = inScope;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    @JsonIgnore
    public boolean hasDateCompleted(){
        boolean hasValue = this.instantCompleted != null;
        return(hasValue);
    }

    public Instant getInstantCompleted() {
        return instantCompleted;
    }

    public void setInstantCompleted(Instant instantCompleted) {
        this.instantCompleted = instantCompleted;
    }

    //
    // Type Based Getters/Setters
    //

    @JsonIgnore
    public String getResponseStringContent(){
        if(hasResponseContent()){
            if(getResponseContentType().equals(String.class)){
                String stringValue = (String)(getResponseContent());
                return(stringValue);
            }
        }
        return(null);
    }

    @JsonIgnore
    public void setResponseStringContent(String stringContent){
        setResponseContent(stringContent);
        setResponseContentType(String.class);
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "CapabilityUtilisationResponse{" +
                "associatedRequestID='" + associatedRequestID + '\'' +
                ", inScope=" + inScope +
                ", successful=" + successful +
                ", dateCompleted=" + instantCompleted +
                ", responseContent=" + responseContent +
                ", responseContentType=" + responseContentType +
                '}';
    }
}
