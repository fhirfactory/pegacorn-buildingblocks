package net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics;

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import net.fhirfactory.pegacorn.common.model.topicid.TopicToken;
import net.fhirfactory.pegacorn.common.model.topicid.TopicTypeEnum;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HL7V2XTopicIDBuilder {
    public TopicToken createTopicToken(String messageName, String version){
        TopicToken newToken = new TopicToken();
        FDN topicFDN = createTopicFDN(messageName);
        if(topicFDN==null){
            return(null);
        }
        if(version == null){
            return(null);
        }
        newToken.setIdentifier(topicFDN.getToken());
        newToken.setVersion(version);
        return(newToken);
    }

    public FDN createTopicFDN(String eventName) {
        FDN payloadTopicFDN = new FDN();
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_DEFINER.getTopicType(), "HL7"));
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_CATEGORY.getTopicType(), "Events"));
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
                payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "ADT"));
                payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), eventName));
                return (payloadTopicFDN);
            }
            default:
                payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "Unknown"));
                payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), eventName));
                return (payloadTopicFDN);
        }
   }

    public TopicToken createTopicToken(String eventType, String eventTrigger, String version){
        FDN payloadTopicFDN = new FDN();
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_DEFINER.getTopicType(), "HL7"));
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_CATEGORY.getTopicType(), "Events"));
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), eventType));
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), eventTrigger));
        TopicToken newToken = new TopicToken();
        newToken.setIdentifier(payloadTopicFDN.getToken());
        newToken.setVersion(version);
        return(newToken);
    }

    public TopicToken createBadTopicToken(){
        FDN payloadTopicFDN = new FDN();
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_DEFINER.getTopicType(), "HL7"));
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_CATEGORY.getTopicType(), "Events"));
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_SUBCATEGORY.getTopicType(), "unknown"));
        payloadTopicFDN.appendRDN(new RDN(TopicTypeEnum.DATASET_RESOURCE.getTopicType(), "unknown"));
        TopicToken newToken = new TopicToken();
        newToken.setIdentifier(payloadTopicFDN.getToken());
        newToken.setVersion("x.x.x");
        return(newToken);
    }
}
