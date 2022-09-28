package net.fhirfactory.pegacorn.internals.fhir.r4.resources.media.factories;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.hl7.fhir.r4.model.Attachment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MediaEncryptionExtensionFactoryTest {

	@Test
	void testInjectingSecretKey() throws MediaEncryptionExtensionException, NoSuchAlgorithmException {
		MediaEncryptionExtensionFactory meef = new MediaEncryptionExtensionFactory();
		Attachment a = new Attachment();

		SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
		Assertions.assertFalse(a.hasExtension(meef.getMediaEncryptionTypeMeaning()));
		meef.injectSecretKey(a, secretKey);
		Assertions.assertTrue(a.hasExtension(meef.getMediaEncryptionTypeMeaning()));
		SecretKey returnedKey = meef.extractSecretKey(a);
		Assertions.assertEquals(secretKey, returnedKey);
	}

}
