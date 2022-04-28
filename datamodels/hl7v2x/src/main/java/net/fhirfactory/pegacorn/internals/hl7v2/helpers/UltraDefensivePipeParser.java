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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UltraDefensivePipeParser {
    private static final Logger LOG = LoggerFactory.getLogger(UltraDefensivePipeParser.class);

    private boolean initialised;

    //
    // Constructor(s)
    //

    public UltraDefensivePipeParser(){
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

    public String extractSegment(String message, HL7v2SegmentTypeEnum segmentType){

        if(StringUtils.isEmpty(message)){
            return(null);
        }
        List<String> segmentList = getSegmentList(message);
        String segment = getSegment(segmentList, segmentType.getKey(),1);
        return(segment);
    }



    public List<String> extractMetadataFromHL7v2xMessage(String messageString){
        if(messageString == null){
            List<String> errorPayload = new ArrayList<>();
            errorPayload.add("*empty messageString*");
            return(errorPayload);
        }
        List<String> segmentList = getSegmentList(messageString);
        String pid = null;
        String msh = null;
        if(segmentList != null){
            pid = getPatientIdentitySegment(segmentList);
            msh = getMessageHeaderSegment(segmentList);
        }
        List<String> metadata = new ArrayList<>();
        if(msh == null){
            metadata.add("*malformed messageString (cannot resolve MSH)*");
            return(metadata);
        }
        metadata.add(msh);
        if(pid == null){
            pid = "No PID Segment";
        }
        metadata.add(pid);
        return(metadata);
    }

    public String getSegment(List<String> segmentList, String segmentName, int repetitionNumber){
        if(segmentList == null){
            return(null);
        }
        int repetitionCounter = 0;
        for(String currentSegment: segmentList){
            if(currentSegment.startsWith(segmentName)){
                repetitionCounter += 1;
                if(repetitionNumber == repetitionCounter){
                    return(currentSegment);
                }
            }
        }
        return(null);
    }

    private String getMessageHeaderSegment(List<String> segmentList){
        if(segmentList == null){
            return(null);
        }
        for(String currentSegment: segmentList){
            if(currentSegment.length() > 3) {
                String currentSegmentStart = currentSegment.substring(0, 4);
                if (currentSegmentStart.contains("MSH")) {
                    return (currentSegment);
                }
            }
        }
        return(null);
    }

    private String getPatientIdentitySegment(List<String> segmentList){
        if(segmentList == null){
            return(null);
        }
        for(String currentSegment: segmentList){
            if(currentSegment.startsWith("PID")){
                return(currentSegment);
            }
        }
        return(null);
    }

    public List<String> getSegmentList(String message){
        getLogger().debug(".getSegmentList(): Entry, message->{}", message);
        if(StringUtils.isEmpty(message)){
            getLogger().debug(".getSegmentList(): Exit, message is empty!");
            return(new ArrayList<>());
        }
        String[] segmentArray = message.lines().toArray(String[]::new);
        List<String> segmentList = new ArrayList<>();
        int segmentListSize = segmentArray.length;
        for(int counter = 0; counter < segmentListSize; counter += 1){
            String currentSegment = segmentArray[counter];
            getLogger().debug("Segment->{}", currentSegment);
            segmentList.add(currentSegment);
        }
        getLogger().debug(".getSegmentList(): Exit, segmentList->{}", segmentList);
        return(segmentList);
    }

    public List<String> getGeneralHeaderDetail(UoWPayload payload){
        List<String> headerInfo = new ArrayList<>();
        if(payload.getPayloadManifest().hasContainerDescriptor()){
            StringBuilder containerBuilder = new StringBuilder();
            String containerMessageDefiner = payload.getPayloadManifest().getContainerDescriptor().getDataParcelDefiner();
            String containerMessageCategory= payload.getPayloadManifest().getContainerDescriptor().getDataParcelCategory();
            String containerMessageSubcategory = payload.getPayloadManifest().getContainerDescriptor().getDataParcelSubCategory();
            String containerMessageResource= payload.getPayloadManifest().getContainerDescriptor().getDataParcelResource();
            String containerMessageVersion = payload.getPayloadManifest().getContainerDescriptor().getVersion();
            containerBuilder.append("Container Payload: "+containerMessageDefiner+"::"+containerMessageCategory+"::"+containerMessageSubcategory+"::"+ containerMessageResource+"("+containerMessageVersion+")");
            headerInfo.add(containerBuilder.toString());
        }
        StringBuilder contentBuilder = new StringBuilder();
        String contentDescriptor = payload.getPayloadManifest().getContentDescriptor().getDataParcelDefiner();
        String contentMessageCategory= payload.getPayloadManifest().getContentDescriptor().getDataParcelCategory();
        String contentMessageSubcategory = payload.getPayloadManifest().getContentDescriptor().getDataParcelSubCategory();
        String contentMessageResource= payload.getPayloadManifest().getContentDescriptor().getDataParcelResource();
        String contentMessageVersion = payload.getPayloadManifest().getContentDescriptor().getVersion();
        contentBuilder.append("Content Payload: "+contentDescriptor+"::"+contentMessageCategory+"::"+contentMessageSubcategory+"::"+ contentMessageResource+"("+contentMessageVersion+")");
        headerInfo.add(contentBuilder.toString());
        return(headerInfo);
    }
}
