package io.github.cactacea.backend.core.domain.enums;

public enum InformationType {
    operator((byte)0),
    invitation((byte)1),
    friendRequest((byte)2),
    tweet((byte)3),
    tweetReply((byte)4),
    commentReply((byte)5);

    public byte value;

    InformationType(byte value) {
        this.value = value;
    }

    static public InformationType forName(byte value) {
        for (InformationType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}
