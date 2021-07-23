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
package net.fhirfactory.pegacorn.platform.edge.endpoints.technologies.activitycache;

import net.fhirfactory.pegacorn.platform.edge.endpoints.roles.common.IPCEndpointAddress;
import net.fhirfactory.pegacorn.platform.edge.endpoints.technologies.activitycache.datatypes.EndpointCheckScheduleElement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EndpointCheckSchedule {

    private Map<IPCEndpointAddress, EndpointCheckScheduleElement> endpointCheckSchedule;

    public EndpointCheckSchedule(){
        endpointCheckSchedule = new ConcurrentHashMap<>();
    }


    //
    // Check Schedule Management
    //

    public void scheduleEndpointCheck(IPCEndpointAddress endpointAddress, boolean endpointRemoved,  boolean endpointAdded){
        if(this.endpointCheckSchedule.containsKey(endpointAddress)){
            return;
        }
        EndpointCheckScheduleElement newScheduleElement = new EndpointCheckScheduleElement(endpointAddress, endpointRemoved, endpointAdded);
        this.endpointCheckSchedule.put(endpointAddress, newScheduleElement);
    }

    public List<EndpointCheckScheduleElement> getEndpointsToCheck(){
        List<EndpointCheckScheduleElement> endpointSet = new ArrayList<>();
        if(this.endpointCheckSchedule.isEmpty()){
            return(endpointSet);
        }
        Set<IPCEndpointAddress> ipcEndpointAddresses = this.endpointCheckSchedule.keySet();
        for(IPCEndpointAddress currentEndpoint: ipcEndpointAddresses){
            EndpointCheckScheduleElement currentElement = this.endpointCheckSchedule.get(currentEndpoint);
            if(currentElement.getTargetTime().getEpochSecond() > (Instant.now().getEpochSecond()+10)){
                endpointSet.add(currentElement);
            }
        }
        for(EndpointCheckScheduleElement currentScheduleElement: endpointSet){
            this.endpointCheckSchedule.remove(currentScheduleElement.getEndpoint());
        }
        return(endpointSet);
    }

    public boolean scheduleIsEmpty(){
        if(this.endpointCheckSchedule.isEmpty()){
            return(true);
        } else {
            return(false);
        }
    }
}
