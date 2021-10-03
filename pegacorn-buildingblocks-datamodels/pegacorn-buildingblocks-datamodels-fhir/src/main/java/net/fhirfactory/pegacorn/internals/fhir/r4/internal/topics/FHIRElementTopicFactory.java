/*
 * Copyright (c) 2020 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelToken;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelTypeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FHIRElementTopicFactory {
    private static final Logger LOG = LoggerFactory.getLogger(FHIRElementTopicFactory.class);

    private static String HL7_MESSAGE_DEFINER = "HL7-FHIR";

    public DataParcelTypeDescriptor newTopicToken(String resourceName, String version){
        LOG.debug(".newTopicToken(): Entry, resourceName->{}, version->{}", resourceName, version);
        if(resourceName == null){
            LOG.debug(".newTopicToken(): Exit, resourceName is null");
            return(null);
        }
        if(version == null){
            LOG.debug(".newTopicToken(): Exit, version is null");
            return(null);
        }
        DataParcelTypeDescriptor newToken = newTopicToken(resourceName);
        newToken.setVersion(version);
        LOG.debug(".newTopicToken(): Exit, newToken->{}", newToken);
        return(newToken);
    }

    public DataParcelTypeDescriptor newTopicToken(String resourceName){
        LOG.debug(".newTopicToken(): Entry, resourceName->{}", resourceName);
        DataParcelTypeDescriptor token = new DataParcelTypeDescriptor();
        token.setDataParcelDefiner(HL7_MESSAGE_DEFINER);
        switch(resourceName){
            case "IdType":{
                token.setDataParcelCategory("DataTypes");
                token.setDataParcelSubCategory("PrimitiveTypes");
                token.setDataParcelResource("IdType");
                break;
            }
            //
            // Base :: Individuals
            //
            case "Patient":
            case "Practitioner":
            case "PractitionerRole":
            case "RelatedPerson":
            case "Person":
            case "Group":
            {
                token.setDataParcelCategory("Base");
                token.setDataParcelSubCategory("Individuals");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Base :: Entities
            //
            case "Organization":
            case "OrganizationAffiliation":
            case "HealthcareService":
            case "Endpoint":
            case "Location":
            case "Substance":
            case "BiologicallyDerivedProduct":
            case "Device":
            case "DeviceMetric": {
                token.setDataParcelCategory("Base");
                token.setDataParcelSubCategory("Entities");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Base :: Workflow
            //
            case "Task":
            case "Appointment":
            case "AppointmentResponse":
            case "Schedule":
            case "Slot":
            case "VerificationResult": {
                token.setDataParcelCategory("Base");
                token.setDataParcelSubCategory("Workflow");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Base :: Management
            //
            case "Encounter":
            case "Flag":
            case "List":
            case "Library":
            case "EpisodeOfCare": {
                token.setDataParcelCategory("Base");
                token.setDataParcelSubCategory("Management");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Clinical :: Summary
            //
            case "AllergyIntolerance":
            case "AdverseEvent":
            case "Condition":
            case "Procedure":
            case "FamilyMemberHistory":
            case "ClinicalImpression":
            case "DetectedIssue": {
                token.setDataParcelCategory("Clinical");
                token.setDataParcelSubCategory("Summary");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Clinical :: Diagnostics
            //
            case "Observation":
            case "Media":
            case "DiagnosticReport":
            case "Specimen":
            case "BodyStructure":
            case "ImagingStudy":
            case "QuestionnaireResponse":
            case "MolecularSequence": {
                token.setDataParcelCategory("Clinical");
                token.setDataParcelSubCategory("Diagnostics");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Clinical :: Medications
            //
            case "MedicationRequest":
            case "MedicationAdministration":
            case "MedicationDispense":
            case "MedicationStatement":
            case "Medication":
            case "MedicationKnowledge":
            case "Immunization":
            case "ImmunizationEvaluation":
            case "ImmunizationRecommendation": {
                token.setDataParcelCategory("Clinical");
                token.setDataParcelSubCategory("Medications");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Clinical :: Care Provision
            //
            case "CarePlan":
            case "CareTeam":
            case "Goal":
            case "ServiceRequest":
            case "NutritionOrder":
            case "VisionPrescription":
            case "RiskAssessment":
            case "RequestGroup": {
                token.setDataParcelCategory("Clinical");
                token.setDataParcelSubCategory("CareProvision");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Clinical :: Request & Response
            //
            case "Communication":
            case "CommunicationRequest":
            case "DeviceRequest":
            case "DeviceUseStatement":
            case "GuidanceResponse":
            case "SupplyRequest":
            case "SupplyDelivery": {
                token.setDataParcelCategory("Clinical");
                token.setDataParcelSubCategory("Request&Response");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Foundation :: Documents
            //
            case "Composition":
            case "DocumentManifest":
            case "DocumentReference":
            case "CatalogEntry": {
                token.setDataParcelCategory("Foundation");
                token.setDataParcelSubCategory("Documents");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Foundation :: Other
            //
            case "Basic":
            case "Binary":
            case "Bundle":
            case "Linkage":
            case "MessageHeader":
            case "OperationOutcome":
            case "Parameters":
            case "Subscription": {
                token.setDataParcelCategory("Foundation");
                token.setDataParcelSubCategory("Other");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Foundation :: Security
            //
            case "AuditEvent":
            case "Provenance":
            case "Consent": {
                token.setDataParcelCategory("Foundation");
                token.setDataParcelSubCategory("Security");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Foundation :: Terminology
            //
            case "CodeSystem":
            case "ValueSet":
            case "ConceptMap":
            case "NamingSystem":
            case "TerminologyCapabilities": {
                token.setDataParcelCategory("Foundation");
                token.setDataParcelSubCategory("Terminology");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Foundation :: Conformance
            //
            case "CapabilityStatement":
            case "StructureDefinition":
            case "ImplementationGuide":
            case "SearchParameter":
            case "MessageDefinition":
            case "OperationDefinition":
            case "CompartmentDefinition":
            case "StructureMap":
            case "GraphDefinition":
            case "ExampleScenario": {
                token.setDataParcelCategory("Foundation");
                token.setDataParcelSubCategory("Conformance");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Financial :: Support
            //
            case "Coverage":
            case "CoverageEligibilityRequest":
            case "CoverageEligibilityResponse":
            case "EnrollmentRequest":
            case "EnrollmentResponse": {
                token.setDataParcelCategory("Financial");
                token.setDataParcelSubCategory("Support");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Financial :: Billing
            //
            case "Claim":
            case "ClaimResponse":
            case "Invoice": {
                token.setDataParcelCategory("Financial");
                token.setDataParcelSubCategory("Billing");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Financial :: Payment
            //
            case "PaymentNotice":
            case "PaymentReconciliation": {
                token.setDataParcelCategory("Financial");
                token.setDataParcelSubCategory("Payment");
                token.setDataParcelResource(resourceName);
                break;
            }
            //
            // Financial :: General
            //
            case "Account":
            case "ChargeItem":
            case "ChargeItemDefinition":
            case "Contract":
            case "ExplanationOfBenefit":
            case "InsurancePlan": {
                token.setDataParcelCategory("Financial");
                token.setDataParcelSubCategory("General");
                token.setDataParcelResource(resourceName);
                break;
            }
            default:
                break;
        }
        LOG.debug(".newTopicToken(): Exit, token->{}", token);
        return (token);
    }
}
