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
package net.fhirfactory.pegacorn.wups.archetypes.unmanaged;

import ca.uhn.fhir.parser.IParser;
import net.fhirfactory.pegacorn.core.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.pathway.ActivityID;
import net.fhirfactory.pegacorn.core.model.petasos.resilience.activitymatrix.sta.TransactionStatusElement;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoW;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.core.model.petasos.wup.WUPJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.wup.datatypes.WUPIdentifier;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.PetasosJobActivityStatusEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.core.model.transaction.valuesets.PegacornTransactionTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.FHIRElementTopicFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierDataTypeHelpers;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierFactory;
import net.fhirfactory.pegacorn.workshops.base.Workshop;
import net.fhirfactory.pegacorn.wups.archetypes.unmanaged.audit.TransactionalWUPAuditEntryManager;
import org.apache.camel.builder.RouteBuilder;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public abstract class NonResilientWithAuditTrailWUP extends RouteBuilder {

    private WorkUnitProcessorTopologyNode topologyNode;
    private WUPJobCard currentJobCard;
    private boolean isInitialised;
    private IParser parserR4;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private FHIRElementTopicFactory topicIDBuilder;

    @Inject
    private TransactionalWUPAuditEntryManager auditEntryManager;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private net.fhirfactory.pegacorn.util.FHIRContextUtility FHIRContextUtility;

    @Inject
    private PegacornIdentifierFactory pegacornIdentifierFactory;

    @Inject
    private PegacornIdentifierDataTypeHelpers identifierHelpers;

    public NonResilientWithAuditTrailWUP(){
        super();
        this.isInitialised = false;
    }

    abstract protected Logger getLogger();
    abstract protected String specifyWUPInstanceName();
    abstract protected String specifyWUPInstanceVersion();
    abstract protected Workshop specifyWorkshop();
    abstract protected void executePostConstructActivities();

//    abstract protected InternalFHIRClientServices getFHIRClientServices();

//    abstract public VirtualDBMethodOutcome synchroniseResource(ResourceType resourceType, Resource resource);

    @PostConstruct
    protected void initialise() {
        getLogger().debug(".initialise(): Entry");
        if (!isInitialised) {
            getLogger().trace(".initialise(): AccessBase is NOT initialised");
            this.parserR4 = FHIRContextUtility.getJsonParser();
            this.isInitialised = true;
            processingPlant.initialisePlant();
            buildWUPNodeElement();
            executePostConstructActivities();
        }
    }

    private void buildWUPNodeElement(){
        getLogger().debug(".buildWUPNodeElement(): Entry");
        WorkUnitProcessorTopologyNode wupNode = getTopologyFactory()
                .createWorkUnitProcessor(specifyWUPInstanceName(),specifyWUPInstanceVersion(), getWorkshop().getWorkshopNode(), ComponentTypeTypeEnum.WUP);
        getTopologyIM().addTopologyNode(specifyWorkshop().getWorkshopNode().getComponentFDN(), wupNode);
        wupNode.setResilienceMode(specifyWorkshop().getWorkshopNode().getResilienceMode());
        wupNode.setConcurrencyMode(specifyWorkshop().getWorkshopNode().getConcurrencyMode());
        this.topologyNode = wupNode;
    }

    //
    // Getters and Setters
    //

    public TopologyNodeFunctionFDN getNodeFunctionFDN(){
        return(topologyNode.getNodeFunctionFDN());
    }

    public TopologyNodeFunctionFDNToken getNodeFunctionFDNToken(){
        return(topologyNode.getNodeFunctionFDN().getFunctionToken());
    }

    public WUPIdentifier getNodeInstanceID(){
        WUPIdentifier wupID = new WUPIdentifier(topologyNode.getComponentFDN().getToken());
        return(wupID);
    }

    public WorkUnitProcessorTopologyNode getTopologyNode() {
        return topologyNode;
    }

    public boolean isInitialised() {
        return isInitialised;
    }

    public IParser getParserR4() {
        return parserR4;
    }

    public FHIRElementTopicFactory getTopicIDBuilder() {
        return topicIDBuilder;
    }

    public net.fhirfactory.pegacorn.util.FHIRContextUtility getFHIRContextUtility() {
        return FHIRContextUtility;
    }

    public ProcessingPlantInterface getProcessingPlant() {
        return processingPlant;
    }

    protected TransactionalWUPAuditEntryManager getAuditEntryManager(){
        return(auditEntryManager);
    }

    protected TopologyIM getTopologyIM(){
        return(topologyIM);
    }

    protected PegacornTopologyFactoryInterface getTopologyFactory(){
        return(processingPlant.getTopologyFactory());
    }

    protected Workshop getWorkshop(){
        return(specifyWorkshop());
    }

    protected String getWUPInstanceVersion(){
        return(specifyWUPInstanceVersion());
    }


    protected void initialiseWUPActivity(boolean resilientActivity){
        ActivityID activityID = new ActivityID();
        activityID.setResilientActivity(resilientActivity);
        activityID.setPresentWUPFunctionToken(getNodeFunctionFDN().getFunctionToken());
        activityID.setPresentWUPIdentifier(getNodeInstanceID());
        ConcurrencyModeEnum concurrencyMode = this.topologyNode.getConcurrencyMode();
        ResilienceModeEnum resilienceMode = this.topologyNode.getResilienceMode();
        Date nowDate = Date.from(Instant.now());
        WUPJobCard jobCard = new WUPJobCard(activityID, PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING, PetasosJobActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING,concurrencyMode,resilienceMode,nowDate);
        this.currentJobCard = jobCard;
    }

    protected void finaliseWUPActivity(){
        this.currentJobCard = null;
    }

    /**
     *
     * @param resourceID
     * @param resourceType
     * @param resourceIdentifier
     * @param fhirResource
     * @param action
     * @return
     */
    protected TransactionStatusElement beginRESTfulTransaction(IdType resourceID, String resourceType, Identifier resourceIdentifier, Resource fhirResource, PegacornTransactionTypeEnum action){
        getLogger().debug(".beginRESTfulTransaction(): Entry, resourceID->{}, resourceType->{}, resourceIdentifier->{}, fhirResource->{}, actopm->{}", resourceID, resourceType, resourceIdentifier, fhirResource, action);
        String resourceKey = identifierHelpers.generatePrintableInformationFromIdentifier(resourceIdentifier);
        initialiseWUPActivity(false);
        TransactionStatusElement transactionStatus = auditEntryManager.beginTransaction(this.getCurrentJobCard(), resourceKey, fhirResource.fhirType(), fhirResource, action );
        return(transactionStatus);
    }

    /**
     *
     * @param uow
     * @param action
     * @return
     */
    protected TransactionStatusElement beginAPITransaction(UoW uow, PegacornTransactionTypeEnum action){
        initialiseWUPActivity(false);
        TransactionStatusElement transactionStatus = auditEntryManager.beginTransaction(this.getCurrentJobCard(),uow, action);
        return(transactionStatus);
    }

    /**
     *
     * @param transaction
     */
    protected void endRESTfulTransaction(TransactionStatusElement transaction){
        auditEntryManager.endTransaction(transaction);
        finaliseWUPActivity();
    }

    /**
     *
     * @param transaction
     */
    protected void endAPITransaction(TransactionStatusElement transaction){
        auditEntryManager.endTransaction(transaction);
        finaliseWUPActivity();
    }


    protected TransactionStatusElement beginSearchTransaction(String resourceType, String resourceVersion, Map<Property, Serializable> parameterSet){
        initialiseWUPActivity(false);
        Set<Property> parameterKeys = parameterSet.keySet();
        String searchParameters = new String();
        int totalCount = parameterKeys.size();
        int counter = 0;
        for(Property currentKey: parameterKeys){
            String parameterValue = parameterSet.get(currentKey).toString();
            searchParameters = searchParameters + currentKey+"="+parameterValue;
            if(counter < (totalCount -1)){
                searchParameters = searchParameters+"&";
            }
        }
        UoWPayload newPayload = new UoWPayload();
        newPayload.setPayload("Resource="+resourceType+"?search=");
        DataParcelTypeDescriptor typeDescriptor = topicIDBuilder.newTopicToken(resourceType, resourceVersion);
        DataParcelManifest parcelManifest = new DataParcelManifest();
        parcelManifest.setContentDescriptor(typeDescriptor);
        parcelManifest.setDataParcelType(DataParcelTypeEnum.SEARCH_QUERY_DATA_PARCEL_TYPE);
        newPayload.setPayloadManifest(parcelManifest);
        UoW uow = new UoW(newPayload);
        TransactionStatusElement transactionStatus = auditEntryManager.beginTransaction(this.getCurrentJobCard(),uow, PegacornTransactionTypeEnum.SEARCH);
        return(transactionStatus);
    }

    protected void endSearchTransaction(Bundle resultSet, TransactionStatusElement startingTransaction){
        String searchAnswerSummary = buildSearchResultString(resultSet);
        UoWPayload payload = new UoWPayload();
        payload.setPayload(searchAnswerSummary);
        DataParcelManifest parcelManifest = startingTransaction.getUnitOfWork().getPayloadTopicID();
        parcelManifest.setDataParcelType(DataParcelTypeEnum.SEARCH_RESULT_DATA_PARCEL_TYPE);
        payload.setPayloadManifest(parcelManifest);
        startingTransaction.getUnitOfWork().getEgressContent().addPayloadElement(payload);
//        auditEntryManager.endTransaction(searchAnswerCount, resourceType , null,action,success,startingTransaction,getNodeInstanceID(),getWUPInstanceVersion());
    }

    //
    // Helpers
    //

    private String buildSearchResultString(Bundle searchResult){
        if(searchResult == null) {
            return("Search Failed");
        }
        int resultCount = searchResult.getTotal();
        if(resultCount == 0){
            return("Search Succeeded: Result Count = 0");
        }
        String resultString = "Search Succeeded: Result Count = " + resultCount + ": Entries --> ";
        for(Bundle.BundleEntryComponent currentBundleEntry: searchResult.getEntry()){
            Resource currentResource = currentBundleEntry.getResource();
            if(currentResource.hasId()){
                resultString = resultString + currentResource.getId();
            } else {
                resultString = resultString + "[Resource Has No Id]";
            }
            if(resultCount > 1) {
                resultString = resultString + ", ";
            }
            resultCount -= 1;
        }
        return(resultString);
    }

    public WUPJobCard getCurrentJobCard() {
        return currentJobCard;
    }

    public void setCurrentJobCard(WUPJobCard currentJobCard) {
        this.currentJobCard = currentJobCard;
    }

    public void setTopologyNode(WorkUnitProcessorTopologyNode topologyNode) {
        this.topologyNode = topologyNode;
    }
}