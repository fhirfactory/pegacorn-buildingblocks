package net.fhirfactory.pegacorn.core.model.petasos.oam.metrics.jmx;

import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.status.valuesets.ActionableTaskOutcomeStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosTaskExecutionStatusEnum;

import java.time.Instant;

public interface ProcessingPlantMBean {
    public Integer getActionableTaskCacheSize();
    public Instant getActivityIndicator();
    public String getOperationalStatus();
    public String getExecutionStatus();
}
