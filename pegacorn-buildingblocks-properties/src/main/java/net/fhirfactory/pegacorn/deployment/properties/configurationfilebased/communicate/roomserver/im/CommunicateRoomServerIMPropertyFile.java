package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.communicate.roomserver.im;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.standard.HTTPClusteredServiceServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.communicate.common.CommunicateSubsystemPropertyFile;

public abstract class CommunicateRoomServerIMPropertyFile extends CommunicateSubsystemPropertyFile {

    private HTTPClusteredServiceServerPortSegment matrixClientServicesAPI;

    public HTTPClusteredServiceServerPortSegment getMatrixClientServicesAPI() {
        return matrixClientServicesAPI;
    }

    public void setMatrixClientServicesAPI(HTTPClusteredServiceServerPortSegment matrixClientServicesAPI) {
        this.matrixClientServicesAPI = matrixClientServicesAPI;
    }
}
