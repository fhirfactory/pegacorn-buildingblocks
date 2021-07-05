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
package net.fhirfactory.pegacorn.internals.communicate.entities.rooms.valuesets;

public enum CommunicateRoomTypeEnum {
    COMMUNICATE_GENERAL_DISCUSSION_ROOM("communicate.room-type.general-discussion-room"),
    COMMUNICATE_PRACTITIONER_MY_MEDIA_ROOM("communicate.room-type.practitioner-media-room"),
    COMMUNICATE_PRACTITIONER_MY_CALLS_ROOM("communicate.room-type.practitioner-calls-room"),
    COMMUNICATE_PRACTITIONER_MY_GENERAL_NOTIFICATIONS_ROOM("communicate.room-type.practitioner-general-notifications"),
    COMMUNICATE_PRACTITIONER_ROLE_FULFILMENT_DISCUSSION_ROOM("communicate.room-type.practitioner-role-fulfilment-discussion-room"),
    COMMUNICATE_PRACTITIONER_ROLE_CENTRAL_ROOM("communicate.room-type.practitioner-role-central-room"),
    COMMUNICATE_HEALTHCARE_SERVICE_CENTRAL_ROOM("communicate.room-type.healthcare-service-central-discussion"),
    COMMUNICATE_PATIENT_CENTRAL_ROOM("communicate.room-type.patient-central-room"),
    COMMUNICATE_PATIENT_TASK_FULFILMENT_ROOM("communicate.room-type.patient-task-fulfilment-room"),
    COMMUNICATE_CARE_TEAM_CENTRAL_ROOM("communicate.room-type.care-team-central-room"),
    COMMUNICATE_CARE_TEAM_FULFILMENT_DISCUSSION_ROOM("communicate.room-type.care-team-fulfillment-discussion-room"),
    COMMUNICATE_CODE_RESPONDER_DISCUSSION_ROOM("communicate.room-type.code-responder-discussion-room"),
    COMMUNICATE_CODE_RESPONDER_CENTRAL_ROOM("communicate.room-type.code-responder-central-room");

    private String roomType;

    private CommunicateRoomTypeEnum(String roomType){
        this.roomType = roomType;
    }

    public String getRoomType(){
        return(roomType);
    }

    public static CommunicateRoomTypeEnum fromRoomTypeString(String roomTypeString){
        for (CommunicateRoomTypeEnum b : CommunicateRoomTypeEnum.values()) {
            if (b.getRoomType().equalsIgnoreCase(roomTypeString)) {
                return b;
            }
        }
        return null;
    }

    public String getRoomTypeDescription(){
        switch(this){
            case COMMUNICATE_CARE_TEAM_CENTRAL_ROOM:
                return("Communicate CareTeam Central Room");
            case COMMUNICATE_GENERAL_DISCUSSION_ROOM:
                return("General Purpose, Non-System Managed, Discussion Room");
            case COMMUNICATE_PRACTITIONER_MY_CALLS_ROOM:
                return("Practitioner's MyCall Room - detailing all audio/video calls made to/from this Practitioner");
            case COMMUNICATE_PRACTITIONER_MY_MEDIA_ROOM:
                return("Practitioner's MyMedia Room - detailing all the media (photos, video, audio) captured and uploaded by this Practitioner");
            case COMMUNICATE_CODE_RESPONDER_CENTRAL_ROOM:
                return("A Code-Responder Central Room - representing a summary of all Code-Response instance(s) of a particular type");
            case COMMUNICATE_CARE_TEAM_FULFILMENT_DISCUSSION_ROOM:
                return("A CareTeam Fulfillment Room - representing the room for dialogue between a Client and a CareTeam fulfiller");
            case COMMUNICATE_CODE_RESPONDER_DISCUSSION_ROOM:
                return("A Code-Responder Room - encapsulating an ongoing dialogue associated with a Code-Response activity");
            case COMMUNICATE_PRACTITIONER_ROLE_CENTRAL_ROOM:
                return("A PractitionerRole Central Room: representing a summary of all PractitionerRole instance(s) of a particular type");
            case COMMUNICATE_HEALTHCARE_SERVICE_CENTRAL_ROOM:
                return("A HealthcareService Central Room: providing a record of all voice services connecting to the identified service");
            case COMMUNICATE_PATIENT_CENTRAL_ROOM:
                return("A Patient Central Room: a centralised point for dialogue surrounding a specific Patient");
            case COMMUNICATE_PATIENT_TASK_FULFILMENT_ROOM:
                return("A Patient Task Room: a room for dialogue surrounding a specific Patient task (page)");
            case COMMUNICATE_PRACTITIONER_MY_GENERAL_NOTIFICATIONS_ROOM:
                return("A General Notifications Room: a centralised point for all System, Organisation or Switchboard Operator originated notifications");
            case COMMUNICATE_PRACTITIONER_ROLE_FULFILMENT_DISCUSSION_ROOM:
                return("A PractitionerRole Fulfillment Room - representing the room for dialogue between a Client and a PractitionerRole fulfiller");
            default:
                return("");
        }
    }


}
