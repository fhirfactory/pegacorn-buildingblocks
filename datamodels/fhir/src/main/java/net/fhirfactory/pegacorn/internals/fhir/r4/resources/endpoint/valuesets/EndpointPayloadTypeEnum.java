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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets;

public enum EndpointPayloadTypeEnum {
    ENDPOINT_PAYLOAD_INTERNAL_IPC("Messaging.MulticastTasks", "pegacorn.internal.messaging.payload.multicast_tasks", "Internal Messaging Payload (Subscribed Task/Trigger Distribution)"),
    ENDPOINT_PAYLOAD_INTERNAL_METRICS("Messaging.Metrics", "pegacorn.internal.messaging.payload.metrics", "Internal Messaging Payload (Petasos Metrics)"),
    ENDPOINT_PAYLOAD_INTERNAL_TOPOLOGY("Messaging.Topology", "pegacorn.internal.messaging.payload.topology", "Internal Messaging Payload (Petasos Topology/Discovery Updates)"),
    ENDPOINT_PAYLOAD_INTERNAL_SUBSCRIPTION("Messaging.Subscriptions", "pegacorn.internal.messaging.payload.subscriptions", "Internal Messaging Payload (Publish/Subscribe Request/Response)"),
    ENDPOINT_PAYLOAD_INTERNAL_INTERCEPTION("Messaging.Interception", "pegacorn.internal.messaging.payload.interception", "Internal Messaging Payload (Interception Data)"),
    ENDPOINT_PAYLOAD_INTERNAL_AUDITEVENTS("Messaging.AuditEvents", "pegacorn.internal.messaging.payload.audit_events", "Internal Messaging Payload (Audit Events)"),
    ENDPOINT_PAYLOAD_INTERNAL_MEDIA("Messaging.Media", "pegacorn.internal.messaging.payload.media", "Internal Messaging Payload (Media)"),
    ENDPOINT_PAYLOAD_INTERNAL_TASKS("Messaging.Tasks", "pegacorn.internal.messaging.payload.tasks", "Internal Messaging Payload (Petasos Tasks Distribution/Replication)"),
    ENDPOINT_PAYLOAD_FHIR("HL7.FHIR", "pegacorn.general.messaging.payload.hl7_fhir", "RESTful HL7 FHIR Operations"),
    ENDPOINT_PAYLOAD_HL7_V2X("HL7.v2x", "pegacorn.general.messaging.payload.hl7_v2x", "MLLP Transported HL7 Version 2.x Events");

    private String displayName;
    private String token;
    private String displayText;

    private EndpointPayloadTypeEnum(String displayName, String token, String displayText){
        this.displayName = displayName;
        this.token = token;
        this.displayText = displayText;
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
