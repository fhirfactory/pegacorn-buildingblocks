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
    EDGE_JGROUPS_INTRAZONE_SERVICE("Edge.JGroups.IntraZone","pegacorn.fhir.endpoint.ipc_messaging.jgroups_intrazone" ),
    EDGE_JGROUPS_INTERZONE_SERVICE("Edge.JGroups.InterZone", "pegacorn.fhir.endpoint.ipc_messaging.jgroups_interzone"),
    EDGE_JGROUPS_INTERSITE_SERVICE("Edge.JGroups.InterSite", "pegacorn.fhir.endpoint.ipc_messaging.jgroups_intersite"),
    EDGE_HTTP_API_SERVER("Edge.HTTP.Server", "pegacorn.fhir.endpoint.ipc_messaging.http_api_server"),
    EDGE_HTTP_API_CLIENT("Edge.HTTP.Client", "pegacorn.fhir.endpoint.ipc_messaging.http_api_client"),
    INTERACT_MLLP_SERVER("Interact.MLLP.Server","pegacorn.fhir.endpoint.interact.mllp_server"),
    INTERACT_MLLP_CLIENT("Interact.MLLP.Client","pegacorn.fhir.endpoint.interact.mllp_client"),
    INTERACT_HTTP_API_SERVER("Interact.HTTP.Server", "pegacorn.fhir.endpoint.interact.http_api_server"),
    INTERACT_HTTP_API_CLIENT("Interact.HTTP.Client", "pegacorn.fhir.endpoint.interact.http_api_client"),
    INTERACT_SQL_SERVER("Interact.SQL.Server", "pegacorn.fhir.endpoint.interact.sql_server"),
    INTERACT_SQL_CLIENT("Interact.SQL.Client", "pegacorn.fhir.endpoint.interact.sql_client"),
    INTERACT_LDAP_SERVER("Interact.LDAP.Server", "pegacorn.fhir.endpoint.interact.ldap_server"),
    INTERACT_LDAP_CLIENT("Interact.LDAP.Client", "pegacorn.fhir.endpoint.interact.ldap_client"),
    OTHER_API_SERVER("API.Server", "endpoint.other_type_of_server"),
    OTHER_API_CLIENT("API.Client", "endpoint.other_type_of_client"),
    OTHER_SERVER("Other.Server", "endpoint.other_server"),
    OTHER_CLIENT("Other.Client", "endpoint.other_client");

    private String token;
    private String displayName;

    private PetasosEndpointTopologyTypeEnum(String displayName, String token){
        this.token = token;
        this.displayName = displayName;
    }

    public String getToken(){
        return(this.token);
    }

    public String getDisplayName(){
        return(this.displayName);
    }

}
