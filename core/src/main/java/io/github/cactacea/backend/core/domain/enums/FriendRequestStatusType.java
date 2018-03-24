package io.github.cactacea.backend.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FriendRequestStatusType {
    noResponded((byte)0),
    accepted((byte)1),
    rejected((byte)2);

    private byte value;

    private FriendRequestStatusType(byte value) {
        this.value = value;
    }

    static public FriendRequestStatusType forName(byte value) {
        for (FriendRequestStatusType e : values()) {
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
