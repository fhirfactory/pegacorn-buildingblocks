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
package net.fhirfactory.pegacorn.internals.matrix.helpers;

import net.fhirfactory.pegacorn.deployment.properties.codebased.communicate.CommunicateConfigurationInterface;
import net.fhirfactory.pegacorn.deployment.properties.codebased.communicate.SynapseServerConfigurationInterface;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MatrixUserIDFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MatrixUserIDFactory.class);

    @Inject
    private SynapseServerConfigurationInterface synapseServerConfiguration;

    @Inject
    private CommunicateConfigurationInterface communicateConfig;

    public String constructSynapseUserID(String practitionerEmailAddress){
        LOG.debug(".constructSynapseUserID(): Entry, practitionerEmailAddress->{}", practitionerEmailAddress);
        String practitionerEmailAddressUserNamePart = extractEmailAddressUserNamePartFromEmailAddress(practitionerEmailAddress);
        String userID = "@"+practitionerEmailAddressUserNamePart+":"+synapseServerConfiguration.getSynapseServerName();
        LOG.debug(".constructSynapseUserID(): Exit, userID->{}", userID);
        return(userID);
    }

    public String extractEmailAddressUserNamePartFromEmailAddress(String practitionerEmailAddress){
        LOG.debug(".extractEmailAddressUserNamePartFromEmailAddress(): Entry, practitionerEmailAddress->{}", practitionerEmailAddress);
        String practitionerEmailAddressUserNamePart = null;
        if(practitionerEmailAddress.contains("@")){
            String[] tempString = practitionerEmailAddress.split("@");
            practitionerEmailAddressUserNamePart = tempString[0];
        } else {
            practitionerEmailAddressUserNamePart = practitionerEmailAddress;
        }
        LOG.debug(".extractEmailAddressUserNamePartFromEmailAddress(): Exit, practitionerEmailAddressUserNamePart->{}", practitionerEmailAddressUserNamePart);
        return(practitionerEmailAddressUserNamePart);
    }

    public String extractEmailAddressUserNamePartFromSynapseUserID(String synapseUserID){
        LOG.debug(".extractEmailAddressUserNamePartFromSynapseUserID(): Entry, synapseUserID->{}", synapseUserID);
        if(synapseUserID == null){
            LOG.debug(".extractEmailAddressUserNamePartFromSynapseUserID(): Exit, synapseUserID is null, returning null");
            return(null);
        }
        boolean synapseUserIDContainsAtSymbol = synapseUserID.contains("@");
        boolean synapseUserIDContainsColonSymbol = synapseUserID.contains(":");
        if (!(synapseUserIDContainsAtSymbol && synapseUserIDContainsColonSymbol)) {
            LOG.debug(".extractEmailAddressUserNamePartFromSynapseUserID(): Exit, synapseUserID is not valid");
            return(null);
        }
        String synapseUserIDWithoutAtSymbol = synapseUserID.substring(1, (synapseUserID.length()-1));
        String[] splitString = synapseUserIDWithoutAtSymbol.split(":");
        String synapseEmailAddressUserNamePart = splitString[0];
        LOG.debug(".constructSynapseUserID(): Exit, synapseEmailAddressUserNamePart->{}", synapseEmailAddressUserNamePart);
        return(synapseEmailAddressUserNamePart);
    }

    public String convertFromSynapseUserIDToPractitionerID(String synapseUserID){
        LOG.debug(".convertFromSynapseUserIDToPractitionerID(): Entry, synapseUserID->{}", synapseUserID);
        if(StringUtils.isBlank(synapseUserID)){
            return(null);
        }
        String emailAddressUserName = this.extractEmailAddressUserNamePartFromSynapseUserID(synapseUserID);
        String practitionerID = emailAddressUserName + "@" + communicateConfig.getEmailAddressDomainName();
        return(practitionerID);
    }
}
