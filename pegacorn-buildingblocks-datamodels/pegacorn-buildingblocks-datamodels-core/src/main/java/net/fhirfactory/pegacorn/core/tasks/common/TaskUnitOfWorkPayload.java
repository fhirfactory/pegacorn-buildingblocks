package net.fhirfactory.pegacorn.core.tasks.common;

import java.util.Objects;

public class TaskUnitOfWorkPayload {
    private String payloadClassType;
    private Object payloadObject;

    public Object getPayloadObject(){
        return(this.payloadObject);
    }

    public void setPayloadObject(Object payloadObject){
        payloadClassType = payloadObject.getClass().getName();
        this.payloadObject = payloadObject;
    }

    public String getPayloadClassType() {
        return payloadClassType;
    }

    public void setPayloadClassType(String payloadClassType) {
        this.payloadClassType = payloadClassType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskUnitOfWorkPayload)) return false;
        TaskUnitOfWorkPayload that = (TaskUnitOfWorkPayload) o;
        return Objects.equals(getPayloadClassType(), that.getPayloadClassType()) && Objects.equals(getPayloadObject(), that.getPayloadObject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPayloadClassType(), getPayloadObject());
    }

    @Override
    public String toString() {
        return "TaskUnitOfWorkPayload{" +
                "payloadClassType='" + payloadClassType + '\'' +
                ", payloadObject=" + payloadObject +
                '}';
    }
}
