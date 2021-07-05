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
package net.fhirfactory.pegacorn.internals.esr.cache;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.internals.esr.cache.common.PegacornESRCache;
import net.fhirfactory.pegacorn.internals.esr.resources.PatientESR;
import net.fhirfactory.pegacorn.internals.esr.resources.common.ExtremelySimplifiedResource;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDT;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierESDTUseEnum;
import net.fhirfactory.pegacorn.internals.esr.resources.datatypes.IdentifierType;
import net.fhirfactory.pegacorn.internals.esr.search.ESRSearchResult;
import net.fhirfactory.pegacorn.internals.esr.search.SearchCriteria;
import net.fhirfactory.pegacorn.internals.esr.search.result.PatientSearchResult;
import net.fhirfactory.pegacorn.internals.esr.transactions.ESRMethodOutcome;
import net.fhirfactory.pegacorn.internals.esr.transactions.exceptions.ResourceInvalidSearchException;

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
    public ESRSearchResult search(SearchCriteria searchCriteria)  throws ResourceInvalidSearchException {
        getLogger().debug(".search(): Entry, searchAttributeName->{}, searchAttributeValue->{}", searchCriteria.getSearchParam().getName(), searchCriteria.getSearchParam().getValue());
        
        if(searchCriteria.isValueNull()){
            throw(new ResourceInvalidSearchException("Search Value is null"));
        }
        if(searchCriteria.isParamNameNull()){
            throw(new ResourceInvalidSearchException("Search Parameter Name is empty"));
        }
        
        
        ESRSearchResult result = instatiateNewESRSearchResult();
        
        if(searchCriteria.isValueEmpty()){
            return(result);
        }
        
        switch(searchCriteria.getSearchParam().getName()){
            case SIMPLIFIED_ID:
            case EMAIL_ADDRESS:{
                result = this.searchCacheUsingSimplifiedID(searchCriteria);
                return (result);
            }
            case DATE_OF_BIRTH: {
                result = this.searchCacheForESRUsingIdentifierParameters(searchCriteria,IdentifierType.DATE_OF_BIRTH, IdentifierESDTUseEnum.USUAL);
                return(result);
            }
            case DISPLAY_NAME: {
                result = this.searchCacheUsingDisplayName(searchCriteria);
                return(result);
            }
            default: {
                return (result);
            }
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
