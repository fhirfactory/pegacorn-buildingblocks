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
package net.fhirfactory.pegacorn.internals.hl7v2.segments.helpers;

import net.fhirfactory.pegacorn.internals.hl7v2.helpers.UltraDefensivePipeParser;
import net.fhirfactory.pegacorn.internals.hl7v2.segments.ZDESegment;
import net.fhirfactory.pegacorn.internals.hl7v2.segments.ZDESegmentSet;
import net.fhirfactory.pegacorn.internals.hl7v2.segments.factories.ZDESegmentFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ZDESegmentHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ZDESegmentHelper.class);

    @Inject
    ZDESegmentFactory zdeSegmentFactory;

    @Inject
    UltraDefensivePipeParser pipeParser;

    //
    // Constructor(s)
    //

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected ZDESegmentFactory getZDESegmentFactory(){
        return(zdeSegmentFactory);
    }

    protected UltraDefensivePipeParser getPipeParser(){
        return(pipeParser);
    }

    //
    // Business Methods
    //

    public String addZDESegmentSet(String originalEvent, ZDESegmentSet zdeSegments){
        getLogger().debug(".addZDESegmentSet(): Entry, originalEvent->{}, zdeSegments->{}", originalEvent, zdeSegments);
        if(StringUtils.isEmpty(originalEvent)){
            getLogger().debug(".addZDESegmentSet(): Exit, originalEvent is empty, returning null");
            return(null);
        }
        if(zdeSegments == null){
            getLogger().debug(".addZDESegmentSet(): Exit, zdeSegment is empty, returning original message");
            return(originalEvent);
        }
        StringBuilder newEventBuilder = new StringBuilder();
        newEventBuilder.append(originalEvent);
        if(!(originalEvent.endsWith("\r"))){
            newEventBuilder.append("\r");
        }
        zdeSegments.compactErrorNotes();
        for(Integer currentKey: zdeSegments.getErrorNotes().keySet()){
            ZDESegment currentSegment = zdeSegments.getErrorNotes().get(currentKey);
            String currentSegmentString = getZDESegmentFactory().newZDESegmentString(currentSegment);
            if(StringUtils.isNotEmpty(currentSegmentString)){
                newEventBuilder.append(currentSegmentString);
            }
        }
        String newEvent = newEventBuilder.toString();
        getLogger().debug(".addZDESegmentSet*(): Exit, newEvent->{}", newEvent);
        return(newEvent);
    }

    public String removeZDESegmentsIfPresent(String originalEvent){
        getLogger().debug(".removeZDESegmentsIfPresent(): Entry, originalEvent->{}", originalEvent);
        if(StringUtils.isEmpty(originalEvent)){
            getLogger().debug(".removeZDESegmentsIfPresent(): Exit, originalEvent is empty, returning null");
            return(null);
        }
        Map<Integer, String> segmentMap = getPipeParser().getOrderedSegmentList(originalEvent);
        if(segmentMap == null) {
            getLogger().debug(".removeZDESegmentIfPresent(): Exit, cannot extract segment list, returning original message");
            return (originalEvent);
        }
        Map<Integer, String> newSegmentMap = new HashMap<>();
        Integer counter = 0;
        for(Integer key: segmentMap.keySet()){
            String currentSegment = segmentMap.get(key);
            Boolean isZDESegment = currentSegment.startsWith("ZDE");
            if(!isZDESegment){
                newSegmentMap.put(counter, currentSegment);
                counter += 1;
            }
        }
        StringBuilder newEventBuilder = new StringBuilder();
        for(Integer key: newSegmentMap.keySet()){
            String currentSegment = newSegmentMap.get(key);
            newEventBuilder.append(currentSegment).append("\r");
        }
        String newEvent = newEventBuilder.toString();
        getLogger().debug(".removeZDESegmentIfPresent(): Exit, newEvent->{}", newEvent);
        return(newEvent);
    }
}
