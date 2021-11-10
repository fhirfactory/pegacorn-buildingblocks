package net.fhirfactory.pegacorn.internals.communicate.entities.rooms.datatypes;

import net.fhirfactory.pegacorn.core.model.dates.EffectivePeriod;
import net.fhirfactory.pegacorn.internals.communicate.entities.rooms.valuesets.CommunicateRoomStatusEnum;

public class CommunicateRoomStatus {
    CommunicateRoomStatusEnum statusValue;
    EffectivePeriod statusPeriod;

    public CommunicateRoomStatusEnum getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(CommunicateRoomStatusEnum statusValue) {
        this.statusValue = statusValue;
    }

    public EffectivePeriod getStatusPeriod() {
        return statusPeriod;
    }

    public void setStatusPeriod(EffectivePeriod statusPeriod) {
        this.statusPeriod = statusPeriod;
    }

    @Override
    public String toString() {
        return "CommunicateRoomStatus{" +
                "statusValue=" + statusValue +
                ", statusPeriod=" + statusPeriod +
                '}';
    }
}
