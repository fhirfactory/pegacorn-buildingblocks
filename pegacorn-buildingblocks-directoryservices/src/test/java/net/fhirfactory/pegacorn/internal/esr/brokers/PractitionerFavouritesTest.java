package net.fhirfactory.pegacorn.internal.esr.brokers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import net.fhirfactory.pegacorn.internals.esr.brokers.GroupESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.HealthcareServiceESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.OrganizationESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.RoleCategoryESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.CareTeamPractitionerRoleMapCache;
import net.fhirfactory.pegacorn.internals.esr.cache.HealthcareServiceESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.OrganizationESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerRoleESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerRoleMapCache;
import net.fhirfactory.pegacorn.internals.esr.resources.HealthcareServiceESR;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerESR;
import net.fhirfactory.pegacorn.internals.esr.resources.PractitionerRoleESR;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.FavouriteListESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.internals.esr.search.FavouriteTypes;

/**
 * Testing of the practitioner favourites.  There are practitioner role favourites, health care service favourites and practitioner favourites.
 * 
 * @author Brendan Douglas
 *
 */
@EnableAutoWeld
@AddBeanClasses({ PractitionerESRBroker.class, PractitionerESRCache.class, PractitionerRoleESRBroker.class, PractitionerRoleESRCache.class, GroupESRBroker.class, PractitionerRoleMapCache.class, PractitionerRoleMapCache.class, CareTeamPractitionerRoleMapCache.class, RoleCategoryESRBroker.class,HealthcareServiceESRBroker.class, HealthcareServiceESRCache.class, OrganizationESRCache.class, OrganizationESRBroker.class })
public class PractitionerFavouritesTest {
    
    @Inject
    private PractitionerESRBroker practitionerBroker;
    
    @Inject
    private PractitionerRoleESRBroker practitionerRoleBroker;
    
    @Inject
    private HealthcareServiceESRBroker healthCareServiceBroker;
    
    @Test
    public void testNoPractitionerFavourites() {
        try {
            practitionerBroker.createPractitionerDE(createPractitioner("Short Name1", "Long Name 1", "test1@test.com"));
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            assertEquals(0, practitioner.getPractitionerFavourites().getFavourites().size()); // Shouldn't be any as I didn't add any favourites.
        } catch(Exception e) {
            fail("Unable to get the practitioner favourites", e);
        }
    }

    
    @Test
    public void testNoPractitionerRoleFavourites() {
        try {
            practitionerBroker.createPractitionerDE(createPractitioner("Short Name1", "Long Name 1", "test1@test.com"));
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            assertEquals(0, practitioner.getPractitionerRoleFavourites().getFavourites().size()); // Shouldn't be any as I didn't add any favourites.
        } catch(Exception e) {
            fail("Unable to get the practitioner favourites", e);
        }
    }

    
    @Test
    public void testNoPractitionerHealthCareServiceFavourites() {
        try {
            practitionerBroker.createPractitionerDE(createPractitioner("Short Name1", "Long Name 1", "test1@test.com"));
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            assertEquals(0, practitioner.getHealthcareServiceFavourites().getFavourites().size()); // Shouldn't be any as I didn't add any favourites.
        } catch(Exception e) {
            fail("Unable to get the practitioner favourites", e);
        }
    }
    
    
    
    @Test
    public void testCreatePractitionerRoleFavourites() {
        try {
            practitionerBroker.createPractitionerDE(createPractitioner("Short Name 1", "Long Name 1", "test1@test.com"));
            practitionerRoleBroker.createPractitionerRole(createPractitionerRole("Role 1 short name", "Role 1 long name"));
            practitionerRoleBroker.createPractitionerRole(createPractitionerRole("Role 2 short name", "Role 2 long name"));
            
            FavouriteListESDT roleList = new FavouriteListESDT();
            roleList.getFavourites().add("Role 1 short name");
            roleList.getFavourites().add("Role 2 short name");
            practitionerBroker.updateFavourites("test1@test.com", FavouriteTypes.PRACTITIONER_ROLE_FAVOURITES, roleList);
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            assertEquals(2, practitioner.getPractitionerRoleFavourites().getFavourites().size());
            assertEquals("Role 1 short name", practitioner.getPractitionerRoleFavourites().getFavourites().get(0));
            assertEquals("Role 2 short name", practitioner.getPractitionerRoleFavourites().getFavourites().get(1));
        } catch(Exception e) {
            fail("Unable to get the practitioner favourites", e);
        }
    }
    
    
    @Test
    public void testCreatePractitionerFavourites() {
        try {
            practitionerBroker.createPractitionerDE(createPractitioner("Short Name1", "Long Name 1", "test1@test.com"));
            practitionerBroker.createPractitionerDE(createPractitioner("Short Name 2", "Long name 2", "test2@test.com"));
            
            FavouriteListESDT practitionerList = new FavouriteListESDT();
            practitionerList.getFavourites().add("test2@test.com");
            practitionerBroker.updateFavourites("test1@test.com", FavouriteTypes.PRACTITIONER_FAVOURITES, practitionerList);
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            assertEquals(1, practitioner.getPractitionerFavourites().getFavourites().size());
            assertEquals("test2@test.com", practitioner.getPractitionerFavourites().getFavourites().get(0));
        } catch(Exception e) {
            fail("Unable to get the practitioner favourites", e);
        }
    }
    
    
    @Test
    public void testCreatePractitionerHealthCareServiceFavourites() {
        try {
            practitionerBroker.createPractitionerDE(createPractitioner("Short Name1", "Long Name 2", "test1@test.com"));
            
            healthCareServiceBroker.createHealthCareService(createHealthCareService("Service 1 short name", "Service 1 long name"));
            healthCareServiceBroker.createHealthCareService(createHealthCareService("Service 2 short name", "Service 2 long name"));
            
            FavouriteListESDT healthCareServiceList = new FavouriteListESDT();
            healthCareServiceList.getFavourites().add("Service 1 short name");
            healthCareServiceList.getFavourites().add("Service 2 short name");
            practitionerBroker.updateFavourites("test1@test.com", FavouriteTypes.SERVICE_FAVOURITES, healthCareServiceList);
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            assertEquals(2, practitioner.getHealthcareServiceFavourites().getFavourites().size());
            assertEquals("Service 1 short name", practitioner.getHealthcareServiceFavourites().getFavourites().get(0));
            assertEquals("Service 2 short name", practitioner.getHealthcareServiceFavourites().getFavourites().get(1));
        } catch(Exception e) {
            fail("Unable to get the practitioner favourites", e);
        }
    }
    
    
    @Test
    public void testUpdatePractitionerRoleFavourites() {
        try {
            testCreatePractitionerRoleFavourites();
            
            // Now update the list of role
            try {
                practitionerRoleBroker.createPractitionerRole(createPractitionerRole("Role 3 short name", "Role 3 long name"));
                practitionerRoleBroker.createPractitionerRole(createPractitionerRole("Role 4 short name", "Role 4 long name"));
                
                FavouriteListESDT roleList = new FavouriteListESDT();
                roleList.getFavourites().add("Role 2 short name");
                roleList.getFavourites().add("Role 3 short name");
                roleList.getFavourites().add("Role 4 short name");
                practitionerBroker.updateFavourites("test1@test.com", FavouriteTypes.PRACTITIONER_ROLE_FAVOURITES, roleList);
                
                PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
                
                // role 1 will no longer exist as a favourite and roles 2, 3 and 4 will.
                assertEquals(3, practitioner.getPractitionerRoleFavourites().getFavourites().size());
                assertEquals("Role 2 short name", practitioner.getPractitionerRoleFavourites().getFavourites().get(0));
                assertEquals("Role 3 short name", practitioner.getPractitionerRoleFavourites().getFavourites().get(1));
                assertEquals("Role 4 short name", practitioner.getPractitionerRoleFavourites().getFavourites().get(2));
            } catch(Exception e) {
                fail("Unable to get the practitioner favourites", e);
            }
        } catch(Exception e) {
            fail("Unable to update the favourites", e);
        }
    }
    
    
    @Test
    public void testUpdatePractitionerFavourites() {
        try {
            testCreatePractitionerFavourites();
            
            practitionerBroker.createPractitionerDE(createPractitioner("Short Name3", "Long Name 3", "test3@test.com"));
            practitionerBroker.createPractitionerDE(createPractitioner("Short Name 4", "Long name 4", "test4@test.com"));
            
            FavouriteListESDT practitionerList = new FavouriteListESDT();
            practitionerList.getFavourites().add("test2@test.com");
            practitionerList.getFavourites().add("test3@test.com");
            practitionerList.getFavourites().add("test4@test.com");
            practitionerBroker.updateFavourites("test1@test.com", FavouriteTypes.PRACTITIONER_FAVOURITES, practitionerList);
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            // practitioner 1 will no longer exist as a favourite and practitioners 2, 3 and 4 will.
            assertEquals(3, practitioner.getPractitionerFavourites().getFavourites().size());
            assertEquals("test2@test.com", practitioner.getPractitionerFavourites().getFavourites().get(0));
            assertEquals("test3@test.com", practitioner.getPractitionerFavourites().getFavourites().get(1));
            assertEquals("test4@test.com", practitioner.getPractitionerFavourites().getFavourites().get(2));
        } catch(Exception e) {
            fail("Unable to get the practitioner favourites", e);
        }
    }
    
    
    @Test
    public void testUpdatePractitionerHealthCareServiceFavourites() {
        try {
            testCreatePractitionerHealthCareServiceFavourites();
                       
            healthCareServiceBroker.createHealthCareService(createHealthCareService("Service 3 short name", "Service 3 long name"));
            healthCareServiceBroker.createHealthCareService(createHealthCareService("Service 4 short name", "Service 4 long name"));
            
            FavouriteListESDT healthCareServiceList = new FavouriteListESDT();
            healthCareServiceList.getFavourites().add("Service 2 short name");
            healthCareServiceList.getFavourites().add("Service 3 short name");
            healthCareServiceList.getFavourites().add("Service 4 short name");
            practitionerBroker.updateFavourites("test1@test.com", FavouriteTypes.SERVICE_FAVOURITES, healthCareServiceList);
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            // service 1 will no longer exist as a favourite and services 2, 3 and 4 will.
            assertEquals(3, practitioner.getHealthcareServiceFavourites().getFavourites().size());
            assertEquals("Service 2 short name", practitioner.getHealthcareServiceFavourites().getFavourites().get(0));
            assertEquals("Service 3 short name", practitioner.getHealthcareServiceFavourites().getFavourites().get(1));
            assertEquals("Service 4 short name", practitioner.getHealthcareServiceFavourites().getFavourites().get(2));
        } catch(Exception e) {
            fail("Unable to get the practitioner favourites", e);
        }
    }
    
    
    @Test
    public void testRemoveAllPractitionerRoleFavourites() {
        try {
            testCreatePractitionerRoleFavourites();
            
            // Now update the list of role
            try {               
                FavouriteListESDT roleList = new FavouriteListESDT();
                practitionerBroker.updateFavourites("test1@test.com", FavouriteTypes.PRACTITIONER_ROLE_FAVOURITES, roleList);
                
                PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
                
                // All gone
                assertEquals(0, practitioner.getPractitionerRoleFavourites().getFavourites().size());
            } catch(Exception e) {
                fail("Unable to get the practitioner favourites", e);
            }
        } catch(Exception e) {
            fail("Unable to update the favourites", e);
        }
    }
    
    
    @Test
    public void testRemoveAllPractitionerFavourites() {
        try {
            testCreatePractitionerFavourites();
                       
            FavouriteListESDT practitionerList = new FavouriteListESDT();
            practitionerBroker.updateFavourites("test1@test.com", FavouriteTypes.PRACTITIONER_FAVOURITES, practitionerList);
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            // All gone
            assertEquals(0, practitioner.getPractitionerFavourites().getFavourites().size());
        } catch(Exception e) {
            fail("Unable to get the practitioner favourites", e);
        }
    }
    
    
    @Test
    public void testRemoveAllPractitionerHealthCareServiceFavourites() {
        try {
            testCreatePractitionerHealthCareServiceFavourites();
           
            FavouriteListESDT healthCareServiceList = new FavouriteListESDT();
            practitionerBroker.updateFavourites("test1@test.com", FavouriteTypes.SERVICE_FAVOURITES, healthCareServiceList);
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            // All gone
            assertEquals(0, practitioner.getHealthcareServiceFavourites().getFavourites().size());
        } catch(Exception e) {
            fail("Unable to get the practitioner favourites", e);
        }
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
    
    
    /**
     * Create a practitioner role.
     * 
     * @param shortName
     * @param longName
     * @return
     */
    private PractitionerRoleESR createPractitionerRole(String shortName, String longName) {
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
        longNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        longNameBasedIdentifier.setValue(longName);
        longNameBasedIdentifier.setLeafValue(null);
        practitionerRole.getIdentifiers().add(longNameBasedIdentifier);

        return practitionerRole;
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

        IdentifierESDT shortNameBasedIdentifier = new IdentifierESDT();
        shortNameBasedIdentifier.setType(IdentifierType.SHORT_NAME);
        shortNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        shortNameBasedIdentifier.setValue(shortName);
        shortNameBasedIdentifier.setLeafValue(null);
        healthCareService.getIdentifiers().add(shortNameBasedIdentifier);
        healthCareService.assignSimplifiedID(true, IdentifierType.SHORT_NAME, IdentifierESDTUseEnum.USUAL);

        IdentifierESDT longNameBasedIdentifier = new IdentifierESDT();
        longNameBasedIdentifier.setType(IdentifierType.LONG_NAME);
        longNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        longNameBasedIdentifier.setValue(longName);
        longNameBasedIdentifier.setLeafValue(null);
        healthCareService.getIdentifiers().add(longNameBasedIdentifier);

        return healthCareService;
    }
}
