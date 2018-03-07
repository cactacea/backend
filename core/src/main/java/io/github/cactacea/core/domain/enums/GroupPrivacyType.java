package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GroupPrivacyType {
    @JsonProperty("0")everyone((byte)0),
    @JsonProperty("1")follows((byte)1),
    @JsonProperty("2")followers((byte)2),
    @JsonProperty("3")friends((byte)3);

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

