package net.fhirfactory.pegacorn.internals.hl7v2.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;

public class MediaPipeParserTest {
	private MediaPipeParser mediaParser;
    private Parser pipeParser;
	
	@SuppressWarnings("resource")
	@BeforeEach
	public void setup() {
		pipeParser = new DefaultHapiContext().getPipeParser();
        pipeParser.getParserConfiguration().setValidating(false);
        pipeParser.getParserConfiguration().setEncodeEmptyMandatoryFirstSegments(true);
        mediaParser = new MediaPipeParser();
	}
	
	@Test
	public void testParseOBXSegmentsIntoChunks() {
		try {
			String message = loadORUAttachmentResource().encode();
			String obxSegment = mediaParser.extractNextAttachmentSegment(message);
			Assertions.assertNotNull(obxSegment);
			int count = 0;
			for(int i = 0; i < obxSegment.length(); i++) {
				if(obxSegment.charAt(i) == '|') {
					++count;
				}
			}
			String[] segments = mediaParser.breakSegmentIntoChunks(obxSegment );
			Assertions.assertNotNull(segments);
			Assertions.assertEquals(StringUtils.countMatches(obxSegment, '|') + 1, segments.length);
			String rejoined = mediaParser.rebuildSegmentFromChunks(segments);
			Assertions.assertEquals(obxSegment, rejoined);
		} catch (HL7Exception e) {
			Assertions.fail(e);
		} catch (IOException e) {
			Assertions.fail(e);
		}
	}
	
	@Test
	public void testReplacementURI() {
			try {
				String message = loadORUAttachmentResource().encode();
				String id = "abcdef";
				String fixedMessage = mediaParser.replaceAttachmentSegment(message, id);
				Assertions.assertNotNull(fixedMessage);
				Assertions.assertNotEquals(message, fixedMessage);
				Assertions.assertTrue(fixedMessage.contains(id));
				Assertions.assertNull(mediaParser.extractNextAttachmentSegment(fixedMessage));
			} catch (HL7Exception | IOException e) {
				Assertions.fail(e);
			}
	}
	
	@Test
	public void testExtractIDFromSegment() {
		try {
			String message = loadORUAttachmentResource().encode();
		
			//Show it failing
			String segment = mediaParser.extractNextAttachmentSegment(message);
			Assertions.assertTrue(segment.startsWith("OBX"));
			String newId = mediaParser.extractIdFromAlteredSegment(segment);
			Assertions.assertNull(newId);
			//Show it succeeding
			String id = "abcdef";
			message = mediaParser.replaceAttachmentSegment(message, id);
			Assertions.assertNotNull(message);
			segment = mediaParser.extractNextAlteredSegment(message);
			Assertions.assertNotNull(segment);
			Assertions.assertTrue(segment.startsWith("OBX"));
			newId = mediaParser.extractIdFromAlteredSegment(segment);
			Assertions.assertNotNull(newId);
			Assertions.assertEquals(id, newId);

		} catch (IOException | HL7Exception e) {
			Assertions.fail(e);
		}
	}
	
	private Message loadORUAttachmentResource() throws IOException {
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
