package net.fhirfactory.pegacorn.wups.archetypes.unmanaged;

import ca.uhn.fhir.parser.IParser;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.components.dataparcel.valuesets.DataParcelTypeEnum;
import net.fhirfactory.pegacorn.components.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.FHIRElementTopicFactory;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierDataTypeHelpers;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier.PegacornIdentifierFactory;
import net.fhirfactory.pegacorn.petasos.model.pathway.ActivityID;
import net.fhirfactory.pegacorn.petasos.model.resilience.activitymatrix.sta.TransactionStatusElement;
import net.fhirfactory.pegacorn.petasos.model.uow.UoW;
import net.fhirfactory.pegacorn.petasos.model.uow.UoWPayload;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPActivityStatusEnum;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPIdentifier;
import net.fhirfactory.pegacorn.petasos.model.wup.WUPJobCard;
import net.fhirfactory.pegacorn.workshops.base.Workshop;
import net.fhirfactory.pegacorn.wups.archetypes.unmanaged.audit.TransactionalWUPAuditEntryManager;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public abstract class NonResilientWithAuditTrailWUP {

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
        this.isInitialised = false;
    }

    abstract protected Logger getLogger();
    abstract protected String specifyWUPInstanceName();
    abstract protected String specifyWUPInstanceVersion();
    abstract protected Workshop specifyWorkshop();

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
        }
    }

    private void buildWUPNodeElement(){
        getLogger().debug(".buildWUPNodeElement(): Entry");
        WorkUnitProcessorTopologyNode wupNode = getTopologyFactory()
                .createWorkUnitProcessor(specifyWUPInstanceName(),specifyWUPInstanceVersion(), getWorkshop().getWorkshopNode(), TopologyNodeTypeEnum.WUP);
        getTopologyIM().addTopologyNode(specifyWorkshop().getWorkshopNode().getNodeFDN(), wupNode);
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
        WUPIdentifier wupID = new WUPIdentifier(topologyNode.getNodeFDN().getToken());
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
        WUPJobCard jobCard = new WUPJobCard(activityID, WUPActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING,WUPActivityStatusEnum.WUP_ACTIVITY_STATUS_EXECUTING,concurrencyMode,resilienceMode,nowDate);
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
    protected TransactionStatusElement beginRESTfulTransaction(IdType resourceID, String resourceType, Identifier resourceIdentifier, Resource fhirResource, TransactionTypeEnum action){
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
    protected TransactionStatusElement beginAPITransaction(UoW uow, TransactionTypeEnum action){
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
        TransactionStatusElement transactionStatus = auditEntryManager.beginTransaction(this.getCurrentJobCard(),uow, TransactionTypeEnum.SEARCH);
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
