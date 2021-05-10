/*
 * Copyright (c) 2020 Mark A. Hunter
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
package net.fhirfactory.pegacorn.services.audit.logger;

import ca.uhn.fhir.parser.IParser;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.FHIRElementTopicIDBuilder;
import net.fhirfactory.pegacorn.services.audit.logger.base.AuditEntryLoggerServiceBase;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TransactionAuditEntryLoggerService extends AuditEntryLoggerServiceBase {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionAuditEntryLoggerService.class);

    private static final String FHIR_VERSION = "4.0.1";

    @Override
    protected Logger specifyLogger(){return(LOG);}
    
    @Inject 
    private FHIRContextUtility FHIRContextUtility;
    
    private IParser parserR4;

    @Inject
    private FHIRElementTopicIDBuilder topicIDBuilder;


    @PostConstruct
    protected void initialise() {
        LOG.debug(".initialise(): Entry");
        this.parserR4 = FHIRContextUtility.getJsonParser();
        LOG.debug(".initialise(): Exit");
    }

    public AuditEvent beginTransaction(AuditEvent rawAuditEvent) {
        LOG.debug(".beginTransaction(): Entry, rawAuditEvent->{}",rawAuditEvent);
        // Do some magic
        return (rawAuditEvent);
    }

    public void endTransaction(AuditEvent rawAuditEvent) {
        LOG.debug(".endTransaction(): Entry, rawAuditEvent->{}",rawAuditEvent);
        // Do some magic
        return;
    }
}
