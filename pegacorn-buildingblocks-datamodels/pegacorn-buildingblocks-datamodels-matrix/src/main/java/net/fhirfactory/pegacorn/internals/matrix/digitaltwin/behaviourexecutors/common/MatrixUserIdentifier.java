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
package net.fhirfactory.pegacorn.internals.matrix.digitaltwin.behaviourexecutors.common;

import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierType;

public class MatrixUserIdentifier extends IdentifierESDT {

    public MatrixUserIdentifier(){
        super();
        this.setType(IdentifierType.MATRIX_USER_ID);
        this.setUse(IdentifierESDTUseEnum.SECONDARY);
        this.setValue(null);
    }

    public MatrixUserIdentifier(String userIdentifier){
        this.setValue(userIdentifier);
    }

    public MatrixUserIdentifier(MatrixUserIdentifier oriIdentifier){
        this.setType(IdentifierType.MATRIX_USER_ID);
        this.setUse(IdentifierESDTUseEnum.SECONDARY);
        this.setValue(oriIdentifier.getValue());
    }
}
