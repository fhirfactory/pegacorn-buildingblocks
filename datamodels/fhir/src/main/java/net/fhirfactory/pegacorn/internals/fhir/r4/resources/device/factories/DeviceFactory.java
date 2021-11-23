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

import net.fhirfactory.pegacorn.core.constants.systemwide.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.HTTPClientAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.HTTPServerAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.base.IPCTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.answer.StandardEdgeAnswerHTTPEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.answer.StandardEdgeIPCEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.ask.StandardEdgeAskHTTPEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.datatypes.JGroupsAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.http.InteractHTTPClientTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.http.InteractHTTPServerTopologyEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.InteractMLLPClientEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.InteractMLLPServerEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.adapters.MLLPClientAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.mllp.adapters.MLLPServerAdapter;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.ProcessingPlantSoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorSoftwareComponent;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkshopSoftwareComponent;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.interact.ClusteredInteractHTTPServerPortSegment;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.device.valuesets.DeviceConfigurationFilePropertyTypeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.endpoint.factories.EndpointFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierFactory;
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
    private PegacornIdentifierFactory identifierFactory;

    @Inject
    private PegacornReferenceProperties systemWideProperties;

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
        ComponentIdType nodeId = node.getComponentID();
        Period period = new Period();
        if(nodeId.hasIdValidityStartInstant()){
            Date startDate = Date.from(nodeId.getIdValidityStartInstant());
            period.setStart(startDate);
        }
        if(nodeId.hasIdValidityEndInstant()){
            Date endDate = Date.from(nodeId.getIdValidityEndInstant());
            period.setEnd(endDate);
        }
        Identifier identifier = getIdentifierFactory().newIdentifier(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_SOFTWARE_COMPONENT, node.getComponentID().getId(), period);

        //
        // Set the Name
        Device.DeviceDeviceNameComponent nameComponent = new Device.DeviceDeviceNameComponent();
        nameComponent.setName(node.getComponentID().getDisplayName());
        nameComponent.setType(Device.DeviceNameType.USERFRIENDLYNAME);
        device.addDeviceName(nameComponent);

        //
        // Set the Distinct Identifier (the FDN)
        device.setDistinctIdentifier(node.getComponentFDN().getToken().getTokenValue());

        //
        // Set the Device Type
        CodeableConcept deviceType = newPegacornSoftwareComponentDeviceType(node.getComponentType());
        device.setType(deviceType);

        //
        // Set the Device Model
        device.setModelNumber(node.getNodeFunctionFDN().getFunctionToken().getToken());

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
        if(node.getActualPodIP() != null){
            Coding podIPAddressTag = getMetaTagFactory().newPodIPAddressTag(node.getActualPodIP());
            device.getMeta().addTag(podIPAddressTag);
        }
        if(node.getActualHostIP() != null){
            Coding hostIPAddressTag = getMetaTagFactory().newHostIPAddressTag(node.getActualHostIP());
            device.getMeta().addTag(hostIPAddressTag);
        }

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
            case EDGE_JGROUPS_INTRAZONE_SERVICE:
            case EDGE_JGROUPS_INTERZONE_SERVICE:
            case EDGE_JGROUPS_INTERSITE_SERVICE:
                StandardEdgeIPCEndpoint jgroupsEndpoint = (StandardEdgeIPCEndpoint)ipcEndpoint;
                JGroupsAdapter jgroupsAdapter = jgroupsEndpoint.getJGroupsAdapter();
                Endpoint endpoint = getEndpointFactory().newJGroupsEndpoint(jgroupsEndpoint, jgroupsAdapter);
                device.addContained(endpoint);
                return(device);
            case EDGE_HTTP_API_SERVER:
                StandardEdgeAnswerHTTPEndpoint answerHTTPEndpoint = (StandardEdgeAnswerHTTPEndpoint) ipcEndpoint;
                HTTPServerAdapter httpServerAdapter = answerHTTPEndpoint.getHTTPServerAdapter();
                Endpoint edgeAnswerEndpoint = getEndpointFactory().newEndpoint(answerHTTPEndpoint, httpServerAdapter);
                device.addContained(edgeAnswerEndpoint);
                return(device);
            case EDGE_HTTP_API_CLIENT:
                StandardEdgeAskHTTPEndpoint askHTTPEndpoint = (StandardEdgeAskHTTPEndpoint) ipcEndpoint;
                for(HTTPClientAdapter currentHTTPClientAdapter: askHTTPEndpoint.getHTTPClientAdapters()) {
                    Endpoint edgeAskEndpoint = getEndpointFactory().newEndpoint(askHTTPEndpoint, currentHTTPClientAdapter);
                    device.addContained(edgeAskEndpoint);
                }
                return(device);
            case INTERACT_MLLP_SERVER:
                InteractMLLPServerEndpoint mllpServerEndpoint = (InteractMLLPServerEndpoint)ipcEndpoint;
                MLLPServerAdapter mllpServerAdapter = mllpServerEndpoint.getMLLPServerAdapter();
                Endpoint mllpServerFHIREndpoint = getEndpointFactory().newEndpoint(mllpServerEndpoint, mllpServerAdapter);
                device.addContained(mllpServerFHIREndpoint);
                return(device);
            case INTERACT_MLLP_CLIENT:
                InteractMLLPClientEndpoint mllpClientEndpoint = (InteractMLLPClientEndpoint) ipcEndpoint;
                for(MLLPClientAdapter currentMLLPClientAdapter: mllpClientEndpoint.getMLLPClientAdapters() ){
                    Endpoint mllpClientFHIREndpoint = getEndpointFactory().newEndpoint(mllpClientEndpoint, currentMLLPClientAdapter);
                    device.addContained(mllpClientFHIREndpoint);
                }
                return(device);
            case INTERACT_HTTP_API_SERVER:
                InteractHTTPServerTopologyEndpoint httpServerTopologyEndpoint = (InteractHTTPServerTopologyEndpoint) ipcEndpoint;
                HTTPServerAdapter interactHTTPServiceAdapter = httpServerTopologyEndpoint.getHTTPServerAdapter();
                Endpoint interactHTTPEndpoint = getEndpointFactory().newEndpoint(httpServerTopologyEndpoint, interactHTTPServiceAdapter);
                device.addContained(interactHTTPEndpoint);
                return(device);
            case INTERACT_HTTP_API_CLIENT:
                InteractHTTPClientTopologyEndpoint httpClientTopologyEndpoint = (InteractHTTPClientTopologyEndpoint) ipcEndpoint;
                for(HTTPClientAdapter currentHTTPClientAdapter: httpClientTopologyEndpoint.getHTTPClientAdapters()) {
                    Endpoint edgeAskEndpoint = getEndpointFactory().newEndpoint(httpClientTopologyEndpoint, currentHTTPClientAdapter);
                    device.addContained(edgeAskEndpoint);
                }
                return(device);
            case INTERACT_SQL_SERVER:
                break;
            case INTERACT_SQL_CLIENT:
                break;
            case INTERACT_LDAP_SERVER:
                break;
            case INTERACT_LDAP_CLIENT:
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
        nameComponent.setName(processingPlant.getSubsystemName());
        nameComponent.setType(Device.DeviceNameType.MODELNAME);
        device.addDeviceName(nameComponent);

        //
        // Set the Other Name (Cluster Service Name)
        Device.DeviceDeviceNameComponent clusterServiceNameComponent = new Device.DeviceDeviceNameComponent();
        clusterServiceNameComponent.setName(processingPlant.getSubsystemName());
        clusterServiceNameComponent.setType(Device.DeviceNameType.OTHER);
        device.addDeviceName(clusterServiceNameComponent);

        //
        // Add the configuration file(s)
        Device.DevicePropertyComponent interZoneAuditConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_INTERZONE_AUDIT, processingPlant.getInterZoneAuditStackConfigFile());
        device.addProperty(interZoneAuditConfigFileProperty);
        Device.DevicePropertyComponent intraZoneAuditConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_INTRAZONE_AUDIT, processingPlant.getIntraZoneAuditStackConfigFile());
        device.addProperty(intraZoneAuditConfigFileProperty);
        Device.DevicePropertyComponent interZoneIPCConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_INTERZONE_IPC, processingPlant.getInterZoneIPCStackConfigFile());
        device.addProperty(interZoneIPCConfigFileProperty);
        Device.DevicePropertyComponent intraZoneIPCConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_INTRAZONE_IPC, processingPlant.getIntraZoneIPCStackConfigFile());
        device.addProperty(intraZoneIPCConfigFileProperty);
        Device.DevicePropertyComponent interZoneInterceptionConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_INTERZONE_INTERCEPTION, processingPlant.getInterZoneInterceptionStackConfigFile());
        device.addProperty(interZoneInterceptionConfigFileProperty);
        Device.DevicePropertyComponent intraZoneInterceptionConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_INTRAZONE_INTERCEPTION, processingPlant.getIntraZoneInterceptionStackConfigFile());
        device.addProperty(intraZoneInterceptionConfigFileProperty);
        Device.DevicePropertyComponent interZoneMetricsConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_INTERZONE_METRICS, processingPlant.getInterZoneMetricsStackConfigFile());
        device.addProperty(interZoneMetricsConfigFileProperty);
        Device.DevicePropertyComponent intraZoneMetricsConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_INTRAZONE_METRICS, processingPlant.getIntraZoneMetricsStackConfigFile());
        device.addProperty(intraZoneMetricsConfigFileProperty);
        Device.DevicePropertyComponent interZoneTasksConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_INTERZONE_TASKS, processingPlant.getInterZoneTaskStackConfigFile());
        device.addProperty(interZoneTasksConfigFileProperty);
        Device.DevicePropertyComponent intraZoneTasksConfigFileProperty = getPropertyFactory().newConfigurationFileDeviceProperty(DeviceConfigurationFilePropertyTypeEnum.CONFIG_FILE_JGROUPS_INTRAZONE_TASKS, processingPlant.getIntraZoneTaskStackConfigFile());
        device.addProperty(intraZoneTasksConfigFileProperty);

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
        String codeSystem = systemWideProperties.getPegacornCodeSystemSite() + PEGACORN_SOFTWARE_COMPONENT_DEVICE_TYPE_SYSTEM;
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

    public CodeableConcept newPegacornSoftwareComponentDeviceType(ComponentTypeTypeEnum componentType){
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

    protected PegacornIdentifierFactory getIdentifierFactory(){
        return(identifierFactory);
    }

    protected DeviceSpecialisationFactory getSpecialisationFactory(){
        return(specialisationFactory);
    }

    protected EndpointFactory getEndpointFactory(){
        return(endpointFactory);
    }
}
