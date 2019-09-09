package io.github.cactacea.backend.auth.enums;

public enum TokenType {
    signUp((byte)0),
    resetPassword((byte)1);

    private byte value;

    private TokenType(byte value) {
        this.value = value;
    }

    static public TokenType forName(byte value) {
        for (TokenType e : values()) {
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
