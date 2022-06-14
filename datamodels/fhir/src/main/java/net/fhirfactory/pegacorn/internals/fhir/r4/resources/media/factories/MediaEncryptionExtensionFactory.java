package net.fhirfactory.pegacorn.internals.fhir.r4.resources.media.factories;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;

import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Base64BinaryType;
import org.hl7.fhir.r4.model.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MediaEncryptionExtensionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MediaEncryptionExtensionFactory.class);
    
    private static final String MEDIA_ENCRYPTION_TYPE_MEANING = "/Media/media-encryption-extension";

	private static final String ALGORITHM = "AES";
    
    public static String getEncryptionAlgorithm() {
    	return ALGORITHM;
    }
    
    public String getMediaEncryptionTypeMeaning(){
        String codeSystem = MEDIA_ENCRYPTION_TYPE_MEANING;
        return (codeSystem);
    }
    
    public void injectSecretKey(Attachment attachment, SecretKey secretKey){
    	//TODO KS inject key here
        LOG.debug(".injectSecretKey(): Entry, attachment->{}, secretKey->{}", attachment, secretKey);

        Extension newAttachmentExtension = new Extension();
        newAttachmentExtension.setUrl(getMediaEncryptionTypeMeaning());
        newAttachmentExtension.setValue(new Base64BinaryType(secretKey.getEncoded()));  
        attachment.addExtension(newAttachmentExtension);
        LOG.debug(".injectSecretKey(): Exit, attachment->{}", attachment);
    }

    public SecretKey extractSecretKey(Attachment attachment) {
        if (!attachment.hasExtension(getMediaEncryptionTypeMeaning())) {
            LOG.debug(".extractSecretKey(): target attachment does not contain the secret key extension");
            return(null);
        }
        Extension extractedStatusExtension = attachment.getExtensionByUrl(getMediaEncryptionTypeMeaning());
        if( !(extractedStatusExtension.getValue() instanceof Base64BinaryType)){
            LOG.debug(".extractSecretKey(): target attachment does not contain the appropriate extension value type");
            return(null);
        }
        Base64BinaryType extractedSecretKey = (Base64BinaryType) (extractedStatusExtension.getValue());
        SecretKey secretKey = new SecretKeySpec(extractedSecretKey.getValue(), ALGORITHM);
        LOG.debug(".extractSecretKey(): Exit, secretKey->{}", secretKey);
        return (secretKey);
    }

}
