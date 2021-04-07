/*
 * Copyright (c) 2020 Mark A. Hunter
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

import net.fhirfactory.pegacorn.common.model.generalid.FDN;
import net.fhirfactory.pegacorn.common.model.generalid.RDN;
import net.fhirfactory.pegacorn.common.model.topicid.TopicToken;
import net.fhirfactory.pegacorn.common.model.topicid.TopicTypeEnum;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FHIRElementTypeExtractor {

	public Class<?> extractResourceType(TopicToken token) throws ClassNotFoundException {

    	if(token == null) {
    		return(null);
    	}
    	if(token.getIdentifier() == null) {
    		return(null);
    	}
    	FDN topicFDN = new FDN(token.getIdentifier());
        String resourceName = null;
        for(RDN currentRDN: topicFDN.getRDNSet()) {
        	if(currentRDN.getQualifier().contentEquals(TopicTypeEnum.DATASET_RESOURCE.getTopicType())) {
        		resourceName = currentRDN.getValue();
        		break;
        	}
        }
        if(resourceName==null) {
        	return(null);
        }
        return(Class.forName("org.hl7.fhir.r4.model." + resourceName));
    }

    
}
