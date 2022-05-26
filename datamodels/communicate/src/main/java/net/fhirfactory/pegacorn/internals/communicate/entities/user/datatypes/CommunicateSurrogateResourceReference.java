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
package net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes;

import net.fhirfactory.pegacorn.core.model.ui.resources.simple.datatypes.ReferenceESDT;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.valuesets.ExtremelySimplifiedResourceTypeEnum;

public class CommunicateSurrogateResourceReference {
    private ReferenceESDT realResourceID;
    private ExtremelySimplifiedResourceTypeEnum realResourceType;
    private CommunicateUserID surrogateUserID;

    public ReferenceESDT getRealResourceID() {
        return realResourceID;
    }

    public void setRealResourceID(ReferenceESDT realResourceID) {
        this.realResourceID = realResourceID;
    }

    public ExtremelySimplifiedResourceTypeEnum getRealResourceType() {
        return realResourceType;
    }

    public void setRealResourceType(ExtremelySimplifiedResourceTypeEnum realResourceType) {
        this.realResourceType = realResourceType;
    }

    public CommunicateUserID getSurrogateUserID() {
        return surrogateUserID;
    }

    public void setSurrogateUserID(CommunicateUserID surrogateUserID) {
        this.surrogateUserID = surrogateUserID;
    }

    @Override
    public String toString() {
        return "CommunicateSurrogateResourceReference{" +
                "realResourceID=" + realResourceID +
                ", realResourceType=" + realResourceType +
                ", surrogateUserID=" + surrogateUserID +
                '}';
    }
}
