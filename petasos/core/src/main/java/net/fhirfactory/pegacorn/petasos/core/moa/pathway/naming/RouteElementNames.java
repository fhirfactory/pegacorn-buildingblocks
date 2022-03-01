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

package net.fhirfactory.pegacorn.petasos.core.moa.pathway.naming;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.componentid.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;


/**
 * 
 * @author Mark A. Hunter
 * @since 2020-06-01
 */
public class RouteElementNames {
    private static final Logger LOG = LoggerFactory.getLogger(RouteElementNames.class);
    protected Logger getLogger(){
        return(LOG);
    }

    private TopologyNodeFunctionFDNToken nodeFunctionFDNToken;
    private boolean mustBeDirect;
    private String wupTypeName;
    private String wupVersion;
    private String sedaParameters;
    private static final String INTRA_FUNCTION_DIRECT_TYPE = "direct:";
    private static final String DIRECT_INTER_FUNCTION_DIRECT_TYPE = "direct:";
    private static final String SEDA_INTER_FUNCTION_DIRECT_TYPE = "seda://";

    public RouteElementNames(TopologyNodeFunctionFDNToken functionToken, boolean mustBeDirect, String sedaParameters){
        getLogger().debug(".RouteElementNames(): Entry, functionToken->{}, mustBeDirect->{}", functionToken, mustBeDirect);
        this.nodeFunctionFDNToken = functionToken;
        this.wupTypeName = simplifyName();
        this.mustBeDirect = mustBeDirect;
        this.sedaParameters = sedaParameters;
    }

    public RouteElementNames(TopologyNodeFunctionFDNToken functionToken){
        getLogger().debug(".RouteElementNames(): Entry, functionToken->{}", functionToken);
        this.nodeFunctionFDNToken = functionToken;
        this.wupTypeName = simplifyName();
        this.mustBeDirect = false;
        this.sedaParameters = null;
    }

    public String simplifyName(){
        getLogger().debug(".simplifyName(): Entry, this.nodeFDNToken --> {}", this.nodeFunctionFDNToken);
        TopologyNodeFunctionFDN wupFunctionFDN = new TopologyNodeFunctionFDN(this.nodeFunctionFDNToken);
        getLogger().trace(".simplifyName(): wupFunctionFDN --> {}", wupFunctionFDN);
        TopologyNodeRDN processingPlantRDN = wupFunctionFDN.extractRDNForNodeType(PegacornSystemComponentTypeTypeEnum.PROCESSING_PLANT);
        getLogger().trace(".simplifyName(): processingPlantRDN (RDN) --> {} ", processingPlantRDN);
        TopologyNodeRDN workshopRDN = wupFunctionFDN.extractRDNForNodeType(PegacornSystemComponentTypeTypeEnum.WORKSHOP);
        getLogger().trace(".simplifyName(): workshopRDN (RDN) --> {} ", workshopRDN);
        TopologyNodeRDN wupFunctionRDN = wupFunctionFDN.extractRDNForNodeType(PegacornSystemComponentTypeTypeEnum.WUP);
        getLogger().trace(".simplifyName(): wupFunctionRDN (RDN) --> {}", wupFunctionRDN);
        String nodeVersion = wupFunctionRDN.getNodeVersion();
        String nodeVersionSimplified = nodeVersion.replace(".","");
        String wupName = processingPlantRDN.getNodeName()+"."+workshopRDN.getNodeName()+"."+wupFunctionRDN.getNodeName()+"."+nodeVersionSimplified;
        getLogger().trace(".simplifyName(): wupName (String) --> {}", wupName);
        return(wupName);
    }

    public String getWupTypeName(){
        return(this.wupTypeName);
    }
    
    public String getRouteCoreWUP(){
        return(this.wupTypeName+".WUP.Core");
    }

    public String getEndPointWUPContainerIngresProcessorIngres() {
        getLogger().debug(".getEndPointWUPContainerIngresProcessorIngres(): Entry");
        String endpointName;
        if(this.mustBeDirect){
            endpointName = DIRECT_INTER_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresProcessor.Ingres";
        } else {
            if(StringUtils.isEmpty(this.sedaParameters)) {
                endpointName = SEDA_INTER_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresProcessor.Ingres";
            } else {
                endpointName = SEDA_INTER_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresProcessor.Ingres" + sedaParameters;
            }
        }
        return(endpointName);
    }

    public String getEndPointWUPContainerIngresProcessorEgress() {
        getLogger().debug(".getEndPointWUPContainerIngresProcessorEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresProcessor.Egress";
        return(endpointName);
    }

    public String getEndPointWUPContainerIngresGatekeeperIngres() {
        getLogger().debug(".getEndPointWUPContainerIngresGatekeeperIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.IngresGatekeeper.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPIngresConduitIngres() {
        getLogger().debug(".getEndPointWUPIngresConduitIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUP.IngresConduit.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPIngres() {
        getLogger().debug(".getEndPointWUPIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUP.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPEgress() {
        getLogger().debug(".getEndPointWUPEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUP.Egress";
        return(endpointName);
    }

    public String getEndPointWUPEgressConduitEgress() {
        getLogger().debug(".getEndPointWUPEgressConduitEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUP.EgressConduit.Egress";
        return(endpointName);
    }

    public String getEndPointWUPContainerInterceptionRedirectPointIngres() {
        getLogger().debug(".getEndPointWUPContainerInterceptionRedirectPointIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.InterceptionRedirectPoint.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPContainerEgressMetadataProcessorIngres() {
        getLogger().debug(".getEndPointWUPContainerEgressMetadataProcessorIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.MetadataProcessor.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPContainerInterceptionReturnPointIngres() {
        getLogger().debug(".getEndPointWUPContainerInterceptionReturnPointIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.InterceptionReturnPoint.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPContainerEgressProcessorIngres() {
        getLogger().debug(".getEndPointWUPContainerEgressProcessorIngres(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.EgressProcessor.Ingres";
        return(endpointName);
    }

    public String getEndPointWUPContainerEgressProcessorEgress() {
        getLogger().debug(".getEndPointWUPContainerEgressProcessorEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".WUPContainer.EgressProcessor.Egress";
        return(endpointName);
    }

    public String getEndpointTaskOutcomeCollection() {
        getLogger().debug(".getEndPointInterchangePayloadTransformerIngres(): Entry");
        String endpointName = PetasosPropertyConstants.TASK_OUTCOME_COLLECTION_QUEUE;
        return(endpointName);
    }

    public String getEndpointNewTaskCreationCompletion() {
        getLogger().debug(".getEndPointInterchangePayloadTransformerEgress(): Entry");
        String endpointName = PetasosPropertyConstants.TASK_DISTRIBUTION_QUEUE;
        return(endpointName);
    }

    public String getEndPointTaskDistributionQueueIngres() {
        getLogger().debug(".getEndPointInterchangeRouterIngres(): Entry");
        String endpointName = PetasosPropertyConstants.TASK_DISTRIBUTION_QUEUE;
        return(endpointName);
    }

    public String getEndPointInterchangeRouterEgress() {
        getLogger().debug(".getEndPointInterchangeRouterEgress(): Entry");
        String endpointName = INTRA_FUNCTION_DIRECT_TYPE + wupTypeName + ".Interchange.Router.Egress";
        return(endpointName);
    }

    public String getRouteIngresProcessorEgress2IngresGatekeeperIngres() {
        getLogger().debug(".getRouteIngresProcessorEgress2IngresGatekeeperIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.IP.E-To-" + wupTypeName +".WUPC.IG.I" ;
        return(endpointName);
    }

    public String getRouteIngresConduitIngres2WUPIngres() {
        getLogger().debug(".getRouteIngresConduitIngres2WUPIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUP.IC.I-To-" + wupTypeName +".WUP.I" ;
        return(endpointName);
    }

    public String getRouteWUPEgress2WUPEgressConduitEgress() {
        getLogger().debug(".getRouteWUPEgress2WUPEgressConduitEgress(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUP.E-To-" + wupTypeName +".WUP.EC.E" ;
        return(endpointName);
    }

    public String getRouteWUPEgressConduitEgress2WUPEgressProcessorIngres() {
        getLogger().debug(".getRouteWUPEgressConduitEgress2WUPEgressProcessorIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUP.EC.E-To-" + wupTypeName +".WUPC.EP.I" ;
        return(endpointName);
    }

    public String getRouteWUPEgressProcessorEgress2WUPEgressGatekeeperIngres() {
        getLogger().debug(".getRouteWUPEgressProcessorEgress2WUPEgressGatekeeperIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUP.EP.E-To-" + wupTypeName +".WUPC.EG.I" ;
        return(endpointName);
    }

    public String getRouteInterchangePayloadTransformerEgress2InterchangePayloadRouterIngres() {
        getLogger().debug(".getRouteInterchangePayloadTransformerEgress2InterchangePayloadRouterIngres(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".IC.PT.E-To-" + wupTypeName +".IC.R.I" ;
        return(endpointName);
    }

    public String getRouteWUPContainerIngressProcessor() {
        getLogger().debug(".getRouteWUPContainerIngressProcessor(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.IP.I-To-" + wupTypeName +".WUPC.IP.E" ;
        return(endpointName);
    }

    public String getRouteWUPContainerIngresGateway() {
        getLogger().debug(".getRouteWUPContainerIngresGateway(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.IG.I-To-" + wupTypeName +".WUPC.IG.E" ;
        return(endpointName);
    }

    public String getRouteWUPContainerEgressGateway() {
        getLogger().debug(".getRouteWUPContainerEgressGateway(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.EG.I-To-" + wupTypeName +".WUPC.EG.E" ;
        return(endpointName);
    }

    public String getRouteWUPContainerEgressProcessor() {
        getLogger().debug(".getRouteWUPContainerEgressProcessor(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".WUPC.EP.I-To-" + wupTypeName +".WUPC.EP.E" ;
        return(endpointName);
    }

    public String getRouteTaskOutcomeCollectionAndProcessing(){
        getLogger().debug(".getRouteInterchangePayloadTransformer(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".IC.PT.I-To-" + wupTypeName +".IC.PT.E" ;
        return(endpointName);
    }

    public String getRouteInterchangeRouter(){
        getLogger().debug(".getRouteInterchangeRouter(): Entry");
        String endpointName = "FROM-" + wupTypeName + ".IC.R.I-To-" + wupTypeName +".IC.R.E" ;
        return(endpointName);
    }
}
