package io.github.cactacea.backend.core.domain.enums;

public enum DeviceType {
    ios((byte)0),
    android((byte)1),
    web((byte)2);

    public byte value;

    DeviceType(byte value) {
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

}
