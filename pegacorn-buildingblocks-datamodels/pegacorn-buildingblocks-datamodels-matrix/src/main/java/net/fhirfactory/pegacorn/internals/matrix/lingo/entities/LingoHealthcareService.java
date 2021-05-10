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
package net.fhirfactory.pegacorn.internals.matrix.lingo.entities;

import net.fhirfactory.pegacorn.internals.esr.resources.HealthcareServiceESR;
import net.fhirfactory.pegacorn.internals.matrix.lingo.entities.common.FunctionBasedEntity;
import net.fhirfactory.pegacorn.internals.matrix.lingo.primitives.MatrixRoom;

import java.util.ArrayList;

public class LingoHealthcareService extends HealthcareServiceESR implements FunctionBasedEntity {
    private MatrixRoom centralRoom;
    private ArrayList<MatrixRoom> individualPractitionerDiscussions;

    @Override
    public MatrixRoom getCentralRoom() {
        return centralRoom;
    }

    @Override
    public void setCentralRoom(MatrixRoom centralRoom) {
        this.centralRoom = centralRoom;
    }

    @Override
    public ArrayList<MatrixRoom> getIndividualPractitionerDiscussions() {
        return (this.individualPractitionerDiscussions);
    }

    @Override
    public void setIndividualPractitionerDiscussions(ArrayList<MatrixRoom> sideRooms) {
        this.individualPractitionerDiscussions = new ArrayList<>();
        this.individualPractitionerDiscussions.addAll(sideRooms);
    }
}
