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
package net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.oam.capabilityadvertising.base;

import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.tasks.PetasosCapabilityDeliveryNodeSet;
import net.fhirfactory.pegacorn.petasos.datasets.manager.DistributedCapabilityMapIM;
import net.fhirfactory.pegacorn.petasos.endpoints.base.PetasosTopologyEndpointChangeInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.common.PetasosAdapterDeltasInterface;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.datatypes.PetasosAdapterAddress;
import net.fhirfactory.pegacorn.petasos.endpoints.technologies.jgroups.base.JGroupsPetasosEndpointBase;

import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;

public abstract class PetasosOAMCapabilityMgtEndpoint extends JGroupsPetasosEndpointBase implements PetasosAdapterDeltasInterface, PetasosTopologyEndpointChangeInterface {

    private boolean capabilityScanScheduled;
    private Object capabilityScanLock;

    private boolean capabilityAdvertismentScheduled;
    private Object capabilityAdvertisementLock;

    private static Long CAPABILITY_ADVERTISEMENT_INITIAL_DELAY=15000L;
    private static Long CAPABILITY_ADVERTISEMENT_PERIOD = 15000L;
    private static int CAPABILITY_ADVERTISEMENT_MAX_COUNT = 4;
    private int capabilityAdvertisementCount;

    private static Long CAPABILITY_SCAN_INITIAL_DELAY=15000L;
    private static Long CAPABILITY_SCAN_PERIOD=15000L;
    private static int CAPABILITY_SCAN_MAX_COUNT = 10;
    private int capabilityScanCount;

    @Inject
    private DistributedCapabilityMapIM capabilityMapIM;

    @Inject
    private ProcessingPlantInterface processingPlant;

    //
    // Constructor
    //

    public PetasosOAMCapabilityMgtEndpoint(){
        super();
        capabilityScanScheduled = false;
        capabilityAdvertismentScheduled = false;
        capabilityScanLock = new Object();
        capabilityAdvertisementLock = new Object();
        capabilityAdvertisementCount = 0;
        capabilityScanCount = 0;
    }

    @Override
    protected void executePostConstructActivities(){
        getCoreSubsystemPetasosEndpointsWatchdog().registerTopologyCallbackChange(this);
        scheduleASubscriptionCheck();
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
        scheduleASubscriptionCheck();
    }

    @Override
    public void interfaceRemoved(PetasosAdapterAddress removedInterface){
        scheduleASubscriptionCheck();
    }

    @Override
    public void interfaceSuspect(PetasosAdapterAddress suspectInterface){

    }

    //
    // Callback Procedures for Subscribing
    //

    protected PetasosCapabilityDeliveryNodeSet probeCapabilityRoutingEndpoint(String endpointName){

    }


    public PetasosCapabilityDeliveryNodeSet rpcProbeCapabilityRoutingEndpointHandler(String endpointName){

    }


    //
    // Capability Advertisement Functions
    //

    public void scheduleACapabilityAdvertisement() {
        getLogger().info(".scheduleACapabilityAdvertisement(): Entry (capabilityScanScheduled->{}", capabilityScanScheduled);
        synchronized (capabilityAdvertisementLock) {
            if (capabilityScanScheduled) {
                // do nothing, it is already scheduled
            } else {
                TimerTask advertiseCapability = new TimerTask() {
                    public void run() {
                        getLogger().info(".advertiseCapability(): Entry");
                        boolean doAgain = advertiseCapabilityTask();
                        getLogger().info(".advertiseCapability(): doAgain ->{}", doAgain);
                        if (!doAgain) {
                            if( getCapabilityAdvertisementCount() > getCapabilityAdvertisementMaxCount()) {
                                cancel();
                                setCapabilityAdvertismentScheduled(false);
                                setCapabilityAdvertisementCount(0);
                            } else {
                                int checkCount = getCapabilityAdvertisementCount();
                                checkCount += 1;
                                setCapabilityAdvertisementCount(checkCount);
                            }
                        }
                        getLogger().info(".advertiseCapability(): Exit");
                    }
                };
                Timer timer = new Timer("CapabilityAdvertisementTimer");
                timer.schedule(advertiseCapability, getCapabilityAdvertisementInitialDelay(), getCapabilityAdvertisementPeriod());
                setCapabilityAdvertismentScheduled(true);
            }
        }
        getLogger().info(".scheduleACapabilityAdvertisement(): Exit");
    }

    public boolean advertiseCapabilityTask(){
        return(false);
    }


    //
    // Capability Scan Functions
    //



    //
    // Getters (and Setters)
    //


    public boolean isCapabilityScanScheduled() {
        return capabilityScanScheduled;
    }

    public Object getCapabilityScanLock() {
        return capabilityScanLock;
    }

    public boolean isCapabilityAdvertismentScheduled() {
        return capabilityAdvertismentScheduled;
    }

    public Object getCapabilityAdvertisementLock() {
        return capabilityAdvertisementLock;
    }

    public static Long getCapabilityAdvertisementInitialDelay() {
        return CAPABILITY_ADVERTISEMENT_INITIAL_DELAY;
    }

    public static Long getCapabilityAdvertisementPeriod() {
        return CAPABILITY_ADVERTISEMENT_PERIOD;
    }

    public static int getCapabilityAdvertisementMaxCount() {
        return CAPABILITY_ADVERTISEMENT_MAX_COUNT;
    }

    public int getCapabilityAdvertisementCount() {
        return capabilityAdvertisementCount;
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

    public void setCapabilityAdvertismentScheduled(boolean capabilityAdvertismentScheduled) {
        this.capabilityAdvertismentScheduled = capabilityAdvertismentScheduled;
    }

    protected DistributedCapabilityMapIM getDistributedCapabilityMapIM(){
        return(capabilityMapIM);
    }

    public void setCapabilityAdvertisementCount(int capabilityAdvertisementCount) {
        this.capabilityAdvertisementCount = capabilityAdvertisementCount;
    }

    public void setCapabilityScanCount(int capabilityScanCount) {
        this.capabilityScanCount = capabilityScanCount;
    }
}

