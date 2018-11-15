package io.github.cactacea.backend.core.domain.enums;

public enum GroupAuthorityType {
    owner((byte)0),
    member((byte)1);

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
