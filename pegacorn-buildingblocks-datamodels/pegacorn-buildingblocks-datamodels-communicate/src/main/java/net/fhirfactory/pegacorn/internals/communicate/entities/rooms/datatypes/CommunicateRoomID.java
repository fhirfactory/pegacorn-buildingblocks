package net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes;

import java.util.Objects;

public class CommunicateRoomID {
    private String value;

    public CommunicateRoomID(){
        this.value = null;
    }

    public CommunicateRoomID(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CommunicateRoomID{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommunicateRoomID)) return false;
        CommunicateRoomID that = (CommunicateRoomID) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
