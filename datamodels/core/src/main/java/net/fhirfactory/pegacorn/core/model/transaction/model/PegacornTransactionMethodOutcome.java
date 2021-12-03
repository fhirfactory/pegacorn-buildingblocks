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
package net.fhirfactory.pegacorn.core.model.transaction.model;

import ca.uhn.fhir.rest.api.MethodOutcome;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.keyring.PegacornResourceKeyring;
import net.fhirfactory.pegacorn.core.model.transaction.valuesets.PegacornTransactionTypeEnum;
import net.fhirfactory.pegacorn.core.model.transaction.valuesets.PegacornTransactionStatusEnum;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.OperationOutcome;

import java.io.Serializable;

public class PegacornTransactionMethodOutcome extends MethodOutcome implements Serializable {
    private PegacornTransactionStatusEnum statusEnum;
    private PegacornTransactionTypeEnum causalAction;
    private Identifier identifier;
    private String reason;

    //
    // Constructor(s)
    //

    public PegacornTransactionMethodOutcome(){
        super();
    }

    public PegacornTransactionMethodOutcome(PegacornTransactionTypeEnum action, Identifier identifier, MethodOutcome ori){
        this.setCreated(ori.getCreated());
        this.setResource(ori.getResource());
        this.setId(ori.getId());
        this.setOperationOutcome(ori.getOperationOutcome());
        this.setResponseHeaders(ori.getResponseHeaders());
        this.identifier = identifier;
        this.reason = null;
        switch(action){
            case CREATE:{
                if(ori.getCreated()){
                    this.statusEnum = PegacornTransactionStatusEnum.CREATION_FINISH;
                } else {
                    if(ori.getOperationOutcome() == null){
                        this.statusEnum = PegacornTransactionStatusEnum.CREATION_FAILURE;
                    } else {
                        OperationOutcome outcome = (OperationOutcome) ori.getOperationOutcome();
                        boolean isDuplicate = false;
                        for(OperationOutcome.OperationOutcomeIssueComponent issue: outcome.getIssue()){
                            if(issue.hasCode()){
                                if(issue.getCode() == OperationOutcome.IssueType.DUPLICATE){
                                    this.statusEnum = PegacornTransactionStatusEnum.CREATION_NOT_REQUIRED;
                                    isDuplicate = true;
                                    break;
                                }
                            }
                        }
                        if(!isDuplicate) {
                            this.statusEnum = PegacornTransactionStatusEnum.CREATION_FAILURE;
                        }
                    }
                }
                break;
            }
            case UPDATE:{
                if(ori.getOperationOutcome() == null){
                    this.statusEnum = PegacornTransactionStatusEnum.UPDATE_FAILURE;
                } else {
                    OperationOutcome outcome = (OperationOutcome)ori.getOperationOutcome();
                    boolean isOK = false;
                    for(OperationOutcome.OperationOutcomeIssueComponent issue: outcome.getIssue()){
                        if(issue.hasDetails()){
                            CodeableConcept details = issue.getDetails();
                            if(details.hasCoding("https://www.hl7.org/fhir/codesystem-operation-outcome.html", "MSG_UPDATED")){
                                this.statusEnum = PegacornTransactionStatusEnum.UPDATE_FINISH;
                                isOK = true;
                                break;
                            }
                        }
                    }
                    if(!isOK){
                        this.statusEnum = PegacornTransactionStatusEnum.UPDATE_FAILURE;
                    }
                }
                break;
            }
        }
        this.causalAction = action;
    }

    public PegacornTransactionMethodOutcome(PegacornTransactionTypeEnum action, PegacornTransactionStatusEnum status, MethodOutcome ori){
        this.setCreated(ori.getCreated());
        this.setResource(ori.getResource());
        this.setId(ori.getId());
        this.setOperationOutcome(ori.getOperationOutcome());
        this.setResponseHeaders(ori.getResponseHeaders());
        this.identifier = null;
        this.statusEnum = status;
        this.causalAction = action;
        this.reason = null;
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public boolean hasResource(){
        if(getResource()==null){
            return(false);
        } else {
            return(true);
        }
    }

    @JsonIgnore
    public boolean hasResourceId(){
        if(getId()==null){
            return(false);
        } else {
            return (true);
        }
    }

    @JsonIgnore
    public boolean hasResponseHeaders(){
        boolean hasValue = getResponseHeaders() == null;
        boolean isNotEmpty = false;
        if(hasValue) {
            isNotEmpty = !(getResponseHeaders().isEmpty());
        }
        if(hasValue && isNotEmpty){
            return(true);
        } else {
            return(false);
        }
    }

    public PegacornTransactionStatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(PegacornTransactionStatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    public PegacornTransactionTypeEnum getCausalAction() {
        return causalAction;
    }

    public void setCausalAction(PegacornTransactionTypeEnum causalAction) {
        this.causalAction = causalAction;
    }

    @JsonIgnore
    public boolean hasIdentifier(){
        boolean hasValue = this.identifier != null;
        return(hasValue);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    @JsonIgnore
    public boolean hasReason(){
        boolean hasValue = this.reason != null;
        return(hasValue);
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PegacornTransactionMethodOutcome{" +
                "statusEnum=" + statusEnum +
                ", causalAction=" + causalAction +
                ", identifier=" + identifier +
                ", reason='" + reason + '\'' +
                ", created=" + getCreated() +
                ", id=" + getId() +
                ", operationOutcome=" + getOperationOutcome() +
                ", resource=" + getResource() +
                ", responseHeaders=" + getResponseHeaders() +
                '}';
    }
}
