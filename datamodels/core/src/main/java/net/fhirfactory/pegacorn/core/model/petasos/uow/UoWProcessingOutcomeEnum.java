/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.petasos.uow;

/**
 *
 * @author ACT Health (Mark A. Hunter)
 */
public enum UoWProcessingOutcomeEnum {
    UOW_OUTCOME_SUCCESS("ProcessingOutcome.Success", "pegacorn.uow.processing-outcome.success"),
    UOW_OUTCOME_SOFTFAILURE("ProcessingOutcome.SoftFailure", "pegacorn.uow.processing-outcome.soft-failure"),
    UOW_OUTCOME_FAILED("ProcessingOutcome.Failed","pegacorn.uow.processing-outcome.failed"),
    UOW_OUTCOME_INCOMPLETE("ProcessingOutcome.Incomplete","pegacorn.uow.processing-outcome.incomplete"),
    UOW_OUTCOME_NO_PROCESSING_REQUIRED("ProcessingOutcome.NoneRequired","pegacorn.uow.processing-outcome.nonerequired"),
    UOW_OUTCOME_FILTERED("ProcessingOutcome.Filtered","pegacorn.uow.processing-outcome.filtered"),
    UOW_OUTCOME_DISCARD("ProcessingOutcome.Discarded","pegacorn.uow.processing-outcome.discard"),
    UOW_OUTCOME_NOTSTARTED("ProcessingOutcome.NotStarted","pegacorn.uow.processing-outcome.not_started"),
    UOW_OUTCOME_CANCELLED("ProcessingOutcome.Cancelled", "pegacorn.uow.processing-outcome.cancelled");
    
    private String token;
    private String displayName;
    
    private UoWProcessingOutcomeEnum(String display, String token ){
        this.token = token;
        this.displayName = display;
    }
    
    public String getToken(){
        return(this.token);
    }

    public String getDisplayName(){
        return(this.displayName);
    }
    
}
