/*
 * Copyright (c) 2022 Mark A. Hunter
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
package net.fhirfactory.pegacorn.internals.hl7v2.helpers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.hl7.fhir.r4.model.Media;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

import com.google.common.annotations.VisibleForTesting;

import javax.inject.Inject;

import net.fhirfactory.pegacorn.internals.hl7v2.triggerevents.valuesets.HL7v2SegmentTypeEnum;

@ApplicationScoped
public class MediaPipeParser {
    private static final String ENCAPSULATED_DATA = "ED";
	private static final String REFERENCE_POINTER = "RP";
	private static final String OBX_KEY = HL7v2SegmentTypeEnum.OBX.getKey();
	private static final Logger LOG = LoggerFactory.getLogger(MediaPipeParser.class);
    private static final String BASE64_PATTERN = "^Base64^";
	private static final String PREFIX = "<fhir-resource>https://hestia-dam-service/fhir/r4/media/id=";
	private static final String SUFFIX = "</fhir-resource>";


    private boolean initialised;

    
    @Inject
    private UltraDefensivePipeParser pipeParser;
    
    //
    // Constructor(s)
    //

    public MediaPipeParser(){
        initialised = false;
    }

    //
    // Post Constructor(s)
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(isInitialised()){
            getLogger().debug(".initialise(): Already initialised, nothing to do!");
        } else {
            getLogger().info(".initialise(): Initialisation Start...");


            setInitialised(true);
            getLogger().info(".initialise(): Initialisation Finish...");
        }
        getLogger().debug(".initialise(): Exit");

    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected boolean isInitialised() {
        return initialised;
    }

    protected void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    //
    // Business Methods
    //

    public String extractOBXSegment(String message){
        if(StringUtils.isEmpty(message)){
            return(null);
        }
        List<String> segmentList = pipeParser.getSegmentList(message);
        String segment = pipeParser.getSegment(segmentList, OBX_KEY,1);
        return(segment);
    }
    
    public String extractNextAttachmentSegment(String message) {
        if(StringUtils.isEmpty(message)){
            return(null);
        }
        return extractSegmentWithPattern(message, BASE64_PATTERN);
    }
    
    public String extractNextAlteredSegment(String message) {
        if(StringUtils.isEmpty(message)){
            return(null);
        }
        return extractSegmentWithPattern(message, PREFIX);
    }

	private String extractSegmentWithPattern(String message, String pattern) {
		List<String> segmentList = pipeParser.getSegmentList(message);
        for(String currentSegment: segmentList){
            if(currentSegment.startsWith(OBX_KEY)){
            	if(currentSegment.contains(pattern)) {
            		return (currentSegment);
            	}
            }
        }
        return (null);
	}
    public String[] breakSegmentIntoChunks(String message) {
    	if(StringUtils.isEmpty(message)){
            return(null);
        }
    	return message.split("\\|");
    }
    public String rebuildSegmentFromChunks(String[] chunks) {
    	if(chunks == null || chunks.length == 0) {
    		return (null);
    	}
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < chunks.length; i++) {
    		sb.append(chunks[i]);
    		sb.append('|');
    	}
    	//Get rid of the final |
    	return sb.substring(0, sb.length() - 1);
    }

    public String replaceAttachmentSegment(String message, String id) {
    	String obx = extractNextAttachmentSegment(message);
    	String[] chunks = breakSegmentIntoChunks(obx);
    	chunks[2] = REFERENCE_POINTER;
    	chunks[5] = buildfilePathOutOfId(id);
    	String fixed = rebuildSegmentFromChunks(chunks);
    	message = message.replace(obx, fixed); //XXX inefficient?
       	return (message);
    }
    
    public String buildfilePathOutOfId(String id) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(PREFIX);
    	sb.append(id);
    	sb.append(SUFFIX);
    	return sb.substring(0);
    }

	public String extractIdFromAlteredSegment(String segment) {
		if(StringUtils.isEmpty(segment)) {
			return (null);
		}
		int prefixIndex = segment.indexOf(PREFIX);
		int suffixIndex = segment.indexOf(SUFFIX);
				
		if(prefixIndex >= 0) {
			return segment.substring(prefixIndex + PREFIX.length(), suffixIndex);
		}
		return (null);
	}
	
    public String replaceAlteredSegment(String message, Media media) {
    	String obx = extractNextAlteredSegment(message);
    	String[] chunks = breakSegmentIntoChunks(obx);
    	chunks[2] = ENCAPSULATED_DATA;
    	chunks[5] = convertMediaBackToMessage(media);
    	String fixed = rebuildSegmentFromChunks(chunks);
    	message = message.replace(obx, fixed); //XXX inefficient?
       	return (message);
    }
    
    private String convertMediaBackToMessage(Media media) {
		
		return contentTypeToHL7(media.getContent().getContentType()) 
				+ BASE64_PATTERN + media.getContent().getData().toString();
	}

	public String contentTypeToHL7(String contentType) {
		//TODO fill out as necessary
		String hl7 = "";
		
		switch(contentType) {
		case "application/pdf":
			hl7 = "^application^pdf";
			break;
		default:
			hl7= "";
			break;
		}
		return hl7;
	}
	
	public String hl7ToContentType(String hl7) {
		//TODO fill out as necessary
		String contentType = "";
		
		switch(hl7) {
		case "^application^pdf":
			contentType = "application/pdf";
			break;
		default:
			contentType= "application/octet-stream";
			break;
		}
		return contentType;
	}

	public boolean hasMatchingPatternInSegmentType(String message, String pattern, HL7v2SegmentTypeEnum segmentType) {
    	return pipeParser.hasMatchingPatternInSegmentType(message, pattern,segmentType);
    }
    
    @VisibleForTesting
    public void setPipeParser(UltraDefensivePipeParser pipeParser) {
    	this.pipeParser = pipeParser;
    }

}
