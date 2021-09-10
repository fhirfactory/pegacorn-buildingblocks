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
package net.fhirfactory.pegacorn.internals.communicate.entities.common.valuesets;

public enum CommunicateResourceTypeEnum {
    COMMUNICATE_CARETEAM("communicate.resource.careteam"),
    COMMUNICATE_CODE_RESPONDER("communicate.resource.code_response"),
    COMMUNICATE_GROUP("communicate.resource.careteam"),
    COMMUNICATE_HEALTHCARESERVICE("communicate.resource.healthcare_service"),
    COMMUNICATE_LOCATION("communicate.resource.location"),
    COMMUNICATE_MEDIA("communicate.resource.media"),
    COMMUNICATE_MESSAGE("communicate.resource.message"),
    COMMUNICATE_ORGANIZATION("communicate.resource.organization"),
    COMMUNICATE_PATIENT("communicate.resource.patient"),
    COMMUNICATE_PRACTITIONER("communicate.resource.practitioner"),
    COMMUNICATE_PRACTITIONER_ROLE("communicate.resource.practitioner_role"),
    COMMUNICATE_ROOM_CODE_RESPONDER("communicate.resource.room.code_responder"),
    COMMUNICATE_ROOM_HISTORIC("communicate.resource.room.historic"),
    COMMUNICATE_ROOM_PATIENT_CENTRAL("communicate.resource.room.patient_central"),
    COMMUNICATE_ROOM_PATIENT_CENTRAL_TASK_FULFILLMENT("communicate.resource.room.patient_central_task_fulfillment"),
    COMMUNICATE_ROOM_PRACTITIONER_MY_CALLS("communicate.resource.room.practitioner_my_calls"),
    COMMUNICATE_ROOM_PRACTITIONER_MY_MEDIA("communicate.resource.room.practitioner_my_media"),
    COMMUNICATE_ROOM_PRACTITIONER_ROLE_CENTRAL("communicate.resource.room.practitioner_role_central"),
    COMMUNICATE_ROOM_PRACTITIONER_ROLE_FULFILLMENT("communicate.resource.room.practitioner_role_fulfillment"),
    COMMUNICATE_ROOM("communicate.resource.room"),
    COMMUNICATE_SESSION("communicate.resource.session"),
    COMMUNICATE_USER("communicate.resource.room.code_responder");

    private String communicateResourceType;

    private CommunicateResourceTypeEnum(String resourceType){
        this.communicateResourceType = resourceType;
    }

    public String getCommunicateResourceType() {
        return (this.communicateResourceType);
    }
}
