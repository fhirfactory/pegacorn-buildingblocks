package net.fhirfactory.pegacorn.internal.esr.brokers;

import static org.junit.jupiter.api.Assertions.fail;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.CommonIdentifierESDTTypes;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.deployment.communicate.matrix.SystemManagedRoomNames;
import net.fhirfactory.pegacorn.internals.esr.brokers.GroupESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.MatrixRoomESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.RoleCategoryESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.CareTeamPractitionerRoleMapCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerRoleMapCache;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.SearchParamTypes;

/**
 * Practitioner search tests.
 * 
 * @author Brendan Douglas
 *
 */
@EnableAutoWeld
@AddBeanClasses({ PractitionerESRBroker.class, PractitionerESRCache.class, CommonIdentifierESDTTypes.class, GroupESRBroker.class, PractitionerRoleMapCache.class, PractitionerRoleMapCache.class, CareTeamPractitionerRoleMapCache.class, SystemManagedRoomNames.class, MatrixRoomESRBroker.class, RoleCategoryESRBroker.class })
public class PractitionerESRBrokerSearchTest extends BaseESRBrokerSearchTest {
    
    private static final String PREFIX = "Practitioner";

    @Inject
    private PractitionerESRBroker broker;

    
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

        CommonIdentifierESDTTypes identifierTypes = new CommonIdentifierESDTTypes();

        IdentifierESDT shortNameBasedIdentifier = new IdentifierESDT();
        shortNameBasedIdentifier.setType(identifierTypes.getShortName());
        shortNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        shortNameBasedIdentifier.setValue(shortName);
        shortNameBasedIdentifier.setLeafValue(null);
        practitioner.getIdentifiers().add(shortNameBasedIdentifier);

        IdentifierESDT longNameBasedIdentifier = new IdentifierESDT();
        longNameBasedIdentifier.setType(identifierTypes.getLongName());
        longNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        longNameBasedIdentifier.setValue(longName);
        longNameBasedIdentifier.setLeafValue(null);
        practitioner.getIdentifiers().add(longNameBasedIdentifier);
        
        
        IdentifierESDT emailBasedIdentifier = new IdentifierESDT();
        emailBasedIdentifier.setType(identifierTypes.getEmailAddress());
        emailBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        emailBasedIdentifier.setValue("John.Smith" + charToAdd + "@test.com.au");
        emailBasedIdentifier.setLeafValue(null);
        practitioner.getIdentifiers().add(emailBasedIdentifier);
        practitioner.assignSimplifiedID(true, identifierTypes.getEmailAddress(), IdentifierESDTUseEnum.USUAL);

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

            search(new SearchCriteria(SearchParamTypes.SIMPLIFIED_ID, "@test"), 25); // Only 25 instead of 26 due to paging.
            search(new SearchCriteria(SearchParamTypes.SIMPLIFIED_ID, "John.Smithz"), 1); 
            search(new SearchCriteria(SearchParamTypes.SIMPLIFIED_ID, "John.Smithzzzz"), 0);

        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }
}
