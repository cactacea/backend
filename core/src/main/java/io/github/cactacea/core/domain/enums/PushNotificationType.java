package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PushNotificationType {
    @JsonProperty("0") message((byte)0),
    @JsonProperty("1") noDisplayedMessage((byte)1),
    @JsonProperty("2") image((byte)2),
    @JsonProperty("3") groupInvitation((byte)3),
    @JsonProperty("4") friendRequest((byte)4),
    @JsonProperty("5") feed((byte)5),
    @JsonProperty("6") comment((byte)6);

    private byte value;

    private PushNotificationType(byte value) {
        this.value = value;
    }

    static public PushNotificationType forName(byte value) {
        for (PushNotificationType e : values()) {
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
