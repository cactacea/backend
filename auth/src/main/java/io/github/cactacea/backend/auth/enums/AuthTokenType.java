package io.github.cactacea.backend.auth.enums;

public enum AuthTokenType {
    signUp((byte)0),
    resetPassword((byte)1);

    private byte value;

    private AuthTokenType(byte value) {
        this.value = value;
    }

    static public AuthTokenType forName(byte value) {
        for (AuthTokenType e : values()) {
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
