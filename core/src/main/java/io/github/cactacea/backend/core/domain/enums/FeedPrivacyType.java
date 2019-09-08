package io.github.cactacea.backend.core.domain.enums;

public enum FeedPrivacyType {
    everyone((byte)0),
    followers((byte)1),
    friends((byte)2),
    self((byte)3);

    public byte value;

    FeedPrivacyType(byte value) {
        this.value = value;
    }

    static public FeedPrivacyType forName(byte value) {
        for (FeedPrivacyType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}
