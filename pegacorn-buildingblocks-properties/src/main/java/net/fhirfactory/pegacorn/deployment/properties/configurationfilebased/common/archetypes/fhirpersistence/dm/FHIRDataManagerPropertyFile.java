package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.fhirpersistence.dm;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.BaseSubsystemPropertyFile;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.standard.HTTPClusteredServiceServerPortSegment;

public abstract class FHIRDataManagerPropertyFile extends BaseSubsystemPropertyFile {
    HTTPClusteredServiceServerPortSegment fhirJPAServerPort;

    public HTTPClusteredServiceServerPortSegment getFHIRJPAServerPort() {
        return fhirJPAServerPort;
    }

    public void getFHIRJPAServerPort(HTTPClusteredServiceServerPortSegment edgeAnswer) {
        this.fhirJPAServerPort = edgeAnswer;
    }
}
