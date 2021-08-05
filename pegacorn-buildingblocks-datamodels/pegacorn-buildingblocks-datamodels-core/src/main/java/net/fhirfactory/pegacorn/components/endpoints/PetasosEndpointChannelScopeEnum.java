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
package net.fhirfactory.pegacorn.components.endpoints;

public enum PetasosEndpointChannelScopeEnum {
    ENDPOINT_CHANNEL_SCOPE_INTERSITE("endpoint.channel_scope.intersite", "InterSite"),
    ENDPOINT_CHANNEL_SCOPE_INTERZONE("endpoint.channel_scope.interzone", "InterZone"),
    ENDPOINT_CHANNEL_SCOPE_INTRAZONE("endpoint.channel_scope.intrazone", "IntraZone");

    private String endpointScope;
    private String endpointScopeName;

    private PetasosEndpointChannelScopeEnum(String scope, String scopeName) {
        this.endpointScope = scope;
        this.endpointScopeName = scopeName;
    }

    public String getEndpointScope() {
        return (this.endpointScope);
    }

    public String getEndpointScopeName(){
        return(this.endpointScopeName);
    }
}
