/*
 * Copyright (c) 2022 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentConnectivityContextEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.FileShareSourceAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.StandardInteractServerTopologyEndpointPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileShareSourceTopologyEndpoint extends StandardInteractServerTopologyEndpointPort {
    private static final Logger LOG = LoggerFactory.getLogger(FileShareSourceTopologyEndpoint.class);

    public FileShareSourceTopologyEndpoint(){
        super();
        setEndpointType(PetasosEndpointTopologyTypeEnum.FILE_SHARE_SOURCE);
        setComponentSystemRole(SoftwareComponentConnectivityContextEnum.COMPONENT_ROLE_INTERACT_INGRES);
    }

    @JsonIgnore
    public FileShareSourceAdapter getFileShareSourceAdapter(){
        getLogger().debug(".getFileShareSourceAdapter(): Entry");
        if(getAdapterList().isEmpty()){
            getLogger().debug(".getFileShareSourceAdapter(): Exit, Adapter list is empty, returning null");
            return(null);
        }
        FileShareSourceAdapter fileShareSource = (FileShareSourceAdapter) getAdapterList().get(0);
        getLogger().debug(".getHTTPServerAdapter(): Exit, httpServer->{}", fileShareSource);
        return(fileShareSource);
    }

    @JsonIgnore
    public void setFileShareSourceAdapter(FileShareSourceAdapter fileShareSource){
        if(fileShareSource != null){
            getAdapterList().add(fileShareSource);
        }
    }

    //
    // Getters
    //

    @Override
    protected Logger getLogger(){
        return(LOG);
    }
}
