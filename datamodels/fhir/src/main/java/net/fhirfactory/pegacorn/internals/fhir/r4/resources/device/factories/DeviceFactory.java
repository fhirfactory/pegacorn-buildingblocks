/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.device.factories;

import net.fhirfactory.pegacorn.core.constants.systemwide.DRICaTSReferenceProperties;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.SoftwareComponentTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.HTTPClientAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.HTTPServerAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.answer.StandardEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.datatypes.JGroupsAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.http.HTTPClientTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.http.HTTPServerTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.mllp.MLLPClientEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.mllp.MLLPServerEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.mllp.adapters.MLLPClientAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.mllp.adapters.MLLPServerAdapter;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.ProcessingPlantSoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkshopSoftwareComponent;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.DRICaTSIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.device.valuesets.DeviceConfigurationFilePropertyTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.factories.EndpointFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.DRICaTSIdentifierFactory;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.Enumeration;
import java.util.Set;

@ApplicationScoped
public class DeviceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceFactory.class);

    @Inject
    private DeviceMetaTagFactory metaTagFactory;

    @Inject
    private DevicePropertyFactory propertyFactory;

    @Inject
    private DRICaTSIdentifierFactory identifierFactory;

    @Inject
    private DRICaTSReferenceProperties systemWideProperties;

    @Inject
    private DeviceSpecialisationFactory specialisationFactory;

    @Inject
    private EndpointFactory endpointFactory;

    private static final String PEGACORN_SOFTWARE_COMPONENT_DEVICE_TYPE_SYSTEM = "/device-type";

    //
    // Business Methods
    //

    public Device newDevice(
            ComponentIdType deviceComponentId,
            Device.FHIRDeviceStatus deviceStatus,
            String deviceName,
            Device.DeviceNameType deviceNameType,
            Set<Device.DevicePropertyComponent> properties,
            ResilienceModeEnum resilienceMode,
            ConcurrencyModeEnum concurrencyMode,
            NetworkSecurityZoneEnum securityZone
            ){

        //
        // Create the empty Device resource
        Device device = new Device();

        //
        // Set the Security Zone
        Coding deviceSecurityZoneTag = getMetaTagFactory().newSecurityTag(securityZone);
        device.getMeta().addTag(deviceSecurityZoneTag);

        //
        // Set the Device Name
        Device.DeviceDeviceNameComponent devName = new Device.DeviceDeviceNameComponent();
        devName.setType(deviceNameType);
        devName.setName(deviceName);
        device.addDeviceName(devName);

        //
        // Set the Device Status
        device.setStatus(deviceStatus);

        //
        // Add the Resilience and Concurrency Properties
        Device.DevicePropertyComponent resiliencePropertyComponent = getPropertyFactory().newDeviceProperty(resilienceMode);
        device.addProperty(resiliencePropertyComponent);
        Device.DevicePropertyComponent concurrencyPropertyComponent = getPropertyFactory().newDeviceProperty(concurrencyMode);
        device.addProperty(concurrencyPropertyComponent);


        //
        // Touch the resource Update marker
        device.getMeta().setLastUpdated(Date.from(Instant.now()));



        return(device);
    }

    public Device newDeviceFromSoftwareComponent(SoftwareComponent node){
        //
        // Create the empty Device resource
        Device device = new Device();

        //
        // Create the Identifier
        ComponentIdType nodeId = node.getComponentId();
        Period period = new Period();
        if(nodeId.hasIdValidityStartInstant()){
            Date startDate = Date.from(nodeId.getIdValidityStartInstant());
            period.setStart(startDate);
        }
        if(nodeId.hasIdValidityEndInstant()){
            Date endDate = Date.from(nodeId.getIdValidityEndInstant());
            period.setEnd(endDate);
        }
        Identifier identifier = getIdentifierFactory().newIdentifier(DRICaTSIdentifierCodeEnum.IDENTIFIER_CODE_SOFTWARE_COMPONENT, node.getComponentId().getId(), period);
        device.addIdentifier(identifier);

        //
        // Set the Name
        Device.DeviceDeviceNameComponent nameComponent = new Device.DeviceDeviceNameComponent();
        nameComponent.setName(node.getComponentId().getName());
        nameComponent.setType(Device.DeviceNameType.MODELNAME);
        device.addDeviceName(nameComponent);
        Device.DeviceDeviceNameComponent displayNameComponent = new Device.DeviceDeviceNameComponent();
        displayNameComponent.setName(node.getComponentId().getDisplayName());
        displayNameComponent.setType(Device.DeviceNameType.USERFRIENDLYNAME);
        device.addDeviceName(displayNameComponent);

        //
        // Set the Device Type
        CodeableConcept deviceType = newPegacornSoftwareComponentDeviceType(node.getComponentType());
        device.setType(deviceType);

        //
        // Set the Device Model
        device.setModelNumber(node.getParticipant().getParticipantId().getName());

        //
        // Set the Security Zone
        Coding deviceSecurityZoneTag = getMetaTagFactory().newSecurityTag(node.getSecurityZone());
        device.getMeta().addTag(deviceSecurityZoneTag);

        //
        // Add the Resilience and Concurrency Properties
        Device.DevicePropertyComponent resiliencePropertyComponent = getPropertyFactory().newDeviceProperty(node.getResilienceMode());
        device.addProperty(resiliencePropertyComponent);
        Device.DevicePropertyComponent concurrencyPropertyComponent = getPropertyFactory().newDeviceProperty(node.getConcurrencyMode());
        device.addProperty(concurrencyPropertyComponent);

        //
        // Add the Pod/Host IP Addresses (if set)
        /*
        if(node.getActualPodIP() != null){
            Coding podIPAddressTag = getMetaTagFactory().newPodIPAddressTag(node.getActualPodIP());
            device.getMeta().addTag(podIPAddressTag);
        }
        if(node.getActualHostIP() != null){
            Coding hostIPAddressTag = getMetaTagFactory().newHostIPAddressTag(node.getActualHostIP());
            device.getMeta().addTag(hostIPAddressTag);
        }

         */

        //
        // Add the Component Role (if present)
        if(node.getComponentSystemRole() != null){
            Device.DeviceSpecializationComponent deviceSpecializationComponent = getSpecialisationFactory().newDeviceSpecialisation(node.getComponentSystemRole());
            device.addSpecialization(deviceSpecializationComponent);
        }

        //
        // All done!
        return(device);
    }

    public Device newDevice(IPCTopologyEndpoint ipcEndpoint){

        //
        // Build using other method!
        Device device = newDevice(ipcEndpoint, null);

        //
        // All done!
        return(device);
    }

    public Device newDevice(IPCTopologyEndpoint ipcEndpoint, Reference parentWUP){

        //
        // Build default Device using other method!
        Device device = newDeviceFromSoftwareComponent(ipcEndpoint);

        switch(ipcEndpoint.getEndpointType()){
            case JGROUPS_INTEGRATION_POINT:
                StandardEdgeIPCEndpoint jgroupsEndpoint = (StandardEdgeIPCEndpoint)ipcEndpoint;
                JGroupsAdapter jgroupsAdapter = jgroupsEndpoint.getJGroupsAdapter();
                Endpoint endpoint = getEndpointFactory().newJGroupsEndpoint(jgroupsEndpoint, jgroupsAdapter);
                device.addContained(endpoint);
                return(device);
            case HTTP_API_SERVER:
                HTTPServerTopologyEndpoint httpServer = (HTTPServerTopologyEndpoint) ipcEndpoint;
                HTTPServerAdapter httpServerAdapter = httpServer.getHTTPServerAdapter();
                Endpoint edgeAnswerEndpoint = getEndpointFactory().newEndpoint(httpServer, httpServerAdapter);
                device.addContained(edgeAnswerEndpoint);
                return(device);
            case HTTP_API_CLIENT:
                HTTPClientTopologyEndpoint httpClientTopologyEndpoint = (HTTPClientTopologyEndpoint) ipcEndpoint;
                for(HTTPClientAdapter currentHTTPClientAdapter: httpClientTopologyEndpoint.getHTTPClientAdapters()) {
                    Endpoint edgeAskEndpoint = getEndpointFactory().newEndpoint(httpClientTopologyEndpoint, currentHTTPClientAdapter);
                    device.addContained(edgeAskEndpoint);
                }
                return(device);
            case MLLP_SERVER:
                MLLPServerEndpoint mllpServerEndpoint = (MLLPServerEndpoint)ipcEndpoint;
                MLLPServerAdapter mllpServerAdapter = mllpServerEndpoint.getMLLPServerAdapter();
                Endpoint mllpServerFHIREndpoint = getEndpointFactory().newEndpoint(mllpServerEndpoint, mllpServerAdapter);
                device.addContained(mllpServerFHIREndpoint);
                return(device);
            case MLLP_CLIENT:
                MLLPClientEndpoint mllpClientEndpoint = (MLLPClientEndpoint) ipcEndpoint;
                for(MLLPClientAdapter currentMLLPClientAdapter: mllpClientEndpoint.getMLLPClientAdapters() ){
                    Endpoint mllpClientFHIREndpoint = getEndpointFactory().newEndpoint(mllpClientEndpoint, currentMLLPClientAdapter);
                    device.addContained(mllpClientFHIREndpoint);
                }
                return(device);
            case SQL_SERVER:
                break;
            case SQL_CLIENT:
                break;
            case LDAP_SERVER:
                break;
            case LDAP_CLIENT:
                break;
            case OTHER_API_SERVER:
                break;
            case OTHER_API_CLIENT:
                break;
            case OTHER_SERVER:
                break;
            case OTHER_CLIENT:
                break;
        }
        //
        // All done!
        return(device);
    }

    public Device newDevice(WorkUnitProcessorSoftwareComponent wupComponent){

        //
        // Build using other method!
        Device device = newDevice(wupComponent, null);

        //
        // All done!
        return(device);
    }

    public Device newDevice(WorkUnitProcessorSoftwareComponent wupComponent, Reference parentWorkshop){
        //
        // Build base SoftwareComponentt device
        Device device = newDeviceFromSoftwareComponent(wupComponent);

        //
        // Add the Parent Workshop (if present)
        if(parentWorkshop != null) {
            device.setParent(parentWorkshop);
        }



        //
        // All done!
        return(device);
    }


    public Device newDevice(WorkshopSoftwareComponent workshop){

        //
        // Build using other method!
        Device device = newDevice(workshop, null);

        //
        // All done!
        return(device);
    }


    public Device newDevice(WorkshopSoftwareComponent workshop, Reference parentProcessingPlant){

        //
        // Build base SoftwareComponentt device
        Device device = newDeviceFromSoftwareComponent(workshop);

        //
        // Add the Parent ProcessingPlant (if present)
        if(parentProcessingPlant != null) {
            device.setParent(parentProcessingPlant);
        }

        //
        // All done!
        return(device);
    }

    public Device newDevice(ProcessingPlantSoftwareComponent processingPlant){

        //
        // Build base SoftwareComponentt device
        Device device = newDeviceFromSoftwareComponent(processingPlant);

        //
        // Set the Model Name (Subsystem Name)
        Device.DeviceDeviceNameComponent nameComponent = new Device.DeviceDeviceNameComponent();
        nameComponent.setName(processingPlant.getParticipant().getParticipantId().getSubsystemName());
        nameComponent.setType(Device.DeviceNameType.MODELNAME);
        device.addDeviceName(nameComponent);

        //
        // Set the Other Name (Cluster Service Name)
        Device.DeviceDeviceNameComponent clusterServiceNameComponent = new Device.DeviceDeviceNameComponent();
        clusterServiceNameComponent.setName(processingPlant.getParticipant().getParticipantId().getSubsystemName());
        clusterServiceNameComponent.setType(Device.DeviceNameType.OTHER);
        device.addDeviceName(clusterServiceNameComponent);

        //
        // Add the configuration file(s)
        Device.DevicePropertyComponent interZoneAuditConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_PETASOS_AUDIT, processingPlant.getPetasosAuditStackConfigFile());
        device.addProperty(interZoneAuditConfigFileProperty);
        Device.DevicePropertyComponent interZoneIPCConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_PETASOS_IPC_MESSAGING, processingPlant.getPetasosIPCStackConfigFile());
        device.addProperty(interZoneIPCConfigFileProperty);
        Device.DevicePropertyComponent interZoneInterceptionConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_PETASOS_INTERCEPTION, processingPlant.getPetasosInterceptionStackConfigFile());
        device.addProperty(interZoneInterceptionConfigFileProperty);
        Device.DevicePropertyComponent interZoneMetricsConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_PETASOS_METRICS, processingPlant.getPetasosMetricsStackConfigFile());
        device.addProperty(interZoneMetricsConfigFileProperty);
        Device.DevicePropertyComponent interZoneTasksConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_PETASOS_TASKS, processingPlant.getPetasosTaskingStackConfigFile());
        device.addProperty(interZoneTasksConfigFileProperty);
        Device.DevicePropertyComponent petasosSubscriptionConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_PETASOS_SUBSCRIPTION, processingPlant.getPetasosSubscriptionsStackConfigFile());
        device.addProperty(petasosSubscriptionConfigFileProperty);
        Device.DevicePropertyComponent petasosTopologyConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_PETASOS_TOPOLOGY, processingPlant.getPetasosSubscriptionsStackConfigFile());
        device.addProperty(petasosTopologyConfigFileProperty);

        //
        // Add Other Configuration Parameters
        Enumeration<String> otherConfigurationParametersEnumerator = processingPlant.getOtherConfigurationParameters().keys();
        while(otherConfigurationParametersEnumerator.hasMoreElements()) {
            String parameterName = otherConfigurationParametersEnumerator.nextElement();
            String otherConfigurationParameter = processingPlant.getOtherConfigurationParameter(parameterName);
            Device.DevicePropertyComponent otherConfigurationParameterComponent = new Device.DevicePropertyComponent();
            CodeableConcept parameterNameCC = new CodeableConcept();
            parameterNameCC.setText(parameterName);
            CodeableConcept parameterValueCC = new CodeableConcept();
            parameterValueCC.setText(otherConfigurationParameter);
            otherConfigurationParameterComponent.setType(parameterNameCC);
            otherConfigurationParameterComponent.addValueCode(parameterValueCC);
            device.addProperty(otherConfigurationParameterComponent);
        }

        //
        // All Done
        return(device);
    }

    //
    // Some Parameter Factories
    //

    public String getPegacornSoftwareComponentDeviceTypeSystem() {
        String codeSystem = systemWideProperties.getDRICaTSCodeSystemSite() + PEGACORN_SOFTWARE_COMPONENT_DEVICE_TYPE_SYSTEM;
        return (codeSystem);
    }

    public CodeableConcept newPegacornSoftwareComponentDeviceType(){
        CodeableConcept softwareComponentCC = new CodeableConcept();
        Coding softwareComponentCoding = new Coding();
        softwareComponentCoding.setCode("SoftwareComponent");
        softwareComponentCoding.setCode("pegacorn.fhir.device.device-type.software-component");
        softwareComponentCoding.setDisplay("Software Component");
        softwareComponentCC.addCoding(softwareComponentCoding);
        softwareComponentCC.setText("Software Component Device Type");
        return(softwareComponentCC);
    }

    public CodeableConcept newPegacornSoftwareComponentDeviceType(SoftwareComponentTypeEnum componentType){
        CodeableConcept softwareComponentCC = new CodeableConcept();
        Coding softwareComponentCoding = new Coding();
        softwareComponentCoding.setCode(componentType.getToken());
        softwareComponentCoding.setCode("pegacorn.fhir.device.device-type.software-component");
        softwareComponentCoding.setDisplay(componentType.getDisplayName());
        softwareComponentCC.addCoding(softwareComponentCoding);
        softwareComponentCC.setText("Software Component("+componentType.getDisplayName()+")");
        return(softwareComponentCC);
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected DeviceMetaTagFactory getMetaTagFactory(){
        return(metaTagFactory);
    }

    protected DevicePropertyFactory getPropertyFactory(){
        return(propertyFactory);
    }

    protected DRICaTSIdentifierFactory getIdentifierFactory(){
        return(identifierFactory);
    }

    protected DeviceSpecialisationFactory getSpecialisationFactory(){
        return(specialisationFactory);
    }

    protected EndpointFactory getEndpointFactory(){
        return(endpointFactory);
    }
}
