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
import net.fhirfactory.buildingblocks.esr.models.resources.CommonIdentifierESDTTypes;
import net.fhirfactory.buildingblocks.esr.models.resources.ExtremelySimplifiedResource;
import net.fhirfactory.buildingblocks.esr.models.resources.HealthcareServiceESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.brokers.HealthcareServiceESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.common.ESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.HealthcareServiceESRCache;
import net.fhirfactory.pegacorn.internals.esr.search.Pagination;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.Sort;

/**
 * Health Care Service search tests.
 * 
 * @author Brendan Douglas
 *
 */
@EnableAutoWeld
@AddBeanClasses({ HealthcareServiceESRBroker.class, HealthcareServiceESRCache.class, CommonIdentifierESDTTypes.class })
public class HealthCareServiceESRBrokerSearchTest extends BaseESRBrokerSearchTest {

    @Inject
    private HealthcareServiceESRBroker broker;

    /**
     * A search test which matches all records. Also tests the default sort order
     */
    @Test
    public void testSearchUsingShortNameAllRecordsMatch() {

        try {
            createHealthCareServices(4);

            search(new SearchCriteria("shortName", "short"), 4);
            search(new SearchCriteria("longName", "long"), 4);

        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }

    /**
     * A search test which matches just one record.
     */
    @Test
    public void testSearchUsingShortNameOneRecordMatches() {

        try {
            createHealthCareServices(4);

            search(new SearchCriteria("shortName", "namea"), 1);
            search(new SearchCriteria("longName", "namey"), 1);
        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }

    /**
     * Search by all names.
     */
    @Test
    public void testAllNameSearch() {
        try {
            createHealthCareServices(26);

            search(new SearchCriteria("allname", "namez"), 2); // There will be 2. One matches short name, the other long name.
        } catch (Exception e) {
            fail("Error performing search", e);
        }

    }

    /**
     * A search test which matches no records.
     */
    @Test
    public void testSearchUsingShortNameNoRecordsMatch() {

        try {
            createHealthCareServices(4);

            search(new SearchCriteria("shortName", "zzzz"), 0);
            search(new SearchCriteria("longName", "xxxxx"), 0);
        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }

    /**
     * A search test which returns 1 page of records only. The test will return 25
     * records.
     */
    @Test
    public void testSearchUsingShortNameDefaultPaging() {

        try {
            createHealthCareServices(26);

            // Match sure the default pagination returns the first 25 records only.
            search(new SearchCriteria("shortName", "health"), 25);
            search(new SearchCriteria("longName", "health"), 25);
        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }

    /**
     * A short name search test which returns 1 page of records using custom paging.
     */
    @Test
    public void testSearchUsingShortNameCustomPaging() {

        try {
            createHealthCareServices(26);

            // Match sure the correct records are returned for the custom paging.
            List<ExtremelySimplifiedResource> searchResult = search(new SearchCriteria("shortName", "health"), new Sort(), new Pagination(5, 3), 5);

            assertEquals("Health Care Service short namep", searchResult.get(0).getDisplayName());
            assertEquals("Health Care Service short nameq", searchResult.get(1).getDisplayName());
            assertEquals("Health Care Service short namer", searchResult.get(2).getDisplayName());
            assertEquals("Health Care Service short names", searchResult.get(3).getDisplayName());
            assertEquals("Health Care Service short namet", searchResult.get(4).getDisplayName());

        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }

    /**
     * Tests default ascending sorting.
     */
    @Test
    public void testDefaultAscendingSorting() {

        try {
            createHealthCareServices(26);

            // Match sure the correct records are returned for the custom paging.
            List<ExtremelySimplifiedResource> searchResult = search(new SearchCriteria("shortName", "health"), new Sort(), new Pagination(), 25);

            String alphabet = "abcdefghijklmnopqrstuvwxyz";

            for (int i = 0; i < 25; i++) {
                char shortNameChar = alphabet.charAt(i);
                assertEquals("Health Care Service short name" + shortNameChar, searchResult.get(i).getDisplayName());
            }
        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }

    /**
     * tests descending sorting.
     */
    @Test
    public void testDescendingSorting() {

        try {
            createHealthCareServices(26);

            // Match sure the correct records are returned for the custom paging.
            List<ExtremelySimplifiedResource> searchResult = search(new SearchCriteria("shortName", "care"), new Sort("shortName", "descending"),
                    new Pagination(), 25);

            String alphabetForward = "abcdefghijklmnopqrstuvwxyz";
            String alphabetbackward = StringUtils.reverse(alphabetForward);

            for (int i = 0; i < 25; i++) {
                char shortNameChar = alphabetbackward.charAt(i);
                assertEquals("Health Care Service short name" + shortNameChar, searchResult.get(i).getDisplayName());
            }
        } catch (Exception e) {
            fail("Error performing search", e);
        }
    }

    /**
     * Create some health care services.
     * 
     * @param number
     * @throws ResourceInvalidSearchException
     */
    private void createHealthCareServices(int number) throws ResourceInvalidSearchException {
        if (number > 26) {
            throw new RuntimeException("Can only create max 26 records");
        }

        String alphabetForward = "abcdefghijklmnopqrstuvwxyz";
        String alphabetbackward = StringUtils.reverse(alphabetForward);

        for (int i = 0; i < number; i++) {
            char shortNameChar = alphabetForward.charAt(i);
            char longtNameChar = alphabetbackward.charAt(i);

            broker.createHealthCareService(
                    createHealthCareService("Health Care Service short name" + shortNameChar, "Health Care Service long name" + longtNameChar));
        }
    }

    /**
     * Create a health care service.
     * 
     * @param shortName
     * @param longName
     * @return
     */
    private HealthcareServiceESR createHealthCareService(String shortName, String longName) {
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
}
