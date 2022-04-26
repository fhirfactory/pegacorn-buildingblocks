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
package net.fhirfactory.pegacorn.internals.hl7v2.triggerevents.valuesets;

public enum HL7v2FieldTypeEnum {
    AD("AD", "Address", 2.1, 2.8, "" ),
    CE("CE", "Coded Element", 2.1, 2.8, ""),
    CF("CF", "Coded Element with Formatted Value", 2.2, 2.8, ""),
    CK("CK", "Composite Id with Check Digit", 2.1, 2.8, ""),
    CM("CM", "...", 2.1, 2.2, ""),
    CM_ABS_RANGE("CM_ABS_RANGE", "Absolute Range", 2.2, 2.8, ""),
    CM_AUI("CM_AUI", "Authroization Information", 2.2, 2.8, ""),
    CM_BATCH_TOTAL("CM_BATCH_TOTAL", "CM for Batch Totals", 2.2, 2.8, ""),
    CM_CCD("CM_CCD", "Charge Time", 2.2, 2.8, ""),
    CM_DDI("CM_DDI", "Daily Deductible", 2.2, 2.8, ""),
    CM_DIN("CM_DIN", "Activation Date", 2.2, 2.8, ""),
    CM_DLD("CM_DLD", "Discharge Location", 2.2, 2.8, ""),
    CM_DLT("CM_DLT", "Delta Check", 2.2, 2.8, ""),
    CN_DTN("CM_DTN", "Day Type and Number", 2.2, 2.8, ""),
    CM_EIP("CM_EIP", "Parent Order", 2.2, 2.8, ""),
    CM_ELD("CM_ELD", "Error", 2.2, 2.8, ""),
    CM_UNDEFINED("CM_UNDEFINED", "Undefined CM Data Type", 2.1, 2.8, ""),
    CN("CN", "Composite Id Number and Name", 2.1, 2.8, ""),
    COMP_ID_DIGIT("COMP_ID_DIGIT", "Composite Id with Check Digit", 2.1, 2.8, ""),
    COMP_ID_NAME("COMP_ID_NAME", "Composite Id and Name", 2.1, 2.8, ""),
    COMP_QUANT("COMP_QUANT", "Composite Quantity/Units", 2.1, 2.8, ""),
    CQ("CQ", "Composite Quantity with Units", 2.1, 2.8, ""),
    DT("DT", "Date", 2.1, 2.8, ""),
    FT("FT", "Formatted Text Data", 2.1, 2.8, ""),
    ID("ID", "Coded Value", 2.1, 2.8, ""),
    NM("NM", "Numeric", 2.1, 2.8, ""),
    PN("PN", "Person Name", 2.1, 2.8, ""),
    SET_ID("SET_ID", "Set Id", 2.1, 2.8, ""),
    SI("SI", "Set Id", 2.1, 2.8, ""),
    ST("ST", "String Data", 2.1, 2.8, ""),
    TM("TM", "Time", 2.1, 2.8, ""),
    TN("TN", "Telephone Number", 2.1, 2.8, ""),
    TS("TS", "Time Stamp", 2.1, 2.8, ""),
    TX("TX", "Text Data", 2.1, 2.8, "");

    private String key;
    private String name;
    private String description;
    private Double since;
    private Double until;

    private HL7v2FieldTypeEnum(String key, String name, Double since, Double until, String description){
        this.key = key;
        this.name = name;
        this.since = since;
        this.until = until;
        this.description = description;
    }
}
