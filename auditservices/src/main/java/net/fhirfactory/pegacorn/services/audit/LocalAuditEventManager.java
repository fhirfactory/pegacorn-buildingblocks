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
package net.fhirfactory.pegacorn.services.audit;

import ca.uhn.fhir.rest.api.MethodOutcome;
import net.fhirfactory.pegacorn.core.model.petasos.audit.PetasosAuditWriterInterface;
import net.fhirfactory.pegacorn.services.audit.cache.AsynchronousWriterAuditEventCache;
import net.fhirfactory.pegacorn.services.audit.forwarder.beans.AuditEventPersistenceAccessor;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class LocalAuditEventManager implements PetasosAuditWriterInterface {
    private static final Logger LOG = LoggerFactory.getLogger(LocalAuditEventManager.class);

    private boolean initialised;
    private boolean writerScheduled;

    private static Long AUDIT_EVENT_ASYNCHRONOUS_WRITER_CHECK_PERIOD = 5000L;
    private static Long AUDIT_EVENT_ASYNCHRONOUS_WRITER_INITIALISE_WAIT = 30000L;

    @Inject
    private AuditEventPersistenceAccessor persistenceAccessor;

    @Inject
    private AsynchronousWriterAuditEventCache eventCache;

    //
    // Constructure
    //

    public LocalAuditEventManager(){
        this.initialised = false;
    }

    //
    // PostConstruct Activities (Initialisation)
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(isInitialised()){
            getLogger().debug(".initialise(): Already initialised, nothing to do!");
        } else {
            getLogger().info(".initialise(): Initialising...");
            getLogger().info(".initialise(): [Schedule Asynchronous Writer Task] Start");
            scheduleAsynchronousWriterTask();
            getLogger().info(".initialise(): [Schedule Asynchronous Writer Task] Finish");
            this.initialised = true;
            getLogger().info(".initialise(): Done...");
        }
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    public boolean isInitialised() {
        return initialised;
    }

    public boolean isWriterScheduled() {
        return writerScheduled;
    }

    public static Long getAuditEventAsynchronousWriterCheckPeriod() {
        return AUDIT_EVENT_ASYNCHRONOUS_WRITER_CHECK_PERIOD;
    }

    public static Long getAuditEventAsynchronousWriterInitialiseWait() {
        return AUDIT_EVENT_ASYNCHRONOUS_WRITER_INITIALISE_WAIT;
    }

    @Override
    public AuditEvent logAuditEventAsynchronously(AuditEvent auditEvent) {
        getLogger().debug(".logAuditEventAsynchronously(): Entry, auditEvent->{}", auditEvent);
        if(auditEvent == null){
            getLogger().debug(".logAuditEventAsynchronously(): Exit, auditEvent is null");
            return(null);
        }
        eventCache.addAuditEvent(auditEvent);
        getLogger().debug(".logAuditEventAsynchronously(): Exit, event cached for writing");
        return(auditEvent);
    }

    @Override
    public AuditEvent logAuditEventSynchronously(AuditEvent auditEvent) {
        getLogger().debug(".logAuditEventSynchronously(): Entry, auditEvent->{}", auditEvent);
        if(auditEvent == null){
            getLogger().debug(".logAuditEventSynchronously(): Exit, auditEvent is null");
            return(null);
        }
        MethodOutcome outcome = persistenceAccessor.utiliseAuditEventPersistenceCapability(auditEvent);
        IIdType id = outcome.getId();
        if(id != null){
            auditEvent.setId(id);
        }
        getLogger().debug(".logAuditEventSynchronously(): Exit, outcome->{}", outcome);
        return(auditEvent);
    }

    //
    // Asynchronous Writer Task
    //

    protected void scheduleAsynchronousWriterTask(){
        getLogger().debug(".scheduleITOpsBackgroundSynchronisationTask(): Entry");
        if(isWriterScheduled()){
            // do nothing
        } else {
            TimerTask AsynchronousAuditEventWriterTask = new TimerTask() {
                public void run() {
                    getLogger().debug(".ITOpsCacheSynchronisationCheck(): Entry");
                    writeQueuedAuditEvents();
                    getLogger().debug(".ITOpsCacheSynchronisationCheck(): Exit");
                }
            };
            String timerName = "AsynchronousAuditEventWriterTask";
            Timer timer = new Timer(timerName);
            timer.schedule(AsynchronousAuditEventWriterTask, getAuditEventAsynchronousWriterInitialiseWait(), getAuditEventAsynchronousWriterCheckPeriod());
            this.writerScheduled = true;
        }
        getLogger().debug(".scheduleITOpsBackgroundSynchronisationTask(): Exit");
    }

    // TODO We should batch up the writing of the events (i.e. support "n" events at a time)
    protected void writeQueuedAuditEvents(){
        while(eventCache.hasEntries()){
            AuditEvent currentAuditEvent = eventCache.peekAuditEvent();
            MethodOutcome outcome = persistenceAccessor.utiliseAuditEventPersistenceCapability(currentAuditEvent);
            //
            // TODO This next bit makes a wild assumption about the success of the writing action!
            //
            eventCache.pollAuditEvent();
        }
    }
}
