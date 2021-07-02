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
package net.fhirfactory.pegacorn.components.transaction.model;

import ca.uhn.fhir.rest.api.MethodOutcome;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionTypeEnum;
import net.fhirfactory.pegacorn.components.transaction.model.TransactionStatusEnum;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.OperationOutcome;

public class TransactionMethodOutcome extends MethodOutcome {
    TransactionStatusEnum statusEnum;
    TransactionTypeEnum causalAction;
    Identifier identifier;

    public boolean hasResource(){
        if(getResource()==null){
            return(false);
        } else {
            return(true);
        }
    }

    public boolean hasResourceId(){
        if(getId()==null){
            return(false);
        } else {
            return (true);
        }
    }

    public TransactionMethodOutcome(){
        super();
    }

    public TransactionMethodOutcome(TransactionTypeEnum action, Identifier identifier, MethodOutcome ori){
        this.setCreated(ori.getCreated());
        this.setResource(ori.getResource());
        this.setId(ori.getId());
        this.setOperationOutcome(ori.getOperationOutcome());
        this.setResponseHeaders(ori.getResponseHeaders());
        this.identifier = identifier;
        switch(action){
            case CREATE:{
                if(ori.getCreated()){
                    this.statusEnum = TransactionStatusEnum.CREATION_FINISH;
                } else {
                    if(ori.getOperationOutcome() == null){
                        this.statusEnum = TransactionStatusEnum.CREATION_FAILURE;
                    } else {
                        OperationOutcome outcome = (OperationOutcome) ori.getOperationOutcome();
                        boolean isDuplicate = false;
                        for(OperationOutcome.OperationOutcomeIssueComponent issue: outcome.getIssue()){
                            if(issue.hasCode()){
                                if(issue.getCode() == OperationOutcome.IssueType.DUPLICATE){
                                    this.statusEnum = TransactionStatusEnum.CREATION_NOT_REQUIRED;
                                    isDuplicate = true;
                                    break;
                                }
                            }
                        }
                        if(!isDuplicate) {
                            this.statusEnum = TransactionStatusEnum.CREATION_FAILURE;
                        }
                    }
                }
                break;
            }
            case UPDATE:{
                if(ori.getOperationOutcome() == null){
                    this.statusEnum = TransactionStatusEnum.UPDATE_FAILURE;
                } else {
                    OperationOutcome outcome = (OperationOutcome)ori.getOperationOutcome();
                    boolean isOK = false;
                    for(OperationOutcome.OperationOutcomeIssueComponent issue: outcome.getIssue()){
                        if(issue.hasDetails()){
                            CodeableConcept details = issue.getDetails();
                            if(details.hasCoding("https://www.hl7.org/fhir/codesystem-operation-outcome.html", "MSG_UPDATED")){
                                this.statusEnum = TransactionStatusEnum.UPDATE_FINISH;
                                isOK = true;
                                break;
                            }
                        }
                    }
                    if(!isOK){
                        this.statusEnum = TransactionStatusEnum.UPDATE_FAILURE;
                    }
                }
                break;
            }
        }
        this.causalAction = action;
    }

    public TransactionMethodOutcome(TransactionTypeEnum action, TransactionStatusEnum status, MethodOutcome ori){
        this.setCreated(ori.getCreated());
        this.setResource(ori.getResource());
        this.setId(ori.getId());
        this.setOperationOutcome(ori.getOperationOutcome());
        this.setResponseHeaders(ori.getResponseHeaders());
        this.identifier = null;
        this.statusEnum = status;
        this.causalAction = action;
    }

    public TransactionStatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(TransactionStatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    public TransactionTypeEnum getCausalAction() {
        return causalAction;
    }

    public void setCausalAction(TransactionTypeEnum causalAction) {
        this.causalAction = causalAction;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }
}
