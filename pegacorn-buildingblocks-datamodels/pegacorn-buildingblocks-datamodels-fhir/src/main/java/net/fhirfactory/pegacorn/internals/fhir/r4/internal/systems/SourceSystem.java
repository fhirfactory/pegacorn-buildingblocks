/* 
 * The MIT License
 *
 * Copyright 2020 Mark A. Hunter (ACT Health).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.fhirfactory.pegacorn.internals.fhir.r4.internal.systems;

import net.fhirfactory.pegacorn.internals.PegacornReferenceProperties;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.codesystems.PegacornIdentifierCodeSystemFactory;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.SubsystemTopologyNode;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Date;

/**
 *
 * @author Mark A. Hunter
 */
public abstract class SourceSystem {
    private static final Logger LOG = LoggerFactory.getLogger(SourceSystem.class);
        
    protected String organizationName;
    protected String systemOwnerContactName;
    protected String systemOwnerContactEmail;
    protected String systemOwnerContactPhone;
    protected String systemAdministratorContactName;
    protected String systemAdministratorContactEmail;
    protected String systemAdministratorContactPhone;
    protected String systemEndpointName;
    protected Practitioner practitionerSystemAdministrator;
    protected Practitioner practitionerSystemOwner;
    protected PractitionerRole practitionerRoleSystemAdministrator;
    protected PractitionerRole practitionerRoleSystemOwner;
    protected String systemReference;

    protected Reference referenceOrganization;
    protected Reference referenceSystemOwnerOrganization;
    protected Reference referenceSystemOwnerPractitionerRole;
    protected Reference referenceSystemOwnerPractitioner;
    protected Reference referenceSystemAdministratorPractitionerRole;
    protected Reference referenceSystemAdministratorPractitioner;
    protected Identifier identifierSystemOwnerPractitioner;
    protected Identifier identifierSystemOwnerPractitionerRole;
    protected Identifier identifierSystemAdministratorPractitioner;
    protected Identifier identifierSystemAdministratorPractitionerRole;
    protected Identifier systemOwnerRoleIdentifier;
    protected Identifier systemAdministratorRoleIdentifier;
    protected Identifier identifierOrganization;
    protected Organization organizationSystemOwner;
    protected Endpoint endpointSystem;
    protected Identifier identifierSystemEndpoint;
    protected Reference systemEndpointReference;
    protected SubsystemTopologyNode topologyNode;

    @Inject
    private PegacornIdentifierCodeSystemFactory pegacornIdentifierCodeSystemFactory;

    @Inject
    private PegacornReferenceProperties systemWideProperties;

    public SourceSystem() {
        this.systemOwnerContactName = specifySystemOwnerContactName();
        this.systemOwnerContactEmail = sepcifySystemOwnerContactEmail();
        this.systemOwnerContactPhone = specifySystemOwnerContactPhone();
        this.systemAdministratorContactName = specifySystemAdministratorContactName();
        this.systemAdministratorContactEmail = specifySystemAdministratorContactEmail();
        this.systemAdministratorContactPhone = specifySystemAdministratorContactPhone();
    }

    @PostConstruct
    public void initialise(){
        this.organizationName = specifyOrganizationName();
        this.systemEndpointName = specifyEndpointName();
        this.systemReference = createSystemReference();
        this.identifierOrganization = createOrganizationIdentifier();
        this.identifierSystemOwnerPractitioner = createSystemOwnerPractitionerIdentifier();
        this.identifierSystemOwnerPractitionerRole = createSystemOwnerPractitionerRoleIdentifier();
        this.identifierSystemEndpoint = createSystemEndpointIdentifier();
        this.identifierSystemAdministratorPractitioner = createSystemAdministratorPractitionerIdentifier();
        this.identifierSystemAdministratorPractitionerRole = createSystemAdministratorPractitionerIdentifier();
        this.referenceOrganization = createOwningOrganizationReference();
        this.referenceSystemOwnerOrganization = createOwningOrganizationReference();
        this.referenceSystemOwnerPractitionerRole = createSystemOwnerPractitionerRoleReference();
        this.referenceSystemOwnerPractitioner = createSystemOwnerPractitionerReference();
        this.referenceSystemAdministratorPractitionerRole = createSystemAdministratorRoleReference();
        this.referenceSystemAdministratorPractitioner = createSystemAdministratorPractitionerReference();
        this.systemEndpointReference = createSystemEndpointReference();
        this.practitionerSystemAdministrator = createSystemAdministratorPractitioner();
        this.practitionerRoleSystemAdministrator = createSystemAdministratorPractitionerRole();
        this.practitionerSystemOwner = createSystemOwnerPractitioner();
        this.practitionerRoleSystemOwner = createSystemOwnerPractitionerRole();
        this.organizationSystemOwner = createOrganization();
        this.endpointSystem = createSystemEndpoint();
    }


    abstract protected String specifyOrganizationName();

    abstract protected String specifySystemOwnerContactName();

    abstract protected String sepcifySystemOwnerContactEmail();

    abstract protected String specifySystemOwnerContactPhone();

    abstract protected String specifySystemAdministratorContactName();

    abstract protected String specifySystemAdministratorContactEmail();

    abstract protected String specifySystemAdministratorContactPhone();
    
    abstract protected String specifyEndpointName();
    
    abstract protected CodeableConcept specifyOrganizationType();
    
    abstract protected String createSystemReference();

//    abstract protected NodeElement createTopologyNode();
    
    // FHIR Resource Creation
    
    protected Endpoint createSystemEndpoint() {
//        NodeElement systemEndpointNode = createTopologyNode();
        Endpoint systemEndpoint = new Endpoint();
        systemEndpoint.getIdentifier().add(getIdentifierSystemEndpoint());
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        systemEndpoint.setPeriod(validPeriod);
        return (systemEndpoint);
    }
    
    protected Reference createSystemEndpointReference() {
        LOG.debug(".createSystemEndpointReference(): Entry");
        Reference systemEndpointRef = new Reference();
        systemEndpointRef.setIdentifier(getIdentifierSystemEndpoint());
        systemEndpointRef.setDisplay(this.getOrganizationName() + " - " + this.getSystemEndpointName());
        systemEndpointRef.setType("Endpoint");
        LOG.debug(".createSystemEndpointReference(): Exit, created Reference --> {}", systemEndpointRef);    	
        return(systemEndpointRef);
    }
    
    protected Identifier createSystemEndpointIdentifier() {
        LOG.debug(".createSystemEndpointIdentifier(): Entry");
        Identifier systemSystemEndpointIdentifier = new Identifier();
        systemSystemEndpointIdentifier.setUse(Identifier.IdentifierUse.SECONDARY);
        CodeableConcept idType = pegacornIdentifierCodeSystemFactory.buildIdentifierType(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_FHIR_ENDPOINT_SYSTEM);
        systemSystemEndpointIdentifier.setType(idType);
        systemSystemEndpointIdentifier.setSystem(getSystemReference());
        systemSystemEndpointIdentifier.setValue(specifyEndpointName());
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        systemSystemEndpointIdentifier.setPeriod(validPeriod);
        systemSystemEndpointIdentifier.setAssigner(buildDefaultEndpointReference());
        LOG.debug(".createSystemEndpointIdentifier(): Exit, created Identifier --> {}", systemSystemEndpointIdentifier);
        return(systemSystemEndpointIdentifier);
    }

    private Reference buildDefaultEndpointReference(){
        Identifier systemSystemEndpointIdentifier = new Identifier();
        systemSystemEndpointIdentifier.setUse(Identifier.IdentifierUse.SECONDARY);
        CodeableConcept idType = pegacornIdentifierCodeSystemFactory.buildIdentifierType(PegacornIdentifierCodeEnum.IDENTIFIER_CODE_FHIR_ENDPOINT_SYSTEM);
        systemSystemEndpointIdentifier.setType(idType);
        systemSystemEndpointIdentifier.setSystem(getSystemReference());
        systemSystemEndpointIdentifier.setValue(systemWideProperties.getSystemDeploymentName());
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        systemSystemEndpointIdentifier.setPeriod(validPeriod);
        systemSystemEndpointIdentifier.setAssigner(getReferenceOrganization());
        Reference systemEndpointReference = new Reference();
        systemEndpointReference.setIdentifier(systemSystemEndpointIdentifier);
        systemEndpointReference.setType("Endpoint");
        systemEndpointReference.setDisplay(systemWideProperties.getSystemDeploymentName());
        return(systemEndpointReference);
    }

    // System Administrator Resource (Practitioner) Set (and Attributes)

    protected Practitioner createSystemAdministratorPractitioner(){
        LOG.debug(".createSystemAdministrator(): Entry");
        Practitioner systemSAPractitioner = new Practitioner();
        systemSAPractitioner.getIdentifier().add(getIdentifierSystemAdministratorPractitioner());
        systemSAPractitioner.setActive(true);
        HumanName simpleName = new HumanName();
        simpleName.setText(this.getSystemAdministratorContactName());
        Period nameEffectivePeriod = new Period();
        nameEffectivePeriod.setStart(Date.from(Instant.now()));
        simpleName.setPeriod(nameEffectivePeriod);
        systemSAPractitioner.getName().add(simpleName);
        LOG.debug(".createSystemAdministrator(): Exit, created Practitioner --> {}", systemSAPractitioner);
        return(systemSAPractitioner);
    }

    protected Reference createSystemAdministratorPractitionerReference(){
        LOG.debug(".createSystemAdministratorPractitionerReference(): Entry");
        Reference systemSysAdminRef = new Reference();
        systemSysAdminRef.setIdentifier(getIdentifierSystemOwnerPractitioner());
        systemSysAdminRef.setDisplay(this.getOrganizationName() + " - " + this.systemAdministratorContactName);
        systemSysAdminRef.setType("Practitioner");
        LOG.debug(".createSystemAdministratorPractitionerReference(): Exit, created Reference --> {}", systemSysAdminRef);
        return (systemSysAdminRef);
    }
    
    protected Identifier createSystemAdministratorPractitionerIdentifier(){
        LOG.debug(".createSystemAdministratorIdentifier(): Entry");
        // Create an empty FHIR::Identifier element
        Identifier systemSystemAdministrationPractitioner = new Identifier();
        // Set the FHIR::Identifier.Use to "SECONDARY" (this id is not their ABN or anything)
        systemSystemAdministrationPractitioner.setUse(Identifier.IdentifierUse.SECONDARY);
        // Set the FHIR::Identifier.Type to RI (Generalized Resource Identifier)
        CodeableConcept orgType = new CodeableConcept();
        Coding orgTypeCoding = new Coding();
        orgTypeCoding.setCode("RI");
        orgTypeCoding.setSystem("http://terminology.hl7.org/ValueSet/v2-0203");
        orgType.getCoding().add(orgTypeCoding);
        orgType.setText("Generalized Resource Identifier");
        systemSystemAdministrationPractitioner.setType(orgType);
        // Set the FHIR::Identifier.System to FHIRFactory (it's our ID we're creating)
        systemSystemAdministrationPractitioner.setSystem("FHIRFactory"); // TODO fix the system identification for Organization Identifiers
        // Set the FHIR::Identifier.Value to the NodeElementIdentifier for the TechnologyOne System
        systemSystemAdministrationPractitioner.setValue(this.getSystemAdministratorContactName());
        // Set the FHIR::Identifier.Period
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        systemSystemAdministrationPractitioner.setPeriod(validPeriod);
        // Set the FHIR::Identifier.Assigner (to us, for this one)
        systemSystemAdministrationPractitioner.setAssigner(new Reference("Organization/FHIRFactory"));
        LOG.debug(".createSystemAdministratorIdentifier(): Exit, created Identifier --> {}", systemSystemAdministrationPractitioner);
        return(systemSystemAdministrationPractitioner);
    }

    // System Administrator Resource (PractitionerRole) Set (and Attributes)

    protected Identifier createSystemAdministratorPractitionerRoleIdentifier(){
        LOG.debug(".createSystemAdministratorPractitionerRoleIdentifier(): Entry");
        // Create an empty FHIR::Identifier element
        Identifier systemSystemAdministrationPractitionerRole = new Identifier();
        // Set the FHIR::Identifier.Use to "SECONDARY" (this id is not their ABN or anything)
        systemSystemAdministrationPractitionerRole.setUse(Identifier.IdentifierUse.SECONDARY);
        // Set the FHIR::Identifier.Type to RI (Generalized Resource Identifier)
        CodeableConcept orgType = new CodeableConcept();
        Coding orgTypeCoding = new Coding();
        orgTypeCoding.setCode("RI");
        orgTypeCoding.setSystem("http://terminology.hl7.org/ValueSet/v2-0203");
        orgType.getCoding().add(orgTypeCoding);
        orgType.setText("Generalized Resource Identifier");
        systemSystemAdministrationPractitionerRole.setType(orgType);
        // Set the FHIR::Identifier.System to FHIRFactory (it's our ID we're creating)
        systemSystemAdministrationPractitionerRole.setSystem("FHIRFactory"); // TODO fix the system identification for Organization Identifiers
        // Set the FHIR::Identifier.Value to the NodeElementIdentifier for the TechnologyOne System
        systemSystemAdministrationPractitionerRole.setValue(this.getOrganizationName() + ": System Administrator Role");
        // Set the FHIR::Identifier.Period
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        systemSystemAdministrationPractitionerRole.setPeriod(validPeriod);
        // Set the FHIR::Identifier.Assigner (to us, for this one)
        systemSystemAdministrationPractitionerRole.setAssigner(new Reference("Organization/FHIRFactory"));
        LOG.debug(".createSystemAdministratorPractitionerRoleIdentifier(): Exit, created Identifier --> {}", systemSystemAdministrationPractitionerRole);
        return(systemSystemAdministrationPractitionerRole);
    }

    protected Reference createSystemAdministratorRoleReference() {
        LOG.debug(".createSystemAdministratorRoleReference(): Entry");
        Reference systemSysAdminRef = new Reference();
        systemSysAdminRef.setIdentifier(createSystemAdministratorPractitionerRoleIdentifier());
        systemSysAdminRef.setDisplay(this.getOrganizationName() + ": System Administrator");
        systemSysAdminRef.setType("PractitionerRole");
        LOG.debug(".createSystemAdministratorRoleReference(): Exit, created Reference --> {}", systemSysAdminRef);
        return (systemSysAdminRef);
    }

    protected PractitionerRole createSystemAdministratorPractitionerRole(){
        LOG.debug(".createSystemAdministratorPractitionerRole(): Entry");
        PractitionerRole systemAdministratorPractitionerRole = new PractitionerRole();
        systemAdministratorPractitionerRole.getIdentifier().add(getIdentifierSystemAdministratorPractitionerRole());
        systemAdministratorPractitionerRole.setActive(true);
        systemAdministratorPractitionerRole.setOrganization(this.getReferenceOrganization());
        LOG.debug(".createSystemAdministratorPractitionerRole(): Exit, created Practitioner --> {}", systemAdministratorPractitionerRole);
        return(systemAdministratorPractitionerRole);
    }

    // System Owner Resource (Practitioner) Set (and Attributes)

    protected Practitioner createSystemOwnerPractitioner(){
        LOG.debug(".createSystemOwner(): Entry");
        Practitioner systemSOPractitioner = new Practitioner();
        systemSOPractitioner.getIdentifier().add(getIdentifierSystemOwnerPractitioner());
        systemSOPractitioner.setActive(true);
        HumanName simpleName = new HumanName();
        simpleName.setText(this.getSystemOwnerContactName());
        Period nameEffectivePeriod = new Period();
        nameEffectivePeriod.setStart(Date.from(Instant.now()));
        simpleName.setPeriod(nameEffectivePeriod);
        systemSOPractitioner.getName().add(simpleName);
        LOG.debug(".createSystemOwner(): Exit, created Practitioner --> {}", systemSOPractitioner);
        return(systemSOPractitioner);
    }

    protected Identifier createSystemOwnerPractitionerIdentifier(){
        LOG.debug(".createOwningPractitionerIdentifier(): Entry");
        // Create an empty FHIR::Identifier element
        Identifier systemOwningPractitionerIdentifier = new Identifier();
        // Set the FHIR::Identifier.Use to "SECONDARY" (this id is not their ABN or anything)
        systemOwningPractitionerIdentifier.setUse(Identifier.IdentifierUse.SECONDARY);
        // Set the FHIR::Identifier.Type to RI (Generalized Resource Identifier)
        CodeableConcept orgType = new CodeableConcept();
        Coding orgTypeCoding = new Coding();
        orgTypeCoding.setCode("RI");
        orgTypeCoding.setSystem("http://terminology.hl7.org/ValueSet/v2-0203");
        orgType.getCoding().add(orgTypeCoding);
        orgType.setText("Generalized Resource Identifier");
        systemOwningPractitionerIdentifier.setType(orgType);
        // Set the FHIR::Identifier.System to FHIRFactory (it's our ID we're creating)
        systemOwningPractitionerIdentifier.setSystem("FHIRFactory"); // TODO fix the system identification for Organization Identifiers
        // Set the FHIR::Identifier.Value to the NodeElementIdentifier for the TechnologyOne System
        systemOwningPractitionerIdentifier.setValue(this.getSystemOwnerContactName());
        // Set the FHIR::Identifier.Period
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        systemOwningPractitionerIdentifier.setPeriod(validPeriod);
        // Set the FHIR::Identifier.Assigner (to us, for this one)
        systemOwningPractitionerIdentifier.setAssigner(new Reference("Organization/FHIRFactory"));
        LOG.debug(".createSystemOwnerIdentifier(): Exit, created Identifier --> {}", systemOwningPractitionerIdentifier);
        return(systemOwningPractitionerIdentifier);
    }

    protected Reference createSystemOwnerPractitionerReference(){
        LOG.debug(".createSystemOwnerPractitionerReference(): Entry");
        Reference systemSysOwnerRef = new Reference();
        systemSysOwnerRef.setIdentifier(getIdentifierSystemOwnerPractitioner());
        systemSysOwnerRef.setDisplay(this.getOrganizationName() + ": " + this.getSystemOwnerContactName());
        systemSysOwnerRef.setType("Practitioner");
        LOG.debug(".createSystemOwnerPractitionerReference(): Exit, created Reference --> {}", systemSysOwnerRef);
        return (systemSysOwnerRef);
    }

    // System Owner Resource (PractitionerRole) Set (and Attributes)

    protected Reference createSystemOwnerPractitionerRoleReference() {
        LOG.debug(".createOwningPractitionerReference(): Entry");
        Reference systemOwningPractitionerRef = new Reference();
        systemOwningPractitionerRef.setIdentifier(getIdentifierSystemOwnerPractitionerRole());
        systemOwningPractitionerRef.setDisplay(this.getOrganizationName() + ": System Owner");
        systemOwningPractitionerRef.setType("PractitionerRole");
        LOG.debug(".createOwningPractitionerReference(): Exit, created Reference --> {}", systemOwningPractitionerRef);
        return (systemOwningPractitionerRef);
    }

    protected Identifier createSystemOwnerPractitionerRoleIdentifier(){
        LOG.debug(".createSystemOwnerIdentifier(): Entry");
        // Create an empty FHIR::Identifier element
        Identifier systemOwningPractitionerIdentifier = new Identifier();
        // Set the FHIR::Identifier.Use to "SECONDARY" (this id is not their ABN or anything)
        systemOwningPractitionerIdentifier.setUse(Identifier.IdentifierUse.SECONDARY);
        // Set the FHIR::Identifier.Type to RI (Generalized Resource Identifier)
        CodeableConcept orgType = new CodeableConcept();
        Coding orgTypeCoding = new Coding();
        orgTypeCoding.setCode("RI");
        orgTypeCoding.setSystem("http://terminology.hl7.org/ValueSet/v2-0203");
        orgType.getCoding().add(orgTypeCoding);
        orgType.setText("Generalized Resource Identifier");
        systemOwningPractitionerIdentifier.setType(orgType);
        // Set the FHIR::Identifier.System to FHIRFactory (it's our ID we're creating)
        systemOwningPractitionerIdentifier.setSystem("FHIRFactory"); // TODO fix the system identification for Organization Identifiers
        // Set the FHIR::Identifier.Value to the NodeElementIdentifier for the TechnologyOne System
        systemOwningPractitionerIdentifier.setValue(this.getOrganizationName() + ": System Owner Role");
        // Set the FHIR::Identifier.Period
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        systemOwningPractitionerIdentifier.setPeriod(validPeriod);
        // Set the FHIR::Identifier.Assigner (to us, for this one)
        systemOwningPractitionerIdentifier.setAssigner(new Reference("Organization/FHIRFactory"));
        LOG.debug(".createSystemOwnerIdentifier(): Exit, created Identifier --> {}", systemOwningPractitionerIdentifier);
        return(systemOwningPractitionerIdentifier);
    }

    protected PractitionerRole createSystemOwnerPractitionerRole(){
        LOG.debug(".createSystemOwner(): Entry");
        PractitionerRole systemOwnerPractitionerRole = new PractitionerRole();
        systemOwnerPractitionerRole.getIdentifier().add(getIdentifierSystemOwnerPractitionerRole());
        systemOwnerPractitionerRole.setActive(true);
        systemOwnerPractitionerRole.setOrganization(this.getReferenceOrganization());
        LOG.debug(".createSystemOwner(): Exit, created Practitioner --> {}", systemOwnerPractitionerRole);
        return(systemOwnerPractitionerRole);
    }
    
    // System Owner Resource (Oranization) Set (and Attributes)
    
    protected Identifier createOrganizationIdentifier(){
        LOG.debug(".createOrganizationIdentifier(): Entry");
        // Create an empty FHIR::Identifier element
        Identifier systemIdentifier = new Identifier();
        // Set the FHIR::Identifier.Use to "SECONDARY" (this id is not their ABN or anything)
        systemIdentifier.setUse(Identifier.IdentifierUse.SECONDARY);
        // Set the FHIR::Identifier.Type to XX (General Organization Identifier)
        CodeableConcept orgType = new CodeableConcept();
        Coding orgTypeCoding = new Coding();
        orgTypeCoding.setCode("XX");
        orgTypeCoding.setSystem("http://terminology.hl7.org/ValueSet/v2-0203");
        orgType.getCoding().add(orgTypeCoding);
        orgType.setText("Organization Identifier");
        systemIdentifier.setType(orgType);
        // Set the FHIR::Identifier.System to FHIRFactory (it's our ID we're creating)
        systemIdentifier.setSystem("FHIRFactory"); // TODO fix the system identification for Organization Identifiers
        // Set the FHIR::Identifier.Value to the NodeElementIdentifier for the TechnologyOne System
        systemIdentifier.setValue(this.getOrganizationName());
        // Set the FHIR::Identifier.Period
        Period validPeriod = new Period();
        validPeriod.setStart(Date.from(Instant.now()));
        systemIdentifier.setPeriod(validPeriod);
        // Set the FHIR::Identifier.Assigner (to us, for this one)
        systemIdentifier.setAssigner(new Reference("Organization/FHIRFactory"));
        LOG.debug("createOrganizationIdentifier(): Exit, created Identifier --> {}", systemIdentifier);
        return (systemIdentifier);
    }

    protected Reference createOwningOrganizationReference() {
        LOG.debug(".createOwningOrganizationReference(): Entry");
        Reference systemReference = new Reference();
        systemReference.setIdentifier(getIdentifierSystemOwnerPractitioner());
        systemReference.setDisplay(this.getOrganizationName());
        systemReference.setType("Oranization");
        LOG.debug(".createOwningOrganizationReference(): Exit, created Reference --> {}", systemReference);
        return(systemReference);
    }

    protected Organization createOrganization(){
        LOG.debug(".createOrganization(): Entry");
        Organization systemOrganization = new Organization();
        systemOrganization.getIdentifier().add(getIdentifierSystemAdministratorPractitioner());
        systemOrganization.setActive(true);
        systemOrganization.getType().add(this.specifyOrganizationType());
        systemOrganization.setName(this.getOrganizationName());
        LOG.debug(".createOrganization(): Exit, created Organization --> {}", systemOrganization);
        return(systemOrganization);
    }
    
    // Bean Methods


    public Reference getReferenceSystemOwnerPractitioner() {
        return referenceSystemOwnerPractitioner;
    }

    public Reference getReferenceSystemAdministratorPractitioner() {
        return referenceSystemAdministratorPractitioner;
    }

    public Identifier getIdentifierSystemOwnerPractitionerRole() {
        return identifierSystemOwnerPractitionerRole;
    }

    public Identifier getIdentifierSystemAdministratorPractitionerRole() {
        return identifierSystemAdministratorPractitionerRole;
    }

    public Practitioner getPractitionerSystemAdministrator() {
        return practitionerSystemAdministrator;
    }

    public Practitioner getPractitionerSystemOwner() {
        return practitionerSystemOwner;
    }

    public String getSystemReference() {
        return systemReference;
    }

    public Identifier getIdentifierSystemOwnerPractitioner() {
        return identifierSystemOwnerPractitioner;
    }

    public Identifier getIdentifierSystemAdministratorPractitioner() {
        return identifierSystemAdministratorPractitioner;
    }

    public Organization getOrganizationSystemOwner() {
        return organizationSystemOwner;
    }

    public Endpoint getEndpointSystem() {
        return endpointSystem;
    }

    public Reference getReferenceOrganization() {
        return referenceOrganization;
    }

    public Reference getReferenceSystemOwnerOrganization() {
        return referenceSystemOwnerOrganization;
    }

    public Reference getReferenceSystemOwnerPractitionerRole() {
        return referenceSystemOwnerPractitionerRole;
    }

    public Reference getReferenceSystemAdministratorPractitionerRole() {
        return referenceSystemAdministratorPractitionerRole;
    }

    public SubsystemTopologyNode getTopologyNode() {
        return topologyNode;
    }

    public Identifier getIdentifierOrganization() {
        return identifierOrganization;
    }

    public Identifier getIdentifierSystemEndpoint() {
        return identifierSystemEndpoint;
    }

    public Reference getSystemEndpointReference() {
        return systemEndpointReference;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getSystemOwnerContactName() {
        return systemOwnerContactName;
    }

    public String getSystemOwnerContactEmail() {
        return systemOwnerContactEmail;
    }

    public String getSystemOwnerContactPhone() {
        return systemOwnerContactPhone;
    }

    public String getSystemAdministratorContactName() {
        return systemAdministratorContactName;
    }

    public String getSystemAdministratorContactEmail() {
        return systemAdministratorContactEmail;
    }

    public String getSystemAdministratorContactPhone() {
        return systemAdministratorContactPhone;
    }

    public String getSystemEndpointName() {
        return systemEndpointName;
    }

    public PractitionerRole getPractitionerRoleSystemAdministrator() {
        return practitionerRoleSystemAdministrator;
    }

    public PractitionerRole getPractitionerRoleSystemOwner() {
        return practitionerRoleSystemOwner;
    }

    public Identifier getSystemOwnerRoleIdentifier() {
        return systemOwnerRoleIdentifier;
    }

    public Identifier getSystemAdministratorRoleIdentifier() {
        return systemAdministratorRoleIdentifier;
    }
}
