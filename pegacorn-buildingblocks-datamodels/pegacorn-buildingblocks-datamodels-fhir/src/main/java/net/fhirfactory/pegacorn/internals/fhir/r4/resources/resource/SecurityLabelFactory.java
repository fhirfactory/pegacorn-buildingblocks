package net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource;

import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.valuesets.InformationCompartmentSecurityCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.valuesets.InformationConfidentialitySecurityCodeEnum;
import net.fhirfactory.pegacorn.internals.fhir.r4.resources.resource.valuesets.InformationSensitivitySecurityCodeEnum;
import org.hl7.fhir.r4.model.*;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SecurityLabelFactory {

    public Coding constructConfidentialitySecurityLabel(InformationConfidentialitySecurityCodeEnum confidentialityCode){
        Coding securityLabelConfidentialityCoding = new Coding();
        securityLabelConfidentialityCoding.setCode(confidentialityCode.name());
        securityLabelConfidentialityCoding.setDisplay(confidentialityCode.getDisplayText());
        securityLabelConfidentialityCoding.setSystem("http://terminology.hl7.org/CodeSystem/v3-Confidentiality");
        return(securityLabelConfidentialityCoding);
    }

    public Coding constructSecurityLabelSensitivity(InformationSensitivitySecurityCodeEnum sensitivityCode){
        Coding securityLabelSensitivityCoding = new Coding();
        securityLabelSensitivityCoding.setCode(sensitivityCode.name());
        securityLabelSensitivityCoding.setDisplay(sensitivityCode.getDisplayText());
        securityLabelSensitivityCoding.setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode");
        return(securityLabelSensitivityCoding);
    }

    public Coding constructSecurityLabelCompartment(InformationCompartmentSecurityCodeEnum compartmentCode) {
        Coding securityLabelCompartmentCoding = new Coding();
        securityLabelCompartmentCoding.setCode(compartmentCode.name());
        securityLabelCompartmentCoding.setDisplay(compartmentCode.getDisplayText());
        securityLabelCompartmentCoding.setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode");
        return (securityLabelCompartmentCoding);
    }
}
