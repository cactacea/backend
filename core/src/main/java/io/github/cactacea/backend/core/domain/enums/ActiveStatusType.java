package io.github.cactacea.backend.core.domain.enums;

public enum ActiveStatusType {
    active((byte)0),
    inactive((byte)1);

    private byte value;

    private ActiveStatusType(byte value) {
        this.value = value;
    }

    static public ActiveStatusType forName(byte value) {
        for (ActiveStatusType e : values()) {
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
