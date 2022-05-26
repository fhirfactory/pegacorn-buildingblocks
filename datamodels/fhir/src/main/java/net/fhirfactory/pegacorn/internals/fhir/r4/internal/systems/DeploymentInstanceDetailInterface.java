package net.fhirfactory.pegacorn.internals.fhir.r4.internal.systems;

import org.hl7.fhir.r4.model.Reference;

public interface DeploymentInstanceDetailInterface {
    public Reference getDeploymentInstanceOrganization();
    public Reference getDeploymentInstanceSystemOwnerOrganization();
    public Reference getDeploymentInstanceSystemOwnerPractitionerRole();
    public Reference getDeploymentInstanceSystemOwnerPractitioner();
    public Reference getDeploymentInstanceSystemAdministratorPractitionerRole();
    public Reference getDeploymentInstanceSystemAdministratorPractitioner();
    public String getDeploymentInstanceSystemEndpointSystem();
    public String getDeploymentInstanceOrganizationName();
    public String getDeploymentInstanceSystemOwnerContactName();
    public String getDeploymentInstanceSystemAdministratorContactName();
}
