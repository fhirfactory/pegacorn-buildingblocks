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
package net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MJSONWebKey {
    private String keyType;
    private String keyOperations;
    private String algorithm;
    private String key;
    private Boolean extractable;

    @JsonProperty("kty")
    public String getKeyType() {
        return keyType;
    }

    @JsonProperty("kty")
    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    @JsonProperty("key_ops")
    public String getKeyOperations() {
        return keyOperations;
    }

    @JsonProperty("key_ops")
    public void setKeyOperations(String keyOperations) {
        this.keyOperations = keyOperations;
    }

    @JsonProperty("alg")
    public String getAlgorithm() {
        return algorithm;
    }

    @JsonProperty("alg")
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    @JsonProperty("k")
    public String getKey() {
        return key;
    }

    @JsonProperty("k")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("ext")
    public Boolean getExtractable() {
        return extractable;
    }

    @JsonProperty("ext")
    public void setExtractable(Boolean extractable) {
        this.extractable = extractable;
    }

    @JsonIgnore
    public boolean isExtractable(){
        return(this.extractable);
    }
}
