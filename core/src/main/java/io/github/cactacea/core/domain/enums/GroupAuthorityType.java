package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GroupAuthorityType {
    @JsonProperty("0")owner(0),
    @JsonProperty("1")member(1);

    private long value;

    private GroupAuthorityType(long value) {
        this.value = value;
    }

    static public GroupAuthorityType forName(long value) {
        for (GroupAuthorityType e : values()) {
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
