/*
 * The MIT License
 *
 * Copyright 2020 Mark A. Hunter (ACT Health).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.group.valuesets;

import org.hl7.fhir.r4.model.UriType;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GroupIdentifierExtensionMeanings {
    private static final UriType IDENTIFIER_ASSOCIATED_FRIENDLY_NAME = new UriType("http://pegacorn.fhirfactory.net/pegacorn/fhir/R4/Identifier/identifier_associated_friendly_name");
    private static final UriType IDENTIFIER_ASSOCIATED_AVATAR = new UriType("http://pegacorn.fhirfactory.net/pegacorn/fhir/R4/Identifier/identifier_associated_avatar");

    public String getIdentifierAssociatedFriendlyName(){return(IDENTIFIER_ASSOCIATED_FRIENDLY_NAME.asStringValue());}
    public String getIdentifierAssociatedAvatar(){return(IDENTIFIER_ASSOCIATED_AVATAR.asStringValue());}
}
