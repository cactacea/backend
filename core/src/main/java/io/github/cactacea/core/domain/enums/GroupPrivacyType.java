package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GroupPrivacyType {
    @JsonProperty("0")everyone(0),
    @JsonProperty("1")follows(1),
    @JsonProperty("2")followers(2),
    @JsonProperty("3")friends(3);

    private long value;

    private GroupPrivacyType(long value) {
        this.value = value;
    }

    static public GroupPrivacyType forName(long value) {
        for (GroupPrivacyType e : values()) {
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

