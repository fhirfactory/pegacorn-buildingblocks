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

//
// This class is only needed because there is something weird about the MethodOutcome (HAPI FHIR)
// class and trying to JSON encode it to a string.
//
package net.fhirfactory.pegacorn.components.transaction.model;

import java.util.Objects;

public class SimpleResourceID {
    private String url;
    private String resourceType;
    private String value;
    private String version;

    public static String DEFAULT_VERSION = "1.0.0";

    public SimpleResourceID(){
        this.url = null;
        this.resourceType = null;
        this.value = null;
        this.version = null;
    }

    public SimpleResourceID(String url, String resourceType, String value, String version){
        this.url = url;
        this.resourceType = resourceType;
        this.value = value;
        this.version = version;
    }

    public SimpleResourceID(String resourceType, String value, String version){
        this.url = null;
        this.resourceType = resourceType;
        this.value = value;
        this.version = version;
    }

    public SimpleResourceID(String value, String version){
        this.url = null;
        this.resourceType = null;
        this.value = value;
        this.version = version;
    }

    public SimpleResourceID(String value){
        this.url = null;
        this.resourceType = null;
        this.value = value;
        this.version = DEFAULT_VERSION;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleResourceID)) return false;
        SimpleResourceID that = (SimpleResourceID) o;
        return Objects.equals(getUrl(), that.getUrl()) && Objects.equals(getResourceType(), that.getResourceType()) && Objects.equals(getValue(), that.getValue()) && Objects.equals(getVersion(), that.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(), getResourceType(), getValue(), getVersion());
    }

    @Override
    public String toString() {
        return "ResourceID{" +
                "url=" + url +
                ", resourceType=" + resourceType +
                ", value=" + value +
                ", version=" + version +
                '}';
    }
}
