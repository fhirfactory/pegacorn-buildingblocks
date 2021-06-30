package net.fhirfactory.pegacorn.internal.esr.brokers;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.RoleESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.internals.esr.brokers.RoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.RoleESRCache;

/**
 * Role search tests.
 * 
 * @author Brendan Douglas
 *
 */
@EnableAutoWeld
@AddBeanClasses({ RoleESRBroker.class, RoleESRCache.class })
public class RoleESRBrokerSearchTest extends BaseESRBrokerSearchTest {
    
    private static final String PREFIX = "Role";

    @Inject
    private RoleESRBroker broker;

    
    /**
     * Create a role.
     * 
     * @param shortName
     * @param longName
     * @return
     */
    public RoleESR createResource(String shortName, String longName, char charToAdd) {
        RoleESR role = new RoleESR();

        role.setDisplayName(shortName);


        IdentifierESDT shortNameBasedIdentifier = new IdentifierESDT();
        shortNameBasedIdentifier.setType(IdentifierType.SHORT_NAME);
        shortNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        shortNameBasedIdentifier.setValue(shortName);
        shortNameBasedIdentifier.setLeafValue(null);
        role.getIdentifiers().add(shortNameBasedIdentifier);
        role.assignSimplifiedID(true, IdentifierType.SHORT_NAME, IdentifierESDTUseEnum.USUAL);

        IdentifierESDT longNameBasedIdentifier = new IdentifierESDT();
        longNameBasedIdentifier.setType(IdentifierType.LONG_NAME);
        longNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        longNameBasedIdentifier.setValue(longName);
        longNameBasedIdentifier.setLeafValue(null);
        role.getIdentifiers().add(longNameBasedIdentifier);

        return role;
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
        broker.createRole((RoleESR) resource);
    } 
}
