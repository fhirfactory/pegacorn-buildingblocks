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
package net.fhirfactory.pegacorn.petasos.endpoints.map;

import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.common.*;
import net.fhirfactory.pegacorn.petasos.endpoints.map.datatypes.PetasosEndpointCheckScheduleElement;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.factories.EndpointConnectionTypeCodeFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.factories.EndpointPayloadTypeFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierFactory;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Enumeration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PetasosEndpointMap {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosEndpointMap.class);

    // ConcurrentHashMap<endpointName, PetasosEndpoint>
    private ConcurrentHashMap<String, PetasosEndpoint> endpoints;
    // ConcurrentHashMap<endpointName, Object>
    private ConcurrentHashMap<String, Object> endpointLocks;
    // ConcurrentHashMap<endpointName, PetasosEndpointCheckScheduleElement>
    private ConcurrentHashMap<String, PetasosEndpointCheckScheduleElement> endpointCheckSchedule;
    // ConcurrentHashMap<endpointServiceName, List<endpointName>>
    private ConcurrentHashMap<String, List<String>> endpointServiceMap;
    private Object endpointServiceMapLock;
    private Object endpointCheckScheduleLock;

    private int ENDPOINT_CHECK_DELAY=10;

    @Inject
    private PegacornIdentifierFactory identifierFactory;

    @Inject
    private EndpointConnectionTypeCodeFactory connectionTypeCodeFactory;

    @Inject
    private EndpointPayloadTypeFactory payloadTypeFactory;

    @Inject
    private ProcessingPlantInterface processingPlantInterface;

    public PetasosEndpointMap(){
        this.endpoints = new ConcurrentHashMap<>();
        this.endpointLocks = new ConcurrentHashMap<>();
        this.endpointCheckSchedule = new ConcurrentHashMap<>();
        this.endpointCheckScheduleLock = new Object();
        this.endpointServiceMap = new ConcurrentHashMap<>();
        this.endpointServiceMapLock = new Object();
    }

    //
    // Abstract Methods
    //

    protected Logger specifyLogger(){
        return(LOG);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(specifyLogger());
    }

    //
    // ServiceMap Methods
    //

    private void addServiceMembership(String endpointServiceName, String endpointName){
        if(StringUtils.isEmpty(endpointServiceName) || StringUtils.isEmpty(endpointName)){
            return;
        }
        synchronized(this.endpointServiceMapLock) {
            if (this.endpointServiceMap.containsKey(endpointServiceName)) {
                List<String> endpointNames = this.endpointServiceMap.get(endpointServiceName);
                if(endpointNames.contains(endpointName)){
                    // do nothing
                } else {
                    endpointNames.add(endpointName);
                }
            } else {
                List<String> endpointNames = new ArrayList<>();
                endpointNames.add(endpointName);
                this.endpointServiceMap.put(endpointServiceName, endpointNames);
            }
        }
    }

    private void removeServiceNameMembership(String endpointServiceName, String endpointName) {
        if (StringUtils.isEmpty(endpointServiceName) || StringUtils.isEmpty(endpointName)) {
            return;
        }
        synchronized (this.endpointServiceMapLock) {
            if (this.endpointServiceMap.containsKey(endpointServiceName)) {
                List<String> endpointNames = this.endpointServiceMap.get(endpointServiceName);
                if (endpointNames.contains(endpointName)) {
                    endpointNames.remove(endpointName);
                }
                if (endpointNames.isEmpty()) {
                    this.endpointServiceMap.remove(endpointServiceName);
                }
            }
        }
    }

    private void removeServiceNameMembership(String endpointName){
        if(StringUtils.isEmpty(endpointName)){
            return;
        }
        if(this.endpointServiceMap.isEmpty()){
            return;
        }
        synchronized (this.endpointServiceMapLock){
            Enumeration<String> serviceNameEnumeration = this.endpointServiceMap.keys();
            while(serviceNameEnumeration.hasMoreElements()){
                String currentServiceName = serviceNameEnumeration.nextElement();
                List<String> currentEndpointNameSet = this.endpointServiceMap.get(currentServiceName);
                if(currentEndpointNameSet.contains(endpointName)){
                    currentEndpointNameSet.remove(endpointName);
                    if(currentEndpointNameSet.isEmpty()){
                        this.endpointServiceMap.remove(currentServiceName);
                    }
                }
            }
        }
    }

    public List<String> getServiceNameMembership(String endpointServiceName){
        List<String> serviceMembership = new ArrayList<>();
        if (StringUtils.isEmpty(endpointServiceName)) {
            return(serviceMembership);
        }
        synchronized (this.endpointServiceMapLock) {
            if (this.endpointServiceMap.containsKey(endpointServiceName)) {
                serviceMembership.addAll(this.endpointServiceMap.get(endpointServiceName));
            }
        }
        return(serviceMembership);
    }

    public void updateServiceNameMembership(String petasosEndpointServiceName, String petasosEndpointName){
        if (StringUtils.isEmpty(petasosEndpointName) || StringUtils.isEmpty(petasosEndpointServiceName)) {
            return;
        }
        boolean alreadyMappedAsRequested = false;
        boolean serviceEntryExists = false;
        synchronized(this.endpointServiceMapLock) {
            if (this.endpointServiceMap.containsKey(petasosEndpointServiceName)) {
                serviceEntryExists = true;
                List<String> endpointNames = this.endpointServiceMap.get(petasosEndpointServiceName);
                if (endpointNames.contains(petasosEndpointName)) {
                    alreadyMappedAsRequested = true;
                }
            }
        }
        if(alreadyMappedAsRequested) {
            return;
        }
        boolean wasMappedElsewhere = false;
        synchronized(this.endpointServiceMapLock) {
            Enumeration<String> serviceNameEnumeration = this.endpointServiceMap.keys();
            List<String> servicesToBeRemovedFromMap = new ArrayList<>();
            while(serviceNameEnumeration.hasMoreElements()){
                String currentServiceName = serviceNameEnumeration.nextElement();
                List<String> endpointNames = this.endpointServiceMap.get(currentServiceName);
                if(endpointNames != null) {
                    if (endpointNames.contains(petasosEndpointName)) {
                        wasMappedElsewhere = true;
                        endpointNames.remove(petasosEndpointName);
                    }
                    if (endpointNames.isEmpty()) {
                        servicesToBeRemovedFromMap.add(currentServiceName);
                    }
                }
            }
            if(!servicesToBeRemovedFromMap.isEmpty()) {
                for (String currentServiceToBeRemoved : servicesToBeRemovedFromMap) {
                    this.endpointServiceMap.remove(currentServiceToBeRemoved);
                }
            }
        }
        addServiceMembership(petasosEndpointServiceName, petasosEndpointName);
    }

    //
    // EndpointMap Methods
    //

    /**
     * This method creates a FHIR::Endpoint resource from the given EndpointAddress.
     * @param petasosEndpointID An PetasosEndpointIdentifier which will be used to build the FHIR::Endpoint
     * @return a FHIR::Endpoint representation of the logical endpoint identified by the EndpointAddress
     */
    protected Endpoint newEndpoint(PetasosEndpointIdentifier petasosEndpointID, String endpointAddressType, PetasosEndpointFunctionTypeEnum endpointType, EndpointPayloadTypeEnum payloadType){
        getLogger().info(".newEndpoint(): Entry, petasosEndpointID->{}", petasosEndpointID);
        if(petasosEndpointID == null){
            getLogger().info(".newEndpoint(): Exit, petasosEndpointID is null, returning null");
            return(null);
        }
        if(StringUtils.isEmpty(petasosEndpointID.getEndpointName()) || StringUtils.isEmpty(endpointAddressType)){
            getLogger().info(".newEndpoint(): Exit, newEndpointAddress.getAddressName() is empty or endpointAddressType null, returning null");
            return(null);
        }
        Endpoint newEndpoint = new Endpoint();
        newEndpoint.setId(petasosEndpointID.getEndpointName());
        Period period = new Period();
        period.setStart(Date.from(Instant.now()));
        newEndpoint.setPeriod(period);
        Identifier identifier = identifierFactory.newIdentifier(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_FHIR_ENDPOINT_SYSTEM, petasosEndpointID.getEndpointName(), period );
        newEndpoint.addIdentifier(identifier);
        newEndpoint.setConnectionType(connectionTypeCodeFactory.newPegacornEndpointJGroupsConnectionCodeSystem("JGroups", endpointType.getFunctionType()));
        UrlType urlType = new UrlType();
        urlType.setId(petasosEndpointID.getEndpointDetailedAddressName());
        newEndpoint.setAddressElement(urlType);
        String endpointFullName = petasosEndpointID.getEndpointSite() + "." + petasosEndpointID.getEndpointZone() + "." + petasosEndpointID.getEndpointGroup() + "." + petasosEndpointID.getEndpointName();
        newEndpoint.setNameElement(new StringType(endpointFullName));
        newEndpoint.addPayloadType(payloadTypeFactory.newPayloadType(payloadType));
        newEndpoint.getMeta().setLastUpdated(Date.from(Instant.now()));
        newEndpoint.getMeta().setSource(processingPlantInterface.getSimpleInstanceName());
        getLogger().info(".newEndpoint(): Exit, newEndpoint->{}", newEndpoint);
        return(newEndpoint);
    }

    /**
     * The method creates a Petasos Endpoint representing the provided interface details. It also builds the associated
     * FHIR::Enpoint representative of the the interface.
     * @param petasosEndpointID
     * @param endpointAddressType
     * @param endpointServiceName
     * @param endpointType
     * @param payloadType
     * @return
     */
    public PetasosEndpoint newPetasosEndpoint(PetasosEndpointIdentifier petasosEndpointID,
                                                 String endpointAddressType,
                                                 String endpointServiceName,
                                                 PetasosEndpointFunctionTypeEnum endpointType,
                                                 EndpointPayloadTypeEnum payloadType,
                                                 PetasosEndpointChannelScopeEnum scope){
        getLogger().info(".newPetasosEndpoint(): Entry");
        Endpoint fhirEndpoint = newEndpoint(petasosEndpointID, endpointAddressType, endpointType, payloadType);
        if(fhirEndpoint == null){
            return(null);
        }
        PetasosEndpoint petasosEndpoint = new PetasosEndpoint();
        petasosEndpoint.setEndpointScope(scope);
        petasosEndpoint.setEndpointServiceName(endpointServiceName);
        petasosEndpoint.setRepresentativeFHIREndpoint(fhirEndpoint);
        petasosEndpoint.setEndpointID(petasosEndpointID);
        petasosEndpoint.setEndpointStatus(PetasosEndpointStatusEnum.PETASOS_ENDPOINT_STATUS_DETECTED);
        getLogger().info(".newPetasosEndpoint(): Entry");
        return(petasosEndpoint);
    }

    /**
     * This method adds an PetasosEndpoint to the cache for the given EndpointAddress element if, and only if, there is
     * not an Endpoint already defined within the cache for the given EndpointAddress.getAddressName() value. It also
     * logs the FHIR::Endpoint addition into the audit trail (which is built and contained within the PetasosEndpoint).
     * @param petasosEndpointID The (EndpointAddress) of the new Endpoint
     * @return a PetasosEndpoint representing the IPC/OAM interface.
     */
    public PetasosEndpoint addEndpoint(PetasosEndpointIdentifier petasosEndpointID,
                                       String endpointAddressType,
                                       String endpointService,
                                       PetasosEndpointFunctionTypeEnum endpointType,
                                       EndpointPayloadTypeEnum payloadType,
                                       PetasosEndpointChannelScopeEnum scope){
        getLogger().info(".addEndpoint(): Entry, petasosEndpointID->{}", petasosEndpointID);

        if(petasosEndpointID == null){
            getLogger().info(".addEndpoint(): Exit, petasosEndpointID is null, return(null)");
            return(null);
        }
        if(StringUtils.isEmpty(petasosEndpointID.getEndpointName())){
            getLogger().info(".addEndpoint(): Exit, petasosEndpointID.getAddressName() is empty, can't do anything! return(null)");
            return(null);
        }

        String endpointName = petasosEndpointID.getEndpointName();
        if(endpoints.containsKey(endpointName)){
            PetasosEndpoint endpoint = endpoints.get(endpointName);
            getLogger().info(".addEndpoint(): Exit, endpoint already registered, endpoint->{}", endpoint);
            return(endpoint);
        }

        PetasosEndpoint endpoint = newPetasosEndpoint(petasosEndpointID, endpointAddressType, endpointService, endpointType, payloadType, scope);
        endpoints.put(endpointName, endpoint);
        endpointLocks.put(endpointName, new Object());
        addServiceMembership(endpointService, petasosEndpointID.getEndpointName());
        //
        //
        // TODO add an audit event here (detailing the addition of a new Endpoint).
        //
        //
        printMap();
        getLogger().debug(".newEndpoint(): Exit, endpoint added, endpoint->{}", endpoint);
        return(endpoint);
    }

    public void printMap(){
        Enumeration<String> endpointNames = endpoints.keys();
        getLogger().info("---------------EndpointMap-------------");
        while(endpointNames.hasMoreElements()){
            String endpointName = endpointNames.nextElement();
            PetasosEndpoint petasosEndpoint = endpoints.get(endpointName);
            getLogger().info("Endpoint->{}", petasosEndpoint);
        }
    }

    /**
     * This method adds an PetasosEndpoint to the cache for the given EndpointAddress element if, and only if, there is
     * not an Endpoint already defined within the cache for the given EndpointAddress.getAddressName() value. It also
     * logs the FHIR::Endpoint addition into the audit trail (which is built and contained within the PetasosEndpoint).
     * @param endpoint
     * @return a PetasosEndpoint representing the IPC/OAM interface.
     */
    public PetasosEndpoint addEndpoint(PetasosEndpoint endpoint){
        getLogger().info(".addEndpoint(): Entry, endpoint->{}", endpoint);

        if(endpoint == null){
            getLogger().info(".addEndpoint(): Exit, endpoint is null, return(null)");
            return(null);
        }

        String endpointName = endpoint.getEndpointID().getEndpointName();
        String endpointServiceName = endpoint.getEndpointServiceName();
        if(endpoints.containsKey(endpointName)){
            PetasosEndpoint retrievedEndpoint = endpoints.get(endpointName);
            getLogger().info(".newEndpoint(): Exit, endpoint already registered, endpoint->{}", endpoint);
            return(retrievedEndpoint);
        } else {
            endpoints.put(endpointName, endpoint);
            endpointLocks.put(endpointName, new Object());
            addServiceMembership(endpointServiceName, endpointName);
            //
            //
            // TODO add an audit event here (detailing the addition of a new Endpoint).
            //
            //
            printMap();
            getLogger().info(".addEndpoint(): Exit, endpoint added, endpoint->{}", endpoint);
            return(endpoint);
        }
    }

    /**
     * This method updates the LastUpdated attribute of the FHIR::Endpoint::Meta element. It is meant to reflect the
     * last instant at which activity occurred (or was detected) on the given endpoint. Node: It does not trigger an update
     * or such.
     * @param endpointName The endpoint name to be "touched".
     */
    public void touchEndpoint(String endpointName){
        getLogger().debug(".touchEndpoint(): Entry, endpointName->{}", endpointName);

        if(StringUtils.isEmpty(endpointName)){
            getLogger().debug(".touchEndpoint(): Exit, endpointName is empty, so doing nothing");
            return;
        }

        if(endpoints.containsKey(endpointName)){
            synchronized (getEndpointLock(endpointName)) {
                endpoints.get(endpointName).getRepresentativeFHIREndpoint().getMeta().setLastUpdated(Date.from(Instant.now()));
            }
        }
        getLogger().debug(".touchEndpoint(): endpoint updated");
    }

    /**
     * This method retrieves the designated PetasosEndpoint from the cache, as identified by the endpointName (which
     * also maps to the FHIR::Endpoint::Id (and (a) an Identifier, (b) FHIR::Endpoint::Name)
     * @param endpointName The name of the FHIR::Endpoint to be retrieved
     * @return THe requested FHIR::Endpoint
     */
    public PetasosEndpoint getEndpoint(String endpointName){
        getLogger().debug(".getEndpoint(): Entry, endpointName->{}", endpointName);

        if(StringUtils.isEmpty(endpointName)){
            getLogger().debug(".touchEndpoint(): Exit, endpointName is empty, so returning null");
            return(null);
        }

        if(endpoints.containsKey(endpointName)){
            PetasosEndpoint endpoint = endpoints.get(endpointName);
            getLogger().debug(".getEndpoint(): Exit, returning endpoint->{}", endpoint);
            return(endpoint);
        } else {
            getLogger().debug(".getEndpoint(): Exit, endpoint not found, returning null");
            return(null);
        }
    }

    /**
     * This method deletes an PetasosEndpoint (and associated FHIR::Endpoint instance from cache.
     * The FHIR::Endpoint is identified by the instance name (endpointName). An Audit Event is generated for
     * the deletion.
     * @param endpointName The name of the FHIR::Endpoint to be deleted.
     */
    public void deleteEndpoint(String endpointName){
        getLogger().debug(".deleteEndpoint(): Entry, endpointName->{}", endpointName);

        if(StringUtils.isEmpty(endpointName)){
            getLogger().debug(".touchEndpoint(): Exit, endpointName is empty, noting to do!");
            return;
        }

        if(endpoints.containsKey(endpointName)){
            PetasosEndpoint endpoint = endpoints.get(endpointName);
            //
            //
            // TODO Log an AuditEvent when an endpoint is deleted.
            //
            //
            endpoints.remove(endpointName);
            endpointLocks.remove(endpointName);
            removeServiceNameMembership(endpointName);
            printMap();
            getLogger().debug(".getEndpoint(): Exit, endpoint removed");
            return;
        } else {
            getLogger().debug(".getEndpoint(): Exit, endpoint is not in the cache");
            return;
        }
    }

    public Object getEndpointLock(String endpointName){
        if(StringUtils.isEmpty(endpointName)){
            return(null);
        }
        Object retrievedLockObject = endpointLocks.get(endpointName);
        return(retrievedLockObject);
    }

    //
    // Scheduled Activities
    //

    //
    // Check Schedule Management
    //

    public void scheduleEndpointCheck(PetasosEndpointIdentifier id, boolean endpointRemoved, boolean endpointAdded, int retryCountSoFar){
        getLogger().info(".scheduleEndpointCheck(): Entry, id->{}, endpointRemoved->{}, endpointAdded->{} ", id, endpointRemoved, endpointAdded);
        if(this.endpointCheckSchedule.containsKey(id)){
            getLogger().info(".scheduleEndpointCheck(): Exit, already scheduled");
            PetasosEndpointCheckScheduleElement petasosEndpointCheckScheduleElement = this.endpointCheckSchedule.get(id);
            petasosEndpointCheckScheduleElement.setRetryCount(retryCountSoFar);
            return;
        }
        PetasosEndpointCheckScheduleElement newScheduleElement = new PetasosEndpointCheckScheduleElement(id, endpointRemoved, endpointAdded, retryCountSoFar);
        this.endpointCheckSchedule.put(id.getEndpointName(), newScheduleElement);
        getLogger().info(".scheduleEndpointCheck(): Exit, check scheduled");
    }

    public void scheduleEndpointCheck(PetasosEndpointIdentifier id, boolean endpointRemoved, boolean endpointAdded){
        getLogger().info(".scheduleEndpointCheck(): Entry, id->{}, endpointRemoved->{}, endpointAdded->{} ", id, endpointRemoved, endpointAdded);
        if(this.endpointCheckSchedule.containsKey(id)){
            getLogger().info(".scheduleEndpointCheck(): Exit, already scheduled");
            return;
        }
        PetasosEndpointCheckScheduleElement newScheduleElement = new PetasosEndpointCheckScheduleElement(id, endpointRemoved, endpointAdded);
        this.endpointCheckSchedule.put(id.getEndpointName(), newScheduleElement);
        getLogger().info(".scheduleEndpointCheck(): Exit, check scheduled");
    }

    public void scheduleEndpointCheck(String endpointName, boolean endpointRemoved, boolean endpointAdded ){
        getLogger().info(".scheduleEndpointCheck(): Entry, endpointName->{}, endpointRemoved->{}, endpointAdded->{} ", endpointName, endpointRemoved, endpointAdded);
        if(StringUtils.isEmpty(endpointName)){
            getLogger().info(".scheduleEndpointCheck(): Exit, endpointName is empty");
            return;
        }
        if(!endpointRemoved && !endpointAdded){
            getLogger().info(".scheduleEndpointCheck(): Exit, neither endpointRemoved or endpointAdded is set");
            return;
        }
        synchronized (this.endpointCheckScheduleLock) {
            PetasosEndpointIdentifier endpointID = null;
            if (this.endpoints.containsKey(endpointName)) {
                endpointID = this.endpoints.get(endpointName).getEndpointID();
            } else {
                endpointID = new PetasosEndpointIdentifier();
                endpointID.setEndpointName(endpointName);
            }
            scheduleEndpointCheck(endpointID, endpointRemoved, endpointAdded);
        }
    }

    public List<PetasosEndpointCheckScheduleElement> getEndpointsToCheck(){
        getLogger().info(".getEndpointsToCheck(): Entry");
        List<PetasosEndpointCheckScheduleElement> endpointSet = new ArrayList<>();
        if(this.endpointCheckSchedule.isEmpty()){
            getLogger().info(".getEndpointsToCheck(): Exit, schedule is empty");
            return(endpointSet);
        }
        synchronized (this.endpointCheckScheduleLock) {
            Set<String> endpointNameSet = this.endpointCheckSchedule.keySet();

            for (String currentEndpointName : endpointNameSet) {
                PetasosEndpointCheckScheduleElement currentElement = this.endpointCheckSchedule.get(currentEndpointName);
                getLogger().info(".getEndpointsToCheck(): Checking entry ->{}", currentElement);
                if ((currentElement.getTargetTime().getEpochSecond() + ENDPOINT_CHECK_DELAY) < (Instant.now().getEpochSecond())) {
                    getLogger().info(".getEndpointsToCheck(): Adding...");
                    endpointSet.add(currentElement);
                }
            }
            for (PetasosEndpointCheckScheduleElement currentScheduleElement : endpointSet) {
                this.endpointCheckSchedule.remove(currentScheduleElement.getPetasosEndpointID());
            }
        }
        getLogger().info(".getEndpointsToCheck(): Exit, size->{}", endpointSet.size());
        return(endpointSet);
    }

    public boolean isCheckScheduleIsEmpty(){
        if(this.endpointCheckSchedule.isEmpty()){
            return(true);
        } else {
            return(false);
        }
    }
}
