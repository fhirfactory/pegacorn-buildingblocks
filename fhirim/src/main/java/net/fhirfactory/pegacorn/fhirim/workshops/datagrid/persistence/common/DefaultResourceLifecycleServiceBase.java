/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.pegacorn.fhirim.workshops.datagrid.persistence.common;

import net.fhirfactory.pegacorn.core.interfaces.datagrid.DatagridElementKeyInterface;
import net.fhirfactory.pegacorn.core.interfaces.datagrid.DatagridEntryLoadRequestInterface;
import net.fhirfactory.pegacorn.core.interfaces.datagrid.DatagridEntrySaveRequestInterface;
import net.fhirfactory.pegacorn.fhirim.workshops.datagrid.cache.common.BaseResourceReplicatedCache;
import net.fhirfactory.pegacorn.platform.edge.ask.base.ResourceFHIRClientService;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;

public abstract class DefaultResourceLifecycleServiceBase implements DatagridEntryLoadRequestInterface, DatagridEntrySaveRequestInterface {

    private boolean initialised;

    //
    // Constructor(s)
    //

    public DefaultResourceLifecycleServiceBase(){
        this.initialised = false;
    }

    //
    // Abstract Methods
    //

    abstract protected ResourceFHIRClientService getResourceFHIRClient();
    abstract protected Logger getLogger();
    abstract protected BaseResourceReplicatedCache getResourceReplicatedCache();
    abstract protected ResourceType specifyResourceType();

    //
    // PostConstructor
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(initialised){
            getLogger().debug(".initialise(): Already initialised, nothing to do");
            // do nothing
        } else {


            this.initialised = true;
        }
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Implemented Methods
    //


    @Override
    public void requestDatagridEntryLoad(DatagridElementKeyInterface elementId) {

    }

    @Override
    public void requestDatagridEntryLoad(Identifier elementIdentifier) {
        Resource resourceByIdentifier = getResourceFHIRClient().findResourceByIdentifier(getResourceType(), elementIdentifier);
    }

    @Override
    public void requestDataGridEntrySave(DatagridElementKeyInterface element) {

    }

    //
    // Getters and Setters
    //

    protected ResourceType getResourceType(){
        return(specifyResourceType());
    }
}
