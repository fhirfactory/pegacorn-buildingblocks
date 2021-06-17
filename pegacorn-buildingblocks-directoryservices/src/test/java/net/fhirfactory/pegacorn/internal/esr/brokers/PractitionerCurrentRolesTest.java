package net.fhirfactory.pegacorn.internal.esr.brokers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import net.fhirfactory.buildingblocks.esr.models.resources.CommonIdentifierESDTTypes;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerESR;
import net.fhirfactory.buildingblocks.esr.models.resources.PractitionerRoleESR;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDT;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.buildingblocks.esr.models.resources.datatypes.PractitionerRoleListESDT;
import net.fhirfactory.pegacorn.deployment.communicate.matrix.SystemManagedRoomNames;
import net.fhirfactory.pegacorn.internals.esr.brokers.GroupESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.HealthcareServiceESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.MatrixRoomESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.PractitionerRoleESRBroker;
import net.fhirfactory.pegacorn.internals.esr.brokers.RoleCategoryESRBroker;
import net.fhirfactory.pegacorn.internals.esr.cache.CareTeamPractitionerRoleMapCache;
import net.fhirfactory.pegacorn.internals.esr.cache.HealthcareServiceESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerRoleESRCache;
import net.fhirfactory.pegacorn.internals.esr.cache.PractitionerRoleMapCache;

/**
 * Testing of the practitioner current roles.
 * 
 * @author Brendan Douglas
 *
 */
@EnableAutoWeld
@AddBeanClasses({ PractitionerESRBroker.class, PractitionerESRCache.class, PractitionerRoleESRBroker.class, PractitionerRoleESRCache.class, CommonIdentifierESDTTypes.class, GroupESRBroker.class, PractitionerRoleMapCache.class, PractitionerRoleMapCache.class, CareTeamPractitionerRoleMapCache.class, SystemManagedRoomNames.class, MatrixRoomESRBroker.class, RoleCategoryESRBroker.class,HealthcareServiceESRBroker.class, HealthcareServiceESRCache.class })
public class PractitionerCurrentRolesTest {
    
    @Inject
    private PractitionerESRBroker practitionerBroker;
    
    @Inject
    private PractitionerRoleESRBroker practitionerRoleBroker;
    
    
    @Test
    public void testNoCurrentPractitionerRoles() {
        try {
            practitionerBroker.createPractitionerDE(createPractitioner("Short Name 1", "Long Name 1", "test1@test.com"));
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            assertEquals(0, practitioner.getCurrentPractitionerRoles().size());
        } catch(Exception e) {
            fail("Unable to get the practitioner roles", e);
        }
        
    }

    
    @Test
    public void testCreatePractitionerRoles() {
        try {
            practitionerBroker.createPractitionerDE(createPractitioner("Short Name 1", "Long Name 1", "test1@test.com"));
            practitionerRoleBroker.createPractitionerRole(createPractitionerRole("Role 1 short name", "Role 1 long name"));
            practitionerRoleBroker.createPractitionerRole(createPractitionerRole("Role 2 short name", "Role 2 long name"));
            
            PractitionerRoleListESDT roleList = new PractitionerRoleListESDT();
            roleList.getPractitionerRoles().add("Role 1 short name");
            roleList.getPractitionerRoles().add("Role 2 short name");
            practitionerBroker.updatePractitionerRoles("test1@test.com", roleList);
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            assertEquals(2, practitioner.getCurrentPractitionerRoles().size());
            assertEquals("Role 1 short name", practitioner.getCurrentPractitionerRoles().get(0).getSimplifiedID());
            assertEquals("Role 2 short name", practitioner.getCurrentPractitionerRoles().get(1).getSimplifiedID());
            
            assertEquals(2, practitioner.getRoleHistory().getAllCurrentRoles().size());
            assertEquals(0, practitioner.getRoleHistory().getAllPreviousRoles().size());
            
            assertNotNull(practitioner.getRoleHistory().getCurrentRole("Role 1 short name"));
            assertNotNull(practitioner.getRoleHistory().getCurrentRole("Role 2 short name"));
            
            // The practitioner role needs to have an association with the practitioner
            PractitionerRoleESR practitionerRole1 = (PractitionerRoleESR)practitionerRoleBroker.getResource("Role 1 short name".toLowerCase()).getEntry();
            PractitionerRoleESR practitionerRole2 = (PractitionerRoleESR)practitionerRoleBroker.getResource("Role 1 short name".toLowerCase()).getEntry();
            
            assertEquals(1, practitionerRole1.getActivePractitionerSet().size());
            assertEquals(1, practitionerRole2.getActivePractitionerSet().size());
            
            assertEquals("test1@test.com", practitionerRole1.getActivePractitionerSet().get(0).getSimplifiedID());
            assertEquals("test1@test.com", practitionerRole2.getActivePractitionerSet().get(0).getSimplifiedID());
            
            assertEquals("test1@test.com", practitionerRole1.getActivePractitionerIds().get(0));
            assertEquals("test1@test.com", practitionerRole2.getActivePractitionerIds().get(0));
        } catch(Exception e) {
            fail("Unable to get the practitioner roles", e);
        }
    }
    
    
    @Test
    public void testRemoveAllPractitionerRoles() {
        try {
            testCreatePractitionerRoles();
            
            practitionerBroker.createPractitionerDE(createPractitioner("Short Name 1", "Long Name 1", "test1@test.com"));
            
            PractitionerRoleListESDT roleList = new PractitionerRoleListESDT();
            practitionerBroker.updatePractitionerRoles("test1@test.com", roleList);
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            assertEquals(0, practitioner.getCurrentPractitionerRoles().size());
            
            // Check the role history to make sure roles 1 and 2 have been end dated
            assertEquals(2, practitioner.getRoleHistory().getAllPreviousRoles().size());
            assertNotNull(practitioner.getRoleHistory().getPreviousRole("Role 1 short name"));
            assertNotNull(practitioner.getRoleHistory().getPreviousRole("Role 2 short name"));
            
            // The practitioner role should no longer have an association with the practitioner
            PractitionerRoleESR practitionerRole1 = (PractitionerRoleESR)practitionerRoleBroker.getResource("Role 1 short name".toLowerCase()).getEntry();
            PractitionerRoleESR practitionerRole2 = (PractitionerRoleESR)practitionerRoleBroker.getResource("Role 1 short name".toLowerCase()).getEntry();
            assertEquals(0, practitionerRole1.getActivePractitionerSet().size());
            assertEquals(0, practitionerRole2.getActivePractitionerSet().size());
            
        } catch(Exception e) {
            fail("Unable to get the practitioner roles", e);
        }
    }
    
    
    @Test
    public void testUpdatePractitionerRoleFavourites() {
        try {
            testCreatePractitionerRoles();
            
            practitionerBroker.createPractitionerDE(createPractitioner("Short Name 1", "Long Name 1", "test1@test.com"));
            
            practitionerRoleBroker.createPractitionerRole(createPractitionerRole("Role 3 short name", "Role 3 long name"));
            practitionerRoleBroker.createPractitionerRole(createPractitionerRole("Role 4 short name", "Role 4 long name"));
            
            PractitionerRoleListESDT roleList = new PractitionerRoleListESDT();
            roleList.getPractitionerRoles().add("Role 2 short name");
            roleList.getPractitionerRoles().add("Role 3 short name");
            roleList.getPractitionerRoles().add("Role 4 short name");
            practitionerBroker.updatePractitionerRoles("test1@test.com", roleList);
            
            PractitionerESR practitioner = (PractitionerESR)practitionerBroker.getResource("test1@test.com").getEntry();
            
            assertEquals(3, practitioner.getCurrentPractitionerRoles().size());
            
            // Should be 1 previous role
            assertEquals(1, practitioner.getRoleHistory().getAllPreviousRoles().size());
            assertNotNull(practitioner.getRoleHistory().getPreviousRole("Role 1 short name"));


            // Should be 3 current roles.
            assertEquals(3, practitioner.getRoleHistory().getAllCurrentRoles().size());
            assertNotNull(practitioner.getRoleHistory().getCurrentRole("Role 2 short name"));
            assertNotNull(practitioner.getRoleHistory().getCurrentRole("Role 3 short name"));
            assertNotNull(practitioner.getRoleHistory().getCurrentRole("Role 4 short name"));
            
        } catch(Exception e) {
            fail("Unable to get the practitioner roles", e);
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
        emailBasedIdentifier.setValue(emailAddress);
        emailBasedIdentifier.setLeafValue(null);
        practitioner.getIdentifiers().add(emailBasedIdentifier);
        practitioner.assignSimplifiedID(true, identifierTypes.getEmailAddress(), IdentifierESDTUseEnum.USUAL);

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

        CommonIdentifierESDTTypes identifierTypes = new CommonIdentifierESDTTypes();

        IdentifierESDT shortNameBasedIdentifier = new IdentifierESDT();
        shortNameBasedIdentifier.setType(identifierTypes.getShortName());
        shortNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        shortNameBasedIdentifier.setValue(shortName);
        shortNameBasedIdentifier.setLeafValue(null);
        practitionerRole.getIdentifiers().add(shortNameBasedIdentifier);
        practitionerRole.assignSimplifiedID(true, identifierTypes.getShortName(), IdentifierESDTUseEnum.USUAL);

        IdentifierESDT longNameBasedIdentifier = new IdentifierESDT();
        longNameBasedIdentifier.setType(identifierTypes.getLongName());
        longNameBasedIdentifier.setUse(IdentifierESDTUseEnum.USUAL);
        longNameBasedIdentifier.setValue(longName);
        longNameBasedIdentifier.setLeafValue(null);
        practitionerRole.getIdentifiers().add(longNameBasedIdentifier);

        return practitionerRole;
    }
}
