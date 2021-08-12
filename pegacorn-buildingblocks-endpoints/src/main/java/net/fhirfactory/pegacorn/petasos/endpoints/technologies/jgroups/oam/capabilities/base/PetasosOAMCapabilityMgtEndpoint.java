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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.capabilities.base;

import net.fhirfactory.pegacorn.core.model.endpoints.PetasosEndpoint;
import net.fhirfactory.pegacorn.core.model.endpoints.PetasosEndpointIdentifier;
import net.fhirfactory.pegacorn.core.model.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.tasks.PetasosCapabilityDeliveryNodeSet;
import net.fhirfactory.pegacorn.core.model.tasks.PetasosCapabilityNodeProbeResponse;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DistributedCapabilityMapIM;
import net.fhirfactory.pegacorn.petasos.endpoints.base.PetasosEndpointChangeCallbackRegistrationInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.base.PetasosEndpointChangeInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubParticipant;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherRegistration;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherSubscriptionRegistration;
import net.fhirfactory.pegacorn.petasos.model.pubsub.InterSubsystemPubSubPublisherSubscriptionRegistrationStatusEnum;
import net.fhirfactory.pegacorn.platform.edge.model.pubsub.RemoteSubscriptionResponse;
import org.jgroups.Address;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PetasosOAMCapabilityMgtEndpoint extends JGroupsPetasosEndpointBase implements PetasosAdapterDeltasInterface, PetasosEndpointChangeInterface {

    private boolean capabilityScanScheduled;
    private Object capabilityScanLock;

    private static Long CAPABILITY_SCAN_INITIAL_DELAY=15000L;
    private static Long CAPABILITY_SCAN_PERIOD=15000L;
    private static int CAPABILITY_SCAN_MAX_COUNT = 5;
    private int capabilityScanCount;

    private ConcurrentHashMap<String, PetasosEndpoint> changeList;
    private int changeListScanCount;
    private static Long CAPABILITY_CHECK_INITIAL_DELAY=15000L;
    private static Long CAPABILITY_CHECK_PERIOD=10000L;
    private static int CAPABILITY_CHECK_MAX_COUNT=3;

    @Inject
    private DistributedCapabilityMapIM capabilityMapIM;

    @Inject
    private ProcessingPlantInterface processingPlant;

    //
    // Constructor
    //

    public PetasosOAMCapabilityMgtEndpoint(){
        super();
        changeList = new ConcurrentHashMap<>();
        capabilityScanScheduled = false;
        capabilityScanLock = new Object();
        capabilityScanCount = 0;
        changeListScanCount = 0;
    }

    @Override
    protected void executePostConstructActivities(){
        getCoreSubsystemPetasosEndpointsWatchdog().registerTopologyCallbackChange(this);
        scheduleACapabilityScan();
    }

    //
    // Abstract Methods
    //

    //
    // Endpoint Discovery
    //

    @Override
    public void registerInterfaceEventCallbacks(PetasosAdapterDeltasInterface interfaceEventCallbacks) {
        this.getMembershipEventListeners().add(interfaceEventCallbacks);
    }

    @Override
    public void interfaceAdded(PetasosAdapterAddress addedInterface){
        scheduleACapabilityScan();
    }

    @Override
    public void interfaceRemoved(PetasosAdapterAddress removedInterface){
        scheduleACapabilityScan();
    }

    @Override
    public void interfaceSuspect(PetasosAdapterAddress suspectInterface){

    }

    @Override
    public void notifyNewEndpoint(PetasosEndpoint newEndpoint) {
        getLogger().info(".notifyNewPublisher(): Entry, newEndpoint->{}", newEndpoint);
        boolean inScope = newEndpoint.getEndpointScope().equals(getPetasosEndpoint().getEndpointScope());
        if(inScope) {
            String endpointName = newEndpoint.getEndpointID().getEndpointName();
            if(changeList.containsKey(endpointName)){
                return;
            }
            changeList.put(endpointName, newEndpoint);
        }
    }

    //
    // Probe and ProbeHandler for Capability Scanning
    //

    /**
     * This method is called to Probe an PetasosEndpoint (which is activing as a PetasosCapabilityRoutingEndpoint)
     * to ascertain what Capability(s) are supported by Nodes front'ed (routed via) that Endpoint.
     *
     * @param targetEndpointID
     * @return
     */
    protected PetasosCapabilityNodeProbeResponse rcpProbeCapabilityRoutingEndpoint(PetasosEndpointIdentifier targetEndpointID){
        PetasosCapabilityDeliveryNodeSet deliveryNodeSet = null;

        String channelName = getEndpointNameUtilities().getOAMCapabilityEndpointChannelNameFromOtherChannelName(targetEndpointID.getEndpointChannelName());
        Address channelAddress = getTargetMemberAddress(channelName);
        getLogger().info(".rcpProbeCapabilityRoutingEndpoint(): Extract JGroups Address->{}", channelAddress);
        try {
            Object objectSet[] = new Object[1];
            Class classSet[] = new Class[1];
            objectSet[0] = getEndpointID();
            classSet[0] = PetasosEndpointIdentifier.class;
            RequestOptions requestOptions = new RequestOptions( ResponseMode.GET_FIRST, getRPCUnicastTimeout());
            PetasosCapabilityNodeProbeResponse response = getRPCDispatcher().callRemoteMethod(channelAddress, "rpcProbeCapabilityRoutingEndpointHandler", objectSet, classSet, requestOptions);
            response.setProbeSuccessful(true);
            getLogger().info(".rcpProbeCapabilityRoutingEndpoint(): Exit, response->{}", response);
            return(response);
        } catch (NoSuchMethodException e) {
            getLogger().info(".rcpProbeCapabilityRoutingEndpoint(): Error (NoSuchMethodException)->", e);
            PetasosCapabilityNodeProbeResponse response = new PetasosCapabilityNodeProbeResponse();
            response.setProbeSuccessful(false);
            response.setProbeCommentary("Error (NoSuchMethodException)" + e.getMessage());
            return(response);
        } catch (Exception e) {
            getLogger().info(".rcpProbeCapabilityRoutingEndpoint: Error (GeneralException) ->",e);
            PetasosCapabilityNodeProbeResponse response = new PetasosCapabilityNodeProbeResponse();
            response.setProbeSuccessful(false);
            response.setProbeCommentary("Error (GeneralException)" + e.getMessage());
            return(response);
        }
    }


    public PetasosCapabilityNodeProbeResponse rpcProbeCapabilityRoutingEndpointHandler(PetasosEndpointIdentifier sourceEndpointID){
        getLogger().info(".rpcProbeCapabilityRoutingEndpointHandler(): Entry, sourceEndpointID->{}", sourceEndpointID);

        boolean withinScopeOfEndpoint = isWithinScopeOfEndpoint(sourceEndpointID);
        if(!withinScopeOfEndpoint){
            PetasosCapabilityNodeProbeResponse response = new PetasosCapabilityNodeProbeResponse();
            response.setInScope(false);
            getLogger().info(".rpcProbeCapabilityRoutingEndpointHandler(): Exit, not within scope of Endpoint to respond!");
            return(response);
        }

        PetasosCapabilityDeliveryNodeSet deliveryNodeSet = capabilityMapIM.getLocalCapabilityNodeSet();
        PetasosCapabilityNodeProbeResponse response = new PetasosCapabilityNodeProbeResponse();
        response.getCapabilityDeliveryNodeSet().addAll(deliveryNodeSet.getCapabilityDeliveryNodeSet());
        response.setProbeDate(Instant.now());
        response.setRoutingEndpointName(getEndpointID().getEndpointName());
        response.setProbeSuccessful(true);
        return(response);
    }


    //
    // Capability Scan Functions
    //

    /**
     * This function schedules a timer-task to scan for any missing Capabilities that are required by this
     * ProcessingPlant (as contained within the DistributedCapabilityMapIM/DM (capabilityFulfillmentStatusMap).
     */
    public void scheduleACapabilityScan() {
        getLogger().info(".scheduleACapabilityScan(): Entry (capabilityScanScheduled->{}", capabilityScanScheduled);
        synchronized (capabilityScanLock) {
            if (capabilityScanScheduled) {
                // do nothing, it is already scheduled
            } else {
                TimerTask scanForCapabilityTimeTask = new TimerTask() {
                    public void run() {
                        getLogger().info(".advertiseCapabilityTimeTask(): Entry");
                        boolean doAgain = scanForCapabilityTask();
                        getLogger().info(".advertiseCapabilityTimeTask(): doAgain ->{}", doAgain);
                        if (!doAgain) {
                            if( getCapabilityScanCount() > getCapabilityScanMaxCount()) {
                                cancel();
                                setCapabilityScanScheduled(false);
                                setCapabilityScanCount(0);
                            } else {
                                int checkCount = getCapabilityScanCount();
                                checkCount += 1;
                                setCapabilityScanCount(checkCount);
                            }
                        }
                        getLogger().info(".advertiseCapabilityTimeTask(): Exit");
                    }
                };
                Timer timer = new Timer("CapabilityScanTimer");
                timer.schedule(scanForCapabilityTimeTask, getCapabilityScanInitialDelay(), getCapabilityScanPeriod());
                setCapabilityScanScheduled(true);
            }
        }
        getLogger().info(".scheduleACapabilityScan(): Exit");
    }

    /**
     * This function is executed within a timer-task to scan for any missing Capabilities that are required by this
     * ProcessingPlant (as contained within the DistributedCapabilityMapIM/DM (capabilityFulfillmentStatusMap).
     *
     * It parses the capabilityFulfillmentStatusMap for any Capability(s) that are not fulfilled and then parses
     * ALL nodes (ProcessingPlant's) that it can reach and attempts to "find" the Capability.
     *
     * This is an expensive function. It should only be used up-to (say) n-times, where-upon if any Capability(s) are
     * not fulfilled, then this ProcessingPlant should be considered as FAILED.
     */
    public boolean scanForCapabilityTask(){
        return(false);
    }


    //
    // Capability Check Functions
    //

    public void scheduleACapabilityScan() {
        getLogger().info(".scheduleACapabilityScan(): Entry (capabilityScanScheduled->{}", capabilityScanScheduled);
        synchronized (capabilityScanLock) {
            if (capabilityScanScheduled) {
                // do nothing, it is already scheduled
            } else {
                TimerTask scanForCapabilityTimeTask = new TimerTask() {
                    public void run() {
                        getLogger().info(".advertiseCapabilityTimeTask(): Entry");
                        boolean doAgain = scanForCapabilityTask();
                        getLogger().info(".advertiseCapabilityTimeTask(): doAgain ->{}", doAgain);
                        if (!doAgain) {
                            if( getCapabilityScanCount() > getCapabilityScanMaxCount()) {
                                cancel();
                                setCapabilityScanScheduled(false);
                                setCapabilityScanCount(0);
                            } else {
                                int checkCount = getCapabilityScanCount();
                                checkCount += 1;
                                setCapabilityScanCount(checkCount);
                            }
                        }
                        getLogger().info(".advertiseCapabilityTimeTask(): Exit");
                    }
                };
                Timer timer = new Timer("CapabilityScanTimer");
                timer.schedule(scanForCapabilityTimeTask, getCapabilityScanInitialDelay(), getCapabilityScanPeriod());
                setCapabilityScanScheduled(true);
            }
        }
        getLogger().info(".scheduleACapabilityScan(): Exit");
    }

    //
    // Getters (and Setters)
    //


    public boolean isCapabilityScanScheduled() {
        return capabilityScanScheduled;
    }

    public Object getCapabilityScanLock() {
        return capabilityScanLock;
    }

    public static Long getCapabilityScanInitialDelay() {
        return CAPABILITY_SCAN_INITIAL_DELAY;
    }

    public static Long getCapabilityScanPeriod() {
        return CAPABILITY_SCAN_PERIOD;
    }

    public static int getCapabilityScanMaxCount() {
        return CAPABILITY_SCAN_MAX_COUNT;
    }

    public int getCapabilityScanCount() {
        return capabilityScanCount;
    }

    public void setCapabilityScanScheduled(boolean capabilityScanScheduled) {
        this.capabilityScanScheduled = capabilityScanScheduled;
    }

    protected DistributedCapabilityMapIM getDistributedCapabilityMapIM(){
        return(capabilityMapIM);
    }

    public void setCapabilityScanCount(int capabilityScanCount) {
        this.capabilityScanCount = capabilityScanCount;
    }

    public static Long getCapabilityCheckInitialDelay() {
        return CAPABILITY_CHECK_INITIAL_DELAY;
    }

    public static Long getCapabilityCheckPeriod() {
        return CAPABILITY_CHECK_PERIOD;
    }

    public static int getCapabilityCheckMaxCount() {
        return CAPABILITY_CHECK_MAX_COUNT;
    }

    public int getChangeListScanCount() {
        return changeListScanCount;
    }

    public void setChangeListScanCount(int changeListScanCount) {
        this.changeListScanCount = changeListScanCount;
    }
}

