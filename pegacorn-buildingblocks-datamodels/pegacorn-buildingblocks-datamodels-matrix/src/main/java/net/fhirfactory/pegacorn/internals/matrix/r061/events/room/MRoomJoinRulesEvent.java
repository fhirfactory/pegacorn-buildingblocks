package net.fhirfactory.pegacorn.internals.matrix.r061.events.room;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.room.common.MRoomEventBase;
import net.fhirfactory.pegacorn.internals.matrix.r061.events.room.contenttypes.MRoomJoinRulesContentType;

public class MRoomJoinRulesEvent extends MRoomEventBase {
    private MRoomJoinRulesContentType content;

    @JsonProperty("content")
    public MRoomJoinRulesContentType getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(MRoomJoinRulesContentType content) {
        this.content = content;
    }
}
