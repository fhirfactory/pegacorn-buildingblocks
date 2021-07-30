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

import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.PetasosTopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.edge.StandardEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.endpoints.endpoints.technologies.datatypes.PetasosAdapterAddressTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Address;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public abstract class JGroupsPetasosAdapterBase extends JGroupsAdapterBase {
    private StandardEdgeIPCEndpoint topologyNode;

    @Inject
    private ProcessingPlantInterface processingPlantInterface;

    @Inject
    private TopologyIM topologyIM;


    //
    // Constructor
    //

    public JGroupsPetasosAdapterBase(){
        super();
    }

    //
    // Abstract Methods
    //

    protected abstract PetasosEndpointIdentifier specifyEndpointID();
    protected abstract String specifyEndpointServiceName();
    protected abstract String specifyIPCInterfaceName();
    protected abstract PetasosTopologyEndpointTypeEnum specifyIPCType();

    //
    // Getters and Setters
    //

    public StandardEdgeIPCEndpoint getTopologyNode() {
        return topologyNode;
    }

    public void setTopologyNode(StandardEdgeIPCEndpoint topologyNode) {
        this.topologyNode = topologyNode;
    }

    protected TopologyIM getTopologyIM(){
        return(this.topologyIM);
    }

    public ProcessingPlantInterface getProcessingPlantInterface() {
        return processingPlantInterface;
    }

    protected String getEndpointServiceName(){
        return(specifyEndpointServiceName());
    }


    //
    // Resolved Parameters
    //

    @Override
    protected String specifyJGroupsClusterName() {
        return specifyEndpointID().getEndpointGroup();
    }

    @Override
    protected String specifyJGroupsChannelName() {
        return specifyEndpointID().getEndpointChannelName();
    }


    //
    // Business Methods
    //

    public List<PetasosAdapterAddress> getTargetMemberAdapterSetForService(String serviceName){
        getLogger().debug(".getTargetMemberAdapterSetForService(): Entry, serviceName->{}", serviceName);
        List<PetasosAdapterAddress> addressSet = new ArrayList<>();
        if(getIPCChannel() == null){
            getLogger().debug(".getTargetMemberAdapterSetForService(): Exit, IPCChannel is null, exit returning (null)");
            return(addressSet);
        }
        getLogger().trace(".getTargetMemberAdapterSetForService(): IPCChannel is NOT null, get updated Address set via view");
        List<Address> addressList = getIPCChannel().getView().getMembers();
        getLogger().trace(".getTargetMemberAdapterSetForService(): Got the Address set via view, now iterate through and see if one is suitable");
        for(Address currentAddress: addressList){
            getLogger().trace(".getTargetMemberAdapterSetForService(): Iterating through Address list, current element->{}", currentAddress);
            if(currentAddress.toString().startsWith(serviceName)){
                getLogger().debug(".getTargetMemberAdapterSetForService(): Exit, A match!, returning address->{}", currentAddress);
                PetasosAdapterAddress currentPetasosAdapterAddress = new PetasosAdapterAddress();
                currentPetasosAdapterAddress.setJGroupsAddress(currentAddress);
                currentPetasosAdapterAddress.setAddressName(currentAddress.toString());
                currentPetasosAdapterAddress.setAddressType(PetasosAdapterAddressTypeEnum.ADDRESS_TYPE_JGROUPS);
                addressSet.add(currentPetasosAdapterAddress);
            }
        }
        getLogger().debug(".getTargetMemberAdapterSetForService(): Exit, addressSet->{}",addressSet);
        return(addressSet);
    }
    public PetasosAdapterAddress getTargetMemberAdapterAddress(String targetMemberKey){
        getLogger().debug(".getTargetMemberAdapterAddress(): Entry, targetMemberKey->{}", targetMemberKey);
        List<PetasosAdapterAddress> addressSet = new ArrayList<>();
        if(getIPCChannel() == null){
            getLogger().debug(".getTargetMemberAdapterAddress(): Exit, IPCChannel is null, exit returning (null)");
            return(null);
        }
        getLogger().trace(".getTargetMemberAdapterAddress(): IPCChannel is NOT null, get updated Address set via view");
        List<Address> addressList = getIPCChannel().getView().getMembers();
        getLogger().trace(".getTargetMemberAdapterAddress(): Got the Address set via view, now iterate through and see if one is suitable");
        for(Address currentAddress: addressList){
            getLogger().trace(".getTargetMemberAdapterAddress(): Iterating through Address list, current element->{}", currentAddress);
            if(currentAddress.toString().contentEquals(targetMemberKey)){
                getLogger().debug(".getTargetMemberAdapterAddress(): Exit, A match!, returning address->{}", currentAddress);
                PetasosAdapterAddress currentPetasosAdapterAddress = new PetasosAdapterAddress();
                currentPetasosAdapterAddress.setJGroupsAddress(currentAddress);
                currentPetasosAdapterAddress.setAddressName(currentAddress.toString());
                currentPetasosAdapterAddress.setAddressType(PetasosAdapterAddressTypeEnum.ADDRESS_TYPE_JGROUPS);
                return(currentPetasosAdapterAddress);
            }
        }
        getLogger().debug(".getTargetMemberAdapterAddress(): Exit, could not find it...");
        return(null);
    }


    public PetasosAdapterAddress getTargetMemberAdapterInstanceForService(String serviceName){
        getLogger().debug(".getTargetMemberAdapterInstanceForService(): Entry, serviceName->{}", serviceName);
        if(getIPCChannel() == null){
            getLogger().debug(".getTargetMemberAdapterInstanceForService(): Exit, IPCChannel is null, exit returning (null)");
            return(null);
        }
        if(StringUtils.isEmpty(serviceName)){
            getLogger().debug(".getTargetMemberAdapterInstanceForService(): Exit, serviceName is null, exit returning (null)");
            return(null);
        }

        List<PetasosAdapterAddress> potentialInterfaces = getTargetMemberAdapterSetForService(serviceName);
        if(potentialInterfaces.isEmpty()){
            getLogger().debug(".getTargetMemberAdapterInstanceForService(): Exit, no available interfaces supporting function");
            return(null);
        } else {
            PetasosAdapterAddress selectedInterface = potentialInterfaces.get(0);
            getLogger().debug(".getTargetMemberAdapterInstanceForService(): Exit, selectedInterface->{}", selectedInterface);
            return(selectedInterface);
        }
    }

    public List<PetasosAdapterAddress> getAllGroupMembers(){
        getLogger().debug(".getAllGroupMembers(): Entry");
        List<PetasosAdapterAddress> groupMembers = getAllClusterTargets();
        List<PetasosAdapterAddress> sameServiceSet = new ArrayList<>();
        for(PetasosAdapterAddress currentAdapterAddress: groupMembers){
            if(StringUtils.startsWith(currentAdapterAddress.getAddressName(), specifyEndpointServiceName())){
                sameServiceSet.add(currentAdapterAddress);
            }
        }
        for(PetasosAdapterAddress sameServiceAddress: sameServiceSet){
            groupMembers.remove(sameServiceAddress);
        }
        getLogger().debug(".getAllGroupMembers(): Exit, size->{}", groupMembers.size());
        return(groupMembers);
    }

}
