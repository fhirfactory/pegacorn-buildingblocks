package net.fhirfactory.pegacorn.core.deployment.nodes;

public enum DefaultWorkshopSetEnum {
    INTERACT_WORKSHOP("Interact"),
    TRANSFORM_WORKSHOP("Transform"),
    POLICY_ENFORCEMENT_POINT_WORKSHOP("PolicyEnforcementPoint"),
    EDGE_WORKSHOP("EdgeIPC");

    private String workshop;

    private DefaultWorkshopSetEnum(String newWorkshop){
        this.workshop = newWorkshop;
    }

    public String getWorkshop(){
        return(this.workshop);
    }
}
