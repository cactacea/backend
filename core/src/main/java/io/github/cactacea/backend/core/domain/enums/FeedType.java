package io.github.cactacea.backend.core.domain.enums;

public enum FeedType {
    posted((byte)0),
    received((byte)1);

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
