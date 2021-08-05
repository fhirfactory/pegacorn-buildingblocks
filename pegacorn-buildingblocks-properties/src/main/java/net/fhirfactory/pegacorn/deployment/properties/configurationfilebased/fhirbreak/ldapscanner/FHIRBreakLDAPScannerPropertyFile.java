package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.fhirbreak.ldapscanner;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.interact.ClusterServiceInteractServerPortSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.fhirbreak.common.FHIRBreakSubsystemPropertyFile;

public class FHIRBreakLDAPScannerPropertyFile extends FHIRBreakSubsystemPropertyFile {

    private ClusterServiceInteractServerPortSegment ldapProxy;
    private ClusterServiceInteractServerPortSegment ldapServer;

    public ClusterServiceInteractServerPortSegment getLdapProxy() {
        return ldapProxy;
    }

    public void setLdapProxy(ClusterServiceInteractServerPortSegment ldapProxy) {
        this.ldapProxy = ldapProxy;
    }

    public ClusterServiceInteractServerPortSegment getLdapServer() {
        return ldapServer;
    }

    public void setLdapServer(ClusterServiceInteractServerPortSegment ldapServer) {
        this.ldapServer = ldapServer;
    }

}
