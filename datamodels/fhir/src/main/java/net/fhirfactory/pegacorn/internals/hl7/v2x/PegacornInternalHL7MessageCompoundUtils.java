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
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.parser.PipeParser;
import org.apache.commons.lang3.SerializationUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PegacornInternalHL7MessageCompoundUtils {

    @Inject
    private PegacornInternalHL7MessageSimpleUtils messageUtils;

    /**
     * Duplicates a message based on a segment type.  eg. if the supplied segmentType is OBX and the message contains 5 OBX segments then 5 messages are
     * returned with a single OBX segment.
     *
     * @param message
     * @param segmentType
     * @return
     */
    public List<Message> duplicateMessage(Message message, String segmentType) throws Exception {
        List<Message>newMessages = new ArrayList<>();

        // Create an array of messages.  1 message for each matching segment type. eg. if the segment type exists 5 times then create 5 messages.
        try (HapiContext context = new DefaultHapiContext();) {
            PipeParser parser = context.getPipeParser();
            parser.getParserConfiguration().setValidating(false);

            ModelClassFactory cmf = new DefaultModelClassFactory();
            context.setModelClassFactory(cmf);

            // Count the number of segments
            int numberOfMatchingSegments = messageUtils.getSegmentCount(message, segmentType);

            if (numberOfMatchingSegments == 0) {
                throw new HL7Exception("Unable to duplicate the message as the supplied segment does not exist in the message.  Segment: " + segmentType);
            }

            for (int i = 0; i < numberOfMatchingSegments; i++) {
                String clonedMessage = SerializationUtils.clone(message.toString());
                newMessages.add(parser.parse(clonedMessage));
            }
        }

        int occurenceToKeep = 1;

        // Now for each of the new messages remove all except one of the matching segments.
        for (Message newMessage : newMessages) {
            int indexOfSegmentToKeep = messageUtils.getSegmentIndex(newMessage, segmentType, occurenceToKeep);

            // Get all the segments indexes
            List<Integer>allSegmentIndexes = messageUtils.getSegmentIndexes(newMessage, segmentType);

            // Make the first matching segment the same as the segment to keep.  This way we just delete the segments which are not the first.
            messageUtils.copySegment(newMessage, indexOfSegmentToKeep, allSegmentIndexes.get(0));

            for (int i = 1; i < allSegmentIndexes.size(); i++) {
                messageUtils.deleteSegment(newMessage, segmentType, 2);
            }

            occurenceToKeep++;
        }

        return newMessages;
    }
}
