package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GroupAuthorityType {
    @JsonProperty("0")owner((byte)0),
    @JsonProperty("1")member((byte)1);

    private byte value;

    private GroupAuthorityType(byte value) {
        this.value = value;
    }

    static public GroupAuthorityType forName(byte value) {
        for (GroupAuthorityType e : values()) {
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
