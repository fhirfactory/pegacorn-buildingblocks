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
package net.fhirfactory.pegacorn.endpoints.endpoints.map;

import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosInterfaceFunctionTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.factories.EndpointConnectionTypeCodeFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.factories.EndpointPayloadTypeFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.valuesets.EndpointPayloadTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierFactory;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosEndpoint;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosInterfaceAddress;
import net.fhirfactory.pegacorn.endpoints.endpoints.datatypes.PetasosInterfaceStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PetasosEndpointMap {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosEndpointMap.class);

    private ConcurrentHashMap<String, PetasosEndpoint> endpoints;

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
    // Factory Methods
    //

    /**
     * This method creates a FHIR::Endpoint resource from the given EndpointAddress.
     * @param newPetasosInterfaceAddress An EndpointAddress which will be used to build the FHIR::Endpoint
     * @return a FHIR::Endpoint representation of the logical endpoint identified by the EndpointAddress
     */
    protected Endpoint newEndpoint(PetasosInterfaceAddress newPetasosInterfaceAddress, PetasosInterfaceFunctionTypeEnum endpointType, String endpointGroup, EndpointPayloadTypeEnum payloadType){
        getLogger().debug(".newEndpoint(): Entry, newEndpointAddress->{}", newPetasosInterfaceAddress);
        if(newPetasosInterfaceAddress == null){
            getLogger().debug(".newEndpoint(): Exit, newEndpointAddress is null, returning null");
            return(null);
        }
        if(StringUtils.isEmpty(newPetasosInterfaceAddress.getAddressName()) || newPetasosInterfaceAddress.getAddressType() == null){
            getLogger().debug(".newEndpoint(): Exit, newEndpointAddress.getAddressName() is empty or newEndpointAddress.getAddressType() null, returning null");
            return(null);
        }
        Endpoint newEndpoint = new Endpoint();
        newEndpoint.setId(newPetasosInterfaceAddress.getAddressName());
        Period period = new Period();
        period.setStart(Date.from(Instant.now()));
        newEndpoint.setPeriod(period);
        Identifier identifier = identifierFactory.newIdentifier(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_FHIR_ENDPOINT_SYSTEM, newPetasosInterfaceAddress.getAddressName(), period );
        newEndpoint.addIdentifier(identifier);
        newEndpoint.setConnectionType(connectionTypeCodeFactory.newPegacornEndpointJGroupsConnectionCodeSystem("JGroups", endpointType.getFunctionType()));
        UrlType urlType = new UrlType();
        urlType.setId("JGroups:"+endpointGroup+":"+ newPetasosInterfaceAddress.getAddressName());
        newEndpoint.setAddressElement(urlType);
        newEndpoint.setNameElement(new StringType(newPetasosInterfaceAddress.getAddressName()));
        newEndpoint.addPayloadType(payloadTypeFactory.newPayloadType(payloadType));
        newEndpoint.getMeta().setLastUpdated(Date.from(Instant.now()));
        newEndpoint.getMeta().setSource(processingPlantInterface.getSimpleInstanceName());
        getLogger().debug(".newEndpoint(): Exit, newEndpoint->{}", newEndpoint);
        return(newEndpoint);
    }

    /**
     * The method creates a Petasos Endpoint representing the provided interface details. It also builds the associated
     * FHIR::Enpoint representative of the the interface.
     * @param interfaceAddress
     * @param endpointType
     * @param endpointSite
     * @param endpointGroup
     * @param endpointZone
     * @param payloadType
     * @return
     */
    protected PetasosEndpoint newPetasosEndpoint(PetasosInterfaceAddress interfaceAddress,
                                          String interfaceService,
                                          PetasosInterfaceFunctionTypeEnum endpointType,
                                          String endpointSite, String endpointGroup, String endpointZone,
                                          EndpointPayloadTypeEnum payloadType){
        Endpoint fhirEndpoint = newEndpoint(interfaceAddress, endpointType, endpointGroup, payloadType);
        if(fhirEndpoint == null){
            return(null);
        }
        PetasosEndpoint petasosEndpoint = new PetasosEndpoint();
        petasosEndpoint.setEndpointService(interfaceService);
        petasosEndpoint.setRepresentativeFHIREndpoint(fhirEndpoint);
        petasosEndpoint.setEndpointName(interfaceAddress.getAddressName());
        petasosEndpoint.setEndpointGroup(endpointGroup);
        petasosEndpoint.setEndpointSite(endpointSite);
        petasosEndpoint.setEndpointZone(endpointZone);
        petasosEndpoint.setStatus(PetasosInterfaceStatusEnum.INTERFACE_STATUS_DETECTED);
        return(petasosEndpoint);
    }

    /**
     * This method adds an PetasosEndpoint to the cache for the given EndpointAddress element if, and only if, there is
     * not an Endpoint already defined within the cache for the given EndpointAddress.getAddressName() value. It also
     * logs the FHIR::Endpoint addition into the audit trail (which is built and contained within the PetasosEndpoint).
     * @param address The (EndpointAddress) of the new Endpoint
     * @return a PetasosEndpoint representing the IPC/OAM interface.
     */
    public PetasosEndpoint addEndpoint(PetasosInterfaceAddress address,
                                       String interfaceService,
                                       PetasosInterfaceFunctionTypeEnum endpointType,
                                       String endpointSite, String endpointGroup, String endpointZone,
                                       EndpointPayloadTypeEnum payloadType){
        getLogger().debug(".addEndpoint(): Entry, address->{}", address);

        if(address == null){
            getLogger().debug(".newEndpoint(): Exit, address is null, return(null)");
            return(null);
        }
        if(StringUtils.isEmpty(address.getAddressName())){
            getLogger().debug(".newEndpoint(): Exit, address.getAddressName() is empty, can't do anything! return(null)");
            return(null);
        }

        String endpointName = address.getAddressName();
        if(endpoints.containsKey(endpointName)){
            PetasosEndpoint endpoint = endpoints.get(endpointName);
            getLogger().debug(".newEndpoint(): Exit, endpoint already registered, endpoint->{}", endpoint);
            return(endpoint);
        }

        PetasosEndpoint endpoint = newPetasosEndpoint(address, interfaceService, endpointType, endpointSite, endpointGroup, endpointZone, payloadType);
        endpoints.put(endpointName, endpoint);
        //
        //
        // TODO add an audit event here (detailing the addition of a new Endpoint).
        //
        //
        getLogger().debug(".newEndpoint(): Exit, endpoint added, endpoint->{}", endpoint);
        return(endpoint);
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
            endpoints.get(endpointName).getRepresentativeFHIREndpoint().getMeta().setLastUpdated(Date.from(Instant.now()));
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
            getLogger().debug(".getEndpoint(): Exit, endpoint removed");
            return;
        } else {
            getLogger().debug(".getEndpoint(): Exit, endpoint is not in the cache");
            return;
        }
    }

}
