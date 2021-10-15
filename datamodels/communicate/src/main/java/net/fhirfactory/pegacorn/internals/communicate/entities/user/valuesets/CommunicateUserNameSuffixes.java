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
package net.fhirfactory.pegacorn.internals.communicate.entities.user.valuesets;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CommunicateUserNameSuffixes {
    private static String HEALTHCARE_SERVICE_VISIBLE_USER_SUFFIX = ".HealthcareService";
    private static String PRACTITIONER_ROLE_SHARED_USER_SUFFIX = ".PractitionerRole";
    private static String CARETEAM_VISIBLE_USER_SUFFIX = ".CareTeam";

    public String getHealthcareServiceVisibleUserSuffix() {
        return HEALTHCARE_SERVICE_VISIBLE_USER_SUFFIX;
    }

    public String getPractitionerRoleSharedUserSuffix() {
        return PRACTITIONER_ROLE_SHARED_USER_SUFFIX;
    }

    public String getCareTeamVisibleUserSuffix() {
        return CARETEAM_VISIBLE_USER_SUFFIX;
    }
}
