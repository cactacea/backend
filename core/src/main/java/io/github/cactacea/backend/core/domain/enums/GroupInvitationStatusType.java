package io.github.cactacea.backend.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GroupInvitationStatusType {
    noResponded((byte)0),
    accepted((byte)1),
    rejected((byte)2);

    private byte value;

    private GroupInvitationStatusType(byte value) {
        this.value = value;
    }

    static public GroupInvitationStatusType forName(byte value) {
        for (GroupInvitationStatusType e : values()) {
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
