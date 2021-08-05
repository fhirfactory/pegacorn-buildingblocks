package net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.interfaces;

import net.fhirfactory.pegacorn.deployment.topology.model.nodes.SolutionTopologyNode;

public interface SolutionNodeFactoryInterface {
    public void initialise();
    public SolutionTopologyNode getSolutionTopologyNode();
}
