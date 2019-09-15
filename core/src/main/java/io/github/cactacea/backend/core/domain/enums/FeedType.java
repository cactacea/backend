package io.github.cactacea.backend.core.domain.enums;

public enum FeedType {
    operator((byte)0),
    invitation((byte)1),
    friendRequest((byte)2),
    tweet((byte)3),
    tweetReply((byte)4),
    commentReply((byte)5);

    public byte value;

    FeedType(byte value) {
        this.value = value;
    }

    static public FeedType forName(byte value) {
        for (FeedType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}
