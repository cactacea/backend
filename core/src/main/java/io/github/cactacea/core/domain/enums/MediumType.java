package io.github.cactacea.core.domain.enums;

public enum MediumType {
    image((byte)0),
    movie((byte)1);

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

