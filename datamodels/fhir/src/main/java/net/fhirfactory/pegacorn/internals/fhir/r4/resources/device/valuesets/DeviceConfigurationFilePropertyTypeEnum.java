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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.device.valuesets;

public enum DeviceConfigurationFilePropertyTypeEnum {
    CONFIG_FILE_JGROUPS_INTRAZONE_IPC("JGroupsIntraZoneIPC", "pegacorn.fhir.device.property.configuration_fule.jgroups_intrazone_ipc", "JGroups Intra-Zone IPC Configuration File"),
    CONFIG_FILE_JGROUPS_INTERZONE_IPC("JGroupsInterZoneIPC", "pegacorn.fhir.device.property.configuration_fule.jgroups_interzone_ipc", "JGroups Inter-Zone IPC Configuration File"),
    CONFIG_FILE_JGROUPS_INTRAZONE_OAM("JGroupsIntraZoneOAM", "pegacorn.fhir.device.property.configuration_fule.jgroups_intrazone_oam", "JGroups Intra-Zone OAM Configuration File"),
    CONFIG_FILE_JGROUPS_INTERZONE_OAM("JGroupsInterZoneOAM", "pegacorn.fhir.device.property.configuration_fule.jgroups_interzone_oam", "JGroups Inter-Zone OAM Configuration File"),
    CONFIG_FILE_JGROUPS_INTRAZONE_AUDIT("JGroupsIntraZoneAudit", "pegacorn.fhir.device.property.configuration_fule.jgroups_intrazone_audit", "JGroups Intra-Zone AuditTrail Configuration File"),
    CONFIG_FILE_JGROUPS_INTERZONE_AUDIT("JGroupsInterZoneAudit", "pegacorn.fhir.device.property.configuration_fule.jgroups_interzone_audit", "JGroups Inter-Zone AuditTrail Configuration File"),
    CONFIG_FILE_JGROUPS_INTRAZONE_TASKS("JGroupsIntraZoneTasks", "pegacorn.fhir.device.property.configuration_fule.jgroups_intrazone_tasks", "JGroups Intra-Zone Tasks Configuration File"),
    CONFIG_FILE_JGROUPS_INTERZONE_TASKS("JGroupsInterZoneTasks", "pegacorn.fhir.device.property.configuration_fule.jgroups_interzone_tasks", "JGroups Inter-Zone Tasks Configuration File"),
    CONFIG_FILE_JGROUPS_INTRAZONE_METRICS("JGroupsIntraZoneMetrics", "pegacorn.fhir.device.property.configuration_fule.jgroups_intrazone_metrics", "JGroups Intra-Zone Metrics Configuration File"),
    CONFIG_FILE_JGROUPS_INTERZONE_METRICS("JGroupsInterZoneMetrics", "pegacorn.fhir.device.property.configuration_fule.jgroups_interzone_metrics", "JGroups Inter-Zone Metrics Configuration File"),
    CONFIG_FILE_JGROUPS_INTRAZONE_INTERCEPTION("JGroupsIntraZoneInterception", "pegacorn.fhir.device.property.configuration_fule.jgroups_intrazone_interception", "JGroups Intra-Zone Interception Configuration File"),
    CONFIG_FILE_JGROUPS_INTERZONE_INTERCEPTION("JGroupsInterZoneInterception", "pegacorn.fhir.device.property.configuration_fule.jgroups_interzone_interception", "JGroups Inter-Zone Interception Configuration File");

    private String displayName;
    private String token;
    private String displayText;

    private DeviceConfigurationFilePropertyTypeEnum(String name, String code, String text){
        this.displayText = text;
        this.token = code;
        this.displayName = name;
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public String getToken(){
        return(this.token);
    }

    public String getDisplayText(){
        return(this.displayText);
    }

}
