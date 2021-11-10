package net.fhirfactory.pegacorn.core.interfaces.topology;

import net.fhirfactory.pegacorn.core.constants.systemwide.DeploymentSystemIdentificationInterface;
import net.fhirfactory.pegacorn.core.model.topology.nodes.SolutionTopologyNode;

public interface SolutionInterface extends DeploymentSystemIdentificationInterface {
    public void initialiseSolutionTopology();
    public SolutionTopologyNode getSolutionNode();
}
