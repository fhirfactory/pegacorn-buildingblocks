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
package net.fhirfactory.pegacorn.deployment.names.functionality.base;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosTopologyEndpointTypeEnum;

public abstract class PegacornCommonInterfaceNamesBase {

    public String getFunctionNamePetasosReplication(){return("PetasosReplication");}
    public String getFunctionNamePetasosStatus(){return("PetasosStatus");}
    public String getFunctionNamePetasosFinalisation(){return("PetasosFinalisation");}
    public String getFunctionNamePetasosTopology(){return("PetasosTopology");}
    //
    // Property Names
    //
    public String getFunctionNamePrometheus(){return("KubernetesSystemPrometheus");}
    public String getFunctionNameJolokia(){return("KubernetesSystemJolokia");}
    public String getFunctionNameKubeReadiness(){return("KubernetesSystemReadiness");}
    public String getFunctionNameKubeLiveliness(){return("KubernetesSystemLiveliness");}
    public String getFunctionNameIntraZoneJGroupsIPC(){return("intra-zone-ipc");}
    public String getFunctionNameInterZoneJGroupsIPC(){return("inter-zone-ipc");}
    public String getFunctionNameIntraZoneJGroupsOAM(){return("inter-zone-oam");}
    public String getFunctionNameInterZoneJGroupsOAM(){return("inter-zone-oam");}
    public String getFunctionNameEdgeReceive(){return("EdgeReceive");}
    public String getFunctionNameEdgeForward(){return("EdgeForward");}
    public String getFunctionNameEdgeAnswer(){return("EdgeAnswer");}
    public String getFunctionNameEdgeAsk(){return("EdgeAsk");}
    //
    // Default ProcessorPlant Interface Binding
    //
    public String getDefaultInterfaceNameForBinding(){return("0.0.0.0");}

    //
    // Endpoint Name Builder
    //
    public String getEndpointServerName(String endpointFunction){
        String endpointServerName = "IPCEndpoint:Server."+endpointFunction;
        return(endpointServerName);
    }

    public String getEndpointClientName(String endpointFunction){
        String endpointClientName = "IPCEndpoint:Client."+endpointFunction;
        return(endpointClientName);
    }

    //
    // Connection Name Builder
    //

    public String getConnectionNameAtoB(String clientEndpoint, String serverEndpoint){
        String newName = "IPCConnection:"+clientEndpoint+"-"+serverEndpoint;
        return(newName);
    }


}
