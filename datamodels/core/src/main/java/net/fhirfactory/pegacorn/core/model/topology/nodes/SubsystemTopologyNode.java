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

import java.awt.*;
import java.util.ArrayList;

public class SubsystemTopologyNode extends SoftwareComponent {
    private static final Logger LOG = LoggerFactory.getLogger(SubsystemTopologyNode.class);

    private ArrayList<ComponentIdType> businessServices;
    private Integer siteCount;

    public SubsystemTopologyNode(){
        businessServices = new ArrayList<>();
    }

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public ArrayList<ComponentIdType> getBusinessServices() {
        return businessServices;
    }

    public void setBusinessServices(ArrayList<ComponentIdType> businessServices) {
        this.businessServices = businessServices;
    }

    public Integer getSiteCount() {
        return siteCount;
    }

    public void setSiteCount(Integer siteCount) {
        this.siteCount = siteCount;
    }
}
