package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PushNotificationType {
    @JsonProperty("0") sendMessage(0),
    @JsonProperty("1") sendNoDisplayedMessage(1),
    @JsonProperty("2") sendImage(2),
    @JsonProperty("3") sendGroupInvitation(3),
    @JsonProperty("4") sendFriendRequest(4),
    @JsonProperty("5") postFeed(5),
    @JsonProperty("6") postComment(6);

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
