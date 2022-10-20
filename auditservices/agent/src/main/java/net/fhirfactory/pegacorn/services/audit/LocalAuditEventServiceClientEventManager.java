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

import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventServiceAgentInterface;
import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventServiceBrokerInterface;
import net.fhirfactory.pegacorn.core.interfaces.auditing.PetasosAuditEventServiceProviderNameInterface;
import net.fhirfactory.pegacorn.services.audit.cache.AsynchronousWriterAuditEventCache;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.hl7.fhir.r4.model.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class LocalAuditEventServiceClientEventManager implements PetasosAuditEventServiceAgentInterface {
    private static final Logger LOG = LoggerFactory.getLogger(LocalAuditEventServiceClientEventManager.class);

    private boolean initialised;
    private boolean writerScheduled;
    private boolean lastAsynchronousWriteWasSuccessful;

    @Inject
    private FHIRContextUtility fhirContextUtility;

    private static Long AUDIT_EVENT_ASYNCHRONOUS_WRITER_CHECK_PERIOD = 5000L;
    private static Long AUDIT_EVENT_ASYNCHRONOUS_WRITER_INITIALISE_WAIT = 30000L;
    private static int MAX_ASYNCHRONOUS_LIST_SIZE = 500;

    @Inject
    private PetasosAuditEventServiceBrokerInterface auditEventService;

    @Inject
    private AsynchronousWriterAuditEventCache eventCache;

    @Inject
    private PetasosAuditEventServiceProviderNameInterface auditEventServiceProvider;

    //
    // ConstructorPetasosAuditEventServiceClientWriterInterface
    //

    public LocalAuditEventServiceClientEventManager(){
        this.initialised = false;
        this.setLastAsynchronousWriteWasSuccessful(true);
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

    public boolean isLastAsynchronousWriteWasSuccessful() {
        return lastAsynchronousWriteWasSuccessful;
    }

    public void setLastAsynchronousWriteWasSuccessful(boolean lastAsynchronousWriteWasSuccessful) {
        this.lastAsynchronousWriteWasSuccessful = lastAsynchronousWriteWasSuccessful;
    }

    protected FHIRContextUtility getFhirContextUtility(){
        return(fhirContextUtility);
    }

    //
    // Audit Event Service
    //

    @Override
    public Boolean captureAuditEvent(AuditEvent auditEvent, boolean synchronous) {
        getLogger().info(".captureAuditEvent(): Entry, auditEvent->{}, synchronous->{}", auditEvent, synchronous);
        if(auditEvent == null){
            getLogger().debug(".captureAuditEvent(): Exit, auditEvent is null");
            return(false);
        }
        Boolean success = false;
        if(synchronous){
            try {
                success = auditEventService.logAuditEvent(auditEventServiceProvider.getPetasosAuditEventServiceProviderName(), auditEvent);
            } catch(Exception ex){
                getLogger().warn(".writeQueuedAuditEvents(): Cannot write audit event, will print AuditEvent here, Message->{}, StackTrace->{}", ex.getCause(), ex.getStackTrace());
                printAuditEvent(auditEvent);
            }
        } else {
            addToWriteQueue(auditEvent);
            success = true;
        }
        getLogger().info(".captureAuditEvent(): Exit, success->{}", success);
        return(success);
    }

    protected void addToWriteQueue(AuditEvent auditEvent){
        getLogger().info(".addToWriteQueue(): Entry, auditEvent->{}", auditEvent);
        if(auditEvent == null){
            getLogger().debug(".addToWriteQueue(): Exit, auditEvent is null");
            return;
        }
        if(eventCache.getNumberOfEntries() > MAX_ASYNCHRONOUS_LIST_SIZE){
            getLogger().warn(".addToWriteQueue(): Queue is full, cannot add new AuditEvent, Printing Here!");
            printAuditEvent(auditEvent);
        } else {
            eventCache.addAuditEvent(auditEvent);
        }
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
        getLogger().debug(".writeQueuedAuditEvents(): Entry");
        boolean thisWriteWasSuccessful = false;
        try {
            while (eventCache.hasEntries()) {
                List<AuditEvent> auditEventList = new ArrayList<>();
                int count = 0;
                while (eventCache.hasEntries()) {
                    AuditEvent currentAuditEvent = eventCache.pollAuditEvent();
                    auditEventList.add(currentAuditEvent);
                    count += 1;
                    if (count >= MAX_ASYNCHRONOUS_LIST_SIZE) {
                        break;
                    }
                }
                Boolean success = false;
                if (!auditEventList.isEmpty()) {
                    success = auditEventService.logAuditEvent(auditEventServiceProvider.getPetasosAuditEventServiceProviderName(), auditEventList);
                }
                if (!success) {
                    for (AuditEvent currentAuditEvent : auditEventList) {
                        eventCache.addAuditEvent(currentAuditEvent);
                    }
                    break;
                } else {
                    thisWriteWasSuccessful = true;
                }
            }
        } catch( Exception ex ){
            getLogger().warn(".writeQueuedAuditEvents(): Cannot write queued audit events, Message->{}, StackTrace->{}", ex.getCause(), ex.getStackTrace());
        }
        if(!thisWriteWasSuccessful && !isLastAsynchronousWriteWasSuccessful()){
            try {
                if (eventCache.hasEntries()) {
                    List<AuditEvent> auditEventList = new ArrayList<>();
                    while (eventCache.hasEntries()) {
                        AuditEvent currentAuditEvent = eventCache.pollAuditEvent();
                        auditEventList.add(currentAuditEvent);
                    }
                    for(AuditEvent currentAuditEvent: auditEventList){
                        printAuditEvent(currentAuditEvent);
                    }
                }
            } catch (Exception ex){

            }
        } else {
            setLastAsynchronousWriteWasSuccessful(thisWriteWasSuccessful);
        }
        getLogger().debug(".writeQueuedAuditEvents(): Exit");
    }

    protected void printAuditEvent(AuditEvent auditEvent){
        String auditEventAsString = null;
        try {
            auditEventAsString = getFhirContextUtility().getJsonParser().encodeResourceToString(auditEvent);
        } catch( Exception ex){
            getLogger().warn(".printAuditEvent(): Cannot convert AuditEvent to String, error->{}", ex.getMessage());
            auditEventAsString = "Unprintable";
        }
        getLogger().warn(".printAuditEvent(): AuditEvent->{}", auditEventAsString);
    }
}
