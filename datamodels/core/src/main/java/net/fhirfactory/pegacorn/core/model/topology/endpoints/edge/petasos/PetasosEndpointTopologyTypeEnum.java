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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos;

public enum PetasosEndpointTopologyTypeEnum {
    JGROUPS_INTRAZONE_SERVICE("JGroups.IntraZone","endpoint.ipc_messaging.jgroups_intrazone" ),
    JGROUPS_INTERZONE_SERVICE("JGroups.InterZone", "endpoint.ipc_messaging.jgroups_interzone"),
    JGROUPS_INTERSITE_SERVICE("JGroups.InterSite", "endpoint.ipc_messaging.jgroups_intersite"),
    MLLP_SERVER("MLLP.Server","endpoint.mllp_server"),
    MLLP_CLIENT("MLLP.Client","endpoint.mllp_client"),
    HTTP_API_SERVER("HTTP.Server", "endpoint.http_api_server"),
    HTTP_API_CLIENT("HTTP.Client", "endpoint.http_api_client"),
    SQL_SERVER("SQL.Server", "endpoint.sql_server"),
    SQL_CLIENT("SQL.Client", "endpoint.sql_client"),
    LDAP_SERVER("LDAP.Server", "endpoint.ldap_server"),
    LDAP_CLIENT("LDAP.Client", "endpoint.ldap_client"),
    OTHER_API_SERVER("API.Server", "endpoint.other_type_of_server"),
    OTHER_API_CLIENT("API.Client", "endpoint.other_type_of_client"),
    OTHER_SERVER("Other.Server", "endpoint.other_server"),
    OTHER_CLIENT("Other.Client", "endpoint.other_client");

    private String endpointType;
    private String displayName;

    private PetasosEndpointTopologyTypeEnum(String displayName, String endpointType){
        this.endpointType = endpointType;
        this.displayName = displayName;
    }

    public String getEndpointType(){
        return(this.endpointType);
    }

    public String getDisplayName(){
        return(this.displayName);
    }

}
