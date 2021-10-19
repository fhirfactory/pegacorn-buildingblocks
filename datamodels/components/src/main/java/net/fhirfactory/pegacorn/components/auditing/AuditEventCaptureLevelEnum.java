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
package net.fhirfactory.pegacorn.components.auditing;

public enum AuditEventCaptureLevelEnum {
    LEVEL_1_BLACK_BOX(1, "Level-1.AETHER-As-BlackBox"),
    LEVEL_2_SUBSYSTEM(2, "Level-2.Subsystem-Ingres-and-Egress"),
    LEVEL_3_WUP_FINALISATION(3, "Level-3.WorkUnitProcessor-Finalisation"),
    LEVEL_4_WUP_ALL(4, "Level-4.WorkUnitProcessor-Start-Finish-and-Finalisation"),
    LEVEL_5_COMPONENT_ALL(5, "Level-5.All-Components");

    private String displayName;
    private int auditLevel;

    private AuditEventCaptureLevelEnum(int level, String name){
        this.displayName = name;
        this.auditLevel = level;
    }

    public String getDisplayName(){
        return(displayName);
    }

    public int getAuditLevel() {
        return auditLevel;
    }
}
