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
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.work.datatypes.TaskWorkItemSubscriptionType;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class TaskWorkItemSubscriptionRegistration implements Serializable {
    private TaskWorkItemSubscriptionType workItemSubscription;
    private PetasosParticipant participant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = PetasosPropertyConstants.DEFAULT_TIMEZONE)
    private Instant registrationInstant;

    //
    // Constructor(s)
    //

    public TaskWorkItemSubscriptionRegistration(){
        this.participant = null;
        this.workItemSubscription = null;
        this.registrationInstant = Instant.now();
    }

    public TaskWorkItemSubscriptionRegistration(DataParcelManifest taskWorkItemManifest, PetasosParticipant subscriber){
        this.participant = subscriber;
        this.workItemSubscription = new TaskWorkItemSubscriptionType(taskWorkItemManifest);
        if(!subscriber.getSubscriptions().contains(getWorkItemSubscription())){
            subscriber.getSubscriptions().add(getWorkItemSubscription());
        }
        this.registrationInstant = Instant.now();
    }

    public TaskWorkItemSubscriptionRegistration(TaskWorkItemSubscriptionType taskWorkItemSubscriptionFilter, PetasosParticipant subscriber){
        this.participant = subscriber;
        this.workItemSubscription = new TaskWorkItemSubscriptionType(taskWorkItemSubscriptionFilter);
        if(!subscriber.getSubscriptions().contains(getWorkItemSubscription())){
            subscriber.getSubscriptions().add(getWorkItemSubscription());
        }
        this.registrationInstant = Instant.now();
    }

    //
    // Getters and Setters
    //

    public TaskWorkItemSubscriptionType getWorkItemSubscription() {
        return workItemSubscription;
    }

    @JsonIgnore
    public void setWorkItemSubscription(DataParcelManifest workItemSubscription) {
        this.workItemSubscription = new TaskWorkItemSubscriptionType(workItemSubscription);
    }

    public void setTaskWorkItemManifest(TaskWorkItemSubscriptionType taskWorkItemManifest){
        this.workItemSubscription = taskWorkItemManifest;
    }

    public Instant getRegistrationInstant() {
        return registrationInstant;
    }

    public void setRegistrationInstant(Instant registrationInstant) {
        this.registrationInstant = registrationInstant;
    }

    public PetasosParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(PetasosParticipant participant) {
        this.participant = participant;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "TaskWorkItemSubscription{" +
                "subscriptionFilter=" + workItemSubscription +
                ", participant=" + participant +
                ", registrationInstant=" + registrationInstant +
                '}';
    }

    //
    // Equals and Hashcode
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskWorkItemSubscriptionRegistration that = (TaskWorkItemSubscriptionRegistration) o;
        return Objects.equals(workItemSubscription, that.workItemSubscription) && Objects.equals(participant, that.participant) && Objects.equals(registrationInstant, that.registrationInstant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workItemSubscription, participant, registrationInstant);
    }
}
