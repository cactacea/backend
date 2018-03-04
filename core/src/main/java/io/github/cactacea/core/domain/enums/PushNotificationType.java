package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PushNotificationType {
    @JsonProperty("0") message(0),
    @JsonProperty("1") noDisplayedMessage(1),
    @JsonProperty("2") image(2),
    @JsonProperty("3") groupInvitation(3),
    @JsonProperty("4") friendRequest(4),
    @JsonProperty("5") feed(5),
    @JsonProperty("6") comment(6);

    private long value;

    private PushNotificationType(long value) {
        this.value = value;
    }

    static public PushNotificationType forName(long value) {
        for (PushNotificationType e : values()) {
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
