package net.fhirfactory.pegacorn.internal.esr.brokers;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.CommonIdentifierESDTTypes;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.HealthcareServiceESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.brokers.HealthcareServiceESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.HealthcareServiceESRCache;

/**
 * Health Care Service search tests.
 * 
 * @author Brendan Douglas
 *
 */
@EnableAutoWeld
@AddBeanClasses({ HealthcareServiceESRBroker.class, HealthcareServiceESRCache.class, CommonIdentifierESDTTypes.class })
public class HealthCareServiceESRBrokerSearchTest extends BaseESRBrokerSearchTest {
    
    private static final String PREFIX = "Health Care Service";

    @Inject
    private HealthcareServiceESRBroker broker;

    
    /**
     * Create a health care service.
     * 
     * @param shortName
     * @param longName
     * @return
     */
    public HealthcareServiceESR createResource(String shortName, String longName, char charToAdd) {
        HealthcareServiceESR healthCareService = new HealthcareServiceESR();

        healthCareService.setDisplayName(shortName);

        CommonIdentifierESDTTypes identifierTypes = new CommonIdentifierESDTTypes();

        IdentifierESDT shortNameBasedIdentifier = new IdentifierESDT();
        shortNameBasedIdentifier.setType(identifierTypes.getShortName());
        shortNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        shortNameBasedIdentifier.setValue(shortName);
        shortNameBasedIdentifier.setLeafValue(null);
        healthCareService.getIdentifiers().add(shortNameBasedIdentifier);
        healthCareService.assignSimplifiedID(true, identifierTypes.getShortName(), IdentifierESDTUseEnum.USUAL);

        IdentifierESDT longNameBasedIdentifier = new IdentifierESDT();
        longNameBasedIdentifier.setType(identifierTypes.getLongName());
        longNameBasedIdentifier.setUse(IdentifierESDTUseEnum.SECONDARY);
        longNameBasedIdentifier.setValue(longName);
        longNameBasedIdentifier.setLeafValue(null);
        healthCareService.getIdentifiers().add(longNameBasedIdentifier);

        return healthCareService;
    }
    
    
    @Override
    protected ESRBroker getBroker() {
        return broker;
    }
    
    
    @Override
    protected String getLongNamePrefix() {
        return PREFIX;
    }
    
    
    @Override
    protected String getShortNamePrefix() {
        return PREFIX;
    }
    
    
    @Override
    protected void storeResource(ExtremelySimplifiedResource resource) throws ResourceInvalidSearchException {
        broker.createHealthCareService((HealthcareServiceESR)resource);
    } 
}