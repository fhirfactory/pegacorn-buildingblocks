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
package net.fhirfactory.pegacorn.platform.ipc.frameworks.messaging.jgroups.common;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.common.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.TopologyEndpointTypeEnum;
import net.fhirfactory.pegacorn.platform.ipc.frameworks.messaging.common.EdgeIPCEndpoint;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.View;
import org.slf4j.Logger;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public abstract class JGroupsEdgeIPCEndpoint extends EdgeIPCEndpoint {

    private static String GROUP_SERVER_ENDPOINT_NAME = "GroupServerEndpoint";
    private static String GROUP_CLIENT_ENDPOINT_NAME = "GroupClientEndpoint";

    private boolean initialised;
    private IPCTopologyEndpoint designatedEndpoint;

    public JGroupsEdgeIPCEndpoint(){
        super();
        initialised = false;
        designatedEndpoint = null;
    }

    protected TopologyEndpointTypeEnum specifyIPCType() {
        return (TopologyEndpointTypeEnum.PEGACORN_IPC_MESSAGING_SERVICE);
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    protected Logger getLogger(){
        return(specifyLogger());
    }

    protected void deriveIPCTopologyEndpoint(){
        getLogger().debug(".deriveIPCTopologyEndpoint(): Entry");
        IPCTopologyEndpoint jgroupsEndpoint = null;
        for(TopologyNodeFDN currentEndpointFDN: getProcessingPlant().getProcessingPlantNode().getEndpoints()){
            IPCTopologyEndpoint currentEndpoint = (IPCTopologyEndpoint)getTopologyIM().getNode(currentEndpointFDN);
            TopologyEndpointTypeEnum endpointType = currentEndpoint.getEndpointType();
            boolean endpointTypeMatches = endpointType.equals(specifyIPCType());
            if(endpointTypeMatches){
                if(hasInterfaceInSameNetworkZone(currentEndpoint)) {
                    jgroupsEndpoint = currentEndpoint;
                    break;
                }
            }
        }
        if(jgroupsEndpoint == null){
            getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, no jGroups Endpoint found");
            return;
        }
        if(jgroupsEndpoint.getSupportedInterfaceSet().size() <= 0){
            getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, no Interfaces defined in jGroups Endpoint");
            return;
        }
        getLogger().debug(".deriveIPCTopologyEndpoint(): Exit, found IPCTopologyEndpoint, returning it");
        setDesignatedEndpoint(jgroupsEndpoint);
    }

    protected JChannel establishJChannel(String groupName, String channelName){
        getLogger().debug(".establishJChannel(): Entry, groupName->{}, channelName->{}", groupName, channelName);
        try {
            JChannel newChannel = new JChannel().name(channelName);
            newChannel.connect(groupName);
            getLogger().debug(".establishJChannel(): Exit, Channel Established");
            return(newChannel);
        } catch (Exception e) {
            getLogger().error(".establishJChannel(): Cannot establish JGroups Channel, error->{}", e.getMessage());
            return(null);
        }
    }

    protected Address deriveTargetAddress(JChannel channelIntoGroup, String targetEndpoint){
        getLogger().debug(".deriveTargetAddress(): Entry, targetEndpoint->{}", targetEndpoint);
        View view = channelIntoGroup.view();
        List<Address> groupMembers = view.getMembers();
        for(Address currentEndpoint: groupMembers) {
            if (currentEndpoint.toString().startsWith(targetEndpoint)) {
                getLogger().trace(".deriveTargetAddress(): Found Server Endpoint");
                return(currentEndpoint);
            }
        }
        return(null);
    }
    //
    // Getters and Setters
    //

    public static String getGroupServerEndpointName() {
        return GROUP_SERVER_ENDPOINT_NAME;
    }

    public static String getGroupClientEndpointName() {
        return GROUP_CLIENT_ENDPOINT_NAME;
    }

    public IPCTopologyEndpoint getDesignatedEndpoint() {
        return designatedEndpoint;
    }

    public void setDesignatedEndpoint(IPCTopologyEndpoint designatedEndpoint) {
        this.designatedEndpoint = designatedEndpoint;
    }

    //
    // Support Functions
    //

    protected String buildChannelName(String subsystemName, String subsystemVersion, Boolean isServer){
        String suffix = null;
        if(isServer){
            suffix = getGroupServerEndpointName();
        } else {
            suffix = getGroupClientEndpointName();
        }
        String name = subsystemName+"-"+subsystemVersion+"-"+suffix+"-"+Long.toString(Date.from(Instant.now()).getTime());
        return(name);
    }

    protected boolean isSameNetworkZone(String targetName){
        NetworkSecurityZoneEnum myEndpointZone = getDesignatedEndpoint().getSecurityZone();
        NetworkSecurityZoneEnum targetEndpointZone = getTopologyIM().getDeploymentNetworkSecurityZone(targetName);
        boolean endpointZoneMatches = myEndpointZone.equals(targetEndpointZone);
        return(endpointZoneMatches);
    }

    protected boolean hasInterfaceInSameNetworkZone(IPCTopologyEndpoint endpoint){
        for(IPCInterface currentInterface: endpoint.getSupportedInterfaceSet()){
            if(isSameNetworkZone(currentInterface.getTargetName())){
                return(true);
            }
        }
        return(false);
    }
}
