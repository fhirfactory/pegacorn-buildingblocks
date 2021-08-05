package net.fhirfactory.pegacorn.deployment.names.functionality.subsystem.common;

import net.fhirfactory.pegacorn.deployment.names.functionality.base.PegacornCommonInterfaceNamesBase;

public class FHIRDBaaSSubsystemComponentNames extends PegacornCommonInterfaceNamesBase {
    public String getFunctionNameDBaaSPostgreSQL(){
        return("DBaaS-PostgreSQL-Server");
    }
    public String getFunctionNameFHIRJPAServer(){return("FHIR-JPA-Server-All");}
}
