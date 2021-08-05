package net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.fhirpersistence.im;

import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.archetypes.ClusterServiceDeliverySubsystemPropertyFile;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.ipc.HTTPIPCServerPortSegment;

public abstract class FHIRInformationManagerPropertyFile extends ClusterServiceDeliverySubsystemPropertyFile {

    HTTPIPCServerPortSegment edgeAnswerConformance;
    HTTPIPCServerPortSegment edgeAnswerTerminology;
    HTTPIPCServerPortSegment edgeAnswerSecurity;
    HTTPIPCServerPortSegment edgeAnswerDocuments;
    HTTPIPCServerPortSegment edgeAnswerOther;
    HTTPIPCServerPortSegment edgeAnswerIndividuals;
    HTTPIPCServerPortSegment edgeAnswerEntities;
    HTTPIPCServerPortSegment edgeAnswerWorkflow;
    HTTPIPCServerPortSegment edgeAnswerManagement;
    HTTPIPCServerPortSegment edgeAnswerSummary;
    HTTPIPCServerPortSegment edgeAnswerDiagnostics;
    HTTPIPCServerPortSegment edgeAnswerMedications;
    HTTPIPCServerPortSegment edgeAnswerCareProvision;
    HTTPIPCServerPortSegment edgeAnswerRequestAndResponse;
    HTTPIPCServerPortSegment edgeAnswerSupport;
    HTTPIPCServerPortSegment edgeAnswerBilling;
    HTTPIPCServerPortSegment edgeAnswerPayment;
    HTTPIPCServerPortSegment edgeAnswerGeneral;

    public HTTPIPCServerPortSegment getEdgeAnswerConformance() {
        return edgeAnswerConformance;
    }

    public void setEdgeAnswerConformance(HTTPIPCServerPortSegment edgeAnswerConformance) {
        this.edgeAnswerConformance = edgeAnswerConformance;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerTerminology() {
        return edgeAnswerTerminology;
    }

    public void setEdgeAnswerTerminology(HTTPIPCServerPortSegment edgeAnswerTerminology) {
        this.edgeAnswerTerminology = edgeAnswerTerminology;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerSecurity() {
        return edgeAnswerSecurity;
    }

    public void setEdgeAnswerSecurity(HTTPIPCServerPortSegment edgeAnswerSecurity) {
        this.edgeAnswerSecurity = edgeAnswerSecurity;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerDocuments() {
        return edgeAnswerDocuments;
    }

    public void setEdgeAnswerDocuments(HTTPIPCServerPortSegment edgeAnswerDocuments) {
        this.edgeAnswerDocuments = edgeAnswerDocuments;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerOther() {
        return edgeAnswerOther;
    }

    public void setEdgeAnswerOther(HTTPIPCServerPortSegment edgeAnswerOther) {
        this.edgeAnswerOther = edgeAnswerOther;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerIndividuals() {
        return edgeAnswerIndividuals;
    }

    public void setEdgeAnswerIndividuals(HTTPIPCServerPortSegment edgeAnswerIndividuals) {
        this.edgeAnswerIndividuals = edgeAnswerIndividuals;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerEntities() {
        return edgeAnswerEntities;
    }

    public void setEdgeAnswerEntities(HTTPIPCServerPortSegment edgeAnswerEntities) {
        this.edgeAnswerEntities = edgeAnswerEntities;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerWorkflow() {
        return edgeAnswerWorkflow;
    }

    public void setEdgeAnswerWorkflow(HTTPIPCServerPortSegment edgeAnswerWorkflow) {
        this.edgeAnswerWorkflow = edgeAnswerWorkflow;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerManagement() {
        return edgeAnswerManagement;
    }

    public void setEdgeAnswerManagement(HTTPIPCServerPortSegment edgeAnswerManagement) {
        this.edgeAnswerManagement = edgeAnswerManagement;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerSummary() {
        return edgeAnswerSummary;
    }

    public void setEdgeAnswerSummary(HTTPIPCServerPortSegment edgeAnswerSummary) {
        this.edgeAnswerSummary = edgeAnswerSummary;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerDiagnostics() {
        return edgeAnswerDiagnostics;
    }

    public void setEdgeAnswerDiagnostics(HTTPIPCServerPortSegment edgeAnswerDiagnostics) {
        this.edgeAnswerDiagnostics = edgeAnswerDiagnostics;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerMedications() {
        return edgeAnswerMedications;
    }

    public void setEdgeAnswerMedications(HTTPIPCServerPortSegment edgeAnswerMedications) {
        this.edgeAnswerMedications = edgeAnswerMedications;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerCareProvision() {
        return edgeAnswerCareProvision;
    }

    public void setEdgeAnswerCareProvision(HTTPIPCServerPortSegment edgeAnswerCareProvision) {
        this.edgeAnswerCareProvision = edgeAnswerCareProvision;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerRequestAndResponse() {
        return edgeAnswerRequestAndResponse;
    }

    public void setEdgeAnswerRequestAndResponse(HTTPIPCServerPortSegment edgeAnswerRequestAndResponse) {
        this.edgeAnswerRequestAndResponse = edgeAnswerRequestAndResponse;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerSupport() {
        return edgeAnswerSupport;
    }

    public void setEdgeAnswerSupport(HTTPIPCServerPortSegment edgeAnswerSupport) {
        this.edgeAnswerSupport = edgeAnswerSupport;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerBilling() {
        return edgeAnswerBilling;
    }

    public void setEdgeAnswerBilling(HTTPIPCServerPortSegment edgeAnswerBilling) {
        this.edgeAnswerBilling = edgeAnswerBilling;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerPayment() {
        return edgeAnswerPayment;
    }

    public void setEdgeAnswerPayment(HTTPIPCServerPortSegment edgeAnswerPayment) {
        this.edgeAnswerPayment = edgeAnswerPayment;
    }

    public HTTPIPCServerPortSegment getEdgeAnswerGeneral() {
        return edgeAnswerGeneral;
    }

    public void setEdgeAnswerGeneral(HTTPIPCServerPortSegment edgeAnswerGeneral) {
        this.edgeAnswerGeneral = edgeAnswerGeneral;
    }
}
