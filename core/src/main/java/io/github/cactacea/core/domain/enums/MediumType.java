package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MediumType {
    @JsonProperty("0")image((byte)0),
    @JsonProperty("1")movie((byte)1);

    private byte value;

    private MediumType(byte value) {
        this.value = value;
    }

    static public MediumType forName(byte value) {
        for (MediumType e : values()) {
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

