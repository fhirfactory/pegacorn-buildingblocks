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
package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.oam.discovery.base;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointStatusEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.utilisationpolicy.PetasosOAMEndpointPolicyLayer;
import org.apache.commons.lang3.SerializationUtils;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

public abstract class PetasosOAMDiscoveryEndpointCoreFunctionLayer extends PetasosOAMEndpointPolicyLayer {

    /**
     *
     * @param targetEndpointID
     * @param myEndpoint
     * @return
     */
    public PetasosEndpoint probeEndpoint(PetasosEndpointIdentifier targetEndpointID, PetasosEndpoint myEndpoint){
        getLogger().debug(".probeEndpoint(): Entry, targetEndpointID->{}", targetEndpointID);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = myEndpoint;
            classSet[0] = PetasosEndpoint.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Address endpointAddress = getTargetMemberAddress(targetEndpointID.getEndpointChannelName());
            PetasosEndpoint targetPetasosEndpoint = getRPCDispatcher().callRemoteMethod(endpointAddress, "probeEndpointHandler", objectSet, classSet, requestOptions);
            getLogger().info(".probeEndpoint(): Exit, response->{}", targetPetasosEndpoint);
            return(targetPetasosEndpoint);
        } catch (NoSuchMethodException e) {
            getLogger().error(".probeEndpoint(): Error (NoSuchMethodException)->", e);
            return(null);
        } catch (Exception e) {
            getLogger().error(".probeEndpoint: Error (GeneralException) ->",e);
            return(null);
        }
    }

    /**
     *
     * @param sourcePetasosEndpoint
     * @return
     */
    public PetasosEndpoint probeEndpointHandler(PetasosEndpoint sourcePetasosEndpoint){
        getLogger().info(".probeEndpointHandler(): Entry, sourcePetasosEndpoint->{}", sourcePetasosEndpoint);
        getEndpointMap().addEndpoint(sourcePetasosEndpoint);
        PetasosEndpoint myEndpoint = SerializationUtils.clone(getPetasosEndpoint());
        myEndpoint.setEndpointStatus(getCoreSubsystemPetasosEndpointsWatchdog().getAggregatePetasosEndpointStatus());
        getLogger().info(".probeEndpointHandler(): Exit, myEndpoint->{}", myEndpoint);
        return(myEndpoint);
    }

    @Override
    public PetasosEndpointStatusEnum checkInterfaceStatus(PetasosEndpointIdentifier endpointID) {
        PetasosEndpoint remotePetasosEndpoint = probeEndpoint(endpointID, getPetasosEndpoint());
        PetasosEndpointStatusEnum endpointStatus = null;
        if(remotePetasosEndpoint != null){
            endpointStatus = remotePetasosEndpoint.getEndpointStatus();
        } else {
            endpointStatus = PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_UNREACHABLE;
        }
        return(endpointStatus);
    }
}
