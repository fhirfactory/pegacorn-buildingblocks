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
package net.fhirfactory.pegacorn.platform.edge.itops.configuration.segments;

import org.apache.commons.lang3.StringUtils;

public class SecurityCredentialSegment {
    private String credentialName;
    private String credentialPassword;
    private String credentialReferenceName;
    private String credentialReferenceKey;

    public SecurityCredentialSegment(){
        setCredentialName(null);
        setCredentialPassword(null);
        setCredentialReferenceKey(null);
        setCredentialReferenceName(null);
    }

    public void mergeOverrides(SecurityCredentialSegment overrides){
        if (hasCredentialName()) {
            setCredentialName(overrides.getCredentialName());
        }
        if(hasCredentialPassword()){
            setCredentialPassword(overrides.getCredentialPassword());
        }
        if (hasCredentialReferenceKey()) {
            setCredentialReferenceKey(overrides.getCredentialReferenceKey());
        }
        if (hasCredentialReferenceName()) {
            setCredentialReferenceName(overrides.getCredentialReferenceName());
        }
    }

    public boolean hasCredentialName(){
        boolean has = !(StringUtils.isEmpty(this.credentialName));
        return(has);
    }

    public boolean hasCredentialPassword(){
        boolean has = !(StringUtils.isEmpty(this.credentialPassword));
        return(has);
    }

    public boolean hasCredentialReferenceName(){
        boolean has = !(StringUtils.isEmpty(this.credentialReferenceName));
        return(has);
    }

    public boolean hasCredentialReferenceKey(){
        boolean has = !(StringUtils.isEmpty(this.credentialReferenceKey));
        return(has);
    }

    public String getCredentialName() {
        return credentialName;
    }

    public void setCredentialName(String credentialName) {
        this.credentialName = credentialName;
    }

    public String getCredentialReferenceName() {
        return credentialReferenceName;
    }

    public void setCredentialReferenceName(String credentialReferenceName) {
        this.credentialReferenceName = credentialReferenceName;
    }

    public String getCredentialPassword() {
        return credentialPassword;
    }

    public void setCredentialPassword(String credentialPassword) {
        this.credentialPassword = credentialPassword;
    }

    public String getCredentialReferenceKey() {
        return credentialReferenceKey;
    }

    public void setCredentialReferenceKey(String credentialReferenceKey) {
        this.credentialReferenceKey = credentialReferenceKey;
    }

    @Override
    public String toString() {
        return "SecurityCredentialSegment{" +
                "credentialName='" + credentialName + '\'' +
                ", credentialPassword='" + credentialPassword + '\'' +
                ", credentialReferenceName='" + credentialReferenceName + '\'' +
                ", credentialReferenceKey='" + credentialReferenceKey + '\'' +
                '}';
    }
}
