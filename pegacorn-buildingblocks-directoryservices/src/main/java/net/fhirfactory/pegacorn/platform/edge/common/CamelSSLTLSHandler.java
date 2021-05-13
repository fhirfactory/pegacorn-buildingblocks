/*
 * Copyright (c) 2021 Jasen Schremmer (ACT Health)
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

package net.fhirfactory.pegacorn.platform.edge.common;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.CamelContext;
import org.apache.camel.support.jsse.CipherSuitesParameters;
import org.apache.camel.support.jsse.KeyManagersParameters;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.apache.camel.support.jsse.SecureSocketProtocolsParameters;
import org.apache.camel.support.jsse.TrustManagersParameters;
import org.apache.commons.lang3.StringUtils;

/**
 * Conditionally add TLS configuration to the camel context, based on if the following config is passed as System properties : 
 * javax.net.ssl.keyStore and javax.net.ssl.keyStorePassword, and optionally javax.net.ssl.trustStore and javax.net.ssl.trustStorePassword
 */

@ApplicationScoped
public class CamelSSLTLSHandler {

    protected String getKeyStorePath() {
        String keyStorePath = System.getProperty("javax.net.ssl.keyStore");
        return keyStorePath;
    }
    
    protected String getKeyStorePassword() {
        String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        return keyStorePassword;
    }
    
    protected String getKeyPassword() {
        return getKeyStorePassword();
    }
    
    protected String getTrustStorePath() {
        String trustStorePath = System.getProperty("javax.net.ssl.trustStore");
        return trustStorePath;
    }
    
    protected String getTrustStorePassword() {
        String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
        return trustStorePassword;
    }
    
    protected SecureSocketProtocolsParameters getSecureSocketProtocolsParameters() {
        SecureSocketProtocolsParameters secureSocketProtocolsParameters = new SecureSocketProtocolsParameters();
        secureSocketProtocolsParameters.getSecureSocketProtocol().add("TLSv1.2");
        secureSocketProtocolsParameters.getSecureSocketProtocol().add("TLSv1.3");
        return secureSocketProtocolsParameters;
    }
    
    protected CipherSuitesParameters getCipherSuitesParameters() {
        CipherSuitesParameters cipherSuitesParameters = new CipherSuitesParameters();
        cipherSuitesParameters.getCipherSuite().add("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");
        cipherSuitesParameters.getCipherSuite().add("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
        return cipherSuitesParameters;
    }
    
    public boolean isSSLTLSEnabled() {
        String keyStorePassword = getKeyStorePassword();
        
        return StringUtils.isNotBlank(keyStorePassword);
    }
    
    public SSLContextParameters getSSLContextParameters() {
        if (! isSSLTLSEnabled()) {
            return null;
        }

        KeyStoreParameters keyStoreParameters = new KeyStoreParameters();
        keyStoreParameters.setResource(getKeyStorePath());
        keyStoreParameters.setPassword(getKeyStorePassword());

        KeyManagersParameters keyManagersParameters = new KeyManagersParameters();
        keyManagersParameters.setKeyStore(keyStoreParameters);
        keyManagersParameters.setKeyPassword(getKeyPassword());
        
        SSLContextParameters sSLContextParameters = new SSLContextParameters();
        sSLContextParameters.setKeyManagers(keyManagersParameters);
        sSLContextParameters.setSecureSocketProtocols(getSecureSocketProtocolsParameters());
        sSLContextParameters.setCipherSuites(getCipherSuitesParameters());

        String trustStorePassword = getTrustStorePassword();            
        if (StringUtils.isNotBlank(trustStorePassword)) {
            KeyStoreParameters trustStoreParameters = new KeyStoreParameters();
            trustStoreParameters.setResource(getTrustStorePath());
            trustStoreParameters.setPassword(trustStorePassword);

            TrustManagersParameters trustManagersParameters = new TrustManagersParameters();
            trustManagersParameters.setKeyStore(trustStoreParameters);
            
            sSLContextParameters.setTrustManagers(trustManagersParameters);
        }

        return sSLContextParameters;
    }
    
    public CamelContext applySSLTLSConfigToCamelContext(CamelContext camelContext) { 
        camelContext.setSSLContextParameters(getSSLContextParameters());
        return camelContext;
    }
}
