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
package net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.factories;

import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.EndpointMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.ProcessingPlantMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.component.WorkUnitProcessorMetricsData;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.PetasosComponentMetric;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.PetasosComponentMetricSet;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.datatypes.PetasosComponentMetricValue;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.valuesets.PetasosComponentMetricTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.reporting.valuesets.PetasosComponentMetricUnitEnum;
import net.fhirfactory.pegacorn.core.model.petasos.participant.ProcessingPlantPetasosParticipantHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PetasosComponentMetricSetFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosComponentMetricSetFactory.class);

    @Inject
    private ProcessingPlantPetasosParticipantHolder participantHolder;

    //
    // Metric Conversion Functions
    //

    public PetasosComponentMetricSet convertProcessingPlantMetricsData(ProcessingPlantMetricsData plantMetricsData){
        getLogger().debug(".convertProcessingPlantMetricsData(): Entry, plantMetricsData->{}", plantMetricsData);

        if(plantMetricsData.getComponentID() == null){
            getLogger().debug(".convertProcessingPlantMetricsData(): Exit, plantMetricsData is null, returning -null-");
            return(null);
        }

        PetasosComponentMetricSet metricSet = new PetasosComponentMetricSet();
        metricSet.setMetricSourceComponentId(plantMetricsData.getComponentID());
        metricSet.setSourceParticipantName(plantMetricsData.getParticipantName());
        metricSet.setComponentType(plantMetricsData.getComponentType());

        if(plantMetricsData.getComponentStatus() != null){
            PetasosComponentMetric componentStatusMetric = new PetasosComponentMetric();
            componentStatusMetric.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            componentStatusMetric.setMetricSource(plantMetricsData.getComponentID());
            componentStatusMetric.setMetricName("ProcessingPlant-Status");
            componentStatusMetric.setMetricType(PetasosComponentMetricTypeEnum.COMPONENT_GENERAL_STATUS);
            componentStatusMetric.setMetricUnit(PetasosComponentMetricUnitEnum.STRING_DESCRIPTION);
            componentStatusMetric.setMetricValue(new PetasosComponentMetricValue(plantMetricsData.getComponentStatus()));
            metricSet.addMetric(componentStatusMetric);
        }

        if(plantMetricsData.getAsynchronousAuditEventsWritten() >= 0){
            PetasosComponentMetric auditEventsWritten = new PetasosComponentMetric();
            auditEventsWritten.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            auditEventsWritten.setMetricSource(plantMetricsData.getComponentID());
            auditEventsWritten.setMetricName("Async-AuditEvent-Writes");
            auditEventsWritten.setMetricType(PetasosComponentMetricTypeEnum.AUDIT_EVENT_WRITES);
            auditEventsWritten.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            auditEventsWritten.setMetricValue(new PetasosComponentMetricValue(plantMetricsData.getAsynchronousAuditEventsWritten()));
            metricSet.addMetric(auditEventsWritten);
        }

        if(plantMetricsData.getSynchronousAuditEventsWritten() >= 0){
            PetasosComponentMetric auditEventsWritten = new PetasosComponentMetric();
            auditEventsWritten.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            auditEventsWritten.setMetricSource(plantMetricsData.getComponentID());
            auditEventsWritten.setMetricName("Sync-AuditEvent-Writes");
            auditEventsWritten.setMetricType(PetasosComponentMetricTypeEnum.AUDIT_EVENT_WRITES);
            auditEventsWritten.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            auditEventsWritten.setMetricValue(new PetasosComponentMetricValue(plantMetricsData.getSynchronousAuditEventsWritten()));
            metricSet.addMetric(auditEventsWritten);
        }

        if(plantMetricsData.getAsynchronousAuditEventsQueued() >= 0){
            PetasosComponentMetric auditEventsWritten = new PetasosComponentMetric();
            auditEventsWritten.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            auditEventsWritten.setMetricSource(plantMetricsData.getComponentID());
            auditEventsWritten.setMetricName("Queued-AuditEvent-Writes");
            auditEventsWritten.setMetricType(PetasosComponentMetricTypeEnum.AUDIT_EVENT_WRITES);
            auditEventsWritten.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            auditEventsWritten.setMetricValue(new PetasosComponentMetricValue(plantMetricsData.getAsynchronousAuditEventsQueued()));
            metricSet.addMetric(auditEventsWritten);
        }

        if(plantMetricsData.getLastActivityInstant() != null){
            PetasosComponentMetric activityInstant = new PetasosComponentMetric();
            activityInstant.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            activityInstant.setMetricSource(plantMetricsData.getComponentID());
            activityInstant.setMetricName("Last-Activity-Instant");
            activityInstant.setMetricType(PetasosComponentMetricTypeEnum.ACTIVITY_TIMESTAMP);
            activityInstant.setMetricUnit(PetasosComponentMetricUnitEnum.TIME_INSTANT);
            activityInstant.setMetricValue(new PetasosComponentMetricValue(plantMetricsData.getLastActivityInstant()));
            metricSet.addMetric(activityInstant);
        }

        if(plantMetricsData.getComponentStartupInstant() != null){
            PetasosComponentMetric activityInstant = new PetasosComponentMetric();
            activityInstant.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            activityInstant.setMetricSource(plantMetricsData.getComponentID());
            activityInstant.setMetricName("Component-Startup-Instant");
            activityInstant.setMetricType(PetasosComponentMetricTypeEnum.ACTIVITY_TIMESTAMP);
            activityInstant.setMetricUnit(PetasosComponentMetricUnitEnum.TIME_INSTANT);
            activityInstant.setMetricValue(new PetasosComponentMetricValue(plantMetricsData.getComponentStartupInstant()));
            metricSet.addMetric(activityInstant);
        }

        if(!plantMetricsData.getLocalCacheStatusMap().isEmpty()){
            for(String currentCacheMetricName: plantMetricsData.getLocalCacheStatusMap().keySet()){
                PetasosComponentMetric componentStatusMetric = new PetasosComponentMetric();
                componentStatusMetric.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
                componentStatusMetric.setMetricSource(plantMetricsData.getComponentID());
                componentStatusMetric.setMetricName(currentCacheMetricName);
                componentStatusMetric.setMetricType(PetasosComponentMetricTypeEnum.CONTEXTUAL);
                componentStatusMetric.setMetricUnit(PetasosComponentMetricUnitEnum.STRING_DESCRIPTION);
                componentStatusMetric.setMetricValue(new PetasosComponentMetricValue(plantMetricsData.getLocalCacheStatusMap().get(currentCacheMetricName)));
                metricSet.addMetric(componentStatusMetric);
            }
        }

        if(plantMetricsData.getIngresMessageCount() > 0){
            PetasosComponentMetric ingresMessageCount = new PetasosComponentMetric();
            ingresMessageCount.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            ingresMessageCount.setMetricSource(plantMetricsData.getComponentID());
            ingresMessageCount.setMetricName("Ingres-Messages");
            ingresMessageCount.setMetricType(PetasosComponentMetricTypeEnum.MESSAGES_RECEIVED);
            ingresMessageCount.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            ingresMessageCount.setMetricValue(new PetasosComponentMetricValue(plantMetricsData.getIngresMessageCount()));
            metricSet.addMetric(ingresMessageCount);
        }

        getLogger().info(".convertProcessingPlantMetricsData(): [EgressMessageCount] Check");
        if(plantMetricsData.getEgressMessageCount() > 0){
            PetasosComponentMetric egressMessageCount = new PetasosComponentMetric();
            egressMessageCount.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            egressMessageCount.setMetricSource(plantMetricsData.getComponentID());
            egressMessageCount.setMetricName("Egress-Messages");
            egressMessageCount.setMetricType(PetasosComponentMetricTypeEnum.MESSAGES_FORWARDED);
            egressMessageCount.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            egressMessageCount.setMetricValue(new PetasosComponentMetricValue(plantMetricsData.getEgressMessageCount()));
            metricSet.addMetric(egressMessageCount);
        }
        getLogger().info(".convertProcessingPlantMetricsData(): [EgressMessageCount] Done...");
        getLogger().info(".convertProcessingPlantMetricsData(): [DistributionCountMap] Check");
        if(!plantMetricsData.getDistributionCountMap().isEmpty()){
            getLogger().info(".convertProcessingPlantMetricsData(): [Processing DistributionCountMap] Start");
            int counter = 0;
            for(String currentTargetName: plantMetricsData.getDistributionCountMap().keySet()){
                PetasosComponentMetric egressMessageCount = new PetasosComponentMetric();
                egressMessageCount.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
                egressMessageCount.setMetricSource(plantMetricsData.getComponentID());
                egressMessageCount.setMetricName("Messages-Forwarded["+currentTargetName+"]");
                egressMessageCount.setMetricType(PetasosComponentMetricTypeEnum.MESSAGES_FORWARDED);
                egressMessageCount.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
                Integer forwardedMessageCount = plantMetricsData.getDistributionCountMap().get(currentTargetName);
                egressMessageCount.setMetricValue(new PetasosComponentMetricValue(forwardedMessageCount));
                metricSet.addMetric(egressMessageCount);
                counter += 1;
            }
            getLogger().info(".convertProcessingPlantMetricsData(): [Processing DistributionCountMap] Finish (entries={})", counter);
        }
        getLogger().info(".convertProcessingPlantMetricsData(): [DistributionCountMap] Done...");

        getLogger().debug(".convertProcessingPlantMetricsData(): Exit, metricSet->{}", metricSet);
        return(metricSet);
    }

    public PetasosComponentMetricSet convertWorkUnitProcessorMetricsData(WorkUnitProcessorMetricsData wupMetricsData){
        getLogger().debug(".convertWorkUnitProcessorMetricsData(): Entry, wupMetricsData->{}", wupMetricsData);

        if(wupMetricsData == null){
            getLogger().debug(".convertWorkUnitProcessorMetricsData(): Exit, wupMetricsData is null, returning -null-");
            return(null);
        }

        if(wupMetricsData.getComponentID() == null){
            getLogger().debug(".convertWorkUnitProcessorMetricsData(): Exit, componentId is null, returning -null-");
            return(null);
        }

        if(wupMetricsData.getParticipantName() == null){
            getLogger().debug(".convertWorkUnitProcessorMetricsData(): Exit, participantName is null, returning -null-");
            return(null);
        }

        PetasosComponentMetricSet metricSet = new PetasosComponentMetricSet();
        metricSet.setMetricSourceComponentId(wupMetricsData.getComponentID());
        metricSet.setSourceParticipantName(wupMetricsData.getParticipantName());
        metricSet.setComponentType(wupMetricsData.getComponentType());metricSet.setSourceParticipantName(wupMetricsData.getParticipantName());

        if(!wupMetricsData.getDistributionCountMap().isEmpty()){
            for(String currentTarget: wupMetricsData.getDistributionCountMap().keySet()){
                PetasosComponentMetric forwardedMessageToParticipantCount = new PetasosComponentMetric();
                forwardedMessageToParticipantCount.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
                forwardedMessageToParticipantCount.setMetricSource(wupMetricsData.getComponentID());
                forwardedMessageToParticipantCount.setMetricName("Forwarded-Tasks-To:"+currentTarget);
                forwardedMessageToParticipantCount.setMetricType(PetasosComponentMetricTypeEnum.TASK_COUNT);
                forwardedMessageToParticipantCount.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
                forwardedMessageToParticipantCount.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getDistributionCountMap().get(currentTarget)));
                metricSet.addMetric(forwardedMessageToParticipantCount);
            }
        }

        if(wupMetricsData.getRegisteredTasks() >= 0){
            PetasosComponentMetric registeredTaskCount = new PetasosComponentMetric();
            registeredTaskCount.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            registeredTaskCount.setMetricSource(wupMetricsData.getComponentID());
            registeredTaskCount.setMetricName(PetasosComponentMetricTypeEnum.REGISTERED_TASK_COUNT.getDisplayName());
            registeredTaskCount.setMetricType(PetasosComponentMetricTypeEnum.REGISTERED_TASK_COUNT);
            registeredTaskCount.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            registeredTaskCount.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getRegisteredTasks()));
            metricSet.addMetric(registeredTaskCount);
        }

        if(wupMetricsData.getStartedTasks() >= 0){
            PetasosComponentMetric startedTasks = new PetasosComponentMetric();
            startedTasks.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            startedTasks.setMetricSource(wupMetricsData.getComponentID());
            startedTasks.setMetricName(PetasosComponentMetricTypeEnum.STARTED_TASK_COUNT.getDisplayName());
            startedTasks.setMetricType(PetasosComponentMetricTypeEnum.STARTED_TASK_COUNT);
            startedTasks.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            startedTasks.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getRegisteredTasks()));
            metricSet.addMetric(startedTasks);
        }

        if(wupMetricsData.getRollingEventProcessingDuration() > 0){
            PetasosComponentMetric rollingEventProcessingDuration = new PetasosComponentMetric();
            rollingEventProcessingDuration.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            rollingEventProcessingDuration.setMetricSource(wupMetricsData.getComponentID());
            rollingEventProcessingDuration.setMetricName(PetasosComponentMetricTypeEnum.ROLLING_TASK_PROCESSING_TIME.getDisplayName());
            rollingEventProcessingDuration.setMetricType(PetasosComponentMetricTypeEnum.ROLLING_TASK_PROCESSING_TIME);
            rollingEventProcessingDuration.setMetricUnit(PetasosComponentMetricUnitEnum.TIME_DURATION_MILLISECONDS);
            String roundedValue = String.format("%.3f", (wupMetricsData.getRollingEventProcessingDuration() + 0.005D)) +  " sec";
            rollingEventProcessingDuration.setMetricValue(new PetasosComponentMetricValue(roundedValue));
            metricSet.addMetric(rollingEventProcessingDuration);
        }

        if(wupMetricsData.getIngresMessageCount() > 0){
            PetasosComponentMetric ingresMessageCount = new PetasosComponentMetric();
            ingresMessageCount.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            ingresMessageCount.setMetricSource(wupMetricsData.getComponentID());
            ingresMessageCount.setMetricName(PetasosComponentMetricTypeEnum.MESSAGES_RECEIVED.getDisplayName());
            ingresMessageCount.setMetricType(PetasosComponentMetricTypeEnum.MESSAGES_RECEIVED);
            ingresMessageCount.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            ingresMessageCount.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getIngresMessageCount()));
            metricSet.addMetric(ingresMessageCount);
        }

        if(wupMetricsData.getEgressMessageCount() > 0){
            PetasosComponentMetric egressMessageCount = new PetasosComponentMetric();
            egressMessageCount.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            egressMessageCount.setMetricSource(wupMetricsData.getComponentID());
            egressMessageCount.setMetricName(PetasosComponentMetricTypeEnum.MESSAGES_FORWARDED.getDisplayName());
            egressMessageCount.setMetricType(PetasosComponentMetricTypeEnum.MESSAGES_FORWARDED);
            egressMessageCount.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            egressMessageCount.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getEgressMessageCount()));
            metricSet.addMetric(egressMessageCount);
        }

        if(wupMetricsData.getEventProcessingStartInstant() != null){
            PetasosComponentMetric activityInstant = new PetasosComponentMetric();
            activityInstant.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            activityInstant.setMetricSource(wupMetricsData.getComponentID());
            activityInstant.setMetricName("Last-Task-Start-Time");
            activityInstant.setMetricType(PetasosComponentMetricTypeEnum.ACTIVITY_TIMESTAMP);
            activityInstant.setMetricUnit(PetasosComponentMetricUnitEnum.TIME_INSTANT);
            activityInstant.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getEventProcessingStartInstant()));
            metricSet.addMetric(activityInstant);
        }

        if(wupMetricsData.getEventProcessingFinishInstant() != null){
            PetasosComponentMetric activityInstant = new PetasosComponentMetric();
            activityInstant.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            activityInstant.setMetricSource(wupMetricsData.getComponentID());
            activityInstant.setMetricName("Last-Task-Finish-Time");
            activityInstant.setMetricType(PetasosComponentMetricTypeEnum.ACTIVITY_TIMESTAMP);
            activityInstant.setMetricUnit(PetasosComponentMetricUnitEnum.TIME_INSTANT);
            activityInstant.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getEventProcessingFinishInstant()));
            metricSet.addMetric(activityInstant);
        }

        if(wupMetricsData.getAverageEventProcessingDuration() > 0){
            PetasosComponentMetric averageEventProcessingDuration = new PetasosComponentMetric();
            averageEventProcessingDuration.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            averageEventProcessingDuration.setMetricSource(wupMetricsData.getComponentID());
            averageEventProcessingDuration.setMetricName(PetasosComponentMetricTypeEnum.CUMULATIVE_TASK_PROCESSING_TIME.getDisplayName());
            averageEventProcessingDuration.setMetricType(PetasosComponentMetricTypeEnum.CUMULATIVE_TASK_PROCESSING_TIME);
            averageEventProcessingDuration.setMetricUnit(PetasosComponentMetricUnitEnum.TIME_DURATION_MILLISECONDS);
            String roundedValue = String.format("%.3f", (wupMetricsData.getAverageEventProcessingDuration() + 0.005D)) + " sec";
            averageEventProcessingDuration.setMetricValue(new PetasosComponentMetricValue(roundedValue));
            metricSet.addMetric(averageEventProcessingDuration);
        }

        if(wupMetricsData.getCancelledTasks() >= 0){
            PetasosComponentMetric cancelledTaskCount = new PetasosComponentMetric();
            cancelledTaskCount.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            cancelledTaskCount.setMetricSource(wupMetricsData.getComponentID());
            cancelledTaskCount.setMetricName(PetasosComponentMetricTypeEnum.CANCELLED_TASK_COUNT.getDisplayName());
            cancelledTaskCount.setMetricType(PetasosComponentMetricTypeEnum.CANCELLED_TASK_COUNT);
            cancelledTaskCount.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            cancelledTaskCount.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getCancelledTasks()));
            metricSet.addMetric(cancelledTaskCount);
        }

        if(wupMetricsData.getFinishedTasks() >= 0){
            PetasosComponentMetric finishedTaskCount = new PetasosComponentMetric();
            finishedTaskCount.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            finishedTaskCount.setMetricSource(wupMetricsData.getComponentID());
            finishedTaskCount.setMetricName(PetasosComponentMetricTypeEnum.FINISHED_TASK_COUNT.getDisplayName());
            finishedTaskCount.setMetricType(PetasosComponentMetricTypeEnum.FINISHED_TASK_COUNT);
            finishedTaskCount.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            finishedTaskCount.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getFinishedTasks()));
            metricSet.addMetric(finishedTaskCount);
        }

        if(wupMetricsData.getFinalisedTasks() >= 0){
            PetasosComponentMetric finalisedTasks = new PetasosComponentMetric();
            finalisedTasks.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            finalisedTasks.setMetricSource(wupMetricsData.getComponentID());
            finalisedTasks.setMetricName(PetasosComponentMetricTypeEnum.FINALISED_TASK_COUNT.getDisplayName());
            finalisedTasks.setMetricType(PetasosComponentMetricTypeEnum.FINALISED_TASK_COUNT);
            finalisedTasks.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            finalisedTasks.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getFinalisedTasks()));
            metricSet.addMetric(finalisedTasks);
        }

        if(wupMetricsData.getFailedTasks() >= 0){
            PetasosComponentMetric finalisedTasks = new PetasosComponentMetric();
            finalisedTasks.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            finalisedTasks.setMetricSource(wupMetricsData.getComponentID());
            finalisedTasks.setMetricName(PetasosComponentMetricTypeEnum.FAILED_TASK_COUNT.getDisplayName());
            finalisedTasks.setMetricType(PetasosComponentMetricTypeEnum.FAILED_TASK_COUNT);
            finalisedTasks.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            finalisedTasks.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getFailedTasks()));
            metricSet.addMetric(finalisedTasks);
        }

        if(wupMetricsData.getCurrentActionableTask() != null){
            PetasosComponentMetric currentActionalTask = new PetasosComponentMetric();
            currentActionalTask.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            currentActionalTask.setMetricSource(wupMetricsData.getComponentID());
            currentActionalTask.setMetricName("Current-Task");
            currentActionalTask.setMetricType(PetasosComponentMetricTypeEnum.CONTEXTUAL);
            currentActionalTask.setMetricUnit(PetasosComponentMetricUnitEnum.STRING_DESCRIPTION);
            currentActionalTask.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getCurrentActionableTask().toString()));
            metricSet.addMetric(currentActionalTask);
        }

        if(wupMetricsData.getLastActionableTask() != null){
            PetasosComponentMetric lastActionalTask = new PetasosComponentMetric();
            lastActionalTask.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            lastActionalTask.setMetricSource(wupMetricsData.getComponentID());
            lastActionalTask.setMetricName(PetasosComponentMetricTypeEnum.LAST_TASK_ID.getDisplayName());
            lastActionalTask.setMetricType(PetasosComponentMetricTypeEnum.LAST_TASK_ID);
            lastActionalTask.setMetricUnit(PetasosComponentMetricUnitEnum.STRING_DESCRIPTION);
            lastActionalTask.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getLastActionableTask().toString()));
            metricSet.addMetric(lastActionalTask);
        }

        if(wupMetricsData.getLastActivityInstant() != null){
            PetasosComponentMetric activityInstant = new PetasosComponentMetric();
            activityInstant.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            activityInstant.setMetricSource(wupMetricsData.getComponentID());
            activityInstant.setMetricName(PetasosComponentMetricTypeEnum.LAST_TASK_INSTANT.getDisplayName());
            activityInstant.setMetricType(PetasosComponentMetricTypeEnum.LAST_TASK_INSTANT);
            activityInstant.setMetricUnit(PetasosComponentMetricUnitEnum.TIME_INSTANT);
            activityInstant.setMetricValue(new PetasosComponentMetricValue(wupMetricsData.getLastActivityInstant()));
            metricSet.addMetric(activityInstant);
        }

        if(wupMetricsData.getLastEventProcessingDuration() > 0){
            PetasosComponentMetric lastEventProcessingDuration = new PetasosComponentMetric();
            lastEventProcessingDuration.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            lastEventProcessingDuration.setMetricSource(wupMetricsData.getComponentID());
            lastEventProcessingDuration.setMetricName(PetasosComponentMetricTypeEnum.LAST_TASK_PROCESSING_TIME.getDisplayName());
            lastEventProcessingDuration.setMetricType(PetasosComponentMetricTypeEnum.LAST_TASK_PROCESSING_TIME);
            lastEventProcessingDuration.setMetricUnit(PetasosComponentMetricUnitEnum.TIME_DURATION_MILLISECONDS);
            String roundedValue = String.format("%.3f", (wupMetricsData.getLastEventProcessingDuration() + 0.005D)) + " sec";
            lastEventProcessingDuration.setMetricValue(new PetasosComponentMetricValue(roundedValue));
            metricSet.addMetric(lastEventProcessingDuration);
        }

        return(metricSet);
    }

    public PetasosComponentMetricSet convertEndpointMetricsData(EndpointMetricsData endpointMetricsData){
        getLogger().debug(".convertEndpointMetricsData(): Entry, endpointMetricsData->{}", endpointMetricsData);

        if(endpointMetricsData.getComponentID() == null){
            getLogger().debug(".convertEndpointMetricsData(): Exit, endpointMetricsData is null, returning -null-");
            return(null);
        }

        PetasosComponentMetricSet metricSet = new PetasosComponentMetricSet();
        metricSet.setMetricSourceComponentId(endpointMetricsData.getComponentID());
        metricSet.setSourceParticipantName(endpointMetricsData.getParticipantName());
        metricSet.setComponentType(endpointMetricsData.getComponentType());

        if (endpointMetricsData.getEgressSendAttemptCount() > 0) {
            PetasosComponentMetric componentStatusMetric = new PetasosComponentMetric();
            componentStatusMetric.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            componentStatusMetric.setMetricSource(endpointMetricsData.getComponentID());
            componentStatusMetric.setMetricName("Egress-Send-Attempt-Count");
            componentStatusMetric.setMetricType(PetasosComponentMetricTypeEnum.TASK_COUNT);
            componentStatusMetric.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            componentStatusMetric.setMetricValue(new PetasosComponentMetricValue(endpointMetricsData.getEgressSendAttemptCount()));
            metricSet.addMetric(componentStatusMetric);
        }

        if(endpointMetricsData.getComponentStatus() != null){
            PetasosComponentMetric componentStatusMetric = new PetasosComponentMetric();
            componentStatusMetric.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            componentStatusMetric.setMetricSource(endpointMetricsData.getComponentID());
            componentStatusMetric.setMetricName("ProcessingPlant-Status");
            componentStatusMetric.setMetricType(PetasosComponentMetricTypeEnum.COMPONENT_GENERAL_STATUS);
            componentStatusMetric.setMetricUnit(PetasosComponentMetricUnitEnum.STRING_DESCRIPTION);
            componentStatusMetric.setMetricValue(new PetasosComponentMetricValue(endpointMetricsData.getComponentStatus()));
            metricSet.addMetric(componentStatusMetric);
        }

        if(endpointMetricsData.getLastActivityInstant() != null){
            PetasosComponentMetric activityInstant = new PetasosComponentMetric();
            activityInstant.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            activityInstant.setMetricSource(endpointMetricsData.getComponentID());
            activityInstant.setMetricName("Last-Activity-Instant");
            activityInstant.setMetricType(PetasosComponentMetricTypeEnum.ACTIVITY_TIMESTAMP);
            activityInstant.setMetricUnit(PetasosComponentMetricUnitEnum.TIME_INSTANT);
            activityInstant.setMetricValue(new PetasosComponentMetricValue(endpointMetricsData.getLastActivityInstant()));
            metricSet.addMetric(activityInstant);
        }

        if(endpointMetricsData.getComponentStartupInstant() != null){
            PetasosComponentMetric activityInstant = new PetasosComponentMetric();
            activityInstant.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            activityInstant.setMetricSource(endpointMetricsData.getComponentID());
            activityInstant.setMetricName("Component-Startup-Instant");
            activityInstant.setMetricType(PetasosComponentMetricTypeEnum.ACTIVITY_TIMESTAMP);
            activityInstant.setMetricUnit(PetasosComponentMetricUnitEnum.TIME_INSTANT);
            activityInstant.setMetricValue(new PetasosComponentMetricValue(endpointMetricsData.getComponentStartupInstant()));
            metricSet.addMetric(activityInstant);
        }

        if(endpointMetricsData.getIngresMessageCount() > 0){
            PetasosComponentMetric ingresMessageCount = new PetasosComponentMetric();
            ingresMessageCount.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            ingresMessageCount.setMetricSource(endpointMetricsData.getComponentID());
            ingresMessageCount.setMetricName(PetasosComponentMetricTypeEnum.MESSAGES_RECEIVED.getDisplayName());
            ingresMessageCount.setMetricType(PetasosComponentMetricTypeEnum.MESSAGES_RECEIVED);
            ingresMessageCount.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            ingresMessageCount.setMetricValue(new PetasosComponentMetricValue(endpointMetricsData.getIngresMessageCount()));
            metricSet.addMetric(ingresMessageCount);
        }

        if(endpointMetricsData.getEgressMessageCount() > 0){
            PetasosComponentMetric egressMessageCount = new PetasosComponentMetric();
            egressMessageCount.setMetricAgent(participantHolder.getMyProcessingPlantPetasosParticipant().getComponentID());
            egressMessageCount.setMetricSource(endpointMetricsData.getComponentID());
            egressMessageCount.setMetricName(PetasosComponentMetricTypeEnum.MESSAGES_FORWARDED.getDisplayName());
            egressMessageCount.setMetricType(PetasosComponentMetricTypeEnum.MESSAGES_FORWARDED);
            egressMessageCount.setMetricUnit(PetasosComponentMetricUnitEnum.INTEGER_COUNT);
            egressMessageCount.setMetricValue(new PetasosComponentMetricValue(endpointMetricsData.getEgressMessageCount()));
            metricSet.addMetric(egressMessageCount);
        }

        getLogger().debug(".convertEndpointMetricsData(): Exit, metricSet->{}", metricSet);
        return(metricSet);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
