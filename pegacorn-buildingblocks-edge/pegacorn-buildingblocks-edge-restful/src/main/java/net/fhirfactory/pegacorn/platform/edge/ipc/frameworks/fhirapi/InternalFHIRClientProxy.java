/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.platform.edge.ipc.frameworks.fhirapi;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;

import net.fhirfactory.pegacorn.deployment.names.sites.SiteKeyNames;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.factories.helpers.TopologyMapTraversalHelpers;
import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.base.IPCClusteredServerTopologyEndpoint;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterfaceDefinition;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;

import net.fhirfactory.pegacorn.deployment.topology.model.nodes.*;
import net.fhirfactory.pegacorn.internals.PegacornReferenceProperties;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

public abstract class InternalFHIRClientProxy extends HAPIServerSecureProxy {

    protected abstract Logger getLogger();

    private String targetEdgeAnswerService;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private PegacornReferenceProperties systemWideProperties;

    @Inject
    private TopologyMapTraversalHelpers mapTraversalHelpers;

    @Inject
    private SiteKeyNames siteKeyNames;

    protected abstract String specifyFHIRServerService();
    protected abstract String specifyRequiredInterfaceName();
    protected abstract String specifyRequiredInterfaceVersion();
    protected abstract String specifyFHIRServerSubsystemName();
    protected abstract String specifyFHIRServerSubsystemVersion();

    @PostConstruct
    public void initialise(){
        this.targetEdgeAnswerService = specifyFHIRServerService();
        newRestfulGenericClient(deriveTargetEndpointDetails());
    }

    public String getTargetEdgeAnswerService() {
        return targetEdgeAnswerService;
    }

    public void setTargetEdgeAnswerService(String targetEdgeAnswerService) {
        this.targetEdgeAnswerService = targetEdgeAnswerService;
    }

    public String getFHIRServerSubsystemName(){
        return(specifyFHIRServerSubsystemName());
    }

    public String getFHIRServerSubsystemVersion(){
        return(specifyFHIRServerSubsystemVersion());
    }

    protected String deriveTargetEndpointDetails(){
        getLogger().debug(".deriveTargetEndpointDetails(): Entry");
        getLogger().trace(".deriveTargetEndpointDetails(): Target Subsystem Name --> {}, Target Subsystem Version --> {}", specifyFHIRServerSubsystemName(), specifyFHIRServerSubsystemVersion());
        List<TopologyNode> matchingNodes = topologyIM.nodeSearch(TopologyNodeTypeEnum.SUBSYSTEM, specifyFHIRServerSubsystemName(),  specifyFHIRServerSubsystemVersion());
        SubsystemTopologyNode targetSubsystem = (SubsystemTopologyNode)matchingNodes.get(0); // TODO this is brave and a bit stupid, so fix
        getLogger().trace(".deriveTargetEndpointDetails(): Target Subsystem (NodeElement) --> {}", targetSubsystem);

        IPCInterfaceDefinition requiredInterfaceDef = new IPCInterfaceDefinition();
        requiredInterfaceDef.setInterfaceFormalName(specifyRequiredInterfaceName());
        requiredInterfaceDef.setInterfaceFormalVersion(specifyRequiredInterfaceVersion());
        BusinessServiceTopologyNode businessService = mapTraversalHelpers.selectBusinessService(targetSubsystem, requiredInterfaceDef);
        DeploymentSiteTopologyNode site = mapTraversalHelpers.selectSite(businessService, siteKeyNames.getSiteA() ); // TODO this should be a comparison against what site I am in
        ClusterServiceTopologyNode clusterService = mapTraversalHelpers.selectClusterService(site, requiredInterfaceDef);

        IPCClusteredServerTopologyEndpoint endpoint = null;
        String dnsName = null;
        String portNumber = null;
        switch(targetSubsystem.getResilienceMode()){
            case RESILIENCE_MODE_MULTISITE:
            case RESILIENCE_MODE_KUBERNETES_MULTISITE:
            case RESILIENCE_MODE_KUBERNETES_CLUSTERED:
            case RESILIENCE_MODE_KUBERNETES_STANDALONE: {
                getLogger().trace(".deriveTargetEndpointDetails(): Is kubernetes based");
                endpoint = mapTraversalHelpers.selectClusterServiceEndpoint(clusterService, requiredInterfaceDef);
                dnsName = endpoint.getServiceDNSName();
                portNumber = Integer.toString(endpoint.getServicePortValue());
                getLogger().trace(".deriveTargetEndpointDetails(): clusterService --> {}", clusterService);
                break;
            }
            case RESILIENCE_MODE_CLUSTERED:
            case RESILIENCE_MODE_STANDALONE:
            default:{
                getLogger().trace(".deriveTargetEndpointDetails(): Is purely clustered or standalone based");
                PlatformTopologyNode platform = mapTraversalHelpers.selectPlatform(clusterService);
                ProcessingPlantTopologyNode processingPlant = mapTraversalHelpers.selectProcessingPlant(platform, requiredInterfaceDef);
                endpoint = mapTraversalHelpers.selectProcessingPlantEndpoint(processingPlant, requiredInterfaceDef);
                dnsName = endpoint.getHostDNSName(); // changed this from "getInterfaceDNSName()" to "getHostDNSName()"!
                portNumber = Integer.toString(endpoint.getPortValue());
                getLogger().trace(".deriveTargetEndpointDetails(): processingPlant --> {}", processingPlant);
            }
        }
        if(getLogger().isTraceEnabled()) {
            getLogger().trace(".deriveTargetEndpointDetails(): targetEndpointName --> {}, targetEndpointVersion --> {}", specifyRequiredInterfaceName(), specifyRequiredInterfaceVersion());
            getLogger().trace(".deriveTargetEndpointDetails(): targetEndpoint (EndpointElement) --> {}", endpoint);
        }
        String http_type = null;
        if(endpoint.isEncrypted()) {
            http_type = "https";
        } else {
            http_type = "http";
        }
        String endpointDetails = http_type + "://" + dnsName + ":" + portNumber + systemWideProperties.getPegacornInternalFhirResourceR4Path();
        getLogger().info(".deriveTargetEndpointDetails(): Exit, endpointDetails --> {}", endpointDetails);
        return(endpointDetails);
    }
}
