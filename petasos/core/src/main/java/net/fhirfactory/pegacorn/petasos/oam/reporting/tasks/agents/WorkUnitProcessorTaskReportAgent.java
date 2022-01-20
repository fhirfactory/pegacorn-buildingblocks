package net.fhirfactory.pegacorn.petasos.oam.reporting.tasks.agents;

import net.fhirfactory.pegacorn.core.interfaces.oam.tasks.PetasosITOpsTaskReportingAgentInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.petasos.oam.topology.valuesets.PetasosMonitoredComponentTypeEnum;
import net.fhirfactory.pegacorn.petasos.oam.reporting.tasks.PetasosTaskReportFactory;

public class WorkUnitProcessorTaskReportAgent extends TaskReportAgentBase{

    public WorkUnitProcessorTaskReportAgent(String participantName, ComponentIdType componentId, PetasosTaskReportFactory factory, PetasosITOpsTaskReportingAgentInterface taskReportingAgent) {
        super(participantName, componentId, factory, taskReportingAgent);
    }

    @Override
    protected PetasosMonitoredComponentTypeEnum specifyComponentType() {
        return PetasosMonitoredComponentTypeEnum.PETASOS_MONITORED_COMPONENT_WORK_UNIT_PROCESSOR;
    }
}
