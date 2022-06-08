/*
 * Copyright (c) 2022 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.petasos.dataparcel.valuesets;

public enum DataParcelInternallyDistributableStatusEnum {
    DATA_PARCEL_INTERNALLY_DISTRIBUTABLE_TRUE("InternallyDistributable.True", "pegacorn.data-parcel.internally-distributable-status.true"),
    DATA_PARCEL_INTERNALLY_DISTRIBUTABLE_FALSE("InternallyDistributable.False", "pegacorn.data-parcel.internally-distributable-status.false");

    private String token;
    private String displayName;

    private DataParcelInternallyDistributableStatusEnum(String displayName, String normalisation){
        this.token = normalisation;
        this.displayName = displayName;
    }

    public String getToken(){
        return(this.token);
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public static DataParcelInternallyDistributableStatusEnum fromNormalisationStatusString(String theString){
        for (DataParcelInternallyDistributableStatusEnum b : DataParcelInternallyDistributableStatusEnum.values()) {
            if (b.getToken().equalsIgnoreCase(theString)) {
                return b;
            }
        }
        return null;
    }
}
