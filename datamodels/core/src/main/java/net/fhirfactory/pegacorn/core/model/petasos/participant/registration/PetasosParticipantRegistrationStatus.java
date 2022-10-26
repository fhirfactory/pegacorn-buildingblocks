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
package net.fhirfactory.pegacorn.core.model.petasos.participant.registration;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.internals.SerializableObject;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class PetasosParticipantRegistrationStatus implements Serializable {
    private String registrationId;
    private PetasosParticipantRegistrationStatusEnum localRegistrationStatus;
    private PetasosParticipantRegistrationStatusEnum centralRegistrationStatus;
    private String registrationCommentary;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant localRegistrationInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant centralRegistrationInstant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant updateInstant;
    private SerializableObject lock;

    public PetasosParticipantRegistrationStatus(){
        this.localRegistrationStatus = PetasosParticipantRegistrationStatusEnum.PARTICIPANT_UNREGISTERED;
        this.localRegistrationInstant = null;
        this.registrationCommentary = null;
        this.centralRegistrationInstant = null;
        this.updateInstant = Instant.now();
        this.centralRegistrationStatus = PetasosParticipantRegistrationStatusEnum.PARTICIPANT_UNREGISTERED;
        this.lock = new SerializableObject();
        this.registrationId = UUID.randomUUID().toString();
    }

    public PetasosParticipantRegistrationStatus(PetasosParticipantRegistrationStatus ori){
        this.localRegistrationStatus = PetasosParticipantRegistrationStatusEnum.PARTICIPANT_UNREGISTERED;
        this.localRegistrationInstant = null;
        this.registrationCommentary = null;
        this.centralRegistrationInstant = null;
        this.centralRegistrationStatus = PetasosParticipantRegistrationStatusEnum.PARTICIPANT_UNREGISTERED;
        this.updateInstant = Instant.now();
        this.lock = new SerializableObject();
        if(ori.getRegistrationId() != null){
            setRegistrationId(ori.getRegistrationId());
        } else {
            this.registrationId = UUID.randomUUID().toString();
        }
        if(ori.getLocalRegistrationStatus() != null){
            setLocalRegistrationStatus(ori.getLocalRegistrationStatus());
        }
        if(ori.getLocalRegistrationInstant() != null){
            setLocalRegistrationInstant(ori.getLocalRegistrationInstant());
        }
        if(ori.getRegistrationCommentary() != null){
            setRegistrationCommentary(ori.getRegistrationCommentary());
        }
        if(ori.getCentralRegistrationInstant() != null){
            setCentralRegistrationInstant(ori.getCentralRegistrationInstant());
        }
        if(ori.getCentralRegistrationStatus() != null){
            setCentralRegistrationStatus(ori.getCentralRegistrationStatus());
        }
        this.lock = new SerializableObject();
    }

    //
    // Business Methods
    //


    //
    // Getters and Setters
    //


    public Instant getUpdateInstant() {
        return updateInstant;
    }

    public void setUpdateInstant(Instant updateInstant) {
        this.updateInstant = updateInstant;
    }

    public PetasosParticipantRegistrationStatusEnum getCentralRegistrationStatus() {
        return centralRegistrationStatus;
    }

    public void setCentralRegistrationStatus(PetasosParticipantRegistrationStatusEnum centralRegistrationStatus) {
        this.centralRegistrationStatus = centralRegistrationStatus;
    }

    public Instant getCentralRegistrationInstant() {
        return centralRegistrationInstant;
    }

    public void setCentralRegistrationInstant(Instant centralRegistrationInstant) {
        this.centralRegistrationInstant = centralRegistrationInstant;
    }

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

    public PetasosParticipantRegistrationStatusEnum getLocalRegistrationStatus() {
        return localRegistrationStatus;
    }

    public void setLocalRegistrationStatus(PetasosParticipantRegistrationStatusEnum localRegistrationStatus) {
        this.localRegistrationStatus = localRegistrationStatus;
    }

    public Instant getLocalRegistrationInstant() {
        return localRegistrationInstant;
    }

    public void setLocalRegistrationInstant(Instant localRegistrationInstant) {
        this.localRegistrationInstant = localRegistrationInstant;
    }

    public String getRegistrationCommentary() {
        return registrationCommentary;
    }

    public void setRegistrationCommentary(String registrationCommentary) {
        this.registrationCommentary = registrationCommentary;
    }

    //
    // Utility Classes
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PetasosParticipantRegistrationStatus{");
        sb.append("registrationId='").append(registrationId).append('\'');
        sb.append(", localRegistrationStatus=").append(localRegistrationStatus);
        sb.append(", centralRegistrationStatus=").append(centralRegistrationStatus);
        sb.append(", registrationCommentary='").append(registrationCommentary).append('\'');
        sb.append(", localRegistrationInstant=").append(localRegistrationInstant);
        sb.append(", centralRegistrationInstant=").append(centralRegistrationInstant);
        sb.append(", updateInstant=").append(updateInstant);
        sb.append(", lock=").append(lock);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetasosParticipantRegistrationStatus that = (PetasosParticipantRegistrationStatus) o;
        return Objects.equals(getRegistrationId(), that.getRegistrationId()) && getLocalRegistrationStatus() == that.getLocalRegistrationStatus() && Objects.equals(getRegistrationCommentary(), that.getRegistrationCommentary()) && Objects.equals(getLocalRegistrationInstant(), that.getLocalRegistrationInstant()) && Objects.equals(getLock(), that.getLock());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegistrationId(), getLocalRegistrationStatus(), getRegistrationCommentary(), getLocalRegistrationInstant(), getLock());
    }
}
