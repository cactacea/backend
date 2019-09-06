package io.github.cactacea.backend.core.domain.enums;

public enum ActiveStatusType {
    active((byte)0),
    inactive((byte)1);

    public byte value;

    ActiveStatusType(byte value) {
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

}
