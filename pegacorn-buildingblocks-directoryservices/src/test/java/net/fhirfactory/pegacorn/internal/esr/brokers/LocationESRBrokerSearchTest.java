package net.fhirfactory.pegacorn.internal.esr.brokers;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.LocationESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.internals.esr.brokers.LocationESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.LocationESRCache;

/**
 * Location search tests.
 * 
 * @author Brendan Douglas
 *
 */
@EnableAutoWeld
@AddBeanClasses({ LocationESRBroker.class, LocationESRCache.class})
public class LocationESRBrokerSearchTest extends BaseESRBrokerSearchTest {
    
    private static final String PREFIX = "Location";

    @Inject
    private LocationESRBroker broker;

    
    /**
     * Create a location.
     * 
     * @param shortName
     * @param longName
     * @return
     */
    public LocationESR createResource(String shortName, String longName, char charToAdd) {
        LocationESR location = new LocationESR();

        location.setDisplayName(shortName);


        IdentifierESDT shortNameBasedIdentifier = new IdentifierESDT();
        shortNameBasedIdentifier.setType(IdentifierType.SHORT_NAME);
        shortNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        shortNameBasedIdentifier.setValue(shortName);
        shortNameBasedIdentifier.setLeafValue(null);
        location.getIdentifiers().add(shortNameBasedIdentifier);
        location.assignSimplifiedID(true, IdentifierType.SHORT_NAME, IdentifierESDTUseEnum.USUAL);

        IdentifierESDT longNameBasedIdentifier = new IdentifierESDT();
        longNameBasedIdentifier.setType(IdentifierType.LONG_NAME);
        longNameBasedIdentifier.setUse(IdentifierESDTUseEnum.OFFICIAL);
        longNameBasedIdentifier.setValue(longName);
        longNameBasedIdentifier.setLeafValue(null);
        location.getIdentifiers().add(longNameBasedIdentifier);

        return location;
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
        broker.createLocationDE((LocationESR) resource);
    } 
}
