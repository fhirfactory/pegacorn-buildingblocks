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
package net.fhirfactory.pegacorn.platform.edge.ask;

import ca.uhn.fhir.rest.api.MethodOutcome;
import net.fhirfactory.pegacorn.platform.edge.ask.base.InternalFHIRClientServicesBase;
import net.fhirfactory.pegacorn.platform.edge.ask.base.ResourceFHIRClientService;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ProvenanceFHIRClientService extends ResourceFHIRClientService {
    private static final Logger LOG = LoggerFactory.getLogger(ProvenanceFHIRClientService.class);

    //
    // Constructor(s)
    //

    public ProvenanceFHIRClientService(){
        super();
    }

    //
    // Getters (and Setters)
    //

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    //
    // Business Methods
    //

    public MethodOutcome createProvenance(String provenanceJSONString){
        getLogger().debug(".createProvenance(): Entry, provenanceJSONString->{}", provenanceJSONString);
        Provenance provenance = getFHIRParser().parseResource(Provenance.class, provenanceJSONString);
        MethodOutcome outcome = createResource(provenance);
        getLogger().info(".createProvenance(): Exit, outcome->{}", outcome);
        return(outcome);
    }

    public MethodOutcome createProvenance(Provenance provenance){
        getLogger().debug(".createProvenance(): Entry, provenance->{}", provenance);
        MethodOutcome outcome = createResource(provenance);
        getLogger().info(".createProvenance(): Exit, outcome->{}", outcome);
        return(outcome);
    }

    public MethodOutcome updateProvenance(Provenance provenance){
        getLogger().debug(".updateProvenance(): Entry, provenance->{}", provenance);
        MethodOutcome outcome = updateResource(provenance);
        getLogger().info(".updateProvenance(): Exit, outcome->{}", outcome);
        return(outcome);
    }

    public List<Provenance> findByReferenceToTask(Identifier taskIdentifier){

        List<Provenance> provenanceList = new ArrayList<>();

        return(provenanceList);
    }
}
