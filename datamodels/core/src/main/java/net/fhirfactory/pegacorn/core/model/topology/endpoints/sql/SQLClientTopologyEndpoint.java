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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.sql;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.fhirfactory.pegacorn.core.model.component.valuesets.SoftwareComponentConnectivityContextEnum;
import net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.StandardInteractClientTopologyEndpointPort;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.sql.adapters.SQLClientAdapter;

public class SQLClientTopologyEndpoint extends StandardInteractClientTopologyEndpointPort {
	
    private String dataSource;
    private String queryTemplate;
    
    //
    // Constructor
    //

    public SQLClientTopologyEndpoint(){
        super();
        this.dataSource = null;
        this.queryTemplate = null;
        setEndpointType(PetasosEndpointTopologyTypeEnum.SQL_CLIENT);
        setComponentSystemRole(SoftwareComponentConnectivityContextEnum.COMPONENT_ROLE_INTERACT_BIDIRECTIONAL);
    }
    
    //
    // Getters and Setters
    //
    
    public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getQueryTemplate() {
		return queryTemplate;
	}

	public void setQueryTemplate(String queryTemplate) {
		this.queryTemplate = queryTemplate;
	}

    @JsonIgnore
    public List<SQLClientAdapter> getSQLClientAdapters() {
        List<SQLClientAdapter> sqlAdapterList = new ArrayList<>();
        for(IPCAdapter currentInterface: getAdapterList()){
        	SQLClientAdapter currentClientAdapter = (SQLClientAdapter)currentInterface;
        	sqlAdapterList.add(currentClientAdapter);
        }
        return sqlAdapterList;
    }
    
	@JsonIgnore
    public void setSQLClientAdapters(List<SQLClientAdapter> targetSQLClientAdapters) {
        if(targetSQLClientAdapters != null) {
            this.getAdapterList().clear();
            this.getAdapterList().addAll(targetSQLClientAdapters);
        }
    }
	
	//
	// toString
	//

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SQLClientTopologyEndpoint [dataSource=").append(dataSource);
		builder.append(", queryTemplate=").append(queryTemplate);
		builder.append(", ").append(super.toString()).append("]");
		return builder.toString();
	}

	
	
}
