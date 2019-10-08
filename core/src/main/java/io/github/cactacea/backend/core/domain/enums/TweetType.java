package io.github.cactacea.backend.core.domain.enums;

public enum TweetType {
    posted((byte)0),
    received((byte)1);

    public byte value;

    TweetType(byte value) {
        this.value = value;
    }

    static public TweetType forName(byte value) {
        for (TweetType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}
