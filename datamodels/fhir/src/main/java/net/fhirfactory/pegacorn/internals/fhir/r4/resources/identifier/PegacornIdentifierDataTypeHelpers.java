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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.identifier;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PegacornIdentifierDataTypeHelpers {
    private static final Logger LOG = LoggerFactory.getLogger(PegacornIdentifierDataTypeHelpers.class);

    /**
     * This method cycles through all the Identifiers and attempts to return "the best"!
     *
     * Order of preference is: OFFICIAL --> USUAL --> SECONDARY --> TEMP --> OLD --> ANY
     * @param identifierSet The list of Identifiers contained within a Resource
     * @return The "Best" identifier from the set.
     */
    public Identifier getBestIdentifier(List<Identifier> identifierSet){
        LOG.debug(".getBestIdentifier(): Entry");
        if(identifierSet == null){
            LOG.warn(".getBestIdentifier(): identifierSet is null");
            return(null);
        }
        if(identifierSet.isEmpty()){
            LOG.warn(".getBestIdentifier(): identifierSet is empty");
            return(null);
        }
        for(Identifier identifier: identifierSet){
            if(identifier.getUse().equals(Identifier.IdentifierUse.OFFICIAL)){
                LOG.debug(".getBestIdentifier(): Found an -OFFICIAL- Identifier, returning it");
                return(identifier);
            }
        }
        for(Identifier identifier: identifierSet){
            if(identifier.getUse().equals(Identifier.IdentifierUse.USUAL)){
                LOG.debug(".getBestIdentifier(): Found an -USUAL- Identifier, returning it");
                return(identifier);
            }
        }
        for(Identifier identifier: identifierSet){
            if(identifier.getUse().equals(Identifier.IdentifierUse.SECONDARY)){
                LOG.debug(".getBestIdentifier(): Found an -SECONDARY- Identifier, returning it");
                return(identifier);
            }
        }
        for(Identifier identifier: identifierSet){
            if(identifier.getUse().equals(Identifier.IdentifierUse.TEMP)){
                LOG.debug(".getBestIdentifier(): Found an -TEMP- Identifier, returning it");
                return(identifier);
            }
        }
        for(Identifier identifier: identifierSet){
            if(identifier.getUse().equals(Identifier.IdentifierUse.OLD)){
                LOG.debug(".getBestIdentifier(): Found an -OLD- Identifier, returning it");
                return(identifier);
            }
        }
        Identifier bestIdentifier = identifierSet.get(0);
        LOG.debug(".getBestIdentifier(): Found nothing, returning the -first- entry");
        return(bestIdentifier);
    }

    public String generatePrintableInformationFromIdentifier(Identifier identifier) {
        if(identifier == null) {
            return("No Identifier");
        }
        String printableInfo = new String();
        if(identifier.hasType()) {
            printableInfo += "Identifier";
            CodeableConcept identifierType = identifier.getType();
            int codeCount = identifierType.getCoding().size();
            printableInfo += "{\"type\":[";
            for(Coding identifierTypeCoding: identifierType.getCoding()) {
                if(identifierTypeCoding.hasSystem()) {
                    printableInfo += "{\"";
                    printableInfo += identifierTypeCoding.getSystem();
                    printableInfo += "\"";
                } else {
                    printableInfo += "\"\"";
                }
                printableInfo += ":";
                if(identifierTypeCoding.hasCode()) {
                    printableInfo += "\"";
                    printableInfo += identifierTypeCoding.getCode();
                    printableInfo += "\"}";
                } else {
                    printableInfo += "\"\"}";
                }
            }
            codeCount -= 1;
            if(codeCount < 1) {
                printableInfo += "]},";
            } else {
                printableInfo += ",";
            }
        }
        if(identifier.hasValue()) {
            printableInfo += "{\"value\":\"" + identifier.getValue() + "\"}";
        }
        return(printableInfo);
    }
}
