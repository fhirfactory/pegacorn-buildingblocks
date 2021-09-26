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
package net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.factories;

import net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.datatypes.CacheActivityStatusElement;
import net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.valuesets.CacheActivityActionEnum;
import net.fhirfactory.pegacorn.fhirim.processingplant.net.fhirfactory.pegacorn.fhirim.cache.valuesets.CacheActivityOutcomeEnum;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.thymeleaf.util.StringUtils;

import java.time.Instant;

public class CacheActivityStatusElementFactory {

    public CacheActivityStatusElement newCacheActivityStatusElement(Identifier identifier, CacheActivityActionEnum action, CacheActivityOutcomeEnum outcomeEnum, String activityLocation, String outcomeDescription){
        CacheActivityStatusElement outcome = new CacheActivityStatusElement();
        outcome.setIdentifier(identifier);
        outcome = newCacheActivityStatusElement(outcome, action, outcomeEnum, activityLocation, outcomeDescription);
        return(outcome);
    }

    public CacheActivityStatusElement newCacheActivityStatusElement(IdType resourceId, CacheActivityActionEnum action, CacheActivityOutcomeEnum outcomeEnum, String activityLocation, String outcomeDescription){
        CacheActivityStatusElement outcome = new CacheActivityStatusElement();
        outcome.setResourceId(resourceId);
        outcome = newCacheActivityStatusElement(outcome, action, outcomeEnum, activityLocation, outcomeDescription);
        return(outcome);
    }
    public CacheActivityStatusElement newCacheActivityStatusElement(CacheActivityStatusElement outcome, CacheActivityActionEnum action, CacheActivityOutcomeEnum outcomeEnum, String activityLocation, String outcomeDescription)
    {
        outcome.setAction(action);
        outcome.setOutcome(outcomeEnum);
        outcome.setUpdateDate(Instant.now());
        if(!StringUtils.isEmpty(activityLocation)) {
            outcome.setActivityLocation(activityLocation);
        }
        if(!StringUtils.isEmpty(outcomeDescription)){
            outcome.setCommentary(outcomeDescription);
        }
        if(outcomeEnum.equals(CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_BLOCKED_SECURITY)){
            outcome.setSuccessful(false);
            outcome.setCommentary("Security Blocked the Activity");
        } else if( outcomeEnum.equals(CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_ERROR)){
            outcome.setSuccessful(false);
            outcome.setCommentary("Error Processing Request");
        } else if (outcomeEnum.equals(CacheActivityOutcomeEnum.CACHE_OUTCOME_RESOURCE_INDETERMINANT)){
            outcome.setSuccessful(false);
            outcome.setCommentary("Activity is in an ");
        }else {
            switch (action) {
                case CACHE_ACTION_RESOURCE_CREATE: {
                    switch (outcomeEnum) {
                        case CACHE_OUTCOME_RESOURCE_CREATED:
                            outcome.setSuccessful(true);
                        case CACHE_OUTCOME_RESOURCE_UPDATED:
                            outcome.setCommentary("Existing Resource Updated");
                            break;
                        case CACHE_OUTCOME_RESOURCE_REMOVED:
                        case CACHE_OUTCOME_RESOURCE_RETRIEVED:
                        default:
                            outcome.setSuccessful(false);
                            outcome.setCommentary("Bad Activity State");
                    }
                    break;
                }
                case CACHE_ACTION_RESOURCE_UPDATE: {
                    switch (outcomeEnum) {
                        case CACHE_OUTCOME_RESOURCE_UPDATED:
                            outcome.setSuccessful(true);
                        case CACHE_OUTCOME_RESOURCE_CREATED:
                            outcome.setCommentary("New Resource Updated");
                            break;
                        case CACHE_OUTCOME_RESOURCE_REMOVED:
                        case CACHE_OUTCOME_RESOURCE_RETRIEVED:
                        default:
                            outcome.setSuccessful(false);
                            outcome.setCommentary("Bad Activity State");
                    }
                    break;
                }
                case CACHE_ACTION_RESOURCE_REMOVE: {
                    switch (outcomeEnum) {
                        case CACHE_OUTCOME_RESOURCE_REMOVED:
                            outcome.setSuccessful(true);
                            break;
                        case CACHE_OUTCOME_RESOURCE_UPDATED:
                        case CACHE_OUTCOME_RESOURCE_CREATED:
                        case CACHE_OUTCOME_RESOURCE_RETRIEVED:
                        default:
                            outcome.setSuccessful(false);
                            outcome.setCommentary("Bad Activity State");
                    }
                    break;
                }
                case CACHE_ACTION_RESOURCE_RETRIEVE: {
                    switch (outcomeEnum) {
                        case CACHE_OUTCOME_RESOURCE_RETRIEVED:
                            outcome.setSuccessful(true);
                            break;
                        case CACHE_OUTCOME_RESOURCE_UPDATED:
                        case CACHE_OUTCOME_RESOURCE_CREATED:
                        case CACHE_OUTCOME_RESOURCE_REMOVED:
                        default:
                            outcome.setSuccessful(false);
                            outcome.setCommentary("Bad Activity State");
                    }
                    break;
                }
                case CACHE_ACTION_RESOURCE_SYNCHRONISE:
                case CACHE_ACTION_RESOURCE_LOAD:
                    outcome.setSuccessful(false);
                    break;
            }
        }
        return(outcome);
    }
}
