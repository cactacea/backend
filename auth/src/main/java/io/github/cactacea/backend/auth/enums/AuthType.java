package io.github.cactacea.backend.auth.enums;

public enum AuthType {
    username((byte)0),
    email((byte)1);

    private byte value;

    private AuthType(byte value) {
        this.value = value;
    }

    static public AuthType forName(byte value) {
        for (AuthType e : values()) {
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
