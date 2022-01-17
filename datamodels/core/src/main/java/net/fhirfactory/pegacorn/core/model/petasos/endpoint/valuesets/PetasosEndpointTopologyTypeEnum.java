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
package net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets;

import org.apache.commons.lang3.StringUtils;

public enum PetasosEndpointTopologyTypeEnum {
    JGROUPS_INTEGRATION_POINT("JGroups.IntegrationPoint", "pegacorn.endpoint.jgroups.integration_point", "endpoint.ipc_messaging.jgroups_interzone_client"),
    HTTP_API_SERVER("HTTP.Server", "pegacorn.fhir.endpoint.http_api_server", "endpoint.http.server" ),
    HTTP_API_CLIENT("HTTP.Client", "pegacorn.fhir.endpoint.http_api_client", "endpoint.http.client"),
    MLLP_SERVER("MLLP.Server","pegacorn.fhir.endpoint.mllp_server", "endpoint.mllp.server"),
    MLLP_CLIENT("MLLP.Client","pegacorn.fhir.endpoint.mllp_client", "endpoint.mllp.client"),
    SQL_SERVER("SQL.Server", "pegacorn.fhir.endpoint.sql_server", "endpoint.sql.server"),
    SQL_CLIENT("SQL.Client", "pegacorn.fhir.endpoint.sql_client", "endpoint.sql.client"),
    LDAP_SERVER("LDAP.Server", "pegacorn.fhir.endpoint.ldap_server","endpoint.ldap.server"),
    LDAP_CLIENT("LDAP.Client", "pegacorn.fhir.endpoint.ldap_client", "endpoint.ldap.client"),
    OTHER_API_SERVER("API.Server", "endpoint.other_type_of_server", "endpoint.other_api.server"),
    OTHER_API_CLIENT("API.Client", "endpoint.other_type_of_client","endpoint.other_api.client"),
    OTHER_SERVER("Other.Server", "endpoint.other_server", "endpoint.other.server"),
    OTHER_CLIENT("Other.Client", "endpoint.other_client", "endpoint.other.client");

    private String token;
    private String displayName;
    private String configTypeName;

    private PetasosEndpointTopologyTypeEnum(String displayName, String token, String configTypeName){
        this.token = token;
        this.displayName = displayName;
        this.configTypeName = configTypeName;
    }

    public String getToken(){
        return(this.token);
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public String getConfigTypeName(){
        return(this.configTypeName);
    }

    public static PetasosEndpointTopologyTypeEnum fromConfigTypeName(String configName){
        if(StringUtils.isEmpty(configName)){
            return(null);
        }
        for(PetasosEndpointTopologyTypeEnum currentType: PetasosEndpointTopologyTypeEnum.values()){
            if(currentType.getConfigTypeName().equalsIgnoreCase(configName)){
                return(currentType);
            }
        }
        return(null);
    }
}
