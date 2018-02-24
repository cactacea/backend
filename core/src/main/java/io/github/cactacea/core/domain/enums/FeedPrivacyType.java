package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FeedPrivacyType {
    @JsonProperty("0")everyone(0),
    @JsonProperty("1")followers(1),
    @JsonProperty("2")friends(2),
    @JsonProperty("3")self(3);

    private long value;

    private FeedPrivacyType(long value) {
        this.value = value;
    }

    static public FeedPrivacyType forName(long value) {
        for (FeedPrivacyType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

    public long toValue() {
        return value;
    }
}
