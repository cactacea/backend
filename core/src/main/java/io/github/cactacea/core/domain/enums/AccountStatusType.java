package io.github.cactacea.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AccountStatusType {
    @JsonProperty("0") singedUp((byte)0),
    @JsonProperty("1") deleted((byte)1),
    @JsonProperty("2") terminated((byte)2);

    private byte value;

    private AccountStatusType(byte value) {
        this.value = value;
    }

    static public AccountStatusType forName(byte value) {
        for (AccountStatusType e : values()) {
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
