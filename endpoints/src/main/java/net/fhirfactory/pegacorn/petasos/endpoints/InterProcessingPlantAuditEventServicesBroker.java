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
package net.fhirfactory.pegacorn.petasos.endpoints;

import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventServiceBrokerInterface;
import net.fhirfactory.pegacorn.core.model.transaction.model.PegacornTransactionMethodOutcome;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.audit.PetasosInterZoneAuditEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.audit.PetasosIntraZoneAuditEndpoint;
import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class InterProcessingPlantAuditEventServicesBroker implements PetasosAuditEventServiceBrokerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(InterProcessingPlantAuditEventServicesBroker.class);

    @Inject
    private PetasosIntraZoneAuditEndpoint intraZoneAuditEndpoint;

    @Inject
    private PetasosInterZoneAuditEndpoint interZoneAuditEndpoint;

    @Override
    public PegacornTransactionMethodOutcome logAuditEvent(String taskFulfiller, AuditEvent task) {
        LOG.debug(".logAuditEvent(): Entry, taskFulfiller->{}, task->{}", taskFulfiller, task);

        PegacornTransactionMethodOutcome outcome = null;
        if(interZoneAuditEndpoint.auditServiceProviderIsInScope(taskFulfiller)){
            LOG.trace(".logAuditEvent(): Using inter-zone communication framework");
            outcome = interZoneAuditEndpoint.logAuditEvent(taskFulfiller, task);
        }
        if(intraZoneAuditEndpoint.auditServiceProviderIsInScope(taskFulfiller)){
            LOG.trace(".logAuditEvent(): Using intra-zone communication framework");
            outcome = intraZoneAuditEndpoint.logAuditEvent(taskFulfiller, task);
        }
        if(outcome == null) {
            LOG.trace(".logAuditEvent(): Can't find suitable capability provider");
        }
        LOG.debug(".logAuditEvent(): Exit, outcome->{}", outcome);
        return(outcome);
    }

    @Override
    public PegacornTransactionMethodOutcome logAuditEvent(String serviceProviderName, List<AuditEvent> auditEventList) {
        LOG.debug(".logAuditEvent(): Entry, taskFulfiller->{}, auditEventList->{}", serviceProviderName, auditEventList);

        PegacornTransactionMethodOutcome outcome = null;
        if(interZoneAuditEndpoint.auditServiceProviderIsInScope(serviceProviderName)){
            LOG.trace(".logAuditEvent(): Using inter-zone communication framework");
            outcome = interZoneAuditEndpoint.logAuditEvent(serviceProviderName, auditEventList);
        }
        if(intraZoneAuditEndpoint.auditServiceProviderIsInScope(serviceProviderName)){
            LOG.trace(".logAuditEvent(): Using intra-zone communication framework");
            outcome = intraZoneAuditEndpoint.logAuditEvent(serviceProviderName, auditEventList);
        }
        if(outcome == null) {
            LOG.trace(".logAuditEvent(): Can't find suitable capability provider");
        }
        LOG.debug(".logAuditEvent(): Exit, outcome->{}", outcome);
        return(outcome);
    }

}
