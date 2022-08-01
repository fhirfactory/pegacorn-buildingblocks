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
package net.fhirfactory.pegacorn.petasos.endpoints.services.common;

import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.jgroups.JGroupsIntegrationPoint;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessingPlantJGroupsIntegrationPointSet {

    private JGroupsIntegrationPoint petasosMessagingServicesEndpoint;
    private JGroupsIntegrationPoint petasosSubscriptionServicesEndpoint;
    private JGroupsIntegrationPoint petasosTopologyServicesEndpoint;
    private JGroupsIntegrationPoint petasosAuditServicesEndpoint;
    private JGroupsIntegrationPoint petasosTaskServicesEndpoint;
    private JGroupsIntegrationPoint petasosInterceptionServicesEndpoint;
    private JGroupsIntegrationPoint petasosMetricsServicesEndpoint;
    private JGroupsIntegrationPoint petasosMediaServicesEndpoint;


    public ProcessingPlantJGroupsIntegrationPointSet(){

        this.petasosMessagingServicesEndpoint = null;
        this.petasosTopologyServicesEndpoint = null;
        this.petasosSubscriptionServicesEndpoint = null;
        this.petasosAuditServicesEndpoint = null;
        this.petasosTaskServicesEndpoint = null;
        this.petasosInterceptionServicesEndpoint = null;
        this.petasosMetricsServicesEndpoint = null;
        this.petasosMediaServicesEndpoint = null;
    }

    //
    // Getters and Setters
    //

    public boolean hasPetasosMessagingServicesEndpoint(){
        boolean hasValue = this.petasosMessagingServicesEndpoint != null;
        return(hasValue);
    }

    public JGroupsIntegrationPoint getPetasosMessagingServicesEndpoint() {
        return petasosMessagingServicesEndpoint;
    }

    public void setPetasosMessagingServicesEndpoint(JGroupsIntegrationPoint petasosMessagingServicesEndpoint) {
        this.petasosMessagingServicesEndpoint = petasosMessagingServicesEndpoint;
    }

    public boolean hasPetasosSubscriptionServicesEndpoint(){
        boolean hasValue = this.petasosSubscriptionServicesEndpoint != null;
        return(hasValue);
    }

    public JGroupsIntegrationPoint getPetasosSubscriptionServicesEndpoint() {
        return petasosSubscriptionServicesEndpoint;
    }

    public void setPetasosSubscriptionServicesEndpoint(JGroupsIntegrationPoint petasosSubscriptionServicesEndpoint) {
        this.petasosSubscriptionServicesEndpoint = petasosSubscriptionServicesEndpoint;
    }

    public boolean hasPetasosTopologyServicesEndpoint(){
        boolean hasValue = this.petasosTopologyServicesEndpoint != null;
        return(hasValue);
    }

    public JGroupsIntegrationPoint getPetasosTopologyServicesEndpoint() {
        return petasosTopologyServicesEndpoint;
    }

    public void setPetasosTopologyServicesEndpoint(JGroupsIntegrationPoint petasosTopologyServicesEndpoint) {
        this.petasosTopologyServicesEndpoint = petasosTopologyServicesEndpoint;
    }

    public boolean hasPetasosAuditServicesEndpoint(){
        boolean hasValue = this.petasosAuditServicesEndpoint != null;
        return(hasValue);
    }

    public JGroupsIntegrationPoint getPetasosAuditServicesEndpoint() {
        return petasosAuditServicesEndpoint;
    }

    public void setPetasosAuditServicesEndpoint(JGroupsIntegrationPoint petasosAuditServicesEndpoint) {
        this.petasosAuditServicesEndpoint = petasosAuditServicesEndpoint;
    }
    

    public boolean hasPetasosMediaServicesEndpoint(){
        boolean hasValue = this.petasosMediaServicesEndpoint != null;
        return(hasValue);
    }

    public JGroupsIntegrationPoint getPetasosMediaServicesEndpoint() {
        return petasosAuditServicesEndpoint;
    }

    public void setPetasosMediaServicesEndpoint(JGroupsIntegrationPoint petasosMediaServicesEndpoint) {
        this.petasosMediaServicesEndpoint = petasosMediaServicesEndpoint;
    }

    public boolean hasPetasosTaskServicesEndpoint(){
        boolean hasValue = this.petasosTaskServicesEndpoint != null;
        return(hasValue);
    }

    public JGroupsIntegrationPoint getPetasosTaskServicesEndpoint() {
        return petasosTaskServicesEndpoint;
    }

    public void setPetasosTaskServicesEndpoint(JGroupsIntegrationPoint petasosTaskServicesEndpoint) {
        this.petasosTaskServicesEndpoint = petasosTaskServicesEndpoint;
    }

    public boolean hasPetasosInterceptionServicesEndpoint(){
        boolean hasValue = this.petasosInterceptionServicesEndpoint != null;
        return(hasValue);
    }

    public JGroupsIntegrationPoint getPetasosInterceptionServicesEndpoint() {
        return petasosInterceptionServicesEndpoint;
    }

    public void setPetasosInterceptionServicesEndpoint(JGroupsIntegrationPoint petasosInterceptionServicesEndpoint) {
        this.petasosInterceptionServicesEndpoint = petasosInterceptionServicesEndpoint;
    }

    public boolean hasPetasosMetricsServicesEndpoint(){
        boolean hasValue = this.petasosMetricsServicesEndpoint != null;
        return(hasValue);
    }

    public JGroupsIntegrationPoint getPetasosMetricsServicesEndpoint() {
        return petasosMetricsServicesEndpoint;
    }

    public void setPetasosMetricsServicesEndpoint(JGroupsIntegrationPoint petasosMetricsServicesEndpoint) {
        this.petasosMetricsServicesEndpoint = petasosMetricsServicesEndpoint;
    }
}
