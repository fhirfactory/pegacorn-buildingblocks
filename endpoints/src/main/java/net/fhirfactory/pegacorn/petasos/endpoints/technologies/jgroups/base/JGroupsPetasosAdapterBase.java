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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.base;

import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.edge.petasos.PetasosEndpointChannelScopeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.edge.petasos.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.edge.answer.StandardEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddressTypeEnum;
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
//    protected abstract String specifyEndpointServiceName();
    protected abstract String specifyIPCInterfaceName();
    protected abstract PetasosEndpointTopologyTypeEnum specifyIPCType();
    abstract protected PetasosEndpointChannelScopeEnum specifyPetasosEndpointScope();

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
        getLogger().debug(".specifyJGroupsClusterName(): Entry");
        if(specifyPetasosEndpointScope().equals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTRAZONE)) {
            String clusterName = specifyEndpointID().getEndpointZone().getNetworkSecurityZoneCamelCase()+"-"+specifyEndpointID().getEndpointGroup();
            getLogger().debug(".specifyJGroupsClusterName(): IntraZone Scope, returning->{}", clusterName);
            return (clusterName);
        }
        if(specifyPetasosEndpointScope().equals(PetasosEndpointChannelScopeEnum.ENDPOINT_CHANNEL_SCOPE_INTERZONE)){
            String clusterName = specifyEndpointID().getEndpointSite()+"-"+specifyEndpointID().getEndpointGroup();
            getLogger().debug(".specifyJGroupsClusterName(): InterZone Scope, returning->{}", clusterName);
            return(clusterName);
        }
        String interSiteClusterName = specifyEndpointID().getEndpointGroup();
        getLogger().info(".specifyJGroupsClusterName(): InterSite Scope, returning->{}", interSiteClusterName);
        return(interSiteClusterName);
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
        synchronized (getCurrentScannedMembershipLock()) {
            List<Address> addressList = getCurrentScannedMembership();
            for (Address currentAddress : addressList) {
                if (currentAddress.toString().contains(serviceName)) {
                    PetasosAdapterAddress currentPetasosAdapterAddress = new PetasosAdapterAddress();
                    currentPetasosAdapterAddress.setJGroupsAddress(currentAddress);
                    currentPetasosAdapterAddress.setAddressName(currentAddress.toString());
                    currentPetasosAdapterAddress.setAddressType(PetasosAdapterAddressTypeEnum.ADDRESS_TYPE_JGROUPS);
                    addressSet.add(currentPetasosAdapterAddress);
                }
            }
        }
        getLogger().debug(".getTargetMemberAdapterSetForService(): Exit, addressSet->{}", addressSet);
        return (addressSet);
    }
    public PetasosAdapterAddress getTargetMemberAdapterAddress(String targetMemberKey){
        getLogger().debug(".getTargetMemberAdapterAddress(): Entry, targetMemberKey->{}", targetMemberKey);
        Address targetAddress = getTargetMemberAddress(targetMemberKey);
        if(targetAddress != null){
            getLogger().debug(".getTargetMemberAdapterAddress(): Exit, A match!, returning address->{}", targetAddress);
            PetasosAdapterAddress currentPetasosAdapterAddress = new PetasosAdapterAddress();
            currentPetasosAdapterAddress.setJGroupsAddress(targetAddress);
            currentPetasosAdapterAddress.setAddressName(targetAddress.toString());
            currentPetasosAdapterAddress.setAddressType(PetasosAdapterAddressTypeEnum.ADDRESS_TYPE_JGROUPS);
            return(currentPetasosAdapterAddress);
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
        getLogger().info(".getAllGroupMembers(): Entry");
        List<PetasosAdapterAddress> groupMembers = getAllClusterTargets();
        List<PetasosAdapterAddress> sameServiceSet = new ArrayList<>();
        for(PetasosAdapterAddress currentAdapterAddress: groupMembers){
            if(currentAdapterAddress.getAddressName().contains(specifyEndpointServiceName())){
                sameServiceSet.add(currentAdapterAddress);
            }
        }
        for(PetasosAdapterAddress sameServiceAddress: sameServiceSet){
            groupMembers.remove(sameServiceAddress);
        }
        getLogger().info(".getAllGroupMembers(): Exit, size->{}", groupMembers.size());
        return(groupMembers);
    }


    //
    // Updated & Documented Business Methods
    //

    /**
     * This method returns a list of the names of all the members of the Cluster
     * @return A List of Strings containing the names of all the members of the JGroups Cluster this interface is connected to.
     */
    public List<String> getAllClusterMembers(){
        getLogger().debug(".getAllClusterMembers(): Entry");
        List<String> memberNameList = new ArrayList<>();
        synchronized (getCurrentScannedMembershipLock()) {
            List<Address> memberAddressList = getCurrentScannedMembership();
            for (Address currentAddress : memberAddressList) {
                String currentMemberName = currentAddress.toString();
                memberNameList.add(currentMemberName);
            }
        }
        getLogger().debug(".getAllClusterMembers(): Exit");
        return(memberNameList);
    }

    /**
     * This method gets all the members of a JGroups Cluster whose name begins with the given namePrefix parameter.
     * A simple "startsWith()" string method is applied to each member retrieved from the JGroups View (where their
     * address is converted to a String - via .toString()).
     *
     * @param namePrefix A string to be checked against using the String function "startsWith()"
     * @return a list of String's representing all members whose name begins with the given prefix
     */
    public List<String> getClusterMemberSetBasedOnPrefix(String namePrefix){
        getLogger().debug(".getClusterMemberSetBasedOnPrefix(): Entry, namePrefix->{}", namePrefix);
        List<String> memberListBasedOnPrefix = new ArrayList<>();
        if(getIPCChannel() == null){
            getLogger().debug(".getClusterMemberSetBasedOnPrefix(): Exit, IPCChannel is null, returning empty set");
            return(memberListBasedOnPrefix);
        }
        if(StringUtils.isEmpty(namePrefix)){
            getLogger().debug(".getClusterMemberSetBasedOnPrefix(): Exit, namePrefix is null, returning empty set");
            return(memberListBasedOnPrefix);
        }
        getLogger().trace(".getClusterMemberSetBasedOnPrefix(): IPCChannel is NOT null & namePrefix is not empty, let's get updated Address set via view");
        List<String> memberList = getAllClusterMembers();
        getLogger().trace(".getClusterMemberSetBasedOnPrefix(): Got the Address set via view, now iterate through and see if one is suitable");
        for(String currentMemberName: memberList){
            getLogger().trace(".getClusterMemberSetBasedOnPrefix(): Iterating through Address list, current element->{}", currentMemberName);
            if(currentMemberName.toString().startsWith(namePrefix)){
                getLogger().debug(".getClusterMemberSetBasedOnPrefix(): currentMemberName is a match for given prefix, so adding it to list");
                memberListBasedOnPrefix.add(currentMemberName);
            }
        }
        getLogger().debug(".getClusterMemberSetBasedOnPrefix(): Exit, memberListBasedOnPrefix->{}",memberListBasedOnPrefix);
        return(memberListBasedOnPrefix);
    }

    /**
     * This method returns the JGroups Address (interface) for the given cluster member name (memberName).
     * @param memberName The Cluster Member name for which we would like the JGroups Address of
     * @return The JGroups Address matching the given memberName or -null- if not found within the JGroups Cluster
     */
    public Address getTargetAddressForClusterMember(String memberName){
        getLogger().debug(".getTargetAddressForClusterMember(): Entry, memberName->{}", memberName);
        Address targetAddress = getTargetMemberAddress(memberName);
        getLogger().debug(".getTargetAddressForClusterMember(): Exit, targetAddress->{}", targetAddress);
        return(targetAddress);
    }

    /**
     * This function retrieves the "first" JGroups Cluster Member whose name begins with the supplied namePrefix.
     * @param namePrefix The namePrefix used to find the "first" JGroups Cluster Member whose name starts with it
     * @return The "first" entry in the list of possible JGroups Cluster Members whose name begins with the supplied namePrefix
     */
    public String getFirstClusterMemberBasedOnPrefix(String namePrefix){
        getLogger().debug(".getFirstClusterMemberBasedOnPrefix(): Entry, namePrefix->{}", namePrefix);
        if(getIPCChannel() == null){
            getLogger().debug(".getFirstClusterMemberBasedOnPrefix(): Exit, IPCChannel is null, exit returning (null)");
            return(null);
        }
        if(StringUtils.isEmpty(namePrefix)){
            getLogger().debug(".getFirstClusterMemberBasedOnPrefix(): Exit, namePrefix is null, exit returning (null)");
            return(null);
        }
        List<String> potentialInterfaces = getClusterMemberSetBasedOnPrefix(namePrefix);
        if(potentialInterfaces.isEmpty()){
            getLogger().debug(".getFirstClusterMemberBasedOnPrefix(): Exit, no available interfaces supporting function");
            return(null);
        } else {
            String selectedMember = potentialInterfaces.get(0);
            getLogger().debug(".getFirstClusterMemberBasedOnPrefix(): Exit, selectedInterface->{}", selectedMember);
            return(selectedMember);
        }
    }

    /**
     * This method checks whether, for the given Cluster Member (name), there is an associated "active" JGroups Cluster
     * Address.
     * @param memberName The member name of the JGroups Cluster we are checking to see is still active
     * @return TRUE if an "active" Address can be found, FALSE otherwises
     */
    protected boolean isTargetClusterAddressActive(String memberName){
        getLogger().debug(".isTargetAddressActive(): Entry, memberName->{}", memberName);
        boolean isActive = isTargetAddressActive(memberName);
        getLogger().debug(".isTargetAddressActive(): Exit, isActive->{}", isActive);
        return(false);
    }

    /**
     * This method returns the JGroups Address of our JGroups Channel instance into the Cluster
     * @return a JGroups Address representing our connection into the JGroups Cluster
     */
    protected Address getMyClusterAddress(){
        if(getIPCChannel() != null){
            Address myAddress = getIPCChannel().getAddress();
            return(myAddress);
        }
        return(null);
    }

    /**
     * This method gets all the members of a JGroups Cluster whose name CONTAINS the given service name parameter.
     * A simple "contains()" string method is applied to each member retrieved from the JGroups View (where their
     * address is converted to a String - via .toString()).
     *
     * @param serviceName A string to be checked against using the String function "startsWith()"
     * @return a list of String's representing all members whose name begins with the given prefix
     */
    public List<String> getClusterMemberSetBasedOnService(String serviceName){
        getLogger().debug(".getClusterMemberSetBasedOnPrefix(): Entry, serviceName->{}", serviceName);
        List<String> memberListForService = new ArrayList<>();
        if(getIPCChannel() == null){
            getLogger().debug(".getClusterMemberSetBasedOnService(): Exit, IPCChannel is null, returning empty set");
            return(memberListForService);
        }
        if(StringUtils.isEmpty(serviceName)){
            getLogger().debug(".getClusterMemberSetBasedOnService(): Exit, namePrefix is null, returning empty set");
            return(memberListForService);
        }
        getLogger().trace(".getClusterMemberSetBasedOnService(): IPCChannel is NOT null & serviceName is not empty, let's get updated Address set via view");
        List<String> memberList = getAllClusterMembers();
        getLogger().trace(".getClusterMemberSetBasedOnService(): Got the Address set via view, now iterate through and see if one is suitable");
        for(String currentMemberName: memberList){
            getLogger().trace(".getClusterMemberSetBasedOnService(): Iterating through Address list, current element->{}", currentMemberName);
            if(currentMemberName.toString().contains(serviceName)){
                getLogger().debug(".getClusterMemberSetBasedOnService(): currentMemberName is a match for given serviceName, so adding it to list");
                memberListForService.add(currentMemberName);
            }
        }
        getLogger().debug(".getClusterMemberSetBasedOnService(): Exit, memberListBasedOnPrefix->{}",memberListForService);
        return(memberListForService);
    }

}
