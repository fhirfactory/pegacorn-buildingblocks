package net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MStrippedStateType {
    private MEventContentType content;
    private String stateKey;
    private String type;
    private String sender;

    public MEventContentType getContent() {
        return content;
    }

    public void setContent(MEventContentType content) {
        this.content = content;
    }

    @JsonProperty("state_key")
    public String getStateKey() {
        return stateKey;
    }

    @JsonProperty("state_key")
    public void setStateKey(String stateKey) {
        this.stateKey = stateKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
