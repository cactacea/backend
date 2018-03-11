package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DeviceType {
    ios((byte)0),
    android((byte)1),
    web((byte)2);

    private byte value;

    private DeviceType(byte value) {
        this.value = value;
    }

    static public DeviceType forName(byte value) {
        for (DeviceType e : values()) {
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
