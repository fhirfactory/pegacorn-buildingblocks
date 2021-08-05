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
package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.ladon.nexus.im;

public abstract class LadonNexusProperties{

    private static Integer VERY_LONG_CACHE_RETENTION_PERIOD = 1800; // 30 Minutes
    private static Integer LONG_CACHE_RETENTION_PERIOD = 300; // 5 Minutes
    private static Integer MEDIUM_CACHE_RETENTION_PERIOD = 60; // 1 Minute
    private static Integer SHORT_CACHE_RETENTION_PERIOD = 10; // 10 Seconds
    private static Integer VERY_SHORT_CACHE_RETENTION_PERIOD = 2; // 2 Seconds

    //
    // VirtualDB Cache Entry (flushing) Ages --> In Seconds
    //
    public Integer getCareTeamCacheAgeThreshold() {
        return (SHORT_CACHE_RETENTION_PERIOD);
    }

    public Integer getCommunicationCacheAgeThreshold() {
        return (LONG_CACHE_RETENTION_PERIOD);
    }

    public Integer getCommunicationRequestCacheAgeThreshold() {
        return (LONG_CACHE_RETENTION_PERIOD);
    }

    public Integer getDocumentReferenceCacheAgeThreshold() {
        return (VERY_LONG_CACHE_RETENTION_PERIOD);
    }

    public Integer getEncounterCacheAgeThreshold() {
        return (SHORT_CACHE_RETENTION_PERIOD);
    }

    public Integer getEndpointCacheAgeThreshold() {
        return (LONG_CACHE_RETENTION_PERIOD);
    }

    public Integer getGroupCacheAgeThreshold() {
        return (SHORT_CACHE_RETENTION_PERIOD);
    }

    public Integer getHealthcareServiceCacheAgeThreshold() {
        return (LONG_CACHE_RETENTION_PERIOD);
    }

    public Integer getLocationCacheAgeThreshold() {
        return (MEDIUM_CACHE_RETENTION_PERIOD);
    }

    public Integer getOrganizationCacheAgeThreshold() {
        return (MEDIUM_CACHE_RETENTION_PERIOD);
    }

    public Integer getPatientCacheAgeThreshold() {
        return (SHORT_CACHE_RETENTION_PERIOD);
    }

    public Integer getPractitionerCacheAgeThreshold() {
        return (SHORT_CACHE_RETENTION_PERIOD);
    }

    public Integer getPractitionerRoleCacheAgeThreshold() {
        return (VERY_SHORT_CACHE_RETENTION_PERIOD);
    }

    public Integer getProcedureCacheAgeThreshold() {
        return (LONG_CACHE_RETENTION_PERIOD);
    }

    public Integer getTaskCacheAgeThreshold() {
        return (VERY_SHORT_CACHE_RETENTION_PERIOD);
    }

    public Integer getValueSetCacheAgeThreshold() {
        return (VERY_LONG_CACHE_RETENTION_PERIOD);
    }
}
