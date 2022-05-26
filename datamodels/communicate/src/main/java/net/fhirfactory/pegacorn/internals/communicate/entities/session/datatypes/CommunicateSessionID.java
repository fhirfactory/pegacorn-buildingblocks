package net.fhirfactory.pegacorn.internals.communicate.entities.session.datatypes;

import java.util.Objects;

public class CommunicateSessionID {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CommunicateSessionID{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommunicateSessionID)) return false;
        CommunicateSessionID that = (CommunicateSessionID) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
