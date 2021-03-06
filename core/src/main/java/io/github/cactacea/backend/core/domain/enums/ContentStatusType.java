package io.github.cactacea.backend.core.domain.enums;

public enum ContentStatusType {
    unchecked((byte)0),
    accepted((byte)1),
    rejected((byte)2);

    public byte value;

    ContentStatusType(byte value) {
        this.value = value;
    }

    static public ContentStatusType forName(byte value) {
        for (ContentStatusType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

}
