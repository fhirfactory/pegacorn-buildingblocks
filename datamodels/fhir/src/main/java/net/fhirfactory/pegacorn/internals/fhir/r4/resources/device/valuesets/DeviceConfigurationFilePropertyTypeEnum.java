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
    CONFIG_FILE_JGROUPS_PETASOS_IPC_MESSAGING("JGroupsInterZoneIPC", "pegacorn.fhir.device.property.configuration_fule.jgroups_interzone_ipc", "JGroups Inter-Zone IPC Messaging Configuration File"),
    CONFIG_FILE_JGROUPS_PETASOS_TOPOLOGY("JGroupsInterZoneTopology", "pegacorn.fhir.device.property.configuration_fule.jgroups_interzone_topology", "JGroups Inter-Zone Topology Services Configuration File"),
    CONFIG_FILE_JGROUPS_PETASOS_AUDIT("JGroupsIntraZoneAudit", "pegacorn.fhir.device.property.configuration_fule.jgroups_intrazone_audit", "JGroups Intra-Zone Audit Services Configuration File"),
    CONFIG_FILE_JGROUPS_PETASOS_TASKS("JGroupsInterZoneTasks", "pegacorn.fhir.device.property.configuration_fule.jgroups_interzone_tasks", "JGroups Inter-Zone Task Services Configuration File"),
    CONFIG_FILE_JGROUPS_PETASOS_SUBSCRIPTION("JGroupsInterZoneSubscription", "pegacorn.fhir.device.property.configuration_fule.jgroups_interzone_subscription", "JGroups Inter-Zone Subscription Configuration File"),
    CONFIG_FILE_JGROUPS_PETASOS_METRICS("JGroupsInterZoneMetrics", "pegacorn.fhir.device.property.configuration_fule.jgroups_interzone_metrics", "JGroups Inter-Zone Metrics Configuration File"),
    CONFIG_FILE_JGROUPS_MULTIUSE_INFINISPAN("JGroupsMultiZoneInfinispan", "pegacorn.fhir.device.property.configuration_fule.jgroups_multizone_infinispan", "JGroups Multi-Zone Infinispan Configuration File"),
    CONFIG_FILE_JGROUPS_PETASOS_INTERCEPTION("JGroupsInterZoneInterception", "pegacorn.fhir.device.property.configuration_fule.jgroups_interzone_interception", "JGroups Inter-Zone Interception Configuration File");

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
