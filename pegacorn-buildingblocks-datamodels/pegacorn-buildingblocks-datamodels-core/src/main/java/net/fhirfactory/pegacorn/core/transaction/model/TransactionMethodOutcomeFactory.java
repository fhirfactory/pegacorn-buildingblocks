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
package net.fhirfactory.pegacorn.core.transaction.model;

import org.hl7.fhir.r4.model.*;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionMethodOutcomeFactory {

    public net.fhirfactory.pegacorn.core.transaction.model.TransactionMethodOutcome createResourceActivityOutcome(IdType resourceId, net.fhirfactory.pegacorn.core.transaction.model.TransactionStatusEnum status, String activityLocation) {
        net.fhirfactory.pegacorn.core.transaction.model.TransactionMethodOutcome vdbOutcome = new net.fhirfactory.pegacorn.core.transaction.model.TransactionMethodOutcome();
        populateResourceActivityOutcome(vdbOutcome, resourceId, status, activityLocation );
        return(vdbOutcome);
    }

    public void populateResourceActivityOutcome(net.fhirfactory.pegacorn.core.transaction.model.TransactionMethodOutcome vdbOutcome, IdType resourceId, net.fhirfactory.pegacorn.core.transaction.model.TransactionStatusEnum status, String activityLocation)
    {
        vdbOutcome.setCreated(false);
        vdbOutcome.setId(resourceId);
        OperationOutcome opOutcome = new OperationOutcome();
        OperationOutcome.OperationOutcomeIssueComponent newOutcomeComponent = new OperationOutcome.OperationOutcomeIssueComponent();
        CodeableConcept details = new CodeableConcept();
        Coding detailsCoding = new Coding();
        switch(status) {
            case DELETE_FINISH: {
                vdbOutcome.setStatusEnum(net.fhirfactory.pegacorn.core.transaction.model.TransactionStatusEnum.DELETE_FINISH);
                vdbOutcome.setCausalAction(net.fhirfactory.pegacorn.core.transaction.model.TransactionTypeEnum.DELETE);
                newOutcomeComponent.setCode(OperationOutcome.IssueType.DELETED);
                newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.INFORMATION);
                detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
                detailsCoding.setCode("MSG_DELETED");
                detailsCoding.setDisplay("This resource has been deleted");
                details.setText("This resource has been deleted");
                break;
            }
            case DELETE_FAILURE: {
                vdbOutcome.setStatusEnum(net.fhirfactory.pegacorn.core.transaction.model.TransactionStatusEnum.DELETE_FAILURE);
                vdbOutcome.setCausalAction(net.fhirfactory.pegacorn.core.transaction.model.TransactionTypeEnum.DELETE);
                newOutcomeComponent.setCode(OperationOutcome.IssueType.NOTFOUND);
                newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.INFORMATION);
                detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
                detailsCoding.setCode("MSG_NO_MATCH");
                String text = "No Resource found matching the query: " + resourceId;
                break;
            }
            case CREATION_NOT_REQUIRED: {
                vdbOutcome.setStatusEnum(net.fhirfactory.pegacorn.core.transaction.model.TransactionStatusEnum.CREATION_NOT_REQUIRED);
                vdbOutcome.setCausalAction(net.fhirfactory.pegacorn.core.transaction.model.TransactionTypeEnum.CREATE);
                newOutcomeComponent.setCode(OperationOutcome.IssueType.INFORMATIONAL);
                newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.INFORMATION);
                detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
                detailsCoding.setCode("MSG_CREATED");
                detailsCoding.setDisplay("New Resource Created");
                details.setText("New Resource Created");
                break;
            }
            case CREATION_FINISH:{
                vdbOutcome.setStatusEnum(net.fhirfactory.pegacorn.core.transaction.model.TransactionStatusEnum.CREATION_FINISH);
                vdbOutcome.setCausalAction(net.fhirfactory.pegacorn.core.transaction.model.TransactionTypeEnum.CREATE);
                newOutcomeComponent.setCode(OperationOutcome.IssueType.INFORMATIONAL);
                newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.INFORMATION);
                detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
                detailsCoding.setCode("MSG_CREATED");
                detailsCoding.setDisplay("New Resource Created");
                details.setText("New Resource Created");
                break;
            }
            case REVIEW_FAILURE: {
                vdbOutcome.setStatusEnum(net.fhirfactory.pegacorn.core.transaction.model.TransactionStatusEnum.REVIEW_FAILURE);
                vdbOutcome.setCausalAction(net.fhirfactory.pegacorn.core.transaction.model.TransactionTypeEnum.REVIEW);
                newOutcomeComponent.setCode(OperationOutcome.IssueType.NOTFOUND);
                newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.WARNING);
                detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
                detailsCoding.setCode("MSG_NO_EXIST");
                String text = "Resource Id " + resourceId + "does not exist";
                detailsCoding.setDisplay(text);
                details.setText(text);
                break;
            }
            case REVIEW_FINISH:{
                vdbOutcome.setStatusEnum(net.fhirfactory.pegacorn.core.transaction.model.TransactionStatusEnum.REVIEW_FINISH);
                vdbOutcome.setCausalAction(net.fhirfactory.pegacorn.core.transaction.model.TransactionTypeEnum.REVIEW);
                newOutcomeComponent.setCode(OperationOutcome.IssueType.INFORMATIONAL);
                newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.INFORMATION);
                detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html"); // TODO Pegacorn specific encoding --> need to check validity
                detailsCoding.setCode("MSG_RESOURCE_RETRIEVED"); // TODO Pegacorn specific encoding --> need to check validity
                details.setText("Resource Id ("+ resourceId +") has been retrieved");
                String text = "Resource Id " + resourceId + "does not exist";
                details.setText(text);
                detailsCoding.setDisplay(text);
                break;
            }
        }
        details.addCoding(detailsCoding);
        newOutcomeComponent.setDiagnostics(activityLocation);
        newOutcomeComponent.setDetails(details);
        opOutcome.addIssue(newOutcomeComponent);
        vdbOutcome.setOperationOutcome(opOutcome);
    }

    public net.fhirfactory.pegacorn.core.transaction.model.TransactionMethodOutcome generateEmptyGetResponse(ResourceType resourceType, IdType id){
        net.fhirfactory.pegacorn.core.transaction.model.TransactionMethodOutcome outcome = new net.fhirfactory.pegacorn.core.transaction.model.TransactionMethodOutcome();
        outcome.setCreated(false);
        outcome.setCausalAction(net.fhirfactory.pegacorn.core.transaction.model.TransactionTypeEnum.REVIEW);
        outcome.setStatusEnum(net.fhirfactory.pegacorn.core.transaction.model.TransactionStatusEnum.REVIEW_FAILURE);
        CodeableConcept details = new CodeableConcept();
        Coding detailsCoding = new Coding();
        detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
        detailsCoding.setCode("MSG_NO_EXIST");
        String text = "Resource Id " + id.toString() + " does not exist";
        detailsCoding.setDisplay(text);
        details.setText(text);
        details.addCoding(detailsCoding);
        OperationOutcome opOutcome = new OperationOutcome();
        OperationOutcome.OperationOutcomeIssueComponent newOutcomeComponent = new OperationOutcome.OperationOutcomeIssueComponent();
        newOutcomeComponent.setDiagnostics(resourceType.toString());
        newOutcomeComponent.setDetails(details);
        newOutcomeComponent.setCode(OperationOutcome.IssueType.NOTFOUND);
        newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.WARNING);
        opOutcome.addIssue(newOutcomeComponent);
        outcome.setOperationOutcome(opOutcome);
        return(outcome);
    }

    public net.fhirfactory.pegacorn.core.transaction.model.TransactionMethodOutcome generateBadAttributeOutcome(String method, net.fhirfactory.pegacorn.core.transaction.model.TransactionTypeEnum action, net.fhirfactory.pegacorn.core.transaction.model.TransactionStatusEnum actionStatus, String text){
        net.fhirfactory.pegacorn.core.transaction.model.TransactionMethodOutcome vdbOutcome = new net.fhirfactory.pegacorn.core.transaction.model.TransactionMethodOutcome();
        vdbOutcome.setCreated(false);
        vdbOutcome.setCausalAction(action);
        vdbOutcome.setStatusEnum(actionStatus);
        CodeableConcept details = new CodeableConcept();
        Coding detailsCoding = new Coding();
        detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
        detailsCoding.setCode("MSG_PARAM_INVALID");
        detailsCoding.setDisplay(text);
        details.setText(text);
        details.addCoding(detailsCoding);
        OperationOutcome opOutcome = new OperationOutcome();
        OperationOutcome.OperationOutcomeIssueComponent newOutcomeComponent = new OperationOutcome.OperationOutcomeIssueComponent();
        newOutcomeComponent.setDiagnostics(method);
        newOutcomeComponent.setDetails(details);
        newOutcomeComponent.setCode(OperationOutcome.IssueType.INVALID);
        newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.ERROR);
        opOutcome.addIssue(newOutcomeComponent);
        vdbOutcome.setOperationOutcome(opOutcome);
        return(vdbOutcome);
    }

    public net.fhirfactory.pegacorn.core.transaction.model.TransactionMethodOutcome generateEmptySearchResponse(ResourceType resourceType){
        net.fhirfactory.pegacorn.core.transaction.model.TransactionMethodOutcome outcome = new TransactionMethodOutcome();
        outcome.setCreated(false);
        outcome.setCausalAction(TransactionTypeEnum.SEARCH);
        outcome.setStatusEnum(TransactionStatusEnum.SEARCH_FAILURE);
        CodeableConcept details = new CodeableConcept();
        Coding detailsCoding = new Coding();
        detailsCoding.setSystem("https://www.hl7.org/fhir/codesystem-operation-outcome.html");
        detailsCoding.setCode("TODO:: Document");
        String text = "TODO:: Document";
        detailsCoding.setDisplay(text);
        details.setText(text);
        details.addCoding(detailsCoding);
        OperationOutcome opOutcome = new OperationOutcome();
        OperationOutcome.OperationOutcomeIssueComponent newOutcomeComponent = new OperationOutcome.OperationOutcomeIssueComponent();
        newOutcomeComponent.setDiagnostics(resourceType.toString());
        newOutcomeComponent.setDetails(details);
        newOutcomeComponent.setCode(OperationOutcome.IssueType.NOTFOUND);
        newOutcomeComponent.setSeverity(OperationOutcome.IssueSeverity.WARNING);
        opOutcome.addIssue(newOutcomeComponent);
        outcome.setOperationOutcome(opOutcome);
        return(outcome);
    }

}
