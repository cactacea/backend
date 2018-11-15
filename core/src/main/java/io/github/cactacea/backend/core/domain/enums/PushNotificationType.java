package io.github.cactacea.backend.core.domain.enums;

public enum PushNotificationType {
    message((byte)0),
    noDisplayedMessage((byte)1),
    image((byte)2),
    groupInvitation((byte)3),
    friendRequest((byte)4),
    feed((byte)5),
    feedReply((byte)6),
    commentReply((byte)7);

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
