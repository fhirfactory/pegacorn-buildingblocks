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
package net.fhirfactory.pegacorn.internals.matrix.r061.api.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MAPIErrorResponse {
    private String errorCode;
    private String errorDescription;

    public MAPIErrorResponse(){
        this.errorCode = null;
        this.errorDescription = null;
    }

    @JsonProperty("errcode")
    public String getErrorCode() {
        return errorCode;
    }

    @JsonProperty("errcode")
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @JsonProperty("error")
    public String getErrorDescription() {
        return errorDescription;
    }

    @JsonProperty("error")
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @JsonIgnore
    public boolean hasErrorCode(){
        if(this.errorCode == null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasErrorDescription(){
        if(this.errorDescription == null){
            return(false);
        } else {
            return(true);
        }
    }
}
