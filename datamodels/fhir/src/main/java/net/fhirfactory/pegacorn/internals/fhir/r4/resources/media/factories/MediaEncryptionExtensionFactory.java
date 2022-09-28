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
    
    private static final String MEDIA_ENCRYPTION_TYPE_MEANING = "identifier://net.fhirfactory.pegacorn/Media/media-encryption-extension";

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

    public SecretKey extractSecretKey(Attachment attachment) throws MediaEncryptionExtensionException {
        LOG.debug(".extractSecretKey(): Entry, attachment->{}", attachment);
        if (attachment == null) {
            throw new IllegalArgumentException("Attachment cannot be null");
        }
        if (!attachment.hasExtension(getMediaEncryptionTypeMeaning())) {
            throw new MediaEncryptionExtensionException("Attachment " + attachment.getUrl() +
                    " does not contain the secret key extension");
        }
        Extension extractedStatusExtension = attachment.getExtensionByUrl(getMediaEncryptionTypeMeaning());
        if( !(extractedStatusExtension.getValue() instanceof Base64BinaryType)){
            throw new MediaEncryptionExtensionException("Attachment " + attachment.getUrl() +
                    ": expected Base64BinaryType but was " + extractedStatusExtension.getValue().getClass().getCanonicalName());
        }
        Base64BinaryType extractedSecretKey = (Base64BinaryType) (extractedStatusExtension.getValue());
        SecretKey secretKey = new SecretKeySpec(extractedSecretKey.getValue(), ALGORITHM);
        LOG.debug(".extractSecretKey(): Exit, secretKey->{}", secretKey);
        return (secretKey);
    }

}
