package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MessageType {
    @JsonProperty("0") text(0),
    @JsonProperty("1") medium(1),
    @JsonProperty("2") stamp(2),
    @JsonProperty("3") groupInvitationd(3),
    @JsonProperty("4") groupJoined(4),
    @JsonProperty("5") groupLeft(5);

    private long value;

    private MessageType(long value) {
        this.value = value;
    }

    static public MessageType forName(long value) {
        for (MessageType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

    public long toValue() {
        return value;
    }
}