package net.fhirfactory.pegacorn.core.model.petasos.endpoint.valuesets;

public enum PetasosIntegrationPointNameEnum {

    PETASOS_TOPOLOGY_SERVICES_GROUP_NAME("Petasos.Topology", "petasos-top", "InternalIPC.TopologyServices"),
    PETASOS_SUBSCRIPTIONS_SERVICES_GROUP_NAME("Petasos.Subscriptions", "petasos-sub", "InternalIPC.SubscriptionServices"),
    PETASOS_TASK_SERVICES_GROUP_NAME("Petasos.Tasking", "petasos-task", "InternalIPC.TaskingServices"),
    PETASOS_INTERCEPTION_GROUP_NAME("Petasos.Snoop", "petasos-snoop", "InternalIPC.InterceptionServices"),
    PETASOS_METRICS_GROUP_NAME("Petasos.Metrics", "petasos-metric", "InternalIPC.MetricsServices"),
    PETASOS_AUDIT_SERVICES_GROUP_NAME("Petasos.Audit", "petasos-audit", "InternalIPC.AuditService"),
    PETASOS_IPC_MESSAGING_GROUP_NAME("Petasos.IPC", "petasos-ipc", "InternalIPC.Messaging"),
    PETASOS_EDGE_ANSWER("EdgeAnswer", "edge-answer", "InternalIPC.FHIR.Servers"),
    PETASOS_EDGE_ASK("EdgeAsk", "edge-ask", "InternalIPC.FHIR.Clients"),
    PETASOS_MULTI_INFINISPAN("DataGrid", "multi-infini", "InternalIPC.DataGridServices");

    private String configName;
    private String groupName;
    private String participantName;

    private PetasosIntegrationPointNameEnum(String groupName, String configName, String participantName){
        this.configName = configName;
        this.groupName = groupName;
        this.participantName = participantName;
    }

    public String getConfigName() {
        return configName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getParticipantName() {
        return participantName;
    }

    public static PetasosIntegrationPointNameEnum fromConfigName(String configName){
        if(configName == null){
            return(null);
        }
        for(PetasosIntegrationPointNameEnum currentName: PetasosIntegrationPointNameEnum.values()){
            if(currentName.getConfigName().equalsIgnoreCase(configName)){
                return(currentName);
            }
        }
        return(null);
    }
}
