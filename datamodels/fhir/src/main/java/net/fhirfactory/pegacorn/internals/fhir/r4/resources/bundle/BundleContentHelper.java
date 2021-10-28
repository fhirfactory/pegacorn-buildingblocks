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
package net.fhirfactory.pegacorn.internals.fhir.r4.resources.bundle;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BundleContentHelper {
    private static final Logger LOG = LoggerFactory.getLogger(BundleContentHelper.class);

    public Resource extractFirstRepOfType(Bundle bundle, ResourceType resourceType){
        return(extractFirstRepOfType(bundle, resourceType.toString()));
    }

    public Resource extractFirstRepOfType(Bundle bundle, String resourceType){
        LOG.info(".extractFirstRepOfType(): Entry, bundle --> {}, resourceType --> {}", bundle, resourceType);
        if(bundle == null || resourceType == null){
            LOG.info(".extractFirstRepOfType(): Exit, Ether the bundle or the resourceType are null!");
            return(null);
        }
//        if(bundle.getTotal() <=0 ){
//            LOG.info(".extractFirstRepOfType(): Exit, Bundle is empty, returning null");
//            return(null);
//        }
        for(Bundle.BundleEntryComponent currentEntry: bundle.getEntry()){
            Resource currentResource = currentEntry.getResource();
            if(currentResource.getResourceType().toString().contentEquals(resourceType)){
                LOG.info(".extractFirstRepOfType(): Exit, Found the \"First\" entry, returning it!");
                return(currentResource);
            }
        }
        LOG.info(".extractFirstRepOfType(): Exit, no Resource found of type specified, returning null");
        return(null);
    }

    public List<Resource> extractAllResourcesOfType(Bundle bundle, ResourceType resourceType){
        LOG.debug(".extractAllResourcesOfType(): Entry, bundle --> {}, resourceType --> {}", bundle, resourceType);
        List<Resource> resourceList = new ArrayList<Resource>();
        if(bundle == null || resourceType == null){
            LOG.debug(".extractAllResourcesOfType(): Exit, Ether the bundle or the resourceType are null, returning empty list!");
            return(resourceList);
        }
        if(bundle.getTotal() <=0 ){
            LOG.debug(".extractAllResourcesOfType(): Exit, Bundle is empty, returning empty list");
            return(resourceList);
        }
        for(Bundle.BundleEntryComponent currentEntry: bundle.getEntry()){
            Resource currentResource = currentEntry.getResource();
            if(currentResource.getResourceType().equals(resourceType)){
                LOG.trace(".extractAllResourcesOfType(): Exit, Found the \"First\" entry, returning it!");
                resourceList.add(currentResource);
            }
        }
        LOG.debug(".extractAllResourcesOfType(): Exit, returning resourceList, number of entries --> {}", resourceList.size());
        return(resourceList);
    }

    public Bundle buildEmptySearchBundle(){
        return(new Bundle());
    }

    public Bundle buildSearchResponseBundle(Resource resource){
        Bundle.BundleEntrySearchComponent bundleEntrySearchComponent = new Bundle.BundleEntrySearchComponent();
        bundleEntrySearchComponent.setScore(1);
        bundleEntrySearchComponent.setMode(Bundle.SearchEntryMode.MATCH);
        Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent();
        bundleEntryComponent.setResource(resource);
        bundleEntryComponent.setSearch(bundleEntrySearchComponent);
        Bundle bundle = new Bundle();
        bundle.setTimestamp(Date.from(Instant.now()));
        bundle.addEntry(bundleEntryComponent);
        bundle.setTotal(1);
        return(bundle);
    }

    public Bundle buildSearchResponseBundle(List<Resource> resourceSet){
        Bundle bundle = new Bundle();
        for(Resource currentResource: resourceSet) {
            Bundle.BundleEntrySearchComponent bundleEntrySearchComponent = new Bundle.BundleEntrySearchComponent();
            bundleEntrySearchComponent.setScore(1);
            bundleEntrySearchComponent.setMode(Bundle.SearchEntryMode.MATCH);
            Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent();
            bundleEntryComponent.setResource(currentResource);
            bundleEntryComponent.setSearch(bundleEntrySearchComponent);
            bundle.setTimestamp(Date.from(Instant.now()));
            bundle.addEntry(bundleEntryComponent);
        }
        bundle.setTotal(resourceSet.size());
        return(bundle);
    }
}
