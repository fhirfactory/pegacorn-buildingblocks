/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.internal.resources.summaries.factories;

import net.fhirfactory.pegacorn.core.model.internal.resources.summaries.WorkUnitProcessorSummary;
import org.hl7.fhir.r4.model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WorkUnitProcessorSummaryFactory extends SoftwareComponentSummaryFactory{
    private static final Logger LOG = LoggerFactory.getLogger(WorkUnitProcessorSummaryFactory.class);


    //
    // Business Methods
    //

    public WorkUnitProcessorSummary newWorkUnitProcessorSummary(Device wupDevice, List<Device> endpointDevices){

        WorkUnitProcessorSummary wupSummary = newWorkUnitProcessorSummary(wupDevice);
        return(wupSummary);
    }

    public WorkUnitProcessorSummary newWorkUnitProcessorSummary(Device wupDevice){

        WorkUnitProcessorSummary wupSummary = new WorkUnitProcessorSummary();
        return(wupSummary);
    }

    //
    // Getters and Setters
    //

    @Override
    protected Logger getLogger(){
        return(LOG);
    }
}
