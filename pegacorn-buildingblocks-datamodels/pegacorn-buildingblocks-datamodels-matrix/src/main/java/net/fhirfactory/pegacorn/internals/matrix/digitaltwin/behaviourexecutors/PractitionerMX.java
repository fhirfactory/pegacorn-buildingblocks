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
package net.fhirfactory.pegacorn.internals.matrix.digitaltwin.behaviourexecutors;

import java.util.ArrayList;
import java.util.HashMap;

import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.matrix.digitaltwin.behaviourexecutors.common.MatrixRoomIdentifier;
import net.fhirfactory.pegacorn.internals.matrix.digitaltwin.behaviourexecutors.common.MatrixUserIdentifier;

public class PractitionerMX extends PractitionerESR {
    private MatrixUserIdentifier matrixUserIdentifier;
    private HashMap<String, String> otherIdentifiers;
    private ArrayList<MatrixRoomIdentifier> roomOwnership;
    private ArrayList<MatrixRoomIdentifier> roomMembership;

    public MatrixUserIdentifier getMatrixUserIdentifier() {
        return matrixUserIdentifier;
    }

    public void setMatrixUserIdentifier(MatrixUserIdentifier matrixUserIdentifier) {
        this.matrixUserIdentifier = matrixUserIdentifier;
    }

    public void setMatrixUserIdentifier(String matrixUserIdentifier){
        MatrixUserIdentifier newUserIdentifier = new MatrixUserIdentifier(matrixUserIdentifier);
        this.matrixUserIdentifier = newUserIdentifier;
    }

    public HashMap<String, String> getOtherIdentifiers() {
        return otherIdentifiers;
    }

    public void setOtherIdentifiers(HashMap<String, String> otherIdentifiers) {
        this.otherIdentifiers = otherIdentifiers;
    }

    public ArrayList<MatrixRoomIdentifier> getRoomOwnership() {
        return roomOwnership;
    }

    public void setRoomOwnership(ArrayList<MatrixRoomIdentifier> roomOwnership) {
        this.roomOwnership = roomOwnership;
    }

    public ArrayList<MatrixRoomIdentifier> getRoomMembership() {
        return roomMembership;
    }

    public void setRoomMembership(ArrayList<MatrixRoomIdentifier> roomMembership) {
        this.roomMembership = roomMembership;
    }
}
