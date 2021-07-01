package net.fhirfactory.pegacorn.internal.esr.brokers;

import static org.junit.jupiter.api.Assertions.fail;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.resources.PractitionerESR;
import net.fhirfactory.buildingblocks.esr.resources.PractitionerRoleESR;
import net.fhirfactory.buildingblocks.esr.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.deployment.communicate.matrix.SystemManagedRoomNames;
import net.fhirfactory.pegacorn.internals.esr.brokers.GroupESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.MatrixRoomESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.RoleCategoryESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.CareTeamPractitionerRoleMapCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerRoleESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerRoleMapCache;
import net.fhirfactory.pegacorn.internals.esr.cache.RoleCategoryESRCache;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.SearchParam;
import net.fhirfactory.pegacorn.internals.esr.search.SearchParamNames;
import net.fhirfactory.pegacorn.internals.esr.search.filter.practitioner.RoleCategoryFilter;

/**
 * Practitioner search tests.
 * 
 * @author Brendan Douglas
 *
 */
@EnableAutoWeld
@AddBeanClasses({ PractitionerESRBroker.class, PractitionerESRCache.class, PractitionerRoleESRBroker.class, RoleCategoryFilter.class, PractitionerRoleESRCache.class, GroupESRBroker.class, PractitionerRoleMapCache.class, CareTeamPractitionerRoleMapCache.class, SystemManagedRoomNames.class, MatrixRoomESRBroker.class, RoleCategoryESRBroker.class, RoleCategoryESRCache.class })
public class PractitionerESRBrokerSearchTest extends BaseESRBrokerSearchTest {
    
    private static final String PREFIX = "Practitioner";

    @Inject
    private PractitionerESRBroker broker;
    
    @Inject
    private PractitionerRoleESRBroker practitionerRoleBroker;
    
    @Inject
    private RoleCategoryESRBroker roleCategoryBroker;
    
    @Inject
    private RoleCategoryFilter roleCategoryFilter;

    
    /**
     * Create a practitioner.
     * 
     * @param shortName
     * @param longName
     * @return
     */
    public PractitionerESR createResource(String shortName, String longName, char charToAdd) {
        PractitionerESR practitioner = new PractitionerESR();

        practitioner.setDisplayName(shortName);

        IdentifierESDT shortNameBasedIdentifier = new IdentifierESDT();
        shortNameBasedIdentifier.setType(IdentifierType.SHORT_NAME);
        shortNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        shortNameBasedIdentifier.setValue(shortName);
        shortNameBasedIdentifier.setLeafValue(null);
        practitioner.getIdentifiers().add(shortNameBasedIdentifier);

        IdentifierESDT longNameBasedIdentifier = new IdentifierESDT();
        longNameBasedIdentifier.setType(IdentifierType.LONG_NAME);
        longNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        longNameBasedIdentifier.setValue(longName);
        longNameBasedIdentifier.setLeafValue(null);
        practitioner.getIdentifiers().add(longNameBasedIdentifier);
        
        
        IdentifierESDT emailBasedIdentifier = new IdentifierESDT();
        emailBasedIdentifier.setType(IdentifierType.EMAIL_ADDRESS);
        emailBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        emailBasedIdentifier.setValue("John.Smith" + charToAdd + "@test.com.au");
        emailBasedIdentifier.setLeafValue(null);
        practitioner.getIdentifiers().add(emailBasedIdentifier);
        practitioner.assignSimplifiedID(true, IdentifierType.EMAIL_ADDRESS, IdentifierESDTUseEnum.USUAL);

        return practitioner;
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
        broker.createPractitionerDE((PractitionerESR) resource);
    } 

    
    @Test
    public void testEmailAddressSearch() {
        try {
            createResources(26);

            search(new SearchCriteria(new SearchParam(SearchParamNames.SIMPLIFIED_ID, "@test")), 25); // Only 25 instead of 26 due to paging.
            search(new SearchCriteria(new SearchParam(SearchParamNames.SIMPLIFIED_ID, "John.Smithz")), 1); 
            search(new SearchCriteria(new SearchParam(SearchParamNames.SIMPLIFIED_ID, "John.Smithzzzz")), 0);

        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }
    
    @Test
    public void careTeamFilterTest() {
        
    }

    
    /**
     * Create a practitioner.
     * 
     * @param shortName
     * @param longName
     * @return
     */
    private PractitionerESR createPractitioner(String shortName, String longName, String emailAddress) {
        PractitionerESR practitioner = new PractitionerESR();

        practitioner.setDisplayName(shortName);

        IdentifierESDT shortNameBasedIdentifier = new IdentifierESDT();
        shortNameBasedIdentifier.setType(IdentifierType.SHORT_NAME);
        shortNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        shortNameBasedIdentifier.setValue(shortName);
        shortNameBasedIdentifier.setLeafValue(null);
        practitioner.getIdentifiers().add(shortNameBasedIdentifier);

        IdentifierESDT longNameBasedIdentifier = new IdentifierESDT();
        longNameBasedIdentifier.setType(IdentifierType.LONG_NAME);
        longNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        longNameBasedIdentifier.setValue(longName);
        longNameBasedIdentifier.setLeafValue(null);
        practitioner.getIdentifiers().add(longNameBasedIdentifier);
        
        
        IdentifierESDT emailBasedIdentifier = new IdentifierESDT();
        emailBasedIdentifier.setType(IdentifierType.EMAIL_ADDRESS);
        emailBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        emailBasedIdentifier.setValue(emailAddress);
        emailBasedIdentifier.setLeafValue(null);
        practitioner.getIdentifiers().add(emailBasedIdentifier);
        practitioner.assignSimplifiedID(true, IdentifierType.EMAIL_ADDRESS, IdentifierESDTUseEnum.USUAL);

        return practitioner;
    }
    
    
    @Test
    public void locationFilterTest() {
        
    } 
    
    
    
    /**
     * Create a practitioner role.
     * 
     * @param shortName
     * @param longName
     * @return
     */
    private PractitionerRoleESR createPractitionerRole(String shortName, String longName, String roleCategoryId) {
        PractitionerRoleESR practitionerRole = new PractitionerRoleESR();
        
        practitionerRole.setPrimaryRoleCategoryID(roleCategoryId);

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
        longNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        longNameBasedIdentifier.setValue(longName);
        longNameBasedIdentifier.setLeafValue(null);
        practitionerRole.getIdentifiers().add(longNameBasedIdentifier);

        return practitionerRole;
    }  
}
