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
package net.fhirfactory.pegacorn.internals.directories.api.beans.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.fhirfactory.pegacorn.internals.directories.brokers.common.ResourceDirectoryBroker;
import net.fhirfactory.pegacorn.internals.directories.entries.common.PegacornDirectoryEntry;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDE;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.IdentifierDEUseEnum;
import net.fhirfactory.pegacorn.internals.directories.entries.datatypes.PegId;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcome;
import net.fhirfactory.pegacorn.internals.directories.model.DirectoryMethodOutcomeEnum;
import net.fhirfactory.pegacorn.internals.directories.model.exceptions.DirectoryEntryNotFoundException;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HandlerBase {

    abstract protected Logger getLogger();
    abstract protected ResourceDirectoryBroker specifyResourceBroker();

    protected ResourceDirectoryBroker getResourceBroker(){
        return(specifyResourceBroker());
    }

    public Map<String, String> extractParameters(String parameterString){

        HashMap<String, String> parameterMap = new HashMap<>();
        if(parameterString == null){
            return(parameterMap);
        }
        if(parameterString.length()<3){
            return(parameterMap);
        }
        String[] parameterSet = parameterString.split("&");
        int numberOfParameters = parameterSet.length;
        for(int counter = 0; counter < numberOfParameters; counter += 1){
            String[] parameterPair = parameterSet[counter].split("=");
            parameterMap.put(parameterPair[0], parameterPair[1]);
        }
        return(parameterMap);
    }

    protected String convertToJSONString(List<PegacornDirectoryEntry> entrySet){
        JsonMapper jsonMapper = new JsonMapper();
        try {
            String arrayAsList = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entrySet);
            return(arrayAsList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return("");
    }

    protected String convertToJSONString(PegacornDirectoryEntry entry){
        JsonMapper jsonMapper = new JsonMapper();
        try {
            String resourceAsString = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entry);
            return(resourceAsString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return("");
    }
    
    //
    // get
    //

    public String get(@Header("CamelHttpQuery") String parameters, @Header("CamelHttpPath") String extraPath, String inputBody, Exchange camelExchange)
            throws DirectoryEntryNotFoundException {
        getLogger().info(".get(): Entry, extraPath --> {}, parameters --> {}, inputBody --> {}", extraPath, parameters, inputBody);
        if(extraPath.isEmpty() || extraPath == null){
            getLogger().info(".get(): Is a Search, so invoking Search function");
            String outcome = doEntrySearch(parameters, inputBody, camelExchange);
            return(outcome);
        } else {
            getLogger().info(".get(): Is a singular Get");
            DirectoryMethodOutcome outcome = getEntry(extraPath, inputBody, camelExchange);
            if(outcome.getStatus().equals(DirectoryMethodOutcomeEnum.REVIEW_ENTRY_FOUND)) {
                PegacornDirectoryEntry directoryEntry = outcome.getEntry();
                getLogger().info(".get(): Result --> {}", directoryEntry);
                String result = convertToJSONString(directoryEntry);
                return (result);
            } else {
                throw( new DirectoryEntryNotFoundException(outcome.getId().getValue()));
            }
        }
    }

    private DirectoryMethodOutcome getEntry(String pathValue, String inputBody, Exchange camelExchange){
        getLogger().info(".getEntry(): Entry, pathValue --> {}", pathValue);
        String idValue = pathValue.substring(1);
        getLogger().info(".getEntry(): Extracted DirectoryEntry Id --> {}", idValue);
        PegId id = new PegId(idValue);
        DirectoryMethodOutcome outcome = getResourceBroker().reviewDirectoryEntry(id);
        getLogger().info(".getEntry(): Exit, outcome --> {}", outcome.getStatus());
        return(outcome);
    }

    //
    // Search via Identifier
    //

    private String doEntrySearch(String parameters, String inputBody, Exchange camelExchange){
        getLogger().info(".doEntrySearch(): Entry, parameters --> {}, inputBody --> {}", parameters, inputBody);
        Map<String, String> parameterSet = this.extractParameters(parameters);
        if(parameterSet.size() == 0){
            getLogger().info(".doEntrySearch(): Zero parameter search, so returning ALL Entries");
            DirectoryMethodOutcome outcome = getResourceBroker().reviewDirectoryEntry();
            getLogger().info(".doEntrySearch(): Zero parameter search completed, number of entries --> {}", outcome.getSearchResult().size());
            getLogger().info(".doEntrySearch(): Convert result to JSON String");
            String result = convertToJSONString(outcome.getSearchResult());
            getLogger().info(".doEntrySearch(): Exit");
            return(result);
        }
        if(parameterSet.size() == 1){
            getLogger().info(".doEntrySearch(): One parameter search, assuming search by Identifier");
            String searchCriteria = parameterSet.get("identifier");
            getLogger().info(".doEntrySearch(): Searching for PegacornDirectoryEntry with Identifier (full detail) --> {}" , searchCriteria);
            String[] splitCriteria = searchCriteria.split("\\|");
            String identifierType = splitCriteria[0];
            IdentifierDEUseEnum identifierUse = IdentifierDEUseEnum.fromUseCode(splitCriteria[1]);
            String identifierValue = splitCriteria[2];
            getLogger().info(".doEntrySearch(): Creating an IdentifierDE with values --> Type:{}, Use:{}, Value:{}" , identifierType, identifierUse, identifierValue);
            IdentifierDE requestedIdentifier = new IdentifierDE();
            requestedIdentifier.setType(identifierType);
            requestedIdentifier.setUse(identifierUse);
            requestedIdentifier.setValue(identifierValue);
            getLogger().info(".doEntrySearch(): IdentifierDE created for query, now invoking search");
            DirectoryMethodOutcome outcome = getResourceBroker().searchForDirectoryResourceRoleUsingIdentifier(requestedIdentifier);
            getLogger().info(".doEntrySearch(): Result --> {}", outcome);
            getLogger().info(".doEntrySearch(): Convert result to JSON String");
            String result = convertToJSONString(outcome.getSearchResult());
            getLogger().info(".doEntrySearch(): Exit");
            return(result);
        }
        return("");
    }
    
}
