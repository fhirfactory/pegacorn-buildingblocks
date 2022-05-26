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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.factories;

import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.AuditEventSourceTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.AuditEventSubTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.auditevent.valuesets.AuditEventTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierFactory;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class AuditEventFactory {
    private static final Logger LOG = LoggerFactory.getLogger(AuditEventFactory.class);

    @Inject
    private PegacornIdentifierFactory identifierFactory;

    public AuditEvent newAuditEvent(Reference petasosNodeReference,
                                    String sourceSystem,
                                    String sourceHost,
                                    String sourceComponentName,
                                    Reference eventLocation,
                                    AuditEventSourceTypeEnum sourceType,
                                    AuditEventTypeEnum eventType,
                                    AuditEventSubTypeEnum eventSubType,
                                    AuditEvent.AuditEventAction eventAction,
                                    AuditEvent.AuditEventOutcome eventOutcome,
                                    String eventOutcomeCommentary,
                                    Period eventPeriod,
                                    AuditEvent.AuditEventEntityComponent auditEntity){

        AuditEvent auditEvent = newAuditEvent(
                petasosNodeReference,
                sourceSystem,
                sourceHost,
                sourceComponentName,
                eventLocation,
                sourceType,
                eventType,
                eventSubType,
                eventAction,
                eventOutcome,
                eventOutcomeCommentary,
                eventPeriod,
                auditEntity,
                null,
                null);

        return(auditEvent);
    }

    public AuditEvent newAuditEvent(Reference petasosNodeReference,
                                    String sourceSystem,
                                    String sourceHost,
                                    String sourceComponentName,
                                    Reference eventLocation,
                                    AuditEventSourceTypeEnum sourceType,
                                    AuditEventTypeEnum eventType,
                                    AuditEventSubTypeEnum eventSubType,
                                    AuditEvent.AuditEventAction eventAction,
                                    AuditEvent.AuditEventOutcome eventOutcome,
                                    String eventOutcomeCommentary,
                                    Period eventPeriod,
                                    AuditEvent.AuditEventEntityComponent auditEntity,
                                    String activitySource,
                                    List<Extension> transactionHistory){

        AuditEvent auditEvent = new AuditEvent();

        auditEvent.setType(eventType.getTypeCoding());
        auditEvent.addSubtype(eventSubType.getSubTypeCoding());

        AuditEvent.AuditEventAgentComponent agent = new AuditEvent.AuditEventAgentComponent();
        agent.setRequestor(false);
        if(petasosNodeReference != null){
            agent.setWho(petasosNodeReference);
        }
        if(eventLocation != null){
            agent.setLocation(eventLocation);
        }
        AuditEvent.AuditEventAgentNetworkComponent agentNetwork = new AuditEvent.AuditEventAgentNetworkComponent();
        agentNetwork.setAddress(sourceHost);
        agentNetwork.setType(AuditEvent.AuditEventAgentNetworkType._1);
        agent.setNetwork(agentNetwork);
        agent.setName(sourceSystem);
        auditEvent.addAgent(agent);

        auditEvent.setAction(eventAction);
        auditEvent.setOutcome(eventOutcome);
        if(!StringUtils.isEmpty(eventOutcomeCommentary)){
            auditEvent.setOutcomeDesc(eventOutcomeCommentary);
        }

        if(eventPeriod != null){
            auditEvent.setPeriod(eventPeriod);
        }

        auditEvent.setRecorded(Date.from(Instant.now()));

        AuditEvent.AuditEventSourceComponent sourceComponent = new AuditEvent.AuditEventSourceComponent();
        sourceComponent.setObserver(petasosNodeReference);
        sourceComponent.addType(sourceType.getSourceTypeCoding());
        sourceComponent.setSite(sourceComponentName);

        auditEvent.setSource(sourceComponent);

        auditEvent.addEntity(auditEntity);

        if(StringUtils.isNotEmpty(activitySource)){
            Extension messageSourceExtension = new Extension();
            messageSourceExtension.setUrl("System.Ingres.Point");
            messageSourceExtension.setValue(new StringType(activitySource));
            auditEvent.addExtension(messageSourceExtension);
        }

        if(transactionHistory != null){
            if(transactionHistory.isEmpty()){
                // do nothing
            } else {
                auditEvent.getExtension().addAll(transactionHistory);
            }
        }

        return(auditEvent);
    }
}
