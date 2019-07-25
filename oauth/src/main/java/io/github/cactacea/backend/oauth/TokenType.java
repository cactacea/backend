package io.github.cactacea.backend.oauth;

public enum TokenType {
    code("code"),
    token("token"),
    refreshToken("refreshToken");

    private String value;

    private TokenType(String value) {
        this.value = value;
    }

    static public TokenType forName(String value) {
        for (TokenType e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }

    public String toValue() {
        return value;
    }
}
