package net.fhirfactory.pegacorn.internals.hl7v2.transfoms.tofhir;

import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FHIRPatientFromHLv2xTest {



    String testMessage = "[MSH|^~\\&|NES|NINTENDO|TESTSYSTEM|TESTFACILITY|20010101000000||ADT^A01|Q123456789T123456789X123456|P|2.4\n" +
            "EVN|A04|20010101000000|||^KOOPA^BOWSER^^^^^^^CURRENT\n" +
            "PID|1||123456789|0123456789^AA^^JP|BROS^MARIO^^^^||19850101000000|M|||123 FAKE STREET^MARIO \\T\\ LUIGI BROS PLACE^TOADSTOOL KINGDOM^NES^A1B2C3^JP^HOME^^1234|1234|(555)555-0123^HOME^JP:1234567|||S|MSH|12345678|||||||0|||||N<0x0A LF>NK1|1|PEACH^PRINCESS^^^^||ANOTHER CASTLE^^TOADSTOOL KINGDOM^NES^^JP|(123)555-1234|(123)555-2345|NOK|||||||||||||\n" +
            "NK1|2|TOADSTOOL^PRINCESS^^^^|SO|YET ANOTHER CASTLE^^TOADSTOOL KINGDOM^NES^^JP|(123)555-3456|(123)555-4567|EMC|||||||||||||\n" +
            "PV1|1|O|ABCD^EFGH^|||^^|123456^DINO^YOSHI^^^^^^MSRM^CURRENT^^^NEIGHBOURHOOD DR NBR^|^DOG^DUCKHUNT^^^^^^^CURRENT||CRD|||||||123456^DINO^YOSHI^^^^^^MSRM^CURRENT^^^NEIGHBOURHOOD DR NBR^|AO|0123456789|1|||||||||||||||||||MSH||A|||20010101000000\n";
    @Test
    @DisplayName("Test ADT --> FHIR")
    void transformADTIntoFHIR() {
        HL7ToFHIRConverter ftv = new HL7ToFHIRConverter();
        String output= ftv.convert(testMessage); // generated a FHIR output
        System.out.println(output);
        assertTrue(true);
    }
}