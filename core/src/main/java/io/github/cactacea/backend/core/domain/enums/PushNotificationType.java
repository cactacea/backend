package io.github.cactacea.backend.core.domain.enums;

public enum PushNotificationType {
    message((byte)0),
    nonDisplay((byte)1),
    image((byte)2),
    invitation((byte)3),
    request((byte)4),
    tweet((byte)5),
    tweetReply((byte)6),
    commentReply((byte)7);

    public byte value;

    PushNotificationType(byte value) {
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

}
