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
package net.fhirfactory.pegacorn.components.dataparcel.valuesets;

public enum DataParcelTypeKeyEnum {
    DATASET_DEFINER("definer"),
    DATASET_CATEGORY("category"),
    DATASET_SUBCATEGORY("subcategory"),
    DATASET_RESOURCE("resource"),
    DATASET_SEGMENT("segment"),
    DATASET_ATTRIBUTE("attribute"),
    DATASET_DISCRIMINATOR_VALUE("discriminator_value"),
    DATASET_NORMALISATION_STATUS("normalisation_status"),
    DATASET_VALIDATION_STATUS("validation_status"),
    DATASET_TYPE("type"),
    DATASET_SOURCE("source"),
    DATASET_INTENDED_TARGET("target"),
    DATASET_VERSION("version");
   
    private String topicKey;

    private DataParcelTypeKeyEnum(String mapElementType){
        this.topicKey = mapElementType;
    }

    public String getTopicKey(){
        return(this.topicKey);
    }

    public static DataParcelTypeKeyEnum fromTopicKeyString(String keyString){
        for (DataParcelTypeKeyEnum b : DataParcelTypeKeyEnum.values()) {
            if (b.getTopicKey().equalsIgnoreCase(keyString)) {
                return b;
            }
        }
        return null;
    }
}
