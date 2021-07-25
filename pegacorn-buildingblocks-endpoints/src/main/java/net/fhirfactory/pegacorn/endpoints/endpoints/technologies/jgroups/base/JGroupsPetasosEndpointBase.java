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
package net.fhirfactory.pegacorn.endpoints.endpoints.technologies.jgroups.base;

import net.fhirfactory.pegacorn.deployment.names.functionality.base.PegacornCommonInterfaceNames;
import net.fhirfactory.pegacorn.deployment.properties.codebased.PegacornIPCCommonValues;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosEndpoint;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosEndpointFunctionTypeEnum;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.endpoints.endpoints.map.PetasosEndpointMap;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.common.PetasosAdapterTechnologyInterface;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

public abstract class JGroupsPetasosEndpointBase extends JGroupsPetasosAdapterBase implements PetasosAdapterTechnologyInterface {

    private PetasosEndpoint petasosEndpoint;

    @Inject
    private PetasosEndpointMap endpointMap;

    @Inject
    private PegacornCommonInterfaceNames interfaceNames;

    @Inject
    private PegacornIPCCommonValues ipcFunctionalityNames;


    //
    // Constructor
    //

    public JGroupsPetasosEndpointBase(){
        super();
    }

    //
    // Abstract Methods
    //

    abstract protected PetasosEndpointFunctionTypeEnum specifyPetasosEndpointFunctionType();
    abstract protected EndpointPayloadTypeEnum specifyPetasosEndpointPayloadType();

    //
    // PostConstruct Initialisation
    //

    @PostConstruct
    public void initialise() {
        getLogger().info(".initialise(): Entry");
        if (isInitialised()) {
            getLogger().info(".initialise(): Exit, already initialised!");
            return;
        }
        // 1st, Derive my Endpoint (Topology)
        getLogger().info(".initialise(): Step 1: Start ==> Get my IPCEndpoint Detail");
        deriveTopologyEndpoint();
        getLogger().info(".initialise(): Step 1: Complete ==> IPCEndpoint derived ->{}", getTopologyNode());

        // 2nd, the PetasosEndpoint
        getLogger().info(".initialise(): Step 2: Start ==> Creating my PetasosEndpoint");
        PetasosEndpoint petasosEndpoint = getEndpointMap().addEndpoint(getEndpointID(), "JGroups", getEndpointServiceName(), getPetasosEndpointFunctionType(), getPetasosEndpointPayloadType());
        this.petasosEndpoint = petasosEndpoint;
        getLogger().info(".initialise(): Step 2: Completed ==> PetasosEndpoint created ->{}", getPetasosEndpoint());

        // 3rd, Initialise my JChannel
        getLogger().info(".initialise(): Step 3: Start ==> Initialise my JChannel Connection & Join Cluster/Group");
        establishJChannel();
        getLogger().info(".initialise(): Step 3: Completed ==> ipcChannel ->{}", getIPCChannel());


    }

    //
    // Getters (and Setters)
    //

    protected PetasosEndpointMap getEndpointMap(){
        return(endpointMap);
    }

    protected PetasosEndpoint getPetasosEndpoint(){
        return(petasosEndpoint);
    }

    protected PetasosEndpointFunctionTypeEnum getPetasosEndpointFunctionType(){
        return(specifyPetasosEndpointFunctionType());
    }

    protected EndpointPayloadTypeEnum getPetasosEndpointPayloadType(){
        return(specifyPetasosEndpointPayloadType());
    }

    protected PegacornCommonInterfaceNames getInterfaceNames(){
        return(interfaceNames);
    }

    public PegacornIPCCommonValues getIPCComponentNames() {
        return ipcFunctionalityNames;
    }

    //
    //
    //

    public void scheduleEndpointScan(){
        List<PetasosAdapterAddress> groupMembers = getAllGroupMembers();
        for(PetasosAdapterAddress currentGroupMember: groupMembers){
            getEndpointMap().scheduleEndpointCheck(currentGroupMember.getAddressName(), false, true);
        }
    }

    /**
     *
     * @param endpointID
     * @param myEndpoint
     * @return
     */
    public PetasosEndpoint probeEndpoint(PetasosEndpointIdentifier endpointID, PetasosEndpoint myEndpoint){
        getLogger().info(".probeEndpoint(): Entry, endpointID->{}", endpointID);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = myEndpoint;
            classSet[0] = PetasosEndpoint.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            Address endpointAddress = getTargetMemberAddress(endpointID.getEndpointName());
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
     * @param endpointID
     * @return
     */
    public PetasosEndpoint probeEndpointHandler(PetasosEndpoint endpointID){
        getLogger().info(".probeEndpointHandler(): Entry, endpointID->{}", endpointID);
        getEndpointMap().addEndpoint(endpointID);
        getLogger().info(".probeEndpointHandler(): Exit, returning->{}", getPetasosEndpoint());
        return(getPetasosEndpoint());
    }

    @Override
    public void registerInterfaceEventCallbacks(PetasosAdapterDeltasInterface interfaceEventCallbacks) {
        this.getMembershipEventListeners().add(interfaceEventCallbacks);
    }
}
