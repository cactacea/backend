package io.github.cactacea.backend.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum NotificationType {
    operator((byte)0),
    groupInvitation((byte)1),
    friendRequest((byte)2),
    feed((byte)3),
    feedReply((byte)4),
    commentReply((byte)5);

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
