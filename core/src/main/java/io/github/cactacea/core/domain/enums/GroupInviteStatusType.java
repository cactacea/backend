package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GroupInviteStatusType {
    @JsonProperty("0") noresponsed(0),
    @JsonProperty("1") accepted(1),
    @JsonProperty("2") rejected(2);

    private long value;

    private GroupInviteStatusType(long value) {
        this.value = value;
    }

    static public GroupInviteStatusType forName(long value) {
        for (GroupInviteStatusType e : values()) {
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
