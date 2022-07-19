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

import net.fhirfactory.pegacorn.internals.hl7v2.triggerevents.valuesets.HL7v2SegmentTypeEnum;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;

import org.hl7.fhir.r4.model.Media;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MediaExtractor extends UltraDefensivePipeParser {
    private static final Logger LOG = LoggerFactory.getLogger(MediaExtractor.class);

    private boolean initialised;

    //
    // Constructor(s)
    //

    public MediaExtractor(){
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
        List<String> segmentList = getSegmentList(message);
        String segment = getSegment(segmentList, HL7v2SegmentTypeEnum.OBX.getKey(),1);
        return(segment);
    }

    public String replaceOBXSegment(String message, String replacement) {
    	//TODO KS transformation here
    	return (message);
    }

	public Media populateMedia(String obxSegment) {
		Media media = new Media();
		//TODO KS populate me!
		return media;
	}

}
