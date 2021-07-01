package net.fhirfactory.pegacorn.internal.esr.brokers;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.resources.PractitionerRoleESR;
import net.fhirfactory.buildingblocks.esr.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.deployment.communicate.matrix.SystemManagedRoomNames;
import net.fhirfactory.pegacorn.internals.esr.brokers.GroupESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.MatrixRoomESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.RoleCategoryESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.CareTeamPractitionerRoleMapCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerRoleESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerRoleMapCache;

/**
 * Practitioner Role search tests.
 * 
 * @author Brendan Douglas
 *
 */
@EnableAutoWeld
@AddBeanClasses({ PractitionerRoleESRBroker.class, PractitionerRoleESRCache.class,GroupESRBroker.class, PractitionerRoleMapCache.class, PractitionerRoleMapCache.class, CareTeamPractitionerRoleMapCache.class, SystemManagedRoomNames.class, MatrixRoomESRBroker.class, RoleCategoryESRBroker.class })
public class PractitionerRoleESRBrokerSearchTest extends BaseESRBrokerSearchTest {
    
    private static final String PREFIX = "Practitioner Role";

    @Inject
    private PractitionerRoleESRBroker broker;

    
    /**
     * Create a practitioner role.
     * 
     * @param shortName
     * @param longName
     * @return
     */
    public PractitionerRoleESR createResource(String shortName, String longName, char charToAdd) {
        PractitionerRoleESR practitionerRole = new PractitionerRoleESR();

        practitionerRole.setDisplayName(shortName);

        IdentifierESDT shortNameBasedIdentifier = new IdentifierESDT();
        shortNameBasedIdentifier.setType(IdentifierType.SHORT_NAME);
        shortNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        shortNameBasedIdentifier.setValue(shortName);
        shortNameBasedIdentifier.setLeafValue(null);
        practitionerRole.getIdentifiers().add(shortNameBasedIdentifier);
        practitionerRole.assignSimplifiedID(true, IdentifierType.SHORT_NAME, IdentifierESDTUseEnum.USUAL);

        IdentifierESDT longNameBasedIdentifier = new IdentifierESDT();
        longNameBasedIdentifier.setType(IdentifierType.LONG_NAME);
        longNameBasedIdentifier.setUse(IdentifierESDTUseEnum.SECONDARY);
        longNameBasedIdentifier.setValue(longName);
        longNameBasedIdentifier.setLeafValue(null);
        practitionerRole.getIdentifiers().add(longNameBasedIdentifier);

        return practitionerRole;
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
        broker.createDirectoryEntry((PractitionerRoleESR) resource);
    } 
}
