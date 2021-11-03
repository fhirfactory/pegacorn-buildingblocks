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
package net.fhirfactory.pegacorn.services.tasks;

import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationRequest;
import net.fhirfactory.pegacorn.core.model.capabilities.base.CapabilityUtilisationResponse;
import net.fhirfactory.pegacorn.core.model.capabilities.base.factories.MethodOutcomeFactory;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosActionableTask;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosTaskPersistenceService;
import net.fhirfactory.pegacorn.petasos.endpoints.CapabilityUtilisationBroker;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.hl7.fhir.r4.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.util.UUID;

public class TaskPersistenceServiceBroker implements PetasosTaskPersistenceService {
    private static final Logger LOG = LoggerFactory.getLogger(TaskPersistenceServiceBroker.class);

    private boolean initialised;
    private ObjectMapper jsonMapper;
    private IParser fhirParser;

    // ***********************************************************************************
    //
    // W A R N I N G: Tactical Solution for Short-Term Integration Support
    //
    private static String TASK_PERSISTENCE_CAPABILITY_PROVIDER = "aether-hestia-task-im";
    //
    // ***********************************************************************************

    @Inject
    private FHIRContextUtility fhirContextUtility;

    @Inject
    private CapabilityUtilisationBroker capabilityUtilisationBroker;

    @Inject
    private MethodOutcomeFactory outcomeFactory;

    @Override
    public PetasosActionableTask persistPetasosActionableTask(PetasosActionableTask task) {
        return null;
    }

    //
    // Constructor
    //

    public TaskPersistenceServiceBroker() {
        this.initialised = false;
        jsonMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        jsonMapper.registerModule(module);
    }

    //
    // Post Construct
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(isInitialised()){
            getLogger().debug(".initialise(): Exit, already initialised, nothing to do!");
            return;
        } else {
            getLogger().info(".initialise(): Initialising...");
            this.fhirParser = fhirContextUtility.getJsonParser();
            getLogger().info(".initialise(): Done...");
            setInitialised(true);
            getLogger().debug("initialise(): Exit");
        }
    }

    //
    // Business Methods
    //

    public synchronized MethodOutcome utiliseAuditEventPersistenceCapability(Task actionableTask) {
        getLogger().debug(".utiliseAuditEventPersistenceCapability(): Entry, actionableTask --> {}", actionableTask);
        //
        // Build Write
        //
        String actionableTaskString = getFHIRParser().encodeResourceToString(actionableTask);
        CapabilityUtilisationRequest task = new CapabilityUtilisationRequest();
        task.setRequestID(UUID.randomUUID().toString());
        task.setRequestContent(actionableTaskString);
        task.setRequiredCapabilityName("FHIR-Task-Persistence");
        task.setRequestInstant(Instant.now());
        //
        // Do Write
        //
        CapabilityUtilisationResponse auditEventWriteOutcome = capabilityUtilisationBroker.executeTask(TASK_PERSISTENCE_CAPABILITY_PROVIDER, task);
        //
        // Extract the response
        //
        String resultString = auditEventWriteOutcome.getResponseStringContent();
        MethodOutcome methodOutcome = outcomeFactory.convertToMethodOutcome(resultString);
        getLogger().debug(".utiliseAuditEventPersistenceCapability(): Entry, methodOutcome --> {}", methodOutcome);
        return (methodOutcome);
    }

    //
    // Getters (and Setters)
    //

    protected boolean isInitialised() {
        return initialised;
    }

    protected void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    protected IParser getFHIRParser(){
        return(fhirParser);
    }

    protected Logger getLogger(){
        return(LOG);
    }
}
