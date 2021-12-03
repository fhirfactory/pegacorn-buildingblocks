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
package net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos;

public enum PetasosEndpointFunctionTypeEnum {
    PETASOS_INTERACT_ENDPOINT("petasos.endpoint_function.interact", "Interact"),
    PETASOS_IPC_ENDPOINT("petasos.endpoint_function.IPC","IPC"),
    PETASOS_SUBSCRIPTIONS_ENDPOINT("petasos.endpoint_function.subscriptions", "Subscriptions"),
    PETASOS_INTERCEPTION_ENDPOINT("petasos.endpoint_function.interception", "Interception"),
    PETASOS_TASKING_ENDPOINT("petasos.endpoint_function.tasking", "Tasking"),
    PETASOS_AUDIT_ENDPOINT("petasos.endpoint_function.audit", "Audit"),
    PETASOS_METRICS_ENDPOINT("petasos.endpoint_function.metrics", "Metrics"),
    PETASOS_TOPOLOGY_ENDPOINT("petasos.endpoint_function.topology", "Topology");

    private String token;
    private String displayName;

    private PetasosEndpointFunctionTypeEnum(String functionType, String functionName ){
        this.token = functionType;
        this.displayName = functionName;
    }

    public String getToken(){
        return(this.token);
    }

    public String getDisplayName(){
        return(this.displayName);
    }
}
