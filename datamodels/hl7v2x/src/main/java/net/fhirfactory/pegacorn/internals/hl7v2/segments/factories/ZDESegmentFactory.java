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
package net.fhirfactory.pegacorn.internals.hl7v2.segments.factories;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.internals.hl7v2.segments.ZDESegment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class ZDESegmentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ZDESegmentFactory.class);

    private DateTimeFormatter timeFormatter;

    //
    // Constructor(s)
    //

    public ZDESegmentFactory(){
        timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.of(PetasosPropertyConstants.DEFAULT_TIMEZONE));

    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

    protected DateTimeFormatter getTimeFormatter(){
        return(timeFormatter);
    }

    //
    // Business Methods
    //

    public String newZDESegmentString(ZDESegment zdeSegment){
        getLogger().debug(".newZFLSegmentString(): Entry, zdeSegment->{}", zdeSegment);
        if(zdeSegment == null){
            return(null);
        }
        StringBuilder zdeStringBuilder = new StringBuilder();
        if(zdeSegment.getSetId() == null){
            zdeSegment.setSetId(0);
        }
        if(StringUtils.isEmpty(zdeSegment.getSource())){
            zdeSegment.setSource("");
        }
        if(StringUtils.isEmpty(zdeSegment.getComment())){
            zdeSegment.setComment("");
        }
        if(StringUtils.isEmpty(zdeSegment.getCommentType())){
            zdeSegment.setCommentType("");
        }
        zdeStringBuilder.append("ZDE").append("|");
        zdeStringBuilder.append(Integer.toString(zdeSegment.getSetId())).append("|");
        zdeStringBuilder.append(zdeSegment.getSource()).append("|");
        zdeStringBuilder.append(zdeSegment.getComment()).append("|");
        zdeStringBuilder.append(zdeSegment.getCommentType()).append("\r");
        String zdeString = zdeStringBuilder.toString();
        getLogger().debug(".newZDESegmentString(): Exit, zdeString->{}", zdeString);
        return(zdeString);
    }
}
