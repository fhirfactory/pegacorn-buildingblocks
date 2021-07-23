/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics;

import net.fhirfactory.pegacorn.components.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelTypeDescriptor;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HL7V2XTopicFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HL7V2XTopicFactory.class);

    private static String HL7_MESSAGE_DEFINER = "HL7";
    private static String HL7_MESSAGE_CATEGORY = "Messages (Version 2.x)";

    private static String HL7_MESSAGE_UNKNOWN = "Unknown";

    /**
     *
     * @param triggerName
     * @param version
     * @return
     */
    public DataParcelTypeDescriptor newDataParcelDescriptor(String triggerName, String version){
        LOG.debug(".createTopicToken(): Entry, messageName->{}, version->{}", triggerName, version);
        if(triggerName == null){
            LOG.debug(".createTopicToken(): Exit, messageName is null");
            return(null);
        }
        if(version == null){
            LOG.debug(".createTopicToken(): Exit, version is null");
            return(null);
        }
        DataParcelTypeDescriptor topicToken = newDataParcelDescriptor(triggerName);
        topicToken.setVersion(version);
        LOG.debug(".createTopicToken(): Exit, topicToken->{}", topicToken);
        return(topicToken);
    }

    /**
     *
     * @param eventName
     * @return
     */
    public DataParcelTypeDescriptor newDataParcelDescriptor(String eventName) {
        LOG.debug(".createTopicToken(): Entry, eventName->{}", eventName);
        DataParcelTypeDescriptor topicToken = new DataParcelTypeDescriptor();
        topicToken.setDataParcelDefiner(HL7_MESSAGE_DEFINER);
        topicToken.setDataParcelCategory(HL7_MESSAGE_CATEGORY);
        switch (eventName) {
            case "A01":
            case "A02":
            case "A03":
            case "A04":
            case "A05":
            case "A06":
            case "A07":
            case "A08":
            case "A09":
            case "A10":
            case "A11":
            case "A12":
            case "A13":
            case "A14":
            case "A15":
            case "A16":
            case "A17":
            case "A18":
            case "A19":
            case "A20":
            case "A21":
            case "A22":
            case "A23":
            case "A24":
            case "A25":
            case "A26":
            case "A27":
            case "A28":
            case "A29":
            case "A30":
            case "A31":
            case "A32":
            case "A33":
            case "A34":
            case "A35":
            case "A36":
            case "A37":
            case "A38":
            case "A39":
            case "A40":
            case "A41":
            case "A42":
            case "A43":
            case "A44":
            case "A45":
            case "A46":
            case "A47":
            case "A48":
            case "A49":
            case "A50":
            case "A51":
            case "A52":
            case "A53":
            case "A54":
            case "A55":
            case "A60":
            case "A61":
            case "A62": {
                topicToken.setDataParcelSubCategory("ADT");
                topicToken.setDataParcelResource(eventName);
                LOG.debug(".createTopicToken(): Exit, topicToken->{}", topicToken);
                return (topicToken);
            }
            default:
                topicToken.setDataParcelSubCategory( "Unknown");
                topicToken.setDataParcelResource(eventName);
                LOG.debug(".createTopicToken(): Exit, topicToken->{}", topicToken);
                return (topicToken);
        }
   }

    /**
     *
     * @param eventType
     * @param eventTrigger
     * @param version
     * @return
     */
    public DataParcelTypeDescriptor newDataParcelDescriptor(String eventType, String eventTrigger, String version){
        LOG.debug(".newTopicToken(): Entry, eventType->{}, eventTrigger->{}, version->{}", eventType, eventTrigger, version);
        DataParcelTypeDescriptor dataParcelToken = new DataParcelTypeDescriptor();
        dataParcelToken.setDataParcelDefiner(SerializationUtils.clone(HL7_MESSAGE_DEFINER));
        dataParcelToken.setDataParcelCategory(SerializationUtils.clone(HL7_MESSAGE_CATEGORY));
        dataParcelToken.setDataParcelSubCategory(SerializationUtils.clone(eventType));
        dataParcelToken.setDataParcelResource(SerializationUtils.clone(eventTrigger));
        dataParcelToken.setVersion(version);
        LOG.debug(".newTopicToken(): Exit, dataParcelToken->{}", dataParcelToken);
        return(dataParcelToken);
    }

    public DataParcelTypeDescriptor newBadDataParcelDescriptor(){
        LOG.debug(".newBadDataParcelDescriptor(): Entry");
        DataParcelTypeDescriptor dataParcelToken = new DataParcelTypeDescriptor();
        dataParcelToken.setDataParcelDefiner(SerializationUtils.clone(HL7_MESSAGE_DEFINER));
        dataParcelToken.setDataParcelCategory(SerializationUtils.clone(HL7_MESSAGE_CATEGORY));
        dataParcelToken.setDataParcelSubCategory(SerializationUtils.clone(HL7_MESSAGE_UNKNOWN));
        dataParcelToken.setDataParcelResource(SerializationUtils.clone(HL7_MESSAGE_UNKNOWN));
        dataParcelToken.setVersion("x.x.x");
        LOG.debug(".newBadDataParcelDescriptor(): Exit");
        return(dataParcelToken);
    }

    public DataParcelManifest newBadDataParcelManifest(){
        LOG.debug(".newBadDataParcelManifest(): Entry");
        DataParcelManifest badManifest = new DataParcelManifest();
        badManifest.setContentDescriptor(newBadDataParcelDescriptor());
        return(newBadDataParcelManifest());
    }
}
