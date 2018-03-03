package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GroupInvitationStatusType {
    @JsonProperty("0") noresponsed(0),
    @JsonProperty("1") accepted(1),
    @JsonProperty("2") rejected(2);

    private long value;

    private GroupInvitationStatusType(long value) {
        this.value = value;
    }

    static public GroupInvitationStatusType forName(long value) {
        for (GroupInvitationStatusType e : values()) {
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
