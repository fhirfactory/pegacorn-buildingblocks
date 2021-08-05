package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.standard;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.base.StandardClusterServiceServerPortSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusteredServiceServerPortSegment extends StandardClusterServiceServerPortSegment {
    private static Logger LOG = LoggerFactory.getLogger(ClusteredServiceServerPortSegment.class);

    public ClusteredServiceServerPortSegment(){
        super();
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }
}
