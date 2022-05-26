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
package net.fhirfactory.pegacorn.core.model.petasos.participant;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.internals.SerializableObject;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class PetasosParticipantRegistration implements Serializable {
    private String registrationId;
    private PetasosParticipantRegistrationStatusEnum registrationStatus;
    private PetasosParticipant participant;
    private String registrationCommentary;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant registrationInstant;
    private SerializableObject lock;

    public PetasosParticipantRegistration(){
        this.registrationStatus = null;
        this.registrationInstant = null;
        this.registrationCommentary = null;
        this.participant = null;
        this.lock = new SerializableObject();
        this.registrationId = UUID.randomUUID().toString();
    }

    public PetasosParticipantRegistration(PetasosParticipantRegistration ori){
        if(ori.getRegistrationId() != null){
            setRegistrationId(ori.getRegistrationId());
        }
        if(ori.getRegistrationStatus() != null){
            setRegistrationStatus(ori.getRegistrationStatus());
        }
        if(ori.getRegistrationInstant() != null){
            setRegistrationInstant(ori.getRegistrationInstant());
        }
        if(ori.getRegistrationCommentary() != null){
            setRegistrationCommentary(ori.getRegistrationCommentary());
        }
        if(ori.getParticipant() != null) {
            setParticipant(ori.getParticipant());
        }
        this.lock = new SerializableObject();
    }

    //
    // Business Methods
    //


    //
    // Getters and Setters
    //


    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public SerializableObject getLock() {
        return lock;
    }

    public void setLock(SerializableObject lock) {
        this.lock = lock;
    }

    public PetasosParticipantRegistrationStatusEnum getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(PetasosParticipantRegistrationStatusEnum registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public Instant getRegistrationInstant() {
        return registrationInstant;
    }

    public void setRegistrationInstant(Instant registrationInstant) {
        this.registrationInstant = registrationInstant;
    }

    public String getRegistrationCommentary() {
        return registrationCommentary;
    }

    public void setRegistrationCommentary(String registrationCommentary) {
        this.registrationCommentary = registrationCommentary;
    }

    public PetasosParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(PetasosParticipant participant) {
        this.participant = participant;
    }

    //
    // Utility Classes
    //

    @Override
    public String toString() {
        return "PetasosParticipantRegistration{" +
                "registrationId='" + registrationId + '\'' +
                ", registrationStatus=" + registrationStatus +
                ", participant=" + participant +
                ", registrationCommentary='" + registrationCommentary + '\'' +
                ", registrationInstant=" + registrationInstant +
                ", lock=" + lock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetasosParticipantRegistration that = (PetasosParticipantRegistration) o;
        return Objects.equals(getRegistrationId(), that.getRegistrationId()) && getRegistrationStatus() == that.getRegistrationStatus() && Objects.equals(getParticipant(), that.getParticipant()) && Objects.equals(getRegistrationCommentary(), that.getRegistrationCommentary()) && Objects.equals(getRegistrationInstant(), that.getRegistrationInstant()) && Objects.equals(getLock(), that.getLock());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegistrationId(), getRegistrationStatus(), getParticipant(), getRegistrationCommentary(), getRegistrationInstant(), getLock());
    }
}
