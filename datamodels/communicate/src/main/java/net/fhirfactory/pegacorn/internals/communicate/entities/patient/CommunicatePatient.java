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
package net.fhirfactory.pegacorn.internals.communicate.entities.patient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes.CommunicateRoomReference;
import net.fhirfactory.pegacorn.internals.communicate.entities.user.datatypes.CommunicateUserReference;
import net.fhirfactory.pegacorn.core.model.ui.resources.simple.PatientESR;

import java.util.concurrent.ConcurrentHashMap;

public class CommunicatePatient extends PatientESR{
    private ConcurrentHashMap<String, CommunicateRoomReference> patientBasedTaskRooms;
    private CommunicateUserReference surrogateCommunicateUser;

    public CommunicatePatient(){
        super();
        this.patientBasedTaskRooms = new ConcurrentHashMap<>();
        this.surrogateCommunicateUser = null;
    }

    public ConcurrentHashMap<String, CommunicateRoomReference> getPatientBasedTaskRooms() {
        return patientBasedTaskRooms;
    }

    public void setPatientBasedTaskRooms(ConcurrentHashMap<String, CommunicateRoomReference> patientBasedTaskRooms) {
        this.patientBasedTaskRooms = patientBasedTaskRooms;
    }

    public CommunicateUserReference getSurrogateCommunicateUser() {
        return surrogateCommunicateUser;
    }

    public void setSurrogateCommunicateUser(CommunicateUserReference surrogateCommunicateUser) {
        this.surrogateCommunicateUser = surrogateCommunicateUser;
    }

    @JsonIgnore
    public PatientESR getPatientESR(){
        PatientESR patient = new PatientESR();

        return(patient);
    }

    @JsonIgnore
    public void setPatientESR(){

    }
}
