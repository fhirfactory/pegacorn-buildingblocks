package net.fhirfactory.pegacorn.workshops;

import net.fhirfactory.pegacorn.deployment.topology.model.nodes.DefaultWorkshopSetEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EdgeWorkshop extends Workshop {
    private static final Logger LOG = LoggerFactory.getLogger(EdgeWorkshop.class);

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyWorkshopName() {
        return (DefaultWorkshopSetEnum.EDGE_WORKSHOP.getWorkshop());
    }

    @Override
    protected String specifyWorkshopVersion() {
        return (getProcessingPlant().getProcessingPlantNode().getNodeRDN().getNodeVersion());
    }

    @Override
    protected void invokePostConstructInitialisation() {

    }
}
