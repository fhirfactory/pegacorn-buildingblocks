package net.fhirfactory.pegacorn.platform.edge.answer.resourceproxies.common;

import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.bundle.BundleContentHelper;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionMethodOutcome;
import net.fhirfactory.pegacorn.platform.edge.model.common.ResourceAccessorInterfaceBase;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

public abstract class EdgeProxyBase {
    boolean isInitialised;

    public EdgeProxyBase(){
        isInitialised = false;
    }

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private BundleContentHelper bundleContentHelper;

    protected BundleContentHelper getBundleContentHelper(){
        return(bundleContentHelper);
    }

    @PostConstruct
    private void initialisePatientProxy(){
        if(!this.isInitialised()) {
            getLogger().info("LadonEdgeProxyBase::initialiseProxy(): Entry, Initialising Services");
            getProcessingPlant().initialisePlant();
            getActualResourceAccessor().initialiseServices();
            this.setInitialised(true);
            getLogger().debug("LadonEdgeProxyBase::initialiseProxy(): Exit");
        }
    }

    public boolean isInitialised() {
        return isInitialised;
    }

    public void setInitialised(boolean initialised) {
        isInitialised = initialised;
    }

    public ProcessingPlantInterface getProcessingPlant() {
        return (processingPlant);
    }

    abstract protected Logger getLogger();
    abstract protected ResourceAccessorInterfaceBase specifyActualResourceAccessor();
    abstract protected TransactionMethodOutcome getResource(IdType id);
    abstract protected TransactionMethodOutcome createResource(Resource resource);
    abstract protected TransactionMethodOutcome updateResource(Resource resource);
    abstract protected TransactionMethodOutcome deleteResource(IdType id);

    protected ResourceAccessorInterfaceBase getActualResourceAccessor(){
        return(specifyActualResourceAccessor());
    }

    protected Bundle searchProcessHasFailed(String searchMethod){
        getLogger().info(searchMethod + ": A resource search has failed (not just not finding any matching resources, but the search process itself)");
        Bundle outputBundle = new Bundle();
        outputBundle.setType(Bundle.BundleType.SEARCHSET);
        outputBundle.setTimestamp(Date.from(Instant.now()));
        outputBundle.setTotal(0);
        getLogger().debug("findByIdentifier(): Exit, search failed (not just not finding any matching resources, but the search process itself faield)!");
        return(outputBundle);
    }

}
