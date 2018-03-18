package io.github.cactacea.core.domain.enums;

public enum ActiveStatus {
    active((byte)0),
    inactive((byte)1);

    private byte value;

    private ActiveStatus(byte value) {
        this.value = value;
    }

    static public ActiveStatus forName(byte value) {
        for (ActiveStatus e : values()) {
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
