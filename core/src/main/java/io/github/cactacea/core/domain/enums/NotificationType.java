package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum NotificationType {
    @JsonProperty("0") operator(0),
    @JsonProperty("1") groupInvitation(1),
    @JsonProperty("2") friendRequest(2);

    private long value;

    private NotificationType(long value) {
        this.value = value;
    }

    static public NotificationType forName(long value) {
        for (NotificationType e : values()) {
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
