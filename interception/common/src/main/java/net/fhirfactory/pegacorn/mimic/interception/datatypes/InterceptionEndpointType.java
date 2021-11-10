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
package net.fhirfactory.pegacorn.mimic.interception.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class InterceptionEndpointType implements Serializable {
    private String endpointName;
    private String endpointDetail;
    private String subsystemName;
    private String workshopName;
    private String wupName;

    //
    // Constructor(s)
    //

    public InterceptionEndpointType(){
        this.endpointName = null;
        this.subsystemName = null;
        this.workshopName = null;
        this.wupName = null;
        this.endpointDetail = null;
    }
    
    //
    // Getters and Setters (Bean Methods)
    //

    @JsonIgnore
    public boolean hasEndpointName(){
        boolean hasValue = this.endpointName != null;
        return(hasValue);
    }

    public String getEndpointName() {
        return endpointName;
    }

    public void setEndpointName(String endpointName) {
        this.endpointName = endpointName;
    }

    @JsonIgnore
    public boolean hasSubsystemName(){
        boolean hasValue = this.subsystemName != null;
        return(hasValue);
    }

    public String getSubsystemName() {
        return subsystemName;
    }

    public void setSubsystemName(String subsystemName) {
        this.subsystemName = subsystemName;
    }

    @JsonIgnore
    public boolean hasWorkshopName(){
        boolean hasValue = this.workshopName != null;
        return(hasValue);
    }

    public String getWorkshopName() {
        return workshopName;
    }

    public void setWorkshopName(String workshopName) {
        this.workshopName = workshopName;
    }

    @JsonIgnore
    public boolean hasWupName(){
        boolean hasValue = this.wupName != null;
        return(hasValue);
    }

    public String getWupName() {
        return wupName;
    }

    public void setWupName(String wupName) {
        this.wupName = wupName;
    }

    @JsonIgnore
    public boolean hasEndpointDetail(){
        boolean hasValue = this.endpointDetail != null;
        return(hasValue);
    }

    public String getEndpointDetail() {
        return endpointDetail;
    }

    public void setEndpointDetail(String endpointDetail) {
        this.endpointDetail = endpointDetail;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "InterceptionEndpointType{" +
                "endpointName='" + endpointName + '\'' +
                ", endpointDetail='" + endpointDetail + '\'' +
                ", subsystemName='" + subsystemName + '\'' +
                ", workshopName='" + workshopName + '\'' +
                ", wupName='" + wupName + '\'' +
                '}';
    }
}
