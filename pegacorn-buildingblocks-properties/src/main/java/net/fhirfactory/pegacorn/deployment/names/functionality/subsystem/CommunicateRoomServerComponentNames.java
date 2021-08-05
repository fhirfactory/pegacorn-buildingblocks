package net.fhirfactory.pegacorn.deployment.names.functionality.subsystem;

import net.fhirfactory.pegacorn.deployment.names.functionality.base.PegacornCommonInterfaceNamesBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CommunicateRoomServerComponentNames extends PegacornCommonInterfaceNamesBase {
    public String getFunctionNameMatrixClientServices(){return("Matrix-ClientServices");}

    public String getFunctionNameDBaaSPostgreSQL(){
        return("DBaaS-PostgreSQL-Server");
    }
}
