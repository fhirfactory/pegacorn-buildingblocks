/*
 * Copyright (c) 2021 Brendan Douglas
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
package net.fhirfactory.pegacorn.internals.hl7.v2x;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.util.SegmentFinder;
import ca.uhn.hl7v2.util.Terser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities that parse/query a HL7 document using a HL7 libnary/terser.
 * 
 * @author Brendan Douglas
 *
 */
@ApplicationScoped
public class PegacornInternalHL7TerserBasedUtils {
    private final Logger LOG = LoggerFactory.getLogger(PegacornInternalHL7MessageSimpleUtils.class);

	@Inject
	private PegacornInternalHL7MessageSimpleUtils internalHL7MessageSimpleUtils;

	@Inject
	private PegacornInternalHL7StringBasedUtils internalHL7StringBasedUtils;

	//
	// Constructor(s)
	//


	//
	// Getters (and Setters)
	//

	protected Logger getLogger(){
		return(this.LOG);
	}

	//
	// Business Methods
	//
    
	/**
	 * Returns a patient identifier. 
	 * 
	 * @param message
	 * @param identifier
	 * @param pidSegmentPath
	 */
	public String getPatientIdentifierValue(Message message, String identifier, String pidSegmentPath)  {

		try {
			Terser terser = new Terser(message);

			Segment segment = terser.getSegment(pidSegmentPath);
			int numberOfRepeitions = segment.getField(3).length;

			for (int i = 0; i < numberOfRepeitions; i++) {
				String identifierType = terser.get(pidSegmentPath + "-3(" + i + ")-5-1");

				if (identifierType != null && identifierType.equals(identifier)) {
					return terser.get(pidSegmentPath + "-3(" + i + ")-1-1");
				}
			}
		} catch (Exception ex){
			getLogger().warn(".getPatientIdentifierValue(): Cannot parse message, error->{}", ExceptionUtils.getMessage(ex));
		}
		
		return "";
	}
	
	
	public void removePatientIdentifierField(Message message, String identifier, String pidSegmentPath) throws Exception  {
		Terser terser = new Terser(message);
		
		Segment segment = terser.getSegment(pidSegmentPath);
		int numberOfRepeitions = segment.getField(3).length;
		
		for (int i = 0; i < numberOfRepeitions; i++) {
			String identifierType = terser.get(pidSegmentPath + "-3(" + i + ")-5-1");
			
			if (identifierType != null && identifierType.equals(identifier)) {
				((AbstractSegment)segment).removeRepetition(3, i);
			}
		}
		
		message.parse(message.toString());		
	}
	
	
	public void removePatientIdentifierTypeCode(Message message, String identifier, String pidSegmentPath) throws Exception  {
		Terser terser = new Terser(message);
		
		Segment segment = terser.getSegment(pidSegmentPath);
		int numberOfRepeitions = segment.getField(3).length;
		
		for (int i = 0; i < numberOfRepeitions; i++) {
			String identifierType = terser.get(pidSegmentPath + "-3(" + i + ")-5-1");
			
			if (identifierType != null && identifierType.equals(identifier)) {
				clear(message, pidSegmentPath + "-3(" + i + ")-5-1");
			}
		}
		
		message.parse(message.toString());		
	}
	
	
	/**
	 * Returns a list of identifiers in the PID segment.
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public List<String> getPatientIdentifierCodes(Message message, String pidSegmentPath) {
		List<String>identifiers = new ArrayList<>();

		try {

			Terser terser = new Terser(message);

			Segment segment = terser.getSegment(pidSegmentPath);
			int numberOfRepeitions = segment.getField(3).length;

			for (int i = 0; i < numberOfRepeitions; i++) {
				String identifier = terser.get(pidSegmentPath + "-3(" + i + ")-5-1");

				if (identifier != null) {
					identifiers.add(identifier);
				}
			}
		} catch(Exception ex){
			getLogger().warn(".getPatientIdentifierCodes(): Unable to parse message, error->{}", ExceptionUtils.getMessage(ex));
			identifiers.clear();
		}
		
		return identifiers;
	}

	
	/**
	 * Removes patient identifier which do not match the idetifier to keep.
	 * 
	 * @param message
	 * @param identifierToKeep
	 * @param pidSegmentPath
	 * @throws Exception
	 */
	public void removeOtherPatientIdentifierFields(Message message, String identifierToKeep, String pidSegmentPath) throws Exception  {
		List<String>patientIdentifierCodes = getPatientIdentifierCodes(message, pidSegmentPath);
		
		for (String patientIdentifierCode : patientIdentifierCodes) {
			if (!patientIdentifierCode.equals(identifierToKeep)) {
				removePatientIdentifierField(message, patientIdentifierCode, pidSegmentPath);
			}
		}	
	}

	
	/**
	 * Returns the number of repetitions of a field.
	 * 
	 * @param message
	 * @param segmentPathSpec
	 * @param fieldIndex
	 * @return
	 */
	public int getNumberOfRepetitions(Message message, String segmentPathSpec, int fieldIndex) throws Exception {
		Terser terser = new Terser(message);
		
		Segment segment = terser.getSegment(segmentPathSpec);
		return segment.getField(fieldIndex).length;
	}

	
	/**
	 * is the message of the supplied type.  The messageType can contain a wildcard eg. ADT_* for all ADT messages or no wildcard eg. ADT_A60.
	 * 
	 * @param message
	 * @param messageType
	 * @return
	 * @throws Exception
	 */
	public boolean isType(Message message, String messageType) throws Exception {
		Terser terser = new Terser(message);
		
		String type = terser.get("/MSH-9-3");

		if (StringUtils.isBlank(type)) {
			type = terser.get("/MSH-9-1") + "_" + terser.get("/MSH-9-2");
		}
		
		
		if (messageType.endsWith("_*")) {	
			return type.substring(0, 3).equals(messageType.substring(0, 3));
		}
		
		return type.equals(messageType);
	}

	
	/**
	 * Set a field value.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param value
	 * @throws HL7Exception
	 */
	public void set(Message message, String targetPathSpec, String value) throws Exception {	
		Terser terser = new Terser(message);
		terser.set(targetPathSpec, value);
	}

	
	/**
	 * Set a field value from another field.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param sourcePathSpec
	 * @throws HL7Exception
	 */
	public void copy(Message message, String targetPathSpec, String sourcePathSpec, boolean copyIfSourceIsBlank, boolean copyIfTargetIsBlank) throws Exception {	
		Terser terser = new Terser(message);
		
		String sourceValue = terser.get(sourcePathSpec);
		String targetValue = terser.get(targetPathSpec);
		
		if (!copyIfSourceIsBlank && StringUtils.isBlank(sourceValue)) {
			return;
		}
		
		if (!copyIfTargetIsBlank && StringUtils.isBlank(targetValue)) {
			return;
		}
		
		terser.set(targetPathSpec, sourceValue);	
	}

	
	/**
	 * Copies from the source field to the target, only if the source field contains a value.
	 * 
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param sourcePathSpec
	 * @param copyIfSourceIsBlank
	 * @param copyIfTargetIsBlank
	 * @throws Exception
	 */
	public void copyIfSourceExists(Message message, String targetPathSpec, String sourcePathSpec, boolean copyIfSourceIsBlank, boolean copyIfTargetIsBlank) throws Exception {	
		copy(message, targetPathSpec, sourcePathSpec, false, true);
	}

	
	/**
	 * Copies from one field to another.  If the source value is null a default value is used.
	 * 
	 * @param message
	 * @param sourcePathSpec
	 * @param targetPathSpec
	 * @param defaultSourcepathSpec
	 * @throws Exception
	 */
	public void copy(Message message, String targetPathSpec, String sourcePathSpec, String defaultSourcepathSpec) throws Exception {
		Terser terser = new Terser(message);
		
		String sourceValue = terser.get(sourcePathSpec);
		
		if (sourceValue == null) {
			sourceValue = terser.get(defaultSourcepathSpec);
		}
		
		terser.set(targetPathSpec, sourceValue);			
	}

	
	/**
	 * Copies the content of the source path before the seperator character to the target.  If the seperator does not exists the entire field is copied.
	 * 
	 * @param message
	 * @param sourcePathSpec
	 * @param targetPathSpec
	 * @param seperator
	 */
	public void copySubstringBefore(Message message, String targetPathSpec, String sourcePathSpec, String seperator) throws Exception {
		Terser terser = new Terser(message);
		
		String sourceValue = terser.get(sourcePathSpec);	
		int indexOfSeperator = StringUtils.indexOf(sourceValue, seperator);
		
		if (indexOfSeperator == -1) {
			terser.set(targetPathSpec, sourceValue);
		} else {
			terser.set(targetPathSpec, StringUtils.substring(sourceValue, 0, indexOfSeperator));
		}
	}
	
	
	/**
	 * Copies the content of the source path after the seperator character to the target.  If the seperator does not exists the entire field is copied.
	 * 
	 * @param message
	 * @param sourcePathSpec
	 * @param targetPathSpec
	 * @param seperator
	 */
	public void copySubstringAfter(Message message, String targetPathSpec, String sourcePathSpec, String seperator) throws Exception {
		Terser terser = new Terser(message);
		
		String sourceValue = terser.get(sourcePathSpec);	
		int indexOfSeperator = StringUtils.indexOf(sourceValue, seperator);
		
		if (indexOfSeperator == -1) {
			terser.set(targetPathSpec, sourceValue);
		} else {
			terser.set(targetPathSpec, StringUtils.substring(sourceValue, indexOfSeperator + 1, sourceValue.length()));
		}
	}

	
	/**
	 * Appends the supplied text to the value at the targetPathSpec.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param textToAppend
	 */
	public void append(Message message, String targetPathSpec, String textToAppend) throws Exception {
		String targetValue = get(message, targetPathSpec);	
		set(message, targetPathSpec, targetValue + textToAppend);		
	}

	
	/**
	 * Prepends the supplied text to the value at the targetPathSpec.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param textToPrepend
	 */
	public void prepend(Message message, String targetPathSpec, String textToPrepend) throws Exception {
		String targetValue = get(message, targetPathSpec);	
		set(message, targetPathSpec, textToPrepend + targetValue);
	}
	
	
	/**
	 * Uses a lookup table to change a fields value.
	 *
	 * @param message
	 * @param targetPathSpec
	 * @param lookupTableClassName
	 * @throws HL7Exception
	 */
	public void lookup(Message message, String targetPathSpec, String lookupTableClassName) throws Exception {	
		
		try {
			Terser terser = new Terser(message);
			
			String existingValue = terser.get(targetPathSpec);
		
			// Use reflection to instantiate the appropriate lookup table class
			Class<?> lookupTableClass = Class.forName(lookupTableClassName);
			Constructor<?> lookupTableConstructor = lookupTableClass.getConstructor();
			PegacornInternalHL7MessageUtilsLookupTable lookupTable = (PegacornInternalHL7MessageUtilsLookupTable) lookupTableConstructor.newInstance();
			
			String transformedValue = lookupTable.lookup(existingValue);
			terser.set(targetPathSpec, transformedValue);
		} catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
			LOG.info("Unable to construct lookup class: {} ", lookupTableClassName);
		} 
	}

	
	/**
	 * Calls a Java class to set the target path value.
	 *
	 * @param message
	 * @param targetPathSpec
	 * @param fieldTransformationClassName
	 */
	public void updateFieldFromCode(Message message, String targetPathSpec, String fieldTransformationClassName) throws Exception {
		Terser terser = new Terser(message);
		
		try {
			// Use reflection to instantiate the appropriate code transformation class
			Class<?> fieldTransformationClass = Class.forName(fieldTransformationClassName);
			Constructor<?> fieldTransformationClassConstructor = fieldTransformationClass.getConstructor();
			PegacornInternalHL7MessageUtilsFieldCodeTransformation transformation = (PegacornInternalHL7MessageUtilsFieldCodeTransformation) fieldTransformationClassConstructor.newInstance();
			
			String transformedValue = transformation.execute(message);
			terser.set(targetPathSpec, transformedValue);
		} catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
			LOG.info("Unable to construct lookup class: {} ", fieldTransformationClassName);
		}
	}

	
	/**
	 * Calls a Java class to set the target path value.
	 * 
	 * @param message
	 * @param transformationClassName
	 */
	public void updateMessageFromCode(Message message, String transformationClassName) throws Exception {
		
		try {
			// Use reflection to instantiate the appropriate code transformation class
			Class<?> transformationClass = Class.forName(transformationClassName);
			Constructor<?> transformationClassConstructor = transformationClass.getConstructor();
			PegacornInternalHL7MessageUtilsCodeTransformation transformation = (PegacornInternalHL7MessageUtilsCodeTransformation) transformationClassConstructor.newInstance();
			
			transformation.execute(message);
		} catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
			LOG.info("Unable to construct lookup class: {} ", transformationClassName);
		}
	}

	
	/**
	 * Clear a field value.  This clears ALL subfields.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @throws HL7Exception
	 */
	public void clear(Message message, String targetPathSpec) throws Exception {
		Terser terser = new Terser(message);	
			
		terser.set(targetPathSpec, "");
		
		// Clear any subfields. //TODO get the number of sub fields if possible.  30 should be OK for now.
		for (int i = 1; i <= 30; i++) {
			terser.set(targetPathSpec + "-" + i, "");
		}
	}
	
	
	/**
	 * Set a field value from a string with variables.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param value
	 * @param params
	 * @throws HL7Exception
	 */
	public void set(Message message, String targetPathSpec, String value, String ... params) throws Exception {
		Terser terser = new Terser(message);
		
		String finalValue = String.format(value, (Object[])params);
		terser.set(targetPathSpec, finalValue);
	}

	
	/**
	 * Changes the message type
	 * 
	 * @param newMessageType
	 * @throws HL7Exception
	 */
	public void changeMessageType(Message message, String newMessageType) throws Exception {
		Terser terser = new Terser(message);
		terser.set("/MSH-9", newMessageType);
	}

	
	/**
	 * Gets a field value.
	 * 
	 * @param message
	 * @param sourcePathSpec
	 * @return
	 * @throws HL7Exception
	 */
	public String get(Message message, String sourcePathSpec) throws Exception {	
		Terser terser = new Terser(message);
		return terser.get(sourcePathSpec);
	}
	

	
	/**
	 * Removes a single segment from a message.
	 * 
	 * @param message
	 * @param sourcePathSpec
	 * @throws HL7Exception
	 */
	public void removeSegment(Message message, String sourcePathSpec) throws Exception {
		Terser terser = new Terser(message);
		
		try {
			AbstractSegment segment = (AbstractSegment)terser.getSegment(sourcePathSpec);

			segment.clear();
			
			// Update the message object with the changes.
			message.parse(message.toString());
		} catch(HL7Exception e ) {
			LOG.warn("Segment to delete does not exist: {}", sourcePathSpec);
		}
	}
	
	
	/**
	 * Removes all segments matching the segment name no matter where they appear in the message.  Please note the segment name is not a path spec.
	 * 
	 * @param message
	 * @param segmentName
	 * @throws HL7Exception
	 */
	public void removeAllSegments(Message message, String segmentName) throws Exception {	
		Terser terser = new Terser(message);
		
		SegmentFinder finder = terser.getFinder();
		
		while(true) {
			try {
				String name = finder.iterate(true, false); // iterate segments only.  The first true = segments.
				
				if (name.startsWith(segmentName)) {

					for (Structure structure : finder.getCurrentChildReps()) {
						AbstractSegment segment = (AbstractSegment)structure;
						segment.clear();
					}
				}
			} catch(HL7Exception e) {
				break;
			}
		}
		
		// Update the message object with the changes.
		message.parse(message.toString());
	}
	
	
	/**
	 * Sets the segments to send.  All other segments are removed.  
	 * 
	 * @param message
	 * @param setSegmentsToKeep
	 */
	public void setSegmentsToKeep(Message message, String ... setSegmentsToKeep) throws Exception {	
		Terser terser = new Terser(message);
		
		SegmentFinder finder = terser.getFinder();
		
		while(true) {
			try {
				String name = finder.iterate(true, false); // iterate segments only.  The first true = segments.
				
				if (!doesContainSegment(message, name, setSegmentsToKeep)) {
					
					for (Structure structure : finder.getCurrentChildReps()) {
						AbstractSegment segment = (AbstractSegment)structure;
						segment.clear();
					}
				}
			} catch(HL7Exception e) {
				break;
			}
		}
		
		// Update the message object with the changes.
		message.parse(message.toString());
	}

	
	public void setSegmentsToKeep(Message message, String setSegmentsToKeep) throws Exception {		
		setSegmentsToKeep(message, setSegmentsToKeep.split(","));
	}
	

	/**
	 * Returns a list of all matching segments.  Please note the segment name is not a path spec.
	 * 
	 * @param message
	 * @param segmentName
	 * @return
	 */
	public List<Segment>getAllSegments(Message message, String segmentName) throws Exception {
		Terser terser = new Terser(message);
		
		List<Segment>segments = new ArrayList<>();
		
		SegmentFinder finder = terser.getFinder();
		
		while(true) {
			try {
				String name = finder.iterate(true, false); // iterate segments only.  The first true = segments.
				
				if (name.startsWith(segmentName)) {
					
					for (Structure structure : finder.getCurrentChildReps()) {
						segments.add((Segment)structure);
					}
				}
			} catch(HL7Exception e) {
				break;
			}
		}	
		
		return segments;
	}

	
	private boolean doesContainSegment(Message message, String segment, String[] requiredSegments) {
		for (String requiredSegment : requiredSegments) {
			if (segment.startsWith(requiredSegment)) {
				return true;
			}
		}
		
		return false;
	}

	
	/**
	 * Check if a segment exists.
	 * 
	 * @param message
	 * @param segment
	 * @return
	 */
	public boolean doesSegmentExist(Message message, String segment) throws Exception {
		 String regex = segment + "\\|";
		 Pattern pattern = Pattern.compile(regex);
		 Matcher matcher = pattern.matcher(message.toString());
		 
		 return matcher.find();
	}

	
	/**
	 * Executes an action for each segment which matches the segment name.
	 * 
	 * @param message
	 * @param segmentName
	 * @param actionClassName
	 */
	public void forEachSegment(Message message, String segmentName, String actionClassName) throws Exception {
		try {
			
			// Use reflection to instantiate the appropriate segment action class.
			Class<?> actionClass = Class.forName(actionClassName);
			Constructor<?> actionClassConstructor = actionClass.getConstructor();
			PegacornInternalHL7MessageSegmentAction segmentAction = (PegacornInternalHL7MessageSegmentAction) actionClassConstructor.newInstance();
			
			for (Segment segment : getAllSegments(message, segmentName)) {
				segmentAction.execute(segment);
			}
		} catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
			LOG.info("Unable to construct lookup class: {} ", actionClassName);
		}	
	}

	
	/**
	 * Executes an action for a single segment.
	 * 
	 * @param message
	 * @param sourcePathSpec
	 * @param actionClassName
	 */
	public void segmentAction(Message message, String sourcePathSpec, String actionClassName) throws Exception {
		try {
			
			Terser terser = new Terser(message);
			AbstractSegment segment = (AbstractSegment)terser.getSegment(sourcePathSpec);
			
			// Use reflection to instantiate the appropriate segment action class.
			Class<?> actionClass = Class.forName(actionClassName);
			Constructor<?> actionClassConstructor = actionClass.getConstructor();
			PegacornInternalHL7MessageSegmentAction segmentAction = (PegacornInternalHL7MessageSegmentAction) actionClassConstructor.newInstance();
			
			segmentAction.execute(segment);
		} catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
			throw new HL7Exception("Unable to construct segment action class", e);
		}		
	}
	
	
	/**
	 * Concatenate field values.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param seperator
	 * @param sourcePathSpecs
	 */
	public void concatenate(Message message, String targetPathSpec, String seperator, String ... sourcePathSpecs) throws Exception {
		Terser terser = new Terser(message);
		
		StringBuilder sb = new StringBuilder();
		
		for (String sourcePathSpec : sourcePathSpecs) {
			if (sb.length() > 0) {
				sb.append(seperator);
			}
			
			String sourceFieldValue = terser.get(sourcePathSpec);
			sb.append(sourceFieldValue);
		}
		
		terser.set(targetPathSpec, sb.toString());
	}

	
	/**
	 * Copies a value from one field to another and replace the params.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param sourcePathSpec
	 * @param sourcePathSpecs
	 * @throws Exception
	 */
	public void copyReplaceParam(Message message, String targetPathSpec, String sourcePathSpec, String ... sourcePathSpecs) throws Exception {
		Terser terser = new Terser(message);
		
		String sourceText = terser.get(sourcePathSpec);
		
		for (int i = 0; i < sourcePathSpecs.length; i++) {
			String sourceValue = terser.get(sourcePathSpecs[i]);
			
			StringUtils.replace(sourceText, "[" + sourcePathSpecs[i] + "]", sourceValue);
		}
		
		terser.set(targetPathSpec, sourceText);
	}
}
