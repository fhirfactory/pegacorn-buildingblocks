package net.fhirfactory.pegacorn.components.interfaces.topology;

import net.fhirfactory.pegacorn.deployment.properties.codebased.DeploymentSystemIdentificationInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.SolutionTopologyNode;

public interface SolutionInterface extends DeploymentSystemIdentificationInterface {
    public void initialiseSolutionTopology();
    public SolutionTopologyNode getSolutionNode();
}
