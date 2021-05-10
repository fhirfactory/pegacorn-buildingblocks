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

import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.matrix.lingo.entities.components.PractitionerRoleDiscussion;
import net.fhirfactory.pegacorn.internals.matrix.lingo.primitives.MatrixRoom;

import java.util.concurrent.ConcurrentHashMap;

public class LingoPractitioner extends PractitionerESR {
    private ConcurrentHashMap<String, PractitionerRoleDiscussion>  practitionerRoleConversations;
    private MatrixRoom myCallsRoom;
    private MatrixRoom myMediaRoom;
    private MatrixRoom myResultNotificationsRoom;
    private MatrixRoom myGeneralNotificationsRoom;

    public ConcurrentHashMap<String, PractitionerRoleDiscussion> getPractitionerRoleConversations() {
        return practitionerRoleConversations;
    }

    public void setPractitionerRoleConversations(ConcurrentHashMap<String, PractitionerRoleDiscussion> practitionerRoleConversations) {
        this.practitionerRoleConversations = practitionerRoleConversations;
    }

    public MatrixRoom getMyCallsRoom() {
        return myCallsRoom;
    }

    public void setMyCallsRoom(MatrixRoom myCallsRoom) {
        this.myCallsRoom = myCallsRoom;
    }

    public MatrixRoom getMyMediaRoom() {
        return myMediaRoom;
    }

    public void setMyMediaRoom(MatrixRoom myMediaRoom) {
        this.myMediaRoom = myMediaRoom;
    }

    public MatrixRoom getMyResultNotificationsRoom() {
        return myResultNotificationsRoom;
    }

    public void setMyResultNotificationsRoom(MatrixRoom myResultNotificationsRoom) {
        this.myResultNotificationsRoom = myResultNotificationsRoom;
    }

    public MatrixRoom getMyGeneralNotificationsRoom() {
        return myGeneralNotificationsRoom;
    }

    public void setMyGeneralNotificationsRoom(MatrixRoom myGeneralNotificationsRoom) {
        this.myGeneralNotificationsRoom = myGeneralNotificationsRoom;
    }
}
