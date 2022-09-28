package net.fhirfactory.pegacorn.internals.fhir.r4.resources.media.factories;

public class MediaEncryptionExtensionException extends Exception {
    private static final long serialVersionUID = 1L;

    public MediaEncryptionExtensionException(String message) {
        super(message);
    }
    
    public MediaEncryptionExtensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
