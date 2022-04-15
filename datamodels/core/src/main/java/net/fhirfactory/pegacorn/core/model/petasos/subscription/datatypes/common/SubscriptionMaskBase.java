/*
 * Copyright (c) 2022 Mark A. Hunter
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
package net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.petasos.participant.PetasosParticipantName;
import net.fhirfactory.pegacorn.core.model.petasos.subscription.datatypes.DataParcelManifestSubscriptionMaskType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.Serializable;

public abstract class SubscriptionMaskBase implements Serializable {

    private Boolean allowAll;

    //
    // Constructor(s)
    //

    public SubscriptionMaskBase(){
        this.allowAll = null;
    }

    public SubscriptionMaskBase(SubscriptionMaskBase ori){
        this.allowAll = null;

        if(ori.hasAllowAll()){
            setAllowAll(ori.getAllowAll());
        }
    }

    //
    // Abstract
    //

    abstract protected Logger getLogger();

    //
    // ifExists (has)
    //

    @JsonIgnore
    public boolean hasAllowAll(){
        boolean hasValue = this.allowAll != null;
        return(hasValue);
    }

    //
    // Getters and Setters
    //

    public Boolean getAllowAll() {
        return allowAll;
    }

    public void setAllowAll(Boolean allowAll) {
        this.allowAll = allowAll;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return "SubscriptionMaskBase{" +
                "allowAll=" + allowAll +
                '}';
    }

    //
    // Comparisons / Mask Tests
    //

    protected boolean participantNamePasses(PetasosParticipantName maskName, PetasosParticipantName testName){
        getLogger().debug(".participantNamePasses(): Entry, maskName->{}, testName->{}", maskName, testName);

        if(maskName != null){
            if(StringUtils.isNotEmpty(maskName.getName())){
                if(maskName.getName().contains(DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER)){
                    getLogger().debug(".participantNamePasses(): Exit, maskName is Wildcard, returning -true-");
                    return(true);
                }
            }
        }

        if(maskName == null && testName != null){
            getLogger().debug(".participantNamePasses(): Exit, maskName is null, but testName is not, returning -false-");
            return(false);
        }
        if(maskName != null && testName == null){
            getLogger().debug(".participantNamePasses(): Exit, maskName is not null AND not a wildcard, but testName is null, returning -false-");
            return(false);
        }
        if(StringUtils.isEmpty(maskName.getName()) && StringUtils.isNotEmpty(testName.getName())){
            getLogger().debug(".participantNamePasses(): Exit, maskName.getName() is empty, but testName.getName() is not, returning -false-");
            return(false);
        }
        if(StringUtils.isNotEmpty(maskName.getName()) && StringUtils.isEmpty(testName.getName())){
            getLogger().debug(".participantNamePasses(): Exit, maskName.getName() is not empty AND not a wildcard, but testName.getName() is null, returning -false-");
            return(false);
        }
        boolean contentMatches = maskName.getName().contentEquals(testName.getName());
        getLogger().debug(".participantNamePasses(): Exit, contentMatches->{} ", contentMatches);
        return(contentMatches);
    }

    protected boolean namePasses(String maskName, String testName){
        getLogger().debug(".namePasses(): Entry, maskName->{}, testName->{}", maskName, testName);

        if(StringUtils.isNotEmpty(maskName)){
            if(maskName.contains(DataParcelManifestSubscriptionMaskType.WILDCARD_CHARACTER)){
                getLogger().debug(".namePasses(): Exit, maskName is Wildcard, returning -true-");
                return(true);
            }
        }
        if(StringUtils.isEmpty(maskName) && StringUtils.isNotEmpty(testName)){
            getLogger().debug(".namePasses(): Exit, maskName is empty, but testName is not, returning -false-");
            return(false);
        }
        if(StringUtils.isNotEmpty(maskName) && StringUtils.isEmpty(testName)){
            getLogger().debug(".namePasses(): Exit, maskName is not empty AND not a wildcard, but testName is null, returning -false-");
            return(false);
        }
        boolean contentMatches = maskName.contentEquals(testName);
        getLogger().debug(".namePasses(): Exit, contentMatches->{} ", contentMatches);
        return(contentMatches);
    }
}
