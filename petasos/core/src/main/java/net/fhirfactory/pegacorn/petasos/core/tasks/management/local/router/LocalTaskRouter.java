/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.core.tasks.management.local.router;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipant;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.PetasosFulfillmentTask;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosTaskExecutionStatusEnum;
import net.fhirfactory.pegacorn.deployment.properties.reference.petasos.PetasosDefaultProperties;
import net.fhirfactory.pegacorn.petasos.core.participants.manager.LocalParticipantManager;
import net.fhirfactory.pegacorn.petasos.core.tasks.accessors.*;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.daemon.DaemonBase;
import net.fhirfactory.pegacorn.petasos.core.tasks.management.local.synchronisation.TaskDataGridProxy;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@ApplicationScoped
public class LocalTaskRouter extends DaemonBase {
    private static final Logger LOG = LoggerFactory.getLogger(LocalTaskRouter.class);

    private Instant lastRouterDaemonRunInstant;
    private boolean taskRouterDaemonRunning;

    private boolean initialised;
    private static final Long LOCAL_TASK_ROUTER_DAEMON_STARTUP_DELAY = 60000L; // milliseconds
    private static final Long LOCAL_TASK_ROUTER_DAEMON_CHECK_PERIOD = 5000L; // milliseconds
    private static final Integer MAX_ITERATIONS_FOR_ROUTER_DAEMON_RUN = 5;

    @Inject
    private PetasosActionableTaskSharedInstanceAccessorFactory actionableTaskSharedInstanceAccessorFactory;

    @Inject
    private PetasosFulfillmentTaskSharedInstanceAccessorFactory fulfillmentTaskSharedInstanceAccessorFactory;

    @Inject
    private PetasosTaskJobCardSharedInstanceAccessorFactory taskJobCardSharedInstanceAccessorFactory;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private PetasosDefaultProperties petasosDefaultProperties;

    @Inject
    private LocalParticipantManager localParticipantManager;

    @Inject
    private TaskDataGridProxy taskDataGridProxy;

    @Produce
    private ProducerTemplate camelProducerService;

    //
    // Constructor(s)
    //

    public LocalTaskRouter(){
        this.initialised = false;
        this.taskRouterDaemonRunning = false;
        this.lastRouterDaemonRunInstant = Instant.EPOCH;
    }

    //
    // Post Construct
    //

    @PostConstruct
    public void initialise(){

    }

    //
    // Task Router
    //

    public void scheduleLocalTaskRouterDaemon() {
        getLogger().debug(".scheduleLocalTaskRouterDaemon(): Entry");
        TimerTask localTaskRouterDaemonTimerTask = new TimerTask() {
            public void run() {
                getLogger().debug(".localTaskRouterDaemonTimerTask(): Entry");
                if(isTaskRouterDaemonRunning()){
                    // do nothing
                } else {
                    localTaskRouterDaemon();
                }
                getLogger().debug(".localTaskRouterDaemonTimerTask(): Exit");
            }
        };
        Timer timer = new Timer("actionableTaskCleanupActivityTimer");
        timer.schedule(localTaskRouterDaemonTimerTask, LOCAL_TASK_ROUTER_DAEMON_STARTUP_DELAY, LOCAL_TASK_ROUTER_DAEMON_CHECK_PERIOD);
        getLogger().debug(".scheduleLocalTaskRouterDaemon(): Exit");
    }

    public void localTaskRouterDaemon(){
        getLogger().debug(".localTaskRouterDaemon(): Entry");
        setTaskRouterDaemonRunning(true);

        try{
            boolean hadTasks = true;
            int iterationCount = 0;
            Set<PetasosParticipant> localParticipantSet = getLocalParticipantManager().getLocalParticipantSet();
            while(hadTasks && iterationCount < MAX_ITERATIONS_FOR_ROUTER_DAEMON_RUN) {
                for (PetasosParticipant localParticipant : localParticipantSet) {
                    if (localParticipant.getParticipantStatus().equals(PetasosParticipantStatusEnum.PETASOS_PARTICIPANT_IDLE)) {
                        PetasosActionableTaskSharedInstance participantTask = getTaskDataGridProxy().loadNextTask(localParticipant.getParticipantName());
                        if (participantTask != null) {
                            hadTasks = true;
                            getCamelProducerService().sendBody(PetasosPropertyConstants.TASK_DISTRIBUTION_QUEUE, ExchangePattern.InOnly, participantTask);
                        }
                    }
                }
                iterationCount += 1;
            }


        } catch(Exception ex){
            getLogger().warn(".localTaskRouterDaemon(): An Issue with the Local Task Router -> ", ex);
        }
        setTaskRouterDaemonRunning(false);
        getLogger().debug(".localTaskRouterDaemon(): Exit");
    }

    //
    // Getters (and Setters)
    //

    public boolean isInitialised() {
        return initialised;
    }

    protected Logger getLogger(){
        return(LOG);
    }

    protected ProcessingPlantInterface getProcessingPlant(){
        return(this.processingPlant);
    }

    protected PetasosDefaultProperties getPetasosDefaultProperties(){
        return(this.petasosDefaultProperties);
    }

    protected LocalParticipantManager getLocalParticipantManager(){
        return(localParticipantManager);
    }

    public Instant getLastRouterDaemonRunInstant() {
        return lastRouterDaemonRunInstant;
    }

    public void setLastRouterDaemonRunInstant(Instant lastRouterDaemonRunInstant) {
        this.lastRouterDaemonRunInstant = lastRouterDaemonRunInstant;
    }

    public boolean isTaskRouterDaemonRunning() {
        return taskRouterDaemonRunning;
    }

    public void setTaskRouterDaemonRunning(boolean taskRouterDaemonRunning) {
        this.taskRouterDaemonRunning = taskRouterDaemonRunning;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    protected TaskDataGridProxy getTaskDataGridProxy(){
        return(taskDataGridProxy);
    }

    protected ProducerTemplate getCamelProducerService(){
        return(camelProducerService);
    }
}
