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

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods to transform a messages and to get date from a message.
 * 
 * @author Brendan Douglas
 *
 */

@ApplicationScoped
public class PegacornInternalHL7MessageSimpleUtils {
	private static final Logger LOG = LoggerFactory.getLogger(PegacornInternalHL7MessageSimpleUtils.class);

	private HapiContext hapiContext;

	@Inject
	private PegacornInternalHL7TerserBasedUtils terserBasedUtils;
	
	@Inject 
	private PegacornInternalHL7StringBasedUtils stringBasedUtils;

	//
	// Constructor(s)
	//

	public PegacornInternalHL7MessageSimpleUtils(){
		hapiContext = new DefaultHapiContext();
	}

	//
	// Getters and Setters
	//

	protected HapiContext getHAPIContext(){
		return(this.hapiContext);
	}

	protected Logger getLogger(){
		return(this.LOG);
	}

	//
	// Business Methods
	//
	
	/**
	 * Returns a {@link Message} from a String
	 * 
	 * @param message
	 * @return
	 */
	public Message getMessage(String message) {
		try {
            PipeParser parser = getHAPIContext().getPipeParser();
            parser.getParserConfiguration().setValidating(false);

            ModelClassFactory cmf = new DefaultModelClassFactory();
			getHAPIContext().setModelClassFactory(cmf);

            Message inputMessage = parser.parse(message);

            return inputMessage;
		} catch(Exception ex){
			getLogger().warn(".getMessage(): Cannot convert String to Message, error->{}", ExceptionUtils.getMessage(ex));
			return(null);
		}
	}
	
	
	public String getMessageCode(Message message) throws Exception {
		return get(message, "MSH-9-1");
	}

	
	/**
	 * Gets the message type.
	 * 
	 * @param message
	 * @return
	 */
	public String getType(Message message) {
		return message.getName();
	}
	
	
	/**
	 * Removes a patient identifier from the PID segment.
	 * 
	 * @param message
	 * @param identifier
	 * @throws Exception
	 */
	public void removePatientIdentifierField(Message message, String identifier) throws Exception  {
		terserBasedUtils.removePatientIdentifierField(message, identifier, "PID");
	}
	
	
	/**
	 * Gets a patient identifier value from the PID segment
	 * 
	 * @param message
	 * @param identifier
	 * @throws Exception
	 */
	public String getPatientIdentifierValue(Message message, String identifier) throws Exception  {
		return terserBasedUtils.getPatientIdentifierValue(message, identifier, "PID");
	}
	
	
	/**
	 * Removes a patient identifier from the PID segment.  The path to the PID segment needs to be supplied eg. PATIENT_RESULT/PATIENT/PID
	 * 
	 * @param message
	 * @param identifier
	 * @throws Exception
	 */
	public void removePatientIdentifierField(Message message, String identifier, String pidSegmentPath) throws Exception  {
		terserBasedUtils.removePatientIdentifierField(message, identifier, pidSegmentPath);
	}
	
	
	/**
	 * Gets a patient identifier from the PID segment. The path to the PID segment needs to be supplied eg. PATIENT_RESULT/PATIENT/PID
	 * 
	 * @param message
	 * @param identifier
	 * @throws Exception
	 */
	public String getPatientIdentifierValue(Message message, String identifier, String pidSegmentPath) throws Exception  {
		return terserBasedUtils.getPatientIdentifierValue(message, identifier, pidSegmentPath);
	}
	
	
	/**
	 * Returns a list of patient identifiers in the PID segment.
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public List<String> getPatientIdentifierCodes(Message message) throws Exception {
		return terserBasedUtils.getPatientIdentifierCodes(message, "PID");
	}

	
	/**
	 * Removes patient identifiers which do not match the identifier to keep.
	 * 
	 * @param message
	 * @param identifier
	 * @throws Exception
	 */
	public void removeOtherPatientIdentifierFields(Message message, String identifierToKeep) throws Exception  {
		terserBasedUtils.removeOtherPatientIdentifierFields(message, identifierToKeep, "PID");
	}

	
	/**
	 * Removes patient identifiers which do not match the identifier to keep.  The path to the PID segment needs to be supplied eg. PATIENT_RESULT/PATIENT/PID
	 * 
	 * @param message
	 * @param identifier
	 * @throws Exception
	 */
	public void removeOtherPatientIdentifierFields(Message message, String identifierToKeep, String pidSegmentPath) throws Exception  {
		terserBasedUtils.removeOtherPatientIdentifierFields(message, identifierToKeep, pidSegmentPath);
	}

	
	/**
	 * Returns a list of patient identifiers in the PID segment.  The path to the PID segment needs to be supplied eg. PATIENT_RESULT/PATIENT/PID
	 * 
	 * @param message
	 * @param pidSegmentPath
	 * @return
	 * @throws Exception
	 */
	public List<String> getPatientIdentifierCodes(Message message, String pidSegmentPath) throws Exception {
		return terserBasedUtils.getPatientIdentifierCodes(message, pidSegmentPath);
	}

	
	/**
	 * Removes the patient identifier type code but leave everything else in the identifier field. The path to the PID segment needs to be supplied eg. PATIENT_RESULT/PATIENT/PID
	 * 
	 * @param message
	 * @param identifier
	 * @throws Exception
	 */
	public void removePatientIdentifierTypeCode(Message message, String identifier, String pidSegmentPath) throws Exception  {
		terserBasedUtils.removePatientIdentifierTypeCode(message, identifier, pidSegmentPath);
	}
	
	/**
	 * Is the message of the supplied type?
	 * 
	 * @param message
	 * @param messageType
	 * @return
	 * @throws Exception
	 */
	public boolean isType(Message message, String messageType) throws Exception {
		return terserBasedUtils.isType(message, messageType);
	}

	
	/**
	 * Set the target field to the supplied value.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param value
	 * @throws HL7Exception
	 */
	public void set(Message message, String targetPathSpec, String value) throws Exception {	
		terserBasedUtils.set(message, targetPathSpec, value);
	}
	
	
	/**
	 * Copies the content of one field to another.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param sourcePathSpec
	 * @throws HL7Exception
	 */
	public void copy(Message message, String targetPathSpec, String sourcePathSpec, boolean copyIfSourceIsBlank, boolean copyIfTargetIsBlank) throws Exception {	
		terserBasedUtils.copy(message, targetPathSpec, sourcePathSpec, copyIfSourceIsBlank, copyIfTargetIsBlank);
	}

	
	/**
	 * Copies the content of one field to another.
	 * 
	 * @param message
	 * @param sourcePathSpec
	 * @param targetPathSpec
	 * @throws Exception
	 */
	public void copy(Message message, String targetPathSpec, String sourcePathSpec) throws Exception {	
		terserBasedUtils.copy(message, targetPathSpec, sourcePathSpec, true, true);
	}

	
	/**
	 * Copies the content from one field to another.  If the source field is null then the default source path is used.
	 * 
	 * @param message
	 * @param sourcePathSpec
	 * @param targetPathSpec
	 * @param defaultIfSourceIsNull
	 */
	public void copy(Message message, String targetPathSpec, String sourcePathSpec, String defaultSourcepathSpec) throws Exception {
		terserBasedUtils.copy(message, targetPathSpec, sourcePathSpec, defaultSourcepathSpec);
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
		terserBasedUtils.copySubstringBefore(message, targetPathSpec, sourcePathSpec, seperator);			
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
		terserBasedUtils.copySubstringAfter(message, targetPathSpec, sourcePathSpec, seperator);
	}

	
	/**
	 * Concatenates the content of the source fields with the specified seperator.
	 * 
	 * @param message
	 * @param targetpathSpec
	 * @param seperator
	 * @param sourcePathSpecs
	 */
	public void concatenate(Message message, String targetPathSpec, String seperator, String ... sourcePathSpecs) throws Exception {
		terserBasedUtils.concatenate(message, targetPathSpec, seperator, sourcePathSpecs);
	}
	
	
	/**
	 * 
	 * Concatenates the content of the source fields without a seperator.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param sourcePathSpecs
	 * @throws Exception
	 */
	public void concatenate(Message message, String targetPathSpec, String ... sourcePathSpecs) throws Exception {
		terserBasedUtils.concatenate(message, targetPathSpec, "", sourcePathSpecs);
	}

	
	/**
	 * Appends the supplied text to the value at the targetPathSpec.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param textToAppend
	 */
	public void append(Message message, String targetPathSpec, String textToAppend) throws Exception {
		terserBasedUtils.append(message, targetPathSpec, textToAppend);
	}

	
	/**
	 * Prepends the supplied text to the value at the targetPathSpec.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param textToPrepend
	 */
	public void prepend(Message message, String targetPathSpec, String textToPrepend) throws Exception {
		terserBasedUtils.prepend(message, targetPathSpec, textToPrepend);		
	}

	
	/**
	 * Uses a lookup table to change a fields value.
	 * 
	 * @param targetPathSpec
	 * @param lookupTable
	 * @throws HL7Exception
	 */
	public void lookup(Message message, String targetPathSpec, String lookupTableClassName) throws Exception {	
		terserBasedUtils.lookup(message, targetPathSpec, lookupTableClassName);
	}

	
	/**
	 * Calls a Java class to set the target path value.
	 * 
	 * @param targetPathSpec
	 * @param transformationClass
	 */
	public void updateFieldFromCode(Message message, String targetPathSpec, String fieldTransformationClassName) throws Exception {
		terserBasedUtils.updateFieldFromCode(message, targetPathSpec, fieldTransformationClassName);
	}

	
	/**
	 * Calls a Java class to set the target path value.
	 * 
	 * @param targetPathSpec
	 * @param transformationClass
	 */
	public void updateMessageFromCode(Message message, String transformationClassName) throws Exception {
		terserBasedUtils.updateMessageFromCode(message, transformationClassName);
	}

	
	/**
	 * Clear a single field value.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @throws HL7Exception
	 */
	public void clear(Message message, String targetPathSpec) throws Exception {
		terserBasedUtils.clear(message, targetPathSpec);
	}
	
	
	/**
	 * Clears a subfield from a field in every instance of a segment.
	 * 
	 * @param message
	 * @param segmentName
	 * @param fieldIndex
	 * @param subFieldIndex
	 */
	public void clear(Message message, String segmentName, int fieldIndex, int subFieldIndex) throws Exception {
		stringBasedUtils.clear(message, segmentName, fieldIndex, subFieldIndex);
	}
	
	
	/**
	 * Clears a field in every instance of a segment.
	 * 
	 * @param message
	 * @param segmentName
	 * @param fieldIndex
	 * @param subFieldIndex
	 */
	public void clear(Message message, String segmentName, int fieldIndex) throws Exception {
		stringBasedUtils.clear(message, segmentName, fieldIndex);
	}
	
	
	/**
	 * Clears multiple fields in a single command.
	 * 
	 * @param message
	 * @param targetPathSpecs
	 * @throws Exception
	 */
	public void clear(Message message, String ... targetPathSpecs) throws Exception {
		
		for (String targetPathSpec : targetPathSpecs) {
			terserBasedUtils.clear(message, targetPathSpec);
		}
	}

	
	/**
	 * Returns the message row indexes of the supplied segment.
	 * 
	 * @param message
	 * @param segmentName
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getSegmentIndexes(Message message, String segmentName) throws Exception {
		return stringBasedUtils.getSegmentIndexes(message, segmentName);
	}
	
	
	/**
	 * Returns the index of a matching segment starting from the supplied starting from index.
	 * 
	 * @param message
	 * @param segmentName
	 * @param startingFrom
	 * @return
	 * @throws Exception
	 */
	public Integer getNextIndex(Message message, String segmentName, int startFromIndex) throws Exception {
		String[] messageRows = message.toString().split("\r");

		for (int i = startFromIndex; i < messageRows.length; i++) {
			if (messageRows[i].startsWith(segmentName + "|")) {
				return i;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Returns all the indexes of a matching segment startinf from the supplied start from index,
	 * 
	 * @param message
	 * @param segmentName
	 * @param startFromIndex
	 * @return
	 * @throws Exception
	 */
	public  List<Integer> getSegmentIndexes(Message message, String segmentName, int startFromIndex) throws Exception {
		List<Integer> segmentIndexes = new ArrayList<>();
		
		String[] messageRows = message.toString().split("\r");

		for (int i = startFromIndex; i < messageRows.length; i++) {
			if (messageRows[i].startsWith(segmentName + "|")) {
				segmentIndexes.add(i);
			}
		}
		
		return segmentIndexes;
	}	

	
	/**
	 * Returns a count of the number of segments matching the supplied segment name.
	 * 
	 * @param message
	 * @param segmentName
	 * @return
	 */
	public int getSegmentCount(Message message, String segmentName) throws Exception {
		return stringBasedUtils.getSegmentCount(message, segmentName);
	}

	
	/**
	 * Deletes a segment from a HL7 messages at the supplied row index.
	 * 
	 * @param message
	 * @param rowIndex
	 * @throws Exception
	 */
	public void deleteSegment(Message message, int rowIndex) throws Exception {
		stringBasedUtils.deleteSegment(message, rowIndex);
	}

	
	/**
	 * Deletes all segments from a HL7 messages which match the segment name.
	 * 
	 * @param message
	 * @param rowIndex
	 * @throws Exception
	 */
	public void deleteAllSegments(Message message, String segmentName) throws Exception {
		stringBasedUtils.deleteAllSegments(message, segmentName);
	}
	
	
	/**
	 * Deletes an occurence of a segment.
	 * 
	 * @param message
	 * @param segmentName
	 * @param occurence
	 */
	public void deleteSegment(Message message, String segmentName, int occurence) throws Exception {
		stringBasedUtils.deleteSegment(message, segmentName, occurence);
	}

	
	/**
	 * Deletes all segments which contains the supplied field value.
	 * 
	 * @param message
	 * @param segmentName
	 * @param fieldIndex
	 * @throws Exception
	 */
	public void deleteAllSegmentsMatchingFieldValue(Message message, String segmentName, int fieldIndex, String value) throws Exception {
		stringBasedUtils.deleteAllSegmentsMatchingFieldValue(message, segmentName, fieldIndex, value);
	}

	
	/**
	 * Deletes a single segment where the supplied value is part of (contains) the field value.
	 * 
	 * @param message
	 * @param segmentName
	 * @param fieldIndex
	 * @throws Exception
	 */
	public void deleteAllSegmentsContainingFieldValue(Message message, String segmentName, int fieldIndex, String value) throws Exception {
		stringBasedUtils.deleteAllSegmentsContainingFieldValue(message, segmentName, fieldIndex, value);
	}

	
	/**
	 * Does this message contain a segment matching the supplied field value.
	 * 
	 * @param message
	 * @param segmentName
	 * @param fieldIndex
	 * @throws Exception
	 */
	public boolean doesFieldMatchValue(Message message, String segmentName, int fieldIndex, String value) throws Exception {
		return stringBasedUtils.doesFieldMatchValue(message, segmentName, fieldIndex, value);
	}

	
	/**
	 * Does this message contain a segment matching the supplied field value.
	 * 
	 * @param message
	 * @param segmentName
	 * @param fieldIndex
	 * @throws Exception
	 */
	public boolean doesFieldContainValue(Message message, String segmentName, int fieldIndex, String value) throws Exception {
		return stringBasedUtils.doesFieldContainValue(message, segmentName, fieldIndex, value);
	}

	
	/**
	 * Gets a field value from a segment.  This does not use the HL7 terser.
	 * 
	 * @param message
	 * @param rowIndex
	 * @param fieldIndex
	 * @return
	 */
	public String getField(Message message, int rowIndex, int fieldIndex) {
		return stringBasedUtils.getField(message, rowIndex, fieldIndex);
	}

	
	/**
	 * Returns a field from a segment.  This does not use the HL7 terser.
	 * 
	 * @param segment
	 * @param fieldIndex
	 * @return
	 */
	public String getField(String segment, int fieldIndex) {
		return stringBasedUtils.getField(segment, fieldIndex);
	}

	
	/**
	 * Gets a repetition of a field from a segment.
	 * 
	 * @param message
	 * @param rowIndex
	 * @param fieldIndex
	 * @param repetition
	 * @return
	 */
	public String getFieldRepetition(String segment, int fieldIndex, int repetition) throws Exception {
		return stringBasedUtils.getFieldRepetition(segment, fieldIndex, repetition);
	}

	
	/**
	 * Gets a repetition of a field.
	 * 
	 * @param message
	 * @param rowIndex
	 * @param fieldIndex
	 * @param repetition
	 * @return
	 */
	public String getFieldRepetition(Message message, int rowIndex, int fieldIndex, int repetition) throws Exception {
		return stringBasedUtils.getFieldRepetition(message, rowIndex, fieldIndex, repetition);
	}

	
	/**
	 * Returns a subfield.
	 * 
	 * @param segment
	 * @param fieldIndex
	 * @param subfield
	 */
	public String getSubfield(String field, int subFieldIndex) {
		return stringBasedUtils.getSubfield(field, subFieldIndex);
	}

	
	/**
	 * Returns a subfield.
	 * 
	 * @param segment
	 * @param fieldIndex
	 * @param subfield
	 */
	public String getSubfield(String segment, int fieldIndex, int subFieldIndex) {
		return stringBasedUtils.getSubfield(segment, fieldIndex, subFieldIndex);
	}
	
	
	/**
	 * Returns a subfield.
	 * 
	 * @param message
	 * @param segmentIndex
	 * @param fieldIndex
	 * @param subFieldIndex
	 * @return
	 */
	public String getSubfield(String message, int segmentIndex, int fieldIndex, int subFieldIndex) {
		return stringBasedUtils.getSubfield(message, segmentIndex, fieldIndex, subFieldIndex);
	}	
	
	
	/**
	 * Set a field value from a string with variables.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param seperator
	 * @param values
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
		terserBasedUtils.removeSegment(message, sourcePathSpec);
	}

	
	/**
	 * Removes all segments matching the segment name no matter where they appear in the message.
	 * 
	 * @param message
	 * @param segmentName
	 * @throws HL7Exception
	 */
	public void removeAllSegments(Message message, String segmentName) throws Exception {	
		terserBasedUtils.removeAllSegments(message, segmentName);
	}
	
	
	/**
	 * Sets the segments to keep. 
	 * 
	 * @param message
	 * @param requiredSegments
	 */
	public void setSegmentsToKeep(Message message, String ... setSegmentsToKeep) throws Exception {	
		terserBasedUtils.setSegmentsToKeep(message, setSegmentsToKeep);
	}

	
	/**
	 * Sets the segments to keep.  The segments to keep are a comma delimited list.
	 * 
	 * @param message
	 * @param setSegmentsToKeep
	 * @throws Exception
	 */
	public void setSegmentsToKeep(Message message, String setSegmentsToKeep) throws Exception {		
		setSegmentsToKeep(message, setSegmentsToKeep.split(","));
	}

	/**
	 * Returns a list of all matching segments.
	 * 
	 * @param message
	 * @param segmentName
	 * @return
	 * @throws Exception
	 */
	public List<Segment>getAllSegments(Message message, String segmentName) throws Exception {
		return terserBasedUtils.getAllSegments(message, segmentName);
	}

	
	/**
	 * Check if a segment exists.
	 * 
	 * @param message
	 * @param segment
	 * @return
	 */
	public boolean doesSegmentExist(Message message, String segment) throws Exception {
		return terserBasedUtils.doesSegmentExist(message, segment);
	}

	
	/**
	 * Executes an action for each segment which matches the segment name.
	 * 
	 * @param segment
	 * @param action
	 */
	public void forEachSegment(Message message, String segmentName, String actionClassName) throws Exception {
		terserBasedUtils.forEachSegment(message, segmentName, actionClassName);
	}

	
	/**
	 * Executes an action for a single segment.
	 * 
	 * @param segment
	 * @param action
	 */
	public void segmentAction(Message message, String sourcePathSpec, String actionClassName) throws Exception {
		terserBasedUtils.segmentAction(message, sourcePathSpec, actionClassName);
	}

	
	/**
	 * Appends a non standard segment at the end of the message.
	 * 
	 * @param semgmentName
	 */
	public String appendNonStandardSegment(Message message, String newSegmentName) throws Exception {	
		return stringBasedUtils.appendNonStandardSegment(message, newSegmentName);
	}
	
	
	/**
	 * Inserts a non standard segment at the specified index.
	 * 
	 * @param segmentName
	 * @param index
	 */
	public String insertNonStandardSegment(Message message, String newSegmentName, int index) throws Exception {	
		return stringBasedUtils.insertNonStandardSegment(message, newSegmentName, index);
	}

	
	/**
	 * Inserts a non standard segment after the the supplied afterSegmentName (1st occurence).
	 * 
	 * @param segmentName
	 * @param afterSegmentName
	 */
	public String insertNonStandardSegmentAfter(Message message, String newSegmentName, String afterSegmentName) throws Exception {
		return stringBasedUtils.insertNonStandardSegmentAfter(message, newSegmentName, afterSegmentName);
	}

	
	/**
	 * Inserts a non standard segment before the the supplied beforeSegmentName (1st occurence)
	 * 
	 * @param segmentName
	 * @param afterSegmentName
	 */
	public String insertNonStandardSegmentBefore(Message message, String newSegmentName, String beforeSegmentName) throws Exception {
		return stringBasedUtils.insertNonStandardSegmentBefore(message, newSegmentName, beforeSegmentName);
	}	

	
	/**
	 * Insert a non standard segment after every afterSegmentName.
	 * 
	 * @param segmentName
	 * @param afterSegmentName
	 */
	public List<String> insertNonStandardSegmentAfterEvery(Message message, String newSegmentName, String afterSegmentName) throws Exception{	
		return stringBasedUtils.insertNonStandardSegmentAfterEvery(message, newSegmentName, afterSegmentName);
	}

	
	/**
	 * Gets a segment at the specified index.
	 * 
	 * @param message
	 * @param segmentIndex
	 * @return
	 * @throws Exception
	 */
	public String getSegment(Message message, int segmentIndex) throws Exception {
		return stringBasedUtils.getSegment(message, segmentIndex);
	}

	
	/**
	 * Returns the index of a matching segment.
	 * 
	 * @param message
	 * @param segmentName
	 * @param occurence
	 * @return
	 * @throws Exception
	 */
	public Integer getSegmentIndex(Message message, String segmentName, int occurence) throws Exception {
		return stringBasedUtils.getSegmentIndex(message, segmentName, occurence);
	}
	
	
	/**
	 * Returns the message row index of the first occurence of the supplied segment name.
	 * 
	 * @param message
	 * @param segmentName
	 * @return
	 * @throws Exception
	 */
	public Integer getFirstSegmentIndex(Message message, String segmentName) throws Exception {
		return getSegmentIndex(message, segmentName, 0);
	}

	/**
	 * Returns a segment content as a string.
	 * 
	 * @param message
	 * @param segmentName
	 * @param occurence
	 * @return
	 * @throws Exception
	 */
	public String getSegment(Message message, String segmentName, int occurence) throws Exception {
		Integer index = getSegmentIndex(message, segmentName, occurence);
		
		if (index == null) {
			return null;
		}
		
		return getSegment(message, index);
	}
	
	
	/**
	 * Copies a value from one field to another and replace the params.
	 * 
	 * @param message
	 * @param targetPathSpec
	 * @param text
	 * @param sourcePathSpecs
	 * @throws Exception
	 */
	public void copyReplaceParam(Message message, String targetPathSpec, String sourcePathSpec, String ... sourcePathSpecs) throws Exception {
		terserBasedUtils.copyReplaceParam(message, targetPathSpec, sourcePathSpec, sourcePathSpecs);
	}
	
	
	


	
	/**
	 * Copies the content of one segment to another.
	 * 
	 * @param message
	 * @param sourceIndex
	 * @param targetIndex
	 */
	public void copySegment(Message message, int sourceIndex, int targetIndex) throws Exception {
		stringBasedUtils.copySegment(message, sourceIndex, targetIndex);
	}

	
	/**
	 * Changes the message version number.
	 * 
	 * @param message
	 * @param newVersion
	 * @throws Exception
	 */
	public void changeMessageVersion(Message message, String newVersion) throws Exception {
		terserBasedUtils.set(message, "MSH-12", newVersion);
	}

	
	/**
	 * Returns the number of repetitions of a field within a segment.
	 * 
	 * @param message
	 * @param segmentPathSpec
	 * @param fieldIndex
	 * @return
	 * @throws Exception
	 */
	public int getNumberOfRepetitions(Message message, String segmentPathSpec, int fieldIndex) throws Exception {
		return terserBasedUtils.getNumberOfRepetitions(message, segmentPathSpec, fieldIndex);
	}
}
