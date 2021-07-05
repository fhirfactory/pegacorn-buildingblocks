package net.fhirfactory.pegacorn.internals.fhir.r4.resources.communication.extensions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.pegacorn.components.dataparcel.DataParcelTypeDescriptor;
import org.hl7.fhir.r4.model.Communication;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CommunicationPayloadTypeExtensionEnricher {
    private static final Logger LOG = LoggerFactory.getLogger(CommunicationPayloadTypeExtensionEnricher.class);

    private ObjectMapper jsonMapper;

    public CommunicationPayloadTypeExtensionEnricher(){
        super();
        jsonMapper = new ObjectMapper();
    }

    private static UriType PAYLOAD_TYPE_MEANING = new UriType("http://www.fhirfactory.net/pegacorn/FHIR/R4/Communication/communication_payload_type_extension");

    public boolean hasPayloadTypeExtension(Communication.CommunicationPayloadComponent payload)
    {
        if (payload.hasExtension(this.PAYLOAD_TYPE_MEANING.asStringValue())) {
            return (true);
        }
        return (false);
    }

    public DataParcelTypeDescriptor extractPayloadTypeExtension(Communication.CommunicationPayloadComponent payload)
    {
        LOG.debug(".extractPayloadTypeExtension(): Entry, payload->{}", payload);
        if (!payload.hasExtension(this.PAYLOAD_TYPE_MEANING.asStringValue())) {
            LOG.warn(".extractPayloadTypeExtension(): Communication::payload does not contain the PayloadType extension");
            return(null);
        }
        Extension extractedPayloadTypeExtension = payload.getExtensionByUrl(PAYLOAD_TYPE_MEANING.asStringValue());
        if( !(extractedPayloadTypeExtension.getValue() instanceof StringType)){
            LOG.warn("extractPayloadTypeExtension(): FHIR::Communication.payload contains the PayloadType extension value type, but it is not a String");
            return(null);
        }
        StringType extractedStatusStringType = (StringType) (extractedPayloadTypeExtension.getValue());
        DataParcelTypeDescriptor parcelTypeDescriptor = null;
        try {
            parcelTypeDescriptor = jsonMapper.readValue(extractedStatusStringType.getValue(), DataParcelTypeDescriptor.class);
        } catch (JsonProcessingException e) {
            LOG.warn(".extractPayloadTypeExtension(): Cannot parse payloadtype extension value, error->{}", e.getMessage());
        }
        LOG.debug(".extractPayloadTypeExtension(): Exit, parcelTypeDescriptor->{}", parcelTypeDescriptor);
        return (parcelTypeDescriptor);
    }

    public void injectPayloadTypeExtension(Communication.CommunicationPayloadComponent payload, DataParcelTypeDescriptor descriptor){
        LOG.debug(".injectPayloadTypeExtension(): Entry, payload->{}, descriptor", payload, descriptor);
        if (payload.hasExtension(this.PAYLOAD_TYPE_MEANING.asStringValue())) {
            payload.removeExtension(this.PAYLOAD_TYPE_MEANING.asStringValue());
        }
        String valueAsString = null;
        try {
            valueAsString = jsonMapper.writeValueAsString(descriptor);
        } catch (JsonProcessingException e) {
            LOG.warn(".injectPayloadTypeExtension(): Cannot parse payloadtype value, error->{}", e.getMessage());
        }
        if(valueAsString != null) {
            Extension newPayloadType = new Extension();
            newPayloadType.setUrlElement(PAYLOAD_TYPE_MEANING);
            newPayloadType.setValue(new StringType(valueAsString));
            payload.addExtension(newPayloadType);
            LOG.debug(".injectPayloadTypeExtension(): Exit, Extension added");
        } else {
            LOG.debug(".injectPayloadTypeExtension(): Exit, Extension could not be added");
        }
    }
}
