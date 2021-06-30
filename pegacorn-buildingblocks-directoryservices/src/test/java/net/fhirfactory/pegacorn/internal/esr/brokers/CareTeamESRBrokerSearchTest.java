package net.fhirfactory.pegacorn.internal.esr.brokers;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.CareTeamESR;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.internals.esr.brokers.CareTeamESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.CareTeamESRCache;

/**
 * Care team search tests.
 * 
 * @author Brendan Douglas
 *
 */
@EnableAutoWeld
@AddBeanClasses({ CareTeamESRBroker.class, CareTeamESRCache.class, PractitionerRoleESRBroker.class })
public class CareTeamESRBrokerSearchTest extends BaseESRBrokerSearchTest {
    
    private static final String PREFIX = "Care Team";

    @Inject
    private CareTeamESRBroker broker;

    
    /**
     * Create a care team.
     * 
     * @param shortName
     * @param longName
     * @return
     */
    public CareTeamESR createResource(String shortName, String longName, char charToAdd) {
        CareTeamESR careTeam = new CareTeamESR();

        careTeam.setDisplayName(shortName);

        IdentifierESDT shortNameBasedIdentifier = new IdentifierESDT();
        shortNameBasedIdentifier.setType(IdentifierType.SHORT_NAME);
        shortNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        shortNameBasedIdentifier.setValue(shortName);
        shortNameBasedIdentifier.setLeafValue(null);
        careTeam.getIdentifiers().add(shortNameBasedIdentifier);
        careTeam.assignSimplifiedID(true, IdentifierType.SHORT_NAME, IdentifierESDTUseEnum.USUAL);

        IdentifierESDT longNameBasedIdentifier = new IdentifierESDT();
        longNameBasedIdentifier.setType(IdentifierType.LONG_NAME);
        longNameBasedIdentifier.setUse(IdentifierESDTUseEnum.SECONDARY);
        longNameBasedIdentifier.setValue(longName);
        longNameBasedIdentifier.setLeafValue(null);
        careTeam.getIdentifiers().add(longNameBasedIdentifier);

        return careTeam;
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
        broker.createCareTeam((CareTeamESR)resource);
    } 
}
