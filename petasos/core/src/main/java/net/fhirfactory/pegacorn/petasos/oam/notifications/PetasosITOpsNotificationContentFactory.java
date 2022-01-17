/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.petasos.oam.notifications;

import net.fhirfactory.pegacorn.core.constants.petasos.PetasosPropertyConstants;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.DataParcelDescriptorKeyEnum;
import net.fhirfactory.pegacorn.core.model.petasos.task.datatypes.fulfillment.valuesets.FulfillmentExecutionStatusEnum;
import net.fhirfactory.pegacorn.core.model.petasos.uow.UoWPayload;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.HL7V2XTopicFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PetasosITOpsNotificationContentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(PetasosITOpsNotificationContentFactory.class);

    private DateTimeFormatter timeFormatter;

    @Inject
    private HL7V2XTopicFactory hl7V2XTopicFactory;

    //
    // Constructor(s)
    //

    public PetasosITOpsNotificationContentFactory(){
        timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS").withZone(ZoneId.of(PetasosPropertyConstants.DEFAULT_TIMEZONE));
    }

    //
    // Getters and Setters
    //

    protected DateTimeFormatter getTimeFormatter(){
        return (timeFormatter);
    }

    protected Logger getLogger(){
        return(LOG);
    }

    //
    // Business Methods
    //

    public String newNotificationContentFromUoWPayload(UoWPayload payload) {
        if (payload == null) {
            return ("");
        }
        if (isHL7V2Payload(payload)) {
            StringBuilder notificationContentBuilder = new StringBuilder();
            notificationContentBuilder.append("Task: Internal Timestamp:" + getTimeFormatter().format(Instant.now()) + "\n");
            if(payload.getPayloadManifest().hasContainerDescriptor()){
                String containerMessageDefiner = payload.getPayloadManifest().getContainerDescriptor().getDataParcelDefiner();
                String containerMessageCategory= payload.getPayloadManifest().getContainerDescriptor().getDataParcelCategory();
                String containerMessageSubcategory = payload.getPayloadManifest().getContainerDescriptor().getDataParcelSubCategory();
                String containerMessageResource= payload.getPayloadManifest().getContainerDescriptor().getDataParcelResource();
                String containerMessageVersion = payload.getPayloadManifest().getContainerDescriptor().getVersion();
                notificationContentBuilder.append("Container Payload: "+containerMessageDefiner+"::"+containerMessageCategory+"::"+containerMessageSubcategory+"::"+ containerMessageResource+"("+containerMessageVersion+") \n");
            }
            String messageType = payload.getPayloadManifest().getContentDescriptor().getDataParcelSubCategory();
            String messageTrigger = payload.getPayloadManifest().getContentDescriptor().getDataParcelResource();
            String messageVersion = payload.getPayloadManifest().getContentDescriptor().getVersion();
            if(payload.getPayloadManifest().hasContainerDescriptor()){
                notificationContentBuilder.append("Content Payload: HL7 v2.x: Trigger:"+messageType+"^"+messageTrigger+"("+messageVersion+")");
            } else {
                notificationContentBuilder.append("Content Payload: HL7 v2.x: Trigger:" + messageType + "^" + messageTrigger + "(" + messageVersion + ") \n");
                notificationContentBuilder.append(makeNotificationContentFromHL7V2Message(payload.getPayload()));
            }
            return (notificationContentBuilder.toString());
        }
        //
        // Default Message
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Task: Internal Timestamp:" + getTimeFormatter().format(Instant.now()) + "\n");
        if(payload.getPayloadManifest().hasContainerDescriptor()){
            if(payload.getPayloadManifest().hasContainerDescriptor()){
                String containerMessageDefiner = payload.getPayloadManifest().getContainerDescriptor().getDataParcelDefiner();
                String containerMessageCategory= payload.getPayloadManifest().getContainerDescriptor().getDataParcelCategory();
                String containerMessageSubcategory = payload.getPayloadManifest().getContainerDescriptor().getDataParcelSubCategory();
                String containerMessageResource= payload.getPayloadManifest().getContainerDescriptor().getDataParcelResource();
                String containerMessageVersion = payload.getPayloadManifest().getContainerDescriptor().getVersion();
                messageBuilder.append("Container Payload: "+containerMessageDefiner+"::"+containerMessageCategory+"::"+containerMessageSubcategory+"::"+ containerMessageResource+"("+containerMessageVersion+") \n");
            }
        }
        String definer = getContentDescriptorElement(payload, DataParcelDescriptorKeyEnum.DATASET_DEFINER);
        String category = getContentDescriptorElement(payload, DataParcelDescriptorKeyEnum.DATASET_CATEGORY);
        String subcategory = getContentDescriptorElement(payload, DataParcelDescriptorKeyEnum.DATASET_SUBCATEGORY);
        String resource = getContentDescriptorElement(payload, DataParcelDescriptorKeyEnum.DATASET_RESOURCE);
        String version = getContentDescriptorElement(payload, DataParcelDescriptorKeyEnum.DATASET_VERSION);
        messageBuilder.append("Content Payload: "+definer+"::"+category+"::"+subcategory+"::"+ resource+"("+version+") \n");
        return(messageBuilder.toString());
    }

    public String newNotificationContentFromUoWPayload(FulfillmentExecutionStatusEnum status, UoWPayload payload){
        if(payload == null){
            return("");
        }
        StringBuilder notificationContentBuilder = new StringBuilder();
        notificationContentBuilder.append("Task: Status("+status.getDisplayName() + "): Internal Timestamp:" + getTimeFormatter().format(Instant.now()) + "\n");
        if(isHL7V2Payload(payload)){
            if(payload.getPayloadManifest().hasContainerDescriptor()){
                String containerMessageDefiner = payload.getPayloadManifest().getContainerDescriptor().getDataParcelDefiner();
                String containerMessageCategory= payload.getPayloadManifest().getContainerDescriptor().getDataParcelCategory();
                String containerMessageSubcategory = payload.getPayloadManifest().getContainerDescriptor().getDataParcelSubCategory();
                String containerMessageResource= payload.getPayloadManifest().getContainerDescriptor().getDataParcelResource();
                String containerMessageVersion = payload.getPayloadManifest().getContainerDescriptor().getVersion();
                notificationContentBuilder.append("Container Payload: "+containerMessageDefiner+"::"+containerMessageCategory+"::"+containerMessageSubcategory+"::"+ containerMessageResource+"("+containerMessageVersion+") \n");
            }
            String messageType = payload.getPayloadManifest().getContentDescriptor().getDataParcelSubCategory();
            String messageTrigger = payload.getPayloadManifest().getContentDescriptor().getDataParcelResource();
            String messageVersion = payload.getPayloadManifest().getContentDescriptor().getVersion();
            if(payload.getPayloadManifest().hasContainerDescriptor()){
                notificationContentBuilder.append("Content Payload: HL7 v2.x: Trigger: "+messageType+"^"+messageTrigger+"("+messageVersion+")");
            } else {
                notificationContentBuilder.append("Content Payload: HL7 v2.x: Trigger: " + messageType + "^" + messageTrigger + "(" + messageVersion + ") \n");
                notificationContentBuilder.append(makeNotificationContentFromHL7V2Message(payload.getPayload()));
            }
            return(notificationContentBuilder.toString());
        }
        //
        // Default Message
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Task: Status("+status.getDisplayName() + "): Internal Timestamp:" + getTimeFormatter().format(Instant.now()) + "\n");
        if(payload.getPayloadManifest().hasContainerDescriptor()){
            if(payload.getPayloadManifest().hasContainerDescriptor()){
                String containerMessageDefiner = payload.getPayloadManifest().getContainerDescriptor().getDataParcelDefiner();
                String containerMessageCategory= payload.getPayloadManifest().getContainerDescriptor().getDataParcelCategory();
                String containerMessageSubcategory = payload.getPayloadManifest().getContainerDescriptor().getDataParcelSubCategory();
                String containerMessageResource= payload.getPayloadManifest().getContainerDescriptor().getDataParcelResource();
                String containerMessageVersion = payload.getPayloadManifest().getContainerDescriptor().getVersion();
                notificationContentBuilder.append("Container Payload: "+containerMessageDefiner+"::"+containerMessageCategory+"::"+containerMessageSubcategory+"::"+ containerMessageResource+"("+containerMessageVersion+") \n");
            }
        }
        String definer = getContentDescriptorElement(payload, DataParcelDescriptorKeyEnum.DATASET_DEFINER);
        String category = getContentDescriptorElement(payload, DataParcelDescriptorKeyEnum.DATASET_CATEGORY);
        String subcategory = getContentDescriptorElement(payload, DataParcelDescriptorKeyEnum.DATASET_SUBCATEGORY);
        String resource = getContentDescriptorElement(payload, DataParcelDescriptorKeyEnum.DATASET_RESOURCE);
        String version = getContentDescriptorElement(payload, DataParcelDescriptorKeyEnum.DATASET_VERSION);
        messageBuilder.append("Content Payload: "+definer+"::"+category+"::"+subcategory+"::"+ resource+"("+version+") \n");
        return(messageBuilder.toString());
    }

    protected String getContentDescriptorElement(UoWPayload payload, DataParcelDescriptorKeyEnum descriptorKey) {
        if(payload == null){
            return(null);
        }
        if(payload.getPayloadManifest() == null){
            return(null);
        }
        if(payload.getPayloadManifest().getContentDescriptor() == null){
            return(null);
        }
        if(descriptorKey == null){
            return(null);
        }
        String descriptorElement = getDescriptorElement(payload.getPayloadManifest().getContentDescriptor(), descriptorKey);
        return(descriptorElement);
    }

    protected String getDescriptorElement(DataParcelTypeDescriptor descriptor, DataParcelDescriptorKeyEnum descriptorKey){

        switch(descriptorKey){
            case DATASET_DEFINER:
                return(descriptor.getDataParcelDefiner());
            case DATASET_CATEGORY:
                return(descriptor.getDataParcelCategory());
            case DATASET_SUBCATEGORY:
                return(descriptor.getDataParcelSubCategory());
            case DATASET_RESOURCE:
                return(descriptor.getDataParcelResource());
            case DATASET_SEGMENT:
                return(descriptor.getDataParcelSegment());
            case DATASET_ATTRIBUTE:
                return(descriptor.getDataParcelAttribute());
            case DATASET_DISCRIMINATOR_TYPE:
                return(descriptor.getDataParcelDiscriminatorType());
            case DATASET_DISCRIMINATOR_VALUE:
                return(descriptor.getDataParcelDiscriminatorValue());
            case DATASET_NORMALISATION_STATUS:
                break;
            case DATASET_VALIDATION_STATUS:
                break;
            case DATASET_TYPE:
                break;
            case DATASET_SOURCE:
                break;
            case DATASET_INTENDED_TARGET:
                break;
            case DATASET_VERSION:
                return(descriptor.getVersion());
        }
        return(null);
    }

    protected boolean isHL7V2Payload(UoWPayload payload){
        if(payload == null){
            return(false);
        }
        if(payload.getPayloadManifest() == null){
            return(false);
        }
        if(payload.getPayloadManifest().getContentDescriptor() == null){
            return(false);
        }
        DataParcelTypeDescriptor payloadDescriptor = payload.getPayloadManifest().getContentDescriptor();
        if(payloadDescriptor.getDataParcelDefiner().contentEquals(hl7V2XTopicFactory.getHl7MessageDefiner())){
            if(payloadDescriptor.getDataParcelCategory().contentEquals(hl7V2XTopicFactory.getHl7MessageCategory())){
                return(true);
            }
        }
        return(false);
    }

    private String makeNotificationContentFromHL7V2Message(String payload){
        if(payload == null){
            return("*empty payload*");
        }
        List<String> segmentList = getSegmentList(payload);
        String pid = null;
        String msh = null;
        if(segmentList != null){
            pid = getPatientIdentitySegment(segmentList);
            msh = getMessageHeaderSegment(segmentList);
        }
        if(pid == null){
            pid = "No PID Segment";
        }
        if(msh == null){
            return("*malformed payload (cannot resolve MSH)*");
        }
        String notificationContent = "--- \n" +
                msh + "\n" +
                pid + "\n" +
                "---";
        return(notificationContent);
    }

    private String getMessageHeaderSegment(List<String> segmentList){
        if(segmentList == null){
            return(null);
        }
        for(String currentSegment: segmentList){
            String currentSegmentStart = currentSegment.substring(0, 6);
            if(currentSegmentStart.contains("MSH")){
                return(currentSegment);
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

    private List<String> getSegmentList(String message){
        if(message == null){
            return(new ArrayList<>());
        }
        if(!message.contains("\r")){
            return(new ArrayList<>());
        }
        String[] segmentArray = message.split("\r");
        List<String> segmentList = new ArrayList<>();
        int segmentListSize = segmentArray.length;
        for(int counter = 0; counter < segmentListSize; counter += 1){
            String currentSegment = segmentArray[counter];
            getLogger().debug("Segment->{}", currentSegment);
            segmentList.add(currentSegment);
        }
        return(segmentList);
    }

}
