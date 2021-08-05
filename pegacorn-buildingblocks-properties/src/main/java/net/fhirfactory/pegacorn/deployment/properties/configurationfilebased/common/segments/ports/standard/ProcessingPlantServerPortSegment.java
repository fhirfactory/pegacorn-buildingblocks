package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.standard;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.base.StandardServerPortSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessingPlantServerPortSegment extends StandardServerPortSegment {
    private static Logger LOG = LoggerFactory.getLogger(ProcessingPlantServerPortSegment.class);

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    public ProcessingPlantServerPortSegment(){
        super();
    }
}
