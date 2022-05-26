package net.fhirfactory.pegacorn.core.model.internal.resources.simple;

import net.fhirfactory.pegacorn.core.model.internal.resources.simple.common.ExtremelySimplifiedResource;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunicateUserESR extends ExtremelySimplifiedResource {
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateUserESR.class);

    @Override
    protected Logger getLogger(){return(LOG);}

    @Override
    protected ResourceType specifyResourceType() {
        return (ResourceType.Practitioner);
    }

}
