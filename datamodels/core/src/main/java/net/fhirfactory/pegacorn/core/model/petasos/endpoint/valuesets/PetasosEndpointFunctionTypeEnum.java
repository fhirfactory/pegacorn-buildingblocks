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
package net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets;

public enum PetasosEndpointFunctionTypeEnum {
    PETASOS_NOTIFICATIONS_ENDPOINT("petasos.endpoint_function.notifications", "Notifications", "JGroups.Notifications", "petasos-ipc"),
    PETASOS_INFINISPAN_ENDPOINT("petasos.endpoint_function.infinispan", "Infinispan", "JGroups.Infinispan", "multi-infini"),
    PETASOS_INTERACT_ENDPOINT("petasos.endpoint_function.interact", "Interact", "JGroups.Interact", "interact"),
    PETASOS_TASK_DISTRIBUTION_GRID_ENDPOINT_ONE("petasos.endpoint_function.task_distribution_grid","TaskDistributionGridServerOne", "JGroups.TaskGridServerOne", "petasos-task"),
    PETASOS_TASK_DISTRIBUTION_GRID_ENDPOINT_TWO("petasos.endpoint_function.task_distribution_grid","TaskDistributionGridServerTwo", "JGroups.TaskGridServerTwo", "petasos-task"),
    PETASOS_TASK_DISTRIBUTION_GRID_CLIENT_ENDPOINT_UNO("petasos.endpoint_function.task_distribution_grid_client", "TaskDistributionGridClientUno", "JGroups.TaskGridClientUno", "petasos-task"),
    PETASOS_TASK_DISTRIBUTION_GRID_CLIENT_ENDPOINT_DUO("petasos.endpoint_function.task_distribution_grid_client", "TaskDistributionGridClientDuo", "JGroups.TaskGridClientDuo", "petasos-task"),
    PETASOS_TASK_CAPABILITY_EXECUTION_ENDPOINT("petasos.endpoint_function.task_capability_execution_endpoint", "TaskCapabilityExecution", "JGroups.TaskCapabilityExecution", "petasos-task"),

    PETASOS_SUBSCRIPTIONS_ENDPOINT("petasos.endpoint_function.subscription_services", "SubscriptionServices","JGroups.SubscriptionServices", "petasos-sub"),
    PETASOS_INTERCEPTION_ENDPOINT("petasos.endpoint_function.interception_services", "InterceptionServices", "JGroups.InterceptionServices", "petasos-snoop"),
    PETASOS_AUDIT_ENDPOINT("petasos.endpoint_function.audit_services", "AuditServices", "JGroups.AuditServices", "petasos-audit"),
    PETASOS_METRICS_ENDPOINT("petasos.endpoint_function.metrics_services", "MetricsServices", "JGroups.MetricServices", "petasos-metric"),
    PETASOS_TOPOLOGY_ENDPOINT("petasos.endpoint_function.topology_services", "TopologyServices", "JGroups.TopologyServices", "petasos-top");

    private String token;
    private String displayName;
    private String endpointParticipantName;
    private String configurationName;

    private PetasosEndpointFunctionTypeEnum(String functionType, String displayName, String participantName, String configurationName ){
        this.token = functionType;
        this.displayName = displayName;
        this.endpointParticipantName = participantName;
        this.configurationName = configurationName;
    }

    public String getToken(){
        return(this.token);
    }

    public String getDisplayName(){
        return(this.displayName);
    }

    public String getEndpointParticipantName(){
        return(this.endpointParticipantName);
    }

    public String getConfigurationName(){
        return(this.configurationName);
    }

    public static PetasosEndpointFunctionTypeEnum getFunctionEnumFromDisplayName(String displayName){
        for(PetasosEndpointFunctionTypeEnum currentFunction: PetasosEndpointFunctionTypeEnum.values()){
            if(currentFunction.getDisplayName().equalsIgnoreCase(displayName)){
                return(currentFunction);
            }
        }
        return(null);
    }
}
