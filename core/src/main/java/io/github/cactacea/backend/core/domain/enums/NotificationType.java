package io.github.cactacea.backend.core.domain.enums;

public enum NotificationType {
    message((byte)0),
    nonDisplay((byte)1),
    image((byte)2),
    invitation((byte)3),
    request((byte)4),
    tweet((byte)5),
    tweetReply((byte)6),
    commentReply((byte)7);

    public byte value;

    NotificationType(byte value) {
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

}
