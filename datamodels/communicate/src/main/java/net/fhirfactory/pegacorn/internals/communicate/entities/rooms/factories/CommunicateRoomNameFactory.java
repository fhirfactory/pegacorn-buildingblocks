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
package net.fhirfactory.pegacorn.internals.communicate.entities.rooms.factories;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CommunicateRoomNameFactory {

    private static String HEALTHCARE_SERVICE_VISIBLE_ROOM_SUFFIX = ".HealthcareService";
    private static String PRACTITIONER_ROLE_SHARED_ROOM_SUFFIX = ".PractitionerRole";
    private static String PRACTITIONER_ROLE_ONE_ON_ONE_ROOM_SUFFIX = ".PRC";
    private static String PRACTITIONER_ROLE_ONE_ON_ONE_ROOM_QUALIFIER = "ActingAs";

    private static String CARE_TEAM_PRIMARY_ROOM_SUFFIX = ".CareTeam";
    private static String RESPONDER_TEAM_INSTANCE_SUFFIX = ".Code";

    private static String PRACTITIONER_MEDIA_ROOM = "MyMedia";
    private static String PRACTITIONER_CALL_ROOM = "MyCalls";
    private static String PRACTITIONER_RESULTS_NOTIFICATION_ROOM = "MyResultNotifications";
    private static String PRACTITIONER_SWITCHBOARD_NOTIFICATIONS = "MySwitchboardMessages";
    private static String PRACTITIONER_ORGANISATION_NOTIFICATIONS = "MySystemMessages";


    public boolean isPractitionerRoomName(String roomName){
        if(roomName == null){
            return(false);
        }
        if(roomName.isEmpty() || roomName.isBlank()){
            return(false);
        }
        if(roomName.contentEquals(PRACTITIONER_MEDIA_ROOM)){
            return(true);
        }
        if(roomName.contentEquals(PRACTITIONER_CALL_ROOM)){
            return(true);
        }
        if(roomName.contentEquals(PRACTITIONER_RESULTS_NOTIFICATION_ROOM)){
            return(true);
        }
        if(roomName.contentEquals(PRACTITIONER_SWITCHBOARD_NOTIFICATIONS)){
            return(true);
        }
        if(roomName.contentEquals(PRACTITIONER_ORGANISATION_NOTIFICATIONS)){
            return(true);
        }
        return(false);
    }

    public String getPractitionerMediaRoomName(){
        return(PRACTITIONER_MEDIA_ROOM);
    }

    public String getPractitionerCallRoom(){
        return(PRACTITIONER_CALL_ROOM);
    }

    public String getPractitionerResultsNotificationRoom(){
        return(PRACTITIONER_RESULTS_NOTIFICATION_ROOM);
    }

    public String getPractitionerSwitchboardNotificationsRoom(){
        return(PRACTITIONER_SWITCHBOARD_NOTIFICATIONS);
    }

    public String getPractitionerSystemMessagesRoom(){
        return(PRACTITIONER_ORGANISATION_NOTIFICATIONS);
    }

    public boolean isHealthcareServiceRoomName(String roomName){
        if(roomName == null){
            return(false);
        }
        if(roomName.isEmpty() || roomName.isBlank()){
            return(false);
        }
        if(roomName.contains(HEALTHCARE_SERVICE_VISIBLE_ROOM_SUFFIX)){
            return(true);
        } else {
            return(false);
        }
    }

    public boolean isPractitionerRoleRoomName(String roomName){
        if(roomName == null){
            return(false);
        }
        if(roomName.isEmpty() || roomName.isBlank()){
            return(false);
        }
        if(roomName.contains(PRACTITIONER_ROLE_ONE_ON_ONE_ROOM_QUALIFIER)){
            return(true);
        }
        if(roomName.contains(PRACTITIONER_ROLE_SHARED_ROOM_SUFFIX)){
            return(true);
        }
        if(roomName.contains(PRACTITIONER_ROLE_ONE_ON_ONE_ROOM_SUFFIX)){
            return(true);
        }
        return(false);
    }

    public String constructHealthServiceRoomName(String healthcareServiceName){
        String name = healthcareServiceName + HEALTHCARE_SERVICE_VISIBLE_ROOM_SUFFIX;
        return(name);
    }

    public String constructPractitionerRoleSharedRoomName(String practitionerRoleName){
        String name = practitionerRoleName + PRACTITIONER_ROLE_SHARED_ROOM_SUFFIX;
        return(name);
    }

    public String constructPractitionerRoleConversationRoomName(String practitonerRoleName, String fulfillerName, String requestor ){
        String name = requestor + "<->" + fulfillerName + "(PRACTITIONER_ROLE_ONE_ON_ONE_ROOM_QUALIFIER "+ practitonerRoleName +")" + PRACTITIONER_ROLE_ONE_ON_ONE_ROOM_SUFFIX;
        return(name);
    }

}
