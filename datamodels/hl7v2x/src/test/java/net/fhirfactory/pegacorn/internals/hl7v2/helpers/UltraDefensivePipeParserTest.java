package net.fhirfactory.pegacorn.internals.hl7v2.helpers;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import net.fhirfactory.pegacorn.internals.hl7v2.triggerevents.valuesets.HL7v2SegmentTypeEnum;

class UltraDefensivePipeParserTest {
	
	private UltraDefensivePipeParser defensivePipeParser;
    private Parser pipeParser;
	
	@SuppressWarnings("resource")
	@BeforeEach
	public void setup() {
		pipeParser = new DefaultHapiContext().getPipeParser();
        pipeParser.getParserConfiguration().setValidating(false);
        pipeParser.getParserConfiguration().setEncodeEmptyMandatoryFirstSegments(true);
        defensivePipeParser = new UltraDefensivePipeParser();
	}

    @Test
    void getSegment() {
    }
    
    @Test
    void testPatterMatcher( ) {
    	Message message;
		try {
			message = loadORUResource();
			Assertions.assertFalse(defensivePipeParser.hasMatchingPatternInSegmentType(message.encode(), "^Base63^", HL7v2SegmentTypeEnum.OBX));
			Assertions.assertTrue(defensivePipeParser.hasMatchingPatternInSegmentType(message.encode(), "^Base64^", HL7v2SegmentTypeEnum.OBX));
		} catch (IOException e) {
			fail(e);
		} catch (HL7Exception e) {
			fail(e);
		}
    	
    }
    
	
	private Message loadORUResource() throws IOException {
		Path filePath = Path.of("./src/test/resources/hl7/message/oru_r01_wth_attachment.txt");
		return loadResource(filePath);
	}
	
	
	
	private Message loadResource(Path filePath) throws IOException {

		String content = Files.readString(filePath);
		Message hl7Msg;
		try {
			hl7Msg = pipeParser.parse(content);
			return hl7Msg;
		} catch (HL7Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}