package net.fhirfactory.pegacorn.internals.fhir.r4.internal.systems;

import org.hl7.fhir.r4.model.Reference;

abstract public class DeploymentInstanceDetail extends SourceSystem implements DeploymentInstanceDetailInterface{
    public DeploymentInstanceDetail(){
        super();
    }

    @Override
    public Reference getDeploymentInstanceOrganization(){
        return(getReferenceOrganization());
    }
    @Override
    public Reference getDeploymentInstanceSystemOwnerOrganization(){
        return(getReferenceSystemOwnerOrganization());
    }
    @Override
    public Reference getDeploymentInstanceSystemOwnerPractitionerRole(){
        return(getReferenceSystemOwnerPractitionerRole());
    }
    @Override
    public Reference getDeploymentInstanceSystemOwnerPractitioner(){
        return(getReferenceSystemOwnerPractitioner());
    }
    @Override
    public Reference getDeploymentInstanceSystemAdministratorPractitionerRole(){
        return(getReferenceSystemAdministratorPractitionerRole());
    }
    @Override
    public Reference getDeploymentInstanceSystemAdministratorPractitioner(){
        return(getReferenceSystemAdministratorPractitioner());
    }
    @Override
    public String getDeploymentInstanceOrganizationName(){
        return(getOrganizationName());
    }
    @Override
    public String getDeploymentInstanceSystemOwnerContactName(){
        return(getSystemOwnerContactName());
    }
    @Override
    public String getDeploymentInstanceSystemAdministratorContactName(){
        return(getSystemAdministratorContactName());
    }
    @Override
    public String getDeploymentInstanceSystemEndpointSystem(){
        return(getSystemReference());
    }
}
