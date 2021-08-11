package net.fhirfactory.pegacorn.core.deployment.endpoints.base;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCClusteredServerTopologyEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalFileShareIPCEndpoint extends IPCClusteredServerTopologyEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalFileShareIPCEndpoint.class);
    private String fileShareName;
    private String fileShareProtocol;
    private String fileSharePath;
    private String fileShareServer;

    public String getFileShareName() {
        return fileShareName;
    }

    public void setFileShareName(String fileShareName) {
        this.fileShareName = fileShareName;
    }

    public String getFileShareProtocol() {
        return fileShareProtocol;
    }

    public void setFileShareProtocol(String fileShareProtocol) {
        this.fileShareProtocol = fileShareProtocol;
    }

    public String getFileSharePath() {
        return fileSharePath;
    }

    public void setFileSharePath(String fileSharePath) {
        this.fileSharePath = fileSharePath;
    }

    public String getFileShareServer() {
        return fileShareServer;
    }

    public void setFileShareServer(String fileShareServer) {
        this.fileShareServer = fileShareServer;
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }
}
