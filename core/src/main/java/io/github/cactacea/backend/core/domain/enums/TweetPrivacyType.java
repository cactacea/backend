package io.github.cactacea.backend.core.domain.enums;

public enum TweetPrivacyType {
    everyone((byte)0),
    followers((byte)1),
    friends((byte)2),
    self((byte)3);

    public byte value;

    TweetPrivacyType(byte value) {
        this.value = value;
    }

    static public TweetPrivacyType forName(byte value) {
        for (TweetPrivacyType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}
