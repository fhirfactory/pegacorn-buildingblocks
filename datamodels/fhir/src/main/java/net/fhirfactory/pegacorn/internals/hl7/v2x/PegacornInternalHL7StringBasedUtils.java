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
import ca.uhn.hl7v2.model.Message;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities that parse/query a HL7 document as a string and do not uses a HL7
 * library or testers.
 * 
 * @author Brendan Douglas
 *
 */
@ApplicationScoped
public class PegacornInternalHL7StringBasedUtils {
	
	enum ComparisionType {
		MATCHES,
		CONTAINS,
		STARTS_WITH,
		ENDS_WITH;
	}
	
	
	/**
	 * Returns the message row indexes of the supplied segment. This does not use
	 * the terser.
	 * 
	 * @param message
	 * @param segmentName
	 * @return
	 */
	public List<Integer> getSegmentIndexes(Message message, String segmentName) throws Exception {
		List<Integer> segmentIndexes = new ArrayList<>();

		String[] messageRows = message.toString().split("\r");

		for (int i = 0; i < messageRows.length; i++) {
			if (messageRows[i].startsWith(segmentName + "|")) {
				segmentIndexes.add(i);
			}
		}

		return segmentIndexes;
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
		return getSegmentIndex(message, segmentName, 1);
	}
	

	/**
	 * Returns a count of the number of segments matching the supplied segment name.
	 * 
	 * @param message
	 * @param segmentName
	 * @return
	 */
	public int getSegmentCount(Message message, String segmentName) throws Exception {
		int segmentCount = 0;

		String[] messageRows = message.toString().split("\r");

		for (int i = 0; i < messageRows.length; i++) {
			if (messageRows[i].startsWith(segmentName + "|")) {
				segmentCount++;
			}
		}

		return segmentCount;
	}

	
	/**
	 * Returns the index of a matching segment.
	 * 
	 * @param message
	 * @param segmentName
	 * @param occurence - starts at 1
	 * @return
	 * @throws Exception
	 */
	public Integer getSegmentIndex(Message message, String segmentName, int occurence) throws Exception {
		List<Integer>segmentIndexes = getSegmentIndexes(message, segmentName);
		
		if (segmentIndexes.isEmpty()) {
			return null;
		}
		
		if (occurence > segmentIndexes.size()) {
			return null;
		}
		
		return segmentIndexes.get(--occurence);  // The supplied occurence starts at 1 Need to subtract 1 for the array index.
	}

	
	/**
	 * Returns a segment as a string.
	 * 
	 * @param message
	 * @param segmentName
	 * @param occurence - starts at 1
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
	 * Deletes a segment from a HL7 messages at the supplied row index. This deletes
	 * based on the row index in the raw HL7 messages and does not use the HL7
	 * terser.
	 * 
	 * @param message
	 * @param rowIndex
	 * @throws Exception
	 */
	public void deleteSegment(Message message, int rowIndex) throws Exception {
		String[] messageRows = message.toString().split("\r");

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < messageRows.length; i++) {
			if (i != rowIndex) {
				sb.append(messageRows[i]).append("\r");
			}
		}

		message.parse(sb.toString());
	}

	
	/**
	 * Deletes an occurence of a segment.
	 * 
	 * @param message
	 * @param segmentName
	 * @param occurence - starts at 1
	 * @throws HL7Exception
	 */
	public void deleteSegment(Message message, String segmentName, int occurence) throws Exception {
		String[] messageRows = message.toString().split("\r");
		
		StringBuilder sb = new StringBuilder();

		int currentOccurence = 0;
		
		for (String row : messageRows) {
			if (row.startsWith(segmentName + "|")) {
				currentOccurence++;
				
				if (currentOccurence != occurence) {
					sb.append(row).append("\r");
				}
			} else {
				sb.append(row).append("\r");
			}
		}

		message.parse(sb.toString());		
	}

	
	/**
	 * Deletes all segments from a HL7 messages which match the segment name. This
	 * deletes based on the row index in the raw HL7 messages and does not use the
	 * HL7 terser.
	 * 
	 * @param message
	 * @throws Exception
	 */
	public void deleteAllSegments(Message message, String segmentName) throws Exception {
		String[] messageRows = message.toString().split("\r");

		StringBuilder sb = new StringBuilder();

		for (String row : messageRows) {
			if (!row.startsWith(segmentName + "|")) {
				sb.append(row).append("\r");
			}
		}

		message.parse(sb.toString());
	}
	

	/**
	 * Deletes all segments which contains the supplied field value. This does not
	 * use the HL7 terser..
	 * 
	 * @param message
	 * @param segmentName
	 * @param fieldIndex
	 * @throws Exception
	 */
	public void deleteAllSegmentsMatchingFieldValue(Message message, String segmentName, int fieldIndex, String value) throws Exception {
		deleteAllSegments(message, segmentName, fieldIndex, value, ComparisionType.MATCHES);
	}
	
	
	/**
	 * Deletes a single segment where the supplied value is part of (contains) the
	 * field value. This does not use the HL7 terser.
	 * 
	 * @param message
	 * @param segmentName
	 * @param fieldIndex
	 * @throws Exception
	 */
	public void deleteAllSegmentsContainingFieldValue(Message message, String segmentName, int fieldIndex, String value) throws Exception {
		deleteAllSegments(message, segmentName, fieldIndex, value, ComparisionType.CONTAINS);
	}
	
	
	/**
	 * Deletes all segments which match the supplied params using the supp,ied comparision type.
	 * 
	 * @param message
	 * @param segmentName
	 * @param fieldIndex
	 * @throws Exception
	 */
	private void deleteAllSegments(Message message, String segmentName, int fieldIndex, String value, ComparisionType compareType) throws Exception {
		String[] messageRows = message.toString().split("\r");

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < messageRows.length; i++) {
			if (messageRows[i].startsWith(segmentName + "|")) {
				String field = getField(messageRows[i], fieldIndex);
				
				if (field == null) {
					continue;
				}
								
				if (!compare(field, value, compareType)) {
					sb.append(messageRows[i]).append("\r");
				}
			} else {
				sb.append(messageRows[i]).append("\r");
			}
		}

		message.parse(sb.toString());
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
		return compareFieldValue(message, segmentName, fieldIndex, value, ComparisionType.MATCHES);
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
		return compareFieldValue(message, segmentName, fieldIndex, value, ComparisionType.CONTAINS);
	}
	
	
	/**
	 * Compares a message field value against a search value using the supplied comparison type.
	 * 
	 * @param message
	 * @param segmentName
	 * @param fieldIndex
	 * @param value
	 * @param comparisonType
	 * @return
	 * @throws Exception
	 */
	private boolean compareFieldValue(Message message, String segmentName, int fieldIndex, String value, ComparisionType comparisonType) throws Exception {
		String[] messageRows = message.toString().split("\r");

		for (int i = 0; i < messageRows.length; i++) {
			if (messageRows[i].startsWith(segmentName + "|")) {
				String field = getField(messageRows[i], fieldIndex);
				if (field != null && compare(field, value, comparisonType)) {
					return true;
				}
			}
		}

		return false;
	}

	
	/**
	 * Gets a field value from a segment. This does not use the HL7 terser.
	 * 
	 * @param message
	 * @param rowIndex
	 * @param fieldIndex
	 * @return
	 */
	public String getField(Message message, int rowIndex, int fieldIndex) {
		String[] messageRows = message.toString().split("\r");
		
		if (rowIndex >= messageRows.length) {
			return null;
		}

		String requiredSegment = messageRows[rowIndex];

		// Now break up into fields

		String[] segmentFields = splitSegmentIntoFields(requiredSegment);
		
		if (fieldIndex >= segmentFields.length) {
			return null;
		}

		return segmentFields[fieldIndex];
	}

	
	/**
	 * Returns a field (1st repetition) from a segment. This does not use the HL7 terser.
	 * 
	 * @param segment
	 * @param fieldIndex
	 * @return
	 */
	public String getField(String segment, int fieldIndex) {
		String[] segmentFields = splitSegmentIntoFields(segment);

		if (fieldIndex >= segmentFields.length) {
			return null;
		}
		
		return segmentFields[fieldIndex];
	}

	
	/**
	 * Splits a segment into an array of fields.
	 * 
	 * @param segment
	 * @return
	 */
	private String[] splitSegmentIntoFields(String segment) {
		
		// For the MSH segment the MSH-1 field value is the separator character (|) and we split based on this so create an empty field then add the seperator character to the field.
		if (segment.startsWith("MSH")) {
			String[] fields = segment.replace("MSH|", "MSH||").split("\\|");
			fields[1] = "|";
			
			return fields;
		}
		
		return segment.split("\\|");
	}
	
	
	/**
	 * Splits a fields into an array of subfields.
	 * 
	 * @param field
	 * @return
	 */
	private String[] splitFieldIntoSubFields(String field) {
		return field.split("\\^");
	}
	
	
	/**
	 * Returns a segment at the specified index.
	 * 
	 * @param message
	 * @param segmentIndex
	 * @return
	 */
	public String getSegment(Message message, int segmentIndex) {
		String[] messageRows = message.toString().split("\r");
		
		if (segmentIndex >= messageRows.length) {
			return null;
		}

		return messageRows[segmentIndex];
	}

	
	/**
	 * Appends a non standard segments at the end of the message.
	 * 
	 * @param message
	 * @param newSegmentName
	 */
	public String appendNonStandardSegment(Message message, String newSegmentName) throws HL7Exception {
		return message.addNonstandardSegment(newSegmentName);
	}

	
	/**
	 * Inserts a non standard segments at the specified index.
	 * 
	 * @param message
	 * @param newSegmentName
	 * @param index
	 */
	public String insertNonStandardSegment(Message message,String newSegmentName, int index) throws HL7Exception {	
		return message.addNonstandardSegment(newSegmentName, index);
	}

	
	/**
	 * Inserts a non standard segments after the the supplied afterSegmentName (1st occurence).
	 * 
	 * @param message
	 * @param newSegmentName
	 * @param afterSegmentName
	 */
	public String insertNonStandardSegmentAfter(Message message, String newSegmentName, String afterSegmentName) throws Exception {
		Integer index = getFirstSegmentIndex(message, afterSegmentName);
		
		if (index == null) {
			throw new HL7Exception("Segment does not exist: " + afterSegmentName);
		}
		
		return insertNonStandardSegment(message, newSegmentName, ++index);
	}

	
	/**
	 * Inserts a non standard segments before the the supplied afterSegmentName
	 * 
	 * @param message
	 * @param newSegmentName
	 * @param beforeSegmentName
	 */
	public String insertNonStandardSegmentBefore(Message message, String newSegmentName, String beforeSegmentName) throws Exception {
		Integer index = getFirstSegmentIndex(message, beforeSegmentName);
		
		if (index == null) {
			throw new HL7Exception("Segment does not exist: " + index);
		}

		return insertNonStandardSegment(message, newSegmentName, index);
	}	
	
	
	/**
	 * Adds a mew segment after all occurences of an existing segment.
	 * 
	 * @param message
	 * @param newSegmentName
	 * @param afterSegmentName
	 * @return
	 * @throws Exception
	 */
	public List<String> insertNonStandardSegmentAfterEvery(Message message, String newSegmentName, String afterSegmentName) throws Exception {
		int count = getSegmentCount(message, afterSegmentName);
		List<String>segmentNames = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			int segmentIndex = getSegmentIndex(message, afterSegmentName, i);
			segmentNames.add(insertNonStandardSegment(message, newSegmentName, ++segmentIndex));
		}
		
		return segmentNames;
	}

	

	
	
	/**
	 * Copies the content of one segment to another.
	 * 
	 * @param message
	 * @param sourceIndex
	 * @param targetIndex
	 * @throws Exception
	 */
	public void copySegment(Message message, int sourceIndex, int targetIndex) throws Exception {
		String[] messageRows = message.toString().split("\r");
		
		if (sourceIndex >= messageRows.length) {
			return;
		}

		String sourceSegment = messageRows[sourceIndex];
	
		messageRows[targetIndex] = sourceSegment;
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < messageRows.length; i++) {
			sb.append(messageRows[i]).append("\r");
		}
		
		message.parse(sb.toString());
	}

	
	/**
	 * @param field
	 * @param subFieldIndex
	 * @return
	 */
	public String getSubfield(String field, int subFieldIndex) {
		if (field == null) {
			return null;
		}
		
		String[] fields = field.split("\\^");
		
		if (subFieldIndex > fields.length) {
			return null;
		}
		
		return fields[--subFieldIndex];  // The supplied subFieldIndex starts at 1 to be consistent with the HAP HL7 library.  need to subtract 1 for the array index.
	}
	
	
	/**
	 * Gets a sub field.
	 * 
	 * @param segment
	 * @param fieldIndex
	 * @param subFieldIndex
	 * @return
	 */
	public String getSubfield(String segment, int fieldIndex, int subFieldIndex) {
		String field = getField(segment, fieldIndex);
		
		if (field == null) {
			return null;
		}
	
		return getSubfield(field, --subFieldIndex);  // The supplied subFieldIndex starts at 1 to be consistent with the HAP HL7 library.  need to subtract 1 for the array index.
	}

	
	/**
	 * Gets a sub field.
	 * 
	 * @param message
	 * @param segmentIndex
	 * @param fieldIndex
	 * @param subFieldIndex
	 * @return
	 */
	public String getSubfield(String message, int segmentIndex, int fieldIndex, int subFieldIndex) {
		String segment = getSegment(null, segmentIndex);
		
		if (segment == null) {
			return null;
		}
		
		String field = getField(segment, fieldIndex);
		
		if (field == null) {
			return null;
		}
		
		return getSubfield(field, --subFieldIndex);  // The supplied subFieldIndex starts at 1 to be consistent with the HAP HL7 library.  need to subtract 1 for the array index.
	}

	
	/**
	 * Creates a field from an array of subField values.
	 * 
	 * @param subFields
	 * @return
	 */
	private String createFieldFromSubFields(String[] subFields) {
		return String.join("^", subFields);
	}

	
	/**
	 * Creates a segment from an array of field valuies.
	 * 
	 * @param fields
	 * @return
	 */
	private String createSegmentFromFields(String[] fields) {
		return String.join("|", fields);
	}

	
	/**
	 * Clears a field in every instance of a segment.
	 * 
	 * @param message
	 * @param segmentName
	 * @param fieldIndex
	 */
	public void clear(Message message, String segmentName, int fieldIndex) throws Exception {
		String[] messageRows = message.toString().split("\r");

		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < messageRows.length; i++) {
			if (messageRows[i].startsWith(segmentName + "|")) {
				
				String[] segmentFields = splitSegmentIntoFields(messageRows[i]);

				if (fieldIndex >= segmentFields.length) {
					return;
				}
				
				// Just ignore if the field does not exist.
				if (fieldIndex >= segmentFields.length) {
					continue;
				}
				
				segmentFields[fieldIndex] = "";
				sb.append(createSegmentFromFields(segmentFields));
			} else {
				sb.append(messageRows[i]);
			}
			
			if (sb.length() > 0) {
				sb.append("\r");
			}
		}
		
		message.parse(sb.toString());
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
		int index = subFieldIndex - 1; // The supplied subFieldIndex starts at 1 to be consistent with the HAP HL7 library.  need to subtract 1 for the array index.
		
		String[] messageRows = message.toString().split("\r");

		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < messageRows.length; i++) {
			if (messageRows[i].startsWith(segmentName + "|")) {
				
				String[] segmentFields = splitSegmentIntoFields(messageRows[i]);

				if (fieldIndex >= segmentFields.length) {
					return;
				}
				
				String field = segmentFields[fieldIndex];
				
				String[] subFields = splitFieldIntoSubFields(field);
				
				// Just ignore if the subfield not does not exist.
				if (index > subFields.length) {
					continue;
				}
				
				subFields[index] = "";
				
				// Recreate the field from the subfield array
				field = createFieldFromSubFields(subFields);
				
				// Just ignore if the field does not exist.
				if (fieldIndex >= segmentFields.length) {
					continue;
				}
				
				segmentFields[fieldIndex] = field;

				// Recreate the segment from the field array
				sb.append(createSegmentFromFields(segmentFields));
			} else {
				sb.append(messageRows[i]);
			}
			
			if (sb.length() > 0) {
				sb.append("\r");
			}
		}
		
		message.parse(sb.toString());
	}

	
	/**
	 * Returns a repetition of a field from a message.
	 * 
	 * @param message
	 * @param rowIndex
	 * @param fieldIndex
	 * @param repetition
	 * @return
	 */
	public String getFieldRepetition(Message message, int rowIndex, int fieldIndex, int repetition) {
		String segment = getSegment(message, rowIndex);
		
		if (StringUtils.isBlank(segment)) {
			return null;
		}
		
		return getFieldRepetition(segment, fieldIndex, repetition);	
	}

	
	/**
	 * Returns a repetition of a field from a segment string.
	 * 
	 * @param segment
	 * @param fieldIndex
	 * @param repetition - starts at 1
	 * @return
	 */
	public String getFieldRepetition(String segment, int fieldIndex, int repetition) {
		String field = getField(segment, fieldIndex);
		
		String repetitions[] = field.split("\\~");
		
		if (repetition > repetitions.length) {
			return null;
		}
		
		return repetitions[--repetition];
	}

	
	private boolean compare(String messageField, String compareField, ComparisionType comparisionType) {
		if (compareField == null) {
			throw new IllegalArgumentException("The value to compare against the field cannot be null");
		}
		
		if (messageField == null) {
			return false;
		}
		
	
		switch(comparisionType) {
			case MATCHES:
				return messageField.equals(compareField);
				
			case CONTAINS:
				return messageField.contains(compareField);
				
			case STARTS_WITH:
				return messageField.startsWith(compareField);
			
			case ENDS_WITH:
				return messageField.endsWith(compareField);
		}
		
		throw new IllegalArgumentException("Unknown comparison type: " + comparisionType);
	}
}