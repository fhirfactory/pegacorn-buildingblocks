/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.deployment.communicate.matrix;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CommunicateSystemManagedRoomNames {
    public String getPractitionerCallRoom(){return("MyCalls");}
    public String getPractitionerMediaRoom(){return("MyMedia");}
    public String getPractitionerGeneralMessagesRoom(){return("GeneralMessages");}
    public String getPractitionerCodeNotificationsRoom(){return("CodeNotifications");}
    public String getPractitionerCriticalResultsNotificationsRoom(){return("ResultNotifications");}

    public String getPractitionerCallRoomAlias(String practitionerEmailAddressUserNamePart){
        return(getPractitionerCallRoom()+"-"+practitionerEmailAddressUserNamePart);
    }
    public String getPractitionerMediaRoomAlias(String practitionerEmailAddressUserNamePart){
        return(getPractitionerMediaRoom()+"-"+practitionerEmailAddressUserNamePart);
    }
    public String getPractitionerGeneralMessagesRoomAlias(String practitionerEmailAddressUserNamePart){
        return(getPractitionerGeneralMessagesRoom()+"-"+practitionerEmailAddressUserNamePart);
    }
    public String getPractitionerCodeNotificationsRoomAlias(String practitionerEmailAddressUserNamePart){
        return(getPractitionerCodeNotificationsRoom()+"-"+practitionerEmailAddressUserNamePart);
    }
    public String getPractitionerCriticalResultsNotificationsRoomAlias(String practitionerEmailAddressUserNamePart){
        return(getPractitionerCriticalResultsNotificationsRoom()+"-"+practitionerEmailAddressUserNamePart);
    }

    public String getPractitionersPractitionerRoleRoomPrefix(){return("MyRole->");}
    public String getPractitionerRoleRoomPrefix(){return("");}
    public String getPractitionerRoleSharedRoomPrefix(){return("PRHidden->");}
    public String getPractitionerRolePublicCallRoom(){return("PRCalls->");}

    public String getPractitionerSharedExtensionRoom(){return("SharedPhone->");}
}
