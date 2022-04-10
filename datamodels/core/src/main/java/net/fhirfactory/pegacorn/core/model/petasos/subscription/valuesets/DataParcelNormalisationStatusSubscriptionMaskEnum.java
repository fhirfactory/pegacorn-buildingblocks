/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.petasos.subscription.valuesets;

public enum DataParcelNormalisationStatusSubscriptionMaskEnum {
    DATA_PARCEL_CONTENT_NORMALISATION_TRUE("Normalised.True", "pegacorn.data-parcel.normalisation-status.true"),
    DATA_PARCEL_CONTENT_NORMALISATION_FALSE("Normalised.False", "pegacorn.data-parcel.normalisation-status.false"),
    DATA_PARCEL_CONTENT_NORMALISATION_ANY("Normalised.Any", "pegacorn.data-parcel.normalisation-status.any");

    private String token;
    private String displayName;

    private DataParcelNormalisationStatusSubscriptionMaskEnum(String displayName, String normalisation){
        this.token = normalisation;
        this.displayName = displayName;
    }

    public String getToken(){
        return(this.token);
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public static DataParcelNormalisationStatusSubscriptionMaskEnum fromNormalisationStatusString(String theString){
        for (DataParcelNormalisationStatusSubscriptionMaskEnum b : DataParcelNormalisationStatusSubscriptionMaskEnum.values()) {
            if (b.getToken().equalsIgnoreCase(theString)) {
                return b;
            }
        }
        return null;
    }
}
