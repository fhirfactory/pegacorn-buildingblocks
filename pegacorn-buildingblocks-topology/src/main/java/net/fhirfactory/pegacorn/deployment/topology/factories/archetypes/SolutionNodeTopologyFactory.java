/*
 * The MIT License
 *
 * Copyright 2020 Mark A. Hunter.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.fhirfactory.pegacorn.deployment.topology.factories.archetypes;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.interfaces.SolutionNodeFactoryInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.SolutionTopologyNode;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;

/**
 *
 * @author Mark A Hunter
 *
 */
public abstract class SolutionNodeTopologyFactory implements SolutionNodeFactoryInterface {

    private boolean initialised;
    private SolutionTopologyNode solutionTopologyNode;

    //
    // Default Constructor
    //

    public SolutionNodeTopologyFactory(){
        this.initialised = false;
    }

    //
    // Abstract Methods
    //

    abstract protected String specifySystemName();
    abstract protected String specifySystemVersion();
    abstract protected Logger specifyLogger();

    //
    // Post Construct Initialiser
    //

    @PostConstruct
    public void initialise(){
        if(!isInitialised()) {
            getLogger().debug(".initialise(): Entry");
            getLogger().debug(".initialise(): buildSolutionNode() --> Start");
            this.solutionTopologyNode = newSolutionNode();
            getLogger().debug(".initialise(): buildSolutionNode() --> Finish");
            setInitialised(true);
        }
    }

    //
    // Getters (and Setters)
    //

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    @Override
    public SolutionTopologyNode getSolutionTopologyNode() {
        return solutionTopologyNode;
    }

    public Logger getLogger(){
        return(specifyLogger());
    }

    /**
     * Solution Node Builder
     *
     * @param
     * @return
     */
    public SolutionTopologyNode newSolutionNode(){
        getLogger().debug(".buildSolutionNode(): Entry");
        SolutionTopologyNode solution = new SolutionTopologyNode();
        TopologyNodeRDN nodeRDN = new TopologyNodeRDN(TopologyNodeTypeEnum.SOLUTION, specifySystemName(), specifySystemVersion() );
        solution.constructFDN(null, nodeRDN);
        solution.constructFunctionFDN(null,nodeRDN );
        solution.setNodeRDN(nodeRDN);
        solution.setComponentType(TopologyNodeTypeEnum.SOLUTION);
        getLogger().debug(".buildSolutionNode(): Exit, solution ->{}", solution);
        return(solution);
    }



}
