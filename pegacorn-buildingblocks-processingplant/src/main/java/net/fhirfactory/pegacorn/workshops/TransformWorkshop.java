package net.fhirfactory.pegacorn.workshops;

import net.fhirfactory.pegacorn.deployment.topology.model.nodes.DefaultWorkshopSetEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransformWorkshop extends Workshop {
    private static final Logger LOG = LoggerFactory.getLogger(TransformWorkshop.class);

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyWorkshopName() {
        return (DefaultWorkshopSetEnum.TRANSFORM_WORKSHOP.getWorkshop());
    }

    @Override
    protected String specifyWorkshopVersion() {
        return (getProcessingPlant().getProcessingPlantNode().getNodeRDN().getNodeVersion());
    }

    @Override
    protected void invokePostConstructInitialisation() {

    }
}
