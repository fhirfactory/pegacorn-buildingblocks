/*
 * Copyright (c) 2021 Mark Hunter
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
package net.fhirfactory.pegacorn.core.model.internal.cache;

import net.fhirfactory.pegacorn.core.model.internal.resources.simple.PatientESR;
import net.fhirfactory.pegacorn.core.model.internal.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.valuesets.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.search.PatientSearchResult;
import net.fhirfactory.pegacorn.core.model.internal.resources.simple.search.common.ESRSearchResult;
import net.fhirfactory.pegacorn.core.model.internal.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.core.model.internal.transactions.exceptions.ResourceInvalidSearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Date;

@ApplicationScoped
public class PatientESRCache extends PegacornESRCache {
    private static final Logger LOG = LoggerFactory.getLogger(PatientESRCache.class);

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    public PatientESRCache(){
        super();
    }

    public ESRMethodOutcome addPatient(PatientESR practitionerESR){
        ESRMethodOutcome outcome = addCacheEntry(practitionerESR);
        return(outcome);
    }

    public PatientESR getPatient(IdentifierESDT practitionerID){
        ExtremelySimplifiedResource foundEntry = this.getCacheEntry(practitionerID);
        PatientESR foundPatientESR = (PatientESR) foundEntry;
        return(foundPatientESR);
    }

    //
    // Search Functions
    //

    @Override
    protected ESRSearchResult instatiateNewESRSearchResult(){
        PatientSearchResult result = new PatientSearchResult();
        return(result);
    }

    @Override
    public ESRSearchResult search(String searchAttributeName, String searchAttributeValue)
            throws ResourceInvalidSearchException {
        getLogger().debug(".search(): Entry, searchAttributeName->{}, searchAttributeValue->{}", searchAttributeName, searchAttributeValue);
        if(searchAttributeName == null || searchAttributeValue == null){
            throw(new ResourceInvalidSearchException("Search Parameter Name or Value are null"));
        }
        if(searchAttributeName.isEmpty()){
            throw(new ResourceInvalidSearchException("Search Parameter Name is empty"));
        }
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(searchAttributeValue.isEmpty()){
            return(result);
        }
        String searchAttributeNameLowerCase = searchAttributeName.toLowerCase();
        switch(searchAttributeNameLowerCase){
            case "simplifiedid":
            case "emailaddress":{
                result = this.searchCacheUsingSimplifiedID(searchAttributeValue);
                return (result);
            }
            case "dateofbirth": {
                result = this.searchCacheForESRUsingIdentifierParameters(searchAttributeValue, "Date of Birth", IdentifierESDTUseEnum.USUAL);
                return(result);
            }
            case "displayname": {
                result = this.searchCacheUsingDisplayName(searchAttributeValue);
                return(result);
            }
            default: {
                return (result);
            }
        }
    }

    @Override
    public Boolean supportsSearchType(String attributeName) {
        String searchAttributeNameLowerCase = attributeName.toLowerCase();
        switch(searchAttributeNameLowerCase){
            case "simplifiedid":
            case "dateofbirth":
            case "displayname":
                return(true);
            default:
                return(false);
        }
    }

    public ESRSearchResult searchCacheViaDateOfBirth(Date dateOfBirth){
        LOG.debug(".searchCacheViaDateOfBirth(): Entry");
        ESRSearchResult result = instatiateNewESRSearchResult();
        if(this.getSimplifiedID2ESRMap().isEmpty()){
            LOG.debug(".searchCacheViaDateOfBirth(): Exit, cache is empty, so returning empty list!");
            return(result);
        }
        for(ExtremelySimplifiedResource currentResource: this.getSimplifiedID2ESRMap().values()){
            PatientESR currentPatient = (PatientESR) currentResource;
            if(currentPatient.getDateOfBirth().equals(dateOfBirth)){
                result.getSearchResultList().add(currentPatient);
            }
        }
        LOG.debug(".searchCacheViaDateOfBirth(): Exit");
        return(result);
    }
}
