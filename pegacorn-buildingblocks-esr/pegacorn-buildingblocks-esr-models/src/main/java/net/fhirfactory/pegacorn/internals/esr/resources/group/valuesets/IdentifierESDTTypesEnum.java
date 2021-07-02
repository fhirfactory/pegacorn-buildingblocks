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
package net.fhirfactory.pegacorn.internals.esr.resources.valuesets;

import javax.enterprise.context.ApplicationScoped;

public enum IdentifierESDTTypesEnum {
    ESR_IDENTIFIER_TYPE_SHORT_NAME("ShortName"),
    ESR_IDENTIFIER_TYPE_LONG_NAME("LongName"),
    ESR_IDENTIFIER_TYPE_EMAIL_ADDRESS("EmailAddress"),
    ESR_IDENTIFIER_TYPE_MATRIX_ROOM_ID("matrix.room_id"),
    ESR_IDENTIFIER_TYPE_ROOM_SYSTEM_ID("RoomSystemID"),
    ESR_IDENTIFIER_TYPE_MATRIX_USER_ID("matrix.user_id"),
    ESR_IDENTIFIER_TYPE_PATIENT_URN("patient_urn"),
    ESR_IDENTIFIER_TYPE_COMMUNICATE_SESSION("SessionID");

    private String identifierType;

    private IdentifierESDTTypesEnum(String identifierType){
        this.identifierType = identifierType;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public static IdentifierESDTTypesEnum fromString(String text) {
        for (IdentifierESDTTypesEnum b : IdentifierESDTTypesEnum.values()) {
            if (b.identifierType.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
