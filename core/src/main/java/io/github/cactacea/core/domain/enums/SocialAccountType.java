package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SocialAccountType {
    @JsonProperty("facebook")facebook(1),
    @JsonProperty("google")google(2),
    @JsonProperty("twitter")twitter(3);

    private long value;

    private SocialAccountType(long value) {
        this.value = value;
    }

    static public SocialAccountType forName(long value) {
        for (SocialAccountType e : values()) {
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
