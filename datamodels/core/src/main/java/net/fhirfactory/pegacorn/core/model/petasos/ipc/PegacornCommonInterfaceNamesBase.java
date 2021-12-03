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
package net.fhirfactory.pegacorn.core.model.petasos.ipc;

import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;

public abstract class PegacornCommonInterfaceNamesBase {

    public String getPetasosReplication(){return("PetasosReplication");}
    public String getPetasosStatus(){return("PetasosStatus");}
    public String getPetasosFinalisation(){return("PetasosFinalisation");}
    public String getPetasosTopology(){return("PetasosTopology");}
    //
    // Property Names
    //
    public String getPrometheusEndpointName(){return("KubernetesSystemPrometheus");}
    public String getJolokiaEndpointName(){return("KubernetesSystemJolokia");}
    public String getKubeReadinessEndpointName(){return("KubernetesSystemReadiness");}
    public String getKubeLivelinessEndpointName(){return("KubernetesSystemLiveliness");}
    public String getIntraZoneJGroupsIPCEndpointName(){return("intra-ipc");}
    public String getInterZoneJGroupsIPCEndpointName(){return("inter-ipc");}
    public String getIntraZoneJGroupsTopologyEndpointName(){return("intra-top");}
    public String getInterZoneJGroupsTopologyEndpointName(){return("inter-top");}
    public String getIntraZoneJGroupsSubscriptionsEndpointName(){return("intra-sub");}
    public String getInterZoneJGroupsSubscriptionsEndpointName(){return("inter-sub");}
    public String getIntraZoneJGroupsTaskingEndpointName(){return("intra-task");}
    public String getInterZoneJGroupsTaskingEndpointName(){return("inter-task");}
    public String getIntraZoneJGroupsAuditEndpointName(){return("intra-audit");}
    public String getInterZoneJGroupsAuditEndpointName(){return("inter-audit");}
    public String getIntraZoneJGroupsInterceptionEndpointName(){return("intra-snoop");}
    public String getInterZoneJGroupsInterceptionEndpointName(){return("inter-snoop");}
    public String getIntraZoneJGroupsMetricsEndpointName(){return("intra-metrics");}
    public String getInterZoneJGroupsMetricsEndpointName(){return("inter-metrics");}
    public String getMultiZoneInfinispaEndpointName(){return("multi-infini");}
    public String getEdgeReceiveEndpointName(){return("edge-rec");}
    public String getEdgeForwardEndpointName(){return("edge-snd");}
    public String getEdgeAnswerEndpointName(){return("edge-answer");}
    public String getEdgeAskEndpointName(){return("edge-ask");}
    //
    // Default ProcessorPlant Interface Binding
    //
    public String getDefaultInterfaceNameForBinding(){return("0.0.0.0");}

    //
    // Endpoint Name Builder
    //
    public String getEndpointServerName(String endpointFunction){
        String endpointServerName = "IPCEndpoint:Server("+endpointFunction+")";
        return(endpointServerName);
    }

    public String getEndpointClientName(String endpointFunction){
        String endpointClientName = "IPCEndpoint:Client("+endpointFunction+")";
        return(endpointClientName);
    }

    public String getEndpointName(PetasosEndpointTopologyTypeEnum endpointType, String endpointFunction){
        String endpointClientName = endpointType.getDisplayName() + "::"+ endpointFunction;
        return(endpointClientName);
    }

    //
    // Connection Name Builder
    //

    public String getConnectionNameAtoB(String clientEndpoint, String serverEndpoint){
        String newName = "ActiveIPCConnection:"+clientEndpoint+"-"+serverEndpoint;
        return(newName);
    }


}
