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
package net.fhirfactory.pegacorn.deployment.properties.environmentlookupbased;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PlatformEnvironmentLookup {
    private static final Logger LOG = LoggerFactory.getLogger(PlatformEnvironmentLookup.class);

    private static String POD_NAME_ENVIRONMENT_VARIABLE_NAME = "MY_POD_NAME";

    public String getPlatformName(boolean isKubernetesDeployed){
        LOG.debug(".getPlatformName(): Entry, isKubernetesDeployed->{}", isKubernetesDeployed);
        String platformName;
        if(isKubernetesDeployed) {
            platformName = getPODName();
        } else {
            platformName = getHostName();
        }
        LOG.debug(".getPlatformName(): Exit, platformName->{}", platformName);
        return(platformName);
    }

    public String getPODName(){
        LOG.debug(".getPODName(): Entry");
        String podName = System.getProperty(POD_NAME_ENVIRONMENT_VARIABLE_NAME);
        if(StringUtils.isBlank(podName)){
            LOG.error(".getPODName(): Could not resolve POD name (using environment variable->{}!", POD_NAME_ENVIRONMENT_VARIABLE_NAME);
        }
        LOG.debug(".getPODName(): Exit, podName->{}", podName);
        return(podName);
    }

    public String getHostName(){
        String hostName = "";
        return(hostName);
    }
}
