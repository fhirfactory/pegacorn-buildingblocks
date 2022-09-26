/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.core.model.topology.nodes;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class PlatformTopologyNode extends SoftwareComponent {
    private static final Logger LOG = LoggerFactory.getLogger(PlatformTopologyNode.class);

    private ArrayList<ComponentIdType> processingPlants;
    private Integer instanceCount;
    private String actualHostIP;
    private String actualPodIP;

    //
    // Constructor(s)
    //

    public PlatformTopologyNode(){
        super();
        this.processingPlants = new ArrayList<>();
        this.actualPodIP = null;
        this.actualHostIP = null;
    }

    //
    // Getters and Setters
    //

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ArrayList<ComponentIdType> getProcessingPlants() {
        return processingPlants;
    }

    public void setProcessingPlants(ArrayList<ComponentIdType> processingPlants) {
        this.processingPlants = processingPlants;
    }

    public String getActualHostIP() {
        return actualHostIP;
    }

    public void setActualHostIP(String actualHostIP) {
        this.actualHostIP = actualHostIP;
    }

    public String getActualPodIP() {
        return actualPodIP;
    }

    public void setActualPodIP(String actualPodIP) {
        this.actualPodIP = actualPodIP;
    }

    public Integer getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(Integer instanceCount) {
        this.instanceCount = instanceCount;
    }

    //
    // To String
    //


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PlatformTopologyNode{");
        sb.append("processingPlants=").append(processingPlants);
        sb.append(", instanceCount=").append(instanceCount);
        sb.append(", actualHostIP='").append(actualHostIP).append('\'');
        sb.append(", actualPodIP='").append(actualPodIP).append('\'');
        sb.append(", ").append(super.toString()).append('}');
        return sb.toString();
    }
}
