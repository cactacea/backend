package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum NotificationType {
    @JsonProperty("0") operator((byte)0),
    @JsonProperty("1") groupInvitation((byte)1),
    @JsonProperty("2") friendRequest((byte)2),
    @JsonProperty("3") feedReply((byte)3),
    @JsonProperty("4") commentReply((byte)4);

    private byte value;

    private NotificationType(byte value) {
        this.value = value;
    }

    static public NotificationType forName(byte value) {
        for (NotificationType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

    public byte toValue() {
        return value;
    }
}
