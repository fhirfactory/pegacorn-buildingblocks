package net.fhirfactory.pegacorn.petasos.core.sta.wup.common;

import ca.uhn.fhir.parser.IParser;
import net.fhirfactory.pegacorn.core.interfaces.topology.PegacornTopologyFactoryInterface;
import net.fhirfactory.pegacorn.core.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFunctionFDNToken;
import net.fhirfactory.pegacorn.core.model.petasos.wup.PetasosTaskJobCard;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.core.model.topology.nodes.WorkshopTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.petasos.audit.brokers.STAServicesAuditBroker;
import net.fhirfactory.pegacorn.util.FHIRContextUtility;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public abstract class GenericSTAWUPTemplate {
    private WorkUnitProcessorTopologyNode wup;
    private PetasosTaskJobCard wupJobCard;
    private boolean isInitialised;

    private IParser parserR4;

    @Inject
    STAServicesAuditBroker servicesBroker;

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private FHIRContextUtility fhirContextUtility;

    public GenericSTAWUPTemplate() {
        isInitialised = false;
    }

    abstract protected String specifySTAClientName();
    abstract protected String specifySTAClientVersion();
    abstract protected WUPArchetypeEnum specifyWUPArchetype();
    abstract protected Logger getLogger();
    abstract protected WorkshopTopologyNode specifyWorkshop();
    abstract protected ProcessingPlantInterface specifyProcessingPlant();
    abstract protected PegacornTopologyFactoryInterface specifyTopologyFactory();

    @PostConstruct
    protected void initialise() {
        getLogger().debug(".initialise(): Entry");
        if (!isInitialised) {
            getLogger().trace(".initialise(): AccessBase is NOT initialised");
            this.parserR4 = fhirContextUtility.getJsonParser();
            this.wup = buildSTAClientNode();
            this.isInitialised = true;
        }
    }

    protected ProcessingPlantInterface getProcessingPlant(){return(specifyProcessingPlant());}
    protected WorkshopTopologyNode getWorkshop(){return(specifyWorkshop());}
    protected PegacornTopologyFactoryInterface getTopologyFactory(){return(specifyTopologyFactory());}
    protected WUPArchetypeEnum getWUPArchetype(){return(specifyWUPArchetype());}

    /**
     * This function builds the Deployment Topology node (a WUP) for the
     * Synchronous Transaction Activity Client Service.
     * <p>
     * It uses the Name (specifyPersistenceServiceName()) defined in the subclass as part
     * of the Identifier and then registers with the Topology Services.
     *
     * @return The NodeElement representing the WUP which this code-set is
     * fulfilling.
     */
    private WorkUnitProcessorTopologyNode buildSTAClientNode() {
        getLogger().debug(".buildSTAClientNode(): Entry");
        TopologyNodeFDN staClientTypeFDN = new TopologyNodeFDN(getWorkshop().getComponentFDN());
        getLogger().trace(".buildSTAClientNode(): Now construct the Work Unit Processing Node");
        WorkUnitProcessorTopologyNode wup = getTopologyFactory().createWorkUnitProcessor(specifySTAClientName(), specifySTAClientVersion(),getWorkshop(), ComponentTypeTypeEnum.WUP);
        getLogger().trace(".buildSTAClientNode(): Constructing WUP Node, Setting Concurrency Mode");
        wup.setConcurrencyMode(getWorkshop().getConcurrencyMode());
        getLogger().trace(".buildSTAClientNode(): Constructing WUP Node, Setting Resillience Mode");
        wup.setResilienceMode(getWorkshop().getResilienceMode());
        getLogger().trace(".buildSTAClientNode(): Now registering the Node");
        getTopologyIM().addTopologyNode(getWorkshop().getComponentFDN(), wup);
        getLogger().debug(".buildSTAClientNode(): Exit, buildSTAClientNode (NodeElementIdentifier) --> {}", wup);
        return (wup);
    }

    public TopologyNodeFunctionFDNToken getApiClientNodeFunction() {
        return getWUP().getNodeFunctionFDN().getFunctionToken();
    }

    public WorkUnitProcessorTopologyNode getWUP() {
        return wup;
    }

    public String getWUPName() {
        return (getWUP().getComponentRDN().getNodeName());
    }

    public PetasosTaskJobCard getWUPJobCard() {
        return (wupJobCard);
    }

    public String getWUPVersion() {
        return (getWUP().getComponentRDN().getNodeVersion());
    }

    public TopologyIM getTopologyIM() {
        return topologyIM;
    }

}
