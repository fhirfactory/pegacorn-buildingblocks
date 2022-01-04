/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.petasos.oam.metrics.cache;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.petasos.oam.common.LocalOAMCacheBase;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.WorkUnitProcessorMetricsAgent;
import net.fhirfactory.pegacorn.petasos.oam.metrics.agents.common.ComponentMetricsAgentBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PetasosLocalMetricsDM extends LocalOAMCacheBase {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosLocalMetricsDM.class);

    private Map<ComponentIdType, ComponentMetricsAgentBase> nodeMetricsMap;
    private Map<ComponentIdType, Object> nodeMetricsLockMap;
    private Object metricsSetMapLock;

    public PetasosLocalMetricsDM(){
        super();
        this.nodeMetricsMap = new ConcurrentHashMap<>();
        this.nodeMetricsLockMap = new ConcurrentHashMap<>();
        this.metricsSetMapLock = new Object();
    }

    public Map<ComponentIdType, ComponentMetricsAgentBase> getNodeMetricsMap() {
        return nodeMetricsMap;
    }

    public Map<ComponentIdType, Object> getNodeMetricsLockMap() {
        return nodeMetricsLockMap;
    }

    public Object getMetricsSetMapLock() {
        return metricsSetMapLock;
    }

    public void addMetricSet(ComponentMetricsAgentBase metrics){
        synchronized (getMetricsSetMapLock()) {
            if (getNodeMetricsLockMap().containsKey(metrics.getComponentID())) {
                getNodeMetricsLockMap().remove(metrics.getComponentID());
            }
            if(getNodeMetricsMap().containsKey(metrics.getComponentID())){
                getNodeMetricsMap().remove(metrics.getComponentID());
            }
            getNodeMetricsMap().put(metrics.getComponentID(), metrics);
            getNodeMetricsLockMap().put(metrics.getComponentID(), new Object());
            refreshCurrentStateUpdateInstant();
        }
    }

    public List<ComponentMetricsAgentBase> getAllMetricsSets(){
        List<ComponentMetricsAgentBase> metricsSetList = new ArrayList<>();
        synchronized (getMetricsSetMapLock()){
            metricsSetList.addAll(getNodeMetricsMap().values());
        }
        return(metricsSetList);
    }

    public Object getNodeMetricsLock(ComponentIdType componentID){
        Object lock = getNodeMetricsLockMap().get(componentID);
        return(lock);
    }

    public ComponentMetricsAgentBase getNodeMetrics(ComponentIdType componentID){
        ComponentMetricsAgentBase metrics = getNodeMetricsMap().get(componentID);
        return(metrics);
    }

    public WorkUnitProcessorMetricsAgent getWorkUnitProcessorMetricsAgent(ComponentIdType componetId){
        WorkUnitProcessorMetricsAgent nodeMetrics = (WorkUnitProcessorMetricsAgent)getNodeMetrics(componetId);
        if(nodeMetrics == null){
            nodeMetrics = new WorkUnitProcessorMetricsAgent(componetId);
            getNodeMetricsMap().put(componetId, nodeMetrics);
            getNodeMetricsLockMap().put(componetId, new Object());
        }
        return(nodeMetrics);
    }

}
