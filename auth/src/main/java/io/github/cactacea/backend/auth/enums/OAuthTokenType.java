package io.github.cactacea.backend.auth.enums;

public enum OAuthTokenType {
    code("code"),
    token("token"),
    refreshToken("refreshToken");

    private String value;

    private OAuthTokenType(String value) {
        this.value = value;
    }

    static public OAuthTokenType forName(String value) {
        for (OAuthTokenType e : values()) {
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
