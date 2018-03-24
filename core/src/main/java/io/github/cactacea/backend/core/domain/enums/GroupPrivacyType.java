package io.github.cactacea.backend.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GroupPrivacyType {
    everyone((byte)0),
    following((byte)1),
    followers((byte)2),
    friends((byte)3);

    private byte value;

    private GroupPrivacyType(byte value) {
        this.value = value;
    }

    static public GroupPrivacyType forName(byte value) {
        for (GroupPrivacyType e : values()) {
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

