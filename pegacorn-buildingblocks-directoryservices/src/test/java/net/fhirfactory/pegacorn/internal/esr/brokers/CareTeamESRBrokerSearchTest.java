package net.fhirfactory.pegacorn.internal.esr.brokers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import net.fhirfactory.buildingblocks.esr.models.exceptions.ResourceInvalidSearchException;
import net.fhirfactory.buildingblocks.esr.models.resources.CareTeamESR;
import net.fhirfactory.buildingblocks.esr.models.resources.CommonIdentifierESDTTypes;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.brokers.CareTeamESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.CareTeamESRCache;
import net.fhirfactory.pegacorn.internals.esr.search.Pagination;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.Sort;

/**
 * Care team search tests.
 * 
 * @author Brendan Douglas
 *
 */
@EnableAutoWeld
@AddBeanClasses({ CareTeamESRBroker.class, CareTeamESRCache.class,PractitionerRoleESRBroker.class })
public class CareTeamESRBrokerSearchTest extends BaseESRBrokerSearchTest {
    
    @Inject
    private CareTeamESRBroker broker;
    
    /**
     * A search test which matches all records.  Also tests the default sort order
     */
    @Test
    public void testSearchUsingShortNameAllRecordsMatch() {
        
        try {
            createCareTeams(4);

            search(new SearchCriteria("shortName", "short"), 4);
            search(new SearchCriteria("longName", "long"), 4);

        } catch(Exception e) {
            fail("Unable to create care team", e);
        }  
    }

    
    /**
     * A search test which matches just one record.
     */
    @Test
    public void testSearchUsingShortNameOneRecordMatches() {
        
        try {
            createCareTeams(4);

            search(new SearchCriteria("shortName", "namea"), 1);
            search(new SearchCriteria("longName", "namey"), 1);
        } catch(Exception e) {
            fail("Unable to create care team", e);
        }  
    }
    
    
    /**
     * Search by all names.
     */
    @Test
    public void testAllNameSearch() {
        try {
            createCareTeams(26);

            search(new SearchCriteria("allname", "namez"), 2); // There will be 2.  One matches short name, the other long name.
        } catch(Exception e) {
            fail("Unable to create care team", e);
        }  
        
    }
   
    
    /**
     * A search test which matches no records.
     */
    @Test
    public void testSearchUsingShortNameNoRecordsMatch() {
        
        try {
            createCareTeams(4);

            search(new SearchCriteria("shortName", "zzzz"),0);
            search(new SearchCriteria("longName", "xxxxx"),0);
        } catch(Exception e) {
            fail("Unable to create care team", e);
        }  
    }
    
    
    
    /**
     * A search test which returns 1 page of records only.  The test will return 25 records.  
     */
    @Test
    public void testSearchUsingShortNameDefaultPaging() {
        
        try {
            createCareTeams(26);
            
            // Match sure the default pagination returns the first 25 records only.
            search(new SearchCriteria("shortName", "care"), 25);
            search(new SearchCriteria("longName", "care"), 25);            
        } catch(Exception e) {
            fail("Unable to create care team", e);
        }  
    }

    
    /**
     * A short name search test which returns 1 page of records using custom paging.
     */
    @Test
    public void testSearchUsingShortNameCustomPaging() {
        
        try {
            createCareTeams(26);
            
            // Match sure the correct records are returned for the custom paging.
            List<ExtremelySimplifiedResource> searchResult = search( new SearchCriteria("shortName", "care"), new Sort(), new Pagination(5, 3), 5);
            
            assertEquals("Care team short namep", searchResult.get(0).getDisplayName());
            assertEquals("Care team short nameq", searchResult.get(1).getDisplayName());
            assertEquals("Care team short namer", searchResult.get(2).getDisplayName());
            assertEquals("Care team short names", searchResult.get(3).getDisplayName());
            assertEquals("Care team short namet", searchResult.get(4).getDisplayName());  
            
        } catch(Exception e) {
            fail("Unable to create care team", e);
        }  
    }
    
    
    /**
     * Tests default ascending sorting.
     */
    @Test
    public void testDefaultAscendingSorting() {
        
        try {
            createCareTeams(26);
            
            // Match sure the correct records are returned for the custom paging.
            List<ExtremelySimplifiedResource> searchResult = search( new SearchCriteria("shortName", "care"), new Sort(), new Pagination(), 25);
            
            String alphabet = "abcdefghijklmnopqrstuvwxyz";

            
            for (int i = 0; i < 25; i++) {
                char shortNameChar = alphabet.charAt(i);
                assertEquals("Care team short name" + shortNameChar, searchResult.get(i).getDisplayName());
            }
        } catch(Exception e) {
            fail("Unable to create care team", e);
        }  
    }
    
    
    /**
     * tests descending sorting.
     */
    @Test
    public void testDescendingSorting() {
        
        try {
            createCareTeams(26);
            
            // Match sure the correct records are returned for the custom paging.
            List<ExtremelySimplifiedResource> searchResult = search(new SearchCriteria("shortName", "care"), new Sort("shortName", "descending"), new Pagination(), 25);
            
            String alphabetForward = "abcdefghijklmnopqrstuvwxyz";
            String alphabetbackward = StringUtils.reverse(alphabetForward);

            
            for (int i = 0; i < 25; i++) {
                char shortNameChar = alphabetbackward.charAt(i);
                assertEquals("Care team short name" + shortNameChar, searchResult.get(i).getDisplayName());
            }
        } catch(Exception e) {
            fail("Unable to create care team", e);
        }  
    }

    
    /**
     * Create some care teams.
     * 
     * @param number
     * @throws ResourceInvalidSearchException
     */
    private void createCareTeams(int number) throws ResourceInvalidSearchException {
        if (number > 26) {
            throw new RuntimeException("Can only create max 26 records");
        }
        
        String alphabetForward = "abcdefghijklmnopqrstuvwxyz";
        String alphabetbackward = StringUtils.reverse(alphabetForward);
        
        for (int i = 0; i < number; i++) {
            char shortNameChar = alphabetForward.charAt(i);
            char longtNameChar = alphabetbackward.charAt(i);
            
            broker.createCareTeam(createCareTeam("Care team short name" + shortNameChar, "Care team long name" + longtNameChar));
        }
    }

    
    /**
     * Create a car team.
     * 
     * @param shortName
     * @param longName
     * @return
     */
    private CareTeamESR createCareTeam(String shortName, String longName) {
        CareTeamESR careTeam = new CareTeamESR();
        
        careTeam.setDisplayName(shortName);
        
        
        CommonIdentifierESDTTypes identifierTypes = new CommonIdentifierESDTTypes();
        
        IdentifierESDT shortNameBasedIdentifier = new IdentifierESDT();
        shortNameBasedIdentifier.setType(identifierTypes.getShortName());
        shortNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        shortNameBasedIdentifier.setValue(shortName);
        shortNameBasedIdentifier.setLeafValue(null);
        careTeam.getIdentifiers().add(shortNameBasedIdentifier);
        careTeam.assignSimplifiedID(true, identifierTypes.getShortName(), IdentifierESDTUseEnum.USUAL);

        IdentifierESDT longNameBasedIdentifier = new IdentifierESDT();
        longNameBasedIdentifier.setType(identifierTypes.getLongName());
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
}
