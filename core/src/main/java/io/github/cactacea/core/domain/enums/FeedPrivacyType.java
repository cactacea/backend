package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FeedPrivacyType {
    @JsonProperty("0")everyone((byte)0),
    @JsonProperty("1")followers((byte)1),
    @JsonProperty("2")friends((byte)2),
    @JsonProperty("3")self((byte)3);

    private byte value;

    private FeedPrivacyType(byte value) {
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

    public byte toValue() {
        return value;
    }
}
